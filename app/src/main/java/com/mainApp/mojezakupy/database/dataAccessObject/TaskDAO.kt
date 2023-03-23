package com.mainApp.mojezakupy.database.dataAccessObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mainApp.mojezakupy.database.entity.TaskEntity
import com.mainApp.mojezakupy.models.SimilarTaskModel

@Dao
interface TaskDAO {
    @Query("SELECT * FROM tasks WHERE list_id = :listId")
    fun getAllByListId(listId: Int): LiveData<MutableList<TaskEntity>>

    @Query(
        "SELECT task_name, task_price, COUNT(tasks.task_name) AS count FROM tasks " +
                "INNER JOIN task_list ON task_list.id = tasks.list_id " +
                "WHERE task_list.is_in_archive = 1 AND task_list.create_date BETWEEN DATE(DATE(), '-30 day') AND DATE(DATE(), '+1 day') " +
                "GROUP BY tasks.task_name, tasks.task_price " +
                "ORDER BY count(*) DESC LIMIT :maxProductsCount;"
    )
    fun getAllFromLastMonth(maxProductsCount: Int = 5): LiveData<MutableList<SimilarTaskModel>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)
}