/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.text.SpannableStringBuilder
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import org.sqlunet.browser.AbstractApplication
import org.sqlunet.browser.common.R
import kotlin.math.roundToLong

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
    fun confirm(context: Context, @StringRes titleId: Int, @StringRes askId: Int, runnable: Runnable) {
        AlertDialog.Builder(context)
            .setIconAttribute(android.R.attr.alertDialogIcon)
            .setTitle(titleId)
            .setMessage(askId)
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
                return String.format(FORMAT, x.roundToLong(), unit)
            }
            x /= 1024.0f
        }
        return String.format(FORMAT, x.roundToLong(), "TB")
    }

    fun hrSize(@IntegerRes id: Int, context: Context): String {
        val x = context.resources.getInteger(id).toLong()
        return hrSize(x)
    }

    fun reportVersion(context: Context): CharSequence {
        val sb = SpannableStringBuilder()
        val packageName = context.applicationInfo.packageName
        sb.append(packageName)
        sb.append('\n')
        val pInfo: PackageInfo
        try {
            pInfo = context.packageManager.getPackageInfo(packageName, 0)
            val code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) pInfo.longVersionCode else @Suppress("DEPRECATION") pInfo.versionCode.toLong()
            sb.append("version: ")
            sb.append(code.toString())
            sb.append('\n')
        } catch (e: PackageManager.NameNotFoundException) {
            sb.append("package info: ")
            sb.append(e.message)
            sb.append('\n')
        }
        sb.append("api: ")
        sb.append(Build.VERSION.SDK_INT.toString())
        sb.append(' ')
        sb.append(Build.VERSION.CODENAME)
        sb.append('\n')

        val app = context.applicationContext as AbstractApplication
        sb.append("build time: ")
        sb.append(app.buildTime())
        sb.append('\n')
        sb.append("git commit hash: ")
        sb.append(app.gitHash())
        sb.append('\n')
        return sb
    }

    fun version(context: Context) {
        val version = reportVersion(context)
        AlertDialog.Builder(context)
            .setTitle(R.string.app_name)
            .setMessage(version)
            .setNegativeButton(R.string.action_dismiss) { _: DialogInterface?, _: Int -> }
            .show()
    }
}
