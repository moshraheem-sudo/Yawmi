package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

data class BillPaymentRecord(
    val monthKey: String,          // e.g. "2026-10"
    val timestamp: Long,           // exact millis when paid
    val amount: Double,            // amount paid in that month
    val installmentIndex: Int = 1  // 1-based installment/month number
)

@Entity(tableName = "bills")
data class BillItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String = "اتصالات وإنترنت",
    val dueDayOfMonth: Int = 1,
    val dueMonth: Int = 0, // 0 = كل الشهور (شهرياً), 1..12 = شهر محدد
    val frequency: String = "شهرية",
    val priority: String = "مهم",
    val isPaid: Boolean = false,
    val isReserved: Boolean = true,
    val hasReminder: Boolean = false,
    val reminderTime: Long? = null,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    // Installment & Monthly Auto-Renewal History fields (v2.0.0)
    val totalInstallmentAmount: Double = 0.0, // المبلغ الكلي للأقساط (مثلاً 1,300,000 د.ع لأقساط السبلت، 0 = التزام شهري مستمر)
    val paidInstallmentAmount: Double = 0.0,  // إجمالي المبلغ المسدد عبر الأشهر
    val paidMonthsCount: Int = 0,             // عدد الأشهر المسلمة حتى الآن
    val lastPaidAt: Long? = null,             // تاريخ وتوقيت آخر تسديد
    val lastPaidMonthKey: String = "",        // مفتاح آخر شهر تم تسديده مثل "2026-10"
    val paymentHistoryJson: String = ""       // سجل الأشهر المسلمة: "monthKey|timestamp|amount|index;..."
) {
    val isInstallment: Boolean
        get() = totalInstallmentAmount > 0.0 && amount > 0.0

    val totalInstallmentMonths: Int
        get() = if (isInstallment) ceil(totalInstallmentAmount / amount).toInt().coerceAtLeast(1) else 0

    fun parsePaymentHistory(): List<BillPaymentRecord> {
        if (paymentHistoryJson.isBlank()) {
            return if (isPaid) {
                val fallbackMonth = lastPaidMonthKey.ifBlank { currentMonthKey(lastPaidAt ?: createdAt) }
                listOf(
                    BillPaymentRecord(
                        monthKey = fallbackMonth,
                        timestamp = lastPaidAt ?: createdAt,
                        amount = amount,
                        installmentIndex = maxOf(paidMonthsCount, 1)
                    )
                )
            } else {
                emptyList()
            }
        }
        return paymentHistoryJson
            .split(";")
            .mapNotNull { entry ->
                val parts = entry.trim().split("|")
                if (parts.size >= 3) {
                    val mKey = parts[0].trim()
                    val ts = parts[1].trim().toLongOrNull() ?: createdAt
                    val amt = parts[2].trim().toDoubleOrNull() ?: amount
                    val idx = parts.getOrNull(3)?.trim()?.toIntOrNull() ?: 1
                    if (mKey.isNotEmpty()) {
                        BillPaymentRecord(
                            monthKey = mKey,
                            timestamp = ts,
                            amount = amt,
                            installmentIndex = idx
                        )
                    } else null
                } else null
            }
            .sortedBy { it.monthKey }
            .mapIndexed { index, record ->
                record.copy(installmentIndex = index + 1)
            }
    }

    val effectivePaidMonthsCount: Int
        get() {
            val historySize = parsePaymentHistory().size
            return maxOf(paidMonthsCount, historySize, if (isPaid) 1 else 0)
        }

    val effectivePaidAmount: Double
        get() {
            val historySum = parsePaymentHistory().sumOf { it.amount }
            val countBasedSum = paidMonthsCount * amount
            return maxOf(paidInstallmentAmount, historySum, countBasedSum, if (isPaid) amount else 0.0)
        }

    val remainingInstallmentAmount: Double
        get() = if (isInstallment) (totalInstallmentAmount - effectivePaidAmount).coerceAtLeast(0.0) else 0.0

    val remainingInstallmentMonths: Int
        get() = if (isInstallment && amount > 0.0) {
            ceil(remainingInstallmentAmount / amount).toInt().coerceAtLeast(0)
        } else 0

    val isInstallmentCompleted: Boolean
        get() = isInstallment && remainingInstallmentAmount <= 0.0

    fun isPaidInMonth(monthKey: String): Boolean {
        return parsePaymentHistory().any { it.monthKey == monthKey } ||
            (isPaid && (lastPaidMonthKey == monthKey || (lastPaidMonthKey.isBlank() && monthKey == currentMonthKey())))
    }

    fun paymentRecordForMonth(monthKey: String): BillPaymentRecord? {
        return parsePaymentHistory().lastOrNull { it.monthKey == monthKey }
    }

    fun withRecordedPayment(
        monthKey: String = currentMonthKey(),
        timestamp: Long = System.currentTimeMillis(),
        paymentAmount: Double = amount
    ): BillItem {
        val currentList = parsePaymentHistory().toMutableList()
        val existingIdx = currentList.indexOfFirst { it.monthKey == monthKey }
        if (existingIdx >= 0) {
            currentList[existingIdx] = BillPaymentRecord(
                monthKey = monthKey,
                timestamp = timestamp,
                amount = paymentAmount,
                installmentIndex = existingIdx + 1
            )
        } else {
            currentList.add(
                BillPaymentRecord(
                    monthKey = monthKey,
                    timestamp = timestamp,
                    amount = paymentAmount,
                    installmentIndex = currentList.size + 1
                )
            )
        }
        val sorted = currentList.sortedBy { it.monthKey }.mapIndexed { idx, rec ->
            rec.copy(installmentIndex = idx + 1)
        }
        val newCount = sorted.size
        val newPaidAmount = sorted.sumOf { it.amount }
        return copy(
            isPaid = true,
            paidMonthsCount = newCount,
            paidInstallmentAmount = newPaidAmount,
            lastPaidAt = timestamp,
            lastPaidMonthKey = monthKey,
            paymentHistoryJson = encodePaymentHistory(sorted)
        )
    }

    fun withAdditionalInstallmentMonthPaid(
        timestamp: Long = System.currentTimeMillis()
    ): BillItem {
        val currentList = parsePaymentHistory().toMutableList()
        val nextMonthKey = if (currentList.isEmpty()) {
            currentMonthKey(timestamp)
        } else {
            val latestKey = currentList.maxOf { it.monthKey }
            val currKey = currentMonthKey(timestamp)
            if (latestKey < currKey) currKey else nextMonthKeyAfter(latestKey)
        }
        return withRecordedPayment(
            monthKey = nextMonthKey,
            timestamp = timestamp,
            paymentAmount = amount
        )
    }

    fun withRemovedPaymentForMonth(monthKey: String = currentMonthKey()): BillItem {
        val remaining = parsePaymentHistory()
            .filterNot { it.monthKey == monthKey }
            .sortedBy { it.monthKey }
            .mapIndexed { idx, rec -> rec.copy(installmentIndex = idx + 1) }
        val lastRec = remaining.lastOrNull()
        return copy(
            isPaid = false,
            paidMonthsCount = remaining.size,
            paidInstallmentAmount = remaining.sumOf { it.amount },
            lastPaidAt = lastRec?.timestamp,
            lastPaidMonthKey = lastRec?.monthKey ?: "",
            paymentHistoryJson = encodePaymentHistory(remaining)
        )
    }

    companion object {
        fun currentMonthKey(timeMillis: Long = System.currentTimeMillis()): String {
            val cal = Calendar.getInstance().apply { timeInMillis = timeMillis }
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH) + 1
            return String.format(Locale.US, "%04d-%02d", year, month)
        }

        fun nextMonthKeyAfter(monthKey: String): String {
            val parts = monthKey.split("-")
            val year = parts.getOrNull(0)?.toIntOrNull() ?: Calendar.getInstance().get(Calendar.YEAR)
            val month = parts.getOrNull(1)?.toIntOrNull() ?: (Calendar.getInstance().get(Calendar.MONTH) + 1)
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, (month - 1).coerceIn(0, 11))
                set(Calendar.DAY_OF_MONTH, 1)
                add(Calendar.MONTH, 1)
            }
            return String.format(
                Locale.US,
                "%04d-%02d",
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1
            )
        }

        fun previousMonthKeyBefore(monthKey: String, monthsAgo: Int): String {
            val parts = monthKey.split("-")
            val year = parts.getOrNull(0)?.toIntOrNull() ?: Calendar.getInstance().get(Calendar.YEAR)
            val month = parts.getOrNull(1)?.toIntOrNull() ?: (Calendar.getInstance().get(Calendar.MONTH) + 1)
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, (month - 1).coerceIn(0, 11))
                set(Calendar.DAY_OF_MONTH, 1)
                add(Calendar.MONTH, -monthsAgo)
            }
            return String.format(
                Locale.US,
                "%04d-%02d",
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1
            )
        }

        fun encodePaymentHistory(records: List<BillPaymentRecord>): String {
            return records.joinToString(";") { rec ->
                "${rec.monthKey}|${rec.timestamp}|${rec.amount}|${rec.installmentIndex}"
            }
        }

        fun buildInitialInstallmentHistory(
            initialPaidMonths: Int,
            monthlyAmount: Double,
            isPaidCurrentMonth: Boolean,
            existingHistory: List<BillPaymentRecord>,
            nowMillis: Long = System.currentTimeMillis()
        ): List<BillPaymentRecord> {
            val targetCount = maxOf(initialPaidMonths, if (isPaidCurrentMonth) 1 else 0)
            if (targetCount <= 0) return emptyList()
            if (existingHistory.size == targetCount) {
                return existingHistory.mapIndexed { index, rec ->
                    rec.copy(amount = monthlyAmount, installmentIndex = index + 1)
                }
            }
            val currentKey = currentMonthKey(nowMillis)
            val result = mutableListOf<BillPaymentRecord>()
            val startOffset = if (isPaidCurrentMonth) targetCount - 1 else targetCount
            for (i in 0 until targetCount) {
                val monthsAgo = startOffset - i
                val mKey = previousMonthKeyBefore(currentKey, monthsAgo)
                val existing = existingHistory.firstOrNull { it.monthKey == mKey }
                val cal = Calendar.getInstance().apply {
                    timeInMillis = nowMillis
                    add(Calendar.MONTH, -monthsAgo)
                }
                result.add(
                    BillPaymentRecord(
                        monthKey = mKey,
                        timestamp = existing?.timestamp ?: cal.timeInMillis,
                        amount = monthlyAmount,
                        installmentIndex = i + 1
                    )
                )
            }
            return result
        }

        fun formatMonthKey(monthKey: String, isAr: Boolean): String {
            val parts = monthKey.split("-")
            val year = parts.getOrNull(0)?.toIntOrNull() ?: return monthKey
            val month = parts.getOrNull(1)?.toIntOrNull() ?: return monthKey
            val arabicMonths = arrayOf(
                "كانون الثاني (يناير)",
                "شباط (فبراير)",
                "آذار (مارس)",
                "نيسان (أبريل)",
                "أيار (مايو)",
                "حزيران (يونيو)",
                "تموز (يوليو)",
                "آب (أغسطس)",
                "أيلول (سبتمبر)",
                "تشرين الأول (أكتوبر)",
                "تشرين الثاني (نوفمبر)",
                "كانون الأول (ديسمبر)"
            )
            val englishMonths = arrayOf(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            )
            val idx = (month - 1).coerceIn(0, 11)
            return if (isAr) {
                "شهر $month - ${arabicMonths[idx]} $year"
            } else {
                "${englishMonths[idx]} $year ($month/$year)"
            }
        }

        fun formatTimestampDateTime(timestamp: Long): String {
            return SimpleDateFormat("yyyy/MM/dd • hh:mm a", Locale.getDefault()).format(Date(timestamp))
        }
    }
}
