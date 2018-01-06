package org.sqlunet.treeview.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
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

	static private final float ANIMATION_DP_PER_MS = 3.f;

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
	private boolean useAnimation = true;

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

	// R O O T

	/**
	 * Get root
	 *
	 * @return root
	 */
	public TreeNode getRoot()
	{
		return this.root;
	}

	// V I E W

	/**
	 * View factory
	 *
	 * @return view
	 */
	@NonNull
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
	@NonNull
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
			@Nullable
			@Override
			public View createNodeView(TreeNode node, Void value)
			{
				return null;
			}

			@NonNull
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
	public void addNode(@NonNull final TreeNode parent, @NonNull final TreeNode node)
	{
		// tree
		parent.addChild(node);

		// view
		if (parent.isExpanded())
		{
			final Controller<?> controller = getNodeController(parent);
			assert controller != null;
			final ViewGroup viewGroup = controller.getChildrenContainerView();
			assert viewGroup != null;
			addNode(viewGroup, node);
		}
	}

	/**
	 * Add node to container
	 *
	 * @param container container
	 * @param node      node
	 */
	private void addNode(@NonNull final ViewGroup container, @NonNull final TreeNode node)
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
	static public void remove(@NonNull final TreeNode node)
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
	public void removeNode(@NonNull final TreeNode node)
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
				final ViewGroup viewGroup = controller.getChildrenContainerView();
				assert viewGroup != null;
				viewGroup.removeViewAt(index);
			}
		}
	}

	/**
	 * Disable
	 *
	 * @param node node
	 */
	static public void disable(@NonNull final TreeNode node)
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
	static public void setNodeValue(@NonNull final TreeNode node, @Nullable final CharSequence value)
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
		assert controller != null;
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
				final TextView textView = view.findViewById(R.id.node_value);
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
	static public void expand(@NonNull final TreeNode node, @SuppressWarnings("SameParameterValue") boolean includeSubnodes)
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
	static public void expand(@NonNull final TreeNode node, int levels)
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
	static public void collapse(@NonNull final TreeNode node, boolean includeSubnodes)
	{
		final Controller<?> controller = node.getController();
		assert controller != null;
		final TreeView treeView = controller.getTreeView();
		assert treeView != null;
		treeView.collapseNode(node, includeSubnodes);
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
	private void expandLevel(@NonNull final TreeNode node, final int level)
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
	private void expandRelativeLevel(@NonNull final TreeNode node, final int levels)
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
	private void expandNode(@NonNull final TreeNode node)
	{
		expandNode(node, false);
	}

	/**
	 * Collapse node
	 *
	 * @param node node
	 */
	@SuppressWarnings("unused")
	public void collapseNode(@NonNull final TreeNode node)
	{
		collapseNode(node, false);
	}

	/**
	 * Toggle node
	 *
	 * @param node node
	 */
	private void toggleNode(@NonNull final TreeNode node)
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
	private void collapseNode(@NonNull final TreeNode node, final boolean includeSubnodes)
	{
		// collapsibility
		if (!node.isCollapsible())
		{
			return;
		}

		// flag
		node.setExpanded(false);

		final Controller<?> controller = getNodeController(node);
		assert controller != null;

		// display
		final ViewGroup viewGroup = controller.getChildrenContainerView();
		assert viewGroup != null;
		if (this.useAnimation)
		{
			animatedCollapse(viewGroup);
		}
		else
		{
			collapse(viewGroup);
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
	private void expandNode(@NonNull final TreeNode node, boolean includeSubnodes)
	{
		// flag
		node.setExpanded(true);

		final Controller<?> controller = getNodeController(node);
		assert controller != null;
		final ViewGroup viewGroup = controller.getChildrenContainerView();
		assert viewGroup != null;

		// clear all views
		viewGroup.removeAllViews();

		// fire expand event
		controller.onExpandEvent(true);

		// children
		for (final TreeNode child : node.getChildren())
		{
			// add children node to container view
			addNode(viewGroup, child);

			// recurse
			if (child.isExpanded() || includeSubnodes)
			{
				expandNode(child, includeSubnodes);
			}
		}

		// display
		if (this.useAnimation)
		{
			animatedExpand(viewGroup);
		}
		else
		{
			expand(viewGroup);
		}
	}

	// E X P A N D / C O L L A P S E   I M P L E M E N T A T I O N

	/**
	 * Expand view (child container)
	 *
	 * @param view view
	 */
	static private void expand(@NonNull final View view)
	{
		view.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
		view.requestLayout();
		view.setVisibility(View.VISIBLE);
	}

	/**
	 * Collapse view (child container)
	 *
	 * @param view view
	 */
	static private void collapse(@NonNull final View view)
	{
		view.setVisibility(View.GONE);
	}

	/**
	 * Animated expand view animated (child container)
	 *
	 * @param view view
	 */
	static private void animatedExpand(@NonNull final View view)
	{
		view.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		final int targetHeight = view.getMeasuredHeight();
		int duration = (int) (ANIMATION_DP_PER_MS * targetHeight / view.getContext().getResources().getDisplayMetrics().density);

		ValueAnimator.setFrameDelay(250);
		final ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
		animator.setRepeatCount(0);
		animator.setDuration(duration);
		animator.setStartDelay(0);
		animator.setInterpolator(new LinearInterpolator());
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator)
			{
				float value = (float) valueAnimator.getAnimatedValue();
				view.getLayoutParams().height = value == 1 ? LayoutParams.WRAP_CONTENT : (int) (targetHeight * value);
				view.requestLayout();
			}
		});
		animator.addListener(new Animator.AnimatorListener()
		{
			@Override
			public void onAnimationStart(final Animator animator0)
			{
				view.getLayoutParams().height = 0;
				view.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationEnd(final Animator animator0)
			{
				view.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
				view.requestLayout();
			}

			@Override
			public void onAnimationCancel(final Animator animator0)
			{
				view.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
				view.requestLayout();
			}

			@Override
			public void onAnimationRepeat(final Animator animator0)
			{
			}
		});
		animator.start();
	}

	/**
	 * Animated collapse view (child container)
	 *
	 * @param view view
	 */
	static private void animatedCollapse(@NonNull final View view)
	{
		final int initialHeight = view.getMeasuredHeight();
		int duration = (int) (ANIMATION_DP_PER_MS * initialHeight / view.getContext().getResources().getDisplayMetrics().density);

		ValueAnimator.setFrameDelay(250);
		final ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
		animator.setRepeatCount(0);
		animator.setDuration(duration);
		animator.setStartDelay(0);
		animator.setInterpolator(new LinearInterpolator());
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator)
			{
				float value = (float) valueAnimator.getAnimatedValue();
				view.getLayoutParams().height = initialHeight - (int) (initialHeight * value);
				view.requestLayout();
			}
		});
		animator.addListener(new Animator.AnimatorListener()
		{
			@Override
			public void onAnimationStart(final Animator animator0)
			{
			}

			@Override
			public void onAnimationEnd(final Animator animator0)
			{
				view.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationCancel(final Animator animator0)
			{
				view.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(final Animator animator0)
			{
			}
		});
		animator.start();
	}

	// P R O P E R T I E S

	/**
	 * Set animation use
	 *
	 * @param useAnimation use animation for expand/collapse
	 */
	@SuppressWarnings("unused")
	public void setAnimation(final boolean useAnimation)
	{
		this.useAnimation = useAnimation;
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
	@NonNull
	@SuppressWarnings("unused")
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
	@SuppressWarnings("unused")
	public void restoreState(@Nullable final String saveState)
	{
		if (saveState != null && !TextUtils.isEmpty(saveState))
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
	private void restoreNodeState(@NonNull final TreeNode node, @NonNull final Set<String> openNodes)
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
	private void getSaveState(@NonNull final TreeNode root, @NonNull final StringBuilder sb)
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
	private void setSelectable(@NonNull final TreeNode node, final boolean selectable)
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
	@NonNull
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
	@NonNull
	private List<TreeNode> getAllSelected(@NonNull final TreeNode node)
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
	@NonNull
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
	public void selectNode(@NonNull final TreeNode node, boolean selected)
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
	private void selectNode(@NonNull final TreeNode node, final boolean selected, final boolean skipCollapsed)
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
	private void fireNodeSelected(@NonNull final TreeNode node, final boolean selected)
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
	private Controller<?> getNodeController(@NonNull final TreeNode node)
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
