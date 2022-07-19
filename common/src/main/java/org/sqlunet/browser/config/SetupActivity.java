/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.config;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;

import org.sqlunet.browser.EntryActivity;
import org.sqlunet.browser.MenuHandler;
import org.sqlunet.browser.common.R;
import org.sqlunet.provider.ManagerContract;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.settings.StorageReports;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

@SuppressWarnings("deprecation")
public class SetupActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener
{
	static private final String TAG = "SetupA";

	/**
	 * Pager that will host the section contents.
	 */
	private ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setup);

		// toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// set up the action bar
		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
		{
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
		}

		// create the adapter that will return a fragment for each of the three sections of the activity.
		final SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);

		// set up the pager with the sections adapter.
		this.viewPager = findViewById(R.id.container);
		this.viewPager.setAdapter(pagerAdapter);
		this.viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				//
			}
		});

		// tab layout
		final TabLayout tabLayout = findViewById(R.id.tab_layout);
		tabLayout.addOnTabSelectedListener(this);

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < pagerAdapter.getCount(); i++)
		{
			// Create a tab with text corresponding to the page titleId defined by the adapter.
			// Also specify this Activity object, which implements the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			tabLayout.addTab(tabLayout.newTab() //
					.setTag(pagerAdapter.getFragmentClass(i)) //
					.setContentDescription(pagerAdapter.getPageDescriptionId(i)) //
					.setText(pagerAdapter.getPageTitleId(i)));
		}
	}

	// T A B  L I S T E N E R

	@Override
	public void onTabSelected(@NonNull TabLayout.Tab tab)
	{
		int position = tab.getPosition();
		this.viewPager.setCurrentItem(position);

		final String fragmentClass = (String) tab.getTag();
		final Fragment fragment = makeFragment(fragmentClass);
		if (fragment instanceof Updatable)
		{
			final Updatable updatable = (Updatable) fragment;
			updatable.update();
		}
	}

	@SuppressWarnings("EmptyMethod")
	@Override
	public void onTabUnselected(TabLayout.Tab tab)
	{

	}

	@SuppressWarnings("EmptyMethod")
	@Override
	public void onTabReselected(TabLayout.Tab tab)
	{

	}

	@NonNull
	private Fragment makeFragment(@Nullable final String fragmentClass)
	{
		Log.d(TAG, "Page fragment " + fragmentClass);
		if (fragmentClass != null && !fragmentClass.isEmpty())
		{
			try
			{
				final Class<?> cl = Class.forName(fragmentClass);
				final Constructor<?> cons = cl.getConstructor();
				return (Fragment) cons.newInstance();
			}
			catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e)
			{
				Log.e(TAG, "Page fragment", e);
			}
		}
		throw new IllegalArgumentException(fragmentClass);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	private class SectionsPagerAdapter extends FragmentPagerAdapter
	{
		// @NonNull
		// private final Fragment[] fragments;

		@NonNull
		private final String[] fragmentClasses;

		SectionsPagerAdapter(@NonNull final FragmentManager fragmentManager, @NonNull final Context context)
		{
			super(fragmentManager);

			final Resources res = context.getResources();
			fragmentClasses = res.getStringArray(R.array.fragment_class_setup_pages);

			// final List<Fragment> listOfFragments = new ArrayList<>();
			// for (String fragmentClass : fragmentClasses)
			// {
			// 	listOfFragments.add(makeFragment(fragmentClass));
			// }
			// this.fragments = listOfFragments.toArray(new Fragment[0]);
		}

		@NonNull
		public String getFragmentClass(int i)
		{
			return fragmentClasses[i];
		}

		@SuppressWarnings({"WeakerAccess"})
		@NonNull
		@Override
		public Fragment getItem(int position)
		{
			return makeFragment(fragmentClasses[position]);
		}

		@SuppressWarnings("WeakerAccess")
		@Override
		public int getCount()
		{
			return fragmentClasses.length;
		}

		@SuppressWarnings("WeakerAccess")
		@Override
		public CharSequence getPageTitle(int position)
		{
			int id = getPageTitleId(position);
			if (id == 0)
			{
				return "";
			}
			return getString(id);
		}

		public int getPageTitleId(final int position)
		{
			switch (position)
			{
				case 0:
					return R.string.title_page_setup_status;
				case 1:
					return R.string.title_page_setup_file;
				case 2:
					return R.string.title_page_setup_database;
			}
			return 0;
		}

		public int getPageDescriptionId(final int position)
		{
			switch (position)
			{
				case 0:
					return R.string.description_page_setup_status;
				case 1:
					return R.string.description_page_setup_file;
				case 2:
					return R.string.description_page_setup_database;
			}
			return 0;
		}
	}

	// M E N U

	@SuppressWarnings("SameReturnValue")
	@Override
	public boolean onCreateOptionsMenu(@NonNull Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setup, menu);
		getMenuInflater().inflate(R.menu.status, menu);
		getMenuInflater().inflate(R.menu.setup_file, menu);
		getMenuInflater().inflate(R.menu.setup_database, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		final int itemId = item.getItemId();

		// handle home
		if (itemId == android.R.id.home)
		{
			Log.d(TAG, "onHomePressed");
			EntryActivity.rerun(this);
			return true;
		}
		else if (itemId == R.id.action_settings)
		{
			final Intent intent = new Intent(this, SettingsActivity.class);
			intent.addFlags(BaseSettingsActivity.SETTINGS_FLAGS);
			startActivity(intent);
			return true;
		}
		else if (itemId == R.id.action_diagnostics)
		{
			final Intent intent = new Intent(this, DiagnosticsActivity.class);
			startActivity(intent);
			return true;
		}
		else if (itemId == R.id.action_logs)
		{
			final Intent intent = new Intent(this, LogsActivity.class);
			startActivity(intent);
			return true;
		}
		else if (itemId == R.id.action_tables_and_indices)
		{
			Intent intent = ManagerContract.makeTablesAndIndexesIntent(this);
			intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_dbobject);
			startActivity(intent);
			return true;
		}
		else if (MenuHandler.menuDispatch(this, item))
		{
			return true;
		}
		if (itemId == R.id.action_dirs)
		{
			final CharSequence message = StorageReports.reportStyledDirs(this);
			new AlertDialog.Builder(this) //
					.setTitle(R.string.action_dirs) //
					.setMessage(message) //
					.setNegativeButton(R.string.action_dismiss, (dialog, whichButton) -> { /*canceled*/ }) //
					.show();
		}
		else if (itemId == R.id.action_storage_dirs)
		{
			final Pair<CharSequence[], CharSequence[]> dirs = StorageReports.getStyledStorageDirectoriesNamesValues(this);
			final CharSequence message = StorageReports.namesValuesToReportStyled(dirs);
			new AlertDialog.Builder(this) //
					.setTitle(R.string.action_storage_dirs) //
					.setMessage(message) //
					.setNegativeButton(R.string.action_dismiss, (dialog, whichButton) -> { /*canceled*/ }) //
					.show();
		}
		else if (itemId == R.id.action_cache_dirs)
		{
			final Pair<CharSequence[], CharSequence[]> dirs = StorageReports.getStyledCachesNamesValues(this);
			final CharSequence message = StorageReports.namesValuesToReportStyled(dirs);
			new AlertDialog.Builder(this) //
					.setTitle(R.string.action_cache_dirs) //
					.setMessage(message) //
					.setNegativeButton(R.string.action_dismiss, (dialog, whichButton) -> { /*canceled*/ }) //
					.show();
		}
		else if (itemId == R.id.action_download_dirs)
		{
			final Pair<CharSequence[], CharSequence[]> dirs = StorageReports.getStyledDownloadNamesValues(this);
			final CharSequence message = StorageReports.namesValuesToReportStyled(dirs);
			new AlertDialog.Builder(this) //
					.setTitle(R.string.action_download_dirs) //
					.setMessage(message) //
					.setNegativeButton(R.string.action_dismiss, (dialog, whichButton) -> { /*canceled*/ }) //
					.show();
		}

		return super.onOptionsItemSelected(item);
	}
}
