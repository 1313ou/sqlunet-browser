package org.sqlunet.browser.config;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.sqlunet.browser.common.R;
import org.sqlunet.settings.StorageUtils;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Task observer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class TaskObserver
{
	static private final String TAG = "TaskObserver";

	/**
	 * Manager listener
	 */
	public interface Listener
	{
		/**
		 * Start event
		 */
		void taskStart(final AsyncTask<?, ?, ?> task);

		/**
		 * Finish event
		 *
		 * @param result progressMessage
		 */
		void taskFinish(boolean result);

		/**
		 * Intermediate update event
		 *
		 * @param progress progress value
		 * @param length   length
		 */
		void taskUpdate(int progress, int length);
	}

	/**
	 * Base listener
	 */
	static abstract class BaseListener implements TaskObserver.Listener
	{
		final Context context;

		BaseListener(final Context context)
		{
			this.context = context;
		}

		@Override
		public void taskStart(final AsyncTask<?, ?, ?> task)
		{
			Log.d(TAG, "Task start");
		}

		@SuppressWarnings("WeakerAccess")
		@Override
		public void taskUpdate(int progress, int length)
		{
			Log.d(TAG, "Task " + progress + '/' + length);
		}

		@SuppressWarnings("WeakerAccess")
		@Override
		public void taskFinish(boolean result)
		{
			Log.d(TAG, "Task " + (result ? "succeeded" : "failed"));
		}
	}

	/**
	 * Toast listener
	 */
	static class ToastListener extends BaseListener
	{
		ToastListener(final Context context)
		{
			super(context);
		}

		@SuppressWarnings("WeakerAccess")
		@Override
		public void taskStart(final AsyncTask<?, ?, ?> task)
		{
			super.taskStart(task);
			Toast.makeText(this.context, R.string.status_task_start_toast, Toast.LENGTH_SHORT).show();
		}

		@SuppressWarnings("WeakerAccess")
		@Override
		public void taskFinish(boolean result)
		{
			super.taskFinish(result);
			Toast.makeText(this.context, R.string.status_task_done_toast, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Toast listener
	 */
	static public class ToastWithStatusListener extends ToastListener
	{
		final private TextView status;

		public ToastWithStatusListener(final Context context, final TextView status)
		{
			super(context);
			this.status = status;
		}

		@Override
		public void taskStart(final AsyncTask<?, ?, ?> task)
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
	static class ProgressListener extends ToastListener
	{
		private final ProgressBar progress;

		/**
		 * Constructor
		 *
		 * @param context  context
		 * @param progress progress bar
		 */
		ProgressListener(final Context context, final ProgressBar progress)
		{
			super(context);
			this.progress = progress;
		}

		@Override
		public void taskStart(AsyncTask<?, ?, ?> task)
		{
			super.taskStart(task);
			this.progress.setIndeterminate(true);
		}

		@Override
		public void taskUpdate(int progress, int length)
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
		 * @param context   context
		 * @param titleId   titleId id
		 * @param messageId message id
		 * @param unitId    unit id
		 */
		@SuppressWarnings("unused")
		DialogListener(@NonNull final Context context, final int titleId, final int messageId, final int unitId)
		{
			this(context, titleId, context.getString(messageId), context.getString(unitId));
		}

		/**
		 * Constructor
		 *
		 * @param context context
		 * @param titleId titleId id
		 * @param message message
		 * @param unit    unit
		 */
		public DialogListener(@NonNull final Context context, final int titleId, final CharSequence message, final CharSequence unit)
		{
			super(context);
			this.progressDialog = makeDialog(context, titleId, message);
			this.unit = unit;
		}

		@Override
		public void taskStart(@NonNull final AsyncTask<?, ?, ?> task)
		{
			super.taskStart(task);
			this.progressDialog.show();
			this.progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, this.context.getString(R.string.action_abort), (dialog, which) -> {
				if (which == DialogInterface.BUTTON_NEGATIVE)
				{
					boolean result = task.cancel(true);
					Log.d(TAG, "Cancel task " + task + ' ' + result);
					dialog.dismiss();
				}
			});
		}

		@Override
		public void taskUpdate(int progress, int length)
		{
			super.taskUpdate(progress, length);
			final boolean indeterminate = length == -1;
			this.progressDialog.setIndeterminate(indeterminate);
			if (indeterminate)
			{
				this.progressDialog.setProgressNumberFormat(null);
				this.progressDialog.setProgressPercentFormat(null);
			}
			final String message = this.unit != null ? countToString(progress, this.unit) : StorageUtils.countToStorageString(progress);
			this.progressDialog.setMessage(message);
			if (!indeterminate)
			{
				final int percent = (int) ((progress * 100F) / length);
				this.progressDialog.setMax(100);
				this.progressDialog.setProgress(percent);
			}
		}

		@Override
		public void taskFinish(boolean result)
		{
			super.taskFinish(result);
			this.progressDialog.dismiss();
		}

		/**
		 * Make dialog
		 *
		 * @param context context
		 * @param titleId titleId id
		 * @param message message
		 * @return dialog
		 */
		@NonNull
		static private ProgressDialog makeDialog(@NonNull final Context context, final int titleId, final CharSequence message)
		{
			final ProgressDialog progressDialog = new ProgressDialog(context);
			progressDialog.setTitle(titleId);
			progressDialog.setMessage(message);
			progressDialog.setIndeterminate(true);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setCancelable(true);
			// until task is available
			progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.action_abort), (dialog, which) -> dialog.dismiss());
			progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.action_dismiss), (dialog, which) -> dialog.dismiss());
			return progressDialog;
		}
	}

	/**
	 * Byte count to string
	 *
	 * @param count byte count
	 * @return string
	 */
	static private String countToString(final int count, final CharSequence unit)
	{
		return NumberFormat.getNumberInstance(Locale.US).format(count) + ' ' + unit;
	}
}
