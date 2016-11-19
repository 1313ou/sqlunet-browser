package org.sqlunet.browser;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;

import org.sqlunet.provider.ProviderArgs;

/**
 * Detail activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Browse2Activity extends Activity
{
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_browse2);

		// show the Up button in the type bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onPostResume()
	{
		super.onPostResume();

		final Bundle args = getIntent().getExtras();
		//final int type = args.getInt(ProviderArgs.ARG_QUERYTYPE);
		final Parcelable pointer = args.getParcelable(ProviderArgs.ARG_QUERYPOINTER);
		final Browse2Fragment fragment = (Browse2Fragment) getFragmentManager().findFragmentById(R.id.fragment_detail);
		fragment.search(pointer);
	}
}
