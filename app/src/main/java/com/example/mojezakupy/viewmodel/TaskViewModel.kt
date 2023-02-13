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
import java.math.RoundingMode
import java.text.DecimalFormat

class TaskViewModel(applicationContext: Context, listId: Int) : ViewModel() {
    private val appDatabase: AppDatabase = AppDatabase.getDatabase(applicationContext)
    private val df = DecimalFormat("#.##")
    val taskList: LiveData<MutableList<TaskEntity>> = appDatabase.tasksDAO().getAllByListId(listId)
    val summaryPrice: LiveData<Float> = appDatabase.taskListDAO().getSummaryPriceAsFlow(listId).asLiveData()
    val countType: LiveData<String> = appDatabase.taskListDAO().getCurrentType(listId).asLiveData()
    val salary: LiveData<Float> = appDatabase.taskListDAO().getSalary(listId).asLiveData()
    val parentList: LiveData<TaskListEntity> = appDatabase.taskListDAO().getListByIdAsLiveData(listId.toString())

    @OptIn(DelicateCoroutinesApi::class)
    fun createTask(
        listId: Int,
        taskName: String,
        taskPrice: Float,
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
                listId,
                taskPrice,
            )
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun updateTaskList(
        listId: Int,
        taskPrice: Float,
    ) {
        df.roundingMode = RoundingMode.DOWN
        val taskList = appDatabase.taskListDAO().getListById(listId)
        taskList.taskCount.plus(1)
        taskList.salary = (taskList.salary - taskPrice)
        val taskSummaryPrice = taskList.taskSummary.plus(taskPrice)
        taskList.taskSummary = taskSummaryPrice

        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.taskListDAO().update(taskList)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun updateTaskListAfterTaskDelete(
        listId: Int,
        taskPrice: Float,
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val taskList = appDatabase.taskListDAO().getListById(listId)
            taskList.taskCount.minus(1)
            taskList.salary = (taskList.salary + taskPrice)
            val taskSummaryPrice = taskList.taskSummary.minus(taskPrice)
            taskList.taskSummary = taskSummaryPrice

            appDatabase.taskListDAO().update(taskList)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun delete(task: TaskEntity) {
        this.updateTaskListAfterTaskDelete(
            task.listId!!,
            task.taskPrice,
        )
        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.tasksDAO().delete(task)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun changeCountType(listId: Int, type: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val taskList = appDatabase.taskListDAO().getListById(listId)
            taskList.countType = type
            appDatabase.taskListDAO().update(taskList)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun changeSalary(listId: Int, salary: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val taskList = appDatabase.taskListDAO().getListById(listId)
            taskList.salary = salary.toFloat()

            appDatabase.taskListDAO().update(taskList)
        }
    }
}