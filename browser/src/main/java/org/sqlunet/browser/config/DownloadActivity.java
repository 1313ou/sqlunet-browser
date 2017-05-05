package org.sqlunet.browser.config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.sqlunet.browser.R;
import org.sqlunet.settings.Settings;

/**
 * Download activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DownloadActivity extends AppCompatActivity implements DownloadFragment.DownloadListener
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
		SIMPLE_SERVICE(R.layout.activity_download), //
		SIMPLE_ASYNCTASK(R.layout.activity_download_simple), //
		DOWNLOADMANAGER(R.layout.activity_download);

		final private int res;

		Downloader(int res)
		{
			this.res = res;
		}

		static public Downloader getFromPref(final Context context)
		{
			final String preferredDownloader = Settings.getDownloaderPref(context);
			if (preferredDownloader == null)
			{
				return SIMPLE_SERVICE;
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

		if (savedInstanceState == null)
		{
			// set this as listener
			BaseDownloadFragment downloadFragment = null;
			switch (downloader)
			{
				case SIMPLE_ASYNCTASK:
					downloadFragment = new SimpleDownloadFragment();
					break;

				case SIMPLE_SERVICE:
					downloadFragment = new SimpleDownloadServiceFragment();
					break;

				case DOWNLOADMANAGER:
					downloadFragment = new DownloadFragment();
					break;
			}
			downloadFragment.setArguments(getIntent().getExtras());
			downloadFragment.setListener(this);

			getSupportFragmentManager() //
					.beginTransaction() //
					.replace(R.id.container_download, downloadFragment) //
					.commit();
		}
	}

	@Override
	public void onDone(boolean result)
	{
		// return progressMessage
		final Intent resultIntent = new Intent();
		resultIntent.putExtra(DownloadActivity.RESULT_DOWNLOAD_DATA_AVAILABLE, result);
		setResult(result ? Activity.RESULT_OK : Activity.RESULT_CANCELED, resultIntent);
		// finish();
	}
}
