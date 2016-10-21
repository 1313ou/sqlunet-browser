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
 *
 * @author Bogdan Melnychuk on 2/10/15.
 */
public class TreeView
{
	private static final String NODES_PATH_SEPARATOR = ";"; //$NON-NLS-1$

	/**
	 * Root node
	 */
	private final TreeNode root;

	/**
	 * Context
	 */
	private final Context context;

	/**
	 * Container style
	 */
	private int containerStyle = 0;

	/**
	 * Default renderer
	 */
	private Class<? extends TreeNode.Renderer<?>> defaultRendererClass = SimpleRenderer.class;

	/**
	 * Node click listener
	 */
	private TreeNode.TreeNodeClickListener nodeClickListener;

	/**
	 * Apply for root
	 */
	private boolean applyForRoot;

	/**
	 * Selection mode enabled
	 */
	private boolean enableSelectionMode;

	/**
	 * Use default animation
	 */
	private boolean useDefaultAnimation = false;

	/**
	 * Use 2D scrolling
	 */
	private boolean use2dScroll = false;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param context context
	 * @param root    root
	 */
	public TreeView(final Context context, final TreeNode root)
	{
		this.root = root;
		this.context = context;
	}

	// V I E W

	/**
	 * Get view
	 *
	 * @param style style
	 * @return view
	 */
	private View getView(final int style)
	{
		final ViewGroup view;
		if (style > 0)
		{
			ContextThemeWrapper newContext = new ContextThemeWrapper(this.context, style);
			view = this.use2dScroll ? new TwoDScrollView(newContext) : new ScrollView(newContext);
		}
		else
		{
			view = this.use2dScroll ? new TwoDScrollView(this.context) : new ScrollView(this.context);
		}

		Context containerContext = this.context;
		if (this.containerStyle != 0 && this.applyForRoot)
		{
			containerContext = new ContextThemeWrapper(this.context, this.containerStyle);
		}

		final LinearLayout viewTreeItems = new LinearLayout(containerContext, null, this.containerStyle);
		viewTreeItems.setId(R.id.tree_items);
		viewTreeItems.setOrientation(LinearLayout.VERTICAL);
		view.addView(viewTreeItems);

		this.root.setRenderer(new TreeNode.Renderer<Void>(this.context)
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

		expandNode(this.root, false);
		return view;
	}

	/**
	 * Get view
	 *
	 * @return view
	 */
	public View getView()
	{
		return getView(-1);
	}

	// A D D / R E M O V E

	/**
	 * Add node to container
	 *
	 * @param container container
	 * @param node      node
	 */
	private void addNode(final ViewGroup container, final TreeNode node)
	{
		final TreeNode.Renderer<?> viewHolder = getNodeRenderer(node);
		final View nodeView = viewHolder.getView();
		container.addView(nodeView);

		// selection
		if (this.enableSelectionMode)
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

	/**
	 * Add node to parent
	 *
	 * @param parent parent node
	 * @param node   node to add
	 */
	@SuppressWarnings("unused")
	public void addNode(final TreeNode parent, final TreeNode node)
	{
		// tree
		parent.addChild(node);

		// view
		if (parent.isExpanded())
		{
			final TreeNode.Renderer<?> parentViewHolder = getNodeRenderer(parent);
			addNode(parentViewHolder.getNodeItemsView(), node);
		}
	}

	/**
	 * Remove node
	 *
	 * @param node node to remove
	 */
	@SuppressWarnings("unused")
	public void removeNode(final TreeNode node)
	{
		if (node.getParent() != null)
		{
			// tree
			TreeNode parent = node.getParent();
			int index = parent.deleteChild(node);

			// view
			if (parent.isExpanded() && index >= 0)
			{
				final TreeNode.Renderer<?> parentViewHolder = getNodeRenderer(parent);
				parentViewHolder.getNodeItemsView().removeViewAt(index);
			}
		}
	}

	// E X P A N D  /  C O L L A P S E

	/**
	 * Expand
	 *
	 * @param node            node
	 * @param includeSubnodes whether to include subnodes
	 */
	static public void expand(final TreeNode node, @SuppressWarnings("SameParameterValue") boolean includeSubnodes)
	{
		node.getRenderer().getTreeView().expandNode(node, includeSubnodes);
	}

	/**
	 * Expand
	 *
	 * @param node   node
	 * @param levels number of levels to expand
	 */
	static public void expand(final TreeNode node, int levels)
	{
		node.getRenderer().getTreeView().expandRelativeLevel(node, levels);
	}

	/**
	 * Collapse
	 *
	 * @param node            node
	 * @param includeSubnodes whether to include subnodes
	 */
	@SuppressWarnings("unused")
	static public void collapse(final TreeNode node, boolean includeSubnodes)
	{
		node.getRenderer().getTreeView().collapseNode(node, includeSubnodes);
	}

	/**
	 * Expand view
	 *
	 * @param view view
	 */
	private static void expand(final View view)
	{
		view.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		final int targetHeight = view.getMeasuredHeight();

		view.getLayoutParams().height = 0;
		view.setVisibility(View.VISIBLE);
		Animation animation = new Animation()
		{
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t)
			{
				view.getLayoutParams().height = interpolatedTime == 1 ? LayoutParams.WRAP_CONTENT : (int) (targetHeight * interpolatedTime);
				view.requestLayout();
			}

			@Override
			public boolean willChangeBounds()
			{
				return true;
			}
		};

		// 1dp/ms
		animation.setDuration((int) (targetHeight / view.getContext().getResources().getDisplayMetrics().density));
		view.startAnimation(animation);
	}

	/**
	 * Collapse view
	 *
	 * @param view view
	 */
	private static void collapse(final View view)
	{
		final int initialHeight = view.getMeasuredHeight();

		Animation a = new Animation()
		{
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t)
			{
				if (interpolatedTime == 1)
				{
					view.setVisibility(View.GONE);
				}
				else
				{
					view.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
					view.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds()
			{
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int) (initialHeight / view.getContext().getResources().getDisplayMetrics().density));
		view.startAnimation(a);
	}

	/**
	 * Expand all
	 */
	@SuppressWarnings("unused")
	public void expandAll()
	{
		expandNode(this.root, true);
	}

	/**
	 * Collapse all
	 */
	private void collapseAll()
	{
		for (TreeNode n : this.root.getChildren())
		{
			collapseNode(n, true);
		}
	}

	/**
	 * Expand from root to level
	 *
	 * @param level level number
	 */
	@SuppressWarnings("unused")
	public void expandLevel(final int level)
	{
		for (TreeNode n : this.root.getChildren())
		{
			expandLevel(n, level);
		}
	}

	/**
	 * Expand to level
	 *
	 * @param node  node
	 * @param level level number
	 */
	private void expandLevel(final TreeNode node, final int level)
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

	/**
	 * Expand relative level
	 *
	 * @param node   node
	 * @param levels number of levels
	 */
	private void expandRelativeLevel(final TreeNode node, final int levels)
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

	/**
	 * Expand node
	 *
	 * @param node node
	 */
	private void expandNode(final TreeNode node)
	{
		expandNode(node, false);
	}

	/**
	 * Collapse node
	 *
	 * @param node node
	 */
	@SuppressWarnings("unused")
	public void collapseNode(final TreeNode node)
	{
		collapseNode(node, false);
	}

	/**
	 * Toggle node
	 *
	 * @param node node
	 */
	private void toggleNode(final TreeNode node)
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

	/**
	 * Collapse node
	 *
	 * @param node            node
	 * @param includeSubnodes whether to include subnodes
	 */
	private void collapseNode(final TreeNode node, final boolean includeSubnodes)
	{
		node.setExpanded(false);
		TreeNode.Renderer<?> nodeViewHolder = getNodeRenderer(node);

		if (this.useDefaultAnimation)
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

	/**
	 * Expand node
	 *
	 * @param node            node
	 * @param includeSubnodes whether to include subnodes
	 */
	private void expandNode(final TreeNode node, boolean includeSubnodes)
	{
		node.setExpanded(true);
		final TreeNode.Renderer<?> parentViewHolder = getNodeRenderer(node);
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
		if (this.useDefaultAnimation)
		{
			expand(parentViewHolder.getNodeItemsView());
		}
		else
		{
			parentViewHolder.getNodeItemsView().setVisibility(View.VISIBLE);
		}
	}

	// P R O P E R T I E S

	/**
	 * Set default animation
	 *
	 * @param defaultAnimation default animation
	 */
	public void setDefaultAnimation(@SuppressWarnings("SameParameterValue") final boolean defaultAnimation)
	{
		this.useDefaultAnimation = defaultAnimation;
	}

	/**
	 * Set default container style
	 *
	 * @param defaultStyle default container style
	 */
	public void setDefaultContainerStyle(final int defaultStyle)
	{
		setDefaultContainerStyle(defaultStyle, false);
	}

	/**
	 * Set default container style
	 *
	 * @param defaultStyle default container style
	 * @param applyForRoot apply for root
	 */
	private void setDefaultContainerStyle(final int defaultStyle, @SuppressWarnings("SameParameterValue") boolean applyForRoot)
	{
		this.containerStyle = defaultStyle;
		this.applyForRoot = applyForRoot;
	}

	/**
	 * Use 2D scroll
	 *
	 * @param use2dScroll use 2D scroll flag
	 */
	@SuppressWarnings("unused")
	public void setUse2dScroll(final boolean use2dScroll)
	{
		this.use2dScroll = use2dScroll;
	}

	/**
	 * Get use 2D scroll
	 *
	 * @return use2dScroll use 2D scroll flag
	 */
	@SuppressWarnings("unused")
	public boolean is2dScrollEnabled()
	{
		return this.use2dScroll;
	}

	/**
	 * Set default renderer
	 *
	 * @param renderer default renderer
	 */
	public void setDefaultRenderer(final Class<? extends TreeNode.Renderer<?>> renderer)
	{
		this.defaultRendererClass = renderer;
	}

	/**
	 * Set default on-click listener
	 *
	 * @param listener on-click listener
	 */
	@SuppressWarnings("unused")
	public void setDefaultNodeClickListener(final TreeNode.TreeNodeClickListener listener)
	{
		this.nodeClickListener = listener;
	}

	// S T A T E

	/**
	 * Get save state
	 *
	 * @return save state
	 */
	public String getSaveState()
	{
		final StringBuilder builder = new StringBuilder();
		getSaveState(this.root, builder);
		if (builder.length() > 0)
		{
			builder.setLength(builder.length() - 1);
		}
		return builder.toString();
	}

	/**
	 * Restore save state
	 *
	 * @param saveState save state
	 */
	public void restoreState(final String saveState)
	{
		if (!TextUtils.isEmpty(saveState))
		{
			collapseAll();
			final String[] openNodesArray = saveState.split(NODES_PATH_SEPARATOR);
			final Set<String> openNodes = new HashSet<>(Arrays.asList(openNodesArray));
			restoreNodeState(this.root, openNodes);
		}
	}

	/**
	 * Restore node state
	 *
	 * @param node      node
	 * @param openNodes open nodes
	 */
	private void restoreNodeState(final TreeNode node, final Set<String> openNodes)
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

	/**
	 * Get save state
	 *
	 * @param root root
	 * @param sb   builder
	 */
	private void getSaveState(final TreeNode root, final StringBuilder sb)
	{
		for (TreeNode node : root.getChildren())
		{
			if (node.isExpanded())
			{
				sb.append(node.getPath());
				sb.append(NODES_PATH_SEPARATOR);
				getSaveState(node, sb);
			}
		}
	}

	// S E L E C T I O N

	/**
	 * Get whether selection mode is enabled
	 *
	 * @return whether selection mode is enabled
	 */
	@SuppressWarnings("unused")
	public boolean isEnableSelectionMode()
	{
		return this.enableSelectionMode;
	}

	/**
	 * Set selection mode enabled
	 *
	 * @param flag selection mode enabled flag
	 */
	@SuppressWarnings("unused")
	public void setEnableSelectionMode(final boolean flag)
	{
		if (!flag)
		{
			// TODO fix double iteration over tree
			deselectAll();
		}

		this.enableSelectionMode = flag;

		for (TreeNode node : this.root.getChildren())
		{
			toggleSelectionMode(node, flag);
		}

	}

	/**
	 * Toggle selection mode
	 *
	 * @param parent       parent
	 * @param childrenFlag flag for children
	 */
	private void toggleSelectionMode(final TreeNode parent, final boolean childrenFlag)
	{
		toggleSelectionForNode(parent);
		if (parent.isExpanded())
		{
			for (TreeNode node : parent.getChildren())
			{
				toggleSelectionMode(node, childrenFlag);
			}
		}
	}

	/**
	 * Get selected nodes
	 *
	 * @return selected nodes
	 */
	private List<TreeNode> getSelected()
	{
		if (this.enableSelectionMode)
		{
			return getSelected(this.root);
		}
		else
		{
			return new ArrayList<>();
		}
	}

	/**
	 * Get selected nodes from parent
	 *
	 * @param parent parent
	 * @return selected nodes
	 */
	private List<TreeNode> getSelected(final TreeNode parent)
	{
		// TODO Do we need to go through whole tree? Save references or consider collapsed nodes as not selected
		List<TreeNode> result = new ArrayList<>();
		for (TreeNode node : parent.getChildren())
		{
			if (node.isSelected())
			{
				result.add(node);
			}
			result.addAll(getSelected(node));
		}
		return result;
	}

	/**
	 * Get selected values that are instances of class
	 *
	 * @param valueClass value class
	 * @param <E>        value type
	 * @return values
	 */
	@SuppressWarnings({"unchecked", "unused"})
	public <E> List<E> getSelectedValues(final Class<E> valueClass)
	{
		List<E> result = new ArrayList<>();
		List<TreeNode> selected = getSelected();
		for (TreeNode node : selected)
		{
			Object value = node.getValue();
			if (value != null && value.getClass().equals(valueClass))
			{
				result.add((E) value);
			}
		}
		return result;
	}

	/**
	 * Select all
	 *
	 * @param skipCollapsed whether to skip collapsed nod
	 */
	@SuppressWarnings("unused")
	public void selectAll(final boolean skipCollapsed)
	{
		propagateSelection(true, skipCollapsed);
	}

	/**
	 * Deselect all
	 */
	private void deselectAll()
	{
		propagateSelection(false, false);
	}

	/**
	 * Propagate selection
	 *
	 * @param selected      selected flag
	 * @param skipCollapsed whether to skip collapsed node
	 */
	private void propagateSelection(final boolean selected, boolean skipCollapsed)
	{
		if (this.enableSelectionMode)
		{
			for (TreeNode node : this.root.getChildren())
			{
				selectNode(node, selected, skipCollapsed);
			}
		}
	}

	/**
	 * Select node
	 *
	 * @param node     node
	 * @param selected selected flag
	 */
	@SuppressWarnings("unused")
	public void selectNode(final TreeNode node, boolean selected)
	{
		if (this.enableSelectionMode)
		{
			node.setSelected(selected);
			toggleSelectionForNode(node);
		}
	}

	/**
	 * Select node and children
	 *
	 * @param parent   parent node
	 * @param selected selected flag
	 */
	private void selectNode(final TreeNode parent, final boolean selected, final boolean skipCollapsed)
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

	/**
	 * Toggle selection for node
	 *
	 * @param node node
	 */
	private void toggleSelectionForNode(final TreeNode node)
	{
		TreeNode.Renderer<?> renderer = getNodeRenderer(node);
		if (renderer.isInitialized())
		{
			getNodeRenderer(node).toggleSelectionMode();
		}
	}

	// R E N D E R E R

	/**
	 * Get renderer for node
	 *
	 * @param node node
	 * @return renderer
	 */
	private TreeNode.Renderer<?> getNodeRenderer(final TreeNode node)
	{
		TreeNode.Renderer<?> renderer = node.getRenderer();
		if (renderer == null)
		{
			try
			{
				final Object object = this.defaultRendererClass.getConstructor(Context.class).newInstance(this.context);
				renderer = (TreeNode.Renderer<?>) object;
				node.setRenderer(renderer);
			}
			catch (Exception e)
			{
				throw new RuntimeException("Could not instantiate class " + this.defaultRendererClass); //$NON-NLS-1$
			}
		}
		if (renderer.getContainerStyle() <= 0)
		{
			renderer.setContainerStyle(this.containerStyle);
		}
		if (renderer.getTreeView() == null)
		{
			renderer.setTreeView(this);
		}
		return renderer;
	}
}
