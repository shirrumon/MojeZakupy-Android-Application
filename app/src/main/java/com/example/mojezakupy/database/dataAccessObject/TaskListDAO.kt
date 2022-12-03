package com.example.mojezakupy.database.dataAccessObject

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mojezakupy.database.entity.TaskListEntity

@Dao
interface TaskListDAO {
    @Query("SELECT * FROM task_list")
    fun getAll(): List<TaskListEntity>

    @Query("SELECT * FROM task_list WHERE id = :id")
    fun funGetListById(id: String): TaskListEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: TaskListEntity)

    @Update
    suspend fun update(task: TaskListEntity)

    @Delete
    suspend fun delete(task: TaskListEntity)
}