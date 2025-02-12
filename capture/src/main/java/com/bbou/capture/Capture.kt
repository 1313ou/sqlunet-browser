/*
 * Copyright (c) 2025. Bernard Bou <1313ou@gmail.com>
 */

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
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
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

    // capture bitmap

    @JvmStatic
    fun getBackgroundFromTheme(context: Context): Int {
        val theme = context.theme

        // Check if the attribute is defined in the theme
        val typedValue = TypedValue()
        if (theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)) {
            if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                // It's a color
                return typedValue.data
            } else if (typedValue.type == TypedValue.TYPE_REFERENCE) {
                // It's a reference to a color (e.g., in colors.xml)
                return ContextCompat.getColor(context, typedValue.resourceId) // Use ContextCompat for compatibility
            }
        }
        // Fallback color (in case the attribute is not defined)
        return ContextCompat.getColor(context, android.R.color.background_light)
    }

    @JvmOverloads
    fun captureScreen(activity: Activity, backGround: Int? = null): Bitmap {
        val view = activity.window.decorView.rootView
        return captureView(view, backGround = backGround)
    }

    @JvmOverloads
    fun captureScreen(activity: Activity, backGround: Int? = null, onCaptureComplete: (Bitmap) -> Unit) {
        val view = activity.window.decorView.rootView
        return captureView(view, backGround = backGround, onCaptureComplete)
    }

    @JvmOverloads
    fun captureView(view: View, backGround: Int? = null): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        if (backGround != null)
            bitmap.eraseColor(backGround)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    @JvmOverloads
    fun captureView(view: View, backGround: Int? = null, onCaptureComplete: (Bitmap) -> Unit) {
        view.post {
            val bitmap = captureView(view, backGround = backGround)
            onCaptureComplete(bitmap)
        }
    }

    // save

    private fun getFilename(): String {
        return if (false) "Screenshot_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.png"
        else CAPTURE_NAME
    }

    fun saveBitmapToExternalCacheFile(bitmap: Bitmap, context: Context): Uri? {
        val file = File(context.externalCacheDir, getFilename())
        return saveBitmapToFile(bitmap, file, context)
    }

    fun saveBitmapToCacheFile(bitmap: Bitmap, context: Context): Uri? {
        val file = File(context.cacheDir, getFilename())
        return saveBitmapToFile(bitmap, file, context)
    }

    fun saveBitmapToFile(bitmap: Bitmap, file: File, context: Context): Uri? {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
        }
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    fun saveBitmapToMediaStoreFile(bitmap: Bitmap, context: Context): Uri? {
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

    fun saveBitmapToFile(bitmap: Bitmap, context: Context): Uri? {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val inMediaStore = sharedPrefs.getBoolean(PREF_CAPTURE_IN_MEDIASTORE, false)
        val uri = if (inMediaStore) saveBitmapToMediaStoreFile(bitmap, context) else saveBitmapToCacheFile(bitmap, context)
        Log.d(TAG, "Capture done $uri")
        return uri
    }

    fun saveBitmapToFile(bitmap: Bitmap, context: Context, onSaveComplete: (Boolean, Uri?) -> Unit) {
        try {
            val uri = saveBitmapToFile(bitmap, context)
            onSaveComplete(true, uri)
        } catch (e: IOException) {
            e.printStackTrace()
            onSaveComplete(false, null)
        }
    }

    // share

    fun sharePng(activity: Activity, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        activity.startActivity(Intent.createChooser(intent, "Share Capture"))
    }

    // combine

    @JvmOverloads
    fun captureAndSave(view: View, context: Context, backGround: Int? = null) {
        val bm = captureView(view, backGround = backGround)
        saveBitmapToFile(bm, context)
    }

    @JvmOverloads
    fun captureAndShare(view: View, activity: Activity, backGround: Int? = null) {
        val bm = captureView(view, backGround = backGround)
        val uri = saveBitmapToFile(bm, activity)
        if (uri != null)
            sharePng(activity, uri)
    }

    private const val TAG = "Capture"
}