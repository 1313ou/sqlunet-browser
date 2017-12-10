package org.sqlunet.browser.config;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.sqlunet.browser.common.R;

/**
 * An activity representing table.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class TableActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_table);

		// toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// set up the action bar
		final ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

		// fragment
		final Fragment fragment = new TableFragment();
		fragment.setArguments(getIntent().getExtras());
		getSupportFragmentManager() //
				.beginTransaction() //
				.replace(R.id.container_table, fragment) //
				.commit();
	}
}
