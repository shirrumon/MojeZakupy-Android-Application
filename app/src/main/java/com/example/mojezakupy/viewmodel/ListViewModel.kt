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
    val list: LiveData<MutableList<TaskListEntity>> = appDatabase.taskListDAO().getAllAsFLow().asLiveData()

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

    fun getAllInstances(): MutableList<TaskListEntity> {
        return appDatabase.taskListDAO().getAll()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun moveToArchive(taskEntity: TaskListEntity) {
        taskEntity.isInArchive = true
        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.taskListDAO().update(taskEntity)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun removeFromArchive(taskEntity: TaskListEntity) {
        taskEntity.isInArchive = false
        GlobalScope.launch(Dispatchers.IO) {
            appDatabase.taskListDAO().update(taskEntity)
        }
    }

    fun getAllFromArchive(): MutableList<TaskListEntity> {
        return appDatabase.taskListDAO().getAllFromArchive()
    }
}