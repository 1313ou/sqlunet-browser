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
import android.widget.TextView;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.control.Controller;
import org.sqlunet.treeview.control.SimpleController;
import org.sqlunet.treeview.model.TreeNode;

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
	static private final String NODES_PATH_SEPARATOR = ";";
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
	 * Default controller
	 */
	private Class<? extends Controller<?>> defaultControllerClass = SimpleController.class;

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
	private boolean selectable;

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
	 * View factory
	 *
	 * @return view
	 */
	public View getView()
	{
		return getView(-1);
	}

	/**
	 * View factory
	 *
	 * @param style style
	 * @return view
	 */
	private View getView(final int style)
	{
		// top scrollview
		final ViewGroup view;
		if (style > 0)
		{
			final ContextThemeWrapper newContext = new ContextThemeWrapper(this.context, style);
			view = this.use2dScroll ? new ScrollView2D(newContext) : new ScrollView(newContext);
		}
		else
		{
			view = this.use2dScroll ? new ScrollView2D(this.context) : new ScrollView(this.context);
		}

		// context
		Context containerContext = this.context;
		if (this.containerStyle != 0 && this.applyForRoot)
		{
			containerContext = new ContextThemeWrapper(this.context, this.containerStyle);
		}

		// content
		final LinearLayout contentView = new LinearLayout(containerContext, null, this.containerStyle);
		contentView.setId(R.id.tree_view);
		contentView.setOrientation(LinearLayout.VERTICAL);
		view.addView(contentView);

		// root
		this.root.setController(new Controller<Void>(this.context)
		{
			@Override
			public View createNodeView(TreeNode node, Void value)
			{
				return null;
			}

			@Override
			public ViewGroup getChildrenContainerView()
			{
				return contentView;
			}
		});

		expandNode(this.root, false);
		return view;
	}

	// A D D / R E M O V E

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
			final Controller<?> controller = getNodeController(parent);
			addNode(controller.getChildrenContainerView(), node);
		}
	}

	/**
	 * Add node to container
	 *
	 * @param container container
	 * @param node      node
	 */
	private void addNode(final ViewGroup container, final TreeNode node)
	{
		final Controller<?> controller = getNodeController(node);
		assert controller != null;
		final View nodeView = controller.getView();

		// add to container
		container.addView(nodeView);

		// inherit selection mode
		node.setSelectable(this.selectable);

		// listener
		nodeView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// if disabled
				if (!node.isEnabled())
				{
					return;
				}

				// click node listener if node has one
				if (node.getClickListener() != null)
				{
					node.getClickListener().onClick(node, node.getValue());
				}
				// else default
				else if (TreeView.this.nodeClickListener != null)
				{
					TreeView.this.nodeClickListener.onClick(node, node.getValue());
				}

				// toggle node
				toggleNode(node);
			}
		});
	}

	/**
	 * Remove
	 *
	 * @param node node
	 */
	static public void remove(final TreeNode node)
	{
		final Controller<?> controller = node.getController();
		assert controller != null;
		final TreeView treeView = controller.getTreeView();
		assert treeView != null;
		treeView.removeNode(node);
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
				final Controller<?> controller = getNodeController(parent);
				controller.getChildrenContainerView().removeViewAt(index);
			}
		}
	}

	/**
	 * Disable
	 *
	 * @param node node
	 */
	static public void disable(final TreeNode node)
	{
		node.disable();

		final Controller<?> controller = node.getController();
		assert controller != null;
		controller.disable();
	}

	/**
	 * Set node text
	 *
	 * @param node  node
	 * @param value character sequence
	 */
	static public void setNodeValue(final TreeNode node, final CharSequence value)
	{
		// delete node from parent if null value
		if (value == null || value.length() == 0)
		{
			TreeView.remove(node);
			return;
		}

		// update value
		node.setValue(value);

		// update view
		final Controller<?> controller = node.getController();
		final View view = controller.getNodeView();
		if (view != null)
		{
			if (view instanceof TextView)
			{
				final TextView textView = (TextView) view;
				textView.setText(value);
			}
			else
			{
				final TextView textView = (TextView) view.findViewById(R.id.node_value);
				if (textView != null)
				{
					textView.setText(value);
				}
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
	static public void expand(final TreeNode node, boolean includeSubnodes)
	{
		final Controller<?> controller = node.getController();
		assert controller != null;
		final TreeView treeView = controller.getTreeView();
		assert treeView != null;
		treeView.expandNode(node, includeSubnodes);
	}

	/**
	 * Expand
	 *
	 * @param node   node
	 * @param levels number of levels to expand
	 */
	static public void expand(final TreeNode node, int levels)
	{
		final Controller<?> controller = node.getController();
		assert controller != null;
		final TreeView treeView = controller.getTreeView();
		assert treeView != null;
		treeView.expandRelativeLevel(node, levels);
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
		final Controller<?> controller = node.getController();
		assert controller != null;
		final TreeView treeView = controller.getTreeView();
		assert treeView != null;
		treeView.collapseNode(node, includeSubnodes);
	}

	/**
	 * Expand view
	 *
	 * @param view view
	 */
	static private void expand(final View view)
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
	static private void collapse(final View view)
	{
		final int initialHeight = view.getMeasuredHeight();

		final Animation animation = new Animation()
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
		animation.setDuration((int) (initialHeight / view.getContext().getResources().getDisplayMetrics().density));
		view.startAnimation(animation);
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
		for (TreeNode node : this.root.getChildren())
		{
			collapseNode(node, true);
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
		for (TreeNode node : this.root.getChildren())
		{
			expandLevel(node, level);
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
		// collapsibility
		if (!node.isCollapsible())
		{
			return;
		}

		// flag
		node.setExpanded(false);

		final Controller<?> controller = getNodeController(node);

		// display
		if (this.useDefaultAnimation)
		{
			collapse(controller.getChildrenContainerView());
		}
		else
		{
			controller.getChildrenContainerView().setVisibility(View.GONE);
		}

		// fire collapse event
		controller.onExpandEvent(false);

		// subnodes
		if (includeSubnodes)
		{
			for (TreeNode child : node.getChildren())
			{
				collapseNode(child, true);
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
		// flag
		node.setExpanded(true);

		final Controller<?> controller = getNodeController(node);

		// clear all views
		controller.getChildrenContainerView().removeAllViews();

		// fire expand event
		controller.onExpandEvent(true);

		// children
		for (final TreeNode child : node.getChildren())
		{
			// add children node to container view
			addNode(controller.getChildrenContainerView(), child);

			// recurse
			if (child.isExpanded() || includeSubnodes)
			{
				expandNode(child, includeSubnodes);
			}
		}

		// display
		if (this.useDefaultAnimation)
		{
			expand(controller.getChildrenContainerView());
		}
		else
		{
			final View container = controller.getChildrenContainerView();
			container.setVisibility(View.VISIBLE);
			// LucasJue
			container.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
			container.requestLayout();
		}
	}

	// P R O P E R T I E S

	/**
	 * Set default animation
	 *
	 * @param defaultAnimation default animation
	 */
	public void setDefaultAnimation(final boolean defaultAnimation)
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
	private void setDefaultContainerStyle(final int defaultStyle, boolean applyForRoot)
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
	 * Set default controller class
	 *
	 * @param controllerClass default controllerClass
	 */
	public void setDefaultController(final Class<? extends Controller<?>> controllerClass)
	{
		this.defaultControllerClass = controllerClass;
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
		for (TreeNode child : node.getChildren())
		{
			if (openNodes.contains(child.getPath()))
			{
				expandNode(child);
				restoreNodeState(child, openNodes);
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

	// mode

	/**
	 * Get whether selection mode is enabled
	 *
	 * @return whether selection mode is enabled
	 */
	@SuppressWarnings("unused")
	public boolean isSelectable()
	{
		return this.selectable;
	}

	/**
	 * Set selection mode
	 *
	 * @param selectable selection mode enable flag
	 */
	@SuppressWarnings("unused")
	public void setSelectable(final boolean selectable)
	{
		if (!selectable)
		{
			deselectAll();
		}

		this.selectable = selectable;

		// propagate from root
		for (TreeNode node : this.root.getChildren())
		{
			setSelectable(node, selectable);
		}
	}

	/**
	 * Set node selectable
	 *
	 * @param node       node
	 * @param selectable selectable flag
	 */
	private void setSelectable(final TreeNode node, final boolean selectable)
	{
		fireNodeSelected(node, selectable);

		// propagate
		if (node.isExpanded())
		{
			for (TreeNode child : node.getChildren())
			{
				setSelectable(child, selectable);
			}
		}
	}

	// selected nodes

	/**
	 * Get selected nodes
	 *
	 * @return selected nodes
	 */
	private List<TreeNode> getAllSelected()
	{
		if (this.selectable)
		{
			return getAllSelected(this.root);
		}
		else
		{
			return new ArrayList<>();
		}
	}

	/**
	 * Get selected nodes from node
	 *
	 * @param node node
	 * @return selected nodes
	 */
	private List<TreeNode> getAllSelected(final TreeNode node)
	{
		List<TreeNode> result = new ArrayList<>();
		for (TreeNode child : node.getChildren())
		{
			if (child.isSelected())
			{
				result.add(child);
			}
			result.addAll(getAllSelected(child));
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
		List<TreeNode> selected = getAllSelected();
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

	// select

	/**
	 * Select all
	 *
	 * @param skipCollapsed whether to skip collapsed nod
	 */
	@SuppressWarnings("unused")
	public void selectAll(final boolean skipCollapsed)
	{
		selectAll(true, skipCollapsed);
	}

	/**
	 * Deselect all
	 */
	@SuppressWarnings("WeakerAccess")
	public void deselectAll()
	{
		selectAll(false, false);
	}

	/**
	 * Select/deselect all
	 *
	 * @param selected      selected flag
	 * @param skipCollapsed whether to skip collapsed node
	 */
	private void selectAll(final boolean selected, boolean skipCollapsed)
	{
		if (this.selectable)
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
		if (this.selectable)
		{
			node.setSelected(selected);
			fireNodeSelected(node, selected);
		}
	}

	/**
	 * Select node and children
	 *
	 * @param node     node
	 * @param selected selected flag
	 */
	private void selectNode(final TreeNode node, final boolean selected, final boolean skipCollapsed)
	{
		node.setSelected(selected);
		fireNodeSelected(node, selected);

		// propagation
		boolean propagate = !skipCollapsed || node.isExpanded();
		if (propagate)
		{
			for (TreeNode child : node.getChildren())
			{
				selectNode(child, selected, skipCollapsed);
			}
		}
	}

	/**
	 * Fire node selection mode event
	 *
	 * @param node     node
	 * @param selected selection mode flag
	 */
	private void fireNodeSelected(final TreeNode node, final boolean selected)
	{
		final Controller<?> controller = getNodeController(node);
		assert controller != null;
		if (controller.isInitialized())
		{
			controller.onSelectedEvent(selected);
		}
	}

	// C O N T R O L L E R

	/**
	 * Get controller for node
	 *
	 * @param node node
	 * @return controller
	 */
	private Controller<?> getNodeController(final TreeNode node)
	{
		Controller<?> controller = node.getController();
		if (controller == null)
		{
			try
			{
				final Object object = this.defaultControllerClass.getConstructor(Context.class).newInstance(this.context);
				controller = (Controller<?>) object;
				node.setController(controller);
			}
			catch (Exception e)
			{
				throw new RuntimeException("Could not instantiate class " + this.defaultControllerClass);
			}
		}
		if (controller.getContainerStyle() <= 0)
		{
			controller.setContainerStyle(this.containerStyle);
		}
		if (controller.getTreeView() == null)
		{
			controller.setTreeView(this);
		}
		return controller;
	}
}
