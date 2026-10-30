package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.YawmiDatabase
import com.example.data.local.entity.ExpenseCategory
import com.example.data.local.entity.ExpenseItem
import com.example.data.local.entity.NoteItem
import com.example.data.local.entity.TaskCategory
import com.example.data.local.entity.TaskItem
import com.example.data.local.entity.TaskPriority
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TaskManagementTest {

    private lateinit var database: YawmiDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, YawmiDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testInsertAndRetrieveTask() = runBlocking {
        val task = TaskItem(
            title = "Complete Android Project",
            description = "Build Yawmi app with Room and Compose",
            priority = TaskPriority.HIGH,
            category = TaskCategory.WORK,
            isPinned = true
        )

        val id = database.taskDao().insertTask(task)
        assertTrue(id > 0)

        val tasks = database.taskDao().getAllTasks().first()
        assertEquals(1, tasks.size)
        val retrieved = tasks[0]
        assertEquals("Complete Android Project", retrieved.title)
        assertEquals(TaskPriority.HIGH, retrieved.priority)
        assertTrue(retrieved.isPinned)
    }

    @Test
    fun testTaskCompletionToggle() = runBlocking {
        val task = TaskItem(title = "Buy Groceries", category = TaskCategory.SHOPPING)
        val id = database.taskDao().insertTask(task)

        database.taskDao().updateTaskCompletion(id, true, System.currentTimeMillis())

        val retrieved = database.taskDao().getTaskByIdSync(id)
        assertNotNull(retrieved)
        assertTrue(retrieved!!.isCompleted)
        assertNotNull(retrieved.completedAt)
    }

    @Test
    fun testNotePersistenceAndSearch() = runBlocking {
        val note1 = NoteItem(title = "Meeting Notes", content = "Discuss Q4 roadmap", colorHex = "#7C3AED")
        val note2 = NoteItem(title = "Shopping List", content = "Milk, Bread, Eggs", colorHex = "#10B981")

        database.noteDao().insertNote(note1)
        database.noteDao().insertNote(note2)

        val searchResult = database.noteDao().searchNotes("roadmap").first()
        assertEquals(1, searchResult.size)
        assertEquals("Meeting Notes", searchResult[0].title)
    }

    @Test
    fun testExpenseTrackingSum() = runBlocking {
        val now = System.currentTimeMillis()
        val exp1 = ExpenseItem(title = "Lunch", amount = 15000.0, category = ExpenseCategory.FOOD, date = now)
        val exp2 = ExpenseItem(title = "Fuel", amount = 25000.0, category = ExpenseCategory.TRANSPORT, date = now)

        database.expenseDao().insertExpense(exp1)
        database.expenseDao().insertExpense(exp2)

        val start = now - 10000
        val end = now + 10000
        val total = database.expenseDao().getTotalExpensesBetweenDates(start, end).first()
        assertEquals(40000.0, total, 0.01)
    }
}
