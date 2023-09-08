/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.download;

import android.app.Activity;
import android.util.Log;

import org.sqlunet.deploy.Deploy;
import org.sqlunet.download.R;
import org.sqlunet.concurrency.ObservedDelegatingTask;
import org.sqlunet.concurrency.Task;
import org.sqlunet.concurrency.TaskDialogObserver;
import org.sqlunet.concurrency.TaskObserver;

import java.io.File;

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

	private Runnable requestNew;

	private Runnable requestKill;

	public void setRequestNew(final Runnable requestNew)
	{
		this.requestNew = requestNew;
	}

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
			String modelDir = Settings.getModelDir(requireContext());
			this.unzipDir = modelDir == null ? null : new File(modelDir);
		}
		if (this.unzipDir == null)
		{
			Log.e(TAG, "Null model dir, aborting deployment");
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
						// record
						FileData.recordModel(requireContext(), BaseDownloadFragment.this.downloadedFile);

						// cleanup
						//noinspection ResultOfMethodCallIgnored
						BaseDownloadFragment.this.downloadedFile.delete();
					}

					// new model
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
		Settings.recordModel(requireContext(), this.downloadedFile);
	}
}
