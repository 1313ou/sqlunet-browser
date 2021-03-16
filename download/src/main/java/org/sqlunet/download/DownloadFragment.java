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

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static org.sqlunet.download.DownloadService.EVENT_FINISH;
import static org.sqlunet.download.DownloadService.EVENT_START;
import static org.sqlunet.download.DownloadService.EVENT_UPDATE;
import static org.sqlunet.download.Killer.EVENT_CANCEL_REQUEST;

/**
 * Download Service fragment.
 * Interface between service and activity.
 * Service sends messages to this fragment's receiver.
 * Signals completion through the OnComplete callback in the activity.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DownloadFragment extends BaseDownloadFragment
{
	/**
	 * Log tag
	 */
	static private final String TAG = "DownloadF";

	/**
	 * Channel id key
	 */
	static private final String CHANNEL_ID = "semantikos_download_notification_channel";

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
	@Nullable
	protected Boolean success;

	/**
	 * Cause
	 */
	@Nullable
	protected String cause;

	/**
	 * Exception
	 */
	@Nullable
	protected String exception;

	/**
	 * Downloading flag (prevents re-entrance)
	 */
	protected boolean downloading = false;

	/**
	 * Downloaded progress when status was read
	 */
	private long progressDownloaded = 0;

	/**
	 * Total progress when status was read
	 */
	private long progressTotal = 0;

	/**
	 * Broadcast receiver for start/finish/update main events
	 */
	@NonNull
	private final BroadcastReceiver mainBroadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(final Context context, @NonNull final Intent intent)
		{
			handleMainIntent(intent);
		}
	};

	/**
	 * Broadcast receiver for update small granularity events
	 */
	private final BroadcastReceiver updateBroadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(final Context context, @NonNull final Intent intent)
		{
			handleUpdateIntent(intent);
		}
	};

	/**
	 * Action for the service
	 */
	protected String getAction()
	{
		return Killer.KILL_DOWNLOAD_SERVICE;
	}

	/**
	 * Layout
	 */
	@Override
	protected int getResId()
	{
		return R.layout.fragment_download;
	}

	// L I F E C Y C L E

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// main receiver
		Log.d(TAG, "Register main receiver");
		LocalBroadcastManager.getInstance(requireContext()).registerReceiver(this.mainBroadcastReceiver, new IntentFilter(DownloadService.MAIN_INTENT_FILTER));

		// notifications
		initChannels();
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
		LocalBroadcastManager.getInstance(requireContext()).registerReceiver(this.updateBroadcastReceiver, new IntentFilter(DownloadService.UPDATE_INTENT_FILTER));
	}

	@Override
	public void onPause()
	{
		super.onPause();

		// update receiver
		Log.d(TAG, "Unregister update receiver");
		LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(this.updateBroadcastReceiver);
	}

	// S T A R T

	/**
	 * Start download
	 */
	@Override
	protected void start()
	{
		synchronized (this)
		{
			Log.d(TAG, "Start");
			if (!this.downloading) // prevent recursion
			{
				// reset
				this.success = null;
				this.exception = null;
				this.cause = null;
				this.progressDownloaded = 0;
				this.progressTotal = 0;

				// args
				final String from = this.downloadUrl;
				assert this.downloadedFile != null;
				final String to = this.downloadedFile.getAbsolutePath();

				// service intent
				final Intent intent = new Intent(requireContext(), DownloadService.class);
				intent.setAction(DownloadService.ACTION_DOWNLOAD);
				intent.putExtra(DownloadService.ARG_FROM_URL, from);
				intent.putExtra(DownloadService.ARG_TO_FILE, to);
				intent.putExtra(DownloadService.ARG_CODE, ++DownloadFragment.downloadId);
				final Context context = requireContext();
				startService(context, intent);

				// status
				this.downloading = true; // set
				return;
			}
		}
		throw new RuntimeException("Already downloading");
	}

	protected void startService(@NonNull Context context, @NonNull Intent intent)
	{
		DownloadService.enqueueWork(context, intent);
	}

	/**
	 * Cancel download
	 */
	@Override
	protected void cancel()
	{
		Killer.kill(requireContext(), getAction());
	}

	/**
	 * Cleanup download
	 */
	@SuppressWarnings("EmptyMethod")
	@Override
	protected void cleanup()
	{
	}

	// S T A T U S

	/**
	 * Download status
	 */
	@Override
	synchronized protected int getStatus(@Nullable final Progress progress)
	{
		Status status;
		if (this.downloading) // status
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
	@Nullable
	@Override
	protected String getReason()
	{
		if (this.exception != null)
		{
			return this.exception;
		}
		if (this.cause != null)
		{
			return this.cause;
		}
		return null;
	}

	// R E C E I V E R

	private void handleMainIntent(@NonNull final Intent intent)
	{
		String event = intent.getStringExtra(DownloadService.EVENT);
		if (event != null)
		{
			switch (event)
			{
				case EVENT_START:
					Log.d(TAG, "Main Broadcast Receive start");
					this.downloading = true; // confirm
					fireNotification(++DownloadFragment.notificationId, NotificationType.START);
					break;

				case EVENT_UPDATE:
					if (this.downloading) // drop event if not
					{
						Log.d(TAG, "Main Broadcast Receive update");
						progressDownloaded = intent.getLongExtra(DownloadService.EVENT_UPDATE_DOWNLOADED, 0);
						progressTotal = intent.getLongExtra(DownloadService.EVENT_UPDATE_TOTAL, 0);
						float progress = (float) progressDownloaded / progressTotal;
						fireNotification(DownloadFragment.notificationId, NotificationType.UPDATE, progress);
					}
					break;

				case EVENT_FINISH:
					int id = intent.getIntExtra(DownloadService.EVENT_FINISH_ID, 0);
					if (id == DownloadFragment.downloadId)
					{
						this.downloading = false; // release

						// parse arguments
						this.success = intent.getBooleanExtra(DownloadService.EVENT_FINISH_RESULT, false);
						this.exception = intent.getStringExtra(DownloadService.EVENT_FINISH_EXCEPTION);
						this.cause = intent.getStringExtra(DownloadService.EVENT_FINISH_CAUSE);
						Log.d(TAG, "Main Broadcast Receive finish " + this.success);

						// fire notification
						fireNotification(DownloadFragment.notificationId, NotificationType.FINISH, this.success);

						// fire onDone
						boolean result = this.success != null ? this.success : false;
						onDone(result);
					}
					break;

				case EVENT_CANCEL_REQUEST:
					this.downloading = false; // release
					this.success = intent.getBooleanExtra(DownloadService.EVENT_FINISH_RESULT, false);
					this.exception = intent.getStringExtra(DownloadService.EVENT_FINISH_EXCEPTION);
					this.cause = intent.getStringExtra(DownloadService.EVENT_FINISH_CAUSE);
					Log.d(TAG, "Main Broadcast Receive cancel request");
					break;
			}
		}
	}

	private void handleUpdateIntent(@NonNull final Intent intent)
	{
		if (EVENT_UPDATE.equals(intent.getStringExtra(DownloadService.EVENT)))
		{
			progressDownloaded = intent.getLongExtra(DownloadService.EVENT_UPDATE_DOWNLOADED, 0);
			progressTotal = intent.getLongExtra(DownloadService.EVENT_UPDATE_TOTAL, 0);
			Log.d(TAG, "Update Broadcast Receiver " + progressDownloaded + '/' + progressTotal);
		}
	}

	// N O T I F I C A T I O N

	private enum NotificationType
	{
		START, UPDATE, FINISH
	}

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
		final String to = this.downloadedFile == null ? this.appContext.getString(R.string.result_deleted) : this.downloadedFile.getName();
		String contentTitle = this.appContext.getString(R.string.title_download);
		String contentText = from + '→' + to;

		// notification
		Notification notification;
		switch (type)
		{
			case START:
				contentText += ' ' + this.appContext.getString(R.string.status_download_running);
				notification = makeNotificationStartOrUpdate(contentTitle, contentText, id);
				break;

			case UPDATE:
				final float downloaded = (Float) args[0];
				final int percent = (int) (downloaded * 100);
				contentText += ' ' + this.appContext.getString(R.string.status_download_running) + ' ' + percent + '%';
				notification = makeNotificationStartOrUpdate(contentTitle, contentText, id);
				break;

			case FINISH:
				final boolean success = (Boolean) args[0];
				contentText += ' ' + this.appContext.getString(success ? R.string.status_download_successful : R.string.status_download_fail);
				notification = makeNotificationFinish(contentTitle, contentText);

				// cancel previous
				manager.cancel(id);
				break;

			default:
				return;
		}

		// issue notification
		Log.d(TAG, "Notification id=" + id + " type=" + type.name() + " " + notification);
		manager.notify(id, notification);
	}

	private Notification makeNotificationStartOrUpdate(final String contentTitle, final String contentText, final int id)
	{
		// builder
		final NotificationCompat.Builder builder = new NotificationCompat.Builder(this.appContext, CHANNEL_ID);
		builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS) //
				.setSmallIcon(android.R.drawable.stat_sys_download) //
				.setContentTitle(contentTitle) //
				.setContentText(contentText) //
		// .setColor(some color) //
		;

		// action
		final Intent intent = new Intent(this.appContext, Killer.class);
		intent.setAction(getAction());
		intent.putExtra(DownloadFragment.NOTIFICATION_ID, id);
		final PendingIntent pendingIntent = PendingIntent.getBroadcast(this.appContext, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT); // use System.currentTimeMillis() to have a unique ID for the pending intent
		NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_notif_cancel, this.appContext.getString(R.string.action_cancel).toUpperCase(Locale.getDefault()), pendingIntent).build();
		builder.addAction(action);

		// build notification
		return builder.build();
	}

	private Notification makeNotificationFinish(final String contentTitle, final String contentText)
	{
		// builder
		final NotificationCompat.Builder builder = new NotificationCompat.Builder(this.appContext, CHANNEL_ID);
		builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS) //
				.setSmallIcon(android.R.drawable.stat_sys_download_done) //
				.setContentTitle(contentTitle) //
				.setContentText(contentText);

		// build notification
		return builder.build();
	}

	private void initChannels()
	{
		if (Build.VERSION.SDK_INT < 26)
		{
			return;
		}
		final Context context = requireContext();

		final NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Download Service", NotificationManager.IMPORTANCE_LOW);
		channel.setDescription("Download Service Channel");
		channel.setSound(null, null);
		final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		assert notificationManager != null;
		notificationManager.createNotificationChannel(channel);
	}
}


