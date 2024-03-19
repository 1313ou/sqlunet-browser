/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download.storage

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.CharacterStyle
import android.text.style.DynamicDrawableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.bbou.download.common.R
import java.util.Collections

/**
 * Report helpers
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object ReportUtils {

    /**
     * Append header
     *
     * @param sb   spannable string builder
     * @param text text
     * @return spannable string builder
     */
    fun appendHeader(sb: SpannableStringBuilder, text: CharSequence?): SpannableStringBuilder {
        return append(sb, text, StyleSpan(Typeface.BOLD))
    }

    /**
     * Append Image
     *
     * @param context context
     * @param sb      spannable string builder
     * @param resId   resource id
     */
    fun appendImage(context: Context, sb: SpannableStringBuilder, resId: Int) {
        append(sb, "\u0000", makeImageSpan(context, resId))
    }

    // S P A N S

    /**
     * Append text
     *
     * @param sb    spannable string builder
     * @param text  text
     * @param spans spans to apply
     */
    private fun append(sb: SpannableStringBuilder, text: CharSequence?, vararg spans: Any): SpannableStringBuilder {
        if (!text.isNullOrEmpty()) {
            val from = sb.length
            sb.append(text)
            val to = sb.length
            for (span in spans) {
                sb.setSpan(span, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return sb
    }

    /**
     * Append text
     *
     * @param sb    spannable string builder
     * @param text  text
     * @param spans spans to apply
     */
    fun appendWithSpans(sb: SpannableStringBuilder, text: CharSequence?, vararg spans: Any): SpannableStringBuilder {
        if (!text.isNullOrEmpty()) {
            val from = sb.length
            sb.append(text)
            val to = sb.length
            applySpans(sb, from, to, *spans)
        }
        return sb
    }

    /**
     * Apply spans
     *
     * @param sb    spannable string builder
     * @param from  from position
     * @param to    to position
     * @param spans spans to apply
     */
    private fun applySpans(sb: SpannableStringBuilder, from: Int, to: Int, vararg spans: Any) {
        for (span in spans) {
            applySpan(sb, from, to, span)
        }
    }

    /**
     * Apply span
     *
     * @param sb   spannable string builder
     * @param from from position
     * @param to   to position
     * @param span span to apply
     */
    private fun applySpan(sb: SpannableStringBuilder, from: Int, to: Int, span: Any) {
        if (span is Array<*> && span.isArrayOf<Any>()) {
            for (span2 in span) {
                sb.setSpan(span2, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        } else if (span is Collection<*>) {
            for (span2 in span) {
                sb.setSpan(span2, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        } else {
            sb.setSpan(span, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    /**
     * Build spans
     *
     * @param bg         background color (il TRANSPARENT skipped)
     * @param fg         foreground color (il TRANSPARENT skipped)
     * @param otherSpans other spans
     * @return spans
     */
    fun spans(@ColorInt bg: Int, @ColorInt fg: Int, vararg otherSpans: CharacterStyle): Any {

        if (bg == Color.TRANSPARENT && fg == Color.TRANSPARENT) {
            return otherSpans
        }
        val spans: MutableList<CharacterStyle> = java.util.ArrayList()
        if (bg != Color.TRANSPARENT) {
            spans.add(BackgroundColorSpan(bg))
        }
        if (fg != Color.TRANSPARENT) {
            spans.add(ForegroundColorSpan(fg))
        }
        Collections.addAll(spans, *otherSpans)
        return spans
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

    /**
     * Enlargement factor
     */
    const val ENLARGE = 1.25F

    // C O L O R S

    /**
     * Back color for storage type
     */
    var storageTypeBackColor = Color.TRANSPARENT

    /**
     * Fore color for storage type
     */
    var storageTypeForeColor = Color.TRANSPARENT

    /**
     * Back color for storage value
     */
    var storageValueBackColor = Color.TRANSPARENT

    /**
     * Fore color for storage value
     */
    var storageValueForeColor = Color.TRANSPARENT

    /**
     * Back color for dir type
     */
    var dirTypeBackColor = Color.TRANSPARENT

    /**
     * Fore color for dir type
     */
    var dirTypeForeColor = Color.TRANSPARENT

    /**
     * Back color for dir value
     */
    var dirValueBackColor = Color.TRANSPARENT

    /**
     * Fore color for dir value
     */
    var dirValueForeColor = Color.TRANSPARENT

    /**
     * Back color for dir ok status
     */
    var dirOkBackColor = Color.TRANSPARENT

    /**
     * Fore color for ok status
     */
    var dirOkForeColor = Color.TRANSPARENT

    /**
     * Back color for fail status
     */
    var dirFailBackColor = Color.TRANSPARENT

    /**
     * Fore color for fail status
     */
    var dirFailForeColor = Color.TRANSPARENT

    /**
     * Set colors from palette resource
     *
     * @param context context
     */
    fun setColorsFromResources(context: Context) {
        // do not reorder : dependent on resource array order
        val palette = context.resources.getIntArray(R.array.palette_storage)
        var i = 0

        storageTypeBackColor = palette[i++]
        storageTypeForeColor = palette[i++]
        storageValueBackColor = palette[i++]
        storageValueForeColor = palette[i++]
        dirTypeBackColor = palette[i++]
        dirTypeForeColor = palette[i++]
        dirValueBackColor = palette[i++]
        dirValueForeColor = palette[i++]
        dirOkBackColor = palette[i++]
        dirOkForeColor = palette[i++]
        dirFailBackColor = palette[i++]
        dirFailForeColor = palette[i]
    }
}
