package com.example.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.YawmiDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = YawmiDatabase.getInstance(context)
                    val now = System.currentTimeMillis()
                    val pendingTasks = db.taskDao().getPendingRemindersSync(now)

                    for (task in pendingTasks) {
                        task.reminderTime?.let { time ->
                            ReminderScheduler.scheduleReminder(
                                context = context,
                                taskId = task.id,
                                title = task.title,
                                reminderTimeMillis = time
                            )
                        }
                    }

                    val pendingNotes = db.noteDao().getPendingNoteRemindersSync(now)
                    for (note in pendingNotes) {
                        note.reminderTime?.let { time ->
                            ReminderScheduler.scheduleNoteReminder(
                                context = context,
                                noteId = note.id,
                                title = note.title,
                                reminderTimeMillis = time
                            )
                        }
                    }

                    val pendingBills = db.billDao().getPendingBillRemindersSync(now)
                    for (bill in pendingBills) {
                        bill.reminderTime?.let { time ->
                            ReminderScheduler.scheduleBillReminder(
                                context = context,
                                billId = bill.id,
                                title = bill.title,
                                reminderTimeMillis = time
                            )
                        }
                    }

                    val unpaidBills = db.billDao().getUnpaidBillsSync()
                    for (bill in unpaidBills) {
                        ReminderScheduler.scheduleAdvanceBillDueAlarms(context, bill)
                    }
                    ReminderScheduler.schedulePeriodicUpcomingBillsCheck(context)
                    ReminderScheduler.checkAndNotifyUpcomingBillsNow(context, unpaidBills)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
