package com.example.mojezakupy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.R
import com.example.mojezakupy.viewmodel.ListViewModel
import com.example.mojezakupy.adapters.CustomListAdapter

class DashboardFragment : Fragment() {
    private var listViewModel: ListViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        listViewModel = activity?.let { ListViewModel(it.applicationContext) }

        val recyclerView: RecyclerView = view.findViewById(R.id.dashboard_recycler)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = listViewModel?.getAllInstances()?.let { activity?.let { it1 ->
            CustomListAdapter(it,
                it1
            )
        } }

        return view
    }
}