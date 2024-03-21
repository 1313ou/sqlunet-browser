/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.app.Activity
import android.content.DialogInterface
import android.text.SpannableStringBuilder
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import org.sqlunet.style.Report.appendHeader
import org.sqlunet.xnet.R

/**
 * Info helper
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object Info {

    @JvmStatic
    fun build(sb: SpannableStringBuilder, vararg lines: CharSequence) {
        for ((i, line) in lines.withIndex()) {
            if (i % 2 == 0) {
                appendHeader(sb, line)
            } else {
                sb.append(line)
            }
            sb.append('\n')
        }
    }

    @JvmStatic
    fun info(activity: Activity, @StringRes messageId: Int, vararg lines: CharSequence) {
        val alert = AlertDialog.Builder(activity)
        alert.setTitle(R.string.action_info)
        alert.setMessage(messageId)
        alert.setNegativeButton(R.string.action_dismiss) { _: DialogInterface?, _: Int -> }
        val sb = SpannableStringBuilder()
        build(sb, *lines)
        val extra = TextView(activity)
        extra.setPadding(20, 0, 20, 0)
        extra.text = sb
        alert.setView(extra)
        alert.show()
    }
}
