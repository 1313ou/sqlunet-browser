/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.assetpack

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import com.bbou.concurrency.Cancelable
import com.bbou.concurrency.observe.TaskObserver
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.android.gms.tasks.Task
import com.google.android.play.core.assetpacks.AssetPackManager
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import com.google.android.play.core.assetpacks.AssetPackState
import com.google.android.play.core.assetpacks.AssetPackStateUpdateListener
import com.google.android.play.core.assetpacks.AssetPackStates
import com.google.android.play.core.assetpacks.model.AssetPackErrorCode
import com.google.android.play.core.assetpacks.model.AssetPackStatus
import java.io.File
import java.util.Locale

/**
 * Asset pack loader
 *
 * @param context context
 * @param pack    asset pack name
 */
class AssetPackLoader(context: Context, private val pack: String) : Cancelable {

    private val assetPackManager: AssetPackManager = AssetPackManagerFactory.getInstance(context)
    private var waitForWifiConfirmationShown = false

    /**
     * Asset pack path
     *
     * @return asset pack path if installed, null other wise
     */
    fun assetPackPathIfInstalled(): String? {
        val packLocation = assetPackManager.getPackLocation(pack)
        if (packLocation != null) {
            val path = packLocation.assetsPath()
            Log.d(TAG, "Asset path $path")
            return path
        }
        return null
    }

    /**
     * Asset pack delivery
     *
     * @param activity  activity
     * @param observer  observer
     * @param whenReady to run when ready
     * @return asset pack path if pack was installed
     */
    fun assetPackDelivery(activity: Activity, observer: TaskObserver<Pair<Number, Number>>, whenReady: Runnable?): String? {
        val packLocation = assetPackManager.getPackLocation(pack)
        if (packLocation != null) {
            val path = packLocation.assetsPath()
            Log.d(TAG, "Asset path $path")
            return path
        }

        // observer
        observer.taskStart(this)

        // listener
        assetPackManager.registerListener(Listener(activity, observer, whenReady))

        // fetch if uninstalled
        assetPackManager
            .getPackStates(listOf(pack))
            .addOnCompleteListener { task: Task<AssetPackStates?> ->
                try {
                    // state
                    val states = task.result
                    val state = states?.packStates()?.get(pack)
                    val status = state?.status()
                    if (status != null) {
                        Log.i(TAG, String.format("AssetPack %s status %s %d/%d %d%%", state.name(), statusToString(status), state.bytesDownloaded(), state.totalBytesToDownload(), state.transferProgressPercentage()))
                        if (AssetPackStatus.NOT_INSTALLED == status || AssetPackStatus.CANCELED == status || AssetPackStatus.FAILED == status) {
                            // do not eat error
                            if (AssetPackStatus.FAILED == status) {
                                val errorCode = state.errorCode()
                                observer.taskUpdate(statusToString(status) + ' ' + errorToString(errorCode))
                            }

                            // fetch
                            // returns an AssetPackStates object containing a list of packs and their initial download states and sizes.
                            // if an asset pack requested via fetch() is already downloading, the download status is returned and no additional download is started.
                            /* val fetchTask0 : Task<AssetPackStates> = */
                            assetPackManager.fetch(listOf(pack))
                                .addOnCompleteListener { Log.i(TAG, "OnFetchCompleted") }
                                .addOnFailureListener { exception: Exception -> Log.i(TAG, "OnFetchFailure " + exception.message) }
                                .addOnSuccessListener {
                                    Log.i(TAG, "OnFetchSuccess ")
                                    val packLocation2 = assetPackManager.getPackLocation(pack)
                                    Log.i(TAG, "OnFetchSuccess, Path asset " + if (packLocation2 == null) "null" else packLocation2.assetsPath())
                                }
                        } else if (AssetPackStatus.COMPLETED == status) {
                            val packLocation1 = assetPackManager.getPackLocation(pack)
                            Log.i(TAG, "Status asset path " + if (packLocation1 == null) "null" else packLocation1.assetsPath())
                            observer.taskUpdate(statusToString(status))
                            observer.taskFinish(true)
                            whenReady?.run()
                        }
                    } else {
                        Log.d(TAG, "AssetPack null status")
                    }
                } catch (e: RuntimeExecutionException) {
                    var message = e.message
                    if (message == null) {
                        message = "<unknown>"
                    }
                    Log.e(TAG, "Failure $message")
                    observer.taskUpdate(message)
                    observer.taskFinish(false)
                }
            }
        return null
    }

    internal inner class Listener(val activity: Activity, private val observer: TaskObserver<Pair<Number, Number>>, private val whenReady: Runnable?) : AssetPackStateUpdateListener {

        override fun onStateUpdate(state: AssetPackState) {
            val status = state.status()
            val statusStr = statusToString(status)
            Log.d(TAG, "Status $statusStr")
            when (status) {
                AssetPackStatus.UNKNOWN, AssetPackStatus.NOT_INSTALLED, AssetPackStatus.PENDING -> // Asset pack state is not known.
                    observer.taskUpdate(statusStr)

                AssetPackStatus.DOWNLOADING -> {
                    val downloaded = state.bytesDownloaded()
                    val totalSize = state.totalBytesToDownload()
                    Log.i(TAG, "Status downloading progress " + String.format("%d / %d", downloaded, totalSize))
                    observer.taskUpdate(statusStr)
                    observer.taskProgress(Pair<Number, Number>(downloaded, totalSize))
                }

                AssetPackStatus.TRANSFERRING -> {
                    val percent2 = state.transferProgressPercentage()
                    val percent2Str = String.format(Locale.getDefault(), "%d %%", percent2)
                    Log.i(TAG, "Status transferring progress $percent2Str")
                    observer.taskUpdate("$statusStr $percent2Str")
                    observer.taskProgress(Pair<Number, Number>(percent2, -1))
                }

                AssetPackStatus.WAITING_FOR_WIFI -> {
                    observer.taskUpdate(statusStr)
                    if (!waitForWifiConfirmationShown) {
                        assetPackManager.showConfirmationDialog(activity).addOnSuccessListener { resultCode: Int ->
                            if (resultCode == Activity.RESULT_OK) {
                                Log.d(TAG, "Confirmation dialog has been accepted.")
                            } else if (resultCode == Activity.RESULT_CANCELED) {
                                Log.d(TAG, "Confirmation dialog has been denied by the user.")
                            }
                        }
                        waitForWifiConfirmationShown = true
                    }
                }

                AssetPackStatus.COMPLETED -> {
                    assetPackManager.unregisterListener(this)
                    val packLocation1 = assetPackManager.getPackLocation(pack)
                    Log.i(TAG, "Status asset path " + if (packLocation1 == null) "null" else packLocation1.assetsPath())
                    observer.taskUpdate(statusStr)
                    observer.taskFinish(true)
                    whenReady?.run()
                }

                AssetPackStatus.FAILED -> {
                    assetPackManager.unregisterListener(this)
                    val errorCode = state.errorCode()
                    Log.e(TAG, "Status error " + errorCode + ' ' + errorToString(errorCode))
                    observer.taskUpdate(statusStr)
                    observer.taskFinish(false)
                    observer.taskUpdate("Error " + errorToString(errorCode))
                }

                AssetPackStatus.CANCELED -> {
                    assetPackManager.unregisterListener(this)
                    val errorCode2 = state.errorCode()
                    Log.i(TAG, "Status canceled " + errorCode2 + ' ' + errorToString(errorCode2))
                    observer.taskUpdate(statusStr)
                    observer.taskFinish(false)
                }
            }
        }
    }

    /**
     * Cancel operation
     *
     * @param mayInterruptIfRunning may interrupt if running
     * @return true
     */
    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        Log.d(TAG, "User cancel")
        assetPackManager.cancel(listOf(pack))
        isCancelled = true
        return true
    }

    private var isCancelled = false

    init {
        dumpLocalTesting(context)
    }

    override fun isCancelled(): Boolean {
        return isCancelled
    }

    companion object {

        private const val TAG = "AssetPackLoader"

        /**
         * Status to string
         *
         * @param status numeric status
         * @return status string
         */
        private fun statusToString(status: Int): String {
            when (status) {
                AssetPackStatus.UNKNOWN -> return "unknown"
                AssetPackStatus.PENDING -> return "pending"
                AssetPackStatus.DOWNLOADING -> return "downloading"
                AssetPackStatus.TRANSFERRING -> return "transferring"
                AssetPackStatus.COMPLETED -> return "completed"
                AssetPackStatus.FAILED -> return "failed"
                AssetPackStatus.CANCELED -> return "canceled"
                AssetPackStatus.WAITING_FOR_WIFI -> return "waiting for wifi"
                AssetPackStatus.NOT_INSTALLED -> return "not installed"
            }
            return "illegal"
        }

        /**
         * Error to string
         *
         * @param error numeric static
         * @return error string
         */
        private fun errorToString(error: Int): String {
            when (error) {
                AssetPackErrorCode.NO_ERROR -> return "no error"
                AssetPackErrorCode.APP_UNAVAILABLE -> return "app unavailable"
                AssetPackErrorCode.PACK_UNAVAILABLE -> return "pack unavailable"
                AssetPackErrorCode.INVALID_REQUEST -> return "invalid request"
                AssetPackErrorCode.DOWNLOAD_NOT_FOUND -> return "download not found"
                AssetPackErrorCode.API_NOT_AVAILABLE -> return "api not available"
                AssetPackErrorCode.NETWORK_ERROR -> return "network error"
                AssetPackErrorCode.ACCESS_DENIED -> return "access denied"
                AssetPackErrorCode.INSUFFICIENT_STORAGE -> return "insufficient storage"
                AssetPackErrorCode.APP_NOT_OWNED -> return "app not owned (not acquired from Play)"
                AssetPackErrorCode.INTERNAL_ERROR -> return "internal error"
            }
            return "unknown $error"
        }

        /**
         * Dispose of asset pack
         *
         * @param activity activity
         * @param packs    packs to delete
         */
        fun assetPackRemove(activity: Activity, vararg packs: String) {
            val assetPackManager = AssetPackManagerFactory.getInstance(activity)
            for (pack in packs) {
                if (pack.isNotEmpty()) {
                    assetPackManager.removePack(pack)
                        .addOnCompleteListener { task3: Task<Void?> -> Log.d(TAG, "Remove success " + task3.isSuccessful) }
                        .addOnFailureListener { exception: Exception -> Log.e(TAG, "Remove failure " + exception.message) }
                }
            }
        }

        /**
         * Local testing dump
         *
         * @param context context
         */
        fun dumpLocalTesting(context: Context) {
            val ds = ContextCompat.getExternalFilesDirs(context, null)
            for (d in ds) {
                val dir = File(d, "local_testing")
                if (dir.exists() && dir.isDirectory()) {
                    val content = File(d, "local_testing").list()
                    if (content != null) {
                        for (f in content) {
                            Log.d(TAG, "exists $f")
                        }
                    }
                }
            }
        }
    }
}