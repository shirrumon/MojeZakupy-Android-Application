package com.example.mojezakupy.fragments.pages

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
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
import com.example.mojezakupy.adapters.pagesAdapters.MainListAdapter
import com.example.mojezakupy.database.entity.TaskListEntity
import com.example.mojezakupy.databinding.FragmentMainListBinding
import com.example.mojezakupy.factory.AlertDialogFactory
import com.example.mojezakupy.factory.SnakeBarFactory
import com.example.mojezakupy.repository.ListOfTasksRepository
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class ListFragment : Fragment() {
    private lateinit var listAdapter: MainListAdapter
    private lateinit var binding: FragmentMainListBinding
    private var taskListEntities: MutableList<TaskListEntity> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_main_list, container, false)
        val repository = ListOfTasksRepository(requireActivity())
        binding = FragmentMainListBinding.inflate(inflater)

        initCreateDialog(view, inflater, repository)
        initAdapterView(view, repository)
        return view
    }

    private fun initAdapterView(view: View?, repository: ListOfTasksRepository) = with(binding) {
        listAdapter = MainListAdapter(requireActivity())
        val recyclerView: RecyclerView = view?.findViewById(R.id.dashboard_recycler)!!
        val emptyCommunicate = view.findViewById<Chip>(R.id.empty_list_communicate)
        recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = listAdapter

        repository.list.observe(viewLifecycleOwner) {
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

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val archivedList: TaskListEntity =
                    taskListEntities[viewHolder.absoluteAdapterPosition]
                val position = viewHolder.absoluteAdapterPosition

                SnakeBarFactory().generateSnakeBar(
                    recyclerView,
                    "został przeniesiony do archiwum",
                    archivedList.listName,
                    Gravity.TOP,
                ).show()
                taskListEntities.removeAt(position)
                repository.moveToArchive(archivedList)
                listAdapter.notifyItemRemoved(position)

                SnakeBarFactory().generateSnakeBar(
                    recyclerView,
                    "został przeniesiony do archiwum",
                    archivedList.listName,
                    Gravity.TOP,
                ).setAction(
                    "Cofnij"
                ) {
                    taskListEntities.add(position, archivedList)
                    repository.removeFromArchive(archivedList)
                    listAdapter.notifyItemInserted(position)
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

        }).attachToRecyclerView(recyclerView)
    }

    @SuppressLint("InflateParams")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initCreateDialog(
        view: View?,
        inflater: LayoutInflater,
        repository: ListOfTasksRepository
    ) {
        view?.findViewById<FloatingActionButton>(R.id.add_list_explain_button)?.setOnClickListener {
            val viewOfInput = inflater.inflate(R.layout.create_list_input, null)
            val createAlertDialogFactory = AlertDialogFactory()
            val alertDialog = createAlertDialogFactory.createCreateListDialog(
                requireActivity(),
                viewOfInput,
                resources.getString(R.string.create_list_header_dialog),
                resources.getString(R.string.decline),
                resources.getString(R.string.accept),
            )

            alertDialog.show()
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val inputWrapper =
                    viewOfInput.findViewById<TextInputLayout>(R.id.dialog_new_list_wrapper)
                val input = viewOfInput.findViewById<TextInputEditText>(R.id.list_new_name)
                if (input.text.toString().isEmpty()) {
                    inputWrapper.error = getString(R.string.error_empty_input)
                } else {
                    repository.saveNewList(input.text.toString())
                    alertDialog.dismiss()
                }
            }
        }
    }
}