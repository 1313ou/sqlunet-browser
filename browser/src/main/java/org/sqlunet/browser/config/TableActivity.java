package org.sqlunet.browser.config;

import org.sqlunet.browser.R;
import org.sqlunet.provider.SqlUNetContract;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * An activity representing table.
 *
 * @author Bernard Bou
 */
public class TableActivity extends Activity
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// layout
		setContentView(R.layout.activity_table);

		// show the Up button in the action bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);

		// status
		final Intent intent = getIntent();
		final String query = intent.getStringExtra(SqlUNetContract.ARG_QUERYURI);
		final String filter = intent.getStringExtra(SqlUNetContract.ARG_QUERYFILTER);
		final TextView queryView = (TextView) findViewById(R.id.queryView);
		queryView.setText(String.format("%s (filter: %s)", query, filter)); //$NON-NLS-1$
	}
}
