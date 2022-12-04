package com.example.mojezakupy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.R
import com.example.mojezakupy.adapters.CustomArchiveListAdapter
import com.example.mojezakupy.viewmodel.ListViewModel

class ListArchiveFragment: Fragment() {
    private var listViewModel: ListViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_archive, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.archive_recycler)
        recyclerView.layoutManager =
            LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false
            )

        listViewModel = activity?.let { ListViewModel(it.applicationContext) }

        val listsFromDb = listViewModel?.getAllFromArchive()
        val listAdapterThis = listsFromDb?.let {
            activity?.let { it1 ->
                CustomArchiveListAdapter(
                    it,
                    it1
                )
            }
        }

        recyclerView.adapter = listAdapterThis

        return view
    }
}