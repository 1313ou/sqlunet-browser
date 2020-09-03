/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.concurrency;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * Task observer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("WeakerAccess")
public class TaskObserver
{
	static private final String TAG = "TaskObserver";

	/**
	 * Manager listener
	 */
	@FunctionalInterface
	public interface Listener
	{
		/**
		 * Start event
		 */
		default void taskStart(final Task<?, ?, ?> task)
		{
		}

		/**
		 * Finish event
		 *
		 * @param result result
		 */
		@SuppressWarnings({"EmptyMethod", "UnusedReturnValue"})
		void taskFinish(boolean result);

		/**
		 * Intermediate update event
		 *
		 * @param progress progress value
		 * @param length   length
		 */
		default void taskUpdate(long progress, long length)
		{
		}
	}

	/**
	 * Base listener
	 */
	static public abstract class BaseListener implements Listener
	{
		/**
		 * Cached context
		 * <p>
		 * Uses:
		 * context.getString(R.string.x)
		 * PreferenceManager.getDefaultSharedPreferences(context)
		 * LocalBroadcastManager.getInstance(context)
		 */
		final protected Context appContext;

		BaseListener(final Context appContext)
		{
			this.appContext = appContext;
		}

		@SuppressWarnings("WeakerAccess")
		@Override
		public void taskStart(final Task<?, ?, ?> task)
		{
			Log.d(TAG, "Task start");
		}

		@SuppressWarnings("WeakerAccess")
		@Override
		public void taskUpdate(long progress, long length)
		{
			Log.d(TAG, "Task " + progress + '/' + length);
		}

		@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
		@Override
		public void taskFinish(boolean result)
		{
			Log.d(TAG, "Task " + (result ? "succeeded" : "failed"));
		}
	}

	/**
	 * Toast listener
	 */
	static public class ToastListener extends BaseListener
	{
		ToastListener(final Context appContext)
		{
			super(appContext);
		}

		@SuppressWarnings("WeakerAccess")
		@Override
		public void taskStart(final Task<?, ?, ?> task)
		{
			super.taskStart(task);
			Toast.makeText(this.appContext, R.string.status_task_start_toast, Toast.LENGTH_SHORT).show();
		}

		@SuppressWarnings("WeakerAccess")
		@Override
		public void taskFinish(boolean result)
		{
			super.taskFinish(result);
			Toast.makeText(this.appContext, R.string.status_task_done_toast, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Toast listener
	 */
	static public class ToastWithStatusListener extends ToastListener
	{
		final private TextView status;

		public ToastWithStatusListener(final Context appContext, final TextView status)
		{
			super(appContext);
			this.status = status;
		}

		@Override
		public void taskStart(final Task<?, ?, ?> task)
		{
			super.taskStart(task);
			this.status.setText(R.string.status_task_running);
		}

		@Override
		public void taskFinish(final boolean result)
		{
			super.taskFinish(result);
			this.status.setText(result ? R.string.status_task_done : R.string.status_task_failed);
		}
	}

	/**
	 * Progress bar listener
	 */
	@SuppressWarnings("unused")
	static public class ProgressListener extends ToastListener
	{
		private final ProgressBar progress;

		/**
		 * Constructor
		 *
		 * @param appContext app context
		 * @param progress   progress bar
		 */
		ProgressListener(final Context appContext, final ProgressBar progress)
		{
			super(appContext);
			this.progress = progress;
		}

		@Override
		public void taskStart(Task<?, ?, ?> task)
		{
			super.taskStart(task);
			this.progress.setIndeterminate(true);
		}

		@Override
		public void taskUpdate(long progress, long length)
		{
			super.taskUpdate(progress, length);
			boolean indeterminate = length == -1;
			this.progress.setIndeterminate(indeterminate);
			if (!indeterminate)
			{
				this.progress.setMax(100);
				this.progress.setProgress(100);
			}
		}

		@Override
		public void taskFinish(boolean result)
		{
			super.taskFinish(result);
			this.progress.setMax(100);
			this.progress.setProgress(100);
		}
	}

	/**
	 * Dialog listener
	 */
	static public class DialogListener extends BaseListener
	{
		@NonNull
		private final ProgressDialog progressDialog;

		private final CharSequence unit;

		/**
		 * Constructor
		 *
		 * @param activity  activity
		 * @param titleId   titleId id
		 * @param messageId message id
		 * @param unitId    unit id
		 */
		@SuppressWarnings("unused")
		DialogListener(@NonNull final Activity activity, final int titleId, final int messageId, final int unitId)
		{
			this(activity, titleId, activity.getString(messageId), activity.getString(unitId));
		}

		/**
		 * Constructor
		 *
		 * @param activity activity
		 * @param titleId  titleId id
		 * @param message  message
		 * @param unit     unit
		 */
		public DialogListener(@NonNull final Activity activity, final int titleId, final CharSequence message, final CharSequence unit)
		{
			super(activity);
			this.progressDialog = makeDialog(activity, titleId, message);
			this.unit = unit;
		}

		@Override
		public void taskStart(@NonNull final Task<?, ?, ?> task)
		{
			super.taskStart(task);
			this.progressDialog.show();
			this.progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, this.appContext.getString(R.string.action_abort), (dialog, which) -> {
				if (which == DialogInterface.BUTTON_NEGATIVE)
				{
					boolean result = task.cancel(true);
					Log.d(TAG, "Cancel task " + task + ' ' + result);
					dialog.dismiss();
				}
			});
		}

		@Override
		public void taskUpdate(long progress, long length)
		{
			super.taskUpdate(progress, length);
			final boolean indeterminate = length == -1;
			this.progressDialog.setIndeterminate(indeterminate);
			if (indeterminate)
			{
				this.progressDialog.setProgressNumberFormat(null);
				this.progressDialog.setProgressPercentFormat(null);
			}
			final String message = this.unit != null ? countToString(progress, this.unit) : countToStorageString(progress);
			this.progressDialog.setMessage(message);
			if (!indeterminate)
			{
				final int percent = (int) ((progress * 100F) / length);
				this.progressDialog.setMax(100);
				this.progressDialog.setProgress(percent);
			}
		}

		@SuppressWarnings("UnusedReturnValue")
		@Override
		public void taskFinish(boolean result)
		{
			super.taskFinish(result);
			this.progressDialog.dismiss();
		}

		/**
		 * Make dialog
		 *
		 * @param activity activity
		 * @param titleId  titleId id
		 * @param message  message
		 * @return dialog
		 */
		@NonNull
		static private ProgressDialog makeDialog(@NonNull final Activity activity, final int titleId, final CharSequence message)
		{
			final ProgressDialog progressDialog = new ProgressDialog(activity);
			progressDialog.setTitle(titleId);
			progressDialog.setMessage(message);
			progressDialog.setIndeterminate(true);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setCancelable(true);
			// until task is available
			progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getString(R.string.action_abort), (dialog, which) -> dialog.dismiss());
			progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, activity.getString(R.string.action_dismiss), (dialog, which) -> dialog.dismiss());
			return progressDialog;
		}
	}

	/**
	 * Byte count to string
	 *
	 * @param count byte count
	 * @return string
	 */
	@NonNull
	static private String countToString(final long count, final CharSequence unit)
	{
		return NumberFormat.getNumberInstance(Locale.US).format(count) + ' ' + unit;
	}

	static private final String[] UNITS = {"B", "KB", "MB", "GB"};

	/**
	 * Byte count to string
	 *
	 * @param count byte count
	 * @return string
	 */
	@NonNull
	static private String countToStorageString(final long count)
	{
		if (count > 0)
		{
			float unit = 1024F * 1024F * 1024F;
			for (int i = 3; i >= 0; i--)
			{
				if (count >= unit)
				{
					return String.format(Locale.ENGLISH, "%.1f %s", count / unit, UNITS[i]);
				}

				unit /= 1024;
			}
		}
		else if (count == 0)
		{
			return "0 Byte";
		}
		return "[n/a]";
	}
}
