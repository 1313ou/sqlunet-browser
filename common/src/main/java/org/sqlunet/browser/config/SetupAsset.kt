/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.core.util.Consumer
import androidx.fragment.app.FragmentActivity
import com.bbou.concurrency.observe.TaskObserver
import com.bbou.deploy.workers.FileTasks.Companion.launchUnzip
import com.bbou.download.DownloadData
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import org.sqlunet.assetpack.AssetPackLoader
import org.sqlunet.assetpack.Settings
import org.sqlunet.browser.common.R
import org.sqlunet.browser.config.SetupDatabaseTasks.deleteDatabase
import org.sqlunet.settings.StorageSettings
import java.io.File

object SetupAsset {

    const val PREF_ASSET_PRIMARY_DEFAULT = "pref_asset_primary_default"
    const val PREF_ASSET_AUTO_CLEANUP = "pref_asset_auto_cleanup"

    /**
     * Deliver asset
     *
     * @param assetPack     asset pack name
     * @param assetDir      asset pack dir
     * @param assetZip      asset pack zip
     * @param assetZipEntry asset pack zip entry
     * @param activity      activity
     * @param observer      observer
     * @param view          view for snackbar
     * @return path if already installed
     */
    @JvmStatic
    fun deliverAsset(assetPack: String, assetDir: String, assetZip: String, assetZipEntry: String, activity: Activity, observer: TaskObserver<Pair<Number, Number>>, whenComplete: Runnable?, view: View?): String? {
        if (assetPack.isEmpty()) {
            throw RuntimeException("Asset is empty")
        }
        if (assetZip.isEmpty()) {
            throw RuntimeException("Asset zip is empty")
        }
        if (assetDir.isEmpty()) {
            throw RuntimeException("Asset dir is empty")
        }
        if (view != null) {
            Snackbar.make(view, R.string.action_asset_deliver, Snackbar.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, R.string.action_asset_deliver, Toast.LENGTH_SHORT).show()
        }

        // deliver asset (returns non null path if already installed)
        val path0 = AssetPackLoader(activity, assetPack) 
            .assetPackDelivery(activity, observer) {

                // run when delivery completes
                val assetPackManager = AssetPackManagerFactory.getInstance(activity)
                val packLocation = assetPackManager.getPackLocation(assetPack)
                if (packLocation != null) {
                    val path = packLocation.assetsPath()
                    val zipFilePath = File(File(path, assetDir), assetZip).absolutePath
                    launchUnzip(activity, observer, zipFilePath, assetZipEntry, StorageSettings.getDatabasePath(activity), Consumer { _: Boolean ->
                        Settings.recordDbAsset(activity, assetPack)
                        com.bbou.download.preference.Settings.recordDatapackSource(activity, DownloadData(zipFilePath, null, null, null, null, null, null), "asset")
                        whenComplete?.run()
                    })
                }
            }

        // if already installed
        if (path0 != null) {
            if (view != null) {
                Snackbar.make(view, R.string.action_asset_installed, Snackbar.LENGTH_LONG) //.setAction(R.string.action_asset_md5, (view2) -> FileAsyncTask.launchMd5(activity, new File(activity.getFilesDir(), TARGET_DB).getAbsolutePath()))
                    //.setAction(R.string.action_asset_dispose, (view2) -> disposeAsset(assetPack, activity, view2))
                    .show()
            } else {
                Toast.makeText(activity, R.string.action_asset_installed, Toast.LENGTH_LONG).show()
            }

            /* boolean success = */deleteDatabase(activity, StorageSettings.getDatabasePath(activity))
            val zipFile = File(File(path0, assetDir), assetZip)
            val zipFilePath = zipFile.absolutePath
            if (zipFile.exists()) {
                launchUnzip(activity, observer, zipFilePath, assetZipEntry, StorageSettings.getDatabasePath(activity), Consumer {
                    Settings.recordDbAsset(activity, assetPack)
                    com.bbou.download.preference.Settings.recordDatapackSource(activity, DownloadData(zipFilePath, null, null, null, null, null, null), "asset")
                    whenComplete?.run()
                })
            }
        }
        return path0
    }

    /**
     * Dispose of asset
     *
     * @param assetPack asset pack name
     * @param activity  activity
     */
    @JvmStatic
    fun disposeAsset(assetPack: String, activity: FragmentActivity) {
        if (assetPack.isEmpty()) {
            throw RuntimeException("Asset is empty")
        }
        AssetPackLoader.assetPackRemove(activity, assetPack)
    }

    /**
     * Dispose of assets
     *
     * @param activity activity
     */
    fun disposeAllAssets(activity: FragmentActivity) {
        val assetPack1 = activity.getString(R.string.asset_primary)
        val assetPack2 = activity.getString(R.string.asset_alt)
        AssetPackLoader.assetPackRemove(activity, assetPack1, assetPack2)
    }
}
