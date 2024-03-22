/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import android.util.Log
import androidx.annotation.RequiresApi
import org.sqlunet.browser.common.BuildConfig
import org.sqlunet.browser.common.R
import org.sqlunet.nightmode.NightMode.nightModeToString
import org.sqlunet.nightmode.NightMode.wrapContext
import org.sqlunet.settings.Settings

abstract class AbstractApplication : Application() {

    override fun onCreate() {
        // setThreadStrictMode();
        // setVmStrictMode();
        super.onCreate()
        Settings.initializeDisplayPrefs(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val newContext = wrapContext(this, newConfig, R.style.MyTheme)
        Log.d(LOG, "onConfigurationChanged: " + nightModeToString(this) + " -> " + nightModeToString(newContext))
        setAllColorsFromResources(newContext)
    }

    abstract fun setAllColorsFromResources(newContext: Context)

    /**
     * Strict mode for VM
     */
    private fun setVmStrictMode() {
        if (BuildConfig.DEBUG) {
            val builder = VmPolicy.Builder() //
                .detectLeakedSqlLiteObjects() //
                .detectLeakedClosableObjects() //
                .penaltyLog()
            if (PENALTY_DEATH) {
                builder.penaltyDeath()
            }
            StrictMode.setVmPolicy(builder.build())
        }
    }

    /**
     * Strict mode for threads
     */
    private fun setThreadStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                ThreadPolicy.Builder() //
                    .detectDiskReads() //
                    .detectDiskWrites() //
                    .detectNetwork() // or .detectAll() for all detectable problems
                    .penaltyLog() //
                    .build()
            )
        }
    }

    // T A S K S

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun dumpTasks() {
        dumpTasks(this.baseContext)
    }

    companion object {
        private const val LOG = "AbstractApp"
        private const val PENALTY_DEATH = false

        @RequiresApi(api = Build.VERSION_CODES.M)
        fun dumpTasks(context: Context) {
            val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val tasks = manager.getAppTasks()
            for (task in tasks) {
                val info = task.taskInfo
                Log.i("task", info.baseActivity.toString())
            }
        }
    }
}
