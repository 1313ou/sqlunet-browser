/*
 * Copyright (c) 2023. Bernard Bou
 */

package com.bbou.concurrency;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

	/**
	 * Constructor
	 *
	 * @param fragmentManager fragment manager
	 */
	public TaskDialogObserver(@NonNull final FragmentManager fragmentManager)
	{
		super();
		this.fragmentManager = fragmentManager;
		this.progressDialogFragment = ProgressDialogFragment.make();
	}

	@Override
	public void taskStart(@NonNull final Cancelable task)
	{
		super.taskStart(task);
		if (!this.fragmentManager.isDestroyed())
		{
			this.progressDialogFragment.setTask(task);
			this.progressDialogFragment.show(this.fragmentManager, "tag");
		}
	}

	@Override
	public void taskProgress(@NonNull final Progress progress, @NonNull final Progress length, @Nullable String unit)
	{
		super.taskProgress(progress, length, unit);
		final long longLength = length.longValue();
		final long longProgress = progress.longValue();
		final boolean indeterminate = longLength == -1L;

		// progress string
		if (this.progressDialogFragment.progressTextView != null)
		{
			this.progressDialogFragment.progressTextView.setText(TaskObserver.countToString(longProgress, longLength, unit));
		}
		// progress
		if (this.progressDialogFragment.progressBar != null)
		{
			this.progressDialogFragment.progressBar.setIndeterminate(indeterminate);
			if (!indeterminate)
			{
				final int percent = (int) ((longProgress * 100F) / longLength);
				this.progressDialogFragment.progressBar.setMax(100);
				this.progressDialogFragment.progressBar.setProgress(percent);
			}
		}
	}

	@Override
	public void taskUpdate(@NonNull final CharSequence status)
	{
		super.taskUpdate(status);
		if (this.progressDialogFragment.statusTextView != null)
		{
			this.progressDialogFragment.statusTextView.setText(status);
		}
	}

	@SuppressWarnings("UnusedReturnValue")
	@Override
	public void taskFinish(boolean result)
	{
		super.taskFinish(result);
		if (this.progressDialogFragment.isAdded() && result)
		{
			this.progressDialogFragment.dismissAllowingStateLoss();
		}
	}

	@NonNull
	@Override
	public TaskObserver.Observer<Progress> setTitle(@NonNull final CharSequence title)
	{
		super.setTitle(title);
		this.progressDialogFragment.setTitle(title);
		return this;
	}

	@NonNull
	@Override
	public TaskObserver.Observer<Progress> setMessage(@NonNull final CharSequence message)
	{
		super.setMessage(message);
		this.progressDialogFragment.setMessage(message);
		return this;
	}

	/**
	 * Dialog fragment
	 */
	public static class ProgressDialogFragment extends DialogFragment
	{
		private TextView progressTextView;

		private ProgressBar progressBar;

		private TextView statusTextView;

		private TextView titleTextView;

		private TextView messageTextView;

		private CharSequence title;

		private CharSequence message;

		@Nullable
		private Cancelable task;

		public ProgressDialogFragment()
		{
		}

		/**
		 * Make dialog fragment
		 *
		 * @return dialog
		 */
		@NonNull
		static private ProgressDialogFragment make()
		{
			return new ProgressDialogFragment();
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
			this.progressTextView = view.findViewById(R.id.progressProgress);
			this.statusTextView = view.findViewById(R.id.status);
			this.titleTextView = view.findViewById(R.id.title);
			this.messageTextView = view.findViewById(R.id.message);
			if (this.title != null)
			{
				this.titleTextView.setText(this.title);
			}
			if (this.message != null)
			{
				this.messageTextView.setText(this.message);
			}
			builder.setView(view);
			builder.setNegativeButton(R.string.action_cancel, (dialog, whichButton) -> {

				// canceled.
				boolean result = this.task != null && this.task.cancel(true);
				Log.d(TAG, "Cancel task @" + (this.task == null ? "null" : Integer.toHexString(this.task.hashCode())) + ' ' + result);
				this.dismiss();
			});
			return builder.create();
		}

		public void setTask(@NonNull final Cancelable task)
		{
			this.task = task;
		}

		public void setTitle(final CharSequence title)
		{
			this.title = title;
			if (this.titleTextView != null)
			{
				this.titleTextView.setText(title);
			}
		}

		public void setMessage(final CharSequence message)
		{
			this.message = message;
			if (this.messageTextView != null)
			{
				this.messageTextView.setText(message);
			}
		}
	}
}
