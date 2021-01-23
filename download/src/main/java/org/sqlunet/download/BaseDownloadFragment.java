/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.download;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import org.sqlunet.concurrency.ObservedDelegatingTask;
import org.sqlunet.concurrency.Task;
import org.sqlunet.concurrency.TaskDialogObserver;
import org.sqlunet.concurrency.TaskObserver;
import org.sqlunet.deploy.Deploy;

import java.io.File;

import androidx.annotation.Nullable;

/**
 * Download activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
abstract public class BaseDownloadFragment extends AbstractDownloadFragment
{
	static private final String TAG = "BaseDownloadF";

	/**
	 * Rename from argument
	 */
	static public final String RENAME_FROM_ARG = "rename_from";

	/**
	 * Rename to argument
	 */
	static public final String RENAME_TO_ARG = "rename_to";

	/*
	 * Rename source
	 */
	@Nullable
	private String renameFrom;

	/*
	 * Rename dest
	 */
	@Nullable
	private String renameTo;

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// arguments
		final Bundle arguments = getArguments();
		this.renameFrom = arguments == null ? null : arguments.getString(RENAME_FROM_ARG);
		this.renameTo = arguments == null ? null : arguments.getString(RENAME_TO_ARG);
	}

	/**
	 * Deploy
	 */
	@Override
	protected void deploy()
	{
		// guard against no downloaded file
		if (this.downloadedFile == null)
		{
			Log.e(TAG, "Deploy failure: no downloaded file");
			return;
		}

		// guard against no unzip dir
		if (this.unzipDir == null)
		{
			Log.e(TAG, "Null db dir, aborting deployment");
			return;
		}

		// log
		Log.d(TAG, "Deploy " + this.downloadedFile + '/' + this.renameFrom + " to " + this.unzipDir + '/' + this.renameTo);

		// make sure unzip directory is clean
		Deploy.emptyDirectory(this.unzipDir);

		// observer to proceed with rename of database file, record and cleanup on successful task termination
		final TaskObserver.BaseObserver<Number> observer = new TaskObserver.BaseObserver<Number>() // guarded, level 1
		{
			@Override
			public void taskFinish(final boolean success)
			{
				super.taskFinish(success);

				if (success && BaseDownloadFragment.this.downloadedFile != null)
				{
					if (BaseDownloadFragment.this.renameFrom != null && BaseDownloadFragment.this.renameTo != null && !BaseDownloadFragment.this.renameFrom.equals(BaseDownloadFragment.this.renameTo))
					{
						// rename
						final File renameFromFile = new File(BaseDownloadFragment.this.unzipDir, renameFrom);
						final File renameToFile = new File(BaseDownloadFragment.this.unzipDir, renameTo);
						boolean result2 = renameFromFile.renameTo(renameToFile);
						Log.d(TAG, "Rename " + renameFromFile + " to " + renameToFile + " : " + result2);

						// record
						Settings.recordDb(BaseDownloadFragment.this.appContext, renameToFile);
					}

					// cleanup
					//noinspection ResultOfMethodCallIgnored
					BaseDownloadFragment.this.downloadedFile.delete();
				}

				// finish download activity
				final Activity activity = getActivity();
				if (activity != null)
				{
					activity.finish();
				}
			}
		};

		// unzip as base task
		final Task<String, Number, Boolean> baseTask = new FileAsyncTask(observer, null, 1000).unzipFromArchive();

		// run task
		final Activity activity = getActivity();
		if (activity == null || isDetached() || activity.isFinishing() || activity.isDestroyed())
		{
			// guard against finished/destroyed activity
			baseTask.execute(this.downloadedFile.getAbsolutePath(), this.unzipDir.getAbsolutePath());
		}
		else
		{
			// augment with a dialog observer if fragment is live
			final TaskObserver.Observer<Number> fatObserver = new TaskDialogObserver<>(getParentFragmentManager()) // guarded, level 1
					.setTitle(this.appContext.getString(R.string.action_unzip_from_archive)) //
					.setMessage(this.downloadedFile.getName());
			final Task<String, Number, Boolean> task = new ObservedDelegatingTask<>(baseTask, fatObserver);
			task.execute(this.downloadedFile.getAbsolutePath(), this.unzipDir.getAbsolutePath());
		}
	}

	/**
	 * Record
	 */
	@Override
	protected void record()
	{
		Settings.recordDb(requireContext(), this.downloadedFile);
	}
}
