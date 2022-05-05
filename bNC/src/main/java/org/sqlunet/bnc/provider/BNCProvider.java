/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.bnc.provider;

import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import org.sqlunet.provider.BaseProvider;
import org.sqlunet.sql.SqlFormatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * WordNet provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class BNCProvider extends BaseProvider
{
	static private final String TAG = "BNCProvider";

	// C O N T E N T   P R O V I D E R   A U T H O R I T Y

	static private final String AUTHORITY = makeAuthority("bnc_authority");

	// U R I M A T C H E R

	static private final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static
	{
		matchURIs();
	}

	// table codes
	static private final int BNC = 11;

	// join tables
	static private final int WORDS_BNC = 100;

	static private void matchURIs()
	{
		BNCProvider.uriMatcher.addURI(AUTHORITY, BNCContract.BNCs.TABLE, BNCProvider.BNC);
		BNCProvider.uriMatcher.addURI(AUTHORITY, BNCContract.Words_BNCs.TABLE, BNCProvider.WORDS_BNC);
	}

	@NonNull
	static public String makeUri(@SuppressWarnings("SameParameterValue") final String table)
	{
		return BaseProvider.SCHEME + AUTHORITY + '/' + table;
	}

	// C O N S T R U C T

	/**
	 * Constructor
	 */
	public BNCProvider()
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
		switch (BNCProvider.uriMatcher.match(uri))
		{
			// TABLES
			case BNC:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + BNCContract.BNCs.TABLE;

			// JOINS
			case WORDS_BNC:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + BNCContract.Words_BNCs.TABLE;

			default:
				throw new UnsupportedOperationException("Illegal MIME type");
		}
	}

	// Q U E R Y

	@Nullable
	@SuppressWarnings("boxing")
	@Override
	public Cursor query(@NonNull final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder)
	{
		if (this.db == null)
		{
			try
			{
				openReadOnly();
			}
			catch (SQLiteCantOpenDatabaseException e)
			{
				return null;
			}
		}

		String actualSelection = selection;
		String actualSelection2 = selection;

		// choose the table to query and a sort order based on the code returned for the incoming URI
		final int code = BNCProvider.uriMatcher.match(uri);
		Log.d(BNCProvider.TAG + "URI", String.format("%s (code %s)", uri, code));
		if (code == UriMatcher.NO_MATCH)
		{
			throw new RuntimeException("Malformed URI " + uri);
		}

		String table;
		String table2;
		switch (code)
		{

			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case BNC:
				table = BNCContract.BNCs.TABLE;
				table2 = Sqls.BNCS.TABLE;
				if (actualSelection != null)
				{
					actualSelection += " AND ";
				}
				else
				{
					actualSelection = "";
				}
				actualSelection2 = actualSelection;
				actualSelection += BNCContract.BNCs.POSID + " = ?";
				actualSelection2 += Sqls.BNCS.SELECTION;

				break;

			// J O I N S

			case WORDS_BNC:
				table = "bnc_bncs " + //
						"LEFT JOIN bnc_spwrs USING (wordid, posid) " + //
						"LEFT JOIN bnc_convtasks USING (wordid, posid) " + //
						"LEFT JOIN bnc_imaginfs USING (wordid, posid) ";
				table2 = Sqls.WORDS_BNCS.TABLE;
				break;

			default:
				return null;
		}

		assert equals(table, table2) : table + "!=" + table2;
		assert equals(actualSelection, actualSelection2) : actualSelection + "!=" + actualSelection2;

		final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, actualSelection, null, null, sortOrder, null);
		logSql(sql, selectionArgs);
		if (BaseProvider.logSql)
		{
			Log.d(BNCProvider.TAG + "SQL", SqlFormatter.format(sql).toString());
			Log.d(BNCProvider.TAG + "ARGS", BaseProvider.argsToString(selectionArgs));
		}

		// do query
		try
		{
			return this.db.rawQuery(sql, selectionArgs);
			//return this.db.query(table, projection, actualSelection, selectionArgs, null, null, sortOrder);
		}
		catch (SQLiteException e)
		{
			Log.d(TAG + "SQL", sql);
			Log.e(TAG, "Bnc provider query failed", e);
			return null;
		}
	}

	private static boolean equals(Object a, Object b)
	{
		return (a == b) || (a != null && a.equals(b));
	}
}
