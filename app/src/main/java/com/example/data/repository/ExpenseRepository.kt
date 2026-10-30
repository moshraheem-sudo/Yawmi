package com.example.data.repository

import com.example.data.local.dao.ExpenseDao
import com.example.data.local.entity.ExpenseItem
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    val allExpenses: Flow<List<ExpenseItem>> = expenseDao.getAllExpenses()

    fun getExpensesBetweenDates(startDate: Long, endDate: Long): Flow<List<ExpenseItem>> =
        expenseDao.getExpensesBetweenDates(startDate, endDate)

    fun getTotalExpensesBetweenDates(startDate: Long, endDate: Long): Flow<Double> =
        expenseDao.getTotalExpensesBetweenDates(startDate, endDate)

    suspend fun insertExpense(expense: ExpenseItem): Long = expenseDao.insertExpense(expense)

    suspend fun updateExpense(expense: ExpenseItem) = expenseDao.updateExpense(expense)

    suspend fun deleteExpense(expense: ExpenseItem) = expenseDao.deleteExpense(expense)

    suspend fun deleteExpenseById(id: Long) = expenseDao.deleteExpenseById(id)

    suspend fun deleteAllExpensesOnly() = expenseDao.deleteAllExpensesOnly()

    suspend fun deleteAllIncomesOnly() = expenseDao.deleteAllIncomesOnly()

    suspend fun deleteAllExpenses() = expenseDao.deleteAllExpenses()
}
