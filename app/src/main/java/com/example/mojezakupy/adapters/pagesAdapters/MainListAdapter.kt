package com.example.mojezakupy.adapters.pagesAdapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.R
import com.example.mojezakupy.database.entity.TaskListEntity
import com.example.mojezakupy.databinding.ListElementLayoutBinding
import com.example.mojezakupy.fragments.TaskListFragment

class MainListAdapter(private val activity: FragmentActivity) :
    ListAdapter<TaskListEntity, MainListAdapter.MainViewHolder>(ItemComparator()) {

    class MainViewHolder(private val binding: ListElementLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(taskList: TaskListEntity) = with(binding) {
            taskListName.text = taskList.listName
            listSubtitle.text = taskList.createDate
            taskListId.text = taskList.id.toString()
        }

        companion object {
            fun create(parent: ViewGroup): MainViewHolder {
                return MainViewHolder(
                    ListElementLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    class ItemComparator : DiffUtil.ItemCallback<TaskListEntity>() {
        override fun areItemsTheSame(oldItem: TaskListEntity, newItem: TaskListEntity): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: TaskListEntity, newItem: TaskListEntity): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            val listId = it.findViewById<TextView>(R.id.task_list_id).text.toString()

            val taskListFragment = TaskListFragment(listId, "0") //hotfix
            activity.supportFragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_container, taskListFragment)
                .addToBackStack(null)
                .commit()
        }
    }
}