package com.example.mojezakupy.fragments

import android.content.DialogInterface
import android.graphics.Canvas
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.R
import com.example.mojezakupy.adapters.CustomTaskListAdapter
import com.example.mojezakupy.database.entity.TaskEntity
import com.example.mojezakupy.factory.SnakeBarFactory
import com.example.mojezakupy.viewmodel.TaskViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.launch

class TaskListFragment(
    private val listId: String,
    private val tasksSummary: String
    ) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)
        val taskViewModel: TaskViewModel? = activity?.let { TaskViewModel(it.applicationContext, listId.toInt()) }

        view.findViewById<TextView>(R.id.task_box_price_summary).text = "Razem: $tasksSummary zł"

        var listFromDb: MutableList<TaskEntity> = arrayListOf()
        var currentAdapter = CustomTaskListAdapter(listFromDb)

        val recyclerView: RecyclerView = view.findViewById(R.id.task_recycler)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        taskViewModel?.taskList?.observe(viewLifecycleOwner) {
            listFromDb = it

            val emptyCommunicate = view.findViewById<Chip>(R.id.empty_list_communicate)
            if(listFromDb.isEmpty()) {
                emptyCommunicate.visibility = View.VISIBLE
            } else {
                emptyCommunicate.visibility = View.GONE
            }

            currentAdapter = CustomTaskListAdapter(listFromDb)
            recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = currentAdapter
        }

        taskViewModel?.countType?.observe(viewLifecycleOwner) {
            if(it == "standard") {
                taskViewModel.summaryPrice.observe(viewLifecycleOwner){ price ->
                    //view.findViewById<TextView>(R.id.task_list_salary).text = "Cena razem: "
                    view.findViewById<TextView>(R.id.task_box_price_summary).text = "Razem: $price zł"
                }
            } else {
                taskViewModel.salary.observe(viewLifecycleOwner){ salary ->
                    //view.findViewById<TextView>(R.id.task_list_salary).text = "Pozostało wypłaty: "
                    view.findViewById<TextView>(R.id.task_box_price_summary).text = "Pozostało: $salary zł"
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
                val deletedCourse: TaskEntity =
                    listFromDb[viewHolder.adapterPosition]

                taskViewModel?.delete(listFromDb[viewHolder.adapterPosition])

                currentAdapter.notifyItemRemoved(viewHolder.adapterPosition)

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
        }).attachToRecyclerView(recyclerView)

        val topBar = view.findViewById<MaterialToolbar>(R.id.topAppBar)
        topBar.setOnClickListener{
            fragmentManager?.popBackStack()
        }

        taskViewModel?.parentList?.observe(viewLifecycleOwner) {
            view.findViewById<TextView>(R.id.parent_list_name).text = it.listName
        }

        view.findViewById<FloatingActionButton>(R.id.add_task_explain_button).setOnClickListener {
            context?.let { it1 ->
                val viewOfInput = inflater.inflate(R.layout.add_product_input_group, null)
                val dialog = MaterialAlertDialogBuilder(it1)
                    .setTitle(resources.getString(R.string.create_product_header))
                    .setView(viewOfInput)
                    .setNegativeButton(resources.getString(R.string.decline), null)
                    .setPositiveButton(resources.getString(R.string.accept), null)
                    .create()

                dialog.show()
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                    val inputProductNameWrapper = viewOfInput.findViewById<TextInputLayout>(R.id.dialog_new_product_name_wrapper)
                    val inputProductName = viewOfInput.findViewById<TextInputEditText>(R.id.product_new_name)

                    val inputProductPriceWrapper = viewOfInput.findViewById<TextInputLayout>(R.id.dialog_new_product_price_wrapper)
                    val inputProductPrice = viewOfInput.findViewById<TextInputEditText>(R.id.product_price)

                    if(inputProductName.text.toString().isEmpty()){
                        inputProductNameWrapper.error = getString(R.string.error_empty_input)
                    } else if (inputProductPrice.text.toString().isEmpty()){
                        inputProductPriceWrapper.error = getString(R.string.error_empty_input)
                    } else {
                        taskViewModel?.createTask(
                            listId.toInt(),
                            inputProductName.text.toString(),
                            inputProductPrice.text.toString()
                        )
                        dialog.dismiss()
                    }
                }
            }
        }

        this.setListeners(view, taskViewModel, listId)
        return view
    }

    private fun setListeners(
        view: View,
        taskViewModel: TaskViewModel?,
        listId: String,
    ) {
//        val addTaskForm: LinearLayout = view.findViewById(R.id.task_create_wrapper)
//
//
//        view.findViewById<Button>(R.id.create_new_task_button_in_form).setOnClickListener {
//            val taskName: TextView = view.findViewById(R.id.add_new_task_name)
//            val taskPrice: TextView = view.findViewById(R.id.add_new_task_price)
//
//            if(taskName.text.toString().isEmpty() || taskPrice.text.toString().isEmpty()){
//                lifecycleScope.launch {
//                    Toast.makeText(activity, "Pole nie może być puste!",
//                        Toast.LENGTH_LONG).show()
//                }
//            } else {
//                taskViewModel?.createTask(
//                    listId.toInt(),
//                    taskName.text.toString(),
//                    taskPrice.text.toString()
//                )
//                addTaskForm.visibility = View.GONE
//            }
//        }

//        view.findViewById<TextView>(R.id.task_list_salary).setOnClickListener {
//            if (view.findViewById<TextView>(R.id.task_list_salary).text.toString() == "Cena razem: ") {
//                view.findViewById<LinearLayout>(R.id.change_price_wrapper).visibility = View.VISIBLE
//                taskViewModel?.changeCountType(listId, "price")
//            } else {
//                view.findViewById<LinearLayout>(R.id.change_price_wrapper).visibility = View.GONE
//                taskViewModel?.changeCountType(listId, "standard")sad
//            }
//        }

//        view.findViewById<Button>(R.id.change_price_button).setOnClickListener {
//            taskViewModel?.changeSalary(listId, view.findViewById<EditText>(R.id.change_price_input).text.toString())
//        }
//
//        view.findViewById<Button>(R.id.close_salary_change_icon).setOnClickListener {
//            view.findViewById<LinearLayout>(R.id.change_price_wrapper).visibility = View.GONE
//        }
    }
}