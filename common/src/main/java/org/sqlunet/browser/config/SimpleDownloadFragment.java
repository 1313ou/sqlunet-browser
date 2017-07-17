package org.sqlunet.browser.config;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.sqlunet.browser.common.R;

/**
 * Download activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SimpleDownloadFragment extends BaseDownloadFragment implements SimpleDownloader.Listener
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
	 * Download manager
	 */
	static public SimpleDownloader downloader;

	/**
	 * Download id
	 */
	static private int downloadId = -1;

	/**
	 * Result
	 */
	private Boolean success;

	/**
	 * Errors when status was read
	 */
	private Exception exception;

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

	@Override
	public void onActivityCreated(final Bundle savedInstance)
	{
		super.onActivityCreated(savedInstance);
		if (savedInstance != null && SimpleDownloadFragment.downloader != null)
		{
			SimpleDownloadFragment.downloader.setListener(this);
		}
	}

	/**
	 * Start download
	 */
	@Override
	protected void start()
	{
		synchronized (this)
		{
			if (!SimpleDownloadFragment.downloading)
			{
				final String from = this.downloadUrl;
				final String to = this.destFile.getAbsolutePath();
				//final String from = StorageSettings.getDbDownloadSource(getBaseContext());
				//final String to = StorageSettings.getDbDownloadTarget(getBaseContext());

				// starting download
				this.success = false;
				this.progressDownloaded = 0;
				this.progressTotal = 0;
				this.exception = null;
				SimpleDownloadFragment.downloader = new SimpleDownloader(getActivity(), from, to, ++SimpleDownloadFragment.downloadId);
				SimpleDownloadFragment.downloader.setListener(this);
				SimpleDownloadFragment.downloader.execute();
				SimpleDownloadFragment.downloading = true;
				return;
			}
		}
		throw new RuntimeException("Already downloading");
	}

	/**
	 * Download status
	 */
	@Override
	synchronized protected int getStatus(final Progress progress)
	{
		Status status = null;
		if (SimpleDownloadFragment.downloader != null)
		{
			AsyncTask.Status taskStatus = SimpleDownloadFragment.downloader.getStatus();
			this.exception = SimpleDownloadFragment.downloader.getException();
			switch (taskStatus)
			{
				case PENDING:
					status = Status.STATUS_PENDING;
					break;
				case RUNNING:
					status = Status.STATUS_RUNNING;
					break;
				case FINISHED:
					status = this.exception == null && this.success != null && this.success ? Status.STATUS_SUCCESSFUL : Status.STATUS_FAILED;
					break;
			}

			if (progress != null)
			{
				progress.downloaded = this.progressDownloaded;
				progress.total = this.progressTotal;
			}
		}
		return status == null ? 0 : status.mask;
	}

	/**
	 * Download reason
	 */
	@Override
	protected String getReason()
	{
		if (this.exception != null)
		{
			return this.exception.getMessage();
		}
		return null;
	}

	/**
	 * Cancel download
	 */
	@Override
	protected void cancel()
	{
		if (SimpleDownloadFragment.downloader != null)
		{
			SimpleDownloadFragment.downloader.cancel(true);
			SimpleDownloadFragment.downloader = null;
		}
	}

	/**
	 * Cleanup download
	 */
	@Override
	protected void cleanup()
	{
	}

	/**
	 * Kill task (called from notification)
	 */
	static public void kill(final Context context)
	{
		if (SimpleDownloadFragment.downloader != null)
		{
			System.out.println("Kill " + SimpleDownloadFragment.downloader.toString());
			SimpleDownloadFragment.downloader.cancel(true);
			SimpleDownloadFragment.downloader = null;
			SimpleDownloadFragment.downloading = false;
		}
	}

	// E V E N T S

	public static class Killer extends BroadcastReceiver
	{
		static public final String KILL_DOWNLOAD = "kill_download";

		public Killer(){}

		@Override
		public void onReceive(final Context context, final Intent intent)
		{
			String action = intent.getAction();
			System.out.println("Received kill " + action);
			if (action.equals(Killer.KILL_DOWNLOAD))
			{
				SimpleDownloadFragment.kill(context);
			}
			int id = intent.getIntExtra(SimpleDownloadFragment.NOTIFICATION_ID, 0);
			if (id != 0)
			{
				final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				manager.cancel(id);
			}
		}
	}

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
			intent.putExtra(SimpleDownloadFragment.NOTIFICATION_ID, id);

			// use System.currentTimeMillis() to have a unique ID for the pending intent
			PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.addAction(R.drawable.error, context.getString(R.string.action_cancel), pendingIntent);
		}

		// notify
		final Notification notification = builder.build();

		// gets an instance of the NotificationManager service
		final NotificationManager manager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);

		// issue notify
		manager.notify(id, notification);
	}

	/**
	 * Start
	 */
	@Override
	public void onDownloadStart()
	{
		fireNotification(++notificationId, false, false);
	}

	/**
	 * Finish
	 *
	 * @param code   download code
	 * @param result true if success
	 */
	@Override
	public void onDownloadFinish(int code, boolean result)
	{
		SimpleDownloadFragment.downloading = false;
		SimpleDownloadFragment.downloader = null;

		if (code == SimpleDownloadFragment.downloadId)
		{
			this.success = result;

			fireNotification(notificationId, true, result);

			Log.d(TAG, "onDownloadFinish: " + this.success);

			// fire on done
			onDone(this.success);
		}
	}

	/**
	 * Intermediate progress notify
	 *
	 * @param total      total to download
	 * @param downloaded downloaded bytes
	 */
	@Override
	public void onDownloadUpdate(long downloaded, long total)
	{
		this.progressDownloaded = downloaded;
		this.progressTotal = total;
	}
}
