package com.example.mojezakupy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.R
import com.example.mojezakupy.database.entity.TaskListEntity
import com.example.mojezakupy.fragments.ArchiveTaskFragment
import com.example.mojezakupy.fragments.TaskListFragment

class CustomArchiveListAdapter(private val dataSet: List<TaskListEntity>, private val activity: FragmentActivity) :
    RecyclerView.Adapter<CustomArchiveListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        val taskCreateDate: TextView
        val hiddenId: TextView

        init {
            textView = view.findViewById(R.id.task_list_name)
            taskCreateDate = view.findViewById(R.id.list_subtitle)
            hiddenId = view.findViewById(R.id.task_list_id)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_element_layout, viewGroup, false)

        view.setOnClickListener{
            val listId = it.findViewById<TextView>(R.id.task_list_id).text.toString()

            val taskListFragment = ArchiveTaskFragment(listId, "0") //hotfix
            activity.supportFragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_container, taskListFragment)
                .addToBackStack(null)
                .commit()
        }

        return ViewHolder(view)
    }
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = dataSet[position].listName
        viewHolder.taskCreateDate.text = dataSet[position].createDate
        viewHolder.hiddenId.text = dataSet[position].id.toString()
    }
    override fun getItemCount() = dataSet.size
}