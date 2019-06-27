package com.dp.githubexample.util

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

/**
 * Helper ext functions for [Context].
 */

fun Context.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration)
        .show()
}

fun Context.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    toast(getText(resId), duration)
}
