package org.sqlunet.browser;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

/**
 * Text search activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class TextSearchActivity extends Activity
{
	/**
	 * Fragment
	 */
	private TextSearchFragment fragment;

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_textsearch);

		// fragment
		if (savedInstanceState == null)
		{
			this.fragment = (TextSearchFragment) getFragmentManager().findFragmentById(R.id.fragment_textsearch);
		}
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
		if (Intent.ACTION_VIEW.equals(action) || Intent.ACTION_SEARCH.equals(action))
		{
			// search query submit or suggestion selection (when a suggested item is selected)
			final String query = intent.getStringExtra(SearchManager.QUERY);
			this.fragment.search(query);
			return;
		}
	}
}
