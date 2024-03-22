/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import org.sqlunet.browser.common.R

/**
 * Utils
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object Utils {

    // Confirm

    /**
     * Confirm
     *
     * @param context  context
     * @param titleId  title resource id
     * @param askId    ask resource id
     * @param runnable run if confirmed
     */
    @JvmStatic
    fun confirm(context: Context, @StringRes titleId: Int, @StringRes askId: Int, runnable: Runnable) {
        AlertDialog.Builder(context) //
            .setIconAttribute(android.R.attr.alertDialogIcon) //
            .setTitle(titleId) //
            .setMessage(askId) //
            .setPositiveButton(R.string.yes) { _: DialogInterface?, _: Int -> runnable.run() }.setNegativeButton(R.string.no, null).show()
    }

    // Human-readable sizes

    private const val FORMAT = "%d %s"

    private val units = arrayOf("B", "KB", "MB", "GB")

    @SuppressLint("DefaultLocale")
    private fun hrSize(x0: Long): String {
        var x = x0.toFloat()
        for (unit in units) {
            if (x > -1024.0 && x < 1024.0) {
                return String.format(FORMAT, Math.round(x), unit)
            }
            x /= 1024.0f
        }
        return String.format(FORMAT, Math.round(x), "TB")
    }

    @JvmStatic
    fun hrSize(@IntegerRes id: Int, context: Context): String {
        val x = context.resources.getInteger(id).toLong()
        return hrSize(x)
    }
}
