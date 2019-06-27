package com.dp.githubexample.util

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * ViewModel class used for facilitating proper scope to the couroutines are used.
 */
open class ScopedAndroidViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {

    protected val context = application

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}