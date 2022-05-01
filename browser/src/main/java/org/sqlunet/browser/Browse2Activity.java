/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;

import org.sqlunet.provider.ProviderArgs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Detail activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Browse2Activity extends AbstractBrowse2Activity
{
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_browse2);

		// toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// set up the action bar
		final ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

		// fragment
		final FragmentManager manager = getSupportFragmentManager();
		Fragment browse2Fragment = manager.findFragmentByTag("browse2");
		if (browse2Fragment == null)
		{
			browse2Fragment = new Browse2Fragment();
			final boolean alt = getIntent().getBooleanExtra(Browse2Fragment.ARG_ALT, false);
			final Bundle args = new Bundle();
			args.putBoolean(Browse2Fragment.ARG_ALT, alt);
			browse2Fragment.setArguments(args);
		}
		manager.beginTransaction() //
				.replace(R.id.container_browse2, browse2Fragment, "browse2") //
				.commit();
	}

	@Override
	protected void onPostResume()
	{
		super.onPostResume();

		final Bundle args = getIntent().getExtras();
		assert args != null;

		//final int type = args.getInt(ProviderArgs.ARG_QUERYTYPE);
		final Parcelable pointer = args.getParcelable(ProviderArgs.ARG_QUERYPOINTER);
		final String pos = args.getString(ProviderArgs.ARG_HINTPOS);
		final Browse2Fragment fragment = (Browse2Fragment) getSupportFragmentManager().findFragmentByTag("browse2");
		assert fragment != null;
		fragment.search(pointer, pos);
	}

	// M E N U

	@SuppressWarnings("SameReturnValue")
	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		// inflate the menu; this adds items to the type bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull final MenuItem item)
	{
		return MenuHandler.menuDispatch(this, item);
	}

	/*
	@Override
	public boolean onPrepareOptionsMenu(final Menu menu)
	{
		MenuHandler.populateAssets(this, menu);
		return super.onPrepareOptionsMenu(menu);
	}
	*/
}
