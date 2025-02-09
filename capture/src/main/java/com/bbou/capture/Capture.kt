package com.bbou.capture

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import androidx.preference.PreferenceManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Capture {

    const val CAPTURE_NAME = "capture.png"

    const val PREF_CAPTURE_IN_MEDIASTORE = "pref_capture_in_media_store"

    fun captureScreen(activity: Activity): Bitmap {
        val view = activity.window.decorView.rootView
        return captureView(view)
    }

    fun captureScreen(activity: Activity, onCaptureComplete: (Bitmap) -> Unit) {
        val view = activity.window.decorView.rootView
        return captureView(view, onCaptureComplete)
    }

    fun captureView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    fun captureView(view: View, onCaptureComplete: (Bitmap) -> Unit) {
        view.post {
            val bitmap = captureView(view)
            onCaptureComplete(bitmap)
        }
    }

    private fun getFilename(): String {
        return if (false) "Screenshot_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.png"
        else CAPTURE_NAME
    }

    fun saveBitmapToExternalCacheFile(context: Context, bitmap: Bitmap): Uri? {
        val file = File(context.externalCacheDir, getFilename())
        return saveBitmapToFile(context, file, bitmap)
    }

    fun saveBitmapToCacheFile(context: Context, bitmap: Bitmap): Uri? {
        val file = File(context.cacheDir, getFilename())
        return saveBitmapToFile(context, file, bitmap)
    }

    fun saveBitmapToFile(context: Context, file: File, bitmap: Bitmap): Uri? {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
        }
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    fun saveBitmapToMediaStoreFile(context: Context, bitmap: Bitmap): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, getFilename())
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)?.let { uri ->
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                return uri
            }
        }
        return null
    }

    fun saveBitmapToFile(context: Context, bitmap: Bitmap): Uri? {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val inMediaStore = sharedPrefs.getBoolean(PREF_CAPTURE_IN_MEDIASTORE, false)
        val uri = if (inMediaStore) saveBitmapToMediaStoreFile(context, bitmap) else saveBitmapToCacheFile(context, bitmap)
        Log.d(TAG, "Capture done $uri")
        return uri
    }

    fun saveBitmapToFile(context: Context, bitmap: Bitmap, onSaveComplete: (Boolean, Uri?) -> Unit) {
        try {
            val uri = saveBitmapToFile(context, bitmap)
            onSaveComplete(true, uri)
        } catch (e: IOException) {
            e.printStackTrace()
            onSaveComplete(false, null)
        }
    }

    fun shareCapture(activity: Activity, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        activity.startActivity(Intent.createChooser(intent, "Share Capture"))
    }

    private const val TAG = "Capture"
}