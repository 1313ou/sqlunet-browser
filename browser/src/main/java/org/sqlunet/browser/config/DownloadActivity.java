package org.sqlunet.browser.config;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

import org.sqlunet.browser.R;

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

	/**
	 * Result extra
	 */
	static private final String RESULT_DOWNLOAD_DATA_AVAILABLE = "download_data_available";

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_download);

		// set this as listener
		DownloadFragment downloadFragment = (DownloadFragment) getFragmentManager().findFragmentById(R.id.fragment_download);
		downloadFragment.setListener(this);
	}

	@Override
	public void onDone(boolean result)
	{
		// return result
		final Intent resultIntent = new Intent();
		resultIntent.putExtra(DownloadActivity.RESULT_DOWNLOAD_DATA_AVAILABLE, result);
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}
}
