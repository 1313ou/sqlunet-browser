/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.fn.selector

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.CursorAdapter
import android.widget.ImageView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import org.sqlunet.Pointer
import org.sqlunet.browser.BaseSelectorsListFragment
import org.sqlunet.browser.fn.R
import org.sqlunet.framenet.FnFramePointer
import org.sqlunet.framenet.FnLexUnitPointer
import org.sqlunet.framenet.loaders.Queries
import org.sqlunet.framenet.loaders.Queries.prepareSelect
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_or_Frames
import org.sqlunet.framenet.provider.FrameNetProvider.Companion.makeUri
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.style.Colors

/**
 * Selector Fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SelectorsFragment : BaseSelectorsListFragment() {

    init {
        layoutId = R.layout.fragment_selectors
        viewModelKey = "fn:selectors(word)"
    }

    override fun activate(position: Int) {
        positionModel!!.setPosition(position)
        if (listener != null) {
            val adapter = (adapter as SimpleCursorAdapter?)!!
            val cursor = adapter.cursor!!
            if (cursor.moveToPosition(position)) {
                // column indexes
                val idIsFrame = cursor.getColumnIndex(LexUnits_or_Frames.ISFRAME)
                val idFnId = cursor.getColumnIndex(LexUnits_or_Frames.FNID)
                val idWord = cursor.getColumnIndex(LexUnits_or_Frames.WORD)
                val idWordId = cursor.getColumnIndex(LexUnits_or_Frames.WORDID)

                // retrieve
                val fnId = cursor.getLong(idFnId)
                val isFrame = cursor.getInt(idIsFrame) != 0
                val word = cursor.getString(idWord)
                val wordId = cursor.getLong(idWordId)

                // pointer
                val pointer = if (isFrame) FnFramePointer(fnId) else FnLexUnitPointer(fnId)

                // notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
                listener!!.onItemSelected(pointer, word, wordId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // arguments
        val args = requireArguments()

        // target word
        var query = args.getString(ProviderArgs.ARG_QUERYSTRING)
        if (query != null) {
            query = query.trim { it <= ' ' }.lowercase()
        }
        word = query!!
    }

    // A D A P T E R

    override fun makeAdapter(): CursorAdapter {
        val adapter = SimpleCursorAdapter(
            requireContext(), R.layout.item_selector, null, arrayOf( //
                LexUnits_or_Frames.NAME, LexUnits_or_Frames.FRAMENAME, LexUnits_or_Frames.WORD, LexUnits_or_Frames.FNID, LexUnits_or_Frames.FNWORDID, LexUnits_or_Frames.WORDID, LexUnits_or_Frames.FRAMEID, LexUnits_or_Frames.ISFRAME
            ), intArrayOf( //
                R.id.fnname, R.id.fnframename, R.id.fnword, R.id.fnid, R.id.fnwordid, R.id.wordid, R.id.fnframeid, R.id.icon
            ), 0
        )
        adapter.viewBinder = SimpleCursorAdapter.ViewBinder setViewBinder@{ view: View, cursor: Cursor, columnIndex: Int ->
            when (view) {
                is TextView -> {
                    val idIsLike = cursor.getColumnIndex(Queries.ISLIKE)
                    val idName = cursor.getColumnIndex(LexUnits_or_Frames.NAME)
                    val text = cursor.getString(columnIndex)
                    if (text == null || "0" == text) {
                        view.setVisibility(View.GONE)
                        return@setViewBinder true
                    } else {
                        view.setVisibility(View.VISIBLE)
                    }
                    view.text = text
                    if (idName == columnIndex) {
                        val isLike = cursor.getInt(idIsLike) != 0
                        view.setTextColor(if (isLike) Colors.textDimmedForeColor else Colors.textNormalForeColor)
                    }
                    return@setViewBinder true
                }

                is ImageView -> {
                    val isFrame = cursor.getInt(columnIndex) != 0
                    view.setImageResource(if (isFrame) R.drawable.roles else R.drawable.member)
                    return@setViewBinder true
                }

                else -> {
                    throw IllegalStateException(view.javaClass.getName() + " is not a view that can be bound by this SimpleCursorAdapter")
                }
            }
        }
        return adapter
    }

    // L O A D

    override fun load() {
        // load the contents
        val sql = prepareSelect(word)
        val uri = Uri.parse(makeUri(sql.providerUri))
        dataModel!!.loadData(uri, sql, null)
    }

    // C L I C K   L I S T E N E R

    /**
     * A callback interface that all activities containing this fragment must implement. This mechanism allows activities to be notified of item selections.
     */
    fun interface Listener {
        /**
         * Callback for when an item has been selected.
         */
        fun onItemSelected(pointer: Pointer?, word: String?, wordId: Long)
    }

    /**
     * The fragment's current callback, which is notified of list item clicks.
     */
    private var listener: Listener? = null

    /**
     * Set listener
     *
     * @param listener listener
     */
    fun setListener(listener: Listener?) {
        this.listener = listener
    }
}
