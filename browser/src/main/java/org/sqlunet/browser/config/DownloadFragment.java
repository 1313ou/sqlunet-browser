package org.sqlunet.browser.config;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class DownloadFragment extends Fragment implements View.OnClickListener
{
	/**
	 * Log tag
	 */
	static private final String TAG = "DownloadFragment";


	/**
	 * Download listener (typically implemented by activity)
	 */
	interface DownloadListener
	{
		void onDone(boolean result);
	}

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
	 * Done listener
	 */
	private DownloadListener listener;

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


	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// activity
		final Context context = getActivity();

		// download data
		this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		this.destDir = new File(StorageSettings.getDataDir(context));
		this.downloadUrl = StorageSettings.getDbDownloadSource(context);
		if (this.downloadUrl == null || this.downloadUrl.isEmpty())
		{
			Toast.makeText(context, R.string.status_error_null_download_url, Toast.LENGTH_SHORT).show();

			// fire done
			if (this.listener != null)
			{
				this.listener.onDone(false);
			}
		}

		// receiver
		this.receiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(final Context context0, final Intent intent)
			{
				final String action = intent.getAction();
				if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action))
				{
					final long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
					if (id == DownloadFragment.this.downloadId)
					{
						final boolean success = retrieve();

						// progress
						if (DownloadFragment.this.progressBar != null)
						{
							DownloadFragment.this.progressBar.setVisibility(View.GONE);
						}
						if (DownloadFragment.this.progressStatus != null)
						{
							DownloadFragment.this.progressStatus.setText(success ? R.string.status_download_successful : R.string.status_download_fail);
						}

						// toast
						Toast.makeText(context0, success ? R.string.status_data_ok : R.string.status_data_fail, Toast.LENGTH_SHORT).show();

						// fire done
						if (DownloadFragment.this.listener != null)
						{
							DownloadFragment.this.listener.onDone(success);
						}
					}
				}
			}
		};
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = inflater.inflate(R.layout.fragment_download, container, false);

		// components
		this.downloadButton = (ImageButton) view.findViewById(R.id.downloadButton);
		this.downloadButton.setOnClickListener(this);
		this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		this.progressStatus = (TextView) view.findViewById(R.id.progressStatus);
		final TextView srcView = (TextView) view.findViewById(R.id.src);
		srcView.setText(this.downloadUrl);
		final TextView targetView = (TextView) view.findViewById(R.id.target);
		targetView.setText(this.destDir != null ? this.destDir.getAbsolutePath() : "");

		// TODO
		// srcView.setSingleLine(true);
		// srcView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
		// targetView.setSingleLine(true);
		// targetView.setEllipsize(TextUtils.TruncateAt.MARQUEE);

		return view;
	}

	/**
	 * Set listener
	 *
	 * @param listener listener
	 */
	public void setListener(final DownloadListener listener)
	{
		this.listener = listener;
	}

	@Override
	public void onStart()
	{
		super.onStart();

		// register receiver
		getActivity().registerReceiver(this.receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

		// finish
		if (finished())
		{
			// toast
			Toast.makeText(getActivity(), R.string.status_data_ok, Toast.LENGTH_SHORT).show();

			// fire done
			if (DownloadFragment.this.listener != null)
			{
				DownloadFragment.this.listener.onDone(true);
			}
		}
	}

	@Override
	public void onStop()
	{
		// register receiver
		getActivity().unregisterReceiver(DownloadFragment.this.receiver);
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
			request.setTitle(getActivity().getResources().getText(titleRes));
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
			Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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
			cursor = DownloadFragment.this.downloadManager.query(query);
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
		final Cursor cursor = DownloadFragment.this.downloadManager.query(query);

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
					final Query query = new Query();
					query.setFilterById(DownloadFragment.this.downloadId);

					// cursor
					final Cursor cursor = DownloadFragment.this.downloadManager.query(query);
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
						final int resStatus = DownloadFragment.status2ResourceId(status);
						if (isAdded())
						{
							Log.d(DownloadFragment.TAG, getResources().getString(resStatus) + " at " + progress);
							getActivity().runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									DownloadFragment.this.progressBar.setProgress(progress);
									DownloadFragment.this.progressStatus.setText(resStatus);
								}
							});
						}
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
