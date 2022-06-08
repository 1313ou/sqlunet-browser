;/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extended cross WordNet-FrameNet-PropBank-VerbNet provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Factory implements Function<String, String[]>, Supplier<String[]>
{
	static class SQLiteQueryBuilder
	{
		private static final Pattern sAggregationPattern = Pattern.compile("(?i)(AVG|COUNT|MAX|MIN|SUM|TOTAL|GROUP_CONCAT)\\((.+)\\)");
		public static final String _COUNT = "_count";

		private String mTables = "";
		private StringBuilder mWhereClause = null;  // lazily created
		private boolean mDistinct;
		private Map<String, String> mProjectionMap = null;
		private Collection<Pattern> mProjectionGreylist = null;
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
			else if (mProjectionMap != null)
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

		public void appendWhere(CharSequence inWhere)
		{
			if (mWhereClause == null)
			{
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

		public String buildUnionSubQuery(String typeDiscriminatorColumn, String[] unionColumns, Set<String> columnsPresentInTable, int computedColumnsOffset, String typeDiscriminatorValue, String selection, String groupBy, String having)
		{
			int unionColumnsCount = unionColumns.length;
			String[] projectionIn = new String[unionColumnsCount];

			for (int i = 0; i < unionColumnsCount; i++)
			{
				String unionColumn = unionColumns[i];

				if (unionColumn.equals(typeDiscriminatorColumn))
				{
					projectionIn[i] = "'" + typeDiscriminatorValue + "' AS " + typeDiscriminatorColumn;
				}
				else if (i <= computedColumnsOffset || columnsPresentInTable.contains(unionColumn))
				{
					projectionIn[i] = unionColumn;
				}
				else
				{
					projectionIn[i] = "NULL AS " + unionColumn;
				}
			}
			return buildQuery(projectionIn, selection, groupBy, having, null /* sortOrder */, null /* limit */);
		}

		public String buildUnionQuery(String[] subQueries, String sortOrder, String limit)
		{
			StringBuilder query = new StringBuilder(128);
			int subQueryCount = subQueries.length;
			String unionOperator = mDistinct ? " UNION " : " UNION ALL ";

			for (int i = 0; i < subQueryCount; i++)
			{
				if (i > 0)
				{
					query.append(unionOperator);
				}
				query.append(subQueries[i]);
			}
			appendClause(query, " ORDER BY ", sortOrder);
			appendClause(query, " LIMIT ", limit);
			return query.toString();
		}
	}

	static public final String SELECTION = "#{selection}";

	// C O N S T R U C T O R

	/**
	 * Constructor
	 */
	public Factory()
	{
	}

	// Q U E R Y

	@Override
	public String[] apply(String keyName)
	{
		Key key = Key.valueOf(keyName);
		return apply(key).toStrings();
	}

	public Result apply(final Key key)
	{
		Result r = new Result();
		switch (key)
		{
			case PREDICATEMATRIX:
				// table = "pm";
				r.table = "${pmvn.table} " + //
						"LEFT JOIN ${pmpb.table} USING (wordid) " + //
						"LEFT JOIN ${pmfn.table} USING (wordid)";
				break;

			case PREDICATEMATRIX_VERBNET:
				r.table = "${pmvn.table}";

				break;

			case PREDICATEMATRIX_PROPBANK:
				r.table = "${pmpb.table}";
				break;

			case PREDICATEMATRIX_FRAMENET:
				r.table = "${pmfn.table}";
				break;

			case SOURCES:
				r.table = "${sources.table}";
				break;

			// J O I N S

			case WORDS_FNWORDS_PBWORDS_VNWORDS:
				r.table = "${words.table} AS ${as_words} " + //
						"LEFT JOIN ${senses.table} AS ${as_senses} USING (${words.wordid}) " + //
						"LEFT JOIN ${synsets.table} AS ${as_synsets} USING (${synsets.synsetid}) " + //
						"LEFT JOIN ${poses.table} AS ${as_poses} USING (${poses.posid}) " + //
						"LEFT JOIN ${casedwords.table} USING (${words.wordid},${casedwords.casedwordid}) " + //
						"LEFT JOIN ${domains.table} USING (${domains.domainid}) " + //
						"LEFT JOIN ${fn_words.table} USING (${words.wordid}) " + //
						"LEFT JOIN ${vn_words.table} USING (${words.wordid}) " + //
						"LEFT JOIN ${pb_words.table} USING (${words.wordid})";
				r.groupBy = "${synsets.synsetid}";
				break;

			case WORDS_PRONUNCIATIONS_FNWORDS_PBWORDS_VNWORDS:
				r.table =
						"${lexes.table} AS ${as_lexes} " + //
						"INNER JOIN ${words.table} AS ${as_words} USING (${words.wordid}) " + //
						"LEFT JOIN ${senses.table} AS ${as_senses} USING (${words.wordid},${lexes.luid}) " + //
						"LEFT JOIN ${synsets.table} AS ${as_synsets} USING (${synsets.synsetid}) " + //
						"LEFT JOIN ${poses.table} AS ${as_poses} USING (${poses.posid}) " + //
						"LEFT JOIN ${casedwords.table} USING (${words.wordid},${casedwords.casedwordid}) " + //
						"LEFT JOIN ${lexes_pronunciations.table} USING (${lexes.luid},${words.wordid}) " + //
						"LEFT JOIN ${pronunciations.table} AS ${as_pronunciations} USING (${pronunciations.pronunciationid}) " +
						"LEFT JOIN ${domains.table} USING (${domains.domainid}) " + //
						"LEFT JOIN ${fn_words.table} USING (${words.wordid}) " + //
						"LEFT JOIN ${vn_words.table} USING (${words.wordid}) " + //
						"LEFT JOIN ${pb_words.table} USING (${words.wordid})";
				r.groupBy = "${synsets.synsetid}";
				break;

			case WORDS_PBWORDS_VNWORDS:
				r.table = "${words.table} AS ${as_words} " + //
						"LEFT JOIN ${senses.table} AS ${as_senses} USING (${words.wordid}) " + //
						"LEFT JOIN ${synsets.table} AS ${as_synsets} USING (${synsets.synsetid}) " + //
						"LEFT JOIN ${poses.table} AS ${as_poses} USING (${poses.posid}) " + //
						"LEFT JOIN ${casedwords.table} USING (${words.wordid},${casedwords.casedwordid}) " + //
						"LEFT JOIN ${domains.table} USING (${domains.domainid}) " + //
						"LEFT JOIN ${vn_words.table} USING (${words.wordid}) " + //
						"LEFT JOIN ${pb_words.table} USING (${words.wordid})";
				r.groupBy = "${synsets.synsetid}";
				break;

			case WORDS_VNWORDS_VNCLASSES:
			{
				r.table = "${vn_words.table} " + //
						"INNER JOIN ${vn_members_senses.table} USING (${vn_words.vnwordid},${words.wordid}) " + //
						"INNER JOIN ${vn_classes.table} AS ${as_classes} USING (${vn_classes.classid}) " + //
						"LEFT JOIN ${synsets.table} USING (${synsets.synsetid})";
				r.groupBy = "${words.wordid},${synsets.synsetid},${vn_classes.classid}";
				break;
			}

			// V N

			case WORDS_VNWORDS_VNCLASSES_U:
			{
				final Result r1 = apply(Key.WORDS_VNWORDS_VNCLASSES_1);
				final Result r2 = apply(Key.WORDS_VNWORDS_VNCLASSES_2);
				final String[] unionProjection = r1.projection;
				final String[] groupByArray = {"${words.wordid}", "${synsets.synsetid}", "${vn_classes.classid}"};
				return makeQuery(r1.table, r2.table, r1.projection, r2.projection, unionProjection, "vn");
			}

			case WORDS_VNWORDS_VNCLASSES_1U2:
			{
				final Result r1 = apply(Key.WORDS_VNWORDS_VNCLASSES_1);
				final Result r2 = apply(Key.WORDS_VNWORDS_VNCLASSES_2);
				final String[] unionProjection = r1.projection;
				return makeEmbeddedQuery(r1.table, r2.table, r1.projection, r2.projection, unionProjection, SELECTION, "vn");
			}

			case WORDS_VNWORDS_VNCLASSES_1:
			{
				r.table = "${pmvn.table} " + //
						"INNER JOIN ${vn_classes.table} USING (${vn_classes.classid}) " + //
						"LEFT JOIN ${synsets.table} USING (${synsets.synsetid})";
				r.projection = new String[]{"${words.wordid}", "${synsets.synsetid}", "${vn_classes.classid}", "${vn_classes.class}", "${vn_classes.classtag}", "${synsets.definition}"};
				break;
			}

			case WORDS_VNWORDS_VNCLASSES_2:
			{
				r.table = "${vn_words.table} " + //
						"INNER JOIN ${vn_members_senses.table} USING (${vn_words.vnwordid},${words.wordid}) " + //
						"INNER JOIN ${vn_classes.table} USING (${vn_classes.classid})";
				r.projection = new String[]{"${words.wordid}", "${synsets.synsetid}", "${vn_classes.classid}", "${vn_classes.class}", "${vn_classes.classtag}"};
				break;
			}

			// P B

			case WORDS_PBWORDS_PBROLESETS:
			{
				r.table = "${pb_words.table} " + //
						"INNER JOIN ${pb_rolesets.table} AS ${as_classes} USING (${pb_words.pbwordid})";
				r.groupBy = "${words.wordid},${synsets.synsetid},${pb_rolesets.rolesetid}";
				break;
			}

			case WORDS_PBWORDS_PBROLESETS_U:
			{
				final Result r1 = apply(Key.WORDS_PBWORDS_PBROLESETS_1);
				final Result r2 = apply(Key.WORDS_PBWORDS_PBROLESETS_2);
				final String[] unionProjection = r1.projection;
				final String[] groupByArray = {"${words.wordid}", "${synsets.synsetid}", "${pb_rolesets.rolesetid}"};
				return makeQuery(r1.table, r2.table, r1.projection, r2.projection, unionProjection, "pb");
			}

			case WORDS_PBWORDS_PBROLESETS_1U2:
			{
				final Result r1 = apply(Key.WORDS_PBWORDS_PBROLESETS_1);
				final Result r2 = apply(Key.WORDS_PBWORDS_PBROLESETS_2);
				final String[] unionProjection = r1.projection;
				final String[] groupByArray = {"${words.wordid}", "${synsets.synsetid}", "${pb_rolesets.rolesetid}"};
				return makeEmbeddedQuery(r1.table, r2.table, r1.projection, r2.projection, unionProjection, SELECTION, "pb");
			}

			case WORDS_PBWORDS_PBROLESETS_1:
			{
				r.table = "${pmpb.table} " + //
						"INNER JOIN ${pb_rolesets.table} USING (${pb_rolesets.rolesetid}) " + //
						"LEFT JOIN ${synsets.table} USING (${synsets.synsetid})";
				r.projection = new String[]{"${words.wordid}", "${synsets.synsetid}", "${pb_rolesets.rolesetid}", "${pb_rolesets.rolesetname}", "${pb_rolesets.rolesethead}", "${pb_rolesets.rolesetdescr}", "${synsets.definition}"};
				break;
			}

			case WORDS_PBWORDS_PBROLESETS_2:
			{
				r.table = "${pb_words.table} " + //
						"INNER JOIN ${pb_rolesets.table} USING (${pb_words.pbwordid})";
				r.projection = new String[]{"${words.wordid}", "${pb_rolesets.rolesetid}", "${pb_rolesets.rolesetname}", "${pb_rolesets.rolesethead}", "${pb_rolesets.rolesetdescr}"};
				break;
			}

			// F N

			case WORDS_FNWORDS_FNFRAMES_U:
			{
				final Result r1 = apply(Key.WORDS_FNWORDS_FNFRAMES_1);
				final Result r2 = apply(Key.WORDS_FNWORDS_FNFRAMES_2);
				final String[] unionProjection = r1.projection;
				final String[] groupByArray = {"${words.wordid}", "${synsets.synsetid}", "${fn_frames.frameid}"};
				return makeQuery(r1.table, r2.table, r1.projection, r2.projection, unionProjection, "fn");
			}

			case WORDS_FNWORDS_FNFRAMES_1U2:
			{
				final Result r1 = apply(Key.WORDS_FNWORDS_FNFRAMES_1);
				final Result r2 = apply(Key.WORDS_FNWORDS_FNFRAMES_2);
				final String[] unionProjection = r1.projection;
				return makeEmbeddedQuery(r1.table, r2.table, r1.projection, r2.projection, unionProjection, SELECTION, "fn");
			}

			case WORDS_FNWORDS_FNFRAMES_1:
			{
				r.table = "${pmfn.table} " + //
						"INNER JOIN ${fn_frames.table} USING (${fn_frames.frameid}) " + //
						"LEFT JOIN ${fn_lexunits.table} USING (${fn_lexunits.luid},${fn_frames.frameid}) " + //
						"LEFT JOIN ${synsets.table} USING (${synsets.synsetid})";
				r.projection = new String[]{"${words.wordid}", "${synsets.synsetid}", "${fn_frames.frameid}", "${fn_frames.frame}", "${fn_frames.framedefinition}", "${fn_lexunits.luid}", "${fn_lexunits.lexunit}", "${fn_lexunits.ludefinition}", "${synsets.definition}"};
				break;
			}

			case WORDS_FNWORDS_FNFRAMES_2:
			{
				r.table = "${fn_words.table} " + //
						"INNER JOIN ${fn_lexemes.table} USING (${fn_words.fnwordid}) " + //
						"INNER JOIN ${fn_lexunits.table} USING (${fn_lexunits.luid},${poses.posid}) " + //
						"INNER JOIN ${fn_frames.table} USING (${vn_frames.frameid})";
				r.projection = new String[]{"${words.wordid}", "${fn_frames.frameid}", "${fn_frames.frame}", "${fn_frames.framedefinition}", "${fn_lexunits.luid}", "${fn_lexunits.lexunit}", "${fn_lexunits.ludefinition}"};
				break;
			}

			default:
				return null;
		}
		return r;
	}

	/**
	 * Make union query
	 *
	 * @param table1           table1
	 * @param table2           table2
	 * @param table1Projection table1 projection
	 * @param table2Projection table2 projection
	 * @param unionProjection  union projection
	 * @param tag              tag
	 * @return union sql
	 */
	private Result makeQuery(final String table1, final String table2, //
			final String[] table1Projection, final String[] table2Projection, //
			final String[] unionProjection, final String tag)
	{
		Result r = new Result();
		r.table = "#{query}";
		return r;
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
	private static Result makeEmbeddedQuery(final String table1, final String table2, //
			final String[] table1Projection, final String[] table2Projection, //
			final String[] unionProjection, final String selection, //
			final String tag)
	{
		final String[] actualUnionProjection = appendProjection(unionProjection, "source");
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
		final String uQuery = "( " + uQueryBuilder.buildUnionQuery(new String[]{pmSubquery, sqlunetSubquery}, null, null) + " )";
		return new Result(uQuery, null, null, null, null, null);
	}


	/**
	 * Append items to projection
	 *
	 * @param projection original projection
	 * @param items      items to addItem to projection
	 * @return augmented projection
	 */
	public static String[] appendProjection(final String[] projection, final String... items)
	{
		String[] projection2;
		int i = 0;
		if (projection == null)
		{
			projection2 = new String[1 + items.length];
			projection2[i++] = "*";
		}
		else
		{
			projection2 = new String[projection.length + items.length];
			for (final String item : projection)
			{
				projection2[i++] = item;
			}
		}

		for (final String item : items)
		{
			projection2[i++] = item;
		}
		return projection2;
	}

	@Override
	public String[] get()
	{
		return Arrays.stream(Key.values()).map(Enum::name).toArray(String[]::new);
	}

	private enum Key
	{
		PREDICATEMATRIX, //
		PREDICATEMATRIX_VERBNET, //
		PREDICATEMATRIX_PROPBANK, //
		PREDICATEMATRIX_FRAMENET, //
		WORDS_FNWORDS_PBWORDS_VNWORDS, WORDS_PRONUNCIATIONS_FNWORDS_PBWORDS_VNWORDS, //

		WORDS_PBWORDS_VNWORDS, //
		WORDS_VNWORDS_VNCLASSES, //
		WORDS_VNWORDS_VNCLASSES_U, //
		WORDS_VNWORDS_VNCLASSES_1, WORDS_VNWORDS_VNCLASSES_2, WORDS_VNWORDS_VNCLASSES_1U2, //
		WORDS_PBWORDS_PBROLESETS, //
		WORDS_PBWORDS_PBROLESETS_U, //
		WORDS_PBWORDS_PBROLESETS_1, WORDS_PBWORDS_PBROLESETS_2, WORDS_PBWORDS_PBROLESETS_1U2, //
		WORDS_FNWORDS_FNFRAMES_U, //
		WORDS_FNWORDS_FNFRAMES_1, WORDS_FNWORDS_FNFRAMES_2, WORDS_FNWORDS_FNFRAMES_1U2, //
		SOURCES,
	}

	static class Result
	{
		String table = null;
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String sortOrder = null;

		public Result()
		{
		}

		public Result(final String table, final String[] projection, final String selection, final String[] selectionArgs, final String groupBy, final String sortOrder)
		{
			this.table = table;
			this.projection = projection;
			this.selection = selection;
			this.selectionArgs = selectionArgs;
			this.groupBy = groupBy;
			this.sortOrder = sortOrder;
		}

		private static String quote(String str)
		{
			return str == null ? null : String.format("\"%s\"", str);
		}

		String[] toStrings()
		{
			return new String[]{ //
					quote(table), //
					projection == null ? null : "{" + Arrays.stream(projection).map(Result::quote).collect(Collectors.joining(",")) + "}", //
					quote(selection), //
					selectionArgs == null ? null : "{" + Arrays.stream(selectionArgs).map(Result::quote).collect(Collectors.joining(",")) + "}", //
					quote(groupBy), //
					quote(sortOrder)};
		}
	}
}
