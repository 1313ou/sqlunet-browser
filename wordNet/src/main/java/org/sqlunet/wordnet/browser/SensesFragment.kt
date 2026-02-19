/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.browser

import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
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
class SensesFragment : Fragment() {

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

    /**
     * Recycler view adapter
     */
    private var adapter: SensesAdapter? = null

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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_senses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SensesAdapter()
        val recyclerView = view.findViewById<RecyclerView>(R.id.senses_list)
        recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()

        // data view models
        Log.d(TAG, "Make models")
        makeModels() // sets cursor

        // data
        senses()
    }

    // V I E W M O D E L S

    /**
     * Make view models
     */
    private fun makeModels() {
        // data model
        dataModel = ViewModelProvider(this)["wn.senses(word)", SqlunetViewModel::class.java]
        dataModel!!.getData().observe(getViewLifecycleOwner()) { cursor: Cursor? ->
            adapter!!.changeCursor(cursor)
        }

        // position model
        positionModel = ViewModelProvider(this)[PositionViewModel::class.java]
        positionModel!!.positionLiveData.observe(getViewLifecycleOwner()) { position: Int ->
            Log.d(TAG, "Observed position change $position")
            adapter!!.setSelectedPosition(position)
        }
        positionModel!!.setPosition(RecyclerView.NO_POSITION)
    }

    // L O A D

    /**
     * Load data from word
     */
    private fun senses() {
        // load the contents
        val sql = prepareSenses(word!!)
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
     * Activate item at position
     *
     * @param position position
     */
    private fun activate(position: Int) {
        positionModel!!.setPosition(position)
        if (listener != null) {
            val cursor = adapter!!.getCursor()
            if (cursor != null && cursor.moveToPosition(position)) {
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

    // A D A P T E R

    inner class SensesAdapter : RecyclerView.Adapter<SensesAdapter.ViewHolder>() {

        private var cursor: Cursor? = null
        private var selectedPosition = RecyclerView.NO_POSITION

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sense, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (cursor!!.moveToPosition(position)) {
                holder.bind(cursor!!)
                holder.itemView.isSelected = selectedPosition == position
            }
        }

        override fun getItemCount(): Int {
            return cursor?.count ?: 0
        }

        @SuppressLint("NotifyDataSetChanged")
        fun changeCursor(cursor: Cursor?) {
            this.cursor = cursor
            notifyDataSetChanged()
        }

        fun getCursor(): Cursor? {
            return cursor
        }

        fun setSelectedPosition(position: Int) {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

            private val posView: TextView = itemView.findViewById(R.id.pos)
            private val sensenumView: TextView = itemView.findViewById(R.id.sensenum)
            private val domainView: TextView = itemView.findViewById(R.id.domain)
            private val definitionView: TextView = itemView.findViewById(R.id.definition)
            private val casedView: TextView = itemView.findViewById(R.id.cased)
            private val pronunciationView: TextView = itemView.findViewById(R.id.pronunciation)
            private val tagcountView: TextView = itemView.findViewById(R.id.tagcount)
            private val lexidView: TextView = itemView.findViewById(R.id.lexid)
            private val sensekeyView: TextView = itemView.findViewById(R.id.sensekey)
            private val wordidView: TextView = itemView.findViewById(R.id.wordid)
            private val synsetidView: TextView = itemView.findViewById(R.id.synsetid)
            private val senseidView: TextView = itemView.findViewById(R.id.senseid)

            init {
                itemView.setOnClickListener(this)
            }

            fun bind(cursor: Cursor) {
                // Extract data from cursor
                val idPos = cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.POS)
                val idDomain = cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.DOMAIN)
                val idDefinition = cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.DEFINITION)
                val idCasedWord = cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.CASEDWORD)
                val idPronunciations = cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.PRONUNCIATIONS)
                val idSenseNum = cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SENSENUM)
                val idSenseKey = cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SENSEKEY)
                val idLexId = cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.LEXID)
                val idTagCount = cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.TAGCOUNT)
                val idWordId = cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.WORDID)
                val idSynsetId = cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SYNSETID)
                val idSenseId = cursor.getColumnIndexOrThrow(WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SENSEID)

                val pos = cursor.getString(idPos)
                val domain = cursor.getString(idDomain)
                val definition = cursor.getString(idDefinition)
                val cased = cursor.getString(idCasedWord)
                val pronunciation = cursor.getString(idPronunciations)
                val tagCount = cursor.getInt(idTagCount)
                val sensenum = cursor.getInt(idSenseNum)
                val lexid = cursor.getInt(idLexId)
                val sensekey = cursor.getString(idSenseKey)
                val wordid = cursor.getLong(idWordId)
                val synsetid = cursor.getLong(idSynsetId)
                val senseid = cursor.getLong(idSenseId)

                // Bind data to views
                posView.text = pos
                domainView.text = domain
                definitionView.text = definition
                bindTextView(casedView, cased)
                bindTextView(pronunciationView, pronunciation)
                bindTextView(tagcountView, if (tagCount <= 0) null else tagCount.toString())
                sensenumView.text = sensenum.toString()
                lexidView.text = lexid.toString()
                sensekeyView.text = sensekey
                wordidView.text = wordid.toString()
                synsetidView.text = synsetid.toString()
                senseidView.text = senseid.toString()
            }
            // helper

            private fun bindTextView(textView: TextView, text: String?) {
                if (text.isNullOrEmpty()) {
                    textView.visibility = View.GONE
                } else {
                    textView.text = text
                    textView.visibility = View.VISIBLE
                }
            }

            override fun onClick(view: View?) {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    activate(position)
                }
            }
        }
    }

    companion object {

        private const val TAG = "SensesF"

        const val FRAGMENT_TAG = "senses"
    }
}
