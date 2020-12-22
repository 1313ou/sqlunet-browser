/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.concurrency;

import android.app.Activity;
import android.app.Dialog;
//import android.app.ProgressDialog;
import android.content.Context;
//import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

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
	public interface Listener<Progress extends Number>
	{
		/**
		 * Start event
		 */
		default void taskStart(@NonNull final Task<?, Progress, ?> task)
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
		default void taskUpdate(@NonNull Progress progress, @NonNull Progress length)
		{
		}
	}

	/**
	 * Base listener
	 */
	static public class BaseListener<Progress extends Number> implements Listener<Progress>
	{
		@SuppressWarnings("WeakerAccess")
		@Override
		public void taskStart(@NonNull final Task<?, Progress, ?> task)
		{
			Log.d(TAG, "Task start");
		}

		@SuppressWarnings("WeakerAccess")
		@Override
		public void taskUpdate(@NonNull Progress progress, @NonNull Progress length)
		{
			// TODO progress observation
			// Log.d(TAG, "Task " + progress + '/' + length);
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
	static public class ToastListener<Progress extends Number> extends BaseListener<Progress>
	{
		/**
		 * Cached context
		 */
		@NonNull
		final protected Context appContext;

		ToastListener(@NonNull final Context appContext)
		{
			super();
			this.appContext = appContext;
		}

		@SuppressWarnings("WeakerAccess")
		@Override
		public void taskStart(@NonNull final Task<?, Progress, ?> task)
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
	static public class ToastWithStatusListener<Progress extends Number> extends ToastListener<Progress>
	{
		@NonNull
		final private TextView status;

		public ToastWithStatusListener(@NonNull final Context appContext, @NonNull final TextView status)
		{
			super(appContext);
			this.status = status;
		}

		@Override
		public void taskStart(@NonNull final Task<?, Progress, ?> task)
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
	static public class ProgressListener<Progress extends Number> extends ToastListener<Progress>
	{
		private final ProgressBar progress;

		/**
		 * Constructor
		 *
		 * @param appContext app context
		 * @param progress   progress bar
		 */
		ProgressListener(@NonNull final Context appContext, final ProgressBar progress)
		{
			super(appContext);
			this.progress = progress;
		}

		@Override
		public void taskStart(@NonNull Task<?, Progress, ?> task)
		{
			super.taskStart(task);
			this.progress.setIndeterminate(true);
		}

		@Override
		public void taskUpdate(@NonNull final Progress progress, @NonNull final Progress length)
		{
			super.taskUpdate(progress, length);
			boolean indeterminate = length.longValue() == -1L;
			this.progress.setIndeterminate(indeterminate);
			if (!indeterminate)
			{
				this.progress.setMax(length.intValue());
				this.progress.setProgress(progress.intValue());
			}
		}

		@Override
		public void taskFinish(boolean result)
		{
			super.taskFinish(result);
			int done = result ? 0 : 100;
			this.progress.setMax(done);
			this.progress.setProgress(done);
		}
	}

	//	/**
	//	 * ProgressDialog listener
	//	 */
	//	static public class ProgressDialogListener<Progress extends Number> extends BaseListener<Progress>
	//	{
	//		@Nullable
	//		private Task<?, ?, ?> task;
	//
	//		@NonNull
	//		private final ProgressDialog progressDialog;
	//
	//		@Nullable
	//		private final CharSequence unit;
	//
	//		/**
	//		 * Constructor
	//		 *
	//		 * @param activity  activity
	//		 * @param titleId   title id
	//		 * @param messageId message id
	//		 * @param unitId    unit id
	//		 */
	//		public ProgressDialogListener(@NonNull final Activity activity, @StringRes final int titleId, @StringRes final int messageId, @StringRes final int unitId)
	//		{
	//			this(activity, activity.getString(titleId), activity.getString(messageId), unitId == 0 ? null : activity.getString(unitId));
	//		}
	//
	//		/**
	//		 * Constructor
	//		 *
	//		 * @param activity activity
	//		 * @param title    title
	//		 * @param message  message
	//		 * @param unit     unit
	//		 */
	//		public ProgressDialogListener(@NonNull final Activity activity, @NonNull final CharSequence title, @NonNull final CharSequence message, @Nullable final CharSequence unit)
	//		{
	//			this.progressDialog = makeDialog(activity, title, message);
	//			this.unit = unit;
	//		}
	//
	//		@Override
	//		public void taskStart(@NonNull final Task<?, Progress, ?> task)
	//		{
	//			super.taskStart(task);
	//			this.task = task;
	//			this.progressDialog.show();
	//		}
	//
	//		@Override
	//		public void taskUpdate(@NonNull final Progress progress, @NonNull final Progress length)
	//		{
	//			super.taskUpdate(progress, length);
	//			final long longLength = length.longValue();
	//			final long longProgress = progress.longValue();
	//			final boolean indeterminate = longLength == -1L;
	//			this.progressDialog.setIndeterminate(indeterminate);
	//			if (indeterminate)
	//			{
	//				this.progressDialog.setProgressNumberFormat(null);
	//				this.progressDialog.setProgressPercentFormat(null);
	//			}
	//			final String message = this.unit != null ? countToString(progress.longValue(), this.unit) : countToStorageString(progress.longValue());
	//          if (longLength != -1L)
	//          {
	//	            message += " / " + longLength;
	//          }
	//			this.progressDialog.setMessage(message);
	//			if (!indeterminate)
	//			{
	//				//final int percent = (int) ((longProgress * 100F) / longLength);
	//				this.progressDialog.setMax((int) longLength);
	//				this.progressDialog.setProgress((int) longProgress);
	//			}
	//		}
	//
	//		@SuppressWarnings("UnusedReturnValue")
	//		@Override
	//		public void taskFinish(boolean result)
	//		{
	//			super.taskFinish(result);
	//			this.progressDialog.dismiss();
	//		}
	//
	//		/**
	//		 * Make dialog
	//		 *
	//		 * @param activity activity
	//		 * @param title    title
	//		 * @param message  message
	//		 * @return dialog
	//		 */
	//		@NonNull
	//		private ProgressDialog makeDialog(@NonNull final Activity activity, @NonNull final CharSequence title, @NonNull final CharSequence message)
	//		{
	//			final ProgressDialog progressDialog = new ProgressDialog(activity);
	//			progressDialog.setTitle(title);
	//			progressDialog.setMessage(message);
	//			progressDialog.setIndeterminate(true);
	//			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	//			progressDialog.setCancelable(true);
	//			progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, activity.getString(R.string.action_dismiss), (dialog, which) -> dialog.dismiss());
	//			progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getString(R.string.action_abort), (dialog, which) -> {
	//
	//				if (which == DialogInterface.BUTTON_NEGATIVE)
	//				{
	//					boolean result = ProgressDialogListener.this.task != null && ProgressDialogListener.this.task.cancel(true);
	//					Log.d(TAG, "Cancel task @" + Integer.toHexString(this.task.hashCode()) + ' ' + result);
	//					dialog.dismiss();
	//				}
	//			});
	//			return progressDialog;
	//		}
	//	}

	/**
	 * Dialog listener
	 */
	static public class DialogListener<Progress extends Number> extends BaseListener<Progress>
	{
		@NonNull
		private final FragmentManager fragmentManager;

		@NonNull
		private final ProgressDialogFragment progressDialogFragment;

		@Nullable
		private final CharSequence unit;

		/**
		 * Constructor
		 *
		 * @param appContext appContext
		 * @param titleId    titleId id
		 * @param messageId  message id
		 * @param unitId     unit id
		 */
		public DialogListener(@NonNull final FragmentManager fragmentManager, @NonNull final Context appContext, @StringRes final int titleId, @StringRes final int messageId, @StringRes final int unitId)
		{
			this(fragmentManager, appContext.getString(titleId), appContext.getString(messageId), unitId == 0 ? null : appContext.getString(unitId));
		}

		/**
		 * Constructor
		 *
		 * @param title   title
		 * @param message message
		 * @param unit    unit
		 */
		public DialogListener(@NonNull final FragmentManager fragmentManager, @NonNull final CharSequence title, @NonNull final CharSequence message, @Nullable final CharSequence unit)
		{
			super();
			this.fragmentManager = fragmentManager;
			this.progressDialogFragment = makeDialogFragment(title, message);
			this.unit = unit;
		}

		@Override
		public void taskStart(@NonNull final Task<?, Progress, ?> task)
		{
			super.taskStart(task);
			this.progressDialogFragment.setTask(task);
			this.progressDialogFragment.show(this.fragmentManager, "tag");
		}

		@Override
		public void taskUpdate(@NonNull final Progress progress, @NonNull final Progress length)
		{
			super.taskUpdate(progress, length);
			final long longLength = length.longValue();
			final long longProgress = progress.longValue();
			final boolean indeterminate = longLength == -1L;
			this.progressDialogFragment.progressBar.setIndeterminate(indeterminate);
			String message = (this.unit != null ? countToString(longProgress, this.unit) : countToStorageString(longProgress));
			if (longLength != -1L)
			{
				message += " / " + longLength;
			}
			this.progressDialogFragment.textView.setText(message);
			if (!indeterminate)
			{
				final int percent = (int) ((longProgress * 100F) / longLength);
				this.progressDialogFragment.progressBar.setMax(100);
				this.progressDialogFragment.progressBar.setProgress(percent);
			}
		}

		@SuppressWarnings("UnusedReturnValue")
		@Override
		public void taskFinish(boolean result)
		{
			super.taskFinish(result);
			this.progressDialogFragment.dismiss();
		}

		/**
		 * Make dialog fragment
		 *
		 * @param title   title id
		 * @param message message
		 * @return dialog
		 */
		@NonNull
		static private ProgressDialogFragment makeDialogFragment(@NonNull final CharSequence title, @NonNull final CharSequence message)
		{
			return new ProgressDialogFragment(title, message);
		}

		public static class ProgressDialogFragment extends DialogFragment
		{
			@NonNull
			private final CharSequence title;

			@NonNull
			private final CharSequence message;

			private TextView textView;

			private ProgressBar progressBar;

			@Nullable
			private Task<?, ?, ?> task;

			public ProgressDialogFragment(@NonNull final CharSequence title, @NonNull final CharSequence message)
			{
				this.title = title;
				this.message = message;
			}

			public void setTask(@NonNull final Task<?, ?, ?> task)
			{
				this.task = task;
			}

			@NonNull
			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState)
			{
				final Activity activity = requireActivity();
				final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				final LayoutInflater inflater = activity.getLayoutInflater();
				final View view = inflater.inflate(R.layout.dialog_progress, null);
				this.progressBar = view.findViewById(R.id.progressBar);
				this.textView = view.findViewById(R.id.progressMessage);
				builder.setView(view);
				builder.setTitle(this.title);
				builder.setMessage(this.message);
				builder.setNegativeButton(R.string.action_cancel, (dialog, whichButton) -> {
					// canceled.
					boolean result = this.task != null && this.task.cancel(true);
					Log.d(TAG, "Cancel task @" + (this.task == null ? "null" : Integer.toHexString(this.task.hashCode())) + ' ' + result);
					this.dismiss();
				});
				return builder.create();
			}
		}
	}

	/**
	 * Byte count to string
	 *
	 * @param count byte count
	 * @return string
	 */
	@NonNull
	static private String countToString(final long count, @NonNull final CharSequence unit)
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
