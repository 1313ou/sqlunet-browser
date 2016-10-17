package org.sqlunet.treeview.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.model.TreeNode;
import org.sqlunet.treeview.renderer.SimpleRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tree view
 * <p>
 * Created by Bogdan Melnychuk on 2/10/15.
 */
public class TreeView
{
	private static final String NODES_PATH_SEPARATOR = ";"; //$NON-NLS-1$

	private final TreeNode mRoot;
	private final Context mContext;
	private boolean applyForRoot;
	private int containerStyle = 0;
	private Class<? extends TreeNode.Renderer<?>> defaultViewHolderClass = SimpleRenderer.class;
	private TreeNode.TreeNodeClickListener nodeClickListener;
	private boolean mSelectionModeEnabled;
	private boolean mUseDefaultAnimation = false;
	private boolean use2dScroll = false;

	// C O N S T R U C T O R

	public TreeView(Context context, TreeNode root)
	{
		this.mRoot = root;
		this.mContext = context;
	}

	// V I E W

	private View getView(int style)
	{
		final ViewGroup view;
		if (style > 0)
		{
			ContextThemeWrapper newContext = new ContextThemeWrapper(this.mContext, style);
			view = this.use2dScroll ? new TwoDScrollView(newContext) : new ScrollView(newContext);
		}
		else
		{
			view = this.use2dScroll ?
					new TwoDScrollView(this.mContext) :
					new ScrollView(this.mContext);
		}

		Context containerContext = this.mContext;
		if (this.containerStyle != 0 && this.applyForRoot)
		{
			containerContext = new ContextThemeWrapper(this.mContext, this.containerStyle);
		}

		final LinearLayout viewTreeItems = new LinearLayout(containerContext, null, this.containerStyle);
		viewTreeItems.setId(R.id.tree_items);
		viewTreeItems.setOrientation(LinearLayout.VERTICAL);
		view.addView(viewTreeItems);

		this.mRoot.setRenderer(new TreeNode.Renderer<Void>(this.mContext)
		{
			@Override
			public View createNodeView(TreeNode node, Void value)
			{
				return null;
			}

			@Override
			public ViewGroup getNodeItemsView()
			{
				return viewTreeItems;
			}
		});

		expandNode(this.mRoot, false);
		return view;
	}

	public View getView()
	{
		return getView(-1);
	}

	// A D D / R E M O V E

	private void addNode(ViewGroup container, final TreeNode node)
	{
		final TreeNode.Renderer<?> viewHolder = getViewHolderForNode(node);
		final View nodeView = viewHolder.getView();
		container.addView(nodeView);

		// selection
		if (this.mSelectionModeEnabled)
		{
			viewHolder.toggleSelectionMode();
		}

		// listener
		nodeView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// click disable
				if (!node.isEnabled())
				{
					return;
				}

				if (node.getClickListener() != null)
				{
					node.getClickListener().onClick(node, node.getValue());
				}
				else if (TreeView.this.nodeClickListener != null)
				{
					TreeView.this.nodeClickListener.onClick(node, node.getValue());
				}
				toggleNode(node);
			}
		});
	}

	@SuppressWarnings("unused")
	public void addNode(TreeNode parent, final TreeNode nodeToAdd)
	{
		// tree
		parent.addChild(nodeToAdd);

		// view
		if (parent.isExpanded())
		{
			final TreeNode.Renderer<?> parentViewHolder = getViewHolderForNode(parent);
			addNode(parentViewHolder.getNodeItemsView(), nodeToAdd);
		}
	}

	@SuppressWarnings("unused")
	public void removeNode(TreeNode node)
	{
		if (node.getParent() != null)
		{
			// tree
			TreeNode parent = node.getParent();
			int index = parent.deleteChild(node);

			// view
			if (parent.isExpanded() && index >= 0)
			{
				final TreeNode.Renderer<?> parentViewHolder = getViewHolderForNode(parent);
				parentViewHolder.getNodeItemsView().removeViewAt(index);
			}
		}
	}

	// P R O P E R T I E S

	public void setDefaultAnimation(@SuppressWarnings("SameParameterValue") boolean defaultAnimation)
	{
		this.mUseDefaultAnimation = defaultAnimation;
	}

	public void setDefaultContainerStyle(int style)
	{
		setDefaultContainerStyle(style, false);
	}

	private void setDefaultContainerStyle(int style, @SuppressWarnings("SameParameterValue") boolean applyForRoot)
	{
		this.containerStyle = style;
		this.applyForRoot = applyForRoot;
	}

	@SuppressWarnings("unused")
	public void setUse2dScroll(boolean use2dScroll)
	{
		this.use2dScroll = use2dScroll;
	}

	@SuppressWarnings("unused")
	public boolean is2dScrollEnabled()
	{
		return this.use2dScroll;
	}

	public void setDefaultViewHolder(Class<? extends TreeNode.Renderer<?>> viewHolder)
	{
		this.defaultViewHolderClass = viewHolder;
	}

	@SuppressWarnings("unused")
	public void setDefaultNodeClickListener(TreeNode.TreeNodeClickListener listener)
	{
		this.nodeClickListener = listener;
	}

	// E X P A N D / C O L L A P S E

	@SuppressWarnings("unused")
	public void expandAll()
	{
		expandNode(this.mRoot, true);
	}

	private void collapseAll()
	{
		for (TreeNode n : this.mRoot.getChildren())
		{
			collapseNode(n, true);
		}
	}

	@SuppressWarnings("unused")
	public void expandLevel(int level)
	{
		for (TreeNode n : this.mRoot.getChildren())
		{
			expandLevel(n, level);
		}
	}

	private void expandLevel(TreeNode node, int level)
	{
		if (node.getLevel() <= level)
		{
			expandNode(node, false);
		}
		for (TreeNode n : node.getChildren())
		{
			expandLevel(n, level);
		}
	}

	private void expandRelativeLevel(TreeNode node, int levels)
	{
		if (levels <= 0)
		{
			return;
		}

		expandNode(node, false);

		for (TreeNode n : node.getChildren())
		{
			expandRelativeLevel(n, levels - 1);
		}
	}

	private void expandNode(TreeNode node)
	{
		expandNode(node, false);
	}

	@SuppressWarnings("unused")
	public void collapseNode(TreeNode node)
	{
		collapseNode(node, false);
	}

	private void toggleNode(TreeNode node)
	{
		if (node.isExpanded())
		{
			collapseNode(node, false);
		}
		else
		{
			expandNode(node, false);
		}
	}

	private void collapseNode(TreeNode node, final boolean includeSubnodes)
	{
		node.setExpanded(false);
		TreeNode.Renderer<?> nodeViewHolder = getViewHolderForNode(node);

		if (this.mUseDefaultAnimation)
		{
			collapse(nodeViewHolder.getNodeItemsView());
		}
		else
		{
			nodeViewHolder.getNodeItemsView().setVisibility(View.GONE);
		}
		nodeViewHolder.toggle(false);
		if (includeSubnodes)
		{
			for (TreeNode n : node.getChildren())
			{
				collapseNode(n, true);
			}
		}
	}

	private void expandNode(final TreeNode node, boolean includeSubnodes)
	{
		node.setExpanded(true);
		final TreeNode.Renderer<?> parentViewHolder = getViewHolderForNode(node);
		parentViewHolder.getNodeItemsView().removeAllViews();

		parentViewHolder.toggle(true);

		for (final TreeNode n : node.getChildren())
		{
			addNode(parentViewHolder.getNodeItemsView(), n);

			if (n.isExpanded() || includeSubnodes)
			{
				expandNode(n, includeSubnodes);
			}
		}
		if (this.mUseDefaultAnimation)
		{
			expand(parentViewHolder.getNodeItemsView());
		}
		else
		{
			parentViewHolder.getNodeItemsView().setVisibility(View.VISIBLE);
		}
	}

	private static void expand(final View v)
	{
		v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		final int targetHeight = v.getMeasuredHeight();

		v.getLayoutParams().height = 0;
		v.setVisibility(View.VISIBLE);
		Animation a = new Animation()
		{
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t)
			{
				v.getLayoutParams().height = interpolatedTime == 1 ?
						LayoutParams.WRAP_CONTENT :
						(int) (targetHeight * interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds()
			{
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
		v.startAnimation(a);
	}

	private static void collapse(final View v)
	{
		final int initialHeight = v.getMeasuredHeight();

		Animation a = new Animation()
		{
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t)
			{
				if (interpolatedTime == 1)
				{
					v.setVisibility(View.GONE);
				}
				else
				{
					v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds()
			{
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
		v.startAnimation(a);
	}

	static public void expand(final TreeNode node, @SuppressWarnings("SameParameterValue") boolean includeSubnodes)
	{
		node.getRenderer().getTreeView().expandNode(node, includeSubnodes);
	}

	static public void expand(final TreeNode node, int levels)
	{
		node.getRenderer().getTreeView().expandRelativeLevel(node, levels);
	}

	@SuppressWarnings("unused")
	static public void collapse(final TreeNode node, boolean includeSubnodes)
	{
		node.getRenderer().getTreeView().collapseNode(node, includeSubnodes);
	}

	// S T A T E

	public String getSaveState()
	{
		final StringBuilder builder = new StringBuilder();
		getSaveState(this.mRoot, builder);
		if (builder.length() > 0)
		{
			builder.setLength(builder.length() - 1);
		}
		return builder.toString();
	}

	public void restoreState(String saveState)
	{
		if (!TextUtils.isEmpty(saveState))
		{
			collapseAll();
			final String[] openNodesArray = saveState.split(NODES_PATH_SEPARATOR);
			final Set<String> openNodes = new HashSet<>(Arrays.asList(openNodesArray));
			restoreNodeState(this.mRoot, openNodes);
		}
	}

	private void restoreNodeState(TreeNode node, Set<String> openNodes)
	{
		for (TreeNode n : node.getChildren())
		{
			if (openNodes.contains(n.getPath()))
			{
				expandNode(n);
				restoreNodeState(n, openNodes);
			}
		}
	}

	private void getSaveState(TreeNode root, StringBuilder sBuilder)
	{
		for (TreeNode node : root.getChildren())
		{
			if (node.isExpanded())
			{
				sBuilder.append(node.getPath());
				sBuilder.append(NODES_PATH_SEPARATOR);
				getSaveState(node, sBuilder);
			}
		}
	}

	// S E L E C T I O N

	@SuppressWarnings("unused")
	public void setSelectionModeEnabled(boolean selectionModeEnabled)
	{
		if (!selectionModeEnabled)
		{
			// TODO fix double iteration over tree
			deselectAll();
		}
		this.mSelectionModeEnabled = selectionModeEnabled;

		for (TreeNode node : this.mRoot.getChildren())
		{
			toggleSelectionMode(node, selectionModeEnabled);
		}

	}

	@SuppressWarnings({"unchecked", "unused"})
	public <E> List<E> getSelectedValues(Class<E> clazz)
	{
		List<E> result = new ArrayList<>();
		List<TreeNode> selected = getSelected();
		for (TreeNode n : selected)
		{
			Object value = n.getValue();
			if (value != null && value.getClass().equals(clazz))
			{
				result.add((E) value);
			}
		}
		return result;
	}

	@SuppressWarnings("unused")
	public boolean isSelectionModeEnabled()
	{
		return this.mSelectionModeEnabled;
	}

	private void toggleSelectionMode(TreeNode parent, boolean selectionModeEnabled1)
	{
		toggleSelectionForNode(parent);
		if (parent.isExpanded())
		{
			for (TreeNode node : parent.getChildren())
			{
				toggleSelectionMode(node, selectionModeEnabled1);
			}
		}
	}

	private List<TreeNode> getSelected()
	{
		if (this.mSelectionModeEnabled)
		{
			return getSelected(this.mRoot);
		}
		else
		{
			return new ArrayList<>();
		}
	}

	// TODO Do we need to go through whole tree? Save references or consider collapsed nodes as not selected
	private List<TreeNode> getSelected(TreeNode parent)
	{
		List<TreeNode> result = new ArrayList<>();
		for (TreeNode n : parent.getChildren())
		{
			if (n.isSelected())
			{
				result.add(n);
			}
			result.addAll(getSelected(n));
		}
		return result;
	}

	@SuppressWarnings("unused")
	public void selectAll(boolean skipCollapsed)
	{
		makeAllSelection(true, skipCollapsed);
	}

	private void deselectAll()
	{
		makeAllSelection(false, false);
	}

	private void makeAllSelection(boolean selected, boolean skipCollapsed)
	{
		if (this.mSelectionModeEnabled)
		{
			for (TreeNode node : this.mRoot.getChildren())
			{
				selectNode(node, selected, skipCollapsed);
			}
		}
	}

	@SuppressWarnings("unused")
	public void selectNode(TreeNode node, boolean selected)
	{
		if (this.mSelectionModeEnabled)
		{
			node.setSelected(selected);
			toggleSelectionForNode(node);
		}
	}

	private void selectNode(TreeNode parent, boolean selected, boolean skipCollapsed)
	{
		parent.setSelected(selected);
		toggleSelectionForNode(parent);
		boolean toContinue = !skipCollapsed || parent.isExpanded();
		if (toContinue)
		{
			for (TreeNode node : parent.getChildren())
			{
				selectNode(node, selected, skipCollapsed);
			}
		}
	}

	private void toggleSelectionForNode(TreeNode node)
	{
		TreeNode.Renderer<?> holder = getViewHolderForNode(node);
		if (holder.isInitialized())
		{
			getViewHolderForNode(node).toggleSelectionMode();
		}
	}

	// V I E W H O L D E R

	private TreeNode.Renderer<?> getViewHolderForNode(TreeNode node)
	{
		TreeNode.Renderer<?> viewHolder = node.getRenderer();
		if (viewHolder == null)
		{
			try
			{
				final Object object = this.defaultViewHolderClass.getConstructor(Context.class).newInstance(this.mContext);
				viewHolder = (TreeNode.Renderer<?>) object;
				node.setRenderer(viewHolder);
			}
			catch (Exception e)
			{
				throw new RuntimeException("Could not instantiate class " + this.defaultViewHolderClass); //$NON-NLS-1$
			}
		}
		if (viewHolder.getContainerStyle() <= 0)
		{
			viewHolder.setContainerStyle(this.containerStyle);
		}
		if (viewHolder.getTreeView() == null)
		{
			viewHolder.setTreeView(this);
		}
		return viewHolder;
	}

}
