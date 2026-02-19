/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.treeview.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import androidx.annotation.StyleRes
import androidx.annotation.StyleableRes
import androidx.core.util.Pair
import androidx.preference.PreferenceManager
import org.sqlunet.treeview.R
import org.sqlunet.treeview.control.RootController
import org.sqlunet.treeview.model.TreeNode
import org.sqlunet.treeview.model.TreeNode.TreeNodeClickListener
import org.sqlunet.treeview.settings.Settings
import androidx.core.view.isNotEmpty
import androidx.core.content.withStyledAttributes

/* @formatter:off */ /*
NestedScrollView, wrapper bottom
	(HorizontalScrollView)  wrapper bottom
		LinearLayout id/tree_view
			SubtreeView
				RelativeLayout id/node_label
					LinearLayout
						AppCompatImageView id/node_junction
						AppCompatImageView id/node_icon
						AppCompatTextView id/node_value
				LinearLayout id/node_children
					SubtreeView
						RelativeLayout id/node_label
							TextView
						LinearLayout id/node_children
					SubtreeView
						RelativeLayout id/node_label
							TextView
						LinearLayout id/node_children
 */
 /* @formatter:on */
/**
 * Tree viewer, makes and manages tree view
 *
 * @author Bogdan Melnychuk on 2/10/15.
 * @author Bernard Bou
 */
class TreeViewer(
    private val context: Context,
    val root: TreeNode,
) {

    /**
     * (Top) tree view, immediately above root, a nested scroll view or horizontal scroll view
     */
    private var treeView: View? = null

    /**
     * Container style
     */
    private var containerStyle: Int

    /**
     * Container style applies to root
     */
    private var containerStyleAppliesToRoot: Boolean

    /**
     * Tree indent factor to apply to default value
     */
    private val treeIndent: Int

    /**
     * Tree row min height
     */
    private val treeRowMinHeight: Int

    /**
     * Node click listener
     */
    private var nodeClickListener: TreeNodeClickListener? = null

    /**
     * Selection mode enabled
     */
    private var selectable = false

    /**
     * Use default animation
     */
    private val useAnimation: Boolean

    init {
        containerStyle = R.style.TreeNodeStyleCustom // R.style.TreeNodeStyleDivided
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        useAnimation = prefs.getBoolean(Settings.PREF_USE_ANIMATION, true)
        val containerContext = ContextThemeWrapper(context, containerStyle)
        treeIndent = computeIndent(containerContext, Settings.getTreeIndent(prefs))
        treeRowMinHeight = computeRowMinHeight(containerContext, Settings.getTreeRowMinHeight(prefs))
        containerStyleAppliesToRoot = false
    }

    // V I E W

    /**
     * View factory
     *
     * @param use2dScroll horizontal and vertical scrolling
     * @return view
     */
    fun makeTreeView(inflater: LayoutInflater, use2dScroll: Boolean): View {
        // Log.d(TAG, "Make tree view")

        // top scrollview
        val wrapper = makeWrapper(inflater, use2dScroll)
        val containerView = wrapper.first
        val anchor = wrapper.second

        // context
        val containerContext = if (containerStyle != 0 && containerStyleAppliesToRoot) ContextThemeWrapper(context, containerStyle) else context

        // content
        val contentView = LinearLayout(containerContext, null, containerStyle)
        contentView.id = R.id.tree_view
        contentView.orientation = LinearLayout.VERTICAL
        contentView.isFocusable = true
        contentView.isFocusableInTouchMode = true
        contentView.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        contentView.visibility = View.GONE
        anchor.addView(contentView)

        // root
        val rootController = this.root.controller as RootController
        rootController.flagEnsureVisible()
        rootController.setContentView(contentView)

        // keep reference
        treeView = anchor
        return containerView
    }

    /**
     * Make wrapper
     * A hierarchy of views to wrap the tree node, usually a single NestedScrollView or NestedScrollView|HorizontalScrollView, that
     * - offers the tree wrapped as a view for inclusion in a fragment (upwards) and
     * - offers an anchor point to tree content (downwards).
     *
     * @param inflater    inflater
     * @param use2dScroll whether to use 2D scrolling
     * @return pair consisting of the wrapped top view and bottom anchor group view; they may be identical
     */
    private fun makeWrapper(inflater: LayoutInflater, use2dScroll: Boolean): Pair<View, ViewGroup> {
        val top = inflater.inflate(if (use2dScroll) R.layout.layout_top_2d else R.layout.layout_top, null, false)
        val anchor = top.findViewById<ViewGroup>(R.id.tree_view_anchor)
        return Pair(top, anchor)
    }

    // M O D I F Y

    /**
     * Update
     * Update node view leaving children unchanged
     *
     * @param node node
     * @return updated view
     */
    fun update(node: TreeNode): View? {
        val controller = node.controller
        val subtreeView = controller.subtreeView
        val nodeView = controller.nodeView
        if (subtreeView != null && nodeView != null) {
            // update node reference
            controller.attachNode(node)
            subtreeView.tag = node

            // remove current node subtreeView
            subtreeView.removeNodeView(nodeView)

            // new node view
            val newNodeView = controller.createNodeView(context, node, treeRowMinHeight)!!
            controller.nodeView = newNodeView

            // insert new node subtreeView
            subtreeView.insertNodeView(newNodeView)
            return newNodeView
        }
        return null
    }

    /**
     * Deadend
     *
     * @param node node
     */
    fun deadend(node: TreeNode) {
        node.isEnabled = false
        val controller = node.controller
        controller.deadend()
    }

    // A D D / R E M O V E

    /**
     * Add node to parent (both tree model and view)
     *
     * @param parent parent node
     * @param node   node to add
     */
    fun add(parent: TreeNode, node: TreeNode) {
        // tree
        parent.addChild(node)

        // view
        if (isExpanded(parent)) {
            val parentController = parent.controller
            val viewGroup = parentController.childrenView!!
            /* var index = parent.indexOf(node); */
            addSubtreeView(viewGroup, node, -1)
        }
    }

    /**
     * Remove (both tree model and view)
     *
     * @param node node
     */
    fun remove(node: TreeNode) {
        val parent = node.parent
        if (parent != null) {
            // view
            removeSubtreeView(node)

            // tree
            parent.deleteChild(node)
        }
    }

    // A D D / R E M O V E  V I E W S

    /**
     * Add node to children view (view only, does not affect tree model)
     *
     * @param childrenView children view
     * @param node         node
     * @param atIndex0      insert-at index
     */
    @Synchronized
    private fun addSubtreeView(childrenView: ViewGroup, node: TreeNode, atIndex0: Int) {
        // Log.d(TAG, "Insert subtree view at index " + atIndex + " for node " + node + " count=" + childrenView.getChildCount())
        var atIndex = atIndex0
        val controller = node.controller
        var subtreeView = controller.subtreeView
        if (subtreeView == null) {
            subtreeView = controller.createView(context, containerStyle, treeIndent, treeRowMinHeight)
        }
        val view = subtreeView!!
        val parent = view.parent
        if (parent != null) {
            val group = parent as ViewGroup
            group.removeView(view)
        }

        // index
        if (atIndex != -1) {
            val n = childrenView.childCount
            var i = 0
            while (i < n) {

                // node (not view) 's index
                val child = childrenView.getChildAt(i)
                val tag = child.tag
                val node2 = tag as TreeNode
                val parent2 = node2.parent
                if (parent2 == null) {
                    i = -1
                    break
                }
                val j = parent2.indexOf(node2)
                if (j >= atIndex) {
                    break
                }
                i++
            }
            atIndex = i
            if (atIndex == n) {
                atIndex = -1
            } else if (atIndex > n) {
                // Log.e(TAG, "Illegal index " + node + " " + atIndex + " on " + n)
                throw RuntimeException("Illegal index $node $atIndex on $n")
            }
        }

        // add to children view
        childrenView.addView(view, atIndex)

        // inherit selection mode
        node.isSelectable = selectable

        // listener
        view.setOnClickListener {

            // if disabled
            if (!node.isEnabled) {
                return@setOnClickListener
            }

            // if deadend
            if (node.isDeadend) {
                return@setOnClickListener
            }

            // click node listener if node has one
            if (node.clickListener != null) {
                node.clickListener!!.onClick(node)
            } else if (nodeClickListener != null) {
                nodeClickListener!!.onClick(node)
            }

            // toggle node
            node.controller.flagEnsureVisible()
            toggleNode(node)
        }
    }

    /**
     * Remove node (view only, does not affect tree model)
     *
     * @param node node to remove
     */
    @Synchronized
    private fun removeSubtreeView(node: TreeNode) {
        // Log.d(TAG, "Remove subtree view for node " + node)
        val parent = node.parent
        if (parent != null) {
            // view
            if (isExpanded(parent)) {
                val parentController = parent.controller
                val viewGroup = parentController.childrenView!!
                val childController = node.controller
                val view: View = childController.subtreeView!!
                val index = viewGroup.indexOfChild(view)
                if (index >= 0) {
                    viewGroup.removeViewAt(index)
                }
            }
        }
    }

    // N E W   V I E W

    /**
     * New node view either by expanding collapsed node or by inserting in already expanded node
     *
     * @param node   node
     * @param levels remaining levels to unfold
     * @return parent's children view == node view container
     */
    fun newNodeView(node: TreeNode, levels: Int): View? {
        val parent = node.parent!!
        return if (isExpanded(parent)) {
            insertNodeView(parent, node)
        } else expandNode(parent, levels, fireHotNodes = false, overrideBreakExpand = false)
    }

    /**
     * Insert new node view in parent's existing children container view
     *
     * @param parent parent node
     * @param node   node to insert
     * @return parent's children view == node view container
     */
    private fun insertNodeView(parent: TreeNode, node: TreeNode): View? {
        val parentController = parent.controller
        val childrenView = parentController.childrenView
        if (childrenView != null) {
            val index = parent.indexOf(node)
            addSubtreeView(childrenView, node, index)

            // display
            if (childrenView.isNotEmpty()) {
                if (useAnimation) {
                    animatedContainerExpand(childrenView)
                } else {
                    containerExpand(childrenView)
                }

                // fire expand event
                parentController.onExpandEvent()
            }
        }
        return childrenView
    }

    // V I S I B I L I T Y

    fun ensureVisible(view: View?) {
        view?.requestFocus()
    }

    fun scrollToDeferred(view: View) {
        // scroll when dimensions are available
        val viewTreeObserver = view.viewTreeObserver
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                view.viewTreeObserver.removeOnPreDrawListener(this)
                return scrollTo(view) // false = cancel the current drawing pass
            }
        })
    }

    private fun scrollTo(view: View): Boolean {
        val y = getPosition(view)
        // Log.d(TAG, "Scroll: to " + y + " " + view)
        if (y == 0) {
            return false
        }
        treeView!!.scrollTo(0, y)
        //((NestedScrollView)TreeViewer.this.view).smoothScrollTo(0, y)
        return true
    }

    private fun getPosition(view: View): Int {
        var y = view.top
        var parent = view.parent as View
        while (parent !== treeView) {
            y += parent.top
            parent = parent.parent as View
        }
        return (y + treeView!!.translationY).toInt()
    }

    // E X P A N D  /  C O L L A P S E

    /**
     * Toggle node
     *
     * @param node node
     */
    private fun toggleNode(node: TreeNode) {
        if (isExpanded(node)) {
            collapseNode(node, false)
        } else {
            expandNode(node, 0, fireHotNodes = true, overrideBreakExpand = true)
        }
    }

    /**
     * Expand all
     */
    fun expandAll() {
        val view = expandNode(this.root, -1, fireHotNodes = false, overrideBreakExpand = false)
        view?.let { ensureVisible(it) }
    }

    /**
     * Collapse all
     */
    private fun collapseAll() {
        for (child in this.root.children) {
            collapseNode(child, true)
        }
    }

    // P R I M I T I V E S

    /**
     * Expand node
     *
     * @param node                node
     * @param levels              expanded subnodes level (-1 == unlimited)
     * @param fireHotNodes        whether to fire hot nodes
     * @param overrideBreakExpand whether to override node break expand
     * @return subtree view
     */
    fun expandNode(node: TreeNode, levels: Int, fireHotNodes: Boolean, overrideBreakExpand: Boolean): View? {
        // Log.d(TAG, "Expand node: " + node)

        // children view group
        val controller = node.controller
        val childrenView = controller.childrenView
        if (childrenView == null) {
            Log.e(TAG, "Node to expand has no view $node")
            return null
            //throw new RuntimeException("Node to expand has no children view " + node);
        }

        // clear all children views
        childrenView.removeAllViews()

        // break expand
        if (overrideBreakExpand || !controller.isBreakExpand) {
            // children
            for (child in node.children.toTypedArray<TreeNode>()) {
                //	TreeNode child = it.next();

                // add child node to children view
                addSubtreeView(childrenView, child, -1)

                // recurse
                if (child.controller.isBreakExpand) {
                    continue
                }
                if (levels != 0) {
                    expandNode(child, levels - 1, fireHotNodes, overrideBreakExpand)
                }
            }

            // display
            if (childrenView.isNotEmpty()) {
                if (useAnimation) {
                    animatedContainerExpand(childrenView)
                } else {
                    containerExpand(childrenView)
                }

                // fire expand event
                controller.onExpandEvent()
            }
        }

        // fire
        if (fireHotNodes) {
            controller.fire()
        }
        return controller.subtreeView
    }

    /**
     * Collapse node
     *
     * @param node            node
     * @param includeSubnodes whether to include subnodes
     */
    fun collapseNode(node: TreeNode, includeSubnodes: Boolean) {
        // collapsibility
        if (!node.isCollapsible) {
            return
        }
        val controller = node.controller

        // display
        val viewGroup = controller.childrenView!!
        if (useAnimation) {
            animatedContainerCollapse(viewGroup)
        } else {
            containerCollapse(viewGroup)
        }

        // fire collapse event
        controller.onCollapseEvent()

        // subnodes
        if (includeSubnodes) {
            for (child in node.children) {
                collapseNode(child, true)
            }
        }
    }

    // P R O P E R T I E S

    /**
     * Set default container style
     *
     * @param defaultStyle default container style
     */
    fun setDefaultContainerStyle(@StyleRes defaultStyle: Int) {
        setDefaultContainerStyle(defaultStyle, false)
    }

    /**
     * Set default container style
     *
     * @param defaultStyle  default container style
     * @param appliesToRoot apply for root
     */
    private fun setDefaultContainerStyle(defaultStyle: Int, appliesToRoot: Boolean) {
        containerStyle = defaultStyle
        containerStyleAppliesToRoot = appliesToRoot
    }

    /**
     * Compute tree child indent
     *
     * @param factor Tree indent factor to apply to default value
     */
    @SuppressLint("ResourceType")
    fun computeIndent(context: Context, factor: Float): Int {
        if (factor != -1f) {
            var defaultValue = 0
            @StyleableRes val attrs = intArrayOf(android.R.attr.paddingStart, android.R.attr.paddingLeft)
            context.withStyledAttributes(containerStyle, attrs) {
                defaultValue = getDimensionPixelSize(0, 0)
                if (defaultValue == 0) {
                    defaultValue = getDimensionPixelSize(1, 0)
                }
            }
            val value = (defaultValue * factor).toInt()
            Log.d(TAG, "Indent default=$defaultValue new=$value factor=$factor")
            return value
        }
        return -1
    }

    /**
     * Compute tree row min height
     *
     * @param factor Tree row min height factor to apply to default value
     */
    @SuppressLint("ResourceType")
    fun computeRowMinHeight(context: Context, factor: Float): Int {
        if (factor != -1f) {
            val defaultValue = context.resources.getDimensionPixelSize(R.dimen.height_row_min)
            val value = (defaultValue * factor).toInt()
            Log.d(TAG, "Row height default=$defaultValue new=$value factor=$factor")
            return value
        }
        return -1
    }

    // C L I C K

    /**
     * Set default on-click listener
     *
     * @param listener on-click listener
     */
    fun setDefaultNodeClickListener(listener: TreeNodeClickListener?) {
        nodeClickListener = listener
    }

    // S E L E C T I O N

    // mode

    /**
     * Get whether selection mode is enabled
     *
     * @return whether selection mode is enabled
     */
    fun isSelectable(): Boolean {
        return selectable
    }

    /**
     * Set selection mode
     *
     * @param selectable selection mode enable flag
     */
    fun setSelectable(selectable: Boolean) {
        if (!selectable) {
            deselectAll()
        }
        this.selectable = selectable

        // propagate from root
        for (child in this.root.children) {
            setSelectable(child, selectable)
        }
    }

    /**
     * Set node selectable
     *
     * @param node       node
     * @param selectable selectable flag
     */
    private fun setSelectable(node: TreeNode, selectable: Boolean) {
        fireNodeSelected(node, selectable)

        // propagate
        if (isExpanded(node)) {
            for (child in node.children) {
                setSelectable(child, selectable)
            }
        }
    }

    // select

    /**
     * Select all
     *
     * @param skipCollapsed whether to skip collapsed nod
     */
    fun selectAll(skipCollapsed: Boolean) {
        selectAll(true, skipCollapsed)
    }

    /**
     * Deselect all
     */
    private fun deselectAll() {
        selectAll(selected = false, skipCollapsed = false)
    }

    /**
     * Select/deselect all
     *
     * @param selected      selected flag
     * @param skipCollapsed whether to skip collapsed node
     */
    private fun selectAll(selected: Boolean, skipCollapsed: Boolean) {
        if (selectable) {
            for (child in this.root.children) {
                selectNode(child, selected, skipCollapsed)
            }
        }
    }

    /**
     * Select node
     *
     * @param node     node
     * @param selected selected flag
     */
    fun selectNode(node: TreeNode, selected: Boolean) {
        if (selectable) {
            node.setSelected(selected)
            fireNodeSelected(node, selected)
        }
    }

    /**
     * Select node and children
     *
     * @param node     node
     * @param selected selected flag
     */
    private fun selectNode(node: TreeNode, selected: Boolean, skipCollapsed: Boolean) {
        node.setSelected(selected)
        fireNodeSelected(node, selected)

        // propagation
        val propagate = !skipCollapsed || isExpanded(node)
        if (propagate) {
            for (child in node.children) {
                selectNode(child, selected, skipCollapsed)
            }
        }
    }

    /**
     * Fire node selection mode event
     *
     * @param node     node
     * @param selected selection mode flag
     */
    private fun fireNodeSelected(node: TreeNode, selected: Boolean) {
        val controller = node.controller
        if (controller.isInitialized) {
            controller.onSelectedEvent(selected)
        }
    }

    // S T A T E

    @Suppress("unused")
    val saveState: String
        /**
         * Get save state
         *
         * @return save state
         */
        get() {
            val sb = StringBuilder()
            getSaveState(this.root, sb)
            if (sb.isNotEmpty()) {
                sb.setLength(sb.length - 1)
            }
            return sb.toString()
        }

    /**
     * Get save state
     *
     * @param node root
     * @param sb   builder
     */
    private fun getSaveState(node: TreeNode, sb: StringBuilder) {
        for (child in node.children) {
            if (isExpanded(child)) {
                sb.append(child.path)
                sb.append(NODES_PATH_SEPARATOR)

                // recurse
                getSaveState(child, sb)
            }
        }
    }

    /**
     * Restore save state
     *
     * @param saveState save state
     */
    fun restoreState(saveState: String?) {
        if (!saveState.isNullOrEmpty()) {
            collapseAll()
            val expandedNodes = saveState.split(NODES_PATH_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val expandedNodeSet: Set<String> = HashSet(listOf(*expandedNodes))
            restoreNodeState(this.root, expandedNodeSet)
        }
    }

    /**
     * Restore node state
     *
     * @param node            node
     * @param expandedNodeSet open nodes
     */
    private fun restoreNodeState(node: TreeNode, expandedNodeSet: Set<String>) {
        for (child in node.children) {
            if (expandedNodeSet.contains(child.path)) {
                expandNode(child, 0, fireHotNodes = false, overrideBreakExpand = false)

                // recurse
                restoreNodeState(child, expandedNodeSet)
            }
        }
    }

    companion object {

        private const val TAG = "TreeViewer"
        private const val NODES_PATH_SEPARATOR = ";"
        private const val ANIMATION_DP_PER_MS = 3f

        // E X P A N D   S T A T U S

        /**
         * Get whether node is expanded
         *
         * @return whether node is expanded
         */
        fun isExpanded(node: TreeNode): Boolean {
            val controller = node.controller
            val childrenView = controller.childrenView ?: return false
            val visibility = childrenView.visibility
            return visibility != View.GONE
        }

        // E X P A N D / C O L L A P S E   T R I G G E R

        /**
         * Expand of view group (child container)
         *
         * @param view view
         */
        private fun containerExpand(view: ViewGroup) {
            view.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            view.requestLayout()
            view.visibility = View.VISIBLE
        }

        /**
         * Collapse of view group (child container)
         *
         * @param view view
         */
        private fun containerCollapse(view: ViewGroup) {
            view.visibility = View.GONE
        }

        /**
         * Animated collapse of view group (child container)
         *
         * @param view view
         */
        private fun animatedContainerExpand(view: ViewGroup) {
            view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val targetHeight = view.measuredHeight
            val duration = (ANIMATION_DP_PER_MS * targetHeight / view.context.resources.displayMetrics.density).toInt()
            ValueAnimator.setFrameDelay(250)
            val animator = ValueAnimator.ofFloat(0f, 1f)
            animator.repeatCount = 0
            animator.duration = duration.toLong()
            animator.startDelay = 0
            animator.interpolator = LinearInterpolator()
            animator.addUpdateListener { valueAnimator: ValueAnimator ->
                val value = valueAnimator.animatedValue as Float
                view.layoutParams.height = if (value == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetHeight * value).toInt()
                view.requestLayout()
            }
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator0: Animator) {
                    view.layoutParams.height = 0
                    view.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animator0: Animator) {
                    view.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    view.requestLayout()
                }

                override fun onAnimationCancel(animator0: Animator) {
                    view.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    view.requestLayout()
                }

                override fun onAnimationRepeat(animator0: Animator) {}
            })
            animator.start()
        }

        /**
         * Animated collapse of view group (child container)
         *
         * @param view view
         */
        private fun animatedContainerCollapse(view: ViewGroup) {
            val initialHeight = view.measuredHeight
            val duration = (ANIMATION_DP_PER_MS * initialHeight / view.context.resources.displayMetrics.density).toInt()
            ValueAnimator.setFrameDelay(250)
            val animator = ValueAnimator.ofFloat(0f, 1f)
            animator.repeatCount = 0
            animator.duration = duration.toLong()
            animator.startDelay = 0
            animator.interpolator = LinearInterpolator()
            animator.addUpdateListener { valueAnimator: ValueAnimator ->
                val value = valueAnimator.animatedValue as Float
                view.layoutParams.height = initialHeight - (initialHeight * value).toInt()
                view.requestLayout()
            }
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator0: Animator) {}
                override fun onAnimationEnd(animator0: Animator) {
                    view.visibility = View.GONE
                }

                override fun onAnimationCancel(animator0: Animator) {
                    view.visibility = View.GONE
                }

                override fun onAnimationRepeat(animator0: Animator) {}
            })
            animator.start()
        }
    }
}
