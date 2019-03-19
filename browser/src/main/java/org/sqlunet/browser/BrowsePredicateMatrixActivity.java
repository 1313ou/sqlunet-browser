package org.sqlunet.browser;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.sqlunet.predicatematrix.PmRolePointer;
import org.sqlunet.provider.ProviderArgs;

/**
 * Predicate Matrix activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class BrowsePredicateMatrixActivity extends AppCompatActivity
{
	/**
	 * Fragment
	 */
	@Nullable
	private BrowsePredicateMatrixFragment fragment;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_predicatematrix);

		// toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// fragment
		if (savedInstanceState == null)
		{
			this.fragment = (BrowsePredicateMatrixFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_predicatematrix);

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
			assert this.fragment != null;
			this.fragment.search(query);
			return;
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
					return;
				}
			}
		}

		// search query from other source
		if (ProviderArgs.ACTION_QUERY.equals(action))
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
						assert this.fragment != null;
						this.fragment.search(rolePointer);
					}
				}
			}
		}
	}
}
