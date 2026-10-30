package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val colorHex: String = "#7C3AED", // Hex color representation for the note
    val isPinned: Boolean = false,
    val imagePath: String? = null,
    val pdfPath: String? = null,
    val pdfName: String? = null,
    val audioPath: String? = null,
    val audioDurationMs: Long = 0L,
    val hasReminder: Boolean = false,
    val reminderTime: Long? = null,
    val category: String = "عام",
    val isFavorite: Boolean = false,
    val fontSize: Int = 15,
    val textAlign: String = "right",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
