/*
 * Copyright (c) 2020. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;

import org.sqlunet.provider.ProviderArgs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Detail activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SnBrowse2Activity extends AppCompatActivity
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
		final SnBrowse2Fragment snBrowse2Fragment = new SnBrowse2Fragment();
		final boolean alt = getIntent().getBooleanExtra(SnBrowse2Fragment.ARG_ALT, false);
		final Bundle args = new Bundle();
		args.putBoolean(SnBrowse2Fragment.ARG_ALT, alt);
		snBrowse2Fragment.setArguments(args);
		getSupportFragmentManager() //
				.beginTransaction() //
				.replace(R.id.container_browse2, snBrowse2Fragment, "snbrowse2") //
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
		final SnBrowse2Fragment fragment = (SnBrowse2Fragment) getSupportFragmentManager().findFragmentByTag("snbrowse2");
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
}
