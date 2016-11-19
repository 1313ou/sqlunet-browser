package org.sqlunet.predicatematrix.browser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import org.sqlunet.predicatematrix.PmRolePointer;
import org.sqlunet.predicatematrix.R;
import org.sqlunet.provider.ProviderArgs;

/**
 * Predicate Matrix activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PredicateMatrixActivity extends Activity
{
	/**
	 * Fragment
	 */
	private PredicateMatrixFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_predicatematrix);

		// fragment
		this.fragment = (PredicateMatrixFragment) getFragmentManager().findFragmentById(R.id.fragment_predicatematrix);
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

		// suggestion from search view
		if (Intent.ACTION_VIEW.equals(action))
		{
			// suggestion selection (when a suggested item is selected)
			this.fragment.suggest(query);
			return;
		}

		// search query from search view
		if (Intent.ACTION_SEARCH.equals(action))
		{
			this.fragment.search(query);
			return;
		}

		// search query from other source
		if(ProviderArgs.ACTION_QUERY.equals(action))
		{
			final Bundle args = intent.getExtras();
			if (args != null)
			{
				final int type = args.getInt(ProviderArgs.ARG_QUERYTYPE);
				if (ProviderArgs.ARG_QUERYTYPE_PM == type || ProviderArgs.ARG_QUERYTYPE_PMROLE == type)
				{
					final Parcelable pointer = args.getParcelable(ProviderArgs.ARG_QUERYPOINTER);
					if (pointer instanceof PmRolePointer)
					{
						final PmRolePointer rolePointer = (PmRolePointer) pointer;
						this.fragment.search(rolePointer);
					}
				}
			}
		}
	}
}
