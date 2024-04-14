/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.provider

import java.util.regex.Pattern

private const val STRICT_FLAGS = 0

class SQLiteQueryBuilder {

    private var mTables = ""
    private var mWhereClause: StringBuilder? = null // lazily created
    private var mDistinct = false
    private val mProjectionMap: Map<String, String>? = null
    private val mProjectionGreylist: Collection<Pattern>? = null

    fun setTables(inTables: String) {
        mTables = inTables
    }

    fun buildQuery(projectionIn: Array<String>, selection: String?, groupBy: String?, having: String?, sortOrder: String?, limit: String?): String {
        val projection = computeProjection(projectionIn)
        val where = computeWhere(selection)
        return buildQueryString(mDistinct, mTables, projection, where, groupBy, having, sortOrder, limit)
    }

    @Throws(IllegalArgumentException::class)
    private fun computeProjection(projectionIn: Array<String>): Array<String>? {
        if (projectionIn.isNotEmpty()) {
            val projectionOut = Array(projectionIn.size) {
                computeSingleProjectionOrThrow(projectionIn[it])
            }
            return projectionOut
        } else if (mProjectionMap != null) {
            // Return all columns in projection map.
            val entrySet = mProjectionMap.entries
            val entryIter = entrySet.iterator()
            return Array(entrySet.size) {
                val (_, value) = entryIter.next()
                // Don't include the _count column when people ask for no projection.
                // if (key == "_count") {
                //    value = null
                //}
                value
            }
        }
        return null
    }

    private fun computeSingleProjectionOrThrow(userColumn: String): String {
        return computeSingleProjection(userColumn)
    }

    @Throws(IllegalArgumentException::class)
    private fun computeSingleProjection(userColumn0: String): String {
        // When no mapping provided, anything goes
        var userColumn = userColumn0
        if (mProjectionMap == null) {
            return userColumn
        }
        var operator: String? = null
        var column = mProjectionMap[userColumn]
        // When no direct match found, look for aggregation
        if (column == null) {
            val matcher = sAggregationPattern.matcher(userColumn)
            if (matcher.matches()) {
                operator = matcher.group(1)
                userColumn = matcher.group(2)!!
                column = mProjectionMap[userColumn]
            }
        }
        if (column != null) {
            return maybeWithOperator(operator, column)
        }
        if (STRICT_FLAGS == 0 && (userColumn.contains(" AS ") || userColumn.contains(" as "))) {
            /* A column alias already exist */
            return maybeWithOperator(operator, userColumn)
        }
        // If greylist is configured, we might be willing to let
        // this custom column bypass our strict checks.
        if (mProjectionGreylist != null) {
            var match = false
            for (p in mProjectionGreylist) {
                if (p.matcher(userColumn).matches()) {
                    match = true
                    break
                }
            }
            if (match) {
                return maybeWithOperator(operator, userColumn)
            }
        }
        throw IllegalArgumentException(userColumn0)
    }

    private fun computeWhere(selection: String?): String? {
        val hasInternal = mWhereClause != null && mWhereClause!!.isNotEmpty()
        val hasExternal = !isEmpty(selection)
        return if (hasInternal || hasExternal) {
            val where = StringBuilder()
            if (hasInternal) {
                where.append('(').append(mWhereClause).append(')')
            }
            if (hasInternal && hasExternal) {
                where.append(" AND ")
            }
            if (hasExternal) {
                where.append('(').append(selection).append(')')
            }
            where.toString()
        } else {
            null
        }
    }

    fun appendWhere(inWhere: CharSequence) {
        if (mWhereClause == null) {
            mWhereClause = StringBuilder(inWhere.length + 16)
        }
        mWhereClause!!.append(inWhere)
    }

    fun setDistinct(b: Boolean) {
        mDistinct = b
    }

    fun buildUnionSubQuery(
        typeDiscriminatorColumn: String,
        unionColumns: Array<String>,
        columnsPresentInTable: Set<String>,
        computedColumnsOffset: Int,
        typeDiscriminatorValue: String,
        selection: String?,
        groupBy: String?,
        having: String?,
    ): String {
        val unionColumnsCount = unionColumns.size
        val projectionIn = Array(unionColumnsCount) {
            val unionColumn = unionColumns[it]
            if (unionColumn == typeDiscriminatorColumn) {
                "'$typeDiscriminatorValue' AS $typeDiscriminatorColumn"
            } else
                if (it <= computedColumnsOffset || columnsPresentInTable.contains(unionColumn))
                    unionColumn
                else
                    "NULL AS $unionColumn"
        }
        return buildQuery(
            projectionIn, selection, groupBy, having,
            null /* sortOrder */,
            null /* limit */
        )
    }

    fun buildUnionQuery(subQueries: Array<String?>, sortOrder: String?, limit: String?): String {
        val query = StringBuilder(128)
        val subQueryCount = subQueries.size
        val unionOperator = if (mDistinct) " UNION " else " UNION ALL "
        for (i in 0 until subQueryCount) {
            if (i > 0) {
                query.append(unionOperator)
            }
            query.append(subQueries[i])
        }
        appendClause(query, " ORDER BY ", sortOrder)
        appendClause(query, " LIMIT ", limit)
        return query.toString()
    }

    companion object {

        private val sAggregationPattern = Pattern.compile("(?i)(AVG|COUNT|MAX|MIN|SUM|TOTAL|GROUP_CONCAT)\\((.+)\\)")

        fun isEmpty(s: CharSequence?): Boolean {
            return s.isNullOrEmpty()
        }

        private fun maybeWithOperator(operator: String?, column: String): String {
            return if (operator != null) {
                "$operator($column)"
            } else {
                column
            }
        }

        fun buildQueryString(distinct: Boolean, tables: String?, columns: Array<String>?, where: String?, groupBy: String?, having: String?, orderBy: String?, limit: String?): String {
            require(!(isEmpty(groupBy) && !isEmpty(having))) { "HAVING clauses are only permitted when using a groupBy clause" }
            val query = StringBuilder(120)
            query.append("SELECT ")
            if (distinct) {
                query.append("DISTINCT ")
            }
            if (!columns.isNullOrEmpty()) {
                appendColumns(query, columns)
            } else {
                query.append("* ")
            }
            query.append("FROM ")
            query.append(tables)
            appendClause(query, " WHERE ", where)
            appendClause(query, " GROUP BY ", groupBy)
            appendClause(query, " HAVING ", having)
            appendClause(query, " ORDER BY ", orderBy)
            appendClause(query, " LIMIT ", limit)
            return query.toString()
        }

        private fun appendColumns(s: StringBuilder, columns: Array<String>) {
            val n = columns.size
            for (i in 0 until n) {
                val column = columns[i]
                if (i > 0) {
                    s.append(", ")
                }
                s.append(column)
            }
            s.append(' ')
        }

        private fun appendClause(s: StringBuilder, name: String, clause: String?) {
            if (!isEmpty(clause)) {
                s.append(name)
                s.append(clause)
            }
        }
    }
}
