package org.sqlunet.framenet.browser;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

import org.sqlunet.framenet.R;

/**
 * AnnoSetQuery activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnSentenceActivity extends Activity
{
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_fnsentence);

		// show the Up button in the action bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);

		// savedInstanceState is non-null when there is fragment state saved from previous configurations of this activity (e.g. when rotating the screen from
		// portrait to landscape). In this case, the fragment will automatically be re-added to its container so we don't need to manually add it.
		// @see http://developer.android.com/guide/components/fragments.html
		if (savedInstanceState == null)
		{
			// create the sense fragment, transmit intent's extras as parameters and add it to the activity using a fragment transaction
			final Bundle args = getIntent().getExtras();
			final FnSentenceFragment fragment = new FnSentenceFragment();
			fragment.setArguments(args);
			getFragmentManager().beginTransaction().add(R.id.container_sentence, fragment).commit();
		}
	}
}
