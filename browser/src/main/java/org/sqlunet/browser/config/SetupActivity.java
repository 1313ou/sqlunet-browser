package org.sqlunet.browser.config;

import org.sqlunet.browser.R;
import org.sqlunet.download.Downloader;
import org.sqlunet.settings.StorageSettings;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SetupActivity extends SetupBaseActivity
{
	@SuppressWarnings("unused")
	private static final String TAG = "SetupActivity"; //$NON-NLS-1$

	// download db button
	private Button downloadDbButton;

	@SuppressWarnings("boxing")
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// layout
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
			@SuppressWarnings("synthetic-access")
			@Override
			public void onClick(final View v)
			{
				SetupActivity.this.downloadDbButton.setEnabled(false);

				// starting download
				final String from = StorageSettings.getDbDownloadSource(getBaseContext());
				final String to = StorageSettings.getDbDownloadTarget(getBaseContext());
				SetupActivity.this.task = new Downloader(from, to, 0, SetupActivity.this).execute();
			}
		});
	}
}
