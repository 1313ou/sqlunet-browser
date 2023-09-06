/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.download;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

/**
 * Download Zip fragment
 * Interface between service and activity.
 * Service sends messages to this fragment's receiver.
 * Signals completion through the OnComplete callback in the activity.
 * Source and stream are zipped.
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DownloadZipFragment extends DownloadFragment
{
	@Override
	protected int getResId()
	{
		return R.layout.fragment_zip_download;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

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

	@Override
	protected void startService(@NonNull Context context, @NonNull Intent intent)
	{
		DownloadZipService.enqueueWork(context, intent);
	}

	/**
	 * Action fro this service
	 */
	@NonNull
	@Override
	protected String getAction()
	{
		return Killer.KILL_ZIP_DOWNLOAD_SERVICE;
	}
}


