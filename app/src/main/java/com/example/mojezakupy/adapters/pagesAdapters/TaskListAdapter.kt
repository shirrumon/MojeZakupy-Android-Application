package com.example.mojezakupy.adapters.pagesAdapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.database.entity.TaskEntity
import com.example.mojezakupy.databinding.ListElementLayoutBinding

class TaskListAdapter :
    ListAdapter<TaskEntity, TaskListAdapter.MainViewHolder>(ItemComparator()) {

    class MainViewHolder(private val binding: ListElementLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(taskList: TaskEntity) = with(binding) {
            taskListName.text = taskList.taskName
            listSubtitle.text = "${taskList.taskPrice} Zł"
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

    class ItemComparator : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
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