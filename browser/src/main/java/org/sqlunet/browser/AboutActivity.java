package org.sqlunet.browser;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * About activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class AboutActivity extends Activity
{
	// protected static final String TAG = "AboutActivity";
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// layout
		setContentView(R.layout.activity_about);

		// show the Up button in the action bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		if (item.getItemId() == R.id.action_help)
		{
			HelpActivity.start(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Show about
	 */
	@SuppressWarnings("unused")
	static public void start(final Context context)
	{
		final Intent intent = new Intent(context, AboutActivity.class);
		context.startActivity(intent);
	}
}
