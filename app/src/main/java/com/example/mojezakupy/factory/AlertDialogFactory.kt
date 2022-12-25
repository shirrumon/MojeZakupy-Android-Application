package com.example.mojezakupy.factory

import android.content.Context
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AlertDialogFactory {
    @RequiresApi(Build.VERSION_CODES.O)
    fun createCreateListDialog(
        context: Context?,
        viewOfInput: View,
        title: String,
        decline: String,
        accept: String,
    ): AlertDialog? {
        return context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(title)
                .setView(viewOfInput)
                .setNegativeButton(decline, null)
                .setPositiveButton(accept, null)
                .create()
        }
    }
}