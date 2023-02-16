package com.example.mojezakupy.fragments.subpages

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.R
import com.example.mojezakupy.adapters.pagesAdapters.TaskListAdapter
import com.example.mojezakupy.database.entity.TaskEntity
import com.example.mojezakupy.databinding.FragmentMainListBinding
import com.example.mojezakupy.factory.SnakeBarFactory
import com.example.mojezakupy.helpers.TextProcessHelper
import com.example.mojezakupy.repository.ListOfTasksRepository
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlin.properties.Delegates

class TaskListFragment : Fragment() {
    private lateinit var taskRepository: ListOfTasksRepository
    private var listId by Delegates.notNull<Int>()
    private var tasksSummary by Delegates.notNull<Float>()
    private lateinit var taskParentName: String
    private lateinit var listAdapter: TaskListAdapter
    private lateinit var binding: FragmentMainListBinding
    private var taskListEntities: MutableList<TaskEntity> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        if (bundle !== null) {
            listId = bundle.getInt("listId")
            tasksSummary = bundle.getFloat("tasksSummary")
            taskParentName = bundle.getString("parentListName").toString()
        }
        taskRepository = ListOfTasksRepository(requireActivity())
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)
        view.findViewById<TextView>(R.id.task_box_price_summary).text = "Razem: $tasksSummary zł"

        binding = FragmentMainListBinding.inflate(inflater)

        initAdapterView(view, taskRepository)
        initListeners(view, taskRepository, inflater)
        initListeners(view, taskRepository, inflater)
        changeEnumerationType(taskRepository, view)
        createDialogProduct(view, inflater)

        return view
    }

    private fun initAdapterView(view: View?, repository: ListOfTasksRepository) = with(binding) {
        listAdapter = TaskListAdapter()
        val recyclerView: RecyclerView = view?.findViewById(R.id.task_recycler)!!
        val emptyCommunicate = view.findViewById<Chip>(R.id.empty_list_communicate)
        recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = listAdapter

        repository.taskList(listId).observe(viewLifecycleOwner) {
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
                val deletedTask: TaskEntity =
                    taskListEntities[viewHolder.absoluteAdapterPosition]
                val position = viewHolder.absoluteAdapterPosition

                SnakeBarFactory().generateSnakeBar(
                    recyclerView,
                    "został przeniesiony do archiwum",
                    deletedTask.taskName,
                    Gravity.TOP,
                ).show()
                taskListEntities.removeAt(position)
                repository.delete(deletedTask)
                listAdapter.notifyItemRemoved(position)

                SnakeBarFactory().generateSnakeBar(
                    recyclerView,
                    "Usunąłeś",
                    deletedTask.taskName,
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

    private fun initListeners(
        view: View,
        tasksRepository: ListOfTasksRepository,
        inflater: LayoutInflater
    ) {
        view.findViewById<TextView>(R.id.parent_list_name).text = taskParentName
        val topBar = view.findViewById<MaterialToolbar>(R.id.topAppBar)
        topBar.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        view.findViewById<FloatingActionButton>(R.id.add_task_by_scan).setOnClickListener {
            this.pickImageFromGallery()
        }

        topBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.change_type -> {
                    val priceBox = view.findViewById<TextView>(R.id.task_box_price_summary)
                    if (priceBox?.text?.split(":")!![0] == "Razem") {
                        tasksRepository.changeCountType(listId, "price")
                        this.startChangeSalaryDialog(inflater)
                    } else {
                        tasksRepository.changeCountType(listId, "standard")
                    }
                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun startChangeSalaryDialog(
        inflater: LayoutInflater
    ) {
        val viewOfInput = inflater.inflate(R.layout.change_salary_dialog, null)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.create_product_header))
            .setView(viewOfInput)
            .setNegativeButton(resources.getString(R.string.decline), null)
            .setPositiveButton(resources.getString(R.string.accept), null)
            .create()

        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val inputProductNameWrapper =
                viewOfInput.findViewById<TextInputLayout>(R.id.dialog_new_product_name_wrapper)
            val salaryInput =
                viewOfInput.findViewById<TextInputEditText>(R.id.change_salary_input)

            if (salaryInput.text.toString().isEmpty()) {
                inputProductNameWrapper.error = getString(R.string.error_empty_input)
            } else {
                var currentSummaryCost = 0.0F
                taskListEntities.forEach {
                    currentSummaryCost += it.taskPrice
                }
                currentSummaryCost = salaryInput.text.toString().toFloat() - currentSummaryCost

                taskRepository.changeSalary(
                    listId,
                    currentSummaryCost,
                )
                dialog.dismiss()
            }
        }
    }

    private fun createDialogProduct(
        view: View,
        inflater: LayoutInflater
    ) {
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
                    val inputProductNameWrapper =
                        viewOfInput.findViewById<TextInputLayout>(R.id.dialog_new_product_name_wrapper)
                    val inputProductName =
                        viewOfInput.findViewById<TextInputEditText>(R.id.product_new_name)

                    val inputProductPriceWrapper =
                        viewOfInput.findViewById<TextInputLayout>(R.id.dialog_new_product_price_wrapper)
                    val inputProductPrice =
                        viewOfInput.findViewById<TextInputEditText>(R.id.product_price)

                    if (inputProductName.text.toString().isEmpty()) {
                        inputProductNameWrapper.error = getString(R.string.error_empty_input)
                    } else if (inputProductPrice.text.toString().isEmpty()) {
                        inputProductPriceWrapper.error = getString(R.string.error_empty_input)
                    } else {
                        taskRepository.createTask(
                            listId,
                            inputProductName.text.toString(),
                            inputProductPrice.text.toString().toFloat()
                        )
                        dialog.dismiss()
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun changeEnumerationType(taskViewModel: ListOfTasksRepository, view: View) {
        taskViewModel.countType(listId).observe(viewLifecycleOwner) {
            if (it == "standard") {
                taskViewModel.summaryPrice(listId).observe(viewLifecycleOwner) { price ->
                    view.findViewById<TextView>(R.id.task_box_price_summary).text =
                        "Razem: $price zł"
                }
            } else {
                taskViewModel.salary(listId).observe(viewLifecycleOwner) { salary ->
                    view.findViewById<TextView>(R.id.task_box_price_summary).text =
                        "Pozostało: $salary zł"
                }
            }
        }
    }

    private fun recognizeTextFromDevice(photo: Bitmap?) {
        val detector =
            FirebaseVision.getInstance().onDeviceTextRecognizer
        val textImage = FirebaseVisionImage.fromBitmap((photo!!))
        val textProcessHelper = TextProcessHelper()

        detector.processImage(textImage)
            .addOnSuccessListener { firebaseVisionText ->
                Log.d(
                    "text from scanner",
                    textProcessHelper.process(firebaseVisionText)[0] + " " + textProcessHelper.process(
                        firebaseVisionText
                    )[1]
                )
            }
            .addOnFailureListener {
                Log.d("error", "error")
            }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncher.launch(intent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (result.resultCode == Activity.RESULT_OK) {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    recognizeTextFromDevice(imageBitmap)
                }
            }
        }
}