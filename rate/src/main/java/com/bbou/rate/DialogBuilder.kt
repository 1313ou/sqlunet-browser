/*
 * Copyright (c) 2016. Shintaro Katafuchi hotchemi
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.rate

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder

internal object DialogBuilder {

    fun build(context: Context, options: DialogOptions): Dialog {
        val view = options.view
        return MaterialAlertDialogBuilder(context)
            // message
            .setMessage(options.getMessageText(context))
            // title
            .apply { if (options.shouldShowTitle()) setTitle(options.getTitleText(context)) }
            // title
            .apply { if (view != null) setView(view) }
            // cancelable
            .setCancelable(options.cancelable)

            // positive button
            .setPositiveButton(options.getPositiveText(context)) { _, _ ->
                val intentToAppstore = options.storeType.getIntent(context)
                try {
                    context.startActivity(intentToAppstore)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
                PreferenceHelper.setAgreeShowDialog(context, false)
            }
            .apply {
                // neutral button
                if (options.shouldShowNeutralButton()) {
                    setNeutralButton(options.getNeutralText(context)) { _, _ -> PreferenceHelper.setRemindInterval(context) }
                }

                // negative button
                if (options.shouldShowNegativeButton()) {
                    setNegativeButton(options.getNegativeText(context)) { _, _ -> PreferenceHelper.setAgreeShowDialog(context, false) }
                }
            }
            .create()
    }
}