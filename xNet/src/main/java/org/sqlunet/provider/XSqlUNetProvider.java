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
import org.sqlunet.provider.XSqlUNetContract.PredicateMatrix_PropBank;
import org.sqlunet.provider.XSqlUNetContract.PredicateMatrix_VerbNet;
import org.sqlunet.provider.XSqlUNetContract.Words_FnWords_FnFrames_U;
import org.sqlunet.provider.XSqlUNetContract.Words_FnWords_PbWords_VnWords;
import org.sqlunet.provider.XSqlUNetContract.Words_PbWords_PbRolesets_U;
import org.sqlunet.provider.XSqlUNetContract.Words_VnWords_VnClasses_U;
import org.sqlunet.provider.XSqlUNetContract.Sources;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Extended cross WordNet-FrameNet-PropBank-VerbNet provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class XSqlUNetProvider extends BaseProvider
{
	static private final String TAG = "XSqlUNetProvider";
	// U R I M A T C H E R

	// uri matcher
	static private final UriMatcher uriMatcher;

	// join codes
	static private final int WORDS_FNWORDS_PBWORDS_VNWORDS = 100;
	static private final int PREDICATEMATRIX = 200;
	static private final int PREDICATEMATRIX_VERBNET = 210;
	static private final int PREDICATEMATRIX_PROPBANK = 220;
	static private final int PREDICATEMATRIX_FRAMENET = 230;
	static private final int WORDS_VNWORDS_VNCLASSES_U = 310;
	static private final int WORDS_PBWORDS_PBROLESETS_U = 320;
	static private final int WORDS_FNWORDS_FNFRAMES_U = 330;
	static private final int SOURCES = 400;

	static
	{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		XSqlUNetProvider.uriMatcher.addURI(XSqlUNetContract.AUTHORITY, Words_FnWords_PbWords_VnWords.TABLE, XSqlUNetProvider.WORDS_FNWORDS_PBWORDS_VNWORDS);
		XSqlUNetProvider.uriMatcher.addURI(XSqlUNetContract.AUTHORITY, PredicateMatrix.TABLE, XSqlUNetProvider.PREDICATEMATRIX);
		XSqlUNetProvider.uriMatcher.addURI(XSqlUNetContract.AUTHORITY, PredicateMatrix_VerbNet.TABLE, XSqlUNetProvider.PREDICATEMATRIX_VERBNET);
		XSqlUNetProvider.uriMatcher.addURI(XSqlUNetContract.AUTHORITY, PredicateMatrix_PropBank.TABLE, XSqlUNetProvider.PREDICATEMATRIX_PROPBANK);
		XSqlUNetProvider.uriMatcher.addURI(XSqlUNetContract.AUTHORITY, PredicateMatrix_FrameNet.TABLE, XSqlUNetProvider.PREDICATEMATRIX_FRAMENET);
		XSqlUNetProvider.uriMatcher.addURI(XSqlUNetContract.AUTHORITY, Words_VnWords_VnClasses_U.TABLE, XSqlUNetProvider.WORDS_VNWORDS_VNCLASSES_U);
		XSqlUNetProvider.uriMatcher.addURI(XSqlUNetContract.AUTHORITY, Words_PbWords_PbRolesets_U.TABLE, XSqlUNetProvider.WORDS_PBWORDS_PBROLESETS_U);
		XSqlUNetProvider.uriMatcher.addURI(XSqlUNetContract.AUTHORITY, Words_FnWords_FnFrames_U.TABLE, XSqlUNetProvider.WORDS_FNWORDS_FNFRAMES_U);
		XSqlUNetProvider.uriMatcher.addURI(XSqlUNetContract.AUTHORITY, XSqlUNetContract.Sources.TABLE, XSqlUNetProvider.SOURCES);
	}

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public XSqlUNetProvider()
	{
	}

	// M I M E

	@Override
	public String getType(final Uri uri)
	{
		switch (XSqlUNetProvider.uriMatcher.match(uri))
		{
			case WORDS_FNWORDS_PBWORDS_VNWORDS:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + XSqlUNetContract.AUTHORITY + '.' + Words_FnWords_PbWords_VnWords.TABLE;
			case PREDICATEMATRIX:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + XSqlUNetContract.AUTHORITY + '.' + PredicateMatrix.TABLE;
			case PREDICATEMATRIX_VERBNET:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + XSqlUNetContract.AUTHORITY + '.' + PredicateMatrix_VerbNet.TABLE;
			case PREDICATEMATRIX_PROPBANK:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + XSqlUNetContract.AUTHORITY + '.' + PredicateMatrix_PropBank.TABLE;
			case PREDICATEMATRIX_FRAMENET:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + XSqlUNetContract.AUTHORITY + '.' + PredicateMatrix_FrameNet.TABLE;
			case WORDS_VNWORDS_VNCLASSES_U:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + XSqlUNetContract.AUTHORITY + '.' + Words_VnWords_VnClasses_U.TABLE;
			case WORDS_PBWORDS_PBROLESETS_U:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + XSqlUNetContract.AUTHORITY + '.' + Words_PbWords_PbRolesets_U.TABLE;
			case WORDS_FNWORDS_FNFRAMES_U:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + XSqlUNetContract.AUTHORITY + '.' + Words_FnWords_FnFrames_U.TABLE;
			case SOURCES:
				return BaseProvider.VENDOR + ".android.cursor.dir/" + BaseProvider.VENDOR + '.' + XSqlUNetContract.AUTHORITY + '.' + Sources.TABLE;
			default:
				throw new UnsupportedOperationException("Illegal MIME type");
		}
	}

	// Q U E R Y

	/**
	 * Query
	 *
	 * @param uri           uri
	 * @param projection    projection
	 * @param selection     selection
	 * @param selectionArgs selection arguments
	 * @param sortOrder     sort order
	 * @return cursor
	 */
	@SuppressWarnings("boxing")
	@Override
	public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder)
	{
		if (this.db == null)
		{
			open();
		}

		// choose the table to query and a sort order based on the code returned for the incoming URI
		final int code = XSqlUNetProvider.uriMatcher.match(uri);
		Log.d(XSqlUNetProvider.TAG + "URI", String.format("%s (code %s)\n", uri, code));
		String groupBy = null;
		String table;
		switch (code)
		{
			case PREDICATEMATRIX:
				// table = "pm";
				table = "pmvn " + //
						"LEFT JOIN pnpb USING (wordid)" + //
						"LEFT JOIN pnfn USING (wordid)" //
				;
				break;

			case PREDICATEMATRIX_VERBNET:
				table = "pmvn";
				break;

			case PREDICATEMATRIX_PROPBANK:
				table = "pmpb";
				break;

			case PREDICATEMATRIX_FRAMENET:
				table = "pmfn";
				break;

			case SOURCES:
				table = "sources";
				break;

			// J O I N S

			case WORDS_FNWORDS_PBWORDS_VNWORDS:
				table = "words AS " + XSqlUNetContract.WORD + ' ' + //
						"LEFT JOIN senses AS " + XSqlUNetContract.SENSE + " USING (wordid) " + //
						"LEFT JOIN synsets AS " + XSqlUNetContract.SYNSET + " USING (synsetid) " + //
						"LEFT JOIN postypes AS " + XSqlUNetContract.POS + " USING (pos) " + //
						"LEFT JOIN casedwords USING (wordid,casedwordid) " + //
						"LEFT JOIN lexdomains USING (lexdomainid) " + //
						"LEFT JOIN fnwords USING (wordid) " + //
						"LEFT JOIN vnwords USING (wordid) " + //
						"LEFT JOIN pbwords USING (wordid)";
				groupBy = "synsetid";
				break;

			case WORDS_VNWORDS_VNCLASSES_U:
			{
				final String table1 = "pmvn " + //
						"INNER JOIN vnclasses USING (classid) " + //
						"LEFT JOIN synsets USING (synsetid)";
				final String table2 = "vnwords INNER JOIN " + //
						"vnclassmembersenses USING (vnwordid) " + //
						"INNER JOIN vnclasses USING (classid)";
				final String[] unionProjection = {"wordid", "synsetid", "classid", "class", "classtag", "definition"};
				final String[] tableProjection = {"wordid", "synsetid", "classid", "class", "classtag"};
				final String[] groupByArray = {"wordid", "synsetid", "classid"};
				final String query = makeQuery(table1, table2, tableProjection, unionProjection, projection, selection, groupByArray, sortOrder, "vn");
				Log.d(XSqlUNetProvider.TAG + "PM-VN", query);
				return raw(query, selectionArgs);
			}

			case WORDS_PBWORDS_PBROLESETS_U:
			{
				final String table1 = "pmpb " + //
						"INNER JOIN pbrolesets USING (rolesetid) " + //
						"LEFT JOIN synsets USING (synsetid)";
				final String table2 = "pbwords " + //
						"INNER JOIN pbrolesets USING (pbwordid)";
				final String[] unionProjection = {"wordid", "synsetid", "rolesetid", "rolesetname", "rolesethead", "rolesetdescr", "definition"};
				final String[] tableProjection = {"wordid", "rolesetid", "rolesetname", "rolesethead", "rolesetdescr"};
				final String[] groupByArray = {"wordid", "synsetid", "rolesetid"};
				final String query = makeQuery(table1, table2, tableProjection, unionProjection, projection, selection, groupByArray, sortOrder, "pb");
				Log.d(XSqlUNetProvider.TAG + "PM-PB", query);
				return raw(query, selectionArgs);
			}

			case WORDS_FNWORDS_FNFRAMES_U:
			{
				final String table1 = "pmfn " + //
						"INNER JOIN fnframes USING (frameid) " + //
						"LEFT JOIN fnlexunits USING (luid,frameid) " + //
						"LEFT JOIN synsets USING (synsetid)";
				final String table2 = "fnwords " + //
						"INNER JOIN fnlexemes USING (fnwordid) " + //
						"INNER JOIN fnlexunits USING (luid,posid) " + //
						"INNER JOIN fnframes USING (frameid)";
				final String[] unionProjection = {"wordid", "synsetid", "frameid", "frame", "framedefinition", "luid", "lexunit", "ludefinition", "definition"};
				final String[] tableProjection = {"wordid", "frameid", "frame", "framedefinition", "luid", "lexunit", "ludefinition"};
				final String[] groupByArray = {"wordid", "synsetid", "frameid"};
				final String query = makeQuery(table1, table2, tableProjection, unionProjection, projection, selection, groupByArray, sortOrder, "fn");
				Log.d(XSqlUNetProvider.TAG + "PM-FN", query);
				return raw(query, selectionArgs);
			}

			default:
			case UriMatcher.NO_MATCH:
				throw new RuntimeException("Malformed URI " + uri);
		}

		if (BaseProvider.debugSql)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, selection, groupBy, null, sortOrder, null);
			Log.d(XSqlUNetProvider.TAG + "SQL", sql);
			Log.d(XSqlUNetProvider.TAG + "ARGS", BaseProvider.argsToString(selectionArgs));
		}

		// do query
		try
		{
			return this.db.query(table, projection, selection, selectionArgs, groupBy, null, sortOrder, null);
		}
		catch (final SQLiteException e)
		{
			final String sql = SQLiteQueryBuilder.buildQueryString(false, table, projection, selection, groupBy, null, sortOrder, null);
			Log.d(TAG + "SQL", sql);
			Log.e(XSqlUNetProvider.TAG, "XSqlUNet provider query failed", e);
			return null;
		}
	}

	/**
	 * Make union query
	 *
	 * @param table1           table1
	 * @param table2           table2
	 * @param tableProjection  table projection
	 * @param unionProjection0 union projection
	 * @param projection       projection
	 * @param selection        selection
	 * @param groupBys         group by
	 * @param sortOrder        sort
	 * @param tag              tag
	 * @return union sql
	 */
	private String makeQuery(final String table1, final String table2, //
			final String[] tableProjection, final String[] unionProjection0, final String[] projection, //
			final String selection, //
			final String[] groupBys, final String sortOrder, final String tag)
	{
		final String[] actualUnionProjection = BaseProvider.appendProjection(unionProjection0, "source");
		final List<String> table1ProjectionList = Arrays.asList(unionProjection0);
		final List<String> table2ProjectionList = Arrays.asList(tableProjection);

		// predicate matrix
		final SQLiteQueryBuilder pmSubQueryBuilder = new SQLiteQueryBuilder();
		pmSubQueryBuilder.setTables(table1);
		final String pmSubquery = pmSubQueryBuilder.buildUnionSubQuery("source", //
				actualUnionProjection, //
				new HashSet<>(table1ProjectionList), //
				0, //
				"pm" + tag, //
				selection, //
				null, //
				null);

		// sqlunet table
		final SQLiteQueryBuilder sqlunetSubQueryBuilder = new SQLiteQueryBuilder();
		sqlunetSubQueryBuilder.setTables(table2);
		final String sqlunetSubquery = sqlunetSubQueryBuilder.buildUnionSubQuery("source", //
				actualUnionProjection, //
				new HashSet<>(table2ProjectionList), //
				0, //
				tag, //
				selection, //
				null, //
				null);

		// union
		final SQLiteQueryBuilder uQueryBuilder = new SQLiteQueryBuilder();
		uQueryBuilder.setDistinct(true);
		final String uQuery = uQueryBuilder.buildUnionQuery(new String[]{pmSubquery, sqlunetSubquery}, null, null);

		// embed
		final SQLiteQueryBuilder embeddingQueryBuilder = new SQLiteQueryBuilder();
		embeddingQueryBuilder.setTables('(' + uQuery + ')');
		final String[] resultProjection = BaseProvider.prependProjection(projection, "GROUP_CONCAT(DISTINCT source) AS sources");

		// group by
		String[] actualGroupBys = groupBys;
		if (actualGroupBys == null)
		{
			actualGroupBys = new String[projection.length];
			for (int i = 0; i < projection.length; i++)
			{
				actualGroupBys[i] = projection[i].replaceFirst("\\sAS\\s*.*$", "");
			}
		}
		String groupBy = TextUtils.join(",", actualGroupBys);
		return embeddingQueryBuilder.buildQuery(resultProjection, null, groupBy, null, sortOrder, null);
	}

	/**
	 * Raw query
	 *
	 * @param sql           sql
	 * @param selectionArgs selection arguments
	 * @return cursor
	 */
	private Cursor raw(final String sql, final String... selectionArgs)
	{
		final String[] selectionArgs2 = new String[2 * selectionArgs.length];
		for (int i = 0; i < selectionArgs.length; i++)
		{
			selectionArgs2[2 * i] = selectionArgs2[2 * i + 1] = selectionArgs[i];
		}

		if (BaseProvider.debugSql)
		{
			Log.d(XSqlUNetProvider.TAG + "SQL", sql);
			Log.d(XSqlUNetProvider.TAG + "ARGS", BaseProvider.argsToString(selectionArgs2));
		}

		// do query
		try
		{
			return this.db.rawQuery(sql, selectionArgs2);
		}
		catch (final SQLiteException e)
		{
			Log.e(XSqlUNetProvider.TAG, "XSqlUNet provider query failed", e);
			return null;
		}
	}
}
