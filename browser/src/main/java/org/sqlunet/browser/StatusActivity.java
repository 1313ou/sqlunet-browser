package org.sqlunet.browser;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.sqlunet.browser.config.DownloadActivity;
import org.sqlunet.browser.config.ManagementActivity;
import org.sqlunet.browser.config.ManagementFragment;
import org.sqlunet.browser.config.SettingsActivity;
import org.sqlunet.browser.config.SetupActivity;
import org.sqlunet.browser.config.SetupSqlActivity;
import org.sqlunet.browser.config.Status;
import org.sqlunet.browser.config.StorageActivity;
import org.sqlunet.settings.Settings;

/**
 * Status activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class StatusActivity extends Activity
{
	static private final String TAG = "SqlUNet Status"; //

	// codes

	private static final int REQUEST_DOWNLOAD_CODE = 0xDDDD;

	private static final int REQUEST_MANAGEMENT_CODE = 0xAAA0;

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// layout
		setContentView(R.layout.activity_status);

		// show the Up button in the action bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onResume()
	{
		super.onRestart();

		// status
		final ImageView db = (ImageView) findViewById(R.id.status_database);
		final ImageButton b_db = (ImageButton) findViewById(R.id.databaseButton);
		final ImageButton b_idx = (ImageButton) findViewById(R.id.indexesButton);
		final ImageButton b_pm = (ImageButton) findViewById(R.id.predicatematrixButton);
		final ImageButton b_ts = (ImageButton) findViewById(R.id.textsearchButton);
		final ImageButton b_tsfn = (ImageButton) findViewById(R.id.textsearchfnButton);

		// click listeners
		b_db.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(StatusActivity.this, DownloadActivity.class);
				startActivityForResult(intent, StatusActivity.REQUEST_DOWNLOAD_CODE);
			}
		});

		b_idx.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(StatusActivity.this, ManagementActivity.class);
				intent.putExtra(ManagementFragment.ARG, 0);
				startActivityForResult(intent, StatusActivity.REQUEST_MANAGEMENT_CODE + 1);
			}
		});

		b_pm.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(StatusActivity.this, ManagementActivity.class);
				intent.putExtra(ManagementFragment.ARG, 1);
				startActivityForResult(intent, StatusActivity.REQUEST_MANAGEMENT_CODE + 2);
			}
		});

		b_ts.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(StatusActivity.this, ManagementActivity.class);
				intent.putExtra(ManagementFragment.ARG, 2);
				startActivityForResult(intent, StatusActivity.REQUEST_MANAGEMENT_CODE + 3);
			}
		});

		b_tsfn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(StatusActivity.this, ManagementActivity.class);
				intent.putExtra(ManagementFragment.ARG, 3);
				startActivityForResult(intent, StatusActivity.REQUEST_MANAGEMENT_CODE + 4);
			}
		});

		final int status = Status.status(getBaseContext());
		if (status != 0)
		{
			db.setImageResource(R.drawable.ic_ok);
			b_db.setVisibility(View.GONE);

			final boolean existsIdx = (status & Status.EXISTS_IDX) != 0;
			final boolean existsPm = (status & Status.EXISTS_PM) != 0;
			final boolean existsTs = (status & Status.EXISTS_TS) != 0;
			final boolean existsTsFn = (status & Status.EXISTS_TSFN) != 0;

			final ImageView idx = (ImageView) findViewById(R.id.status_indexes);
			final ImageView pm = (ImageView) findViewById(R.id.status_predicatematrix);
			final ImageView ts = (ImageView) findViewById(R.id.status_textsearch);
			final ImageView tsfn = (ImageView) findViewById(R.id.status_textsearchfn);
			idx.setImageResource(existsIdx ? R.drawable.ic_ok : R.drawable.ic_fail);
			pm.setImageResource(existsPm ? R.drawable.ic_ok : R.drawable.ic_fail);
			ts.setImageResource(existsTs ? R.drawable.ic_ok : R.drawable.ic_fail);
			tsfn.setImageResource(existsTsFn ? R.drawable.ic_ok : R.drawable.ic_fail);

			b_idx.setVisibility(existsIdx ? View.GONE : View.VISIBLE);
			b_pm.setVisibility(existsPm ? View.GONE : View.VISIBLE);
			b_ts.setVisibility(existsTs ? View.GONE : View.VISIBLE);
			b_tsfn.setVisibility(existsTsFn ? View.GONE : View.VISIBLE);
		}
		else
		{
			db.setImageResource(R.drawable.ic_fail);
			b_db.setVisibility(View.VISIBLE);
		}
	}

	// M E N U

	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		// inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.status, menu);
		return true;
	}

	// A C T I O N H A N D L I N G

	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		Intent intent;

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
			case R.id.action_storage:
				intent = new Intent(this, StorageActivity.class);
				break;
			case R.id.action_management:
				intent = new Intent(this, ManagementActivity.class);
				break;
			case R.id.action_appsettings:
				Settings.applicationSettings(this, "org.sqlunet.browser"); //
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}

		// start activity
		startActivity(intent);
		return true;
	}

	// S P E C I F I C R E T U R N S

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent returnIntent)
	{
		// handle selection of input by other activity which returns selected input
		switch (requestCode)
		{
			case REQUEST_DOWNLOAD_CODE:
				Log.d(TAG, "result=" + resultCode); //
				break;
			default:
				break;
		}
		super.onActivityResult(requestCode, resultCode, returnIntent);
	}
}
