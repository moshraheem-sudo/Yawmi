package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.YawmiDatabase
import com.example.data.local.entity.ExpenseItem
import com.example.data.local.entity.NoteItem
import com.example.data.local.entity.TaskItem
import com.example.data.repository.ExpenseRepository
import com.example.data.repository.NoteRepository
import com.example.data.repository.TaskRepository
import com.example.data.repository.UserPreferences
import com.example.data.repository.UserPreferencesRepository
import com.example.reminder.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

import com.example.data.local.entity.BillItem
import com.example.data.repository.BillRepository

class YawmiViewModel(application: Application) : AndroidViewModel(application) {

    private val database = YawmiDatabase.getInstance(application)
    private val taskRepository = TaskRepository(database.taskDao())
    private val noteRepository = NoteRepository(database.noteDao())
    private val expenseRepository = ExpenseRepository(database.expenseDao())
    private val billRepository = BillRepository(database.billDao())
    private val preferencesRepository = UserPreferencesRepository(application)

    init {
        // Seed initial default bills if empty (matching the user's reference screenshots)
        viewModelScope.launch {
            kotlinx.coroutines.delay(200)
            val currentBills = database.billDao().getAllBills().firstOrNull()
            if (currentBills.isNullOrEmpty()) {
                billRepository.insertBills(
                        listOf(
                            BillItem(
                                title = "أجور الإنترنت والواي فاي",
                                amount = 45000.0,
                                category = "اتصالات وإنترنت",
                                dueDayOfMonth = 27,
                                frequency = "شهرية",
                                priority = "مهم",
                                isPaid = false,
                                isReserved = true
                            ),
                            BillItem(
                                title = "مولد الكهرباء الشهري",
                                amount = 60000.0,
                                category = "كهرباء وماء",
                                dueDayOfMonth = 1,
                                frequency = "شهرية",
                                priority = "مهم",
                                isPaid = false,
                                isReserved = true
                            ),
                            BillItem(
                                title = "اشتراك قنوات وبرامج ترفيهية",
                                amount = 15000.0,
                                category = "ترفيه وتسوق",
                                dueDayOfMonth = 11,
                                frequency = "شهرية",
                                priority = "منخفض الأهمية",
                                isPaid = false,
                                isReserved = false
                            )
                        )
                    )
                }

            // Clean up any legacy auto-generated bill payment entries in expenses so commitments and expenses never double-count
            try {
                val existingExpenses = database.expenseDao().getAllExpenses().firstOrNull().orEmpty()
                existingExpenses.filter { !it.isIncome && it.note.startsWith("سداد فاتورة:") }.forEach { legacyExp ->
                    expenseRepository.deleteExpense(legacyExp)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Ensure all pending reminders (tasks, notes, bills) are actively scheduled in AlarmManager
            // and automatically synchronize monthly commitments & installment history
            try {
                val now = System.currentTimeMillis()
                val appContext = getApplication<Application>()
                ReminderScheduler.syncMonthlyCommitments(appContext, database.billDao())

                database.taskDao().getPendingRemindersSync(now).forEach { task ->
                    task.reminderTime?.let { time ->
                        ReminderScheduler.scheduleReminder(appContext, task.id, task.title, time)
                    }
                }
                database.noteDao().getPendingNoteRemindersSync(now).forEach { note ->
                    note.reminderTime?.let { time ->
                        ReminderScheduler.scheduleNoteReminder(appContext, note.id, note.title, time)
                    }
                }
                database.billDao().getPendingBillRemindersSync(now).forEach { bill ->
                    bill.reminderTime?.let { time ->
                        ReminderScheduler.scheduleBillReminder(appContext, bill.id, bill.title, time)
                    }
                }
                val unpaidBills = database.billDao().getUnpaidBillsSync()
                unpaidBills.forEach { bill ->
                    ReminderScheduler.scheduleAdvanceBillDueAlarms(appContext, bill)
                }
                ReminderScheduler.schedulePeriodicUpcomingBillsCheck(appContext)
                val currentPrefs = preferencesRepository.userPreferencesFlow.firstOrNull()
                if (currentPrefs?.notificationsEnabled != false) {
                    ReminderScheduler.checkAndNotifyUpcomingBillsNow(appContext, unpaidBills)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Bills State
    val allBills: StateFlow<List<BillItem>> = billRepository.allBills
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // User Preferences

    // User Preferences
    val userPreferences: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    // Tasks State
    val allTasks: StateFlow<List<TaskItem>> = taskRepository.allTasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Upcoming reminders
    val upcomingReminders: StateFlow<List<TaskItem>> = allTasks.map { tasks ->
        val now = System.currentTimeMillis()
        tasks.filter { it.hasReminder && !it.isCompleted && (it.reminderTime ?: 0L) > now }
            .sortedBy { it.reminderTime }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notes State
    private val _noteSearchQuery = MutableStateFlow("")
    val noteSearchQuery: StateFlow<String> = _noteSearchQuery.asStateFlow()

    val allNotes: StateFlow<List<NoteItem>> = combine(
        noteRepository.allNotes,
        _noteSearchQuery
    ) { notes, query ->
        if (query.isBlank()) {
            notes
        } else {
            notes.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.content.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Expenses State
    val allExpenses: StateFlow<List<ExpenseItem>> = expenseRepository.allExpenses
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Calendar selected date (start of day in ms)
    private val _selectedCalendarDate = MutableStateFlow(getStartOfDay(System.currentTimeMillis()))
    val selectedCalendarDate: StateFlow<Long> = _selectedCalendarDate.asStateFlow()

    // Tasks for the selected calendar date
    val tasksForSelectedDate: StateFlow<List<TaskItem>> = combine(
        allTasks,
        _selectedCalendarDate
    ) { tasks, selectedDate ->
        val endOfDay = getEndOfDay(selectedDate)
        tasks.filter { task ->
            val due = task.dueDate
            if (due != null) {
                due in selectedDate..endOfDay
            } else {
                false
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Helper functions for start and end of day
    private fun getStartOfDay(timeMillis: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = timeMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun getEndOfDay(timeMillis: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = timeMillis
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return cal.timeInMillis
    }

    fun setSelectedCalendarDate(dateMillis: Long) {
        _selectedCalendarDate.value = getStartOfDay(dateMillis)
    }

    fun setNoteSearchQuery(query: String) {
        _noteSearchQuery.value = query
    }

    // Task Actions
    fun saveTask(
        task: TaskItem,
        context: Context,
        onReminderResult: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            val insertedId = if (task.id == 0L) {
                taskRepository.insertTask(task)
            } else {
                taskRepository.updateTask(task)
                task.id
            }

            if (task.hasReminder && task.reminderTime != null && !task.isCompleted) {
                val scheduled = ReminderScheduler.scheduleReminder(
                    context = context,
                    taskId = insertedId,
                    title = task.title,
                    reminderTimeMillis = task.reminderTime
                )
                onReminderResult(scheduled)
            } else {
                ReminderScheduler.cancelReminder(context, insertedId)
                onReminderResult(true)
            }
        }
    }

    fun toggleTaskCompleted(task: TaskItem, context: Context) {
        viewModelScope.launch {
            val nextState = !task.isCompleted
            taskRepository.toggleCompleted(task.id, nextState)
            if (nextState) {
                ReminderScheduler.cancelReminder(context, task.id)
            } else if (task.hasReminder && task.reminderTime != null && task.reminderTime > System.currentTimeMillis()) {
                ReminderScheduler.scheduleReminder(
                    context = context,
                    taskId = task.id,
                    title = task.title,
                    reminderTimeMillis = task.reminderTime
                )
            }
        }
    }

    fun toggleTaskPinned(task: TaskItem) {
        viewModelScope.launch {
            taskRepository.togglePinned(task.id, !task.isPinned)
        }
    }

    fun deleteTask(task: TaskItem, context: Context) {
        viewModelScope.launch {
            ReminderScheduler.cancelReminder(context, task.id)
            taskRepository.deleteTask(task)
        }
    }

    // Note Actions
    fun saveNote(note: NoteItem, context: Context, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val noteToSave = note.copy(updatedAt = System.currentTimeMillis())
            val id = if (noteToSave.id == 0L) {
                noteRepository.insertNote(noteToSave)
            } else {
                noteRepository.updateNote(noteToSave)
                noteToSave.id
            }

            if (noteToSave.hasReminder && noteToSave.reminderTime != null) {
                val scheduled = ReminderScheduler.scheduleNoteReminder(
                    context = context,
                    noteId = id,
                    title = noteToSave.title.ifBlank { "ملاحظة" },
                    reminderTimeMillis = noteToSave.reminderTime
                )
                onResult(scheduled)
            } else {
                ReminderScheduler.cancelNoteReminder(context, id)
                onResult(true)
            }
        }
    }

    fun toggleNotePinned(note: NoteItem) {
        viewModelScope.launch {
            noteRepository.togglePinned(note.id, !note.isPinned)
        }
    }

    fun toggleNoteFavorite(note: NoteItem) {
        viewModelScope.launch {
            noteRepository.toggleFavorite(note.id, !note.isFavorite)
        }
    }

    fun deleteNote(note: NoteItem, context: Context) {
        viewModelScope.launch {
            ReminderScheduler.cancelNoteReminder(context, note.id)
            noteRepository.deleteNote(note)
        }
    }

    // Expense Actions
    fun saveExpense(expense: ExpenseItem) {
        viewModelScope.launch {
            if (expense.id == 0L) {
                expenseRepository.insertExpense(expense)
            } else {
                expenseRepository.updateExpense(expense)
            }
        }
    }

    fun deleteExpense(expense: ExpenseItem) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expense)
        }
    }

    fun clearAllExpensesOnly() {
        viewModelScope.launch {
            expenseRepository.deleteAllExpensesOnly()
        }
    }

    fun clearAllIncomesOnly() {
        viewModelScope.launch {
            expenseRepository.deleteAllIncomesOnly()
        }
    }

    // Preferences Actions
    fun setLanguage(lang: String) {
        viewModelScope.launch {
            preferencesRepository.setLanguage(lang)
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun setFontScaleMode(mode: String) {
        viewModelScope.launch {
            preferencesRepository.setFontScaleMode(mode)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setNotificationsEnabled(enabled)
        }
    }

    fun setVibrateEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setVibrateEnabled(enabled)
        }
    }

    fun setBasicSalary(salary: Double) {
        viewModelScope.launch {
            preferencesRepository.setBasicSalary(salary)
        }
    }

    fun setAddedSalary(added: Double) {
        viewModelScope.launch {
            preferencesRepository.setAddedSalary(added)
        }
    }

    fun setAddedSalaryName(name: String) {
        viewModelScope.launch {
            preferencesRepository.setAddedSalaryName(name)
        }
    }

    fun setPreviousMonthBalance(balance: Double) {
        viewModelScope.launch {
            preferencesRepository.setPreviousMonthBalance(balance)
        }
    }

    fun setSalaryDayOfMonth(day: Int) {
        viewModelScope.launch {
            preferencesRepository.setSalaryDayOfMonth(day)
        }
    }

    fun saveBill(
        bill: BillItem,
        context: Context? = null,
        onReminderResult: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            val normalizedBill = if (bill.isPaid && bill.paymentHistoryJson.isBlank()) {
                bill.withRecordedPayment(
                    monthKey = BillItem.currentMonthKey(),
                    timestamp = bill.lastPaidAt ?: System.currentTimeMillis(),
                    paymentAmount = bill.amount
                )
            } else {
                bill
            }
            val billId = if (normalizedBill.id == 0L) {
                billRepository.insertBill(normalizedBill)
            } else {
                billRepository.updateBill(normalizedBill)
                normalizedBill.id
            }
            val savedBill = normalizedBill.copy(id = billId)

            if (context != null) {
                ReminderScheduler.scheduleAdvanceBillDueAlarms(context, savedBill)
                if (!savedBill.isPaid) {
                    ReminderScheduler.checkAndNotifyUpcomingBillsNow(
                        context = context,
                        bills = listOf(savedBill),
                        forceNotifyBillId = billId
                    )
                }

                if (savedBill.hasReminder && savedBill.reminderTime != null && !savedBill.isPaid) {
                    val scheduled = ReminderScheduler.scheduleBillReminder(
                        context = context,
                        billId = billId,
                        title = savedBill.title,
                        reminderTimeMillis = savedBill.reminderTime
                    )
                    onReminderResult(scheduled)
                } else {
                    ReminderScheduler.cancelBillReminder(context, billId)
                    if (!savedBill.isPaid) {
                        ReminderScheduler.scheduleAdvanceBillDueAlarms(context, savedBill)
                    } else if (savedBill.hasReminder && savedBill.dueMonth == 0 && !savedBill.isInstallmentCompleted) {
                        val nextMonthTime = ReminderScheduler.computeNextMonthReminderTime(savedBill)
                        ReminderScheduler.scheduleBillReminder(context, billId, savedBill.title, nextMonthTime)
                    }
                    onReminderResult(true)
                }
            }
        }
    }

    fun deleteBill(bill: BillItem, context: Context? = null) {
        viewModelScope.launch {
            if (context != null) {
                ReminderScheduler.cancelBillReminder(context, bill.id)
            }
            billRepository.deleteBill(bill)
        }
    }

    fun payBill(bill: BillItem, context: Context? = null) {
        viewModelScope.launch {
            if (context != null) {
                ReminderScheduler.cancelBillReminder(context, bill.id)
            }
            // Record this month's payment with exact timestamp & amount in the commitment's monthly history
            var paidBill = bill.withRecordedPayment(
                monthKey = BillItem.currentMonthKey(),
                timestamp = System.currentTimeMillis(),
                paymentAmount = bill.amount
            )
            // Schedule next month's automatic reminder at the exact same specified time if recurring
            if (context != null && paidBill.hasReminder && paidBill.dueMonth == 0 && !paidBill.isInstallmentCompleted) {
                val nextMonthTime = ReminderScheduler.computeNextMonthReminderTime(paidBill)
                paidBill = paidBill.copy(reminderTime = nextMonthTime)
                ReminderScheduler.scheduleBillReminder(
                    context = context,
                    billId = paidBill.id,
                    title = paidBill.title,
                    reminderTimeMillis = nextMonthTime
                )
            }
            billRepository.updateBill(paidBill)
        }
    }

    fun unpayBill(bill: BillItem, context: Context? = null) {
        viewModelScope.launch {
            val unpaidBill = bill.withRemovedPaymentForMonth(BillItem.currentMonthKey())
            billRepository.updateBill(unpaidBill)
            if (context != null) {
                ReminderScheduler.scheduleAdvanceBillDueAlarms(context, unpaidBill)
                ReminderScheduler.checkAndNotifyUpcomingBillsNow(
                    context = context,
                    bills = listOf(unpaidBill),
                    forceNotifyBillId = unpaidBill.id
                )
                if (unpaidBill.hasReminder && unpaidBill.reminderTime != null && unpaidBill.reminderTime > System.currentTimeMillis()) {
                    ReminderScheduler.scheduleBillReminder(
                        context = context,
                        billId = unpaidBill.id,
                        title = unpaidBill.title,
                        reminderTimeMillis = unpaidBill.reminderTime
                    )
                }
            }
        }
    }

    fun clearAllData(context: Context, onCleared: () -> Unit) {
        viewModelScope.launch {
            // Cancel reminders
            val now = System.currentTimeMillis()
            val pendingReminders = database.taskDao().getPendingRemindersSync(now)
            for (t in pendingReminders) {
                ReminderScheduler.cancelReminder(context, t.id)
            }

            taskRepository.deleteAllTasks()
            noteRepository.deleteAllNotes()
            expenseRepository.deleteAllExpenses()
            onCleared()
        }
    }
}
