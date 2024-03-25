/*
 * Copyright (c) 2023. Bernard Bou
 */
package androidx.app.local

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.local.app.R
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.ExpandableListView.OnChildClickListener
import android.widget.ExpandableListView.OnGroupCollapseListener
import android.widget.ExpandableListView.OnGroupExpandListener
import android.widget.TextView
import androidx.fragment.app.Fragment

open class ExpandableListFragment : Fragment(), OnChildClickListener, OnGroupCollapseListener, OnGroupExpandListener {

    /**
     * ExpandableListAdapter associated with this activity's ExpandableListView.
     */
    private var expandableListAdapter: ExpandableListAdapter? = null

    /**
     * List view
     */
    private var mExpandableList: ExpandableListView? = null

    /**
     * Click listener
     */
    private val mOnClickListener = OnItemClickListener { parent: AdapterView<*>?, v: View?, position: Int, id: Long -> onListItemClick(parent as ExpandableListView?, v, position, id) }

    /**
     * Focus handler (receives posted focus request)
     */
    private val mFocusHandler = Handler(Looper.getMainLooper())

    /**
     * Focus request
     */
    private val mRequestFocus = Runnable {
        mExpandableList!!.focusableViewAvailable(mExpandableList)
    }

    /**
     * Custom empty view
     */
    private var mEmptyView: View? = null

    /**
     * Standard empty view
     */
    private var mStandardEmptyView: TextView? = null

    /**
     * Text for standard empty view
     */
    private var mEmptyText: CharSequence? = null
    private var mProgressContainer: View? = null
    private var mExpandableListContainer: View? = null
    private var mExpandableListShown = false

    @SuppressLint("CutPasteId")
    private fun ensureList() {
        if (mExpandableList != null) {
            return
        }
        val view = view ?: throw IllegalStateException("Content view not yet created")
        if (view is ExpandableListView) {
            mExpandableList = view
        } else {
            // empty view
            mStandardEmptyView = view.findViewById(R.id.empty)
            if (mStandardEmptyView == null) {
                // there is no standard empty view so there must be a custom one
                mEmptyView = view.findViewById(android.R.id.empty)
            } else {
                mStandardEmptyView!!.visibility = View.GONE
            }

            // containers
            mProgressContainer = view.findViewById(R.id.container_progress)
            mExpandableListContainer = view.findViewById(R.id.container_list)

            // expandable list
            val rawExpandableListView = view.findViewById<View>(android.R.id.list)
            if (rawExpandableListView !is ExpandableListView) {
                if (rawExpandableListView == null) {
                    throw RuntimeException("Your content must have a ListView whose id attribute is 'android.R.id.list'")
                }
                throw RuntimeException("Content has view with id attribute 'android.R.id.list' that is not a ListView class")
            }
            mExpandableList = rawExpandableListView

            // empty view
            if (mEmptyView != null) {
                // custom empty view
                mExpandableList!!.setEmptyView(mEmptyView)
            } else if (mEmptyText != null) {
                // standard empty text view
                mStandardEmptyView!!.text = mEmptyText
                mExpandableList!!.setEmptyView(mStandardEmptyView)
            }
        }
        mExpandableListShown = true

        // listeners
        mExpandableList!!.onItemClickListener = mOnClickListener
        mExpandableList!!.setOnChildClickListener(this)
        mExpandableList!!.setOnGroupExpandListener(this)
        mExpandableList!!.setOnGroupCollapseListener(this)

        // adapter
        if (expandableListAdapter != null) {
            val adapter = expandableListAdapter
            expandableListAdapter = null
            listAdapter = adapter
        } else {
            // starting without an adapter, so assume we won't have our data right away and start with the progress indicator.
            if (mProgressContainer != null) {
                setListShown(shown = false, animate = false)
            }
        }

        // request focus
        mFocusHandler.post(mRequestFocus)
    }
    // L I F E C Y C L E   E V E N T S
    /**
     * Provide default implementation to return a simple list view. Subclasses can override to replace with their own layout. If doing so, the returned view
     * hierarchy *must* have a ListView whose id is [android.R.id.list] and can optionally have a sibling view id
     * [android.R.id.empty] that is to be shown when the list is empty.
     *
     *
     * If you are overriding this method with your own custom content, consider including the standard layout [android.R.layout.list_content] in your
     * layout file, so that you continue to retain all of the standard behavior of ListFragment. In particular, this is currently the only way to have the
     * built-in indeterminant progress state be shown.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.expandable_list_fragment, container, false)
    }

    /**
     * Attach to list view once the view hierarchy has been created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ensureList()
    }

    /**
     * Detach from list view.
     */
    override fun onDestroyView() {
        mFocusHandler.removeCallbacks(mRequestFocus)
        mExpandableList = null
        mExpandableListShown = false
        mEmptyView = null
        mStandardEmptyView = null
        mProgressContainer = null
        mExpandableListContainer = null
        super.onDestroyView()
    }

    // E X P A N D A B L E   L I S T   V I E W

    /**
     * List view
     */
    protected val listView: ExpandableListView?
        get() = expandableListView

    /**
     * Expandable list view
     */
    protected val expandableListView: ExpandableListView?
        get() {
            try {
                ensureList()
            } catch (ise: IllegalStateException) {
                return null
            }
            return mExpandableList
        }

    /**
     * Set the currently selected list item to the specified position with the adapter's data
     *
     * @param position position
     */
    fun setSelection(position: Int) {
        ensureList()
        mExpandableList!!.setSelection(position)
    }

    /**
     * Position of the currently selected list item.
     */
    val selectedItemPosition: Int
        get() {
            ensureList()
            return mExpandableList!!.selectedItemPosition
        }

    /**
     * Position (in packed position representation) of the currently selected group or child. Use [ExpandableListView.getPackedPositionType],
     * [ExpandableListView.getPackedPositionGroup], and [ExpandableListView.getPackedPositionChild] to unpack the returned packed position.
     */
    val selectedPosition: Long
        get() {
            return mExpandableList!!.getSelectedPosition()
        }

    /**
     * Cursor row ID of the currently selected list item.
     */
    val selectedItemId: Long
        get() {
            ensureList()
            return mExpandableList!!.selectedItemId
        }

    /**
     * ID of the currently selected group or child.
     */
    val selectedId: Long
        get() {
            return mExpandableList!!.getSelectedId()
        }

    /**
     * Sets the selection to the specified group.
     *
     * @param groupPosition The position of the group that should be selected.
     */
    fun setSelectedGroup(groupPosition: Int) {
        mExpandableList!!.setSelectedGroup(groupPosition)
    }

    /**
     * Sets the selection to the specified child. If the child is in a collapsed group, the group will only be expanded and child subsequently selected if
     * shouldExpandGroup is set to true, otherwise the method will return false.
     *
     * @param groupPosition     The position of the group that contains the child.
     * @param childPosition     The position of the child within the group.
     * @param shouldExpandGroup Whether the child's group should be expanded if it is collapsed.
     * @return Whether the selection was successfully set on the child.
     */
    fun setSelectedChild(groupPosition: Int, childPosition: Int, shouldExpandGroup: Boolean): Boolean {
        return mExpandableList!!.setSelectedChild(groupPosition, childPosition, shouldExpandGroup)
    }

    // A D A P T E R

    /**
     * ListAdapter associated with this activity's ListView.
     */
    protected var listAdapter: ExpandableListAdapter?
        get() = expandableListAdapter
        /**
         * Provide the adapter for the list view.
         */
        protected set(adapter) {
            synchronized(this) {
                val hadAdapter = expandableListAdapter != null
                expandableListAdapter = adapter
                if (mExpandableList != null) {
                    mExpandableList!!.setAdapter(adapter)
                    if (!mExpandableListShown && !hadAdapter) {
                        // The list was hidden, and previously didn't have an
                        // adapter. It is now time to show it.
                        val view = requireView()
                        setListShown(true, view.windowToken != null)
                    }
                }
            }
        }

    // D I S P L A Y

    /**
     * The default content for a ListFragment has a TextView that can be shown when the list is empty. If you would like to have it shown, call this method to
     * supply the text it should use.
     */
    fun setEmptyText(text: CharSequence?) {
        ensureList()
        checkNotNull(mStandardEmptyView) { "Can't be used with a custom content view" }
        mStandardEmptyView!!.text = text
        if (mEmptyText == null) {
            mExpandableList!!.setEmptyView(mStandardEmptyView)
        }
        mEmptyText = text
    }

    /**
     * Control whether the list is being displayed. You can make it not displayed if you are waiting for the initial data to show in it. During this time an
     * indeterminant progress indicator will be shown instead.
     *
     *
     * Applications do not normally need to use this themselves. The default behavior of ListFragment is to start with the list not being shown, only showing it
     * once an adapter is given with setListAdapter(ListAdapter). If the list at that point had not been shown, when it does get shown it will be do
     * without the user ever seeing the hidden state.
     *
     * @param shown If true, the list view is shown; if false, the progress indicator. The initial value is true.
     */
    fun setListShown(shown: Boolean) {
        setListShown(shown, true)
    }

    /**
     * Like [.setListShown], but no animation is used when transitioning from the previous state.
     */
    fun setListShownNoAnimation(shown: Boolean) {
        setListShown(shown, false)
    }

    /**
     * Control whether the list is being displayed. You can make it not displayed if you are waiting for the initial data to show in it. During this time an
     * indeterminant progress indicator will be shown instead.
     *
     * @param shown   If true, the list view is shown; if false, the progress indicator. The initial value is true.
     * @param animate If true, an animation will be used to transition to the new state.
     */
    private fun setListShown(shown: Boolean, animate: Boolean) {
        ensureList()
        checkNotNull(mProgressContainer) { "Can't be used with a custom content view" }
        if (mExpandableListShown == shown) {
            return
        }
        mExpandableListShown = shown
        if (shown) {
            if (animate) {
                mProgressContainer!!.startAnimation(AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_out))
                mExpandableListContainer!!.startAnimation(AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in))
            } else {
                mProgressContainer!!.clearAnimation()
                mExpandableListContainer!!.clearAnimation()
            }
            mProgressContainer!!.visibility = View.GONE
            mExpandableListContainer!!.visibility = View.VISIBLE
        } else {
            if (animate) {
                mProgressContainer!!.startAnimation(AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in))
                mExpandableListContainer!!.startAnimation(AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_out))
            } else {
                mProgressContainer!!.clearAnimation()
                mExpandableListContainer!!.clearAnimation()
            }
            mProgressContainer!!.visibility = View.VISIBLE
            mExpandableListContainer!!.visibility = View.GONE
        }
    }

    // E X P A N D

    /**
     * Expand section
     */
    fun expand(groupPosition: Int) {
        val expandableListView = expandableListView!!
        expandableListView.expandGroup(groupPosition)
    }

    /**
     * Collapse section
     */
    fun collapse(groupPosition: Int) {
        val expandableListView = expandableListView!!
        expandableListView.collapseGroup(groupPosition)
    }

    // O V E R R I D A B L E S

    /**
     * This method will be called when an item in the list is selected. Subclasses should override. Subclasses can call
     * getListView().getItemAtPosition(position) if they need to access the data associated with the selected item.
     *
     * @param l        The ListView where the click happened
     * @param v        The view that was clicked within the ListView
     * @param position The position of the view in the list
     * @param id       The row id of the item that was clicked
     */
    protected fun onListItemClick(l: ExpandableListView?, v: View?, position: Int, id: Long) {
        //
    }

    /**
     * Override this for receiving callbacks when a child has been clicked.
     *
     *
     * {@inheritDoc}
     */
    override fun onChildClick(parent: ExpandableListView, v: View, groupPosition: Int, childPosition: Int, id: Long): Boolean {
        return false
    }

    /**
     * Override this for receiving callbacks when a group has been collapsed.
     */
    override fun onGroupCollapse(groupPosition: Int) {
        //
    }

    /**
     * Override this for receiving callbacks when a group has been expanded.
     */
    override fun onGroupExpand(groupPosition: Int) {
        //
    }

    /**
     * Override this to populate the context menu when an item is long pressed. menuInfo will contain an
     * [android.widget.ExpandableListView.ExpandableListContextMenuInfo] whose packedPosition is a packed position that should be used with
     * [ExpandableListView.getPackedPositionType] and the other similar methods.
     *
     *
     * {@inheritDoc}
     */
    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        //
    }
}
