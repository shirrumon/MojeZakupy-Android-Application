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

class TaskViewModel(applicationContext: Context, listId: Int) : ViewModel() {
    private val appDatabase: AppDatabase = AppDatabase.getDatabase(applicationContext)
    val taskList: LiveData<MutableList<TaskEntity>> = appDatabase.tasksDAO().getAllByListId(listId)
    val summaryPrice: LiveData<String> = appDatabase.taskListDAO().getSummaryPriceAsFlow(listId).asLiveData()

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
        val taskList = appDatabase.taskListDAO().getListById(listId)
        val taskSummaryPrice = taskList.taskSummary?.toInt()?.plus(taskPrice)
        val updatedTaskListEntity = TaskListEntity(
            taskList.id,
            taskList.listName,
            taskList.taskCount.plus(1),
            taskSummaryPrice.toString()
        )

        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.taskListDAO().update(updatedTaskListEntity)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun updateTaskListAfterTaskDelete(
        listId: String,
        taskPrice: Int,
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val taskList = appDatabase.taskListDAO().getListById(listId)
            val taskSummaryPrice = taskList.taskSummary?.toInt()?.minus(taskPrice)
            val updatedTaskListEntity = TaskListEntity(
                taskList.id,
                taskList.listName,
                taskList.taskCount.minus(1),
                taskSummaryPrice.toString()
            )
            appDatabase.taskListDAO().update(updatedTaskListEntity)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun delete(task: TaskEntity) {
        this.updateTaskListAfterTaskDelete(
            task.listId.toString(),
            task.taskPrice.toInt(),
        )
        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.tasksDAO().delete(task)
        }
    }
}