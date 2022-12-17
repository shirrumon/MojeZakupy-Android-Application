package com.example.mojezakupy.fragments

import android.content.DialogInterface
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.R
import com.example.mojezakupy.adapters.CustomListAdapter
import com.example.mojezakupy.database.entity.TaskListEntity
import com.example.mojezakupy.factory.SnakeBarFactory
import com.example.mojezakupy.viewmodel.ListViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class ListFragment : Fragment() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        val listViewModel = activity?.let { ListViewModel(it.applicationContext) }

        val recyclerView: RecyclerView = view.findViewById(R.id.dashboard_recycler)
        recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        var listsFromDb: MutableList<TaskListEntity> = arrayListOf()
        var listAdapterThis = activity?.let { CustomListAdapter(listsFromDb, it) }
        recyclerView.adapter = listAdapterThis

        listViewModel?.list?.observe(viewLifecycleOwner) { listEntities ->
            Log.d("test", "ok")
            listsFromDb = listEntities
            listAdapterThis = activity?.let { CustomListAdapter(listsFromDb, it) }
            recyclerView.adapter = listAdapterThis
        }

        view.findViewById<FloatingActionButton>(R.id.add_list_explain_button).setOnClickListener{
            context?.let { it1 ->
                val viewOfInput = inflater.inflate(R.layout.create_list_input, null)
                val dialog = MaterialAlertDialogBuilder(it1)
                    .setTitle(resources.getString(R.string.create_list_header_dialog))
                    .setView(viewOfInput)
                    .setNegativeButton(resources.getString(R.string.decline), null)
                    .setPositiveButton(resources.getString(R.string.accept), null)
                    .create()

                dialog.show()
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                    val inputWrapper = viewOfInput.findViewById<TextInputLayout>(R.id.dialog_new_list_wrapper)
                    val input = viewOfInput.findViewById<TextInputEditText>(R.id.list_new_name)
                    if(input.text.toString().isEmpty()){
                        inputWrapper.error = getString(R.string.error_empty_input)
                    } else {
                        listViewModel?.saveNewList(input.text.toString())
                        dialog.dismiss()
                    }
                }
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
                val deletedCourse: TaskListEntity =
                    listsFromDb.get(viewHolder.adapterPosition)

                val position = viewHolder.adapterPosition

                listViewModel?.moveToArchive(listsFromDb.get(viewHolder.adapterPosition))

                listsFromDb.removeAt(viewHolder.adapterPosition)

                listAdapterThis?.notifyItemRemoved(viewHolder.adapterPosition)

                SnakeBarFactory().generateSnakeBar(
                    recyclerView,
                    "Przemisciles do archiwum",
                    deletedCourse.listName,
                    Gravity.TOP,
                ).setAction(
                    "Cofnij"
                ) {
                    listsFromDb.add(position, deletedCourse)
                    listViewModel?.removeFromArchive(deletedCourse)
                    listAdapterThis?.notifyItemInserted(position)
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
                    RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
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
        }).attachToRecyclerView(recyclerView)

        return view
    }
}