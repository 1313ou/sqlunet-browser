/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.predicatematrix.provider;

import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import org.sqlunet.predicatematrix.provider.PredicateMatrixControl.Result;
import org.sqlunet.provider.BaseProvider;
import org.sqlunet.sql.SqlFormatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * PredicateMatrix provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PredicateMatrixProvider extends BaseProvider
{
	static private final String TAG = "PMProvider";

	// C O N T E N T   P R O V I D E R   A U T H O R I T Y

	static private final String AUTHORITY = makeAuthority("predicatematrix_authority");

	// U R I M A T C H E R

	static private final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static
	{
		matchURIs();
	}

	static private void matchURIs()
	{
		PredicateMatrixProvider.uriMatcher.addURI(AUTHORITY, PredicateMatrixContract.Pm.URI, PredicateMatrixControl.PM);
		PredicateMatrixProvider.uriMatcher.addURI(AUTHORITY, PredicateMatrixContract.Pm_X.URI, PredicateMatrixControl.PM_X);
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
	public PredicateMatrixProvider()
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
		switch (PredicateMatrixProvider.uriMatcher.match(uri))
		{
			case PredicateMatrixControl.PM:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PredicateMatrixContract.Pm.URI;
			case PredicateMatrixControl.PM_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PredicateMatrixContract.Pm_X.URI;
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

		// choose the table to query and a sort order based on the code returned for the incoming URI
		final int code = PredicateMatrixProvider.uriMatcher.match(uri);
		Log.d(TAG + "URI", String.format("%s (code %s)\n", uri, code));
		if (code == UriMatcher.NO_MATCH)
		{
			throw new RuntimeException("Malformed URI " + uri);
		}

		Result result = PredicateMatrixControl.queryMain(code, projection0, selection0, selectionArgs0);
		if (result != null)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, result.table, result.projection, result.selection, result.groupBy, null, sortOrder0, null);
			logSql(sql, selectionArgs0);
			if (BaseProvider.logSql)
			{
				Log.d(TAG + "SQL", SqlFormatter.format(sql).toString());
				Log.d(TAG + "ARGS", BaseProvider.argsToString(result.selectionArgs == null ? selectionArgs0 : result.selectionArgs));
			}

			// do query
			try
			{
				final Cursor cursor = this.db.rawQuery(sql, result.selectionArgs == null ? selectionArgs0 : result.selectionArgs);
				Log.d(TAG + "COUNT", cursor.getCount() + " items");
				return cursor;
			}
			catch (SQLiteException e)
			{
				Log.d(TAG + "SQL", sql);
				Log.e(TAG, "PropBank provider query failed", e);
			}
		}
		return null;
	}
}
