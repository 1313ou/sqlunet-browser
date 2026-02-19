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
import org.sqlunet.syntagnet.provider.SyntagNetContract

class SnSelectorsAdapter(val activate: (position: Int) -> Unit) : RecyclerView.Adapter<SnSelectorsAdapter.ViewHolder>(), CursorRecyclerViewAdapter {

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
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_snselector, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cursor = this.cursor
        requireNotNull(cursor) { "Cursor is null" }
        if (!cursor.moveToPosition(position)) {
            throw IllegalStateException("Can't move cursor to position $position")
        }

        bindTextView(holder.word1, cursor, SyntagNetContract.WORD1)
        bindTextView(holder.word2, cursor, SyntagNetContract.WORD2)
        bindTextView(holder.pos1, cursor, SyntagNetContract.POS1)
        bindTextView(holder.pos2, cursor, SyntagNetContract.POS2)

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
        if (text == null) {
            textView.visibility = View.GONE
        } else {
            textView.visibility = View.VISIBLE
            textView.text = text
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val word1: TextView = itemView.findViewById(R.id.word1)
        val word2: TextView = itemView.findViewById(R.id.word2)
        val pos1: TextView = itemView.findViewById(R.id.pos1)
        val pos2: TextView = itemView.findViewById(R.id.pos2)

        init {
            itemView.setOnClickListener {
                onClickListener.invoke(bindingAdapterPosition)
            }
        }
    }

    companion object {

        private const val TAG = "SnSelectorsA"
    }
}
