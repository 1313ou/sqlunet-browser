/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.vn.xselector

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
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
import android.widget.SimpleCursorTreeAdapter
import androidx.lifecycle.ViewModelProvider
import org.sqlunet.browser.BaseSelectorsExpandableListFragment
import org.sqlunet.browser.SqlunetViewModel
import org.sqlunet.browser.vn.R
import org.sqlunet.browser.vn.Settings
import org.sqlunet.browser.vn.Settings.getAllPref
import org.sqlunet.browser.vn.xselector.XSelectorPointer.CREATOR.getMask
import org.sqlunet.loaders.Queries.preparePbSelectVn
import org.sqlunet.loaders.Queries.prepareVnXSelectVn
import org.sqlunet.loaders.Queries.prepareWordXSelectVn
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.provider.XNetContract.Words_PbWords_VnWords
import org.sqlunet.provider.XNetContract.Words_XNet
import org.sqlunet.provider.XSqlUNetProvider.Companion.makeUri
import kotlin.math.floor

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
        fun onItemSelected(pointer: XSelectorPointer?, word: String?, cased: String?, pronunciation: String?, pos: String?)
    }

    /**
     * Activate on click flag
     */
    private var activateOnItemClick = true

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
     * VerbNet model
     */
    private var vnFromWordIdModel: SqlunetViewModel? = null

    /**
     * PropBank model
     */
    private var pbFromWordIdModel: SqlunetViewModel? = null

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
        listView!!.setChoiceMode(if (activateOnItemClick) AbsListView.CHOICE_MODE_SINGLE else AbsListView.CHOICE_MODE_NONE)

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
        //		idLiveData.setValue(idCursor)
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
                    "verbnet" -> vnFromWordIdModel!!.getData().getValue()
                    "propbank" -> pbFromWordIdModel!!.getData().getValue()
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

            override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View, parent: ViewGroup): View? {
                this.cursor ?: return null
                return super.getGroupView(groupPosition, isExpanded, convertView, parent)
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
        // var handler = new Handler(Looper.getMainLooper())
        Log.d(TAG, "Restore saved position " + Integer.toHexString(groupState) + " " + this)
        val groupCount = listAdapter!!.groupCount
        for (i in 0 until groupCount) {
            if (groupState and (1 shl i) != 0) {
                expand(i)

                //int groupPosition = i
                //requireActivity().runOnUiThread(() -> expand(finalI))
                //handler.postDelayed(() -> expand(groupPosition), 1500)
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
        wordIdFromWordModel = ViewModelProvider(this)["vn:xselectors.wordid(word)", SqlunetViewModel::class.java]
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
        vnFromWordIdModel = ViewModelProvider(this)["vn:xselectors.vn(wordid)", SqlunetViewModel::class.java]
        vnFromWordIdModel!!.getData().observe(owner) { cursor: Cursor? ->
            if (cursor != null && vnFromWordIdModel!!.getData().hasActiveObservers()) {
                // CursorDump.dumpXCursor(cursor)

                // pass on to list adapter
                val adapter = listAdapter as CursorTreeAdapter?
                if (adapter != null && groupPositions[GROUPINDEX_VERBNET] != AdapterView.INVALID_POSITION) {
                    adapter.setChildrenCursor(groupPositions[GROUPINDEX_VERBNET], cursor)
                }
            } else {
                Log.i(TAG, "VN none")
            }
        }
        pbFromWordIdModel = ViewModelProvider(this)["vn:xselectors.pb(wordid)", SqlunetViewModel::class.java]
        pbFromWordIdModel!!.getData().observe(owner) { cursor: Cursor? ->
            if (cursor != null && pbFromWordIdModel!!.getData().hasActiveObservers()) {
                // CursorDump.dumpXCursor(cursor)

                // pass on to list adapter
                val adapter = listAdapter as CursorTreeAdapter?
                if (adapter != null && groupPositions[GROUPINDEX_PROPBANK] != AdapterView.INVALID_POSITION) {
                    adapter.setChildrenCursor(groupPositions[GROUPINDEX_PROPBANK], cursor)
                }
            } else {
                Log.i(TAG, "PB none")
            }
        }
    }

    // L O A D

    /**
     * Load id from word
     */
    private fun load() {
        // load the contents
        val sql = prepareWordXSelectVn(word!!)
        val uri = Uri.parse(makeUri(sql.providerUri))
        wordIdFromWordModel!!.loadData(uri, sql) { cursor: Cursor -> wordIdFromWordPostProcess(cursor) }
    }

    /**
     * Post processing, extraction of wordid from cursor
     * Closes cursor because it's no longer needed.
     *
     * @param cursor cursor
     */
    private fun wordIdFromWordPostProcess(cursor: Cursor) {
        if (cursor.moveToFirst()) {
            val idWordId = cursor.getColumnIndex(Words_PbWords_VnWords.WORDID)
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
            GROUPID_VERBNET -> {

                //	var<Cursor> vnLiveData = this.vnFromWordIdModel.getMutableData()
                //	var vnCursor = vnLiveData.getValue()
                //	if (vnCursor != null && !vnCursor.isClosed())
                //	{
                //		vnLiveData.setValue(vnCursor)
                //	}
                //	else
                loadVn(wordId)
            }

            GROUPID_PROPBANK -> {

                //	var<Cursor> pbLiveData = this.pbFromWordIdModel.getMutableData()
                //	var pbCursor = pbLiveData.getValue()
                //	if (pbCursor != null && !pbCursor.isClosed())
                //	{
                //		pbLiveData.setValue(pbCursor)
                //	}
                //	else
                loadPb(wordId)
            }

            else -> {}
        }
    }

    /**
     * Load VerbNet data
     *
     * @param wordId word id
     */
    private fun loadVn(wordId: Long) {
        val sql = prepareVnXSelectVn(wordId)
        val uri = Uri.parse(makeUri(sql.providerUri))
        vnFromWordIdModel!!.loadData(uri, sql, null)
    }

    /**
     * Load PropBank data
     *
     * @param wordId word id
     */
    private fun loadPb(wordId: Long) {
        val sql = preparePbSelectVn(wordId)
        val uri = Uri.parse(makeUri(sql.providerUri))
        pbFromWordIdModel!!.loadData(uri, sql, null)
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

    override fun onChildClick(parent: ExpandableListView, v: View, groupPosition: Int, childPosition: Int, id: Long): Boolean {
        super.onChildClick(parent, v, groupPosition, childPosition, id)
        if (listener != null) {
            //Log.d(TAG, "Click: group=" + groupPosition + " child=" + childPosition + " id=" + id)
            val index = parent.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition))
            parent.setItemChecked(index, true)
            val adapter = (listAdapter as CursorTreeAdapter?)!!
            val cursor = adapter.getChild(groupPosition, childPosition)
            if (!cursor.isAfterLast) {
                // column indices
                val idSynsetId = cursor.getColumnIndex(Words_XNet.SYNSETID)
                val idXId = cursor.getColumnIndex(Words_XNet.XID)
                val idXClassId = cursor.getColumnIndex(Words_XNet.XCLASSID)
                val idXMemberId = cursor.getColumnIndex(Words_XNet.XMEMBERID)
                val idXSources = cursor.getColumnIndex(Words_XNet.SOURCES)
                // var idWordId = cursor.getColumnIndex(Words_XNet.WORDID)

                // data
                val wordId = wordId
                val word = word!!
                val cased = if (this.word == this.word!!.lowercase()) null else this.word
                val pronunciation: String? = null
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
                if (groupPosition == groupPositions[GROUPINDEX_VERBNET]) {
                    groupId = GROUPID_VERBNET
                } else if (groupPosition == groupPositions[GROUPINDEX_PROPBANK]) {
                    groupId = GROUPID_PROPBANK
                }

                // pointer
                val pointer = XSelectorPointer(synsetId, wordId, xId, xClassId, xMemberId, xSources, xMask, groupId)
                Log.d(TAG, "pointer=$pointer")

                // notify the active listener (the activity, if the fragment is attached to one) that an item has been selected
                listener!!.onItemSelected(pointer, word, cased, pronunciation, pos)
            }
            // cursor ownership is transferred  to adapter, so do not call
            // cursor.close()
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
         * VerbNet position index
         */
        const val GROUPINDEX_VERBNET = 0

        /**
         * Propbank position index
         */
        const val GROUPINDEX_PROPBANK = 1

        /**
         * VerbNet id for loader
         */
        const val GROUPID_VERBNET = 2

        /**
         * Propbank id for loader
         */
        const val GROUPID_PROPBANK = 3

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
            R.id.xdefinition
        )

        /**
         * Target resource
         */
        private val childFrom = arrayOf(
            Words_XNet.WORDID,
            Words_XNet.SYNSETID,
            Words_XNet.XID,
            Words_XNet.XMEMBERID,
            Words_XNet.XNAME,
            Words_XNet.XHEADER,
            Words_XNet.XINFO,
            Words_XNet.XDEFINITION
        )

        /**
         * Populate group cursor. Requires context to be available
         */
        private fun populateGroupCursor(context: Context, cursor: MatrixCursor): IntArray {
            // fill groups
            var position = 0
            val enable = getAllPref(context)
            val groupPositions = intArrayOf(AdapterView.INVALID_POSITION, AdapterView.INVALID_POSITION)
            if (Settings.Source.VERBNET.test(enable)) {
                groupPositions[GROUPINDEX_VERBNET] = position++
                cursor.addRow(arrayOf<Any>(GROUPID_VERBNET, "verbnet", R.drawable.verbnet.toString()))
            }
            if (Settings.Source.PROPBANK.test(enable)) {
                groupPositions[GROUPINDEX_PROPBANK] = position
                cursor.addRow(arrayOf<Any>(GROUPID_PROPBANK, "propbank", R.drawable.propbank.toString()))
            }
            // position++ if more
            return groupPositions
        }
    }
}