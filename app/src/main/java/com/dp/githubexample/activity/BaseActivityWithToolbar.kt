package com.dp.githubexample.activity

import android.content.Intent
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.core.app.TaskStackBuilder


abstract class BaseActivityWithToolbar : AppCompatActivity() {

    protected lateinit var toolbar: Toolbar

    protected fun setupToolbar(@IdRes toolbarId: Int) {
        toolbar = findViewById(toolbarId)
        setSupportActionBar(toolbar)
    }

    protected fun enableDisplayHomeAsUp() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val upIntent = NavUtils.getParentActivityIntent(this)
                if (upIntent != null) {
                    upIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                        TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities()
                    } else {
                        NavUtils.navigateUpTo(this, upIntent)
                    }
                }

                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }
}