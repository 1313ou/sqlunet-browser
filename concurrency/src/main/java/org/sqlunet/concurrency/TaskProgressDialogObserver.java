/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.concurrency;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Task DialogProgress observer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@Deprecated
@SuppressWarnings({"WeakerAccess", "deprecation"})
public class TaskProgressDialogObserver<Progress extends Number> extends TaskObserver.BaseObserver<Progress>
{
	static private final String TAG = "TaskDPObserver";

	@Nullable
	private Cancelable task;

	@NonNull
	private final ProgressDialog progressDialog;

	/**
	 * Constructor
	 *
	 * @param activity activity
	 * @param title    title
	 * @param message  message
	 */
	public TaskProgressDialogObserver(@NonNull final Activity activity, @NonNull final CharSequence title, @NonNull final CharSequence message)
	{
		this.progressDialog = makeDialog(activity, title, message);
	}

	@Override
	public void taskStart(@NonNull final Cancelable task)
	{
		super.taskStart(task);
		this.task = task;
		this.progressDialog.setMessage("");
		this.progressDialog.setIndeterminate(true);
		this.progressDialog.show();
	}

	@Override
	public void taskProgress(@NonNull final Progress progress, @NonNull final Progress length, @Nullable String unit)
	{
		super.taskProgress(progress, length, unit);
		final long longLength = length.longValue();
		final long longProgress = progress.longValue();
		final boolean indeterminate = longLength == -1L;
		this.progressDialog.setIndeterminate(indeterminate);
		if (indeterminate)
		{
			this.progressDialog.setProgressNumberFormat(null);
			this.progressDialog.setProgressPercentFormat(null);
		}
		this.progressDialog.setMessage(TaskObserver.countToString(longProgress, longLength, unit));
		if (!indeterminate)
		{
			//final int percent = (int) ((longProgress * 100F) / longLength);
			this.progressDialog.setMax((int) longLength);
			this.progressDialog.setProgress((int) longProgress);
		}
	}

	@Override
	public void taskUpdate(@NonNull final CharSequence status)
	{
		super.taskUpdate(status);
		this.progressDialog.setMessage(status);
	}

	@SuppressWarnings("UnusedReturnValue")
	@Override
	public void taskFinish(boolean result)
	{
		super.taskFinish(result);
		this.progressDialog.dismiss();
	}

	@NonNull
	@Override
	public TaskObserver.Observer<Progress> setTitle(@NonNull final CharSequence title)
	{
		super.setTitle(title);
		this.progressDialog.setTitle(title);
		return this;
	}

	@NonNull
	@Override
	public TaskObserver.Observer<Progress> setMessage(@NonNull final CharSequence message)
	{
		super.setMessage(message);
		this.progressDialog.setMessage(message);
		return this;
	}

	/**
	 * Make dialog
	 *
	 * @param activity activity
	 * @param title    title
	 * @param message  message
	 * @return dialog
	 */
	@NonNull
	private ProgressDialog makeDialog(@NonNull final Activity activity, @NonNull final CharSequence title, @NonNull final CharSequence message)
	{
		final ProgressDialog progressDialog = new ProgressDialog(activity);
		progressDialog.setTitle(title);
		progressDialog.setMessage(message);
		progressDialog.setIndeterminate(true);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setCancelable(true);
		progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, activity.getString(R.string.action_dismiss), (dialog, which) -> dialog.dismiss());
		progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getString(R.string.action_abort), (dialog, which) -> {

			if (which == DialogInterface.BUTTON_NEGATIVE)
			{
				boolean result = TaskProgressDialogObserver.this.task != null && TaskProgressDialogObserver.this.task.cancel(true);
				Log.d(TAG, "Cancel task @" + Integer.toHexString(this.task == null ? 0 : this.task.hashCode()) + ' ' + result);
				dialog.dismiss();
			}
		});
		return progressDialog;
	}
}
