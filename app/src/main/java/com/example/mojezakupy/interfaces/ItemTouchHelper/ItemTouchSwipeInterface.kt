package com.example.mojezakupy.interfaces.ItemTouchHelper

import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.adapters.CustomListAdapter
import com.example.mojezakupy.database.entity.TaskListEntity
import com.example.mojezakupy.viewmodel.ListViewModel

interface ItemTouchSwipeInterface {
    fun deleteToLeft()
    fun archiveToLeft(
        listsFromDb: MutableList<TaskListEntity>,
        listViewModel: ListViewModel?,
        listAdapterThis: CustomListAdapter?,
        recyclerView: RecyclerView,
        activity: FragmentActivity?
    ): ItemTouchHelper
}