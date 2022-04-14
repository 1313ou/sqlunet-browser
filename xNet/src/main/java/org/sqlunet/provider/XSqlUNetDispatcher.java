package org.sqlunet.provider;

import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import androidx.annotation.NonNull;

public class XSqlUNetDispatcher
{
	// table codes
	static protected final int PREDICATEMATRIX = 200;
	static protected final int PREDICATEMATRIX_VERBNET = 210;
	static protected final int PREDICATEMATRIX_PROPBANK = 220;
	static protected final int PREDICATEMATRIX_FRAMENET = 230;
	// join codes
	static protected final int WORDS_FNWORDS_PBWORDS_VNWORDS = 100;
	static protected final int WORDS_PBWORDS_VNWORDS = 101;
	static protected final int WORDS_VNWORDS_VNCLASSES = 310;
	static protected final int WORDS_VNWORDS_VNCLASSES_U = 311;
	static protected final int WORDS_PBWORDS_PBROLESETS = 320;
	static protected final int WORDS_PBWORDS_PBROLESETS_U = 321;
	static protected final int WORDS_FNWORDS_FNFRAMES_U = 331;
	static protected final int SOURCES = 400;

	public static Result queryMain(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table = null;
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String orderBy = null;

		switch (code)
		{
			case XSqlUNetDispatcher.PREDICATEMATRIX:
				// table = "pm";
				table = "pmvn " + //
						"LEFT JOIN pnpb USING (wordid)" + //
						"LEFT JOIN pnfn USING (wordid)" //
				;
				break;

			case XSqlUNetDispatcher.PREDICATEMATRIX_VERBNET:
				table = "pmvn";
				break;

			case XSqlUNetDispatcher.PREDICATEMATRIX_PROPBANK:
				table = "pmpb";
				break;

			case XSqlUNetDispatcher.PREDICATEMATRIX_FRAMENET:
				table = "pmfn";
				break;

			case XSqlUNetDispatcher.SOURCES:
				table = "sources";
				break;

			// J O I N S

			case XSqlUNetDispatcher.WORDS_FNWORDS_PBWORDS_VNWORDS:
				table = "words AS " + XSqlUNetContract.WORD + ' ' + //
						"LEFT JOIN senses AS " + XSqlUNetContract.SENSE + " USING (wordid) " + //
						"LEFT JOIN synsets AS " + XSqlUNetContract.SYNSET + " USING (synsetid) " + //
						"LEFT JOIN poses AS " + XSqlUNetContract.POSID + " USING (posid) " + //
						"LEFT JOIN casedwords USING (wordid,casedwordid) " + //
						"LEFT JOIN domains USING (domainid) " + //
						"LEFT JOIN fn_words USING (wordid) " + //
						"LEFT JOIN vn_words USING (wordid) " + //
						"LEFT JOIN pb_words USING (wordid)";
				groupBy = "synsetid";
				break;

			case XSqlUNetDispatcher.WORDS_PBWORDS_VNWORDS:
				table = "words AS " + XSqlUNetContract.WORD + ' ' + //
						"LEFT JOIN senses AS " + XSqlUNetContract.SENSE + " USING (wordid) " + //
						"LEFT JOIN synsets AS " + XSqlUNetContract.SYNSET + " USING (synsetid) " + //
						"LEFT JOIN poses AS " + XSqlUNetContract.POSID + " USING (posid) " + //
						"LEFT JOIN casedwords USING (wordid,casedwordid) " + //
						"LEFT JOIN domains USING (domainid) " + //
						"LEFT JOIN vn_words USING (wordid) " + //
						"LEFT JOIN pb_words USING (wordid)";
				groupBy = "synsetid";
				break;

			case XSqlUNetDispatcher.WORDS_VNWORDS_VNCLASSES:
			{
				table = "vn_words " + //
						"INNER JOIN vn_members_senses USING (wordid) " + //
						"INNER JOIN vn_classes AS " + XSqlUNetContract.CLASS + " USING (classid) " + //
						"LEFT JOIN synsets USING (synsetid)";
				groupBy = "wordid,synsetid,classid";
				break;
			}

			case XSqlUNetDispatcher.WORDS_VNWORDS_VNCLASSES_U:
			{
				final String table1 = "pmvn " + //
						"INNER JOIN vn_classes USING (classid) " + //
						"LEFT JOIN synsets USING (synsetid)";
				final String table2 = "vn_words " + //
						"INNER JOIN vn_members_senses USING (vnwordid) " + //
						"INNER JOIN vn_classes USING (classid)";
				final String[] unionProjection = {"wordid", "synsetid", "classid", "class", "classtag", "definition"};
				final String[] table1Projection = unionProjection;
				final String[] table2Projection = {"wordid", "synsetid", "classid", "class", "classtag"};
				final String[] groupByArray = {"wordid", "synsetid", "classid"};
				//final String query = makeQuery(table1, table2, table1Projection, table2Projection, unionProjection, projection, selection, groupByArray, sortOrder, "vn");
				//Log.d(XSqlUNetProvider.TAG + "PM-VN", query);
				break;
			}

			case XSqlUNetDispatcher.WORDS_PBWORDS_PBROLESETS:
			{
				table = "pb_words " + //
						"INNER JOIN pb_rolesets AS " + XSqlUNetContract.CLASS + " USING (pbwordid)";
				groupBy = "wordid,synsetid,rolesetid";
				break;
			}

			case XSqlUNetDispatcher.WORDS_PBWORDS_PBROLESETS_U:
			{
				final String table1 = "pmpb " + //
						"INNER JOIN pb_rolesets USING (rolesetid) " + //
						"LEFT JOIN synsets USING (synsetid)";
				final String table2 = "pb_words " + //
						"INNER JOIN pb_rolesets USING (pbwordid)";
				final String[] unionProjection = {"wordid", "synsetid", "rolesetid", "rolesetname", "rolesethead", "rolesetdescr", "definition"};
				final String[] table1Projection = unionProjection;
				final String[] table2Projection = {"wordid", "rolesetid", "rolesetname", "rolesethead", "rolesetdescr"};
				final String[] groupByArray = {"wordid", "synsetid", "rolesetid"};
				//final String query = makeQuery(table1, table2, table1Projection, table2Projection, unionProjection, projection, selection, groupByArray, sortOrder, "pb");
				//Log.d(XSqlUNetProvider.TAG + "PM-PB", query);
				break;
			}

			case XSqlUNetDispatcher.WORDS_FNWORDS_FNFRAMES_U:
			{
				final String table1 = "pmfn " + //
						"INNER JOIN fn_frames USING (frameid) " + //
						"LEFT JOIN fn_lexunits USING (luid,frameid) " + //
						"LEFT JOIN synsets USING (synsetid)";
				final String table2 = "fnwords " + //
						"INNER JOIN fn_lexemes USING (fnwordid) " + //
						"INNER JOIN fn_lexunits USING (luid,posid) " + //
						"INNER JOIN fn_frames USING (frameid)";
				final String[] unionProjection = {"wordid", "synsetid", "frameid", "frame", "framedefinition", "luid", "lexunit", "ludefinition", "definition"};
				final String[] table1Projection = unionProjection;
				final String[] table2Projection = {"wordid", "frameid", "frame", "framedefinition", "luid", "lexunit", "ludefinition"};
				final String[] groupByArray = {"wordid", "synsetid", "frameid"};
				//final String query = makeQuery(table1, table2, table1Projection, table2Projection, unionProjection, projection, selection, groupByArray, sortOrder, "fn");
				//Log.d(XSqlUNetProvider.TAG + "PM-FN", query);
				break;
			}

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs, groupBy, orderBy);
	}

	static public class Result
	{
		final String table;
		final String[] projection;
		final String selection;
		final String[] selectionArgs;
		final String groupBy;
		final String orderBy;

		public Result(final String table, final String[] projection, final String selection, final String[] selectionArgs, final String groupBy, final String orderBy)
		{
			this.table = table;
			this.projection = projection;
			this.selection = selection;
			this.selectionArgs = selectionArgs;
			this.groupBy = groupBy;
			this.orderBy = orderBy;
		}
	}

	/**
	 * Make union query
	 *
	 * @param table1           table1
	 * @param table2           table2
	 * @param table1Projection table1 projection
	 * @param table2Projection table2 projection
	 * @param unionProjection  union projection
	 * @param projection       final projection
	 * @param selection        selection
	 * @param groupBys         group by
	 * @param sortOrder        sort
	 * @param tag              tag
	 * @return union sql
	 */
	static String makeQuery(final String table1, final String table2, //
			final String[] table1Projection, final String[] table2Projection, //
			final String[] unionProjection, @NonNull final String[] projection, //
			final String selection, //
			final String[] groupBys, final String sortOrder, final String tag)
	{
		final String[] actualUnionProjection = BaseProvider.appendProjection(unionProjection, "source");
		final List<String> table1ProjectionList = Arrays.asList(table1Projection);
		final List<String> table2ProjectionList = Arrays.asList(table2Projection);

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
	 * Make args
	 *
	 * @param selectionArgs selection arguments
	 * @return cursor
	 */
	static String[] makeSelectionArgs(@NonNull final String... selectionArgs)
	{
		final String[] selectionArgs2 = new String[2 * selectionArgs.length];
		for (int i = 0; i < selectionArgs.length; i++)
		{
			selectionArgs2[2 * i] = selectionArgs2[2 * i + 1] = selectionArgs[i];
		}
		return selectionArgs2;
	}
}
