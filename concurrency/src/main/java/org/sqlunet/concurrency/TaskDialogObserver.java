/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.concurrency;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

/**
 * Task dialog observer
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("WeakerAccess")
public class TaskDialogObserver<Progress extends Number> extends TaskObserver.BaseObserver<Progress>
{
	static private final String TAG = "TaskDObserver";

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
	public TaskDialogObserver(@NonNull final FragmentManager fragmentManager, @NonNull final Context appContext, @StringRes final int titleId, @StringRes final int messageId, @StringRes final int unitId)
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
	public TaskDialogObserver(@NonNull final FragmentManager fragmentManager, @NonNull final CharSequence title, @NonNull final CharSequence message, @Nullable final CharSequence unit)
	{
		super();
		this.fragmentManager = fragmentManager;
		this.progressDialogFragment = makeDialogFragment(title, message);
		this.unit = unit;
	}

	@Override
	public void taskStart(@NonNull final Cancelable task)
	{
		super.taskStart(task);
		this.progressDialogFragment.setTask(task);
		if (!this.fragmentManager.isDestroyed())
		{
			this.progressDialogFragment.show(this.fragmentManager, "tag");
		}
	}

	@Override
	public void taskProgress(@NonNull final Progress progress, @NonNull final Progress length)
	{
		super.taskProgress(progress, length);
		final long longLength = length.longValue();
		final long longProgress = progress.longValue();
		final boolean indeterminate = longLength == -1L;
		this.progressDialogFragment.progressBar.setIndeterminate(indeterminate);
		String strProgress = (this.unit != null ? TaskObserver.countToString(longProgress, this.unit) : TaskObserver.countToStorageString(longProgress));
		if (longLength != -1L)
		{
			String strLength = (this.unit != null ? TaskObserver.countToString(longLength, this.unit) : TaskObserver.countToStorageString(longLength));
			strProgress += " / " + strLength;
		}
		this.progressDialogFragment.progressTextView.setText(strProgress);
		if (!indeterminate)
		{
			final int percent = (int) ((longProgress * 100F) / longLength);
			this.progressDialogFragment.progressBar.setMax(100);
			this.progressDialogFragment.progressBar.setProgress(percent);
		}
	}

	@Override
	public void taskUpdate(@NonNull final String message)
	{
		super.taskUpdate(message);
		this.progressDialogFragment.messageTextView.setText(message);
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

		private TextView messageTextView;

		private TextView progressTextView;

		private ProgressBar progressBar;

		@Nullable
		private Cancelable task;

		public ProgressDialogFragment(@NonNull final CharSequence title, @NonNull final CharSequence message)
		{
			this.title = title;
			this.message = message;
		}

		public void setTask(@NonNull final Cancelable task)
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
			this.messageTextView = view.findViewById(R.id.progressMessage);
			this.progressTextView = view.findViewById(R.id.progressProgress);
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
