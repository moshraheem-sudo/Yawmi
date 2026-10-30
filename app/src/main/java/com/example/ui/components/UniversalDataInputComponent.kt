package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.ExpenseCategory
import com.example.data.local.entity.TaskCategory
import com.example.data.local.entity.TaskPriority
import com.example.localization.LocalAppStrings
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityLow
import com.example.ui.theme.PriorityMedium

/**
 * Universal category selector for adding new items.
 */
enum class InputEntryCategory {
    TASK,
    NOTE,
    EXPENSE
}

/**
 * Type-safe result wrapper emitted when new data is added through the reusable component.
 */
sealed interface NewDataEntry {
    data class Task(
        val title: String,
        val description: String,
        val priority: TaskPriority = TaskPriority.MEDIUM,
        val category: TaskCategory = TaskCategory.PERSONAL
    ) : NewDataEntry

    data class Note(
        val title: String,
        val content: String,
        val colorHex: String = "#131C31"
    ) : NewDataEntry

    data class Expense(
        val title: String,
        val amount: Double,
        val note: String,
        val category: ExpenseCategory = ExpenseCategory.OTHER
    ) : NewDataEntry
}

/**
 * A versatile, reusable input component with fields for title, description,
 * and category (Task, Note, or Expense) allowing users to swiftly add new data.
 */
@Composable
fun UniversalDataInputComponent(
    modifier: Modifier = Modifier,
    initialCategory: InputEntryCategory = InputEntryCategory.TASK,
    onAddEntry: (NewDataEntry) -> Unit,
    onCancel: (() -> Unit)? = null,
    showCancelButton: Boolean = false,
    headerTitle: String? = null,
    headerSubtitle: String? = null
) {
    val strings = LocalAppStrings.current

    var selectedCategory by remember(initialCategory) { mutableStateOf(initialCategory) }
    var title by remember { mutableStateOf("") }
    var descriptionValue by remember { mutableStateOf(TextFieldValue("")) }
    val description = descriptionValue.text
    var titleError by remember { mutableStateOf<String?>(null) }

    // Task-specific contextual state
    var taskPriority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var taskCategory by remember { mutableStateOf(TaskCategory.PERSONAL) }

    // Note-specific contextual state
    var noteColorHex by remember { mutableStateOf("#131C31") }

    // Expense-specific contextual state
    var expenseAmountText by remember { mutableStateOf("") }
    var expenseCategory by remember { mutableStateOf(ExpenseCategory.FOOD) }
    var amountError by remember { mutableStateOf<String?>(null) }

    val noteColors = remember {
        listOf(
            "#131C31" to "ليلي ملكي",
            "#7C3AED" to "بنفسجي",
            "#059669" to "أخضر طبيعي",
            "#8D6E63" to "ترابي دافئ",
            "#2563EB" to "أزرق نيلي",
            "#DC2626" to "أحمر مرجاني",
            "#D97706" to "عنبري",
            "#FFFFFF" to "أبيض ناصع",
            "#000000" to "أسود حالك"
        )
    }

    fun resetFields() {
        title = ""
        descriptionValue = TextFieldValue("")
        titleError = null
        expenseAmountText = ""
        amountError = null
    }

    fun formatDescription(prefix: String, suffix: String = "") {
        val text = descriptionValue.text
        val sel = descriptionValue.selection
        val start = sel.min
        val end = sel.max
        val hasSel = start != end && start >= 0 && end <= text.length
        if (hasSel) {
            val selText = text.substring(start, end)
            val newText = text.replaceRange(start, end, "$prefix$selText$suffix")
            descriptionValue = TextFieldValue(newText, TextRange(start + prefix.length, end + prefix.length))
        } else {
            val newText = text.replaceRange(start, start, "$prefix$suffix")
            descriptionValue = TextFieldValue(newText, TextRange(start + prefix.length, start + prefix.length))
        }
    }

    fun insertListPrefix(prefix: String) {
        val text = descriptionValue.text
        val sel = descriptionValue.selection
        val start = sel.min
        val end = sel.max
        val hasSel = start != end && start >= 0 && end <= text.length
        if (hasSel) {
            val selText = text.substring(start, end)
            val formatted = selText.lines().joinToString("\n") { line -> if (line.startsWith(prefix.trim())) line else "$prefix$line" }
            val newText = text.replaceRange(start, end, formatted)
            descriptionValue = TextFieldValue(newText, TextRange(start + formatted.length, start + formatted.length))
        } else {
            val actualPrefix = if (start == 0 || (start > 0 && text.getOrNull(start - 1) == '\n')) prefix else "\n$prefix"
            val newText = text.replaceRange(start, start, actualPrefix)
            descriptionValue = TextFieldValue(newText, TextRange(start + actualPrefix.length, start + actualPrefix.length))
        }
    }

    fun submit() {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isBlank()) {
            titleError = strings.validationRequiredField
            return
        }
        titleError = null

        when (selectedCategory) {
            InputEntryCategory.TASK -> {
                onAddEntry(
                    NewDataEntry.Task(
                        title = trimmedTitle,
                        description = description.trim(),
                        priority = taskPriority,
                        category = taskCategory
                    )
                )
                resetFields()
            }
            InputEntryCategory.NOTE -> {
                onAddEntry(
                    NewDataEntry.Note(
                        title = trimmedTitle,
                        content = description.trim(),
                        colorHex = noteColorHex
                    )
                )
                resetFields()
            }
            InputEntryCategory.EXPENSE -> {
                val parsedAmount = expenseAmountText.trim().toDoubleOrNull()
                if (parsedAmount == null || parsedAmount <= 0.0) {
                    amountError = strings.validationInvalidAmount
                    return
                }
                amountError = null
                onAddEntry(
                    NewDataEntry.Expense(
                        title = trimmedTitle,
                        amount = parsedAmount,
                        note = description.trim(),
                        category = expenseCategory
                    )
                )
                resetFields()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Optional Header
        if (headerTitle != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = headerTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (headerSubtitle != null) {
                        Text(
                            text = headerSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (onCancel != null && showCancelButton) {
                    IconButton(
                        onClick = onCancel,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = strings.btnCancel,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 1. Category Selector (Task, Note, Expense)
        Text(
            text = strings.entryCategoryLabel,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategorySegmentButton(
                title = strings.categoryTask,
                icon = Icons.Default.CheckCircle,
                isSelected = selectedCategory == InputEntryCategory.TASK,
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .weight(1f)
                    .testTag("universal_category_task"),
                onClick = {
                    selectedCategory = InputEntryCategory.TASK
                    titleError = null
                }
            )

            CategorySegmentButton(
                title = strings.categoryNote,
                icon = Icons.Default.Description,
                isSelected = selectedCategory == InputEntryCategory.NOTE,
                selectedContainerColor = Color(0xFF7C3AED),
                selectedContentColor = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .testTag("universal_category_note"),
                onClick = {
                    selectedCategory = InputEntryCategory.NOTE
                    titleError = null
                }
            )

            CategorySegmentButton(
                title = strings.categoryExpense,
                icon = Icons.Default.Payments,
                isSelected = selectedCategory == InputEntryCategory.EXPENSE,
                selectedContainerColor = Color(0xFF059669),
                selectedContentColor = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .testTag("universal_category_expense"),
                onClick = {
                    selectedCategory = InputEntryCategory.EXPENSE
                    titleError = null
                }
            )
        }

        // 2. Title Field
        val titleLabel = when (selectedCategory) {
            InputEntryCategory.TASK -> strings.taskTitleLabel
            InputEntryCategory.NOTE -> strings.noteTitleLabel
            InputEntryCategory.EXPENSE -> strings.expenseTitleLabel
        }

        val titlePlaceholder = when (selectedCategory) {
            InputEntryCategory.TASK -> strings.taskTitlePlaceholder
            InputEntryCategory.NOTE -> strings.noteTitlePlaceholder
            InputEntryCategory.EXPENSE -> strings.expenseTitlePlaceholder
        }

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                if (titleError != null && it.isNotBlank()) titleError = null
            },
            label = { Text(titleLabel) },
            placeholder = { Text(titlePlaceholder, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Title,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (title.isNotEmpty()) {
                    IconButton(onClick = { title = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            isError = titleError != null,
            supportingText = {
                if (titleError != null) {
                    Text(text = titleError!!, color = MaterialTheme.colorScheme.error)
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("universal_input_title"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        // 3. Expense Amount Field (when category is Expense)
        AnimatedVisibility(
            visible = selectedCategory == InputEntryCategory.EXPENSE,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            OutlinedTextField(
                value = expenseAmountText,
                onValueChange = {
                    // Only allow numeric and decimal input
                    if (it.isEmpty() || it.matches(Regex("""^\d*\.?\d*$"""))) {
                        expenseAmountText = it
                        if (amountError != null) amountError = null
                    }
                },
                label = { Text("${strings.expenseAmountLabel} (${strings.currencyUnit})") },
                placeholder = { Text(strings.expenseAmountPlaceholder) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = null,
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    Text(
                        text = strings.currencyUnit,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF059669),
                        modifier = Modifier.padding(end = 12.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = amountError != null,
                supportingText = {
                    if (amountError != null) {
                        Text(text = amountError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("universal_input_amount"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }

        // 4. Description / Content Field
        val descLabel = when (selectedCategory) {
            InputEntryCategory.TASK -> strings.taskDescLabel
            InputEntryCategory.NOTE -> strings.noteContentLabel
            InputEntryCategory.EXPENSE -> strings.expenseNoteLabel
        }

        val descPlaceholder = when (selectedCategory) {
            InputEntryCategory.TASK -> strings.taskDescPlaceholder
            InputEntryCategory.NOTE -> strings.noteContentPlaceholder
            InputEntryCategory.EXPENSE -> strings.expenseNoteLabel
        }

        OutlinedTextField(
            value = descriptionValue,
            onValueChange = { descriptionValue = it },
            label = { Text(descLabel, style = MaterialTheme.typography.bodyLarge) },
            placeholder = { Text(descPlaceholder, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f))) },
            textStyle = MaterialTheme.typography.bodyLarge,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Subject,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            },
            minLines = 2,
            maxLines = 4,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("universal_input_description"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        // 5. Contextual Options for each Category
        when (selectedCategory) {
            InputEntryCategory.TASK -> {
                // Task Priority Row
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = strings.priorityLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PriorityChip(
                            label = strings.taskPriorityHigh,
                            priority = TaskPriority.HIGH,
                            isSelected = taskPriority == TaskPriority.HIGH,
                            color = PriorityHigh,
                            modifier = Modifier.weight(1f),
                            onClick = { taskPriority = TaskPriority.HIGH }
                        )
                        PriorityChip(
                            label = strings.taskPriorityMedium,
                            priority = TaskPriority.MEDIUM,
                            isSelected = taskPriority == TaskPriority.MEDIUM,
                            color = PriorityMedium,
                            modifier = Modifier.weight(1f),
                            onClick = { taskPriority = TaskPriority.MEDIUM }
                        )
                        PriorityChip(
                            label = strings.taskPriorityLow,
                            priority = TaskPriority.LOW,
                            isSelected = taskPriority == TaskPriority.LOW,
                            color = PriorityLow,
                            modifier = Modifier.weight(1f),
                            onClick = { taskPriority = TaskPriority.LOW }
                        )
                    }
                }
            }

            InputEntryCategory.NOTE -> {
                // Note Accent Color Row
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Palette,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = strings.noteColorLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        noteColors.forEach { (hex, name) ->
                            val parsed = runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrDefault(Color.Gray)
                            val isSelected = noteColorHex.equals(hex, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(parsed)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f),
                                        shape = CircleShape
                                    )
                                    .clickable { noteColorHex = hex },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = name,
                                        tint = if (hex == "#FFFFFF") Color.Black else Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Quick Word Typesetting Format Chips (Direct formatting without placeholder text)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { formatDescription("**", "**") },
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                        ) {
                            Text("B عريض", fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { formatDescription("*", "*") },
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                        ) {
                            Text("I مائل", fontSize = 13.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { formatDescription("<u>", "</u>") },
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                        ) {
                            Text("U تحت خط", fontSize = 13.sp, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { insertListPrefix("# ") },
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                        ) {
                            Text("H1 عنوان", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { insertListPrefix("1. ") },
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                        ) {
                            Text("1. ترقيم", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { insertListPrefix("• ") },
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                        ) {
                            Text("• تنقيط", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { insertListPrefix("- [ ] ") },
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                        ) {
                            Text("☑ مهمة", fontSize = 13.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { insertListPrefix("> ") },
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                        ) {
                            Text("❝ اقتباس", fontSize = 13.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                    }
                }
            }

            InputEntryCategory.EXPENSE -> {
                // Expense Category Chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = strings.expenseCategoryLabel,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val isAr = strings.appName == "يومي"
                        val categories = listOf(
                            ExpenseCategory.CAR_FARE to (if (isAr) "أجرة سيارة" else "Taxi"),
                            ExpenseCategory.HOUSEHOLD to (if (isAr) "مصروف بيت" else "Household"),
                            ExpenseCategory.MEDICINE to (if (isAr) "علاجات وأدوية" else "Medicines"),
                            ExpenseCategory.STUDY_FEES to (if (isAr) "أجور دراسة" else "Study Fees"),
                            ExpenseCategory.SHOPPING to strings.expCategoryShopping,
                            ExpenseCategory.RESTAURANTS to (if (isAr) "مطاعم" else "Restaurants"),
                            ExpenseCategory.MARKETS to (if (isAr) "أسواق" else "Markets"),
                            ExpenseCategory.TRAVEL to (if (isAr) "سفر" else "Travel"),
                            ExpenseCategory.DELIVERY to (if (isAr) "نقل وتوصيل" else "Delivery"),
                            ExpenseCategory.GIFT to (if (isAr) "هدية" else "Gift"),
                            ExpenseCategory.DONATION to (if (isAr) "تبرع" else "Donation"),
                            ExpenseCategory.PARENTS_RIGHTS to (if (isAr) "حقوق الوالدين" else "Parents"),
                            ExpenseCategory.FAMILY_EXPENSE to (if (isAr) "مصروف الأهل" else "Family"),
                            ExpenseCategory.BILLS to strings.expCategoryBills,
                            ExpenseCategory.OTHER to strings.expCategoryOther
                        )

                        categories.forEach { (cat, label) ->
                            FilterChip(
                                selected = expenseCategory == cat,
                                onClick = { expenseCategory = cat },
                                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 6. Action Buttons (Submit & Cancel)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showCancelButton && onCancel != null) {
                OutlinedButton(
                    onClick = {
                        resetFields()
                        onCancel()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("universal_input_cancel_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(strings.btnCancel)
                }
            }

            Button(
                onClick = { submit() },
                modifier = Modifier
                    .weight(if (showCancelButton && onCancel != null) 1.5f else 1f)
                    .height(48.dp)
                    .testTag("universal_input_submit_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when (selectedCategory) {
                        InputEntryCategory.TASK -> MaterialTheme.colorScheme.primary
                        InputEntryCategory.NOTE -> Color(0xFF7C3AED)
                        InputEntryCategory.EXPENSE -> Color(0xFF059669)
                    }
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                val submitLabel = when (selectedCategory) {
                    InputEntryCategory.TASK -> strings.actionAddTask
                    InputEntryCategory.NOTE -> strings.actionAddNote
                    InputEntryCategory.EXPENSE -> strings.actionAddExpense
                }
                Text(
                    text = submitLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

/**
 * Embedded Card variant of the universal input component, ideal for the Home screen
 * or top of list screens.
 */
@Composable
fun UniversalDataInputCard(
    modifier: Modifier = Modifier,
    initialCategory: InputEntryCategory = InputEntryCategory.TASK,
    onAddEntry: (NewDataEntry) -> Unit
) {
    val strings = LocalAppStrings.current
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var activeCategory by rememberSaveable { mutableStateOf(initialCategory) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("universal_data_input_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with title and expand/collapse toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = strings.quickEntryTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isExpanded) "اضغط للطي" else strings.quickEntrySubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Quick collapsed selector chips when folded
            if (!isExpanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                activeCategory = InputEntryCategory.TASK
                                isExpanded = true
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = strings.categoryTask,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                activeCategory = InputEntryCategory.NOTE
                                isExpanded = true
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = strings.categoryNote,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                activeCategory = InputEntryCategory.EXPENSE
                                isExpanded = true
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = strings.categoryExpense,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    UniversalDataInputComponent(
                        initialCategory = activeCategory,
                        headerTitle = null,
                        headerSubtitle = null,
                        onAddEntry = { entry ->
                            onAddEntry(entry)
                            isExpanded = false
                        },
                        showCancelButton = false
                    )
                }
            }
        }
    }
}

/**
 * Dialog variant of the universal input component, allowing it to be invoked from
 * anywhere in the app (e.g. Floating Action Buttons, AppTopBar, or shortcut menus).
 */
@Composable
fun UniversalDataInputDialog(
    initialCategory: InputEntryCategory = InputEntryCategory.TASK,
    onDismiss: () -> Unit,
    onAddEntry: (NewDataEntry) -> Unit
) {
    val strings = LocalAppStrings.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("universal_data_input_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                UniversalDataInputComponent(
                    initialCategory = initialCategory,
                    headerTitle = strings.quickEntryTitle,
                    headerSubtitle = strings.quickEntrySubtitle,
                    onAddEntry = { entry ->
                        onAddEntry(entry)
                        onDismiss()
                    },
                    onCancel = onDismiss,
                    showCancelButton = true
                )
            }
        }
    }
}

@Composable
private fun CategorySegmentButton(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    selectedContainerColor: Color,
    selectedContentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp)),
        color = if (isSelected) selectedContainerColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        contentColor = if (isSelected) selectedContentColor else MaterialTheme.colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.5.sp
            )
        }
    }
}

@Composable
private fun PriorityChip(
    label: String,
    priority: TaskPriority,
    isSelected: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(10.dp)),
        color = if (isSelected) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        shape = RoundedCornerShape(10.dp),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(1.5.dp, color)
        } else {
            null
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
