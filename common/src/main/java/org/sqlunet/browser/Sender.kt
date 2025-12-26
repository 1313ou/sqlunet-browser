/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser

import android.content.Context
import android.content.Intent
import org.sqlunet.browser.common.R

object Sender {

    fun send(activityContext: Context, title: String, content: CharSequence, vararg to: String) {
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        sendIntent.putExtra(Intent.EXTRA_TEXT, content)
        if (to.isNotEmpty()) {
            sendIntent.putExtra(Intent.EXTRA_EMAIL, to)
        }
        sendIntent.type = "message/rfc822" // prompts email client only
        activityContext.startActivity(Intent.createChooser(sendIntent, activityContext.getString(R.string.title_dialog_select_email)))
    }
}
