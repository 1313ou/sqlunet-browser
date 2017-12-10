package org.sqlunet.browser.config;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.sqlunet.browser.common.R;

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
	static private final String TAG = "SimpleDownloadServiceF";

	/**
	 * Notification id key
	 */
	static private final String NOTIFICATION_ID = "notification_id";

	/**
	 * Channel id
	 */
	static private final String CHANNEL_ID = "simple_download_service";

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
	static private Boolean success;

	/**
	 * Exception
	 */
	static private String exception;

	/**
	 * Downloading flag (prevents re-entrance)
	 */
	static private boolean downloading = false;

	/**
	 * Downloaded progress when status was read
	 */
	private long progressDownloaded = 0;

	/**
	 * Total progress when status was read
	 */
	private long progressTotal = 0;

	/**
	 * Broadcast receiver for start finish events
	 */
	private final BroadcastReceiver mainBroadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(final Context context, final Intent intent)
		{
			switch (intent.getStringExtra(SimpleDownloaderService.EVENT))
			{
				case SimpleDownloaderService.EVENT_START:
					Log.d(TAG, "Start");
					SimpleDownloadServiceFragment.downloading = true;
					fireNotification(++SimpleDownloadServiceFragment.notificationId, NotificationType.START);
					break;

				case SimpleDownloaderService.EVENT_UPDATE:
					Log.d(TAG, "Update");
					SimpleDownloadServiceFragment.downloading = true;
					progressDownloaded = intent.getIntExtra(SimpleDownloaderService.EVENT_UPDATE_DOWNLOADED, 0);
					progressTotal = intent.getIntExtra(SimpleDownloaderService.EVENT_UPDATE_TOTAL, 0);
					float progress = (float) progressDownloaded / progressTotal;
					fireNotification(SimpleDownloadServiceFragment.notificationId, NotificationType.UPDATE, progress);
					break;

				case SimpleDownloaderService.EVENT_FINISH:
					int id = intent.getIntExtra(SimpleDownloaderService.EVENT_FINISH_ID, 0);
					if (id == SimpleDownloadServiceFragment.downloadId)
					{
						SimpleDownloadServiceFragment.downloading = false;
						SimpleDownloadServiceFragment.success = intent.getBooleanExtra(SimpleDownloaderService.EVENT_FINISH_RESULT, false);
						SimpleDownloadServiceFragment.exception = intent.getStringExtra(SimpleDownloaderService.EVENT_FINISH_EXCEPTION);
						Log.d(TAG, "Finish " + SimpleDownloadServiceFragment.success);

						// fire notification
						fireNotification(SimpleDownloadServiceFragment.notificationId, NotificationType.FINISH, SimpleDownloadServiceFragment.success);

						// fire ondone
						onDone(SimpleDownloadServiceFragment.success);

						// receiver
						Log.d(TAG, "Unregister main receiver");
						LocalBroadcastManager.getInstance(context).unregisterReceiver(SimpleDownloadServiceFragment.this.mainBroadcastReceiver);
					}
					break;
			}
		}
	};

	/**
	 * Broadcast receiver for update events
	 */
	private final BroadcastReceiver updateBroadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(final Context context, final Intent intent)
		{
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
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		initChannels();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		Log.d(TAG, "Register update receiver");
		final Context context = getActivity();
		assert context != null;
		LocalBroadcastManager.getInstance(context).registerReceiver(this.updateBroadcastReceiver, new IntentFilter(SimpleDownloaderService.UPDATE_INTENT_FILTER));
	}

	@Override
	public void onPause()
	{
		super.onPause();
		Log.d(TAG, "Unregister update receiver");
		final Context context = getActivity();
		assert context != null;
		LocalBroadcastManager.getInstance(context).unregisterReceiver(this.updateBroadcastReceiver);
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
				Log.d(TAG, "Register main receiver");
				final Context context = getActivity();
				assert context != null;
				LocalBroadcastManager.getInstance(context).registerReceiver(this.mainBroadcastReceiver, new IntentFilter(SimpleDownloaderService.MAIN_INTENT_FILTER));

				// reset
				SimpleDownloadServiceFragment.success = null;
				SimpleDownloadServiceFragment.exception = null;
				this.progressDownloaded = 0;
				this.progressTotal = 0;

				// args
				final String from = this.downloadUrl;
				final String to = this.destFile.getAbsolutePath();

				// service intent
				final Intent intent = new Intent(getActivity(), SimpleDownloaderService.class);
				intent.setAction(SimpleDownloaderService.ACTION_DOWNLOAD);
				intent.putExtra(SimpleDownloaderService.ARG_FROMURL, from);
				intent.putExtra(SimpleDownloaderService.ARG_TOFILE, to);
				intent.putExtra(SimpleDownloaderService.ARG_CODE, ++SimpleDownloadServiceFragment.downloadId);
				getActivity().startService(intent);

				// status
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
		final Context context = getActivity();
		kill(context);
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
	 *
	 * @param context context
	 */
	private static void kill(final Context context)
	{
		Log.d(TAG, "Kill service");
		SimpleDownloadServiceFragment.downloading = false;
		final Intent intent = new Intent(context, SimpleDownloaderService.class);
		context.stopService(intent);  // execute the Service.onDestroy() method immediately but then let the code in onHandleIntent() finish all the way through before destroying the service.
	}

	// S T A T U S

	/**
	 * Download status
	 */
	@Override
	synchronized protected int getStatus(final Progress progress)
	{
		Status status;
		if (SimpleDownloadServiceFragment.downloading)
		{
			status = Status.STATUS_RUNNING;
		}
		else
		{
			if (SimpleDownloadServiceFragment.success == null)
			{
				status = Status.STATUS_PENDING;
			}
			else
			{
				status = SimpleDownloadServiceFragment.exception == null && SimpleDownloadServiceFragment.success ? Status.STATUS_SUCCESSFUL : Status.STATUS_FAILED;
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
		if (SimpleDownloadServiceFragment.exception != null)
		{
			return SimpleDownloadServiceFragment.exception;
		}
		return null;
	}


	// E V E N T S

	/**
	 * Killer (used in notifications)
	 */
	public static class Killer extends BroadcastReceiver
	{
		static public final String KILL_DOWNLOAD_SERVICE = "kill_download_service";

		public Killer()
		{
		}

		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			System.out.println("Received kill " + action);
			assert action != null;
			if (action.equals(SimpleDownloadServiceFragment.Killer.KILL_DOWNLOAD_SERVICE))
			{
				SimpleDownloadServiceFragment.kill(context);
			}
			int id = intent.getIntExtra(SimpleDownloadServiceFragment.NOTIFICATION_ID, 0);
			if (id != 0)
			{
				final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				assert manager != null;
				manager.cancel(id);
			}
		}
	}

	private enum NotificationType
	{
		START, UPDATE, FINISH
	}

	/**
	 * Persistent notification builder
	 */
	private NotificationCompat.Builder builder;

	/**
	 * UI notification
	 *
	 * @param id   id
	 * @param type notification
	 * @param args arguments
	 */
	private void fireNotification(int id, final NotificationType type, final Object... args)
	{
		final String from = Uri.parse(this.downloadUrl).getHost();
		final String to = this.destFile.getName();
		String contentTitle = context.getString(R.string.title_download);
		String contentText = from + '→' + to;
		switch (type)
		{
			case START:
				contentText += ' ' + this.context.getString(R.string.status_download_running);
				this.builder = new NotificationCompat.Builder(this.context, CHANNEL_ID);
				this.builder.setSmallIcon(android.R.drawable.stat_sys_download) //
						.setContentTitle(contentTitle) //
						.setContentText(contentText);
				// action
				final Intent intent = new Intent(this.context, Killer.class);
				intent.setAction(Killer.KILL_DOWNLOAD_SERVICE);
				intent.putExtra(SimpleDownloadServiceFragment.NOTIFICATION_ID, id);

				// use System.currentTimeMillis() to have a unique ID for the pending intent
				PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
				this.builder.addAction(R.drawable.error, context.getString(R.string.action_cancel), pendingIntent);
				break;

			case UPDATE:
				final float downloaded = (Float) args[0];
				final int percent = (int) (downloaded * 100);
				contentText += ' ' + this.context.getString(R.string.status_download_running);
				contentText += ' ' + Integer.toString(percent) + '%';
				this.builder.setContentText(contentText);
				break;

			case FINISH:
				final boolean success = (Boolean) args[0];
				contentText += ' ' + this.context.getString(success ? R.string.status_download_successful : R.string.status_download_fail);
				this.builder = new NotificationCompat.Builder(this.context, CHANNEL_ID);
				this.builder.setSmallIcon(android.R.drawable.stat_sys_download_done) //
						.setContentText(contentText);
				break;
		}


		// notification
		final Notification notification = builder.build();

		// gets an instance of the NotificationManager service
		final NotificationManager manager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
		assert manager != null;

		// issue notification
		manager.notify(id, notification);
	}

	private void initChannels()
	{
		if (Build.VERSION.SDK_INT < 26)
		{
			return;
		}
		final Context context = getContext();
		assert context != null;
		final NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Simple Download Service", NotificationManager.IMPORTANCE_DEFAULT);
		channel.setDescription("Simple Download Service Channel");
		final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		assert notificationManager != null;
		notificationManager.createNotificationChannel(channel);
	}
}


