package org.sqlunet.provider;

import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import org.sqlunet.provider.ManagerContract.TablesAndIndices;

import java.util.ArrayList;
import java.util.List;

/**
 * WordNet provider
 *
 * @author Bernard Bou
 */
public class ManagerProvider extends SqlUNetProvider
{
	static private final String TAG = "ManagerProvider"; //$NON-NLS-1$

	// U R I M A T C H E R

	// uri matcher
	private static final UriMatcher uriMatcher;

	// join codes
	private static final int TABLES_AND_INDICES = 100;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(final Uri uri)
	{
		switch (ManagerProvider.uriMatcher.match(uri))
		{
			case TABLES_AND_INDICES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + ManagerContract.AUTHORITY + '.' + TablesAndIndices.TABLE; //$NON-NLS-1$

			default:
				throw new UnsupportedOperationException("Illegal MIME type"); //$NON-NLS-1$
		}
	}

	// Q U E R Y

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@SuppressWarnings("boxing")
	@Override
	public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder)
	{
		if (this.db == null)
		{
			open();
		}

		// choose the table to query and a sort order based on the code returned for the incoming URI
		String table;
		final String groupBy = null;
		final int code = ManagerProvider.uriMatcher.match(uri);
		Log.d(ManagerProvider.TAG + "URI", String.format("%s (code %s)\n", uri, code)); //$NON-NLS-1$ //$NON-NLS-2$

		switch (code)
		{
			case TABLES_AND_INDICES:
				table = uri.getLastPathSegment();
				break;

			default:
			case UriMatcher.NO_MATCH:
				throw new RuntimeException("Malformed URI " + uri); //$NON-NLS-1$
		}

		if (SqlUNetProvider.debugSql)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, selection, groupBy, null, sortOrder, null);
			Log.d(ManagerProvider.TAG + "SQL", sql); //$NON-NLS-1$
			Log.d(ManagerProvider.TAG + "ARGS", SqlUNetProvider.argsToString(selectionArgs)); //$NON-NLS-1$
		}

		// do query
		try
		{
			return this.db.query(table, projection, selection, selectionArgs, groupBy, null, sortOrder);
		}
		catch (SQLiteException e)
		{
			Log.e(TAG, "Manager provider query failed", e); //$NON-NLS-1$
			return null;
		}
	}

	static public List<String> getTables(final Context context)
	{
		final List<String> tables = new ArrayList<>();
		final Uri uri = Uri.parse(TablesAndIndices.CONTENT_URI);
		final String[] projection = new String[]{TablesAndIndices.TYPE, TablesAndIndices.NAME};
		final String selection = TablesAndIndices.TYPE + " = 'table' AND name NOT IN ('sqlite_sequence', 'android_metadata' )"; //$NON-NLS-1$
		final String[] selectionArgs = new String[]{};
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
