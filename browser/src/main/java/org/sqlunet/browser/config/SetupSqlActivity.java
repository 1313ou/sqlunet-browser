package org.sqlunet.browser.config;

import java.io.File;

import org.sqlunet.browser.R;
import org.sqlunet.download.Downloader;
import org.sqlunet.provider.ExecuteManager;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.settings.StorageUtils;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SetupSqlActivity extends SetupBaseActivity
{
	private static final String TAG = "SetupSqlActivity"; //$NON-NLS-1$

	// download sql button
	private Button downloadSqlButton;

	// import button
	private Button importButton;

	// pm button
	private Button pmButton;

	// index button
	private Button indexButton;

	@SuppressWarnings("boxing")
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// layout
		setContentView(R.layout.activity_setup_sql);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		// cache
		TextView cache_storage = (TextView) findViewById(R.id.cache_storage);

		// sql
		final String sql = StorageSettings.getSqlSource(getBaseContext());

		// source
		TextView source = (TextView) findViewById(R.id.source);
		source.setText(StorageSettings.getSqlDownloadSource(getBaseContext()));

		// target
		TextView target = (TextView) findViewById(R.id.target);
		target.setText(StorageSettings.getSqlDownloadTarget(this));

		// sql import
		TextView sqlImport = (TextView) findViewById(R.id.sql_import);
		sqlImport.setText(sql + '!' + StorageSettings.getImportEntry(getBaseContext()));

		// sql import
		TextView sqlPm = (TextView) findViewById(R.id.sql_pm);
		sqlPm.setText(sql + '!' + StorageSettings.getPmEntry(getBaseContext()));

		// sql index
		TextView sqlIndex = (TextView) findViewById(R.id.sql_index);
		sqlIndex.setText(sql + '!' + StorageSettings.getIndexEntry(getBaseContext()));

		// cache
		final String cacheDir = StorageSettings.getCacheDir(this);
		final float[] cacheStats = StorageUtils.storageStats(cacheDir);
		final float ca = cacheStats[0];
		final float cc = cacheStats[1];
		final float cp = cacheStats[2];
		final String storageData = getResources().getString(R.string.title_storage_data);
		cache_storage.setText(String.format(storageData, cacheDir, ca, cc, cp));

		// buttons
		this.downloadSqlButton = (Button) findViewById(R.id.downloadSql);
		this.importButton = (Button) findViewById(R.id.importButton);
		this.pmButton = (Button) findViewById(R.id.pmButton);
		this.indexButton = (Button) findViewById(R.id.indexButton);

		// download sql button
		this.downloadSqlButton.setOnClickListener(new View.OnClickListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public void onClick(final View v)
			{
				SetupSqlActivity.this.downloadSqlButton.setEnabled(false);
				SetupSqlActivity.this.importButton.setEnabled(false);
				SetupSqlActivity.this.indexButton.setEnabled(false);

				// starting download
				final String from = StorageSettings.getSqlDownloadSource(getBaseContext());
				final String to = StorageSettings.getSqlDownloadTarget(getBaseContext());
				SetupSqlActivity.this.task = new Downloader(from, to, 1, SetupSqlActivity.this).execute();
			}
		});

		// import button
		this.importButton.setOnClickListener(new View.OnClickListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public void onClick(final View v)
			{
				// starting create task
				try
				{
					SetupSqlActivity.this.task = new ExecuteManager(StorageSettings.getDatabasePath(getBaseContext()), SetupSqlActivity.this, 1000).executeFromArchive(StorageSettings.getSqlSource(getBaseContext()),
							StorageSettings.getImportEntry(getBaseContext()));
				}
				catch (final Exception e)
				{
					Log.e(TAG, "While importing", e); //$NON-NLS-1$
				}
			}
		});

		// index button
		this.indexButton.setOnClickListener(new View.OnClickListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public void onClick(final View v)
			{
				// starting indexing task
				try
				{
					SetupSqlActivity.this.task = new ExecuteManager(StorageSettings.getDatabasePath(getBaseContext()), SetupSqlActivity.this, 1).executeFromArchive(StorageSettings.getSqlSource(getBaseContext()),
							StorageSettings.getIndexEntry(getBaseContext()));
				}
				catch (final Exception e)
				{
					Log.e(TAG, "While indexing", e); //$NON-NLS-1$
				}
			}
		});

		// pm button
		this.pmButton.setOnClickListener(new View.OnClickListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public void onClick(final View v)
			{
				// starting pm task
				try
				{
					SetupSqlActivity.this.task = new ExecuteManager(StorageSettings.getDatabasePath(getBaseContext()), SetupSqlActivity.this, 1).executeFromArchive(StorageSettings.getSqlSource(getBaseContext()),
							StorageSettings.getPmEntry(getBaseContext()));
				}
				catch (final Exception e)
				{
					Log.e(TAG, "While preparing predicatematrix", e); //$NON-NLS-1$
				}
			}
		});

		if (new File(sql).exists())
		{
			this.importButton.setVisibility(View.VISIBLE);
			this.pmButton.setVisibility(View.VISIBLE);
			this.indexButton.setVisibility(View.VISIBLE);
		}
	}

	// D O W N L O A D L I S T E N E R

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sqlunet.download.Downloader.Listener#downloadFinish(int, boolean)
	 */
	@Override
	public void downloadFinish(final int code, final boolean result)
	{
		super.downloadFinish(code, result);

		// delete sql file
		// if (!result)
		// {
		// String to = StorageSettings.getSqlDownloadTarget(this);
		// new File(to).delete();
		// }

		this.importButton.setVisibility(result ? View.VISIBLE : View.GONE);
		this.pmButton.setVisibility(result ? View.VISIBLE : View.GONE);
		this.indexButton.setVisibility(result ? View.VISIBLE : View.GONE);

		this.importButton.setEnabled(code != 0);
		this.pmButton.setEnabled(code != 0);
		this.indexButton.setEnabled(code != 0);
	}
}
