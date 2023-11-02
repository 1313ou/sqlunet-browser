/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.sqlunet.browser.common.R;
import org.sqlunet.settings.Settings;

import androidx.annotation.ArrayRes;
import androidx.annotation.AttrRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

/**
 * Base search fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class BaseSearchFragment extends Fragment implements SearchListener
{
	static private final String TAG = "BaseSearchF";

	/**
	 * Saved state of spinner
	 */
	static private final String STATE_SPINNER = "selected_mode";

	// Q U E R Y

	@Nullable
	protected String query;

	// C O M P O N E N T S

	/**
	 * Search view -held in search menuitem) that holds query
	 */
	@Nullable
	private SearchView searchView;

	/**
	 * Action bar search mode spinner
	 */
	@Nullable
	private Spinner spinner;

	// R E S O U R C E S

	@LayoutRes
	int layoutId;

	@MenuRes
	int menuId;

	@AttrRes
	int colorAttrId;

	@ArrayRes
	int spinnerLabels;

	@ArrayRes
	int spinnerIcons;

	// C R E A T I O N

	/**
	 * Constructor
	 */
	public BaseSearchFragment()
	{
		Log.d(TAG, "Constructor " + this);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreate() " + this + " from " + savedInstanceState);
		super.onCreate(savedInstanceState);

		final FragmentManager manager = getChildFragmentManager();
		manager.addOnBackStackChangedListener(() -> {

			final Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
			if (manager.getBackStackEntryCount() > 0)
			{
				toolbar.setSubtitle(query);
			}
			else
			{
				toolbar.setSubtitle(R.string.app_subname);
			}
		});
	}

	// V I E W

	@SuppressLint("InflateParams")
	@Nullable
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreateView() " + this + " from " + savedInstanceState);
		return inflater.inflate(this.layoutId, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState)
	{
		Log.d(TAG, "onViewCreated() " + this + " from " + savedInstanceState);
		super.onViewCreated(view, savedInstanceState);

		// menu provider
		final MenuProvider menuProvider = new MenuProvider()
		{
			@Override
			public void onCreateMenu(@NonNull final Menu menu, @NonNull final MenuInflater menuInflater)
			{
				// inflate
				menu.clear();
				menuInflater.inflate(R.menu.main_safedata, menu);
				menuInflater.inflate(menuId, menu);
				// MenuCompat.setGroupDividerEnabled(menu, true);
				Log.d(TAG, "MenuProvider: onCreateMenu() size=" + menu.size());

				// set up search view
				BaseSearchFragment.this.searchView = getSearchView(menu);
				assert BaseSearchFragment.this.searchView != null; // must have
				setupSearchView(BaseSearchFragment.this.searchView, getSearchInfo(requireActivity()));

				// toolbar
				// set spinner, searchview
				final Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
				assert toolbar != null; // must have
				setupToolBar(toolbar);
			}

			@SuppressWarnings("deprecation")
			@Override
			public boolean onMenuItemSelected(@NonNull final MenuItem menuItem)
			{
				boolean handled = onOptionsItemSelected(menuItem);
				if (handled)
				{
					return true;
				}
				return MenuHandler.menuDispatch((AppCompatActivity) requireActivity(), menuItem);
			}
		};

		final MenuHost menuHost = requireActivity();
		menuHost.addMenuProvider(menuProvider, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
	}

	@Override
	public void onPause()
	{
		super.onPause();

		closeKeyboard();
	}

	// T O O L B A R

	/**
	 * Set up toolbar's custom view, its spinner, title, background
	 *
	 * @param toolbar toolbar
	 */
	@SuppressLint("InflateParams")
	public void setupToolBar(@NonNull final Toolbar toolbar)
	{
		Log.d(TAG, "Toolbar: set up " + this);

		final AppCompatActivity activity = (AppCompatActivity) requireActivity();

		// title
		toolbar.setTitle(R.string.title_activity_browse);
		// toolbar.setSubtitle(R.string.app_subname);

		// background
		final int color = ColorUtils.fetchColor(activity, this.colorAttrId);
		toolbar.setBackground(new ColorDrawable(color));

		// nav
		// this breaks behaviour of drawer toggle
		//		toolbar.setNavigationOnClickListener(v -> {
		//
		//			if (!isAdded())
		//			{
		//				return;
		//			}
		//			Log.d(TAG, "BackStack: navigation button clicked");
		//			final FragmentManager manager = getChildFragmentManager();
		//			int count = manager.getBackStackEntryCount();
		//			if (count >= 1)
		//			{
		//				Log.d(TAG, dumpBackStack(manager, "child"));
		//				manager.popBackStack();
		//			}
		//			else
		//			{
		//				FragmentManager manager2 = getParentFragmentManager();
		//				int count2 = manager2.getBackStackEntryCount();
		//				if (count2 >= 1)
		//				{
		//					Log.d(TAG, dumpBackStack(manager2, "parent"));
		//					manager2.popBackStack();
		//				}
		//				else
		//				{
		//					Log.d(TAG, "BackStack: activity onBackPressed() - none");
		//					requireActivity().getOnBackPressedDispatcher().onBackPressed();
		//				}
		//			}
		//		});

		// spinner
		this.spinner = toolbar.findViewById(R.id.spinner);
		if (this.spinner == null)
		{
			// toolbar customized view if toolbar does not contain spinner
			final View customView = getLayoutInflater().inflate(R.layout.actionbar_custom, null);
			toolbar.addView(customView);
			this.spinner = toolbar.findViewById(R.id.spinner);
		}
		if (this.spinner != null)
		{
			setupSpinner(this.spinner);
		}
	}

	// S E A R C H V I E W

	@Nullable
	private static SearchView getSearchView(@NonNull final Menu menu)
	{
		// menu item
		final MenuItem searchMenuItem = menu.findItem(R.id.search);
		if (searchMenuItem == null)
		{
			return null;
		}
		// search view
		return (SearchView) searchMenuItem.getActionView();
	}

	/**
	 * Set up search view
	 *
	 * @param searchView     search view
	 * @param searchableInfo searchable info
	 */
	private void setupSearchView(@NonNull final SearchView searchView, @Nullable final SearchableInfo searchableInfo)
	{
		// search view
		searchView.setSearchableInfo(searchableInfo);
		searchView.setIconifiedByDefault(true);
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
		{
			@Override
			public boolean onQueryTextSubmit(final String query)
			{
				clearSearchView(searchView);
				closeKeyboard();
				return false;
			}

			@Override
			public boolean onQueryTextChange(final String newText)
			{
				return false;
			}
		});

		// trigger focus
		if (triggerFocusSearch())
		{
			new Handler(Looper.getMainLooper()).postDelayed(() -> searchView.setIconified(false), 1500);
		}
	}

	@Nullable
	private static SearchableInfo getSearchInfo(@NonNull final Activity activity)
	{
		final ComponentName componentName = activity.getComponentName();
		final SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
		assert searchManager != null;
		return searchManager.getSearchableInfo(componentName);
	}

	protected boolean triggerFocusSearch()
	{
		return true;
	}

	public void clearQuery()
	{
		if (this.searchView != null)
		{
			clearSearchView(this.searchView);
		}
		closeKeyboard();
	}

	private static void clearSearchView(@NonNull final SearchView searchView)
	{
		searchView.clearFocus();
		searchView.setFocusable(false);
		searchView.setQuery("", false);
		searchView.setIconified(true);
	}

	private void closeKeyboard()
	{
		// activity
		final Activity activity = requireActivity();

		// view
		final View view = activity.getCurrentFocus();
		if (view != null)
		{
			final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			assert imm != null;
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	// S P I N N E R

	/**
	 * Set up spinner
	 *
	 * @param spinner spinner
	 */
	@SuppressWarnings("WeakerAccess")
	protected void setupSpinner(@NonNull final Spinner spinner)
	{
		spinner.setVisibility(View.GONE);
	}

	/**
	 * Set up spinner
	 */
	@NonNull
	@SuppressWarnings("WeakerAccess")
	protected BaseAdapter getSpinnerAdapter()
	{
		final Context context = requireContext();

		// resources
		final Resources resources = context.getResources();

		// adapter values and icons
		final CharSequence[] modeLabels = resources.getTextArray(this.spinnerLabels);
		final TypedArray array = resources.obtainTypedArray(this.spinnerIcons);
		int n = array.length();
		final int[] modeIcons = new int[n];
		for (int i = 0; i < n; i++)
		{
			modeIcons[i] = array.getResourceId(i, -1);
		}
		array.recycle();

		// adapter
		final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(context, R.layout.spinner_item_actionbar, android.R.id.text1, modeLabels)
		{
			@NonNull
			@Override
			public View getView(final int position, final View convertView, @NonNull final ViewGroup parent)
			{
				final CharSequence rowItem = getItem(position);
				assert rowItem != null;

				final View view = super.getView(position, convertView, parent);
				final TextView textView = view.findViewById(android.R.id.text1);
				textView.setText("");
				int resId = modeIcons[position];

				final int color = ColorUtils.fetchColor(context, R.attr.colorOnPrimary);
				final Drawable drawable = ColorUtils.getDrawable(context, resId);
				ColorUtils.tint(color, drawable);

				textView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);

				return view;
			}

			@NonNull
			@Override
			public View getDropDownView(final int position, final View convertView, @NonNull final ViewGroup parent)
			{
				final CharSequence rowItem = getItem(position);
				assert rowItem != null;

				final View view = super.getDropDownView(position, convertView, parent);
				final TextView textView = view.findViewById(android.R.id.text1);
				textView.setText(rowItem);
				int resId = modeIcons[position];

				final int color = ColorUtils.fetchColor(context, R.attr.colorOnPrimary);
				final Drawable drawable = ColorUtils.getDrawable(context, resId);
				ColorUtils.tint(color, drawable);

				textView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);

				return view;
			}
		};
		adapter.setDropDownViewResource(R.layout.spinner_item_actionbar_dropdown);

		return adapter;
	}

	/**
	 * Get search type position by peeking at spinner state or registry if spinner is still null
	 *
	 * @return search type position
	 */
	protected int getSearchModePosition()
	{
		if (this.spinner != null)
		{
			return this.spinner.getSelectedItemPosition();
		}

		final Settings.Selector selectorMode = Settings.Selector.getPref(requireContext());
		return selectorMode.ordinal();
	}

	// S E A R C H   L I S T E N E R
	public void search(final String query)
	{
		this.query = query;

		// subtitle
		// final Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
		// assert toolbar != null;
		// toolbar.setSubtitle(query);
	}

	// S A V E / R E S T O R E

	@SuppressWarnings("WeakerAccess")
	@Override
	public void onSaveInstanceState(@NonNull final Bundle outState)
	{
		Log.d(TAG, "Save instance state");

		// always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(outState);

		// spinner
		if (this.spinner != null)
		{
			// serialize the current dropdown position
			final int position = this.spinner.getSelectedItemPosition();
			outState.putInt(BaseSearchFragment.STATE_SPINNER, position);
		}
	}

	@Override
	public void onViewStateRestored(@Nullable final Bundle savedInstanceState)
	{
		super.onViewStateRestored(savedInstanceState);

		// restore from saved instance
		if (savedInstanceState != null && this.spinner != null)
		{
			final int selected = savedInstanceState.getInt(STATE_SPINNER);
			this.spinner.setSelection(selected);
		}
	}

	// B A C K S T A C K   H E L P E R

	private String dumpBackStack(@NonNull final FragmentManager manager, @NonNull final String type)
	{
		StringBuilder sb = new StringBuilder();
		int count = manager.getBackStackEntryCount();
		for (int i = 0; i < count; i++)
		{
			FragmentManager.BackStackEntry entry = manager.getBackStackEntryAt(i);
			sb.append("BackStack: ") //
					.append(type) //
					.append('\n') //
					.append("[") //
					.append(i) //
					.append("]: ") //
					.append(entry.getName()) //
					.append(' ') //
					.append(entry.getId()) //
					.append(' ') //
					.append(entry.getClass().getName());
		}
		// sb.append('\n');
		// sb.append("BackStack: ") //
		// 		.append(type) //
		//		.append('\n') //
		// 		.append(" popBackStack() ") //
		// 		.append(manager.getBackStackEntryAt(count - 1));
		return sb.toString();
	}
}
