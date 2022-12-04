package com.example.mojezakupy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.R
import com.example.mojezakupy.viewmodel.ListViewModel
import com.example.mojezakupy.adapters.CustomListAdapter
import com.example.mojezakupy.database.entity.TaskListEntity
import com.google.android.material.snackbar.Snackbar

class DashboardFragment : Fragment() {
    private var listViewModel: ListViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        listViewModel = activity?.let { ListViewModel(it.applicationContext) }

        val recyclerView: RecyclerView = view.findViewById(R.id.dashboard_recycler)
        recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        val listsFromDb = listViewModel?.getAllInstances()
        val listAdapterThis = listsFromDb?.let {
            activity?.let { it1 ->
                CustomListAdapter(
                    it,
                    it1
                )
            }
        }
        recyclerView.adapter = listAdapterThis

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedCourse: TaskListEntity? =
                    listsFromDb?.get(viewHolder.adapterPosition)

                val position = viewHolder.adapterPosition

                listsFromDb?.get(viewHolder.adapterPosition)
                    ?.let { listViewModel?.moveToArchive(it) }
                listsFromDb?.removeAt(viewHolder.adapterPosition)

                listAdapterThis?.notifyItemRemoved(viewHolder.adapterPosition)

                if (deletedCourse != null) {
                    Snackbar.make(
                        recyclerView,
                        "Przemisciles do archiwum " + deletedCourse.listName,
                        Snackbar.LENGTH_LONG
                    ).setAction(
                        "Cofnij",
                        View.OnClickListener {
                            listsFromDb.add(position, deletedCourse)
                            listViewModel?.removeFromArchive(deletedCourse)
                            listAdapterThis?.notifyItemInserted(position)
                        }).show()
                }
            }
        }).attachToRecyclerView(recyclerView)

        return view
    }
}