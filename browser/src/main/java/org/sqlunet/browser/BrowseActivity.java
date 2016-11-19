package org.sqlunet.browser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Browse activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class BrowseActivity extends Activity
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
		this.fragment = (BrowseFragment) getFragmentManager().findFragmentById(R.id.fragment_browse);
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

	// S E A R C H

	/**
	 * Handle intent dispatched by search view (either onCreate or onNewIntent if activity is single top)
	 *
	 * @param intent intent
	 */
	private void handleSearchIntent(final Intent intent)
	{
		final String action = intent.getAction();
		//final String query = intent.getStringExtra(SearchManager.QUERY);
		final String query = intent.getDataString();

		// view type
		if (Intent.ACTION_VIEW.equals(action))
		{
			// suggestion selection (when a suggested item is selected)
			this.fragment.suggest(query);
			return;
		}

		// search type
		if (Intent.ACTION_SEARCH.equals(action))
		{
			this.fragment.search(query);
		}
	}
}
