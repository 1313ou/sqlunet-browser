package org.sqlunet.browser.config;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.sqlunet.browser.R;

import java.util.Collection;

/**
 * Management tasks
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class ManagementTasks
{
	private static final String TAG = "Management Tasks";
	/**
	 * Create database
	 *
	 * @param context      context
	 * @param databasePath path
	 */
	static public void createDatabase(final Context context, final String databasePath)
	{
		try
		{
			final SQLiteDatabase db = context.openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);
			db.close();
		}
		catch (Exception e)
		{
			Log.e(TAG, "While creating database", e);
		}
	}

	/**
	 * Delete database
	 *
	 * @param context      context
	 * @param databasePath path
	 */
	static public void deleteDatabase(final Context context, final String databasePath)
	{
		// make sure you close all connections before deleting
		boolean result = context.deleteDatabase(databasePath);
		Log.d(TAG, "While dropping database: " + result);
	}

	/**
	 * Vacuum database
	 *
	 * @param context      context
	 * @param databasePath path
	 * @param tempDir      temp directory
	 */
	static public void vacuum(final Context context, final String databasePath, final String tempDir)
	{
		new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected Void doInBackground(Void... params)
			{
				final SQLiteDatabase db = context.openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);

				// set parameters
				// db.execSQL("PRAGMA journal_mode = PERSIST;");
				db.execSQL("PRAGMA temp_store = FILE;");
				db.execSQL("PRAGMA temp_store_directory = '" + tempDir + "';");
				Log.d(TAG, "vacuuming in " + tempDir);
				db.execSQL("VACUUM");
				Log.d(TAG, "vacuumed in " + tempDir);
				db.close();
				return null;
			}

			@Override
			protected void onPostExecute(Void result)
			{
				Toast.makeText(context, R.string.status_op_done, Toast.LENGTH_SHORT).show();
			}

			@Override
			protected void onPreExecute()
			{ /* */
			}

			@Override
			protected void onProgressUpdate(Void... values)
			{ /* */
			}
		}.execute();
	}

	/**
	 * Flush tables
	 *
	 * @param context      context
	 * @param databasePath path
	 * @param tables       tables to flush
	 */
	static public void flushAll(final Context context, final String databasePath, final Collection<String> tables)
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

	/**
	 * Drop tables
	 *
	 * @param context      context
	 * @param databasePath path
	 * @param tables       tables to drop
	 */
	static public void dropAll(final Context context, final String databasePath, final Collection<String> tables)
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
}
