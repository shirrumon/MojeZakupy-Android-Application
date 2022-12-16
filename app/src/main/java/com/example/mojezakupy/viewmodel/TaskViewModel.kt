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
    val countType: LiveData<String> = appDatabase.taskListDAO().getCurrentType(listId).asLiveData()
    val salary: LiveData<Int> = appDatabase.taskListDAO().getSalary(listId).asLiveData()

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
    private fun updateTaskList(
        listId: String,
        taskPrice: Int,
    ) {
        val taskList = appDatabase.taskListDAO().getListById(listId)
        taskList.taskCount.plus(1)
        taskList.salary = (taskList.salary - taskPrice)
        val taskSummaryPrice = taskList.taskSummary?.toInt()?.plus(taskPrice)
        taskList.taskSummary = taskSummaryPrice.toString()

        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.taskListDAO().update(taskList)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun updateTaskListAfterTaskDelete(
        listId: String,
        taskPrice: Int,
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val taskList = appDatabase.taskListDAO().getListById(listId)
            taskList.taskCount.minus(1)
            taskList.salary = (taskList.salary + taskPrice)
            val taskSummaryPrice = taskList.taskSummary?.toInt()?.minus(taskPrice)
            taskList.taskSummary = taskSummaryPrice.toString()

            appDatabase.taskListDAO().update(taskList)
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

    @OptIn(DelicateCoroutinesApi::class)
    fun changeCountType(listId: String, type: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val taskList = appDatabase.taskListDAO().getListById(listId)
            taskList.countType = type
            appDatabase.taskListDAO().update(taskList)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun changeSalary(listId: String, salary: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val taskList = appDatabase.taskListDAO().getListById(listId)
            taskList.salary = salary.toInt()

            appDatabase.taskListDAO().update(taskList)
        }
    }
}