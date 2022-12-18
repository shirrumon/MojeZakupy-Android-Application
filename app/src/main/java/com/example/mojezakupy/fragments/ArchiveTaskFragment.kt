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
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
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

        view.findViewById<TextView>(R.id.task_box_price_summary_archive).text = "Razem: $tasksSummary zł"

        var listFromDb: MutableList<TaskEntity> = arrayListOf()
        var currentAdapter = CustomTaskListAdapter(listFromDb)

        val recyclerView: RecyclerView = view.findViewById(R.id.task_recycler_archive)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = currentAdapter

        taskViewModel?.taskList?.observe(viewLifecycleOwner) {
            listFromDb = it

            val emptyCommunicate = view.findViewById<Chip>(R.id.empty_list_communicate)
            if(listFromDb.isEmpty()) {
                emptyCommunicate.visibility = View.VISIBLE
            } else {
                emptyCommunicate.visibility = View.GONE
            }

            currentAdapter = CustomTaskListAdapter(listFromDb)
            recyclerView.adapter = currentAdapter
        }

        val topBar = view.findViewById<MaterialToolbar>(R.id.topAppBar)
        topBar.setOnClickListener{
            fragmentManager?.popBackStack()
        }

        taskViewModel?.parentList?.observe(viewLifecycleOwner) {
            view.findViewById<TextView>(R.id.parent_list_name).text = it.listName
        }
        return view
    }
}