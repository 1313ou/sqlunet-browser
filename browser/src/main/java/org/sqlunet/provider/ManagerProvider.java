package org.sqlunet.provider;

import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import org.sqlunet.provider.ManagerContract.TablesAndIndices;
import org.sqlunet.sql.SqlFormatter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 5House-keeping) Manager provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class ManagerProvider extends BaseProvider
{
	static private final String TAG = "ManagerProvider";
	// U R I M A T C H E R

	// uri matcher
	static private final UriMatcher uriMatcher;

	// join codes
	static private final int TABLES_AND_INDICES = 100;

	static
	{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		ManagerProvider.uriMatcher.addURI(ManagerContract.AUTHORITY, TablesAndIndices.TABLE, ManagerProvider.TABLES_AND_INDICES);
	}

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public ManagerProvider()
	{
	}

	// M I M E

	@Override
	public String getType(@NonNull final Uri uri)
	{
		switch (ManagerProvider.uriMatcher.match(uri))
		{
			case TABLES_AND_INDICES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + ManagerContract.AUTHORITY + '.' + TablesAndIndices.TABLE;
			default:
				throw new UnsupportedOperationException("Illegal MIME type");
		}
	}

	// Q U E R Y

	@SuppressWarnings("boxing")
	@Override
	public Cursor query(@NonNull final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder)
	{
		if (this.db == null)
		{
			try
			{
				open();
			}
			catch (SQLiteCantOpenDatabaseException e)
			{
				return null;
			}
		}

		// choose the table to query and a sort order based on the code returned for the incoming URI
		final int code = ManagerProvider.uriMatcher.match(uri);
		Log.d(ManagerProvider.TAG + "URI", String.format("%s (code %s)\n", uri, code));
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

		final String groupBy = null;
		if (BaseProvider.debugSql)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, selection, groupBy, null, sortOrder, null);
			// logSql(sql, selectionArgs);
			Log.d(ManagerProvider.TAG + "SQL", SqlFormatter.format(sql).toString());
			Log.d(ManagerProvider.TAG + "ARGS", BaseProvider.argsToString(selectionArgs));
		}

		// do query
		try
		{
			return this.db.query(table, projection, selection, selectionArgs, groupBy, null, sortOrder);
		}
		catch (SQLiteException e)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, selection, groupBy, null, sortOrder, null);
			Log.d(ManagerProvider.TAG + "SQL", sql);
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
	static public Collection<String> getTables(final Context context)
	{
		final Collection<String> tables = new ArrayList<>();
		final Uri uri = Uri.parse(TablesAndIndices.CONTENT_URI);
		final String[] projection = {TablesAndIndices.TYPE, TablesAndIndices.NAME};
		final String selection = TablesAndIndices.TYPE + " = 'table' AND name NOT IN ('sqlite_sequence', 'android_metadata' )";
		final String[] selectionArgs = {};
		final String sortOrder = null;
		final Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
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
			cursor.close();
		}
		return tables;
	}
}
