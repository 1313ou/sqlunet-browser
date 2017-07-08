package org.sqlunet.browser;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Browse activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class BrowseActivity extends AppCompatActivity
{
	/**
	 * Fragment
	 */
	private BrowseFragment fragment;

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_browse);

		// fragment
		this.fragment = (BrowseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_browse);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		handleSearchIntent(getIntent());
	}

	@Override
	protected void onNewIntent(final Intent intent)
	{
		handleSearchIntent(intent);
	}

	// M E N U

	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		// inflate the menu; this adds items to the type bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		return MenuHandler.menuDispatch(this, item);
	}

	// S E A R C H

	/**
	 * Handle intent dispatched by search view (either onCreate or onNewIntent if activity is single top)
	 *
	 * @param intent intent
	 */
	private void handleSearchIntent(final Intent intent)
	{
		final String action = intent.getAction();
		if (Intent.ACTION_VIEW.equals(action) || Intent.ACTION_SEARCH.equals(action))
		{
			// search query submit or suggestion selection (when a suggested item is selected)
			final String query = intent.getStringExtra(SearchManager.QUERY);
			this.fragment.search(query);
		}
	}
}
