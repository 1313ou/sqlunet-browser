/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.fn.selector

import android.database.Cursor
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import org.sqlunet.Pointer
import org.sqlunet.browser.BaseSelectorsRecyclerFragment
import org.sqlunet.browser.fn.R
import org.sqlunet.framenet.FnFramePointer
import org.sqlunet.framenet.FnLexUnitPointer
import org.sqlunet.framenet.loaders.Queries
import org.sqlunet.framenet.loaders.Queries.prepareSelect
import org.sqlunet.framenet.provider.FrameNetContract.LexUnits_or_Frames
import org.sqlunet.framenet.provider.FrameNetProvider.Companion.makeUri
import org.sqlunet.provider.ProviderArgs

/**
 * Selector Fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SelectorsFragment : BaseSelectorsRecyclerFragment() {

    init {
        layoutId = R.layout.fragment_selectors
        viewModelKey = "fn:selectors(word)"
    }

    override fun activate(position: Int) {
        positionModel!!.setPosition(position)
        if (listener != null) {
            val adapter = recyclerView!!.adapter as SelectorsAdapter
            val cursor = adapter.getCursor()
            if (cursor != null && cursor.moveToPosition(position)) {
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

    override fun makeAdapter(): RecyclerView.Adapter<*> {
        val adapter = SelectorsAdapter()
        adapter.setOnClickListener(object : SelectorsAdapter.OnClickListener {
            override fun onClick(position: Int, view: View) {
                activate(position)
            }
        })
        return adapter
    }

    // L O A D

    override fun load() {
        // load the contents
        val sql = prepareSelect(word)
        val uri = makeUri(sql.providerUri).toUri()
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
