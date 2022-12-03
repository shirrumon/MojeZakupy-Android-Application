package com.example.mojezakupy.database.dataAccessObject

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mojezakupy.database.entity.TaskEntity

@Dao
interface TaskDAO {
    @Query("SELECT * FROM tasks WHERE list_id = :listId")
    fun getAllByListId(listId: Int): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)
}