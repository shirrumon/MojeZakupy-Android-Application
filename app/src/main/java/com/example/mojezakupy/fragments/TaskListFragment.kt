package com.example.mojezakupy.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.R
import com.example.mojezakupy.adapters.CustomTaskListAdapter
import com.example.mojezakupy.database.entity.TaskEntity
import com.example.mojezakupy.factory.ItemTouchSwipeFactory
import com.example.mojezakupy.helpers.TextProcessHelper
import com.example.mojezakupy.viewmodel.TaskViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlin.properties.Delegates

class TaskListFragment : Fragment() {

    private var taskViewModel: Any = ""
    private var listId by Delegates.notNull<Int>()
    private var tasksSummary by Delegates.notNull<Float>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        if (bundle !== null) {
            listId = bundle.getInt("listId")
            tasksSummary = bundle.getFloat("tasksSummary")
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)
        taskViewModel = activity?.let { TaskViewModel(it.applicationContext, listId.toInt()) }!!
        val deleteSwipeHelper = ItemTouchSwipeFactory()

        view.findViewById<TextView>(R.id.task_box_price_summary).text = "Razem: $tasksSummary zł"

        var listFromDb: MutableList<TaskEntity>
        var currentAdapter: CustomTaskListAdapter

        val recyclerView: RecyclerView = view.findViewById(R.id.task_recycler)
        recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        (taskViewModel as TaskViewModel).taskList.observe(viewLifecycleOwner) {
            listFromDb = it

            val emptyCommunicate = view.findViewById<Chip>(R.id.empty_list_communicate)
            if (listFromDb.isEmpty()) {
                emptyCommunicate.visibility = View.VISIBLE
            } else {
                emptyCommunicate.visibility = View.GONE
            }

            currentAdapter = CustomTaskListAdapter(listFromDb)
            recyclerView.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            deleteSwipeHelper.deleteToLeft(
                it,
                taskViewModel as TaskViewModel,
                currentAdapter,
                recyclerView,
                activity
            ).attachToRecyclerView(recyclerView)

            this.changeEnumerationType(taskViewModel as TaskViewModel, view)
            this.initListeners(view, taskViewModel as TaskViewModel, inflater)
            this.createDialogProduct(view, inflater)

            recyclerView.adapter = currentAdapter
        }

        (taskViewModel as TaskViewModel).countType

        this.setParentListName(taskViewModel as TaskViewModel, view)
        this.setAiCameraPermissions(view)

        return view
    }

    private fun initListeners(
        view: View,
        taskViewModel: TaskViewModel,
        inflater: LayoutInflater
    ) {
        val topBar = view.findViewById<MaterialToolbar>(R.id.topAppBar)
        topBar.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        topBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.change_type -> {
                    val priceBox = view.findViewById<TextView>(R.id.task_box_price_summary)
                    if (priceBox?.text?.split(":")!![0] == "Razem") {
                        taskViewModel.changeCountType(listId, "price")
                    } else {
                        taskViewModel.changeCountType(listId, "standard")
                    }
                }
                R.id.set_salary -> this.startChangeSalaryDialog(inflater)
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun startChangeSalaryDialog(
        inflater: LayoutInflater
    ) {
        context?.let { it1 ->
            val viewOfInput = inflater.inflate(R.layout.change_salary_dialog, null)
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
                val salaryInput =
                    viewOfInput.findViewById<TextInputEditText>(R.id.change_salary_input)

                if (salaryInput.text.toString().isEmpty()) {
                    inputProductNameWrapper.error = getString(R.string.error_empty_input)
                } else {
                    (taskViewModel as TaskViewModel).changeSalary(
                        listId,
                        salaryInput.text.toString(),
                    )
                    dialog.dismiss()
                }
            }
        }
    }

    private fun setParentListName(taskViewModel: TaskViewModel, view: View) {
        taskViewModel.parentList.observe(viewLifecycleOwner) {
            view.findViewById<TextView>(R.id.parent_list_name).text = it.listName
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
                        (taskViewModel as TaskViewModel).createTask(
                            listId.toInt(),
                            inputProductName.text.toString(),
                            inputProductPrice.text.toString().toFloat()
                        )
                        dialog.dismiss()
                    }
                }
            }
        }
    }

    private fun setAiCameraPermissions(view: View) {
        view.findViewById<FloatingActionButton>(R.id.add_task_by_scan)
            .setOnClickListener {
                if (activity?.let { it1 ->
                        ActivityCompat.checkSelfPermission(
                            it1,
                            Manifest.permission.CAMERA
                        )
                    } != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.CAMERA),
                        101
                    )
                } else {
                    this.pickImageFromGallery()
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun changeEnumerationType(taskViewModel: TaskViewModel, view: View) {
        taskViewModel.countType.observe(viewLifecycleOwner) {
            if (it == "standard") {
                taskViewModel.summaryPrice.observe(viewLifecycleOwner) { price ->
                    view.findViewById<TextView>(R.id.task_box_price_summary).text =
                        "Razem: $price zł"
                }
            } else {
                taskViewModel.salary.observe(viewLifecycleOwner) { salary ->
                    view.findViewById<TextView>(R.id.task_box_price_summary).text =
                        "Pozostało: $salary zł"
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("test", "a")
        when (item.itemId) {
            R.id.change_type -> Log.d("test", "a")
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            recognizeTextFromDevice(imageBitmap)
        }
    }

    private fun recognizeTextFromDevice(photo: Bitmap?) {
        val detector =
            FirebaseVision.getInstance().onDeviceTextRecognizer // Получаем состояние FirebaseVisionTextRecognizer
        val textImage = FirebaseVisionImage.fromBitmap((photo!!))
        val textProcessHelper = TextProcessHelper()

        detector.processImage(textImage)
            .addOnSuccessListener { firebaseVisionText ->
                Log.d(
                    "text from scaner",
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
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, 1)
        } catch (e: ActivityNotFoundException) {
            Log.d("error", "error")
        }
    }
}