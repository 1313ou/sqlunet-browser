package org.sqlunet.browser.config;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.sqlunet.browser.R;
import org.sqlunet.provider.ProviderArgs;

/**
 * An activity representing table.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class TableActivity extends Activity
{
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

		// query
		final Intent intent = getIntent();
		final String query = intent.getStringExtra(ProviderArgs.ARG_QUERYURI);
		final String filter = intent.getStringExtra(ProviderArgs.ARG_QUERYFILTER);
		final TextView queryView = (TextView) findViewById(R.id.queryView);
		queryView.setText(String.format("%s (filter: %s)", query, filter));
	}
}
