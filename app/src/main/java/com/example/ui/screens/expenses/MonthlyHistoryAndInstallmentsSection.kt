package com.example.ui.screens.expenses

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BillItem
import com.example.data.local.entity.BillPaymentRecord
import com.example.data.local.entity.ExpenseCategory
import com.example.data.local.entity.ExpenseItem
import com.example.localization.AppStrings
import com.example.ui.components.EmptyState
import com.example.ui.theme.CyanSkyPrimary
import com.example.util.CurrencyFormatterUtils
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MonthBillPaymentEntry(
    val bill: BillItem,
    val record: BillPaymentRecord
)

data class MonthlyArchiveSummary(
    val monthKey: String,
    val isCurrentMonth: Boolean,
    val billPayments: List<MonthBillPaymentEntry>,
    val unpaidBillsForMonth: List<BillItem>,
    val expenses: List<ExpenseItem>,
    val incomes: List<ExpenseItem>,
    val totalBillsPaidAmount: Double,
    val totalUnpaidBillsAmount: Double,
    val totalExpensesAmount: Double,
    val totalIncomesAmount: Double
) {
    val combinedSpentAmount: Double
        get() = totalBillsPaidAmount + totalExpensesAmount
}

/**
 * Box displayed inside BillCardItem showing installment progress, paid months, remaining months,
 * remaining balance, and automatic monthly renewal badge.
 */
@Composable
fun BillInstallmentAndRenewalSection(
    bill: BillItem,
    currencyFormatter: DecimalFormat,
    currencyUnit: String,
    isAr: Boolean,
    onOpenHistoryDialog: () -> Unit
) {
    val historyRecords = remember(bill) { bill.parsePaymentHistory() }
    val paidMonths = bill.effectivePaidMonthsCount
    val paidAmount = bill.effectivePaidAmount
    val lastPaidFormatted = remember(bill.lastPaidAt, historyRecords) {
        val ts = bill.lastPaidAt ?: historyRecords.lastOrNull()?.timestamp
        ts?.let { BillItem.formatTimestampDateTime(it) }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // شارة التحديث الشهري التلقائي + تاريخ وتوقيت آخر تسديد
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (bill.dueMonth == 0) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CyanSkyPrimary.copy(alpha = 0.14f),
                    border = BorderStroke(0.8.dp, CyanSkyPrimary.copy(alpha = 0.45f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Autorenew,
                            contentDescription = null,
                            tint = CyanSkyPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAr) "يتجدد تلقائياً كل شهر بنفس الموعد" else "Auto-renews monthly at scheduled time",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanSkyPrimary
                        )
                    }
                }
            }

            if (bill.isPaid && lastPaidFormatted != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.14f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAr) "سُدد: $lastPaidFormatted" else "Paid: $lastPaidFormatted",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }
        }

        // بطاقة متابعة الأقساط والأشهر المسلمة والمتبقية (مثل أقساط سبلت 1,300,000 د.ع)
        if (bill.isInstallment || historyRecords.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (bill.isInstallment) Color(0xFF0F2942) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                border = BorderStroke(
                    1.dp,
                    if (bill.isInstallment) CyanSkyPrimary.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenHistoryDialog() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(11.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (bill.isInstallment) {
                        val totalMonths = bill.totalInstallmentMonths
                        val remainingMonths = bill.remainingInstallmentMonths
                        val remainingAmount = bill.remainingInstallmentAmount
                        val progress = if (bill.totalInstallmentAmount > 0) {
                            (paidAmount / bill.totalInstallmentAmount).toFloat().coerceIn(0f, 1f)
                        } else 0f

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isAr) "إجمالي الأقساط الكلي: ${currencyFormatter.format(bill.totalInstallmentAmount)} $currencyUnit"
                                else "Total Installment: ${currencyFormatter.format(bill.totalInstallmentAmount)} $currencyUnit",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyanSkyPrimary
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (isAr) "${(progress * 100).toInt()}% مكتمل" else "${(progress * 100).toInt()}% Done",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF34D399),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF10B981),
                            trackColor = Color.White.copy(alpha = 0.15f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isAr) "الأشهر المسلمة ($paidMonths من $totalMonths شهر):"
                                    else "Paid ($paidMonths of $totalMonths mos):",
                                    fontSize = 10.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                                Text(
                                    text = "${currencyFormatter.format(paidAmount)} $currencyUnit",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF34D399)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (isAr) "المتبقي ($remainingMonths شهر متبقي):"
                                    else "Remaining ($remainingMonths mos left):",
                                    fontSize = 10.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                                Text(
                                    text = "${currencyFormatter.format(remainingAmount)} $currencyUnit",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFFD166)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = if (bill.isInstallment) Color(0xFF93C5FD) else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (isAr) "سجل الأشهر المسلمة: $paidMonths شهر (${currencyFormatter.format(paidAmount)} $currencyUnit)"
                                else "Paid Months History: $paidMonths mos (${currencyFormatter.format(paidAmount)} $currencyUnit)",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (bill.isInstallment) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = if (isAr) "مراجعة الأشهر ⬅" else "View Months ⬅",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (bill.isInstallment) CyanSkyPrimary else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dialog to review all paid months with exact dates, times, amounts, remaining months,
 * and ability to record a new monthly installment or start a new month cycle.
 */
@Composable
fun BillInstallmentHistoryDialog(
    bill: BillItem,
    currencyFormatter: DecimalFormat,
    currencyUnit: String,
    isAr: Boolean,
    onDismiss: () -> Unit,
    onUpdateBill: (BillItem) -> Unit
) {
    val historyRecords = remember(bill) { bill.parsePaymentHistory().sortedByDescending { it.monthKey } }
    val paidMonths = bill.effectivePaidMonthsCount
    val paidAmount = bill.effectivePaidAmount
    val totalMonths = bill.totalInstallmentMonths
    val remainingMonths = bill.remainingInstallmentMonths
    val remainingAmount = bill.remainingInstallmentAmount

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyanSkyPrimary.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = CyanSkyPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = bill.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = if (isAr) "سجل الأشهر المسلمة ومتابعة الأقساط والتواريخ"
                        else "Paid Months History & Installment Tracker",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // بطاقة الإحصائيات الكلية للقسط أو الالتزام
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2942)),
                    border = BorderStroke(1.dp, CyanSkyPrimary.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isAr) "مبلغ الدفعة الشهرية:" else "Monthly Payment:",
                                fontSize = 11.5.sp,
                                color = Color(0xFFCBD5E1)
                            )
                            Text(
                                text = "${currencyFormatter.format(bill.amount)} $currencyUnit",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        if (bill.isInstallment) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isAr) "المبلغ الكلي للأقساط ($totalMonths شهر):" else "Total Contract Amount ($totalMonths mos):",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                                Text(
                                    text = "${currencyFormatter.format(bill.totalInstallmentAmount)} $currencyUnit",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CyanSkyPrimary
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isAr) "الأشهر المسلمة كاملاً:" else "Total Paid Months:",
                                fontSize = 11.5.sp,
                                color = Color(0xFFCBD5E1)
                            )
                            Text(
                                text = if (isAr) "$paidMonths شهر (${currencyFormatter.format(paidAmount)} $currencyUnit)"
                                else "$paidMonths mos (${currencyFormatter.format(paidAmount)} $currencyUnit)",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF34D399)
                            )
                        }

                        if (bill.isInstallment) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isAr) "المتبقي (الأشهر والمبلغ):" else "Remaining (Months & Amount):",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                                Text(
                                    text = if (isAr) "متبقي $remainingMonths شهر (${currencyFormatter.format(remainingAmount)} $currencyUnit)"
                                    else "$remainingMonths mos left (${currencyFormatter.format(remainingAmount)} $currencyUnit)",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFFD166)
                                )
                            }
                        }
                    }
                }

                // أزرار التحكم السريعة (تسليم دفعة شهر جديد / بدء دورة شهر جديد)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!bill.isInstallmentCompleted) {
                        Button(
                            onClick = {
                                val updated = bill.withAdditionalInstallmentMonthPaid(System.currentTimeMillis())
                                onUpdateBill(updated)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAr) "تسليم دفعة شهر جديد" else "Log New Month Payment",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (bill.isPaid && !bill.isInstallmentCompleted) {
                        OutlinedButton(
                            onClick = {
                                // Renew for a new monthly cycle while keeping all history records intact
                                onUpdateBill(bill.copy(isPaid = false))
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, CyanSkyPrimary),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Autorenew,
                                contentDescription = null,
                                tint = CyanSkyPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAr) "تجديد لدورة شهر جديد" else "Renew for New Month",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanSkyPrimary
                            )
                        }
                    }
                }

                Text(
                    text = if (isAr) "تفاصيل الأشهر المسلمة بالتاريخ والتوقيت والمبلغ (${historyRecords.size}):"
                    else "Paid Months Breakdown by Date, Time & Amount (${historyRecords.size}):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (historyRecords.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isAr) "لم يتم تسجيل أي دفعة شهرية بعد لهذا الالتزام."
                            else "No monthly payments recorded yet.",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                } else {
                    historyRecords.forEach { rec ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                            border = BorderStroke(0.8.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF10B981).copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = if (bill.isInstallment) {
                                                    if (isAr) "القسط ${rec.installmentIndex} من $totalMonths"
                                                    else "Installment ${rec.installmentIndex}/$totalMonths"
                                                } else {
                                                    if (isAr) "الدفعة #${rec.installmentIndex}"
                                                    else "Payment #${rec.installmentIndex}"
                                                },
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF10B981),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = BillItem.formatMonthKey(rec.monthKey, isAr),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "📅 ${BillItem.formatTimestampDateTime(rec.timestamp)}",
                                        fontSize = 10.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${currencyFormatter.format(rec.amount)} $currencyUnit",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF10B981)
                                    )
                                    IconButton(
                                        onClick = {
                                            val updated = bill.withRemovedPaymentForMonth(rec.monthKey)
                                            onUpdateBill(updated)
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete month record",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                Text(if (isAr) "إغلاق" else "Close", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    )
}

/**
 * TAB 3: MONTHLY HISTORY & ARCHIVE WINDOW (نافذة السجل الشهرية: كل شهر يفتح داخله التزامات ومصاريف بالتاريخ والتوقيت والمبلغ وإحصائيات وأقساط)
 */
@Composable
fun MonthlyHistoryTabContent(
    bills: List<BillItem>,
    expenses: List<ExpenseItem>,
    incomes: List<ExpenseItem>,
    totalIncome: Double,
    currencyFormatter: DecimalFormat,
    strings: AppStrings,
    isAr: Boolean,
    onOpenAddExpense: () -> Unit,
    onOpenAddIncome: () -> Unit,
    onEditExpense: (ExpenseItem) -> Unit,
    onDeleteExpense: (ExpenseItem) -> Unit,
    onClearAllExpenses: () -> Unit,
    onClearAllIncomes: () -> Unit,
    onPayBill: (BillItem) -> Unit,
    onEditBill: (BillItem) -> Unit,
    onUpdateBill: (BillItem) -> Unit
) {
    val currentMonthKey = remember { BillItem.currentMonthKey() }
    var selectedMonthKey by remember { mutableStateOf<String?>(null) }
    var inspectingBillForHistory by remember { mutableStateOf<BillItem?>(null) }

    // Gather all months from current month, all expenses, all incomes, and all bill payment records
    val monthlySummaries = remember(bills, expenses, incomes, currentMonthKey) {
        val allMonthKeys = mutableSetOf(currentMonthKey)
        expenses.forEach { allMonthKeys.add(BillItem.currentMonthKey(it.date)) }
        incomes.forEach { allMonthKeys.add(BillItem.currentMonthKey(it.date)) }
        bills.forEach { bill ->
            bill.parsePaymentHistory().forEach { rec ->
                allMonthKeys.add(rec.monthKey)
            }
        }

        allMonthKeys
            .sortedDescending()
            .map { mKey ->
                val isCurrent = mKey == currentMonthKey
                val monthBillPayments = bills.mapNotNull { bill ->
                    val rec = bill.paymentRecordForMonth(mKey)
                    if (rec != null) MonthBillPaymentEntry(bill, rec) else null
                }.sortedByDescending { it.record.timestamp }

                val unpaidForMonth = if (isCurrent) {
                    bills.filter { !it.isPaidInMonth(mKey) && !it.isInstallmentCompleted }
                } else {
                    emptyList()
                }

                val monthExpenses = expenses.filter { BillItem.currentMonthKey(it.date) == mKey }
                    .sortedByDescending { it.date }
                val monthIncomes = incomes.filter { BillItem.currentMonthKey(it.date) == mKey }
                    .sortedByDescending { it.date }

                MonthlyArchiveSummary(
                    monthKey = mKey,
                    isCurrentMonth = isCurrent,
                    billPayments = monthBillPayments,
                    unpaidBillsForMonth = unpaidForMonth,
                    expenses = monthExpenses,
                    incomes = monthIncomes,
                    totalBillsPaidAmount = monthBillPayments.sumOf { it.record.amount },
                    totalUnpaidBillsAmount = unpaidForMonth.sumOf { it.amount },
                    totalExpensesAmount = monthExpenses.sumOf { it.amount },
                    totalIncomesAmount = monthIncomes.sumOf { it.amount }
                )
            }
    }

    val selectedSummary = remember(selectedMonthKey, monthlySummaries) {
        selectedMonthKey?.let { key ->
            monthlySummaries.firstOrNull { it.monthKey == key }
        }
    }

    if (inspectingBillForHistory != null) {
        val latestBill = bills.firstOrNull { it.id == inspectingBillForHistory?.id } ?: inspectingBillForHistory!!
        BillInstallmentHistoryDialog(
            bill = latestBill,
            currencyFormatter = currencyFormatter,
            currencyUnit = strings.currencyUnit,
            isAr = isAr,
            onDismiss = { inspectingBillForHistory = null },
            onUpdateBill = { updated ->
                onUpdateBill(updated)
                inspectingBillForHistory = updated
            }
        )
    }

    if (selectedSummary != null) {
        BackHandler {
            selectedMonthKey = null
        }
        InsideMonthDetailWindow(
            summary = selectedSummary,
            allBills = bills,
            totalIncome = totalIncome,
            currencyFormatter = currencyFormatter,
            strings = strings,
            isAr = isAr,
            onBackToMonthsList = { selectedMonthKey = null },
            onOpenAddExpense = onOpenAddExpense,
            onOpenAddIncome = onOpenAddIncome,
            onEditExpense = onEditExpense,
            onDeleteExpense = onDeleteExpense,
            onClearAllExpenses = onClearAllExpenses,
            onClearAllIncomes = onClearAllIncomes,
            onPayBill = onPayBill,
            onEditBill = onEditBill,
            onInspectBillHistory = { bill -> inspectingBillForHistory = bill }
        )
    } else {
        // =====================================================================
        // LEVEL 1: MONTHLY ARCHIVE LIST + INSTALLMENTS TRACKER SUMMARY
        // =====================================================================
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Header Card explaining the Monthly History System
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2942)),
                    border = BorderStroke(1.2.dp, CyanSkyPrimary.copy(alpha = 0.55f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(CyanSkyPrimary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = CyanSkyPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isAr) "سجل الأشهر والإحصائيات والأقساط" else "Monthly Archive, Stats & Installments",
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = if (isAr) "اضغط على أي شهر لفتح نافذته (التزامات ومصاريف بالتاريخ والوقت وإحصائيات)"
                                        else "Tap any month to open its commitments, expenses & stats",
                                        fontSize = 10.5.sp,
                                        color = Color(0xFF93C5FD)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Cumulative Installments & Recurring Commitments Tracker Card (متابعة الأقساط والأشهر المسلمة والمتبقية)
            if (bills.isNotEmpty()) {
                item {
                    InstallmentsAndCommitmentsTrackerCard(
                        bills = bills,
                        currencyFormatter = currencyFormatter,
                        currencyUnit = strings.currencyUnit,
                        isAr = isAr,
                        onInspectBill = { bill -> inspectingBillForHistory = bill }
                    )
                }
            }

            // 3. Section Title for Months List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "قائمة الأشهر المسجلة (${monthlySummaries.size} شهر)"
                        else "Recorded Months (${monthlySummaries.size})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isAr) "اختر الشهر لفتح نافذته" else "Tap a month to open",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 4. Monthly Folder Cards
            items(monthlySummaries, key = { "month_card_${it.monthKey}" }) { monthSummary ->
                MonthFolderCardItem(
                    summary = monthSummary,
                    totalIncome = totalIncome,
                    currencyFormatter = currencyFormatter,
                    currencyUnit = strings.currencyUnit,
                    isAr = isAr,
                    onClick = { selectedMonthKey = monthSummary.monthKey }
                )
            }
        }
    }
}

@Composable
private fun InstallmentsAndCommitmentsTrackerCard(
    bills: List<BillItem>,
    currencyFormatter: DecimalFormat,
    currencyUnit: String,
    isAr: Boolean,
    onInspectBill: (BillItem) -> Unit
) {
    val installmentBills = remember(bills) { bills.filter { it.isInstallment } }
    val displayBills = if (installmentBills.isNotEmpty()) installmentBills else bills

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.2.dp, Color(0xFF10B981).copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PieChart,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (isAr) "إحصائيات الأقساط والأشهر المسلمة والمتبقية"
                            else "Installments & Paid/Remaining Months Stats",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isAr) "اضغط على أي التزام لمراجعة تاريخ جميع الأشهر المسلمة وكم شهر تبقى"
                            else "Tap any item to review all paid months and remaining balance",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            displayBills.forEach { bill ->
                val history = bill.parsePaymentHistory()
                val paidMonths = bill.effectivePaidMonthsCount
                val paidAmount = bill.effectivePaidAmount
                val totalMonths = bill.totalInstallmentMonths
                val remainingMonths = bill.remainingInstallmentMonths
                val remainingAmount = bill.remainingInstallmentAmount

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    border = BorderStroke(
                        1.dp,
                        if (bill.isInstallment) CyanSkyPrimary.copy(alpha = 0.45f)
                        else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onInspectBill(bill) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(11.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = bill.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.16f)
                            ) {
                                Text(
                                    text = if (bill.isInstallment) {
                                        if (isAr) "المبلغ الكلي: ${currencyFormatter.format(bill.totalInstallmentAmount)} $currencyUnit"
                                        else "Total: ${currencyFormatter.format(bill.totalInstallmentAmount)} $currencyUnit"
                                    } else {
                                        if (isAr) "الدفعة الشهرية: ${currencyFormatter.format(bill.amount)} $currencyUnit"
                                        else "Monthly: ${currencyFormatter.format(bill.amount)} $currencyUnit"
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (bill.isInstallment) {
                            val progress = if (bill.totalInstallmentAmount > 0) {
                                (paidAmount / bill.totalInstallmentAmount).toFloat().coerceIn(0f, 1f)
                            } else 0f

                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = Color(0xFF10B981),
                                trackColor = MaterialTheme.colorScheme.outlineVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isAr) "المسلم: $paidMonths من $totalMonths شهر (${currencyFormatter.format(paidAmount)} $currencyUnit)"
                                    else "Paid: $paidMonths/$totalMonths mos (${currencyFormatter.format(paidAmount)} $currencyUnit)",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                                Text(
                                    text = if (isAr) "المتبقي: $remainingMonths شهر (${currencyFormatter.format(remainingAmount)} $currencyUnit)"
                                    else "Left: $remainingMonths mos (${currencyFormatter.format(remainingAmount)} $currencyUnit)",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF59E0B)
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isAr) "الأشهر المسلمة: $paidMonths شهر (${currencyFormatter.format(paidAmount)} $currencyUnit)"
                                    else "Paid Months: $paidMonths mos (${currencyFormatter.format(paidAmount)} $currencyUnit)",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                                Text(
                                    text = if (isAr) "تاريخ الأشهر المسلمة ⬅" else "History ⬅",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        if (history.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                history.forEach { rec ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.surface
                                    ) {
                                        Text(
                                            text = "✓ ${rec.monthKey}",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF10B981),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthFolderCardItem(
    summary: MonthlyArchiveSummary,
    totalIncome: Double,
    currencyFormatter: DecimalFormat,
    currencyUnit: String,
    isAr: Boolean,
    onClick: () -> Unit
) {
    val netRemaining = totalIncome - summary.combinedSpentAmount

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("month_archive_card_${summary.monthKey}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (summary.isCurrentMonth) Color(0xFF132A3E) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (summary.isCurrentMonth) 1.5.dp else 1.dp,
            color = if (summary.isCurrentMonth) Color(0xFF10B981) else MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Month Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (summary.isCurrentMonth) Color(0xFF10B981).copy(alpha = 0.22f)
                                else CyanSkyPrimary.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = if (summary.isCurrentMonth) Color(0xFF10B981) else CyanSkyPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = BillItem.formatMonthKey(summary.monthKey, isAr),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (summary.isCurrentMonth) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isAr) {
                                "${summary.billPayments.size} التزام مسدد • ${summary.expenses.size} مصروف يومي"
                            } else {
                                "${summary.billPayments.size} paid bills • ${summary.expenses.size} expenses"
                            },
                            fontSize = 11.sp,
                            color = if (summary.isCurrentMonth) Color(0xFFCBD5E1) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (summary.isCurrentMonth) Color(0xFF10B981) else MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = if (summary.isCurrentMonth) (if (isAr) "الشهر الحالي" else "Current")
                        else summary.monthKey,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (summary.isCurrentMonth) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Summary Grid inside Month Card: التزامات الشهر | مصاريف الشهر | المجموع الكلي للشهر
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF0F2942).copy(alpha = 0.75f),
                    border = BorderStroke(0.8.dp, CyanSkyPrimary.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = if (isAr) "التزامات الشهر (${summary.billPayments.size})" else "Commitments (${summary.billPayments.size})",
                            fontSize = 10.sp,
                            color = Color(0xFF93C5FD)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${currencyFormatter.format(summary.totalBillsPaidAmount)} $currencyUnit",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF3B1219).copy(alpha = 0.85f),
                    border = BorderStroke(0.8.dp, Color(0xFFEF4444).copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = if (isAr) "مصاريف الشهر (${summary.expenses.size})" else "Expenses (${summary.expenses.size})",
                            fontSize = 10.sp,
                            color = Color(0xFFFCA5A5)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${currencyFormatter.format(summary.totalExpensesAmount)} $currencyUnit",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Total spent & Net remaining row + Open Month Window CTA
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (summary.isCurrentMonth) Color.Black.copy(alpha = 0.25f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isAr) "إجمالي الصرف (التزامات + مصاريف): ${currencyFormatter.format(summary.combinedSpentAmount)} $currencyUnit"
                            else "Total Spent: ${currencyFormatter.format(summary.combinedSpentAmount)} $currencyUnit",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD166)
                        )
                        if (totalIncome > 0) {
                            Text(
                                text = if (isAr) "المتبقي من الراتب: ${currencyFormatter.format(netRemaining)} $currencyUnit"
                                else "Remaining Salary: ${currencyFormatter.format(netRemaining)} $currencyUnit",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (netRemaining >= 0) Color(0xFF34D399) else Color(0xFFF87171)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isAr) "فتح نافذة الشهر" else "Open Month",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF10B981)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * LEVEL 2: INSIDE MONTH DETAIL WINDOW (نافذة الشهر التفصيلية: التزامات ومصاريف بالتاريخ والتوقيت والمبلغ + إحصائيات وأقساط)
 */
@Composable
private fun InsideMonthDetailWindow(
    summary: MonthlyArchiveSummary,
    allBills: List<BillItem>,
    totalIncome: Double,
    currencyFormatter: DecimalFormat,
    strings: AppStrings,
    isAr: Boolean,
    onBackToMonthsList: () -> Unit,
    onOpenAddExpense: () -> Unit,
    onOpenAddIncome: () -> Unit,
    onEditExpense: (ExpenseItem) -> Unit,
    onDeleteExpense: (ExpenseItem) -> Unit,
    onClearAllExpenses: () -> Unit,
    onClearAllIncomes: () -> Unit,
    onPayBill: (BillItem) -> Unit,
    onEditBill: (BillItem) -> Unit,
    onInspectBillHistory: (BillItem) -> Unit
) {
    // 0 = شامل (الكل), 1 = التزامات الشهر, 2 = مصاريف الشهر, 3 = الإحصائيات والأقساط, 4 = المبالغ المضافة
    var activeSection by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredBillPayments = remember(summary.billPayments, searchQuery) {
        if (searchQuery.isBlank()) summary.billPayments
        else summary.billPayments.filter {
            it.bill.title.contains(searchQuery, ignoreCase = true) ||
                it.bill.category.contains(searchQuery, ignoreCase = true)
        }
    }

    val filteredExpenses = remember(summary.expenses, searchQuery) {
        if (searchQuery.isBlank()) summary.expenses
        else summary.expenses.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                it.note.contains(searchQuery, ignoreCase = true)
        }
    }

    val filteredIncomes = remember(summary.incomes, searchQuery) {
        if (searchQuery.isBlank()) summary.incomes
        else summary.incomes.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                it.note.contains(searchQuery, ignoreCase = true)
        }
    }

    val netRemaining = totalIncome - summary.combinedSpentAmount

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Top Navigation Header inside the Month Window
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2942)),
                border = BorderStroke(1.2.dp, Color(0xFF10B981).copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = BillItem.formatMonthKey(summary.monthKey, isAr),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = if (isAr) "نافذة التزامات ومصاريف وإحصائيات الشهر بالتاريخ والتوقيت والمبلغ"
                            else "Month Commitments, Expenses & Statistics by Date, Time & Amount",
                            fontSize = 10.5.sp,
                            color = Color(0xFF93C5FD)
                        )
                    }

                    OutlinedButton(
                        onClick = onBackToMonthsList,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, CyanSkyPrimary),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isAr) "كل الأشهر ↩" else "All Months ↩",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanSkyPrimary
                        )
                    }
                }
            }
        }

        // 2. Section Filter Chips inside the Month Window
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val tabs = if (isAr) {
                    listOf(
                        0 to "شامل (الكل)",
                        1 to "التزامات الشهر (${summary.billPayments.size})",
                        2 to "مصاريف الشهر (${summary.expenses.size})",
                        3 to "الإحصائيات والأقساط",
                        4 to "المبالغ المضافة (${summary.incomes.size})"
                    )
                } else {
                    listOf(
                        0 to "All Overview",
                        1 to "Commitments (${summary.billPayments.size})",
                        2 to "Expenses (${summary.expenses.size})",
                        3 to "Stats & Installments",
                        4 to "Added Income (${summary.incomes.size})"
                    )
                }

                tabs.forEach { (idx, label) ->
                    FilterChip(
                        selected = activeSection == idx,
                        onClick = { activeSection = idx },
                        label = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (activeSection == idx) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }

        // 3. Search Bar inside the Month Window
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        if (isAr) "ابحث في التزامات أو مصاريف هذا الشهر..." else "Search this month's commitments or expenses...",
                        fontSize = 12.sp
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                } else null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF10B981),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
        }

        // =====================================================================
        // SECTION A: MONTH STATISTICS & INSTALLMENTS PROGRESS
        // =====================================================================
        if (activeSection == 0 || activeSection == 3) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B4332)),
                    border = BorderStroke(1.2.dp, Color(0xFF10B981).copy(alpha = 0.6f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        Text(
                            text = if (isAr) "📊 إحصائيات ${BillItem.formatMonthKey(summary.monthKey, true)}"
                            else "📊 Statistics for ${BillItem.formatMonthKey(summary.monthKey, false)}",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFD8F3DC)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isAr) "مجموع التزامات الشهر المسددة (${summary.billPayments.size}):"
                                else "Paid Commitments (${summary.billPayments.size}):",
                                fontSize = 11.5.sp,
                                color = Color(0xFFB7E4C7)
                            )
                            Text(
                                text = "${currencyFormatter.format(summary.totalBillsPaidAmount)} ${strings.currencyUnit}",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF93C5FD)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isAr) "+ مجموع مصاريف الشهر اليومية (${summary.expenses.size}):"
                                else "+ Daily Expenses (${summary.expenses.size}):",
                                fontSize = 11.5.sp,
                                color = Color(0xFFB7E4C7)
                            )
                            Text(
                                text = "${currencyFormatter.format(summary.totalExpensesAmount)} ${strings.currencyUnit}",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFCA5A5)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isAr) "= إجمالي الصرف الكلي للشهر (الالتزامات + المصاريف):"
                                else "= Combined Month Total:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFFD166)
                            )
                            Text(
                                text = "-${currencyFormatter.format(summary.combinedSpentAmount)} ${strings.currencyUnit}",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFFD166)
                            )
                        }

                        if (totalIncome > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color.White.copy(alpha = 0.2f))
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isAr) "النهائي للراتب والدخل:" else "Final Total Salary:",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFFB7E4C7)
                                )
                                Text(
                                    text = "+${currencyFormatter.format(totalIncome)} ${strings.currencyUnit}",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isAr) "الصافي المتبقي من الراتب:" else "Net Remaining Salary:",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${currencyFormatter.format(netRemaining)} ${strings.currencyUnit}",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (netRemaining >= 0) Color(0xFF6EE7B7) else Color(0xFFF87171)
                                )
                            }
                        }
                    }
                }
            }

            if (allBills.isNotEmpty()) {
                item {
                    InstallmentsAndCommitmentsTrackerCard(
                        bills = allBills,
                        currencyFormatter = currencyFormatter,
                        currencyUnit = strings.currencyUnit,
                        isAr = isAr,
                        onInspectBill = onInspectBillHistory
                    )
                }
            }
        }

        // =====================================================================
        // SECTION B: MONTH COMMITMENTS BY DATE, TIME & AMOUNT (التزامات الشهر بالتاريخ والتوقيت والمبلغ)
        // =====================================================================
        if (activeSection == 0 || activeSection == 1) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "💳 التزامات الشهر المسددة بالتاريخ والتوقيت (${filteredBillPayments.size})"
                        else "💳 Paid Month Commitments (${filteredBillPayments.size})",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${currencyFormatter.format(summary.totalBillsPaidAmount)} ${strings.currencyUnit}",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyanSkyPrimary
                    )
                }
            }

            if (filteredBillPayments.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isAr) "لا توجد التزامات مسددة في هذا الشهر بعد."
                            else "No paid commitments recorded in this month.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            } else {
                items(
                    filteredBillPayments,
                    key = { "month_bill_${summary.monthKey}_${it.bill.id}_${it.record.timestamp}" }
                ) { entry ->
                    MonthPaidBillCardItem(
                        entry = entry,
                        currencyFormatter = currencyFormatter,
                        currencyUnit = strings.currencyUnit,
                        isAr = isAr,
                        onInspectHistory = { onInspectBillHistory(entry.bill) },
                        onEditBill = { onEditBill(entry.bill) }
                    )
                }
            }

            // Unpaid commitments for the current month
            if (summary.isCurrentMonth && summary.unpaidBillsForMonth.isNotEmpty()) {
                item {
                    Text(
                        text = if (isAr) "⏳ التزامات هذا الشهر غير المسددة بعد (${summary.unpaidBillsForMonth.size})"
                        else "⏳ Unpaid Commitments This Month (${summary.unpaidBillsForMonth.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF87171)
                    )
                }

                items(
                    summary.unpaidBillsForMonth,
                    key = { "unpaid_month_bill_${it.id}" }
                ) { unpaidBill ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.45f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = unpaidBill.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${currencyFormatter.format(unpaidBill.amount)} ${strings.currencyUnit} • ${unpaidBill.category}",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFF87171)
                                )
                                if (unpaidBill.isInstallment) {
                                    Text(
                                        text = if (isAr) "متبقي ${unpaidBill.remainingInstallmentMonths} شهر (${currencyFormatter.format(unpaidBill.remainingInstallmentAmount)} ${strings.currencyUnit})"
                                        else "${unpaidBill.remainingInstallmentMonths} months remaining",
                                        fontSize = 10.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Button(
                                onClick = { onPayBill(unpaidBill) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isAr) "تسديد الآن" else "Pay Now",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // =====================================================================
        // SECTION C: MONTH DAILY EXPENSES BY DATE, TIME & AMOUNT (مصاريف الشهر بالتاريخ والتوقيت والمبلغ)
        // =====================================================================
        if (activeSection == 0 || activeSection == 2) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isAr) "🛒 مصاريف الشهر بالتاريخ والتوقيت والمبلغ (${filteredExpenses.size})"
                            else "🛒 Month Expenses by Date, Time & Amount (${filteredExpenses.size})",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${currencyFormatter.format(summary.totalExpensesAmount)} ${strings.currencyUnit}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFEF4444)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = onOpenAddExpense,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAr) "إضافة مصروف" else "Add Expense",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (summary.expenses.isNotEmpty() && summary.isCurrentMonth) {
                            OutlinedButton(
                                onClick = onClearAllExpenses,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.6f)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (filteredExpenses.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isAr) "لا توجد مصاريف يومية مسجلة في هذا الشهر."
                            else "No daily expenses recorded in this month.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            } else {
                items(filteredExpenses, key = { "month_exp_${it.id}" }) { expItem ->
                    MonthTransactionDetailedCard(
                        item = expItem,
                        currencyFormatter = currencyFormatter,
                        currencyUnit = strings.currencyUnit,
                        isAr = isAr,
                        onEdit = { onEditExpense(expItem) },
                        onDelete = { onDeleteExpense(expItem) }
                    )
                }
            }
        }

        // =====================================================================
        // SECTION D: ADDED INCOMES IN MONTH (المبالغ المضافة والأعمال الخارجية)
        // =====================================================================
        if (activeSection == 0 || activeSection == 4) {
            if (filteredIncomes.isNotEmpty() || activeSection == 4) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "💰 المبالغ المضافة في الشهر (${filteredIncomes.size})"
                            else "💰 Added Incomes in Month (${filteredIncomes.size})",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Button(
                            onClick = onOpenAddIncome,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAr) "إضافة مبلغ" else "Add Income",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                items(filteredIncomes, key = { "month_inc_${it.id}" }) { incItem ->
                    MonthTransactionDetailedCard(
                        item = incItem,
                        currencyFormatter = currencyFormatter,
                        currencyUnit = strings.currencyUnit,
                        isAr = isAr,
                        onEdit = { onEditExpense(incItem) },
                        onDelete = { onDeleteExpense(incItem) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthPaidBillCardItem(
    entry: MonthBillPaymentEntry,
    currencyFormatter: DecimalFormat,
    currencyUnit: String,
    isAr: Boolean,
    onInspectHistory: () -> Unit,
    onEditBill: () -> Unit
) {
    val bill = entry.bill
    val record = entry.record
    val dateTimeStr = remember(record.timestamp) {
        BillItem.formatTimestampDateTime(record.timestamp)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onInspectHistory() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = bill.title,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${bill.category} • 📅 $dateTimeStr",
                        fontSize = 10.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "-${currencyFormatter.format(record.amount)} $currencyUnit",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF10B981)
                    )
                    Text(
                        text = if (isAr) "سجل الأشهر ⬅" else "History ⬅",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanSkyPrimary
                    )
                }
            }

            if (bill.isInstallment) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F2942),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "القسط ${record.installmentIndex} من ${bill.totalInstallmentMonths} (المسلم: ${bill.effectivePaidMonthsCount} شهر)"
                            else "Installment ${record.installmentIndex}/${bill.totalInstallmentMonths} (Paid: ${bill.effectivePaidMonthsCount})",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF34D399)
                        )
                        Text(
                            text = if (isAr) "متبقي ${bill.remainingInstallmentMonths} شهر (${currencyFormatter.format(bill.remainingInstallmentAmount)} $currencyUnit)"
                            else "Left: ${bill.remainingInstallmentMonths} mos (${currencyFormatter.format(bill.remainingInstallmentAmount)} $currencyUnit)",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD166)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthTransactionDetailedCard(
    item: ExpenseItem,
    currencyFormatter: DecimalFormat,
    currencyUnit: String,
    isAr: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateTimeStr = remember(item.date) {
        BillItem.formatTimestampDateTime(item.date)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            0.9.dp,
            if (item.isIncome) Color(0xFF10B981).copy(alpha = 0.35f)
            else Color(0xFFDC2626).copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${if (item.isIncome) "+" else "-"}${currencyFormatter.format(item.amount)} $currencyUnit",
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (item.isIncome) Color(0xFF10B981) else Color(0xFFEF4444)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "📅 $dateTimeStr${if (item.note.isNotBlank()) " • ${item.note}" else ""}",
                    fontSize = 10.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = if (isAr) "تعديل" else "Edit", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onDelete,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.6f)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (isAr) "حذف" else "Delete",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF4444)
                    )
                }
            }
        }
    }
}
