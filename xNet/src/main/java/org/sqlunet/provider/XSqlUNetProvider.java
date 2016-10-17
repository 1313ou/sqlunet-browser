package org.sqlunet.provider;

import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.sqlunet.provider.XSqlUNetContract.PredicateMatrix;
import org.sqlunet.provider.XSqlUNetContract.PredicateMatrix_FrameNet;
import org.sqlunet.provider.XSqlUNetContract.PredicateMatrix_Propbank;
import org.sqlunet.provider.XSqlUNetContract.PredicateMatrix_VerbNet;
import org.sqlunet.provider.XSqlUNetContract.Words_FnWords_FnFrames_U;
import org.sqlunet.provider.XSqlUNetContract.Words_FnWords_PbWords_VnWords;
import org.sqlunet.provider.XSqlUNetContract.Words_PbWords_PbRolesets_U;
import org.sqlunet.provider.XSqlUNetContract.Words_VnWords_VnClasses_U;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Cross WordNet-FrameNet-Propbank-VerbNet provider
 *
 * @author Bernard Bou
 */
public class XSqlUNetProvider extends SqlUNetProvider
{
	static private final String TAG = "XSqlUNetProvider"; //$NON-NLS-1$

	// U R I M A T C H E R

	// uri matcher
	private static final UriMatcher uriMatcher;

	// join codes
	private static final int WORDS_FNWORDS_PBWORDS_VNWORDS = 100;
	private static final int PREDICATEMATRIX = 200;
	private static final int PREDICATEMATRIX_VERBNET = 210;
	private static final int PREDICATEMATRIX_PROPBANK = 220;
	private static final int PREDICATEMATRIX_FRAMENET = 230;
	private static final int WORDS_VNWORDS_VNCLASSES_U = 310;
	private static final int WORDS_PBWORDS_PBROLESETS_U = 320;
	private static final int WORDS_FNWORDS_FNFRAMES_U = 330;

	static
	{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		XSqlUNetProvider.uriMatcher.addURI(XSqlUNetContract.AUTHORITY, Words_FnWords_PbWords_VnWords.TABLE, XSqlUNetProvider.WORDS_FNWORDS_PBWORDS_VNWORDS);
		XSqlUNetProvider.uriMatcher.addURI(XSqlUNetContract.AUTHORITY, PredicateMatrix.TABLE, XSqlUNetProvider.PREDICATEMATRIX);
		XSqlUNetProvider.uriMatcher.addURI(XSqlUNetContract.AUTHORITY, PredicateMatrix_VerbNet.TABLE, XSqlUNetProvider.PREDICATEMATRIX_VERBNET);
		XSqlUNetProvider.uriMatcher.addURI(XSqlUNetContract.AUTHORITY, PredicateMatrix_Propbank.TABLE, XSqlUNetProvider.PREDICATEMATRIX_PROPBANK);
		XSqlUNetProvider.uriMatcher.addURI(XSqlUNetContract.AUTHORITY, PredicateMatrix_FrameNet.TABLE, XSqlUNetProvider.PREDICATEMATRIX_FRAMENET);
		XSqlUNetProvider.uriMatcher.addURI(XSqlUNetContract.AUTHORITY, Words_VnWords_VnClasses_U.TABLE, XSqlUNetProvider.WORDS_VNWORDS_VNCLASSES_U);
		XSqlUNetProvider.uriMatcher.addURI(XSqlUNetContract.AUTHORITY, Words_PbWords_PbRolesets_U.TABLE, XSqlUNetProvider.WORDS_PBWORDS_PBROLESETS_U);
		XSqlUNetProvider.uriMatcher.addURI(XSqlUNetContract.AUTHORITY, Words_FnWords_FnFrames_U.TABLE, XSqlUNetProvider.WORDS_FNWORDS_FNFRAMES_U);
	}

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public XSqlUNetProvider()
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
		switch (XSqlUNetProvider.uriMatcher.match(uri))
		{
			case WORDS_FNWORDS_PBWORDS_VNWORDS:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + XSqlUNetContract.AUTHORITY + '.' + Words_FnWords_PbWords_VnWords.TABLE; //$NON-NLS-1$
			case PREDICATEMATRIX:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + XSqlUNetContract.AUTHORITY + '.' + PredicateMatrix.TABLE; //$NON-NLS-1$
			case PREDICATEMATRIX_VERBNET:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + XSqlUNetContract.AUTHORITY + '.' + PredicateMatrix_VerbNet.TABLE; //$NON-NLS-1$
			case PREDICATEMATRIX_PROPBANK:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + XSqlUNetContract.AUTHORITY + '.' + PredicateMatrix_Propbank.TABLE; //$NON-NLS-1$
			case PREDICATEMATRIX_FRAMENET:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + XSqlUNetContract.AUTHORITY + '.' + PredicateMatrix_FrameNet.TABLE; //$NON-NLS-1$
			case WORDS_VNWORDS_VNCLASSES_U:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + XSqlUNetContract.AUTHORITY + '.' + Words_VnWords_VnClasses_U.TABLE; //$NON-NLS-1$
			case WORDS_PBWORDS_PBROLESETS_U:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + XSqlUNetContract.AUTHORITY + '.' + Words_PbWords_PbRolesets_U.TABLE; //$NON-NLS-1$
			case WORDS_FNWORDS_FNFRAMES_U:
				return SqlUNetContract.VENDOR + ".android.cursor.dir/" + SqlUNetContract.VENDOR + '.' + XSqlUNetContract.AUTHORITY + '.' + Words_FnWords_FnFrames_U.TABLE; //$NON-NLS-1$

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
	public Cursor query(final Uri uri, final String[] projection0, final String selection, final String[] selectionArgs, final String sortOrder)
	{
		if (this.db == null)
		{
			open();
		}

		// choose the table to query and a sort order based on the code returned for the incoming URI
		String table;
		String groupBy = null;
		final int code = XSqlUNetProvider.uriMatcher.match(uri);
		Log.d(XSqlUNetProvider.TAG + "URI", String.format("%s (code %s)\n", uri, code)); //$NON-NLS-1$ //$NON-NLS-2$
		switch (code)
		{
			// J O I N S

			case WORDS_FNWORDS_PBWORDS_VNWORDS:
				table = "words AS w " + // //$NON-NLS-1$
						"LEFT JOIN senses AS s USING (wordid) " + // //$NON-NLS-1$
						"LEFT JOIN casedwords AS c USING (wordid,casedwordid) " + // //$NON-NLS-1$
						"LEFT JOIN synsets AS y USING (synsetid) " + // //$NON-NLS-1$
						"LEFT JOIN postypes AS p USING (pos) " + // //$NON-NLS-1$
						"LEFT JOIN lexdomains USING (lexdomainid) " + // //$NON-NLS-1$
						"LEFT JOIN fnwords USING (wordid) " + // //$NON-NLS-1$
						"LEFT JOIN vnwords USING (wordid) " + // //$NON-NLS-1$
						"LEFT JOIN pbwords USING (wordid)"; //$NON-NLS-1$
				groupBy = "synsetid"; //$NON-NLS-1$
				break;

			case PREDICATEMATRIX:
				// table = "pm";
				table = "pmvn " + // //$NON-NLS-1$
						"LEFT JOIN pnpb USING (wordid)" + // //$NON-NLS-1$
						"LEFT JOIN pnfn USING (wordid)" // //$NON-NLS-1$
				;
				break;

			case PREDICATEMATRIX_VERBNET:
				table = "pmvn"; //$NON-NLS-1$
				break;

			case PREDICATEMATRIX_PROPBANK:
				table = "pmpb"; //$NON-NLS-1$
				break;

			case PREDICATEMATRIX_FRAMENET:
				table = "pmfn"; //$NON-NLS-1$
				break;

			case WORDS_VNWORDS_VNCLASSES_U:
			{
				final String table1 = "pmvn INNER JOIN vnclasses ON vnclassid = classid LEFT JOIN synsets USING (synsetid)"; //$NON-NLS-1$
				final String table2 = "vnwords INNER JOIN vnclassmembersenses USING (vnwordid) INNER JOIN vnclasses USING (classid)"; //$NON-NLS-1$
				final String[] unionProjection = new String[]{"wordid", "synsetid", "classid", "class", "classtag", "definition"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
				final String[] tableProjection = new String[]{"wordid", "synsetid", "classid", "class", "classtag"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				final String[] groupBy0 = new String[]{"wordid", "synsetid", "classid"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				final String query = makeQuery(table1, table2, tableProjection, unionProjection, projection0, selection, groupBy0, sortOrder, "vn"); //$NON-NLS-1$
				Log.d(XSqlUNetProvider.TAG + "PM-VN", query); //$NON-NLS-1$

				return raw(query, selectionArgs);
			}

			case WORDS_PBWORDS_PBROLESETS_U:
			{
				final String table1 = "pmpb INNER JOIN pbrolesets ON pbrolesetid = rolesetid LEFT JOIN synsets USING (synsetid)"; //$NON-NLS-1$
				final String table2 = "pbwords INNER JOIN pbrolesets USING (pbwordid)"; //$NON-NLS-1$
				final String[] unionProjection = new String[]{"wordid", "synsetid", "rolesetid", "rolesetname", "rolesethead", "rolesetdescr", "definition"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
				final String[] tableProjection = new String[]{"wordid", "rolesetid", "rolesetname", "rolesethead", "rolesetdescr"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				final String[] groupBy0 = new String[]{"wordid", "synsetid", "rolesetid"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				final String query = makeQuery(table1, table2, tableProjection, unionProjection, projection0, selection, groupBy0, sortOrder, "pb"); //$NON-NLS-1$
				Log.d(XSqlUNetProvider.TAG + "PM-PB", query); //$NON-NLS-1$

				return raw(query, selectionArgs);
			}

			case WORDS_FNWORDS_FNFRAMES_U:
			{
				final String table1 = "pmfn INNER JOIN fnframes ON frameid = fnframeid LEFT JOIN fnlexemes USING (fnwordid) LEFT JOIN fnlexunits USING (luid,frameid) LEFT JOIN synsets USING (synsetid)"; //$NON-NLS-1$
				final String table2 = "fnwords INNER JOIN fnlexemes USING (fnwordid) INNER JOIN fnlexunits USING (luid,posid) INNER JOIN fnframes USING (frameid)"; //$NON-NLS-1$
				final String[] unionProjection = new String[]{"wordid", "synsetid", "frameid", "frame", "framedefinition", "luid", "lexunit", "ludefinition", "definition"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
				final String[] tableProjection = new String[]{"wordid", "frameid", "frame", "framedefinition", "luid", "lexunit", "ludefinition"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
				final String[] groupBy0 = new String[]{"wordid", "synsetid", "frameid"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				final String query = makeQuery(table1, table2, tableProjection, unionProjection, projection0, selection, groupBy0, sortOrder, "fn"); //$NON-NLS-1$
				Log.d(XSqlUNetProvider.TAG + "PM-FN", query); //$NON-NLS-1$

				return raw(query, selectionArgs);
			}

			default:
			case UriMatcher.NO_MATCH:
				throw new RuntimeException("Malformed URI " + uri); //$NON-NLS-1$
		}

		if (SqlUNetProvider.debugSql)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection0, selection, groupBy, null, sortOrder, null);
			Log.d(XSqlUNetProvider.TAG + "SQL", sql); //$NON-NLS-1$
			Log.d(XSqlUNetProvider.TAG + "ARGS", SqlUNetProvider.argsToString(selectionArgs)); //$NON-NLS-1$
		}

		// do query
		try
		{
			return this.db.query(table, projection0, selection, selectionArgs, groupBy, null, sortOrder);
		}
		catch (final SQLiteException e)
		{
			Log.e(XSqlUNetProvider.TAG, "XSqlUNet provider query failed", e); //$NON-NLS-1$
			return null;
		}
	}

	private String makeQuery(final String table1, final String table2, final String[] tableProjection0, final String[] unionProjection0, final String[] projection0, final String selection0, final String[] groupBys0, final String sortOrder0, final String tag0)
	{
		final String[] unionProjection = SqlUNetProvider.appendProjection(unionProjection0, "source"); //$NON-NLS-1$
		final List<String> table1ProjectionList = Arrays.asList(unionProjection0);
		final List<String> table2ProjectionList = Arrays.asList(tableProjection0);

		// predicate matrix
		final SQLiteQueryBuilder pmSubQueryBuilder = new SQLiteQueryBuilder();
		pmSubQueryBuilder.setTables(table1);
		final String pmSubquery = pmSubQueryBuilder.buildUnionSubQuery("source", // //$NON-NLS-1$
				unionProjection, //
				new HashSet<>(table1ProjectionList), //
				0, //
				"pm" + tag0, // //$NON-NLS-1$
				selection0, //
				null, //
				null);

		// sqlunet table
		final SQLiteQueryBuilder sqlunetSubQueryBuilder = new SQLiteQueryBuilder();
		sqlunetSubQueryBuilder.setTables(table2);
		final String sqlunetSubquery = sqlunetSubQueryBuilder.buildUnionSubQuery("source", // //$NON-NLS-1$
				unionProjection, //
				new HashSet<>(table2ProjectionList), //
				0, //
				tag0, //
				selection0, //
				null, //
				null);

		// union
		final SQLiteQueryBuilder uQueryBuilder = new SQLiteQueryBuilder();
		uQueryBuilder.setDistinct(true);
		final String uQuery = uQueryBuilder.buildUnionQuery(new String[]{pmSubquery, sqlunetSubquery}, null, null);

		// embed
		final SQLiteQueryBuilder embeddingQueryBuilder = new SQLiteQueryBuilder();
		embeddingQueryBuilder.setTables('(' + uQuery + ')');
		final String[] resultProjection = SqlUNetProvider.prependProjection(projection0, "GROUP_CONCAT(DISTINCT source) AS sources"); //$NON-NLS-1$

		// group by
		String[] groupBys = groupBys0;
		if (groupBys == null)
		{
			groupBys = new String[projection0.length];
			for (int i = 0; i < projection0.length; i++)
			{
				groupBys[i] = projection0[i].replaceFirst("\\sAS\\s*.*$", ""); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		String groupBy = TextUtils.join(",", groupBys); //$NON-NLS-1$

		return embeddingQueryBuilder.buildQuery(resultProjection, null, groupBy, null, sortOrder0, null);
	}

	private Cursor raw(final String sql, final String[] selectionArgs)
	{
		final String[] selectionArgs2 = new String[2 * selectionArgs.length];
		for (int i = 0; i < selectionArgs.length; i++)
		{
			selectionArgs2[2 * i] = selectionArgs2[2 * i + 1] = selectionArgs[i];
		}

		if (SqlUNetProvider.debugSql)
		{
			Log.d(XSqlUNetProvider.TAG + "SQL", sql); //$NON-NLS-1$
			Log.d(XSqlUNetProvider.TAG + "ARGS", SqlUNetProvider.argsToString(selectionArgs2)); //$NON-NLS-1$
		}

		// do query
		try
		{
			return this.db.rawQuery(sql, selectionArgs2);
		}
		catch (final SQLiteException e)
		{
			Log.e(XSqlUNetProvider.TAG, "XSqlUNet provider query failed", e); //$NON-NLS-1$
			return null;
		}
	}
}
