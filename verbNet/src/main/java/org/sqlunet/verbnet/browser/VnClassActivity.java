package org.sqlunet.verbnet.browser;


import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

import org.sqlunet.verbnet.R;

/**
 * VerbNet class activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VnClassActivity extends Activity
{
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content layout
		setContentView(R.layout.activity_vnclass);

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
			final VnClassFragment fragment = new VnClassFragment();
			fragment.setArguments(args);
			getFragmentManager().beginTransaction().add(R.id.container_vnclass, fragment).commit();
		}
	}
}
