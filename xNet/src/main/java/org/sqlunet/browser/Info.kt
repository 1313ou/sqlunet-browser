/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.app.Activity
import android.text.SpannableStringBuilder
import android.widget.TextView
import androidx.annotation.StringRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.sqlunet.style.Report.appendHeader
import org.sqlunet.xnet.R
import org.sqlunet.core.R as CoreR

/**
 * Info helper
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object Info {

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

    fun info(activity: Activity, @StringRes messageId: Int, vararg lines: CharSequence) {
        val alert = MaterialAlertDialogBuilder(activity, CoreR.style.MyM3AlertDialogOverlay)
        alert.setTitle(R.string.action_info)
        alert.setMessage(messageId)
        alert.setNegativeButton(R.string.action_dismiss) { _, _ -> }
        val sb = SpannableStringBuilder()
        build(sb, *lines)
        val extra = TextView(activity)
        extra.setPadding(20, 0, 20, 0)
        extra.text = sb
        alert.setView(extra)
        alert.show()
    }
}
