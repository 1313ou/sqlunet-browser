package org.sqlunet.wordnet.browser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.sqlunet.wordnet.R;

/**
 * Sense activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SenseKeyActivity extends SynsetActivity
{
	private boolean fromSavedInstance;

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_synset);

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
				final SenseKeyFragment fragment2 = new SenseKeyFragment();
				final Bundle args = getIntent().getExtras();
				fragment2.setArguments(args);
				fragment2.setExpand(true);
				fragment = fragment2;
			}
			manager.beginTransaction() //
					.replace(R.id.container_synset, fragment) //
					.commit();
		}
	}
}
