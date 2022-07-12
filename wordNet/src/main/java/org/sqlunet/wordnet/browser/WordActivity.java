/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.browser;

import android.os.Bundle;

import org.sqlunet.wordnet.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Synset activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class WordActivity extends AppCompatActivity
{
	private boolean fromSavedInstance;

	@Override
	protected void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_word);

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
		if (savedInstanceState != null)
		{
			this.fromSavedInstance = true;
		}
	}

	@Override
	public void onStart()
	{
		super.onStart();

		if (!this.fromSavedInstance)
		{
			// create the word fragment, transmit intent's extras as parameters and addItem it to the activity using a fragment transaction
			final FragmentManager manager = getSupportFragmentManager();
			Fragment fragment = manager.findFragmentByTag("word");
			if (fragment == null)
			{
				fragment = new WordFragment();
				final Bundle args = getIntent().getExtras();
				fragment.setArguments(args);
			}
			manager.beginTransaction() //
					.setReorderingAllowed(true) //
					.replace(R.id.container_word, fragment, "word") //
					.commit();
		}
	}
}
