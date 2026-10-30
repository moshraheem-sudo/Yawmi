package com.example.ui.screens.tasks

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TaskCategory
import com.example.data.local.entity.TaskItem
import com.example.data.local.entity.TaskPriority
import com.example.localization.LocalAppStrings
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityLow
import com.example.ui.theme.PriorityMedium
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenTaskEditor(
    task: TaskItem?,
    currentLanguage: String,
    initialDueDate: Long? = null,
    onDismiss: () -> Unit,
    onSave: (TaskItem) -> Unit
) {
    val strings = LocalAppStrings.current
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var dueDate by remember {
        mutableLongStateOf(task?.dueDate ?: initialDueDate ?: System.currentTimeMillis())
    }
    var hasTime by remember { mutableStateOf(task?.hasTime ?: false) }
    var dueHour by remember { mutableIntStateOf(task?.dueTimeHour ?: 9) }
    var dueMinute by remember { mutableIntStateOf(task?.dueTimeMinute ?: 0) }
    var priority by remember { mutableStateOf(task?.priority ?: TaskPriority.MEDIUM) }
    var category by remember { mutableStateOf(task?.category ?: TaskCategory.GENERAL) }
    var isPinned by remember { mutableStateOf(task?.isPinned ?: false) }
    var hasReminder by remember { mutableStateOf(task?.hasReminder ?: false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }

    // Smart horizontal swipe gesture support (left or right to dismiss)
    val animOffsetX = remember { Animatable(0f) }
    val dismissThresholdPx = with(density) { 110.dp.toPx() }

    fun handleSave() {
        if (title.isBlank()) {
            titleError = true
            return
        }

        // Calculate reminder timestamp if reminder enabled
        val reminderMillis = if (hasReminder) {
            val cal = Calendar.getInstance().apply {
                timeInMillis = dueDate
                if (hasTime) {
                    set(Calendar.HOUR_OF_DAY, dueHour)
                    set(Calendar.MINUTE, dueMinute)
                    set(Calendar.SECOND, 0)
                } else {
                    set(Calendar.HOUR_OF_DAY, 9)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }
            }
            cal.timeInMillis
        } else null

        val itemToSave = task?.copy(
            title = title.trim(),
            description = description.trim(),
            dueDate = dueDate,
            hasTime = hasTime,
            dueTimeHour = if (hasTime) dueHour else null,
            dueTimeMinute = if (hasTime) dueMinute else null,
            priority = priority,
            category = category,
            isPinned = isPinned,
            hasReminder = hasReminder,
            reminderTime = reminderMillis
        ) ?: TaskItem(
            title = title.trim(),
            description = description.trim(),
            dueDate = dueDate,
            hasTime = hasTime,
            dueTimeHour = if (hasTime) dueHour else null,
            dueTimeMinute = if (hasTime) dueMinute else null,
            priority = priority,
            category = category,
            isPinned = isPinned,
            hasReminder = hasReminder,
            reminderTime = reminderMillis
        )
        onSave(itemToSave)
    }

    fun handleAutoSaveAndExit() {
        val hasContent = title.isNotBlank()
        val isModified = if (task == null) {
            hasContent
        } else {
            title.trim() != task.title ||
                description.trim() != task.description ||
                dueDate != task.dueDate ||
                hasTime != task.hasTime ||
                dueHour != (task.dueTimeHour ?: 9) ||
                dueMinute != (task.dueTimeMinute ?: 0) ||
                priority != task.priority ||
                category != task.category ||
                isPinned != task.isPinned ||
                hasReminder != task.hasReminder
        }
        if (hasContent && isModified) {
            handleSave()
        } else {
            onDismiss()
        }
    }

    BackHandler {
        handleAutoSaveAndExit()
    }

    val dateFormatted = remember(dueDate, currentLanguage) {
        val locale = if (currentLanguage == "ar") Locale("ar") else Locale.ENGLISH
        SimpleDateFormat("EEEE، d MMMM yyyy", locale).format(Date(dueDate))
    }

    val timeFormatted = remember(dueHour, dueMinute) {
        val period = if (dueHour >= 12) "PM" else "AM"
        val displayHour = if (dueHour % 12 == 0) 12 else dueHour % 12
        String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, dueMinute, period)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(animOffsetX.value.roundToInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        coroutineScope.launch {
                            if (abs(animOffsetX.value) > dismissThresholdPx) {
                                val target = if (animOffsetX.value > 0) 1500f else -1500f
                                animOffsetX.animateTo(target, tween(180))
                                handleAutoSaveAndExit()
                            } else {
                                animOffsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                            }
                        }
                    },
                    onDragCancel = {
                        coroutineScope.launch { animOffsetX.animateTo(0f) }
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            animOffsetX.snapTo(animOffsetX.value + dragAmount)
                        }
                    }
                )
            },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (task == null) strings.dialogAddTaskTitle else strings.dialogEditTaskTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (currentLanguage == "ar") "حفظ تلقائي • اسحب للرجوع" else "Auto-saves • Swipe to dismiss",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 9.5.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { handleAutoSaveAndExit() },
                        modifier = Modifier.testTag("task_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (currentLanguage == "ar") "رجوع" else "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    // Pin toggle icon
                    IconButton(
                        onClick = { isPinned = !isPinned },
                        modifier = Modifier.testTag("task_top_pin_button")
                    ) {
                        Icon(
                            imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = strings.pinTask,
                            tint = if (isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Save Button
                    Button(
                        onClick = { handleSave() },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("task_top_save_button"),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = strings.btnSave,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = strings.btnSave, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Drag gesture indicator pill
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    )
                }
            }

            // 1. Task Title & Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (currentLanguage == "ar") "معلومات المهمة" else "Task Information",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            if (it.isNotBlank()) titleError = false
                        },
                        label = { Text(strings.taskTitleLabel, fontSize = 12.sp) },
                        placeholder = { Text(strings.taskTitlePlaceholder, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Title,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        isError = titleError,
                        supportingText = if (titleError) {
                            { Text(strings.validationRequiredField, fontSize = 11.sp) }
                        } else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("full_task_title_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(strings.taskDescLabel, fontSize = 12.sp) },
                        placeholder = { Text(strings.taskDescPlaceholder, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("full_task_desc_input"),
                        minLines = 3,
                        maxLines = 6,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // 2. Date, Time & Reminder Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (currentLanguage == "ar") "الموعد والتنبيه" else "Schedule & Reminders",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Date picker trigger field
                    OutlinedTextField(
                        value = dateFormatted,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(strings.dueDateLabel, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Quick date shortcuts (Today, Tomorrow, Next Week)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val isToday = isSameDay(dueDate, System.currentTimeMillis())
                        val tomorrowMillis = System.currentTimeMillis() + 86400000L
                        val isTomorrow = isSameDay(dueDate, tomorrowMillis)
                        val nextWeekMillis = System.currentTimeMillis() + (7 * 86400000L)
                        val isNextWeek = isSameDay(dueDate, nextWeekMillis)

                        FilterChip(
                            selected = isToday,
                            onClick = { dueDate = System.currentTimeMillis() },
                            label = { Text(if (currentLanguage == "ar") "اليوم" else "Today", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            selected = isTomorrow,
                            onClick = { dueDate = tomorrowMillis },
                            label = { Text(if (currentLanguage == "ar") "غداً" else "Tomorrow", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            selected = isNextWeek,
                            onClick = { dueDate = nextWeekMillis },
                            label = { Text(if (currentLanguage == "ar") "بعد أسبوع" else "Next Week", fontSize = 11.sp) },
                            modifier = Modifier.weight(1.2f)
                        )
                    }

                    // Due Time Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = strings.dueTimeLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                if (hasTime) {
                                    Text(
                                        text = timeFormatted,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (hasTime) {
                                TextButton(onClick = { showTimePicker = true }) {
                                    Text(if (currentLanguage == "ar") "تعديل الوقت" else "Change", fontSize = 11.sp)
                                }
                            }
                            Switch(
                                checked = hasTime,
                                onCheckedChange = {
                                    hasTime = it
                                    if (it) showTimePicker = true
                                }
                            )
                        }
                    }

                    // Alarm & System Notification Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = if (hasReminder) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = strings.enableReminder,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (currentLanguage == "ar") "منبه ورنين مع إشعار للنظام" else "Alarm ring and system notification",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = hasReminder,
                            onCheckedChange = {
                                hasReminder = it
                                if (it) hasTime = true
                            }
                        )
                    }
                }
            }

            // 3. Priority & Category Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (currentLanguage == "ar") "الأولوية والتصنيف" else "Priority & Category",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Priority selection
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.priorityLabel,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TaskPriority.values().forEach { prio ->
                                val isSelected = priority == prio
                                val prioName = when (prio) {
                                    TaskPriority.HIGH -> strings.taskPriorityHigh
                                    TaskPriority.MEDIUM -> strings.taskPriorityMedium
                                    TaskPriority.LOW -> strings.taskPriorityLow
                                }
                                val prioColor = when (prio) {
                                    TaskPriority.HIGH -> PriorityHigh
                                    TaskPriority.MEDIUM -> PriorityMedium
                                    TaskPriority.LOW -> PriorityLow
                                }

                                FilterChip(
                                    selected = isSelected,
                                    onClick = { priority = prio },
                                    label = {
                                        Text(
                                            text = prioName,
                                            fontSize = 11.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    leadingIcon = {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(prioColor)
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Category selection (Unified Single Card with Dropdown Menu)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = strings.categoryLabel,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { showCategoryDropdown = true }
                                    .testTag("task_category_selector_box")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = getTaskCategoryIcon(category),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = getCategoryName(category, strings),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 13.sp
                                        )
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = if (currentLanguage == "ar") "تغيير التصنيف" else "Change",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Icon(
                                            imageVector = if (showCategoryDropdown) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            DropdownMenu(
                                expanded = showCategoryDropdown,
                                onDismissRequest = { showCategoryDropdown = false },
                                modifier = Modifier.width(240.dp)
                            ) {
                                TaskCategory.values().forEach { cat ->
                                    val isSelected = category == cat
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(
                                                imageVector = getTaskCategoryIcon(cat),
                                                contentDescription = null,
                                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        },
                                        text = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = getCategoryName(cat, strings),
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                                    fontSize = 13.sp
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
                                            category = cat
                                            showCategoryDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Pin to top toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = if (isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = strings.pinTask, style = MaterialTheme.typography.bodyMedium)
                        }

                        Switch(
                            checked = isPinned,
                            onCheckedChange = { isPinned = it }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Primary Bottom Save Button
            Button(
                onClick = { handleSave() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("full_task_bottom_save_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (task == null) strings.dialogAddTaskTitle else strings.btnSave,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Material 3 Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dueDate
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            dueDate = it
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(strings.btnConfirm)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(strings.btnCancel)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Material 3 Time Picker Dialog
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = dueHour,
            initialMinute = dueMinute,
            is24Hour = false
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text(strings.selectDueTime, fontSize = 14.sp, fontWeight = FontWeight.Bold) },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        dueHour = timePickerState.hour
                        dueMinute = timePickerState.minute
                        showTimePicker = false
                    }
                ) {
                    Text(strings.btnConfirm)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(strings.btnCancel)
                }
            }
        )
    }
}

fun getTaskCategoryIcon(category: TaskCategory): ImageVector {
    return when (category) {
        TaskCategory.GENERAL -> Icons.Default.Description
        TaskCategory.WORK -> Icons.Default.Work
        TaskCategory.PERSONAL -> Icons.Default.Person
        TaskCategory.STUDY -> Icons.Default.School
        TaskCategory.HEALTH -> Icons.Default.FitnessCenter
        TaskCategory.SHOPPING -> Icons.Default.ShoppingCart
        TaskCategory.OTHER -> Icons.Default.MoreHoriz
    }
}

private fun isSameDay(d1: Long, d2: Long): Boolean {
    val c1 = Calendar.getInstance().apply { timeInMillis = d1 }
    val c2 = Calendar.getInstance().apply { timeInMillis = d2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}
