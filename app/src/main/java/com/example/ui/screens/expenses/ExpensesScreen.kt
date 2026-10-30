package com.example.ui.screens.expenses

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BillItem
import com.example.data.local.entity.ExpenseCategory
import com.example.data.local.entity.ExpenseItem
import com.example.data.repository.UserPreferences
import com.example.localization.AppStrings
import com.example.localization.LocalAppStrings
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.EmptyState
import androidx.compose.foundation.verticalScroll
import com.example.ui.theme.CyanSkyPrimary
import com.example.util.CurrencyFormatterUtils
import com.example.util.ThousandsSeparatorVisualTransformation
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class FinanceTab {
    OVERVIEW,
    BILLS,
    HISTORY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    expenses: List<ExpenseItem>,
    bills: List<BillItem> = emptyList(),
    currentLanguage: String,
    preferences: UserPreferences,
    onUpdateBasicSalary: (Double) -> Unit,
    onUpdateAddedSalary: (Double) -> Unit,
    onUpdatePreviousMonthBalance: (Double) -> Unit = {},
    onUpdateSalaryDay: (Int) -> Unit = {},
    onSaveExpense: (ExpenseItem) -> Unit,
    onDeleteExpense: (ExpenseItem) -> Unit,
    onClearAllExpenses: () -> Unit = {},
    onClearAllIncomes: () -> Unit = {},
    onSaveBill: (BillItem) -> Unit = {},
    onDeleteBill: (BillItem) -> Unit = {},
    onPayBill: (BillItem) -> Unit = {},
    onUnpayBill: (BillItem) -> Unit = {}
) {
    val strings = LocalAppStrings.current
    val isAr = currentLanguage == "ar"
    val currencyFormatter = remember { DecimalFormat("#,###") }

    // Screen Tabs: 0: الرئيسية (Overview), 1: الالتزامات (Bills), 2: السجل (History)
    var currentTab by remember { mutableStateOf(FinanceTab.OVERVIEW) }
    var openRedExpensesInBillsTab by remember { mutableStateOf(false) }

    // Dialog States
    var showSetNewSalaryDialog by remember { mutableStateOf(false) }
    var showEditBasicSalaryDialog by remember { mutableStateOf(false) }
    var showEditAddedSalaryDialog by remember { mutableStateOf(false) }
    var showEditPreviousBalanceDialog by remember { mutableStateOf(false) }
    var showEditSalaryDayDialog by remember { mutableStateOf(false) }

    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var showAddIncomeDialog by remember { mutableStateOf(false) }
    var editingExpense by remember { mutableStateOf<ExpenseItem?>(null) }
    var expenseToDelete by remember { mutableStateOf<ExpenseItem?>(null) }
    var showClearAllExpensesConfirm by remember { mutableStateOf(false) }
    var showClearAllIncomesConfirm by remember { mutableStateOf(false) }

    var showAddBillDialog by remember { mutableStateOf(false) }
    var editingBill by remember { mutableStateOf<BillItem?>(null) }
    var billToDelete by remember { mutableStateOf<BillItem?>(null) }

    // Calculations
    val basicSalary = preferences.basicSalary
    val addedSalary = preferences.addedSalary
    val previousBalance = preferences.previousMonthBalance
    val totalIncome = basicSalary + addedSalary + previousBalance

    // Expenses vs Income separation
    val actualExpenses = remember(expenses) { expenses.filter { !it.isIncome } }
    val incomeTransactions = remember(expenses) { expenses.filter { it.isIncome } }

    val (todayTotalExpenses, monthTotalExpenses) = remember(actualExpenses) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfToday = cal.timeInMillis
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val endOfToday = cal.timeInMillis

        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        val startOfMonth = cal.timeInMillis

        val todaySum = actualExpenses.filter { it.date in startOfToday..endOfToday }.sumOf { it.amount }
        val monthSum = actualExpenses.filter { it.date >= startOfMonth }.sumOf { it.amount }
        Pair(todaySum, monthSum)
    }

    // Bills calculations
    val unpaidBills = remember(bills) { bills.filter { !it.isPaid } }
    val reservedBillsTotal = remember(bills) { bills.filter { !it.isPaid && it.isReserved }.sumOf { it.amount } }

    // Actual Current Balance (أموالك في الجيب والحساب)
    val actualCurrentBalance = remember(totalIncome, monthTotalExpenses) {
        totalIncome - monthTotalExpenses
    }

    // Available to spend after reserving unpaid commitments (المتاح للصرف بعد حجز الالتزامات)
    val availableToSpend = remember(actualCurrentBalance, reservedBillsTotal) {
        actualCurrentBalance - reservedBillsTotal
    }

    // Days until next salary
    val daysUntilSalary = remember(preferences.salaryDayOfMonth) {
        val cal = Calendar.getInstance()
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val targetDay = preferences.salaryDayOfMonth.coerceIn(1, maxDaysInMonth)
        if (targetDay >= currentDay) {
            targetDay - currentDay
        } else {
            (maxDaysInMonth - currentDay) + targetDay
        }
    }

    // Daily average spending this month
    val dailyAverageSpending = remember(monthTotalExpenses) {
        val cal = Calendar.getInstance()
        val currentDay = cal.get(Calendar.DAY_OF_MONTH).coerceAtLeast(1)
        monthTotalExpenses / currentDay
    }

    // Date header formatted
    val todayDateFormatted = remember {
        val sdf = SimpleDateFormat("EEEE، d MMMM", if (isAr) Locale("ar") else Locale.ENGLISH)
        sdf.format(Date())
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "ميزانيتي" else "My Budget",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            )
                        }
                        Text(
                            text = if (isAr) "إدارة الراتب والمصاريف بالدينار العراقي" else "Salary & Expenses Manager in IQD",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }

                    // Date Pill
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = todayDateFormatted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Styled Segmented Tab Row matching screenshots
                TabRow(
                    selectedTabIndex = currentTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[currentTab.ordinal]),
                            color = Color(0xFF10B981),
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = currentTab == FinanceTab.OVERVIEW,
                        onClick = { currentTab = FinanceTab.OVERVIEW },
                        text = {
                            Text(
                                text = strings.tabOverview,
                                fontSize = 12.5.sp,
                                fontWeight = if (currentTab == FinanceTab.OVERVIEW) FontWeight.Bold else FontWeight.Normal,
                                color = if (currentTab == FinanceTab.OVERVIEW) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                    Tab(
                        selected = currentTab == FinanceTab.BILLS,
                        onClick = { currentTab = FinanceTab.BILLS },
                        text = {
                            Text(
                                text = strings.tabCommitments,
                                fontSize = 12.5.sp,
                                fontWeight = if (currentTab == FinanceTab.BILLS) FontWeight.Bold else FontWeight.Normal,
                                color = if (currentTab == FinanceTab.BILLS) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                    Tab(
                        selected = currentTab == FinanceTab.HISTORY,
                        onClick = { currentTab = FinanceTab.HISTORY },
                        text = {
                            Text(
                                text = strings.tabHistory,
                                fontSize = 12.5.sp,
                                fontWeight = if (currentTab == FinanceTab.HISTORY) FontWeight.Bold else FontWeight.Normal,
                                color = if (currentTab == FinanceTab.HISTORY) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                FinanceTab.OVERVIEW -> {
                    OverviewTabContent(
                        basicSalary = basicSalary,
                        addedSalary = addedSalary,
                        previousBalance = previousBalance,
                        totalIncome = totalIncome,
                        monthTotalExpenses = monthTotalExpenses,
                        todayTotalExpenses = todayTotalExpenses,
                        actualCurrentBalance = actualCurrentBalance,
                        availableToSpend = availableToSpend,
                        reservedBillsTotal = reservedBillsTotal,
                        daysUntilSalary = daysUntilSalary,
                        dailyAverageSpending = dailyAverageSpending,
                        salaryDayOfMonth = preferences.salaryDayOfMonth,
                        bills = bills,
                        recentTransactions = expenses.take(10),
                        currencyFormatter = currencyFormatter,
                        strings = strings,
                        isAr = isAr,
                        onOpenSetNewSalary = { showSetNewSalaryDialog = true },
                        onOpenEditBasicSalary = { showEditBasicSalaryDialog = true },
                        onOpenEditAddedSalary = { showEditAddedSalaryDialog = true },
                        onOpenEditPreviousBalance = { showEditPreviousBalanceDialog = true },
                        onOpenEditSalaryDay = { showEditSalaryDayDialog = true },
                        onOpenAddExpense = {
                            editingExpense = null
                            showAddExpenseDialog = true
                        },
                        onOpenAddIncome = {
                            editingExpense = null
                            showAddIncomeDialog = true
                        },
                        onNavigateToBills = {
                            openRedExpensesInBillsTab = false
                            currentTab = FinanceTab.BILLS
                        },
                        onOpenRedExpensesWindow = {
                            openRedExpensesInBillsTab = true
                            currentTab = FinanceTab.BILLS
                        },
                        onEditTransaction = { tx ->
                            editingExpense = tx
                            if (tx.isIncome) {
                                showAddIncomeDialog = true
                            } else {
                                showAddExpenseDialog = true
                            }
                        },
                        onDeleteTransaction = { tx ->
                            expenseToDelete = tx
                        },
                        onClearAllExpenses = {
                            showClearAllExpensesConfirm = true
                        },
                        onPayBill = onPayBill
                    )
                }

                FinanceTab.BILLS -> {
                    BillsTabContent(
                        bills = bills,
                        redExpenses = actualExpenses,
                        initialShowRedExpenses = openRedExpensesInBillsTab,
                        currencyFormatter = currencyFormatter,
                        strings = strings,
                        isAr = isAr,
                        onOpenAddBill = {
                            editingBill = null
                            showAddBillDialog = true
                        },
                        onEditBill = { bill ->
                            editingBill = bill
                            showAddBillDialog = true
                        },
                        onDeleteBill = { bill ->
                            billToDelete = bill
                        },
                        onPayBill = onPayBill,
                        onUnpayBill = onUnpayBill,
                        onOpenAddRedExpense = {
                            editingExpense = null
                            showAddExpenseDialog = true
                        },
                        onEditRedExpense = { exp ->
                            editingExpense = exp
                            showAddExpenseDialog = true
                        },
                        onDeleteRedExpense = { exp ->
                            expenseToDelete = exp
                        },
                        onClearAllRedExpenses = {
                            showClearAllExpensesConfirm = true
                        }
                    )
                }

                FinanceTab.HISTORY -> {
                    HistoryTabContent(
                        expenses = actualExpenses,
                        incomes = incomeTransactions,
                        monthTotal = monthTotalExpenses,
                        currencyFormatter = currencyFormatter,
                        strings = strings,
                        isAr = isAr,
                        onOpenAddExpense = {
                            editingExpense = null
                            showAddExpenseDialog = true
                        },
                        onOpenAddIncome = {
                            editingExpense = null
                            showAddIncomeDialog = true
                        },
                        onEditExpense = { exp ->
                            editingExpense = exp
                            if (exp.isIncome) {
                                showAddIncomeDialog = true
                            } else {
                                showAddExpenseDialog = true
                            }
                        },
                        onDeleteExpense = { exp ->
                            expenseToDelete = exp
                        },
                        onClearAllExpenses = {
                            showClearAllExpensesConfirm = true
                        },
                        onClearAllIncomes = {
                            showClearAllIncomesConfirm = true
                        }
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------
    // DIALOGS
    // -------------------------------------------------------------

    // 0. Dedicated Set New Salary Dialog (نافذة تحديد الراتب الجديد وتحديده)
    if (showSetNewSalaryDialog) {
        SetNewSalaryDialog(
            currentBasicSalary = basicSalary,
            currentAddedSalary = addedSalary,
            currentSalaryDay = preferences.salaryDayOfMonth,
            currency = strings.currencyUnit,
            strings = strings,
            isAr = isAr,
            onDismiss = { showSetNewSalaryDialog = false },
            onConfirm = { newBasic, newAdded, newDay ->
                onUpdateBasicSalary(newBasic)
                onUpdateAddedSalary(newAdded)
                onUpdateSalaryDay(newDay)
                showSetNewSalaryDialog = false
            }
        )
    }

    // 1. Basic Salary Dialog
    if (showEditBasicSalaryDialog) {
        SalaryAmountInputDialog(
            title = strings.basicSalaryTitle,
            subtitle = strings.basicSalaryDesc,
            currentAmount = basicSalary,
            currency = strings.currencyUnit,
            isAr = isAr,
            saveLabel = strings.saveSalary,
            cancelLabel = strings.btnCancel,
            quickAmounts = listOf(250000L, 500000L, 750000L, 1000000L, 1500000L, 2000000L),
            onDismiss = { showEditBasicSalaryDialog = false },
            onSave = { newAmount ->
                onUpdateBasicSalary(newAmount)
                showEditBasicSalaryDialog = false
            }
        )
    }

    // 2. Added Salary Dialog
    if (showEditAddedSalaryDialog) {
        SalaryAmountInputDialog(
            title = strings.addedSalaryTitle,
            subtitle = strings.addedSalaryDesc,
            currentAmount = addedSalary,
            currency = strings.currencyUnit,
            isAr = isAr,
            saveLabel = strings.saveSalary,
            cancelLabel = strings.btnCancel,
            quickAmounts = listOf(50000L, 100000L, 150000L, 250000L, 500000L),
            onDismiss = { showEditAddedSalaryDialog = false },
            onSave = { newAmount ->
                onUpdateAddedSalary(newAmount)
                showEditAddedSalaryDialog = false
            }
        )
    }

    // 3. Previous Month Balance Dialog (رصيد مرحّل)
    if (showEditPreviousBalanceDialog) {
        SalaryAmountInputDialog(
            title = strings.previousMonthBalanceTitle,
            subtitle = strings.manualEntryCarryover,
            currentAmount = previousBalance,
            currency = strings.currencyUnit,
            isAr = isAr,
            saveLabel = strings.saveSalary,
            cancelLabel = strings.btnCancel,
            quickAmounts = listOf(100000L, 250000L, 500000L, 1000000L),
            onDismiss = { showEditPreviousBalanceDialog = false },
            onSave = { newAmount ->
                onUpdatePreviousMonthBalance(newAmount)
                showEditPreviousBalanceDialog = false
            }
        )
    }

    // 4. Salary Reception Day of Month Dialog
    if (showEditSalaryDayDialog) {
        SalaryDayInputDialog(
            currentDay = preferences.salaryDayOfMonth,
            isAr = isAr,
            title = strings.setSalaryReceptionDay,
            onDismiss = { showEditSalaryDayDialog = false },
            onSave = { day ->
                onUpdateSalaryDay(day)
                showEditSalaryDayDialog = false
            }
        )
    }

    // 5. Add / Edit Expense Dialog
    if (showAddExpenseDialog) {
        ExpenseOrIncomeInputDialog(
            isIncome = false,
            initialItem = editingExpense,
            currency = strings.currencyUnit,
            strings = strings,
            isAr = isAr,
            onDismiss = {
                showAddExpenseDialog = false
                editingExpense = null
            },
            onSave = { item ->
                onSaveExpense(item)
                showAddExpenseDialog = false
                editingExpense = null
            }
        )
    }

    // 6. Add / Edit Income / Bonus Dialog
    if (showAddIncomeDialog) {
        ExpenseOrIncomeInputDialog(
            isIncome = true,
            initialItem = editingExpense,
            currency = strings.currencyUnit,
            strings = strings,
            isAr = isAr,
            onDismiss = {
                showAddIncomeDialog = false
                editingExpense = null
            },
            onSave = { item ->
                onSaveExpense(item)
                showAddIncomeDialog = false
                editingExpense = null
            }
        )
    }

    // 7. Add / Edit Bill Dialog
    if (showAddBillDialog) {
        BillInputDialog(
            initialBill = editingBill,
            currency = strings.currencyUnit,
            strings = strings,
            isAr = isAr,
            onDismiss = {
                showAddBillDialog = false
                editingBill = null
            },
            onSave = { bill ->
                onSaveBill(bill)
                showAddBillDialog = false
                editingBill = null
            }
        )
    }

    // Delete Confirm Dialog for Single Expense
    if (expenseToDelete != null) {
        ConfirmDeleteDialog(
            title = strings.deleteConfirmTitle,
            message = strings.deleteExpenseConfirmMessage,
            onConfirm = {
                expenseToDelete?.let { onDeleteExpense(it) }
                expenseToDelete = null
            },
            onDismiss = { expenseToDelete = null }
        )
    }

    // Delete Confirm Dialog for Clearing Entire Expense Log (حذف سجل المصروف بالكامل)
    if (showClearAllExpensesConfirm) {
        ConfirmDeleteDialog(
            title = if (isAr) "تأكيد حذف سجل المصروفات" else "Clear Expense Log",
            message = if (isAr) "هل أنت متأكد من حذف جميع المصروفات المسجلة بالكامل؟" else "Are you sure you want to delete all recorded expenses?",
            onConfirm = {
                onClearAllExpenses()
                showClearAllExpensesConfirm = false
            },
            onDismiss = { showClearAllExpensesConfirm = false }
        )
    }

    // Delete Confirm Dialog for Clearing Added Incomes Log
    if (showClearAllIncomesConfirm) {
        ConfirmDeleteDialog(
            title = if (isAr) "تأكيد حذف سجل المبالغ المضافة" else "Clear Income Log",
            message = if (isAr) "هل أنت متأكد من حذف جميع المبالغ المضافة المسجلة؟" else "Are you sure you want to delete all recorded income entries?",
            onConfirm = {
                onClearAllIncomes()
                showClearAllIncomesConfirm = false
            },
            onDismiss = { showClearAllIncomesConfirm = false }
        )
    }

    // Delete Confirm Dialog for Bill
    if (billToDelete != null) {
        ConfirmDeleteDialog(
            title = strings.deleteConfirmTitle,
            message = if (isAr) "هل أنت متأكد من حذف هذا الالتزام / الفاتورة؟" else "Are you sure you want to delete this bill?",
            onConfirm = {
                billToDelete?.let { onDeleteBill(it) }
                billToDelete = null
            },
            onDismiss = { billToDelete = null }
        )
    }
}

// =================================================================
// TAB 1: OVERVIEW & SALARY DASHBOARD (Screenshots 2 & 6)
// =================================================================
@Composable
private fun OverviewTabContent(
    basicSalary: Double,
    addedSalary: Double,
    previousBalance: Double,
    totalIncome: Double,
    monthTotalExpenses: Double,
    todayTotalExpenses: Double,
    actualCurrentBalance: Double,
    availableToSpend: Double,
    reservedBillsTotal: Double,
    daysUntilSalary: Int,
    dailyAverageSpending: Double,
    salaryDayOfMonth: Int,
    bills: List<BillItem>,
    recentTransactions: List<ExpenseItem>,
    currencyFormatter: DecimalFormat,
    strings: AppStrings,
    isAr: Boolean,
    onOpenSetNewSalary: () -> Unit,
    onOpenEditBasicSalary: () -> Unit,
    onOpenEditAddedSalary: () -> Unit,
    onOpenEditPreviousBalance: () -> Unit,
    onOpenEditSalaryDay: () -> Unit,
    onOpenAddExpense: () -> Unit,
    onOpenAddIncome: () -> Unit,
    onNavigateToBills: () -> Unit,
    onOpenRedExpensesWindow: () -> Unit,
    onPayBill: (BillItem) -> Unit,
    onEditTransaction: (ExpenseItem) -> Unit,
    onDeleteTransaction: (ExpenseItem) -> Unit,
    onClearAllExpenses: () -> Unit
) {
    val unpaidBills = remember(bills) { bills.filter { !it.isPaid } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // 0. بطاقة الراتب الفعلي المخصص مع زر تحديد الراتب الجديد (USER REQUIREMENT)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.8.dp, CyanSkyPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header Row: Title on TOP and "تحديد الراتب الجديد" Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isAr) "الراتب الفعلي (إجمالي الراتب)" else "Actual Net Salary",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanSkyPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isAr) "الأساسي + المضاف لشهرك الحالي" else "Base + Added for current month",
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // زر تحديد الراتب الجديد
                        Button(
                            onClick = onOpenSetNewSalary,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyanSkyPrimary,
                                contentColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "تحديد الراتب الجديد" else "Set New Salary",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Price / Amount Value BELOW IT
                    Text(
                        text = "${currencyFormatter.format(basicSalary + addedSalary)} ${strings.currencyUnit}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Breakdown row: الراتب الأساسي | الراتب المضاف | يوم الاستلام
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isAr) "الراتب الأساسي:" else "Base Salary:",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${currencyFormatter.format(basicSalary)} ${strings.currencyUnit}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF60A5FA)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(26.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant)
                            )

                            Column {
                                Text(
                                    text = if (isAr) "الراتب المضاف:" else "Added Salary:",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${currencyFormatter.format(addedSalary)} ${strings.currencyUnit}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(26.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant)
                            )

                            Column {
                                Text(
                                    text = if (isAr) "يوم النزول:" else "Deposit Day:",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "$salaryDayOfMonth ${if (isAr) "من الشهر" else "of month"}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF59E0B)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 1. HERO CARD: الرصيد الفعلي الحالي (أموالك في الجيب والحساب)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1B4332) // Forest Emerald Green
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Title on TOP
                    Text(
                        text = strings.actualCurrentBalance,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFD8F3DC)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Price / Amount Value BELOW IT
                    Text(
                        text = "${currencyFormatter.format(actualCurrentBalance)} ${strings.currencyUnit}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sub-boxes: المتاح للصرف (يمين) | التزامات محجوزة (يسار)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Box A: المتاح للصرف
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF2D6A4F).copy(alpha = 0.85f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = strings.availableToSpend,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD8F3DC)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "${currencyFormatter.format(availableToSpend)} ${strings.currencyUnit}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = strings.afterReservedCommitments,
                                    fontSize = 9.5.sp,
                                    color = Color(0xFFB7E4C7)
                                )
                            }
                        }

                        // Box B: التزامات محجوزة
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF2D6A4F).copy(alpha = 0.85f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = strings.reservedCommitments,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD8F3DC)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "${currencyFormatter.format(reservedBillsTotal)} ${strings.currencyUnit}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (reservedBillsTotal > 0) Color(0xFFFFD166) else Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = strings.unpaidDues,
                                    fontSize = 9.5.sp,
                                    color = Color(0xFFB7E4C7)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. متبقي للراتب القادم & متوسط الصرف اليومي (Screenshot 2)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Right side: متبقي للراتب القادم
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenEditSalaryDay() }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = strings.daysRemainingForSalary,
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = String.format(Locale.getDefault(), "%d %s", daysUntilSalary, if (isAr) "يوم" else "days"),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF10B981)
                        )
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    // Left side: متوسط الصرف اليومي
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    ) {
                        Text(
                            text = strings.dailyAverageExpense,
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "${currencyFormatter.format(dailyAverageSpending.toLong())} ${strings.currencyUnit}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // 3. إجراءات سريعة (Quick Actions: - مصروف | + إضافة مبلغ جديد) (Screenshot 2)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = strings.quickActions,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // زر أحمر: - مصروف
                    Button(
                        onClick = onOpenAddExpense,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.btnLogExpense,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // زر أخضر: + إضافة مبلغ جديد
                    Button(
                        onClick = onOpenAddIncome,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.btnAddIncome,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // زر الوصول السريع لنافذة المصروفات (الزر الأحمر) داخل الالتزامات للتعديل والحذف
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFDC2626).copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Color(0xFFDC2626).copy(alpha = 0.45f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onOpenRedExpensesWindow() }
                        .testTag("open_red_expenses_in_commitments_btn")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDC2626)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isAr) "نافذة المصروف في الالتزامات" else "Expenses Window in Commitments",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEF4444)
                                )
                                Text(
                                    text = if (isAr) "اضغط هنا لتعديل أو حذف أي مصروف أو مسح سجل المصروف" else "Tap to edit, delete, or clear expense log",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // 4. بطاقات تحديد الراتب الأساسي والراتب المضاف (USER REQUIREMENT)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card A: بطاقة الراتب الأساسي (Title on TOP, Price BELOW IT!)
                SalarySpecCard(
                    title = strings.basicSalaryTitle,
                    subtitle = strings.basicSalaryDesc,
                    amount = basicSalary,
                    currency = strings.currencyUnit,
                    icon = Icons.Default.AccountBalance,
                    themeColor = Color(0xFF3B82F6),
                    currencyFormatter = currencyFormatter,
                    isAr = isAr,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenEditBasicSalary
                )

                // Card B: بطاقة الراتب المضاف (Title on TOP, Price BELOW IT!)
                SalarySpecCard(
                    title = strings.addedSalaryTitle,
                    subtitle = strings.addedSalaryDesc,
                    amount = addedSalary,
                    currency = strings.currencyUnit,
                    icon = Icons.Default.TrendingUp,
                    themeColor = Color(0xFF10B981),
                    currencyFormatter = currencyFormatter,
                    isAr = isAr,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenEditAddedSalary
                )
            }
        }

        // 5. بطاقة المتبقي من الشهر السابق (رصيد مرحّل) (Screenshot 6)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
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
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Savings,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            // Title on TOP
                            Text(
                                text = strings.previousMonthBalanceTitle,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Edit Button
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.clickable { onOpenEditPreviousBalance() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isAr) "إدخال يدوي / تعديل" else "Manual Edit",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Price / Amount Value BELOW IT!
                    Text(
                        text = "${currencyFormatter.format(previousBalance)} ${strings.currencyUnit}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF10B981)
                    )

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = strings.manualEntryCarryover,
                        fontSize = 10.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 6. فواتير والتزامات قادمة (Upcoming Commitments Banner) (Screenshot 2)
        if (unpaidBills.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = strings.commitmentsAndBillsTitle,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFEF4444).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "${unpaidBills.size} ${if (isAr) "عاجل" else "urgent"}",
                                        color = Color(0xFFEF4444),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            TextButton(
                                onClick = onNavigateToBills,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                            ) {
                                Text(
                                    text = if (isAr) "عرض الكل" else "View All",
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Render up to 2 upcoming bills
                        unpaidBills.take(2).forEach { bill ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = bill.title,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${currencyFormatter.format(bill.amount)} ${strings.currencyUnit} • ${bill.category}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Button(
                                        onClick = { onPayBill(bill) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = strings.btnPayBill, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. تفصيل الحساب المالي للدورة الحالية (Screenshot 6)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Text(
                        text = strings.financialAccountingDetails,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = strings.financialAccountingSubtitle,
                        fontSize = 10.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Row: Total Income
                    FinancialDetailRow(
                        label = strings.totalIncomeTitle,
                        value = "+${currencyFormatter.format(totalIncome)} ${strings.currencyUnit}",
                        color = Color(0xFF10B981)
                    )

                    // Row: Month Expenses Total
                    FinancialDetailRow(
                        label = strings.monthTotalExpenses,
                        value = "-${currencyFormatter.format(monthTotalExpenses)} ${strings.currencyUnit}",
                        color = Color(0xFFEF4444)
                    )

                    // Row: Today's Expenses
                    FinancialDetailRow(
                        label = strings.todayTotalExpenses,
                        value = "${currencyFormatter.format(todayTotalExpenses)} ${strings.currencyUnit}",
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Progress indicator for income consumed
                    if (totalIncome > 0) {
                        val ratio = (monthTotalExpenses / totalIncome).toFloat().coerceIn(0f, 1f)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${strings.salarySpentRatio}: ${(ratio * 100).toInt()}%",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (ratio <= 0.8f) strings.salarySafeStatus else strings.salaryWarningStatus,
                                fontSize = 10.sp,
                                color = if (ratio <= 0.8f) Color(0xFF10B981) else Color(0xFFEF4444)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { ratio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = when {
                                ratio > 0.85f -> Color(0xFFEF4444)
                                ratio > 0.70f -> Color(0xFFF59E0B)
                                else -> Color(0xFF10B981)
                            },
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }

        // 8. آخر المعاملات المسجلة (مع أزرار تعديل وحذف لكل مصروف وحذف السجل)
        if (recentTransactions.isNotEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.latestTransactions,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFDC2626).copy(alpha = 0.14f),
                                border = BorderStroke(0.8.dp, Color(0xFFDC2626).copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onClearAllExpenses() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteSweep,
                                        contentDescription = null,
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isAr) "حذف سجل المصروف" else "Clear Expenses",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFEF4444)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    recentTransactions.forEach { tx ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                0.8.dp,
                                if (tx.isIncome) Color(0xFF10B981).copy(alpha = 0.25f)
                                else Color(0xFFDC2626).copy(alpha = 0.25f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tx.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${if (tx.isIncome) "+" else "-"}${currencyFormatter.format(tx.amount)} ${strings.currencyUnit} • " +
                                            if (tx.isIncome) (if (isAr) "دخل إضافي / راتب" else "Income / Salary")
                                            else (if (isAr) "مصروف" else "Expense"),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (tx.isIncome) Color(0xFF10B981) else Color(0xFFEF4444)
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onEditTransaction(tx) }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = if (isAr) "تعديل" else "Edit",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFDC2626).copy(alpha = 0.14f),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onDeleteTransaction(tx) }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically
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
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFEF4444)
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
}

// =================================================================
// TAB 2: BILLS & COMMITMENTS + RED-BUTTON EXPENSES WINDOW (الالتزامات + نافذة المصروف الأحمر)
// =================================================================
@Composable
private fun BillsTabContent(
    bills: List<BillItem>,
    redExpenses: List<ExpenseItem>,
    initialShowRedExpenses: Boolean = false,
    currencyFormatter: DecimalFormat,
    strings: AppStrings,
    isAr: Boolean,
    onOpenAddBill: () -> Unit,
    onEditBill: (BillItem) -> Unit,
    onDeleteBill: (BillItem) -> Unit,
    onPayBill: (BillItem) -> Unit,
    onUnpayBill: (BillItem) -> Unit = {},
    onOpenAddRedExpense: () -> Unit,
    onEditRedExpense: (ExpenseItem) -> Unit,
    onDeleteRedExpense: (ExpenseItem) -> Unit,
    onClearAllRedExpenses: () -> Unit
) {
    // 0 = الفواتير والالتزامات (مع بطاقة نافذة المصروف الأحمر المدمجة), 1 = نافذة المصروفات (الزر الأحمر) المستقلة
    var activeCommitmentsSection by remember(initialShowRedExpenses) {
        mutableIntStateOf(if (initialShowRedExpenses) 1 else 0)
    }
    var filterUnpaidOnly by remember { mutableStateOf(false) }
    var sortByClosestDue by remember { mutableStateOf(true) }
    var redExpenseSearchQuery by remember { mutableStateOf("") }

    val totalBillsAmount = remember(bills) { bills.sumOf { it.amount } }
    val unpaidBillsList = remember(bills) { bills.filter { !it.isPaid } }
    val paidBillsList = remember(bills) { bills.filter { it.isPaid } }
    val unpaidBillsAmount = remember(unpaidBillsList) { unpaidBillsList.sumOf { it.amount } }
    val remindersCount = remember(bills) { bills.count { it.hasReminder && !it.isPaid } }

    val totalRedExpensesAmount = remember(redExpenses) { redExpenses.sumOf { it.amount } }
    val filteredRedExpenses = remember(redExpenses, redExpenseSearchQuery) {
        if (redExpenseSearchQuery.isBlank()) redExpenses
        else redExpenses.filter {
            it.title.contains(redExpenseSearchQuery, ignoreCase = true) ||
                it.note.contains(redExpenseSearchQuery, ignoreCase = true)
        }
    }

    val filteredBills = remember(bills, filterUnpaidOnly, sortByClosestDue) {
        var list = if (filterUnpaidOnly) bills.filter { !it.isPaid } else bills
        list = if (sortByClosestDue) {
            val cal = Calendar.getInstance()
            val currentDay = cal.get(Calendar.DAY_OF_MONTH)
            list.sortedBy {
                if (it.dueDayOfMonth >= currentDay) it.dueDayOfMonth - currentDay
                else it.dueDayOfMonth + 30 - currentDay
            }
        } else {
            list.sortedByDescending { it.amount }
        }
        list
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 0. شريط التبديل داخل الالتزامات: (الالتزامات الثابتة | نافذة المصروف - الزر الأحمر)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(
                    1.2.dp,
                    if (activeCommitmentsSection == 1) Color(0xFFDC2626).copy(alpha = 0.65f)
                    else MaterialTheme.colorScheme.outlineVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // القسم الأول: الالتزامات والفواتير
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { activeCommitmentsSection = 0 }
                            .testTag("commitments_subtab_bills"),
                        shape = RoundedCornerShape(12.dp),
                        color = if (activeCommitmentsSection == 0) Color(0xFF10B981) else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                tint = if (activeCommitmentsSection == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (isAr) "الالتزامات (${bills.size})" else "Bills (${bills.size})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeCommitmentsSection == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // القسم الثاني: نافذة المصروفات الخاصة بالزر الأحمر (- مصروف)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { activeCommitmentsSection = 1 }
                            .testTag("commitments_subtab_red_expenses"),
                        shape = RoundedCornerShape(12.dp),
                        color = if (activeCommitmentsSection == 1) Color(0xFFDC2626)
                        else Color(0xFFDC2626).copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.RemoveCircle,
                                contentDescription = null,
                                tint = if (activeCommitmentsSection == 1) Color.White else Color(0xFFEF4444),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (isAr) "نافذة المصروف (${redExpenses.size})" else "Expenses (${redExpenses.size})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeCommitmentsSection == 1) Color.White else Color(0xFFEF4444)
                            )
                        }
                    }
                }
            }
        }

        if (activeCommitmentsSection == 1) {
            // =========================================================================
            // نافذة المصروفات الخاصة بالزر الأحمر (- مصروف) للتعديل والحذف ومسح السجل
            // =========================================================================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF3B1219)),
                    border = BorderStroke(1.5.dp, Color(0xFFDC2626)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFDC2626)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isAr) "نافذة المصروفات" else "Expenses Window",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = if (isAr) "إدارة وتعديل وحذف المصروفات المسجلة" else "Manage, edit, and delete recorded expenses",
                                        fontSize = 11.sp,
                                        color = Color(0xFFFCA5A5)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = if (isAr) "${redExpenses.size} عملية" else "${redExpenses.size} items",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFECACA),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isAr) "إجمالي المصروفات المسجلة:" else "Total Recorded Expenses:",
                            fontSize = 11.5.sp,
                            color = Color(0xFFFCA5A5)
                        )
                        Text(
                            text = "${currencyFormatter.format(totalRedExpensesAmount)} ${strings.currencyUnit}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        if (totalRedExpensesAmount > 0) {
                            Text(
                                text = CurrencyFormatterUtils.toArabicDenomination(totalRedExpensesAmount.toLong()),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFCA5A5)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onOpenAddRedExpense,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isAr) "إضافة مصروف جديد" else "Add Expense",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            if (redExpenses.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = onClearAllRedExpenses,
                                    modifier = Modifier.height(42.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color(0xFFF87171)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFCA5A5))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteSweep,
                                        contentDescription = null,
                                        tint = Color(0xFFF87171),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isAr) "حذف السجل بالكامل" else "Clear All",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // شريط البحث في المصروفات
            item {
                OutlinedTextField(
                    value = redExpenseSearchQuery,
                    onValueChange = { redExpenseSearchQuery = it },
                    placeholder = {
                        Text(
                            if (isAr) "ابحث في سجل المصروفات لتعديله أو حذفه..." else "Search expenses to edit or delete...",
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = if (redExpenseSearchQuery.isNotEmpty()) {
                        {
                            IconButton(onClick = { redExpenseSearchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFDC2626),
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            }

            if (filteredRedExpenses.isEmpty()) {
                item {
                    EmptyState(
                        title = if (isAr) "لا توجد مصروفات مسجلة حالياً" else "No recorded expenses",
                        subtitle = if (isAr) "أي مصروف تضيفه في الرئيسية يظهر هنا لتتمكن من تعديله أو حذفه في أي وقت" else "Recorded expenses appear here for editing or deletion",
                        icon = Icons.Default.ReceiptLong
                    )
                }
            } else {
                items(filteredRedExpenses, key = { "red_exp_${it.id}" }) { expItem ->
                    TransactionCardItem(
                        item = expItem,
                        currencyFormatter = currencyFormatter,
                        currency = strings.currencyUnit,
                        strings = strings,
                        isAr = isAr,
                        onEdit = { onEditRedExpense(expItem) },
                        onDelete = { onDeleteRedExpense(expItem) }
                    )
                }
            }
        } else {
            // =========================================================================
            // نافذة الالتزامات والفواتير + بطاقة نافذة المصروف الأحمر السريعة في الأعلى
            // =========================================================================
            // 1. TOP SUMMARY CARDS: بطاقة المجموع الكلي وبطاقة الغير مسدد (USER REQUIREMENT)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Card A: بطاقة المجموع الكلي للالتزامات
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF0F2942)
                        ),
                        border = BorderStroke(1.2.dp, CyanSkyPrimary.copy(alpha = 0.5f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isAr) "المجموع الكلي" else "Total Commitments",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanSkyPrimary
                                )
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CyanSkyPrimary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = CyanSkyPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "${currencyFormatter.format(totalBillsAmount)} ${strings.currencyUnit}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (totalBillsAmount > 0) {
                                Text(
                                    text = CurrencyFormatterUtils.toArabicDenomination(totalBillsAmount.toLong()),
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF93C5FD),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.White.copy(alpha = 0.08f)
                            ) {
                                Text(
                                    text = if (isAr) "إجمالي ${bills.size} التزام (${paidBillsList.size} مسددة)"
                                    else "${bills.size} total (${paidBillsList.size} paid)",
                                    fontSize = 10.sp,
                                    color = Color(0xFFCBD5E1),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Card B: بطاقة الغير مسدد
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF3B1219)
                        ),
                        border = BorderStroke(1.2.dp, Color(0xFFF87171).copy(alpha = 0.55f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isAr) "الغير مسدد" else "Unpaid Total",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFCA5A5)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFEF4444).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = Color(0xFFF87171),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "${currencyFormatter.format(unpaidBillsAmount)} ${strings.currencyUnit}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (unpaidBillsAmount > 0) {
                                Text(
                                    text = CurrencyFormatterUtils.toArabicDenomination(unpaidBillsAmount.toLong()),
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFCA5A5),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.White.copy(alpha = 0.08f)
                            ) {
                                Text(
                                    text = if (isAr) "${unpaidBillsList.size} التزام غير مسدد"
                                    else "${unpaidBillsList.size} unpaid",
                                    fontSize = 10.sp,
                                    color = Color(0xFFFECACA),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 1.5 بطاقة نافذة المصروفات الخاصة بالزر الأحمر (- مصروف) مدمجة داخل الالتزامات
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFDC2626).copy(alpha = 0.1f)
                    ),
                    border = BorderStroke(1.2.dp, Color(0xFFDC2626).copy(alpha = 0.55f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFDC2626)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isAr) "نافذة المصروف" else "Expenses Window",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFEF4444),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = if (isAr) "الإجمالي: ${currencyFormatter.format(totalRedExpensesAmount)} ${strings.currencyUnit} (${redExpenses.size} مصروف)"
                                        else "Total: ${currencyFormatter.format(totalRedExpensesAmount)} ${strings.currencyUnit} (${redExpenses.size})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = { activeCommitmentsSection = 1 },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text(
                                    text = if (isAr) "إدارة وتعديل وحذف" else "Manage & Edit",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        if (redExpenses.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            redExpenses.take(3).forEach { exp ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surface
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 7.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = exp.title,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "-${currencyFormatter.format(exp.amount)} ${strings.currencyUnit}",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFFEF4444)
                                            )
                                        }
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            IconButton(
                                                onClick = { onEditRedExpense(exp) },
                                                modifier = Modifier.size(30.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "Edit",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = { onDeleteRedExpense(exp) },
                                                modifier = Modifier.size(30.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete",
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
                }
            }

            // 2. Filter, Sort & Notification Reminder Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FilterList,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = strings.filterAndSortBills,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Toggle Sort Button
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                modifier = Modifier.clickable { sortByClosestDue = !sortByClosestDue }
                            ) {
                                Text(
                                    text = if (sortByClosestDue) strings.sortClosestDue else strings.sortHighestAmount,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = !filterUnpaidOnly,
                                    onClick = { filterUnpaidOnly = false },
                                    label = { Text(strings.showAllBills, fontSize = 11.sp) }
                                )
                                FilterChip(
                                    selected = filterUnpaidOnly,
                                    onClick = { filterUnpaidOnly = true },
                                    label = { Text(strings.showUnpaidBills, fontSize = 11.sp) }
                                )
                            }

                            // Active Reminders Indicator Badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (remindersCount > 0 || unpaidBillsList.isNotEmpty()) Color(0xFFF59E0B).copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isAr) "إشعارات تلقائية قبل الموعد ✓" else "Auto Due Alerts ✓",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF59E0B)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Add Bill Button
            item {
                Button(
                    onClick = onOpenAddBill,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = strings.btnAddBill, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            // List of Bills
            if (filteredBills.isEmpty()) {
                item {
                    EmptyState(
                        title = if (isAr) "لا توجد فواتير أو التزامات" else "No bills or commitments",
                        subtitle = if (isAr) "أضف فواتيرك الثابتة لتتبع ميزانيتك بدقة" else "Add recurring bills to manage budget",
                        icon = Icons.Default.Receipt
                    )
                }
            } else {
                items(filteredBills, key = { it.id }) { bill ->
                    BillCardItem(
                        bill = bill,
                        currencyFormatter = currencyFormatter,
                        strings = strings,
                        isAr = isAr,
                        onEdit = { onEditBill(bill) },
                        onDelete = { onDeleteBill(bill) },
                        onPay = { onPayBill(bill) },
                        onUnpay = { onUnpayBill(bill) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BillCardItem(
    bill: BillItem,
    currencyFormatter: DecimalFormat,
    strings: AppStrings,
    isAr: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onPay: () -> Unit,
    onUnpay: () -> Unit = {}
) {
    val cal = Calendar.getInstance()
    val currentDay = cal.get(Calendar.DAY_OF_MONTH)
    val daysLeft = if (bill.dueDayOfMonth >= currentDay) bill.dueDayOfMonth - currentDay
    else bill.dueDayOfMonth + 30 - currentDay

    val reminderFormatted = remember(bill.reminderTime) {
        bill.reminderTime?.let {
            SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale.getDefault()).format(Date(it))
        }
    }

    val (catIcon, catColor) = remember(bill.category, bill.title) {
        when {
            bill.category.contains("سيارة") || bill.category.contains("نقل") || bill.category.contains("سفر") ->
                Icons.Default.DirectionsCar to Color(0xFF38BDF8)
            bill.category.contains("بيت") || bill.category.contains("سكن") || bill.category.contains("إيجار") || bill.category.contains("الأهل") || bill.category.contains("الوالدين") ->
                Icons.Default.Home to Color(0xFFA855F7)
            bill.category.contains("علاج") || bill.category.contains("أدوية") || bill.category.contains("صحة") ->
                Icons.Default.LocalPharmacy to Color(0xFFEF4444)
            bill.category.contains("تسوق") || bill.category.contains("أسواق") ->
                Icons.Default.ShoppingCart to Color(0xFFF43F5E)
            bill.category.contains("مطاعم") || bill.category.contains("طعام") ->
                Icons.Default.LocalDining to Color(0xFFF97316)
            bill.category.contains("هدية") || bill.category.contains("تبرع") ->
                Icons.Default.Payments to Color(0xFF10B981)
            bill.category.contains("دراسة") || bill.title.contains("دراسة") ->
                Icons.Default.School to Color(0xFF06B6D4)
            bill.category.contains("إنترنت") || bill.title.contains("إنترنت") || bill.title.contains("واي") ->
                Icons.Default.Wifi to Color(0xFF10B981)
            bill.category.contains("كهرباء") || bill.category.contains("مولد") || bill.title.contains("كهرباء") || bill.title.contains("مولد") ->
                Icons.Default.FlashOn to Color(0xFFF59E0B)
            else -> Icons.Default.Receipt to Color(0xFF6366F1)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Icon, Title & Category, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(catColor.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = catIcon,
                            contentDescription = null,
                            tint = catColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = bill.title,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = bill.category,
                            fontSize = 11.sp,
                            color = catColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Delete Icon
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Badges row: تاريخ الاستحقاق | حالة السداد | التذكير
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    val dueText = if (bill.dueMonth in 1..12) {
                        if (isAr) "استحقاق: ${bill.dueDayOfMonth} / شهر ${bill.dueMonth}"
                        else "Due: ${bill.dueDayOfMonth}/${bill.dueMonth}"
                    } else {
                        if (isAr) "شهرياً (يوم ${bill.dueDayOfMonth})"
                        else "Monthly (Day ${bill.dueDayOfMonth})"
                    }
                    Text(
                        text = dueText,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (bill.isPaid) Color(0xFF10B981).copy(alpha = 0.15f)
                    else Color(0xFFEF4444).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (bill.isPaid) (if (isAr) "مسددة ✓" else "Paid ✓")
                        else if (daysLeft == 0) (if (isAr) "غير مسددة (مستحقة اليوم!)" else "Unpaid (Due today!)")
                        else String.format(Locale.getDefault(), if (isAr) "غير مسددة (متبقي %d يوم)" else "Unpaid (%d days left)", daysLeft),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (bill.isPaid) Color(0xFF10B981) else Color(0xFFF87171),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (bill.hasReminder && reminderFormatted != null) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF59E0B).copy(alpha = 0.18f),
                        modifier = Modifier.clickable { onEdit() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = reminderFormatted,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF59E0B)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Amount Section (Title on TOP, Value BELOW IT)
            Text(
                text = if (isAr) "المبلغ المطلوب:" else "Required Amount:",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${currencyFormatter.format(bill.amount)} ${strings.currencyUnit}",
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row: حالة الحجز | زر التذكير | تعديل | تسديد / إلغاء التسديد
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (bill.isReserved) strings.reservedInBudget else strings.notReserved,
                    fontSize = 10.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Quick Reminder / Notification Button
                    OutlinedButton(
                        onClick = onEdit,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp),
                        border = BorderStroke(
                            1.dp,
                            if (bill.hasReminder) Color(0xFFF59E0B) else MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Icon(
                            imageVector = if (bill.hasReminder) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                            contentDescription = null,
                            tint = if (bill.hasReminder) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isAr) "تذكير" else "Alert",
                            fontSize = 11.sp,
                            color = if (bill.hasReminder) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    OutlinedButton(
                        onClick = onEdit,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = strings.btnEditBill, fontSize = 11.sp)
                    }

                    if (!bill.isPaid) {
                        Button(
                            onClick = onPay,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = strings.btnPayBill, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(
                            onClick = onUnpay,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp),
                            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = if (isAr) "إلغاء التسديد" else "Mark Unpaid",
                                fontSize = 10.5.sp,
                                color = Color(0xFFF87171),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// =================================================================
// TAB 3: HISTORY & TRANSACTIONS (Screenshot 4)
// =================================================================
@Composable
private fun HistoryTabContent(
    expenses: List<ExpenseItem>,
    incomes: List<ExpenseItem>,
    monthTotal: Double,
    currencyFormatter: DecimalFormat,
    strings: AppStrings,
    isAr: Boolean,
    onOpenAddExpense: () -> Unit,
    onOpenAddIncome: () -> Unit,
    onEditExpense: (ExpenseItem) -> Unit,
    onDeleteExpense: (ExpenseItem) -> Unit,
    onClearAllExpenses: () -> Unit,
    onClearAllIncomes: () -> Unit
) {
    var historySubTab by remember { mutableIntStateOf(0) } // 0: المصروفات اليومية, 1: المبالغ المضافة
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<ExpenseCategory?>(null) }

    val currentList = if (historySubTab == 0) expenses else incomes

    val filteredList = remember(currentList, searchQuery, selectedCategoryFilter) {
        currentList.filter { item ->
            val matchesQuery = searchQuery.isBlank() ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.note.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategoryFilter == null || item.category == selectedCategoryFilter
            matchesQuery && matchesCategory
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Sub-tabs switcher (Screenshot 4: المصروفات اليومية | المبالغ المضافة)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { historySubTab = 0 },
                        shape = RoundedCornerShape(10.dp),
                        color = if (historySubTab == 0) Color(0xFFDC2626).copy(alpha = 0.18f) else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = if (historySubTab == 0) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.tabDailyExpenses,
                                fontSize = 12.5.sp,
                                fontWeight = if (historySubTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (historySubTab == 0) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { historySubTab = 1 },
                        shape = RoundedCornerShape(10.dp),
                        color = if (historySubTab == 1) Color(0xFF10B981).copy(alpha = 0.2f) else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = if (historySubTab == 1) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.tabAddedAmounts,
                                fontSize = 12.5.sp,
                                fontWeight = if (historySubTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (historySubTab == 1) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Summary Card with Clear Log Action
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // Title on TOP
                        Text(
                            text = if (historySubTab == 0) (if (isAr) "إجمالي المصروفات المسجلة (${expenses.size})" else "Total Recorded Expenses (${expenses.size})")
                            else (if (isAr) "إجمالي المبالغ المضافة المسجلة (${incomes.size})" else "Total Recorded Added Amounts (${incomes.size})"),
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        // Price BELOW IT
                        Text(
                            text = "${currencyFormatter.format(if (historySubTab == 0) monthTotal else incomes.sumOf { it.amount })} ${strings.currencyUnit}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (historySubTab == 0) Color(0xFFEF4444) else Color(0xFF10B981)
                        )
                    }

                    if (currentList.isNotEmpty()) {
                        OutlinedButton(
                            onClick = {
                                if (historySubTab == 0) onClearAllExpenses() else onClearAllIncomes()
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.6f)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (historySubTab == 0) (if (isAr) "حذف سجل المصروف" else "Clear Expenses")
                                else (if (isAr) "حذف السجل" else "Clear Log"),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                        }
                    }
                }
            }
        }

        // Search Bar (Screenshot 4)
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(strings.searchExpensesPlaceholder, fontSize = 12.sp) },
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
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
        }

        // Items list (History log with Edit and Delete on every item)
        if (filteredList.isEmpty()) {
            item {
                EmptyState(
                    title = if (historySubTab == 0) strings.noExpensesFound else (if (isAr) "لا توجد مبالغ مضافة" else "No added income"),
                    subtitle = strings.noExpensesSubtitle,
                    icon = Icons.Default.Receipt
                )
            }
        } else {
            items(filteredList, key = { it.id }) { item ->
                TransactionCardItem(
                    item = item,
                    currencyFormatter = currencyFormatter,
                    currency = strings.currencyUnit,
                    strings = strings,
                    isAr = isAr,
                    onEdit = { onEditExpense(item) },
                    onDelete = { onDeleteExpense(item) }
                )
            }
        }
    }
}

@Composable
private fun TransactionCardItem(
    item: ExpenseItem,
    currencyFormatter: DecimalFormat,
    currency: String,
    strings: AppStrings,
    isAr: Boolean = true,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = remember(item.date) {
        val sdf = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale.getDefault())
        sdf.format(Date(item.date))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            0.9.dp,
            if (item.isIncome) Color(0xFF10B981).copy(alpha = 0.3f)
            else Color(0xFFDC2626).copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Title on TOP
                Text(
                    text = item.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                // Price BELOW IT
                Text(
                    text = "${if (item.isIncome) "+" else "-"}${currencyFormatter.format(item.amount)} $currency",
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (item.isIncome) Color(0xFF10B981) else Color(0xFFEF4444)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$dateStr${if (item.note.isNotBlank()) " • ${item.note}" else ""}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
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

// =================================================================
// REUSABLE SALARY CARD (Title on TOP, Value BELOW IT)
// =================================================================
@Composable
private fun SalarySpecCard(
    title: String,
    subtitle: String,
    amount: Double,
    currency: String,
    icon: ImageVector,
    themeColor: Color,
    currencyFormatter: DecimalFormat,
    isAr: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.2.dp, themeColor.copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header Row: Icon + Edit Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(themeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = themeColor, modifier = Modifier.size(17.dp))
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = themeColor.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = themeColor, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isAr) "تحديد" else "Set",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // TITLE ON TOP
            Text(
                text = title,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // AMOUNT VALUE BELOW IT
            if (amount > 0.0) {
                Text(
                    text = "${currencyFormatter.format(amount)} $currency",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = themeColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Text(
                    text = if (isAr) "غير محدد" else "Not set",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (isAr) "اضغط للتعيين ➜" else "Tap to set ➜",
                    fontSize = 9.5.sp,
                    color = themeColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FinancialDetailRow(
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

// =================================================================
// SALARY AMOUNT INPUT DIALOG (With live commas and Arabic breakdown)
// =================================================================
@Composable
private fun SalaryAmountInputDialog(
    title: String,
    subtitle: String,
    currentAmount: Double,
    currency: String,
    isAr: Boolean,
    saveLabel: String,
    cancelLabel: String,
    quickAmounts: List<Long>,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf(if (currentAmount > 0) currentAmount.toLong().toString() else "") }
    val parsedAmount = amountText.toLongOrNull() ?: 0L

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Text Field with thousands visual transformation!
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        amountText = input.filter { it.isDigit() }
                    },
                    visualTransformation = ThousandsSeparatorVisualTransformation(),
                    label = { Text(if (isAr) "المبلغ ($currency)" else "Amount in $currency", fontSize = 11.sp) },
                    placeholder = { Text("مثال: 800000", fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Live Comma & Arabic Denomination Preview
                if (parsedAmount > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isAr) "المبلغ بالأرقام والفوارز:" else "Formatted:",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${CurrencyFormatterUtils.format(parsedAmount)} $currency",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isAr) "القيمة كتابةً:" else "Words:",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = CurrencyFormatterUtils.toArabicDenomination(parsedAmount),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }
                    }
                }

                // Quick Increment Shortcuts
                Text(
                    text = if (isAr) "مبالغ مقترحة سريعة:" else "Quick amounts:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickAmounts.forEach { amt ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { amountText = amt.toString() }
                        ) {
                            Text(
                                text = "${CurrencyFormatterUtils.format(amt)}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(parsedAmount.toDouble()) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                Text(text = saveLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = cancelLabel, fontSize = 12.sp)
            }
        }
    )
}

// =================================================================
// SALARY RECEPTION DAY DIALOG
// =================================================================
@Composable
private fun SalaryDayInputDialog(
    currentDay: Int,
    isAr: Boolean,
    title: String,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var selectedDay by remember { mutableIntStateOf(currentDay) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isAr) "اختر اليوم من الشهر الذي تستلم فيه راتبك:" else "Select salary day of month:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(1, 5, 10, 15, 20, 23, 24, 25, 26, 27, 28, 30).forEach { day ->
                        Surface(
                            shape = CircleShape,
                            color = if (selectedDay == day) Color(0xFF10B981) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { selectedDay = day }
                        ) {
                            Box(
                                modifier = Modifier.size(38.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.toString(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedDay == day) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(selectedDay) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                Text(if (isAr) "حفظ" else "Save", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isAr) "إلغاء" else "Cancel", fontSize = 12.sp)
            }
        }
    )
}

// =================================================================
// EXPENSE OR INCOME INPUT DIALOG
// =================================================================
@Composable
private fun ExpenseOrIncomeInputDialog(
    isIncome: Boolean,
    initialItem: ExpenseItem?,
    currency: String,
    strings: AppStrings,
    isAr: Boolean,
    onDismiss: () -> Unit,
    onSave: (ExpenseItem) -> Unit
) {
    var title by remember { mutableStateOf(initialItem?.title ?: "") }
    var amountText by remember { mutableStateOf(if (initialItem != null) initialItem.amount.toLong().toString() else "") }
    var selectedCategory by remember { mutableStateOf(initialItem?.category ?: ExpenseCategory.OTHER) }
    var note by remember { mutableStateOf(initialItem?.note ?: "") }
    val parsedAmount = amountText.toLongOrNull() ?: 0L

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = when {
                    initialItem != null && isIncome -> if (isAr) "تعديل المبلغ المضاف" else "Edit Added Income"
                    initialItem != null && !isIncome -> if (isAr) "تعديل المصروف" else "Edit Expense"
                    isIncome -> if (isAr) "إضافة مبلغ جديد / دخل إضافي" else "Add Income"
                    else -> if (isAr) "تسجيل مصروف جديد" else "Record Expense"
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Description field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isAr) "البيان / الوصف" else "Description", fontSize = 11.sp) },
                    placeholder = {
                        Text(
                            if (isIncome) (if (isAr) "مثال: مكافأة عمل، أرباح، دخل إضافي" else "e.g. Bonus, Overtime")
                            else (if (isAr) "مثال: وجبة طعام، وقود، تسوق" else "e.g. Food, Fuel"),
                            fontSize = 11.sp
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Amount field with thousands commas
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input -> amountText = input.filter { it.isDigit() } },
                    visualTransformation = ThousandsSeparatorVisualTransformation(),
                    label = { Text(if (isAr) "المبلغ ($currency)" else "Amount ($currency)", fontSize = 11.sp) },
                    placeholder = { Text("0", fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Live denomination preview
                if (parsedAmount > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${CurrencyFormatterUtils.format(parsedAmount)} $currency",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = CurrencyFormatterUtils.toArabicDenomination(parsedAmount),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isIncome) Color(0xFF10B981) else Color(0xFFEF4444)
                            )
                        }
                    }
                }

                // Unified Category Box with Dropdown Menu (مربع واحد للتصنيف يفتح قائمة الخيارات)
                if (!isIncome) {
                    var showExpenseCatMenu by remember { mutableStateOf(false) }
                    fun expCatLabel(cat: ExpenseCategory): String = when (cat) {
                        ExpenseCategory.CAR_FARE -> if (isAr) "أجرة سيارة" else "Taxi / Car"
                        ExpenseCategory.HOUSEHOLD -> if (isAr) "مصروف بيت" else "Household"
                        ExpenseCategory.MEDICINE -> if (isAr) "علاجات وأدوية" else "Medicines"
                        ExpenseCategory.STUDY_FEES -> if (isAr) "أجور دراسة" else "Study / Tuition Fees"
                        ExpenseCategory.SHOPPING -> strings.expCategoryShopping
                        ExpenseCategory.RESTAURANTS -> if (isAr) "مطاعم" else "Restaurants"
                        ExpenseCategory.MARKETS -> if (isAr) "أسواق" else "Markets"
                        ExpenseCategory.TRAVEL -> if (isAr) "سفر" else "Travel"
                        ExpenseCategory.DELIVERY -> if (isAr) "نقل وتوصيل" else "Delivery"
                        ExpenseCategory.GIFT -> if (isAr) "هدية" else "Gift"
                        ExpenseCategory.DONATION -> if (isAr) "تبرع" else "Donation"
                        ExpenseCategory.PARENTS_RIGHTS -> if (isAr) "حقوق الوالدين" else "Parents' Rights"
                        ExpenseCategory.FAMILY_EXPENSE -> if (isAr) "مصروف الأهل" else "Family Expense"
                        ExpenseCategory.FOOD -> strings.expCategoryFood
                        ExpenseCategory.TRANSPORT -> strings.expCategoryTransport
                        ExpenseCategory.BILLS -> strings.expCategoryBills
                        ExpenseCategory.HEALTH -> strings.expCategoryHealth
                        ExpenseCategory.ENTERTAINMENT -> strings.expCategoryEntertainment
                        ExpenseCategory.OTHER -> strings.expCategoryOther
                    }

                    Text(
                        text = strings.expenseCategoryLabel,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showExpenseCatMenu = true }
                                .testTag("expense_dialog_category_box")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 11.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Label,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(17.dp)
                                    )
                                    Text(
                                        text = expCatLabel(selectedCategory),
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Icon(
                                    imageVector = if (showExpenseCatMenu) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showExpenseCatMenu,
                            onDismissRequest = { showExpenseCatMenu = false },
                            modifier = Modifier
                                .width(250.dp)
                                .heightIn(max = 310.dp)
                        ) {
                            ExpenseCategory.values().forEach { cat ->
                                val isSelected = selectedCategory == cat
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = expCatLabel(cat),
                                                fontSize = 12.5.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        selectedCategory = cat
                                        showExpenseCatMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && parsedAmount > 0) {
                        onSave(
                            initialItem?.copy(
                                title = title.trim(),
                                amount = parsedAmount.toDouble(),
                                category = selectedCategory,
                                note = note.trim(),
                                isIncome = isIncome
                            ) ?: ExpenseItem(
                                title = title.trim(),
                                amount = parsedAmount.toDouble(),
                                category = selectedCategory,
                                note = note.trim(),
                                isIncome = isIncome
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isIncome) Color(0xFF10B981) else Color(0xFFDC2626)
                )
            ) {
                Text(if (isAr) "حفظ" else "Save", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isAr) "إلغاء" else "Cancel", fontSize = 12.sp)
            }
        }
    )
}

// =================================================================
// BILL INPUT DIALOG (With Paid/Unpaid Status, Expanded Categories & Due Reminder)
// =================================================================
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun BillInputDialog(
    initialBill: BillItem?,
    currency: String,
    strings: AppStrings,
    isAr: Boolean,
    onDismiss: () -> Unit,
    onSave: (BillItem) -> Unit
) {
    var title by remember { mutableStateOf(initialBill?.title ?: "") }
    var amountText by remember { mutableStateOf(if (initialBill != null) initialBill.amount.toLong().toString() else "") }
    var category by remember { mutableStateOf(initialBill?.category ?: (if (isAr) "مصروف بيت" else "Household")) }
    var dueDay by remember { mutableIntStateOf(initialBill?.dueDayOfMonth ?: 1) }
    var dueMonth by remember { mutableIntStateOf(initialBill?.dueMonth ?: 0) }
    var priority by remember { mutableStateOf(initialBill?.priority ?: (if (isAr) "مهم" else "Important")) }
    var isPaid by remember { mutableStateOf(initialBill?.isPaid ?: false) }
    var isReserved by remember { mutableStateOf(initialBill?.isReserved ?: true) }
    var hasReminder by remember { mutableStateOf(initialBill?.hasReminder ?: false) }

    // Default reminder timestamp based on dueDay & dueMonth at 9:00 AM
    val defaultReminderMillis = remember(dueDay, dueMonth) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            if (dueMonth in 1..12) {
                set(Calendar.MONTH, dueMonth - 1)
            }
            val maxDay = getActualMaximum(Calendar.DAY_OF_MONTH)
            set(Calendar.DAY_OF_MONTH, dueDay.coerceIn(1, maxDay))
            if (timeInMillis <= System.currentTimeMillis()) {
                if (dueMonth in 1..12) {
                    add(Calendar.YEAR, 1)
                } else {
                    add(Calendar.MONTH, 1)
                }
            }
        }
        cal.timeInMillis
    }

    var reminderTimeMillis by remember {
        mutableStateOf(initialBill?.reminderTime ?: defaultReminderMillis)
    }
    var showReminderDatePicker by remember { mutableStateOf(false) }
    var showReminderTimePicker by remember { mutableStateOf(false) }

    val parsedAmount = amountText.toLongOrNull() ?: 0L

    // Expanded categories requested by the user (including أجور دراسة)
    val categories = if (isAr) {
        listOf(
            "أجرة سيارة",
            "مصروف بيت",
            "علاجات وأدوية",
            "أجور دراسة",
            "تسوق",
            "مطاعم",
            "أسواق",
            "سفر",
            "نقل وتوصيل",
            "هدية",
            "تبرع",
            "حقوق الوالدين",
            "مصروف الأهل",
            "مولد",
            "اتصالات وإنترنت",
            "كهرباء وماء",
            "سكن وإيجار",
            "أقساط",
            "اشتراكات",
            "أخرى"
        )
    } else {
        listOf(
            "Car / Taxi Fare",
            "Household Expense",
            "Medicines",
            "Study / Tuition Fees",
            "Shopping",
            "Restaurants",
            "Markets",
            "Travel",
            "Transport & Delivery",
            "Gift",
            "Donation",
            "Parents' Rights",
            "Family Allowance",
            "Generator",
            "Internet",
            "Electricity & Water",
            "Rent & Housing",
            "Installments",
            "Subscriptions",
            "Other"
        )
    }

    val monthsList = if (isAr) {
        listOf(
            0 to "كل الشهور (شهرياً)",
            1 to "1 - كانون الثاني (يناير)",
            2 to "2 - شباط (فبراير)",
            3 to "3 - آذار (مارس)",
            4 to "4 - نيسان (أبريل)",
            5 to "5 - أيار (مايو)",
            6 to "6 - حزيران (يونيو)",
            7 to "7 - تموز (يوليو)",
            8 to "8 - آب (أغسطس)",
            9 to "9 - أيلول (سبتمبر)",
            10 to "10 - تشرين الأول (أكتوبر)",
            11 to "11 - تشرين الثاني (نوفمبر)",
            12 to "12 - كانون الأول (ديسمبر)"
        )
    } else {
        listOf(
            0 to "Every Month",
            1 to "1 - Jan",
            2 to "2 - Feb",
            3 to "3 - Mar",
            4 to "4 - Apr",
            5 to "5 - May",
            6 to "6 - Jun",
            7 to "7 - Jul",
            8 to "8 - Aug",
            9 to "9 - Sep",
            10 to "10 - Oct",
            11 to "11 - Nov",
            12 to "12 - Dec"
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialBill == null) strings.btnAddBill else strings.btnEditBill,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isAr) "اسم الفاتورة / الالتزام" else "Bill Name", fontSize = 11.sp) },
                    placeholder = { Text(if (isAr) "مثال: مصروف البيت، أجرة سيارة، مولد" else "e.g. Household, Taxi, Generator", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input -> amountText = input.filter { it.isDigit() } },
                    visualTransformation = ThousandsSeparatorVisualTransformation(),
                    label = { Text(if (isAr) "المبلغ المطلوب ($currency)" else "Amount ($currency)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (parsedAmount > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${CurrencyFormatterUtils.format(parsedAmount)} $currency",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = CurrencyFormatterUtils.toArabicDenomination(parsedAmount),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // 1. خيار تحديد هل الفاتورة مسددة أم غير مسددة (نفس ما في النافذة الأساسية للالتزامات)
                Text(
                    text = if (isAr) "حالة التسديد (هل مسددة؟):" else "Payment Status:",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // غير مسددة
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isPaid = false },
                        shape = RoundedCornerShape(10.dp),
                        color = if (!isPaid) Color(0xFFEF4444).copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(
                            width = if (!isPaid) 1.5.dp else 1.dp,
                            color = if (!isPaid) Color(0xFFEF4444) else MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (!isPaid) Color(0xFFF87171) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "غير مسددة" else "Unpaid",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isPaid) Color(0xFFF87171) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // مسددة ✓
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isPaid = true },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isPaid) Color(0xFF10B981).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(
                            width = if (isPaid) 1.5.dp else 1.dp,
                            color = if (isPaid) Color(0xFF10B981) else MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isPaid) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "مسددة ✓" else "Paid ✓",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPaid) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // خيار حجز المبلغ من الميزانية
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = isReserved,
                        onClick = { isReserved = true },
                        label = { Text(if (isAr) "محجوزة من الميزانية" else "Reserved in Budget", fontSize = 10.5.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = !isReserved,
                        onClick = { isReserved = false },
                        label = { Text(if (isAr) "غير محجوزة" else "Not Reserved", fontSize = 10.5.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // 2. التصنيف (مربع واحد / بطاقة عند الضغط عليها تنبثق قائمة الخيارات)
                var showBillCategoryMenu by remember { mutableStateOf(false) }
                Text(
                    text = if (isAr) "التصنيف (اضغط لاختيار التصنيف المناسب):" else "Category:",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.14f),
                        border = BorderStroke(1.2.dp, Color(0xFF10B981).copy(alpha = 0.7f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showBillCategoryMenu = true }
                            .testTag("bill_category_selector_box")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 11.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Label,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(17.dp)
                                )
                                Text(
                                    text = category,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Icon(
                                imageVector = if (showBillCategoryMenu) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showBillCategoryMenu,
                        onDismissRequest = { showBillCategoryMenu = false },
                        modifier = Modifier
                            .width(250.dp)
                            .heightIn(max = 320.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = category == cat
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = cat,
                                            fontSize = 12.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color(0xFF10B981),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    category = cat
                                    showBillCategoryMenu = false
                                }
                            )
                        }
                    }
                }

                // 3. تحديد شهر ويوم الاستحقاق لتسليم المستحقات (مربع واحد أنيق لشهر الاستحقاق)
                var showDueMonthMenu by remember { mutableStateOf(false) }
                val selectedMonthLabel = monthsList.firstOrNull { it.first == dueMonth }?.second
                    ?: monthsList.first().second
                Text(
                    text = if (isAr) "شهر الاستحقاق:" else "Due Month:",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showDueMonthMenu = true }
                            .testTag("bill_due_month_selector_box")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(17.dp)
                                )
                                Text(
                                    text = selectedMonthLabel,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Icon(
                                imageVector = if (showDueMonthMenu) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showDueMonthMenu,
                        onDismissRequest = { showDueMonthMenu = false },
                        modifier = Modifier
                            .width(250.dp)
                            .heightIn(max = 300.dp)
                    ) {
                        monthsList.forEach { (mIndex, mName) ->
                            val isSelected = dueMonth == mIndex
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = mName,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color(0xFF10B981),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    dueMonth = mIndex
                                    val cal = Calendar.getInstance().apply { timeInMillis = reminderTimeMillis }
                                    if (mIndex in 1..12) {
                                        cal.set(Calendar.MONTH, mIndex - 1)
                                    }
                                    reminderTimeMillis = cal.timeInMillis
                                    showDueMonthMenu = false
                                }
                            )
                        }
                    }
                }

                Text(
                    text = if (isAr) "يوم الاستحقاق الشهري: $dueDay" else "Due Day of Month: $dueDay",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    (1..31).forEach { day ->
                        Surface(
                            shape = CircleShape,
                            color = if (dueDay == day) Color(0xFF10B981) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable {
                                dueDay = day
                                val cal = Calendar.getInstance().apply { timeInMillis = reminderTimeMillis }
                                val maxD = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                                cal.set(Calendar.DAY_OF_MONTH, day.coerceIn(1, maxD))
                                reminderTimeMillis = cal.timeInMillis
                            }
                        ) {
                            Box(modifier = Modifier.size(34.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = day.toString(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (dueDay == day) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // 4. ميزة إشعار التذكير بالشهر أو التاريخ لتسليم المستحقات (USER REQUIREMENT)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (hasReminder) Color(0xFFF59E0B).copy(alpha = 0.12f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    border = BorderStroke(
                        1.dp,
                        if (hasReminder) Color(0xFFF59E0B) else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (hasReminder) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = if (hasReminder) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isAr) "إشعار تذكير بتسليم المستحقات" else "Due Date Notification Alert",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isAr) "تنبيه بالشهر والتاريخ والوقت لتسديد الفاتورة" else "Remind me on the due date & month",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Switch(
                                checked = hasReminder,
                                onCheckedChange = { enabled ->
                                    hasReminder = enabled
                                    if (enabled && reminderTimeMillis <= System.currentTimeMillis()) {
                                        reminderTimeMillis = defaultReminderMillis
                                    }
                                }
                            )
                        }

                        if (hasReminder) {
                            val formattedDate = remember(reminderTimeMillis) {
                                SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date(reminderTimeMillis))
                            }
                            val formattedTime = remember(reminderTimeMillis) {
                                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(reminderTimeMillis))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showReminderDatePicker = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = formattedDate, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { showReminderTimePicker = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = formattedTime, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && parsedAmount > 0) {
                        val frequencyStr = if (dueMonth in 1..12) {
                            if (isAr) "شهر $dueMonth" else "Month $dueMonth"
                        } else {
                            if (isAr) "شهرية" else "Monthly"
                        }
                        onSave(
                            initialBill?.copy(
                                title = title.trim(),
                                amount = parsedAmount.toDouble(),
                                category = category,
                                dueDayOfMonth = dueDay,
                                dueMonth = dueMonth,
                                frequency = frequencyStr,
                                priority = priority,
                                isPaid = isPaid,
                                isReserved = isReserved,
                                hasReminder = hasReminder,
                                reminderTime = if (hasReminder) reminderTimeMillis else null
                            ) ?: BillItem(
                                title = title.trim(),
                                amount = parsedAmount.toDouble(),
                                category = category,
                                dueDayOfMonth = dueDay,
                                dueMonth = dueMonth,
                                frequency = frequencyStr,
                                priority = priority,
                                isPaid = isPaid,
                                isReserved = isReserved,
                                hasReminder = hasReminder,
                                reminderTime = if (hasReminder) reminderTimeMillis else null
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                Text(if (isAr) "حفظ" else "Save", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isAr) "إلغاء" else "Cancel", fontSize = 12.sp)
            }
        }
    )

    if (showReminderDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = reminderTimeMillis)
        DatePickerDialog(
            onDismissRequest = { showReminderDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedDate ->
                            val oldCal = Calendar.getInstance().apply { timeInMillis = reminderTimeMillis }
                            val newCal = Calendar.getInstance().apply {
                                timeInMillis = selectedDate
                                set(Calendar.HOUR_OF_DAY, oldCal.get(Calendar.HOUR_OF_DAY))
                                set(Calendar.MINUTE, oldCal.get(Calendar.MINUTE))
                                set(Calendar.SECOND, 0)
                            }
                            reminderTimeMillis = newCal.timeInMillis
                            dueDay = newCal.get(Calendar.DAY_OF_MONTH)
                            dueMonth = newCal.get(Calendar.MONTH) + 1
                        }
                        showReminderDatePicker = false
                    }
                ) {
                    Text(if (isAr) "تأكيد" else "Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReminderDatePicker = false }) {
                    Text(if (isAr) "إلغاء" else "Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showReminderTimePicker) {
        val cal = Calendar.getInstance().apply { timeInMillis = reminderTimeMillis }
        val timePickerState = rememberTimePickerState(
            initialHour = cal.get(Calendar.HOUR_OF_DAY),
            initialMinute = cal.get(Calendar.MINUTE),
            is24Hour = false
        )
        AlertDialog(
            onDismissRequest = { showReminderTimePicker = false },
            title = {
                Text(
                    text = if (isAr) "تحديد وقت إشعار المستحقات" else "Select Reminder Time",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val updatedCal = Calendar.getInstance().apply {
                            timeInMillis = reminderTimeMillis
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                            set(Calendar.SECOND, 0)
                        }
                        reminderTimeMillis = updatedCal.timeInMillis
                        showReminderTimePicker = false
                    }
                ) {
                    Text(if (isAr) "تأكيد" else "Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReminderTimePicker = false }) {
                    Text(if (isAr) "إلغاء" else "Cancel")
                }
            }
        )
    }
}

// =================================================================
// DEDICATED NEW SALARY SETUP DIALOG (نافذة تحديد الراتب الجديد وتحديده)
// =================================================================
@Composable
private fun SetNewSalaryDialog(
    currentBasicSalary: Double,
    currentAddedSalary: Double,
    currentSalaryDay: Int,
    currency: String,
    strings: AppStrings,
    isAr: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (newBasic: Double, newAdded: Double, newDay: Int) -> Unit
) {
    var basicText by remember { mutableStateOf(if (currentBasicSalary > 0) currentBasicSalary.toLong().toString() else "") }
    var addedText by remember { mutableStateOf(if (currentAddedSalary > 0) currentAddedSalary.toLong().toString() else "") }
    var selectedDay by remember { mutableIntStateOf(currentSalaryDay.coerceIn(1, 31)) }

    val parsedBasic = basicText.toLongOrNull() ?: 0L
    val parsedAdded = addedText.toLongOrNull() ?: 0L
    val totalActual = parsedBasic + parsedAdded

    val quickSalaryPresets = listOf(500000L, 750000L, 1000000L, 1250000L, 1500000L, 2000000L)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyanSkyPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = CyanSkyPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isAr) "تحديد الراتب الجديد" else "Set New Salary",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (isAr) "حدد الراتب الأساسي والمخصصات وموعد الصرف" else "Specify base, bonus & deposit day",
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. حقل الراتب الأساسي الجديد
                Column {
                    Text(
                        text = if (isAr) "1. الراتب الأساسي الجديد ($currency)" else "1. New Base Salary ($currency)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = basicText,
                        onValueChange = { input -> basicText = input.filter { it.isDigit() } },
                        visualTransformation = ThousandsSeparatorVisualTransformation(),
                        placeholder = { Text(if (isAr) "مثال: 1,000,000" else "e.g. 1,000,000", fontSize = 13.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (parsedBasic > 0) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = CurrencyFormatterUtils.toArabicDenomination(parsedBasic),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                }

                // مبالغ سريعة مقترحة للراتب
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickSalaryPresets.forEach { preset ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { basicText = preset.toString() }
                        ) {
                            Text(
                                text = CurrencyFormatterUtils.format(preset),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // 2. حقل الراتب المضاف / الحوافز والمخصصات
                Column {
                    Text(
                        text = if (isAr) "2. الراتب المضاف / حوافز إضافية ($currency)" else "2. Added / Bonus Salary ($currency)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = addedText,
                        onValueChange = { input -> addedText = input.filter { it.isDigit() } },
                        visualTransformation = ThousandsSeparatorVisualTransformation(),
                        placeholder = { Text(if (isAr) "اختياري (أصفار أو مخصصات)" else "Optional (e.g. 150,000)", fontSize = 13.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (parsedAdded > 0) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = CurrencyFormatterUtils.toArabicDenomination(parsedAdded),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                }

                // 3. يوم استلام ونزول الراتب من كل شهر
                Column {
                    Text(
                        text = if (isAr) "3. يوم استلام الراتب من كل شهر" else "3. Salary Reception Day of Month",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(1, 5, 10, 15, 20, 23, 25, 28, 30).forEach { day ->
                            val isSelected = selectedDay == day
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) CyanSkyPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { selectedDay = day }
                            ) {
                                Text(
                                    text = "$day",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // بطاقة ملخص الراتب الفعلي
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF0F2035),
                    border = BorderStroke(1.dp, CyanSkyPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = if (isAr) "إجمالي الراتب الفعلي الجديد:" else "Total Actual New Salary:",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${CurrencyFormatterUtils.format(totalActual)} $currency",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = CyanSkyPrimary
                        )
                        if (totalActual > 0) {
                            Text(
                                text = CurrencyFormatterUtils.toArabicDenomination(totalActual),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(parsedBasic.toDouble(), parsedAdded.toDouble(), selectedDay)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanSkyPrimary, contentColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = if (isAr) "اعتماد الراتب الجديد ✓" else "Confirm New Salary ✓",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = if (isAr) "إلغاء" else "Cancel",
                    color = Color(0xFF94A3B8)
                )
            }
        }
    )
}
