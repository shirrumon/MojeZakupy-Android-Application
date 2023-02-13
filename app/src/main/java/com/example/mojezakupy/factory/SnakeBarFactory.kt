package com.example.mojezakupy.factory

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class SnakeBarFactory {
    fun generateSnakeBar(
        recyclerView: RecyclerView,
        Message: String,
        Name: String?,
        gravity: Int = Gravity.TOP
    ): Snackbar {
        val snakeBar = Snackbar.make(
            recyclerView,
            "$Name $Message",
            Snackbar.LENGTH_SHORT
        )

        val snakeBarView: View = snakeBar.view
        val params = snakeBarView.layoutParams as FrameLayout.LayoutParams
        params.gravity = gravity
        snakeBarView.layoutParams = params

        return snakeBar
    }
}