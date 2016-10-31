package org.sqlunet.treeview.renderer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.view.TreeNodeWrapperView;
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
	 * Tree view
	 */
	private TreeView treeView;

	/**
	 * Node view
	 */
	private View nodeView;

	/**
	 * View
	 */
	private View view;

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

	// V I E W

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

	/**
	 * Get view
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
		final View nodeView1 = getNodeView();

		// wrapper
		final TreeNodeWrapperView nodeWrapperView = new TreeNodeWrapperView(nodeView1.getContext(), getContainerStyle());
		nodeWrapperView.insertNodeView(nodeView1);
		this.view = nodeWrapperView;

		return this.view;
	}

	/**
	 * Get whether view is initialized
	 *
	 * @return whether view is initialized
	 */
	public boolean isInitialized()
	{
		return this.view != null;
	}

	/**
	 * Get node items container view
	 *
	 * @return node items container view
	 */
	public ViewGroup getNodeItemsView()
	{
		return (ViewGroup) getView().findViewById(R.id.node_items);
	}

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

	/**
	 * Set node
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
