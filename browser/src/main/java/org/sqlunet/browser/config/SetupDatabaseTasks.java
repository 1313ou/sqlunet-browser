package org.sqlunet.browser.config;

import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import org.sqlunet.bnc.provider.BNCContract;
import org.sqlunet.framenet.provider.FrameNetContract;
import org.sqlunet.predicatematrix.provider.PredicateMatrixContract;
import org.sqlunet.propbank.provider.PropBankContract;
import org.sqlunet.provider.ManagerContract;
import org.sqlunet.provider.XSqlUNetContract;
import org.sqlunet.verbnet.provider.VerbNetContract;
import org.sqlunet.wordnet.provider.WordNetContract;

import java.util.Collection;

/**
 * Manage tasks
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class SetupDatabaseTasks
{
	static private final String TAG = "SetupDatabaseTasks";

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
		final String[] authorities = {ManagerContract.AUTHORITY, XSqlUNetContract.AUTHORITY, WordNetContract.AUTHORITY, VerbNetContract.AUTHORITY, PropBankContract.AUTHORITY, FrameNetContract.AUTHORITY, //
				PredicateMatrixContract.AUTHORITY, BNCContract.AUTHORITY,};
		for (String authority : authorities)
		{
			final ContentProviderClient client = context.getContentResolver().acquireContentProviderClient(authority);
			assert client != null;
			final ContentProvider provider = client.getLocalContentProvider();
			assert provider != null;
			provider.shutdown();
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
			{
				//noinspection deprecation
				client.release();
			}
			else
			{
				client.close();
			}
		}

		// delete
		boolean result = context.deleteDatabase(databasePath);
		Log.d(TAG, "While dropping database: " + result);
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
}
