package org.sqlunet.download;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.sqlunet.download.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Download activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DownloadFragment extends BaseDownloadFragment
{
	static private final String TAG = "DownloadF";

	/**
	 * Instance state label
	 */
	static private final String SHOW_BTN_STATE = "show_btn_state";

	/**
	 * Reason
	 */
	enum Reason
	{
		PAUSED_WAITING_TO_RETRY(1, R.string.status_download_paused_waiting_to_retry), PAUSED_WAITING_FOR_NETWORK(2, R.string.status_download_paused_waiting_for_network), PAUSED_QUEUED_FOR_WIFI(3, R.string.status_download_paused_queued_for_wifi), PAUSED_UNKNOWN(4, R.string.status_download_paused_unknown),

		ERROR_UNKNOWN(1000, R.string.status_download_error_unknown), ERROR_FILE_ERROR(1001, R.string.status_download_error_file_error), ERROR_UNHANDLED_HTTP_CODE(1002, R.string.status_download_error_unhandled_http_code), ERROR_HTTP_DATA_ERROR(1004, R.string.status_download_error_http_data_error), ERROR_TOO_MANY_REDIRECTS(1005, R.string.status_download_error_too_many_redirects), ERROR_INSUFFICIENT_SPACE(1006, R.string.status_download_error_insufficient_space), ERROR_DEVICE_NOT_FOUND(1007, R.string.status_download_error_device_not_found), ERROR_CANNOT_RESUME(1008, R.string.status_download_error_cannot_resume), ERROR_FILE_ALREADY_EXISTS(1009, R.string.status_download_error_file_already_exists), ERROR_BLOCKED(1010, R.string.status_download_error_blocked),

		EXCEPTION(10000, R.string.status_download_exception);

		final int code;

		final private int res;

		Reason(int code, int res)
		{
			this.code = code;
			this.res = res;
		}

		static int toRes(@Nullable final Reason reason)
		{
			return reason == null ? R.string.status_error_reason_unknown : reason.res;
		}

		static Reason valueOf(int code)
		{
			for (Reason reason : values())
			{
				if (reason.code == code)
				{
					return reason;
				}
			}
			return null;
		}
	}

	static private long pack(int status, int reason)
	{
		return ((long) status) | (((long) reason) << 32);
	}

	static private int unpackStatus(long status)
	{
		return (int) (status & 0x00000000FFFFFFFFL);
	}

	static private int unpackReason(long status)
	{
		return (int) ((status & 0xFFFFFFFF00000000L) >> 32);
	}

	/**
	 * Download manager
	 */
	@Nullable
	private DownloadManager downloadManager;

	/**
	 * Done receiver
	 */
	@Nullable
	private BroadcastReceiver receiver;

	/**
	 * Download id
	 */
	private long downloadId = -1;

	/**
	 * Download extended status
	 */
	private long xStatus = 0;

	/**
	 * Exception flag
	 */
	private boolean exceptionHasOccurred = false;

	/**
	 * Exception exception message
	 */
	@Nullable
	private String exceptionMessage;

	/**
	 * Show downloads button
	 */
	private Button showButton;

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// activity
		final Context context = getActivity();
		assert context != null;

		// downloader
		this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

		// receiver
		this.receiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(final Context context0, @NonNull final Intent intent)
			{
				final String action = intent.getAction();
				if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action))
				{
					Log.d(TAG, "Download complete");
					final long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
					if (id == DownloadFragment.this.downloadId)
					{
						// status
						long status = getXStatus(null);
						if (status != 0) // not cancelled
						{
							DownloadFragment.this.xStatus = status;
						}

						// cleanup if fail
						boolean success = Status.STATUS_SUCCESSFUL.test(status);
						if (!success)
						{
							assert DownloadFragment.this.destFile != null;
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
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, @Nullable final Bundle savedInstanceState)
	{
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		//noinspection ConstantConditions
		this.showButton = view.findViewById(R.id.showButton);
		this.showButton.setOnClickListener(v -> showDownloads());
		if (savedInstanceState != null)
		{
			//noinspection WrongConstant
			this.showButton.setVisibility(savedInstanceState.getInt(SHOW_BTN_STATE, View.INVISIBLE));
		}
		return view;
	}

	@Override
	public void onSaveInstanceState(@NonNull final Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putInt(SHOW_BTN_STATE, this.showButton.getVisibility());
	}

	@Override
	public void onClick(@NonNull final View view)
	{
		final int id = view.getId();
		if (id == R.id.downloadButton)
		{
			this.showButton.setVisibility(View.VISIBLE);
		}
		super.onClick(view);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		// register receiver
		Log.d(TAG, "Register listener");
		final Activity activity = getActivity();
		assert activity != null;
		activity.registerReceiver(this.receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}

	@Override
	public void onStop()
	{
		// unregisterDb receiver
		Log.d(TAG, "Unregister listener");
		final Activity activity = getActivity();
		assert activity != null;
		activity.unregisterReceiver(DownloadFragment.this.receiver);
		super.onStop();
	}

	/**
	 * Start download.
	 */
	@Override
	protected void start()
	{
		this.exceptionHasOccurred = false;
		this.exceptionMessage = null;

		try
		{
			final Uri downloadUri = Uri.parse(this.downloadUrl);
			final Request request = new Request(downloadUri);
			if (this.destFile != null)
			{
				Uri destUri = Uri.fromFile(this.destFile);
				request.setDestinationUri(destUri);
			}
			request.setTitle(getString(R.string.title_download));
			request.setDescription(downloadUri.getLastPathSegment());
			request.setNotificationVisibility(Request.VISIBILITY_VISIBLE);

			// request.setAllowedOverMetered(false);
			// request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
			// request.setAllowedOverRoaming(false);

			assert this.downloadManager != null;
			this.downloadId = this.downloadManager.enqueue(request);
		}
		catch (SecurityException e)
		{
			flagException(e);
			throw e;
		}
	}

	private void flagException(@NonNull final Exception e)
	{
		this.xStatus = pack(Status.STATUS_FAILED.mask, Reason.EXCEPTION.code);
		this.exceptionHasOccurred = true;
		this.exceptionMessage = e.getMessage();
	}

	/**
	 * Download status
	 */
	synchronized private long getXStatus(@Nullable final Progress progress)
	{
		// query
		final Query query = new Query();
		query.setFilterById(DownloadFragment.this.downloadId);

		// cursor
		int statusCode = 0;
		int reasonCode = 0;
		assert DownloadFragment.this.downloadManager != null;
		final Cursor cursor = DownloadFragment.this.downloadManager.query(query);
		if (cursor.moveToFirst())
		{
			// status
			final int dmStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
			final Status status = dmStatus2Status(dmStatus);
			statusCode = status == null ? 0 : status.mask;
			switch (dmStatus)
			{
				case DownloadManager.STATUS_PENDING:
				case DownloadManager.STATUS_SUCCESSFUL:
					break;

				case DownloadManager.STATUS_FAILED:
				case DownloadManager.STATUS_PAUSED:
					final int dmReason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
					final Reason reason = dmReason2Reason(dmReason);
					reasonCode = reason == null ? 0 : reason.code;
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

		// done with cursor
		cursor.close();
		Log.d(TAG, "READ STATUS status=" + statusCode + " reason=" + reasonCode);
		return pack(statusCode, reasonCode);
	}

	/**
	 * Download status
	 */
	@Override
	protected int getStatus(final Progress progress)
	{
		if (!this.exceptionHasOccurred)
		{
			this.xStatus = getXStatus(progress);
		}
		return unpackStatus(this.xStatus);
	}

	/**
	 * Download reason
	 */
	@Nullable
	@Override
	protected String getReason()
	{
		int reasonCode = unpackReason(this.xStatus);
		if (reasonCode == 0)
		{
			return null;
		}

		final Reason reason = Reason.valueOf(reasonCode);
		final int reasonResId = Reason.toRes(reason);

		String result = makeString(reasonResId);
		if (reason == Reason.EXCEPTION)
		{
			result += '\n' + this.exceptionMessage;
		}
		return result;
	}

	/**
	 * Cancel download
	 */
	@Override
	protected void cancel()
	{
		if (this.downloadId != -1)
		{
			Log.d(TAG, "Cancel DM " + this.downloadId);
			assert this.downloadManager != null;
			this.downloadManager.remove(this.downloadId);
		}
	}

	/**
	 * Cleanup download
	 */
	@Override
	protected void cleanup()
	{
		if (this.downloadId != -1)
		{
			Log.d(TAG, "Cleanup DM " + this.downloadId);
			assert this.downloadManager != null;
			this.downloadManager.remove(this.downloadId);
		}
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

	/**
	 * Show downloads
	 */
	private void showDownloads()
	{
		final Intent intent = new Intent();
		intent.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
		startActivity(intent);
	}

	@SuppressWarnings("WeakerAccess")
	@Override
	protected void onDone(boolean success)
	{
		if (DownloadFragment.this.showButton != null)
		{
			if (success)
			{
				DownloadFragment.this.showButton.setVisibility(View.GONE);
			}
		}
		super.onDone(success);
	}
}
