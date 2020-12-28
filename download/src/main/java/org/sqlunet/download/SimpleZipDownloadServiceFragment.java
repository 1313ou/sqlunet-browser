/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.download;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

/**
 * Download Service fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SimpleZipDownloadServiceFragment extends SimpleDownloadServiceFragment
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
		if (this.sourceUrl.endsWith(".zip"))
		{
			this.sourceUrl = this.sourceUrl.substring(0, this.sourceUrl.length() - 4);
		}
		if (!this.downloadUrl.endsWith(".zip"))
		{
			this.downloadUrl += ".zip";
		}
	}

	@Override
	protected void startService(@NonNull Context context, @NonNull Intent intent)
	{
		SimpleZipDownloaderService.enqueueWork(context, intent);
	}
}

