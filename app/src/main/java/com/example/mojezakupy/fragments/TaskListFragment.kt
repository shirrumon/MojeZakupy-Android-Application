package com.example.mojezakupy.fragments

import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.R
import com.example.mojezakupy.adapters.CustomTaskListAdapter
import com.example.mojezakupy.viewmodel.TaskViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class TaskListFragment(
    private val listId: String,
    private val tasksSummary: String
    ) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        val taskViewModel: TaskViewModel? = activity?.let { TaskViewModel(it.applicationContext, listId.toInt()) }

        view.findViewById<TextView>(R.id.task_box_price_summary).text = tasksSummary

        val recyclerView: RecyclerView = view.findViewById(R.id.task_recycler)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = taskViewModel?.getAllInstances(listId.toInt())?.let { CustomTaskListAdapter(it) }

        if (taskViewModel != null) {
            taskViewModel.allTasksAsFlow.observe(viewLifecycleOwner, Observer {
                recyclerView.adapter = CustomTaskListAdapter(it)
            })

            taskViewModel.summaryPrice.observe(viewLifecycleOwner, Observer {
                view.findViewById<TextView>(R.id.task_box_price_summary).text = it
            })
        }

        this.setListeners(view, taskViewModel, listId)
        return view
    }

    private fun setListeners(
        view: View,
        taskViewModel: TaskViewModel?,
        listId: String,
    ) {
        view.findViewById<FloatingActionButton>(R.id.add_task_explain_button).setOnClickListener{
            val addTaskForm: LinearLayout = view.findViewById(R.id.task_create_wrapper)
            if(addTaskForm.visibility == View.GONE) {
                addTaskForm.visibility = View.VISIBLE
            } else {
                addTaskForm.visibility = View.GONE
            }
        }

        view.findViewById<Button>(R.id.create_new_task_button_in_form).setOnClickListener {
            val taskName: TextView = view.findViewById(R.id.add_new_task_name)
            val taskPrice: TextView = view.findViewById(R.id.add_new_task_price)

            if(taskName.text.toString().isEmpty() || taskPrice.text.toString().isEmpty()){
                lifecycleScope.launch {
                    Toast.makeText(activity, "Pole nie może być puste!",
                        Toast.LENGTH_LONG).show()
                }
            } else {
                taskViewModel?.createTask(
                    listId.toInt(),
                    taskName.text.toString(),
                    taskPrice.text.toString()
                )
            }
        }
    }
}