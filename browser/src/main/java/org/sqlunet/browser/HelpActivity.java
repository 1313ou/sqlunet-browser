package org.sqlunet.browser;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

/**
 * Help activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class HelpActivity extends Activity
{
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_help);

		// show the Up button in the type bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
}
