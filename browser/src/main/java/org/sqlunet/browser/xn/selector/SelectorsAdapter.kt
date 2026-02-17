/*
 * Copyright (c) 2026. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.xn.selector

import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.sqlunet.browser.CursorRecyclerViewAdapter
import org.sqlunet.browser.R
import org.sqlunet.provider.XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords
import org.sqlunet.speak.Pronunciation.Companion.sortedPronunciations

class SelectorsAdapter(val activate: (position: Int) -> Unit) : RecyclerView.Adapter<SelectorsAdapter.ViewHolder>(), CursorRecyclerViewAdapter {

    private var cursor: Cursor? = null
    private var activatedPosition = RecyclerView.NO_POSITION

    private val onClickListener = { position: Int ->
        Log.d(TAG, "Activate position $position")
        val previouslyActivatedPosition = activatedPosition
        activatedPosition = position
        if (previouslyActivatedPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(previouslyActivatedPosition)
        }
        notifyItemChanged(activatedPosition)
        activate(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_selector, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cursor?.let {
            if (!it.moveToPosition(position)) {
                throw IllegalStateException("Can't move cursor to position $position")
            }

            // Bind data
            bindTextView(holder.pos, it, Words_Pronunciations_FnWords_PbWords_VnWords.POS, false)
            bindTextView(holder.sensenum, it, Words_Pronunciations_FnWords_PbWords_VnWords.SENSENUM, false)
            bindTextView(holder.domain, it, Words_Pronunciations_FnWords_PbWords_VnWords.DOMAIN, false)
            bindTextView(holder.definition, it, Words_Pronunciations_FnWords_PbWords_VnWords.DEFINITION, false)
            bindTextView(holder.cased, it, Words_Pronunciations_FnWords_PbWords_VnWords.CASED, false)

            // Pronunciation has special handling
            val pronunColIdx = it.getColumnIndex(Words_Pronunciations_FnWords_PbWords_VnWords.PRONUNCIATIONS)
            var pronunText: String? = if (pronunColIdx != -1) it.getString(pronunColIdx) else null
            if (pronunText != null) {
                pronunText = sortedPronunciations(pronunText)
            }
            if (pronunText.isNullOrEmpty()) {
                holder.pronunciation.visibility = View.GONE
            } else {
                holder.pronunciation.visibility = View.VISIBLE
                holder.pronunciation.text = pronunText
            }

            val tagCountColIdx = it.getColumnIndex(Words_Pronunciations_FnWords_PbWords_VnWords.TAGCOUNT)
            val tagCount = if (tagCountColIdx != -1) it.getInt(tagCountColIdx) else 0
            bindTextView(holder.tagcount, if (tagCount > 0) tagCount.toString() else null, false)
            bindTextView(holder.lexid, it, Words_Pronunciations_FnWords_PbWords_VnWords.LUID, false)
            bindTextView(holder.sensekey, it, Words_Pronunciations_FnWords_PbWords_VnWords.SENSEKEY, false)
            bindTextView(holder.wordid, it, Words_Pronunciations_FnWords_PbWords_VnWords.WORDID, true)
            bindTextView(holder.synsetid, it, Words_Pronunciations_FnWords_PbWords_VnWords.SYNSETID, true)
            bindTextView(holder.senseid, it, Words_Pronunciations_FnWords_PbWords_VnWords.SENSEID, true)
            bindTextView(holder.vnwordid, it, Words_Pronunciations_FnWords_PbWords_VnWords.VNWORDID, true)
            bindTextView(holder.pbwordid, it, Words_Pronunciations_FnWords_PbWords_VnWords.PBWORDID, true)
            bindTextView(holder.fnwordid, it, Words_Pronunciations_FnWords_PbWords_VnWords.FNWORDID, true)

            holder.itemView.isActivated = position == activatedPosition
        }
    }

    private fun bindTextView(textView: TextView, cursor: Cursor, columnName: String, isId: Boolean) {
        val columnIndex = cursor.getColumnIndex(columnName)
        val text = if (columnIndex != -1) cursor.getString(columnIndex) else null
        bindTextView(textView, text, isId)
    }

    private fun bindTextView(textView: TextView, text: String?, isId: Boolean) {
        if (text.isNullOrEmpty() || (isId && text == "0")) {
            textView.visibility = View.GONE
        } else {
            textView.visibility = View.VISIBLE
            textView.text = text
        }
    }

    override fun getItemCount(): Int {
        return cursor?.count ?: 0
    }

    override fun getCursor(): Cursor? {
        return cursor
    }

    override fun changeCursor(cursor: Cursor?) {
        val old = this.cursor
        if (old === cursor) {
            return
        }
        this.cursor = cursor
        if (this.cursor != null) {
            activatedPosition = RecyclerView.NO_POSITION
            notifyDataSetChanged()
        }
        old?.close()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pos: TextView = itemView.findViewById(R.id.pos)
        val sensenum: TextView = itemView.findViewById(R.id.sensenum)
        val domain: TextView = itemView.findViewById(R.id.domain)
        val definition: TextView = itemView.findViewById(R.id.definition)
        val cased: TextView = itemView.findViewById(R.id.cased)
        val pronunciation: TextView = itemView.findViewById(R.id.pronunciation)
        val tagcount: TextView = itemView.findViewById(R.id.tagcount)
        val lexid: TextView = itemView.findViewById(R.id.lexid)
        val sensekey: TextView = itemView.findViewById(R.id.sensekey)
        val wordid: TextView = itemView.findViewById(R.id.wordid)
        val synsetid: TextView = itemView.findViewById(R.id.synsetid)
        val senseid: TextView = itemView.findViewById(R.id.senseid)
        val vnwordid: TextView = itemView.findViewById(R.id.vnwordid)
        val pbwordid: TextView = itemView.findViewById(R.id.pbwordid)
        val fnwordid: TextView = itemView.findViewById(R.id.fnwordid)

        init {
            itemView.setOnClickListener {
                onClickListener.invoke(bindingAdapterPosition)
            }
        }
    }

    companion object {
        private const val TAG = "SelectorsA"
    }
}
