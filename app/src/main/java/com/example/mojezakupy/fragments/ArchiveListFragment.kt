package com.example.mojezakupy.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.R
import com.example.mojezakupy.adapters.CustomArchiveListAdapter
import com.example.mojezakupy.database.entity.TaskEntity
import com.example.mojezakupy.database.entity.TaskListEntity
import com.example.mojezakupy.factory.SnakeBarFactory
import com.example.mojezakupy.viewmodel.ListViewModel

class ArchiveListFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_archive, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.archive_recycler)
        recyclerView.layoutManager =
            LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false
            )

        val listViewModel = activity?.let { ListViewModel(it.applicationContext) }

        var listsFromDb: MutableList<TaskListEntity> = arrayListOf()
        var listAdapterThis = activity?.let { CustomArchiveListAdapter(listsFromDb, it) }
        recyclerView.adapter = listAdapterThis

        listViewModel?.archiveList?.observe(viewLifecycleOwner) { taskList ->
            listsFromDb = taskList
            listAdapterThis = activity?.let { CustomArchiveListAdapter(listsFromDb, it) }
            recyclerView.adapter = listAdapterThis
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedCourse: TaskListEntity =
                    listsFromDb.get(viewHolder.adapterPosition)

                listViewModel?.delete(listsFromDb.get(viewHolder.adapterPosition))

                listAdapterThis?.notifyItemRemoved(viewHolder.adapterPosition)

                SnakeBarFactory().generateSnakeBar(
                    recyclerView,
                    "Usunąłeś",
                    deletedCourse.listName,
                    Gravity.TOP,
                ).show()
            }
        }).attachToRecyclerView(recyclerView)

        return view
    }
}