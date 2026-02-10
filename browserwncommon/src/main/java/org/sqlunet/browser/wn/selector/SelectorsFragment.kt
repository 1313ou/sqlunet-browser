/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.wn.selector

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import org.sqlunet.browser.BaseSelectorsRecyclerFragment
import org.sqlunet.browser.CursorRecyclerViewAdapter
import org.sqlunet.browser.wn.lib.R
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.wordnet.SensePointer
import org.sqlunet.wordnet.loaders.Queries
import org.sqlunet.wordnet.provider.WordNetContract
import org.sqlunet.wordnet.provider.WordNetProvider

/**
 * Selector Fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SelectorsFragment : BaseSelectorsRecyclerFragment() {

    /**
     * Word id
     */
    private var wordId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // arguments
        val args = requireArguments()

        // target word
        var query = args.getString(ProviderArgs.ARG_QUERYSTRING)!!
        query = query.trim { it <= ' ' }.lowercase()
        word = query
        wordId = 0
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
        val sql = Queries.prepareWordXSelect(word)
        val uri: Uri = WordNetProvider.makeUri(sql.providerUri).toUri()
        dataModel!!.loadData(uri, sql) { cursor: Cursor -> wordIdFromWordPostProcess(cursor) }
    }

    /**
     * Post processing, extraction of wordid from cursor
     *
     * @param cursor cursor
     */
    private fun wordIdFromWordPostProcess(cursor: Cursor) {
        if (cursor.moveToFirst()) {
            val idWordId = cursor.getColumnIndex(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.WORDID)
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
        fun onItemSelected(pointer: SensePointer, word: String, cased: String?, pronunciation: String?, pos: String)
    }

    /**
     * The fragment's current callback, which is notified of list item clicks.
     */
    private var listener: Listener? = null

    init {
        layoutId = R.layout.fragment_selectors
        viewModelKey = "wn:selectors(word)"
    }

    /**
     * Set listener
     *
     * @param listener listener
     */
    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    override fun activate(position: Int) {
        positionModel!!.setPosition(position)
        if (listener != null) {
            val cursor = (recyclerView!!.adapter as CursorRecyclerViewAdapter).getCursor()
            if (cursor != null && cursor.moveToPosition(position)) {
                // column indexes
                val idSynsetId = cursor.getColumnIndex(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SYNSETID)
                val idPos = cursor.getColumnIndex(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.POS)
                val idCased = cursor.getColumnIndex(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.CASEDWORD)
                val idPronunciation = cursor.getColumnIndex(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.PRONUNCIATIONS)

                // retrieve
                val synsetId = if (cursor.isNull(idSynsetId)) 0 else cursor.getLong(idSynsetId)
                val pos = cursor.getString(idPos)
                val cased = cursor.getString(idCased)
                val pronunciation = cursor.getString(idPronunciation)

                // pointer
                val pointer = SensePointer(synsetId, wordId)

                // notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
                listener!!.onItemSelected(pointer, word, cased, pronunciation, pos)
            }
        }
    }
}
