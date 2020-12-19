/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.download;

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
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
	@Nullable
	static private Boolean success;

	/**
	 * Exception
	 */
	@Nullable
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
	@NonNull
	private final BroadcastReceiver mainBroadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(final Context context, @NonNull final Intent intent)
		{
			String event = intent.getStringExtra(SimpleDownloaderService.EVENT);
			if (event != null)
			{
				switch (event)
				{
					case SimpleDownloaderService.EVENT_START:
						Log.d(TAG, "Start");
						SimpleDownloadServiceFragment.downloading = true;
						fireNotification(++SimpleDownloadServiceFragment.notificationId, NotificationType.START);
						break;

					case SimpleDownloaderService.EVENT_UPDATE:
						if (SimpleDownloadServiceFragment.downloading)
						{
							Log.d(TAG, "Update");
							// SimpleDownloadServiceFragment.downloading = true;
							progressDownloaded = intent.getLongExtra(SimpleDownloaderService.EVENT_UPDATE_DOWNLOADED, 0);
							progressTotal = intent.getLongExtra(SimpleDownloaderService.EVENT_UPDATE_TOTAL, 0);
							float progress = (float) progressDownloaded / progressTotal;
							fireNotification(SimpleDownloadServiceFragment.notificationId, NotificationType.UPDATE, progress);
						}
						break;

					case SimpleDownloaderService.EVENT_FINISH:
						int id = intent.getIntExtra(SimpleDownloaderService.EVENT_FINISH_ID, 0);
						if (id == SimpleDownloadServiceFragment.downloadId)
						{
							SimpleDownloadServiceFragment.downloading = false;
							SimpleDownloadServiceFragment.success = intent.getBooleanExtra(SimpleDownloaderService.EVENT_FINISH_RESULT, false);
							SimpleDownloadServiceFragment.exception = intent.getStringExtra(SimpleDownloaderService.EVENT_FINISH_EXCEPTION);
							Log.d(TAG, "Finish " + success);

							// fire notification
							fireNotification(SimpleDownloadServiceFragment.notificationId, NotificationType.FINISH, SimpleDownloadServiceFragment.success);

							// fire onDone
							boolean result = success != null ? success : false;
							onDone(result);
						}
						break;
				}
			}
		}
	};

	/**
	 * Broadcast receiver for update events
	 */
	private final BroadcastReceiver updateBroadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(final Context context, @NonNull final Intent intent)
		{
			if (SimpleDownloaderService.EVENT_UPDATE.equals(intent.getStringExtra(SimpleDownloaderService.EVENT)))
			{
				progressDownloaded = intent.getLongExtra(SimpleDownloaderService.EVENT_UPDATE_DOWNLOADED, 0);
				progressTotal = intent.getLongExtra(SimpleDownloaderService.EVENT_UPDATE_TOTAL, 0);
				Log.d(TAG, "Update " + progressDownloaded + '/' + progressTotal);
			}
		}
	};

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// main receiver
		Log.d(TAG, "Register main receiver");
		LocalBroadcastManager.getInstance(requireContext()).registerReceiver(this.mainBroadcastReceiver, new IntentFilter(SimpleDownloaderService.MAIN_INTENT_FILTER));

		initChannels();
	}

	@Override
	protected int getResId()
	{
		return R.layout.fragment_download;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		// main receiver
		Log.d(TAG, "Unregister main receiver");
		LocalBroadcastManager.getInstance(this.appContext).unregisterReceiver(this.mainBroadcastReceiver);
	}

	@Override
	public void onResume()
	{
		super.onResume();

		// update receiver
		Log.d(TAG, "Register update receiver");
		LocalBroadcastManager.getInstance(requireContext()).registerReceiver(this.updateBroadcastReceiver, new IntentFilter(SimpleDownloaderService.UPDATE_INTENT_FILTER));
	}

	@Override
	public void onPause()
	{
		super.onPause();

		// update receiver
		Log.d(TAG, "Unregister update receiver");
		LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(this.updateBroadcastReceiver);
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
				// reset
				SimpleDownloadServiceFragment.success = null;
				SimpleDownloadServiceFragment.exception = null;
				this.progressDownloaded = 0;
				this.progressTotal = 0;

				// args
				final String from = this.downloadUrl;
				assert this.downloadedFile != null;
				final String to = this.downloadedFile.getAbsolutePath();

				// service intent
				final Intent intent = new Intent(requireContext(), SimpleDownloaderService.class);
				intent.setAction(SimpleDownloaderService.ACTION_DOWNLOAD);
				intent.putExtra(SimpleDownloaderService.ARG_FROM_URL, from);
				intent.putExtra(SimpleDownloaderService.ARG_TO_FILE, to);
				intent.putExtra(SimpleDownloaderService.ARG_CODE, ++SimpleDownloadServiceFragment.downloadId);
				final Context context = requireContext();
				startService(context, intent);

				// status
				SimpleDownloadServiceFragment.downloading = true;
				return;
			}
		}
		throw new RuntimeException("Already downloading");
	}

	protected void startService(@NonNull Context context, @NonNull Intent intent)
	{
		SimpleDownloaderService.enqueueWork(context, intent);
	}

	/**
	 * Cancel download
	 */
	@Override
	protected void cancel()
	{
		kill(requireContext());
	}

	/**
	 * Cleanup download
	 */
	@SuppressWarnings("EmptyMethod")
	@Override
	protected void cleanup()
	{
	}

	/**
	 * Kill task (called from notification)
	 *
	 * @param context context
	 */
	private static void kill(@NonNull final Context context)
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
	synchronized protected int getStatus(@Nullable final Progress progress)
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
				status = SimpleDownloadServiceFragment.exception == null && success ? Status.STATUS_SUCCESSFUL : Status.STATUS_FAILED;
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
	@Nullable
	@Override
	protected String getReason()
	{
		if (SimpleDownloadServiceFragment.exception != null)
		{
			return SimpleDownloadServiceFragment.exception;
		}
		return null;
	}


	// K I L L   E V E N T

	/**
	 * Killer (used in notifications)
	 */
	public static class Killer extends BroadcastReceiver
	{
		static final String KILL_DOWNLOAD_SERVICE = "kill_download_service";

		public Killer()
		{
		}

		@Override
		public void onReceive(@NonNull Context context, @NonNull Intent intent)
		{
			String action = intent.getAction();
			System.out.println("Received kill " + action);
			assert action != null;
			if (action.equals(Killer.KILL_DOWNLOAD_SERVICE))
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

	// N O T I F I C A T I O N

	private enum NotificationType
	{
		START, UPDATE, FINISH
	}

	/**
	 * Persistent notification builder
	 */
	@Nullable
	private NotificationCompat.Builder builder;

	/**
	 * UI notification
	 *
	 * @param id   id
	 * @param type notification
	 * @param args arguments
	 */
	protected void fireNotification(int id, @NonNull final NotificationType type, final Object... args)
	{
		// get an instance of the NotificationManager service
		final NotificationManager manager = (NotificationManager) this.appContext.getSystemService(Context.NOTIFICATION_SERVICE);
		assert manager != null;

		// content
		final String from = Uri.parse(this.downloadUrl).getHost();
		assert this.downloadedFile != null;
		final String to = this.downloadedFile.getName();
		String contentTitle = this.appContext.getString(R.string.title_download);
		String contentText = from + '→' + to;

		// notification
		Notification notification;
		switch (type)
		{
			case START:
				contentText += ' ' + this.appContext.getString(R.string.status_download_running);
				this.builder = new NotificationCompat.Builder(this.appContext, CHANNEL_ID);
				this.builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS) //
						.setSmallIcon(android.R.drawable.stat_sys_download) //
						.setContentTitle(contentTitle) //
						.setContentText(contentText);
				// action
				final Intent intent = new Intent(this.appContext, Killer.class);
				intent.setAction(Killer.KILL_DOWNLOAD_SERVICE);
				intent.putExtra(SimpleDownloadServiceFragment.NOTIFICATION_ID, id);

				// use System.currentTimeMillis() to have a unique ID for the pending intent
				PendingIntent pendingIntent = PendingIntent.getBroadcast(this.appContext, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
				this.builder.addAction(R.drawable.ic_error, this.appContext.getString(R.string.action_cancel), pendingIntent);

				notification = this.builder.build();
				break;

			case UPDATE:
				final float downloaded = (Float) args[0];
				final int percent = (int) (downloaded * 100);
				contentText += ' ' + this.appContext.getString(R.string.status_download_running);
				contentText += ' ' + Integer.toString(percent) + '%';

				ensureBuilder();
				assert this.builder != null;
				this.builder.setContentText(contentText);

				notification = this.builder.build();
				break;

			case FINISH:
				final boolean success = (Boolean) args[0];
				contentText += ' ' + this.appContext.getString(success ? R.string.status_download_successful : R.string.status_download_fail);

				// force new builder (no action)
				this.builder = new NotificationCompat.Builder(this.appContext, CHANNEL_ID);
				this.builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS) //
						.setSmallIcon(android.R.drawable.stat_sys_download_done) //
						.setContentText(contentText);

				manager.cancel(id);
				notification = this.builder.build();
				this.builder = null;
				break;

			default:
				return;
		}

		// issue notification
		Log.d(TAG, "Notification id=" + id + " type=" + type.name() + " " + notification);
		manager.notify(id, notification);
	}

	private void ensureBuilder()
	{
		if (this.builder == null)
		{
			Log.d(TAG, "New Notification Builder");
			this.builder = new NotificationCompat.Builder(this.appContext, CHANNEL_ID);
			this.builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS) //
					.setSmallIcon(android.R.drawable.stat_sys_download);
		}
	}

	private void initChannels()
	{
		if (Build.VERSION.SDK_INT < 26)
		{
			return;
		}
		final Context context = getContext();
		assert context != null;
		final NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Simple Download Service", NotificationManager.IMPORTANCE_LOW);
		channel.setDescription("Simple Download Service Channel");
		channel.setSound(null, null);
		final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		assert notificationManager != null;
		notificationManager.createNotificationChannel(channel);
	}
}


