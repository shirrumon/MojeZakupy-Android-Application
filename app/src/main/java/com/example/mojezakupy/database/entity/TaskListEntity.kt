package com.example.mojezakupy.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_list")
data class TaskListEntity(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "list_name") val listName: String?,
    @ColumnInfo(name = "task_count") val taskCount: Int = 0,
    @ColumnInfo(name = "task_summary") var taskSummary: String? = "0",
    @ColumnInfo(name = "is_in_archive") var isInArchive: Boolean = false,
    @ColumnInfo(name = "count_type") var countType: String = "standard",
    @ColumnInfo(name = "salary") var salary: Int = 0,
    @ColumnInfo(name = "create_date") var createDate: String = "",
    @ColumnInfo(name = "due_date") var dueDate: String = ""
)