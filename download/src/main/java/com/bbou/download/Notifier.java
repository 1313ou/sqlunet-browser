/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.download;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


/**
 * Notifier.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Notifier
{
	/**
	 * Log tag
	 */
	static protected final String TAG = "Notifier";

	/**
	 * Channel id key
	 */
	static private final String CHANNEL_ID = "download_notification_channel";

	enum NotificationType
	{
		START, UPDATE, FINISH, CANCEL
	}

	/**
	 * Fire UI notification
	 *
	 * @param notificationId notification id
	 * @param type           notification
	 * @param args           arguments
	 */
	public static void fireNotification(@NonNull final Context context, int notificationId, @NonNull final NotificationType type, String contentText, final Object... args)
	{
		// get an instance of the NotificationManager service
		final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		assert manager != null;

		// content
		String contentTitle = context.getString(R.string.title_download);

		// notification
		Notification notification;
		switch (type)
		{
			case START:
				contentText += ' ' + context.getString(R.string.status_download_running);
				notification = makeNotificationStartOrUpdate(context, contentTitle, contentText, notificationId);
				break;

			case UPDATE:
				final float downloaded = (Float) args[0];
				final int percent = (int) (downloaded * 100);
				contentText += ' ' + context.getString(R.string.status_download_running) + ' ' + percent + '%';
				notification = makeNotificationStartOrUpdate(context, contentTitle, contentText, notificationId);
				break;

			case FINISH:
				final boolean success = (Boolean) args[0];
				contentText += ' ' + context.getString(success ? R.string.status_download_successful : R.string.status_download_fail);
				notification = makeNotificationFinish(context, contentTitle, contentText);

				// cancel previous
				// manager.cancel(notificationId);
				break;

			case CANCEL:
				manager.cancel(notificationId);
				return;

			default:
				return;
		}

		// issue notification
		if (notification != null)
		{
			Log.d(TAG, "Notification id=" + notificationId + " type=" + type.name());
			manager.notify(notificationId, notification);
		}
	}

	/**
	 * Cancel UI notification
	 *
	 * @param notificationId notificationId
	 */
	protected void cancelNotification(@NonNull final Context context, int notificationId)
	{
		if (notificationId != 0)
		{
			final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			assert manager != null;
			manager.cancel(notificationId);
		}
	}

	@Nullable
	static Notification makeNotificationStartOrUpdate(@NonNull final Context context, final String contentTitle, final String contentText, final int id)
	{
		try
		{
			// builder
			final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
			builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS) //
					.setSmallIcon(android.R.drawable.stat_sys_download) //
					.setContentTitle(contentTitle) //
					.setContentText(contentText) //
			//.setColor(some color) //
			;

			// intent
			final Intent intent = DownloadFragment.makeCancelIntent(context, id);

			// pending intent
			final PendingIntent pendingIntent = DownloadFragment.makePendingIntent(context, intent);

			// action
			NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_notif_cancel, context.getString(R.string.action_cancel).toUpperCase(Locale.getDefault()), pendingIntent).build();
			builder.addAction(action);

			return builder.build();
		}
		catch (SecurityException ignored)
		{
		}
		return null;
	}

	@Nullable
	static Notification makeNotificationFinish(@NonNull final Context context, final String contentTitle, final String contentText)
	{
		try
		{
			// builder
			final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
			builder.setDefaults(NotificationCompat.DEFAULT_LIGHTS) //
					.setSmallIcon(android.R.drawable.stat_sys_download_done) //
					.setContentTitle(contentTitle) //
					.setContentText(contentText);

			// build notification
			return builder.build();
		}
		catch (SecurityException ignored)
		{
		}
		return null;
	}

	public static void initChannels(@NonNull final Context context)
	{
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
		{
			return;
		}
		final NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Download", NotificationManager.IMPORTANCE_LOW);
		channel.setDescription("Download Channel");
		channel.setSound(null, null);
		final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		assert notificationManager != null;
		notificationManager.createNotificationChannel(channel);
	}
}


