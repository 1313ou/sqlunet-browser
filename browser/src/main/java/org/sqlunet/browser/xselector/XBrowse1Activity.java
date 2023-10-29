/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser.xselector;

import android.os.Bundle;

import org.sqlunet.browser.AbstractBrowse1Activity;
import org.sqlunet.browser.BaseBrowse1Fragment;
import org.sqlunet.browser.R;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

/**
 * X selector activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class XBrowse1Activity extends AbstractBrowse1Activity
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

		// fragment
		// savedInstanceState is non-null when there is fragment state saved from previous configurations of this activity (e.g. when rotating the screen from
		// portrait to landscape). In this case, the fragment will automatically be re-added to its container so we don't need to manually addItem it.
		// @see http://developer.android.com/guide/components/fragments.html
		if (savedInstanceState == null)
		{
			final Fragment fragment = new XBrowse1Fragment();
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