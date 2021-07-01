package org.sqlunet.download;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static org.sqlunet.download.DownloadService.EVENT;
import static org.sqlunet.download.DownloadService.EVENT_FINISH_CAUSE;
import static org.sqlunet.download.DownloadService.EVENT_FINISH_RESULT;
import static org.sqlunet.download.DownloadService.MAIN_INTENT_FILTER;

/**
 * Killer (used in notifications)
 */
public class Killer extends BroadcastReceiver
{
	static private final String TAG = "Killer";

	static final String KILL_DOWNLOAD_SERVICE = "kill_download_service";

	static final String KILL_ZIP_DOWNLOAD_SERVICE = "kill_zip_download_service";

	static final String EVENT_CANCEL_REQUEST = "cancel_request";

	public Killer()
	{
	}

	@Override
	public void onReceive(@NonNull Context context, @NonNull Intent intent)
	{
		String action = intent.getAction();
		Log.i(TAG, "Received " + action);
		assert action != null;

		// stop service
		kill(context, action);

		// notification
		int notificationId = intent.getIntExtra(DownloadFragment.NOTIFICATION_ID, 0);
		if (notificationId != 0)
		{
			final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			assert manager != null;
			manager.cancel(notificationId);
		}
	}

	/**
	 * Kill service (called from notification)
	 *
	 * @param context context
	 * @param action  action
	 */
	static void kill(@NonNull final Context context, final String action)
	{
		// stop service
		if (action.equals(Killer.KILL_DOWNLOAD_SERVICE))
		{
			Log.d(TAG, "Kill download service");

			// stop service (broadcasting action)
			DownloadService.kill(context);

			// stop service (calling stopService)
			// execute the Service.onDestroy() method immediately
			// but then let the code in onHandleIntent() finish all the way through before destroying the service.
			final Intent intent = new Intent(context, DownloadService.class);
			context.stopService(intent);

			// broadcast to fragment
			broadcast(context, MAIN_INTENT_FILTER, //
					EVENT, EVENT_CANCEL_REQUEST, //
					EVENT_FINISH_RESULT, false, //
					EVENT_FINISH_CAUSE, "cancel");
		}
		else if (action.equals(Killer.KILL_ZIP_DOWNLOAD_SERVICE))
		{
			Log.d(TAG, "Kill zip download service");

			// stop service (broadcasting action)
			DownloadService.kill(context);

			// stop service
			// execute the Service.onDestroy() method immediately
			// but then let the code in onHandleIntent() finish all the way through before destroying the service.
			final Intent intent = new Intent(context, DownloadZipService.class);
			context.stopService(intent);

			// broadcast to fragment
			broadcast(context, MAIN_INTENT_FILTER, //
					EVENT, EVENT_CANCEL_REQUEST, //
					EVENT_FINISH_RESULT, false, //
					EVENT_FINISH_CAUSE, "cancel");
		}
	}

	/**
	 * Broadcast message
	 *
	 * @param context      context
	 * @param intentFilter intent filter
	 * @param args         arguments
	 */
	static private void broadcast(@NonNull final Context context, @SuppressWarnings("SameParameterValue") final String intentFilter, @NonNull final Object... args)
	{
		final Intent broadcastIntent = new Intent(intentFilter);
		broadcastIntent.setPackage(context.getPackageName());
		for (int i = 0; i < args.length; i = i + 2)
		{
			final String key = (String) args[i];
			final Object value = args[i + 1];

			// string
			if (value instanceof String)
			{
				broadcastIntent.putExtra(key, (String) value);
			}
			// int
			else if (value instanceof Integer)
			{
				broadcastIntent.putExtra(key, (int) value);
			}
			// long
			else if (value instanceof Long)
			{
				broadcastIntent.putExtra(key, (long) value);
			}
			// boolean
			else if (value instanceof Boolean)
			{
				broadcastIntent.putExtra(key, (boolean) value);
			}
		}
		LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
	}
}
