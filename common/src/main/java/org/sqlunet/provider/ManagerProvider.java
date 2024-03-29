/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.provider;

import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import org.sqlunet.provider.ManagerContract.TablesAndIndices;

import java.util.ArrayList;
import java.util.Collection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 5House-keeping) Manager provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class ManagerProvider extends BaseProvider
{
	static private final String TAG = "ManagerProvider";

	// C O N T E N T   P R O V I D E R   A U T H O R I T Y

	static private final String AUTHORITY = makeAuthority("manager_authority");

	// U R I M A T C H E R

	static private final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static
	{
		matchURIs();
	}

	// join codes
	static private final int TABLES_AND_INDICES = 100;

	private static void matchURIs()
	{
		ManagerProvider.uriMatcher.addURI(AUTHORITY, TablesAndIndices.TABLE, ManagerProvider.TABLES_AND_INDICES);
	}

	@NonNull
	static public String makeUri(@SuppressWarnings("SameParameterValue") final String table)
	{
		return BaseProvider.SCHEME + AUTHORITY + '/' + table;
	}

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public ManagerProvider()
	{
	}

	// C L O S E

	/**
	 * Close provider
	 *
	 * @param context context
	 */
	static public void close(@NonNull final Context context)
	{
		final Uri uri = Uri.parse(BaseProvider.SCHEME + AUTHORITY);
		closeProvider(context, uri);
	}

	// M I M E

	@NonNull
	@Override
	public String getType(@NonNull final Uri uri)
	{
		if (ManagerProvider.uriMatcher.match(uri) == TABLES_AND_INDICES)
		{
			return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + TablesAndIndices.TABLE;
		}
		throw new UnsupportedOperationException("Illegal MIME type");
	}

	// Q U E R Y

	@Nullable
	@Override
	public Cursor query(@NonNull final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder)
	{
		if (this.db == null)
		{
			try
			{
				openReadWrite();
			}
			catch (SQLiteCantOpenDatabaseException e)
			{
				return null;
			}
		}

		// choose the table to query and a sort order based on the code returned for the incoming URI
		final int code = ManagerProvider.uriMatcher.match(uri);
		// Log.d(TAG + "URI", String.format("%s (code %s)\n", uri, code));
		String table;
		switch (code)
		{
			case TABLES_AND_INDICES:
				table = uri.getLastPathSegment();
				break;

			default:
			case UriMatcher.NO_MATCH:
				throw new RuntimeException("Malformed URI " + uri);
		}

		//if (BaseProvider.logSql)
		//{
		// final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, selection, groupBy, null, sortOrder, null);
		// logSql(sql, selectionArgs);
		// Log.d(TAG + "SQL", SqlFormatter.format(sql).toString());
		// Log.d(TAG + "ARGS", BaseProvider.argsToString(selectionArgs));
		//}

		// do query
		try
		{
			return this.db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
		}
		catch (SQLiteException e)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, selection, null, null, sortOrder, null);
			Log.d(TAG + "SQL", sql);
			Log.e(TAG, "Manager provider query failed", e);
			return null;
		}
	}

	/**
	 * Get tables utility
	 *
	 * @param context context
	 * @return collection of tables
	 */
	@NonNull
	static public Collection<String> getTables(@NonNull final Context context)
	{
		final Collection<String> tables = new ArrayList<>();
		final Uri uri = Uri.parse(makeUri(TablesAndIndices.URI));
		final String[] projection = {TablesAndIndices.TYPE, TablesAndIndices.NAME};
		final String selection = TablesAndIndices.TYPE + " = 'table' AND name NOT IN ('sqlite_sequence', 'android_metadata' )";
		final String[] selectionArgs = {};
		try (final Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null))
		{
			if (cursor != null)
			{
				if (cursor.moveToFirst())
				{
					final int idName = cursor.getColumnIndex(TablesAndIndices.NAME);
					do
					{
						final String table = cursor.getString(idName);
						tables.add(table);
					}
					while (cursor.moveToNext());
				}
			}
		}
		return tables;
	}
}
