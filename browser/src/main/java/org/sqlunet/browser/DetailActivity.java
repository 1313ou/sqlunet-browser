package org.sqlunet.browser;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;

import org.sqlunet.browser.selector.SelectorActivity;
import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.wordnet.browser.SenseFragment;

/**
 * An activity representing a sense detail screen. This activity is only used on handset devices. On tablet-size devices, sense details are presented
 * side-by-side with a list of items in a {@link SelectorActivity}. This activity is mostly just a 'shell' activity containing nothing more than a
 * {@link SenseFragment}.
 *
 * @author Bernard Bou
 */
public class DetailActivity extends Activity
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// layout
		setContentView(R.layout.activity_detail);

		// show the Up button in the action bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);

		// savedInstanceState is non-null when there is fragment state saved from previous configurations of this activity (e.g. when rotating the screen from
		// portrait to landscape). In this case, the fragment will automatically be re-added to its container so we don't need to manually add it.
		// @see http://developer.android.com/guide/components/fragments.html
		//		if (savedInstanceState == null)
		//		{
		//			// create the sense fragment, transmit intents as parameters and add it to the activity using a fragment transaction
		//			final DetailFragment fragment = new DetailFragment();
		//			getFragmentManager().beginTransaction().replace(R.id.container_main, fragment).commit();
		//		}
	}

	@Override
	protected void onPostResume()
	{
		super.onPostResume();

		final Bundle arguments = getIntent().getExtras();
		final Parcelable pointer = arguments.getParcelable(SqlUNetContract.ARG_QUERYPOINTER);
		final DetailFragment fragment = (DetailFragment) getFragmentManager().findFragmentById(R.id.fragment_detail);
		fragment.search(pointer);
	}
}
