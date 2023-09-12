/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.download;

import android.content.Context;

import java.util.UUID;
import java.util.function.BiConsumer;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Download worker
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DownloadWork
{
	public static final String WORKER_TAG = "download";
	public static final String ARG_FROM = "from_url";
	public static final String ARG_TO = "to_file";
	public static final String ARG_RENAME_FROM = "rename_from";
	public static final String ARG_RENAME_TO = "rename_to";
	static final String EXCEPTION = "download_exception";
	static final String EXCEPTION_CAUSE = "download_exception_cause";
	static final String PROGRESS = "download_progress";
	static final String TOTAL = "download_total";
	static final String SIZE = "download_size";
	static final String ETAG = "download_etag";
	static final String DATE = "download_date";
	static final String VERSION = "download_version";
	static final String STATIC_VERSION = "download_static_version";

	// W O R K

	static public class DownloadWorker extends Worker
	{
		/**
		 * Delegate
		 */
		protected DownloadCore delegate;

		/**
		 * Progress consumer
		 */
		protected final BiConsumer<Long, Long> progressConsumer = (downloaded, total) -> setProgressAsync(new Data.Builder() //
				.putLong(PROGRESS, downloaded) //
				.putLong(TOTAL, total) //
				.build());

		public DownloadWorker(@NonNull final Context context, @NonNull final WorkerParameters params)
		{
			super(context, params);
			setProgressAsync(new Data.Builder().putInt(PROGRESS, 0).build());
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
		@WorkerThread
		public Result doWork()
		{
			// retrieve params
			final Data inData = this.getInputData();
			String fromUrl = inData.getString(ARG_FROM);
			String toFile = inData.getString(ARG_TO);
			String renameFrom = inData.getString(ARG_RENAME_FROM);
			String renameTo = inData.getString(ARG_RENAME_TO);

			// do the work
			this.delegate = new DownloadCore(progressConsumer);
			try
			{
				DownloadCore.DownloadData outData = this.delegate.work(fromUrl, toFile, renameFrom, renameTo, null);
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

	// O B S E R V E

	public static void observe(@NonNull final Context context, @NonNull final UUID id, @NonNull final LifecycleOwner owner, @NonNull final Observer<WorkInfo> observer)
	{
		LiveData<WorkInfo> live = WorkManager.getInstance(context).getWorkInfoByIdLiveData(id);
		live.observe(owner, observer);
	}

	// S T A R T

	/**
	 * Enqueuing work with observer
	 *
	 * @param context  context
	 * @param fromUrl  url to download
	 * @param toFile   file to save
	 * @param owner    lifecycle owner
	 * @param observer observer
	 * @return work uuid
	 */
	public static UUID startWork(@NonNull final Context context, @NonNull final String fromUrl, @NonNull final String toFile, @NonNull final LifecycleOwner owner, @NonNull final Observer<WorkInfo> observer)
	{
		final WorkManager wm = WorkManager.getInstance(context);

		// request
		final Data data = new Data.Builder() //
				.putString(ARG_FROM, fromUrl) //
				.putString(ARG_TO, toFile) //
				.build();
		final WorkRequest downloadRequest = new OneTimeWorkRequest.Builder(DownloadWorker.class) //
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

	/**
	 * Cancel work
	 *
	 * @param context context
	 * @param uuid    work uuid
	 */
	public static void stopWork(@NonNull final Context context, @NonNull final UUID uuid)
	{
		WorkManager.getInstance(context).cancelWorkById(uuid);
	}
}
