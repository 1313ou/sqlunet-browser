/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.provider

import org.sqlunet.provider.BaseProvider.Companion.appendProjection
import org.sqlunet.provider.BaseProvider.Companion.prependProjection

object Utils {

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
    @JvmStatic
    fun makeUnionQuery(
        table1: String, table2: String,
        table1Projection: Array<String>, table2Projection: Array<String>,
        unionProjection: Array<String>,
        projection: Array<String>,
        selection: String?,
        selectionArgs: Array<String>,
        groupBys: Array<String>?,
        sortOrder: String?, tag: String,
    ): XNetControl.Result {

        // embedded
        val uQuery = makeEmbeddedQuery(
            table1, table2,  //
            table1Projection, table2Projection,  //
            unionProjection, selection,  //
            tag
        )

        // table
        val table = "( $uQuery )"

        // group by
        var actualGroupBys = groupBys
        if (actualGroupBys == null) {
            actualGroupBys = Array(projection.size) {
                projection[it].replaceFirst("\\sAS\\s*.*$".toRegex(), "")
            }
        }
        val groupBy = join(",", actualGroupBys)

        // args
        val selectionArgs2 = unfoldSelectionArgs(selectionArgs)
        return XNetControl.Result(table, projection, null, selectionArgs2, groupBy, sortOrder)
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
     *
     *
     * SELECT PROJ1, 'pm[tag]' AS source
     * FROM TABLE1
     * WHERE (#{selection})
     * UNION
     * SELECT PROJ2, '[tag]' AS source
     * FROM TABLE2
     * WHERE (#{selection})
     */
    private fun makeEmbeddedQuery(
        table1: String, table2: String,  //
        table1Projection: Array<String>, table2Projection: Array<String>,  //
        unionProjection: Array<String>, selection: String?,  //
        tag: String,
    ): String {
        val actualUnionProjection = appendProjection(unionProjection, "source")
        val table1ProjectionList = listOf(*table1Projection)
        val table2ProjectionList = listOf(*table2Projection)

        // predicate matrix
        val pmSubQueryBuilder = SQLiteQueryBuilder()
        pmSubQueryBuilder.setTables(table1)
        val pmSubquery = pmSubQueryBuilder.buildUnionSubQuery(
            "source",  //
            actualUnionProjection,  //
            HashSet(table1ProjectionList),  //
            0,  //
            "pm$tag",  //
            selection,  //
            null,  //
            null
        )

        // sqlunet table
        val sqlunetSubQueryBuilder = SQLiteQueryBuilder()
        sqlunetSubQueryBuilder.setTables(table2)
        val sqlunetSubquery = sqlunetSubQueryBuilder.buildUnionSubQuery(
            "source",  //
            actualUnionProjection,  //
            HashSet(table2ProjectionList),  //
            0,  //
            tag,  //
            selection,  //
            null,  //
            null
        )

        // union
        val uQueryBuilder = SQLiteQueryBuilder()
        uQueryBuilder.setDistinct(true)
        return uQueryBuilder.buildUnionQuery(arrayOf(pmSubquery, sqlunetSubquery), null, null)
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
    fun makeQuerySql(
        table1: String, table2: String,  //
        table1Projection: Array<String>, table2Projection: Array<String>,  //
        unionProjection: Array<String>, projection: Array<String>,  //
        selection: String?,  //
        groupBys: Array<String>?, sortOrder: String?, tag: String,
    ): String {
        val embeddedQuery = makeEmbeddedQuery(
            table1, table2,  //
            table1Projection, table2Projection,  //
            unionProjection, selection,  //
            tag
        )

        // embed
        val embeddingQueryBuilder = SQLiteQueryBuilder()

        // table
        embeddingQueryBuilder.setTables("($embeddedQuery)")

        // projection
        val resultProjection = prependProjection(projection, "GROUP_CONCAT(DISTINCT source) AS sources")

        // group by
        val groupBy = makeGroupBys(groupBys, projection)
        return embeddingQueryBuilder.buildQuery(resultProjection, null, groupBy, null, sortOrder, null)
    }

    private fun makeGroupBys(groupBys0: Array<String>?, projection: Array<String>): String {
        // group by
        var groupBys = groupBys0
        if (groupBys == null) {
            groupBys = Array(projection.size) {
                projection[it].replaceFirst("\\sAS\\s*.*$".toRegex(), "")
            }
        }
        return join(",", groupBys)
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
    fun makeQuerySql0(
        table1: String, table2: String,  //
        table1Projection: Array<String>, table2Projection: Array<String>,  //
        unionProjection: Array<String>?, projection: Array<String>,  //
        selection: String?,  //
        groupBys: Array<String>?, sortOrder: String?, tag: String,
    ): String {
        val actualUnionProjection = appendProjection(unionProjection, "source")
        val table1ProjectionList = listOf(*table1Projection)
        val table2ProjectionList = listOf(*table2Projection)

        // predicate matrix
        val pmSubQueryBuilder = SQLiteQueryBuilder()
        pmSubQueryBuilder.setTables(table1)
        val pmSubquery = pmSubQueryBuilder.buildUnionSubQuery(
            "source",  //
            actualUnionProjection,  //
            HashSet(table1ProjectionList),  //
            0,  //
            "pm$tag",  //
            selection,  //
            null,  //
            null
        )

        // sqlunet table
        val sqlunetSubQueryBuilder = SQLiteQueryBuilder()
        sqlunetSubQueryBuilder.setTables(table2)
        val sqlunetSubquery = sqlunetSubQueryBuilder.buildUnionSubQuery(
            "source",  //
            actualUnionProjection,  //
            HashSet(table2ProjectionList),  //
            0,  //
            tag,  //
            selection,  //
            null,  //
            null
        )

        // union
        val uQueryBuilder = SQLiteQueryBuilder()
        uQueryBuilder.setDistinct(true)
        val uQuery = uQueryBuilder.buildUnionQuery(arrayOf(pmSubquery, sqlunetSubquery), null, null)

        // embed
        val embeddingQueryBuilder = SQLiteQueryBuilder()
        embeddingQueryBuilder.setTables("($uQuery)")
        val resultProjection = prependProjection(projection, "GROUP_CONCAT(DISTINCT source) AS sources")

        // group by
        var actualGroupBys = groupBys
        if (actualGroupBys == null) {
            actualGroupBys = Array(projection.size) {
                projection[it].replaceFirst("\\sAS\\s*.*$".toRegex(), "")
            }
        }
        val groupBy = join(",", actualGroupBys)
        return embeddingQueryBuilder.buildQuery(resultProjection, null, groupBy, null, sortOrder, null)
    }

    /**
     * Join strings
     * Avoid using either TextUtils.join (Android dependency) or String.join(API)
     *
     * @param delimiter delimiter
     * @param tokens    tokens
     * @return joined
     */
    private fun join(delimiter: CharSequence, tokens: Array<String>): String {
        val length = tokens.size
        if (length == 0) {
            return ""
        }
        val sb = StringBuilder()
        sb.append(tokens[0])
        for (i in 1 until length) {
            sb.append(delimiter)
            sb.append(tokens[i])
        }
        return sb.toString()
    }

    /**
     * Make args for union
     *
     * @param selectionArgs selection arguments
     * @return selection arguments for union
     */
    fun unfoldSelectionArgs(selectionArgs: Array<String>?): Array<String>? {
        if (selectionArgs == null)
            return null
        val selectionArgs2 = Array(2 * selectionArgs.size) {
            val i: Int = it / 2
            selectionArgs[i]
        }
        return selectionArgs2
    }
}
