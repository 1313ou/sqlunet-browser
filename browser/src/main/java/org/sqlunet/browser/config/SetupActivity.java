package org.sqlunet.browser.config;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.sqlunet.browser.R;

public class SetupActivity extends AppCompatActivity implements ActionBar.TabListener
{
	/**
	 * Pager that will host the section contents.
	 */
	private ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setup);

		// create the adapter that will return a fragment for each of the three sections of the activity.
		final SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// set up the pager with the sections adapter.
		this.viewPager = (ViewPager) findViewById(R.id.container);
		this.viewPager.setAdapter(pagerAdapter);

		// set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
		actionBar.setTitle(R.string.title_setup_section);

		// When swiping between different sections, select the corresponding tab.
		// We can also use ActionBar.Tab#select() to do this if we have a reference to the Tab.
		this.viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < pagerAdapter.getCount(); i++)
		{
			// Create a tab with text corresponding to the page titleId defined by the adapter.
			// Also specify this Activity object, which implements the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab() //
					.setText(pagerAdapter.getPageTitle(i)) //
					.setTabListener(this));
		}
	}

	// T A B  L I S T E N E R

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
		// when the given tab is selected, switch to the corresponding page in the ViewPager.
		this.viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter
	{
		public SectionsPagerAdapter(final FragmentManager fragmentManager)
		{
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int position)
		{
			final FragmentManager manager = getSupportFragmentManager();
			switch (position)
			{
				case 0:
					return new SetupStatusFragment();
				case 1:
					return new SetupFileFragment();
				case 2:
					return new SetupDatabaseFragment();
				case 3:
					return new SetupSqlFragment();
			}
			return null;
		}

		@Override
		public int getCount()
		{
			// Show 3 total pages.
			return 4;
		}

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setup, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			final Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
