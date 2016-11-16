package org.sqlunet.predicatematrix.browser;

import android.app.Activity;
import android.app.SearchManager;
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

		// search
		if (Intent.ACTION_SEARCH.equals(action))
		{
			this.fragment.search(query);
		}
		else
		{
			final Bundle args = intent.getExtras();
			if (args != null)
			{
				final int queryAction = args.getInt(ProviderArgs.ARG_QUERYACTION);
				if (ProviderArgs.ARG_QUERYACTION_PM == queryAction || ProviderArgs.ARG_QUERYACTION_PMROLE == queryAction)
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
