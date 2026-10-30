package com.example.ui.screens.notes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.localization.AppStrings
import com.example.ui.screens.notes.audio.AudioPlayerHelper
import java.io.File

/**
 * Statistics extracted for Word-like document metrics.
 */
data class DocumentStats(
    val wordCount: Int,
    val charCount: Int,
    val paragraphCount: Int,
    val readingTimeMinutes: Int
)

fun calculateDocumentStats(content: String): DocumentStats {
    val trimmed = content.trim()
    if (trimmed.isEmpty()) {
        return DocumentStats(0, 0, 0, 0)
    }
    val words = trimmed.split("\\s+".toRegex()).filter { it.isNotBlank() }
    val paragraphs = trimmed.split("\n\n+".toRegex()).filter { it.isNotBlank() }
    val readingMinutes = kotlin.math.max(1, (words.size + 150) / 180)
    return DocumentStats(
        wordCount = words.size,
        charCount = trimmed.length,
        paragraphCount = paragraphs.size,
        readingTimeMinutes = readingMinutes
    )
}

/**
 * Word Document Page View:
 * Renders notes with high-fidelity formatting, margins, typography hierarchy,
 * interactive checklists, and attachment media cards like a printed Microsoft Word document.
 */
@Composable
fun WordDocumentView(
    title: String,
    content: String,
    noteTextColor: Color,
    notePlaceholderColor: Color,
    primaryColor: Color,
    currentLanguage: String,
    strings: AppStrings,
    attachedImagePath: String?,
    attachedPdfPath: String?,
    attachedPdfName: String?,
    attachedAudioPath: String?,
    audioDurationMs: Long,
    audioPlayer: AudioPlayerHelper,
    isPlayingAudio: Boolean,
    onTogglePlayAudio: () -> Unit,
    onRemoveImage: (() -> Unit)? = null,
    onRemovePdf: (() -> Unit)? = null,
    onRemoveAudio: (() -> Unit)? = null,
    onContentChanged: (String) -> Unit
) {
    val stats = remember(content) { calculateDocumentStats(content) }
    val isAr = currentLanguage == "ar"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        // 1. Microsoft Word Document Metadata Ribbon Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatPill(
                        label = "${stats.wordCount} ${strings.noteWordCount}",
                        icon = Icons.Default.Description,
                        accentColor = primaryColor
                    )
                    StatPill(
                        label = "${stats.charCount} ${strings.noteCharCount}",
                        icon = null,
                        accentColor = noteTextColor
                    )
                    StatPill(
                        label = "${stats.paragraphCount} ${strings.noteParagraphCount}",
                        icon = null,
                        accentColor = noteTextColor
                    )
                }

                StatPill(
                    label = "~${stats.readingTimeMinutes} ${if (isAr) "دقيقة قراءة" else "min read"}",
                    icon = Icons.Default.Timer,
                    accentColor = Color(0xFF10B981)
                )
            }
        }

        // 2. The Word Page Container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("word_page_document"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Page Header Brand & Margin Line
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "مستند يومي • Word Typeset" else "Yawmi Document • Word Typeset",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )

                    Text(
                        text = strings.noteAutoSaved,
                        fontSize = 9.sp,
                        color = Color(0xFF10B981)
                    )
                }

                HorizontalDivider(
                    color = primaryColor.copy(alpha = 0.3f),
                    thickness = 1.5.dp,
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                // Attached Image Banner (if present)
                attachedImagePath?.let { imgPath ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(bottom = 14.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = File(imgPath),
                                contentDescription = "Attached Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            if (onRemoveImage != null) {
                                IconButton(
                                    onClick = onRemoveImage,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .size(28.dp)
                                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                // Attached PDF Banner (if present)
                attachedPdfPath?.let { _ ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = attachedPdfName ?: "Document.pdf",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = noteTextColor
                                    )
                                    Text(
                                        text = strings.attachPdf,
                                        fontSize = 9.sp,
                                        color = notePlaceholderColor
                                    )
                                }
                            }

                            if (onRemovePdf != null) {
                                IconButton(onClick = onRemovePdf, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove PDF", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }

                // Attached Audio Voice / File Player Banner
                attachedAudioPath?.let { _ ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = primaryColor.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = onTogglePlayAudio,
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(primaryColor)
                                ) {
                                    Icon(
                                        imageVector = if (isPlayingAudio) Icons.Default.Stop else Icons.Default.PlayArrow,
                                        contentDescription = "Play/Stop",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isAr) "تسجيل صوتي مرفق" else "Audio Track",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = noteTextColor
                                    )
                                    Text(
                                        text = formatTime(audioDurationMs),
                                        fontSize = 9.5.sp,
                                        color = primaryColor
                                    )
                                }
                            }

                            if (onRemoveAudio != null) {
                                IconButton(onClick = onRemoveAudio, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove Audio", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }

                // Document Title
                if (title.isNotBlank()) {
                    Text(
                        text = title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = noteTextColor,
                        lineHeight = 32.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )
                }

                // Render Content Blocks
                if (content.isBlank()) {
                    Text(
                        text = if (isAr) "المستند فارغ... اكتب أو اختر من شريط الأدوات لتنضيد النص." else "Document is empty... write or use toolbar to format.",
                        fontSize = 16.sp,
                        color = notePlaceholderColor,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                } else {
                    RenderWordContentBlocks(
                        content = content,
                        noteTextColor = noteTextColor,
                        primaryColor = primaryColor,
                        onCheckboxToggled = { lineIndex, isChecked ->
                            val updatedContent = toggleCheckboxAtLine(content, lineIndex, isChecked)
                            onContentChanged(updatedContent)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom Page Footer
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "الصفحة 1 من 1" else "Page 1 of 1",
                        fontSize = 12.sp,
                        color = notePlaceholderColor
                    )
                    Text(
                        text = if (isAr) "تنسيق ميكروسوفت وورد • يومي" else "Microsoft Word Style • Yawmi",
                        fontSize = 12.sp,
                        color = notePlaceholderColor
                    )
                }
            }
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    accentColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = accentColor
        )
    }
}

/**
 * Parses and renders blocks of text according to Microsoft Word typesetting standards:
 * - H1, H2, H3 Headings
 * - Word Quote Callout boxes
 * - Checklists with clickable boxes
 * - Bullet and numbered lists
 * - Horizontal rules
 * - Inline bold, italic, underline, strike, marker highlights
 */
@Composable
private fun RenderWordContentBlocks(
    content: String,
    noteTextColor: Color,
    primaryColor: Color,
    onCheckboxToggled: (Int, Boolean) -> Unit
) {
    val lines = remember(content) { content.split("\n") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        lines.forEachIndexed { index, line ->
            val trimmed = line.trim()

            when {
                // Heading 1 (# ...)
                trimmed.startsWith("# ") -> {
                    val headingText = trimmed.removePrefix("# ").trim()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp, bottom = 6.dp)
                    ) {
                        Text(
                            text = headingText,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor,
                            lineHeight = 32.sp
                        )
                        Box(
                            modifier = Modifier
                                .padding(top = 3.dp)
                                .size(width = 40.dp, height = 3.dp)
                                .clip(RoundedCornerShape(1.5.dp))
                                .background(primaryColor)
                        )
                    }
                }

                // Heading 2 (## ...)
                trimmed.startsWith("## ") -> {
                    val headingText = trimmed.removePrefix("## ").trim()
                    Text(
                        text = headingText,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = primaryColor.copy(alpha = 0.95f),
                        lineHeight = 28.sp,
                        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                    )
                }

                // Heading 3 (### ...)
                trimmed.startsWith("### ") -> {
                    val headingText = trimmed.removePrefix("### ").trim()
                    Text(
                        text = headingText,
                        fontSize = 18.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = noteTextColor,
                        lineHeight = 25.sp,
                        modifier = Modifier.padding(top = 8.dp, bottom = 3.dp)
                    )
                }

                // Horizontal Rule (--- or ___)
                trimmed == "---" || trimmed == "___" || trimmed == "***" -> {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }

                // Callout / Blockquote (> ...)
                trimmed.startsWith("> ") -> {
                    val quoteText = trimmed.removePrefix("> ").trim()
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = primaryColor.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            primaryColor.copy(alpha = 0.25f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 4.dp, height = 28.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(primaryColor)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = parseWordInlineFormatting(quoteText, noteTextColor, primaryColor),
                                fontSize = 17.sp,
                                fontStyle = FontStyle.Italic,
                                color = noteTextColor,
                                lineHeight = 25.sp
                            )
                        }
                    }
                }

                // Checklist [ ] or [x] or [✓]
                trimmed.startsWith("[ ] ") || trimmed.startsWith("[x] ", ignoreCase = true) || trimmed.startsWith("[✓] ") -> {
                    val isChecked = trimmed.startsWith("[x] ", ignoreCase = true) || trimmed.startsWith("[✓] ")
                    val taskText = when {
                        trimmed.startsWith("[ ] ") -> trimmed.removePrefix("[ ] ")
                        trimmed.startsWith("[x] ", ignoreCase = true) -> trimmed.removePrefix("[x] ")
                        else -> trimmed.removePrefix("[✓] ")
                    }.trim()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onCheckboxToggled(index, !isChecked) }
                            .padding(vertical = 4.dp, horizontal = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { onCheckboxToggled(index, it) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = primaryColor,
                                uncheckedColor = MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = parseWordInlineFormatting(taskText, noteTextColor, primaryColor),
                            fontSize = 17.5.sp,
                            color = if (isChecked) noteTextColor.copy(alpha = 0.5f) else noteTextColor,
                            textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
                            lineHeight = 25.sp
                        )
                    }
                }

                // Bullet List (• ... or - ...)
                trimmed.startsWith("• ") || (trimmed.startsWith("- ") && !trimmed.startsWith("---")) -> {
                    val itemText = if (trimmed.startsWith("• ")) trimmed.removePrefix("• ") else trimmed.removePrefix("- ")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 9.dp, start = 4.dp, end = 10.dp)
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(primaryColor)
                        )
                        Text(
                            text = parseWordInlineFormatting(itemText.trim(), noteTextColor, primaryColor),
                            fontSize = 17.5.sp,
                            color = noteTextColor,
                            lineHeight = 25.sp
                        )
                    }
                }

                // Numbered List (1. ...)
                trimmed.matches(Regex("""^\d+\.\s+.*""")) -> {
                    val numberMatch = Regex("""^(\d+)\.\s+(.*)""").find(trimmed)
                    val num = numberMatch?.groupValues?.getOrNull(1) ?: "1"
                    val itemText = numberMatch?.groupValues?.getOrNull(2) ?: ""

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 2.dp, end = 10.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(primaryColor.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "$num.",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                        }
                        Text(
                            text = parseWordInlineFormatting(itemText.trim(), noteTextColor, primaryColor),
                            fontSize = 17.5.sp,
                            color = noteTextColor,
                            lineHeight = 25.sp
                        )
                    }
                }

                // Empty line
                trimmed.isEmpty() -> {
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Standard Paragraph Line
                else -> {
                    Text(
                        text = parseWordInlineFormatting(line, noteTextColor, primaryColor),
                        fontSize = 17.5.sp,
                        color = noteTextColor,
                        lineHeight = 26.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * Parses Word inline styles:
 * - **bold**
 * - *italic*
 * - <u>underline</u>
 * - ~~strikethrough~~
 * - `code`
 * - ==highlight== or <mark color="...">...</mark>
 */
fun parseWordInlineFormatting(text: String, baseColor: Color, primaryColor: Color): AnnotatedString {
    return buildAnnotatedString {
        var cursor = 0

        // Regex token pattern matching formatting markers
        val regex = Regex("""(\*\*([^*]+)\*\*)|(\*([^*]+)\*)|(<u>([^<]+)</u>)|(~~([^~]+)~~)|(`([^`]+)`)|(==([^=]+)==)""")
        val matches = regex.findAll(text)

        for (match in matches) {
            val start = match.range.first
            val end = match.range.last + 1

            if (start > cursor) {
                append(text.substring(cursor, start))
            }

            val fullMatch = match.value
            when {
                // Bold: **text**
                fullMatch.startsWith("**") && fullMatch.endsWith("**") -> {
                    val inner = match.groupValues[2]
                    val s = length
                    append(inner)
                    addStyle(SpanStyle(fontWeight = FontWeight.Bold), s, length)
                }
                // Italic: *text*
                fullMatch.startsWith("*") && fullMatch.endsWith("*") -> {
                    val inner = match.groupValues[4]
                    val s = length
                    append(inner)
                    addStyle(SpanStyle(fontStyle = FontStyle.Italic), s, length)
                }
                // Underline: <u>text</u>
                fullMatch.startsWith("<u>") && fullMatch.endsWith("</u>") -> {
                    val inner = match.groupValues[6]
                    val s = length
                    append(inner)
                    addStyle(SpanStyle(textDecoration = TextDecoration.Underline), s, length)
                }
                // Strikethrough: ~~text~~
                fullMatch.startsWith("~~") && fullMatch.endsWith("~~") -> {
                    val inner = match.groupValues[8]
                    val s = length
                    append(inner)
                    addStyle(SpanStyle(textDecoration = TextDecoration.LineThrough), s, length)
                }
                // Code: `text`
                fullMatch.startsWith("`") && fullMatch.endsWith("`") -> {
                    val inner = match.groupValues[10]
                    val s = length
                    append(inner)
                    addStyle(
                        SpanStyle(
                            background = Color(0xFF1E293B),
                            color = Color(0xFF38BDF8),
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        ),
                        s,
                        length
                    )
                }
                // Highlight: ==text==
                fullMatch.startsWith("==") && fullMatch.endsWith("==") -> {
                    val inner = match.groupValues[12]
                    val s = length
                    append(inner)
                    addStyle(
                        SpanStyle(
                            background = Color(0xFFFDE047).copy(alpha = 0.5f),
                            color = Color(0xFF1E293B),
                            fontWeight = FontWeight.SemiBold
                        ),
                        s,
                        length
                    )
                }
                else -> {
                    append(fullMatch)
                }
            }

            cursor = end
        }

        if (cursor < text.length) {
            append(text.substring(cursor))
        }
    }
}

private fun toggleCheckboxAtLine(content: String, targetLineIndex: Int, newChecked: Boolean): String {
    val lines = content.split("\n").toMutableList()
    if (targetLineIndex in lines.indices) {
        val original = lines[targetLineIndex]
        val trimmed = original.trim()
        val textAfter = when {
            trimmed.startsWith("[ ] ") -> trimmed.removePrefix("[ ] ")
            trimmed.startsWith("[x] ", ignoreCase = true) -> trimmed.removePrefix("[x] ")
            trimmed.startsWith("[✓] ") -> trimmed.removePrefix("[✓] ")
            else -> trimmed
        }
        lines[targetLineIndex] = if (newChecked) "[✓] $textAfter" else "[ ] $textAfter"
    }
    return lines.joinToString("\n")
}

private fun formatTime(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(java.util.Locale.getDefault(), "%02d:%02d", minutes, seconds)
}
