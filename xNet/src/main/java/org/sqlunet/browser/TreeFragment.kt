/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import org.sqlunet.model.TreeFactory.makeTreeNode
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.treeview.control.RootController
import org.sqlunet.treeview.model.TreeNode
import org.sqlunet.treeview.settings.Settings
import org.sqlunet.treeview.view.TreeViewer

/**
 * A fragment representing a synset.
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class TreeFragment protected constructor() : Fragment() {

    /**
     * Tree model root
     */
    @JvmField
    protected val treeRoot: TreeNode = TreeNode(null, RootController())

    /**
     * Tree view
     */
    var treeViewer: TreeViewer? = null
        private set

    // Data
    @JvmField
    protected var layoutId = 0

    @JvmField
    protected var treeContainerId = 0

    @JvmField
    @StringRes
    protected var headerId = 0

    @JvmField
    @DrawableRes
    protected var iconId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // root node
        treeRoot.isSelectable = false

        // sub root node
        val header = resources.getString(headerId)
        makeTreeNode(header, iconId, false).addTo(treeRoot)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // view
        val view = inflater.inflate(layoutId, container, false)

        // tree viewer
        // Log.d(TAG, "Create tree");
        treeViewer = TreeViewer(requireContext(), treeRoot)

        // tree view
        val use2dScroll = scroll2D
        val treeview = treeViewer!!.makeTreeView(inflater, use2dScroll)

        // container
        val treeContainer = view.findViewById<ViewGroup>(treeContainerId)
        treeContainer.addView(treeview)
        return view
    }

    fun getPointer(args: Bundle): Parcelable {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) args.getParcelable(ProviderArgs.ARG_QUERYPOINTER, Parcelable::class.java)!! else args.getParcelable(ProviderArgs.ARG_QUERYPOINTER)!!
    }

    protected open val scroll2D: Boolean
        get() {
            val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
            return prefs.getBoolean(Settings.PREF_SCROLL_2D, false)
        }

    override fun onStart() {
        super.onStart()
        treeViewer!!.expandAll()
    }
}
