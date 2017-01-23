package org.sqlunet.browser.config;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.sqlunet.browser.MainActivity;
import org.sqlunet.browser.R;

/**
 * Set up with SQL activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SetupSqlActivity extends FragmentActivity
{
	static private final String TAG = "SetupSqlActivity";

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_setup_actions);

		// show the Up button in the type bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);

		// fragment
		final Fragment fragment = new SetupSqlFragment();
		fragment.setArguments(getIntent().getExtras());
		getSupportFragmentManager() //
				.beginTransaction() //
				.replace(R.id.container_setup, fragment) //
				.commit();
	}

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
		return MainActivity.dispatch(this, item);
	}
}
