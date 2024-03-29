/*
 * Copyright (c) 2023. Bernard Bou
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Asset load activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class AssetLoadActivity extends AppCompatActivity
{
	static private final String TAG = "AssetLoadA";

	@Override
	public void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_assetload);

		// toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

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
	public boolean onCreateOptionsMenu(@NonNull final Menu menu)
	{
		// inflate the menu; this adds items to the type bar if it is present.
		getMenuInflater().inflate(R.menu.initialize, menu);
		// MenuCompat.setGroupDividerEnabled(menu, true);
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

	/** @noinspection deprecation*/
	@Override
	public void onBackPressed()
	{
		Log.d(TAG, "onBackPressed");
		super.onBackPressed();
	}
}
