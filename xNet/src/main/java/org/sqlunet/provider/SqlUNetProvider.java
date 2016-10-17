package org.sqlunet.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import org.sqlunet.settings.StorageSettings;

public abstract class SqlUNetProvider extends ContentProvider
{
	private static final String TAG = "SqlUNetProvider"; //$NON-NLS-1$

	/**
	 * Debug generated SQL
	 */
	protected static final boolean debugSql = true;

/*
	private static final class DatabaseHelper extends SQLiteOpenHelper
	{
		// constructor
		DatabaseHelper(final Context context)
		{
			super(context, StorageSettings.getDatabasePath(context), null, 31);
		}

		@Override
		public void onCreate(final SQLiteDatabase db)
		{
			// do nothing
		}

		@Override
		public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion)
		{
			// do nothing
			System.err.println("Upgrade"); //$NON-NLS-1$
		}
	}
*/

	// D A T A B A S E

	// database

	protected SQLiteDatabase db;

	// O P E N / S H U T D O W N

	protected void open() throws SQLiteCantOpenDatabaseException
	{
		final String path = StorageSettings.getDatabasePath(getContext());
		try
		{
			this.db = open(path, SQLiteDatabase.OPEN_READONLY);
			Log.d(SqlUNetProvider.TAG, "Opened by " + this.getClass() + " content provider: " + this.db.getPath()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		catch (final SQLiteCantOpenDatabaseException e)
		{
			Log.e(SqlUNetProvider.TAG, "Open failed by " + this.getClass() + " content provider: " + this.db.getPath(), e); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Open database
	 *
	 * @param path  database path
	 * @param flags database name
	 * @return opened database
	 */
	private SQLiteDatabase open(final String path, @SuppressWarnings("SameParameterValue") final int flags)
	{
		this.db = SQLiteDatabase.openDatabase(path, null, flags);
		return this.db;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.content.ContentProvider#shutdown()
	 */
	@Override
	public void shutdown()
	{
		if (this.db != null)
		{
			synchronized (this)
			{
				this.db.close();
				this.db = null;
			}
			Log.d(SqlUNetProvider.TAG, "Closed by " + this.getClass() + " content provider: " + this.db.getPath()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		super.shutdown();
	}

	// W R I T E O P E R A T I O N S

	/*
	 * (non-Javadoc)
	 *
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(final Uri uri, final String selection, final String[] selectionArgs)
	{
		throw new UnsupportedOperationException("Read-only"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(final Uri uri, final ContentValues values)
	{
		throw new UnsupportedOperationException("Read-only"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs)
	{
		throw new UnsupportedOperationException("Read-only"); //$NON-NLS-1$
	}

	// H E L P E R S

	/**
	 * Append items to projection
	 *
	 * @param projection original projection
	 * @param items      items to add to projection
	 * @return augmented projection
	 */
	protected static String[] appendProjection(final String[] projection, final String... items)
	{
		String[] projection2;
		int i = 0;
		if (projection == null)
		{
			projection2 = new String[1 + items.length];
			projection2[i++] = "*"; //$NON-NLS-1$
		}
		else
		{
			projection2 = new String[projection.length + items.length];
			for (final String item : projection)
			{
				projection2[i++] = item;
			}
		}

		for (final String item : items)
		{
			projection2[i++] = item;
		}
		return projection2;
	}

	/**
	 * Add items to projection
	 *
	 * @param projection original projection
	 * @param items      items to add to projection
	 * @return augmented projection
	 */
	static String[] prependProjection(final String[] projection, @SuppressWarnings("SameParameterValue") final String... items)
	{
		String[] projection2;
		if (projection == null)
		{
			projection2 = new String[1 + items.length];
		}
		else
		{
			projection2 = new String[projection.length + items.length];
		}
		int i = 0;
		for (final String item : items)
		{
			projection2[i++] = item;
		}
		if (projection == null)
		{
			projection2[i] = "*"; //$NON-NLS-1$
		}
		else
		{
			for (final String item : projection)
			{
				projection2[i++] = item;
			}
		}
		return projection2;
	}

	protected static String argsToString(final String[] args)
	{
		final StringBuilder sb = new StringBuilder();
		if (args != null && args.length > 0)
		{
			for (final String s : args)
			{
				if (sb.length() > 0)
				{
					sb.append(", "); //$NON-NLS-1$
				}
				sb.append(s);
			}
		}
		return sb.toString();
	}
}
