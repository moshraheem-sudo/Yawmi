package com.example.ui.screens.expenses

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.School
import com.example.localization.AppStrings
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ExpenseCategory
import com.example.data.local.entity.ExpenseItem
import com.example.localization.LocalAppStrings
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenExpenseEditor(
    expense: ExpenseItem?,
    currentLanguage: String,
    onDismiss: () -> Unit,
    onSave: (ExpenseItem) -> Unit
) {
    val strings = LocalAppStrings.current
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    var title by remember { mutableStateOf(expense?.title ?: "") }
    var amountText by remember {
        mutableStateOf(if (expense != null) expense.amount.toLong().toString() else "")
    }
    var category by remember { mutableStateOf(expense?.category ?: ExpenseCategory.FOOD) }
    var date by remember { mutableLongStateOf(expense?.date ?: System.currentTimeMillis()) }
    var note by remember { mutableStateOf(expense?.note ?: "") }

    var titleError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Smart horizontal swipe gesture support (left or right to dismiss)
    val animOffsetX = remember { Animatable(0f) }
    val dismissThresholdPx = with(density) { 110.dp.toPx() }

    fun handleSave() {
        val parsedAmount = amountText.toDoubleOrNull()
        var hasError = false
        if (title.isBlank()) {
            titleError = true
            hasError = true
        }
        if (parsedAmount == null || parsedAmount <= 0) {
            amountError = true
            hasError = true
        }
        if (hasError) return

        val itemToSave = expense?.copy(
            title = title.trim(),
            amount = parsedAmount!!,
            category = category,
            date = date,
            note = note.trim()
        ) ?: ExpenseItem(
            title = title.trim(),
            amount = parsedAmount!!,
            category = category,
            date = date,
            note = note.trim()
        )
        onSave(itemToSave)
    }

    BackHandler {
        onDismiss()
    }

    val dateFormatted = remember(date, currentLanguage) {
        val locale = if (currentLanguage == "ar") Locale("ar") else Locale.ENGLISH
        SimpleDateFormat("EEEE، d MMMM yyyy", locale).format(Date(date))
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
                                onDismiss()
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
                            text = if (expense == null) strings.dialogAddExpenseTitle else strings.dialogEditExpenseTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (currentLanguage == "ar") "اسحب يميناً أو يساراً للرجوع" else "Swipe left/right to dismiss",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 9.5.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("expense_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (currentLanguage == "ar") "رجوع" else "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = { handleSave() },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("expense_top_save_button"),
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
            // Drag gesture indicator hint
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

            // 1. Amount Card (Hero input)
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${strings.expenseAmountLabel} (${strings.currencyUnit})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (amountText.isNotEmpty()) {
                            TextButton(
                                onClick = { amountText = "" },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (currentLanguage == "ar") "مسح" else "Clear",
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = {
                            amountText = it.filter { char -> char.isDigit() }
                            if (it.isNotBlank()) amountError = false
                        },
                        visualTransformation = com.example.util.ThousandsSeparatorVisualTransformation(),
                        placeholder = {
                            Text(
                                text = "0",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        },
                        trailingIcon = {
                            Text(
                                text = strings.currencyUnit,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        },
                        isError = amountError,
                        supportingText = if (amountError) {
                            { Text(strings.validationInvalidAmount, fontSize = 10.5.sp) }
                        } else null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 19.sp
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("full_expense_amount_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    // Live comma & Arabic words denomination preview to clarify zeros
                    val parsedAmt = amountText.toLongOrNull() ?: 0L
                    if (parsedAmt > 0L) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (currentLanguage == "ar") "المبلغ المكتوب:" else "Formatted:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${com.example.util.CurrencyFormatterUtils.format(parsedAmt)} ${strings.currencyUnit}  (${com.example.util.CurrencyFormatterUtils.toArabicDenomination(parsedAmt)})",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Quick increment chips (+1,000, +5,000, +10,000, +25,000, +50,000)
                    Text(
                        text = if (currentLanguage == "ar") "إضافة سريعة للمبلغ:" else "Quick amount shortcuts:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1000L, 5000L, 10000L, 25000L, 50000L).forEach { addAmount ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                modifier = Modifier.clickable {
                                    val currentVal = amountText.toLongOrNull() ?: 0L
                                    amountText = (currentVal + addAmount).toString()
                                    amountError = false
                                }
                            ) {
                                Text(
                                    text = "+%,d".format(Locale.ENGLISH, addAmount),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // 2. Details Card (Title & Category)
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
                        text = if (currentLanguage == "ar") "بيان وتصنيف المصروف" else "Expense Details & Category",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            if (it.isNotBlank()) titleError = false
                        },
                        label = { Text(strings.expenseTitleLabel, fontSize = 12.sp) },
                        placeholder = { Text(strings.expenseTitlePlaceholder, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
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
                            .testTag("full_expense_title_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Category selection (Unified Single Card with DropdownMenu)
                    var showExpenseCategoryDropdown by remember { mutableStateOf(false) }
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = strings.expenseCategoryLabel,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { showExpenseCategoryDropdown = true }
                                    .testTag("full_expense_category_box")
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
                                            imageVector = getExpenseCategoryIcon(category),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = getExpenseCategoryName(category, strings),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = if (currentLanguage == "ar") "اختر التصنيف" else "Select",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Icon(
                                            imageVector = if (showExpenseCategoryDropdown) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            DropdownMenu(
                                expanded = showExpenseCategoryDropdown,
                                onDismissRequest = { showExpenseCategoryDropdown = false },
                                modifier = Modifier
                                    .width(250.dp)
                                    .heightIn(max = 310.dp)
                            ) {
                                ExpenseCategory.values().forEach { cat ->
                                    val isSelected = category == cat
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(
                                                imageVector = getExpenseCategoryIcon(cat),
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
                                                    text = getExpenseCategoryName(cat, strings),
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
                                            category = cat
                                            showExpenseCategoryDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Date & Extra Notes Card
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
                        text = if (currentLanguage == "ar") "التاريخ والملاحظات" else "Date & Additional Notes",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Date picker field
                    OutlinedTextField(
                        value = dateFormatted,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(strings.expenseDateLabel, fontSize = 12.sp) },
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

                    // Quick date buttons: Today / Yesterday
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val isToday = isSameDay(date, System.currentTimeMillis())
                        val yesterdayMillis = System.currentTimeMillis() - 86400000L
                        val isYesterday = isSameDay(date, yesterdayMillis)

                        FilterChip(
                            selected = isToday,
                            onClick = { date = System.currentTimeMillis() },
                            label = { Text(if (currentLanguage == "ar") "اليوم" else "Today", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            selected = isYesterday,
                            onClick = { date = yesterdayMillis },
                            label = { Text(if (currentLanguage == "ar") "أمس" else "Yesterday", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Optional extra note
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text(strings.expenseNoteLabel, fontSize = 12.sp) },
                        placeholder = {
                            Text(
                                if (currentLanguage == "ar") "مكان الشراء أو تفاصيل الفاتورة..." else "Store name, receipt notes...",
                                fontSize = 11.5.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Primary Bottom Save Button
            Button(
                onClick = { handleSave() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("full_expense_bottom_save_button"),
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
                    text = if (expense == null) strings.dialogAddExpenseTitle else strings.btnSave,
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
            initialSelectedDateMillis = date
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            date = it
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
}

private fun isSameDay(d1: Long, d2: Long): Boolean {
    val c1 = Calendar.getInstance().apply { timeInMillis = d1 }
    val c2 = Calendar.getInstance().apply { timeInMillis = d2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}

fun getExpenseCategoryIcon(category: ExpenseCategory): androidx.compose.ui.graphics.vector.ImageVector {
    return when (category) {
        ExpenseCategory.CAR_FARE -> Icons.Default.DirectionsCar
        ExpenseCategory.HOUSEHOLD -> Icons.Default.Receipt
        ExpenseCategory.MEDICINE -> Icons.Default.LocalPharmacy
        ExpenseCategory.STUDY_FEES -> Icons.Default.School
        ExpenseCategory.SHOPPING -> Icons.Default.ShoppingCart
        ExpenseCategory.RESTAURANTS -> Icons.Default.LocalDining
        ExpenseCategory.MARKETS -> Icons.Default.ShoppingCart
        ExpenseCategory.TRAVEL -> Icons.Default.DirectionsCar
        ExpenseCategory.DELIVERY -> Icons.Default.DirectionsCar
        ExpenseCategory.GIFT -> Icons.Default.Payments
        ExpenseCategory.DONATION -> Icons.Default.Payments
        ExpenseCategory.PARENTS_RIGHTS -> Icons.Default.Payments
        ExpenseCategory.FAMILY_EXPENSE -> Icons.Default.Payments
        ExpenseCategory.FOOD -> Icons.Default.LocalDining
        ExpenseCategory.TRANSPORT -> Icons.Default.DirectionsCar
        ExpenseCategory.BILLS -> Icons.Default.Receipt
        ExpenseCategory.HEALTH -> Icons.Default.LocalPharmacy
        ExpenseCategory.ENTERTAINMENT -> Icons.Default.Movie
        ExpenseCategory.OTHER -> Icons.Default.MoreHoriz
    }
}

fun getExpenseCategoryName(category: ExpenseCategory, strings: AppStrings): String {
    val isAr = strings.appName == "يومي"
    return when (category) {
        ExpenseCategory.CAR_FARE -> if (isAr) "أجرة سيارة" else "Car / Taxi Fare"
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
        ExpenseCategory.PARENTS_RIGHTS -> if (isAr) "حقوق الوالدين" else "Parents' Support"
        ExpenseCategory.FAMILY_EXPENSE -> if (isAr) "مصروف الأهل" else "Family Expense"
        ExpenseCategory.FOOD -> strings.expCategoryFood
        ExpenseCategory.TRANSPORT -> strings.expCategoryTransport
        ExpenseCategory.BILLS -> strings.expCategoryBills
        ExpenseCategory.HEALTH -> strings.expCategoryHealth
        ExpenseCategory.ENTERTAINMENT -> strings.expCategoryEntertainment
        ExpenseCategory.OTHER -> strings.expCategoryOther
    }
}
