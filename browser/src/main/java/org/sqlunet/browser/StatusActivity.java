package org.sqlunet.browser;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

/**
 * Status activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class StatusActivity extends Activity
{
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_status);

		// show the Up button in the type bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
		// actionBar.setDisplayShowCustomEnabled(false);
		// actionBar.setDisplayShowHomeEnabled(true);
		// actionBar.setDisplayHomeAsUpEnabled(true);
		// actionBar.setDisplayShowTitleEnabled(false);
	}
}
