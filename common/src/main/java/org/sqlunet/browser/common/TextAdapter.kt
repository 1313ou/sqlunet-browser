/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.common

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import org.sqlunet.style.RegExprSpanner

class TextAdapter(
    context: Context,
    query: String,
    private var cursor: Cursor?,
    private val listener: (Cursor, Int, Long) -> Unit,
    private val layoutId: Int?
) : RecyclerView.Adapter<TextAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private val spanner: RegExprSpanner

    init {
        val patterns = toPatterns(query)
        spanner = RegExprSpanner(patterns, org.sqlunet.style.Factories.boldFactory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(layoutId ?: R.layout.item_text, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (cursor!!.moveToPosition(position)) {
            // val idSynsetId = cursor!!.getColumnIndex("synsetid")
            // val synsetId = cursor!!.getString(idSynsetId)
            val idTextId = 1 // cursor!!.getColumnIndex("text")
            val text = cursor!!.getString(idTextId)
            val sb = SpannableStringBuilder(text)
            spanner.setSpan(sb, 0, 0)
            holder.textView.text = sb
            holder.itemView.setOnClickListener { listener(cursor!!, position, getItemId(position)) }
        }
    }

    override fun getItemCount(): Int {
        return cursor?.count ?: 0
    }

    override fun getItemId(position: Int): Long {
        return if (cursor!!.moveToPosition(position)) cursor!!.getLong(1) else 0
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeCursor(newCursor: Cursor?) {
        if (cursor === newCursor) {
            return
        }
        this.cursor = newCursor
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textView: TextView = itemView as TextView // itemView.findViewById(R.id.item0)
    }

    private fun toPatterns(query: String): Array<String> {
        val tokens = query.split("[\\s()]+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val patterns: MutableList<String> = ArrayList()
        for (token in tokens) {
            var token2 = token.trim { it <= ' ' }
            token2 = token2.replace("\\*$".toRegex(), "")
            if (token2.isEmpty() || "AND" == token2 || "OR" == token2 || "NOT" == token2 || token2.startsWith("NEAR")) {
                continue
            }
            patterns.add("((?i)$token2)")
        }
        return patterns.toTypedArray<String>()
    }

    companion object {

        /**
         * Append text
         *
         * @param sb    spannable string builder
         * @param text  text
         * @param spans spans to apply
         */
        fun append(sb: SpannableStringBuilder, text: CharSequence?, vararg spans: Any) {
            if (text.isNullOrEmpty()) {
                return
            }
            val from = sb.length
            sb.append(text)
            val to = sb.length
            for (span in spans) {
                sb.setSpan(span, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
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
}
