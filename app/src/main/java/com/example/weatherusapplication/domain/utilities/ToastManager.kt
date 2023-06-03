package com.example.weatherusapplication.domain.utilities

import android.content.Context
import android.widget.Toast

/**
 * ToastManager Helper Class
 */
object ToastManager {
    fun showShortToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
