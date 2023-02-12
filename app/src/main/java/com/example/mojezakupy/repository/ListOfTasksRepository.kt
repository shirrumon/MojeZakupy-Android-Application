package com.example.mojezakupy.repository

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import com.example.mojezakupy.database.AppDatabase
import com.example.mojezakupy.database.entity.TaskListEntity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ListOfTasksRepository(applicationContext: Context) {
    private val appDatabase: AppDatabase = AppDatabase.getDatabase(applicationContext)
    val list: LiveData<MutableList<TaskListEntity>> = appDatabase.taskListDAO().getAll()
    val archiveList: LiveData<MutableList<TaskListEntity>> = appDatabase.taskListDAO().getArchiveList()

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
}