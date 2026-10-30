package com.example.data.repository

import com.example.data.local.dao.TaskDao
import com.example.data.local.entity.TaskItem
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<TaskItem>> = taskDao.getAllTasks()

    fun getTasksForDateRange(startOfDay: Long, endOfDay: Long): Flow<List<TaskItem>> =
        taskDao.getTasksForDateRange(startOfDay, endOfDay)

    fun getUpcomingReminders(currentTime: Long): Flow<List<TaskItem>> =
        taskDao.getUpcomingReminders(currentTime)

    fun getTaskById(id: Long): Flow<TaskItem?> = taskDao.getTaskById(id)

    suspend fun getTaskByIdSync(id: Long): TaskItem? = taskDao.getTaskByIdSync(id)

    suspend fun getPendingRemindersSync(currentTime: Long): List<TaskItem> =
        taskDao.getPendingRemindersSync(currentTime)

    suspend fun insertTask(task: TaskItem): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: TaskItem) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskItem) = taskDao.deleteTask(task)

    suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)

    suspend fun toggleCompleted(id: Long, completed: Boolean) {
        val completedAt = if (completed) System.currentTimeMillis() else null
        taskDao.updateTaskCompletion(id, completed, completedAt)
    }

    suspend fun togglePinned(id: Long, pinned: Boolean) {
        taskDao.updateTaskPinned(id, pinned)
    }

    suspend fun deleteAllTasks() = taskDao.deleteAllTasks()
}
