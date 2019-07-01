/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.predicatematrix.provider;

import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import org.sqlunet.predicatematrix.provider.PredicateMatrixContract.Pm;
import org.sqlunet.provider.BaseProvider;
import org.sqlunet.sql.SqlFormatter;

import androidx.annotation.NonNull;

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

	// table codes
	static private final int PM = 10;

	// join codes
	static private final int PM_X = 11;

	static private void matchURIs()
	{
		PredicateMatrixProvider.uriMatcher.addURI(AUTHORITY, PredicateMatrixContract.Pm.TABLE, PredicateMatrixProvider.PM);
		PredicateMatrixProvider.uriMatcher.addURI(AUTHORITY, PredicateMatrixContract.Pm_X.TABLE, PredicateMatrixProvider.PM_X);
	}

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

	// M I M E

	@Override
	public String getType(@NonNull final Uri uri)
	{
		switch (PredicateMatrixProvider.uriMatcher.match(uri))
		{
			case PM:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PredicateMatrixContract.Pm.TABLE;
			case PM_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + AUTHORITY + '.' + PredicateMatrixContract.Pm_X.TABLE;
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
		final int code = PredicateMatrixProvider.uriMatcher.match(uri);
		Log.d(PredicateMatrixProvider.TAG + "URI", String.format("%s (code %s)\n", uri, code));
		String table;
		switch (code)
		{

			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			// J O I N S

			case PM:
				table = Pm.TABLE;
				break;

			case PM_X:
				table = "pm " + //
						"LEFT JOIN pmroles AS " + PredicateMatrixContract.PMROLE + " USING (pmroleid) " + //
						"LEFT JOIN pmpredicates AS " + PredicateMatrixContract.PMPREDICATE + " USING (pmpredid) " + //
						"LEFT JOIN synsets USING (synsetid) " + //
						"LEFT JOIN vnclasses AS " + PredicateMatrixContract.VNCLASS + " ON vnclassid = " + PredicateMatrixContract.VNCLASS + ".classid " + //
						"LEFT JOIN vnroles AS " + PredicateMatrixContract.VNROLE + " ON vnroleid = " + PredicateMatrixContract.VNROLE + ".roleid " + //
						"LEFT JOIN vnroletypes AS " + PredicateMatrixContract.VNROLETYPE + " ON " + PredicateMatrixContract.VNROLE + ".roletypeid = " + PredicateMatrixContract.VNROLETYPE + ".roletypeid " + //
						"LEFT JOIN pbrolesets AS " + PredicateMatrixContract.PBROLESET + " ON pbrolesetid = " + PredicateMatrixContract.PBROLESET + ".rolesetid " + //
						"LEFT JOIN pbroles AS " + PredicateMatrixContract.PBROLE + " ON pbroleid = " + PredicateMatrixContract.PBROLE + ".roleid " + //
						"LEFT JOIN pbargns AS " + PredicateMatrixContract.PBARG + " ON " + PredicateMatrixContract.PBROLE + ".narg = " + PredicateMatrixContract.PBARG + ".narg " + //
						"LEFT JOIN fnframes AS " + PredicateMatrixContract.FNFRAME + " ON fnframeid = " + PredicateMatrixContract.FNFRAME + ".frameid " + //
						"LEFT JOIN fnfes AS " + PredicateMatrixContract.FNFE + " ON fnfeid = " + PredicateMatrixContract.FNFE + ".feid " + //
						"LEFT JOIN fnfetypes AS " + PredicateMatrixContract.FNFETYPE + " ON " + PredicateMatrixContract.FNFE + ".fetypeid = " + PredicateMatrixContract.FNFETYPE + ".fetypeid " + //
						"LEFT JOIN fnlexunits AS " + PredicateMatrixContract.FNLU + " ON fnluid = " + PredicateMatrixContract.FNLU + ".luid";
				break;

			default:
			case UriMatcher.NO_MATCH:
				throw new RuntimeException("Malformed URI " + uri);
		}

		final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, selection, null, null, sortOrder, null);
		logSql(sql, selectionArgs);
		if (BaseProvider.logSql)
		{
			Log.d(PredicateMatrixProvider.TAG + "SQL", SqlFormatter.format(sql).toString());
			Log.d(PredicateMatrixProvider.TAG + "ARGS", BaseProvider.argsToString(selectionArgs));
		}

		// do query
		try
		{
			return this.db.rawQuery(sql, selectionArgs);
			//return this.db.query(table, projection, selection, selectionArgs, null, null, sortOrder, null);
		}
		catch (SQLiteException e)
		{
			Log.d(TAG + "SQL", sql);
			Log.e(TAG, "PropBank provider query failed", e);
			return null;
		}
	}
}
