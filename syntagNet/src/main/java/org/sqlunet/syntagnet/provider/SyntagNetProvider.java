/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.syntagnet.provider;

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
import org.sqlunet.syntagnet.provider.SyntagNetContract.SnCollocations;
import org.sqlunet.syntagnet.provider.SyntagNetContract.SnCollocations_X;

import androidx.annotation.NonNull;

/**
 * SyntagNet provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SyntagNetProvider extends BaseProvider
{
	static private final String TAG = "SyntagNetProvider";

	// C O N T E N T   P R O V I D E R   A U T H O R I T Y

	static private final String AUTHORITY = makeAuthority("syntagnet_authority");

	// U R I M A T C H E R

	// uri matcher
	static private final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static
	{
		matchURIs();
	}

	// table codes
	static private final int COLLOCATIONS = 10;

	// join codes
	static private final int COLLOCATIONS_X = 100;

	static private void matchURIs()
	{
		SyntagNetProvider.uriMatcher.addURI(AUTHORITY, SnCollocations.TABLE, SyntagNetProvider.COLLOCATIONS);
		SyntagNetProvider.uriMatcher.addURI(AUTHORITY, SnCollocations_X.TABLE, SyntagNetProvider.COLLOCATIONS_X);
	}

	@NonNull
	static public String makeUri(final String table)
	{
		return BaseProvider.SCHEME + AUTHORITY + '/' + table;
	}

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public SyntagNetProvider()
	{
		//
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

	@Override
	public String getType(@NonNull final Uri uri)
	{
		switch (SyntagNetProvider.uriMatcher.match(uri))
		{
			// T A B L E S
			case COLLOCATIONS:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + SnCollocations.TABLE;
			case COLLOCATIONS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + SnCollocations_X.TABLE;

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
				openReadOnly();
			}
			catch (SQLiteCantOpenDatabaseException e)
			{
				return null;
			}
		}

		// choose the table to query and a sort order based on the code returned for the incoming URI
		String actualSelection = selection;
		@SuppressWarnings("UnnecessaryLocalVariable") String actualSortOrder = sortOrder;
		final int code = SyntagNetProvider.uriMatcher.match(uri);
		Log.d(SyntagNetProvider.TAG + "URI", String.format("%s (code %s)\n", uri, code));
		String groupBy = null;
		String table;
		switch (code)
		{
			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case COLLOCATIONS:
				table = SnCollocations.TABLE;
				if (actualSelection != null)
				{
					actualSelection += " AND ";
				}
				else
				{
					actualSelection = "";
				}
				actualSelection += SnCollocations.COLLOCATIONID + " = ?";
				break;

			// J O I N S

			case COLLOCATIONS_X:
				table = "syntagms " + //
						"JOIN words AS " + SyntagNetContract.W1 + " ON (word1id = " + SyntagNetContract.W1 + ".wordid) " + //
						"JOIN words AS " + SyntagNetContract.W2 + " ON (word2id = " + SyntagNetContract.W2 + ".wordid) " + //
						"JOIN synsets AS " + SyntagNetContract.S1 + " ON (synset1id = " + SyntagNetContract.S1 + ".synsetid) " + //
						"JOIN synsets AS " + SyntagNetContract.S2 + " ON (synset2id = " + SyntagNetContract.S2 + ".synsetid)";
				break;


			default:
			case UriMatcher.NO_MATCH:
				throw new RuntimeException("Malformed URI " + uri);
		}

		final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, actualSelection, groupBy, null, actualSortOrder, null);
		logSql(sql, selectionArgs);
		if (BaseProvider.logSql)
		{
			Log.d(SyntagNetProvider.TAG + "SQL", SqlFormatter.format(sql).toString());
			Log.d(SyntagNetProvider.TAG + "ARGS", BaseProvider.argsToString(selectionArgs));
		}

		// do query
		try
		{
			return this.db.rawQuery(sql, selectionArgs);
			//return this.db.query(table, projection, actualSelection, selectionArgs, groupBy, null, actualSortOrder, null);
		}
		catch (SQLiteException e)
		{
			Log.d(TAG + "SQL", sql);
			Log.e(TAG, "SyntagNet provider query failed", e);
			return null;
		}
	}
}
