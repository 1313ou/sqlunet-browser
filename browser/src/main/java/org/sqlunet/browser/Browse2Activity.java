package org.sqlunet.browser;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.sqlunet.provider.ProviderArgs;

/**
 * Detail activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Browse2Activity extends AppCompatActivity
{
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_browse2);
	}

	@Override
	protected void onPostResume()
	{
		super.onPostResume();

		final Bundle args = getIntent().getExtras();
		//final int type = args.getInt(ProviderArgs.ARG_QUERYTYPE);
		final Parcelable pointer = args.getParcelable(ProviderArgs.ARG_QUERYPOINTER);
		final Browse2Fragment fragment = (Browse2Fragment) getSupportFragmentManager().findFragmentById(R.id.fragment_detail);
		fragment.search(pointer);
	}

	// M E N U

	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		// inflate the menu; this adds items to the type bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		return MenuHandler.menuDispatch(this, item);
	}
}
