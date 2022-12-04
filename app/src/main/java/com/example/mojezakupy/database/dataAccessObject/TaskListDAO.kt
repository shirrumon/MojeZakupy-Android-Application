package com.example.mojezakupy.database.dataAccessObject

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mojezakupy.database.entity.TaskListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskListDAO {
    @Query("SELECT * FROM task_list")
    fun getAll(): List<TaskListEntity>

    @Query("SELECT * FROM task_list WHERE id = :id")
    fun funGetListById(id: String): TaskListEntity?

    @Query("SELECT task_summary FROM task_list WHERE id = :listId")
    fun getSummaryPriceAsFlow(listId: Int): Flow<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: TaskListEntity)

    @Update
    suspend fun update(task: TaskListEntity)

    @Delete
    suspend fun delete(task: TaskListEntity)
}