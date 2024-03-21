/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.style

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import org.sqlunet.style.Spanner.Companion.appendWithSpans

/**
 * Report helper
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object Report {

    /**
     * Append header
     *
     * @param sb   spannable string builder
     * @param text text
     * @return spannable string builder
     */
    @JvmStatic
    fun appendHeader(sb: SpannableStringBuilder, text: CharSequence?): SpannableStringBuilder {
        return appendWithSpans(sb, text, StyleSpan(Typeface.BOLD))
    }

    /**
     * Append Image
     *
     * @param context context
     * @param sb      spannable string builder
     * @param resId   resource id
     */
    @JvmStatic
    fun appendImage(context: Context, sb: SpannableStringBuilder, @DrawableRes resId: Int) {
        appendWithSpans(sb, "\u0000", makeImageSpan(context, resId))
    }

    /**
     * Make image span
     *
     * @param context context
     * @param resId   res id
     * @return image span
     */
    private fun makeImageSpan(context: Context, @DrawableRes resId: Int): Any {
        val drawable = AppCompatResources.getDrawable(context, resId)!!
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        return ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BOTTOM)
    }
}
