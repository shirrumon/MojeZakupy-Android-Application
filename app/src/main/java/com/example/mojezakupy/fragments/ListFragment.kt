package com.example.mojezakupy.fragments

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.R
import com.example.mojezakupy.adapters.CustomListAdapter
import com.example.mojezakupy.database.entity.TaskListEntity
import com.example.mojezakupy.factory.AlertDialogFactory
import com.example.mojezakupy.factory.ItemTouchSwipeFactory
import com.example.mojezakupy.viewmodel.ListViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

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
            listsFromDb = listEntities

            val emptyCommunicate = view.findViewById<Chip>(R.id.empty_list_communicate)
            if(listsFromDb.isEmpty()) {
                emptyCommunicate.visibility = View.VISIBLE
            } else {
                emptyCommunicate.visibility = View.GONE
            }

            listAdapterThis = activity?.let { CustomListAdapter(listsFromDb, it) }
            recyclerView.adapter = listAdapterThis

            val swipeHelper: ItemTouchHelper = (ItemTouchSwipeFactory::archiveToLeft)(
                ItemTouchSwipeFactory(),
                listsFromDb,
                listViewModel,
                listAdapterThis,
                recyclerView,
                activity
            )

            swipeHelper.attachToRecyclerView(recyclerView)
        }

        view.findViewById<FloatingActionButton>(R.id.add_list_explain_button).setOnClickListener{
            val viewOfInput = inflater.inflate(R.layout.create_list_input, null)
            val createAlertDialogFactory = AlertDialogFactory()
            val alertDialog = createAlertDialogFactory.createCreateListDialog(
                context,
                viewOfInput,
                resources.getString(R.string.create_list_header_dialog),
                resources.getString(R.string.decline),
                resources.getString(R.string.accept),
            )

            alertDialog?.show()
            alertDialog?.getButton(DialogInterface.BUTTON_POSITIVE)?.setOnClickListener {
                val inputWrapper = viewOfInput.findViewById<TextInputLayout>(R.id.dialog_new_list_wrapper)
                val input = viewOfInput.findViewById<TextInputEditText>(R.id.list_new_name)
                if(input.text.toString().isEmpty()){
                    inputWrapper.error = getString(R.string.error_empty_input)
                } else {
                    listViewModel?.saveNewList(input.text.toString())
                    alertDialog.dismiss()
                }
            }
        }

        return view
    }
}