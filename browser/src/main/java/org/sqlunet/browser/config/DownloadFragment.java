package org.sqlunet.browser.config;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.sqlunet.browser.R;
import org.sqlunet.settings.Storage;
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

	/**
	 * Result status
	 */
	private TextView status;

	/**
	 * Cancel downloads button
	 */
	private Button cancelButton;

	/**
	 * Show downloads button
	 */
	private Button showButton;

	/**
	 * Result message
	 */
	private String result;

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
			warn(getActivity().getString(R.string.status_error_null_download_url));

			// fire done
			fireDone(false);
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
						if (!success)
						{
							final File targetFile = new File(DownloadFragment.this.destDir, Storage.DBFILE);
							if (targetFile.exists())
							{
								//noinspection ResultOfMethodCallIgnored
								targetFile.delete();
							}
						}

						// progress
						if (DownloadFragment.this.progressBar != null)
						{
							DownloadFragment.this.progressBar.setVisibility(View.INVISIBLE);
						}
						if (DownloadFragment.this.progressStatus != null)
						{
							DownloadFragment.this.progressStatus.setVisibility(View.INVISIBLE);
						}
						if (DownloadFragment.this.status != null)
						{
							DownloadFragment.this.status.setText(success ? getActivity().getString(R.string.status_download_successful) : DownloadFragment.this.result);
							DownloadFragment.this.status.setVisibility(View.VISIBLE);
						}

						// buttons
						if (DownloadFragment.this.downloadButton != null)
						{
							DownloadFragment.this.downloadButton.setBackgroundResource(success ? R.drawable.bg_button_ok : R.drawable.bg_button_err);
							DownloadFragment.this.downloadButton.setEnabled(false);
							DownloadFragment.this.downloadButton.setVisibility(View.VISIBLE);
						}

						if (DownloadFragment.this.cancelButton != null)
						{
							DownloadFragment.this.cancelButton.setVisibility(View.GONE);
						}
						if (DownloadFragment.this.showButton != null)
						{
							if (success)
							{
								DownloadFragment.this.showButton.setVisibility(View.GONE);
							}
						}

						// fire done
						fireDone(success);
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
		this.status = (TextView) view.findViewById(R.id.status);

		// buttons
		this.cancelButton = (Button) view.findViewById(R.id.cancelButton);
		this.cancelButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (DownloadFragment.this.downloadId != 0)
				{
					cancelDownload();
				}
			}
		});
		this.showButton = (Button) view.findViewById(R.id.showButton);
		this.showButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showDownloads();
			}
		});

		final TextView srcView = (TextView) view.findViewById(R.id.src);
		srcView.setText(this.downloadUrl);
		srcView.setSingleLine(true);
		srcView.setEllipsize(TextUtils.TruncateAt.MARQUEE);

		final TextView targetView = (TextView) view.findViewById(R.id.target);
		targetView.setText(this.destDir != null ? this.destDir.getAbsolutePath() : "");
		targetView.setSingleLine(true);
		targetView.setEllipsize(TextUtils.TruncateAt.MARQUEE);

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
		Log.d(TAG, "Register listener");
		getActivity().registerReceiver(this.receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

		// finish
		if (finished())
		{
			// fire done
			fireDone(true);
		}
	}

	@Override
	public void onStop()
	{
		// unregister receiver
		Log.d(TAG, "Unregister listener");
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
			this.cancelButton.setVisibility(View.VISIBLE);
			this.showButton.setVisibility(View.VISIBLE);

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

			// request.setAllowedOverMetered(false);
			// request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
			// request.setAllowedOverRoaming(false);

			request.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
			this.downloadId = this.downloadManager.enqueue(request);

			// start progress
			startProgress();
		}
		catch (Exception e)
		{
			warn(e.getLocalizedMessage());
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
	 * @return _status
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
				if (downloadStatus(cursor))
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
						final int progress = total == 0 ? 0 : (int) (downloaded * 100L / total);

						// exit loop condition
						final int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
						switch (status)
						{
							case DownloadManager.STATUS_FAILED:
								int reasonResId = getReasonResId(cursor);
								warn(makeStatusString(status2ResourceId(status), reasonResId));
								cancelDownload();
								//$FALL-THROUGH
								//noinspection fallthrough
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

	private boolean downloadStatus(final Cursor cursor)
	{
		// column for download  status
		int columnStatus = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
		int status = cursor.getInt(columnStatus);

		int reasonResId = -1;
		switch (status)
		{
			case DownloadManager.STATUS_SUCCESSFUL:
				return true;

			case DownloadManager.STATUS_FAILED:
			case DownloadManager.STATUS_PAUSED:
				reasonResId = getReasonResId(cursor);
				// $FALL-THROUGH
				//noinspection fallthrough
			case DownloadManager.STATUS_PENDING:
			case DownloadManager.STATUS_RUNNING:
				break;
		}

		// message
		final String message = makeStatusString(status2ResourceId(status), reasonResId);

		// warn
		warn(message);
		return false;
	}

	/**
	 * Reason for fail or pause
	 *
	 * @param cursor download manager cursor
	 * @return resId
	 */
	private int getReasonResId(final Cursor cursor)
	{
		// column for reason code if the download failed or paused
		int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
		int reason = cursor.getInt(columnReason);
		return reason2ResourceId(reason);
	}

	/**
	 * Get _status message as per _status returned by cursor
	 *
	 * @param status _status
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
	 * Get _status message as per _status returned by cursor
	 *
	 * @param reason _status
	 * @return string resource id
	 */
	static private int reason2ResourceId(final int reason)
	{
		switch (reason)
		{
			case DownloadManager.ERROR_CANNOT_RESUME:
				return R.string.status_download_error_cannot_resume;

			case DownloadManager.ERROR_DEVICE_NOT_FOUND:
				return R.string.status_download_error_device_not_found;

			case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
				return R.string.status_download_error_file_already_exists;

			case DownloadManager.ERROR_FILE_ERROR:
				return R.string.status_download_error_file_error;

			case DownloadManager.ERROR_HTTP_DATA_ERROR:
				return R.string.status_download_error_http_data_error;

			case DownloadManager.ERROR_INSUFFICIENT_SPACE:
				return R.string.status_download_error_insufficient_space;

			case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
				return R.string.status_download_error_too_many_redirects;

			case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
				return R.string.status_download_error_unhandled_http_code;

			case DownloadManager.ERROR_UNKNOWN:
				return R.string.status_download_error_unknown;

			case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
				return R.string.status_download_paused_queued_for_wifi;

			case DownloadManager.PAUSED_UNKNOWN:
				return R.string.status_download_paused_unknown;

			case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
				return R.string.status_download_paused_waiting_for_network;

			case DownloadManager.PAUSED_WAITING_TO_RETRY:
				return R.string.status_download_paused_waiting_to_retry;

			default:
				return -1;
		}
	}

	/**
	 * Make status string
	 *
	 * @param statusResId status resource id
	 * @param reasonResId reason res id
	 * @return string
	 */
	private String makeStatusString(int statusResId, int reasonResId)
	{
		final Resources res = getActivity().getResources();
		String message = res.getString(statusResId);
		if (reasonResId != -1)
		{
			message += '\n' + res.getString(reasonResId);
		}
		return message;
	}

	private void warn(final String message)
	{
		this.result = message;
		final Activity activity = getActivity();
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
			}
		});
	}

	/**
	 * Cancel download
	 */
	private void cancelDownload()
	{
		this.downloadManager.remove(DownloadFragment.this.downloadId);
	}

	/**
	 * Show downloads
	 */
	private void showDownloads()
	{
		final Intent intent = new Intent();
		intent.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
		startActivity(intent);
	}

	private void fireDone(final boolean status)
	{
		if (this.listener == null)
		{
			return;
		}

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				DownloadFragment.this.listener.onDone(status);
			}
		}, 1000);
	}
}
