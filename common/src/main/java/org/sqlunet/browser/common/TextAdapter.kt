/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.common

import android.content.Context
import android.database.Cursor
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.sqlunet.style.RegExprSpanner

class TextAdapter(
    context: Context,
    private val query: String,
    private var cursor: Cursor?,
    private val listener: (Cursor, Int, Long) -> Unit
) : RecyclerView.Adapter<TextAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private val spanner: RegExprSpanner

    init {
        val patterns = toPatterns(query)
        spanner = RegExprSpanner(patterns, org.sqlunet.style.Factories.boldFactory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_text, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (cursor!!.moveToPosition(position)) {
            val textId = cursor!!.getColumnIndex("synsetid")
            val text = cursor!!.getString(1)
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

    fun swapCursor(newCursor: Cursor?) {
        if (cursor === newCursor) {
            return
        }
        this.cursor = newCursor
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(android.R.id.text1)
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
}
