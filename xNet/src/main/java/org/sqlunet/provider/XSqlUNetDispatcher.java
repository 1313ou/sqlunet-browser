package org.sqlunet.provider;

import org.sqlunet.xnet.provider.Q;
import org.sqlunet.xnet.provider.V;

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
	static protected final int WORDS_VNWORDS_VNCLASSES_1 = 312;
	static protected final int WORDS_VNWORDS_VNCLASSES_2 = 313;
	static protected final int WORDS_VNWORDS_VNCLASSES_1U2 = 314;
	static protected final int WORDS_PBWORDS_PBROLESETS = 320;
	static protected final int WORDS_PBWORDS_PBROLESETS_U = 321;
	static protected final int WORDS_PBWORDS_PBROLESETS_1 = 322;
	static protected final int WORDS_PBWORDS_PBROLESETS_2 = 323;
	static protected final int WORDS_PBWORDS_PBROLESETS_1U2 = 324;
	static protected final int WORDS_FNWORDS_FNFRAMES_U = 331;
	static protected final int WORDS_FNWORDS_FNFRAMES_1 = 332;
	static protected final int WORDS_FNWORDS_FNFRAMES_2 = 333;
	static protected final int WORDS_FNWORDS_FNFRAMES_1U2 = 334;
	static protected final int SOURCES = 400;

	public static Result queryMain(final int code, final String uriLast, final String[] projection0, final String selection0, final String[] selectionArgs0)
	{
		String table;
		String[] projection = projection0;
		String selection = selection0;
		String[] selectionArgs = selectionArgs0;
		String groupBy = null;
		String orderBy = null;

		switch (code)
		{
			case XSqlUNetDispatcher.PREDICATEMATRIX:
				// table = "pm";
				table = Q.PREDICATEMATRIX.TABLE;
				break;

			case XSqlUNetDispatcher.PREDICATEMATRIX_VERBNET:
				table = Q.PREDICATEMATRIX_VERBNET.TABLE;
				break;

			case XSqlUNetDispatcher.PREDICATEMATRIX_PROPBANK:
				table = Q.PREDICATEMATRIX_PROPBANK.TABLE;
				break;

			case XSqlUNetDispatcher.PREDICATEMATRIX_FRAMENET:
				table = Q.PREDICATEMATRIX_FRAMENET.TABLE;
				break;

			case XSqlUNetDispatcher.SOURCES:
				table = Q.SOURCES.TABLE;
				break;

			// J O I N S

			case XSqlUNetDispatcher.WORDS_FNWORDS_PBWORDS_VNWORDS:
				table = Q.WORDS_FNWORDS_PBWORDS_VNWORDS.TABLE;
				groupBy = V.SYNSETID;
				break;

			case XSqlUNetDispatcher.WORDS_PBWORDS_VNWORDS:
				table = Q.WORDS_PBWORDS_VNWORDS.TABLE;
				groupBy = V.SYNSETID;
				break;

			case XSqlUNetDispatcher.WORDS_VNWORDS_VNCLASSES:
			{
				table = Q.WORDS_VNWORDS_VNCLASSES.TABLE;
				groupBy = String.format("%s,%s,%s", V.WORDID, V.SYNSETID, V.CLASSID);
				break;
			}

			case XSqlUNetDispatcher.WORDS_PBWORDS_PBROLESETS:
			{
				table = Q.WORDS_PBWORDS_PBROLESETS.TABLE;
				groupBy = String.format("%s,%s,%s", V.WORDID, V.SYNSETID, V.ROLESETID);
				break;
			}

			// U

			case WORDS_VNWORDS_VNCLASSES_1:
				table = Q.WORDS_VNWORDS_VNCLASSES_1.TABLE;
				projection = Q.WORDS_VNWORDS_VNCLASSES_1.PROJECTION;
				break;

			case WORDS_VNWORDS_VNCLASSES_2:
				table = Q.WORDS_VNWORDS_VNCLASSES_2.TABLE;
				projection = Q.WORDS_VNWORDS_VNCLASSES_2.PROJECTION;
				break;

			case WORDS_VNWORDS_VNCLASSES_1U2:
				table = Q.WORDS_VNWORDS_VNCLASSES_1U2.TABLE; //.replaceAll("#\\{selection\\}", selection);
				break;

			case XSqlUNetDispatcher.WORDS_VNWORDS_VNCLASSES_U:
			{
				/*
				final String table1 = Q.WORDS_VNWORDS_VNCLASSES_1.TABLE;
				final String table2 = Q.WORDS_VNWORDS_VNCLASSES_2.TABLE;
				final String[] table1Projection = Q.WORDS_VNWORDS_VNCLASSES_1.PROJECTION;
				final String[] table2Projection = Q.WORDS_VNWORDS_VNCLASSES_2.PROJECTION;
				final String[] unionProjection = table1Projection;
				final String[] groupByArray = {V.WORDID, V.SYNSETID, V.CLASSID};
				return makeUnionQuery(table1, table2, table1Projection, table2Projection, unionProjection, projection, selection, selectionArgs, groupByArray, orderBy, "vn");
				*/
				table = Q.WORDS_VNWORDS_VNCLASSES_1U2.TABLE.replaceAll("#\\{selection\\}", selection);
				selectionArgs = unfoldSelectionArgs(selectionArgs);
				groupBy = String.format("%s,%s,%s", V.WORDID, V.SYNSETID, V.CLASSID);
				break;
			}

			case WORDS_PBWORDS_PBROLESETS_1:
				table = Q.WORDS_PBWORDS_PBROLESETS_1.TABLE;
				projection = Q.WORDS_PBWORDS_PBROLESETS_1.PROJECTION;
				break;

			case WORDS_PBWORDS_PBROLESETS_2:
				table = Q.WORDS_PBWORDS_PBROLESETS_2.TABLE;
				projection = Q.WORDS_PBWORDS_PBROLESETS_2.PROJECTION;
				break;

			case WORDS_PBWORDS_PBROLESETS_1U2:
				table = Q.WORDS_PBWORDS_PBROLESETS_1U2.TABLE; //.replaceAll("#\\{selection\\}", selection);
				break;

			case XSqlUNetDispatcher.WORDS_PBWORDS_PBROLESETS_U:
			{
				/*
				final String table1 = Q.WORDS_PBWORDS_PBROLESETS_1.TABLE;
				final String table2 = Q.WORDS_PBWORDS_PBROLESETS_2.TABLE;
				final String[] table1Projection = Q.WORDS_PBWORDS_PBROLESETS_1.PROJECTION;
				final String[] table2Projection = Q.WORDS_PBWORDS_PBROLESETS_2.PROJECTION;
				final String[] unionProjection = table1Projection;
				final String[] groupByArray = {V.WORDID, V.SYNSETID, V.ROLESETID};
				return makeUnionQuery(table1, table2, table1Projection, table2Projection, unionProjection, projection, selection, selectionArgs, groupByArray, orderBy, "pb");
				*/
				table = Q.WORDS_PBWORDS_PBROLESETS_1U2.TABLE.replaceAll("#\\{selection\\}", selection);
				selectionArgs = unfoldSelectionArgs(selectionArgs);
				groupBy = String.format("%s,%s,%s", V.WORDID, V.SYNSETID, V.ROLESETID);
				break;
			}

			case WORDS_FNWORDS_FNFRAMES_1:
				table = Q.WORDS_FNWORDS_FNFRAMES_1.TABLE;
				projection = Q.WORDS_FNWORDS_FNFRAMES_1.PROJECTION;
				break;

			case WORDS_FNWORDS_FNFRAMES_2:
				table = Q.WORDS_FNWORDS_FNFRAMES_2.TABLE;
				projection = Q.WORDS_FNWORDS_FNFRAMES_2.PROJECTION;
				break;

			case WORDS_FNWORDS_FNFRAMES_1U2:
				table = Q.WORDS_FNWORDS_FNFRAMES_1U2.TABLE; //.replaceAll("\\$\\{selection\\}", selection);
				break;

			case XSqlUNetDispatcher.WORDS_FNWORDS_FNFRAMES_U:
			{
				/*
				final String table1 = Q.WORDS_FNWORDS_FNFRAMES_1.TABLE;
				final String table2 = Q.WORDS_FNWORDS_FNFRAMES_2.TABLE;
				final String[] table1Projection = Q.WORDS_FNWORDS_FNFRAMES_1.PROJECTION;
				final String[] table2Projection = Q.WORDS_FNWORDS_FNFRAMES_2.PROJECTION;
				final String[] unionProjection = table1Projection;
				final String[] groupByArray = {V.WORDID, V.SYNSETID, V.FRAMEID};
				return makeUnionQuery(table1, table2, table1Projection, table2Projection, unionProjection, projection, selection, selectionArgs, groupByArray, orderBy, "fn");
				*/
				table = Q.WORDS_FNWORDS_FNFRAMES_1U2.TABLE.replaceAll("#\\{selection\\}", selection);
				selectionArgs = unfoldSelectionArgs(selectionArgs);
				groupBy = String.format("%s,%s,%s", V.WORDID, V.SYNSETID, V.FRAMEID);
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
		final String table = ("( " + uQuery + " )");

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
	 * <p>
	 * SELECT PROJ1, 'pm[tag]' AS source
	 * FROM TABLE1
	 * WHERE (#{selection})
	 * UNION
	 * SELECT PROJ2, '[tag]' AS source
	 * FROM TABLE2
	 * WHERE (#{selection})
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

	/**
	 * Join strings
	 * Avoid using either TextUtils.join (Android dependency) or String.join(API)
	 *
	 * @param delimiter delimiter
	 * @param tokens    tokens
	 * @return joined
	 */
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
	static String makeQuerySql0(final String table1, final String table2, //
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
