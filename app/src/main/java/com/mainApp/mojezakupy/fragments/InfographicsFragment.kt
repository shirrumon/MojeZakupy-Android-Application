package com.mainApp.mojezakupy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mainApp.mojezakupy.R
import com.mainApp.mojezakupy.helpers.GetDataSetFromTask
import com.mainApp.mojezakupy.viewmodel.InfoGraphicsViewModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.android.material.chip.Chip

class InfographicsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_infographics, container, false)
    }

    override fun onStart() {
        super.onStart()
        val aaChartView = view?.findViewById<AAChartView>(R.id.aa_chart_view)
        val infoGraphicsViewModel = InfoGraphicsViewModel(requireContext())

        val aaChartModel : AAChartModel = AAChartModel()
            .chartType(AAChartType.Pie)
            .dataLabelsEnabled(true)
            .backgroundColor(R.color.black)

        infoGraphicsViewModel.allProductsFromLastMonth.observe(viewLifecycleOwner) { data ->
            val dataSetHelper = GetDataSetFromTask()
            val dataSet = dataSetHelper.getDataSetOfPopularProducts(data)
            aaChartModel.series(arrayOf(
                AASeriesElement()
                    .data(dataSet)))

            if(dataSet.isEmpty()){
                view?.findViewById<Chip>(R.id.empty_data_communicate)?.visibility = View.VISIBLE
            } else {
                view?.findViewById<Chip>(R.id.empty_data_communicate)?.visibility = View.GONE
                aaChartView?.aa_drawChartWithChartModel(aaChartModel)
            }
        }
    }
}