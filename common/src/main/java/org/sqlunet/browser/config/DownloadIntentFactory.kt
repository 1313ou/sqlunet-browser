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
import com.bbou.download.preference.Settings.Mode
import org.sqlunet.settings.StorageSettings
import androidx.core.net.toUri

object DownloadIntentFactory {

    fun makeIntent(context: Context): Intent {
        var type = Mode.getModePref(context)
        if (type == null) {
            type = Mode.DOWNLOAD_ZIP
        }
        return makeIntent(context, type)
    }

    private fun makeIntent(context: Context, type: Mode): Intent {
        return when (type) {
            Mode.DOWNLOAD -> makeIntentPlainDownload(context)
            Mode.DOWNLOAD_ZIP -> makeIntentZipDownload(context)
            Mode.DOWNLOAD_ZIP_THEN_UNZIP -> makeIntentDownloadThenDeploy(context)
            else -> throw RuntimeException(type.toString())
        }
    }

    private fun makeIntentPlainDownload(context: Context): Intent {
        val dbSource = StorageSettings.getDbDownloadSourcePath(context)
        return makeIntentPlainDownload(context, dbSource)
    }

    private fun makeIntentPlainDownload(context: Context, dbSource: String?): Intent {
        val dbDest = StorageSettings.getDatabasePath(context)
        val intent = Intent(context, DownloadActivity::class.java)
        intent.putExtra(DOWNLOAD_MODE_ARG, Mode.DOWNLOAD.toString()) // plain transfer
        intent.putExtra(DOWNLOAD_FROM_ARG, dbSource) // source file
        intent.putExtra(DOWNLOAD_TO_FILE_ARG, dbDest) // dest file
        intent.putExtra(DOWNLOAD_TARGET_FILE_ARG, dbDest) // target file
        return intent
    }

    private fun makeIntentZipDownload(context: Context): Intent {
        val dbZipSource = StorageSettings.getDbDownloadZippedSourcePath(context)
        return makeIntentZipDownload(context, dbZipSource)
    }

    private fun makeIntentZipDownload(context: Context, dbZipSource: String?): Intent {
        val dbZipEntry = StorageSettings.getDbDownloadName(context)
        val dbDestDir = StorageSettings.getDataDir(context)
        val dbName = StorageSettings.getDatabaseName()
        val dbTarget = StorageSettings.getDatabasePath(context)
        val intent = Intent(context, DownloadActivity::class.java)
        intent.putExtra(DOWNLOAD_MODE_ARG, Mode.DOWNLOAD_ZIP.toString()) // zipped transfer
        intent.putExtra(DOWNLOAD_FROM_ARG, dbZipSource) // source archive
        intent.putExtra(DOWNLOAD_TO_DIR_ARG, dbDestDir) // dest directory
        intent.putExtra(DOWNLOAD_ENTRY_ARG, dbZipEntry) // zip entry
        intent.putExtra(DOWNLOAD_RENAME_FROM_ARG, dbZipEntry) // rename from
        intent.putExtra(DOWNLOAD_RENAME_TO_ARG, dbName) // rename to
        intent.putExtra(DOWNLOAD_TARGET_FILE_ARG, dbTarget) // target file
        return intent
    }

    fun makeIntentDownloadThenDeploy(context: Context): Intent {
        val dbZipSource = StorageSettings.getDbDownloadZippedSourcePath(context)
        val dbZipDest = StorageSettings.getCachedZippedPath(context)
        return makeIntentDownloadThenDeploy(context, dbZipSource, dbZipDest)
    }

    private fun makeIntentDownloadThenDeploy(context: Context, dbZipSource: String?, dbZipDest: String?): Intent {
        val dbDir = StorageSettings.getDataDir(context)
        val dbRenameFrom = StorageSettings.getDbDownloadName(context)
        val dbRenameTo = StorageSettings.getDatabaseName()
        val dbTarget = StorageSettings.getDatabasePath(context)
        val intent = Intent(context, DownloadActivity::class.java)
        intent.putExtra(DOWNLOAD_MODE_ARG, Mode.DOWNLOAD_ZIP_THEN_UNZIP.toString()) // zip transfer then unzip
        intent.putExtra(DOWNLOAD_FROM_ARG, dbZipSource) // source archive
        intent.putExtra(DOWNLOAD_TO_FILE_ARG, dbZipDest) // destination archive
        intent.putExtra(THEN_UNZIP_TO_ARG, dbDir) // unzip destination directory
        intent.putExtra(DOWNLOAD_RENAME_FROM_ARG, dbRenameFrom) // rename from
        intent.putExtra(DOWNLOAD_RENAME_TO_ARG, dbRenameTo) // rename to
        intent.putExtra(DOWNLOAD_TARGET_FILE_ARG, dbTarget) // target file
        return intent
    }

    fun makeUpdateIntent(context: Context): Intent {
        val downloadSourceType = Settings.getDatapackSourceType(context)
        val downloadSourceUrl = (if ("download" == downloadSourceType) Settings.getDatapackSource(context) else StorageSettings.getDbDownloadZippedSourcePath(context))!!
        if (!downloadSourceUrl.endsWith(Deploy.ZIP_EXTENSION)) {
            return makeIntentPlainDownload(context, downloadSourceUrl)
        }

        // source has zip extension
        var mode = Mode.getModePref(context)
        if (mode == null) {
            mode = Mode.DOWNLOAD_ZIP
        }
        return when (mode) {
            Mode.DOWNLOAD_ZIP_THEN_UNZIP -> {
                val name = downloadSourceUrl.toUri().lastPathSegment
                val cache = StorageSettings.getCacheDir(context)
                val cachePath = "$cache/$name"
                makeIntentDownloadThenDeploy(context, downloadSourceUrl, cachePath)
            }

            Mode.DOWNLOAD_ZIP -> makeIntentZipDownload(context, downloadSourceUrl)
            Mode.DOWNLOAD -> throw RuntimeException(mode.toString())
            // else -> throw RuntimeException(mode.toString())
        }
    }
}
