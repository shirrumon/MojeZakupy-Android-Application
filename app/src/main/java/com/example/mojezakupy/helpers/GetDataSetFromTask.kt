package com.example.mojezakupy.helpers

import com.example.mojezakupy.models.SimilarTaskModel

class GetDataSetFromTask {
    fun getDataSetOfPopularProducts(
        productList: MutableList<SimilarTaskModel>
    ): Array<Any> {
        val dataSetToReturn = mutableListOf<Any>()
        productList.forEach { data ->
            dataSetToReturn.add(arrayOf(data.task_name, data.count))
        }

        return dataSetToReturn.toTypedArray()
    }
}