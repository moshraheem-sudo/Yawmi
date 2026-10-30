package com.example.ui.screens.calendar

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TaskItem
import com.example.localization.LocalAppStrings
import com.example.ui.components.EmptyState
import com.example.ui.screens.home.HomeTaskItem
import com.example.ui.screens.tasks.AddEditTaskDialog
import com.example.ui.screens.tasks.FullScreenTaskEditor
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarScreen(
    tasks: List<TaskItem>,
    selectedDateMillis: Long,
    currentLanguage: String,
    onSelectDate: (Long) -> Unit,
    onToggleTaskCompleted: (TaskItem) -> Unit,
    onSaveTask: (TaskItem, Context, (Boolean) -> Unit) -> Unit
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current

    // Calendar state for displayed Month and Year
    var currentMonthCalendar by remember {
        mutableStateOf(Calendar.getInstance().apply { timeInMillis = selectedDateMillis })
    }

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<TaskItem?>(null) }

    val locale = remember(currentLanguage) {
        if (currentLanguage == "ar") Locale("ar") else Locale.ENGLISH
    }

    val monthYearTitle = remember(currentMonthCalendar, currentLanguage) {
        val sdf = SimpleDateFormat("MMMM yyyy", locale)
        sdf.format(currentMonthCalendar.time)
    }

    val selectedDateFormatted = remember(selectedDateMillis, currentLanguage) {
        val sdf = SimpleDateFormat("EEEE، d MMMM yyyy", locale)
        sdf.format(Date(selectedDateMillis))
    }

    // Days in current displayed month
    val daysInMonthList = remember(currentMonthCalendar) {
        val cal = currentMonthCalendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1=Sun, 2=Mon...
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val days = mutableListOf<CalendarDayItem>()
        // Blank cells before first day
        val blanks = (firstDayOfWeek - Calendar.SUNDAY)
        for (i in 0 until blanks) {
            days.add(CalendarDayItem(dayNumber = 0, dateMillis = 0L, isCurrentMonth = false))
        }
        for (d in 1..maxDays) {
            cal.set(Calendar.DAY_OF_MONTH, d)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            days.add(CalendarDayItem(dayNumber = d, dateMillis = cal.timeInMillis, isCurrentMonth = true))
        }
        days
    }

    // Tasks on the selected date
    val tasksOnSelectedDate = remember(tasks, selectedDateMillis) {
        val startOfDay = selectedDateMillis
        val endOfDay = selectedDateMillis + (24 * 60 * 60 * 1000L) - 1
        tasks.filter { task ->
            task.dueDate != null && task.dueDate in startOfDay..endOfDay
        }
    }

    // Days in month that have tasks (to show dot indicator)
    val taskDatesSet = remember(tasks) {
        tasks.mapNotNull { it.dueDate }.map { due ->
            val c = Calendar.getInstance().apply {
                timeInMillis = due
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            c.timeInMillis
        }.toSet()
    }

    val dayNames = remember(currentLanguage) {
        if (currentLanguage == "ar") {
            listOf("أحد", "إثنين", "ثلاثاء", "أربعاء", "خميس", "جمعة", "سبت")
        } else {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("calendar_screen_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Month Navigation Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header row: Previous Month, Month Title, Next Month
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                val cal = currentMonthCalendar.clone() as Calendar
                                cal.add(Calendar.MONTH, -1)
                                currentMonthCalendar = cal
                            },
                            modifier = Modifier.testTag("calendar_prev_month")
                        ) {
                            Icon(
                                imageVector = if (currentLanguage == "ar") Icons.AutoMirrored.Filled.ArrowForward else Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous Month"
                            )
                        }

                        Text(
                            text = monthYearTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        IconButton(
                            onClick = {
                                val cal = currentMonthCalendar.clone() as Calendar
                                cal.add(Calendar.MONTH, 1)
                                currentMonthCalendar = cal
                            },
                            modifier = Modifier.testTag("calendar_next_month")
                        ) {
                            Icon(
                                imageVector = if (currentLanguage == "ar") Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next Month"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Day of week headers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        dayNames.forEach { name ->
                            Text(
                                text = name,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Grid of Days (rows of 7)
                    val chunkedWeeks = daysInMonthList.chunked(7)
                    chunkedWeeks.forEach { week ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            week.forEach { dayItem ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (dayItem.isCurrentMonth) {
                                        val isSelected = isSameDay(dayItem.dateMillis, selectedDateMillis)
                                        val hasTasks = taskDatesSet.contains(dayItem.dateMillis)

                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isSelected) MaterialTheme.colorScheme.primary
                                                    else Color.Transparent
                                                )
                                                .clickable { onSelectDate(dayItem.dateMillis) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = "${dayItem.dayNumber}",
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                                    else MaterialTheme.colorScheme.onSurface
                                                )
                                                if (hasTasks) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.dp)
                                                            .clip(CircleShape)
                                                            .background(
                                                                if (isSelected) MaterialTheme.colorScheme.onPrimary
                                                                else MaterialTheme.colorScheme.primary
                                                            )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            // Fill remaining slots if row < 7
                            for (pad in 0 until (7 - week.size)) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // Selected Date Header and Add Task Action
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.calendarTasksOnDay,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = selectedDateFormatted,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = { showAddTaskDialog = true },
                    modifier = Modifier.testTag("calendar_add_task_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = strings.actionAddTask, fontSize = 10.5.sp)
                }
            }
        }

        // List of tasks for this day
        if (tasksOnSelectedDate.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = strings.noTasksForSelectedDay,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        } else {
            items(tasksOnSelectedDate, key = { it.id }) { task ->
                HomeTaskItem(
                    task = task,
                    onToggleComplete = { onToggleTaskCompleted(task) },
                    onEdit = { taskToEdit = task }
                )
            }
        }
    }

    if (showAddTaskDialog || taskToEdit != null) {
        FullScreenTaskEditor(
            task = taskToEdit,
            currentLanguage = currentLanguage,
            initialDueDate = selectedDateMillis,
            onDismiss = {
                showAddTaskDialog = false
                taskToEdit = null
            },
            onSave = { savedTask ->
                onSaveTask(savedTask, context) {}
                showAddTaskDialog = false
                taskToEdit = null
            }
        )
    }
}

data class CalendarDayItem(
    val dayNumber: Int,
    val dateMillis: Long,
    val isCurrentMonth: Boolean
)

private fun isSameDay(d1: Long, d2: Long): Boolean {
    val c1 = Calendar.getInstance().apply { timeInMillis = d1 }
    val c2 = Calendar.getInstance().apply { timeInMillis = d2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}
