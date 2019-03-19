/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package org.sqlunet.preference;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.XmlRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle.State;
import androidx.preference.Preference;

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
@SuppressWarnings("WeakerAccess")
public class PreferenceActivityCompatDelegate
{
	public interface Connector
	{
		void onBuildHeaders(@NonNull List<Header> target);

		boolean onIsMultiPane();

		boolean isValidFragment(@Nullable String fragmentName);
	}

	public static final long HEADER_ID_UNDEFINED = -1;

	private static final String HEADERS_TAG = ":android:headers";
	private static final String CUR_HEADER_TAG = ":android:cur_header";
	private static final String BACK_STACK_PREFS = ":android:prefs";

	// A C T I V I T Y   A N D   F R A G M E N T

	@NonNull
	private final FragmentActivity activity;

	@Nullable
	private Fragment fragment;

	// V I E W   E L E M E N T S

	// this element is compulsory in layout
	@SuppressWarnings("NullableProblems")
	@NonNull
	private ViewGroup headersContainer;

	// this element is compulsory in layout
	@SuppressWarnings("NullableProblems")
	@NonNull
	private ViewGroup prefsContainer;

	// this element is compulsory in layout
	@SuppressWarnings("NullableProblems")
	@NonNull
	private ListView listView;

	// this element is compulsory in layout
	@SuppressWarnings("NullableProblems")
	@NonNull
	private FrameLayout listFooter;

	// this element is optional in layout
	@Nullable
	private TextView breadCrumbTitle;

	// H E A D E R S

	@NonNull
	private final ArrayList<Header> headers = new ArrayList<>();

	@Nullable
	private Header curHeader;

	// A D A P T E R S

	@NonNull
	private final Connector connector;

	private ListAdapter adapter;

	// H A N D L E R   A N D   R U N N A B L E S

	private final Handler handler = new Handler();

	private final Runnable requestFocus = () -> listView.focusableViewAvailable(listView);

	@Nullable
	private final Runnable buildHeaders;

	// L I S T E N E R

	@NonNull
	private final OnItemClickListener onClickListener = (parent, view, position, id) -> onListItemClick(position);

	// S T A T E

	private boolean finishedStart = false;

	private boolean singlePane;

	// C O N S T R U C T O R

	public PreferenceActivityCompatDelegate(@NonNull final FragmentActivity activity, @NonNull final Connector connector)
	{
		this.activity = activity;
		this.connector = connector;
		this.buildHeaders = () -> {
			this.headers.clear();
			this.connector.onBuildHeaders(this.headers);
			assert this.adapter != null : "Null adapter";
			if (this.adapter instanceof BaseAdapter)
			{
				((BaseAdapter) this.adapter).notifyDataSetChanged();
			}
			if (this.curHeader != null)
			{
				final Header mappedHeader = findBestMatchingHeader(this.curHeader, this.headers);
				if (mappedHeader != null)
				{
					setSelectedHeader(mappedHeader);
				}
			}
		};
	}

	// L I F E C Y C L E

	public void onCreate(@Nullable final Bundle savedInstanceState)
	{
		setContentView(R.layout.content);

		// find layout elements (non null)
		this.prefsContainer = findViewById(R.id.prefs_frame);
		this.headersContainer = findViewById(R.id.headers);
		this.listView = findViewById(R.id.list);
		this.listFooter = findViewById(R.id.list_footer);

		// click listener
		this.listView.setOnItemClickListener(onClickListener);

		// adapter
		if (this.finishedStart)
		{
			setListAdapter(this.adapter);
		}
		// post request focus
		this.handler.post(requestFocus);

		// state
		this.finishedStart = true;
		this.singlePane = !this.connector.onIsMultiPane();

		// breadcrumb
		final View breadCrumbSection = findNullableViewById(R.id.breadcrumb_section);
		this.breadCrumbTitle = findNullableViewById(R.id.bread_crumb_title);
		if (this.singlePane && breadCrumbSection != null && this.breadCrumbTitle != null)
		{
			this.breadCrumbTitle.setVisibility(View.GONE);
			breadCrumbSection.setVisibility(View.GONE);
		}

		// restore
		if (savedInstanceState != null)
		{
			final ArrayList<Header> headers = savedInstanceState.getParcelableArrayList(HEADERS_TAG);
			if (headers != null)
			{
				this.headers.addAll(headers);
				final int curHeader = savedInstanceState.getInt(CUR_HEADER_TAG, (int) HEADER_ID_UNDEFINED);
				if (curHeader >= 0 && curHeader < this.headers.size())
				{
					setSelectedHeader(this.headers.get(curHeader));
				}
				else if (!this.singlePane)
				{
					switchToHeader(onGetInitialHeader());
				}
			}
			else
			{
				showBreadCrumbs(getTitle());
			}
		}
		else
		{
			this.connector.onBuildHeaders(this.headers);
			if (!this.singlePane && this.headers.size() > 0)
			{
				switchToHeader(onGetInitialHeader());
			}
		}

		// geometry
		if (this.headers.size() > 0)
		{
			setListAdapter(new HeaderAdapter(getContext(), this.headers));
			if (!this.singlePane)
			{
				this.listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
			}
		}
		if (this.singlePane)
		{
			if (this.curHeader != null)
			{
				this.headersContainer.setVisibility(View.GONE);
			}
			else
			{
				this.prefsContainer.setVisibility(View.GONE);
			}
		}
		else if (this.headers.size() > 0 && this.curHeader != null)
		{
			setSelectedHeader(this.curHeader);
		}
	}

	public void onDestroy()
	{
		this.handler.removeCallbacks(this.buildHeaders);
		this.handler.removeCallbacks(this.requestFocus);
	}

	public void onSaveInstanceState(@NonNull final Bundle outState)
	{
		if (this.headers.size() > 0)
		{
			outState.putParcelableArrayList(HEADERS_TAG, this.headers);
			if (this.curHeader != null)
			{
				final int index = this.headers.indexOf(this.curHeader);
				if (index >= 0)
				{
					outState.putInt(CUR_HEADER_TAG, index);
				}
			}
		}
	}

	public void onRestoreInstanceState(@NonNull final Bundle state)
	{
		if (!this.singlePane)
		{
			if (this.curHeader != null)
			{
				setSelectedHeader(this.curHeader);
			}
		}
	}

	private boolean isResumed()
	{
		return this.activity.getLifecycle().getCurrentState() == State.RESUMED;
	}

	public void startPreferenceFragment(@NonNull final Preference pref)
	{
		// final Fragment fragment = Fragment.instantiate(getContext(), pref.getFragment(), pref.getExtras());
		final FragmentManager fragmentManager = getFragmentManager();
		FragmentFactory factory = fragmentManager.getFragmentFactory();
		final Fragment fragment = factory.instantiate(getContext().getClassLoader(), pref.getFragment(), pref.getExtras());

		getFragmentManager().beginTransaction().replace(R.id.prefs, fragment).setBreadCrumbTitle(pref.getTitle()).setTransition(FragmentTransaction.TRANSIT_NONE).addToBackStack(BACK_STACK_PREFS).commitAllowingStateLoss();
	}

	// A C C E S S

	@NonNull
	private FragmentManager getFragmentManager()
	{
		return this.activity.getSupportFragmentManager();
	}

	@NonNull
	private Context getContext()
	{
		return this.activity;
	}

	@NonNull
	private Resources getResources()
	{
		return this.activity.getResources();
	}

	@Nullable
	private CharSequence getTitle()
	{
		return this.activity.getTitle();
	}

	private void setTitle(@Nullable final CharSequence title)
	{
		this.activity.setTitle(title);
	}

	private void setContentView(@LayoutRes final int layoutResID)
	{
		this.activity.setContentView(layoutResID);
	}

	public boolean isMultiPane()
	{
		return !this.singlePane;
	}

	@NonNull
	private <T extends View> T findViewById(@IdRes final int id)
	{
		//noinspection ConstantConditions
		return this.activity.findViewById(id);
	}

	@Nullable
	private <T extends View> T findNullableViewById(@IdRes final int id)
	{
		return this.activity.findViewById(id);
	}


	private void setListAdapter(final ListAdapter adapter)
	{
		this.adapter = adapter;
		this.listView.setAdapter(adapter);
	}

	public int getSelectedItemPosition()
	{
		return listView.getSelectedItemPosition();
	}

	public void setListFooter(@NonNull final View view)
	{
		this.listFooter.removeAllViews();
		this.listFooter.addView(view, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
	}

	// H E A D E R S

	public boolean hasHeaders()
	{
		return this.headersContainer.getVisibility() == View.VISIBLE;
	}

	@NonNull
	public List<Header> getHeaders()
	{
		return this.headers;
	}

	@Nullable
	private Header findBestMatchingHeader(@NonNull final Header current, @NonNull final ArrayList<Header> from)
	{
		final ArrayList<Header> matches = new ArrayList<>();
		for (final Header oh : from)
		{
			if (current == oh || (current.id != HEADER_ID_UNDEFINED && current.id == oh.id))
			{
				matches.clear();
				matches.add(oh);
				break;
			}
			if (current.fragment != null)
			{
				if (current.fragment.equals(oh.fragment))
				{
					matches.add(oh);
				}
			}
			else if (current.intent != null)
			{
				if (current.intent.equals(oh.intent))
				{
					matches.add(oh);
				}
			}
			else if (current.title != null)
			{
				if (current.title.equals(oh.title))
				{
					matches.add(oh);
				}
			}
		}
		if (matches.size() == 1)
		{
			return matches.get(0);
		}
		for (final Header oh : matches)
		{
			if (current.fragmentArguments != null && current.fragmentArguments.equals(oh.fragmentArguments))
			{
				return oh;
			}
			if (current.extras != null && current.extras.equals(oh.extras))
			{
				return oh;
			}
			if (current.title != null && current.title.equals(oh.title))
			{
				return oh;
			}
		}
		return null;
	}

	@NonNull
	private Header onGetInitialHeader()
	{
		for (int i = 0; i < this.headers.size(); i++)
		{
			final Header h = this.headers.get(i);
			if (h.fragment != null)
			{
				return h;
			}
		}
		throw new IllegalStateException("Must have at least one header with a fragment");
	}

	public void invalidateHeaders()
	{
		this.handler.removeCallbacks(this.buildHeaders);
		this.handler.post(this.buildHeaders);
	}

	public void loadHeadersFromResource(@XmlRes final int resId, @NonNull final List<Header> target)
	{
		HeaderLoader.loadFromResource(getContext(), resId, target);
	}

	public void switchToHeader(@NonNull final Header header)
	{
		if (this.curHeader == header)
		{
			getFragmentManager().popBackStack(BACK_STACK_PREFS, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
		else
		{
			if (header.fragment == null)
			{
				throw new IllegalStateException("can't switch to header that has no fragment");
			}
			this.handler.post(() -> {
				switchToHeaderInner(header.fragment, header.fragmentArguments);
				setSelectedHeader(header);
			});
		}
	}

	private void switchToHeaderInner(@NonNull final String fragmentName, @Nullable final Bundle args)
	{
		final FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.popBackStack(BACK_STACK_PREFS, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		if (!this.connector.isValidFragment(fragmentName))
		{
			throw new IllegalArgumentException("Invalid fragment for this activity: " + fragmentName);
		}
		// fragment = Fragment.instantiate(getContext(), fragmentName, args);
		FragmentFactory factory = fragmentManager.getFragmentFactory();
		this.fragment = factory.instantiate(getContext().getClassLoader(), fragmentName, null);
		this.fragment.setArguments(args);


		fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_NONE).replace(R.id.prefs, fragment).commitAllowingStateLoss();

		if (this.singlePane && this.prefsContainer.getVisibility() == View.GONE)
		{
			this.prefsContainer.setVisibility(View.VISIBLE);
			this.headersContainer.setVisibility(View.GONE);
		}
	}

	private void setSelectedHeader(@NonNull final Header header)
	{
		this.curHeader = header;
		final int index = this.headers.indexOf(header);
		if (index >= 0)
		{
			this.listView.setItemChecked(index, true);
		}
		else
		{
			this.listView.clearChoices();
		}
		showBreadCrumbs(header);
	}

	private void onHeaderClick(@NonNull final Header header)
	{
		if (header.fragment != null)
		{
			switchToHeader(header);
		}
		else if (header.intent != null)
		{
			getContext().startActivity(header.intent);
		}
	}

	private void onListItemClick(final int position)
	{
		if (!isResumed())
		{
			return;
		}
		if (this.adapter != null)
		{
			final Object item = this.adapter.getItem(position);
			if (item instanceof Header)
			{
				onHeaderClick((Header) item);
			}
		}
	}

	// B R E A D C R U M B S

	private void showBreadCrumbs(@NonNull final Header header)
	{
		final Resources resources = getResources();
		CharSequence title = header.getBreadCrumbTitle(resources);
		if (title == null)
		{
			title = header.getTitle(resources);
		}
		if (title == null)
		{
			title = getTitle();
		}
		showBreadCrumbs(title);
	}

	private void showBreadCrumbs(@Nullable final CharSequence title)
	{
		if (this.breadCrumbTitle == null)
		{
			setTitle(title);
			return;
		}
		if (this.breadCrumbTitle.getVisibility() != View.VISIBLE)
		{
			setTitle(title);
		}
		else
		{
			this.breadCrumbTitle.setText(title);
		}
	}

	// M I S C

	public boolean onBackPressed()
	{
		final FragmentManager manager = getFragmentManager();
		if (this.curHeader == null || !this.singlePane || manager.getBackStackEntryCount() != 0)
		{
			return false;
		}
		if (this.fragment != null)
		{
			manager.beginTransaction().remove(this.fragment).commitAllowingStateLoss();
			this.fragment = null;
		}
		this.curHeader = null;
		this.prefsContainer.setVisibility(View.GONE);
		this.headersContainer.setVisibility(View.VISIBLE);
		showBreadCrumbs(getTitle());
		this.listView.clearChoices();
		return true;
	}
}
