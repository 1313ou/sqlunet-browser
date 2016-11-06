package org.sqlunet.propbank.provider;

import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import org.sqlunet.provider.BaseProvider;

/**
 * PropBank provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PropBankProvider extends BaseProvider
{
	static private final String TAG = "PropBankProvider";
	// U R I M A T C H E R

	// uri matcher
	private static final UriMatcher uriMatcher;

	// table codes
	private static final int PBROLESET = 10;
	private static final int PBROLESETS = 11;

	// join codes
	private static final int PBROLESETS_X = 100;
	private static final int PBROLESETS_X_BY_ROLESET = 101;
	private static final int WORDS_PBROLESETS = 110;
	private static final int PBROLESETS_PBROLES = 120;
	private static final int PBROLESETS_PBEXAMPLES = 130;
	private static final int PBROLESETS_PBEXAMPLES_BY_EXAMPLE = 131;

	// text search codes
	private static final int LOOKUP_FTS_EXAMPLES = 501;

	static
	{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		PropBankProvider.uriMatcher.addURI(PropBankContract.AUTHORITY, PropBankContract.PbRoleSets.TABLE, PropBankProvider.PBROLESET);
		PropBankProvider.uriMatcher.addURI(PropBankContract.AUTHORITY, PropBankContract.PbRoleSets.TABLE, PropBankProvider.PBROLESETS);
		PropBankProvider.uriMatcher.addURI(PropBankContract.AUTHORITY, PropBankContract.PbRoleSets_X.TABLE, PropBankProvider.PBROLESETS_X);
		PropBankProvider.uriMatcher.addURI(PropBankContract.AUTHORITY, PropBankContract.PbRoleSets_X.TABLE_BY_ROLESET, PropBankProvider.PBROLESETS_X_BY_ROLESET);
		PropBankProvider.uriMatcher.addURI(PropBankContract.AUTHORITY, PropBankContract.Words_PbRoleSets.TABLE, PropBankProvider.WORDS_PBROLESETS);
		PropBankProvider.uriMatcher.addURI(PropBankContract.AUTHORITY, PropBankContract.PbRoleSets_PbRoles.TABLE, PropBankProvider.PBROLESETS_PBROLES);
		PropBankProvider.uriMatcher.addURI(PropBankContract.AUTHORITY, PropBankContract.PbRoleSets_PbExamples.TABLE, PropBankProvider.PBROLESETS_PBEXAMPLES);
		PropBankProvider.uriMatcher.addURI(PropBankContract.AUTHORITY, PropBankContract.PbRoleSets_PbExamples.TABLE_BY_EXAMPLE, PropBankProvider.PBROLESETS_PBEXAMPLES_BY_EXAMPLE);

		PropBankProvider.uriMatcher.addURI(PropBankContract.AUTHORITY, PropBankContract.Lookup_PbExamples.TABLE + "/", PropBankProvider.LOOKUP_FTS_EXAMPLES);
	}

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public PropBankProvider()
	{
		//
	}

	// M I M E

	@Override
	public String getType(final Uri uri)
	{
		switch (PropBankProvider.uriMatcher.match(uri))
		{
			case PBROLESET:
				return BaseProvider.VENDOR + ".android.cursor.item/" + BaseProvider.VENDOR + '.' + PropBankContract.AUTHORITY + '.' + PropBankContract.PbRoleSets.TABLE;
			case PBROLESETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + PropBankContract.AUTHORITY + '.' + PropBankContract.PbRoleSets.TABLE;
			case PBROLESETS_X:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + PropBankContract.AUTHORITY + '.' + PropBankContract.PbRoleSets_X.TABLE;
			case PBROLESETS_X_BY_ROLESET:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + PropBankContract.AUTHORITY + '.' + PropBankContract.PbRoleSets_X.TABLE_BY_ROLESET;
			case WORDS_PBROLESETS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + PropBankContract.AUTHORITY + '.' + PropBankContract.Words_PbRoleSets.TABLE;
			case PBROLESETS_PBROLES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + PropBankContract.AUTHORITY + '.' + PropBankContract.PbRoleSets_PbRoles.TABLE;
			case PBROLESETS_PBEXAMPLES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + PropBankContract.AUTHORITY + '.' + PropBankContract.PbRoleSets_PbExamples.TABLE;
			case PBROLESETS_PBEXAMPLES_BY_EXAMPLE:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + PropBankContract.AUTHORITY + '.' + PropBankContract.PbRoleSets_PbExamples.TABLE_BY_EXAMPLE;
			// S E A R C H
			case LOOKUP_FTS_EXAMPLES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + PropBankContract.AUTHORITY + '.' + PropBankContract.Lookup_PbExamples.TABLE;
			default:
				throw new UnsupportedOperationException("Illegal MIME type");
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

		// choose the table to query and a sort order based on the code returned for the incoming URI
		String actualSelection = selection;
		String actualSortOrder = sortOrder;
		final int code = PropBankProvider.uriMatcher.match(uri);
		Log.d(PropBankProvider.TAG + "URI", String.format("%s (code %s)\n", uri, code));
		String groupBy = null;
		String table;
		switch (code)
		{

			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case PBROLESET:
				table = PropBankContract.PbRoleSets.TABLE;
				if (actualSelection != null)
				{
					actualSelection += " AND ";
				}
				else
				{
					actualSelection = "";
				}
				actualSelection += PropBankContract.PbRoleSets.ROLESETID + " = ?";
				break;

			case PBROLESETS:
				table = PropBankContract.PbRoleSets.TABLE;
				break;

			// J O I N S

			case PBROLESETS_X_BY_ROLESET:
				groupBy = PropBankContract.PbRoleSets_X.ROLESETID;
				//$FALL-THROUGH$
				//noinspection fallthrough
			case PBROLESETS_X:
				table = "pbrolesets " + //
						"LEFT JOIN pbrolesetmembers AS " + PropBankContract.MEMBER + " USING (rolesetid) " + //
						"LEFT JOIN pbwords AS " + PropBankContract.WORD + " ON " + PropBankContract.MEMBER + ".pbwordid = " + PropBankContract.WORD + ".pbwordid";
				break;

			case WORDS_PBROLESETS:
				table = "words " + //
						"INNER JOIN pbwords USING (wordid) " + //
						"INNER JOIN pbrolesets USING (pbwordid)";
				break;

			case PBROLESETS_PBROLES:
				table = "pbrolesets " + //
						"INNER JOIN pbroles USING (rolesetid) " + //
						"LEFT JOIN pbfuncs USING (func) " + //
						"LEFT JOIN pbvnthetas USING (theta)";
				actualSortOrder = "narg";
				break;

			case PBROLESETS_PBEXAMPLES_BY_EXAMPLE:
				groupBy = PropBankContract.EXAMPLE + ".exampleid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case PBROLESETS_PBEXAMPLES:
				table = "pbrolesets " + //
						"INNER JOIN pbexamples AS " + PropBankContract.EXAMPLE + " USING (rolesetid) " + //
						"LEFT JOIN pbrels AS " + PropBankContract.REL + " USING (exampleid) " + //
						"LEFT JOIN pbargs AS " + PropBankContract.ARG + " USING (exampleid) " + //
						"LEFT JOIN pbfuncs AS " + PropBankContract.FUNC + " ON (" + PropBankContract.ARG + ".func = " + PropBankContract.FUNC + ".func) " + //
						"LEFT JOIN pbaspects USING (aspect) " + //
						"LEFT JOIN pbforms USING (form) " + //
						"LEFT JOIN pbtenses USING (tense) " + //
						"LEFT JOIN pbvoices USING (voice) " + //
						"LEFT JOIN pbpersons USING (person) " + //
						"LEFT JOIN pbroles USING (rolesetid,narg) " + //
						"LEFT JOIN pbvnthetas USING (theta)";
				actualSortOrder = PropBankContract.EXAMPLE + ".exampleid,narg";
				break;

			case LOOKUP_FTS_EXAMPLES:
				table = "pbexamples_text_fts4";
				break;

			default:
			case UriMatcher.NO_MATCH:
				throw new RuntimeException("Malformed URI " + uri);
		}

		if (BaseProvider.debugSql)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, actualSelection, groupBy, null, actualSortOrder, null);
			Log.d(PropBankProvider.TAG + "SQL", sql);
			Log.d(PropBankProvider.TAG + "ARGS", BaseProvider.argsToString(selectionArgs));
		}

		// do query
		try
		{
			return this.db.query(table, projection, actualSelection, selectionArgs, groupBy, null, actualSortOrder, null);
		}
		catch (SQLiteException e)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, actualSelection, groupBy, null, actualSortOrder, null);
			Log.d(TAG + "SQL", sql);
			Log.e(TAG, "PropBank provider query failed", e);
			return null;
		}
	}
}
