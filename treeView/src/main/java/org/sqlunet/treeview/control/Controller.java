/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.treeview.control;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.SubtreeView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Base controller
 *
 * @author Bogdan Melnychuk on 2/10/15.
 */
public abstract class Controller<E>
{
	// M O D E L

	/**
	 * Node
	 */
	@SuppressWarnings("WeakerAccess")
	protected TreeNode node;

	// B E H A V I O U R

	/**
	 * BreakExpand flag
	 */
	@SuppressWarnings("WeakerAccess")
	protected boolean breakExpand;

	/**
	 * Ensure visible
	 */
	@SuppressWarnings("WeakerAccess")
	protected boolean ensureVisible;

	// V I E W

	/**
	 * View (wrapper subtreeView that includes label and children)
	 */
	@Nullable
	private SubtreeView subtreeView;

	/**
	 * Node view (node label)
	 */
	@Nullable
	private View nodeView;

	/**
	 * Child nodes' container view
	 */
	@Nullable
	private ViewGroup childrenView;


	// C O N S T R U C T

	/**
	 * Constructor
	 *
	 * @param breakExpand whether this controller breaks expansion
	 */
	@SuppressWarnings("WeakerAccess")
	protected Controller(final boolean breakExpand)
	{
		this.breakExpand = breakExpand;
		this.ensureVisible = false;
	}

	// V I E W

	public SubtreeView createView(@NonNull final Context context, final int containerStyle)
	{
		// node view
		//noinspection unchecked
		this.nodeView = createNodeView(context, this.node, (E) this.node.getValue());
		assert this.nodeView != null;

		// wrapper
		this.subtreeView = new SubtreeView(context, containerStyle, this.nodeView);
		this.subtreeView.setTag(this.node);

		// children view
		this.childrenView = this.subtreeView.childrenContainer;

		return this.subtreeView;
	}

	/**
	 * Get (wrapper) subtreeView
	 *
	 * @return subtreeView
	 */
	@Nullable
	public SubtreeView getSubtreeView()
	{
		return this.subtreeView;
	}

	/**
	 * Get children nodes' container view
	 *
	 * @return children nodes' container view
	 */
	@Nullable
	public ViewGroup getChildrenView()
	{
		return this.childrenView;
	}

	// N O D E V I E W

	/**
	 * Create node view
	 *
	 * @param node  node
	 * @param value value
	 * @return node view
	 */
	@Nullable
	public abstract View createNodeView(@NonNull final Context context, final TreeNode node, final E value);

	/**
	 * Get node view
	 *
	 * @return node view
	 */
	@Nullable
	public View getNodeView()
	{
		return this.nodeView;
	}

	/**
	 * Get node view
	 *
	 * @param nodeView node view
	 */
	public void setNodeView(@Nullable final View nodeView)
	{
		this.nodeView = nodeView;
	}

	// I N I T

	/**
	 * Get whether subtreeView is initialized
	 *
	 * @return whether subtreeView is initialized
	 */
	public boolean isInitialized()
	{
		return this.subtreeView != null;
	}

	// B E H A V I O U R

	public boolean isBreakExpand()
	{
		return this.breakExpand;
	}

	public void setBreakExpand(final boolean breakExpand)
	{
		this.breakExpand = breakExpand;
	}

	public boolean takeEnsureVisible()
	{
		boolean result = this.ensureVisible;
		this.ensureVisible = false;
		return result;
	}

	public void ensureVisible()
	{
		this.ensureVisible = true;
	}

	// M O D E L

	/**
	 * Attach/Set node
	 *
	 * @param node node
	 */
	public void attachNode(final TreeNode node)
	{
		this.node = node;
	}

	// S T A T E

	/**
	 * Disable
	 */
	@SuppressWarnings("EmptyMethod")
	public void deadend()
	{
		// empty
	}

	// F I R E

	/**
	 * Fire
	 */
	public void fire()
	{
		// empty
	}

	// E V E N T   L I S T E N E R

	/**
	 * Expand event notification
	 */
	@SuppressWarnings("EmptyMethod")
	public void onExpandEvent()
	{
		// empty
	}

	/**
	 * Collapse event notification
	 */
	@SuppressWarnings("EmptyMethod")
	public void onCollapseEvent()
	{
		// empty
	}

	/**
	 * Selection event
	 *
	 * @param selected selected flag
	 */
	@SuppressWarnings("EmptyMethod")
	public void onSelectedEvent(@SuppressWarnings("UnusedParameters") final boolean selected)
	{
		// empty
	}
}
