/*
 * Copyright (c) 2023. Bernard Bou
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
import org.sqlunet.syntagnet.provider.SyntagNetControl.Result;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

	static private void matchURIs()
	{
		uriMatcher.addURI(AUTHORITY, SnCollocations.URI, SyntagNetControl.COLLOCATIONS);
		uriMatcher.addURI(AUTHORITY, SnCollocations_X.URI, SyntagNetControl.COLLOCATIONS_X);
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

	@NonNull
	@Override
	public String getType(@NonNull final Uri uri)
	{
		switch (uriMatcher.match(uri))
		{
			// T A B L E S
			case SyntagNetControl.COLLOCATIONS:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + SnCollocations.URI;
			case SyntagNetControl.COLLOCATIONS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + SnCollocations_X.URI;

			default:
				throw new UnsupportedOperationException("Illegal MIME type");
		}
	}

	// Q U E R Y

	@Nullable
	@SuppressWarnings("boxing")
	@Override
	public Cursor query(@NonNull final Uri uri, final String[] projection0, final String selection0, final String[] selectionArgs0, final String sortOrder0)
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
		final int code = uriMatcher.match(uri);
		Log.d(TAG + "URI", String.format("%s (code %s)\n", uri, code));
		if (code == UriMatcher.NO_MATCH)
		{
			throw new RuntimeException("Malformed URI " + uri);
		}

		Result result = SyntagNetControl.queryMain(code, uri.getLastPathSegment(), projection0, selection0, selectionArgs0);
		if (result != null)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, result.table, result.projection, result.selection, result.groupBy, null, sortOrder0, null);
			logSql(sql, result.selectionArgs);
			if (BaseProvider.logSql)
			{
				Log.d(TAG + "SQL", SqlFormatter.format(sql).toString());
				Log.d(TAG + "ARG", BaseProvider.argsToString(result.selectionArgs == null ? selectionArgs0 : result.selectionArgs));
			}

			// do query
			try
			{
				final Cursor cursor = this.db.rawQuery(sql, result.selectionArgs == null ? selectionArgs0 : result.selectionArgs);
				Log.d(TAG + "COUNT", cursor.getCount() + " items");
				return cursor;
			}
			catch (@NonNull final SQLiteException e)
			{
				Log.d(TAG + "SQL", sql);
				Log.e(TAG, "WordNet provider query failed", e);
			}
		}
		return null;
	}
}
