/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.treeview.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.sqlunet.treeview.R;
import org.sqlunet.treeview.control.CompositeValue;
import org.sqlunet.treeview.control.Controller;
import org.sqlunet.treeview.control.RootController;
import org.sqlunet.treeview.model.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

/* @formatter:off */
/*
NestedScrollView/ScrollView2D
	LinearLayout id/treeview
		SubtreeView
			RelativeLayout id/node_label
				LinearLayout
					AppCompatImageView id/node_junction
					AppCompatImageView id/node_icon
					AppCompatTextView id/node_value
			LinearLayout id/node_children
				SubtreeView
					RelativeLayout id/node_label
						TextView
					LinearLayout id/node_children
				SubtreeView
					RelativeLayout id/node_label
						TextView
					LinearLayout id/node_children
 */
/* @formatter:on */

/**
 * Tree view
 *
 * @author Bogdan Melnychuk on 2/10/15.
 * @author Bernard Bou
 */
public class TreeView
{
	private static final String TAG = "TreeView";

	static private final String NODES_PATH_SEPARATOR = ";";

	static private final float ANIMATION_DP_PER_MS = 3.f;

	static private final String PREF_SCROLL_2D = "pref_scroll_2d";

	static private final String PREF_USE_ANIMATION = "pref_use_animation";

	/**
	 * Root node
	 */
	private final TreeNode root;

	/**
	 * (Top) scroll view
	 */
	private View view;

	/**
	 * Context
	 */
	private final Context context;

	/**
	 * Container style
	 */
	private int containerStyle = 0;

	/**
	 * Container style applies to root
	 */
	private boolean containerStyleAppliesToRoot;

	/**
	 * Node click listener
	 */
	private TreeNode.TreeNodeClickListener nodeClickListener;

	/**
	 * Selection mode enabled
	 */
	private boolean selectable;

	/**
	 * Use default animation
	 */
	private final boolean useAnimation;

	/**
	 * Use 2D scrolling
	 */
	private final boolean use2dScroll;

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
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		this.useAnimation = prefs.getBoolean(PREF_USE_ANIMATION, true);
		this.use2dScroll = prefs.getBoolean(PREF_SCROLL_2D, false);
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
	public View makeView()
	{
		return makeView(0);
	}

	/**
	 * View factory
	 *
	 * @param style style
	 * @return view
	 */
	@NonNull
	private View makeView(@SuppressWarnings("SameParameterValue") final int style)
	{
		Log.d(TAG, "Make tree view");

		// top scrollview
		final ViewGroup view;
		if (style > 0)
		{
			final ContextThemeWrapper newContext = new ContextThemeWrapper(this.context, style);
			view = this.use2dScroll ? new ScrollView2D(newContext) : new NestedScrollView(newContext);
		}
		else
		{
			view = this.use2dScroll ? new ScrollView2D(this.context) : new NestedScrollView(this.context);
		}

		// context
		Context containerContext = this.context;
		if (this.containerStyle != 0 && this.containerStyleAppliesToRoot)
		{
			containerContext = new ContextThemeWrapper(this.context, this.containerStyle);
		}

		// content
		final LinearLayout contentView = new LinearLayout(containerContext, null, this.containerStyle);
		contentView.setId(R.id.tree_view);
		contentView.setOrientation(LinearLayout.VERTICAL);
		contentView.setVisibility(View.GONE);
		view.addView(contentView);

		// root
		final RootController rootController = (RootController) this.root.getController();
		rootController.setContentView(contentView);

		// keep reference
		// TODO
		this.view = view;

		return view;
	}

	// M O D I F Y

	/**
	 * Update
	 * Update node view leaving children unchanged
	 *
	 * @param node node
	 */
	public void update(@NonNull final TreeNode node)
	{
		final Controller controller = node.getController();
		final View nodeView = controller.getNodeView();
		final SubtreeView subtreeView = controller.getSubtreeView();
		if (subtreeView != null && nodeView != null)
		{
			// update node reference
			controller.attachNode(node);
			subtreeView.setTag(node);

			// remove current node subtreeView
			subtreeView.removeNodeView(nodeView);

			// new node view
			@SuppressWarnings("unchecked") final View newNodeView = controller.createNodeView(this.context, node, node.getValue());
			assert newNodeView != null;
			controller.setNodeView(newNodeView);

			// insert new node subtreeView
			subtreeView.insertNodeView(newNodeView);
		}
	}

	/**
	 * Set node text
	 *
	 * @param node  node
	 * @param value character sequence
	 */
	@SuppressWarnings("unused")
	public void setNodeValue(@NonNull final TreeNode node, @Nullable final CharSequence value)
	{
		// delete node from parent if null value
		if (value == null || value.length() == 0)
		{
			remove(node);
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
				final TextView textView = view.findViewById(R.id.node_value);
				if (textView != null)
				{
					textView.setText(value);
				}
			}
		}
	}

	/**
	 * Deadend
	 *
	 * @param node node
	 */
	public void deadend(@NonNull final TreeNode node)
	{
		node.setEnabled(false);

		final Controller<?> controller = node.getController();
		controller.deadend();
	}

	// E X P A N D   S T A T U S

	/**
	 * Get whether node is expanded
	 *
	 * @return whether node is expanded
	 */
	static public boolean isExpanded(final TreeNode node)
	{
		final Controller controller = node.getController();
		final ViewGroup childrenView = controller.getChildrenView();
		if (childrenView == null)
		{
			return false;
		}
		int visibility = childrenView.getVisibility();
		return visibility != View.GONE;
	}

	// A D D / R E M O V E

	/**
	 * Add node to parent (both tree model and view)
	 *
	 * @param parent parent node
	 * @param node   node to add
	 */
	@SuppressWarnings("unused")
	public void add(@NonNull final TreeNode parent, @NonNull final TreeNode node)
	{
		// tree
		parent.addChild(node);

		// view
		if (isExpanded(parent))
		{
			final Controller<?> parentController = parent.getController();
			final ViewGroup viewGroup = parentController.getChildrenView();
			assert viewGroup != null;
			int index = parent.indexOf(node);
			addSubtreeView(viewGroup, node, -1);
		}
	}

	/**
	 * Remove (both tree model and view)
	 *
	 * @param node node
	 */
	public void remove(@NonNull final TreeNode node)
	{
		TreeNode parent = node.getParent();
		if (parent != null)
		{
			// view
			removeSubtreeView(node);

			// tree
			parent.deleteChild(node);
		}
	}

	// A D D / R E M O V E  V I E W S

	/**
	 * Add node to children view (view only, does not affect tree model)
	 *
	 * @param childrenView children view
	 * @param node         node
	 * @param atIndex      insert-at index
	 */
	synchronized public void addSubtreeView(@NonNull final ViewGroup childrenView, @NonNull final TreeNode node, int atIndex)
	{
		Log.d(TAG, "Insert subtree view at index " + atIndex + " for node " + node + " count=" + childrenView.getChildCount());
		final Controller<?> controller = node.getController();
		View subtreeView = controller.getSubtreeView();
		if (subtreeView == null)
		{
			subtreeView = controller.createView(this.context, this.containerStyle);
		}
		final View view = subtreeView;

		// remove from parent
		ViewParent parent = view.getParent();
		if (parent != null)
		{
			final ViewGroup group = (ViewGroup) parent;
			group.removeView(view);
		}

		// index
		if (atIndex != -1)
		{
			int n = childrenView.getChildCount();
			int i = 0;
			for (; i < n; i++)
			{
				View child = childrenView.getChildAt(i);
				Object tag = child.getTag();
				TreeNode node2 = (TreeNode) tag;
				TreeNode parent2 = node2.getParent();
				int j = parent2.indexOf(node2);
				if (j >= atIndex)
				{
					break;
				}
			}
			atIndex = i;
			if (atIndex == n)
			{
				atIndex = -1;
			}
			else if (atIndex > n)
			{
				// Log.e(TAG, "Illegal index " + node + " " + atIndex + " on " + n);
				throw new RuntimeException("Illegal index " + node + " " + atIndex + " on " + n);
			}
		}

		//TODO scroll when dimensions are available
		/*
		final ViewTreeObserver viewTreeObserver = childrenView.getViewTreeObserver();
		viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
		{
			@Override
			public boolean onPreDraw()
			{
				childrenView.getViewTreeObserver().removeOnPreDrawListener(this);

				int h = view.getHeight();
				if (h > 0)
				{
					Log.d(TAG, "Scroll by " + h);
					TreeView.this.view.scrollBy(0, h);
					//((NestedScrollView)TreeView.this.view).smoothScrollBy(0, h);
					return false; // cancel the current drawing pass
				}
				return true; // proceed with the current drawing pass
			}
		});
		*/

		// add to children view
		childrenView.addView(view, atIndex);

		// inherit selection mode
		node.setSelectable(this.selectable);

		// listener
		view.setOnClickListener(v -> {

			// if disabled
			if (!node.isEnabled())
			{
				return;
			}

			// if deadend
			if (node.isDeadend())
			{
				return;
			}

			// click node listener if node has one
			if (node.getClickListener() != null)
			{
				node.getClickListener().onClick(node, node.getValue());
			}
			// else default
			else if (this.nodeClickListener != null)
			{
				this.nodeClickListener.onClick(node, node.getValue());
			}

			// toggle node
			toggleNode(node);
		});
	}

	/**
	 * Remove node (view only, does not affect tree model)
	 *
	 * @param node node to remove
	 */
	@SuppressWarnings("unused")
	synchronized private void removeSubtreeView(@NonNull final TreeNode node)
	{
		Log.d(TAG, "Remove subtree view for node " + node);
		TreeNode parent = node.getParent();
		if (parent != null)
		{
			// view
			if (isExpanded(parent))
			{
				final Controller<?> parentController = parent.getController();
				final ViewGroup viewGroup = parentController.getChildrenView();
				assert viewGroup != null;

				final Controller<?> childController = node.getController();
				final View view = childController.getSubtreeView();
				assert view != null;

				int index = viewGroup.indexOfChild(view);
				if (index >= 0)
				{
					viewGroup.removeViewAt(index);
				}
			}
		}
	}

	// E X P A N D  /  C O L L A P S E

	/**
	 * Toggle node
	 *
	 * @param node node
	 */
	private void toggleNode(@NonNull final TreeNode node)
	{
		if (isExpanded(node))
		{
			collapseNode(node, false);
		}
		else
		{
			expandNode(node, 0, true, true);
		}
	}

	/**
	 * Expand all
	 */
	public void expandAll()
	{
		expandNode(this.root, -1, false, false);
	}

	/**
	 * Collapse all
	 */
	private void collapseAll()
	{
		for (TreeNode child : this.root.getChildren())
		{
			collapseNode(child, true);
		}
	}

	// P R I M I T I V E S


	private boolean isNodeWithCompositeValueText(final TreeNode node, final String text)
	{
		final Object value = node.getValue();
		return (value instanceof CompositeValue) && text.equals(((CompositeValue) value).text.toString());
	}

	/**
	 * Expand node
	 *
	 * @param node                node
	 * @param levels              expanded subnodes level (-1 == unlimited)
	 * @param fireHotNodes        whether to fire hot nodes
	 * @param overrideBreakExpand whether to override node break expand
	 */
	public void expandNode(@NonNull final TreeNode node, final int levels, final boolean fireHotNodes, final boolean overrideBreakExpand)
	{
		Log.d(TAG, "Expand node " + node);
		//if (isNodeWithCompositeValueText(node, "Agent Agent"))
		//{
		//	Log.d(TAG, "XXX " + " " + node);
		//}

		// children view group
		final Controller<?> controller = node.getController();
		final ViewGroup childrenView = controller.getChildrenView();
		if (childrenView == null)
		{
			// Log.e(TAG, "Node to expand has no view " + node);
			// return;
			throw new RuntimeException("Node to expand has no children view " + node);
		}

		// clear all children views
		childrenView.removeAllViews();

		// break expand
		if (overrideBreakExpand || !controller.isBreakExpand())
		{
			// children

			/* @formatter:off */
			/*
			These raise concurrent access exception:
				Iterator<TreeNode> it = node.getChildrenList().listIterator();
				while (it.hasNext())
			or
				for (final TreeNode child : node.getChildren())
			*/
			/* @formatter:on */

			for (final TreeNode child : node.getChildrenList().toArray(new TreeNode[0]))
			{
				//	TreeNode child = it.next();

				// add child node to children view view
				addSubtreeView(childrenView, child, -1);

				// recurse
				if (child.getController().isBreakExpand())
				{
					continue;
				}
				if (/* TODO isExpanded(child) ||*/ levels < 0 || levels > 0)
				{
					expandNode(child, levels - 1, fireHotNodes, overrideBreakExpand);
				}
			}

			// display
			if (childrenView.getChildCount() != 0)
			{
				if (this.useAnimation)
				{
					animatedContainerExpand(childrenView);
				}
				else
				{
					containerExpand(childrenView);
				}

				// fire expand event
				controller.onExpandEvent();
			}
		}

		// fire
		if (fireHotNodes)
		{
			controller.fire();
		}
	}

	/**
	 * Collapse node
	 *
	 * @param node            node
	 * @param includeSubnodes whether to include subnodes
	 */
	@SuppressWarnings("WeakerAccess")
	public void collapseNode(@NonNull final TreeNode node, final boolean includeSubnodes)
	{
		// collapsibility
		if (!node.isCollapsible())
		{
			return;
		}

		final Controller<?> controller = node.getController();

		// display
		final ViewGroup viewGroup = controller.getChildrenView();
		assert viewGroup != null;
		if (this.useAnimation)
		{
			animatedContainerCollapse(viewGroup);
		}
		else
		{
			containerCollapse(viewGroup);
		}

		// fire collapse event
		controller.onCollapseEvent();

		// subnodes
		if (includeSubnodes)
		{
			for (TreeNode child : node.getChildren())
			{
				collapseNode(child, true);
			}
		}
	}

	// E X P A N D / C O L L A P S E   T R I G G E R

	/**
	 * Expand of view group (child container)
	 *
	 * @param view view
	 */
	static private void containerExpand(@NonNull final ViewGroup view)
	{
		view.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
		view.requestLayout();
		view.setVisibility(View.VISIBLE);
	}

	/**
	 * Collapse of view group (child container)
	 *
	 * @param view view
	 */
	static private void containerCollapse(@NonNull final ViewGroup view)
	{
		view.setVisibility(View.GONE);
	}

	/**
	 * Animated collapse of view group (child container)
	 *
	 * @param view view
	 */
	static private void animatedContainerExpand(@NonNull final ViewGroup view)
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
		animator.addUpdateListener(valueAnimator -> {
			float value = (float) valueAnimator.getAnimatedValue();
			view.getLayoutParams().height = value == 1 ? LayoutParams.WRAP_CONTENT : (int) (targetHeight * value);
			view.requestLayout();
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
	 * Animated collapse of view group (child container)
	 *
	 * @param view view
	 */
	static private void animatedContainerCollapse(@NonNull final ViewGroup view)
	{
		final int initialHeight = view.getMeasuredHeight();
		int duration = (int) (ANIMATION_DP_PER_MS * initialHeight / view.getContext().getResources().getDisplayMetrics().density);

		ValueAnimator.setFrameDelay(250);
		final ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
		animator.setRepeatCount(0);
		animator.setDuration(duration);
		animator.setStartDelay(0);
		animator.setInterpolator(new LinearInterpolator());
		animator.addUpdateListener(valueAnimator -> {
			float value = (float) valueAnimator.getAnimatedValue();
			view.getLayoutParams().height = initialHeight - (int) (initialHeight * value);
			view.requestLayout();
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
	 * @param defaultStyle  default container style
	 * @param appliesToRoot apply for root
	 */
	private void setDefaultContainerStyle(final int defaultStyle, @SuppressWarnings("SameParameterValue") boolean appliesToRoot)
	{
		this.containerStyle = defaultStyle;
		this.containerStyleAppliesToRoot = appliesToRoot;
	}

	// C L I C K

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
		for (TreeNode child : this.root.getChildren())
		{
			setSelectable(child, selectable);
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
		if (isExpanded(node))
		{
			for (TreeNode child : node.getChildren())
			{
				setSelectable(child, selectable);
			}
		}
	}

	// selected nodes

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
	private void deselectAll()
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
			for (TreeNode child : this.root.getChildren())
			{
				selectNode(child, selected, skipCollapsed);
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
		boolean propagate = !skipCollapsed || isExpanded(node);
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
		final Controller<?> controller = node.getController();
		if (controller.isInitialized())
		{
			controller.onSelectedEvent(selected);
		}
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
		final StringBuilder sb = new StringBuilder();
		getSaveState(this.root, sb);
		if (sb.length() > 0)
		{
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * Get save state
	 *
	 * @param node root
	 * @param sb   builder
	 */
	private void getSaveState(@NonNull final TreeNode node, @NonNull final StringBuilder sb)
	{
		for (TreeNode child : node.getChildren())
		{
			if (isExpanded(child))
			{
				sb.append(child.getPath());
				sb.append(NODES_PATH_SEPARATOR);

				// recurse
				getSaveState(child, sb);
			}
		}
	}

	/**
	 * Restore save state
	 *
	 * @param saveState save state
	 */
	@SuppressWarnings("unused")
	public void restoreState(@Nullable final String saveState)
	{
		if (saveState != null && !saveState.isEmpty())
		{
			collapseAll();

			final String[] expandedNodes = saveState.split(NODES_PATH_SEPARATOR);
			final Set<String> expandedNodeSet = new HashSet<>(Arrays.asList(expandedNodes));
			restoreNodeState(this.root, expandedNodeSet);
		}
	}

	/**
	 * Restore node state
	 *
	 * @param node            node
	 * @param expandedNodeSet open nodes
	 */
	private void restoreNodeState(@NonNull final TreeNode node, @NonNull final Set<String> expandedNodeSet)
	{
		for (TreeNode child : node.getChildren())
		{
			if (expandedNodeSet.contains(child.getPath()))
			{
				expandNode(child, 0, false, false);

				// recurse
				restoreNodeState(child, expandedNodeSet);
			}
		}
	}

	public void newNodeView(final TreeNode node, final int levels)
	{
		final TreeNode parent = node.getParent();
		if (!TreeView.isExpanded(parent))
		{
			expandNode(parent, levels, false, false);
		}
		else
		{
			insertNodeView(parent, node);
		}
	}

	private void insertNodeView(final TreeNode parent, final TreeNode node)
	{
		final Controller<?> parentController = parent.getController();
		ViewGroup childrenView = parentController.getChildrenView();
		int index = parent.indexOf(node);
		addSubtreeView(childrenView, node, index);

		// display
		if (childrenView.getChildCount() != 0)
		{
			if (this.useAnimation)
			{
				animatedContainerExpand(childrenView);
			}
			else
			{
				containerExpand(childrenView);
			}

			// fire expand event
			parentController.onExpandEvent();
		}
	}
}

