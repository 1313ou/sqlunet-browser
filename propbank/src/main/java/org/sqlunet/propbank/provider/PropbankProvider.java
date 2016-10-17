package org.sqlunet.propbank.provider;

import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import org.sqlunet.propbank.provider.PropbankContract.PbRolesets;
import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.provider.SqlUNetProvider;

/**
 * WordNet provider
 *
 * @author Bernard Bou
 */
public class PropbankProvider extends SqlUNetProvider
{
	static private final String TAG = "PropbankProvider"; //$NON-NLS-1$

	// U R I M A T C H E R

	// uri matcher
	private static final UriMatcher uriMatcher;

	// table codes
	private static final int PBROLESET = 10;
	private static final int PBROLESETS = 11;

	// join codes
	private static final int WORDS_PBROLESETS = 100;
	private static final int PBROLESETS_PBROLES = 110;
	private static final int PBROLESETS_PBEXAMPLES = 120;
	private static final int PBROLESETS_PBEXAMPLES_BY_EXAMPLE = 121;

	static
	{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		PropbankProvider.uriMatcher.addURI(PropbankContract.AUTHORITY, PropbankContract.PbRolesets.TABLE, PropbankProvider.PBROLESET);
		PropbankProvider.uriMatcher.addURI(PropbankContract.AUTHORITY, PropbankContract.PbRolesets.TABLE, PropbankProvider.PBROLESETS);
		PropbankProvider.uriMatcher.addURI(PropbankContract.AUTHORITY, PropbankContract.Words_PbRolesets.TABLE, PropbankProvider.WORDS_PBROLESETS);
		PropbankProvider.uriMatcher.addURI(PropbankContract.AUTHORITY, PropbankContract.PbRolesets_PbRoles.TABLE, PropbankProvider.PBROLESETS_PBROLES);
		PropbankProvider.uriMatcher.addURI(PropbankContract.AUTHORITY, PropbankContract.PbRolesets_PbExamples.TABLE, PropbankProvider.PBROLESETS_PBEXAMPLES);
		PropbankProvider.uriMatcher.addURI(PropbankContract.AUTHORITY, PropbankContract.PbRolesets_PbExamples.TABLE_BY_EXAMPLE, PropbankProvider.PBROLESETS_PBEXAMPLES_BY_EXAMPLE);
	}

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public PropbankProvider()
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
		switch (PropbankProvider.uriMatcher.match(uri))
		{
			case PBROLESET:
				return SqlUNetContract.VENDOR + ".android.cursor.item/" + SqlUNetContract.VENDOR + '.' + PropbankContract.AUTHORITY + '.' + PropbankContract.PbRolesets.TABLE; //$NON-NLS-1$
			case PBROLESETS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + PropbankContract.AUTHORITY + '.' + PropbankContract.PbRolesets.TABLE; //$NON-NLS-1$
			case WORDS_PBROLESETS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + PropbankContract.AUTHORITY + '.' + PropbankContract.Words_PbRolesets.TABLE; //$NON-NLS-1$
			case PBROLESETS_PBROLES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + PropbankContract.AUTHORITY + '.' + PropbankContract.PbRolesets_PbRoles.TABLE; //$NON-NLS-1$
			case PBROLESETS_PBEXAMPLES:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + PropbankContract.AUTHORITY + '.' + PropbankContract.PbRolesets_PbExamples.TABLE; //$NON-NLS-1$
			case PBROLESETS_PBEXAMPLES_BY_EXAMPLE:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + PropbankContract.AUTHORITY + '.' + PropbankContract.PbRolesets_PbExamples.TABLE_BY_EXAMPLE; //$NON-NLS-1$

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
	public Cursor query(final Uri uri, final String[] projection, final String selection0, final String[] selectionArgs, final String sortOrder0)
	{
		if (this.db == null)
		{
			open();
		}

		// choose the table to query and a sort order based on the code returned for the incoming URI
		String table;
		String selection = selection0;
		String groupBy = null;
		String sortOrder = sortOrder0;
		final int code = PropbankProvider.uriMatcher.match(uri);
		Log.d(PropbankProvider.TAG + "URI", String.format("%s (code %s)\n", uri, code)); //$NON-NLS-1$ //$NON-NLS-2$
		switch (code)
		{

			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case PBROLESET:
				table = PbRolesets.TABLE;
				if (selection != null)
				{
					selection += " AND "; //$NON-NLS-1$
				}
				else
				{
					selection = ""; //$NON-NLS-1$
				}
				selection += PbRolesets.ROLESETID + " = ?"; //$NON-NLS-1$
				break;

			case PBROLESETS:
				table = PbRolesets.TABLE;
				break;

			// J O I N S

			case WORDS_PBROLESETS:
				table = "words " + // //$NON-NLS-1$
						"INNER JOIN pbwords USING (wordid) " + // //$NON-NLS-1$
						"INNER JOIN pbrolesets USING (pbwordid)"; //$NON-NLS-1$
				break;

			case PBROLESETS_PBROLES:
				table = "pbrolesets " + // //$NON-NLS-1$
						"INNER JOIN pbroles USING (rolesetid) " + // //$NON-NLS-1$
						"LEFT JOIN pbfuncs USING (func) " + // //$NON-NLS-1$
						"LEFT JOIN pbvnthetas USING (theta)"; //$NON-NLS-1$
				sortOrder = "narg"; //$NON-NLS-1$
				break;

			case PBROLESETS_PBEXAMPLES_BY_EXAMPLE:
				groupBy = "e.exampleid"; //$NON-NLS-1$
				//$FALL-THROUGH$
			case PBROLESETS_PBEXAMPLES:
				table = "pbrolesets " + // //$NON-NLS-1$
						"INNER JOIN pbexamples AS e USING (rolesetid) " + // //$NON-NLS-1$
						"LEFT JOIN pbrels AS r USING (exampleid) " + // //$NON-NLS-1$
						"LEFT JOIN pbargs AS a USING (exampleid) " + // //$NON-NLS-1$
						"LEFT JOIN pbfuncs AS f ON (a.func = f.func) " + // //$NON-NLS-1$
						"LEFT JOIN pbaspects USING (aspect) " + // //$NON-NLS-1$
						"LEFT JOIN pbforms USING (form) " + // //$NON-NLS-1$
						"LEFT JOIN pbtenses USING (tense) " + // //$NON-NLS-1$
						"LEFT JOIN pbvoices USING (voice) " + // //$NON-NLS-1$
						"LEFT JOIN pbpersons USING (person) " + // //$NON-NLS-1$
						"LEFT JOIN pbroles USING (rolesetid,narg) " + // //$NON-NLS-1$
						"LEFT JOIN pbvnthetas USING (theta)"; //$NON-NLS-1$
				sortOrder = "e.exampleid,narg"; //$NON-NLS-1$
				break;

			default:
			case UriMatcher.NO_MATCH:
				throw new RuntimeException("Malformed URI " + uri); //$NON-NLS-1$
		}

		if (SqlUNetProvider.debugSql)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, selection, groupBy, null, sortOrder, null);
			Log.d(PropbankProvider.TAG + "SQL", sql); //$NON-NLS-1$
			Log.d(PropbankProvider.TAG + "ARGS", SqlUNetProvider.argsToString(selectionArgs)); //$NON-NLS-1$
		}

		// do query
		try
		{
			return this.db.query(table, projection, selection, selectionArgs, groupBy, null, sortOrder);
		}
		catch (SQLiteException e)
		{
			Log.e(TAG, "Propbank provider query failed", e); //$NON-NLS-1$
			return null;
		}
	}
}
