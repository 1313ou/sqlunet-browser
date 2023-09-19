/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.download;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.WorkInfo;

/**
 * Base download fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class BaseDownloadFragment extends AbstractDownloadFragment
{
	/**
	 * Log tag
	 */
	static protected final String TAG = "BaseDownloadF";

	// L I F E C Y C L E

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// arguments
		unmarshal();

		// main receiver
		Log.d(TAG, "Register main receiver");

		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_DOWNLOAD_CANCEL);
		intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

		ContextCompat.registerReceiver(requireContext(), this.cancelReceiver, intentFilter, ContextCompat.RECEIVER_NOT_EXPORTED);

		// notifications
		Notifier.initChannels(requireContext());
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		// main receiver
		Log.d(TAG, "Unregister main receiver");
		requireContext().unregisterReceiver(this.cancelReceiver);
	}

	// A R G U M E N T S

	abstract protected void unmarshal();

	// S T A T U S

	/**
	 * Result
	 */
	@Nullable
	protected Boolean success;

	/**
	 * Cancel pending
	 */
	protected boolean cancel;

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
	long progressDownloaded = 0;

	/**
	 * Total progress when status was read
	 */
	long progressTotal = 0;

	// B R O A D C A S T

	/**
	 * New data available request runnable, run when new data becomes available
	 */
	protected Runnable requestNew;

	/**
	 * Kill old data request runnable, run when old data should be discarded
	 */
	protected Runnable requestKill;

	/**
	 * Set new data available request runnable
	 *
	 * @param requestNew new data available request runnable
	 */
	public void setRequestNew(final Runnable requestNew)
	{
		this.requestNew = requestNew;
	}

	/**
	 * Set kill old data request runnable
	 *
	 * @param requestKill new data available request runnable
	 */
	public void setRequestKill(final Runnable requestKill)
	{
		this.requestKill = requestKill;
	}

	// N O T I F I C A T I O N

	/**
	 * Notification id key
	 */
	static public final String NOTIFICATION_ID = "notification_id";

	/**
	 * Id for the current notification
	 */
	static protected int notificationId = 0;

	/**
	 * Fire UI notification
	 *
	 * @param context        context
	 * @param notificationId notification id
	 * @param type           notification
	 * @param args           arguments
	 */
	abstract protected void fireNotification(@NonNull final Context context, int notificationId, @NonNull final Notifier.NotificationType type, final Object... args);

	// O B S E R V E R

	protected final androidx.lifecycle.Observer<WorkInfo> observer = (wi) -> {

		// progress
		if (this.downloading) // drop event if not
		{
			Data data = wi.getProgress();
			progressDownloaded = data.getLong(DownloadWork.PROGRESS, 0);
			progressTotal = data.getLong(DownloadWork.TOTAL, 0);
			float progress = (float) progressDownloaded / progressTotal;
			fireNotification(requireContext(), notificationId, Notifier.NotificationType.UPDATE, progress);
			Log.d(TAG, "Observed progress " + progressDownloaded + '/' + progressTotal);
		}

		// state
		WorkInfo.State state = wi.getState();
		switch (state)
		{
			// the WorkRequest is enqueued and eligible to run when its Constraints are met and resources are available
			case ENQUEUED:
				Log.d(TAG, "Observed enqueued");
				break;

			// the WorkRequest is currently being executed.
			case RUNNING:
				Log.d(TAG, "Observed running");
				this.downloading = true; // confirm
				fireNotification(requireContext(), notificationId, Notifier.NotificationType.START);
				break;

			// the WorkRequest has completed in a successful state.
			case SUCCEEDED:
				Log.d(TAG, "Observed succeeded");

				// state
				this.downloading = false;
				this.success = true;
				this.exception = null;
				this.cause = null;

				// record
				final Data data = wi.getOutputData();
				final String fromUrl = data.getString(DownloadWork.ARG_FROM);
				final long date = data.getLong(DownloadWork.DATE, -1);
				final long size = data.getLong(DownloadWork.SIZE, -1);
				final String etag = data.getString(DownloadWork.ETAG);
				final String version = data.getString(DownloadWork.VERSION);
				final String staticVersion = data.getString(DownloadWork.STATIC_VERSION);
				Settings.recordDatapackSource(requireContext(), fromUrl, date, size, etag, version, staticVersion);

				// fire notification
				fireNotification(requireContext(), notificationId, Notifier.NotificationType.FINISH, true);

				// fire onDone
				onDone(Status.STATUS_SUCCEEDED);
				break;

			// the WorkRequest has completed in a failure state.
			// All dependent work will also be marked as {@code #FAILED} and will never run.
			case FAILED:
				Log.d(TAG, "Observed failed");

				// state
				this.downloading = false; // release
				final Data errData = wi.getOutputData();
				this.success = false;
				this.exception = errData.getString(DownloadWork.EXCEPTION);
				this.cause = errData.getString(DownloadWork.EXCEPTION_CAUSE);

				// fire notification
				fireNotification(requireContext(), notificationId, Notifier.NotificationType.FINISH, false);

				// fire onDone
				onDone(Status.STATUS_FAILED);
				break;

			// the WorkRequest is currently blocked because its prerequisites haven't finished successfully.
			case BLOCKED:
				Log.d(TAG, "Observed block");
				break;

			// the WorkRequest has been cancelled and will not execute.
			// All dependent work will also be marked as #CANCELLED and will not run.
			case CANCELLED:
				Log.d(TAG, "Observed cancel");

				// state
				this.downloading = false;
				this.success = false;
				this.cancel = true;
				this.exception = null;
				this.cause = null;

				// fire notification
				fireNotification(requireContext(), notificationId, Notifier.NotificationType.CANCEL, false);

				// fire onDone
				onDone(Status.STATUS_CANCELLED);
				break;
		}
	};

	// C A N C E L   B R O A D C A S T / R E C E I V E R

	public static final String ACTION_DOWNLOAD_CANCEL = "action_cancel_download";

	/**
	 * Broadcast receiver for cancel events
	 */
	@NonNull
	protected final BroadcastReceiver cancelReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(final Context context, @NonNull final Intent intent)
		{

			Log.d(TAG, "Received broadcast request");
			String action = intent.getAction();
			if (ACTION_DOWNLOAD_CANCEL.equals(action))
			{
				Log.d(TAG, "Received cancel broadcast request");

				// do
				cancel();
			}
		}
	};

	public static Intent makeCancelIntent(@NonNull final Context context, int notificationId)
	{
		final Intent intent = new Intent();
		intent.setPackage(context.getApplicationContext().getPackageName());
		intent.setAction(ACTION_DOWNLOAD_CANCEL);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.putExtra(NOTIFICATION_ID, notificationId);
		return intent;
	}

	public static PendingIntent makePendingIntent(@NonNull final Context context, @NonNull final Intent intent)
	{
		final int uid = (int) System.currentTimeMillis();
		final int flags = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M ? //
				PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT : //
				PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_CANCEL_CURRENT;
		return PendingIntent.getBroadcast(context, uid, intent, flags);
	}

	public static void requestCancel(@NonNull final Context context, int notificationId)
	{
		Intent intent = makeCancelIntent(context, notificationId);
		Log.d(TAG, "Sending cancel request (broadcast intent)");
		context.sendBroadcast(intent);
	}

	public static void requestPendingCancel(@NonNull final Context context, int notificationId)
	{
		try
		{
			Intent intent = makeCancelIntent(context, notificationId);
			PendingIntent pendingIntent = makePendingIntent(context, intent);
			Log.d(TAG, "Pending intent " + pendingIntent);
			Log.d(TAG, "Pending intent creator package " + pendingIntent.getCreatorPackage());
			Log.d(TAG, "Sending cancel request (sent pending intent)");
			// pendingIntent.send();
			pendingIntent.send(1313, (pendingIntent1, intent1, resultCode, resultData, resultExtras) -> Log.d(TAG, "Sent pending intent " + resultCode), null);
		}
		catch (PendingIntent.CanceledException e)
		{
			throw new RuntimeException(e);
		}
	}

	// C O N T R O L

	/**
	 * Work uuid
	 */
	@Nullable
	protected UUID uuid;

	/**
	 * Cancel download
	 */
	protected void cancel()
	{
		this.cancel = true;
		if (this.uuid != null)
		{
			DownloadWork.stopWork(requireContext(), uuid);
		}
	}

	// S T A T U S

	/**
	 * Download status translation to the abstract layer
	 *
	 * @param progress container to return progress as well
	 */
	@Override
	synchronized protected Status getStatus(@Nullable final Progress progress)
	{
		// progress
		if (progress != null)
		{
			progress.downloaded = this.progressDownloaded;
			progress.total = this.progressTotal;
		}

		// status
		if (this.cancel)
		{
			return Status.STATUS_CANCELLED;
		}
		else if (this.downloading)
		{
			return Status.STATUS_RUNNING;
		}
		else
		{
			if (this.success == null)
			{
				return Status.STATUS_PENDING;
			}
			else
			{
				return this.exception == null && this.success ? Status.STATUS_SUCCEEDED : Status.STATUS_FAILED;
			}
		}
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

	@Override
	protected void show()
	{
	}
}
