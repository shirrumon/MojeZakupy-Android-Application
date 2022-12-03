package com.example.mojezakupy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mojezakupy.R
import com.example.mojezakupy.database.entity.TaskListEntity
import com.example.mojezakupy.viewmodel.ListViewModel


class DashboardFragment : Fragment() {

    private var listViewModel: ListViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        listViewModel = activity?.let { ListViewModel(it.applicationContext) }

        val recyclerView: RecyclerView = view.findViewById(R.id.dashboard_recycler)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = listViewModel?.getAllInstances()?.let { activity?.let { it1 ->
            CustomAdapter(it,
                it1
            )
        } }

        return view
    }

}

class CustomAdapter(private val dataSet: List<TaskListEntity>, private val activity: FragmentActivity) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        val taskCount: TextView
        val hiddenId: TextView
        val summaryPrice: TextView

        init {
            // Define click listener for the ViewHolder's View.
            textView = view.findViewById(R.id.task_list_name)
            taskCount = view.findViewById(R.id.task_list_count)
            summaryPrice = view.findViewById(R.id.task_list_summary_price)
            hiddenId = view.findViewById(R.id.task_list_id)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_element_layout, viewGroup, false)

        view.setOnClickListener{
            val listId = it.findViewById<TextView>(R.id.task_list_id).text.toString()
            val tasksSummary = it.findViewById<TextView>(R.id.task_list_summary_price).text.toString()

            val listFragment = ListFragment(listId, tasksSummary)
            activity.supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, listFragment)
                .addToBackStack(null)
                .commit()
        }

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textView.text = dataSet[position].listName
        viewHolder.taskCount.text = dataSet[position].taskCount.toString() + " tasków"
        viewHolder.summaryPrice.text = dataSet[position].taskSummary
        viewHolder.hiddenId.text = dataSet[position].id.toString()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}

