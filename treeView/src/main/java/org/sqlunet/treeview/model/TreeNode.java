/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.treeview.model;

import org.sqlunet.treeview.control.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Tree node
 *
 * @author Bogdan Melnychuk on 2/10/15.
 */
public class TreeNode
{
	static private final String NODES_ID_SEPARATOR = ":";

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
	@Nullable
	private TreeNode parent;

	/**
	 * Children nodes
	 */
	@NonNull
	private final List<TreeNode> children;

	/**
	 * Controller
	 */
	@NonNull
	private final Controller<?> controller;

	/**
	 * Click listener
	 */
	private TreeNodeClickListener listener;

	/**
	 * Enabled
	 */
	private boolean enabled;

	/**
	 * Selected
	 */
	private boolean selected;

	/**
	 * Selectable
	 */
	private boolean selectable;

	/**
	 * Collapsible
	 */
	private boolean collapsible;

	/**
	 * Dead end
	 */
	private boolean deadend;

	// C O N S T R U C T O R

	public TreeNode(final Object value, @NonNull final Controller<?> controller)
	{
		this.children = new ArrayList<>();
		this.value = value;
		this.enabled = true;
		this.selected = false;
		this.selectable = false;
		this.collapsible = true;
		this.deadend = false;
		this.controller = controller;

		this.controller.attachNode(this);
	}

	public TreeNode(final Object value, @NonNull final Controller<?> controller, @SuppressWarnings("SameParameterValue") final boolean collapsible)
	{
		this(value, controller);
		this.collapsible = collapsible;
	}

	// T R E E

	/**
	 * Add this node to parent node
	 *
	 * @param parentNode parent node
	 * @return this node
	 */
	@NonNull
	public TreeNode addTo(@NonNull final TreeNode parentNode)
	{
		parentNode.addChild(this);
		return this;
	}

	/**
	 * Add child node
	 *
	 * @param childNode child node
	 * @return this node
	 */
	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	public TreeNode addChild(@NonNull final TreeNode childNode)
	{
		childNode.parent = this;
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
	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	public TreeNode addChildren(@NonNull final TreeNode... nodes)
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
	@NonNull
	public TreeNode addChildren(@NonNull final Iterable<TreeNode> nodes)
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
	 * Prepend this node to parent node
	 *
	 * @param parentNode parent node
	 * @return this node
	 */
	@NonNull
	public TreeNode prependTo(@NonNull final TreeNode parentNode)
	{
		parentNode.prependChild(this);
		return this;
	}

	/**
	 * Prepend child node
	 *
	 * @param childNode child node
	 * @return this node
	 */
	@NonNull
	@SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
	public TreeNode prependChild(@NonNull TreeNode childNode)
	{
		childNode.parent = this;
		childNode.id = size();
		this.children.add(0, childNode);
		return this;
	}

	/**
	 * Prepend children nodes
	 *
	 * @param nodes children nodes
	 * @return this node
	 */
	@NonNull
	public TreeNode prependChildren(@NonNull final TreeNode... nodes)
	{
		for (int i = nodes.length - 1; i >= 0; i--)
		{
			TreeNode node = nodes[i];
			if (node != null)
			{
				prependChild(node);
			}
		}
		return this;
	}

	/**
	 * Self-delete
	 *
	 * @return true if deleted
	 */
	public boolean delete()
	{
		final TreeNode parent = getParent();
		//noinspection SimplifiableIfStatement
		if (parent != null)
		{
			return parent.deleteChild(this) != -1;
		}
		return false;
	}

	/**
	 * Delete child
	 *
	 * @param child child to delete
	 * @return child indexOf
	 */
	public int deleteChild(@NonNull final TreeNode child)
	{
		for (int i = 0; i < this.children.size(); i++)
		{
			if (child.id == this.children.get(i).id)
			{
				this.children.remove(i);
				child.parent = null;
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
	@NonNull
	public Iterable<TreeNode> getChildren()
	{
		return Collections.unmodifiableList(this.children);
	}

	@NonNull
	public List<TreeNode> getChildrenList()
	{
		return this.children;
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
	 * Index of child
	 *
	 * @param child child
	 * @return child indexOf
	 */
	public int indexOf(@NonNull final TreeNode child)
	{
		for (int i = 0; i < this.children.size(); i++)
		{
			if (child.id == this.children.get(i).id)
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * Get parent
	 *
	 * @return parent
	 */
	@Nullable
	public TreeNode getParent()
	{
		return this.parent;
	}

	/**
	 * Get root
	 *
	 * @return root
	 */
	@Nullable
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
	@NonNull
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
	private boolean isNotRoot()
	{
		return this.parent != null;
	}

	/**
	 * Whether node is first child
	 *
	 * @return Whether node is first child
	 */
	public boolean isFirstChild()
	{
		if (isNotRoot())
		{
			return this.parent.children.get(0).id == this.id;
		}
		return false;
	}

	/**
	 * Whether node is last child
	 *
	 * @return Whether node is last child
	 */
	public boolean isLastChild()
	{
		if (isNotRoot())
		{
			int parentSize = this.parent.children.size();
			if (parentSize > 0)
			{
				return this.parent.children.get(parentSize - 1).id == this.id;
			}
		}
		return false;
	}

	// I D

	/**
	 * Get id
	 *
	 * @return id
	 */
	private int getId()
	{
		return this.id;
	}

	// V A L U E

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

	// E N A B L E / D I S A B L E

	/**
	 * Set node enabled
	 *
	 * @param enabled flag
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	/**
	 * Get whether this node is enabled
	 *
	 * @return whether this node is enabled
	 */
	public boolean isEnabled()
	{
		return this.enabled;
	}

	// A T T R I B U T E S

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
	public boolean isSelectable()
	{
		return this.selectable;
	}

	/**
	 * Set node selectable
	 *
	 * @param selectable selectable flag
	 */
	public void setSelectable(boolean selectable)
	{
		this.selectable = selectable;
	}


	/**
	 * Get whether this node is collapsible
	 *
	 * @return whether this node is collapsible
	 */
	public boolean isCollapsible()
	{
		return this.collapsible;
	}

	/**
	 * Set node collapsible
	 *
	 * @param collapsible collapsible flag
	 */
	public void setCollapsible(boolean collapsible)
	{
		this.collapsible = collapsible;
	}


	/**
	 * Get whether this node is deadend
	 *
	 * @return whether this node is deadend
	 */
	public boolean isDeadend()
	{
		return this.deadend;
	}

	/**
	 * Set node deadend
	 *
	 * @param deadend flag
	 */
	public void setDeadend(boolean deadend)
	{
		this.deadend = deadend;
	}

	// C O N T R O L L E R

	/**
	 * Get controller
	 *
	 * @return controller
	 */
	@NonNull
	public Controller<?> getController()
	{
		return this.controller;
	}

	// S T R I N G I F Y

	@NonNull
	@Override
	public String toString()
	{
		return "#" + id + ' ' + //
				(value == null ? "null" : '[' + value.toString().replace('\n', '┃') + ']') + ' ' + //
				"controller=" + controller.getClass().getSimpleName() + ' ' + //
				"parent=" + (parent == null ? "none" : parent.id) + ' ' + //
				"num children=" + children.size();
	}

	@NonNull
	public String toStringWithChildren()
	{
		final StringBuilder sb = new StringBuilder();
		toStringWithChildren(sb, 0);
		return sb.toString();
	}

	private void toStringWithChildren(@NonNull final StringBuilder sb, final int level)
	{
		if (level > 0)
		{
			for (int i = 0; i < level - 1; i++)
			{
				sb.append("     ");
			}
			sb.append("└");
			//sb.append("├");
			sb.append("────");
		}
		sb.append(this);

		sb.append('\n');
		for (TreeNode child : getChildren())
		{
			child.toStringWithChildren(sb, level + 1);
		}

	}

	// C L I C K  L I S T E N E R

	/**
	 * Set click listener
	 *
	 * @param listener click listener
	 * @return this node
	 */
	@NonNull
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
	@FunctionalInterface
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
}
