package com.example.mojezakupy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mojezakupy.R
import com.example.mojezakupy.helpers.GetDataSetFromTask
import com.example.mojezakupy.viewmodel.InfoGraphicsViewModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.android.material.chip.Chip

class InfographicsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_infographics, container, false)
        val aaChartView = view.findViewById<AAChartView>(R.id.aa_chart_view)
        val infoGraphicsViewModel = context?.let { InfoGraphicsViewModel(it) }

        val aaChartModel : AAChartModel = AAChartModel()
            .chartType(AAChartType.Pie)
            .dataLabelsEnabled(true)

        infoGraphicsViewModel?.allProductsFromLastMonth?.observe(viewLifecycleOwner) { data ->
            val dataSetHelper = GetDataSetFromTask()
            val dataSet = dataSetHelper.getDataSetOfPopularProducts(data)
            aaChartModel.series(arrayOf(
                AASeriesElement()
                    .data(dataSet)))

            if(dataSet.isEmpty()){
                view.findViewById<Chip>(R.id.empty_data_communicate).visibility = View.VISIBLE
            } else {
                view.findViewById<Chip>(R.id.empty_data_communicate).visibility = View.GONE
                aaChartView.aa_drawChartWithChartModel(aaChartModel)
            }
        }

        return view
    }
}