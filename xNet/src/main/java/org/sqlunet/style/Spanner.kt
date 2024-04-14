/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.style

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.text.style.ReplacementSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import org.sqlunet.xnet.R

typealias Span = Any

/**
 * Spanner
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class Spanner {

    // I N T E R F A C E S

    /**
     * Span factory
     *
     * @author [Bernard Bou](mailto:1313ou@gmail.com)
     */
    fun interface SpanFactory {

        fun make(flags: Long): Span?
    }

    /**
     * Click image interface
     *
     * @author [Bernard Bou](mailto:1313ou@gmail.com)
     */
    fun interface OnClickImage {

        fun onClickImage(sb: SpannableStringBuilder?, position: Int, collapsed: Boolean)
    }

    // H I D D E N S P A N

    /**
     * Hidden span
     *
     * @author [Bernard Bou](mailto:1313ou@gmail.com)
     */
    class HiddenSpan : ReplacementSpan() {

        override fun draw(canvas: Canvas, arg1: CharSequence, arg2: Int, arg3: Int, arg4: Float, arg5: Int, arg6: Int, arg7: Int, arg8: Paint) {
        }

        override fun getSize(paint: Paint, text: CharSequence, from: Int, to: Int, fm: FontMetricsInt?): Int {
            return 0
        }
    }

    companion object {

        private const val TAG = "Spanner"

        /**
         * Collapsed marker
         */
        private const val COLLAPSEDCHAR = '@'

        /**
         * Collapsed marker
         */
        private const val COLLAPSEDSTRING = COLLAPSEDCHAR.toString()

        /**
         * Expanded marker
         */
        private const val EXPANDEDSTRING = "~"

        /**
         * End of expanded string marker
         */
        private const val EOEXPANDEDSTRING = '~'

        // A P P L Y  S P A N

        /**
         * Apply spans
         *
         * @param sb    spannable string builder
         * @param from  start
         * @param to    finish
         * @param spans spans to apply
         */
        fun setSpan(sb: SpannableStringBuilder, from: Int, to: Int, spans: Span?) {
            if (spans != null && to - from > 0) {
                if (spans is Array<*> && spans.isArrayOf<Span>()) {
                    for (span in spans) {
                        if (span != null) {
                            sb.setSpan(span, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    }
                } else if (spans is Collection<*>) {
                    for (span2 in spans) {
                        sb.setSpan(span2, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                } else {
                    sb.setSpan(spans, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }

        /**
         * Apply spans
         *
         * @param sb        spannable string builder
         * @param from      start
         * @param to        finish
         * @param flags     flags
         * @param factories span factories to call to get spans
         */
        fun setSpan(sb: SpannableStringBuilder, from: Int, to: Int, flags: Long, vararg factories: SpanFactory) {
            if (to - from > 0) {
                for (spanFactory in factories) {
                    val spans = spanFactory.make(flags)
                    setSpan(sb, from, to, spans)
                }
            }
        }

        // I M A G E

        /**
         * Append spans
         *
         * @param sb    spannable string builder
         * @param spans image span with possible image style span
         */
        private fun appendImageSpans(sb: SpannableStringBuilder, vararg spans: Span) {
            val from = sb.length
            sb.append(COLLAPSEDCHAR)
            val to = sb.length
            for (span in spans) {
                sb.setSpan(span, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        /**
         * Append image
         *
         * @param sb       spannable string builder
         * @param drawable drawable to use
         */
        fun appendImage(sb: SpannableStringBuilder, drawable: Drawable) {
            val span: Span = ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BASELINE)
            appendImageSpans(sb, span)
        }

        // C L I C K A B L E

        /**
         * Append clickable image
         *
         * @param sb       spannable string builder
         * @param caption  caption
         * @param listener click listener
         * @param context  context
         */
        fun appendClickableImage(sb: SpannableStringBuilder, caption: CharSequence, listener: OnClickImage, context: Context) {
            val collapsedDrawable = getDrawable(context, R.drawable.ic_collapsed)
            val expandedDrawable = getDrawable(context, R.drawable.ic_expanded)
            appendClickableImage(sb, collapsedDrawable, expandedDrawable, caption, listener)
        }

        /**
         * Append clickable image
         *
         * @param sb                spannable string builder
         * @param collapsedDrawable collapse drawable
         * @param expandedDrawable  expandContainer drawable
         * @param caption           caption
         * @param listener          click listener
         */
        private fun appendClickableImage(sb: SpannableStringBuilder, collapsedDrawable: Drawable, expandedDrawable: Drawable, caption: CharSequence, listener: OnClickImage) {
            val span = ImageSpan(collapsedDrawable, DynamicDrawableSpan.ALIGN_BASELINE)
            val span2: ClickableSpan = object : ClickableSpan() {
                @Synchronized
                override fun onClick(view: View) {
                    // Log.d(TAG, "Click image")
                    val textView = view as TextView
                    val sb1 = textView.getText() as SpannableStringBuilder
                    val clickableStart = textView.selectionStart
                    val clickableEnd = textView.selectionEnd
                    val spans: Array<out ImageSpan>? = sb1.getSpans(clickableStart, clickableEnd, ImageSpan::class.java)
                    if (spans != null) {
                        for (span3 in spans) {
                            // get image span
                            val from = sb1.getSpanStart(span3)
                            val to = sb1.getSpanEnd(span3)

                            // remove image span
                            sb1.removeSpan(span3)

                            // text
                            val c = sb1[from]
                            val collapsed = c == COLLAPSEDCHAR
                            sb1.replace(from, to, if (collapsed) EXPANDEDSTRING else COLLAPSEDSTRING)

                            // set new image span
                            val newImageSpan: Span = ImageSpan(if (collapsed) expandedDrawable else collapsedDrawable, DynamicDrawableSpan.ALIGN_BASELINE)
                            sb1.setSpan(newImageSpan, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                            // fire click
                            Log.d(TAG, "$from->$to")
                            listener.onClickImage(sb1, to + caption.length + 2, collapsed)
                        }
                    }
                    textView.text = sb1
                }
            }
            appendImageSpans(sb, span, span2)
            sb.append(' ').append(caption).append('\n')
        }

        /**
         * Insert tag
         *
         * @param sb       spannable string builder
         * @param position insert position
         * @param tag      tag
         */
        fun insertTag(sb: SpannableStringBuilder, position: Int, tag: CharSequence) {
            val insert = "$tag\n$EOEXPANDEDSTRING"
            sb.insert(position, insert)
            val mark = position + tag.length + 1
            sb.setSpan(HiddenSpan(), mark, mark + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        /**
         * Collapse
         *
         * @param sb       spannable string builder
         * @param position position to start from (to end-of-expanded string)
         */
        fun collapse(sb: SpannableStringBuilder, position: Int) {
            sb.delete(position, find(sb, position, EOEXPANDEDSTRING))
        }

        // T E X T

        /**
         * Append text
         *
         * @param sb        spannable string builder
         * @param text      text
         * @param flags     flags
         * @param factories span factories
         * @return input spannable string builder
         */
        fun append(sb: SpannableStringBuilder, text: CharSequence?, flags: Long, vararg factories: SpanFactory?): Appendable {
            if (!text.isNullOrEmpty()) {
                val from = sb.length
                sb.append(text)
                val to = sb.length
                for (spanFactory in factories) {
                    val span = spanFactory!!.make(flags)
                    if (span != null) {
                        applySpan(sb, from, to, span)
                    }
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
        fun appendWithSpans(sb: SpannableStringBuilder, text: CharSequence?, vararg spans: Span): SpannableStringBuilder {
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
        fun applySpans(sb: SpannableStringBuilder, from: Int, to: Int, vararg spans: Span) {
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
        private fun applySpan(sb: SpannableStringBuilder, from: Int, to: Int, span: Span) {
            if (span is Array<*> && span.isArrayOf<Span>()) {
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

        // H E L P E R S

        /**
         * Find delimiter
         *
         * @param sb        spannable string builder
         * @param start     search start
         * @param delimiter delimiter
         * @return delimiter position or -1 if not found
         */
        private fun find(sb: SpannableStringBuilder, start: Int, delimiter: Char): Int {
            var i = start
            while (i < sb.length) {
                if (sb[i] == delimiter) {
                    return i + 1
                }
                i++
            }
            return -1
        }

        /**
         * Get drawable from resource id
         *
         * @param context context
         * @param resId   resource id
         * @return drawable
         */
        fun getDrawable(context: Context, @DrawableRes resId: Int): Drawable {
            val drawable = AppCompatResources.getDrawable(context, resId)!!
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            return drawable
        }
    }
}
