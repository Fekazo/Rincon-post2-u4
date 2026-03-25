package com.universidad.taskmanager.data

import com.universidad.taskmanager.data.local.TaskDao
import com.universidad.taskmanager.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: TaskDao) {

    fun getAllTasks(): Flow<List<TaskEntity>> = dao.observeAll()

    fun getTasksByStatus(completed: Boolean) = dao.observeByStatus(completed)

    suspend fun addTask(title: String, description: String) =
        dao.insert(TaskEntity(title = title, description = description))

    suspend fun toggleComplete(id: Int, completed: Boolean) =
        dao.toggleComplete(id, completed)

    suspend fun deleteTask(task: TaskEntity) =
        dao.delete(task)
}