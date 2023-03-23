package com.mainApp.mojezakupy.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mainApp.mojezakupy.database.AppDatabase
import com.mainApp.mojezakupy.models.SimilarTaskModel

class InfoGraphicsViewModel(applicationContext: Context) : ViewModel() {
    private val appDatabase: AppDatabase = AppDatabase.getDatabase(applicationContext)
    val allProductsFromLastMonth: LiveData<MutableList<SimilarTaskModel>> = appDatabase.tasksDAO().getAllFromLastMonth()
}