package org.sqlunet.browser.config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.sqlunet.browser.R;
import org.sqlunet.settings.Settings;

/**
 * Download activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DownloadActivity extends Activity implements DownloadFragment.DownloadListener
{
	/**
	 * Log tag
	 */
	// static private final String TAG = "DownloadActivity";

	private enum Downloader
	{
		DOWNLOADMANAGER(R.layout.activity_download),
		BASIC(R.layout.activity_download_basic);

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

	/**
	 * Result extra
	 */
	static private final String RESULT_DOWNLOAD_DATA_AVAILABLE = "download_data_available";

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		final Downloader downloader = Downloader.getFromPref(this);

		// content
		setContentView(downloader.res);

		// set this as listener
		BaseDownloadFragment downloadFragment = (BaseDownloadFragment) getFragmentManager().findFragmentById(R.id.fragment_download);
		downloadFragment.setListener(this);
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
