package org.sqlunet.browser.config;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.sqlunet.browser.R;
import org.sqlunet.settings.StorageSettings;

import java.io.File;

/**
 * Download activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DownloadActivity extends Activity implements View.OnClickListener
{
	/**
	 * Log tag
	 */
	static private final String TAG = "Download";
	/**
	 * Result extra
	 */
	static private final String RESULT_DOWNLOAD_DATA_AVAILABLE = "download_data_available";
	/**
	 * Download id
	 */
	private long downloadId = -1;

	/**
	 * Download uri
	 */
	private String downloadUrl;

	/**
	 * Destination uri
	 */
	private File destDir;

	/**
	 * Download manager
	 */
	private DownloadManager downloadManager;

	/**
	 * Done receiver
	 */
	private BroadcastReceiver receiver;

	/**
	 * Download button
	 */
	private ImageButton downloadButton;

	/**
	 * Progress bar
	 */
	private ProgressBar progressBar;

	/**
	 * Progress status
	 */
	private TextView progressStatus;

	/**
	 * Source
	 */
	private TextView src;

	/**
	 * Target
	 */
	private TextView target;

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_download);

		// download data
		this.downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
		this.destDir = new File(StorageSettings.getDataDir(this));
		this.downloadUrl = StorageSettings.getDbDownloadSource(this);
		if (this.downloadUrl == null || this.downloadUrl.isEmpty())
		{
			Toast.makeText(this, R.string.status_error_null_download_url, Toast.LENGTH_SHORT).show();
			finish();
		}

		// components
		this.downloadButton = (ImageButton) findViewById(R.id.downloadButton);
		this.downloadButton.setOnClickListener(this);
		this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
		this.progressStatus = (TextView) findViewById(R.id.progressStatus);
		this.src = (TextView) findViewById(R.id.src);
		this.target = (TextView) findViewById(R.id.target);

		// receiver
		this.receiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(final Context context, final Intent intent)
			{
				final String action = intent.getAction();
				if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action))
				{
					final long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
					if (id == DownloadActivity.this.downloadId)
					{
						final boolean success = retrieve();

						// progress
						DownloadActivity.this.progressBar.setVisibility(View.GONE);
						DownloadActivity.this.progressStatus.setText(success ? R.string.status_download_successful : R.string.status_download_fail);

						// toast
						Toast.makeText(DownloadActivity.this, success ? R.string.status_data_ok : R.string.status_data_fail, Toast.LENGTH_SHORT).show();

						// return result
						final Intent resultIntent = new Intent();
						resultIntent.putExtra(DownloadActivity.RESULT_DOWNLOAD_DATA_AVAILABLE, true);
						DownloadActivity.this.setResult(Activity.RESULT_OK, resultIntent);

						finish();
					}
				}
			}
		};
	}

	@Override
	protected void onPostCreate(final Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);

		this.src.setText(this.downloadUrl);
		this.target.setText(this.destDir != null ? this.destDir.getAbsolutePath() : "");
		// TODO
		// this.src.setSingleLine(true);
		// this.src.setEllipsize(TextUtils.TruncateAt.MARQUEE);
		// this.target.setSingleLine(true);
		// this.target.setEllipsize(TextUtils.TruncateAt.MARQUEE);
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		// register receiver
		registerReceiver(this.receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

		// finish
		if (finished())
		{
			// toast
			Toast.makeText(DownloadActivity.this, R.string.status_data_ok, Toast.LENGTH_SHORT).show();

			// return result
			final Intent resultIntent = new Intent();
			resultIntent.putExtra(DownloadActivity.RESULT_DOWNLOAD_DATA_AVAILABLE, true);
			setResult(Activity.RESULT_OK, resultIntent);

			finish();
		}
	}

	@Override
	protected void onStop()
	{
		// register receiver
		unregisterReceiver(DownloadActivity.this.receiver);
		super.onStop();
	}

	@Override
	public void onClick(final View view)
	{
		final int id = view.getId();
		if (id == R.id.downloadButton)
		{
			this.downloadButton.setVisibility(View.INVISIBLE);
			this.progressBar.setVisibility(View.VISIBLE);
			this.progressStatus.setVisibility(View.VISIBLE);

			// start download
			start();
		}
	}

	/**
	 * Start download. Typically call start(final String downloadUrl, final int titleRes)
	 */
	private void start()
	{
		start(R.string.about_sqlunet_label);
	}

	/**
	 * Start download. Assume download url has been set by derived class
	 */
	private void start(final int titleRes)
	{
		final Uri downloadUri = Uri.parse(this.downloadUrl);
		try
		{
			final Request request = new Request(downloadUri);
			request.setTitle(getResources().getText(titleRes));
			request.setDescription(downloadUri.getLastPathSegment());
			if (this.destDir != null)
			{
				final File destFile = new File(this.destDir, downloadUri.getLastPathSegment());
				Uri destUri = Uri.fromFile(destFile);
				request.setDestinationUri(destUri);
			}

			// @formatter: off
			// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			// {
			//      request.setAllowedOverMetered(false);
			// }
			// else
			// {
			//      request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
			// }
			// request.setAllowedOverRoaming(false);
			// @formatter: on

			request.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
			this.downloadId = this.downloadManager.enqueue(request);

			// start progress
			startProgress();
		}
		catch (Exception e)
		{
			Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Whether download has finished
	 *
	 * @return true if download has finished
	 */
	private boolean finished()
	{
		// query
		final Query query = new Query();
		query.setFilterById(this.downloadId);

		// cursor
		Cursor cursor = null;
		try
		{
			cursor = DownloadActivity.this.downloadManager.query(query);
			if (cursor.moveToFirst())
			{
				final int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
				final int status = cursor.getInt(columnIndex);
				switch (status)
				{
					case DownloadManager.STATUS_SUCCESSFUL:
					case DownloadManager.STATUS_FAILED:
						return true;
					default:
						break;
				}
			}
			return false;
		}
		finally
		{
			if (cursor != null)
			{
				cursor.close();
			}
		}
	}

	/**
	 * Retrieve data
	 *
	 * @return status
	 */
	private boolean retrieve()
	{
		// query
		final Query query = new Query();
		query.setFilterById(this.downloadId);

		// cursor
		final Cursor cursor = DownloadActivity.this.downloadManager.query(query);

		//noinspection TryFinallyCanBeTryWithResources
		try
		{
			if (cursor.moveToFirst())
			{
				final int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
				if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columnIndex))
				{
					// local uri
					final String uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
					final Uri uri = Uri.parse(uriString);
					Log.d(TAG, "Local Uri " + uri.toString());
					return true;
				}
			}
			return false;
		}
		finally
		{
			cursor.close();
		}
	}

	/**
	 * Start progress update thread
	 */
	private void startProgress()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				boolean downloading = true;
				while (downloading)
				{
					// query
					final DownloadManager.Query query = new Query();
					query.setFilterById(DownloadActivity.this.downloadId);

					// cursor
					final Cursor cursor = DownloadActivity.this.downloadManager.query(query);
					if (cursor.moveToFirst())
					{
						// size info
						final int downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
						final int total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
						final int progress = (int) (downloaded * 100L / total);

						// exit loop condition
						final int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
						switch (status)
						{
							case DownloadManager.STATUS_FAILED:
							case DownloadManager.STATUS_SUCCESSFUL:
								downloading = false;
								break;
							default:
								break;
						}

						// update UI
						final int resStatus = DownloadActivity.status2ResourceId(status);
						Log.d(DownloadActivity.TAG, getResources().getString(resStatus) + " at " + progress);
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								DownloadActivity.this.progressBar.setProgress(progress);
								DownloadActivity.this.progressStatus.setText(resStatus);
							}
						});
					}
					cursor.close();

					// sleep
					try
					{
						Thread.sleep(2000);
					}
					catch (final InterruptedException e)
					{
						//
					}
				}
			}
		}).start();
	}

	/**
	 * Get status message as per status returned by cursor
	 *
	 * @param status status
	 * @return string resource id
	 */
	static private int status2ResourceId(final int status)
	{
		switch (status)
		{
			case DownloadManager.STATUS_FAILED:
				return R.string.status_download_fail;
			case DownloadManager.STATUS_PAUSED:
				return R.string.status_download_paused;
			case DownloadManager.STATUS_PENDING:
				return R.string.status_download_pending;
			case DownloadManager.STATUS_RUNNING:
				return R.string.status_download_running;
			case DownloadManager.STATUS_SUCCESSFUL:
				return R.string.status_download_successful;
			default:
				return -1;
		}
	}

	/**
	 * Show downloads
	 */
	@SuppressWarnings("unused")
	public void showDownload()
	{
		final Intent intent = new Intent();
		intent.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
		startActivity(intent);
	}
}
