/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.wn.selector

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.CursorAdapter
import android.widget.ImageView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import org.sqlunet.browser.BaseSelectorsListFragment
import org.sqlunet.browser.wn.lib.R
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.speak.Pronunciation.Companion.sortedPronunciations
import org.sqlunet.wordnet.SensePointer
import org.sqlunet.wordnet.loaders.Queries.prepareWordXSelect
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains
import org.sqlunet.wordnet.provider.WordNetProvider.Companion.makeUri

/**
 * Selector Fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SelectorsFragment : BaseSelectorsListFragment() {

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

    override fun makeAdapter(): CursorAdapter {
        val adapter = SimpleCursorAdapter(
            requireContext(), R.layout.item_selector, null, arrayOf(
                Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.POSID,
                Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.DOMAIN,
                Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.DEFINITION,
                Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.CASEDWORD,
                Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.PRONUNCIATIONS,
                Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SENSENUM,
                Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SENSEKEY,
                Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.LEXID,
                Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.TAGCOUNT,
                Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.WORDID,
                Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SYNSETID,
                Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SENSEID
            ), intArrayOf(
                R.id.pos,
                R.id.domain,
                R.id.definition,
                R.id.cased,
                R.id.pronunciation,
                R.id.sensenum,
                R.id.sensekey,
                R.id.lexid,
                R.id.tagcount,
                R.id.wordid,
                R.id.synsetid,
                R.id.senseid
            ), 0
        )
        adapter.viewBinder = SimpleCursorAdapter.ViewBinder setViewBinder@{ view: View, cursor: Cursor, columnIndex: Int ->
            var text = cursor.getString(columnIndex)

            // pronunciation
            if (view.id == R.id.pronunciation) {
                text = sortedPronunciations(text)
            }

            // visibility
            if (text == null) {
                view.visibility = View.GONE
                return@setViewBinder false
            } else {
                view.visibility = View.VISIBLE
            }

            // type of view
            when (view) {
                is TextView -> {
                    view.text = text
                    return@setViewBinder true
                }

                is ImageView -> {
                    try {
                        view.setImageResource(text.toInt())
                        return@setViewBinder true
                    } catch (nfe: NumberFormatException) {
                        view.setImageURI(Uri.parse(text))
                        return@setViewBinder true
                    }
                }

                else -> {
                    throw IllegalStateException(view.javaClass.name + " is not a view that can be bound by this SimpleCursorAdapter")
                }
            }
        }
        return adapter
    }

    // L O A D

    override fun load() {
        // load the contents
        val sql = prepareWordXSelect(word)
        val uri = Uri.parse(makeUri(sql.providerUri))
        dataModel!!.loadData(uri, sql) { cursor: Cursor -> wordIdFromWordPostProcess(cursor) }
    }

    /**
     * Post processing, extraction of wordid from cursor
     *
     * @param cursor cursor
     */
    private fun wordIdFromWordPostProcess(cursor: Cursor) {
        if (cursor.moveToFirst()) {
            val idWordId = cursor.getColumnIndex(Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.WORDID)
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
            val adapter = adapter!! as SimpleCursorAdapter
            val cursor = adapter.cursor!!
            if (cursor.moveToPosition(position)) {
                // column indexes
                val idSynsetId = cursor.getColumnIndex(Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SYNSETID)
                val idPos = cursor.getColumnIndex(Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.POS)
                val idCased = cursor.getColumnIndex(Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.CASEDWORD)
                val idPronunciation = cursor.getColumnIndex(Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.PRONUNCIATIONS)

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
