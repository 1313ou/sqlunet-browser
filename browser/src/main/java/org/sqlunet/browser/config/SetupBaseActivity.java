package org.sqlunet.browser.config;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.sqlunet.browser.R;
import org.sqlunet.provider.ExecuteManager;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.settings.StorageUtils;

import java.io.File;

/**
 * Set up activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressLint("Registered")
public class SetupBaseActivity extends Activity implements BasicDownloader.Listener, ExecuteManager.Listener
{
	static private final String TAG = "SetupBaseActivity";

	// task
	protected AsyncTask<?, Long, Boolean> task;

	// progress dialog
	private ProgressDialog progressDialog;

	@SuppressWarnings("boxing")
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// show the Up button in the type bar.
		final ActionBar actionBar = getActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		// database
		final String databasePath = StorageSettings.getDatabasePath(getBaseContext());
		TextView database = (TextView) findViewById(R.id.database);
		database.setText(databasePath);

		// storage capacity
		TextView data_storage = (TextView) findViewById(R.id.data_storage);
		final String dataDir = StorageSettings.getDataDir(this);
		final float[] dataStats = StorageUtils.storageStats(dataDir);
		final float df = dataStats[StorageUtils.STORAGE_FREE];
		final float dc = dataStats[StorageUtils.STORAGE_CAPACITY];
		final float dp = dataStats[StorageUtils.STORAGE_OCCUPANCY];
		data_storage.setText(getString(R.string.format_storage_data, dataDir, StorageUtils.mbToString(df), StorageUtils.mbToString(dc), dp));
	}

	// D I A L O G

	/**
	 * Make dialog
	 *
	 * @param messageId progressMessage resource
	 * @param style     style
	 * @return dialog
	 */
	private ProgressDialog makeDialog(final int messageId, final int style)
	{
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(messageId);
		progressDialog.setMessage("");
		progressDialog.setIndeterminate(true);
		progressDialog.setMax(100);
		progressDialog.setProgressStyle(style);
		progressDialog.setCancelable(true);
		progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.action_abort), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(final DialogInterface dialog, final int which)
			{
				if (which == DialogInterface.BUTTON_NEGATIVE)
				{
					boolean result = SetupBaseActivity.this.task.cancel(true);
					Log.d(TAG, "Cancel task " + SetupBaseActivity.this.task + ' ' + result);
					dialog.dismiss();
				}
			}
		});
		progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.action_dismiss), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(final DialogInterface dialog, final int which)
			{
				if (which == DialogInterface.BUTTON_POSITIVE)
				{
					dialog.dismiss();
				}
			}
		});
		return progressDialog;
	}

	// D O W N L O A D L I S T E N E R

	@Override
	public void onDownloadStart()
	{
		this.progressDialog = makeDialog(R.string.status_downloading, ProgressDialog.STYLE_HORIZONTAL);
		this.progressDialog.show();
	}

	@Override
	public void onDownloadFinish(final int code, final boolean result)
	{
		this.progressDialog.dismiss();
		Log.d(TAG, "Download " + (result ? "succeeded" : "failed")); ////
		Toast.makeText(this, result ? R.string.title_download_complete : R.string.title_download_failed, Toast.LENGTH_SHORT).show();

		// delete file if failed
		if (!result)
		{
			String to = StorageSettings.getSqlDownloadTarget(this);
			//noinspection ResultOfMethodCallIgnored
			new File(to).delete();
		}
	}

	@Override
	public void onDownloadUpdate(final long total, final long downloaded)
	{
		this.progressDialog.setIndeterminate(total == -1);
		this.progressDialog.setProgress((int) (100 * (downloaded / total)));
		this.progressDialog.setMessage(downloaded / (1024 * 1024) + " MBytes");
	}

	// E X E C U T E L I S T E N E R

	@Override
	public void managerStart()
	{
		this.progressDialog = makeDialog(R.string.status_managing, ProgressDialog.STYLE_SPINNER);
		this.progressDialog.setIndeterminate(false);
		this.progressDialog.show();
	}

	@Override
	public void managerFinish(final boolean result)
	{
		this.progressDialog.dismiss();
		Log.d(TAG, "SQL update " + (result ? "succeeded" : "failed")); ////
		Toast.makeText(this, result ? R.string.title_sqlupdate_complete : R.string.title_sqlupdate_failed, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void managerUpdate(final int progress)
	{
		this.progressDialog.setIndeterminate(progress == -1);
		this.progressDialog.setProgress(progress);
		this.progressDialog.setMessage(progress + " statements");
	}
}
