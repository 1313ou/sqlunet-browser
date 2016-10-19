package org.sqlunet.browser.config;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.sqlunet.browser.R;
import org.sqlunet.browser.StatusActivity;
import org.sqlunet.provider.ManagerContract;
import org.sqlunet.provider.ManagerProvider;
import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.settings.StorageSettings;

/**
 * Management activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class ManagementActivity extends Activity
{
	// private static final String TAG = "ManagementActivity";

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// layout
		setContentView(R.layout.activity_management);

		// show the Up button in the action bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);

		// show fragment in the container view.
		if (savedInstanceState == null)
		{
			final Fragment fragment = new ManagementFragment();
			fragment.setArguments(this.getIntent().getExtras());
			getFragmentManager().beginTransaction() //
					.replace(R.id.container_management, fragment)//
					.commit();
		}
	}

	// M E N U

	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		// inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.management, menu);
		return true;
	}

	// A C T I O N H A N D L I N G

	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		Intent intent = null;

		// handle item selection
		switch (item.getItemId())
		{
			case R.id.action_settings:
				intent = new Intent(this, SettingsActivity.class);
				break;

			case R.id.action_status:
				intent = new Intent(this, StatusActivity.class);
				break;

			case R.id.action_setup:
				intent = new Intent(this, SetupActivity.class);
				break;

			case R.id.action_setup_sql:
				intent = new Intent(this, SetupSqlActivity.class);
				break;

			case R.id.action_tables_and_indices:
				intent = ManagerContract.makeTablesAndIndicesIntent(this);
				intent.putExtra(SqlUNetContract.ARG_QUERYLAYOUT, R.layout.item_manager);
				break;

			case R.id.action_create_database:
				ManagementTasks.createDatabase(this, StorageSettings.getDatabasePath(this));
				break;

			case R.id.action_drop_database:
				ManagementTasks.deleteDatabase(this, StorageSettings.getDatabasePath(this));
				break;

			case R.id.action_vacuum:
				ManagementTasks.vacuum(this, StorageSettings.getDatabasePath(this), StorageSettings.getDataDir(this));
				break;

			case R.id.action_flush_tables:
				ManagementTasks.flushAll(this, StorageSettings.getDatabasePath(this), ManagerProvider.getTables(this));
				break;

			case R.id.action_drop_tables:
				ManagementTasks.dropAll(this, StorageSettings.getDatabasePath(this), ManagerProvider.getTables(this));
				break;

			default:
				return super.onOptionsItemSelected(item);
		}

		if (intent != null)
		{
			startActivity(intent);
		}
		return true;
	}
}
