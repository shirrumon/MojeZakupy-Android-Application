package com.example.mojezakupy.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_list")
data class TaskListEntity(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "list_name") val listName: String?,
    @ColumnInfo(name = "task_count") val taskCount: Int = 0,
    @ColumnInfo(name = "task_summary") val taskSummary: String? = "0",
    @ColumnInfo(name = "is_in_archive") var isInArchive: Boolean = false,
)