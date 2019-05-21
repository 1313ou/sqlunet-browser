/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package android.support.local.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

@SuppressWarnings("unused")
public class ExpandableListFragment extends Fragment implements ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupCollapseListener, ExpandableListView.OnGroupExpandListener
{
	@Nullable
	private ExpandableListAdapter mAdapter;

	@Nullable
	private ExpandableListView mExpandableList;

	final private Handler mHandler = new Handler();

	final private AdapterView.OnItemClickListener mOnClickListener = (parent, v, position, id) -> onListItemClick((ExpandableListView) parent, v, position, id);

	final private Runnable mRequestFocus = () -> {
		assert ExpandableListFragment.this.mExpandableList != null;
		ExpandableListFragment.this.mExpandableList.focusableViewAvailable(ExpandableListFragment.this.mExpandableList);
	};

	/**
	 * Custom empty view
	 */
	@Nullable
	private View mEmptyView;

	/**
	 * Standard empty view
	 */
	@Nullable
	private TextView mStandardEmptyView;

	/**
	 * Text for standard empty view
	 */
	private CharSequence mEmptyText;

	@Nullable
	private View mProgressContainer;

	@Nullable
	private View mExpandableListContainer;

	private boolean mExpandableListShown;

	// C O N S T R U C T O R

	public ExpandableListFragment()
	{
	}

	@SuppressLint("CutPasteId")
	private void ensureList()
	{
		if (this.mExpandableList != null)
		{
			return;
		}
		final View view = getView();
		if (view == null)
		{
			throw new IllegalStateException("Content view not yet created");
		}
		if (view instanceof ExpandableListView)
		{
			this.mExpandableList = (ExpandableListView) view;
		}
		else
		{
			// empty view
			this.mStandardEmptyView = view.findViewById(R.id.empty);
			if (this.mStandardEmptyView == null)
			{
				// there is no standard empty view so there must be a custom one
				this.mEmptyView = view.findViewById(android.R.id.empty);
			}
			else
			{
				this.mStandardEmptyView.setVisibility(View.GONE);
			}

			// containers
			this.mProgressContainer = view.findViewById(R.id.container_progress);
			this.mExpandableListContainer = view.findViewById(R.id.container_list);

			// expandable list
			View rawExpandableListView = view.findViewById(android.R.id.list);
			if (!(rawExpandableListView instanceof ExpandableListView))
			{
				if (rawExpandableListView == null)
				{
					throw new RuntimeException("Your content must have a ListView whose id attribute is 'android.R.id.list'");
				}
				throw new RuntimeException("Content has view with id attribute 'android.R.id.list' that is not a ListView class");
			}
			this.mExpandableList = (ExpandableListView) rawExpandableListView;

			// empty view
			if (this.mEmptyView != null)
			{
				// custom empty view
				this.mExpandableList.setEmptyView(this.mEmptyView);
			}
			else if (this.mEmptyText != null)
			{
				// standard empty text view
				this.mStandardEmptyView.setText(this.mEmptyText);
				this.mExpandableList.setEmptyView(this.mStandardEmptyView);
			}
		}
		this.mExpandableListShown = true;

		// listeners
		this.mExpandableList.setOnItemClickListener(this.mOnClickListener);
		this.mExpandableList.setOnChildClickListener(this);
		this.mExpandableList.setOnGroupExpandListener(this);
		this.mExpandableList.setOnGroupCollapseListener(this);

		// adapter
		if (this.mAdapter != null)
		{
			ExpandableListAdapter adapter = this.mAdapter;
			this.mAdapter = null;
			setListAdapter(adapter);
		}
		else
		{
			// starting without an adapter, so assume we won't have our data right away and start with the progress indicator.
			if (this.mProgressContainer != null)
			{
				setListShown(false, false);
			}
		}

		// request focus
		this.mHandler.post(this.mRequestFocus);
	}

	// L I F E C Y C L E   E V E N T S

	/**
	 * Provide default implementation to return a simple list view. Subclasses can override to replace with their own layout. If doing so, the returned view
	 * hierarchy <em>must</em> have a ListView whose id is {@link android.R.id#list android.R.id.list} and can optionally have a sibling view id
	 * {@link android.R.id#empty android.R.id.empty} that is to be shown when the list is empty.
	 * <p>
	 * If you are overriding this method with your own custom content, consider including the standard layout {@link android.R.layout#list_content} in your
	 * layout file, so that you continue to retain all of the standard behavior of ListFragment. In particular, this is currently the only way to have the
	 * built-in indeterminant progress state be shown.
	 */
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.expandable_list_fragment, container, false);
	}

	/**
	 * Attach to list view once the view hierarchy has been created.
	 */
	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		ensureList();
	}

	/**
	 * Detach from list view.
	 */
	@Override
	public void onDestroyView()
	{
		this.mHandler.removeCallbacks(this.mRequestFocus);
		this.mExpandableList = null;
		this.mExpandableListShown = false;
		this.mEmptyView = null;
		this.mStandardEmptyView = null;
		this.mProgressContainer = null;
		this.mExpandableListContainer = null;
		super.onDestroyView();
	}

	// E X P A N D A B L E   L I S T   V I E W

	/**
	 * Get the activity's list view widget.
	 */
	@Nullable
	protected ExpandableListView getListView()
	{
		ensureList();
		return this.mExpandableList;
	}

	/**
	 * Get the activity's expandable list view widget. This can be used to get the selection, set the selection, and many other useful functions.
	 *
	 * @see ExpandableListView
	 */
	@Nullable
	protected ExpandableListView getExpandableListView()
	{
		ensureList();
		return this.mExpandableList;
	}

	/**
	 * Set the currently selected list item to the specified position with the adapter's data
	 *
	 * @param position position
	 */
	public void setSelection(int position)
	{
		ensureList();
		assert this.mExpandableList != null;
		this.mExpandableList.setSelection(position);
	}

	/**
	 * Get the position of the currently selected list item.
	 */
	public int getSelectedItemPosition()
	{
		ensureList();
		assert this.mExpandableList != null;
		return this.mExpandableList.getSelectedItemPosition();
	}

	/**
	 * Gets the position (in packed position representation) of the currently selected group or child. Use {@link ExpandableListView#getPackedPositionType},
	 * {@link ExpandableListView#getPackedPositionGroup}, and {@link ExpandableListView#getPackedPositionChild} to unpack the returned packed position.
	 *
	 * @return A packed position representation containing the currently selected group or child's position and type.
	 */
	public long getSelectedPosition()
	{
		assert this.mExpandableList != null;
		return this.mExpandableList.getSelectedPosition();
	}

	/**
	 * Get the cursor row ID of the currently selected list item.
	 */
	public long getSelectedItemId()
	{
		ensureList();
		assert this.mExpandableList != null;
		return this.mExpandableList.getSelectedItemId();
	}

	/**
	 * Gets the ID of the currently selected group or child.
	 *
	 * @return The ID of the currently selected group or child.
	 */
	public long getSelectedId()
	{
		assert this.mExpandableList != null;
		return this.mExpandableList.getSelectedId();
	}

	/**
	 * Sets the selection to the specified child. If the child is in a collapsed group, the group will only be expanded and child subsequently selected if
	 * shouldExpandGroup is set to true, otherwise the method will return false.
	 *
	 * @param groupPosition     The position of the group that contains the child.
	 * @param childPosition     The position of the child within the group.
	 * @param shouldExpandGroup Whether the child's group should be expanded if it is collapsed.
	 * @return Whether the selection was successfully set on the child.
	 */
	public boolean setSelectedChild(int groupPosition, int childPosition, boolean shouldExpandGroup)
	{
		assert this.mExpandableList != null;
		return this.mExpandableList.setSelectedChild(groupPosition, childPosition, shouldExpandGroup);
	}

	/**
	 * Sets the selection to the specified group.
	 *
	 * @param groupPosition The position of the group that should be selected.
	 */
	public void setSelectedGroup(int groupPosition)
	{
		assert this.mExpandableList != null;
		this.mExpandableList.setSelectedGroup(groupPosition);
	}

	// A D A P T E R

	/**
	 * Get the ListAdapter associated with this activity's ListView.
	 */
	@Nullable
	protected ExpandableListAdapter getListAdapter()
	{
		return this.mAdapter;
	}

	/**
	 * Get the ExpandableListAdapter associated with this activity's ExpandableListView.
	 */
	@Nullable
	public ExpandableListAdapter getExpandableListAdapter()
	{
		return this.mAdapter;
	}

	/**
	 * Provide the cursor for the list view.
	 */
	protected void setListAdapter(ExpandableListAdapter adapter)
	{
		synchronized (this)
		{
			boolean hadAdapter = this.mAdapter != null;
			this.mAdapter = adapter;
			if (this.mExpandableList != null)
			{
				this.mExpandableList.setAdapter(adapter);
				if (!this.mExpandableListShown && !hadAdapter)
				{
					// The list was hidden, and previously didn't have an
					// adapter. It is now time to show it.
					final View view = getView();
					assert view != null;
					setListShown(true, view.getWindowToken() != null);
				}
			}
		}
	}

	// D I S P L A Y

	/**
	 * The default content for a ListFragment has a TextView that can be shown when the list is empty. If you would like to have it shown, call this method to
	 * supply the text it should use.
	 */
	public void setEmptyText(CharSequence text)
	{
		ensureList();
		if (this.mStandardEmptyView == null)
		{
			throw new IllegalStateException("Can't be used with a custom content view");
		}
		this.mStandardEmptyView.setText(text);
		if (this.mEmptyText == null)
		{
			assert this.mExpandableList != null;
			this.mExpandableList.setEmptyView(this.mStandardEmptyView);
		}
		this.mEmptyText = text;
	}

	/**
	 * Control whether the list is being displayed. You can make it not displayed if you are waiting for the initial data to show in it. During this time an
	 * indeterminant progress indicator will be shown instead.
	 * <p>
	 * Applications do not normally need to use this themselves. The default behavior of ListFragment is to start with the list not being shown, only showing it
	 * once an adapter is given with setListAdapter(ListAdapter). If the list at that point had not been shown, when it does get shown it will be do
	 * without the user ever seeing the hidden state.
	 *
	 * @param shown If true, the list view is shown; if false, the progress indicator. The initial value is true.
	 */
	public void setListShown(boolean shown)
	{
		setListShown(shown, true);
	}

	/**
	 * Like {@link #setListShown(boolean)}, but no animation is used when transitioning from the previous state.
	 */
	public void setListShownNoAnimation(boolean shown)
	{
		setListShown(shown, false);
	}

	/**
	 * Control whether the list is being displayed. You can make it not displayed if you are waiting for the initial data to show in it. During this time an
	 * indeterminant progress indicator will be shown instead.
	 *
	 * @param shown   If true, the list view is shown; if false, the progress indicator. The initial value is true.
	 * @param animate If true, an animation will be used to transition to the new state.
	 */
	private void setListShown(boolean shown, boolean animate)
	{
		ensureList();
		if (this.mProgressContainer == null)
		{
			throw new IllegalStateException("Can't be used with a custom content view");
		}
		if (this.mExpandableListShown == shown)
		{
			return;
		}
		this.mExpandableListShown = shown;
		assert this.mExpandableListContainer != null;
		if (shown)
		{
			if (animate)
			{
				this.mProgressContainer.startAnimation(AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_out));
				this.mExpandableListContainer.startAnimation(AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in));
			}
			else
			{
				this.mProgressContainer.clearAnimation();
				this.mExpandableListContainer.clearAnimation();
			}
			this.mProgressContainer.setVisibility(View.GONE);
			this.mExpandableListContainer.setVisibility(View.VISIBLE);
		}
		else
		{
			if (animate)
			{
				this.mProgressContainer.startAnimation(AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in));
				this.mExpandableListContainer.startAnimation(AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_out));
			}
			else
			{
				this.mProgressContainer.clearAnimation();
				this.mExpandableListContainer.clearAnimation();
			}
			this.mProgressContainer.setVisibility(View.VISIBLE);
			this.mExpandableListContainer.setVisibility(View.GONE);
		}
	}

	// O V E R R I D A B L E S

	/**
	 * This method will be called when an item in the list is selected. Subclasses should override. Subclasses can call
	 * getListView().getItemAtPosition(position) if they need to access the data associated with the selected item.
	 *
	 * @param l        The ListView where the click happened
	 * @param v        The view that was clicked within the ListView
	 * @param position The position of the view in the list
	 * @param id       The row id of the item that was clicked
	 */
	@SuppressWarnings("EmptyMethod")
	protected void onListItemClick(ExpandableListView l, View v, int position, long id)
	{
		//
	}

	/**
	 * Override this for receiving callbacks when a child has been clicked.
	 * <p>
	 * {@inheritDoc}
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
	{
		return false;
	}

	/**
	 * Override this for receiving callbacks when a group has been collapsed.
	 */
	@Override
	public void onGroupCollapse(int groupPosition)
	{
		//
	}

	/**
	 * Override this for receiving callbacks when a group has been expanded.
	 */
	@Override
	public void onGroupExpand(int groupPosition)
	{
		//
	}

	/**
	 * Override this to populate the context menu when an item is long pressed. menuInfo will contain an
	 * {@link android.widget.ExpandableListView.ExpandableListContextMenuInfo} whose packedPosition is a packed position that should be used with
	 * {@link ExpandableListView#getPackedPositionType(long)} and the other similar methods.
	 * <p>
	 * {@inheritDoc}
	 */
	@SuppressWarnings("EmptyMethod")
	@Override
	public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo)
	{
		//
	}
}
