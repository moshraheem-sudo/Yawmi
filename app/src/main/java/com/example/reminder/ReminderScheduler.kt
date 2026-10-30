package com.example.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object ReminderScheduler {

    fun scheduleReminder(
        context: Context,
        taskId: Long,
        title: String,
        reminderTimeMillis: Long
    ): Boolean {
        val now = System.currentTimeMillis()
        if (reminderTimeMillis < now - 60_000L) {
            return false
        }
        val triggerTime = maxOf(reminderTimeMillis, now + 2_000L)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return false

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("TASK_ID", taskId)
            putExtra("TASK_TITLE", title)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return setAlarmInternal(alarmManager, triggerTime, pendingIntent)
    }

    fun cancelReminder(context: Context, taskId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        try {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun scheduleNoteReminder(
        context: Context,
        noteId: Long,
        title: String,
        reminderTimeMillis: Long
    ): Boolean {
        val now = System.currentTimeMillis()
        if (reminderTimeMillis < now - 60_000L) {
            return false
        }
        val triggerTime = maxOf(reminderTimeMillis, now + 2_000L)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return false

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("NOTE_ID", noteId)
            putExtra("NOTE_TITLE", title)
        }

        val requestCode = 100000 + noteId.toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return setAlarmInternal(alarmManager, triggerTime, pendingIntent)
    }

    fun cancelNoteReminder(context: Context, noteId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderReceiver::class.java)
        val requestCode = 100000 + noteId.toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        try {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun scheduleBillReminder(
        context: Context,
        billId: Long,
        title: String,
        reminderTimeMillis: Long
    ): Boolean {
        val now = System.currentTimeMillis()
        if (reminderTimeMillis < now - 60_000L) {
            return false
        }
        val triggerTime = maxOf(reminderTimeMillis, now + 2_000L)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return false

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("BILL_ID", billId)
            putExtra("BILL_TITLE", title)
        }

        val requestCode = 200000 + billId.toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return setAlarmInternal(alarmManager, triggerTime, pendingIntent)
    }

    fun cancelBillReminder(context: Context, billId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderReceiver::class.java)
        val requestCode = 200000 + billId.toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        try {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        cancelAdvanceBillDueAlarms(context, billId)
    }

    /**
     * Automatically schedules advance notifications before the bill's due date (2 days before, 1 day before, and on due day)
     * so the user always gets reminded in the top Notification Center before a commitment is due.
     */
    fun scheduleAdvanceBillDueAlarms(
        context: Context,
        bill: com.example.data.local.entity.BillItem
    ) {
        if (bill.isPaid || bill.id <= 0L) {
            cancelAdvanceBillDueAlarms(context, bill.id)
            return
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val now = System.currentTimeMillis()

        val dueDateCal = computeNextDueDateCalendar(bill.dueDayOfMonth, bill.dueMonth)
        val daysBeforeList = listOf(2, 1, 0)

        for (daysBefore in daysBeforeList) {
            val triggerCal = (dueDateCal.clone() as java.util.Calendar).apply {
                add(java.util.Calendar.DAY_OF_YEAR, -daysBefore)
                set(java.util.Calendar.HOUR_OF_DAY, 10)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            if (triggerCal.timeInMillis > now + 5_000L) {
                val intent = Intent(context, ReminderReceiver::class.java).apply {
                    putExtra("ADVANCE_BILL_ID", bill.id)
                    putExtra("DAYS_BEFORE_DUE", daysBefore)
                }
                val requestCode = 250000 + (bill.id.toInt() * 10) + daysBefore
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                setAlarmInternal(alarmManager, triggerCal.timeInMillis, pendingIntent)
            }
        }
    }

    fun cancelAdvanceBillDueAlarms(context: Context, billId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        for (daysBefore in listOf(2, 1, 0)) {
            val intent = Intent(context, ReminderReceiver::class.java)
            val requestCode = 250000 + (billId.toInt() * 10) + daysBefore
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            try {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Schedules a recurring background alarm check every 8 hours so the app always monitors upcoming
     * due dates in the background and posts notifications to the top Notification Center.
     */
    fun schedulePeriodicUpcomingBillsCheck(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("CHECK_ALL_UPCOMING_BILLS", true)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            777777,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val nextTrigger = System.currentTimeMillis() + 6 * 60 * 60 * 1000L // 6 hours
        setAlarmInternal(alarmManager, nextTrigger, pendingIntent)
    }

    /**
     * Evaluates unpaid bills and posts a notification in the top Notification Center for any commitment
     * due within 3 days (or today), avoiding duplicate notifications on the same day unless forceNotifyBillId matches.
     */
    fun checkAndNotifyUpcomingBillsNow(
        context: Context,
        bills: List<com.example.data.local.entity.BillItem>,
        forceNotifyBillId: Long = -1L
    ) {
        val prefs = context.getSharedPreferences("yawmi_bill_due_alerts", Context.MODE_PRIVATE)
        val todayKey = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.US).format(java.util.Date())
        val formatter = java.text.DecimalFormat("#,###")

        for (bill in bills) {
            if (bill.isPaid) continue
            val (daysLeft, dueDateStr) = calculateDaysLeftAndDateString(bill.dueDayOfMonth, bill.dueMonth)
            if (daysLeft in 0..3) {
                val prefKey = "notified_${bill.id}_$todayKey"
                val alreadyNotifiedToday = prefs.getBoolean(prefKey, false)
                if (!alreadyNotifiedToday || bill.id == forceNotifyBillId) {
                    NotificationHelper.showUpcomingBillNotification(
                        context = context,
                        billId = bill.id,
                        billTitle = bill.title,
                        category = bill.category,
                        amountFormatted = formatter.format(bill.amount),
                        dueDateText = dueDateStr,
                        daysLeft = daysLeft,
                        playLoudAlarm = false
                    )
                    prefs.edit().putBoolean(prefKey, true).apply()
                }
            }
        }
    }

    /**
     * Automatically synchronizes monthly commitments at the start of each new month (or scheduled due cycle):
     * 1. Ensures any already-paid commitment has its payment timestamp & month recorded in paymentHistoryJson.
     * 2. When a new month arrives, any recurring commitment (dueMonth == 0) that has not completed all its installments
     *    automatically renews (isPaid = false) at the exact same scheduled time while preserving all previous paid months in history.
     */
    suspend fun syncMonthlyCommitments(
        context: Context,
        billDao: com.example.data.local.dao.BillDao
    ) {
        val currentMonthKey = com.example.data.local.entity.BillItem.currentMonthKey()
        val allBills = billDao.getAllBillsSync()
        for (bill in allBills) {
            if (bill.isPaid && bill.paymentHistoryJson.isBlank()) {
                // Backfill existing paid commitment so it appears in the monthly history log with date, time, and amount
                val recorded = bill.withRecordedPayment(
                    monthKey = bill.lastPaidMonthKey.ifBlank { currentMonthKey },
                    timestamp = bill.lastPaidAt ?: bill.createdAt,
                    paymentAmount = bill.amount
                )
                billDao.updateBill(recorded)
            } else if (bill.isPaid && bill.dueMonth == 0 && bill.lastPaidMonthKey.isNotBlank() && bill.lastPaidMonthKey < currentMonthKey) {
                // A new month has started! Automatically renew the commitment unless it is a completed installment
                if (!bill.isInstallmentCompleted) {
                    val nextReminderTime = computeNextMonthReminderTime(bill)
                    val renewedBill = bill.copy(
                        isPaid = false,
                        reminderTime = if (bill.hasReminder) nextReminderTime else bill.reminderTime
                    )
                    billDao.updateBill(renewedBill)
                    scheduleAdvanceBillDueAlarms(context, renewedBill)
                    if (renewedBill.hasReminder && renewedBill.reminderTime != null) {
                        scheduleBillReminder(context, renewedBill.id, renewedBill.title, renewedBill.reminderTime)
                    }
                }
            }
        }
    }

    /**
     * Computes the next month's reminder timestamp preserving the user's exact scheduled hour and minute.
     */
    fun computeNextMonthReminderTime(bill: com.example.data.local.entity.BillItem): Long {
        val nowCal = java.util.Calendar.getInstance()
        val baseCal = java.util.Calendar.getInstance().apply {
            if (bill.reminderTime != null) {
                timeInMillis = bill.reminderTime
            } else {
                set(java.util.Calendar.HOUR_OF_DAY, 9)
                set(java.util.Calendar.MINUTE, 0)
            }
        }
        val targetHour = baseCal.get(java.util.Calendar.HOUR_OF_DAY)
        val targetMinute = baseCal.get(java.util.Calendar.MINUTE)

        val nextCal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
            set(java.util.Calendar.HOUR_OF_DAY, targetHour)
            set(java.util.Calendar.MINUTE, targetMinute)
            val maxDay = getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
            set(java.util.Calendar.DAY_OF_MONTH, bill.dueDayOfMonth.coerceIn(1, maxDay))
            if (timeInMillis <= nowCal.timeInMillis) {
                add(java.util.Calendar.MONTH, 1)
                val nextMax = getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
                set(java.util.Calendar.DAY_OF_MONTH, bill.dueDayOfMonth.coerceIn(1, nextMax))
            }
        }
        return nextCal.timeInMillis
    }

    fun computeNextDueDateCalendar(dueDayOfMonth: Int, dueMonth: Int): java.util.Calendar {
        val nowCal = java.util.Calendar.getInstance()
        val targetCal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 23)
            set(java.util.Calendar.MINUTE, 59)
            set(java.util.Calendar.SECOND, 59)
        }
        if (dueMonth in 1..12) {
            targetCal.set(java.util.Calendar.MONTH, dueMonth - 1)
            val maxDay = targetCal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
            targetCal.set(java.util.Calendar.DAY_OF_MONTH, dueDayOfMonth.coerceIn(1, maxDay))
            if (targetCal.timeInMillis < nowCal.timeInMillis - 24 * 3600 * 1000L) {
                targetCal.add(java.util.Calendar.YEAR, 1)
            }
        } else {
            val maxDay = targetCal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
            targetCal.set(java.util.Calendar.DAY_OF_MONTH, dueDayOfMonth.coerceIn(1, maxDay))
            if (targetCal.get(java.util.Calendar.DAY_OF_MONTH) < nowCal.get(java.util.Calendar.DAY_OF_MONTH)) {
                targetCal.add(java.util.Calendar.MONTH, 1)
                val nextMaxDay = targetCal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
                targetCal.set(java.util.Calendar.DAY_OF_MONTH, dueDayOfMonth.coerceIn(1, nextMaxDay))
            }
        }
        return targetCal
    }

    fun calculateDaysLeftAndDateString(dueDayOfMonth: Int, dueMonth: Int): Pair<Int, String> {
        val nowCal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val dueCal = computeNextDueDateCalendar(dueDayOfMonth, dueMonth).apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val diffMs = dueCal.timeInMillis - nowCal.timeInMillis
        val daysLeft = (diffMs / (24 * 60 * 60 * 1000L)).toInt().coerceAtLeast(0)
        val dateStr = java.text.SimpleDateFormat("yyyy/MM/dd", java.util.Locale.getDefault()).format(dueCal.time)
        return Pair(daysLeft, dateStr)
    }

    private fun setAlarmInternal(
        alarmManager: AlarmManager,
        reminderTimeMillis: Long,
        pendingIntent: PendingIntent
    ): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminderTimeMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminderTimeMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderTimeMillis,
                    pendingIntent
                )
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    reminderTimeMillis,
                    pendingIntent
                )
                true
            } catch (e2: Exception) {
                e2.printStackTrace()
                false
            }
        }
    }
}
