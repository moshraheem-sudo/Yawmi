package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bills")
data class BillItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String = "اتصالات وإنترنت",
    val dueDayOfMonth: Int = 1,
    val dueMonth: Int = 0, // 0 = كل الشهور (شهرياً), 1..12 = شهر محدد
    val frequency: String = "شهرية",
    val priority: String = "مهم",
    val isPaid: Boolean = false,
    val isReserved: Boolean = true,
    val hasReminder: Boolean = false,
    val reminderTime: Long? = null,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
