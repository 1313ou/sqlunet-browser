/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.download;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.util.Pair;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.sqlunet.Deploy;
import org.sqlunet.concurrency.Task;
import org.sqlunet.concurrency.TaskObserver;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * File async tasks
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FileAsyncTask
{
	// static private final String TAG = "FileAsyncTask";

	/**
	 * MD5Downloader listener
	 */
	@FunctionalInterface
	public interface ResultListener
	{
		/**
		 * Done
		 *
		 * @param result file/md5 digest/etc
		 */
		void onResult(final Object result);
	}

	/**
	 * Task listener
	 */
	final private TaskObserver.Listener listener;

	/**
	 * Result listener
	 */
	final private ResultListener resultListener;

	/**
	 * Publish rate
	 */
	private final int publishRate;

	/**
	 * Constructor
	 *
	 * @param listener       listener
	 * @param resultListener result listener
	 * @param publishRate    publish rate
	 */
	@SuppressWarnings("WeakerAccess")
	protected FileAsyncTask(final TaskObserver.Listener listener, final ResultListener resultListener, @SuppressWarnings("SameParameterValue") final int publishRate)
	{
		this.listener = listener;
		this.resultListener = resultListener;
		this.publishRate = publishRate;
	}

	// CORE

	static private class AsyncCopyFromFile extends Task<String, Long, Boolean> implements Deploy.Publisher
	{
		/**
		 * Task listener
		 */
		final private TaskObserver.Listener listener;

		/**
		 * Result listener
		 */
		final private ResultListener resultListener;

		/**
		 * Publish rate
		 */
		private final int publishRate;

		/**
		 * Constructor
		 *
		 * @param listener       listener
		 * @param resultListener result listener
		 * @param publishRate    public rate
		 */
		AsyncCopyFromFile(final TaskObserver.Listener listener, final ResultListener resultListener, final int publishRate)
		{
			this.listener = listener;
			this.resultListener = resultListener;
			this.publishRate = publishRate;
		}

		@NonNull
		@SuppressWarnings("boxing")
		@Override
		protected Boolean job(final String... params)
		{
			String srcFileArg = params[0];
			String destFileArg = params[1];

			// outsource it to deploy
			return Deploy.copyFromFile(srcFileArg, destFileArg, this, this, this.publishRate);
		}

		@Override
		protected void onPre()
		{
			this.listener.taskStart(this);
		}

		@Override
		protected void onProgress(final Long... params)
		{
			this.listener.taskUpdate(params[0], params[1]);
		}

		@Override
		protected void onJobComplete(final Boolean result)
		{
			this.listener.taskFinish(result);
			if (this.resultListener != null)
			{
				this.resultListener.onResult(result);
			}
		}

		@Override
		protected void onCancelled()
		{
			this.listener.taskFinish(false);
		}

		@Override
		public void publish(long current, long total)
		{
			pushProgress(current, total);
		}
	}

	static private class AsyncUnzipFromArchive extends Task<String, Long, Boolean> implements Deploy.Publisher
	{
		/**
		 * Task listener
		 */
		final private TaskObserver.Listener listener;

		/**
		 * Result listener
		 */
		final private ResultListener resultListener;

		/**
		 * Publish rate
		 */
		private final int publishRate;

		/**
		 * Constructor
		 *
		 * @param listener       listener
		 * @param resultListener result listener
		 * @param publishRate    public rate
		 */
		AsyncUnzipFromArchive(final TaskObserver.Listener listener, final ResultListener resultListener, final int publishRate)
		{
			this.listener = listener;
			this.resultListener = resultListener;
			this.publishRate = publishRate;
		}

		@NonNull
		@Override
		@SuppressWarnings("boxing")
		protected Boolean job(final String... params)
		{
			String srcArchiveArg = params[0];
			String destDirArg = params[1];

			// outsource it to deploy
			return Deploy.unzipFromArchive(srcArchiveArg, destDirArg, this, this, this.publishRate);
		}

		@Override
		protected void onPre()
		{
			this.listener.taskStart(this);
		}

		@Override
		protected void onProgress(final Long... params)
		{
			this.listener.taskUpdate(params[0], params[1]);
		}

		@Override
		protected void onJobComplete(final Boolean result)
		{
			this.listener.taskFinish(result);
			if (this.resultListener != null)
			{
				this.resultListener.onResult(result);
			}
		}

		@Override
		protected void onJobCancelled(final Boolean result)
		{
			this.listener.taskFinish(false);
		}

		@Override
		public void publish(long current, long total)
		{
			pushProgress(current, total);
		}
	}

	static private class AsyncUnzipEntryFromArchive extends Task<String, Long, Boolean> implements Deploy.Publisher
	{
		/**
		 * Task listener
		 */
		final private TaskObserver.Listener listener;

		/**
		 * Result listener
		 */
		final private ResultListener resultListener;

		/**
		 * Publish rate
		 */
		private final int publishRate;

		/**
		 * Constructor
		 *
		 * @param listener       listener
		 * @param resultListener result listener
		 * @param publishRate    public rate
		 */
		AsyncUnzipEntryFromArchive(final TaskObserver.Listener listener, final ResultListener resultListener, final int publishRate)
		{
			this.listener = listener;
			this.resultListener = resultListener;
			this.publishRate = publishRate;
		}

		@NonNull
		@Override
		@SuppressWarnings("boxing")
		protected Boolean job(final String... params)
		{
			String srcArchiveArg = params[0];
			String srcEntryArg = params[1];
			String destFileArg = params[2];

			// outsource it to deploy
			return Deploy.unzipEntryFromArchive(srcArchiveArg, srcEntryArg, destFileArg, this, this, this.publishRate);
		}

		@Override
		protected void onPre()
		{
			this.listener.taskStart(this);
		}

		@Override
		protected void onProgress(final Long... params)
		{
			this.listener.taskUpdate(params[0], params[1]);
		}

		@Override
		protected void onJobComplete(final Boolean result)
		{
			this.listener.taskFinish(result);
			if (this.resultListener != null)
			{
				this.resultListener.onResult(result);
			}
		}

		@Override
		protected void onJobCancelled(final Boolean result)
		{
			this.listener.taskFinish(false);
		}

		@Override
		public void publish(long current, long total)
		{
			pushProgress(current, total);
		}
	}

	static private class AsyncMd5FromFile extends Task<String, Long, String> implements Deploy.Publisher
	{
		/**
		 * Task listener
		 */
		final private TaskObserver.Listener listener;

		/**
		 * Result listener
		 */
		final private ResultListener resultListener;

		/**
		 * Publish rate
		 */
		private final int publishRate;

		/**
		 * Constructor
		 *
		 * @param listener       listener
		 * @param resultListener result listener
		 * @param publishRate    public rate
		 */
		AsyncMd5FromFile(final TaskObserver.Listener listener, final ResultListener resultListener, final int publishRate)
		{
			this.listener = listener;
			this.resultListener = resultListener;
			this.publishRate = publishRate;
		}

		@Nullable
		@Override
		protected String job(final String... params)
		{
			String srcFileArg = params[0];

			// outsource it to deploy
			return Deploy.md5FromFile(srcFileArg, this, this, this.publishRate);
		}

		@Override
		protected void onPre()
		{
			this.listener.taskStart(this);
		}

		@Override
		protected void onProgress(final Long... params)
		{
			this.listener.taskUpdate(params[0], params[1]);
		}

		@Override
		protected void onJobCancelled(final String result)
		{
			this.listener.taskFinish(false);
			if (this.resultListener != null)
			{
				this.resultListener.onResult(null);
			}
		}

		@Override
		protected void onJobComplete(@Nullable final String result)
		{
			this.listener.taskFinish(result != null);
			if (this.resultListener != null)
			{
				this.resultListener.onResult(result);
			}
		}

		@Override
		public void publish(long current, long total)
		{
			pushProgress(current, total);
		}
	}

	// HELPERS

	/**
	 * Copy from source file
	 *
	 * @param src  source file
	 * @param dest dest file
	 */
	@NonNull
	@SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
	public Task<String, Long, Boolean> copyFromFile(final String src, final String dest)
	{
		final Task<String, Long, Boolean> task = new AsyncCopyFromFile(this.listener, this.resultListener, this.publishRate);
		task.run(src, dest);
		return task;
	}

	/**
	 * Expand entry from zipfile
	 *
	 * @param srcArchive zip file path
	 * @param srcEntry   entry
	 * @param dest       dest file
	 */
	@SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
	public Task<String, Long, Boolean> unzipEntryFromArchive(final String srcArchive, final String srcEntry, final String dest)
	{
		final Task<String, Long, Boolean> task = new AsyncUnzipEntryFromArchive(this.listener, this.resultListener, this.publishRate);
		return task.run(srcArchive, srcEntry, dest);
	}

	/**
	 * Expand all from zipfile
	 *
	 * @param srcArchive zip file path
	 * @param dest       dest file
	 */
	@SuppressWarnings("UnusedReturnValue")
	public Task<String, Long, Boolean> unzipFromArchive(final String srcArchive, final String dest)
	{
		final Task<String, Long, Boolean> task = new AsyncUnzipFromArchive(this.listener, this.resultListener, this.publishRate);
		return task.run(srcArchive, dest);
	}

	/**
	 * Md5 check sum of file
	 *
	 * @param targetFile file path
	 */
	@SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
	public Task<String, Long, String> md5FromFile(final String targetFile)
	{
		final Task<String, Long, String> task = new AsyncMd5FromFile(this.listener, this.resultListener, this.publishRate);
		return task.run(targetFile);
	}

	// DIALOG

	/**
	 * Unzip data base from
	 *
	 * @param activity     activity
	 * @param containerDir container dir
	 * @param databasePath database path
	 */
	static public void unzipFromArchive(@NonNull final Activity activity, final String containerDir, final String databasePath)
	{
		final AlertDialog.Builder alert = new AlertDialog.Builder(activity); // unguarded, level 1
		alert.setTitle(R.string.action_unzip_from_archive);
		alert.setMessage(R.string.hint_unzip_from_archive);
		final EditText input = new EditText(activity);
		String fromPath = containerDir;
		if (fromPath != null)
		{
			fromPath += File.separatorChar + Settings.HINT_DB_ZIP;
			input.setText(fromPath);
			input.setSelection(fromPath.length());
		}
		alert.setView(input);
		alert.setPositiveButton(R.string.action_ok, (dialog, whichButton) -> {

			final String sourceFile = input.getText().toString();
			if (activity.isFinishing() || activity.isDestroyed())
			{
				return;
			}
			final TaskObserver.Listener listener = new TaskObserver.DialogListener(activity, R.string.action_unzip_from_archive, sourceFile, null); // guarded, level 2
			final ResultListener resultListener = result -> {
				final Boolean success = (Boolean) result;
				if (success)
				{
					FileData.recordDatabase(activity, new File(sourceFile));
				}
			};
			new FileAsyncTask(listener, resultListener, 1000).unzipFromArchive(sourceFile, databasePath);
		});
		alert.setNegativeButton(R.string.action_cancel, (dialog, whichButton) -> {
			// canceled.
		});
		alert.show();
	}

	/**
	 * Unzip data base from
	 *
	 * @param activity     activity
	 * @param containerDir container dir
	 * @param zipEntry     zip entry
	 * @param databasePath database path
	 */
	static public void unzipEntryFromArchive(@NonNull final Activity activity, final String containerDir, final String zipEntry, final String databasePath)
	{
		final AlertDialog.Builder alert = new AlertDialog.Builder(activity); // unguarded, level 1
		alert.setTitle(R.string.action_unzip_from_archive);
		alert.setMessage(R.string.hint_unzip_from_archive);
		final EditText input = new EditText(activity);
		String fromPath = containerDir;
		if (fromPath != null)
		{
			fromPath += File.separatorChar + Settings.HINT_DB_ZIP;
			input.setText(fromPath);
			input.setSelection(fromPath.length());
		}
		alert.setView(input);
		alert.setPositiveButton(R.string.action_ok, (dialog, whichButton) -> {

			final String sourceFile = input.getText().toString();

			if (activity.isFinishing() || activity.isDestroyed())
			{
				return;
			}
			final TaskObserver.Listener listener = new TaskObserver.DialogListener(activity, R.string.action_unzip_from_archive, sourceFile, null); // guarded, level 2
			final ResultListener resultListener = result -> {
				final Boolean success = (Boolean) result;
				if (success)
				{
					FileData.recordDatabase(activity, new File(sourceFile));
				}
			};
			new FileAsyncTask(listener, resultListener, 1000).unzipEntryFromArchive(sourceFile, zipEntry, databasePath);
		});
		alert.setNegativeButton(R.string.action_cancel, (dialog, whichButton) -> {
			// canceled.
		});
		alert.show();
	}

	/**
	 * Copy data base from file in cache
	 *
	 * @param activity     activity
	 * @param containerDir container dir
	 * @param databasePath database path
	 */
	static public void copyFromFile(@NonNull final Activity activity, final String containerDir, final String databasePath)
	{
		final AlertDialog.Builder alert = new AlertDialog.Builder(activity); // unguarded, level 1
		alert.setTitle(R.string.action_copy_from_file);
		alert.setMessage(R.string.hint_copy_from_file);
		final EditText input = new EditText(activity);
		String fromPath = containerDir;
		if (fromPath != null)
		{
			fromPath += File.separatorChar + Settings.HINT_DB_ZIP;
			input.setText(fromPath);
			input.setSelection(fromPath.length());
		}
		alert.setView(input);
		alert.setPositiveButton(R.string.action_ok, (dialog, whichButton) -> {
			final String sourceFile = input.getText().toString();
			if (activity.isFinishing() || activity.isDestroyed())
			{
				return;
			}
			final TaskObserver.Listener listener = new TaskObserver.DialogListener(activity, R.string.action_copy_from_file, sourceFile, null); // guarded, level 2
			final ResultListener resultListener = result -> {
				final Boolean success = (Boolean) result;
				if (success)
				{
					FileData.recordDatabase(activity, new File(sourceFile));
				}
			};
			new FileAsyncTask(listener, resultListener, 1000).copyFromFile(sourceFile, databasePath);
		});
		alert.setNegativeButton(R.string.action_cancel, (dialog, whichButton) -> {
			// canceled.
		});
		alert.show();
	}

	/**
	 * MD5
	 *
	 * @param activity       activity
	 * @param path           path
	 * @param resultListener result listener
	 */
	static public void md5(@NonNull final Activity activity, final String path, final ResultListener resultListener)
	{
		if (activity.isFinishing() || activity.isDestroyed())
		{
			return;
		}
		final TaskObserver.Listener listener = new TaskObserver.DialogListener(activity, R.string.action_md5, path, null); // guarded, level > 1
		new FileAsyncTask(listener, resultListener, 1000).md5FromFile(path);
	}

	static public void md5(@NonNull final Activity activity)
	{
		final Pair<CharSequence[], CharSequence[]> result = StorageReports.getStyledCachesNamesValues(activity);
		final CharSequence[] names = result.first;
		final CharSequence[] values = result.second;
		final Pair<CharSequence[], CharSequence[]> result2 = StorageReports.getStorageDirectoriesNamesValues(activity);
		final CharSequence[] names2 = result2.first;
		final CharSequence[] values2 = result2.second;

		int candidateCount = 0;
		final RadioGroup input = new RadioGroup(activity);
		for (int i = 0; i < names.length && i < values.length; i++)
		{
			//final CharSequence name = names[i];
			final CharSequence value = values[i];
			final String dirValue = value.toString();
			final File dir = new File(dirValue);
			if (!dir.exists())
			{
				continue;
			}
			final File[] files = dir.listFiles((dir1, name) -> name.matches(".*\\.zip"));
			if (files == null)
			{
				continue;
			}
			for (File file : files)
			{
				if (file.exists())
				{
					//final SpannableStringBuilder sb = new SpannableStringBuilder();
					//Report.appendHeader(sb, name.toString().split("\n")[0]).append('\n').append(file.getAbsolutePath());
					final RadioButton radioButton = new RadioButton(activity);
					radioButton.setText(file.getAbsolutePath());
					radioButton.setTag(file.getAbsolutePath());
					input.addView(radioButton);
					candidateCount++;
				}
			}
		}
		for (int i = 0; i < names2.length && i < values2.length; i++)
		{
			//final CharSequence name = names2[i];
			final CharSequence value = values2[i];
			final String dirValue = value.toString();
			final File dir = new File(dirValue);
			if (!dir.exists())
			{
				continue;
			}
			final File[] files = dir.listFiles((dir1, name) -> name.matches(".*\\.zip"));
			if (files == null)
			{
				continue;
			}
			for (File file : files)
			{
				if (file.exists())
				{
					//final SpannableStringBuilder sb = new SpannableStringBuilder();
					//Report.appendHeader(sb, name.toString().split("\n")[0]).append('\n').append(file.getAbsolutePath());
					final RadioButton radioButton = new RadioButton(activity);
					radioButton.setText(file.getAbsolutePath());
					radioButton.setTag(file.getAbsolutePath());
					input.addView(radioButton);
					candidateCount++;
				}
			}
		}
		if (candidateCount == 0)
		{
			Toast.makeText(activity, R.string.md5_none, Toast.LENGTH_SHORT).show();
			return;
		}

		final AlertDialog.Builder alert = new AlertDialog.Builder(activity); // unguarded, level 1
		alert.setTitle(R.string.action_md5_ask);
		alert.setMessage(R.string.hint_md5);
		alert.setView(input);
		alert.setPositiveButton(R.string.action_ok, (dialog, whichButton) -> {
			dialog.dismiss();

			//final String sourceFile = input0.getText();
			//final String sourceFile = input.getSelectedItem().toString();
			int childCount = input.getChildCount();
			for (int i = 0; i < childCount; i++)
			{
				final RadioButton radioButton = (RadioButton) input.getChildAt(i);
				if (radioButton.getId() == input.getCheckedRadioButtonId())
				{
					final String sourceFile = radioButton.getTag().toString();
					if (new File(sourceFile).exists())
					{
						FileAsyncTask.md5(activity, sourceFile, result1 -> {

							if (!activity.isFinishing() && !activity.isDestroyed())
							{

								final String computedResult = (String) result1;
								final SpannableStringBuilder sb = new SpannableStringBuilder();
								Report.appendHeader(sb, activity.getString(R.string.md5_computed));
								sb.append('\n');
								sb.append(computedResult == null ? activity.getString(R.string.status_task_failed) : computedResult);

								// selectable
								final TextView resultView = new TextView(activity);
								resultView.setText(sb);
								resultView.setPadding(35, 20, 35, 20);
								resultView.setTextIsSelectable(true);

								new AlertDialog.Builder(activity) // guarded, level 2
										.setTitle(activity.getString(R.string.action_md5_of) + ' ' + sourceFile) //
										.setView(resultView)
										//.setMessage(sb)
										.show();
							}
						});
					}
					else
					{
						final AlertDialog.Builder alert2 = new AlertDialog.Builder(activity); // unguarded, level 1
						alert2.setTitle(sourceFile) //
								.setMessage(activity.getString(R.string.status_error_no_file)) //
								.show();
					}
				}
			}
		});
		alert.setNegativeButton(R.string.action_cancel, (dialog, whichButton) -> {
			// canceled.
		});
		alert.show();
	}
}
