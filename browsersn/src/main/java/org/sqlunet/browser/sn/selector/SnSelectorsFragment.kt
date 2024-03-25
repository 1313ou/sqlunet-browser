/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.sn.selector

import android.database.Cursor
import android.database.MatrixCursor
import android.database.MergeCursor
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
import org.sqlunet.syntagnet.loaders.Queries.prepareSnSelect
import org.sqlunet.syntagnet.provider.SyntagNetContract
import org.sqlunet.syntagnet.provider.SyntagNetContract.SnCollocations_X
import org.sqlunet.syntagnet.provider.SyntagNetProvider
import org.sqlunet.wordnet.provider.WordNetContract
import org.sqlunet.wordnet.provider.WordNetProvider

/**
 * Selector Fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SnSelectorsFragment : BaseSelectorsListFragment() {

    /**
     * Word id
     */
    private var wordId: Long = -1

    init {
        layoutId = R.layout.fragment_selectors
        viewModelKey = "snx:selectors(word)"
    }

    override fun activate(position: Int) {
        positionModel!!.setPosition(position)
        if (listeners != null) {
            val adapter = (adapter as SimpleCursorAdapter?)!!
            val cursor = adapter.cursor!!
            if (cursor.moveToPosition(position)) {
                // column indexes
                val idSynset1Id = cursor.getColumnIndex(SnCollocations_X.SYNSET1ID)
                val idPos1 = cursor.getColumnIndex(SyntagNetContract.POS1)
                val idWord1Id = cursor.getColumnIndex(SnCollocations_X.WORD1ID)
                val idSynset2Id = cursor.getColumnIndex(SnCollocations_X.SYNSET2ID)
                val idPos2 = cursor.getColumnIndex(SyntagNetContract.POS2)
                val idWord2Id = cursor.getColumnIndex(SnCollocations_X.WORD2ID)

                // retrieve
                val synset1Id = if (cursor.isNull(idSynset1Id)) -1 else cursor.getLong(idSynset1Id)
                val word1Id = if (cursor.isNull(idWord1Id)) -1 else cursor.getLong(idWord1Id)
                val pos1: Char = if (cursor.isNull(idPos1)) '\u0000' else cursor.getString(idPos1)[0]
                val synset2Id = if (cursor.isNull(idSynset2Id)) -1 else cursor.getLong(idSynset2Id)
                val word2Id = if (cursor.isNull(idWord2Id)) -1 else cursor.getLong(idWord2Id)
                val pos2: Char = if (cursor.isNull(idPos2)) '\u0000' else cursor.getString(idPos2)[0]
                val target = if (wordId != -1L && word1Id != word2Id) (if (word1Id == wordId) 1 else if (word2Id == wordId) 2 else 0) else 0

                // pointer
                val pointer = CollocationSelectorPointer(synset1Id, word1Id, pos1, synset2Id, word2Id, pos2, target)

                // notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
                for (listener in listeners!!) {
                    listener.onItemSelected(pointer)
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
        wordId = queryId(query)
    }

    override fun makeAdapter(): CursorAdapter {
        val adapter = SimpleCursorAdapter(
            requireContext(), R.layout.item_snselector, null,  //
            DISPLAYED_COLUMNS, DISPLAYED_COLUMN_RES_IDS, 0
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
                        view.setImageURI(Uri.parse(text))
                        return@setViewBinder true
                    }
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
        if (wordId == -1L) {
            return
        }
        val sql = prepareSnSelect(wordId)
        val uri = Uri.parse(SyntagNetProvider.makeUri(sql.providerUri))
        dataModel!!.loadData(uri, sql, null)
    }

    // H E L P E R S

    /**
     * Query word
     *
     * @param query query word
     * @return word id
     */
    private fun queryId(query: String?): Long {
        val uri = Uri.parse(WordNetProvider.makeUri(WordNetContract.Words.URI))
        val projection = arrayOf(WordNetContract.Words.WORDID)
        val selection = WordNetContract.Words.WORD + " = ?" //
        val selectionArgs = arrayOf(query)
        requireContext().contentResolver.query(uri, projection, selection, selectionArgs, null).use { cursor ->
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    val idWordId = cursor.getColumnIndex(WordNetContract.Words.WORDID)
                    return cursor.getLong(idWordId)
                }
            }
        }
        return -1
    }

    // V I E W M O D E L S

    override fun augmentCursor(cursor: Cursor): Cursor {
        return augmentCursor(cursor, wordId, word)
    }

    /**
     * Augment cursor with special values
     *
     * @param cursor cursor
     * @param wordid word id
     * @param word   word
     * @return augmented cursor
     */
    private fun augmentCursor(cursor: Cursor, wordid: Long, word: String): Cursor {
        // Create a MatrixCursor filled with the special rows to add.
        val matrixCursor = MatrixCursor(COLUMNS)

        //	"_id",  WORD1ID,  WORD2ID,  SYNSET1ID,  SYNSET2ID,  WORD1,  WORD2,  POS1,  POS2
        matrixCursor.addRow(arrayOf<Any?>(Int.MAX_VALUE, wordid, wordid, null, null, "* $word *", "", null, null))
        matrixCursor.addRow(arrayOf<Any?>(Int.MAX_VALUE - 1, wordid, null, null, null, word, "*", null, null))
        matrixCursor.addRow(arrayOf<Any?>(Int.MAX_VALUE - 2, null, wordid, null, null, "*", word, null, null))

        // Merge your existing cursor with the matrixCursor you created.
        return MergeCursor(arrayOf(matrixCursor, cursor))
    }

    // C L I C K   L I S T E N E R

    /**
     * A callback interface that all activities containing this fragment must implement. This mechanism allows activities to be notified of item selections.
     */
    fun interface Listener {
        /**
         * Callback for when an item has been selected.
         */
        fun onItemSelected(pointer: CollocationSelectorPointer?)
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

    companion object {

        /**
         * Columns
         */
        private val COLUMNS = arrayOf( //
            "_id",  //
            SnCollocations_X.WORD1ID,  //
            SnCollocations_X.WORD2ID,  //
            SnCollocations_X.SYNSET1ID,  //
            SnCollocations_X.SYNSET2ID,  //
            SyntagNetContract.WORD1,  //
            SyntagNetContract.WORD2,  //
            SyntagNetContract.POS1,  //
            SyntagNetContract.POS2
        )

        /**
         * Displayed columns
         */
        private val DISPLAYED_COLUMNS = arrayOf( //
            SyntagNetContract.WORD1,  //
            SyntagNetContract.WORD2,  //
            SyntagNetContract.POS1,  //
            SyntagNetContract.POS2
        )

        /**
         * Column resources
         */
        private val DISPLAYED_COLUMN_RES_IDS = intArrayOf( //
            R.id.word1,  //
            R.id.word2,  //
            R.id.pos1,  //
            R.id.pos2
        )
    }
}
