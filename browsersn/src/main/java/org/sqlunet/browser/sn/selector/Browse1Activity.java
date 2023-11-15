/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.sn.selector;

import android.os.Bundle;

import org.sqlunet.browser.AbstractBrowse1Activity;
import org.sqlunet.browser.BaseBrowse1Fragment;
import org.sqlunet.browser.sn.R;
import org.sqlunet.browser.sn.selector.Browse1Fragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

/**
 * Selector activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Browse1Activity extends AbstractBrowse1Activity
{
	@Override
	protected void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_browse1);

		// toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// set up the action bar
		final ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

		// fragment
		// savedInstanceState is non-null when there is fragment state saved from previous configurations of this activity (e.g. when rotating the screen from
		// portrait to landscape). In this case, the fragment will automatically be re-added to its container so we don't need to manually addItem it.
		// @see http://developer.android.com/guide/components/fragments.html
		if (savedInstanceState == null)
		{
			final Fragment fragment = new Browse1Fragment();
			fragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager() //
					.beginTransaction() //
					.setReorderingAllowed(true) //
					.replace(R.id.container_browse, fragment, BaseBrowse1Fragment.FRAGMENT_TAG) //
					// .addToBackStack(BaseBrowse1Fragment.FRAGMENT_TAG) //
					.commit();
		}
	}
}
