/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.xn.xselector

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.CursorTreeAdapter
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.SimpleCursorTreeAdapter
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import org.sqlunet.browser.BaseSelectorsExpandableListFragment
import org.sqlunet.browser.R
import org.sqlunet.browser.SqlunetViewModel
import org.sqlunet.browser.xn.XnSettings
import org.sqlunet.browser.xn.xselector.XSelectorPointer.Companion.getMask
import org.sqlunet.loaders.Queries.prepareFnXSelect
import org.sqlunet.loaders.Queries.preparePbXSelect
import org.sqlunet.loaders.Queries.prepareVnXSelect
import org.sqlunet.loaders.Queries.prepareWordPronunciationXSelect
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.provider.XNetContract.Words_FnWords_PbWords_VnWords
import org.sqlunet.provider.XNetContract.Words_XNet_U
import org.sqlunet.provider.XSqlUNetProvider
import org.sqlunet.wordnet.loaders.Queries.prepareWnPronunciationXSelect
import org.sqlunet.wordnet.provider.WordNetProvider
import kotlin.math.floor
import androidx.core.net.toUri

/**
 * X selector fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class XSelectorsFragment : BaseSelectorsExpandableListFragment() {

    /**
     * A callback interface that all activities containing this fragment must implement. This mechanism allows activities to be notified of item selections.
     */
    fun interface Listener {

        /**
         * Callback for when an item has been selected.
         */
        fun onItemSelected(pointer: XSelectorPointer, word: String, cased: String?, pronunciation: String?, pos: String?)
    }

    /**
     * Activate on click flag
     */
    var activateOnItemClick = true

    /**
     * Group positions
     */
    private lateinit var groupPositions: IntArray

    /**
     * The current restored group state.
     */
    private var restoredGroupState: Int? = null

    /**
     * The fragment's current callback object, which is notified of list item clicks.
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
     * Id view model
     */
    private var wordIdFromWordModel: SqlunetViewModel? = null

    /**
     * WordNet model
     */
    private var wnFromWordIdModel: SqlunetViewModel? = null

    /**
     * VerbNet model
     */
    private var vnFromWordIdModel: SqlunetViewModel? = null

    /**
     * PropBank model
     */
    private var pbFromWordIdModel: SqlunetViewModel? = null

    /**
     * FrameNet model
     */
    private var fnFromWordIdModel: SqlunetViewModel? = null

    // L I F E C Y C L E

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
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_xselectors, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // when setting CHOICE_MODE_SINGLE, ListView will automatically give items the 'activated' state when touched.
        listView!!.choiceMode = if (activateOnItemClick) AbsListView.CHOICE_MODE_SINGLE else AbsListView.CHOICE_MODE_NONE

        // data view models
        makeModels()

        // adapter
        val adapter = makeAdapter()
        listAdapter = adapter
    }

    override fun onStart() {
        super.onStart()

        // load the contents
        // var<Cursor> idLiveData = wordIdFromWordModel.getMutableData()
        // var idCursor = idLiveData.getValue()
        // if (idCursor != null && !idCursor.isClosed())
        // {
        //  	idLiveData.setValue(idCursor)
        // }
        // else
        load()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // serialize and persist the activated group state
        val expandableListView = expandableListView
        if (expandableListView != null) {
            val adapter = listAdapter
            if (adapter != null) {
                val groupCount = adapter.groupCount
                var groupState = 0
                for (i in 0 until groupCount) {
                    if (expandableListView.isGroupExpanded(i)) {
                        groupState = groupState or (1 shl i)
                    }
                }
                outState.putInt(STATE_GROUPS, groupState)
                Log.d(TAG, "Saved group states " + Integer.toHexString(groupState) + " " + this)
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        restoredGroupState = savedInstanceState?.getInt(STATE_GROUPS)
    }

    // A D A P T E R

    /**
     * Make adapter
     */
    private fun makeAdapter(): ExpandableListAdapter {
        // group cursor populated
        Log.d(TAG, "make group cursor")
        val groupCursor = MatrixCursor(arrayOf(GROUPID_COLUMN, GROUPNAME_COLUMN, GROUPICON_COLUMN))
        groupPositions = populateGroupCursor(requireContext(), groupCursor)
        groupCursor.moveToFirst()

        // adapter
        Log.d(TAG, "Make adapter")
        return object : SimpleCursorTreeAdapter(requireContext(), groupCursor, R.layout.item_group_xselector, groupFrom, groupTo, R.layout.item_xselector, childFrom, childTo) {
            override fun getChildrenCursor(groupCursor: Cursor): Cursor? {
                // given the group, return a cursor for all the children within that group
                val groupName = groupCursor.getString(groupCursor.getColumnIndexOrThrow(GROUPNAME_COLUMN))
                Log.d(TAG, "getChildrenCursor(cursor) groupName= $groupName")

                // get cursor
                val cursor: Cursor? = when (groupName) {
                    "wordnet" -> wnFromWordIdModel!!.getData().getValue()
                    "verbnet" -> vnFromWordIdModel!!.getData().getValue()
                    "propbank" -> pbFromWordIdModel!!.getData().getValue()
                    "framenet" -> fnFromWordIdModel!!.getData().getValue()
                    else -> throw IllegalArgumentException("Illegal group name $groupName")
                }
                if (cursor != null && !cursor.isClosed) {
                    return cursor
                }

                // load
                val groupId = groupCursor.getInt(groupCursor.getColumnIndexOrThrow(GROUPID_COLUMN))
                Log.d(TAG, "getChildrenCursor(cursor) groupId=$groupId")
                startChildLoader(groupId)
                return null // set later when loader completes
            }

            override fun onGroupCollapsed(groupPosition: Int) {
                // super.onGroupCollapsed(groupPosition)
                // prevent from deactivating cursor
            }

            override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
                return true
            }

            override fun setViewText(v: TextView, text: String?) {
                /*
				int id = v.getId()
				if (R.id.xpronunciation == id)
				{
					Log.d(TAG, "Text: " + text)
				}
				 */
                if (text.isNullOrEmpty()) {
                    v.visibility = View.GONE
                } else {
                    v.visibility = View.VISIBLE
                    super.setViewText(v, text)
                }
            }

            override fun setViewImage(v: ImageView, value: String) {
                val id = v.id
                if (R.id.xsources == id) {
                    val fields = value.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    for (field in fields) {
                        when (field) {
                            "wn" -> {
                                v.setImageResource(R.drawable.wordnet)
                                return
                            }

                            "vn" -> {
                                v.setImageResource(R.drawable.verbnet)
                                return
                            }

                            "pb" -> {
                                v.setImageResource(R.drawable.propbank)
                                return
                            }

                            "fn" -> {
                                v.setImageResource(R.drawable.framenet)
                                return
                            }
                        }
                    }
                    v.setImageDrawable(null)
                    // v.setVisibility(View.GONE);
                } else if (R.id.pm == id) {
                    val fields2 = value.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    for (field2 in fields2) {
                        if (field2.startsWith("pm")) {
                            v.setImageResource(R.drawable.predicatematrix)
                            v.visibility = View.VISIBLE
                            return
                        }
                    }
                    v.setImageDrawable(null)
                    v.visibility = View.GONE
                } else {
                    super.setViewImage(v, value)
                }
            }
        }
    }

    /**
     * Initialize groups
     */
    private fun initializeGroups() {
        // expand (triggers data loading)
        Log.d(TAG, "expand group $GROUP_POSITION_INITIAL $this")
        expand(GROUP_POSITION_INITIAL)
    }

    /**
     * Restore groups
     */
    private fun restoreGroups(groupState: Int?) {
        if (groupState == null || listAdapter == null) {
            return
        }
        // var handler = new Handler(Looper.getMainLooper());
        Log.d(TAG, "Restore saved position " + Integer.toHexString(groupState) + " " + this)
        val groupCount = listAdapter!!.groupCount
        for (i in 0 until groupCount) {
            if (groupState and (1 shl i) != 0) {
                expand(i)
            }
        }
    }

    // V I E W M O D E L S

    /**
     * Make view models
     */
    private fun makeModels() {
        Log.d(TAG, "Make models")
        val owner = getViewLifecycleOwner()
        wordIdFromWordModel = ViewModelProvider(this)["xselectors.wordid(word)", SqlunetViewModel::class.java]
        wordIdFromWordModel!!.getData().observe(owner) { cursor: Cursor? ->
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            wordIdFromWordModel!!.getData().removeObservers(this)
            val adapter = makeAdapter()
            listAdapter = adapter
            if (restoredGroupState != null) {
                restoreGroups(restoredGroupState)
            } else {
                initializeGroups()
            }
        }
        wnFromWordIdModel = ViewModelProvider(this)["xselectors.wn(wordid)", SqlunetViewModel::class.java]
        wnFromWordIdModel!!.getData().observe(owner) { cursor: Cursor? ->
            if (cursor != null && wnFromWordIdModel!!.getData().hasActiveObservers()) {
                // CursorDump.dumpXCursor(cursor)

                // pass on to list adapter
                val adapter = listAdapter as CursorTreeAdapter?
                if (adapter != null && groupPositions[GROUPINDEX_WORDNET] != AdapterView.INVALID_POSITION) {
                    adapter.setChildrenCursor(groupPositions[GROUPINDEX_WORDNET], cursor)
                }
            } else {
                Log.i(TAG, "WN none")
            }
        }
        vnFromWordIdModel = ViewModelProvider(this)["xselectors.vn(wordid)", SqlunetViewModel::class.java]
        vnFromWordIdModel!!.getData().observe(owner) { cursor: Cursor? ->
            if (cursor != null && vnFromWordIdModel!!.getData().hasActiveObservers()) {
                // CursorDump.dumpXCursor(cursor);

                // pass on to list adapter
                val adapter = listAdapter as CursorTreeAdapter?
                if (adapter != null && groupPositions[GROUPINDEX_VERBNET] != AdapterView.INVALID_POSITION) {
                    adapter.setChildrenCursor(groupPositions[GROUPINDEX_VERBNET], cursor)
                }
            } else {
                Log.i(TAG, "VN none")
            }
        }
        pbFromWordIdModel = ViewModelProvider(this)["xselectors.pb(wordid)", SqlunetViewModel::class.java]
        pbFromWordIdModel!!.getData().observe(owner) { cursor: Cursor? ->
            if (cursor != null && pbFromWordIdModel!!.getData().hasActiveObservers()) {
                // CursorDump.dumpXCursor(cursor);

                // pass on to list adapter
                val adapter = listAdapter as CursorTreeAdapter?
                if (adapter != null && groupPositions[GROUPINDEX_PROPBANK] != AdapterView.INVALID_POSITION) {
                    adapter.setChildrenCursor(groupPositions[GROUPINDEX_PROPBANK], cursor)
                }
            } else {
                Log.i(TAG, "PB none")
            }
        }
        fnFromWordIdModel = ViewModelProvider(this)["xselectors.fn(wordid)", SqlunetViewModel::class.java]
        fnFromWordIdModel!!.getData().observe(owner) { cursor: Cursor? ->
            if (cursor != null && fnFromWordIdModel!!.getData().hasActiveObservers()) {
                // CursorDump.dumpXCursor(cursor);

                // pass on to list adapter
                val adapter = listAdapter as CursorTreeAdapter?
                if (adapter != null && groupPositions[GROUPINDEX_FRAMENET] != AdapterView.INVALID_POSITION) {
                    adapter.setChildrenCursor(groupPositions[GROUPINDEX_FRAMENET], cursor)
                }
            } else {
                Log.i(TAG, "FN none")
            }
        }
    }

    // L O A D

    /**
     * Load id from word
     */
    private fun load() {
        // load the contents
        val sql = prepareWordPronunciationXSelect(word!!)
        val uri = XSqlUNetProvider.makeUri(sql.providerUri).toUri()
        wordIdFromWordModel!!.loadData(uri, sql) { cursor: Cursor -> wordIdFromWordPostProcess(cursor) }
    }

    /**
     * Post processing, extraction of wordid from cursor
     *
     * @param cursor cursor
     */
    private fun wordIdFromWordPostProcess(cursor: Cursor) {
        if (cursor.moveToFirst()) {
            val idWordId = cursor.getColumnIndex(Words_FnWords_PbWords_VnWords.WORDID)
            wordId = cursor.getLong(idWordId)
        }
    }

    /**
     * Start child loader for
     *
     * @param groupId group id
     */
    private fun startChildLoader(groupId: Int) {
        Log.d(TAG, "Invoking startChildLoader() for groupId=$groupId")
        when (groupId) {
            GROUPID_WORDNET -> {

                //	var<Cursor> wnLiveData = this.wnFromWordIdModel.getMutableData();
                //	var wnCursor = wnLiveData.getValue();
                //	if (wnCursor != null && !wnCursor.isClosed())
                //	{
                //		wnLiveData.setValue(wnCursor);
                //	}
                //	else
                loadWn(wordId)
            }

            GROUPID_VERBNET -> {

                //	var<Cursor> vnLiveData = this.vnFromWordIdModel.getMutableData();
                //	var vnCursor = vnLiveData.getValue();
                //	if (vnCursor != null && !vnCursor.isClosed())
                //	{
                //		vnLiveData.setValue(vnCursor);
                //	}
                //	else
                loadVn(wordId)
            }

            GROUPID_PROPBANK -> {

                //				var<Cursor> pbLiveData = this.pbFromWordIdModel.getMutableData();
                //				var pbCursor = pbLiveData.getValue();
                //				if (pbCursor != null && !pbCursor.isClosed())
                //				{
                //					pbLiveData.setValue(pbCursor);
                //				}
                //				else
                loadPb(wordId)
            }

            GROUPID_FRAMENET -> {

                //				var<Cursor> fnLiveData = this.fnFromWordIdModel.getMutableData();
                //				var fnCursor = fnLiveData.getValue();
                //				if (fnCursor != null && !fnCursor.isClosed())
                //				{
                //					fnLiveData.setValue(fnCursor);
                //				}
                //				else
                loadFn(wordId)
            }

            else -> {}
        }
    }

    /**
     * Load WordNet data
     *
     * @param wordId word id
     */
    private fun loadWn(wordId: Long) {
        Log.d(TAG, "loadWn $wordId")
        val sql = prepareWnPronunciationXSelect(wordId)
        val uri = WordNetProvider.makeUri(sql.providerUri).toUri()
        wnFromWordIdModel!!.loadData(uri, sql, null)
    }

    /**
     * Load VerbNet data
     *
     * @param wordId word id
     */
    private fun loadVn(wordId: Long) {
        Log.d(TAG, "loadVn $wordId")
        val sql = prepareVnXSelect(wordId)
        val uri = XSqlUNetProvider.makeUri(sql.providerUri).toUri()
        vnFromWordIdModel!!.loadData(uri, sql, null)
    }

    /**
     * Load PropBank data
     *
     * @param wordId word id
     */
    private fun loadPb(wordId: Long) {
        Log.d(TAG, "loadPb $wordId")
        val sql = preparePbXSelect(wordId)
        val uri = XSqlUNetProvider.makeUri(sql.providerUri).toUri()
        pbFromWordIdModel!!.loadData(uri, sql, null)
    }

    /**
     * Load FrameNet data
     *
     * @param wordId word id
     */
    private fun loadFn(wordId: Long) {
        Log.d(TAG, "loadFn $wordId")
        val sql = prepareFnXSelect(wordId)
        val uri = XSqlUNetProvider.makeUri(sql.providerUri).toUri()
        fnFromWordIdModel!!.loadData(uri, sql, null)
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

    override fun onChildClick(parent: ExpandableListView, v: View, groupPosition: Int, childPosition: Int, id: Long): Boolean {
        super.onChildClick(parent, v, groupPosition, childPosition, id)
        if (listener != null) {
            //Log.d(TAG, "Click: group=" + groupPosition + " child=" + childPosition + " id=" + id);
            val index = parent.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition))
            parent.setItemChecked(index, true)
            val adapter = (listAdapter as CursorTreeAdapter?)!!
            val cursor = adapter.getChild(groupPosition, childPosition)
            if (!cursor.isAfterLast) {
                // column indices
                val idSynsetId = cursor.getColumnIndex(Words_XNet_U.SYNSETID)
                val idXId = cursor.getColumnIndex(Words_XNet_U.XID)
                val idXClassId = cursor.getColumnIndex(Words_XNet_U.XCLASSID)
                val idXMemberId = cursor.getColumnIndex(Words_XNet_U.XMEMBERID)
                val idXPronunciationId = cursor.getColumnIndex(Words_XNet_U.XPRONUNCIATION)
                val idXSources = cursor.getColumnIndex(Words_XNet_U.SOURCES)
                // var idWordId = cursor.getColumnIndex(Words_XNet_U.WORDID);

                // data
                val wordId = wordId
                val word = word!!
                val cased = if (this.word == this.word!!.lowercase()) null else this.word
                val pronunciation = if (idXPronunciationId == -1) null else cursor.getString(idXPronunciationId)
                val synsetId = if (cursor.isNull(idSynsetId)) 0 else cursor.getLong(idSynsetId)
                val pos = synsetIdToPos(synsetId)
                val xId = if (cursor.isNull(idXId)) 0 else cursor.getLong(idXId)
                val xClassId = if (cursor.isNull(idXClassId)) 0 else cursor.getLong(idXClassId)
                val xMemberId = if (cursor.isNull(idXMemberId)) 0 else cursor.getLong(idXMemberId)
                val xSources = cursor.getString(idXSources)
                val xMask = getMask(xSources)
                if (groupPosition == AdapterView.INVALID_POSITION) {
                    return false
                }
                var groupId = -1
                when (groupPosition) {
                    groupPositions[GROUPINDEX_WORDNET] -> {
                        groupId = GROUPID_WORDNET
                    }

                    groupPositions[GROUPINDEX_VERBNET] -> {
                        groupId = GROUPID_VERBNET
                    }

                    groupPositions[GROUPINDEX_PROPBANK] -> {
                        groupId = GROUPID_PROPBANK
                    }

                    groupPositions[GROUPINDEX_FRAMENET] -> {
                        groupId = GROUPID_FRAMENET
                    }
                }

                // pointer
                val pointer = XSelectorPointer(synsetId, wordId, xId, xClassId, xMemberId, xSources, xMask, groupId)
                Log.d(TAG, "pointer=$pointer")

                // notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
                listener!!.onItemSelected(pointer, word, cased, pronunciation, pos)
            }
            // cursor ownership is transferred  to adapter, so do not call
            // cursor.close();
        }
        return true
    }

    // E X P A N D

    /**
     * Expand all
     */
    private fun expandAll() {
        val expandableListView = expandableListView
        val adapter = listAdapter
        if (expandableListView == null || adapter == null) {
            return
        }
        val count = adapter.groupCount
        for (position in 0 until count) {
            expandableListView.expandGroup(position)
        }
    }

    // H E L P E R

    /**
     * Extract pos from synset id number
     *
     * @param synsetId synset id
     * @return pos
     */
    private fun synsetIdToPos(synsetId: Long?): String? {
        if (synsetId == null) {
            return null
        }
        val p = floor((synsetId / 100000000f).toDouble()).toInt()
        return when (p) {
            1 -> "n"
            2 -> "v"
            3 -> "a"
            4 -> "r"
            else -> null
        }
    }

    companion object {

        private const val TAG = "XSelectorsF"

        /**
         * The (saved instance state) key representing the groups state.
         */
        private const val STATE_GROUPS = "groups_state"

        /**
         * id column
         */
        private const val GROUPID_COLUMN = "_id"

        /**
         * Name column
         */
        private const val GROUPNAME_COLUMN = "xn"

        /**
         * Icon column
         */
        private const val GROUPICON_COLUMN = "xicon"

        /**
         * First expanded group
         */
        const val GROUP_POSITION_INITIAL = 0

        /**
         * WordNet position index
         */
        const val GROUPINDEX_WORDNET = 0

        /**
         * VerbNet position index
         */
        const val GROUPINDEX_VERBNET = 1

        /**
         * Propbank position index
         */
        const val GROUPINDEX_PROPBANK = 2

        /**
         * FrameNet position index
         */
        const val GROUPINDEX_FRAMENET = 3

        /**
         * WordNet id for loader
         */
        const val GROUPID_WORDNET = 1

        /**
         * VerbNet id for loader
         */
        const val GROUPID_VERBNET = 2

        /**
         * Propbank id for loader
         */
        const val GROUPID_PROPBANK = 3

        /**
         * FrameNet id for loader
         */
        const val GROUPID_FRAMENET = 4

        /**
         * Source fields for groups
         */
        private val groupFrom = arrayOf(GROUPNAME_COLUMN, GROUPICON_COLUMN)

        /**
         * Target resource for groups
         */
        private val groupTo = intArrayOf(R.id.xn, R.id.xicon)

        /**
         * Source fields
         */
        private val childTo = intArrayOf(
            R.id.wordid,
            R.id.synsetid,
            R.id.xid,
            R.id.xmemberid,
            R.id.xname,
            R.id.xheader,
            R.id.xinfo,
            R.id.xpronunciation,
            R.id.xdefinition,
            R.id.xsourcestext,
            R.id.xsources,
            R.id.pm
        )

        /**
         * Target resource
         */
        private val childFrom = arrayOf(
            Words_XNet_U.WORDID,
            Words_XNet_U.SYNSETID,
            Words_XNet_U.XID,
            Words_XNet_U.XMEMBERID,
            Words_XNet_U.XNAME,
            Words_XNet_U.XHEADER,
            Words_XNet_U.XINFO,
            Words_XNet_U.XPRONUNCIATION,
            Words_XNet_U.XDEFINITION,
            Words_XNet_U.SOURCES,
            Words_XNet_U.SOURCES,
            Words_XNet_U.SOURCES
        )

        /**
         * Populate group cursor. Requires context to be available
         */
        private fun populateGroupCursor(context: Context, cursor: MatrixCursor): IntArray {
            // fill groups
            var position = 0
            val enable = XnSettings.getAllPref(context)
            val groupPositions = intArrayOf(AdapterView.INVALID_POSITION, AdapterView.INVALID_POSITION, AdapterView.INVALID_POSITION, AdapterView.INVALID_POSITION)
            if (XnSettings.Source.WORDNET.test(enable)) {
                groupPositions[GROUPINDEX_WORDNET] = position++
                cursor.addRow(arrayOf<Any>(GROUPID_WORDNET, "wordnet", R.drawable.wordnet.toString()))
            }
            if (XnSettings.Source.VERBNET.test(enable)) {
                groupPositions[GROUPINDEX_VERBNET] = position++
                cursor.addRow(arrayOf<Any>(GROUPID_VERBNET, "verbnet", R.drawable.verbnet.toString()))
            }
            if (XnSettings.Source.PROPBANK.test(enable)) {
                groupPositions[GROUPINDEX_PROPBANK] = position++
                cursor.addRow(arrayOf<Any>(GROUPID_PROPBANK, "propbank", R.drawable.propbank.toString()))
            }
            if (XnSettings.Source.FRAMENET.test(enable)) {
                groupPositions[GROUPINDEX_FRAMENET] = position
                cursor.addRow(arrayOf<Any>(GROUPID_FRAMENET, "framenet", R.drawable.framenet.toString()))
            }
            // increment position if anything added
            return groupPositions
        }
    }
}