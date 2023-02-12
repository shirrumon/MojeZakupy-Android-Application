package com.example.mojezakupy.factory

import android.graphics.Canvas
import android.os.Build
import android.util.Log
import android.view.Gravity
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.R
import com.example.mojezakupy.adapters.CustomTaskListAdapter
import com.example.mojezakupy.adapters.pagesAdapters.MainListAdapter
import com.example.mojezakupy.database.entity.TaskEntity
import com.example.mojezakupy.database.entity.TaskListEntity
import com.example.mojezakupy.interfaces.ItemTouchHelper.ItemTouchSwipeInterface
import com.example.mojezakupy.repository.ListOfTasksRepository
import com.example.mojezakupy.viewmodel.TaskViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class ItemTouchSwipeFactory : ItemTouchSwipeInterface {
    override fun deleteToLeft(
        listFromDb: MutableList<TaskEntity>,
        taskViewModel: TaskViewModel?,
        currentAdapter: CustomTaskListAdapter?,
        recyclerView: RecyclerView,
        activity: FragmentActivity?
    ): ItemTouchHelper {
        val deleteSwipeHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedCourse: TaskEntity =
                    listFromDb[viewHolder.adapterPosition]

                (taskViewModel as TaskViewModel).delete(listFromDb[viewHolder.adapterPosition])

                currentAdapter?.notifyItemRemoved(viewHolder.adapterPosition)

                SnakeBarFactory().generateSnakeBar(
                    recyclerView,
                    "Usunąłeś",
                    deletedCourse.taskName,
                    Gravity.TOP,
                ).show()
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
        })

        return deleteSwipeHelper
    }

    override fun archiveToLeft(
        listsFromDb: MutableList<TaskListEntity>,
        repository: ListOfTasksRepository,
        listAdapterThis: MainListAdapter,
        recyclerView: RecyclerView,
        activity: FragmentActivity?
    ): ItemTouchHelper {
        Log.e("list init", listsFromDb.size.toString())
        val obj = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Log.e(
                    "list swipe",
                    listsFromDb.size.toString() + "pos: ${viewHolder.adapterPosition}"
                )
                val deletedCourse: TaskListEntity =
                    listsFromDb[viewHolder.adapterPosition]

                val position = viewHolder.adapterPosition

                repository.moveToArchive(listsFromDb[viewHolder.adapterPosition])

                listsFromDb.removeAt(viewHolder.adapterPosition)

                listAdapterThis.notifyItemRemoved(viewHolder.adapterPosition)

                SnakeBarFactory().generateSnakeBar(
                    recyclerView,
                    "Przemisciles do archiwum",
                    deletedCourse.listName,
                    Gravity.TOP,
                ).setAction(
                    "Cofnij"
                ) {
                    listsFromDb.add(position, deletedCourse)
                    repository.removeFromArchive(deletedCourse)
                    listAdapterThis.notifyItemInserted(position)
                }.show()
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
                        R.color.archive_swipe_background
                    )
                }?.let {
                    RecyclerViewSwipeDecorator.Builder(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                        .addBackgroundColor(
                            it
                        )
                        .addActionIcon(R.drawable.ic_baseline_archive_32)
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
        }

        return ItemTouchHelper(obj)
    }
}