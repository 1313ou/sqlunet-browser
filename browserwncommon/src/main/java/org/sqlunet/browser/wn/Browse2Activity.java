/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.wn;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;

import org.sqlunet.browser.AbstractBrowse2Activity;
import org.sqlunet.browser.MenuHandler;
import org.sqlunet.browser.wn.Browse2Fragment;
import org.sqlunet.browser.wn.lib.R;
import org.sqlunet.provider.ProviderArgs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

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
	}

	@Override
	protected void onPostResume()
	{
		super.onPostResume();

		final Bundle args = getIntent().getExtras();
		assert args != null;
		//final int type = args.getInt(ProviderArgs.ARG_QUERYTYPE);
		final Parcelable pointer = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU ? args.getParcelable(ProviderArgs.ARG_QUERYPOINTER, Parcelable.class) : args.getParcelable(ProviderArgs.ARG_QUERYPOINTER);
		final String word = args.getString(ProviderArgs.ARG_HINTWORD);
		final String cased = args.getString(ProviderArgs.ARG_HINTCASED);
		final String pronunciation = args.getString(ProviderArgs.ARG_HINTPRONUNCIATION);
		final String pos = args.getString(ProviderArgs.ARG_HINTPOS);
		final Browse2Fragment fragment = (Browse2Fragment) getSupportFragmentManager().findFragmentById(R.id.fragment_detail);
		assert fragment != null;
		fragment.search(pointer, word, cased, pronunciation, pos);
	}

	// M E N U

	@Override
	public boolean onCreateOptionsMenu(@NonNull final Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		// MenuCompat.setGroupDividerEnabled(menu, true);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull final MenuItem item)
	{
		return MenuHandler.menuDispatch(this, item);
	}
}
