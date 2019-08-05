/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.config;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;

import org.sqlunet.browser.common.R;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
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
		assert actionBar != null;
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

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
		final TabLayout tabLayout = findViewById(R.id.tablayout);
		tabLayout.setOnTabSelectedListener(this);

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < pagerAdapter.getCount(); i++)
		{
			// Create a tab with text corresponding to the page titleId defined by the adapter.
			// Also specify this Activity object, which implements the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			tabLayout.addTab(tabLayout.newTab() //
					.setTag(pagerAdapter.getItem(i)) //
					.setText(pagerAdapter.getPageTitle(i)));
		}
	}

	// T A B  L I S T E N E R

	@Override
	public void onTabSelected(@NonNull TabLayout.Tab tab)
	{
		int position = tab.getPosition();
		this.viewPager.setCurrentItem(position);

		final Fragment fragment = (Fragment) tab.getTag();
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

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	private class SectionsPagerAdapter extends FragmentPagerAdapter
	{
		private final Fragment[] fragments;

		SectionsPagerAdapter(@NonNull final FragmentManager fragmentManager, @NonNull final Context context)
		{
			super(fragmentManager);
			final List<Fragment> listOfFragments = new ArrayList<>();
			final Resources res = context.getResources();
			String[] fragmentClasses = res.getStringArray(R.array.fragment_class_setup_pages);
			for (String fragmentClass : fragmentClasses)
			{
				listOfFragments.add(makeFragment(fragmentClass));
			}
			this.fragments = listOfFragments.toArray(new Fragment[0]);
		}

		@Nullable
		@SuppressWarnings("TryWithIdenticalCatches")
		private Fragment makeFragment(@Nullable final String fragmentClass)
		{
			if (fragmentClass != null && !fragmentClass.isEmpty())
			{
				try
				{
					final Class<?> cl = Class.forName(fragmentClass);
					final Constructor<?> cons = cl.getConstructor();
					return (Fragment) cons.newInstance();
				}
				catch (ClassNotFoundException e)
				{
					Log.e(TAG, "Page fragment", e);
				}
				catch (NoSuchMethodException e)
				{
					Log.e(TAG, "Page fragment", e);
				}
				catch (java.lang.InstantiationException e)
				{
					Log.e(TAG, "Page fragment", e);
				}
				catch (IllegalAccessException e)
				{
					Log.e(TAG, "Page fragment", e);
				}
				catch (InvocationTargetException e)
				{
					Log.e(TAG, "Page fragment", e);
				}
			}
			return null;
		}

		@SuppressWarnings({"TryWithIdenticalCatches", "WeakerAccess"})
		@NonNull
		@Override
		public Fragment getItem(int position)
		{
			return this.fragments[position];
		}

		@SuppressWarnings("WeakerAccess")
		@Override
		public int getCount()
		{
			return this.fragments.length;
		}

		@SuppressWarnings("WeakerAccess")
		@Override
		public CharSequence getPageTitle(int position)
		{
			switch (position)
			{
				case 0:
					return getString(R.string.page_setup);
				case 1:
					return getString(R.string.page_setup_file);
				case 2:
					return getString(R.string.page_setup_database);
				case 3:
					return getString(R.string.page_setup_sql);
			}
			return null;
		}
	}

	// M E N U

	@SuppressWarnings("SameReturnValue")
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setup, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == R.id.action_settings)
		{
			final Intent intent = new Intent(this, SettingsActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return true;
		}
		else if (id == R.id.action_diagnostics)
		{
			final Intent intent = new Intent(this, DiagnosticsActivity.class);
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
