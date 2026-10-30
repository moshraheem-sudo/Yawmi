package com.example.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.YawmiDatabase
import com.example.data.repository.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra("TASK_ID", -1L)
        val noteId = intent.getLongExtra("NOTE_ID", -1L)
        val billId = intent.getLongExtra("BILL_ID", -1L)
        val advanceBillId = intent.getLongExtra("ADVANCE_BILL_ID", -1L)
        val daysBeforeDue = intent.getIntExtra("DAYS_BEFORE_DUE", 1)
        val checkAllUpcomingBills = intent.getBooleanExtra("CHECK_ALL_UPCOMING_BILLS", false)

        if (taskId == -1L && noteId == -1L && billId == -1L && advanceBillId == -1L && !checkAllUpcomingBills) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = YawmiDatabase.getInstance(context)
                val prefsRepo = UserPreferencesRepository(context)
                val prefs = prefsRepo.userPreferencesFlow.first()

                if (prefs.notificationsEnabled) {
                    if (taskId != -1L) {
                        val task = db.taskDao().getTaskByIdSync(taskId)
                        if (task != null && !task.isCompleted) {
                            NotificationHelper.showReminderNotification(
                                context = context,
                                taskId = taskId,
                                title = task.title,
                                content = task.description.ifBlank {
                                    if (prefs.language == "ar") "حان موعد المهمة المجدولة" else "Scheduled task time has arrived"
                                }
                            )
                        }
                    } else if (noteId != -1L) {
                        val note = db.noteDao().getNoteByIdSync(noteId)
                        if (note != null) {
                            NotificationHelper.showReminderNotification(
                                context = context,
                                taskId = 100000 + noteId,
                                title = "📝 ${note.title.ifBlank { if (prefs.language == "ar") "تذكير بملاحظة" else "Note Reminder" }}",
                                content = note.content.take(100).ifBlank {
                                    if (prefs.language == "ar") "تذكير بملاحظتك المسجلة في يومي" else "Reminder for your saved note"
                                }
                            )
                        }
                    } else if (billId != -1L) {
                        ReminderScheduler.syncMonthlyCommitments(context, db.billDao())
                        val bill = db.billDao().getBillByIdSync(billId)
                        if (bill != null && !bill.isPaid) {
                            val formattedAmount = DecimalFormat("#,###").format(bill.amount)
                            val (_, dueDateStr) = ReminderScheduler.calculateDaysLeftAndDateString(bill.dueDayOfMonth, bill.dueMonth)
                            NotificationHelper.showReminderNotification(
                                context = context,
                                taskId = 200000 + billId,
                                title = if (prefs.language == "ar") {
                                    "💳 تذكير بموعد تسديد: ${bill.title}"
                                } else {
                                    "💳 Bill Due Reminder: ${bill.title}"
                                },
                                content = if (prefs.language == "ar") {
                                    "المبلغ المطلوب: $formattedAmount د.ع • تاريخ الاستحقاق: $dueDateStr • التصنيف: ${bill.category}"
                                } else {
                                    "Amount Due: $formattedAmount IQD • Due Date: $dueDateStr • Category: ${bill.category}"
                                }
                            )
                            // If recurring monthly commitment, schedule next month's reminder at the exact same specified time
                            if (bill.dueMonth == 0 && bill.hasReminder && !bill.isInstallmentCompleted) {
                                val nextMonthTime = ReminderScheduler.computeNextMonthReminderTime(bill)
                                db.billDao().updateBill(bill.copy(reminderTime = nextMonthTime))
                                ReminderScheduler.scheduleBillReminder(context, bill.id, bill.title, nextMonthTime)
                            }
                        }
                    } else if (advanceBillId != -1L) {
                        ReminderScheduler.syncMonthlyCommitments(context, db.billDao())
                        val bill = db.billDao().getBillByIdSync(advanceBillId)
                        if (bill != null && !bill.isPaid) {
                            val formattedAmount = DecimalFormat("#,###").format(bill.amount)
                            val (daysLeft, dueDateStr) = ReminderScheduler.calculateDaysLeftAndDateString(bill.dueDayOfMonth, bill.dueMonth)
                            NotificationHelper.showUpcomingBillNotification(
                                context = context,
                                billId = bill.id,
                                billTitle = bill.title,
                                category = bill.category,
                                amountFormatted = formattedAmount,
                                dueDateText = dueDateStr,
                                daysLeft = minOf(daysLeft, daysBeforeDue),
                                playLoudAlarm = true
                            )
                        }
                    } else if (checkAllUpcomingBills) {
                        ReminderScheduler.syncMonthlyCommitments(context, db.billDao())
                        val unpaidBills = db.billDao().getUnpaidBillsSync()
                        ReminderScheduler.checkAndNotifyUpcomingBillsNow(context, unpaidBills)
                        ReminderScheduler.schedulePeriodicUpcomingBillsCheck(context)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
