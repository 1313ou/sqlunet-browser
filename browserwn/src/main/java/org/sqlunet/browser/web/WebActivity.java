package org.sqlunet.browser.web;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.sqlunet.browser.wn.R;

/**
 * An activity representing a web view.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class WebActivity extends AppCompatActivity
{
	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// lay out
		setContentView(R.layout.activity_web);

		// toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// set up the action bar
		final ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

		// fragment
		// savedInstanceState is non-null when there is fragment state saved from previous configurations of this activity (e.g. when rotating the screen from
		// portrait to landscape). In this case, the fragment will automatically be re-added to its container so we don't need to manually addItem it.
		// @see http://developer.android.com/guide/components/fragments.html
		if (savedInstanceState == null)
		{
			final Fragment fragment = new WebFragment();
			fragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager() //
					.beginTransaction() //
					.replace(R.id.container_web, fragment) //
					.commit();
		}
	}
}
