package org.sqlunet.browser.config;

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
					Log.d(TAG, "Download complete");
					final long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
					if (id == DownloadFragment.this.downloadId)
					{
						// status
						long status = getStatus(null);
						if(status != 0) // not cancelled
							DownloadFragment.this.status = status;

						// cleanup if fail
						boolean success = Status.STATUS_SUCCESSFUL.test(status);
						if (!success)
						{
							if (DownloadFragment.this.destFile.exists())
							{
								//noinspection ResultOfMethodCallIgnored
								DownloadFragment.this.destFile.delete();
							}
						}

						// fire on done
						onDone(success);
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
	protected long getStatus(final Progress progress)
	{
		int status = 0;
		int reason = 0;

		// query
		final Query query = new Query();
		query.setFilterById(DownloadFragment.this.downloadId);

		// cursor
		final Cursor cursor = DownloadFragment.this.downloadManager.query(query);
		if (cursor.moveToFirst())
		{
			// status
			final int dmStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
			status = dmStatus2Status(dmStatus).mask;
			switch (dmStatus)
			{
				case DownloadManager.STATUS_PENDING:
				case DownloadManager.STATUS_SUCCESSFUL:
					break;

				case DownloadManager.STATUS_FAILED:
				case DownloadManager.STATUS_PAUSED:
					final int dmReason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
					reason = dmReason2Reason(dmReason).code;
					break;

				default:
					break;
			}

			// size info
			if (progress != null)
			{
				progress.total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
				progress.downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
			}
		}
		cursor.close();
		Log.d(TAG, "READ STATUS status=" + status + " reason=" + reason);
		return pack(status, reason);
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
	private Reason getReason(final Cursor cursor)
	{
		// column for reason code if the download failed or paused
		int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
		int reason = cursor.getInt(columnReason);
		return dmReason2Reason(reason);
	}

	/**
	 * Get _status progressMessage as per _status returned by cursor
	 *
	 * @param status _status
	 * @return string resource id
	 */
	static private Status dmStatus2Status(final int status)
	{
		switch (status)
		{
			case DownloadManager.STATUS_FAILED:
				return Status.STATUS_FAILED;
			case DownloadManager.STATUS_PAUSED:
				return Status.STATUS_PAUSED;
			case DownloadManager.STATUS_PENDING:
				return Status.STATUS_PENDING;
			case DownloadManager.STATUS_RUNNING:
				return Status.STATUS_RUNNING;
			case DownloadManager.STATUS_SUCCESSFUL:
				return Status.STATUS_SUCCESSFUL;
			default:
				return null;
		}
	}

	/**
	 * Get _status reason as per _status returned by cursor
	 *
	 * @param reason _Download Manager reason
	 * @return Status reason
	 */
	static private Reason dmReason2Reason(final int reason)
	{
		switch (reason)
		{
			case DownloadManager.ERROR_CANNOT_RESUME:
				return Reason.ERROR_CANNOT_RESUME;

			case DownloadManager.ERROR_DEVICE_NOT_FOUND:
				return Reason.ERROR_DEVICE_NOT_FOUND;

			case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
				return Reason.ERROR_FILE_ALREADY_EXISTS;

			case DownloadManager.ERROR_FILE_ERROR:
				return Reason.ERROR_FILE_ERROR;

			case DownloadManager.ERROR_HTTP_DATA_ERROR:
				return Reason.ERROR_HTTP_DATA_ERROR;

			case DownloadManager.ERROR_INSUFFICIENT_SPACE:
				return Reason.ERROR_INSUFFICIENT_SPACE;

			case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
				return Reason.ERROR_TOO_MANY_REDIRECTS;

			case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
				return Reason.ERROR_UNHANDLED_HTTP_CODE;

			case DownloadManager.ERROR_UNKNOWN:
				return Reason.ERROR_UNKNOWN;

			case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
				return Reason.PAUSED_QUEUED_FOR_WIFI;

			case DownloadManager.PAUSED_UNKNOWN:
				return Reason.PAUSED_UNKNOWN;

			case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
				return Reason.PAUSED_WAITING_FOR_NETWORK;

			case DownloadManager.PAUSED_WAITING_TO_RETRY:
				return Reason.PAUSED_WAITING_TO_RETRY;

			default:
				return null;
		}
	}
}
