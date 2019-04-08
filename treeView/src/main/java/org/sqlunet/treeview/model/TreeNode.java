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
	private TreeNode parent;

	/**
	 * Children nodes
	 */
	@NonNull
	private final List<TreeNode> children;

	/**
	 * Controller class
	 */
	@Nullable
	public Class<?> controllerClass;

	/**
	 * Controller
	 */
	@Nullable
	private Controller<?> controller;

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

	/**
	 * Collapsible
	 */
	private boolean collapsible;

	// C O N S T R U C T O R

	public TreeNode(final Object value, final Class<?> controllerClass)
	{
		this.children = new ArrayList<>();
		this.value = value;
		this.enabled = true;
		this.selected = false;
		this.selectable = false;
		this.expanded = false;
		this.collapsible = true;
		this.controllerClass = controllerClass;
	}

	public TreeNode(final Object value, final Class<?> controllerClass, @SuppressWarnings("SameParameterValue") final boolean collapsible)
	{
		this(value, controllerClass);
		this.collapsible = collapsible;
	}

	/**
	 * Factory
	 *
	 * @return node
	 */
	@NonNull
	static public TreeNode makeRoot()
	{
		final TreeNode root = new TreeNode(null, null);
		root.setSelectable(false);
		return root;
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
	@SuppressWarnings("unused")
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
	@SuppressWarnings("UnusedReturnValue")
	private TreeNode prependChild(@NonNull TreeNode childNode)
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
	@SuppressWarnings({"unused"})
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
	@SuppressWarnings("unused")
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
	 * @return child index
	 */
	public int deleteChild(@NonNull final TreeNode child)
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
	@NonNull
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
	@SuppressWarnings("unused")
	public boolean isFirstChild()
	{
		//noinspection SimplifiableIfStatement
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
	@SuppressWarnings("unused")
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

	// A T T R I B U T E S

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
	public void setSelectable(boolean selectable)
	{
		this.selectable = selectable;
	}


	/**
	 * Get whether this node is collapsible
	 *
	 * @return whether this node is collapsible
	 */
	@SuppressWarnings("unused")
	public boolean isCollapsible()
	{
		return this.collapsible;
	}

	/**
	 * Set node collapsible
	 *
	 * @param collapsible collapsible flag
	 */
	@SuppressWarnings("unused")
	public void setCollapsible(boolean collapsible)
	{
		this.collapsible = collapsible;
	}

	// E N A B L E / D I S A B L E

	/**
	 * Disable node
	 */
	public void disable()
	{
		this.enabled = false;
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

	// C O N T R O L L E R

	/**
	 * Set controller
	 *
	 * @param controller controller
	 * @return this node
	 */
	@NonNull
	public TreeNode setController(@Nullable final Controller<?> controller)
	{
		this.controller = controller;

		// adjust attached node
		if (controller != null)
		{
			controller.attachNode(this);
		}
		return this;
	}

	/**
	 * Get controller
	 *
	 * @return controller
	 */
	@Nullable
	public Controller<?> getController()
	{
		return this.controller;
	}

	// C L I C K  L I S T E N E R

	/**
	 * Set click listener
	 *
	 * @param listener click listener
	 * @return this node
	 */
	@NonNull
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
}
