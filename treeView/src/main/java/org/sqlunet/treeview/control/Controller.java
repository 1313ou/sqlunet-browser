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
 * @param <E> value type
 * @author Bogdan Melnychuk on 2/10/15.
 */
public abstract class Controller<E>
{
	// M O D E L

	/**
	 * Node
	 */
	protected TreeNode node;

	// V I E W

	/**
	 * View (wrapper view that includes label and children)
	 */
	@Nullable
	private View view;

	/**
	 * Node view (node label)
	 */
	@Nullable
	private View nodeView;

	/**
	 * Child nodes' container view
	 */
	@Nullable
	private ViewGroup childrenContainer;


	// C O N S T R U C T

	/**
	 * Constructor
	 */
	protected Controller()
	{
	}

	// V I E W

	public View createView(@NonNull final Context context, final int containerStyle)
	{
		// node view
		this.nodeView = createNodeView(context, this.node, (E) this.node.getValue());

		// wrapper
		final SubtreeView subtreeView = new SubtreeView(context, containerStyle);
		subtreeView.insertNodeView(this.nodeView);
		this.view = subtreeView;

		// children view
		this.childrenContainer = subtreeView.childrenContainer;

		return this.view;
	}


	/**
	 * Get (wrapper) view
	 *
	 * @return view
	 */
	@Nullable
	public View getView()
	{
		return this.view;
	}

	/**
	 * Get children nodes' container view
	 *
	 * @return children nodes' container view
	 */
	@Nullable
	public ViewGroup getChildrenContainerView()
	{
		return this.childrenContainer;
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
	protected abstract View createNodeView(@NonNull final Context context, final TreeNode node, final E value);

	/**
	 * Get node view
	 *
	 * @return node view
	 */
	@Nullable
	@SuppressWarnings("unchecked")
	public View getNodeView()
	{
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
	public void disable()
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
	public void onExpandEvent()
	{
		// empty
	}

	/**
	 * Collapse event notification
	 */
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
