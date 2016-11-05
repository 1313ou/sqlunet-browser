package org.sqlunet.treeview.renderer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.SubtreeView;
import org.sqlunet.treeview.view.TreeView;

/**
 * Base renderer
 *
 * @param <E> value type
 * @author Bogdan Melnychuk on 2/10/15.
 */
public abstract class Renderer<E>
{
	/**
	 * Node
	 */
	@SuppressWarnings("WeakerAccess")
	protected TreeNode node;

	/**
	 * Tree view (whole tree view it is part of)
	 */
	private TreeView treeView;

	/**
	 * View (wrapper view that includes label and subtreechildren)
	 */
	private View view;

	/**
	 * Node view (node label)
	 */
	private View nodeView;

	/**
	 * Container style
	 */
	private int containerStyle;

	/**
	 * Context
	 */
	final Context context;

	/**
	 * Constructor
	 *
	 * @param context context
	 */
	public Renderer(Context context)
	{
		this.context = context;
	}

	// T R E E V I E W

	/**
	 * Set tree view
	 *
	 * @param treeView tree view
	 */
	public void setTreeView(TreeView treeView)
	{
		this.treeView = treeView;
	}

	/**
	 * Get tree view
	 *
	 * @return tree view
	 */
	public TreeView getTreeView()
	{
		return this.treeView;
	}

	// V I E W

	/**
	 * Get (wrapper) view
	 *
	 * @return view
	 */
	public View getView()
	{
		// return cached value
		if (this.view != null)
		{
			return this.view;
		}

		// make view
		final View nodeView = getNodeView();

		// wrapper
		final SubtreeView nodeWrapperView = new SubtreeView(nodeView.getContext(), getContainerStyle());
		nodeWrapperView.insertNodeView(nodeView);
		this.view = nodeWrapperView;

		return this.view;
	}

	/**
	 * Get children nodes' container view
	 *
	 * @return children nodes' container view
	 */
	public ViewGroup getChildrenContainerView()
	{
		return (ViewGroup) getView().findViewById(R.id.node_children);
	}

	// N O D E V I E W

	/**
	 * Create node view
	 *
	 * @param node  node
	 * @param value value
	 * @return node view
	 */
	public abstract View createNodeView(@SuppressWarnings("UnusedParameters") final TreeNode node, final E value);

	/**
	 * Get node view
	 *
	 * @return node view
	 */
	@SuppressWarnings("unchecked")
	public View getNodeView()
	{
		if (this.nodeView == null)
		{
			this.nodeView = createNodeView(this.node, (E) this.node.getValue());
		}
		return this.nodeView;
	}

	// I N I T

	/**
	 * Get whether view is initialized
	 *
	 * @return whether view is initialized
	 */
	public boolean isInitialized()
	{
		return this.view != null;
	}

	// A T T A C H

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
	public void disable()
	{
		// empty
	}

	// S T Y L E

	/**
	 * Set container style
	 *
	 * @param style container style
	 */
	public void setContainerStyle(int style)
	{
		this.containerStyle = style;
	}

	/**
	 * Get container style
	 *
	 * @return container style
	 */
	public int getContainerStyle()
	{
		return this.containerStyle;
	}

	// E V E N T   L I S T E N E R

	/**
	 * Expand/Collapse event notification
	 *
	 * @param expand true if expand event, false if collapse event
	 */
	public void onExpandEvent(boolean expand)
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
