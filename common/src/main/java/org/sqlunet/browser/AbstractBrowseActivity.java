package org.sqlunet.browser;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.sqlunet.browser.common.R;

/**
 * Browse activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public abstract class AbstractBrowseActivity<F extends BaseSearchFragment> extends AppCompatActivity
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
		setContentView(R.layout.activity_browse);

		// toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// fragment
		if (savedInstanceState == null)
		{
			this.fragment = (F) getSupportFragmentManager().findFragmentById(R.id.fragment_browse);

			// set up the action bar
			final ActionBar actionBar = getSupportActionBar();
			assert actionBar != null;
			assert this.fragment != null;
			this.fragment.setActionBarUpDisabled(actionBar, this);
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
		if (Intent.ACTION_VIEW.equals(action) || Intent.ACTION_SEARCH.equals(action))
		{
			// search query submit or suggestion selection (when a suggested item is selected)
			final String query = intent.getStringExtra(SearchManager.QUERY);
			if (query != null)
			{
				assert this.fragment != null;
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
