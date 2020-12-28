/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser.config;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;

import org.sqlunet.deploy.Deploy;
import org.sqlunet.browser.common.R;
import org.sqlunet.concurrency.Task;
import org.sqlunet.download.Settings;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.settings.StorageUtils;

import java.io.File;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;

@SuppressWarnings("WeakerAccess")
public class Diagnostics
{
	@FunctionalInterface
	interface ResultListener<T>
	{
		void onResult(T t);
	}

	static public class AsyncDiagnostics extends Task<Context, Long, String>
	{
		/**
		 * Result listener
		 */
		final private ResultListener<String> resultListener;

		/**
		 * Constructor
		 *
		 * @param resultListener result listener
		 */
		AsyncDiagnostics(final ResultListener<String> resultListener)
		{
			this.resultListener = resultListener;
		}

		@NonNull
		@Override
		protected String doInBackground(final Context... params)
		{
			final Context context = params[0];
			return report(context);
		}

		@Override
		protected void onPostExecute(final String result)
		{
			if (this.resultListener != null)
			{
				this.resultListener.onResult(result);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@NonNull
	private static String report(@NonNull final Context context)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("DIAGNOSTICS");
		sb.append('\n');
		sb.append('\n');

		final String packageName = context.getApplicationInfo().packageName;
		sb.append(packageName);
		sb.append('\n');

		final PackageInfo pInfo;
		try
		{
			pInfo = context.getPackageManager().getPackageInfo(packageName, 0);
			final long code = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P ? pInfo.getLongVersionCode() : pInfo.versionCode;
			sb.append("version: ");
			sb.append(code);
			sb.append('\n');
		}
		catch (PackageManager.NameNotFoundException e)
		{
			sb.append("package info: ");
			sb.append(e);
			sb.append('\n');
		}

		sb.append("api: ");
		sb.append(Build.VERSION.SDK_INT);
		sb.append(' ');
		sb.append(Build.VERSION.CODENAME);
		sb.append('\n');
		sb.append('\n');

		final String database = StorageSettings.getDatabasePath(context);

		sb.append("path: ");
		sb.append(database);
		sb.append('\n');

		if (!database.isEmpty())
		{
			final File databaseFile = new File(database);
			final boolean databaseExists = databaseFile.exists();

			sb.append("exists: ");
			sb.append(databaseExists);
			sb.append('\n');

			final String parent = databaseFile.getParent();
			final float[] dataStats = StorageUtils.storageStats(parent);
			final float df = dataStats[StorageUtils.STORAGE_FREE];
			final float dc = dataStats[StorageUtils.STORAGE_CAPACITY];
			final float dp = dataStats[StorageUtils.STORAGE_OCCUPANCY];
			sb.append("free: ");
			sb.append(StorageUtils.mbToString(df));
			sb.append('\n');
			sb.append("capacity: ");
			sb.append(StorageUtils.mbToString(dc));
			sb.append('\n');
			sb.append("occupancy: ");
			sb.append(dp);
			sb.append('%');
			sb.append('\n');

			if (databaseExists)
			{
				final boolean databaseIsFile = databaseFile.isFile();
				final long databaseLastModified = databaseFile.lastModified();
				final long databaseSize = databaseFile.length();
				final boolean databaseCanRead = databaseFile.canRead();

				sb.append("is file: ");
				sb.append(databaseIsFile);
				sb.append('\n');

				sb.append("size: ");
				sb.append(databaseSize);
				sb.append('\n');

				sb.append("last modified: ");
				sb.append(databaseLastModified == -1 || databaseLastModified == 0 ? "n/a" : new Date(databaseLastModified).toString());
				sb.append('\n');

				sb.append("can read: ");
				sb.append(databaseCanRead);
				sb.append('\n');

				final String md5 = Deploy.computeDigest(database);
				sb.append("md5: ");
				sb.append(md5 == null ? "null" : md5);
				sb.append('\n');

				sb.append("can open: ");
				boolean databaseCanOpen = false;
				try
				{
					databaseCanOpen = canOpen(database);
					sb.append(databaseCanOpen);
					sb.append('\n');

					final int status = Status.status(context);
					final boolean existsDb = (status & Status.EXISTS) != 0;
					final boolean existsTables = (status & Status.EXISTS_TABLES) != 0;
					if (existsDb)
					{
						sb.append('\n');
						sb.append("tables exist: ");
						sb.append(existsTables);
						sb.append('\n');

						final Resources res = context.getResources();
						final String[] requiredTables = res.getStringArray(R.array.required_tables);
						final String[] requiredIndexes = res.getStringArray(R.array.required_indexes);

						final int requiredPmTablesResId = res.getIdentifier("required_pm", "array", packageName);
						final int requiredTSWnResId = res.getIdentifier("required_texts_wn", "array", packageName);
						final int requiredTSVnResId = res.getIdentifier("required_texts_vn", "array", packageName);
						final int requiredTSPbResId = res.getIdentifier("required_texts_pb", "array", packageName);
						final int requiredTSFnResId = res.getIdentifier("required_texts_fn", "array", packageName);

						final String[] requiredPmTables = requiredPmTablesResId == 0 ? null : res.getStringArray(requiredPmTablesResId);
						final String[] requiredTSWn = requiredTSWnResId == 0 ? null : res.getStringArray(requiredTSWnResId);
						final String[] requiredTSVn = requiredTSVnResId == 0 ? null : res.getStringArray(requiredTSVnResId);
						final String[] requiredTSPb = requiredTSPbResId == 0 ? null : res.getStringArray(requiredTSPbResId);
						final String[] requiredTSFn = requiredTSFnResId == 0 ? null : res.getStringArray(requiredTSFnResId);

						try
						{
							final List<String> existingTablesAndIndexes = Status.tablesAndIndexes(context);
							if (existingTablesAndIndexes != null)
							{
								for (String table : requiredTables)
								{
									sb.append("table ");
									sb.append(table);
									sb.append(" exists: ");
									boolean exists = existingTablesAndIndexes.contains(table);
									sb.append(exists);
									if (exists)
									{
										sb.append(" rows: ");
										sb.append(rowCount(database, table));
									}
									sb.append('\n');
								}
								sb.append('\n');
								for (String index : requiredIndexes)
								{
									sb.append("index ");
									sb.append(index);
									sb.append(": ");
									sb.append(existingTablesAndIndexes.contains(index));
									sb.append('\n');
								}
								if (requiredPmTables != null)
								{
									sb.append('\n');
									for (String table : requiredPmTables)
									{
										sb.append("pm table ");
										sb.append(table);
										sb.append(" exists: ");
										boolean exists = existingTablesAndIndexes.contains(table);
										sb.append(exists);
										if (exists)
										{
											sb.append(" rows: ");
											sb.append(rowCount(database, table));
										}
										sb.append('\n');
									}
								}
								if (requiredTSWn != null)
								{
									sb.append('\n');
									for (String table : requiredTSWn)
									{
										sb.append("wn table ");
										sb.append(table);
										sb.append(" exists: ");
										boolean exists = existingTablesAndIndexes.contains(table);
										sb.append(exists);
										if (exists)
										{
											sb.append(" rows: ");
											sb.append(rowCount(database, table));
										}
										sb.append('\n');
									}
								}
								if (requiredTSVn != null)
								{
									sb.append('\n');
									for (String table : requiredTSVn)
									{
										sb.append("vn table ");
										sb.append(table);
										sb.append(" exists: ");
										boolean exists = existingTablesAndIndexes.contains(table);
										sb.append(exists);
										if (exists)
										{
											sb.append(" rows: ");
											sb.append(rowCount(database, table));
										}
										sb.append('\n');
									}
								}
								if (requiredTSPb != null)
								{
									sb.append('\n');
									for (String table : requiredTSPb)
									{
										sb.append("pb table ");
										sb.append(table);
										sb.append(" exists: ");
										boolean exists = existingTablesAndIndexes.contains(table);
										sb.append(exists);
										if (exists)
										{
											sb.append(" rows: ");
											sb.append(rowCount(database, table));
										}
										sb.append('\n');
									}
								}
								if (requiredTSFn != null)
								{
									sb.append('\n');
									for (String table : requiredTSFn)
									{
										sb.append("fn table ");
										sb.append(table);
										sb.append(" exists: ");
										boolean exists = existingTablesAndIndexes.contains(table);
										sb.append(exists);
										if (exists)
										{
											sb.append(" rows: ");
											sb.append(rowCount(database, table));
										}
										sb.append('\n');
									}
								}
							}
							else
							{
								sb.append("null existing tables or indexes");
								sb.append('\n');
							}
						}
						catch (Exception e)
						{
							sb.append("cannot read tables or indexes: ");
							sb.append(e);
							sb.append('\n');
						}
					}
				}
				catch (@NonNull final SQLiteCantOpenDatabaseException e)
				{
					sb.append(databaseCanOpen);
					sb.append('\n');
					sb.append(e);
					sb.append('\n');
				}
			}
		}

		final String source = Settings.getDbSource(context);
		final long sourceSize = Settings.getDbSourceSize(context);
		final long sourceStamp = Settings.getDbSourceDate(context);
		final String name = Settings.getDbName(context);
		final long size = Settings.getDbSize(context);
		final long stamp = Settings.getDbDate(context);

		sb.append('\n');
		sb.append("recorded source: ");
		sb.append(name == null ? "null" : source);
		sb.append('\n');
		sb.append("recorded source size: ");
		sb.append(size == -1 ? "null" : sourceSize);
		sb.append('\n');
		sb.append("recorded source date: ");
		sb.append(stamp == -1 || stamp == 0 ? "null" : new Date(sourceStamp).toString());
		sb.append('\n');
		sb.append("recorded name: ");
		sb.append(name == null ? "null" : name);
		sb.append('\n');
		sb.append("recorded size: ");
		sb.append(size == -1 ? "null" : size);
		sb.append('\n');
		sb.append("recorded date: ");
		sb.append(stamp == -1 || stamp == 0 ? "null" : new Date(stamp).toString());
		sb.append('\n');

		final String dbDownloadSource = StorageSettings.getDbDownloadSource(context, org.sqlunet.download.Settings.Downloader.isZipDownloaderPref(context));
		final String dbDownloadTarget = StorageSettings.getDbDownloadTarget(context);

		sb.append('\n');
		sb.append("download source: ");
		sb.append(dbDownloadSource);
		sb.append('\n');
		sb.append("download target: ");
		sb.append(dbDownloadTarget);
		sb.append('\n');

		return sb.toString();
	}

	@SuppressWarnings("SameReturnValue")
	static private boolean canOpen(@NonNull final String path) throws SQLiteCantOpenDatabaseException
	{
		try (SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY))
		{
			return true;
		}
	}

	static private long rowCount(@NonNull final String path, final String table) throws SQLiteException
	{
		try (SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY))
		{
			return DatabaseUtils.queryNumEntries(db, table);
		}
	}
}
