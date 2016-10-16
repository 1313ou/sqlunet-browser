package org.sqlunet.treeview.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.view.TreeView;
import org.sqlunet.treeview.view.TreeNodeWrapperView;

/**
 * Tree node
 *
 * Created by Bogdan Melnychuk on 2/10/15.
 */
public class TreeNode
{
	private static final String NODES_ID_SEPARATOR = ":"; //$NON-NLS-1$

	private int mId;

	private Object mValue;

	private TreeNode mParent;

	private final List<TreeNode> children;

	private Renderer<?> mRenderer;

	private TreeNodeClickListener mListener;

	private boolean mSelected;

	private boolean mSelectable = true;

	private boolean mEnabled = true;

	private boolean mExpanded;

	// C O N S T R U C T O R

	public TreeNode(Object value)
	{
		this.children = new ArrayList<>();
		this.mValue = value;
	}

	public static TreeNode makeRoot()
	{
		TreeNode root = new TreeNode(null);
		root.setSelectable(false);
		return root;
	}

	// T R E E

	@SuppressWarnings("UnusedReturnValue")
	public TreeNode addChild(TreeNode childNode)
	{
		childNode.mParent = this;
		// TODO think about id generation
		childNode.mId = size();
		this.children.add(childNode);
		return this;
	}

	@SuppressWarnings("UnusedReturnValue")
	public TreeNode addChildren(TreeNode... nodes)
	{
		for (TreeNode n : nodes)
		{
			if (n != null)
				addChild(n);
		}
		return this;
	}

	@SuppressWarnings("unused")
	public TreeNode addChildren(Collection<TreeNode> nodes)
	{
		for (TreeNode n : nodes)
		{
			if (n != null)
				addChild(n);
		}
		return this;
	}

	public int deleteChild(TreeNode child)
	{
		for (int i = 0; i < this.children.size(); i++)
		{
			if (child.mId == this.children.get(i).mId)
			{
				this.children.remove(i);
				return i;
			}
		}
		return -1;
	}

	public List<TreeNode> getChildren()
	{
		return Collections.unmodifiableList(this.children);
	}

	private int size()
	{
		return this.children.size();
	}

	public TreeNode getParent()
	{
		return this.mParent;
	}

	@SuppressWarnings("unused")
	public TreeNode getRoot()
	{
		TreeNode root = this;
		while (root.mParent != null)
		{
			root = root.mParent;
		}
		return root;
	}

	public String getPath()
	{
		final StringBuilder path = new StringBuilder();
		TreeNode node = this;
		while (node.mParent != null)
		{
			path.append(node.getId());
			node = node.mParent;
			if (node.mParent != null)
			{
				path.append(NODES_ID_SEPARATOR);
			}
		}
		return path.toString();
	}

	public int getLevel()
	{
		int level = 0;
		TreeNode root = this;
		while (root.mParent != null)
		{
			root = root.mParent;
			level++;
		}
		return level;
	}

	public boolean isLeaf()
	{
		return size() == 0;
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean isRoot()
	{
		return this.mParent == null;
	}

	@SuppressWarnings("unused")
	public boolean isFirstChild()
	{
		if (!isRoot())
		{
			List<TreeNode> parentChildren = this.mParent.children;
			return parentChildren.get(0).mId == this.mId;
		}
		return false;
	}

	@SuppressWarnings("unused")
	public boolean isLastChild()
	{
		if (!isRoot())
		{
			int parentSize = this.mParent.children.size();
			if (parentSize > 0)
			{
				final List<TreeNode> parentChildren = this.mParent.children;
				return parentChildren.get(parentSize - 1).mId == this.mId;
			}
		}
		return false;
	}

	// A T T R I B U T E S

	private int getId()
	{
		return this.mId;
	}

	public Object getValue()
	{
		return this.mValue;
	}

	public void setValue(final Object value0)
	{
		this.mValue = value0;
	}

	public boolean isExpanded()
	{
		return this.mExpanded;
	}

	@SuppressWarnings("UnusedReturnValue")
	public TreeNode setExpanded(boolean expanded)
	{
		this.mExpanded = expanded;
		return this;
	}

	public void setSelected(boolean selected)
	{
		this.mSelected = selected;
	}

	public boolean isSelected() {
		return this.mSelectable && this.mSelected;
	}

	private void setSelectable(@SuppressWarnings("SameParameterValue") boolean selectable)
	{
		this.mSelectable = selectable;
	}

	@SuppressWarnings("unused")
	public boolean isSelectable()
	{
		return this.mSelectable;
	}

	// R E N D E R E R

	public TreeNode setRenderer(Renderer<?> renderer)
	{
		this.mRenderer = renderer;
		if (renderer != null)
		{
			renderer.mNode = this;
		}
		return this;
	}

	public Renderer<?> getRenderer()
	{
		return this.mRenderer;
	}

	// C L I C K L I S T E N E R

	@SuppressWarnings("unused")
	public TreeNode setClickListener(TreeNodeClickListener listener)
	{
		this.mListener = listener;
		return this;
	}

	public TreeNodeClickListener getClickListener()
	{
		return this.mListener;
	}

	@SuppressWarnings("unused")
	public interface TreeNodeClickListener
	{
		void onClick(TreeNode node, Object value);
	}

	// S T A T E

	public void disable()
	{
		this.mRenderer.disable();
		this.mEnabled = false;
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean isEnabled()
	{
		return mEnabled;
	}

	// B A S E R E N D E R E R

	public static abstract class Renderer<E>
	{
		protected TreeNode mNode;

		TreeView tView;

		private View nodeView;

		private View mView;

		int containerStyle;

		protected final Context context;

		public Renderer(Context context)
		{
			this.context = context;
		}

		// node view
		public abstract View createNodeView(@SuppressWarnings("UnusedParameters") TreeNode node, E value);

		@SuppressWarnings("unchecked")
		public View getNodeView()
		{
			if (this.nodeView == null)
			{
				this.nodeView = createNodeView(this.mNode, (E) this.mNode.getValue());
			}
			return this.nodeView;
		}

		// view

		public View getView()
		{
			// return cached value
			if (this.mView != null)
			{
				return this.mView;
			}

			// make view
			final View nodeView1 = getNodeView();

			// wrapper
			final TreeNodeWrapperView nodeWrapperView = new TreeNodeWrapperView(nodeView1.getContext(), getContainerStyle());
			nodeWrapperView.insertNodeView(nodeView1);
			this.mView = nodeWrapperView;

			return this.mView;
		}

		public boolean isInitialized()
		{
			return this.mView != null;
		}

		// node items view

		public ViewGroup getNodeItemsView()
		{
			return (ViewGroup) getView().findViewById(R.id.node_items);
		}

		// tree view

		public void setTreeView(TreeView treeView)
		{
			this.tView = treeView;
		}

		public TreeView getTreeView()
		{
			return this.tView;
		}

		// container style

		public void setContainerStyle(int style)
		{
			this.containerStyle = style;
		}

		public int getContainerStyle()
		{
			return this.containerStyle;
		}

		public void toggle(boolean active)
		{
			// empty
		}

		@SuppressWarnings("EmptyMethod")
		public void toggleSelectionMode()
		{
			// empty
		}
		
		public void disable()
		{
			// empty
		}
	}
}
