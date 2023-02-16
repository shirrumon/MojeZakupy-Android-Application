package com.example.mojezakupy.fragments.pages

import android.graphics.Canvas
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.R
import com.example.mojezakupy.adapters.pagesAdapters.MainListAdapter
import com.example.mojezakupy.database.entity.TaskListEntity
import com.example.mojezakupy.databinding.FragmentMainListBinding
import com.example.mojezakupy.factory.SnakeBarFactory
import com.example.mojezakupy.repository.ListOfTasksRepository
import com.google.android.material.chip.Chip
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class ArchiveListFragment: Fragment() {
    private lateinit var listAdapter: MainListAdapter
    private lateinit var binding: FragmentMainListBinding
    private var taskListEntities: MutableList<TaskListEntity> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_list_archive, container, false)
        val repository = ListOfTasksRepository(requireActivity())
        binding = FragmentMainListBinding.inflate(inflater)

        initAdapterView(view, repository)

        return view
    }

    private fun initAdapterView(view: View?, repository: ListOfTasksRepository) {
        listAdapter = MainListAdapter(requireActivity(), "archive")
        val recyclerView: RecyclerView = view?.findViewById(R.id.archive_recycler)!!
        val emptyCommunicate = view.findViewById<Chip>(R.id.empty_list_communicate)
        recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = listAdapter

        repository.archiveList.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                emptyCommunicate.visibility = View.VISIBLE
            } else {
                emptyCommunicate.visibility = View.GONE
                listAdapter.submitList(it)
                taskListEntities = it
            }
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedList: TaskListEntity =
                    taskListEntities[viewHolder.absoluteAdapterPosition]
                val position = viewHolder.absoluteAdapterPosition

                SnakeBarFactory().generateSnakeBar(
                    recyclerView,
                    getString(R.string.messages_after_delete),
                    deletedList.listName,
                    Gravity.TOP,
                ).show()

                taskListEntities.removeAt(position)
                repository.delete(deletedList)
                listAdapter.notifyItemRemoved(position)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                activity?.let {
                    ContextCompat.getColor(
                        it.applicationContext,
                        R.color.delete_swipe_background
                    )
                }?.let {
                    RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(
                            it
                        )
                        .addActionIcon(R.drawable.ic_baseline_delete_32)
                        .addCornerRadius(1, 15)
                        .create()
                        .decorate()
                }
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }).attachToRecyclerView(recyclerView)
    }
}