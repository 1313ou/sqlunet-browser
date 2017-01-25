package org.sqlunet.framenet.browser;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import org.sqlunet.framenet.R;

/**
 * AnnoSet activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnAnnoSetActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_fnannoset);

		// show the Up button in the type bar.
		final ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);

		// fragment
		// savedInstanceState is non-null when there is fragment state saved from previous configurations of this activity (e.g. when rotating the screen from
		// portrait to landscape). In this case, the fragment will automatically be re-added to its container so we don't need to manually addItem it.
		// @see http://developer.android.com/guide/components/fragments.html
		if (savedInstanceState == null)
		{
			// create the sense fragment, transmit intent's extras as parameters and addItem it to the activity using a fragment transaction
			final FnAnnoSetFragment fragment = new FnAnnoSetFragment();
			fragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager() //
					.beginTransaction() //
					.replace(R.id.container_annoset, fragment) //
					.commit();
		}
	}
}
