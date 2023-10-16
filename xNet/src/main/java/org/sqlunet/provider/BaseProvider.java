/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;

import org.sqlunet.settings.StorageSettings;
import org.sqlunet.sql.Utils;

import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * SqlUNet provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public abstract class BaseProvider extends ContentProvider
{
	static private final String TAG = "BaseProvider";

	// C O N T E N T   P R O V I D E R   A U T H O R I T Y

	@NonNull
	static protected String makeAuthority(final String configKey)
	{
		try
		{
			final InputStream is = BaseProvider.class.getResourceAsStream("/org/sqlunet/config.properties");
			final Properties properties = new Properties();
			properties.load(is);

			String authority = properties.getProperty(configKey);
			if (authority != null && !authority.isEmpty())
			{
				return authority;
			}
			throw new RuntimeException("Null provider key=" + configKey);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@NonNull
	@SuppressWarnings("WeakerAccess")
	static protected Uri[] getAuthorityUris()
	{
		try
		{
			final InputStream is = BaseProvider.class.getResourceAsStream("/org/sqlunet/config.properties");
			final Properties properties = new Properties();
			properties.load(is);

			final Set<String> authorityKeys = properties.stringPropertyNames();
			final Uri[] authorities = new Uri[authorityKeys.size()];
			int i = 0;
			for (String authorityKey : authorityKeys)
			{
				authorities[i++] = Uri.parse(BaseProvider.SCHEME + properties.getProperty(authorityKey));
			}
			return authorities;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	// S Q L   B U F F E R

	private static final int DEFAULT_SQL_BUFFER_CAPACITY = 15;

	/**
	 * Circular buffer
	 */
	static public class CircularBuffer extends LinkedList<CharSequence>
	{
		static public final String PREF_SQL_BUFFER_CAPACITY = "pref_sql_buffer_capacity";

		static public final String PREF_SQL_LOG = "pref_sql_log";

		private final int limit;

		CircularBuffer(@SuppressWarnings("SameParameterValue") final int number)
		{
			this.limit = number;
		}

		synchronized void addItem(final CharSequence value)
		{
			addLast(value);
			if (size() > this.limit)
			{
				removeFirst();
			}
		}

		@NonNull
		synchronized public CharSequence[] reverseItems()
		{
			final int n = size();
			// Log.d(TAG, "sql size " + n);
			final CharSequence[] array = new CharSequence[n];
			Iterator<CharSequence> iter = listIterator();
			int i = n - 1;
			while (iter.hasNext())
			{
				CharSequence sql = iter.next();
				// Log.d(TAG, "get sql " + sql + " " + i);
				array[i] = sql;
				i--;
			}
			return array;
		}

		/*
		@SuppressWarnings("unused")
		public int getLimit()
		{
			return this.limit;
		}
		*/

		/*
		@SuppressWarnings("unused")
		public void setLimit(int limit)
		{
			this.limit = limit;
		}
		*/

		/*
		 * Get sql circular buffer capacity
		 *
		 * @param context context
		 * @return preferred sql circular buffer capacity
		 */
		/*
		static public int getSqlBufferCapacityPref(final Context context)
		{
			final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			final String capacityStr = sharedPref.getString(PREF_SQL_BUFFER_CAPACITY, null);
			try
			{
				return Integer.parseInt(capacityStr);
			}
			catch (Exception e)
			{
				//
			}
			return -1;
		}
		*/

		/*
		 * Get preferred sql log preference
		 *
		 * @param context context
		 * @return preferred sql log preference
		 */
		/*
		static public boolean getSqlLogPref(final Context context)
		{
			final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			return sharedPref.getBoolean(CircularBuffer.PREF_SQL_LOG, false);
		}
		*/
	}

	/**
	 * SQL statement buffer
	 */
	@NonNull
	@SuppressWarnings("StaticVariableOfConcreteClass")
	static public CircularBuffer sqlBuffer = new CircularBuffer(DEFAULT_SQL_BUFFER_CAPACITY);

	/**
	 * Record generated SQL
	 */
	static public boolean logSql = false;

	static protected final String VENDOR = "sqlunet";

	protected static final String SCHEME = "content://";

	// D A T A B A S E

	// database

	@Nullable
	protected SQLiteDatabase db;

	// O P E N / S H U T D O W N

	protected void openReadOnly() throws SQLiteCantOpenDatabaseException
	{
		final Context context = getContext();
		assert context != null;
		final String path = StorageSettings.getDatabasePath(context);
		try
		{
			this.db = openReadOnly(path, SQLiteDatabase.OPEN_READONLY);
			assert this.db != null;
			Log.d(TAG, "Opened by " + this.getClass() + " content provider: " + this.db.getPath());
		}
		catch (@NonNull final SQLiteCantOpenDatabaseException e)
		{
			Log.e(BaseProvider.TAG, "Open failed by " + this.getClass() + " content provider: " + path, e);
			throw e;
		}
	}

	protected void openReadWrite() throws SQLiteCantOpenDatabaseException
	{
		final Context context = getContext();
		assert context != null;
		final String path = StorageSettings.getDatabasePath(context);
		try
		{
			this.db = openReadOnly(path, SQLiteDatabase.OPEN_READWRITE);
			assert this.db != null;
			Log.d(TAG, "Opened by " + this.getClass() + " content provider: " + this.db.getPath());
		}
		catch (@NonNull final SQLiteCantOpenDatabaseException e)
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
	@Nullable
	private SQLiteDatabase openReadOnly(@NonNull final String path, @SuppressWarnings("SameParameterValue") final int flags)
	{
		this.db = SQLiteDatabase.openDatabase(path, null, flags);
		return this.db;
	}

	@SuppressWarnings("SameReturnValue")
	@Override
	public boolean onCreate()
	{
		return true;
	}

	@Override
	public void shutdown()
	{
		Log.d(TAG, "Shutdown " + this.getClass());
		// super.shutdown();
		close();
	}

	@SuppressWarnings("SameReturnValue")
	@Override
	public boolean refresh(final Uri uri, @Nullable final Bundle args, @Nullable final CancellationSignal cancellationSignal)
	{
		super.refresh(uri, args, cancellationSignal);
		Log.d(TAG, "Refresh " + this.getClass());
		close();
		return true;
	}

	@Nullable
	@SuppressWarnings("SameReturnValue")
	@Override
	public Bundle call(@NonNull final String method, final String arg, final Bundle extras)
	{
		Log.d(TAG, "Called '" + method + "' on " + this.getClass());
		if (CALLED_REFRESH_METHOD.equals(method))
		{
			close();
		}
		return null;
	}

	/**
	 * Refresh method name
	 */
	@SuppressWarnings("WeakerAccess")
	static public final String CALLED_REFRESH_METHOD = "closeProvider";

	/**
	 * Close provider
	 */
	private void close()
	{
		if (this.db != null)
		{
			final String path = this.db.getPath();
			synchronized (this)
			{
				this.db.close();
				this.db = null;
			}
			Log.d(TAG, "Close " + this.getClass() + " content provider: " + path);
		}
	}

	/**
	 * Close provider
	 *
	 * @param context context
	 * @param uri     provider uri
	 */
	@SuppressWarnings("WeakerAccess")
	static public void closeProvider(@NonNull final Context context, @NonNull final Uri uri)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			context.getContentResolver().refresh(uri, null, null);
		}
		else
		{
			context.getContentResolver().call(uri, BaseProvider.CALLED_REFRESH_METHOD, null, null);
		}
	}

	/**
	 * Close all providers
	 *
	 * @param context context
	 */
	static public void closeProviders(@NonNull final Context context)
	{
		final Uri[] uris = getAuthorityUris();
		for (Uri uri : uris)
		{
			closeProvider(context, uri);
		}
	}

	// W R I T E O P E R A T I O N S

	@Override
	public int delete(@NonNull final Uri uri, final String selection, final String[] selectionArgs)
	{
		throw new UnsupportedOperationException("Read-only");
	}


	@NonNull
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
	 * @param items      items to addItem to projection
	 * @return augmented projection
	 */
	@NonNull
	public static String[] appendProjection(@Nullable final String[] projection, @NonNull final String... items)
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
	 * @param items      items to addItem to projection
	 * @return augmented projection
	 */
	@NonNull
	static String[] prependProjection(@Nullable final String[] projection, @NonNull @SuppressWarnings("SameParameterValue") final String... items)
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
	@NonNull
	static protected String argsToString(@Nullable final String... args)
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

	/*
	 * Resize sql buffer
	 */
	/*
	public void resizeSql()
	{
		final Context context = getContext();
		int capacity = CircularBuffer.getSqlBufferCapacityPref(context);
		if (capacity != -1)
		{
			BaseProvider.resizeSql(capacity);
		}
	}
	*/

	/**
	 * Resize sql buffer
	 *
	 * @param capacity capacity
	 */
	static public void resizeSql(final int capacity)
	{
		// Log.d(TAG, "Sql buffer capacity " + capacity);
		sqlBuffer = new CircularBuffer(capacity);
	}

	/**
	 * Log query
	 *
	 * @param sql  sql
	 * @param args parameters
	 */
	protected void logSql(final String sql, final String... args)
	{
		final String sql2 = Utils.replaceArgs(sql, Utils.toArgs(args));
		if (sql2 != null)
		{
			sqlBuffer.addItem(sql2);
		}
	}
}
