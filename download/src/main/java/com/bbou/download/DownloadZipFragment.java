/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */

package com.bbou.download;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Download Zip Work fragment
 * Interface between work and activity.
 * Cancel messages are to be sent to this fragment's receiver.
 * Signals completion through the OnComplete callback in the activity.
 * This fragment uses a zipped file zip downloader core (only matched
 * entries or all by default) are written to target directory location.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DownloadZipFragment extends DownloadFragment
{
	/**
	 * Zip entry argument
	 */
	static public final String DOWNLOAD_ENTRY_ARG = "entry";

	/**
	 * Download source entry
	 */
	@SuppressWarnings("WeakerAccess")
	@Nullable
	protected String sourceEntry;

	@Override
	protected int getResId()
	{
		return R.layout.fragment_zip_download;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// arguments
		final Bundle arguments = getArguments();
		this.sourceEntry = arguments == null ? null : arguments.getString(DOWNLOAD_ENTRY_ARG);

		// download source data
		assert this.sourceUrl != null;
		if (this.sourceUrl.endsWith(".zip"))
		{
			this.sourceUrl = this.sourceUrl.substring(0, this.sourceUrl.length() - 4);
		}
		assert this.downloadUrl != null;
		if (!this.downloadUrl.endsWith(".zip"))
		{
			this.downloadUrl += ".zip";
		}
	}

	/**
	 * Start download
	 */
	@Override
	protected void start()
	{
		synchronized (this)
		{
			Log.d(TAG, "Starting");
			if (!this.downloading) // prevent recursion
			{
				// reset
				this.success = null;
				this.cancel = false;
				this.exception = null;
				this.cause = null;
				this.progressDownloaded = 0;
				this.progressTotal = 0;

				// args
				final String from = this.downloadUrl;
				assert from != null;
				assert this.downloadedFile != null;
				final String to = this.downloadedFile.getAbsolutePath();
				final String entry = this.sourceEntry;

				// start job
				start(from, entry, to);

				// status
				this.downloading = true; // set
				return;
			}
		}
		throw new RuntimeException("Already downloading");
	}

	/**
	 * Start
	 *
	 * @param fromUrl source zip url
	 * @param entry   source zip entry
	 * @param toFile  destination file
	 */
	protected void start(@NonNull final String fromUrl, @Nullable final String entry, @NonNull final String toFile)
	{
		this.uuid = DownloadZipWork.startWork(requireContext(), fromUrl, entry, toFile, this, this.observer);
	}
}


