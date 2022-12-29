package com.example.mojezakupy.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mojezakupy.database.AppDatabase
import com.example.mojezakupy.models.SimilarTaskModel

class InfoGraphicsViewModel(applicationContext: Context) : ViewModel() {
    private val appDatabase: AppDatabase = AppDatabase.getDatabase(applicationContext)
    val allProductsFromLastMonth: LiveData<MutableList<SimilarTaskModel>> = appDatabase.tasksDAO().getAllFromLastMonth()
}