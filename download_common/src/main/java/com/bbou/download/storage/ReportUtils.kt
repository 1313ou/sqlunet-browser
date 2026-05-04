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
import androidx.annotation.ReturnThis
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
     * @receiver sb spannable string builder
     * @param text text
     */
    @ReturnThis
    fun SpannableStringBuilder.appendHeader(text: CharSequence?): SpannableStringBuilder {
        append(text, StyleSpan(Typeface.BOLD))
        return this
    }

    /**
     * Append Image
     *
     * @receiver spannable string builder
     * @param context context
     * @param resId   resource id
     */
    @ReturnThis
    fun SpannableStringBuilder.appendImage(context: Context, resId: Int): SpannableStringBuilder {
        append("\u0000", makeImageSpan(context, resId))
        return this
    }

    // S P A N S

    /**
     * Append text
     *
     * @receiver spannable string builder
     * @param text  text
     * @param spans spans to apply
     */
    @ReturnThis
    private fun SpannableStringBuilder.append(text: CharSequence?, vararg spans: Any): SpannableStringBuilder {
        if (!text.isNullOrEmpty()) {
            val from = length
            append(text)
            val to = length
            for (span in spans) {
                setSpan(span, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return this
    }

    /**
     * Append text
     *
     * @receiver spannable string builder
     * @param text  text
     * @param spans spans to apply
     */
    @ReturnThis
    fun SpannableStringBuilder.appendWithSpans(text: CharSequence?, vararg spans: Any): SpannableStringBuilder {
        if (!text.isNullOrEmpty()) {
            val from = length
            append(text)
            val to = length
            applySpans(from, to, *spans)
        }
        return this
    }

    /**
     * Apply spans
     *
     * @receiver spannable string builder
     * @param sb    spannable string builder
     * @param from  from position
     * @param to    to position
     * @param spans spans to apply
     */
    @ReturnThis
    private fun SpannableStringBuilder.applySpans(from: Int, to: Int, vararg spans: Any): SpannableStringBuilder {
        for (span in spans) {
            applySpan(from, to, span)
        }
        return this
    }

    /**
     * Apply span
     *
     * @param sb   spannable string builder
     * @param from from position
     * @param to   to position
     * @param span span to apply
     */
    @ReturnThis
    private fun SpannableStringBuilder.applySpan(from: Int, to: Int, span: Any): SpannableStringBuilder {
        when (span) {
            is Array<*> if span.isArrayOf<Any>() -> {
                for (span2 in span) {
                    setSpan(span2, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }

            is Collection<*> -> {
                for (span2 in span) {
                    setSpan(span2, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }

            else -> {
                setSpan(span, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return this
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
        // increment i if colors added
    }
}
