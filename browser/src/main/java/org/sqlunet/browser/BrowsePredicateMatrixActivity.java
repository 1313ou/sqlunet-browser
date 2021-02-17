/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import org.sqlunet.predicatematrix.PmRolePointer;
import org.sqlunet.provider.ProviderArgs;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

/**
 * Predicate Matrix activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class BrowsePredicateMatrixActivity extends AbstractBrowseActivity<BrowsePredicateMatrixFragment>
{
	@LayoutRes
	protected int getLayoutId()
	{
		return R.layout.activity_predicatematrix;
	}

	@IdRes
	protected int getFragmentId()
	{
		return R.id.fragment_predicatematrix;
	}

	// S E A R C H

	/**
	 * Handle intent dispatched by search view (either onCreate or onNewIntent if activity is single top)
	 *
	 * @param intent intent
	 */
	@Override
	protected void handleSearchIntent(@NonNull final Intent intent)
	{
		final String action = intent.getAction();

		final boolean isActionView = Intent.ACTION_VIEW.equals(action);
		if (isActionView || Intent.ACTION_SEARCH.equals(action))
		{
			// search query submit (SEARCH) or suggestion selection (when a suggested item is selected) (VIEW)
			final String query = intent.getStringExtra(SearchManager.QUERY);
			assert this.fragment != null;
			if (isActionView)
			{
				this.fragment.clearQuery();
			}
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
