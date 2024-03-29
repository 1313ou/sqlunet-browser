/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.browser

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.CursorAdapter
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import androidx.fragment.app.ListFragment
import androidx.lifecycle.ViewModelProvider
import org.sqlunet.browser.PositionViewModel
import org.sqlunet.browser.SqlunetViewModel
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.wordnet.R
import org.sqlunet.wordnet.SensePointer
import org.sqlunet.wordnet.loaders.Queries.prepareSenses
import org.sqlunet.wordnet.provider.WordNetContract
import org.sqlunet.wordnet.provider.WordNetProvider.Companion.makeUri

/**
 * Senses selector fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SensesFragment : ListFragment() {

    /**
     * A callback interface that all activities containing this fragment must implement. This mechanism allows activities to be notified of item selections.
     */
    fun interface Listener {

        /**
         * Callback for when an item has been selected.
         */
        fun onItemSelected(sense: SensePointer?, word: String?, cased: String?, pos: String?)
    }

    /**
     * Activate on click flag: in two-pane mode, list items should be given the 'activated' state when touched.
     */
    private var activateOnItemClick = true

    /**
     * The fragment's current callback, which is notified of list item clicks.
     */
    private var listener: Listener? = null

    /**
     * Search query
     */
    private var word: String? = null

    /**
     * Word id
     */
    private var wordId: Long = 0

    /**
     * Data view model
     */
    private var dataModel: SqlunetViewModel? = null

    /**
     * Position view model
     */
    private var positionModel: PositionViewModel? = null

    // L I F E C Y C L E

    // --activate--

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // arguments
        val args = requireArguments()

        // target word
        var query = args.getString(ProviderArgs.ARG_QUERYSTRING)
        if (query != null) {
            query = query.trim { it <= ' ' }.lowercase()
        }
        word = query
        wordId = 0

        // list adapter, with no data
        val adapter = makeAdapter()
        setListAdapter(adapter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_senses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // when setting CHOICE_MODE_SINGLE, ListView will automatically give items the 'activated' state when touched.
        getListView().setChoiceMode(if (activateOnItemClick) AbsListView.CHOICE_MODE_SINGLE else AbsListView.CHOICE_MODE_NONE)
    }

    override fun onStart() {
        super.onStart()

        // data view models
        Log.d(TAG, "Make models")
        makeModels() // sets cursor

        // data
        senses()
    }

    // --deactivate--

    override fun onDestroy() {
        super.onDestroy()
        val adapter = listAdapter as CursorAdapter?
        adapter?.changeCursor(null)
    }

    // H E L P E R S

   /**
     * Make adapter
     *
     * @return adapter
     */
    private fun makeAdapter(): ListAdapter {
        Log.d(TAG, "Make adapter")
        val adapter = SimpleCursorAdapter(
            requireContext(), R.layout.item_sense, null, arrayOf(
                WordNetContract.Poses.POS,
                WordNetContract.Senses.SENSENUM,
                WordNetContract.Domains.DOMAIN,
                WordNetContract.Synsets.DEFINITION,
                WordNetContract.CasedWords.CASEDWORD,
                WordNetContract.Senses.TAGCOUNT,
                WordNetContract.Senses.LEXID,
                WordNetContract.Senses.SENSEKEY,
                WordNetContract.Words.WORDID,
                WordNetContract.Synsets.SYNSETID,
                WordNetContract.Senses.SENSEID
            ), intArrayOf(
                R.id.pos,
                R.id.sensenum,
                R.id.domain,
                R.id.definition,
                R.id.cased,
                R.id.tagcount,
                R.id.lexid,
                R.id.sensekey,
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

    // V I E W M O D E L S

    /**
     * Make view models
     */
    private fun makeModels() {
        // data model
        dataModel = ViewModelProvider(this)["wn.senses(word)", SqlunetViewModel::class.java]
        dataModel!!.getData().observe(getViewLifecycleOwner()) { cursor: Cursor? ->
            // pass on to list adapter
            val adapter = (listAdapter as CursorAdapter?)!!
            adapter.changeCursor(cursor)
        }

        // position model
        positionModel = ViewModelProvider(this)[PositionViewModel::class.java]
        positionModel!!.positionLiveData.observe(getViewLifecycleOwner()) { position: Int ->
            Log.d(TAG, "Observed position change $position")
            getListView().setItemChecked(position, position != AdapterView.INVALID_POSITION)
        }
        positionModel!!.setPosition(AdapterView.INVALID_POSITION)
    }

    // L O A D

    /**
     * Load data from word
     */
    private fun senses() {
        // load the contents
        val sql = prepareSenses(word!!)
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
            val idWordId = cursor.getColumnIndex(WordNetContract.Words.WORDID)
            wordId = cursor.getLong(idWordId)
        }
    }

    // L I S T E N E R

    /**
     * Set listener
     *
     * @param listener listener
     */
    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    // C L I C K

   /**
     * Turns on activate-on-click mode. When this mode is on, list items will be given the 'activated' state when touched.
     *
     * @param activateOnItemClick true if activate
     */
    fun setActivateOnItemClick(activateOnItemClick: Boolean) {
        this.activateOnItemClick = activateOnItemClick
    }

    override fun onListItemClick(listView: ListView, view: View, position: Int, id: Long) {
        super.onListItemClick(listView, view, position, id)
        activate(position)
    }

    /**
     * Activate item at position
     *
     * @param position position
     */
    private fun activate(position: Int) {
        positionModel!!.setPosition(position)
        if (listener != null) {
            val adapter = (listAdapter as SimpleCursorAdapter?)!!
            val cursor = adapter.cursor!!
            if (cursor.moveToPosition(position)) {
                // column indexes
                val idSynsetId = cursor.getColumnIndex(WordNetContract.Synsets.SYNSETID)
                val idPos = cursor.getColumnIndex(WordNetContract.Poses.POS)
                val idCased = cursor.getColumnIndex(WordNetContract.CasedWords.CASEDWORD)

                // retrieve
                val synsetId = if (cursor.isNull(idSynsetId)) 0 else cursor.getLong(idSynsetId)
                val pos = cursor.getString(idPos)
                val cased = cursor.getString(idCased)

                // pointer
                val pointer = SensePointer(synsetId, wordId)

                // notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
                listener!!.onItemSelected(pointer, word, cased, pos)
            }
        }
    }

    companion object {

        private const val TAG = "SensesF"
    }
}
