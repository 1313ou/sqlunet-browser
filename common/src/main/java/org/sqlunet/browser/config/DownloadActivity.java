/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.config;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.sqlunet.browser.EntryActivity;
import org.sqlunet.browser.MenuHandler;
import org.sqlunet.browser.common.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

public class DownloadActivity extends org.sqlunet.download.DownloadActivity
{
	static private final String TAG = "DownloadA";

	@Override
	public void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// set up the action bar
		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
		{
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
		}
	}

	// M E N U

	@SuppressWarnings("SameReturnValue")
	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		// inflate the menu; this adds items to the type bar if it is present.
		getMenuInflater().inflate(R.menu.initialize, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull final MenuItem item)
	{
		// handle home
		if (item.getItemId() == android.R.id.home)
		{
			Log.d(TAG, "onHomePressed");
			EntryActivity.rerun(this);
			return true;
		}
		return MenuHandler.menuDispatchWhenCantRun(this, item);
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
	}
}
