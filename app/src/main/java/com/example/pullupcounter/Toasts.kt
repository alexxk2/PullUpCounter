package com.example.pullupcounter

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes


private fun showToast(context: Context, @StringRes messageRes: Int){
    Toast.makeText(context, messageRes, Toast.LENGTH_SHORT).show()
}

fun MainActivity.showToast(@StringRes messageRes: Int){
    showToast(this, messageRes)
}