package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ExpenseCategory {
    STUDY_FEES,     // أجور دراسة
    CAR_FARE,       // أجرة سيارة
    HOUSEHOLD,      // مصروف بيت
    MEDICINE,       // علاجات وأدوية
    SHOPPING,       // تسوق
    RESTAURANTS,    // مطاعم
    MARKETS,        // أسواق
    TRAVEL,         // سفر
    DELIVERY,       // نقل وتوصيل
    GIFT,           // هدية
    DONATION,       // تبرع
    PARENTS_RIGHTS, // حقوق الوالدين
    FAMILY_EXPENSE, // مصروف الأهل
    FOOD,           // طعام ومشروبات
    TRANSPORT,      // مواصلات ووقود
    BILLS,          // فواتير وخدمات
    HEALTH,         // صحة وصيدلية
    ENTERTAINMENT,  // ترفيه
    OTHER           // أخرى
}

@Entity(tableName = "expenses")
data class ExpenseItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double, // in Iraqi Dinar (IQD)
    val category: ExpenseCategory = ExpenseCategory.OTHER,
    val date: Long = System.currentTimeMillis(),
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isIncome: Boolean = false
)
