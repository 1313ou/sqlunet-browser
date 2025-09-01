/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.sn.selector

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.CursorAdapter
import android.widget.ImageView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import org.sqlunet.browser.BaseSelectorsListFragment
import org.sqlunet.browser.Selectors
import org.sqlunet.browser.sn.R
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.wordnet.loaders.Queries.prepareSelectSn
import org.sqlunet.wordnet.provider.WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains
import org.sqlunet.wordnet.provider.WordNetProvider.Companion.makeUri
import androidx.core.net.toUri

/**
 * Selector Fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SelectorsFragment : BaseSelectorsListFragment() {

    /**
     * Word id
     */
    private var wordId: Long = -1

    init {
        layoutId = R.layout.fragment_selectors
        viewModelKey = "sn:selectors(word)"
    }

    override fun activate(position: Int) {
        positionModel!!.setPosition(position)
        if (listeners != null) {
            val adapter = (adapter as SimpleCursorAdapter?)!!
            val cursor = adapter.cursor!!
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
        val listView = listView
        listView!!.clearChoices()
        listView.requestLayout()
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

    override fun makeAdapter(): CursorAdapter {
        val adapter = SimpleCursorAdapter(
            requireContext(), R.layout.item_selector, null, arrayOf(
                Words_Senses_CasedWords_Synsets_Poses_Domains.POSID,
                Words_Senses_CasedWords_Synsets_Poses_Domains.DOMAIN,
                Words_Senses_CasedWords_Synsets_Poses_Domains.DEFINITION,
                Words_Senses_CasedWords_Synsets_Poses_Domains.CASEDWORD,
                Words_Senses_CasedWords_Synsets_Poses_Domains.SENSENUM,
                Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEKEY,
                Words_Senses_CasedWords_Synsets_Poses_Domains.LEXID,
                Words_Senses_CasedWords_Synsets_Poses_Domains.TAGCOUNT,
                Words_Senses_CasedWords_Synsets_Poses_Domains.WORDID,
                Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID,
                Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEID
            ), intArrayOf(
                R.id.pos,
                R.id.domain,
                R.id.definition,
                R.id.cased,
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
            val text = cursor.getString(columnIndex)
            if (text == null) {
                view.visibility = View.GONE
                return@setViewBinder false
            } else {
                view.visibility = View.VISIBLE
            }
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
                        view.setImageURI(text.toUri())
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
        val sql = prepareSelectSn(word)
        val uri = makeUri(sql.providerUri).toUri()
        dataModel!!.loadData(uri, sql) { cursor: Cursor -> wordIdFromWordPostProcess(cursor) }
    }

    /**
     * Post processing, extraction of wordid from cursor
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
