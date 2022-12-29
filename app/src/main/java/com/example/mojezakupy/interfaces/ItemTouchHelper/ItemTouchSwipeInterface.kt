package com.example.mojezakupy.interfaces.ItemTouchHelper

import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.adapters.CustomListAdapter
import com.example.mojezakupy.adapters.CustomTaskListAdapter
import com.example.mojezakupy.database.entity.TaskEntity
import com.example.mojezakupy.database.entity.TaskListEntity
import com.example.mojezakupy.viewmodel.ListViewModel
import com.example.mojezakupy.viewmodel.TaskViewModel

interface ItemTouchSwipeInterface {
    fun deleteToLeft(
        listFromDb: MutableList<TaskEntity>,
        taskViewModel: TaskViewModel?,
        currentAdapter: CustomTaskListAdapter?,
        recyclerView: RecyclerView,
        activity: FragmentActivity?
    ): ItemTouchHelper

    fun archiveToLeft(
        listsFromDb: MutableList<TaskListEntity>,
        listViewModel: ListViewModel?,
        listAdapterThis: CustomListAdapter?,
        recyclerView: RecyclerView,
        activity: FragmentActivity?
    ): ItemTouchHelper
}