package com.example.mojezakupy.adapters.pagesAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.database.entity.TaskListEntity
import com.example.mojezakupy.databinding.ListElementLayoutBinding

class MainListAdapter :
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
        holder.bind(getItem(position))
    }
}