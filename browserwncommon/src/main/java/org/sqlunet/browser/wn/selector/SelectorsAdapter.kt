/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.wn.selector

import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.sqlunet.browser.CursorRecyclerViewAdapter
import org.sqlunet.browser.wn.lib.R
import org.sqlunet.wordnet.provider.WordNetContract

class SelectorsAdapter : RecyclerView.Adapter<SelectorsAdapter.ViewHolder>(), CursorRecyclerViewAdapter {

    private var cursor: Cursor? = null
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_selector, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (cursor!!.moveToPosition(position)) {
            val posId = cursor!!.getInt(cursor!!.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.POSID))
            val domain = cursor!!.getString(cursor!!.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.DOMAIN))
            val definition = cursor!!.getString(cursor!!.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.DEFINITION))
            val casedWord = cursor!!.getString(cursor!!.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.CASEDWORD))
            val pronunciations = cursor!!.getString(cursor!!.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.PRONUNCIATIONS))
            val senseNum = cursor!!.getInt(cursor!!.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SENSENUM))
            val senseKey = cursor!!.getString(cursor!!.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SENSEKEY))
            val lexId = cursor!!.getInt(cursor!!.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.LEXID))
            val tagCount = cursor!!.getInt(cursor!!.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.TAGCOUNT))
            val wordId = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.WORDID))
            val synsetId = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SYNSETID))
            val senseId = cursor!!.getLong(cursor!!.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SENSEID))

            holder.pos.text = posId.toString()
            holder.domain.text = domain
            holder.definition.text = definition
            holder.cased.text = casedWord
            holder.pronunciation.text = pronunciations
            holder.sensenum.text = senseNum.toString()
            holder.sensekey.text = senseKey
            holder.lexid.text = lexId.toString()
            holder.tagcount.text = tagCount.toString()
            holder.wordid.text = wordId.toString()
            holder.synsetid.text = synsetId.toString()
            holder.senseid.text = senseId.toString()

            holder.itemView.setOnClickListener {
                onClickListener?.onClick(position, holder.itemView)
            }
        }
    }

    override fun getItemCount(): Int {
        return cursor?.count ?: 0
    }

    override fun changeCursor(cursor: Cursor?) {
        this.cursor = cursor
        notifyDataSetChanged()
    }

    override fun getCursor(): Cursor? {
        return cursor
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, view: View)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
