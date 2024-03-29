/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.sqlunet.browser.common.R
import org.sqlunet.browser.config.LoadActivity
import org.sqlunet.browser.config.SetupAsset
import org.sqlunet.browser.config.SetupDatabaseTasks
import org.sqlunet.browser.config.Status
import org.sqlunet.settings.Settings
import org.sqlunet.settings.StorageSettings

/**
 * Entry point activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class EntryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Lifecycle: OnCreate()")

        // clear (some/all) settings on first run of this version
        val upgrade = Settings.isUpgrade(this) // upgrade[0]=recorded version, upgrade[1]=this build
        if (upgrade[0] < upgrade[1]) {
            if (upgrade[0] < 94) {
                val success = SetupDatabaseTasks.deleteDatabase(this, StorageSettings.getDatabasePath(this))
                if (success) {
                    Log.d(TAG, "Deleted database")
                } else {
                    Log.e(TAG, "Error deleting database")
                }
                com.bbou.download.preference.Settings.unrecordDatapack(this)
                Toast.makeText(this, R.string.sqlunet2, Toast.LENGTH_LONG).show()
            }
            Settings.onUpgrade(this, upgrade[1])
        }

        // settings
        Settings.updateGlobals(this)

        // clean up assets
        if (Settings.getAssetAutoCleanup(this)) {
            SetupAsset.disposeAllAssets(this)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "Lifecycle: OnNewIntent()")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "Lifecycle: OnResume()")
        dispatch()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Lifecycle: onDestroy()")
    }

    /**
     * Dispatch, depending on whether the app can run
     */
    private fun dispatch() {
        // check hook
        val canRun = Status.canRun(baseContext)
        if (!canRun) {
            branchOffToLoad(this)
            return
        }

        // switch as per preferred launch mode
        val clazz = Settings.getLaunchPref(this) // = "org.sqlunet.browser.MainActivity" or overriding "org.sqlunet.browser.wn.MainActivity"
        val intent = Intent()
        intent.setClassName(this, clazz)
        intent.addFlags(0)
        startActivity(intent)
        finish()
    }

    companion object {

        private const val TAG = "EntryA"

        /**
         * Branch off to load activity
         *
         * @param activity activity to branch from
         */
        fun branchOffToLoadIfCantRun(activity: AppCompatActivity) {
            val canRun = Status.canRun(activity)
            if (!canRun) {
                branchOffToLoad(activity)
            }
        }

        /**
         * Branch off to load activity
         *
         * @param activity activity to branch from
         */
        private fun branchOffToLoad(activity: AppCompatActivity) {
            val intent = Intent(activity, LoadActivity::class.java)
            intent.addFlags(0)
            activity.startActivity(intent)
        }

        /**
         * Rerun entry activity. Task is cleared before the activity is started. The activity becomes the new root of an otherwise empty task.
         *
         * @param context context
         */
        @JvmStatic
        fun rerun(context: Context) {
            val intent = Intent(context, EntryActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }
}
