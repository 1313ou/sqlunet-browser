/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.xn.selector

import android.database.Cursor
import android.os.Bundle
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import org.sqlunet.browser.BaseSelectorsRecyclerFragment
import org.sqlunet.browser.R
import org.sqlunet.loaders.Queries.prepareWordPronunciationSelect
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.provider.XNetContract.Words_Pronunciations_FnWords_PbWords_VnWords
import org.sqlunet.provider.XSqlUNetProvider.Companion.makeUri

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

    init {
        layoutId = R.layout.fragment_selectors
        viewModelKey = "selectors(word)"
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
        wordId = 0
    }

    // A D A P T E R

    override val adapter: RecyclerView.Adapter<*> = SelectorsAdapter { position: Int ->
        select(position)
    }

    // L O A D

    override fun load() {
        // load the contents
        val sql = prepareWordPronunciationSelect(word)
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
            val idWordId = cursor.getColumnIndex(Words_Pronunciations_FnWords_PbWords_VnWords.WORDID)
            wordId = cursor.getLong(idWordId)
        }
    }

    // S E L E C T I O N

    override fun select(position: Int) {
        positionModel!!.setPosition(position)
        if (listener != null) {
            val adapter = recyclerView.adapter as SelectorsAdapter
            val cursor = adapter.getCursor()
            if (cursor != null && cursor.moveToPosition(position)) {
                // column indexes
                val idSynsetId = cursor.getColumnIndex(Words_Pronunciations_FnWords_PbWords_VnWords.SYNSETID)
                val idPos = cursor.getColumnIndex(Words_Pronunciations_FnWords_PbWords_VnWords.POS)
                val idCased = cursor.getColumnIndex(Words_Pronunciations_FnWords_PbWords_VnWords.CASED)
                val idPronunciation = cursor.getColumnIndex(Words_Pronunciations_FnWords_PbWords_VnWords.PRONUNCIATIONS)

                // retrieve
                val synsetId = if (cursor.isNull(idSynsetId)) 0 else cursor.getLong(idSynsetId)
                val pos = cursor.getString(idPos)
                val cased = cursor.getString(idCased)
                val pronunciation = cursor.getString(idPronunciation)

                // pointer
                val pointer: SelectorPointer = PosSelectorPointer(synsetId, wordId, pos[0])

                // notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
                listener!!.onItemSelected(pointer, word, cased, pronunciation, pos)
            }
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
        fun onItemSelected(pointer: SelectorPointer, word: String, cased: String?, pronunciation: String?, pos: String)
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
