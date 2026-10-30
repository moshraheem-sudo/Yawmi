package com.example.data.repository

import com.example.data.local.dao.BillDao
import com.example.data.local.entity.BillItem
import kotlinx.coroutines.flow.Flow

class BillRepository(private val billDao: BillDao) {
    val allBills: Flow<List<BillItem>> = billDao.getAllBills()

    suspend fun insertBill(bill: BillItem): Long = billDao.insertBill(bill)

    suspend fun insertBills(bills: List<BillItem>) = billDao.insertBills(bills)

    suspend fun updateBill(bill: BillItem) = billDao.updateBill(bill)

    suspend fun deleteBill(bill: BillItem) = billDao.deleteBill(bill)

    suspend fun setBillPaid(id: Long, isPaid: Boolean) = billDao.setBillPaid(id, isPaid)

    suspend fun deleteAllBills() = billDao.deleteAllBills()
}
