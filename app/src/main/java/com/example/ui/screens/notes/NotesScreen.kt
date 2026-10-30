package com.example.ui.screens.notes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.NoteItem
import com.example.localization.LocalAppStrings
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.EmptyState
import com.example.ui.theme.CyanSkyPrimary
import com.example.ui.theme.MidnightSlateBg
import com.example.ui.theme.MidnightSlateBorder
import com.example.ui.theme.MidnightSlateHighlightBorder
import com.example.ui.theme.MidnightSlateSurface
import com.example.ui.theme.MidnightSlateSurfaceVariant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val NotePalette = listOf(
    "#162235",   // Midnight Slate
    "#131C31",   // Obsidian Dark
    "#2E1065",   // Royal Purple Velvet
    "#1E293B",   // Sleek Slate Navy
    "#1E1B4B",   // Deep Indigo Blue
    "#064E3B",   // Deep Emerald Forest
    "#451A03",   // Warm Espresso Wood
    "#4C0519",   // Deep Burgundy Rose
    "#FFFFFF",   // Pure White
    "#000000",   // Pure Black AMOLED
    "#00000000", // Transparent / Glass
    "#2D6A4F"    // Natural Forest Green
)

fun getNoteColorLabel(hex: String, currentLanguage: String): String {
    val isAr = currentLanguage == "ar"
    return when (hex.uppercase()) {
        "#162235" -> if (isAr) "كحلي حديث" else "Modern Slate"
        "#131C31" -> if (isAr) "ليلي ملكي" else "Obsidian"
        "#2E1065" -> if (isAr) "بنفسجي داكن" else "Royal Purple"
        "#1E293B" -> if (isAr) "كحلي هادئ" else "Navy Slate"
        "#1E1B4B" -> if (isAr) "نيلي داكن" else "Deep Indigo"
        "#064E3B" -> if (isAr) "زمردي غامق" else "Emerald"
        "#451A03" -> if (isAr) "ترابي دافئ" else "Espresso"
        "#4C0519" -> if (isAr) "عنابي راقي" else "Burgundy"
        "#FFFFFF" -> if (isAr) "أبيض ناصع" else "White"
        "#000000" -> if (isAr) "أسود حالك" else "Black"
        "#00000000", "#00FFFFFF" -> if (isAr) "شفاف زجاجي" else "Frosted Glass"
        "#2D6A4F" -> if (isAr) "طبيعي أخضر" else "Nature Green"
        else -> if (isAr) "مخصص" else "Custom"
    }
}

enum class NotesMainTab {
    ALL,
    FAVORITES
}

@Composable
fun NotesScreen(
    notes: List<NoteItem>,
    searchQuery: String,
    currentLanguage: String,
    onSearchQueryChange: (String) -> Unit,
    onSaveNote: (NoteItem) -> Unit,
    onTogglePinned: (NoteItem) -> Unit,
    onDeleteNote: (NoteItem) -> Unit,
    onToggleFavorite: ((NoteItem) -> Unit)? = null,
    onOpenNoteEditor: ((NoteItem?) -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    val isAr = currentLanguage == "ar"

    var mainTab by remember { mutableStateOf(NotesMainTab.ALL) }
    var selectedCategoryFilter by remember { mutableStateOf("الكل") }
    var isGridView by remember { mutableStateOf(true) }
    var isSearchVisible by remember { mutableStateOf(false) }
    var noteToDelete by remember { mutableStateOf<NoteItem?>(null) }
    var showTopMoreMenu by remember { mutableStateOf(false) }

    // Filter notes based on MainTab (All vs Favorites), category, and search query
    val filteredNotes = remember(notes, mainTab, selectedCategoryFilter, searchQuery) {
        notes.filter { note ->
            val matchesTab = when (mainTab) {
                NotesMainTab.ALL -> true
                NotesMainTab.FAVORITES -> note.isFavorite
            }
            val matchesCategory = if (selectedCategoryFilter == "الكل" || selectedCategoryFilter == "All") {
                true
            } else {
                note.category.equals(selectedCategoryFilter, ignoreCase = true)
            }
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                note.title.contains(searchQuery, ignoreCase = true) ||
                        note.content.contains(searchQuery, ignoreCase = true)
            }
            matchesTab && matchesCategory && matchesSearch
        }
    }

    val bgColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val borderColor = MaterialTheme.colorScheme.outline
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant

    Scaffold(
        containerColor = bgColor,
        floatingActionButton = {
            // Centered Squircle Add Button
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(surfaceColor)
                    .border(2.dp, primaryColor, RoundedCornerShape(18.dp))
                    .clickable { onOpenNoteEditor?.invoke(null) }
                    .testTag("notes_add_fab"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = strings.dialogAddNoteTitle,
                    tint = onSurfaceColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        floatingActionButtonPosition = androidx.compose.material3.FabPosition.Center
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Unified Top Header Row: Action Buttons + "الملاحظات" Title & Icon (replacing "متابعة القسم")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Actions & Status (More Menu, Search, View Mode Toggle)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        IconButton(
                            onClick = { showTopMoreMenu = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More",
                                tint = onSurfaceColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = showTopMoreMenu,
                            onDismissRequest = { showTopMoreMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (isAr) "ملاحظة جديدة" else "New Note") },
                                onClick = {
                                    showTopMoreMenu = false
                                    onOpenNoteEditor?.invoke(null)
                                },
                                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text(if (isAr) "إظهار/إخفاء البحث" else "Toggle Search") },
                                onClick = {
                                    showTopMoreMenu = false
                                    isSearchVisible = !isSearchVisible
                                },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    IconButton(
                        onClick = { isSearchVisible = !isSearchVisible },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (isSearchVisible) primaryColor else onSurfaceColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    // View Mode Toggle (List vs Grid)
                    IconButton(
                        onClick = { isGridView = !isGridView },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isGridView) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                            contentDescription = "Toggle Grid/List",
                            tint = onSurfaceColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Window Name "الملاحظات" with its Edit Icon Badge (in place of "متابعة القسم")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onOpenNoteEditor?.invoke(null) }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isAr) "الملاحظات" else "Notes",
                        color = onSurfaceColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Notes",
                            tint = primaryColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Search Bar (if activated)
            AnimatedVisibility(visible = isSearchVisible) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("notes_search_input"),
                    placeholder = {
                        Text(
                            text = strings.searchNotesPlaceholder,
                            fontSize = 13.sp,
                            color = onSurfaceVariantColor
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = primaryColor
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = onSurfaceVariantColor
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = borderColor,
                        focusedContainerColor = surfaceColor,
                        unfocusedContainerColor = surfaceColor,
                        focusedTextColor = onSurfaceColor,
                        unfocusedTextColor = onSurfaceColor
                    )
                )
            }

            // 3. Filter Row 1 (Two Big Segmented Tabs: "الكل" & "المفضلة ★")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // المفضلة ★
                val isFavSelected = mainTab == NotesMainTab.FAVORITES
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(22.dp))
                        .background(surfaceColor)
                        .border(
                            width = if (isFavSelected) 2.dp else 1.dp,
                            color = if (isFavSelected) primaryColor else borderColor,
                            shape = RoundedCornerShape(22.dp)
                        )
                        .clickable { mainTab = NotesMainTab.FAVORITES }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isAr) "المفضلة" else "Favorites",
                            color = if (isFavSelected) onSurfaceColor else onSurfaceVariantColor,
                            fontSize = 15.sp,
                            fontWeight = if (isFavSelected) FontWeight.Bold else FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (isFavSelected) Color(0xFFF59E0B) else onSurfaceVariantColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // الكل
                val isAllSelected = mainTab == NotesMainTab.ALL
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(22.dp))
                        .background(surfaceColor)
                        .border(
                            width = if (isAllSelected) 2.dp else 1.dp,
                            color = if (isAllSelected) primaryColor else borderColor,
                            shape = RoundedCornerShape(22.dp)
                        )
                        .clickable { mainTab = NotesMainTab.ALL }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isAr) "الكل" else "All",
                            color = if (isAllSelected) onSurfaceColor else onSurfaceVariantColor,
                            fontSize = 15.sp,
                            fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = null,
                            tint = if (isAllSelected) primaryColor else onSurfaceVariantColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 4. Filter Row 2: Unified Single Category Card/Box (مربع واحد للتصنيف يفتح قائمة منبثقة)
            var showCategoryFilterMenu by remember { mutableStateOf(false) }
            val categoryFilters = listOf(
                "الكل" to Icons.Default.GridView,
                "عام" to Icons.Default.Label,
                "عمل" to Icons.Default.Work,
                "شخصي" to Icons.Default.Person,
                "دراسة" to Icons.Default.Book,
                "صحة" to Icons.Default.Star,
                "تسوق" to Icons.Default.Label,
                "أخرى" to Icons.Default.Label
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Surface(
                    color = surfaceColor,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, primaryColor.copy(alpha = 0.65f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showCategoryFilterMenu = true }
                        .testTag("notes_category_filter_box")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Label,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "التصنيف:" else "Category:",
                                color = onSurfaceVariantColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = selectedCategoryFilter,
                                color = onSurfaceColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isAr) "اختر التصنيف ▾" else "Select ▾",
                                color = primaryColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                DropdownMenu(
                    expanded = showCategoryFilterMenu,
                    onDismissRequest = { showCategoryFilterMenu = false },
                    modifier = Modifier
                        .background(surfaceColor)
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                ) {
                    categoryFilters.forEach { (catName, catIcon) ->
                        val isSelected = selectedCategoryFilter == catName
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = catName,
                                    color = if (isSelected) primaryColor else onSurfaceColor,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = catIcon,
                                    contentDescription = null,
                                    tint = if (isSelected) primaryColor else onSurfaceVariantColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = primaryColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            onClick = {
                                selectedCategoryFilter = catName
                                showCategoryFilterMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 5. Notes List / Grid
            if (filteredNotes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        icon = Icons.Default.Edit,
                        title = if (searchQuery.isNotEmpty()) strings.noNotesFound else strings.noNotesSubtitle,
                        subtitle = ""
                    )
                }
            } else if (isGridView) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp
                ) {
                    items(filteredNotes, key = { it.id }) { note ->
                        NoteCardItem(
                            note = note,
                            isAr = isAr,
                            onClick = { onOpenNoteEditor?.invoke(note) },
                            onTogglePinned = { onTogglePinned(note) },
                            onToggleFavorite = { onToggleFavorite?.invoke(note) },
                            onDelete = { noteToDelete = note }
                        )
                    }
                }
            } else {
                // List View
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredNotes, key = { it.id }) { note ->
                        NoteCardItem(
                            note = note,
                            isAr = isAr,
                            onClick = { onOpenNoteEditor?.invoke(note) },
                            onTogglePinned = { onTogglePinned(note) },
                            onToggleFavorite = { onToggleFavorite?.invoke(note) },
                            onDelete = { noteToDelete = note }
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (noteToDelete != null) {
        ConfirmDeleteDialog(
            title = strings.deleteConfirmTitle,
            message = strings.deleteNoteConfirmMessage,
            onConfirm = {
                noteToDelete?.let { onDeleteNote(it) }
                noteToDelete = null
            },
            onDismiss = { noteToDelete = null }
        )
    }
}

// Note Card Component
@Composable
private fun NoteCardItem(
    note: NoteItem,
    isAr: Boolean,
    onClick: () -> Unit,
    onTogglePinned: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    val surfaceColor = MaterialTheme.colorScheme.surface
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val borderColor = MaterialTheme.colorScheme.outline
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant

    // Check if pinned to show highlight border
    val hasHighlightBorder = note.isPinned

    // Extract checklist lines if present
    val checklistLines = remember(note.content) {
        note.content.lines().filter {
            it.trim().startsWith("☑") || it.trim().startsWith("✅") ||
                    it.trim().startsWith("[ ]") || it.trim().startsWith("[x]")
        }.take(3)
    }

    // Calculate word count
    val wordCount = remember(note.content) {
        note.content.split("\\s+".toRegex()).count { it.isNotBlank() }
    }

    // Format date
    val formattedDate = remember(note.updatedAt) {
        SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date(note.updatedAt))
    }

    Surface(
        color = surfaceColor,
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (hasHighlightBorder) 2.dp else 1.dp,
            color = if (hasHighlightBorder) primaryColor else borderColor
        ),
        shadowElevation = if (hasHighlightBorder) 4.dp else 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .testTag("note_card_${note.id}")
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Top Row: Icons Bar + Category Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Tag Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(surfaceVariantColor)
                            .border(0.8.dp, borderColor, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = note.category.ifBlank { "عام" },
                            color = onSurfaceVariantColor,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Icons cluster + Interactive Favorite Star Button (زر مفضلة)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (!note.audioPath.isNullOrBlank()) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Audio",
                                tint = primaryColor,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        if (!note.imagePath.isNullOrBlank()) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Image",
                                tint = primaryColor,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        if (note.hasReminder) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = "Alarm",
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        if (note.isPinned) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "Pinned",
                                tint = primaryColor,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        // Direct Favorite Star Toggle Button on every note card (زر مفضلة ★)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (note.isFavorite) Color(0xFFF59E0B).copy(alpha = 0.2f) else surfaceVariantColor,
                            border = androidx.compose.foundation.BorderStroke(
                                0.9.dp,
                                if (note.isFavorite) Color(0xFFF59E0B) else borderColor
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onToggleFavorite() }
                                .testTag("note_fav_btn_${note.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (note.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = if (isAr) "مفضلة" else "Favorite",
                                    tint = if (note.isFavorite) Color(0xFFF59E0B) else onSurfaceVariantColor,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (isAr) "مفضلة" else "Fav",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (note.isFavorite) Color(0xFFF59E0B) else onSurfaceVariantColor
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Title
                Text(
                    text = note.title.ifBlank { if (isAr) "بدون عنوان" else "Untitled" },
                    color = onSurfaceColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Content Preview
                if (note.content.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = note.content.lines().filter { !it.trim().startsWith("☑") && !it.trim().startsWith("✅") }.joinToString("\n").trim(),
                        color = onSurfaceVariantColor,
                        fontSize = 12.5.sp,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 17.sp
                    )
                }

                // Checklist Embedded Container
                if (checklistLines.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(surfaceVariantColor)
                            .border(0.8.dp, borderColor, RoundedCornerShape(12.dp))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        checklistLines.forEach { line ->
                            val cleanLine = line.replace("☑", "").replace("✅", "").replace("[ ]", "").replace("[x]", "").trim()
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = cleanLine,
                                    color = onSurfaceColor,
                                    fontSize = 11.5.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Footer Row: Word Count, Date & Quick Pin/Delete Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Date & Word Count
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = formattedDate,
                            color = onSurfaceVariantColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            text = "• $wordCount ${if (isAr) "كلمة" else "words"}",
                            color = onSurfaceVariantColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = onTogglePinned,
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                imageVector = if (note.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                                contentDescription = "Pin",
                                tint = if (note.isPinned) primaryColor else onSurfaceVariantColor,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFEF4444).copy(alpha = 0.75f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // More Options Dropdown Button (long press / options)
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
            ) {
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(if (note.isPinned) (if (isAr) "إلغاء التثبيت" else "Unpin") else (if (isAr) "تثبيت في الأعلى" else "Pin to top")) },
                        onClick = {
                            showMenu = false
                            onTogglePinned()
                        },
                        leadingIcon = {
                            Icon(
                                if (note.isPinned) Icons.Outlined.PushPin else Icons.Default.PushPin,
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (note.isFavorite) (if (isAr) "إزالة من المفضلة" else "Remove from Favorites") else (if (isAr) "إضافة للمفضلة" else "Add to Favorites")) },
                        onClick = {
                            showMenu = false
                            onToggleFavorite()
                        },
                        leadingIcon = {
                            Icon(
                                if (note.isFavorite) Icons.Default.StarBorder else Icons.Default.Star,
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (isAr) "حذف الملاحظة" else "Delete Note", color = Color(0xFFEF4444)) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color(0xFFEF4444)
                            )
                        }
                    )
                }
            }
        }
    }
}
