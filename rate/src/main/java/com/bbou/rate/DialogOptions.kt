/*
 * Copyright (c) 2016. Shintaro Katafuchi hotchemi
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.rate

import android.content.Context
import android.view.View

internal class DialogOptions {

    private var showNeutralButton = true
    private var showNegativeButton = true
    private var showTitle = true

    @JvmField
    var cancelable = false

    @JvmField
    var storeType = StoreType.GOOGLE
    var titleResId = R.string.rate_dialog_title
    var messageResId = R.string.rate_dialog_message
    var textPositiveResId = R.string.rate_dialog_ok
    var textNeutralResId = R.string.rate_dialog_cancel
    var textNegativeResId = R.string.rate_dialog_no
    private var titleText: String? = null
    private var messageText: String? = null
    private var positiveText: String? = null
    private var neutralText: String? = null
    private var negativeText: String? = null

    @JvmField
    var view: View? = null

    fun shouldShowNeutralButton(): Boolean {
        return showNeutralButton
    }

    fun setShowNeutralButton(showNeutralButton: Boolean) {
        this.showNeutralButton = showNeutralButton
    }

    fun shouldShowNegativeButton(): Boolean {
        return showNegativeButton
    }

    fun setShowNegativeButton(showNegativeButton: Boolean) {
        this.showNegativeButton = showNegativeButton
    }

    fun shouldShowTitle(): Boolean {
        return showTitle
    }

    fun setShowTitle(showTitle: Boolean) {
        this.showTitle = showTitle
    }

    fun getTitleText(context: Context): String {
        return titleText ?: context.getString(titleResId)
    }

    fun setTitleText(titleText: String?) {
        this.titleText = titleText
    }

    fun getMessageText(context: Context): String {
        return messageText ?: context.getString(messageResId)
    }

    fun setMessageText(messageText: String?) {
        this.messageText = messageText
    }

    fun getPositiveText(context: Context): String {
        return positiveText ?: context.getString(textPositiveResId)
    }

    fun setPositiveText(positiveText: String?) {
        this.positiveText = positiveText
    }

    fun getNeutralText(context: Context): String {
        return neutralText ?: context.getString(textNeutralResId)
    }

    fun setNeutralText(neutralText: String?) {
        this.neutralText = neutralText
    }

    fun getNegativeText(context: Context): String {
        return negativeText ?: context.getString(textNegativeResId)
    }

    fun setNegativeText(negativeText: String?) {
        this.negativeText = negativeText
    }
}
