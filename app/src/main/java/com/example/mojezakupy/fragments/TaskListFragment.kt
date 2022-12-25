package com.example.mojezakupy.fragments

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.R
import com.example.mojezakupy.adapters.CustomTaskListAdapter
import com.example.mojezakupy.database.entity.TaskEntity
import com.example.mojezakupy.factory.SnakeBarFactory
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
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class TaskListFragment(
    private val listId: String,
    private val tasksSummary: String
    ) : Fragment() {

    private var taskViewModel: Any = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)
        taskViewModel = activity?.let { TaskViewModel(it.applicationContext, listId.toInt()) }!!

        view.findViewById<TextView>(R.id.task_box_price_summary).text = "Razem: $tasksSummary zł"

        var listFromDb: MutableList<TaskEntity> = arrayListOf()
        var currentAdapter = CustomTaskListAdapter(listFromDb)

        val recyclerView: RecyclerView = view.findViewById(R.id.task_recycler)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        (taskViewModel as TaskViewModel).taskList.observe(viewLifecycleOwner) {
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

        (taskViewModel as TaskViewModel).countType?.observe(viewLifecycleOwner) {
            if(it == "standard") {
                (taskViewModel as TaskViewModel).summaryPrice.observe(viewLifecycleOwner){ price ->
                    //view.findViewById<TextView>(R.id.task_list_salary).text = "Cena razem: "
                    view.findViewById<TextView>(R.id.task_box_price_summary).text = "Razem: $price zł"
                }
            } else {
                (taskViewModel as TaskViewModel).salary.observe(viewLifecycleOwner){ salary ->
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

                (taskViewModel as TaskViewModel)?.delete(listFromDb[viewHolder.adapterPosition])

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

        (taskViewModel as TaskViewModel)?.parentList?.observe(viewLifecycleOwner) {
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
                        (taskViewModel as TaskViewModel)?.createTask(
                            listId.toInt(),
                            inputProductName.text.toString(),
                            inputProductPrice.text.toString()
                        )
                        dialog.dismiss()
                    }
                }
            }
        }

        view.findViewById<FloatingActionButton>(R.id.add_task_by_scan)
            .setOnClickListener {
                if (activity?.let { it1 ->
                        ActivityCompat.checkSelfPermission(
                            it1,
                            Manifest.permission.CAMERA
                        )
                    } != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(requireActivity(),
                        arrayOf(Manifest.permission.CAMERA),
                        101)
                } else {
                    pickImageFromGallery()
                }
            }

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("test", "a")
        when(item.itemId){
            R.id.change_type -> Log.d("test", "a")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setListeners(
        view: View?,
        listId: String,
    ) {
        val priceBox = view?.findViewById<TextView>(R.id.task_box_price_summary)
        if (priceBox?.text.toString() == "Razem: ") {
            (taskViewModel as TaskViewModel).changeCountType(listId, "price")
        } else {
            (taskViewModel as TaskViewModel).changeCountType(listId, "standard")
        }
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
        val detector = FirebaseVision.getInstance().onDeviceTextRecognizer // Получаем состояние FirebaseVisionTextRecognizer
        val textImage = FirebaseVisionImage.fromBitmap((photo!!))
        val textProcessHelper = TextProcessHelper()

        detector.processImage(textImage)
            .addOnSuccessListener { firebaseVisionText ->
                Log.d("text from scaner", textProcessHelper.process(firebaseVisionText)[0] + " " + textProcessHelper.process(firebaseVisionText)[1])
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