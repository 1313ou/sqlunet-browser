/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.sn.selector

import android.database.Cursor
import android.os.Bundle
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import org.sqlunet.browser.BaseSelectorsRecyclerFragment
import org.sqlunet.browser.Selectors
import org.sqlunet.browser.sn.R
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.wordnet.loaders.Queries.prepareSelectSn
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains
import org.sqlunet.wordnet.provider.WordNetProvider.Companion.makeUri

/**
 * Selector Fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SelectorsFragment : BaseSelectorsRecyclerFragment() {

    /**
     * Word id
     */
    private var wordId: Long = -1

    init {
        layoutId = R.layout.fragment_selectors
        viewModelKey = "sn:selectors(word)"
    }

    override fun select(position: Int) {
        positionModel!!.setPosition(position)
        if (listeners != null) {
            val adapter = recyclerView.adapter as SelectorsAdapter
            val cursor = adapter.getCursor()!!
            if (cursor.moveToPosition(position)) {
                // column indexes
                val idSynsetId = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID)
                val idPos = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_Poses_Domains.POS)
                val idCased = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_Poses_Domains.CASEDWORD)

                // retrieve
                val synsetId = if (cursor.isNull(idSynsetId)) 0 else cursor.getLong(idSynsetId)
                val pos = cursor.getString(idPos)
                val cased = cursor.getString(idCased)
                val pronunciation: String? = null

                // pointer
                val pointer: SelectorPointer = PosSelectorPointer(synsetId, wordId, pos[0])

                // notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
                for (listener in listeners!!) {
                    listener.onItemSelected(pointer, word, cased, pronunciation, pos)
                }
            }
        }
    }

    /**
     * Deactivate all
     */
    fun deactivate() {
        (recyclerView.adapter as SelectorsAdapter).deactivate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // arguments
        val args = requireArguments()

        // activate on click
        activateOnItemClick = args.getBoolean(Selectors.IS_TWO_PANE, false)

        // target word
        var query = args.getString(ProviderArgs.ARG_QUERYSTRING)
        if (query != null) {
            query = query.trim { it <= ' ' }.lowercase()
        }
        word = query!!
        wordId = 0
    }

    // A D A P T E R

    override val adapter: RecyclerView.Adapter<*> = SelectorsAdapter { position: Int ->
        select(position)
    }

    // L O A D

    override fun load() {
        // load the contents
        val sql = prepareSelectSn(word)
        val uri = makeUri(sql.providerUri).toUri()
        dataModel!!.loadData(uri, sql) { cursor: Cursor -> wordIdFromWordPostProcess(cursor) }
    }

    /**
     * Post-processing, extraction of wordid from cursor
     *
     * @param cursor cursor
     */
    private fun wordIdFromWordPostProcess(cursor: Cursor) {
        if (cursor.moveToFirst()) {
            val idWordId = cursor.getColumnIndex(Words_Senses_CasedWords_Synsets_Poses_Domains.WORDID)
            wordId = cursor.getLong(idWordId)
        }
    }

    // C L I C K   L I S T E N E R

    /**
     * A callback interface that all activities containing this fragment must implement. This mechanism allows activities to be notified of item selections.
     */
    fun interface Listener {

        /**
         * Callback for when an item has been selected.
         */
        fun onItemSelected(pointer: SelectorPointer?, word: String?, cased: String?, pronunciation: String?, pos: String?)
    }

    /**
     * The fragment's current callbacks, which are notified of list item clicks.
     */
    private var listeners: Array<out Listener>? = null

    /**
     * Set listeners
     *
     * @param listeners listeners
     */
    fun setListeners(vararg listeners: Listener) {
        this.listeners = listeners
    }
}
