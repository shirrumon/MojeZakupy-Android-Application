package com.example.mojezakupy.repository

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ListOfTasksRepository(applicationContext: Context) {
    private val appDatabase: AppDatabase = AppDatabase.getDatabase(applicationContext)
    val list: LiveData<MutableList<TaskListEntity>> = appDatabase.taskListDAO().getAll()
    val archiveList: LiveData<MutableList<TaskListEntity>> = appDatabase.taskListDAO().getArchiveList()

    private val appDatabaseTasks: AppDatabase = AppDatabase.getDatabase(applicationContext)

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(DelicateCoroutinesApi::class)
    fun saveNewList(listName: String) {
        val newListInstance = TaskListEntity(
            null,
            listName,
            0,
            0.0F,
            false,
            "standard",
            0.0F,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString(),
            ""
        )

        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.taskListDAO().insert(newListInstance)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(DelicateCoroutinesApi::class)
    fun moveToArchive(taskListEntity: TaskListEntity) {
        taskListEntity.isInArchive = true
        taskListEntity.dueDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString()
        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.taskListDAO().update(taskListEntity)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun removeFromArchive(taskListEntity: TaskListEntity) {
        taskListEntity.isInArchive = false
        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.taskListDAO().update(taskListEntity)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun delete(taskListEntity: TaskListEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.taskListDAO().delete(taskListEntity)
        }
    }

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
            appDatabaseTasks.tasksDAO().insert(task)
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
        val taskList = appDatabaseTasks.taskListDAO().getListById(listId)
        taskList.taskCount.plus(1)
        taskList.salary = (taskList.salary - taskPrice)
        val taskSummaryPrice = taskList.taskSummary.plus(taskPrice)
        taskList.taskSummary = taskSummaryPrice

        GlobalScope.launch(Dispatchers.IO) {
            appDatabaseTasks.taskListDAO().update(taskList)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun updateTaskListAfterTaskDelete(
        listId: Int,
        taskPrice: Float,
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val taskList = appDatabaseTasks.taskListDAO().getListById(listId)
            taskList.taskCount.minus(1)
            taskList.salary = (taskList.salary + taskPrice)
            val taskSummaryPrice = taskList.taskSummary.minus(taskPrice)
            taskList.taskSummary = taskSummaryPrice

            appDatabaseTasks.taskListDAO().update(taskList)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun delete(task: TaskEntity) {
        this.updateTaskListAfterTaskDelete(
            task.listId!!,
            task.taskPrice,
        )
        GlobalScope.launch(Dispatchers.IO) {
            appDatabaseTasks.tasksDAO().delete(task)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun changeCountType(listId: Int, type: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val taskList = appDatabaseTasks.taskListDAO().getListById(listId)
            taskList.countType = type
            appDatabaseTasks.taskListDAO().update(taskList)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun changeSalary(listId: Int, salary: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val taskList = appDatabaseTasks.taskListDAO().getListById(listId)
            taskList.salary = salary.toFloat()

            appDatabaseTasks.taskListDAO().update(taskList)
        }
    }

    fun taskList(listId: Int): LiveData<MutableList<TaskEntity>> {
        return appDatabaseTasks.tasksDAO().getAllByListId(listId)
    }

    fun summaryPrice(listId: Int): LiveData<Float> {
        return appDatabaseTasks.taskListDAO().getSummaryPriceAsFlow(listId).asLiveData()
    }

    fun countType(listId: Int): LiveData<String> {
        return appDatabaseTasks.taskListDAO().getCurrentType(listId).asLiveData()
    }

    fun salary(listId: Int): LiveData<Float> {
        return appDatabaseTasks.taskListDAO().getSalary(listId).asLiveData()
    }

    fun parentList(listId: Int): LiveData<TaskListEntity> {
        return appDatabaseTasks.taskListDAO().getListByIdAsLiveData(listId.toString())
    }
}