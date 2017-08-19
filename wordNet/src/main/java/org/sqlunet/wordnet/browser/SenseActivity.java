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
public class SenseActivity extends SynsetActivity
{
	public SenseActivity()
	{
		super();
		// this.layoutId = R.layout.activity_synset;
	}

	@Override
	public void onStart()
	{
		super.onStart();

		if (!this.fromSavedInstance)
		{
			// create the sense fragment, transmit intent's extras as parameters and add it to the activity using a fragment transaction
			final FragmentManager manager = getSupportFragmentManager();
			Fragment fragment = manager.findFragmentByTag("sense");
			if (fragment == null)
			{
				fragment = new SenseFragment();
				final Bundle args = getIntent().getExtras();
				fragment.setArguments(args);
			}
			manager.beginTransaction() //
					.replace(R.id.container_synset, fragment, "sense") //
					.commit();
		}
	}
}
