package com.example.mojezakupy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.R
import com.example.mojezakupy.database.entity.TaskEntity

class CustomTaskListAdapter(private val dataSet: List<TaskEntity>) :
    RecyclerView.Adapter<CustomTaskListAdapter.ViewHolder>() {
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

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CustomTaskListAdapter.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.task_element, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textName.text = dataSet[position].taskName
        viewHolder.taskPrice.text = dataSet[position].taskPrice.toString() + " zł"
        viewHolder.taskId.text = dataSet[position].id.toString()
    }

    override fun getItemCount() = dataSet.size
}