package com.example.mojezakupy.adapters.pagesAdapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.R
import com.example.mojezakupy.database.entity.TaskListEntity
import com.example.mojezakupy.databinding.ListElementLayoutBinding
import com.example.mojezakupy.fragments.subpages.TaskListFragment

class MainListAdapter(private val activity: FragmentActivity) :
    ListAdapter<TaskListEntity, MainListAdapter.MainViewHolder>(ItemComparator()) {

    class MainViewHolder(private val binding: ListElementLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(taskList: TaskListEntity) = with(binding) {
            taskListName.text = taskList.listName
            listSubtitle.text = taskList.createDate
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
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            item.id?.let { id -> bundle.putInt("listId", id) }
            bundle.putFloat("tasksSummary", item.taskSummary)
            bundle.putString("parentListName", item.listName)

            val taskListFragment = TaskListFragment()
            taskListFragment.arguments = bundle

            activity.supportFragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_container, taskListFragment)
                .addToBackStack(null)
                .commit()
        }
    }
}