package org.sqlunet.download;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.sqlunet.download.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
		DOWNLOAD_MANAGER(R.layout.activity_download);

		final private int res;

		Downloader(int res)
		{
			this.res = res;
		}

		static Downloader getFromPref(final Context context)
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
	protected void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		final Downloader downloader = Downloader.getFromPref(this);

		// content
		setContentView(downloader.res);

		// toolbar
		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// set up the action bar
		final ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);

		if (savedInstanceState == null)
		{
			// set this as listener
			BaseDownloadFragment downloadFragment = null;
			switch (downloader)
			{
				case SIMPLE_SERVICE:
					downloadFragment = new SimpleDownloadServiceFragment();
					break;

				case DOWNLOAD_MANAGER:
					downloadFragment = new DownloadFragment();
					break;
			}
			downloadFragment.setArguments(getIntent().getExtras());
			downloadFragment.setListener(this);

			getSupportFragmentManager() //
					.beginTransaction() //
					.replace(R.id.container, downloadFragment) //
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
