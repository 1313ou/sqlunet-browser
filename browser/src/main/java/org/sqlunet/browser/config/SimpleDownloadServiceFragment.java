package org.sqlunet.browser.config;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.sqlunet.browser.R;

/**
 * Download Service fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SimpleDownloadServiceFragment extends BaseDownloadFragment
{
	/**
	 * Log tag
	 */
	static private final String TAG = "SimpleDownloadF";

	/**
	 * Notification id key
	 */
	static public final String NOTIFICATION_ID = "notification_id";

	/**
	 * Id for the current notification
	 */
	static private int notificationId = 0;

	/**
	 * Download id
	 */
	static private int downloadId = -1;

	/**
	 * Result
	 */
	private Boolean success;

	/**
	 * Exception
	 */
	private String exception;

	/**
	 * Downloaded progress when status was read
	 */
	private long progressDownloaded = 0;

	/**
	 * Total progress when status was read
	 */
	private long progressTotal = 0;

	/**
	 * Downloading flag (prevents re-entrance)
	 */
	static private boolean downloading = false;

	/**
	 * Broadcast receiver for start finish events
	 */
	private BroadcastReceiver startFinishBroadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(final Context context, final Intent intent)
		{
			//Log.d(TAG, "RECEIVE");

			switch (intent.getStringExtra(SimpleDownloaderService.EVENT))
			{
				case SimpleDownloaderService.EVENT_START:
					Log.d(TAG, "Start");
					SimpleDownloadServiceFragment.downloading = true;
					fireNotification(++SimpleDownloadServiceFragment.notificationId, false, false);
					break;

				case SimpleDownloaderService.EVENT_FINISH:
					int id = intent.getIntExtra(SimpleDownloaderService.EVENT_FINISH_ID, 0);
					if (id == SimpleDownloadServiceFragment.downloadId)
					{
						SimpleDownloadServiceFragment.downloading = false;
						success = intent.getBooleanExtra(SimpleDownloaderService.EVENT_FINISH_RESULT, false);
						exception = intent.getStringExtra(SimpleDownloaderService.EVENT_FINISH_EXCEPTION);
						Log.d(TAG, "Finish " + success);

						// notification
						fireNotification(SimpleDownloadServiceFragment.notificationId, true, success);

						// fireNotification on done
						onDone(success);
					}
					break;
			}
		}
	};

	/**
	 * Broadcast receiver for update events
	 */
	private BroadcastReceiver updateBroadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(final Context context, final Intent intent)
		{
			//Log.d(TAG, "RECEIVE");

			switch (intent.getStringExtra(SimpleDownloaderService.EVENT))
			{
				case SimpleDownloaderService.EVENT_UPDATE:
					progressDownloaded = intent.getIntExtra(SimpleDownloaderService.EVENT_UPDATE_DOWNLOADED, 0);
					progressTotal = intent.getIntExtra(SimpleDownloaderService.EVENT_UPDATE_TOTAL, 0);
					Log.d(TAG, "Update " + progressDownloaded + '/' + progressTotal);
					break;
			}
		}
	};

	@Override
	public void onCreate(final Bundle savedInstance)
	{
		super.onCreate(savedInstance);
		Log.d(TAG, "Register start/finish receiver");
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(this.startFinishBroadcastReceiver, new IntentFilter(SimpleDownloaderService.START_FINISH_INTENT_FILTER));
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.d(TAG, "Unregister start/finish receiver");
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this.startFinishBroadcastReceiver);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		Log.d(TAG, "Register update receiver");
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(this.updateBroadcastReceiver, new IntentFilter(SimpleDownloaderService.UPDATE_INTENT_FILTER));
	}

	@Override
	public void onPause()
	{
		super.onPause();
		Log.d(TAG, "Unregister update receiver");
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this.updateBroadcastReceiver);
	}

	/**
	 * Start download
	 */
	@Override
	protected void start()
	{
		synchronized (this)
		{
			Log.d(TAG, "START");
			if (!SimpleDownloadServiceFragment.downloading)
			{
				final String from = this.downloadUrl;
				final String to = this.destFile.getAbsolutePath();

				// starting download
				this.success = false;
				this.progressDownloaded = 0;
				this.progressTotal = 0;
				this.exception = null;

				final Intent intent = new Intent(getActivity(), SimpleDownloaderService.class);
				intent.setAction(SimpleDownloaderService.ACTION_DOWNLOAD);
				intent.putExtra(SimpleDownloaderService.ARG_FROMURL, from);
				intent.putExtra(SimpleDownloaderService.ARG_TOFILE, to);
				intent.putExtra(SimpleDownloaderService.ARG_CODE, ++SimpleDownloadServiceFragment.downloadId);
				getActivity().startService(intent);

				SimpleDownloadServiceFragment.downloading = true;
				return;
			}
		}
		throw new RuntimeException("Already downloading");
	}

	/**
	 * Cancel download
	 */
	@Override
	protected void cancel()
	{
		System.out.println("CANCEL");
		SimpleDownloadServiceFragment.downloading = false;
		final Intent intent = new Intent(getActivity(), SimpleDownloaderService.class);
		getActivity().stopService(intent);  // execute the Service.onDestroy() method immediately but then let the code in onHandleIntent() finish all the way through before destroying the service.
	}

	/**
	 * Kill task (called from notification)
	 */
	protected void kill()
	{
		System.out.println("KILL");
		SimpleDownloaderService.kill(getActivity());
	}

	// S T A T U S

	/**
	 * Download status
	 */
	@Override
	synchronized protected int getStatus(final Progress progress)
	{
		Status status;
		if (downloading)
		{
			status = Status.STATUS_RUNNING;
		}
		else
		{
			if (this.success == null)
			{
				status = Status.STATUS_PENDING;
			}
			else
			{
				status = this.exception == null && this.success ? Status.STATUS_SUCCESSFUL : Status.STATUS_FAILED;
			}
		}

		if (progress != null)
		{
			progress.downloaded = this.progressDownloaded;
			progress.total = this.progressTotal;
		}
		return status.mask;
	}

	/**
	 * Download reason
	 */
	@Override
	protected String getReason()
	{
		if (this.exception != null)
		{
			return this.exception;
		}
		return null;
	}


	// E V E N T S

	/**
	 * UI notification
	 *
	 * @param id      id
	 * @param finish  has finished
	 * @param success true if successful
	 */
	private void fireNotification(int id, final boolean finish, final boolean success)
	{
		final String from = Uri.parse(this.downloadUrl).getHost();
		final String to = this.destFile.getName();

		final NotificationCompat.Builder builder = //
				new NotificationCompat.Builder(this.context) //
						.setSmallIcon(finish ? android.R.drawable.stat_sys_download_done : android.R.drawable.stat_sys_download)//
						.setContentTitle(context.getString(R.string.title_download) + ' ' + this.context.getString(finish ? (success ? R.string.status_download_successful : R.string.status_download_fail) : R.string.status_download_running))//
						.setContentText(from + '→' + to);
		if (!finish)
		{
			final Intent intent = new Intent(this.context, Killer.class);
			intent.setAction(Killer.KILL_DOWNLOAD);
			intent.putExtra(SimpleDownloadServiceFragment.NOTIFICATION_ID, id);

			// use System.currentTimeMillis() to have a unique ID for the pending intent
			PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.addAction(R.drawable.error, context.getString(R.string.action_cancel), pendingIntent);
		}

		// notification
		final Notification notification = builder.build();

		// gets an instance of the NotificationManager service
		final NotificationManager manager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);

		// issue notification
		manager.notify(id, notification);
	}
}


