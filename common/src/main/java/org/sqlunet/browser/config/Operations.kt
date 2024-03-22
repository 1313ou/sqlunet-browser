/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.net.Uri
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.bbou.deploy.workers.FileTasks.Companion.launchCopy
import com.bbou.deploy.workers.FileTasks.Companion.launchMd5
import com.bbou.deploy.workers.FileTasks.Companion.launchUnzip
import com.bbou.download.preference.Settings.unrecordDatapack
import com.bbou.download.workers.choose.FileTaskChooser.copyFromFile
import com.bbou.download.workers.choose.FileTaskChooser.unzipEntryFromArchive
import com.bbou.download.workers.choose.MD5Chooser.md5
import org.sqlunet.browser.common.R
import org.sqlunet.settings.StorageReports.getStyledCachesNamesValues
import org.sqlunet.settings.StorageReports.getStyledDownloadNamesValues
import org.sqlunet.settings.StorageReports.getStyledStorageDirectoriesNamesValues
import org.sqlunet.settings.StorageSettings.getDataDir
import org.sqlunet.settings.StorageSettings.getDatabasePath

object Operations {

    fun copy(uri: Uri, activity: FragmentActivity) {
        launchCopy(activity, uri, getDatabasePath(activity)) { result: Boolean ->
            if (result) {
                activity.finish()
            } else {
                val tv = activity.findViewById<TextView>(R.id.status)
                tv?.setText(R.string.result_fail)
            }
        }
    }

    fun unzip(uri: Uri, activity: FragmentActivity) {
        launchUnzip(activity, uri, getDataDir(activity)) { result: Boolean ->
            if (result) {
                activity.finish()
            } else {
                val tv = activity.findViewById<TextView>(R.id.status)
                tv?.setText(R.string.result_fail)
            }
        }
    }

    fun unzipEntry(uri: Uri, entry: String, activity: FragmentActivity) {
        launchUnzip(activity, uri, entry, getDataDir(activity)) { result: Boolean ->
            if (result) {
                activity.finish()
            } else {
                val tv = activity.findViewById<TextView>(R.id.status)
                tv?.setText(R.string.result_fail)
            }
        }
    }

    fun md5(uri: Uri, activity: FragmentActivity) {
        launchMd5(activity, uri) { activity.finish() }
    }

    // file

    @JvmStatic
    fun copy(activity: FragmentActivity) {
        val downloadDirs = getStyledDownloadNamesValues(activity)
        val cachedDirs = getStyledCachesNamesValues(activity)
        copyFromFile(activity, getDatabasePath(activity), mergeNamesValues(downloadDirs, cachedDirs))
        unrecordDatapack(activity)
    }

    @JvmStatic
    fun unzip(activity: FragmentActivity) {
        val downloadDirs = getStyledDownloadNamesValues(activity)
        val cachedDirs = getStyledCachesNamesValues(activity)
        unzipEntryFromArchive(activity, getDatabasePath(activity), mergeNamesValues(downloadDirs, cachedDirs))
        unrecordDatapack(activity)
    }

    @JvmStatic
    fun md5(activity: FragmentActivity) {
        val downloadDirs = getStyledDownloadNamesValues(activity)
        val cachedDirs = getStyledCachesNamesValues(activity)
        val storageDirs = getStyledStorageDirectoriesNamesValues(activity)
        md5(activity, mergeNamesValues(downloadDirs, cachedDirs, storageDirs))
    }

    @SafeVarargs
    private fun mergeNamesValues(nameValues1: android.util.Pair<Array<CharSequence>, Array<String>>, vararg otherNameValues: android.util.Pair<Array<CharSequence>, Array<String>>): Pair<Array<CharSequence>, Array<String>> {
        val names: MutableList<CharSequence> = ArrayList(listOf(*nameValues1.first))
        for (nameValues2 in otherNameValues) {
            names.addAll(listOf(*nameValues2.first))
        }
        val values: MutableList<String> = ArrayList(listOf(*nameValues1.second))
        for (nameValues2 in otherNameValues) {
            values.addAll(listOf(*nameValues2.second))
        }
        return Pair(names.toTypedArray<CharSequence>(), values.toTypedArray<String>())
    }

    // exec

    fun execSql(uri: Uri, activity: FragmentActivity) {
        ExecAsyncTask.launchExecUri(activity, uri, getDatabasePath(activity)) { result: Boolean ->
            if (result) {
                activity.finish()
            } else {
                val tv = activity.findViewById<TextView>(R.id.status)
                tv?.setText(R.string.result_fail)
            }
        }
    }

    fun execZippedSql(uri: Uri, entry: String, activity: FragmentActivity) {
        ExecAsyncTask.launchExecZippedUri(activity, uri, entry, getDatabasePath(activity)) { result: Boolean ->
            if (result) {
                activity.finish()
            } else {
                val tv = activity.findViewById<TextView>(R.id.status)
                tv?.setText(R.string.result_fail)
            }
        }
    }
}
