package com.example.ui.screens.notes

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatAlignJustify
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.PushPin
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.local.entity.NoteItem
import com.example.localization.LocalAppStrings
import com.example.ui.screens.notes.audio.AudioPlayerHelper
import com.example.ui.screens.notes.audio.AudioRecorderHelper
import com.example.ui.screens.notes.pdf.NotePdfExporter
import com.example.ui.theme.CyanSkyPrimary
import com.example.ui.theme.MidnightSlateBg
import com.example.ui.theme.MidnightSlateBorder
import com.example.ui.theme.MidnightSlateHighlightBorder
import com.example.ui.theme.MidnightSlateSurface
import com.example.ui.theme.MidnightSlateSurfaceVariant
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val NoteCategoriesList = listOf("عام", "عمل", "شخصي", "دراسة", "صحة", "تسوق", "أخرى")

enum class NoteTextAlign {
    RIGHT,
    CENTER,
    LEFT,
    JUSTIFY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenNoteEditor(
    note: NoteItem?,
    currentLanguage: String,
    onDismiss: () -> Unit,
    onSave: (NoteItem) -> Unit
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isAr = currentLanguage == "ar"

    var title by remember { mutableStateOf(note?.title ?: "") }
    var contentValue by remember {
        val initialText = note?.content ?: ""
        mutableStateOf(TextFieldValue(initialText, TextRange(initialText.length)))
    }
    var selectedCategory by remember { mutableStateOf(note?.category ?: "عام") }
    var selectedColor by remember { mutableStateOf(note?.colorHex ?: "#162235") }
    var isPinned by remember { mutableStateOf(note?.isPinned ?: false) }
    var isFavorite by remember { mutableStateOf(note?.isFavorite ?: false) }

    // Formatting Toolbar States (Matching Screenshot)
    var isFormattingExpanded by remember { mutableStateOf(true) }
    var isBottomToolsExpanded by remember { mutableStateOf(true) }
    var fontSizeSp by remember { mutableIntStateOf(note?.fontSize ?: 15) }
    var isBold by remember { mutableStateOf(false) }
    var isItalic by remember { mutableStateOf(false) }
    var isUnderline by remember { mutableStateOf(false) }
    var textAlign by remember {
        val initialAlign = when (note?.textAlign) {
            "center" -> NoteTextAlign.CENTER
            "left" -> NoteTextAlign.LEFT
            "justify" -> NoteTextAlign.JUSTIFY
            else -> NoteTextAlign.RIGHT
        }
        mutableStateOf(initialAlign)
    }

    // Undo / Redo history
    val undoStack = remember { mutableStateListOf<TextFieldValue>() }
    val redoStack = remember { mutableStateListOf<TextFieldValue>() }

    fun updateContentWithHistory(newValue: TextFieldValue) {
        if (newValue.text != contentValue.text) {
            undoStack.add(contentValue)
            redoStack.clear()
            contentValue = newValue
        } else {
            contentValue = newValue
        }
    }

    // Media & Attachments
    var attachedImagePath by remember { mutableStateOf(note?.imagePath) }
    var attachedPdfPath by remember { mutableStateOf(note?.pdfPath) }
    var attachedPdfName by remember { mutableStateOf(note?.pdfName) }
    var attachedAudioPath by remember { mutableStateOf(note?.audioPath) }
    var audioDurationMs by remember { mutableStateOf(note?.audioDurationMs ?: 0L) }
    var showFullImageDialog by remember { mutableStateOf(false) }

    // Reminder
    var hasReminder by remember { mutableStateOf(note?.hasReminder ?: false) }
    var reminderTime by remember { mutableStateOf(note?.reminderTime) }
    var showReminderPicker by remember { mutableStateOf(false) }
    var showFontSizeDialog by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }

    // Audio Recorder
    val audioRecorder = remember { AudioRecorderHelper(context) }
    var isRecordingAudio by remember { mutableStateOf(false) }
    var recordingSeconds by remember { mutableIntStateOf(0) }

    // Audio Player
    var isPlayingAudio by remember { mutableStateOf(false) }
    val audioPlayer = remember {
        AudioPlayerHelper(onCompletion = { isPlayingAudio = false })
    }

    DisposableEffect(Unit) {
        onDispose {
            if (isRecordingAudio) {
                audioRecorder.cancelRecording()
            }
            audioPlayer.stop()
        }
    }

    // Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val imagesDir = File(context.filesDir, "note_images").apply { mkdirs() }
                    val imageFile = File(imagesDir, "img_${System.currentTimeMillis()}.jpg")
                    val outputStream = FileOutputStream(imageFile)
                    inputStream?.copyTo(outputStream)
                    inputStream?.close()
                    outputStream.close()
                    attachedImagePath = imageFile.absolutePath
                    Toast.makeText(context, if (isAr) "تم إرفاق الصورة بنجاح" else "Image attached", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, if (isAr) "فشل إرفاق الصورة" else "Failed to attach image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Audio Permission
    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            audioRecorder.startRecording(
                onSuccess = {
                    isRecordingAudio = true
                    recordingSeconds = 0
                },
                onError = {
                    isRecordingAudio = false
                    Toast.makeText(context, if (isAr) "فشل بدء التسجيل الصوتي" else "Failed to start recording", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Toast.makeText(context, if (isAr) "مطلوب إذن الميكروفون لتسجيل الصوت" else "Microphone permission required", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(isRecordingAudio) {
        if (isRecordingAudio) {
            while (isRecordingAudio) {
                delay(1000L)
                recordingSeconds++
            }
        }
    }

    fun buildNoteToSave(): NoteItem? {
        val trimmedTitle = title.trim()
        val trimmedContent = contentValue.text.trim()
        if (trimmedTitle.isBlank() && trimmedContent.isBlank() && attachedImagePath == null && attachedAudioPath == null) {
            return null
        }
        val finalTitle = if (trimmedTitle.isBlank()) {
            val firstLine = trimmedContent.lines().firstOrNull() ?: ""
            if (firstLine.length > 30) firstLine.take(30) + "..." else firstLine.ifBlank { if (isAr) "ملاحظة" else "Note" }
        } else {
            trimmedTitle
        }

        val textAlignStr = when (textAlign) {
            NoteTextAlign.CENTER -> "center"
            NoteTextAlign.LEFT -> "left"
            NoteTextAlign.JUSTIFY -> "justify"
            NoteTextAlign.RIGHT -> "right"
        }

        return NoteItem(
            id = note?.id ?: 0L,
            title = finalTitle,
            content = contentValue.text,
            colorHex = selectedColor,
            isPinned = isPinned,
            category = selectedCategory,
            isFavorite = isFavorite,
            fontSize = fontSizeSp,
            textAlign = textAlignStr,
            imagePath = attachedImagePath,
            pdfPath = attachedPdfPath,
            pdfName = attachedPdfName,
            audioPath = attachedAudioPath,
            audioDurationMs = audioDurationMs,
            hasReminder = hasReminder,
            reminderTime = reminderTime,
            createdAt = note?.createdAt ?: System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }

    fun handleAutoSaveAndExit() {
        val candidate = buildNoteToSave()
        if (candidate != null) {
            val hasChanged = note == null ||
                    note.title != candidate.title ||
                    note.content != candidate.content ||
                    note.category != candidate.category ||
                    note.isFavorite != candidate.isFavorite ||
                    note.isPinned != candidate.isPinned ||
                    note.fontSize != candidate.fontSize ||
                    note.textAlign != candidate.textAlign ||
                    note.imagePath != candidate.imagePath ||
                    note.audioPath != candidate.audioPath ||
                    note.hasReminder != candidate.hasReminder ||
                    note.reminderTime != candidate.reminderTime
            if (hasChanged) {
                onSave(candidate)
                return
            }
        }
        onDismiss()
    }

    BackHandler {
        handleAutoSaveAndExit()
    }

    // Text Insertion Utilities
    fun insertPrefixToCurrentLine(prefix: String) {
        val currentText = contentValue.text
        val selectionStart = contentValue.selection.start.coerceIn(0, currentText.length)
        val lineStart = currentText.lastIndexOf('\n', (selectionStart - 1).coerceAtLeast(0)).let {
            if (it == -1) 0 else it + 1
        }
        val newText = StringBuilder(currentText).insert(lineStart, prefix).toString()
        val newCursor = selectionStart + prefix.length
        updateContentWithHistory(TextFieldValue(newText, TextRange(newCursor)))
    }

    fun wrapSelectionWith(wrapper: String) {
        val currentText = contentValue.text
        val start = contentValue.selection.min
        val end = contentValue.selection.max
        if (start == end) {
            val newText = StringBuilder(currentText).insert(start, "$wrapper$wrapper").toString()
            updateContentWithHistory(TextFieldValue(newText, TextRange(start + wrapper.length)))
        } else {
            val selected = currentText.substring(start, end)
            val newText = currentText.substring(0, start) + wrapper + selected + wrapper + currentText.substring(end)
            updateContentWithHistory(TextFieldValue(newText, TextRange(end + wrapper.length * 2)))
        }
    }

    // Arabic Kashida (تطويل / ربط من الجانبين) utility
    fun applyOrToggleKashida() {
        textAlign = NoteTextAlign.JUSTIFY
        val currentText = contentValue.text
        if (currentText.isBlank()) return
        val nonConnecting = setOf('ا', 'أ', 'إ', 'آ', 'د', 'ذ', 'ر', 'ز', 'و', 'ؤ', 'ء', 'ة', 'ى')
        val start = contentValue.selection.min.coerceIn(0, currentText.length)
        val end = contentValue.selection.max.coerceIn(0, currentText.length)
        val targetRange = if (start != end) start until end else 0 until currentText.length
        val segment = currentText.substring(targetRange)

        val transformed = if (segment.contains('ـ')) {
            // Remove existing Kashida tatweel if already applied
            segment.replace("ـ", "")
        } else {
            // Insert Kashida tatweel in eligible Arabic words for balanced two-sided stretching
            segment.split(" ").joinToString(" ") { word ->
                if (word.length >= 4 && word.any { it in '\u0621'..'\u064A' }) {
                    val sb = StringBuilder()
                    var inserted = false
                    for (i in word.indices) {
                        val ch = word[i]
                        sb.append(ch)
                        if (!inserted && i in 1 until word.length - 1 &&
                            ch in '\u0621'..'\u064A' && ch !in nonConnecting &&
                            word[i + 1] in '\u0621'..'\u064A'
                        ) {
                            sb.append("ــ")
                            inserted = true
                        }
                    }
                    sb.toString()
                } else {
                    word
                }
            }
        }

        val newFullText = currentText.substring(0, targetRange.first) + transformed + currentText.substring(targetRange.last + 1)
        updateContentWithHistory(TextFieldValue(newFullText, TextRange(newFullText.length.coerceAtLeast(0))))
    }

    val bgColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val borderColor = MaterialTheme.colorScheme.outline
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant

    Scaffold(
        containerColor = bgColor,
        topBar = {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Save Button
                    Button(
                        onClick = {
                            val trimmedTitle = title.trim()
                            val trimmedContent = contentValue.text.trim()
                            if (trimmedTitle.isBlank() && trimmedContent.isBlank()) {
                                Toast.makeText(context, strings.validationRequiredField, Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val finalTitle = if (trimmedTitle.isBlank()) {
                                val firstLine = trimmedContent.lines().firstOrNull() ?: ""
                                if (firstLine.length > 30) firstLine.take(30) + "..." else firstLine
                            } else {
                                trimmedTitle
                            }

                            val textAlignStr = when (textAlign) {
                                NoteTextAlign.CENTER -> "center"
                                NoteTextAlign.LEFT -> "left"
                                NoteTextAlign.JUSTIFY -> "justify"
                                NoteTextAlign.RIGHT -> "right"
                            }

                            val noteToSave = NoteItem(
                                id = note?.id ?: 0L,
                                title = finalTitle,
                                content = contentValue.text,
                                colorHex = selectedColor,
                                isPinned = isPinned,
                                category = selectedCategory,
                                isFavorite = isFavorite,
                                fontSize = fontSizeSp,
                                textAlign = textAlignStr,
                                imagePath = attachedImagePath,
                                pdfPath = attachedPdfPath,
                                pdfName = attachedPdfName,
                                audioPath = attachedAudioPath,
                                audioDurationMs = audioDurationMs,
                                hasReminder = hasReminder,
                                reminderTime = reminderTime,
                                createdAt = note?.createdAt ?: System.currentTimeMillis(),
                                updatedAt = System.currentTimeMillis()
                            )
                            onSave(noteToSave)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                            contentColor = onPrimaryColor
                        ),
                        shape = RoundedCornerShape(24.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("note_editor_save_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "حفظ" else "Save",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    // Favorite Toggle Pill Button (زر مفضلة ★)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isFavorite) Color(0xFFF59E0B).copy(alpha = 0.2f) else surfaceColor,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isFavorite) Color(0xFFF59E0B) else borderColor
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { isFavorite = !isFavorite }
                            .testTag("note_editor_favorite_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) Color(0xFFF59E0B) else onSurfaceVariantColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAr) "مفضلة" else "Fav",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isFavorite) Color(0xFFF59E0B) else onSurfaceColor
                            )
                        }
                    }
                }

                // Title: ملاحظة جديدة
                Text(
                    text = if (note == null) (if (isAr) "ملاحظة جديدة" else "New Note") else (if (isAr) "تعديل الملاحظة" else "Edit Note"),
                    color = onSurfaceColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("note_editor_title")
                )

                // Back Arrow (Auto-saves if non-empty)
                IconButton(
                    onClick = { handleAutoSaveAndExit() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = onSurfaceColor
                    )
                }
            }
        },
        bottomBar = {
            // Bottom Tools Dock with navigationBarsPadding so it never overlaps system navigation buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!isBottomToolsExpanded) {
                    // Collapsed tools button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(surfaceColor)
                            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                            .clickable { isBottomToolsExpanded = true }
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(surfaceVariantColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Tools",
                                tint = onSurfaceColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isAr) "الأدوات" else "Tools",
                            color = onSurfaceColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    // Expanded tools dock
                    Surface(
                        color = surfaceColor,
                        shape = RoundedCornerShape(26.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                        shadowElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 1. تنسيق (Format / Ribbon toggle)
                            ToolDockItem(
                                icon = Icons.Default.FormatColorText,
                                label = if (isAr) "تنسيق" else "Format",
                                isSelected = isFormattingExpanded,
                                onClick = {
                                    isFormattingExpanded = !isFormattingExpanded
                                }
                            )

                            // 2. منبه (Reminder / Alarm)
                            ToolDockItem(
                                icon = if (hasReminder) Icons.Default.Alarm else Icons.Default.Notifications,
                                label = if (isAr) "منبه" else "Alarm",
                                isSelected = hasReminder,
                                onClick = { showReminderPicker = true }
                            )

                            // 3. صورة (Image)
                            ToolDockItem(
                                icon = Icons.Default.Image,
                                label = if (isAr) "صورة" else "Image",
                                isSelected = attachedImagePath != null,
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            )

                            // 4. صوت (Voice Audio)
                            ToolDockItem(
                                icon = if (isRecordingAudio) Icons.Default.Stop else Icons.Default.Mic,
                                label = if (isRecordingAudio) "${recordingSeconds}s" else (if (isAr) "صوت" else "Voice"),
                                isSelected = isRecordingAudio || attachedAudioPath != null,
                                onClick = {
                                    if (isRecordingAudio) {
                                        val recordedFile = audioRecorder.stopRecording()
                                        isRecordingAudio = false
                                        if (recordedFile != null) {
                                            attachedAudioPath = recordedFile.absolutePath
                                            audioDurationMs = recordingSeconds * 1000L
                                            Toast.makeText(context, if (isAr) "تم حفظ التسجيل الصوتي" else "Voice note saved", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                            )

                            // 5. مهام (Checklist / Tasks)
                            ToolDockItem(
                                icon = Icons.Default.CheckBox,
                                label = if (isAr) "مهام" else "Tasks",
                                isSelected = false,
                                onClick = {
                                    insertPrefixToCurrentLine("☑ ")
                                }
                            )

                            // 6. طي الأدوات (Collapse tools dock)
                            ToolDockItem(
                                icon = Icons.Default.KeyboardArrowDown,
                                label = if (isAr) "طي" else "Hide",
                                isSelected = false,
                                onClick = {
                                    isBottomToolsExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Unified Single Category Selector Box (مربع واحد للتصنيف يفتح قائمة منبثقة)
            Box(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    color = surfaceColor,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, primaryColor.copy(alpha = 0.65f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showCategoryMenu = true }
                        .testTag("note_category_selector_box")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 11.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isAr) "تصنيف الملاحظة:" else "Note Category:",
                                color = onSurfaceVariantColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = primaryColor.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, primaryColor)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectedCategory,
                                        color = onSurfaceColor,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = primaryColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isAr) "تغيير" else "Change",
                                color = primaryColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = if (showCategoryMenu) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                DropdownMenu(
                    expanded = showCategoryMenu,
                    onDismissRequest = { showCategoryMenu = false },
                    modifier = Modifier
                        .background(surfaceColor)
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                ) {
                    NoteCategoriesList.forEach { category ->
                        val isSelected = category == selectedCategory
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = category,
                                    color = if (isSelected) primaryColor else onSurfaceColor,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            },
                            trailingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = primaryColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else null,
                            onClick = {
                                selectedCategory = category
                                showCategoryMenu = false
                            }
                        )
                    }
                }
            }

            // 2. Expandable "تنسيق وترقيم" Header & Panel
            Surface(
                color = surfaceColor,
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Header Bar (clickable to expand/collapse)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isFormattingExpanded = !isFormattingExpanded }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isFormattingExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Toggle Formatting",
                            tint = onSurfaceVariantColor,
                            modifier = Modifier.size(22.dp)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isAr) "تنسيق وترقيم" else "Formatting & Lists",
                                color = onSurfaceColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "A",
                                color = primaryColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Formatting Buttons (shown when expanded)
                    AnimatedVisibility(
                        visible = isFormattingExpanded,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Row 1: Font Size (15) | Color A | U | I | B | Right (يمين) | Center (منتصف) | Left (يسار) | Justify/Kashida (ضبط من الجانبين)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Font Size Chip e.g. "15"
                                Box(
                                    modifier = Modifier
                                        .height(31.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(surfaceVariantColor)
                                        .clickable { showFontSizeDialog = true }
                                        .padding(horizontal = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$fontSizeSp",
                                        color = onSurfaceColor,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Color 'A'
                                FormattingIconButton(
                                    text = "A",
                                    isUnderlined = true,
                                    isSelected = false,
                                    onClick = {
                                        wrapSelectionWith("==")
                                    }
                                )

                                // Underline 'U'
                                FormattingIconButton(
                                    text = "U",
                                    isUnderlined = true,
                                    isSelected = isUnderline,
                                    onClick = {
                                        isUnderline = !isUnderline
                                        wrapSelectionWith("<u>")
                                    }
                                )

                                // Italic 'I'
                                FormattingIconButton(
                                    text = "I",
                                    isItalic = true,
                                    isSelected = isItalic,
                                    onClick = {
                                        isItalic = !isItalic
                                        wrapSelectionWith("*")
                                    }
                                )

                                // Bold 'B'
                                FormattingIconButton(
                                    text = "B",
                                    isBold = true,
                                    isSelected = isBold,
                                    onClick = {
                                        isBold = !isBold
                                        wrapSelectionWith("**")
                                    }
                                )

                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(22.dp)
                                        .background(borderColor)
                                )

                                // 1. محاذاة يمين (Right Align)
                                AlignIconButton(
                                    icon = Icons.AutoMirrored.Filled.FormatAlignRight,
                                    isSelected = textAlign == NoteTextAlign.RIGHT,
                                    onClick = { textAlign = NoteTextAlign.RIGHT }
                                )

                                // 2. محاذاة منتصف (Center Align)
                                AlignIconButton(
                                    icon = Icons.Default.FormatAlignCenter,
                                    isSelected = textAlign == NoteTextAlign.CENTER,
                                    onClick = { textAlign = NoteTextAlign.CENTER }
                                )

                                // 3. محاذاة يسار (Left Align)
                                AlignIconButton(
                                    icon = Icons.AutoMirrored.Filled.FormatAlignLeft,
                                    isSelected = textAlign == NoteTextAlign.LEFT,
                                    onClick = { textAlign = NoteTextAlign.LEFT }
                                )

                                // 4. ضبط / كشيدة ربط من الجانبين (Justify / Kashida)
                                AlignIconButton(
                                    icon = Icons.Default.FormatAlignJustify,
                                    isSelected = textAlign == NoteTextAlign.JUSTIFY,
                                    onClick = {
                                        textAlign = NoteTextAlign.JUSTIFY
                                    }
                                )
                            }

                            // Row 2: كشيدة ضبط | ترقيم | نقاط | شرط - | مربع ☑
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // زر كشيدة ربط من الجانبين وترتيب الضبط
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(
                                            if (textAlign == NoteTextAlign.JUSTIFY) primaryColor
                                            else surfaceVariantColor
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (textAlign == NoteTextAlign.JUSTIFY) primaryColor else primaryColor.copy(alpha = 0.45f),
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .clickable { applyOrToggleKashida() }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .testTag("kashida_justify_btn"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.FormatAlignJustify,
                                            contentDescription = null,
                                            tint = if (textAlign == NoteTextAlign.JUSTIFY) onPrimaryColor else primaryColor,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isAr) "كشيدة / ضبط ـ" else "Kashida / Justify",
                                            color = if (textAlign == NoteTextAlign.JUSTIFY) onPrimaryColor else onSurfaceColor,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // ترقيم (Numbered list)
                                ListTypePill(
                                    label = if (isAr) "ترقيم" else "Numbered",
                                    icon = Icons.Default.FormatListNumbered,
                                    onClick = { insertPrefixToCurrentLine("1. ") }
                                )

                                // نقاط (Bullet list)
                                ListTypePill(
                                    label = if (isAr) "نقاط" else "Bullets",
                                    icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                                    onClick = { insertPrefixToCurrentLine("• ") }
                                )

                                // شرط - (Dash list)
                                ListTypePill(
                                    label = if (isAr) "شرط -" else "Dash -",
                                    icon = null,
                                    onClick = { insertPrefixToCurrentLine("- ") }
                                )

                                // مربع ☑ (Task Checkbox)
                                ListTypePill(
                                    label = if (isAr) "مربع ☑" else "Checkbox",
                                    icon = null,
                                    onClick = { insertPrefixToCurrentLine("☑ ") }
                                )
                            }
                        }
                    }
                }
            }

            // 3. Main Editor Card
            Surface(
                color = surfaceColor,
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 360.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Title Input Field
                    BasicTextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("note_title_input"),
                        textStyle = TextStyle(
                            color = onSurfaceColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = when (textAlign) {
                                NoteTextAlign.CENTER -> TextAlign.Center
                                NoteTextAlign.LEFT -> TextAlign.Left
                                NoteTextAlign.JUSTIFY -> TextAlign.Justify
                                NoteTextAlign.RIGHT -> TextAlign.Right
                            }
                        ),
                        cursorBrush = SolidColor(primaryColor),
                        decorationBox = { innerTextField ->
                            if (title.isEmpty()) {
                                Text(
                                    text = if (isAr) "العنوان" else "Title",
                                    color = onSurfaceVariantColor,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = when (textAlign) {
                                        NoteTextAlign.CENTER -> TextAlign.Center
                                        NoteTextAlign.LEFT -> TextAlign.Left
                                        NoteTextAlign.JUSTIFY -> TextAlign.Justify
                                        NoteTextAlign.RIGHT -> TextAlign.Right
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            innerTextField()
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(borderColor)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Content Input Field
                    BasicTextField(
                        value = contentValue,
                        onValueChange = { updateContentWithHistory(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 260.dp)
                            .testTag("note_content_input"),
                        textStyle = TextStyle(
                            color = onSurfaceColor,
                            fontSize = fontSizeSp.sp,
                            lineHeight = (fontSizeSp + 8).sp,
                            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                            fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                            textDecoration = if (isUnderline) TextDecoration.Underline else TextDecoration.None,
                            textAlign = when (textAlign) {
                                NoteTextAlign.CENTER -> TextAlign.Center
                                NoteTextAlign.LEFT -> TextAlign.Left
                                NoteTextAlign.JUSTIFY -> TextAlign.Justify
                                NoteTextAlign.RIGHT -> TextAlign.Right
                            }
                        ),
                        cursorBrush = SolidColor(primaryColor),
                        decorationBox = { innerTextField ->
                            if (contentValue.text.isEmpty()) {
                                Text(
                                    text = if (isAr) "اكتب ملاحظتك هنا..." else "Write your note here...",
                                    color = onSurfaceVariantColor,
                                    fontSize = fontSizeSp.sp,
                                    textAlign = when (textAlign) {
                                        NoteTextAlign.CENTER -> TextAlign.Center
                                        NoteTextAlign.LEFT -> TextAlign.Left
                                        NoteTextAlign.JUSTIFY -> TextAlign.Justify
                                        NoteTextAlign.RIGHT -> TextAlign.Right
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            innerTextField()
                        }
                    )

                    // Attachments Previews (Images, Audio, PDF, Reminder)
                    if (attachedImagePath != null || attachedAudioPath != null || hasReminder) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(borderColor)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Reminder Pill Preview
                        if (hasReminder && reminderTime != null) {
                            val timeStr = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale.getDefault()).format(Date(reminderTime!!))
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(surfaceVariantColor)
                                    .border(1.dp, primaryColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Alarm,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${if (isAr) "منبه:" else "Alarm:"} $timeStr",
                                    color = onSurfaceColor,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = {
                                        hasReminder = false
                                        reminderTime = null
                                    },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove Reminder",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Audio Player Preview
                        if (attachedAudioPath != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(surfaceVariantColor)
                                    .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        if (isPlayingAudio) {
                                            audioPlayer.pause()
                                            isPlayingAudio = false
                                        } else {
                                            audioPlayer.play(attachedAudioPath!!) { }
                                            isPlayingAudio = true
                                        }
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(primaryColor)
                                ) {
                                    Icon(
                                        imageVector = if (isPlayingAudio) Icons.Default.Stop else Icons.Default.PlayArrow,
                                        contentDescription = "Play Audio",
                                        tint = onPrimaryColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isAr) "تسجيل صوتي مرفق" else "Voice Note Attachment",
                                    color = onSurfaceColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = {
                                        audioPlayer.stop()
                                        isPlayingAudio = false
                                        attachedAudioPath = null
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Audio",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Image Preview
                        if (attachedImagePath != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            ) {
                                AsyncImage(
                                    model = attachedImagePath,
                                    contentDescription = "Attached Image",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { showFullImageDialog = true },
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    onClick = { attachedImagePath = null },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.6f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove Image",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    // Font Size Selection Dialog
    if (showFontSizeDialog) {
        AlertDialog(
            onDismissRequest = { showFontSizeDialog = false },
            containerColor = surfaceColor,
            title = {
                Text(
                    text = if (isAr) "تغيير حجم الخط" else "Select Font Size",
                    color = onSurfaceColor,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf(12, 14, 15, 16, 18, 20, 24).forEach { size ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (fontSizeSp == size) primaryColor else surfaceVariantColor)
                                .clickable {
                                    fontSizeSp = size
                                    showFontSizeDialog = false
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$size",
                                color = if (fontSizeSp == size) onPrimaryColor else onSurfaceColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFontSizeDialog = false }) {
                    Text(text = if (isAr) "تم" else "Done", color = primaryColor)
                }
            }
        )
    }

    // Reminder Picker Dialog
    if (showReminderPicker) {
        val calendar = remember { Calendar.getInstance().apply { reminderTime?.let { timeInMillis = it } } }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = reminderTime ?: System.currentTimeMillis()
        )
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE)
        )
        var isPickingTime by remember { mutableStateOf(false) }

        if (!isPickingTime) {
            DatePickerDialog(
                onDismissRequest = { showReminderPicker = false },
                confirmButton = {
                    TextButton(onClick = { isPickingTime = true }) {
                        Text(if (isAr) "التالي: اختيار الوقت" else "Next: Pick Time", color = primaryColor)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showReminderPicker = false }) {
                        Text(if (isAr) "إلغاء" else "Cancel", color = onSurfaceVariantColor)
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        } else {
            AlertDialog(
                onDismissRequest = { showReminderPicker = false },
                containerColor = surfaceColor,
                title = {
                    Text(
                        text = if (isAr) "تحديد وقت المنبه" else "Pick Alarm Time",
                        color = onSurfaceColor,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    TimePicker(state = timePickerState)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val selectedDateMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                            val pickedCal = Calendar.getInstance().apply {
                                timeInMillis = selectedDateMillis
                                set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                set(Calendar.MINUTE, timePickerState.minute)
                                set(Calendar.SECOND, 0)
                            }
                            reminderTime = pickedCal.timeInMillis
                            hasReminder = true
                            showReminderPicker = false
                            Toast.makeText(context, if (isAr) "تم ضبط المنبه بنجاح" else "Reminder set", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text(if (isAr) "تأكيد" else "Confirm", color = primaryColor)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isPickingTime = false }) {
                        Text(if (isAr) "رجوع" else "Back", color = onSurfaceVariantColor)
                    }
                }
            )
        }
    }
}

// Tool Dock Item for bottom bar
@Composable
private fun ToolDockItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(if (isSelected) primaryColor else surfaceVariantColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) onPrimaryColor else onSurfaceColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = if (isSelected) primaryColor else onSurfaceVariantColor,
            fontSize = 10.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
    }
}

// Formatting icon button (B, I, U, A)
@Composable
private fun FormattingIconButton(
    text: String,
    isBold: Boolean = false,
    isItalic: Boolean = false,
    isUnderlined: Boolean = false,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .size(31.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) primaryColor else surfaceVariantColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) onPrimaryColor else onSurfaceColor,
            fontSize = 13.5.sp,
            fontWeight = if (isBold) FontWeight.Black else FontWeight.Bold,
            fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
            textDecoration = if (isUnderlined) TextDecoration.Underline else TextDecoration.None
        )
    }
}

// Alignment Icon button
@Composable
private fun AlignIconButton(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .size(31.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) primaryColor else surfaceVariantColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) onPrimaryColor else onSurfaceColor,
            modifier = Modifier.size(17.dp)
        )
    }
}

// List Type Pill: ترقيم, نقاط, شرط, مربع
@Composable
private fun ListTypePill(
    label: String,
    icon: ImageVector?,
    onClick: () -> Unit
) {
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(surfaceVariantColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                color = onSurfaceColor,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium
            )
            if (icon != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = onSurfaceVariantColor,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}
