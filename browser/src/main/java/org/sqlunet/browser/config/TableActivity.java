package org.sqlunet.browser.config;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import org.sqlunet.browser.R;

/**
 * An activity representing table.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class TableActivity extends FragmentActivity
{
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_table);

		// show the Up button in the type bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);

		// fragment
		final Fragment fragment = new TableFragment();
		fragment.setArguments(getIntent().getExtras());
		getSupportFragmentManager() //
				.beginTransaction() //
				.replace(R.id.container_table, fragment) //
				.commit();
	}
}
