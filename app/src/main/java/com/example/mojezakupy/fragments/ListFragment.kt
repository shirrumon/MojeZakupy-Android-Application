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
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.R
import com.example.mojezakupy.database.entity.TaskEntity
import com.example.mojezakupy.database.entity.TaskListEntity
import com.example.mojezakupy.viewmodel.TaskViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ListFragment(
    private val listId: String,
    private val tasksSummary: String
    ) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        val taskViewModel: TaskViewModel? = activity?.let { TaskViewModel(it.applicationContext) }

        view.findViewById<TextView>(R.id.task_box_price_summary).text = tasksSummary

        val recyclerView: RecyclerView = view.findViewById(R.id.task_recycler)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = taskViewModel?.getAllInstances(listId.toInt())?.let { CustomTaskAdapter(it) }

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
                this.createTask(
                    taskViewModel,
                    listId,
                    taskName.text.toString(),
                    taskPrice.text.toString(),
                )
            }
        }
    }

    private fun createTask(
        taskViewModel: TaskViewModel?,
        listId: String,
        taskName: String,
        taskPrice: String,
    ) {
        taskViewModel?.createTask(
            listId.toInt(),
            taskName,
            taskPrice
        )

        reloadFragment(taskPrice)
    }

    private fun reloadFragment(
        taskPrice: String
    ) {
        val summaryPrice = tasksSummary.toInt() + taskPrice.toInt()
        val listFragment = ListFragment(listId, summaryPrice.toString())
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.fragment_container, listFragment)
            ?.addToBackStack(null)
            ?.commit()
    }
}

class CustomTaskAdapter(private val dataSet: List<TaskEntity>) :
    RecyclerView.Adapter<CustomTaskAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textName: TextView
        val taskPrice: TextView
        val taskId: TextView

        init {
            textName = view.findViewById(R.id.task_name)
            taskPrice = view.findViewById(R.id.task_price)
            taskId = view.findViewById(R.id.task_id)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CustomTaskAdapter.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.task_element, viewGroup, false)

        //view.setOn
        //val taskId: TextView = it.findViewById(R.id.task_id)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textName.text = dataSet[position].taskName
        viewHolder.taskPrice.text = dataSet[position].taskPrice.toString() + " zł"
        viewHolder.taskId.text = dataSet[position].id.toString()
    }

    override fun getItemCount() = dataSet.size
}
