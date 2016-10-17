package org.sqlunet.bnc.provider;

import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.provider.SqlUNetProvider;

/**
 * WordNet provider
 *
 * @author Bernard Bou
 */
public class BNCProvider extends SqlUNetProvider
{
	static private final String TAG = "BNCProvider"; //$NON-NLS-1$

	// U R I M A T C H E R

	// uri matcher
	private static final UriMatcher uriMatcher;

	// table codes
	private static final int BNC = 11;

	// join tables
	private static final int WORDS_BNC = 100;

	static
	{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		BNCProvider.uriMatcher.addURI(BNCContract.AUTHORITY, BNCContract.BNCs.TABLE, BNCProvider.BNC);
		BNCProvider.uriMatcher.addURI(BNCContract.AUTHORITY, BNCContract.Words_BNCs.TABLE, BNCProvider.WORDS_BNC);
	}

	// C O N S T R U C T

	/**
	 * Constructor
	 */
	public BNCProvider()
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
		switch (BNCProvider.uriMatcher.match(uri))
		{

			// TABLES

			case BNC:
				return SqlUNetContract.VENDOR + ".android.cursor.item/" + SqlUNetContract.VENDOR + '.' + BNCContract.AUTHORITY + '.' + BNCContract.BNCs.TABLE; //$NON-NLS-1$

			// JOINS

			case WORDS_BNC:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + BNCContract.AUTHORITY + '.' + BNCContract.Words_BNCs.TABLE; //$NON-NLS-1$

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
	public Cursor query(final Uri uri, final String[] projection, final String selection0, final String[] selectionArgs, final String sortOrder)
	{
		if (this.db == null)
		{
			open();
		}

		String selection = selection0;
		// choose the table to query and a sort order based on the code returned for the incoming URI
		String table;
		final String groupBy = null;
		final int code = BNCProvider.uriMatcher.match(uri);
		Log.d(BNCProvider.TAG + "URI", String.format("%s (code %s)", uri, code)); //$NON-NLS-1$ //$NON-NLS-2$

		switch (code)
		{

			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case BNC:
				table = BNCContract.BNCs.TABLE;
				if (selection != null)
				{
					selection += " AND "; //$NON-NLS-1$
				}
				else
				{
					selection = ""; //$NON-NLS-1$
				}
				selection += BNCContract.BNCs.POS + " = ?"; //$NON-NLS-1$
				break;

			// J O I N S

			case WORDS_BNC:
				table = "bncs " + // //$NON-NLS-1$
						"LEFT JOIN bncspwrs USING (wordid, pos) " + // //$NON-NLS-1$
						"LEFT JOIN bncconvtasks USING (wordid, pos) " + // //$NON-NLS-1$
						"LEFT JOIN bncimaginfs USING (wordid, pos) "; //$NON-NLS-1$
				break;

			default:
			case UriMatcher.NO_MATCH:
				throw new RuntimeException("Malformed URI " + uri); //$NON-NLS-1$
		}

		if (SqlUNetProvider.debugSql)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, selection, groupBy, null, sortOrder, null);
			Log.d(BNCProvider.TAG + "SQL", sql); //$NON-NLS-1$
			Log.d(BNCProvider.TAG + "ARGS", SqlUNetProvider.argsToString(selectionArgs)); //$NON-NLS-1$
		}

		// do query
		try
		{
			return this.db.query(table, projection, selection, selectionArgs, groupBy, null, sortOrder);
		}
		catch (SQLiteException e)
		{
			Log.e(TAG, "Bnc provider query failed", e); //$NON-NLS-1$
			return null;
		}
	}
}
