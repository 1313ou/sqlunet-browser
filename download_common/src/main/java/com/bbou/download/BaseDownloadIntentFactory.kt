/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package com.bbou.download

import android.app.Activity
import android.content.Context
import android.content.Intent
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

/**
 * Download intent factory
 */
object BaseDownloadIntentFactory {

    /**
     * Intent factory for plain download
     *
     * @param context context
     * @param activityClass activity class
     * @param dbSource source
     * @return
     */
    fun makeIntentPlainDownload(context: Context, activityClass: Class<out Activity>, dbSource: String?): Intent {
        val dbDest = Settings.getDownloadTarget(context)
        val intent = Intent(context, activityClass).apply {
            putExtra(DOWNLOAD_MODE_ARG, Settings.Mode.DOWNLOAD.toString()) // plain transfer
            putExtra(DOWNLOAD_FROM_ARG, dbSource) // source file
            putExtra(DOWNLOAD_TO_FILE_ARG, dbDest) // dest file and target file
            putExtra(DOWNLOAD_TARGET_FILE_ARG, dbDest) // dest file and target file
        }
        return intent
    }

    /**
     * Intent factory for zip download
     *
     * @param context context
     * @param activityClass activity class
     * @param dbZipSource zip source
     * @return
     */
    fun makeIntentZipDownload(context: Context, activityClass: Class<out Activity>, dbZipSource: String?): Intent {
        val dbZipEntry: String? = null
        val dbDestDir = Settings.getDatapackDir(context)
        val dbTarget = Settings.getDownloadTarget(context)
        val dbName = Settings.getDatapackName(context)
        val intent = Intent(context, activityClass).apply {
            putExtra(DOWNLOAD_MODE_ARG, Settings.Mode.DOWNLOAD_ZIP.toString()) // zipped transfer
            putExtra(DOWNLOAD_FROM_ARG, dbZipSource) // source archive
            putExtra(DOWNLOAD_TO_DIR_ARG, dbDestDir) // dest directory
            putExtra(DOWNLOAD_ENTRY_ARG, dbZipEntry) // zip entry
            putExtra(DOWNLOAD_RENAME_FROM_ARG, dbZipEntry) // rename from
            putExtra(DOWNLOAD_RENAME_TO_ARG, dbName) // rename to
            putExtra(DOWNLOAD_TARGET_FILE_ARG, dbTarget) // target file
        }
        return intent
    }

    /**
     * Intent factory for download and deploy
     *
     * @param context context
     * @param activityClass activity class
     * @param dbZipSource zip source
     * @param dbZipDest zip destination
     * @return intent
     */
    fun makeIntentDownloadThenDeploy(context: Context, activityClass: Class<out Activity>, dbZipSource: String?, dbZipDest: String?): Intent {
        val dbDir = Settings.getDatapackDir(context)
        val dbRenameFrom = Settings.getDatapackName(context)
        val dbRenameTo = Settings.getDatapackName(context)
        val dbTarget = Settings.getDownloadTarget(context)
        val intent = Intent(context, activityClass).apply {
            putExtra(DOWNLOAD_MODE_ARG, Settings.Mode.DOWNLOAD_ZIP_THEN_UNZIP.toString()) // zip transfer then unzip
            putExtra(DOWNLOAD_FROM_ARG, dbZipSource) // source archive
            putExtra(DOWNLOAD_TO_FILE_ARG, dbZipDest) // destination archive
            putExtra(THEN_UNZIP_TO_ARG, dbDir) // unzip destination directory
            putExtra(DOWNLOAD_RENAME_FROM_ARG, dbRenameFrom) // rename from
            putExtra(DOWNLOAD_RENAME_TO_ARG, dbRenameTo) // rename to
            putExtra(DOWNLOAD_TARGET_FILE_ARG, dbTarget) // target file
        }
        return intent
    }
}
