package com.example.mojezakupy.models

data class SimilarTaskModel(
    val task_name: String,
    val task_price: Float,
    val count: Int = 1
)