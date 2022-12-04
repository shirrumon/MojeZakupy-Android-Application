package com.example.mojezakupy.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.R
import com.example.mojezakupy.adapters.CustomTaskListAdapter
import com.example.mojezakupy.database.entity.TaskEntity
import com.example.mojezakupy.database.entity.TaskListEntity
import com.example.mojezakupy.viewmodel.TaskViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ArchiveTaskFragment(
    private val listId: String,
    private val tasksSummary: String
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.task_archive_fragment, container, false)
        val taskViewModel: TaskViewModel? = activity?.let { TaskViewModel(it.applicationContext, listId.toInt()) }

        view.findViewById<TextView>(R.id.task_box_price_summary_archive).text = tasksSummary

        var listsFromDb = taskViewModel?.getAllInstances(listId.toInt())
        val currentAdapter = listsFromDb?.let { CustomTaskListAdapter(it) }

        val recyclerView: RecyclerView = view.findViewById(R.id.task_recycler_archive)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = currentAdapter

        if (taskViewModel != null) {
            taskViewModel.allTasksAsFlow.observe(viewLifecycleOwner, Observer {
                listsFromDb = taskViewModel?.getAllInstances(listId.toInt())
                recyclerView.adapter = CustomTaskListAdapter(it)
            })

            taskViewModel.summaryPrice.observe(viewLifecycleOwner, Observer {
                listsFromDb = taskViewModel?.getAllInstances(listId.toInt())
                view.findViewById<TextView>(R.id.task_box_price_summary_archive).text = it
            })
        }

//        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
//            override fun onMove(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder,
//                target: RecyclerView.ViewHolder
//            ): Boolean {
//                return false
//            }
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                val deletedCourse: TaskEntity? =
//                    listsFromDb?.get(viewHolder.adapterPosition)
//
//                listsFromDb?.get(viewHolder.adapterPosition)?.let { taskViewModel?.delete(it) }
//
//                currentAdapter?.notifyItemRemoved(viewHolder.adapterPosition)
//
//                if (deletedCourse != null) {
//                    Snackbar.make(
//                        recyclerView,
//                        "Usunąłeś " + deletedCourse.taskName,
//                        Snackbar.LENGTH_LONG
//                    ).show()
//                }
//            }
//        }).attachToRecyclerView(recyclerView)

        return view
    }
}