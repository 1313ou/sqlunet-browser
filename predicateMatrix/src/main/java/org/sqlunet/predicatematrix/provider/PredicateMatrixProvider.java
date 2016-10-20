package org.sqlunet.predicatematrix.provider;

import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import org.sqlunet.predicatematrix.provider.PredicateMatrixContract.Pm;
import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.provider.SqlUNetProvider;

/**
 * PredicateMatrix provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PredicateMatrixProvider extends SqlUNetProvider
{
	static private final String TAG = "PMProvider"; //$NON-NLS-1$

	// U R I M A T C H E R

	// uri matcher
	private static final UriMatcher uriMatcher;

	// table codes
	private static final int PM = 10;

	// join codes
	private static final int PM_X = 11;

	static
	{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		PredicateMatrixProvider.uriMatcher.addURI(PredicateMatrixContract.AUTHORITY, PredicateMatrixContract.Pm.TABLE, PredicateMatrixProvider.PM);
		PredicateMatrixProvider.uriMatcher.addURI(PredicateMatrixContract.AUTHORITY, PredicateMatrixContract.Pm_X.TABLE, PredicateMatrixProvider.PM_X);
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
	public String getType(final Uri uri)
	{
		switch (PredicateMatrixProvider.uriMatcher.match(uri))
		{
			case PM:
				return SqlUNetContract.VENDOR + ".android.cursor.item/" + SqlUNetContract.VENDOR + '.' + PredicateMatrixContract.AUTHORITY + '.' + PredicateMatrixContract.Pm.TABLE; //$NON-NLS-1$
			case PM_X:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + PredicateMatrixContract.AUTHORITY + '.' + PredicateMatrixContract.Pm_X.TABLE; //$NON-NLS-1$

			default:
				throw new UnsupportedOperationException("Illegal MIME type"); //$NON-NLS-1$
		}
	}

	// Q U E R Y

	@SuppressWarnings("boxing")
	@Override
	public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder0)
	{
		if (this.db == null)
		{
			open();
		}

		// choose the table to query and a sort order based on the code returned for the incoming URI
		final int code = PredicateMatrixProvider.uriMatcher.match(uri);
		Log.d(PredicateMatrixProvider.TAG + "URI", String.format("%s (code %s)\n", uri, code)); //$NON-NLS-1$ //$NON-NLS-2$
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
				table = "pm " + // //$NON-NLS-1$
						"LEFT JOIN pmroles AS mr USING (pmroleid) " + // //$NON-NLS-1$
						"LEFT JOIN pmpredicates AS mp USING (pmpredid) " + // //$NON-NLS-1$
						"LEFT JOIN synsets USING (synsetid) " + // //$NON-NLS-1$
						"LEFT JOIN vnclasses AS vc ON vnclassid = vc.classId " + // //$NON-NLS-1$
						"LEFT JOIN vnroles AS vr ON vnroleid = vr.roleId " + // //$NON-NLS-1$
						"LEFT JOIN vnroletypes AS vt ON vr.roletypeid = vt.roletypeid " + // //$NON-NLS-1$
						"LEFT JOIN pbrolesets AS ps ON pbrolesetid = ps.roleSetId " + // //$NON-NLS-1$
						"LEFT JOIN pbroles AS pr ON pbroleid = pr.roleId " + // //$NON-NLS-1$
						"LEFT JOIN pbargns AS pt ON pr.narg = pt.narg " + // //$NON-NLS-1$
						"LEFT JOIN fnframes AS ff ON fnframeid = ff.frameId " + // //$NON-NLS-1$
						"LEFT JOIN fnfes AS fr ON fnfeid = fr.feid " + // //$NON-NLS-1$
						"LEFT JOIN fnfetypes AS ft ON fr.fetypeid = ft.fetypeid " + // //$NON-NLS-1$
						"LEFT JOIN fnlexunits AS fl ON fnluid = fl.luid"; //$NON-NLS-1$
				break;

			default:
			case UriMatcher.NO_MATCH:
				throw new RuntimeException("Malformed URI " + uri); //$NON-NLS-1$
		}

		if (SqlUNetProvider.debugSql)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, selection, null, null, sortOrder0, null);
			Log.d(PredicateMatrixProvider.TAG + "SQL", sql); //$NON-NLS-1$
			Log.d(PredicateMatrixProvider.TAG + "ARGS", SqlUNetProvider.argsToString(selectionArgs)); //$NON-NLS-1$
		}

		// do query
		try
		{
			return this.db.query(table, projection, selection, selectionArgs, null, null, sortOrder0);
		}
		catch (SQLiteException e)
		{
			Log.e(TAG, "Propbank provider query failed", e); //$NON-NLS-1$
			return null;
		}
	}
}
