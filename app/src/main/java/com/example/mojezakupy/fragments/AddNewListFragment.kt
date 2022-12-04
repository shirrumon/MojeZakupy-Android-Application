package com.example.mojezakupy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.mojezakupy.R
import com.example.mojezakupy.viewmodel.ListViewModel

class AddNewListFragment : Fragment() {
    private var listViewModel: ListViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_add_new_list, container, false)
        listViewModel = activity?.applicationContext?.let { ListViewModel(it) }
        addEventListeners(view)

        return view
    }

    private fun addEventListeners(view: View) {
        view.findViewById<Button>(R.id.addNewListButton).setOnClickListener {
            val newTaskListName = view.findViewById<EditText>(R.id.addNewListInput).text.toString()

            if(newTaskListName.isNotEmpty()){
                listViewModel?.saveNewList(newTaskListName)
                val dashboardFragment = DashboardFragment()
                activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.fragment_container, dashboardFragment)
                    ?.addToBackStack(null)
                    ?.commit()
            } else {
                Toast.makeText(activity,"Nazwa listy nie może być pusta!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}