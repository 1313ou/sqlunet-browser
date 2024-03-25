/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.treeview.view

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import org.sqlunet.treeview.R

/**
 * SubtreeView (Tree node wrapper view)
 * -container for label
 * -container for node's children
 *
 * @author Bogdan Melnychuk on 2/10/15.
 */
class SubtreeView : LinearLayout {
    /**
     * Container style
     */
    private val containerStyle: Int

    /**
     * Tree padding / indentation / offset (to parent), -1 uses default styled value
     */
    private val treeIndent: Int

    /**
     * Node label view
     */
    private val nodeView: View

    /**
     * Node container
     */
    var childrenContainer: ViewGroup? = null

    /**
     * Constructor
     *
     * @param context        context
     * @param containerStyle container style
     * @param treeIndent     tree indent
     * @param nodeView       node view (group)
     */
    @JvmOverloads
    constructor(context: Context?, containerStyle: Int = -1, treeIndent: Int = 0, nodeView: View = View(context)) : super(context) {
        this.containerStyle = containerStyle
        this.treeIndent = treeIndent
        this.nodeView = nodeView
        init(context)
    }

    /**
     * Constructor
     *
     * @param context        context
     * @param attrs          attributes
     * @param containerStyle container style
     * @param treeIndent     tree indent
     * @param nodeView       node view (group)
     */
    constructor(context: Context?, attrs: AttributeSet?, containerStyle: Int, treeIndent: Int, nodeView: View) : super(context, attrs) {
        this.containerStyle = containerStyle
        this.treeIndent = treeIndent
        this.nodeView = nodeView
        init(context)
    }

    /**
     * Constructor
     *
     * @param context        context
     * @param attrs          attributes
     * @param defStyleAttr   def style attribute
     * @param containerStyle container style
     * @param treeIndent     tree indent
     * @param nodeView       node view (group)
     */
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, containerStyle: Int, treeIndent: Int, nodeView: View) : super(context, attrs, defStyleAttr) {
        this.containerStyle = containerStyle
        this.treeIndent = treeIndent
        this.nodeView = nodeView
        init(context)
    }

    /**
     * Constructor
     *
     * @param context        context
     * @param attrs          attributes
     * @param defStyleAttr   def style attribute
     * @param defStyleRes    def style resource
     * @param containerStyle container style
     * @param treeIndent     tree indent
     * @param nodeView       node view (group)
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int, containerStyle: Int, treeIndent: Int, nodeView: View) : super(context, attrs, defStyleAttr, defStyleRes) {
        this.containerStyle = containerStyle
        this.treeIndent = treeIndent
        this.nodeView = nodeView
        init(context)
    }

    /**
     * Init
     */
    @SuppressLint("ResourceType")
    private fun init(context: Context?) {
        orientation = VERTICAL
        isFocusable = true
        //setFocusable(View.FOCUSABLE)

        // node view
        insertNodeView(nodeView)

        // node container for children
        val containerContext = ContextThemeWrapper(context, containerStyle)
        val nodeChildrenContainer = LinearLayout(containerContext, null, containerStyle)
        nodeChildrenContainer.setLayoutParams(LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        nodeChildrenContainer.setId(R.id.node_children)
        nodeChildrenContainer.orientation = VERTICAL
        nodeChildrenContainer.visibility = GONE
        if (treeIndent != -1) {
            nodeChildrenContainer.setPadding(treeIndent, 0, 0, 0)
        }
        childrenContainer = nodeChildrenContainer
        addView(childrenContainer)
    }

    /**
     * Insert node view
     *
     * @param nodeView node view
     */
    fun insertNodeView(nodeView: View) {
        nodeView.setId(R.id.node_label)
        addView(nodeView, 0, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
    }

    /**
     * Remove node view
     *
     * @param nodeView node view
     */
    fun removeNodeView(nodeView: View?) {
        removeView(nodeView)
    }

    override fun toString(): String {
        return "subtreeview for $tag"
    }
}
