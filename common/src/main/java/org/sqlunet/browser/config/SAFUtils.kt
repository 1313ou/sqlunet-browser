/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.arch.core.util.Function
import androidx.core.util.Consumer
import java.io.FileDescriptor
import java.io.IOException
import java.io.InputStream

object SAFUtils {
    private const val TAG = "SAFUtils"

    // L I S T E N E R

    fun makeListener(activity: AppCompatActivity, consumer: Consumer<Uri?>): ActivityResultLauncher<Intent> {
        return activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultData = result.data
                if (resultData != null) {
                    val uri = resultData.data
                    Log.i(TAG, "Uri: " + uri.toString())
                    consumer.accept(uri)
                }
            }
        }
    }

    // P I C K

    fun pick(launcher: ActivityResultLauncher<Intent>, vararg mimeTypes: String) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)

        // Filter to only show results that can be "opened", such as a file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        // Filter to show only docs of selected type, using the image MIME data type.
        // To search for all documents available via installed storage providers, it would be "*/*".
        setType(intent, *mimeTypes)
        launcher.launch(intent)
    }

    private fun setType(intent: Intent, vararg mimeTypes: String) {
        intent.setType(if (mimeTypes.size == 1) mimeTypes[0] else "*/*")
        if (mimeTypes.isNotEmpty()) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
    }

    // Q U E R Y

    fun querySize(uri: Uri, resolver: ContentResolver): String? {
        // The query, since it only applies to a single document, will only return one row. There's no need to filter, sort, or select fields, since we want all fields for one document.
        resolver.query(uri, null, null, null, null, null).use {
            // moveToFirst() returns false if the cursor has 0 rows.  Very handy for "if there's anything to look at, look at it" conditionals.
            if (it != null && it.moveToFirst()) {
                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                // If the size is unknown, the value stored is null.
                // But since an int can't be null in Java, the behavior is implementation-specific, which is just a fancy term for "unpredictable".
                // So as a rule, check if it's null before assigning to an int.
                // This will happen often: The storage API allows for remote files, whose size might not be locally known.
                return if (!it.isNull(sizeIndex)) {
                    // Technically the column stores an int, but cursor.getString() will do the conversion automatically.
                    it.getString(sizeIndex)
                } else {
                    "N/A"
                }
            }
        }
        return "Error"
    }

    fun queryName(uri: Uri, resolver: ContentResolver): String? {
        // The query, since it only applies to a single document, will only return one row. There's no need to filter, sort, or select fields, since we want all fields for one document.
        resolver.query(uri, null, null, null, null, null).use {
            // moveToFirst() returns false if the cursor has 0 rows.  Very handy for "if there's anything to look at, look at it" conditionals.
            if (it != null && it.moveToFirst()) {
                // Note it's called "Display Name".  This is provider-specific, and might not necessarily be the file name.
                val displayNameIdx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                return it.getString(displayNameIdx)
            }
        }
        return "Error"
    }

    fun getType(uri: Uri, resolver: ContentResolver): String? {
        return resolver.getType(uri)
    }

    // F I L E   D E S C R I P T O R

    @Throws(IOException::class)
    fun <R> applyFileDescriptor(uri: Uri, resolver: ContentResolver, f: Function<FileDescriptor?, R>): R? {
        resolver.openFileDescriptor(uri, "r").use { return f.apply(it!!.fileDescriptor) }
    }

    // I N P U T S T R E A M

    @Throws(IOException::class)
    fun <R> applyInputStream(uri: Uri, resolver: ContentResolver, f: Function<InputStream?, R>): R? {
        resolver.openInputStream(uri).use { return f.apply(it) }
    }
}
