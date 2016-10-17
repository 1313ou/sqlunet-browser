package org.sqlunet.browser.config;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import org.sqlunet.browser.R;
import org.sqlunet.settings.Storage;
import org.sqlunet.settings.StorageStyle;

public class StorageActivity extends Activity
{
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// layout
		setContentView(R.layout.activity_storage);
	}

	protected void onResume()
	{
		super.onResume();

		// db
		final TextView db = (TextView) findViewById(R.id.database);
		db.setText(Storage.getSqlUNetStorage(getBaseContext()).getAbsolutePath());

		// storage
		final TextView storage = (TextView) findViewById(R.id.storage);
		storage.setText(StorageStyle.reportStyledCandidateStorage(getBaseContext()));

		// storage devices
		final TextView storageDevices = (TextView) findViewById(R.id.storage_devices);
		storageDevices.setText(StorageStyle.reportExternalStorage(getBaseContext()));
	}
}
