/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.config;

import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import org.sqlunet.browser.common.R;
import org.sqlunet.download.DownloadActivity;
import org.sqlunet.settings.StorageSettings;

import java.util.Collection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static org.sqlunet.download.BaseDownloadFragment.DOWNLOAD_FROM_ARG;
import static org.sqlunet.download.BaseDownloadFragment.DOWNLOAD_TO_ARG;

/**
 * Manage tasks
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SetupDatabaseTasks
{
	static private final String TAG = "SetupDatabaseTasks";

	/**
	 * Create database
	 *
	 * @param context      context
	 * @param databasePath path
	 * @return true if successful
	 */
	static public boolean createDatabase(@NonNull final Context context, final String databasePath)
	{
		try
		{
			final SQLiteDatabase db = context.openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);
			db.close();
			return true;
		}
		catch (Exception e)
		{
			Log.e(TAG, "While creating database", e);
		}
		return false;
	}

	/**
	 * Delete database
	 *
	 * @param context      context
	 * @param databasePath path
	 * @return true if successful
	 */
	@SuppressWarnings("deprecation")
	static public boolean deleteDatabase(@NonNull final Context context, final String databasePath)
	{
		// make sure you close all connections before deleting
		final String[] authorities = context.getResources().getStringArray(R.array.provider_authorities);
		for (String authority : authorities)
		{
			final ContentProviderClient client = context.getContentResolver().acquireContentProviderClient(authority);
			assert client != null;
			final ContentProvider provider = client.getLocalContentProvider();
			assert provider != null;
			provider.shutdown();
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
			{
				client.release();
			}
			else
			{
				client.close();
			}
		}

		// delete
		boolean result = context.deleteDatabase(databasePath);
		Log.d(TAG, "Dropping database: " + result);
		return result;
	}

	/**
	 * Update data
	 *
	 * @param context context
	 */
	static public void update(@NonNull final Context context)
	{
		final boolean success = SetupDatabaseTasks.deleteDatabase(context, StorageSettings.getDatabasePath(context));
		if (success)
		{
			final Intent intent = new Intent(context, DownloadActivity.class);
			intent.putExtra(DOWNLOAD_FROM_ARG, StorageSettings.getDbDownloadSource(context));
			intent.putExtra(DOWNLOAD_TO_ARG, StorageSettings.getDbDownloadTarget(context));
			context.startActivity(intent);
		}
	}

	/**
	 * Drop tables
	 *
	 * @param context      context
	 * @param databasePath path
	 * @param tables       tables to drop
	 */
	@SuppressWarnings("unused")
	static public void dropAll(@NonNull final Context context, final String databasePath, @Nullable final Collection<String> tables)
	{
		if (tables != null && !tables.isEmpty())
		{
			final SQLiteDatabase db = context.openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);
			for (final String table : tables)
			{
				db.execSQL("DROP TABLE IF EXISTS " + table);
				Log.d(TAG, table + ": dropped");
			}
			db.close();
		}
	}

	/**
	 * Flush tables
	 *
	 * @param context      context
	 * @param databasePath path
	 * @param tables       tables to flush
	 */
	@SuppressWarnings("unused")
	static public void flushAll(@NonNull final Context context, final String databasePath, @Nullable final Collection<String> tables)
	{
		if (tables != null && !tables.isEmpty())
		{
			final SQLiteDatabase db = context.openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);
			for (final String table : tables)
			{
				int deletedRows = db.delete(table, null, null);
				Log.d(TAG, table + ": deleted " + deletedRows + " rows");
			}
			db.close();
		}
	}
}
