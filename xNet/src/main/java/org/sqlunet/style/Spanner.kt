/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.style

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.text.style.ReplacementSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.ReturnThis
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
                when (spans) {
                    is Array<*> if spans.isArrayOf<Span>() -> {
                        for (span in spans) {
                            if (span != null) {
                                sb.setSpan(span, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                            }
                        }
                    }

                    is Collection<*> -> {
                        for (span2 in spans) {
                            sb.setSpan(span2, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    }

                    else -> {
                        sb.setSpan(spans, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
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
         * @receiver spannable string builder
         * @param spans image span with possible image style span
         */
        @ReturnThis
        private fun SpannableStringBuilder.appendImageSpans(vararg spans: Span): SpannableStringBuilder {
            val from = length
            append(COLLAPSEDCHAR)
            val to = length
            for (span in spans) {
                setSpan(span, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            return this
        }

        /**
         * Append image
         *
         * @receiver spannable string builder
         * @param drawable drawable to use
         */
        @ReturnThis
        fun SpannableStringBuilder.appendImage(drawable: Drawable): SpannableStringBuilder {
            val span: Span = ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BASELINE)
            appendImageSpans(span)
            return this
        }

        // C L I C K A B L E

        // how to use:
        // val textView: TextView = ...
        // prepareTextViewForClickableSpan(tv)
        // textView.text= SpannableStringBuilder()
        //  .appendClickableText("text", callback)

        /**
         * Append clickable text
         *
         * @receiver       spannable string builder
         * @param text     text
         * @param listener click listener
         */
        @Suppress("unused")
        @ReturnThis
        fun SpannableStringBuilder.appendClickableText(text: CharSequence, listener: () -> Unit): SpannableStringBuilder {
            val span: ClickableSpan = object : ClickableSpan() {

                override fun onClick(view: View) {
                    Log.d(TAG, "Click text $text")
                    listener()
                }

                override fun updateDrawState(drawState: TextPaint) {
                    super.updateDrawState(drawState)
                    drawState.isUnderlineText = true // Optional: underline the text
                    drawState.color = Color.RED // Optional: change text color
                }
            }
            appendWithSpans(text, span)
            return this
        }

        @Suppress("unused")
        fun prepareTextViewForClickableSpan(tv: TextView) {
            tv.movementMethod = LinkMovementMethod.getInstance()
            tv.isFocusable = true
            tv.isFocusableInTouchMode = true
            tv.isClickable = true
            tv.isLongClickable = true
            tv.isLongClickable = true
            tv.setOnClickListener {}
        }

        /**
         * Append clickable image
         *
         * @receiver       spannable string builder
         * @param caption  caption
         * @param listener click listener
         * @param context  context
         */
        @ReturnThis
        fun SpannableStringBuilder.appendClickableImage(caption: CharSequence, listener: (sb: SpannableStringBuilder?, position: Int, collapsed: Boolean) -> Unit, context: Context): SpannableStringBuilder {
            val collapsedDrawable = getDrawable(context, R.drawable.ic_collapsed)
            val expandedDrawable = getDrawable(context, R.drawable.ic_expanded)
            appendClickableImage(collapsedDrawable, expandedDrawable, caption, listener)
            return this
        }

        /**
         * Append clickable image
         *
         * @receiver                spannable string builder
         * @param collapsedDrawable collapse drawable
         * @param expandedDrawable  expandContainer drawable
         * @param caption           caption
         * @param listener          click listener
         */
        @ReturnThis
        private fun SpannableStringBuilder.appendClickableImage(collapsedDrawable: Drawable, expandedDrawable: Drawable, caption: CharSequence, listener: (sb: SpannableStringBuilder?, position: Int, collapsed: Boolean) -> Unit): SpannableStringBuilder {
            val span = ImageSpan(collapsedDrawable, DynamicDrawableSpan.ALIGN_BASELINE)
            val span2: ClickableSpan = object : ClickableSpan() {
                @Synchronized
                override fun onClick(view: View) {
                    // Log.d(TAG, "Click image")
                    val textView = view as TextView
                    val sb1 = textView.text as SpannableStringBuilder
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
                            listener(sb1, to + caption.length + 2, collapsed)
                        }
                    }
                    textView.text = sb1
                }
            }
            appendImageSpans(span, span2)
            append(' ').append(caption).append('\n')
            return this
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
            sb.delete(position, sb.find(position, EOEXPANDEDSTRING))
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
                        sb.applySpan(from, to, span)
                    }
                }
            }
            return sb
        }

        /**
         * Append text
         *
         * @receiver    spannable string builder
         * @param text  text
         * @param spans spans to apply
         */
        @ReturnThis
        fun SpannableStringBuilder.appendWithSpans(text: CharSequence?, vararg spans: Span): SpannableStringBuilder {
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
         * @receiver    spannable string builder
         * @param from  from position
         * @param to    to position
         * @param spans spans to apply
         */
        @ReturnThis
        fun SpannableStringBuilder.applySpans(from: Int, to: Int, vararg spans: Span): SpannableStringBuilder {
            for (span in spans) {
                applySpan(from, to, span)
            }
            return this
        }

        /**
         * Apply span
         *
         * receiver   spannable string builder
         * @param from from position
         * @param to   to position
         * @param span span to apply
         */
        @ReturnThis
        private fun SpannableStringBuilder.applySpan(from: Int, to: Int, span: Span): SpannableStringBuilder {
            when (span) {
                is Array<*> if span.isArrayOf<Span>() -> {
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

        // H E L P E R S

        /**
         * Find delimiter
         *
         * @receiver        spannable string builder
         * @param start     search start
         * @param delimiter delimiter
         * @return delimiter position or -1 if not found
         */
        private fun SpannableStringBuilder.find(start: Int, @Suppress("SameParameterValue") delimiter: Char): Int {
            var i = start
            while (i < length) {
                if (this[i] == delimiter) {
                    return i + 1
                }
                i++
            }
            return -1
        }

        /**
         * Get drawable from resource id
         *
         * @param activityContext context
         * @param resId resource id
         * @return drawable
         */
        fun getDrawable(activityContext: Context, @DrawableRes resId: Int): Drawable {
            val drawable = AppCompatResources.getDrawable(activityContext, resId)!!
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            return drawable
        }
    }
}
