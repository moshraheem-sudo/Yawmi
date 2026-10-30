package com.example.ui.screens.notes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatClear
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings

enum class WordToolbarTab {
    STYLES,    // أنماط وخط
    LISTS,     // قوائم وتنظيم
    MEDIA,     // وسائط ومرفقات
    TOOLS      // أدوات وورد
}

enum class FormatCommand {
    BOLD,
    ITALIC,
    UNDERLINE,
    STRIKETHROUGH,
    CODE,
    HIGHLIGHT_YELLOW,
    HIGHLIGHT_GREEN,
    H1,
    H2,
    H3,
    BULLET_LIST,
    NUMBERED_LIST,
    CHECKLIST,
    QUOTE,
    DIVIDER,
    ALIGN_RIGHT,
    ALIGN_CENTER,
    ALIGN_LEFT
}

/**
 * Microsoft Word-style Ribbon Toolbar:
 * Elevated cleanly above mobile navigation buttons with high-contrast, crystal-clear tools.
 */
@Composable
fun WordFormattingToolbar(
    currentTab: WordToolbarTab,
    onTabSelected: (WordToolbarTab) -> Unit,
    strings: AppStrings,
    currentLanguage: String,
    primaryColor: Color,
    // Media triggers
    onPickImage: () -> Unit,
    onPickPdf: () -> Unit,
    onPickAudioFile: () -> Unit,
    onRecordAudioToggle: () -> Unit,
    isRecordingAudio: Boolean,
    recordingSeconds: Int,
    // Note color & reminder
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    showColorPicker: Boolean,
    onToggleColorPicker: () -> Unit,
    onOpenReminder: () -> Unit,
    // Formatting command dispatcher (handles selected text and direct insertion cleanly)
    onFormatCommand: (FormatCommand) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onClearFormatting: () -> Unit,
    onCopyAll: () -> Unit
) {
    val isAr = currentLanguage == "ar"

    // Elevated Toolbar Container with Navigation Bars Insets & IME Padding
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() // Lifts toolbar above Android system navigation buttons / pill
            .imePadding()            // Lifts toolbar above software keyboard
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .padding(bottom = 14.dp) // Extra clearance to ensure system navigation buttons never obstruct tools
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 14.dp, shape = RoundedCornerShape(18.dp)),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF141D30), // Deep obsidian slate for high contrast
            border = BorderStroke(1.5.dp, Color(0xFF2E3D5B)) // Crisp visible border
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                // 1. Live Recording Banner if recording
                AnimatedVisibility(visible = isRecordingAudio) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.2f))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.error)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${strings.recordingAudio} (${formatDurationSec(recordingSeconds)})",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        TextButton(onClick = onRecordAudioToggle) {
                            Text(strings.stopRecording, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                // 2. Note Color Palette Selector Row if toggled
                AnimatedVisibility(visible = showColorPicker) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A))
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = if (isAr) "اختر لون وسمة الملاحظة:" else "Choose Note Theme Color:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            NotePalette.forEach { hex ->
                                val isSelected = selectedColor.equals(hex, ignoreCase = true)
                                val isWhite = hex.equals("#FFFFFF", ignoreCase = true)
                                val isBlack = hex.equals("#000000", ignoreCase = true)
                                val isTransparent = hex.equals("#00000000", ignoreCase = true) || hex.equals("#00FFFFFF", ignoreCase = true)
                                val pColor = if (isTransparent) Color.Transparent else runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrDefault(Color.Gray)

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onColorSelected(hex) }
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (isTransparent) Color(0xFF1E293B) else pColor)
                                            .border(
                                                width = if (isSelected) 3.dp else 1.5.dp,
                                                color = when {
                                                    isSelected -> Color(0xFF8B5CF6)
                                                    isWhite -> Color(0xFFCBD5E1)
                                                    isBlack -> Color(0xFF475569)
                                                    isTransparent -> Color(0xFF38BDF8)
                                                    else -> Color(0xFF475569)
                                                },
                                                shape = CircleShape
                                            )
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = if (isWhite) Color.Black else Color.White,
                                                modifier = Modifier.size(20.dp).align(Alignment.Center)
                                            )
                                        } else if (isTransparent) {
                                            Icon(
                                                imageVector = Icons.Outlined.Layers,
                                                contentDescription = null,
                                                tint = Color(0xFF38BDF8),
                                                modifier = Modifier.size(18.dp).align(Alignment.Center)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = getNoteColorLabel(hex, currentLanguage),
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color(0xFFA78BFA) else Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Word Ribbon Tab Headers Row (High Contrast & Clear Typography)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Tabs
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WordRibbonTabButton(
                            title = if (isAr) "✍️ خط وتنسيق" else "✍️ Styles",
                            isSelected = currentTab == WordToolbarTab.STYLES,
                            primaryColor = primaryColor,
                            onClick = { onTabSelected(WordToolbarTab.STYLES) }
                        )
                        WordRibbonTabButton(
                            title = if (isAr) "📝 قوائم وتنظيم" else "📝 Lists",
                            isSelected = currentTab == WordToolbarTab.LISTS,
                            primaryColor = primaryColor,
                            onClick = { onTabSelected(WordToolbarTab.LISTS) }
                        )
                        WordRibbonTabButton(
                            title = if (isAr) "📎 مرفقات" else "📎 Media",
                            isSelected = currentTab == WordToolbarTab.MEDIA,
                            primaryColor = primaryColor,
                            onClick = { onTabSelected(WordToolbarTab.MEDIA) }
                        )
                        WordRibbonTabButton(
                            title = if (isAr) "⚙️ أدوات" else "⚙️ Tools",
                            isSelected = currentTab == WordToolbarTab.TOOLS,
                            primaryColor = primaryColor,
                            onClick = { onTabSelected(WordToolbarTab.TOOLS) }
                        )
                    }

                    // Quick Palette & Reminder Action Icons (Distinct & High Contrast)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Palette Button
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (showColorPicker) Color(0xFF7C3AED) else Color(0xFF1E293B))
                                .border(1.dp, Color(0xFF334155), CircleShape)
                                .clickable { onToggleColorPicker() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = strings.noteBackgroundTheme,
                                tint = if (showColorPicker) Color.White else Color(0xFFA78BFA),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Reminder Button
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B))
                                .border(1.dp, Color(0xFF334155), CircleShape)
                                .clickable { onOpenReminder() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = strings.setNoteReminder,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Divider line between tabs and action tools
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF263554))
                )

                // 4. Large, Crystal-Clear Tools Row for the Active Tab
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    when (currentTab) {
                        WordToolbarTab.STYLES -> {
                            // Headings: H1, H2, H3
                            HeadingToolPill(
                                tag = "H1",
                                label = if (isAr) "رئيسي" else "Main",
                                color = Color(0xFF8B5CF6),
                                onClick = { onFormatCommand(FormatCommand.H1) }
                            )
                            HeadingToolPill(
                                tag = "H2",
                                label = if (isAr) "فرعي" else "Sub",
                                color = Color(0xFFA78BFA),
                                onClick = { onFormatCommand(FormatCommand.H2) }
                            )
                            HeadingToolPill(
                                tag = "H3",
                                label = if (isAr) "صغير" else "Minor",
                                color = Color(0xFFC4B5FD),
                                onClick = { onFormatCommand(FormatCommand.H3) }
                            )

                            ToolDivider()

                            // Character Formats: Bold, Italic, Underline, Strikethrough, Code
                            FormatCharBtn(
                                label = "B",
                                tooltip = strings.boldFormat,
                                isBold = true,
                                onClick = { onFormatCommand(FormatCommand.BOLD) }
                            )
                            FormatCharBtn(
                                label = "I",
                                tooltip = strings.italicFormat,
                                isItalic = true,
                                onClick = { onFormatCommand(FormatCommand.ITALIC) }
                            )
                            FormatCharBtn(
                                label = "U",
                                tooltip = strings.formatUnderline,
                                isUnderline = true,
                                onClick = { onFormatCommand(FormatCommand.UNDERLINE) }
                            )
                            FormatCharBtn(
                                label = "S",
                                tooltip = strings.formatStrikethrough,
                                isStrike = true,
                                onClick = { onFormatCommand(FormatCommand.STRIKETHROUGH) }
                            )
                            WordToolIconBtn(
                                icon = Icons.Default.Code,
                                tooltip = "Code",
                                tint = Color(0xFF38BDF8),
                                onClick = { onFormatCommand(FormatCommand.CODE) }
                            )

                            ToolDivider()

                            // Highlighter Markers
                            HighlighterToolPill(
                                swatchColor = Color(0xFFFACC15),
                                label = if (isAr) "تظليل أصفر" else "Yellow",
                                onClick = { onFormatCommand(FormatCommand.HIGHLIGHT_YELLOW) }
                            )

                            HighlighterToolPill(
                                swatchColor = Color(0xFF4ADE80),
                                label = if (isAr) "تظليل أخضر" else "Green",
                                onClick = { onFormatCommand(FormatCommand.HIGHLIGHT_GREEN) }
                            )

                            ToolDivider()

                            // Alignments
                            WordToolIconBtn(
                                icon = Icons.AutoMirrored.Filled.FormatAlignRight,
                                tooltip = strings.formatAlignRight,
                                onClick = { onFormatCommand(FormatCommand.ALIGN_RIGHT) }
                            )
                            WordToolIconBtn(
                                icon = Icons.Default.FormatAlignCenter,
                                tooltip = strings.formatAlignCenter,
                                onClick = { onFormatCommand(FormatCommand.ALIGN_CENTER) }
                            )
                            WordToolIconBtn(
                                icon = Icons.AutoMirrored.Filled.FormatAlignLeft,
                                tooltip = strings.formatAlignLeft,
                                onClick = { onFormatCommand(FormatCommand.ALIGN_LEFT) }
                            )
                        }

                        WordToolbarTab.LISTS -> {
                            RichFeatureChip(
                                icon = Icons.Default.FormatListBulleted,
                                label = if (isAr) "قائمة نقطية" else "Bullets",
                                tint = Color(0xFF38BDF8),
                                onClick = { onFormatCommand(FormatCommand.BULLET_LIST) }
                            )
                            RichFeatureChip(
                                icon = Icons.Default.FormatListNumbered,
                                label = if (isAr) "قائمة مرقمة" else "Numbered",
                                tint = Color(0xFF818CF8),
                                onClick = { onFormatCommand(FormatCommand.NUMBERED_LIST) }
                            )
                            RichFeatureChip(
                                icon = Icons.Default.Check,
                                label = if (isAr) "مهمة تفاعلية" else "Checklist",
                                tint = Color(0xFF10B981),
                                onClick = { onFormatCommand(FormatCommand.CHECKLIST) }
                            )
                            RichFeatureChip(
                                icon = Icons.Default.FormatQuote,
                                label = if (isAr) "اقتباس مميز" else "Quote",
                                tint = Color(0xFFF59E0B),
                                onClick = { onFormatCommand(FormatCommand.QUOTE) }
                            )
                            RichFeatureChip(
                                icon = Icons.Default.HorizontalRule,
                                label = if (isAr) "فاصل أفقي" else "Divider",
                                tint = Color(0xFF94A3B8),
                                onClick = { onFormatCommand(FormatCommand.DIVIDER) }
                            )
                        }

                        WordToolbarTab.MEDIA -> {
                            RichFeatureChip(
                                icon = Icons.Default.Image,
                                label = if (isAr) "إرفاق صورة" else "Attach Photo",
                                tint = Color(0xFF38BDF8),
                                containerColor = Color(0xFF0369A1).copy(alpha = 0.25f),
                                onClick = onPickImage
                            )
                            RichFeatureChip(
                                icon = if (isRecordingAudio) Icons.Default.Stop else Icons.Default.Mic,
                                label = if (isRecordingAudio) strings.stopRecording else strings.recordAudio,
                                tint = if (isRecordingAudio) Color(0xFFEF4444) else Color(0xFF10B981),
                                containerColor = if (isRecordingAudio) Color(0xFFEF4444).copy(alpha = 0.25f) else Color(0xFF059669).copy(alpha = 0.25f),
                                onClick = onRecordAudioToggle
                            )
                            RichFeatureChip(
                                icon = Icons.Default.Audiotrack,
                                label = if (isAr) "ملف صوتي (MP3)" else "Audio File",
                                tint = Color(0xFFA78BFA),
                                containerColor = Color(0xFF6D28D9).copy(alpha = 0.25f),
                                onClick = onPickAudioFile
                            )
                            RichFeatureChip(
                                icon = Icons.Default.PictureAsPdf,
                                label = if (isAr) "مستند PDF" else "PDF Document",
                                tint = Color(0xFFEF4444),
                                containerColor = Color(0xFFB91C1C).copy(alpha = 0.25f),
                                onClick = onPickPdf
                            )
                        }

                        WordToolbarTab.TOOLS -> {
                            RichFeatureChip(
                                icon = Icons.AutoMirrored.Filled.Undo,
                                label = if (isAr) "تراجع" else "Undo",
                                tint = Color(0xFFE2E8F0),
                                onClick = onUndo
                            )
                            RichFeatureChip(
                                icon = Icons.AutoMirrored.Filled.Redo,
                                label = if (isAr) "إعادة" else "Redo",
                                tint = Color(0xFFE2E8F0),
                                onClick = onRedo
                            )
                            RichFeatureChip(
                                icon = Icons.Default.ContentCopy,
                                label = if (isAr) "نسخ النص كاملاً" else "Copy All",
                                tint = Color(0xFF38BDF8),
                                onClick = onCopyAll
                            )
                            RichFeatureChip(
                                icon = Icons.Default.FormatClear,
                                label = if (isAr) "مسح التنسيق" else "Clear Format",
                                tint = Color(0xFFEF4444),
                                onClick = onClearFormatting
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tab Ribbon Button: High visibility and contrast.
 */
@Composable
private fun WordRibbonTabButton(
    title: String,
    isSelected: Boolean,
    primaryColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) primaryColor else Color(0xFF1E293B),
        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (isSelected) Color.White else Color(0xFFE2E8F0),
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp)
        )
    }
}

/**
 * Heading pill with distinct tag (e.g. H1) and label.
 */
@Composable
private fun HeadingToolPill(
    tag: String,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.22f),
        border = BorderStroke(1.2.dp, color.copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = tag,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

/**
 * High-contrast character formatting button (B, I, U, S).
 */
@Composable
private fun FormatCharBtn(
    label: String,
    tooltip: String,
    isBold: Boolean = false,
    isItalic: Boolean = false,
    isUnderline: Boolean = false,
    isStrike: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(width = 48.dp, height = 44.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF1E293B),
        border = BorderStroke(1.2.dp, Color(0xFF334155))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontSize = 18.sp,
                fontWeight = if (isBold) FontWeight.Black else FontWeight.Bold,
                fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                textDecoration = when {
                    isUnderline -> TextDecoration.Underline
                    isStrike -> TextDecoration.LineThrough
                    else -> TextDecoration.None
                },
                color = Color.White
            )
        }
    }
}

/**
 * High-contrast tool icon button with distinct border and touch target.
 */
@Composable
private fun WordToolIconBtn(
    icon: ImageVector,
    tooltip: String,
    tint: Color = Color.White,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(width = 48.dp, height = 44.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF1E293B),
        border = BorderStroke(1.2.dp, Color(0xFF334155))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = tooltip,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Rich feature chip with clear icon, label, and high visibility.
 */
@Composable
private fun RichFeatureChip(
    icon: ImageVector,
    label: String,
    tint: Color = Color.White,
    containerColor: Color = Color(0xFF1E293B),
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        border = BorderStroke(1.2.dp, tint.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

/**
 * Highlighter pill with vibrant color preview.
 */
@Composable
private fun HighlighterToolPill(
    swatchColor: Color,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = swatchColor.copy(alpha = 0.25f),
        border = BorderStroke(1.2.dp, swatchColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(swatchColor)
            )
            Text(
                text = label,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ToolDivider() {
    Box(
        modifier = Modifier
            .size(width = 1.dp, height = 26.dp)
            .background(Color(0xFF334155))
    )
}

private fun formatDurationSec(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(java.util.Locale.getDefault(), "%02d:%02d", minutes, seconds)
}
