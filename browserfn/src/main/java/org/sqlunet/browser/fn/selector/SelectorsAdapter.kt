/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.fn.selector

import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.sqlunet.browser.CursorRecyclerViewAdapter
import org.sqlunet.browser.fn.R
import org.sqlunet.framenet.loaders.Queries
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_or_Frames
import org.sqlunet.style.Colors
import org.sqlunet.xnet.R as XNetR

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
     * Tracks activated view
     */
    var activated: View? = null

    /**
     * OnClickListener
     */
    private val onClickListener = { position: Int, itemView: View ->
        activated?.isActivated = false

        itemView.isActivated = true
        activated = itemView
        Log.d(TAG, "Activate position $position view $itemView")

        activate(position)
    }

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
        val idIsFrame = cursor.getColumnIndex(LexUnits_or_Frames.ISFRAME)
        val idFnId = cursor.getColumnIndex(LexUnits_or_Frames.FNID)
        val idName = cursor.getColumnIndex(LexUnits_or_Frames.NAME)
        val idFrameName = cursor.getColumnIndex(LexUnits_or_Frames.FRAMENAME)
        val idWord = cursor.getColumnIndex(LexUnits_or_Frames.WORD)
        val idWordId = cursor.getColumnIndex(LexUnits_or_Frames.WORDID)
        val idFnWordId = cursor.getColumnIndex(LexUnits_or_Frames.FNWORDID)
        val idFrameId = cursor.getColumnIndex(LexUnits_or_Frames.FRAMEID)
        val idIsLike = cursor.getColumnIndex(Queries.ISLIKE)

        val isFrame = cursor.getInt(idIsFrame) != 0
        val fnId = cursor.getLong(idFnId)
        val name = cursor.getString(idName)
        val frameName = cursor.getString(idFrameName)
        val word = cursor.getString(idWord)
        val wordId = cursor.getLong(idWordId)
        val fnWordId = cursor.getLong(idFnWordId)
        val frameId = cursor.getLong(idFrameId)
        val isLike = cursor.getInt(idIsLike) != 0

        bindTextView(viewHolder.fnNameView, name, isLike)
        bindTextView(viewHolder.fnFrameNameView, frameName, false)
        bindTextView(viewHolder.fnWordView, word, false)
        bindTextView(viewHolder.fnIdView, fnId.toString(), false)
        bindTextView(viewHolder.fnWordIdView, fnWordId.toString(), false)
        bindTextView(viewHolder.wordIdView, wordId.toString(), false)
        bindTextView(viewHolder.fnFrameIdView, frameId.toString(), false)

        viewHolder.iconView.setImageResource(if (isFrame) XNetR.drawable.roles else XNetR.drawable.member)
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

    // helper

    private fun bindTextView(textView: TextView, text: String?, isDimmed: Boolean) {
        if (text == null || "0" == text) {
            textView.visibility = View.GONE
        } else {
            textView.visibility = View.VISIBLE
            textView.text = text
            textView.setTextColor(if (isDimmed) Colors.textDimmedForeColor else Colors.textNormalForeColor)
        }
    }

    // click

    /**
     * ViewHolder
     *
     * @param itemView view
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener { onClickListener.invoke(bindingAdapterPosition, itemView) }
        }

        val fnNameView: TextView = itemView.findViewById(R.id.fnname)
        val fnFrameNameView: TextView = itemView.findViewById(R.id.fnframename)
        val fnWordView: TextView = itemView.findViewById(R.id.fnword)
        val fnIdView: TextView = itemView.findViewById(R.id.fnid)
        val fnWordIdView: TextView = itemView.findViewById(R.id.fnwordid)
        val wordIdView: TextView = itemView.findViewById(R.id.wordid)
        val fnFrameIdView: TextView = itemView.findViewById(R.id.fnframeid)
        val iconView: ImageView = itemView.findViewById(R.id.icon)
    }

    companion object {

        private const val TAG = "SelectorsA"
    }
}
