package com.example.mojezakupy.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.R
import com.example.mojezakupy.viewmodel.ListViewModel
import com.example.mojezakupy.adapters.CustomListAdapter
import com.example.mojezakupy.database.entity.TaskListEntity
import com.example.mojezakupy.factory.SnakeBarFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class ListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            listAdapterThis = activity?.let { CustomListAdapter(listsFromDb, it) }
            recyclerView.adapter = listAdapterThis
        }

        val addListForm: LinearLayout = view.findViewById(R.id.create_new_list_form)
        view.findViewById<FloatingActionButton>(R.id.add_list_explain_button).setOnClickListener{
            if(addListForm.visibility == View.GONE) {
                addListForm.visibility = View.VISIBLE
            } else {
                addListForm.visibility = View.GONE
            }
        }

        view.findViewById<Button>(R.id.add_new_list_form_button).setOnClickListener {
            val listName: TextView = view.findViewById(R.id.add_new_list_input)

            if(listName.text.toString().isEmpty()){
                lifecycleScope.launch {
                    Toast.makeText(activity, "Pole nie może być puste!",
                        Toast.LENGTH_LONG).show()
                }
            } else {
                listViewModel?.saveNewList(listName.text.toString())
                addListForm.visibility = View.GONE
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
        }).attachToRecyclerView(recyclerView)

        return view
    }
}