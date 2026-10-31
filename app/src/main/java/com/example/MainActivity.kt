package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.localization.ArabicStrings
import com.example.localization.EnglishStrings
import com.example.localization.LocalAppLayoutDirection
import com.example.localization.LocalAppStrings
import com.example.reminder.NotificationHelper
import com.example.ui.components.AppBottomBar
import com.example.ui.components.AppDestination
import com.example.ui.components.AppTopBar
import com.example.ui.components.NewDataEntry
import com.example.ui.components.UniversalDataInputDialog
import android.widget.Toast
import com.example.ui.screens.calendar.CalendarScreen
import com.example.ui.screens.expenses.ExpensesScreen
import com.example.ui.screens.expenses.FullScreenExpenseEditor
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.notes.FullScreenNoteEditor
import com.example.ui.screens.notes.NotesScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.tasks.AddEditTaskDialog
import com.example.ui.screens.tasks.FullScreenTaskEditor
import com.example.ui.screens.tasks.TasksScreen
import com.example.ui.theme.YawmiTheme
import com.example.ui.viewmodel.YawmiViewModel

import com.example.reminder.AlarmPlayer

class MainActivity : ComponentActivity() {

    private val viewModel: YawmiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificationHelper.createNotificationChannel(this)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }
        if (intent.getBooleanExtra("FROM_ALARM", false)) {
            AlarmPlayer.stop(this)
        }

        setContent {
            val preferences by viewModel.userPreferences.collectAsStateWithLifecycle()
            val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
            val allNotes by viewModel.allNotes.collectAsStateWithLifecycle()
            val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
            val allBills by viewModel.allBills.collectAsStateWithLifecycle()
            val upcomingReminders by viewModel.upcomingReminders.collectAsStateWithLifecycle()
            val noteSearchQuery by viewModel.noteSearchQuery.collectAsStateWithLifecycle()
            val selectedCalendarDate by viewModel.selectedCalendarDate.collectAsStateWithLifecycle()

            val isDarkTheme = when (preferences.themeMode.lowercase()) {
                "dark", "black", "transparent", "notes" -> true
                "light", "white", "natural" -> false
                else -> isSystemInDarkTheme()
            }

            val appStrings = if (preferences.language == "ar") ArabicStrings else EnglishStrings
            val layoutDirection = if (preferences.language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr

            val currentDensity = androidx.compose.ui.platform.LocalDensity.current
            val compactDensity = remember(currentDensity.density, preferences.fontScale) {
                androidx.compose.ui.unit.Density(
                    density = currentDensity.density,
                    fontScale = preferences.fontScale
                )
            }

            YawmiTheme(
                themeMode = preferences.themeMode,
                darkTheme = isDarkTheme
            ) {
                CompositionLocalProvider(
                    LocalLayoutDirection provides layoutDirection,
                    LocalAppStrings provides appStrings,
                    androidx.compose.ui.platform.LocalDensity provides compactDensity
                ) {
                    YawmiMainApp(
                        viewModel = viewModel,
                        preferences = preferences,
                        allTasks = allTasks,
                        allNotes = allNotes,
                        allExpenses = allExpenses,
                        allBills = allBills,
                        upcomingReminders = upcomingReminders,
                        noteSearchQuery = noteSearchQuery,
                        selectedCalendarDate = selectedCalendarDate
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra("FROM_ALARM", false)) {
            AlarmPlayer.stop(this)
        }
    }
}

@Composable
fun YawmiMainApp(
    viewModel: YawmiViewModel,
    preferences: com.example.data.repository.UserPreferences,
    allTasks: List<com.example.data.local.entity.TaskItem>,
    allNotes: List<com.example.data.local.entity.NoteItem>,
    allExpenses: List<com.example.data.local.entity.ExpenseItem>,
    allBills: List<com.example.data.local.entity.BillItem>,
    upcomingReminders: List<com.example.data.local.entity.TaskItem>,
    noteSearchQuery: String,
    selectedCalendarDate: Long
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val strings = LocalAppStrings.current
    var currentDestination by remember { mutableStateOf(AppDestination.HOME) }
    var previousDestination by remember { mutableStateOf(AppDestination.HOME) }

    // Full screen note editor state
    var fullScreenEditingNote by remember { mutableStateOf<com.example.data.local.entity.NoteItem?>(null) }
    var isFullScreenNoteEditorOpen by remember { mutableStateOf(false) }

    // Quick Add dialogs triggered from Home screen
    var showQuickAddTaskDialog by remember { mutableStateOf(false) }
    var showQuickAddExpenseDialog by remember { mutableStateOf(false) }
    var taskToEditOnHome by remember { mutableStateOf<com.example.data.local.entity.TaskItem?>(null) }

    if (isFullScreenNoteEditorOpen) {
        FullScreenNoteEditor(
            note = fullScreenEditingNote,
            currentLanguage = preferences.language,
            onDismiss = {
                isFullScreenNoteEditorOpen = false
                fullScreenEditingNote = null
            },
            onSave = { noteToSave ->
                viewModel.saveNote(noteToSave, context) { scheduled ->
                    if (noteToSave.hasReminder && !scheduled) {
                        Toast.makeText(context, strings.reminderPastTimeError, Toast.LENGTH_SHORT).show()
                    } else if (noteToSave.hasReminder && scheduled) {
                        Toast.makeText(context, strings.reminderScheduledSuccess, Toast.LENGTH_SHORT).show()
                    }
                }
                isFullScreenNoteEditorOpen = false
                fullScreenEditingNote = null
            }
        )
        return
    }

    BackHandler(enabled = currentDestination != AppDestination.HOME) {
        currentDestination = previousDestination.takeIf { it != currentDestination } ?: AppDestination.HOME
    }

    val screenTitle = when (currentDestination) {
        AppDestination.HOME -> strings.appName
        AppDestination.TASKS -> strings.tasksTitle
        AppDestination.NOTES -> strings.notesTitle
        AppDestination.EXPENSES -> strings.expensesTitle
        AppDestination.CALENDAR -> strings.calendarTitle
        AppDestination.SETTINGS -> strings.settingsTitle
    }

    Scaffold(
        topBar = {
            if (currentDestination != AppDestination.NOTES) {
                AppTopBar(
                    title = screenTitle,
                    canNavigateBack = currentDestination == AppDestination.CALENDAR,
                    onNavigateBack = { currentDestination = previousDestination },
                    showCalendarAction = currentDestination != AppDestination.CALENDAR,
                    onCalendarClick = {
                        previousDestination = currentDestination
                        currentDestination = AppDestination.CALENDAR
                    },
                    currentLanguage = preferences.language,
                    onToggleLanguage = {
                        val next = if (preferences.language == "ar") "en" else "ar"
                        viewModel.setLanguage(next)
                    }
                )
            }
        },
        bottomBar = {
            AppBottomBar(
                currentDestination = currentDestination,
                onNavigate = { dest ->
                    previousDestination = currentDestination
                    currentDestination = dest
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                AppDestination.HOME -> {
                    HomeScreen(
                        tasks = allTasks,
                        expenses = allExpenses,
                        bills = allBills,
                        upcomingReminders = upcomingReminders,
                        currentLanguage = preferences.language,
                        preferences = preferences,
                        onToggleTaskCompleted = { task ->
                            viewModel.toggleTaskCompleted(task, context)
                        },
                        onEditTask = { task ->
                            taskToEditOnHome = task
                        },
                        onOpenAddTask = {
                            showQuickAddTaskDialog = true
                        },
                        onOpenAddNote = {
                            fullScreenEditingNote = null
                            isFullScreenNoteEditorOpen = true
                        },
                        onOpenAddExpense = {
                            showQuickAddExpenseDialog = true
                        },
                        onNavigateToTasks = {
                            currentDestination = AppDestination.TASKS
                        },
                        onNavigateToExpenses = {
                            currentDestination = AppDestination.EXPENSES
                        },
                        onNavigateToCalendar = {
                            previousDestination = AppDestination.HOME
                            currentDestination = AppDestination.CALENDAR
                        },
                        onAddUniversalEntry = { entry ->
                            when (entry) {
                                is NewDataEntry.Task -> {
                                    viewModel.saveTask(
                                        com.example.data.local.entity.TaskItem(
                                            title = entry.title,
                                            description = entry.description,
                                            priority = entry.priority,
                                            category = entry.category
                                        ),
                                        context
                                    )
                                    Toast.makeText(context, strings.quickAddSuccess, Toast.LENGTH_SHORT).show()
                                }
                                is NewDataEntry.Note -> {
                                    viewModel.saveNote(
                                        com.example.data.local.entity.NoteItem(
                                            title = entry.title,
                                            content = entry.content,
                                            colorHex = entry.colorHex
                                        ),
                                        context
                                    )
                                    Toast.makeText(context, strings.quickAddSuccess, Toast.LENGTH_SHORT).show()
                                }
                                is NewDataEntry.Expense -> {
                                    viewModel.saveExpense(
                                        com.example.data.local.entity.ExpenseItem(
                                            title = entry.title,
                                            amount = entry.amount,
                                            note = entry.note,
                                            category = entry.category
                                        )
                                    )
                                    Toast.makeText(context, strings.quickAddSuccess, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }

                AppDestination.TASKS -> {
                    TasksScreen(
                        tasks = allTasks,
                        currentLanguage = preferences.language,
                        onSaveTask = { task, ctx, onResult ->
                            viewModel.saveTask(task, ctx, onResult)
                        },
                        onToggleCompleted = { task ->
                            viewModel.toggleTaskCompleted(task, context)
                        },
                        onTogglePinned = { task ->
                            viewModel.toggleTaskPinned(task)
                        },
                        onDeleteTask = { task ->
                            viewModel.deleteTask(task, context)
                        }
                    )
                }

                AppDestination.NOTES -> {
                    NotesScreen(
                        notes = allNotes,
                        searchQuery = noteSearchQuery,
                        currentLanguage = preferences.language,
                        onSearchQueryChange = { q ->
                            viewModel.setNoteSearchQuery(q)
                        },
                        onSaveNote = { note ->
                            viewModel.saveNote(note, context)
                        },
                        onTogglePinned = { note ->
                            viewModel.toggleNotePinned(note)
                        },
                        onDeleteNote = { note ->
                            viewModel.deleteNote(note, context)
                        },
                        onToggleFavorite = { note ->
                            viewModel.toggleNoteFavorite(note)
                        },
                        onOpenNoteEditor = { note ->
                            fullScreenEditingNote = note
                            isFullScreenNoteEditorOpen = true
                        }
                    )
                }

                AppDestination.EXPENSES -> {
                    ExpensesScreen(
                        expenses = allExpenses,
                        bills = allBills,
                        currentLanguage = preferences.language,
                        preferences = preferences,
                        onUpdateBasicSalary = { salary ->
                            viewModel.setBasicSalary(salary)
                        },
                        onUpdateAddedSalary = { added ->
                            viewModel.setAddedSalary(added)
                        },
                        onUpdateAddedSalaryName = { name ->
                            viewModel.setAddedSalaryName(name)
                        },
                        onUpdatePreviousMonthBalance = { balance ->
                            viewModel.setPreviousMonthBalance(balance)
                        },
                        onUpdateSalaryDay = { day ->
                            viewModel.setSalaryDayOfMonth(day)
                        },
                        onSaveExpense = { expense ->
                            viewModel.saveExpense(expense)
                        },
                        onDeleteExpense = { expense ->
                            viewModel.deleteExpense(expense)
                        },
                        onSaveBill = { bill ->
                            viewModel.saveBill(bill, context) { scheduled ->
                                if (bill.hasReminder && !scheduled) {
                                    Toast.makeText(context, strings.reminderPastTimeError, Toast.LENGTH_SHORT).show()
                                } else if (bill.hasReminder && scheduled) {
                                    Toast.makeText(context, strings.reminderScheduledSuccess, Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onDeleteBill = { bill ->
                            viewModel.deleteBill(bill, context)
                        },
                        onPayBill = { bill ->
                            viewModel.payBill(bill, context)
                        },
                        onUnpayBill = { bill ->
                            viewModel.unpayBill(bill, context)
                        },
                        onClearAllExpenses = {
                            viewModel.clearAllExpensesOnly()
                        },
                        onClearAllIncomes = {
                            viewModel.clearAllIncomesOnly()
                        }
                    )
                }

                AppDestination.CALENDAR -> {
                    CalendarScreen(
                        tasks = allTasks,
                        selectedDateMillis = selectedCalendarDate,
                        currentLanguage = preferences.language,
                        onSelectDate = { dateMillis ->
                            viewModel.setSelectedCalendarDate(dateMillis)
                        },
                        onToggleTaskCompleted = { task ->
                            viewModel.toggleTaskCompleted(task, context)
                        },
                        onSaveTask = { task, ctx, onResult ->
                            viewModel.saveTask(task, ctx, onResult)
                        }
                    )
                }

                AppDestination.SETTINGS -> {
                    SettingsScreen(
                        preferences = preferences,
                        onLanguageChange = { lang ->
                            viewModel.setLanguage(lang)
                        },
                        onThemeModeChange = { mode ->
                            viewModel.setThemeMode(mode)
                        },
                        onFontScaleModeChange = { mode ->
                            viewModel.setFontScaleMode(mode)
                        },
                        onNotificationsEnabledChange = { enabled ->
                            viewModel.setNotificationsEnabled(enabled)
                        },
                        onVibrateEnabledChange = { enabled ->
                            viewModel.setVibrateEnabled(enabled)
                        },
                        onClearAllData = { ctx, onDone ->
                            viewModel.clearAllData(ctx, onDone)
                        }
                    )
                }
            }
        }
    }

    if (showQuickAddTaskDialog || taskToEditOnHome != null) {
        val editing = taskToEditOnHome
        FullScreenTaskEditor(
            task = editing,
            currentLanguage = preferences.language,
            onDismiss = {
                showQuickAddTaskDialog = false
                taskToEditOnHome = null
            },
            onSave = { taskToSave ->
                viewModel.saveTask(taskToSave, context) { scheduled ->
                    if (taskToSave.hasReminder && !scheduled) {
                        Toast.makeText(context, strings.reminderPastTimeError, Toast.LENGTH_SHORT).show()
                    } else if (taskToSave.hasReminder && scheduled) {
                        Toast.makeText(context, strings.reminderScheduledSuccess, Toast.LENGTH_SHORT).show()
                    }
                }
                showQuickAddTaskDialog = false
                taskToEditOnHome = null
            }
        )
    }

    if (showQuickAddExpenseDialog) {
        FullScreenExpenseEditor(
            expense = null,
            currentLanguage = preferences.language,
            onDismiss = { showQuickAddExpenseDialog = false },
            onSave = { expenseToSave ->
                viewModel.saveExpense(expenseToSave)
                showQuickAddExpenseDialog = false
            }
        )
    }
}
