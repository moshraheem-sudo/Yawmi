package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.local.entity.ExpenseCategory
import com.example.data.local.entity.TaskCategory
import com.example.data.local.entity.TaskPriority

class Converters {
    @TypeConverter
    fun fromTaskPriority(priority: TaskPriority?): String = priority?.name ?: TaskPriority.MEDIUM.name

    @TypeConverter
    fun toTaskPriority(value: String?): TaskPriority =
        value?.let { runCatching { TaskPriority.valueOf(it) }.getOrNull() } ?: TaskPriority.MEDIUM

    @TypeConverter
    fun fromTaskCategory(category: TaskCategory?): String = category?.name ?: TaskCategory.PERSONAL.name

    @TypeConverter
    fun toTaskCategory(value: String?): TaskCategory =
        value?.let { runCatching { TaskCategory.valueOf(it) }.getOrNull() } ?: TaskCategory.PERSONAL

    @TypeConverter
    fun fromExpenseCategory(category: ExpenseCategory?): String = category?.name ?: ExpenseCategory.OTHER.name

    @TypeConverter
    fun toExpenseCategory(value: String?): ExpenseCategory =
        value?.let { runCatching { ExpenseCategory.valueOf(it) }.getOrNull() } ?: ExpenseCategory.OTHER
}
