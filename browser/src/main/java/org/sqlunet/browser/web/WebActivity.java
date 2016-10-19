package org.sqlunet.browser.web;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

import org.sqlunet.browser.R;

/**
 * An activity representing a web view.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class WebActivity extends Activity
{
	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// lay out
		setContentView(R.layout.activity_web);

		// show the Up button in the action bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
}
