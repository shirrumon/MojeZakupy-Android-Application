package com.example.mojezakupy.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.mojezakupy.database.AppDatabase
import com.example.mojezakupy.database.entity.TaskEntity
import com.example.mojezakupy.database.entity.TaskListEntity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TaskViewModel(applicationContext: Context, listed: Int) : ViewModel() {
    private val appDatabase: AppDatabase = AppDatabase.getDatabase(applicationContext)
    val allTasksAsFlow: LiveData<List<TaskEntity>> = appDatabase.tasksDAO().getAllByListIdAsFlow(listed).asLiveData()
    val summaryPrice: LiveData<String> = appDatabase.taskListDAO().getSummaryPriceAsFlow(listed).asLiveData()

    @OptIn(DelicateCoroutinesApi::class)
    fun createTask(
        listId: Int,
        taskName: String,
        taskPrice: String,
    ) {
        val task = TaskEntity(
            null,
            listId,
            taskName,
            taskPrice
        )

        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.tasksDAO().insert(task)
            updateTaskList(
                listId.toString(),
                taskPrice.toInt(),
            )
        }
    }

    fun getAllInstances(listId: Int): List<TaskEntity> {
        return appDatabase.tasksDAO().getAllByListId(listId)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun deleteTask(task: TaskEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.tasksDAO().delete(task)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun updateTaskList(
        listId: String,
        taskPrice: Int,
    ) {
        val taskList = appDatabase.taskListDAO().funGetListById(listId)
        val taskSummaryPrice = taskList?.taskSummary?.toInt()?.plus(taskPrice)
        val updatedTaskListEntity = TaskListEntity(
            taskList?.id,
            taskList?.listName,
            taskList?.taskCount?.plus(1) ?: 1,
            taskSummaryPrice.toString()
        )

        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.taskListDAO().update(updatedTaskListEntity)
        }
    }
}