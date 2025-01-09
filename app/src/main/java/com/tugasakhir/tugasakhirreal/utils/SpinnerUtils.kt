package com.tugasakhir.tugasakhirreal.utils

import android.content.Context
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

object SpinnerUtils {
    fun setupSpinner(context: Context, spinner: Spinner, resourceId: Int, listener: AdapterView.OnItemSelectedListener) {
        ArrayAdapter.createFromResource(
            context,
            resourceId,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = listener
    }
}
