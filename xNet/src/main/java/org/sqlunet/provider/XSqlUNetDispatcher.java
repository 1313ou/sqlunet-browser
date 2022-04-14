package org.sqlunet.provider;

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
		String[] projection = projection0;
		String selection = selection0;
		String[] selectionArgs = selectionArgs0;
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

			case XSqlUNetDispatcher.WORDS_PBWORDS_PBROLESETS:
			{
				table = "pb_words " + //
						"INNER JOIN pb_rolesets AS " + XSqlUNetContract.CLASS + " USING (pbwordid)";
				groupBy = "wordid,synsetid,rolesetid";
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
				return makeUnionQuery(table1, table2, table1Projection, table2Projection, unionProjection, projection, selection, selectionArgs, groupByArray, orderBy, "vn");
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
				return makeUnionQuery(table1, table2, table1Projection, table2Projection, unionProjection, projection, selection, selectionArgs, groupByArray, orderBy, "pb");
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
				return makeUnionQuery(table1, table2, table1Projection, table2Projection, unionProjection, projection, selection, selectionArgs, groupByArray,  orderBy, "fn");
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
	 * @param selectionArgs    selection arguments
	 * @param groupBys         group by
	 * @param sortOrder        sort
	 * @param tag              tag
	 * @return result
	 */
	static Result makeUnionQuery(final String table1, final String table2, //
			final String[] table1Projection, final String[] table2Projection, //
			final String[] unionProjection, //
			@NonNull final String[] projection, //
			final String selection, //
			final String[] selectionArgs, //
			final String[] groupBys, final String sortOrder, final String tag)
	{
		// embbedded
		final String uQuery = makeEmbeddedQuery(table1, table2, //
				table1Projection, table2Projection, //
				unionProjection, selection, //
				tag);

		// table
		final String table = ('(' + uQuery + ')');

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
		final String groupBy = join(",", actualGroupBys);

		// args
		final String[] selectionArgs2 = unfoldSelectionArgs(selectionArgs);

		return new Result(table, projection, selection, selectionArgs2, groupBy, sortOrder);
	}

	/**
	 * Make embedded union query
	 *
	 * @param table1           table1
	 * @param table2           table2
	 * @param table1Projection table1 projection
	 * @param table2Projection table2 projection
	 * @param unionProjection  union projection
	 * @param selection        selection
	 * @param tag              tag
	 * @return union sql
	 */
	private static String makeEmbeddedQuery(final String table1, final String table2, //
			final String[] table1Projection, final String[] table2Projection, //
			final String[] unionProjection, final String selection, //
			final String tag)
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
		return uQuery;
	}

	/**
	 * Make args
	 *
	 * @param selectionArgs selection arguments
	 * @return cursor
	 */
	private static String[] unfoldSelectionArgs(@NonNull final String... selectionArgs)
	{
		final String[] selectionArgs2 = new String[2 * selectionArgs.length];
		for (int i = 0; i < selectionArgs.length; i++)
		{
			selectionArgs2[2 * i] = selectionArgs2[2 * i + 1] = selectionArgs[i];
		}
		return selectionArgs2;
	}

	// Avoid either TextUtils.join (Android dependency) or String.join(API)
	private static String join(final CharSequence delimiter, final CharSequence[] tokens)
	{
		final int length = tokens.length;
		if (length == 0)
		{
			return "";
		}
		final StringBuilder sb = new StringBuilder();
		sb.append(tokens[0]);
		for (int i = 1; i < length; i++)
		{
			sb.append(delimiter);
			sb.append(tokens[i]);
		}
		return sb.toString();
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
	static String makeQuerySql(final String table1, final String table2, //
			final String[] table1Projection, final String[] table2Projection, //
			final String[] unionProjection, @NonNull final String[] projection, //
			final String selection, //
			final String[] groupBys, final String sortOrder, final String tag)
	{
		final String embeddedQuery = makeEmbeddedQuery(table1, table2, //
				table1Projection, table2Projection, //
				unionProjection, selection, //
				tag);

		// embed
		final SQLiteQueryBuilder embeddingQueryBuilder = new SQLiteQueryBuilder();

		// table
		embeddingQueryBuilder.setTables('(' + embeddedQuery + ')');

		// projection
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
		String groupBy = join(",", actualGroupBys);

		return embeddingQueryBuilder.buildQuery(resultProjection, null, groupBy, null, sortOrder, null);
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
		String groupBy = join(",", actualGroupBys);
		return embeddingQueryBuilder.buildQuery(resultProjection, null, groupBy, null, sortOrder, null);
	}
}
