/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.download;

import android.app.Activity;
import android.app.AlertDialog;

import org.sqlunet.concurrency.Task;
import org.sqlunet.concurrency.TaskDialogObserver;
import org.sqlunet.concurrency.TaskObserver;
import org.sqlunet.deploy.ObservedDeploy;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

/**
 * File async tasks
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FileAsyncTask
{
	// static private final String TAG = "FileAsyncTask";

	/**
	 * Result listener interface
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
	 * Task observer
	 */
	final private TaskObserver.Observer<Number> observer;

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
	 * @param observer       observer
	 * @param resultListener result listener
	 * @param publishRate    publish rate
	 */
	@SuppressWarnings("WeakerAccess")
	protected FileAsyncTask(final TaskObserver.Observer<Number> observer, final ResultListener resultListener, @SuppressWarnings("SameParameterValue") final int publishRate)
	{
		this.observer = observer;
		this.resultListener = resultListener;
		this.publishRate = publishRate;
	}

	// CORE

	static private class AsyncCopyFromFile extends Task<String, Number, Boolean> implements ObservedDeploy.Publisher
	{
		/**
		 * Task observer
		 */
		final private TaskObserver.Observer<Number> observer;

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
		 * @param observer       observer
		 * @param resultListener result listener
		 * @param publishRate    public rate
		 */
		AsyncCopyFromFile(final TaskObserver.Observer<Number> observer, final ResultListener resultListener, final int publishRate)
		{
			this.observer = observer;
			this.resultListener = resultListener;
			this.publishRate = publishRate;
		}

		@NonNull
		@SuppressWarnings("boxing")
		@Override
		protected Boolean doInBackground(final String... params)
		{
			String srcFileArg = params[0];
			String destFileArg = params[1];

			// outsource it to deploy
			return ObservedDeploy.copyFromFile(srcFileArg, destFileArg, this, this, this.publishRate);
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			this.observer.taskStart(this);
		}

		@Override
		protected void onProgressUpdate(final Number... params)
		{
			super.onProgressUpdate(params);
			this.observer.taskProgress(params[0], params[1]);
		}

		@Override
		protected void onPostExecute(final Boolean result)
		{
			this.observer.taskFinish(result);
			if (this.resultListener != null)
			{
				this.resultListener.onResult(result);
			}
		}

		@Override
		protected void onCancelled()
		{
			this.observer.taskFinish(false);
		}

		@Override
		public void publish(long current, long total)
		{
			publishProgress(current, total);
		}
	}

	static private class AsyncUnzipFromArchive extends Task<String, Number, Boolean> implements ObservedDeploy.Publisher
	{
		/**
		 * Task observer
		 */
		final private TaskObserver.Observer<Number> observer;

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
		 * @param observer       observer
		 * @param resultListener result listener
		 * @param publishRate    public rate
		 */
		AsyncUnzipFromArchive(final TaskObserver.Observer<Number> observer, final ResultListener resultListener, final int publishRate)
		{
			this.observer = observer;
			this.resultListener = resultListener;
			this.publishRate = publishRate;
		}

		@NonNull
		@Override
		@SuppressWarnings("boxing")
		protected Boolean doInBackground(final String... params)
		{
			String srcArchiveArg = params[0];
			String destDirArg = params[1];

			// outsource it to deploy
			return ObservedDeploy.unzipFromArchive(srcArchiveArg, destDirArg, this, this, this.publishRate);
		}

		@Override
		protected void onPreExecute()
		{
			this.observer.taskStart(this);
		}

		@Override
		protected void onProgressUpdate(final Number... params)
		{
			this.observer.taskProgress(params[0], params[1]);
		}

		@Override
		protected void onPostExecute(final Boolean result)
		{
			this.observer.taskFinish(result);
			if (this.resultListener != null)
			{
				this.resultListener.onResult(result);
			}
		}

		@Override
		protected void onCancelled(final Boolean result)
		{
			this.observer.taskFinish(false);
		}

		@Override
		public void publish(long current, long total)
		{
			publishProgress(current, total);
		}
	}

	static private class AsyncUnzipEntryFromArchive extends Task<String, Number, Boolean> implements ObservedDeploy.Publisher
	{
		/**
		 * Task observer
		 */
		final private TaskObserver.Observer<Number> observer;

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
		 * @param observer       observer
		 * @param resultListener result listener
		 * @param publishRate    public rate
		 */
		AsyncUnzipEntryFromArchive(final TaskObserver.Observer<Number> observer, final ResultListener resultListener, final int publishRate)
		{
			this.observer = observer;
			this.resultListener = resultListener;
			this.publishRate = publishRate;
		}

		@NonNull
		@Override
		@SuppressWarnings("boxing")
		protected Boolean doInBackground(final String... params)
		{
			String srcArchiveArg = params[0];
			String srcEntryArg = params[1];
			String destFileArg = params[2];

			// outsource it to deploy
			return ObservedDeploy.unzipEntryFromArchive(srcArchiveArg, srcEntryArg, destFileArg, this, this, this.publishRate);
		}

		@Override
		protected void onPreExecute()
		{
			this.observer.taskStart(this);
		}

		@Override
		protected void onProgressUpdate(final Number... params)
		{
			this.observer.taskProgress(params[0], params[1]);
		}

		@Override
		protected void onPostExecute(final Boolean result)
		{
			this.observer.taskFinish(result);
			if (this.resultListener != null)
			{
				this.resultListener.onResult(result);
			}
		}

		@Override
		protected void onCancelled(final Boolean result)
		{
			this.observer.taskFinish(false);
		}

		@Override
		public void publish(long current, long total)
		{
			publishProgress(current, total);
		}
	}

	static private class AsyncMd5FromFile extends Task<String, Number, String> implements ObservedDeploy.Publisher
	{
		/**
		 * Task observer
		 */
		final private TaskObserver.Observer<Number> observer;

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
		 * @param observer       observer
		 * @param resultListener result listener
		 * @param publishRate    public rate
		 */
		AsyncMd5FromFile(final TaskObserver.Observer<Number> observer, final ResultListener resultListener, final int publishRate)
		{
			this.observer = observer;
			this.resultListener = resultListener;
			this.publishRate = publishRate;
		}

		@Nullable
		@Override
		protected String doInBackground(final String... params)
		{
			String srcFileArg = params[0];

			// outsource it to deploy
			return ObservedDeploy.md5FromFile(srcFileArg, this, this, this.publishRate);
		}

		@Override
		protected void onPreExecute()
		{
			this.observer.taskStart(this);
		}

		@Override
		protected void onProgressUpdate(final Number... params)
		{
			this.observer.taskProgress(params[0], params[1]);
		}

		@Override
		protected void onCancelled(final String result)
		{
			this.observer.taskFinish(false);
			if (this.resultListener != null)
			{
				this.resultListener.onResult(null);
			}
		}

		@Override
		protected void onPostExecute(@Nullable final String result)
		{
			this.observer.taskFinish(result != null);
			if (this.resultListener != null)
			{
				this.resultListener.onResult(result);
			}
		}

		@Override
		public void publish(long current, long total)
		{
			publishProgress(current, total);
		}
	}

	// HELPERS

	/**
	 * Copy from source file
	 */
	@NonNull
	@SuppressWarnings("WeakerAccess")
	public Task<String, Number, Boolean> copyFromFile()
	{
		return new AsyncCopyFromFile(this.observer, this.resultListener, this.publishRate);
	}

	/**
	 * Expand entry from zipfile
	 */
	@NonNull
	@SuppressWarnings("WeakerAccess")
	public Task<String, Number, Boolean> unzipEntryFromArchive()
	{
		return new AsyncUnzipEntryFromArchive(this.observer, this.resultListener, this.publishRate);
	}

	/**
	 * Expand all from zipfile
	 */
	@NonNull
	@SuppressWarnings("UnusedReturnValue")
	public Task<String, Number, Boolean> unzipFromArchive()
	{
		return new AsyncUnzipFromArchive(this.observer, this.resultListener, this.publishRate);
	}

	/**
	 * Md5 check sum of file
	 */
	@NonNull
	@SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
	public Task<String, Number, String> md5FromFile()
	{
		return new AsyncMd5FromFile(this.observer, this.resultListener, this.publishRate);
	}

	// L A U N C H E R S

	public static void launchUnzip(@NonNull final FragmentActivity activity, @NonNull final String sourceFile, @NonNull final String databasePath, @Nullable final Runnable whenDone)
	{
		final TaskObserver.Observer<Number> observer = new TaskDialogObserver<>(activity.getSupportFragmentManager(), null) // guarded, level 2
				.setTitle(activity.getString(R.string.action_unzip_from_archive)) //
				.setMessage(sourceFile);
		final FileAsyncTask.ResultListener resultListener = result -> {

			final Boolean success = (Boolean) result;
			if (success)
			{
				Settings.recordDb(activity, new File(sourceFile));
				if (whenDone != null)
				{
					whenDone.run();
				}
			}
		};
		final Task<String, Number, Boolean> ft = new FileAsyncTask(observer, resultListener, 1000).unzipFromArchive();
		ft.execute(sourceFile, databasePath);
	}

	public static void launchUnzip(@NonNull final FragmentActivity activity, @NonNull final String sourceFile, @NonNull final String zipEntry, @NonNull final String databasePath, @Nullable final Runnable whenDone)
	{
		final TaskObserver.Observer<Number> observer = new TaskDialogObserver<>(activity.getSupportFragmentManager(), null) // guarded, level 2
				.setTitle(activity.getString(R.string.action_unzip_from_archive)) //
				.setMessage(sourceFile);
		final FileAsyncTask.ResultListener resultListener = result -> {

			final Boolean success = (Boolean) result;
			if (success)
			{
				Settings.recordDb(activity, new File(sourceFile));
				if (whenDone != null)
				{
					whenDone.run();
				}
			}
		};
		final Task<String, Number, Boolean> ft = new FileAsyncTask(observer, resultListener, 1000).unzipEntryFromArchive();
		ft.execute(sourceFile, zipEntry, databasePath);
	}

	public static void launchCopy(@NonNull final FragmentActivity activity, @NonNull final String sourceFile, @NonNull final String databasePath, @Nullable final Runnable whenDone)
	{
		final TaskObserver.Observer<Number> observer = new TaskDialogObserver<>(activity.getSupportFragmentManager(), null) // guarded, level 2
				.setTitle(activity.getString(R.string.action_copy_from_file)) //
				.setMessage(sourceFile);
		final FileAsyncTask.ResultListener resultListener = result -> {

			final Boolean success = (Boolean) result;
			if (success)
			{
				Settings.recordDb(activity, new File(sourceFile));
			}
			if (whenDone != null)
			{
				whenDone.run();
			}
		};
		final Task<String, Number, Boolean> ft = new FileAsyncTask(observer, resultListener, 1000).copyFromFile();
		ft.execute(sourceFile, databasePath);
	}

	public static void launchMd5(@NonNull final FragmentActivity activity, @NonNull final String sourceFile)
	{
		final TaskObserver.Observer<Number> observer = new TaskDialogObserver<>(activity.getSupportFragmentManager(), null) // guarded, level 2
				.setTitle(activity.getString(R.string.action_md5)) //
				.setMessage(sourceFile);
		final FileAsyncTask.ResultListener resultListener = result -> {

			final String md5 = (String) result;
			final AlertDialog.Builder alert2 = new AlertDialog.Builder(activity); // unguarded, level 1
			if (md5 != null)
			{
				alert2.setMessage(md5);
			}
			else
			{
				alert2.setMessage(R.string.result_fail);
			}
			alert2.show();
		};
		final Task<String, Number, String> ft = new FileAsyncTask(observer, resultListener, 1000).md5FromFile();
		ft.execute(sourceFile);
	}

	// L A U N C H E R S  W I T H  O B S E R V E R S

	public static void launchUnzip2(@NonNull final Activity activity, @NonNull final TaskObserver.Observer<Number> observer, @NonNull final String sourceFile, @NonNull final String zipEntry, @NonNull final String databasePath, @Nullable final Runnable whenDone)
	{
		final FileAsyncTask.ResultListener resultListener = result -> {

			final Boolean success = (Boolean) result;
			if (success)
			{
				Settings.recordDb(activity, new File(sourceFile));
				if (whenDone != null)
				{
					whenDone.run();
				}
			}
		};
		final Task<String, Number, Boolean> ft = new FileAsyncTask(observer, resultListener, 1000).unzipEntryFromArchive();
		ft.execute(sourceFile, zipEntry, databasePath);
	}
}
