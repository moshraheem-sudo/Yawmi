package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskPriority {
    HIGH,
    MEDIUM,
    LOW
}

enum class TaskCategory {
    GENERAL,
    WORK,
    PERSONAL,
    STUDY,
    HEALTH,
    SHOPPING,
    OTHER
}

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDate: Long? = null, // epoch millis for date (midnight or exact)
    val hasTime: Boolean = false,
    val dueTimeHour: Int? = null,
    val dueTimeMinute: Int? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val category: TaskCategory = TaskCategory.PERSONAL,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val isPinned: Boolean = false,
    val hasReminder: Boolean = false,
    val reminderTime: Long? = null, // epoch millis for notification alarm
    val createdAt: Long = System.currentTimeMillis()
)
