/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download.workers

import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.bbou.download.Keys
import com.bbou.download.UpdateActivity
import com.bbou.download.UpdateFragment
import com.bbou.download.common.R
import com.bbou.download.preference.Settings
import com.bbou.download.utils.FileData
import com.bbou.download.workers.utils.FileDataDownloader
import java.util.Date
import androidx.core.net.toUri

/**
 * Update launcher
 */
object UpdateStarter {

    /**
     * Start file data downloading and update
     *
     * @param activity       launching activity
     * @param downloadIntent download intent (activity launched if update is requested)
     */
    fun start(activity: FragmentActivity, downloadIntent: Intent) {

        // unmarshal arguments
        val downloadSourceUrl = downloadIntent.getStringExtra(Keys.DOWNLOAD_FROM_ARG)
        val name = downloadSourceUrl?.toUri()?.lastPathSegment

        // download source data
        if (downloadSourceUrl.isNullOrEmpty() || name == null) {
            val message = activity.getString(R.string.no_url)
            activity.runOnUiThread { Toast.makeText(activity, message, Toast.LENGTH_LONG).show() }
            return
        }

        // in-use downstream data
        val downDateValue = Settings.getDatapackDate(activity)
        val downSizeValue = Settings.getDatapackSize(activity)
        val downDate = if (downDateValue == -1L || downDateValue == 0L) null else Date(downDateValue)
        val downSize = if (downSizeValue == -1L) null else downSizeValue

        // in-use downstream recorded source data
        val downSource = Settings.getDatapackSource(activity)
        val downSourceDateValue = Settings.getDatapackSourceDate(activity)
        val downSourceSizeValue = Settings.getDatapackSourceSize(activity)
        val downSourceDate = if (downSourceDateValue == -1L || downSourceDateValue == 0L) null else Date(downSourceDateValue)
        val downSourceSize = if (downSourceSizeValue == -1L) null else downSourceSizeValue
        val downSourceEtag = Settings.getDatapackSourceEtag(activity)
        val downSourceVersion = Settings.getDatapackSourceVersion(activity)
        val downSourceStaticVersion = Settings.getDatapackSourceStaticVersion(activity)

        // download source data (acquired by task)
        val task = FileDataDownloader { srcData: FileData? ->

            // upstream data from http connection
            val srcDate = srcData?.getDate()
            val srcSize = srcData?.getSize()
            val srcEtag = srcData?.etag
            val srcVersion = srcData?.version
            val srcStaticVersion = srcData?.staticVersion

            // newer
            val same = srcEtag != null && srcEtag == downSourceEtag || srcVersion != null && srcVersion == downSourceVersion || srcStaticVersion != null && srcStaticVersion == downSourceStaticVersion // one match
            val newer = srcDate == null || downDate == null || srcDate > downDate // upstream src date from http connection is newer than recorded date
            val srcNewer = srcDate == null || downSourceDate == null || srcDate > downSourceDate // upstream src date from http connection is newer than recorded source date

            // start update activity
            val intent = Intent(activity, UpdateActivity::class.java)

            // result
            val na = activity.getString(R.string.na)
            val bytes = activity.getString(R.string.bytes)
            intent.putExtra(UpdateFragment.UP_SOURCE_ARG, downloadSourceUrl)
            intent.putExtra(UpdateFragment.UP_DATE_ARG, srcDate?.toString() ?: na)
            intent.putExtra(UpdateFragment.UP_SIZE_ARG, if (srcSize == null) na else "$srcSize $bytes")
            intent.putExtra(UpdateFragment.UP_ETAG_ARG, srcEtag ?: na)
            intent.putExtra(UpdateFragment.UP_VERSION_ARG, srcVersion ?: na)
            intent.putExtra(UpdateFragment.UP_STATIC_VERSION_ARG, srcStaticVersion ?: na)
            intent.putExtra(UpdateFragment.DOWN_NAME_ARG, name)
            intent.putExtra(UpdateFragment.DOWN_DATE_ARG, downDate?.toString() ?: na)
            intent.putExtra(UpdateFragment.DOWN_SIZE_ARG, if (downSize == null) na else "$downSize $bytes")
            intent.putExtra(UpdateFragment.DOWN_SOURCE_ARG, downSource ?: activity.getString(R.string.unsourced))
            intent.putExtra(UpdateFragment.DOWN_SOURCE_DATE_ARG, downSourceDate?.toString() ?: na)
            intent.putExtra(UpdateFragment.DOWN_SOURCE_SIZE_ARG, if (downSourceSize == null) na else "$downSourceSize $bytes")
            intent.putExtra(UpdateFragment.DOWN_SOURCE_ETAG_ARG, downSourceEtag ?: na)
            intent.putExtra(UpdateFragment.DOWN_SOURCE_VERSION_ARG, downSourceVersion ?: na)
            intent.putExtra(UpdateFragment.DOWN_SOURCE_STATIC_VERSION_ARG, if (downSourceVersion == null) na else downSourceStaticVersion)
            intent.putExtra(UpdateFragment.NEWER_ARG, !same && (newer || srcNewer))

            // to do if confirmed
            intent.putExtra(UpdateFragment.DOWNLOAD_INTENT_ARG, downloadIntent)
            activity.startActivity(intent)
        }
        task.execute(downloadSourceUrl)
    }
}
