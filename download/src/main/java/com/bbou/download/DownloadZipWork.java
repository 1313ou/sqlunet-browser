/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.download;

import android.content.Context;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.WorkerParameters;

/**
 * Download worker
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DownloadZipWork extends DownloadWork
{
	private static final String ARG_ENTRY = "from_entry";

	// S T A R T

	static public class DownloadZipWorker extends DownloadWork.DownloadWorker
	{
		public DownloadZipWorker(@NonNull final Context context, @NonNull final WorkerParameters params)
		{
			super(context, params);
		}

		@Override
		public void onStopped()
		{
			super.onStopped();

			// use this callback as the signal for your worker to cancel its ongoing work
			if (this.delegate != null)
			{
				this.delegate.cancel();
			}
		}

		@NonNull
		@Override
		public Result doWork()
		{
			// retrieve params
			Data inData = this.getInputData();
			String fromUrl = inData.getString(ARG_FROM);
			String entry = inData.getString(ARG_ENTRY);
			String toFile = inData.getString(ARG_TO);
			String renameFrom = inData.getString(ARG_RENAME_FROM);
			String renameTo = inData.getString(ARG_RENAME_TO);

			// do the work
			this.delegate = new DownloadZipCore(progressConsumer);
			try
			{
				DownloadCore.DownloadData outData = delegate.work(fromUrl, toFile, renameFrom, renameTo, entry);
				Data outputData = new Data.Builder() //
						.putString(ARG_FROM, outData.fromUrl) //
						.putString(ARG_TO, outData.toFile) //
						.putString(EXCEPTION, null) //
						.putLong(SIZE, outData.size) //
						.putLong(PROGRESS, outData.size) //
						.putLong(TOTAL, outData.size) //
						.putLong(DATE, outData.date) //
						.putString(ETAG, outData.etag) //
						.putString(VERSION, outData.version) //
						.putString(STATIC_VERSION, outData.staticVersion) //
						.build();
				return Result.success(outputData);
			}
			catch (Exception e)
			{
				Data outputData = new Data.Builder() //
						.putString(EXCEPTION, e.getMessage()) //
						.putString(EXCEPTION_CAUSE, e.getCause() != null ? e.getCause().getMessage() : null) //
						.putLong(PROGRESS, 0) //
						.putLong(TOTAL, 0) //
						.build();
				return Result.failure(outputData);
			}
		}
	}

	/**
	 * Enqueuing work with observer
	 *
	 * @param context    context
	 * @param fromUrl    url to download
	 * @param toFile     file to save
	 * @param renameFrom rename source
	 * @param renameTo   rename destinaation
	 * @param owner      lifecycle owner
	 * @param observer   observer
	 * @return work uuid
	 */
	public static UUID startWork(@NonNull final Context context, @NonNull final String fromUrl, @Nullable final String entry, @NonNull final String toFile, @Nullable final String renameFrom, @Nullable final String renameTo, @NonNull final LifecycleOwner owner, @NonNull final Observer<WorkInfo> observer)
	{
		final WorkManager wm = WorkManager.getInstance(context);

		// request
		final Data data = new Data.Builder() //
				.putString(ARG_FROM, fromUrl) //
				.putString(ARG_ENTRY, entry) //
				.putString(ARG_TO, toFile) //
				.putString(ARG_RENAME_FROM, renameFrom) //
				.putString(ARG_RENAME_TO, renameTo) //
				.build();
		final WorkRequest downloadRequest = new OneTimeWorkRequest.Builder(DownloadZipWork.DownloadZipWorker.class) //
				.setInputData(data) //
				.addTag(WORKER_TAG) //
				.build();
		final UUID uuid = downloadRequest.getId();

		// observe
		observe(context, uuid, owner, observer);

		// run
		wm.enqueue(downloadRequest);
		return uuid;
	}
}
