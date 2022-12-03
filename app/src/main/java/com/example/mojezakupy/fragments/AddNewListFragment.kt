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

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddNewListFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private var listViewModel: ListViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            } else {
                Toast.makeText(activity,"Nazwa listy nie może być pusta!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddNewListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}