/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.config

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.bbou.deploy.workers.Deploy
import com.bbou.download.Keys.DOWNLOAD_ENTRY_ARG
import com.bbou.download.Keys.DOWNLOAD_FROM_ARG
import com.bbou.download.Keys.DOWNLOAD_MODE_ARG
import com.bbou.download.Keys.DOWNLOAD_RENAME_FROM_ARG
import com.bbou.download.Keys.DOWNLOAD_RENAME_TO_ARG
import com.bbou.download.Keys.DOWNLOAD_TARGET_FILE_ARG
import com.bbou.download.Keys.DOWNLOAD_TO_DIR_ARG
import com.bbou.download.Keys.DOWNLOAD_TO_FILE_ARG
import com.bbou.download.Keys.THEN_UNZIP_TO_ARG
import com.bbou.download.preference.Settings
import com.bbou.download.preference.Settings.Mode.Companion.getModePref
import com.bbou.download.preference.Settings.getDatapackSource
import com.bbou.download.preference.Settings.getDatapackSourceType
import org.sqlunet.settings.StorageSettings.getCacheDir
import org.sqlunet.settings.StorageSettings.getCachedZippedPath
import org.sqlunet.settings.StorageSettings.getDataDir
import org.sqlunet.settings.StorageSettings.getDatabaseName
import org.sqlunet.settings.StorageSettings.getDatabasePath
import org.sqlunet.settings.StorageSettings.getDbDownloadName
import org.sqlunet.settings.StorageSettings.getDbDownloadSourcePath
import org.sqlunet.settings.StorageSettings.getDbDownloadZippedSourcePath

object DownloadIntentFactory {

    @JvmStatic
    fun makeIntent(context: Context): Intent {
        var type = getModePref(context)
        if (type == null) {
            type = Settings.Mode.DOWNLOAD_ZIP
        }
        return makeIntent(context, type)
    }

    private fun makeIntent(context: Context, type: Settings.Mode): Intent {
        return when (type) {
            Settings.Mode.DOWNLOAD -> makeIntentPlainDownload(context)
            Settings.Mode.DOWNLOAD_ZIP -> makeIntentZipDownload(context)
            Settings.Mode.DOWNLOAD_ZIP_THEN_UNZIP -> makeIntentDownloadThenDeploy(context)
            else -> throw RuntimeException(type.toString())
        }
    }

    private fun makeIntentPlainDownload(context: Context): Intent {
        val dbSource = getDbDownloadSourcePath(context)
        return makeIntentPlainDownload(context, dbSource)
    }

    private fun makeIntentPlainDownload(context: Context, dbSource: String?): Intent {
        val dbDest = getDatabasePath(context)
        val intent = Intent(context, DownloadActivity::class.java)
        intent.putExtra(DOWNLOAD_MODE_ARG, Settings.Mode.DOWNLOAD.toString()) // plain transfer
        intent.putExtra(DOWNLOAD_FROM_ARG, dbSource) // source file
        intent.putExtra(DOWNLOAD_TO_FILE_ARG, dbDest) // dest file
        intent.putExtra(DOWNLOAD_TARGET_FILE_ARG, dbDest) // target file
        return intent
    }

    private fun makeIntentZipDownload(context: Context): Intent {
        val dbZipSource = getDbDownloadZippedSourcePath(context)
        return makeIntentZipDownload(context, dbZipSource)
    }

    private fun makeIntentZipDownload(context: Context, dbZipSource: String?): Intent {
        val dbZipEntry = getDbDownloadName(context)
        val dbDestDir = getDataDir(context)
        val dbName = getDatabaseName()
        val dbTarget = getDatabasePath(context)
        val intent = Intent(context, DownloadActivity::class.java)
        intent.putExtra(DOWNLOAD_MODE_ARG, Settings.Mode.DOWNLOAD_ZIP.toString()) // zipped transfer
        intent.putExtra(DOWNLOAD_FROM_ARG, dbZipSource) // source archive
        intent.putExtra(DOWNLOAD_TO_DIR_ARG, dbDestDir) // dest directory
        intent.putExtra(DOWNLOAD_ENTRY_ARG, dbZipEntry) // zip entry
        intent.putExtra(DOWNLOAD_RENAME_FROM_ARG, dbZipEntry) // rename from
        intent.putExtra(DOWNLOAD_RENAME_TO_ARG, dbName) // rename to
        intent.putExtra(DOWNLOAD_TARGET_FILE_ARG, dbTarget) // target file
        return intent
    }

    @JvmStatic
    fun makeIntentDownloadThenDeploy(context: Context): Intent {
        val dbZipSource = getDbDownloadZippedSourcePath(context)
        val dbZipDest = getCachedZippedPath(context)
        return makeIntentDownloadThenDeploy(context, dbZipSource, dbZipDest)
    }

    private fun makeIntentDownloadThenDeploy(context: Context, dbZipSource: String?, dbZipDest: String?): Intent {
        val dbDir = getDataDir(context)
        val dbRenameFrom = getDbDownloadName(context)
        val dbRenameTo = getDatabaseName()
        val dbTarget = getDatabasePath(context)
        val intent = Intent(context, DownloadActivity::class.java)
        intent.putExtra(DOWNLOAD_MODE_ARG, Settings.Mode.DOWNLOAD_ZIP_THEN_UNZIP.toString()) // zip transfer then unzip
        intent.putExtra(DOWNLOAD_FROM_ARG, dbZipSource) // source archive
        intent.putExtra(DOWNLOAD_TO_FILE_ARG, dbZipDest) // destination archive
        intent.putExtra(THEN_UNZIP_TO_ARG, dbDir) // unzip destination directory
        intent.putExtra(DOWNLOAD_RENAME_FROM_ARG, dbRenameFrom) // rename from
        intent.putExtra(DOWNLOAD_RENAME_TO_ARG, dbRenameTo) // rename to
        intent.putExtra(DOWNLOAD_TARGET_FILE_ARG, dbTarget) // target file
        return intent
    }

    @JvmStatic
    fun makeUpdateIntent(context: Context): Intent {
        val downloadSourceType = getDatapackSourceType(context)
        val downloadSourceUrl = (if ("download" == downloadSourceType) getDatapackSource(context) else getDbDownloadZippedSourcePath(context))!!
        if (!downloadSourceUrl.endsWith(Deploy.ZIP_EXTENSION)) //
        {
            return makeIntentPlainDownload(context, downloadSourceUrl)
        }

        // source has zip extension
        var mode = getModePref(context)
        if (mode == null) {
            mode = Settings.Mode.DOWNLOAD_ZIP
        }
        return when (mode) {
            Settings.Mode.DOWNLOAD_ZIP_THEN_UNZIP -> {
                val name = Uri.parse(downloadSourceUrl).lastPathSegment
                val cache = getCacheDir(context)
                val cachePath = "$cache/$name"
                makeIntentDownloadThenDeploy(context, downloadSourceUrl, cachePath)
            }

            Settings.Mode.DOWNLOAD_ZIP -> makeIntentZipDownload(context, downloadSourceUrl)
            Settings.Mode.DOWNLOAD -> throw RuntimeException(mode.toString())
            else -> throw RuntimeException(mode.toString())
        }
    }
}