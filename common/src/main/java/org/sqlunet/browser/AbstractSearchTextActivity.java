/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.sqlunet.browser.common.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Abstract search text activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public abstract class AbstractSearchTextActivity<F extends BaseSearchFragment> extends AppCompatActivity
{
	/**
	 * Fragment
	 */
	@Nullable
	private F fragment;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_searchtext);

		// toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// fragment
		if (savedInstanceState == null)
		{
			this.fragment = (F) getSupportFragmentManager().findFragmentById(R.id.fragment_searchtext);
			assert this.fragment != null;
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		// check hook
		EntryActivity.forkOffIfCantRun(this);

		// handle sent intent
		handleSearchIntent(getIntent());
	}

	@Override
	protected void onNewIntent(@NonNull final Intent intent)
	{
		super.onNewIntent(intent);
		handleSearchIntent(intent);
	}

	@Override
	protected void onNightModeChanged(final int mode)
	{
		super.onNightModeChanged(mode);
		final Configuration overrideConfig = AbstractApplication.createOverrideConfigurationForDayNight(this, mode);
		getApplication().onConfigurationChanged(overrideConfig);
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

	// S E A R C H

	/**
	 * Handle intent dispatched by search view (either onCreate or onNewIntent if activity is single top)
	 *
	 * @param intent intent
	 */
	private void handleSearchIntent(@NonNull final Intent intent)
	{
		final String action = intent.getAction();
		final boolean isActionView = Intent.ACTION_VIEW.equals(action);
		if (isActionView || Intent.ACTION_SEARCH.equals(action))
		{
			// search query submit (SEARCH) or suggestion selection (when a suggested item is selected) (VIEW)
			final String query = intent.getStringExtra(SearchManager.QUERY);
			if (query != null)
			{
				assert this.fragment != null;
				if (isActionView)
				{
					this.fragment.clearQuery();
				}
				this.fragment.search(query);
			}
		}
		else if (Intent.ACTION_SEND.equals(action))
		{
			final String type = intent.getType();
			if ("text/plain".equals(type))
			{
				final String query = intent.getStringExtra(Intent.EXTRA_TEXT);
				if (query != null)
				{
					assert this.fragment != null;
					this.fragment.search(query);
				}
			}
		}
	}
}
