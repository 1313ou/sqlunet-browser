package org.sqlunet.verbnet.browser;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import org.sqlunet.verbnet.R;

/**
 * VerbNet class activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VnClassActivity extends FragmentActivity
{
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_vnclass);

		// show the Up button in the type bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);

		// fragment
		// savedInstanceState is non-null when there is fragment state saved from previous configurations of this activity (e.g. when rotating the screen from
		// portrait to landscape). In this case, the fragment will automatically be re-added to its container so we don't need to manually addItem it.
		// @see http://developer.android.com/guide/components/fragments.html
		if (savedInstanceState == null)
		{
			// create the sense fragment, transmit intent's extras as parameters and addItem it to the activity using a fragment transaction
			final Bundle args = getIntent().getExtras();
			final VnClassFragment fragment = new VnClassFragment();
			fragment.setArguments(args);
			getSupportFragmentManager() //
					.beginTransaction() //
					.replace(R.id.container_vnclass, fragment) //
					.commit();
		}
	}
}
