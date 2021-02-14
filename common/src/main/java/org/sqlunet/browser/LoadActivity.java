/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.sqlunet.browser.common.R;
import org.sqlunet.browser.config.Status;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Status activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class LoadActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_asset);

		// toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// actionbar
		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
		{
			final Intent intent = getIntent();
			boolean cantRun = intent.getBooleanExtra(Status.CANTRUN, false);
			actionBar.setDisplayShowHomeEnabled(!cantRun);
			actionBar.setDisplayHomeAsUpEnabled(!cantRun);
			actionBar.setDisplayShowTitleEnabled(true);
		}
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
		// handle item selection
		if (item.getItemId() == android.R.id.home)
		{
			final Intent intent = getIntent();
			boolean cantRun = intent.getBooleanExtra(Status.CANTRUN, false);
			if (!cantRun)
			{
				return super.onOptionsItemSelected(item);
			}
		}

		return MenuHandler.menuDispatch(this, item);
	}
}
