/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.xn;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;

import org.sqlunet.browser.AbstractBrowse2Activity;
import org.sqlunet.browser.BaseBrowse2Fragment;
import org.sqlunet.browser.MenuHandler;
import org.sqlunet.browser.R;
import org.sqlunet.browser.xn.Browse2Fragment;
import org.sqlunet.provider.ProviderArgs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Detail activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Browse2Activity extends AbstractBrowse2Activity
{
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_browse2);

		// toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// set up the action bar
		final ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

		// fragment
		final FragmentManager manager = getSupportFragmentManager();
		Fragment browse2Fragment = manager.findFragmentByTag(BaseBrowse2Fragment.FRAGMENT_TAG);
		if (browse2Fragment == null)
		{
			browse2Fragment = new Browse2Fragment();
			final boolean alt = getIntent().getBooleanExtra(Browse2Fragment.ARG_ALT, false);
			final Bundle args = new Bundle();
			args.putBoolean(Browse2Fragment.ARG_ALT, alt);
			browse2Fragment.setArguments(args);
		}
		manager.beginTransaction() //
				.setReorderingAllowed(true) //
				.replace(R.id.container_browse2, browse2Fragment, BaseBrowse2Fragment.FRAGMENT_TAG) //
				// .addToBackStack(BaseBrowse2Fragment.FRAGMENT_TAG) //
				.commit();
	}

	@Override
	protected void onPostResume()
	{
		super.onPostResume();

		final Bundle args = getIntent().getExtras();
		assert args != null;

		//final int type = args.getInt(ProviderArgs.ARG_QUERYTYPE);
		final Parcelable pointer = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU ? args.getParcelable(ProviderArgs.ARG_QUERYPOINTER, Parcelable.class) : args.getParcelable(ProviderArgs.ARG_QUERYPOINTER);
		final String word = args.getString(ProviderArgs.ARG_HINTWORD);
		final String cased = args.getString(ProviderArgs.ARG_HINTCASED);
		final String pronunciation = args.getString(ProviderArgs.ARG_HINTPRONUNCIATION);
		final String pos = args.getString(ProviderArgs.ARG_HINTPOS);
		final Browse2Fragment fragment = (Browse2Fragment) getSupportFragmentManager().findFragmentByTag(BaseBrowse2Fragment.FRAGMENT_TAG);
		assert fragment != null;
		fragment.search(pointer, word, cased, pronunciation, pos);
	}

	// M E N U

	@SuppressWarnings("SameReturnValue")
	@Override
	public boolean onCreateOptionsMenu(@NonNull final Menu menu)
	{
		// inflate the menu; this adds items to the type bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		// MenuCompat.setGroupDividerEnabled(menu, true);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull final MenuItem item)
	{
		return MenuHandler.menuDispatch(this, item);
	}
}
