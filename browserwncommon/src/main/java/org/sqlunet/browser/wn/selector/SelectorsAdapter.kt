/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.wn.selector

import android.annotation.SuppressLint
import android.database.Cursor
import android.util.Log
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
class SelectorsAdapter(val activate: (position: Int) -> Unit) : RecyclerView.Adapter<SelectorsAdapter.ViewHolder>(), CursorRecyclerViewAdapter {

    /**
     * Cursor
     */
    private var cursor: Cursor? = null

    /**
     * Tracks activated item position
     */
    private var activatedPosition = RecyclerView.NO_POSITION

    /**
     * OnClickListener
     */
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

    // holder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_selector, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        cursor?.let {
            if (!it.moveToPosition(position)) {
                throw IllegalStateException("Can't move cursor to position $position")
            }
            val idPosId = it.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.POSID)
            val idDomain = it.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.DOMAIN)
            val idDefinition = it.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.DEFINITION)
            val idCasedWord = it.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.CASEDWORD)
            val idPronunciations = it.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.PRONUNCIATIONS)
            val idSenseNum = it.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SENSENUM)
            val idSenseKey = it.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SENSEKEY)
            val idLexId = it.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.LEXID)
            val idTagCount = it.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.TAGCOUNT)
            val idWordId = it.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.WORDID)
            val idSynsetId = it.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SYNSETID)
            val idSenseId = it.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SENSEID)

            val posId = it.getString(idPosId)
            val domain = it.getString(idDomain)
            val definition = it.getString(idDefinition)
            val casedWord = it.getString(idCasedWord)
            val pronunciations = it.getString(idPronunciations)
            val senseNum = it.getInt(idSenseNum)
            val senseKey = it.getString(idSenseKey)
            val lexId = it.getInt(idLexId)
            val tagCount = it.getInt(idTagCount)
            val wordId = it.getLong(idWordId)
            val synsetId = it.getLong(idSynsetId)
            val senseId = it.getLong(idSenseId)

            viewHolder.pos.text = posId
            viewHolder.domain.text = domain
            viewHolder.definition.text = definition
            bindTextView(viewHolder.cased, casedWord)
            bindTextView(viewHolder.pronunciation, pronunciations)
            bindTextView(viewHolder.tagcount, if (tagCount <= 0) null else tagCount.toString())
            viewHolder.sensekey.text = senseKey
            viewHolder.sensenum.text = senseNum.toString()
            viewHolder.lexid.text = lexId.toString()
            viewHolder.wordid.text = wordId.toString()
            viewHolder.synsetid.text = synsetId.toString()
            viewHolder.senseid.text = senseId.toString()

            viewHolder.itemView.isActivated = position == activatedPosition
        }
    }

    override fun getCursor(): Cursor? {
        return cursor
    }

    override fun getItemCount(): Int {
        return cursor?.count ?: 0
    }

    @SuppressLint("NotifyDataSetChanged")
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

    // helper

    private fun bindTextView(textView: TextView, text: String?) {
        if (text.isNullOrEmpty()) {
            textView.visibility = View.GONE
        } else {
            textView.visibility = View.VISIBLE
            textView.text = text
        }
    }

    /**
     * ViewHolder
     *
     * @param itemView view
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

        init {
            // Log.d(TAG, "ItemView $itemView")
            itemView.setOnClickListener {
                // Log.d(TAG, "Click position=$position, adapterPosition=$adapterPosition, bindingPosition=$bindingAdapterPosition, layoutPosition=$layoutPosition, absoluteAdapterPosition=$absoluteAdapterPosition")
                onClickListener.invoke(bindingAdapterPosition)
            }
        }
    }

    companion object {

        private const val TAG = "SelectorsA"
    }
}
