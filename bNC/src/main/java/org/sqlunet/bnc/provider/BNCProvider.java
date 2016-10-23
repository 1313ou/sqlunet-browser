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
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class BNCProvider extends SqlUNetProvider
{
	static private final String TAG = "BNCProvider"; //

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

	@Override
	public String getType(final Uri uri)
	{
		switch (BNCProvider.uriMatcher.match(uri))
		{

			// TABLES

			case BNC:
				return SqlUNetContract.VENDOR + ".android.cursor.item/" + SqlUNetContract.VENDOR + '.' + BNCContract.AUTHORITY + '.' + BNCContract.BNCs.TABLE; //

			// JOINS

			case WORDS_BNC:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + BNCContract.AUTHORITY + '.' + BNCContract.Words_BNCs.TABLE; //

			default:
				throw new UnsupportedOperationException("Illegal MIME type"); //
		}
	}

	// Q U E R Y

	@SuppressWarnings("boxing")
	@Override
	public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder)
	{
		if (this.db == null)
		{
			open();
		}

		String actualSelection = selection;

		// choose the table to query and a sort order based on the code returned for the incoming URI
		final int code = BNCProvider.uriMatcher.match(uri);
		Log.d(BNCProvider.TAG + "URI", String.format("%s (code %s)", uri, code)); //

		String table;
		switch (code)
		{

			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case BNC:
				table = BNCContract.BNCs.TABLE;
				if (actualSelection != null)
				{
					actualSelection += " AND "; //
				}
				else
				{
					actualSelection = ""; //
				}
				actualSelection += BNCContract.BNCs.POS + " = ?"; //
				break;

			// J O I N S

			case WORDS_BNC:
				table = "bncs " + //
						"LEFT JOIN bncspwrs USING (wordid, pos) " + //
						"LEFT JOIN bncconvtasks USING (wordid, pos) " + //
						"LEFT JOIN bncimaginfs USING (wordid, pos) "; //
				break;

			default:
			case UriMatcher.NO_MATCH:
				throw new RuntimeException("Malformed URI " + uri); //
		}

		final String groupBy = null;
		if (SqlUNetProvider.debugSql)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, actualSelection, groupBy, null, sortOrder, null);
			Log.d(BNCProvider.TAG + "SQL", sql); //
			Log.d(BNCProvider.TAG + "ARGS", SqlUNetProvider.argsToString(selectionArgs)); //
		}

		// do query
		try
		{
			return this.db.query(table, projection, actualSelection, selectionArgs, groupBy, null, sortOrder);
		}
		catch (SQLiteException e)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, actualSelection, groupBy, null, sortOrder, null);
			Log.d(TAG + "SQL", sql); //
			Log.e(TAG, "Bnc provider query failed", e); //
			return null;
		}
	}
}
