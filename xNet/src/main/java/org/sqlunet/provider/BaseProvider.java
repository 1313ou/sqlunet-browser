package org.sqlunet.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import org.sqlunet.settings.StorageSettings;

/**
 * SqlUNet provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public abstract class BaseProvider extends ContentProvider
{
	static private final String TAG = "BaseProvider";

	/**
	 * Debug generated SQL
	 */
	static protected final boolean debugSql = true;

	static protected final String VENDOR = "sqlunet";

	static public final String SCHEME = "content://";

/*
	static private final class DatabaseHelper extends SQLiteOpenHelper
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
			System.err.println("Upgrade");
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
			Log.d(BaseProvider.TAG, "Opened by " + this.getClass() + " content provider: " + this.db.getPath());
		}
		catch (final SQLiteCantOpenDatabaseException e)
		{
			Log.e(BaseProvider.TAG, "Open failed by " + this.getClass() + " content provider: " + path, e);
			throw e;
		}
	}

	/**
	 * Open database
	 *
	 * @param path  database path
	 * @param flags database name
	 * @return opened database
	 */
	private SQLiteDatabase open(final String path, final int flags)
	{
		this.db = SQLiteDatabase.openDatabase(path, null, flags);
		return this.db;
	}


	@Override
	public boolean onCreate()
	{
		return true;
	}


	@Override
	public void shutdown()
	{
		if (this.db != null)
		{
			final String path = this.db.getPath();
			synchronized (this)
			{
				this.db.close();
				this.db = null;
			}
			Log.d(BaseProvider.TAG, "Closed by " + this.getClass() + " content provider: " + path);
		}
		super.shutdown();
	}

	// W R I T E O P E R A T I O N S


	@Override
	public int delete(@NonNull final Uri uri, final String selection, final String[] selectionArgs)
	{
		throw new UnsupportedOperationException("Read-only");
	}


	@Override
	public Uri insert(@NonNull final Uri uri, final ContentValues values)
	{
		throw new UnsupportedOperationException("Read-only");
	}


	@Override
	public int update(@NonNull final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs)
	{
		throw new UnsupportedOperationException("Read-only");
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
			projection2[i++] = "*";
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
	static String[] prependProjection(final String[] projection, final String... items)
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
			projection2[i] = "*";
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

	/**
	 * Convert args to string
	 *
	 * @param args args
	 * @return string
	 */
	protected static String argsToString(final String... args)
	{
		final StringBuilder sb = new StringBuilder();
		if (args != null && args.length > 0)
		{
			for (final String s : args)
			{
				if (sb.length() > 0)
				{
					sb.append(", ");
				}
				sb.append(s);
			}
		}
		return sb.toString();
	}
}
