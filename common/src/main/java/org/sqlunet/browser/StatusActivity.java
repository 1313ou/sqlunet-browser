/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.sqlunet.browser.common.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Status activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class StatusActivity extends AppCompatActivity
{
	static private final String TAG = "StatusA";

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_status);

		// toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// actionbar
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
