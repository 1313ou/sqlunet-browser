package org.sqlunet.browser.config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import org.sqlunet.browser.R;
import org.sqlunet.settings.Settings;

/**
 * Download activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DownloadActivity extends FragmentActivity implements DownloadFragment.DownloadListener
{
	// static private final String TAG = "DownloadActivity";

	/**
	 * Result extra
	 */
	static private final String RESULT_DOWNLOAD_DATA_AVAILABLE = "download_data_available";

	/**
	 * Downloaders
	 */
	private enum Downloader
	{
		DOWNLOADMANAGER(R.layout.activity_download),
		SIMPLE(R.layout.activity_download_simple);

		final private int res;

		Downloader(int res)
		{
			this.res = res;
		}

		public static Downloader getFromPref(final Context context)
		{
			final String preferredDownloader = Settings.getDownloaderPref(context);
			if (preferredDownloader == null)
			{
				return DOWNLOADMANAGER;
			}
			return Downloader.valueOf(preferredDownloader);
		}
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);


		final Downloader downloader = Downloader.getFromPref(this);

		// content
		setContentView(downloader.res);

		// set this as listener
		BaseDownloadFragment downloadFragment = null;
		switch (downloader)
		{
			case DOWNLOADMANAGER:
				downloadFragment = new DownloadFragment();
				break;

			case SIMPLE:
				downloadFragment = new SimpleDownloadFragment();
				break;
		}
		downloadFragment.setArguments(getIntent().getExtras());
		downloadFragment.setListener(this);

		getSupportFragmentManager() //
				.beginTransaction() //
				.replace(R.id.container_download, downloadFragment) //
				.commit();

	}

	@Override
	public void onDone(boolean result)
	{
		// return progressMessage
		final Intent resultIntent = new Intent();
		resultIntent.putExtra(DownloadActivity.RESULT_DOWNLOAD_DATA_AVAILABLE, result);
		setResult(Activity.RESULT_OK, resultIntent);
		//TODO finish();
	}
}
