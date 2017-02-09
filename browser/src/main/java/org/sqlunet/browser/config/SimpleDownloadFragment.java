package org.sqlunet.browser.config;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.sqlunet.browser.R;

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
	static private final String TAG = "SimpleDownloadFragment";

	/**
	 * Id for the current notification
	 */
	static private int notificationId = 0;

	/**
	 * Download manager
	 */
	private SimpleDownloader downloader;

	/**
	 * Download id
	 */
	static private int downloadId = -1;

	/**
	 * Result
	 */
	private boolean success;

	/**
	 * Errors when status was read
	 */
	private Exception exception;

	/**
	 * Progress when status was read
	 */
	private Progress progress;

	/**
	 * Start download
	 */
	@Override
	protected void start()
	{
		final String from = this.downloadUrl;
		final String to = this.destFile.getAbsolutePath();
		//final String from = StorageSettings.getDbDownloadSource(getBaseContext());
		//final String to = StorageSettings.getDbDownloadTarget(getBaseContext());

		// starting download
		this.success = false;
		this.progress = new Progress();
		this.exception = null;
		this.downloader = new SimpleDownloader(from, to, ++SimpleDownloadFragment.downloadId, this);
		this.downloader.execute();
	}

	/**
	 * Download status
	 */
	@Override
	synchronized protected int getStatus(final Progress progress)
	{
		Status status = null;
		if (this.downloader != null)
		{
			AsyncTask.Status taskStatus = this.downloader.getStatus();
			this.exception = this.downloader.getException();
			switch (taskStatus)
			{
				case PENDING:
					status = Status.STATUS_PENDING;
					break;
				case RUNNING:
					status = Status.STATUS_RUNNING;
					break;
				case FINISHED:
					status = this.exception == null ? Status.STATUS_SUCCESSFUL : Status.STATUS_FAILED;
					break;
			}

			if (progress != null)
			{
				progress.downloaded = this.progress.downloaded;
				progress.total = this.progress.total;
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
		this.downloader.cancel(true);
	}

	// E V E N T S

	/**
	 * UI notification
	 *
	 * @param id      id
	 * @param finish  has finished
	 * @param success is successful
	 */
	private void notify(int id, final boolean finish, final boolean success)
	{
		final String from = Uri.parse(this.downloadUrl).getHost();
		final String to = this.destFile.getName();
		final NotificationCompat.Builder builder = //
				new NotificationCompat.Builder(getActivity()) //
						.setSmallIcon(finish ? android.R.drawable.stat_sys_download_done : android.R.drawable.stat_sys_download)//
						.setContentTitle(getString(R.string.title_download) + ' ' + getString(finish ? (success ? R.string.status_download_successful : R.string.status_download_fail) : R.string.status_download_running))//
						.setContentText(from + '→' + to);

		// notify
		final Notification notification = builder.build();

		// gets an instance of the NotificationManager service
		final NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

		// issue notify
		manager.notify(id, notification);
	}

	/**
	 * Start
	 */
	@Override
	public void onDownloadStart()
	{
		notify(++notificationId, false, false);
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
		if (code == SimpleDownloadFragment.downloadId)
		{
			this.success = result;

			notify(notificationId, true, result);

			// cleanup if fail
			/*
			if (!this.success)
			{
				if (this.destFile.exists())
				{
					//noinspection ResultOfMethodCallIgnored
					this.destFile.delete();
				}
			}
			*/
			Log.d(TAG, "success " + this.success);

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
		this.progress.downloaded = downloaded;
		this.progress.total = total;
	}
}
