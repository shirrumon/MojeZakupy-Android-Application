package com.example.mojezakupy.fragments.subpages

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.R
import com.example.mojezakupy.adapters.pagesAdapters.TaskListAdapter
import com.example.mojezakupy.database.entity.TaskEntity
import com.example.mojezakupy.databinding.FragmentMainListBinding
import com.example.mojezakupy.repository.ListOfTasksRepository
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import kotlin.properties.Delegates

class ArchiveTaskFragment : Fragment() {

    private lateinit var taskRepository: ListOfTasksRepository
    private var listId by Delegates.notNull<Int>()
    private var tasksSummary by Delegates.notNull<Float>()
    private lateinit var taskParentName: String
    private lateinit var listAdapter: TaskListAdapter
    private lateinit var binding: FragmentMainListBinding
    private var taskListEntities: MutableList<TaskEntity> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        if (bundle !== null) {
            listId = bundle.getInt("listId")
            tasksSummary = bundle.getFloat("tasksSummary")
            taskParentName = bundle.getString("parentListName").toString()
        }
        taskRepository = ListOfTasksRepository(requireActivity())
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_archive, container, false)
        view.findViewById<TextView>(R.id.task_box_price_summary_archive).text = "Razem: $tasksSummary zł"

        binding = FragmentMainListBinding.inflate(inflater)

        initAdapterView(view, taskRepository)
        return view
    }

    private fun initAdapterView(view: View?, repository: ListOfTasksRepository) = with(binding) {
        listAdapter = TaskListAdapter()
        val recyclerView: RecyclerView = view?.findViewById(R.id.task_recycler_archive)!!
        val emptyCommunicate = view.findViewById<Chip>(R.id.empty_list_communicate)
        recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = listAdapter

        repository.taskList(listId).observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                emptyCommunicate.visibility = View.VISIBLE
            } else {
                emptyCommunicate.visibility = View.GONE
                listAdapter.submitList(it)
                taskListEntities = it
            }
        }

        view.findViewById<TextView>(R.id.parent_list_name).text = taskParentName
        val topBar = view.findViewById<MaterialToolbar>(R.id.topAppBar)
        topBar.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}