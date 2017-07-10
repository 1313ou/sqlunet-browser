package org.sqlunet.browser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.sqlunet.browser.config.Status;

/**
 * Status activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class StatusActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_status);

		// actionbar
		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
		{
			final Intent intent = getIntent();
			boolean cantRun = intent.getBooleanExtra(Status.CANTRUN, false);
			actionBar.setDisplayShowHomeEnabled(!cantRun);
			actionBar.setDisplayHomeAsUpEnabled(!cantRun);
			actionBar.setDisplayShowTitleEnabled(true);
		}
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
		// handle item selection
		switch (item.getItemId())
		{
			case android.R.id.home:
				final Intent intent = getIntent();
				boolean cantRun = intent.getBooleanExtra(Status.CANTRUN, false);
				if (!cantRun)
				{
					return super.onOptionsItemSelected(item);
				}
				break;
		}

		return MenuHandler.menuDispatch(this, item);
	}
}
