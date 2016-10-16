package org.sqlunet.browser.config;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.sqlunet.browser.R;

import java.util.List;

class ManagementTasks
{
	private static final String TAG = "Management Tasks"; //$NON-NLS-1$

	static public void createDatabase(final Context context, final String databasePath)
	{
		try
		{
			final SQLiteDatabase db = context.openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);
			db.close();
		} catch (Exception e)
		{
			Log.e(TAG, "While creating database", e); //$NON-NLS-1$
		}
	}

	static public void deleteDatabase(final Context context, final String databasePath)
	{
		// make sure you close all database connections before deleting
		boolean result = context.deleteDatabase(databasePath);
		Log.d(TAG, "While dropping database: " + result); //$NON-NLS-1$	
	}

	static public void vacuum(final Context context, final String databasePath, final String tempDir)
	{
		new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected Void doInBackground(Void... params)
			{
				final SQLiteDatabase db = context.openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);

				// set parameters
				// db.execSQL("PRAGMA journal_mode = PERSIST;"); //$NON-NLS-1$
				db.execSQL("PRAGMA temp_store = FILE;"); //$NON-NLS-1$
				db.execSQL("PRAGMA temp_store_directory = '" + tempDir + "';"); //$NON-NLS-1$ //$NON-NLS-2$
				Log.d(TAG, "vacuuming in " + tempDir); //$NON-NLS-1$
				db.execSQL("VACUUM"); //$NON-NLS-1$
				Log.d(TAG, "vacuumed in " + tempDir); //$NON-NLS-1$
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

	static public void flushAll(final Context context, final String databasePath, final List<String> tables)
	{
		if (tables != null && !tables.isEmpty())
		{
			final SQLiteDatabase db = context.openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);
			for (final String table : tables)
			{
				int deletedRows = db.delete(table, null, null);
				Log.d(TAG, table + ": deleted " + deletedRows + " rows"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			db.close();
		}
	}

	static public void dropAll(final Context context, final String databasePath, final List<String> tables)
	{
		if (tables != null && !tables.isEmpty())
		{
			final SQLiteDatabase db = context.openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);
			for (final String table : tables)
			{
				db.execSQL("DROP TABLE IF EXISTS " + table); //$NON-NLS-1$
				Log.d(TAG, table + ": dropped"); //$NON-NLS-1$
			}
			db.close();
		}
	}
}
