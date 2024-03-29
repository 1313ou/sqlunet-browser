/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download.workers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bbou.download.CompletionListener
import com.bbou.download.Keys.BROADCAST_ACTION
import com.bbou.download.Keys.BROADCAST_KILL_REQUEST_VALUE
import com.bbou.download.Keys.BROADCAST_NEW_REQUEST_VALUE
import com.bbou.download.Keys.BROADCAST_REQUEST_KEY
import com.bbou.download.Keys.DOWNLOAD_MODE_ARG
import com.bbou.download.common.R
import com.bbou.download.preference.Settings

/**
 * Download activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class DownloadActivity : AppCompatActivity(), CompletionListener {

    /**
     * onCreate
     *
     * @param savedInstanceState saved instance state
     */
    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // download mode to downloader
        val overriddenMode = intent.getStringExtra(DOWNLOAD_MODE_ARG)
        val mode = (if (overriddenMode == null) Settings.Mode.getModePref(this) else Settings.Mode.valueOf(overriddenMode))!!
        val downloader = mode.toDownloader()

        // content
        setContentView(R.layout.activity_download)

        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // set up the action bar
        val actionBar = supportActionBar!!
        actionBar.setDisplayShowTitleEnabled(true)

        // fragment
        if (savedInstanceState == null) {

            // fragment
            val downloadFragment = toFragment(downloader)

            // pass arguments over to fragment
            val args = intent.extras
            downloadFragment.arguments = args
            if (args != null) {
                val broadcastAction = args.getString(BROADCAST_ACTION)
                val broadcastRequestKey = args.getString(BROADCAST_REQUEST_KEY)
                if (!broadcastAction.isNullOrEmpty() && !broadcastRequestKey.isNullOrEmpty()) {
                    val broadcastKillRequestValue = args.getString(BROADCAST_KILL_REQUEST_VALUE)
                    if (!broadcastKillRequestValue.isNullOrEmpty()) {
                        downloadFragment.setRequestKill { broadcastRequest(this, broadcastAction, broadcastRequestKey, broadcastKillRequestValue) }
                    }
                    val broadcastNewRequestValue = args.getString(BROADCAST_NEW_REQUEST_VALUE)
                    if (!broadcastNewRequestValue.isNullOrEmpty()) {
                        downloadFragment.setRequestNew { broadcastRequest(this, broadcastAction, broadcastRequestKey, broadcastNewRequestValue) }
                    }
                }
            }
            supportFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.container, downloadFragment)
                .commit()
        }
    }

    /**
     * Download complete callback
     * Do nothing (let user decide whether to deploy downloaded bundle)
     *
     * @param success true if success
     */
    override fun onComplete(success: Boolean) {
        Log.d(TAG, "OnComplete succeeded=$success $this")

        // finish activity
        if (success) {
            finish()
        }
    }

    companion object {

        private const val TAG = "DownloadA"

        /**
         * Broadcast request
         *
         * @param context               context
         * @param broadcastAction       broadcast action
         * @param broadcastRequestKey   broadcast request arg key
         * @param broadcastRequestValue broadcast request arg value
         */
        private fun broadcastRequest(context: Context, broadcastAction: String, broadcastRequestKey: String, broadcastRequestValue: String) {
            Log.d(TAG, "Send broadcast request $broadcastRequestValue")
            val intent = Intent()
            intent.setPackage(context.packageName)
            intent.setAction(broadcastAction)
            intent.putExtra(broadcastRequestKey, broadcastRequestValue)
            context.sendBroadcast(intent)
        }
    }
}
