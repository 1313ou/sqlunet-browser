/*
 * Copyright (c) 2026. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.sn.selector

import android.annotation.SuppressLint
import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.sqlunet.browser.CursorRecyclerViewAdapter
import org.sqlunet.browser.sn.R
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains

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
        val cursor = this.cursor
        requireNotNull(cursor) { "Cursor is null" }
        if (!cursor.moveToPosition(position)) {
            throw IllegalStateException("Can't move cursor to position $position")
        }

        bindTextView(holder.pos, cursor, Words_Senses_CasedWords_Synsets_Poses_Domains.POSID)
        bindTextView(holder.domain, cursor, Words_Senses_CasedWords_Synsets_Poses_Domains.DOMAIN)
        bindTextView(holder.definition, cursor, Words_Senses_CasedWords_Synsets_Poses_Domains.DEFINITION)
        bindTextView(holder.cased, cursor, Words_Senses_CasedWords_Synsets_Poses_Domains.CASEDWORD)
        bindTextView(holder.sensenum, cursor, Words_Senses_CasedWords_Synsets_Poses_Domains.SENSENUM)
        bindTextView(holder.sensekey, cursor, Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEKEY)
        bindTextView(holder.lexid, cursor, Words_Senses_CasedWords_Synsets_Poses_Domains.LEXID)
        val tagCountCol = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_Poses_Domains.TAGCOUNT)
        val tagCount = if(tagCountCol != -1) cursor.getInt(tagCountCol) else 0
        bindTextView(holder.tagcount, if (tagCount > 0) tagCount.toString() else null)
        bindTextView(holder.wordid, cursor, Words_Senses_CasedWords_Synsets_Poses_Domains.WORDID)
        bindTextView(holder.synsetid, cursor, Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID)
        bindTextView(holder.senseid, cursor, Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEID)

        holder.itemView.isActivated = position == activatedPosition
    }

    override fun getItemCount(): Int {
        return cursor?.count ?: 0
    }

    override fun getCursor(): Cursor? {
        return cursor
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun changeCursor(cursor: Cursor?) {
        val old = this.cursor
        if (old === cursor) {
            return
        }
        this.cursor = cursor
        if (this.cursor != null) {
            deactivate()
            notifyDataSetChanged()
        }
        old?.close()
    }
    
    fun deactivate() {
        val previouslyActivatedPosition = activatedPosition
        activatedPosition = RecyclerView.NO_POSITION
        if (previouslyActivatedPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(previouslyActivatedPosition)
        }
    }

    private fun bindTextView(textView: TextView, cursor: Cursor, columnName: String) {
        val text = cursor.getString(cursor.getColumnIndexOrThrow(columnName))
        bindTextView(textView, text)
    }
    
    private fun bindTextView(textView: TextView, text: String?) {
        if (text == null) {
            textView.visibility = View.GONE
        } else {
            textView.visibility = View.VISIBLE
            textView.text = text
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pos: TextView = itemView.findViewById(R.id.pos)
        val domain: TextView = itemView.findViewById(R.id.domain)
        val definition: TextView = itemView.findViewById(R.id.definition)
        val cased: TextView = itemView.findViewById(R.id.cased)
        val sensenum: TextView = itemView.findViewById(R.id.sensenum)
        val sensekey: TextView = itemView.findViewById(R.id.sensekey)
        val lexid: TextView = itemView.findViewById(R.id.lexid)
        val tagcount: TextView = itemView.findViewById(R.id.tagcount)
        val wordid: TextView = itemView.findViewById(R.id.wordid)
        val synsetid: TextView = itemView.findViewById(R.id.synsetid)
        val senseid: TextView = itemView.findViewById(R.id.senseid)
        
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
