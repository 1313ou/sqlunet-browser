package org.sqlunet.treeview.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.view.TreeNodeWrapperView;
import org.sqlunet.treeview.view.TreeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tree node
 *
 * @author Bogdan Melnychuk on 2/10/15.
 */
public class TreeNode
{
	private static final String NODES_ID_SEPARATOR = ":"; //$NON-NLS-1$

	/**
	 * Id
	 */
	private int id;

	/**
	 * Value
	 */
	private Object value;

	/**
	 * Parent node
	 */
	private TreeNode parent;

	/**
	 * Children nodes
	 */
	private final List<TreeNode> children;

	/**
	 * Renderer
	 */
	private Renderer<?> renderer;

	/**
	 * Click listener
	 */
	private TreeNodeClickListener listener;

	/**
	 * Selected
	 */
	private boolean selected;

	/**
	 * Selectable
	 */
	private boolean selectable;

	/**
	 * Enabled
	 */
	private boolean enabled;

	/**
	 * Expanded
	 */
	private boolean expanded;

	// C O N S T R U C T O R

	public TreeNode(Object value)
	{
		this.children = new ArrayList<>();
		this.value = value;
		this.selectable = true;
		this.enabled = true;
	}

	/**
	 * Factory
	 *
	 * @return node
	 */
	public static TreeNode makeRoot()
	{
		final TreeNode root = new TreeNode(null);
		root.setSelectable(false);
		return root;
	}

	// T R E E

	/**
	 * Add child node
	 *
	 * @param childNode child node
	 * @return this node
	 */
	@SuppressWarnings("UnusedReturnValue")
	public TreeNode addChild(TreeNode childNode)
	{
		childNode.parent = this;
		// TODO think about id generation
		childNode.id = size();
		this.children.add(childNode);
		return this;
	}

	/**
	 * Add children nodes
	 *
	 * @param nodes children nodes
	 * @return this node
	 */
	@SuppressWarnings("UnusedReturnValue")
	public TreeNode addChildren(final TreeNode... nodes)
	{
		for (TreeNode node : nodes)
		{
			if (node != null)
			{
				addChild(node);
			}
		}
		return this;
	}

	/**
	 * Add children
	 *
	 * @param nodes children node iteration
	 * @return this node
	 */
	@SuppressWarnings("unused")
	public TreeNode addChildren(final Iterable<TreeNode> nodes)
	{
		for (TreeNode node : nodes)
		{
			if (node != null)
			{
				addChild(node);
			}
		}
		return this;
	}

	/**
	 * Delete child
	 *
	 * @param child child to delete
	 * @return child index
	 */
	public int deleteChild(final TreeNode child)
	{
		for (int i = 0; i < this.children.size(); i++)
		{
			if (child.id == this.children.get(i).id)
			{
				this.children.remove(i);
				return i;
			}
		}
		return -1;
	}

	/**
	 * Get children
	 *
	 * @return children
	 */
	public Iterable<TreeNode> getChildren()
	{
		return Collections.unmodifiableList(this.children);
	}

	/**
	 * Number of children
	 *
	 * @return Number of children
	 */
	private int size()
	{
		return this.children.size();
	}

	/**
	 * Get parent
	 *
	 * @return parent
	 */
	public TreeNode getParent()
	{
		return this.parent;
	}

	/**
	 * Get root
	 *
	 * @return root
	 */
	@SuppressWarnings("unused")
	public TreeNode getRoot()
	{
		TreeNode root = this;
		while (root.parent != null)
		{
			root = root.parent;
		}
		return root;
	}

	/**
	 * Get path
	 *
	 * @return path
	 */
	public String getPath()
	{
		final StringBuilder path = new StringBuilder();
		TreeNode node = this;
		while (node.parent != null)
		{
			path.append(node.getId());
			node = node.parent;
			if (node.parent != null)
			{
				path.append(NODES_ID_SEPARATOR);
			}
		}
		return path.toString();
	}

	/**
	 * Get level
	 *
	 * @return level of node
	 */
	public int getLevel()
	{
		int level = 0;
		TreeNode root = this;
		while (root.parent != null)
		{
			root = root.parent;
			level++;
		}
		return level;
	}

	/**
	 * Whether node is leaf
	 *
	 * @return Whether node is leaf
	 */
	public boolean isLeaf()
	{
		return size() == 0;
	}

	/**
	 * Whether node is root
	 *
	 * @return Whether node is root
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean isRoot()
	{
		return this.parent == null;
	}

	/**
	 * Whether node is first child
	 *
	 * @return Whether node is first child
	 */
	@SuppressWarnings("unused")
	public boolean isFirstChild()
	{
		if (!isRoot())
		{
			List<TreeNode> parentChildren = this.parent.children;
			return parentChildren.get(0).id == this.id;
		}
		return false;
	}

	/**
	 * Whether node is last child
	 *
	 * @return Whether node is last child
	 */
	@SuppressWarnings("unused")
	public boolean isLastChild()
	{
		if (!isRoot())
		{
			int parentSize = this.parent.children.size();
			if (parentSize > 0)
			{
				final List<TreeNode> parentChildren = this.parent.children;
				return parentChildren.get(parentSize - 1).id == this.id;
			}
		}
		return false;
	}

	// A T T R I B U T E S

	/**
	 * Get id
	 *
	 * @return id
	 */
	private int getId()
	{
		return this.id;
	}

	/**
	 * Get value
	 *
	 * @return value
	 */
	public Object getValue()
	{
		return this.value;
	}

	/**
	 * Set value
	 *
	 * @param value value
	 */
	public void setValue(final Object value)
	{
		this.value = value;
	}

	/**
	 * Get whether node is expanded
	 *
	 * @return whether node is expanded
	 */
	public boolean isExpanded()
	{
		return this.expanded;
	}

	/**
	 * Set node expanded
	 *
	 * @param expanded expanded flag
	 */
	public void setExpanded(boolean expanded)
	{
		this.expanded = expanded;
	}

	/**
	 * Get whether this node is selected
	 *
	 * @return whether this node is selected
	 */
	public boolean isSelected()
	{
		return this.selectable && this.selected;
	}


	/**
	 * Set this node selected
	 *
	 * @param selected selected flag
	 */
	public void setSelected(boolean selected)
	{
		this.selected = selected;
	}

	/**
	 * Get whether this node is selectable
	 *
	 * @return whether this node is selectable
	 */
	@SuppressWarnings("unused")
	public boolean isSelectable()
	{
		return this.selectable;
	}

	/**
	 * Set node selectable
	 *
	 * @param selectable selectable flag
	 */
	private void setSelectable(@SuppressWarnings("SameParameterValue") boolean selectable)
	{
		this.selectable = selectable;
	}

	// R E N D E R E R

	/**
	 * Set renderer
	 *
	 * @param renderer renderer
	 * @return this node
	 */
	public TreeNode setRenderer(Renderer<?> renderer)
	{
		this.renderer = renderer;
		if (renderer != null)
		{
			renderer.node = this;
		}
		return this;
	}

	/**
	 * Get renderer
	 *
	 * @return renderer
	 */
	public Renderer<?> getRenderer()
	{
		return this.renderer;
	}

	// C L I C K L I S T E N E R

	/**
	 * Set click listener
	 *
	 * @param listener click listener
	 * @return this node
	 */
	@SuppressWarnings("unused")
	public TreeNode setClickListener(TreeNodeClickListener listener)
	{
		this.listener = listener;
		return this;
	}

	/**
	 * Get click listener
	 *
	 * @return click listener
	 */
	public TreeNodeClickListener getClickListener()
	{
		return this.listener;
	}

	/**
	 * Click listener interface
	 */
	@SuppressWarnings("unused")
	public interface TreeNodeClickListener
	{
		/**
		 * Click event
		 *
		 * @param node  node
		 * @param value value
		 */
		void onClick(TreeNode node, Object value);
	}

	// S T A T E

	/**
	 * Disable node
	 */
	public void disable()
	{
		this.renderer.disable();
		this.enabled = false;
	}

	/**
	 * Get whether this node is enabled
	 *
	 * @return whether this node is enabled
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean isEnabled()
	{
		return this.enabled;
	}

	// B A S E R E N D E R E R

	/**
	 * Renderer interface
	 *
	 * @param <E> value type
	 */
	public static abstract class Renderer<E>
	{
		/**
		 * Node
		 */
		protected TreeNode node;

		/**
		 * Tree view
		 */
		TreeView treeView;

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
		int containerStyle;

		/**
		 * Context
		 */
		protected final Context context;

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

		// S T A T E

		/**
		 * Toggle
		 *
		 * @param active active state
		 */
		public void toggle(boolean active)
		{
			// empty
		}

		/**
		 * Disable
		 */
		public void disable()
		{
			// empty
		}

		/**
		 * Toggle selection model
		 */
		@SuppressWarnings("EmptyMethod")
		public void toggleSelectionMode()
		{
			// empty
		}
	}
}
