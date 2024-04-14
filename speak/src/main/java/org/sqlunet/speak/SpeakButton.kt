/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.speak

import android.content.Context
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.DynamicDrawableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

object SpeakButton {

    /**
     * Collapsed marker
     */
    private const val COLLAPSED_CHAR = '@'

    // I M A G E

    /**
     * Append spans
     *
     * @param sb         spannable string builder
     * @param imageSpan  image span
     * @param clickSpan  click span
     * @param extraSpans possible image style span
     */
    private fun appendImageSpans(sb: SpannableStringBuilder, caption: CharSequence?, imageSpan: Any, clickSpan: Any, vararg extraSpans: Any) {
        val from = sb.length
        sb.append(COLLAPSED_CHAR)
        var to = sb.length
        sb.setSpan(imageSpan, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        if (!caption.isNullOrEmpty()) {
            sb.append(' ')
            sb.append(caption)
        }
        to = sb.length
        sb.setSpan(clickSpan, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        for (span in extraSpans) {
            sb.setSpan(span, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    // C L I C K A B L E

    /**
     * Append clickable image
     *
     * @param sb          spannable string builder
     * @param drawableRes drawable res
     * @param caption     caption
     * @param listener    click listener
     */
    fun appendClickableImage(sb: SpannableStringBuilder, @DrawableRes drawableRes: Int, caption: CharSequence, listener: Runnable, context: Context) {
        val span: ImageSpan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ImageSpan(context, drawableRes, DynamicDrawableSpan.ALIGN_BOTTOM)
        } else {
            ImageSpan(context, drawableRes)
        }
        val span2: ClickableSpan = object : ClickableSpan() {
            @Synchronized
            override fun onClick(view: View) {
                // Log.d(TAG, "Click")
                listener.run()
            }
        }
        appendImageSpans(sb, caption, span, span2, ForegroundColorSpan(fetchColor(context, R.attr.colorHighlight2OnBackground)))
    }

    @ColorInt
    private fun fetchColor(context: Context, @AttrRes attr: Int): Int {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }
}
