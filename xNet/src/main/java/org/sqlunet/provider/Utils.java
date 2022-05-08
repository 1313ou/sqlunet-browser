package org.sqlunet.provider;

import org.sqlunet.provider.XNetControl.Result;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import androidx.annotation.NonNull;

public class Utils
{
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
			String selection, //
			final String[] selectionArgs, //
			final String[] groupBys, final String sortOrder, final String tag)
	{
		// embbedded
		final String uQuery = makeEmbeddedQuery(table1, table2, //
				table1Projection, table2Projection, //
				unionProjection, selection, //
				tag);
		selection = null;

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

		return new XNetControl.Result(table, projection, selection, selectionArgs2, groupBy, sortOrder);
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
		return uQueryBuilder.buildUnionQuery(new String[]{pmSubquery, sqlunetSubquery}, null, null);
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
		String groupBy = makeGroupBys(groupBys, projection);

		return embeddingQueryBuilder.buildQuery(resultProjection, null, groupBy, null, sortOrder, null);
	}

	public static String makeGroupBys(String[] groupBys0, String[] projection)
	{
		// group by
		String[] groupBys = groupBys0;
		if (groupBys == null)
		{
			groupBys = new String[projection.length];
			for (int i = 0; i < projection.length; i++)
			{
				groupBys[i] = projection[i].replaceFirst("\\sAS\\s*.*$", "");
			}
		}
		return join(",", groupBys);
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


	/**
	 * Join strings
	 * Avoid using either TextUtils.join (Android dependency) or String.join(API)
	 *
	 * @param delimiter delimiter
	 * @param tokens    tokens
	 * @return joined
	 */
	private static String join(@SuppressWarnings("SameParameterValue") final CharSequence delimiter, final CharSequence[] tokens)
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
	 * Make args
	 *
	 * @param selectionArgs selection arguments
	 * @return cursor
	 */
	static String[] unfoldSelectionArgs(@NonNull final String... selectionArgs)
	{
		final String[] selectionArgs2 = new String[2 * selectionArgs.length];
		for (int i = 0; i < selectionArgs.length; i++)
		{
			selectionArgs2[2 * i] = selectionArgs2[2 * i + 1] = selectionArgs[i];
		}
		return selectionArgs2;
	}
}
