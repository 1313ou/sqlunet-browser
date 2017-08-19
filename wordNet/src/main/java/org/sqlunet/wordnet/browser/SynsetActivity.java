package org.sqlunet.wordnet.browser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.sqlunet.wordnet.R;

/**
 * Synset activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SynsetActivity extends AppCompatActivity
{
	protected boolean fromSavedInstance;

	protected int layoutId;

	public SynsetActivity()
	{
		this.layoutId = R.layout.activity_synset;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(this.layoutId);

		// toolbar
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

		//if (!this.fromSavedInstance)
		{
			// create the word fragment, transmit intent's extras as parameters and addItem it to the activity using a fragment transaction
			final FragmentManager manager = getSupportFragmentManager();
			Fragment fragment = manager.findFragmentByTag("synset");
			if (this.fromSavedInstance || fragment == null)
			{
				fragment = new SynsetFragment();
				final Bundle args = getIntent().getExtras();
				fragment.setArguments(args);
			}
			manager.beginTransaction() //
					.replace(R.id.container_synset, fragment, "synset") //
					.commit();
		}
	}
}
