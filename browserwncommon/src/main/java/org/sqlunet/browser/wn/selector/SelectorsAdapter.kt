/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.wn.selector

import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.sqlunet.browser.CursorRecyclerViewAdapter
import org.sqlunet.browser.wn.lib.R
import org.sqlunet.wordnet.provider.WordNetContract

/**
 * Selectors adapter
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SelectorsAdapter : RecyclerView.Adapter<SelectorsAdapter.ViewHolder>(), CursorRecyclerViewAdapter {

    /**
     * Cursor
     */
    private var cursor: Cursor? = null

    /**
     * OnClickListener
     */
    private var onClickListener: OnClickListener? = null

    // holder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_selector, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val cursor = this.cursor
        requireNotNull(cursor) { "Cursor is null" }
        if (!cursor.moveToPosition(position)) {
            throw IllegalStateException("Can't move cursor to position $position")
        }
        val posId = cursor.getString(cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.POSID))
        val domain = cursor.getString(cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.DOMAIN))
        val definition = cursor.getString(cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.DEFINITION))
        val casedWord = cursor.getString(cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.CASEDWORD))
        val pronunciations = cursor.getString(cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.PRONUNCIATIONS))
        val senseNum = cursor.getInt(cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SENSENUM))
        val senseKey = cursor.getString(cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SENSEKEY))
        val lexId = cursor.getInt(cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.LEXID))
        val tagCount = cursor.getInt(cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.TAGCOUNT))
        val wordId = cursor.getLong(cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.WORDID))
        val synsetId = cursor.getLong(cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SYNSETID))
        val senseId = cursor.getLong(cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SENSEID))

        bindTextView(viewHolder.pos, posId)
        bindTextView(viewHolder.domain, domain)
        bindTextView(viewHolder.definition, definition)
        bindTextView(viewHolder.cased, casedWord)
        bindTextView(viewHolder.pronunciation, pronunciations)
        bindTextView(viewHolder.sensekey, senseKey)
        viewHolder.sensenum.text = senseNum.toString()
        viewHolder.lexid.text = lexId.toString()
        viewHolder.tagcount.text = tagCount.toString()
        viewHolder.wordid.text = wordId.toString()
        viewHolder.synsetid.text = synsetId.toString()
        viewHolder.senseid.text = senseId.toString()
   }

    override fun getCursor(): Cursor? {
        return cursor
    }

    override fun getItemCount(): Int {
        return cursor?.count ?: 0
    }

    override fun changeCursor(cursor: Cursor?) {
        val old = this.cursor
        if (old === cursor) {
            return
        }
        this.cursor = cursor
        if (this.cursor != null) {
        notifyDataSetChanged()
    }
        old?.close()
    }

    // click


    /**
     * Set OnClickListener
     *
     * @param onClickListener onClickListener
     */
    fun setOnClickListener(onClickListener: OnClickListener?) {
        this.onClickListener = onClickListener
    }

    /**
     * OnClickListener interface
     */
    interface OnClickListener {

        /**
         * OnClick
         *
         * @param position position
         * @param view     view
         */
        fun onClick(position: Int, view: View)
    }

    // helper

    private fun bindTextView(textView: TextView, text: String?) {
        if (text.isNullOrEmpty()) {
            textView.visibility = View.GONE
        } else {
            textView.text = text
            textView.visibility = View.VISIBLE
        }
    }

    /**
     * ViewHolder
     *
     * @param itemView view
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {  onClickListener?.onClick(position, viewHolder.itemView) }
        }

        val pos: TextView = itemView.findViewById(R.id.pos)
        val domain: TextView = itemView.findViewById(R.id.domain)
        val definition: TextView = itemView.findViewById(R.id.definition)
        val cased: TextView = itemView.findViewById(R.id.cased)
        val pronunciation: TextView = itemView.findViewById(R.id.pronunciation)
        val sensenum: TextView = itemView.findViewById(R.id.sensenum)
        val sensekey: TextView = itemView.findViewById(R.id.sensekey)
        val lexid: TextView = itemView.findViewById(R.id.lexid)
        val tagcount: TextView = itemView.findViewById(R.id.tagcount)
        val wordid: TextView = itemView.findViewById(R.id.wordid)
        val synsetid: TextView = itemView.findViewById(R.id.synsetid)
        val senseid: TextView = itemView.findViewById(R.id.senseid)
    }
}
