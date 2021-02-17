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
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;

import org.sqlunet.assetpack.AssetPackLoader;
import org.sqlunet.browser.common.R;
import org.sqlunet.concurrency.Task;
import org.sqlunet.deploy.Deploy;
import org.sqlunet.download.Settings;
import org.sqlunet.settings.StorageSettings;
import org.sqlunet.settings.StorageUtils;

import java.io.File;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public class Diagnostics
{
	@FunctionalInterface
	interface ResultListener<T>
	{
		void onResult(T t);
	}

	static public class AsyncDiagnostics extends Task<Context, Long, CharSequence>
	{
		/**
		 * Result listener
		 */
		final private ResultListener<CharSequence> resultListener;

		/**
		 * Constructor
		 *
		 * @param resultListener result listener
		 */
		AsyncDiagnostics(final ResultListener<CharSequence> resultListener)
		{
			this.resultListener = resultListener;
		}

		@NonNull
		@Override
		protected CharSequence doInBackground(final Context... params)
		{
			final Context context = params[0];
			return report(context);
		}

		@Override
		protected void onPostExecute(final CharSequence result)
		{
			if (this.resultListener != null)
			{
				this.resultListener.onResult(result);
			}
		}
	}

	@NonNull
	private static CharSequence report(@NonNull final Context context)
	{
		final SpannableStringBuilder sb = new SpannableStringBuilder();
		append(sb, "DIAGNOSTICS", new StyleSpan(Typeface.BOLD));
		sb.append('\n');

		// APP

		sb.append('\n');
		append(sb, "app", new StyleSpan(Typeface.BOLD));
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
			sb.append(Long.toString(code));
			sb.append('\n');
		}
		catch (PackageManager.NameNotFoundException e)
		{
			sb.append("package info: ");
			sb.append(e.getMessage());
			sb.append('\n');
		}

		sb.append("api: ");
		sb.append(Integer.toString(Build.VERSION.SDK_INT));
		sb.append(' ');
		sb.append(Build.VERSION.CODENAME);
		sb.append('\n');

		// DATABASE

		final String database = StorageSettings.getDatabasePath(context);

		sb.append('\n');
		append(sb, "database", new StyleSpan(Typeface.BOLD));
		sb.append('\n');
		sb.append("path: ");
		sb.append(database);
		sb.append('\n');

		if (!database.isEmpty())
		{
			final File databaseFile = new File(database);
			final boolean databaseExists = databaseFile.exists();

			sb.append("exists: ");
			sb.append(Boolean.toString(databaseExists));
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
			sb.append(Float.toString(dp));
			sb.append('%');
			sb.append('\n');

			if (databaseExists)
			{
				final boolean databaseIsFile = databaseFile.isFile();
				final long databaseLastModified = databaseFile.lastModified();
				final long databaseSize = databaseFile.length();
				final boolean databaseCanRead = databaseFile.canRead();

				sb.append("is file: ");
				sb.append(Boolean.toString(databaseIsFile));
				sb.append('\n');

				sb.append("size: ");
				sb.append(Long.toString(databaseSize));
				sb.append('\n');

				sb.append("last modified: ");
				sb.append(databaseLastModified == -1 || databaseLastModified == 0 ? "n/a" : new Date(databaseLastModified).toString());
				sb.append('\n');

				sb.append("can read: ");
				sb.append(Boolean.toString(databaseCanRead));
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
					sb.append(Boolean.toString(databaseCanOpen));
					sb.append('\n');

					final int status = Status.status(context);
					final boolean existsDb = (status & Status.EXISTS) != 0;
					final boolean existsTables = (status & Status.EXISTS_TABLES) != 0;
					if (existsDb)
					{
						// TABLES

						sb.append('\n');
						append(sb, "tables", new StyleSpan(Typeface.BOLD));
						sb.append('\n');
						sb.append("tables exist: ");
						sb.append(Boolean.toString(existsTables));
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
									sb.append(Boolean.toString(exists));
									if (exists)
									{
										sb.append(" rows: ");
										sb.append(Long.toString(rowCount(database, table)));
									}
									sb.append('\n');
								}
								sb.append('\n');
								for (String index : requiredIndexes)
								{
									sb.append("index ");
									sb.append(index);
									sb.append(": ");
									sb.append(Boolean.toString(existingTablesAndIndexes.contains(index)));
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
										sb.append(Boolean.toString(exists));
										if (exists)
										{
											sb.append(" rows: ");
											sb.append(Long.toString(rowCount(database, table)));
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
										sb.append(Boolean.toString(exists));
										if (exists)
										{
											sb.append(" rows: ");
											sb.append(Long.toString(rowCount(database, table)));
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
										sb.append(Boolean.toString(exists));
										if (exists)
										{
											sb.append(" rows: ");
											sb.append(Long.toString(rowCount(database, table)));
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
										sb.append(Boolean.toString(exists));
										if (exists)
										{
											sb.append(" rows: ");
											sb.append(Long.toString(rowCount(database, table)));
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
										sb.append(Boolean.toString(exists));
										if (exists)
										{
											sb.append(" rows: ");
											sb.append(Long.toString(rowCount(database, table)));
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
							sb.append(e.getMessage());
							sb.append('\n');
						}
					}
				}
				catch (@NonNull final SQLiteCantOpenDatabaseException e)
				{
					sb.append(Boolean.toString(databaseCanOpen));
					sb.append('\n');
					sb.append(e.getMessage());
					sb.append('\n');
				}
			}
		}

		// RECORDED SOURCE

		final String source = Settings.getDbSource(context);
		final long sourceSize = Settings.getDbSourceSize(context);
		final long sourceStamp = Settings.getDbSourceDate(context);
		final String sourceEtag = Settings.getDbSourceEtag(context);
		final String sourceVersion = Settings.getDbSourceVersion(context);
		final String sourceStaticVersion = Settings.getDbSourceStaticVersion(context);
		final String name = Settings.getDbName(context);
		final long size = Settings.getDbSize(context);
		final long stamp = Settings.getDbDate(context);

		sb.append('\n');
		append(sb, "source", new StyleSpan(Typeface.BOLD));
		sb.append('\n');
		sb.append("recorded source: ");
		sb.append(source == null ? "null" : source);
		sb.append('\n');
		sb.append("recorded source size: ");
		sb.append(sourceSize == -1 ? "null" : Long.toString(sourceSize));
		sb.append('\n');
		sb.append("recorded source date: ");
		sb.append(sourceStamp == -1 || sourceStamp == 0 ? "null" : new Date(sourceStamp).toString());
		sb.append('\n');
		sb.append("recorded source etag: ");
		sb.append(sourceEtag == null ? "null" : sourceEtag);
		sb.append('\n');
		sb.append("recorded source version: ");
		sb.append(sourceVersion == null ? "null" : sourceVersion);
		sb.append('\n');
		sb.append("recorded source static version: ");
		sb.append(sourceStaticVersion == null ? "null" : sourceStaticVersion);
		sb.append('\n');
		sb.append("recorded name: ");
		sb.append(name == null ? "null" : name);
		sb.append('\n');
		sb.append("recorded size: ");
		sb.append(size == -1 ? "null" : Long.toString(size));
		sb.append('\n');
		sb.append("recorded date: ");
		sb.append(stamp == -1 || stamp == 0 ? "null" : new Date(stamp).toString());
		sb.append('\n');

		// ASSET PACKS

		final String assetPack = context.getString(R.string.asset_primary);
		final String assetZip = context.getString(R.string.asset_zip_primary);
		final String assetDir = context.getString(R.string.asset_dir_primary);

		sb.append('\n');
		append(sb, "assets", new StyleSpan(Typeface.BOLD));
		sb.append('\n');
		sb.append("primary asset pack: ");
		sb.append(assetPack);
		sb.append('\n');
		sb.append("primary asset archive: ");
		sb.append(assetDir);
		sb.append('/');
		sb.append(assetZip);
		sb.append('\n');
		String assetLocation = new AssetPackLoader(context, assetPack).assetPackPathIfInstalled();
		sb.append("primary asset ");
		sb.append(assetPack);
		if (assetLocation != null)
		{
			sb.append(" installed at: ");
			sb.append(assetLocation);
		}
		else
		{
			sb.append(" not installed");
		}
		sb.append('\n');

		final String altAssetPack = context.getString(R.string.asset_alt);
		final String altAssetDir = context.getString(R.string.asset_dir_alt);
		final String altAssetZip = context.getString(R.string.asset_zip_alt);
		sb.append("alt asset pack: ");
		if (!altAssetPack.isEmpty())
		{
			sb.append(altAssetPack);
			sb.append('\n');
			sb.append("alt asset archive: ");
			sb.append(altAssetDir);
			sb.append('/');
			sb.append(altAssetZip);
			sb.append('\n');
		}
		String altAssetLocation = new AssetPackLoader(context, altAssetPack).assetPackPathIfInstalled();
		sb.append("alt asset ");
		sb.append(altAssetPack);
		if (altAssetLocation != null)
		{
			sb.append(" installed at: ");
			sb.append(altAssetLocation);
		}
		else
		{
			sb.append(" not installed");
		}
		sb.append('\n');

		// DOWNLOAD

		final String dbDownloadSource = StorageSettings.getDbDownloadSource(context, org.sqlunet.download.Settings.Downloader.isZipDownloaderPref(context));
		final String dbDownloadTarget = StorageSettings.getDbDownloadTarget(context);

		sb.append('\n');
		append(sb, "download", new StyleSpan(Typeface.BOLD));
		sb.append('\n');
		sb.append("download source: ");
		sb.append(dbDownloadSource);
		sb.append('\n');
		sb.append("download target: ");
		sb.append(dbDownloadTarget);
		sb.append('\n');

		return sb;
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

	/**
	 * Append text
	 *
	 * @param sb    spannable string builder
	 * @param text  text
	 * @param spans spans to apply
	 */
	static private void append(@NonNull final SpannableStringBuilder sb, @Nullable final CharSequence text, @NonNull final Object... spans)
	{
		if (text == null || text.length() == 0)
		{
			return;
		}

		final int from = sb.length();
		sb.append(text);
		final int to = sb.length();

		for (final Object span : spans)
		{
			sb.setSpan(span, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
}
