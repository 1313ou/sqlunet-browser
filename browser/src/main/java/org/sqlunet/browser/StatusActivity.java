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
		final ImageButton buttonDb = (ImageButton) findViewById(R.id.databaseButton);
		final ImageButton buttonIndexes = (ImageButton) findViewById(R.id.indexesButton);
		final ImageButton buttonPm = (ImageButton) findViewById(R.id.predicatematrixButton);
		final ImageButton buttonTextSearchWn = (ImageButton) findViewById(R.id.textsearchWnButton);
		final ImageButton buttonTextSearchVn = (ImageButton) findViewById(R.id.textsearchVnButton);
		final ImageButton buttonTextSearchPb = (ImageButton) findViewById(R.id.textsearchPbButton);
		final ImageButton buttonTextSearchFn = (ImageButton) findViewById(R.id.textsearchFnButton);

		// click listeners
		buttonDb.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(StatusActivity.this, DownloadActivity.class);
				startActivityForResult(intent, StatusActivity.REQUEST_DOWNLOAD_CODE);
			}
		});

		buttonIndexes.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(StatusActivity.this, ManagementActivity.class);
				intent.putExtra(ManagementFragment.ARG, Status.DO_INDEXES);
				startActivityForResult(intent, StatusActivity.REQUEST_MANAGEMENT_CODE + Status.DO_INDEXES);
			}
		});

		buttonPm.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(StatusActivity.this, ManagementActivity.class);
				intent.putExtra(ManagementFragment.ARG, Status.DO_PM);
				startActivityForResult(intent, StatusActivity.REQUEST_MANAGEMENT_CODE + Status.DO_PM);
			}
		});

		buttonTextSearchWn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(StatusActivity.this, ManagementActivity.class);
				intent.putExtra(ManagementFragment.ARG, Status.DO_TS_WN);
				startActivityForResult(intent, StatusActivity.REQUEST_MANAGEMENT_CODE + Status.DO_TS_WN);
			}
		});

		buttonTextSearchVn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(StatusActivity.this, ManagementActivity.class);
				intent.putExtra(ManagementFragment.ARG, Status.DO_TS_WN);
				startActivityForResult(intent, StatusActivity.REQUEST_MANAGEMENT_CODE + Status.DO_TS_WN);
			}
		});

		buttonTextSearchPb.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(StatusActivity.this, ManagementActivity.class);
				intent.putExtra(ManagementFragment.ARG, Status.DO_TS_PB);
				startActivityForResult(intent, StatusActivity.REQUEST_MANAGEMENT_CODE + Status.DO_TS_PB);
			}
		});

		buttonTextSearchFn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final Intent intent = new Intent(StatusActivity.this, ManagementActivity.class);
				intent.putExtra(ManagementFragment.ARG, Status.DO_TS_FN);
				startActivityForResult(intent, StatusActivity.REQUEST_MANAGEMENT_CODE + Status.DO_TS_FN);
			}
		});

		final int status = Status.status(getBaseContext());
		if (status != 0)
		{
			db.setImageResource(R.drawable.ic_ok);
			buttonDb.setVisibility(View.GONE);

			final boolean existsIndexes = (status & Status.EXISTS_INDEXES) != 0;
			final boolean existsPm = (status & Status.EXISTS_PM) != 0;
			final boolean existsTsWn = (status & Status.EXISTS_TS_WN) != 0;
			final boolean existsTsVn = (status & Status.EXISTS_TS_VN) != 0;
			final boolean existsTsPb = (status & Status.EXISTS_TS_PB) != 0;
			final boolean existsTsFn = (status & Status.EXISTS_TS_FN) != 0;

			final ImageView imageIndexes = (ImageView) findViewById(R.id.status_indexes);
			final ImageView imagePm = (ImageView) findViewById(R.id.status_predicatematrix);
			final ImageView imageTextSearchWn = (ImageView) findViewById(R.id.status_textsearchWn);
			final ImageView imageTextSearchVn = (ImageView) findViewById(R.id.status_textsearchVn);
			final ImageView imageTextSearchPb = (ImageView) findViewById(R.id.status_textsearchPb);
			final ImageView imageTextSearchFn = (ImageView) findViewById(R.id.status_textsearchFn);
			imageIndexes.setImageResource(existsIndexes ? R.drawable.ic_ok : R.drawable.ic_fail);
			imagePm.setImageResource(existsPm ? R.drawable.ic_ok : R.drawable.ic_fail);
			imageTextSearchWn.setImageResource(existsTsWn ? R.drawable.ic_ok : R.drawable.ic_fail);
			imageTextSearchVn.setImageResource(existsTsVn ? R.drawable.ic_ok : R.drawable.ic_fail);
			imageTextSearchPb.setImageResource(existsTsPb ? R.drawable.ic_ok : R.drawable.ic_fail);
			imageTextSearchFn.setImageResource(existsTsFn ? R.drawable.ic_ok : R.drawable.ic_fail);

			buttonIndexes.setVisibility(existsIndexes ? View.GONE : View.VISIBLE);
			buttonPm.setVisibility(existsPm ? View.GONE : View.VISIBLE);
			buttonTextSearchWn.setVisibility(existsTsWn ? View.GONE : View.VISIBLE);
			buttonTextSearchVn.setVisibility(existsTsVn ? View.GONE : View.VISIBLE);
			buttonTextSearchPb.setVisibility(existsTsPb ? View.GONE : View.VISIBLE);
			buttonTextSearchFn.setVisibility(existsTsFn ? View.GONE : View.VISIBLE);
		}
		else
		{
			db.setImageResource(R.drawable.ic_fail);
			buttonDb.setVisibility(View.VISIBLE);
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
