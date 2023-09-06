/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.provider;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLiteQueryBuilder
{
	private static final Pattern sAggregationPattern = Pattern.compile("(?i)(AVG|COUNT|MAX|MIN|SUM|TOTAL|GROUP_CONCAT)\\((.+)\\)");
	public static final String _COUNT = "_count";

	private String mTables = "";
	private StringBuilder mWhereClause = null;  // lazily created
	private boolean mDistinct;
	private final Map<String, String> mProjectionMap = null;
	private final Collection<Pattern> mProjectionGreylist = null;
	private int mStrictFlags;

	static boolean isEmpty(CharSequence s)
	{
		return s == null || s.length() == 0;
	}

	public void setTables(String inTables)
	{
		mTables = inTables;
	}

	public String buildQuery(String[] projectionIn, String selection, String groupBy, String having, String sortOrder, String limit)
	{
		String[] projection = computeProjection(projectionIn);
		String where = computeWhere(selection);

		return buildQueryString(mDistinct, mTables, projection, where, groupBy, having, sortOrder, limit);
	}

	public String[] computeProjection(String[] projectionIn)
	{
		if (projectionIn.length > 0)
		{
			String[] projectionOut = new String[projectionIn.length];
			for (int i = 0; i < projectionIn.length; i++)
			{
				projectionOut[i] = computeSingleProjectionOrThrow(projectionIn[i]);
			}
			return projectionOut;
		}
		else //noinspection ConstantConditions
			if (mProjectionMap != null)
		{
			// Return all columns in projection map.
			Set<Map.Entry<String, String>> entrySet = mProjectionMap.entrySet();
			String[] projection = new String[entrySet.size()];
			Iterator<Map.Entry<String, String>> entryIter = entrySet.iterator();
			int i = 0;

			while (entryIter.hasNext())
			{
				Map.Entry<String, String> entry = entryIter.next();

				// Don't include the _count column when people ask for no projection.
				if (entry.getKey().equals(_COUNT))
				{
					continue;
				}
				projection[i++] = entry.getValue();
			}
			return projection;
		}
		return null;
	}

	private String computeSingleProjectionOrThrow(String userColumn)
	{
		final String column = computeSingleProjection(userColumn);
		if (column != null)
		{
			return column;
		}
		else
		{
			throw new IllegalArgumentException("Invalid column " + userColumn);
		}
	}

	private String computeSingleProjection(String userColumn)
	{
		// When no mapping provided, anything goes
		//noinspection ConstantConditions
		if (mProjectionMap == null)
		{
			return userColumn;
		}

		String operator = null;
		String column = mProjectionMap.get(userColumn);

		// When no direct match found, look for aggregation
		if (column == null)
		{
			final Matcher matcher = sAggregationPattern.matcher(userColumn);
			if (matcher.matches())
			{
				operator = matcher.group(1);
				userColumn = matcher.group(2);
				column = mProjectionMap.get(userColumn);
			}
		}

		if (column != null)
		{
			return maybeWithOperator(operator, column);
		}

		if (mStrictFlags == 0 && (userColumn.contains(" AS ") || userColumn.contains(" as ")))
		{
			/* A column alias already exist */
			return maybeWithOperator(operator, userColumn);
		}

		// If greylist is configured, we might be willing to let
		// this custom column bypass our strict checks.
		if (mProjectionGreylist != null)
		{
			boolean match = false;
			for (Pattern p : mProjectionGreylist)
			{
				if (p.matcher(userColumn).matches())
				{
					match = true;
					break;
				}
			}

			if (match)
			{
				return maybeWithOperator(operator, userColumn);
			}
		}
		return null;
	}

	private static String maybeWithOperator(String operator, String column)
	{
		if (operator != null)
		{
			return operator + "(" + column + ")";
		}
		else
		{
			return column;
		}
	}

	String computeWhere(String selection)
	{
		final boolean hasInternal = mWhereClause != null && mWhereClause.length() > 0;
		final boolean hasExternal = !isEmpty(selection);

		if (hasInternal || hasExternal)
		{
			final StringBuilder where = new StringBuilder();
			if (hasInternal)
			{
				where.append('(').append(mWhereClause).append(')');
			}
			if (hasInternal && hasExternal)
			{
				where.append(" AND ");
			}
			if (hasExternal)
			{
				where.append('(').append(selection).append(')');
			}
			return where.toString();
		}
		else
		{
			return null;
		}
	}

	public static String buildQueryString(boolean distinct, String tables, String[] columns, String where, String groupBy, String having, String orderBy, String limit)
	{
		if (isEmpty(groupBy) && !isEmpty(having))
		{
			throw new IllegalArgumentException("HAVING clauses are only permitted when using a groupBy clause");
		}

		StringBuilder query = new StringBuilder(120);

		query.append("SELECT ");
		if (distinct)
		{
			query.append("DISTINCT ");
		}
		if (columns != null && columns.length != 0)
		{
			appendColumns(query, columns);
		}
		else
		{
			query.append("* ");
		}
		query.append("FROM ");
		query.append(tables);
		appendClause(query, " WHERE ", where);
		appendClause(query, " GROUP BY ", groupBy);
		appendClause(query, " HAVING ", having);
		appendClause(query, " ORDER BY ", orderBy);
		appendClause(query, " LIMIT ", limit);

		return query.toString();
	}

	public void appendWhere(CharSequence inWhere) {
		if (mWhereClause == null) {
			mWhereClause = new StringBuilder(inWhere.length() + 16);
		}
		mWhereClause.append(inWhere);
	}

	public static void appendColumns(StringBuilder s, String[] columns)
	{
		int n = columns.length;

		for (int i = 0; i < n; i++)
		{
			String column = columns[i];
			if (column != null)
			{
				if (i > 0)
				{
					s.append(", ");
				}
				s.append(column);
			}
		}
		s.append(' ');
	}

	private static void appendClause(StringBuilder s, String name, String clause)
	{
		if (!isEmpty(clause))
		{
			s.append(name);
			s.append(clause);
		}
	}

	public void setDistinct(final boolean b)
	{
		mDistinct = b;
	}

	public String buildUnionSubQuery(
			String typeDiscriminatorColumn,
			String[] unionColumns,
			Set<String> columnsPresentInTable,
			int computedColumnsOffset,
			String typeDiscriminatorValue,
			String selection,
			String groupBy,
			String having) {
		int unionColumnsCount = unionColumns.length;
		String[] projectionIn = new String[unionColumnsCount];

		for (int i = 0; i < unionColumnsCount; i++) {
			String unionColumn = unionColumns[i];

			if (unionColumn.equals(typeDiscriminatorColumn)) {
				projectionIn[i] = "'" + typeDiscriminatorValue + "' AS "
						+ typeDiscriminatorColumn;
			} else if (i <= computedColumnsOffset
					|| columnsPresentInTable.contains(unionColumn)) {
				projectionIn[i] = unionColumn;
			} else {
				projectionIn[i] = "NULL AS " + unionColumn;
			}
		}
		return buildQuery(
				projectionIn, selection, groupBy, having,
				null /* sortOrder */,
				null /* limit */);
	}

	public String buildUnionQuery(String[] subQueries, String sortOrder, String limit) {
		StringBuilder query = new StringBuilder(128);
		int subQueryCount = subQueries.length;
		String unionOperator = mDistinct ? " UNION " : " UNION ALL ";

		for (int i = 0; i < subQueryCount; i++) {
			if (i > 0) {
				query.append(unionOperator);
			}
			query.append(subQueries[i]);
		}
		appendClause(query, " ORDER BY ", sortOrder);
		appendClause(query, " LIMIT ", limit);
		return query.toString();
	}
}
