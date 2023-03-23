package com.mainApp.mojezakupy.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "list_id") val listId: Int?,
    @ColumnInfo(name = "task_name") val taskName: String?,
    @ColumnInfo(name = "task_price") val taskPrice: Float = 0.0F,
)
