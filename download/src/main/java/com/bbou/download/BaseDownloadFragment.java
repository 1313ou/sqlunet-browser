/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.download;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.bbou.concurrency.ObservedDelegatingTask;
import com.bbou.concurrency.Task;
import com.bbou.concurrency.TaskDialogObserver;
import com.bbou.concurrency.TaskObserver;
import com.bbou.deploy.Deploy;

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
	static public final String DOWNLOAD_RENAME_FROM_ARG = "rename_from";

	/**
	 * Rename to argument
	 */
	static public final String DOWNLOAD_RENAME_TO_ARG = "rename_to";

	/**
	 * Rename source
	 */
	@Nullable
	protected String renameFrom;

	/**
	 * Rename dest
	 */
	@Nullable
	protected String renameTo;

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// arguments
		final Bundle arguments = getArguments();
		this.renameFrom = arguments == null ? null : arguments.getString(DOWNLOAD_RENAME_FROM_ARG);
		this.renameTo = arguments == null ? null : arguments.getString(DOWNLOAD_RENAME_TO_ARG);
	}

	/**
	 * New data available request runnable, run when new data becomes available
	 */
	private Runnable requestNew;

	/**
	 * Kill old data request runnable, run when old data should be discarded
	 */
	private Runnable requestKill;

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
			String datapackDir = Settings.getDatapackDir(requireContext());
			this.unzipDir = datapackDir == null ? null : new File(datapackDir);
		}
		if (this.unzipDir == null)
		{
			Log.e(TAG, "Null datapack dir, aborting deployment");
			return;
		}

		// log
		Log.d(TAG, "Deploying " + this.downloadedFile + " to " + this.unzipDir);

		// make sure unzip directory is clean
		Deploy.emptyDirectory(this.unzipDir);

		// kill request
		if (requestKill != null)
		{
			requestKill.run();
		}

		// observer to proceed with record, cleanup and broadcast on successful task termination
		final TaskObserver.BaseObserver<Number> observer = new TaskObserver.BaseObserver<Number>() // guarded, level 1
		{
			@Override
			public void taskFinish(final boolean success)
			{
				super.taskFinish(success);

				if (success && BaseDownloadFragment.this.downloadedFile != null)
				{
					// record and delete downloaded file
					// downloadedFile might become null within task
					//noinspection ConstantConditions
					if (BaseDownloadFragment.this.downloadedFile != null)
					{
						if (BaseDownloadFragment.this.renameFrom != null && BaseDownloadFragment.this.renameTo != null && !BaseDownloadFragment.this.renameFrom.equals(BaseDownloadFragment.this.renameTo))
						{
							// rename
							final File renameFromFile = new File(BaseDownloadFragment.this.unzipDir, renameFrom);
							final File renameToFile = new File(BaseDownloadFragment.this.unzipDir, renameTo);
							boolean result2 = renameFromFile.renameTo(renameToFile);
							Log.d(TAG, "Rename " + renameFromFile + " to " + renameToFile + " : " + result2);
						}

						// record
						FileData.recordDatapack(requireContext(), BaseDownloadFragment.this.downloadedFile);

						// cleanup
						//noinspection ResultOfMethodCallIgnored
						BaseDownloadFragment.this.downloadedFile.delete();
					}

					// new datapack
					if (requestNew != null)
					{
						requestNew.run();
					}
				}

				// signal
				onComplete(success);
			}
		};

		// unzip as base task
		final Task<String, Number, Boolean> baseTask = new FileAsyncTask(observer, null, 1000).unzipFromArchiveFile(this.unzipDir.getAbsolutePath());

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
					.setTitle(requireContext().getString(R.string.action_unzip_from_archive)) //
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
		Settings.recordDatapack(requireContext(), this.downloadedFile);
	}
}
