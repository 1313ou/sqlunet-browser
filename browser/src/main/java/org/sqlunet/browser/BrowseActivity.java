package org.sqlunet.browser;

import android.app.Activity;
import android.app.SearchManager;
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
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(final Intent intent)
	{
		handleIntent(intent);
	}

	// I N T E N T

	/**
	 * Handle intent (either onCreate or if activity is single top)
	 *
	 * @param intent intent
	 */
	private void handleIntent(final Intent intent)
	{
		final String action = intent.getAction();
		final String query = intent.getStringExtra(SearchManager.QUERY);

		// view action
		if (Intent.ACTION_VIEW.equals(action))
		{
			// suggestion selection (when a suggested item is selected)
			this.fragment.suggest(query);
			return;
		}

		// search action
		if (Intent.ACTION_SEARCH.equals(action))
		{
			this.fragment.search(query);
		}
	}
}
