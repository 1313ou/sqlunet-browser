package org.sqlunet.browser.config;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.sqlunet.browser.R;
import org.sqlunet.settings.StorageSettings;

/**
 * Set up activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SetupActivity extends SetupBaseActivity
{
	// static private final String TAG = "SetupActivity";
	// download db button
	private Button downloadDbButton;

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// content
		setContentView(R.layout.activity_setup);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		// source db
		TextView sourceDb = (TextView) findViewById(R.id.db);
		sourceDb.setText(StorageSettings.getDbDownloadSource(getBaseContext()));

		// download db button
		this.downloadDbButton = (Button) findViewById(R.id.downloadDb);
		this.downloadDbButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				SetupActivity.this.downloadDbButton.setEnabled(false);

				// starting download
				final String from = StorageSettings.getDbDownloadSource(getBaseContext());
				final String to = StorageSettings.getDbDownloadTarget(getBaseContext());
				SetupActivity.this.task = new BasicDownloader(from, to, 0, SetupActivity.this).execute();
			}
		});
	}
}
