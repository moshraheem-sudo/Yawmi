package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.BillItem
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {
    @Query("SELECT * FROM bills ORDER BY dueDayOfMonth ASC, id ASC")
    fun getAllBills(): Flow<List<BillItem>>

    @Query("SELECT * FROM bills ORDER BY dueDayOfMonth ASC, id ASC")
    suspend fun getAllBillsSync(): List<BillItem>

    @Query("SELECT * FROM bills WHERE id = :id LIMIT 1")
    suspend fun getBillByIdSync(id: Long): BillItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: BillItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBills(bills: List<BillItem>)

    @Update
    suspend fun updateBill(bill: BillItem)

    @Delete
    suspend fun deleteBill(bill: BillItem)

    @Query("UPDATE bills SET isPaid = :isPaid WHERE id = :id")
    suspend fun setBillPaid(id: Long, isPaid: Boolean)

    @Query("SELECT * FROM bills WHERE hasReminder = 1 AND isPaid = 0 AND reminderTime IS NOT NULL AND reminderTime > :now")
    suspend fun getPendingBillRemindersSync(now: Long): List<BillItem>

    @Query("SELECT * FROM bills WHERE isPaid = 0")
    suspend fun getUnpaidBillsSync(): List<BillItem>

    @Query("DELETE FROM bills")
    suspend fun deleteAllBills()
}
