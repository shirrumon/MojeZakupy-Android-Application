package com.example.mojezakupy.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.mojezakupy.database.AppDatabase
import com.example.mojezakupy.database.entity.TaskListEntity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ListViewModel(applicationContext: Context) : ViewModel() {
    private val appDatabase: AppDatabase = AppDatabase.getDatabase(applicationContext)
    val list: LiveData<MutableList<TaskListEntity>> = appDatabase.taskListDAO().getAll()
    val archiveList: LiveData<MutableList<TaskListEntity>> = appDatabase.taskListDAO().getArchiveList()

    @OptIn(DelicateCoroutinesApi::class)
    fun saveNewList(listName: String) {
        val newListInstance = TaskListEntity(
            null,
            listName,
            0
        )

        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.taskListDAO().insert(newListInstance)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun moveToArchive(taskListEntity: TaskListEntity) {
        taskListEntity.isInArchive = true
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