//package com.example.mojezakupy.factory.fragments
//
//import android.content.DialogInterface
//import android.os.Build
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.annotation.RequiresApi
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.ItemTouchHelper
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.mojezakupy.R
//import com.example.mojezakupy.adapters.CustomListAdapter
//import com.example.mojezakupy.factory.AlertDialogFactory
//import com.example.mojezakupy.factory.ItemTouchSwipeFactory
//import com.example.mojezakupy.repository.ListOfTasksRepository
//import com.google.android.material.chip.Chip
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import com.google.android.material.textfield.TextInputEditText
//import com.google.android.material.textfield.TextInputLayout
//
//abstract class ListFragmentFactory(
//    private val fragmentLayout: Int,
//    private val listToObserve: String
//) : Fragment() {
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(fragmentLayout, container, false)
//        val repository = ListOfTasksRepository(requireActivity())
//
//        repository.list.observe(viewLifecycleOwner) { listEntities ->
//            val recyclerView: RecyclerView = view.findViewById(R.id.dashboard_recycler)
//            recyclerView.layoutManager =
//                LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
//
//            var listAdapterThis = activity?.let { CustomListAdapter(listEntities, it) }
//            recyclerView.adapter = listAdapterThis
//
//            val emptyCommunicate = view.findViewById<Chip>(R.id.empty_list_communicate)
//            if(listEntities.isEmpty()) {
//                emptyCommunicate.visibility = View.VISIBLE
//            } else {
//                emptyCommunicate.visibility = View.GONE
//            }
//
//            listAdapterThis = activity?.let { CustomListAdapter(listEntities, it) }
//            recyclerView.adapter = listAdapterThis
//
//            val swipeHelper: ItemTouchHelper = (ItemTouchSwipeFactory::archiveToLeft)(
//                ItemTouchSwipeFactory(),
//                listEntities,
//                repository,
//                listAdapterThis,
//                recyclerView,
//                activity
//            )
//
//            swipeHelper.attachToRecyclerView(recyclerView)
//
//            view.findViewById<FloatingActionButton>(R.id.add_list_explain_button).setOnClickListener{
//                val viewOfInput = inflater.inflate(R.layout.create_list_input, null)
//                val createAlertDialogFactory = AlertDialogFactory()
//                val alertDialog = createAlertDialogFactory.createCreateListDialog(
//                    requireActivity(),
//                    viewOfInput,
//                    resources.getString(R.string.create_list_header_dialog),
//                    resources.getString(R.string.decline),
//                    resources.getString(R.string.accept),
//                )
//
//                alertDialog.show()
//                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
//                    val inputWrapper = viewOfInput.findViewById<TextInputLayout>(R.id.dialog_new_list_wrapper)
//                    val input = viewOfInput.findViewById<TextInputEditText>(R.id.list_new_name)
//                    if(input.text.toString().isEmpty()){
//                        inputWrapper.error = getString(R.string.error_empty_input)
//                    } else {
//                        repository.saveNewList(input.text.toString())
//                        alertDialog.dismiss()
//                    }
//                }
//            }
//        }
//
//        return super.onCreateView(inflater, container, savedInstanceState)
//    }
//}