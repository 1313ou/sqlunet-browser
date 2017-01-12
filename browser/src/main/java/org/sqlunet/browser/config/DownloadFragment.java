package org.sqlunet.browser.config;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.sqlunet.browser.R;

/**
 * Download activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DownloadFragment extends BaseDownloadFragment
{
	/**
	 * Log tag
	 */
	static private final String TAG = "DownloadFragment";

	/**
	 * Download manager
	 */
	private DownloadManager downloadManager;

	/**
	 * Done receiver
	 */
	private BroadcastReceiver receiver;

	/**
	 * Download id
	 */
	private long downloadId = -1;

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// activity
		final Context context = getActivity();

		// downloader
		this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

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
						// status
						final Status status = new Status();
						getStatus(status);

						// cleanup if fail
						if (!status.success)
						{
							if (DownloadFragment.this.destFile.exists())
							{
								//noinspection ResultOfMethodCallIgnored
								DownloadFragment.this.destFile.delete();
							}
						}

						// fire on done
						onDone(status.success);
					}
				}
			}
		};
	}

	@Override
	public void onStart()
	{
		super.onStart();

		// register receiver
		Log.d(TAG, "Register listener");
		getActivity().registerReceiver(this.receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}

	@Override
	public void onStop()
	{
		// unregister receiver
		Log.d(TAG, "Unregister listener");
		getActivity().unregisterReceiver(DownloadFragment.this.receiver);
		super.onStop();
	}

	/**
	 * Start download.
	 */
	@Override
	protected void start()
	{
		final Uri downloadUri = Uri.parse(this.downloadUrl);
		final Request request = new Request(downloadUri);
		if (this.destFile != null)
		{
			Uri destUri = Uri.fromFile(this.destFile);
			request.setDestinationUri(destUri);
		}
		request.setTitle(getActivity().getResources().getText(R.string.title_download_id));
		request.setDescription(downloadUri.getLastPathSegment());

		// request.setAllowedOverMetered(false);
		// request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
		// request.setAllowedOverRoaming(false);

		request.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
		this.downloadId = this.downloadManager.enqueue(request);
	}

	/**
	 * Download status
	 */
	@Override
	protected boolean getStatus(final Status result)
	{
		result.finished = true;
		result.success = false;
		result.cancel = true;

		// query
		final Query query = new Query();
		query.setFilterById(DownloadFragment.this.downloadId);

		// cursor
		final Cursor cursor = DownloadFragment.this.downloadManager.query(query);
		if (cursor.moveToFirst())
		{
			// status
			final int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
			result.localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
			switch (status)
			{
				case DownloadManager.STATUS_SUCCESSFUL:
					result.finished = true;
					result.success = true;
					result.cancel = false;
					result.message = makeStatusString(status2ResourceId(DownloadManager.STATUS_SUCCESSFUL), -1);
					break;

				case DownloadManager.STATUS_FAILED:
					result.finished = true;
					result.success = false;
					result.cancel = true;
					result.message = makeStatusString(status2ResourceId(status), getReasonResId(cursor));
					break;

				case DownloadManager.STATUS_PAUSED:
					result.finished = false;
					result.success = false;
					result.cancel = false;
					result.message = makeStatusString(status2ResourceId(DownloadManager.STATUS_PAUSED), getReasonResId(cursor));
					break;

				case DownloadManager.STATUS_PENDING:
					result.finished = false;
					result.success = false;
					result.cancel = false;
					result.message = makeStatusString(status2ResourceId(DownloadManager.STATUS_PENDING), -1);
					break;

				default:
					break;
			}

			// size info
			final int downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
			final int total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
			result.progress100 = total == 0 ? 0 : (int) (downloaded * 100L / total);
		}
		cursor.close();

		return result.finished;
	}

	/**
	 * Cancel download
	 */
	@Override
	protected void cancel()
	{
		if (this.downloadId != -1)
		{
			this.downloadManager.remove(DownloadFragment.this.downloadId);
		}
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
}
