/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.provider

import org.sqlunet.provider.BaseProvider.Companion.prependProjection
import org.sqlunet.provider.Utils.makeUnionQuery

object QueriesLegacy {

    fun queryLegacy(code: Int, uriLast: String?, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): XNetControl.Result? {
        return queryLegacyMain(code, uriLast, projection0, selection0, selectionArgs0)
    }

    private fun queryLegacyMain(code: Int, @Suppress("unused") uriLast: String?, projection0: Array<String>?, selection0: String?, selectionArgs0: Array<String>?): XNetControl.Result? {
        val table: String
        var projection = projection0
        var groupBy: String? = null
        val sortOrder: String? = null
        when (code) {
            XNetControl.PREDICATEMATRIX -> table = "pm_vn " +
                    "LEFT JOIN pm_pb USING (wordid) " + "LEFT JOIN pm_fn USING (wordid)"

            XNetControl.PREDICATEMATRIX_VERBNET -> table = "pm_vn"
            XNetControl.PREDICATEMATRIX_PROPBANK -> table = "pm_pb"
            XNetControl.PREDICATEMATRIX_FRAMENET -> table = "pm_fn"
            XNetControl.SOURCES -> table = "sources"
            XNetControl.WORDS_FNWORDS_PBWORDS_VNWORDS -> {
                table = "words AS " + XNetContract.AS_WORDS + ' ' +
                        "LEFT JOIN senses AS " + XNetContract.AS_SENSES + " USING (wordid) " +
                        "LEFT JOIN synsets AS " + XNetContract.AS_SYNSETS + " USING (synsetid) " +
                        "LEFT JOIN poses AS " + XNetContract.AS_POSES + " USING (posid) " +
                        "LEFT JOIN casedwords USING (wordid,casedwordid) " +
                        "LEFT JOIN domains USING (domainid) " +
                        "LEFT JOIN fn_words USING (wordid) " +
                        "LEFT JOIN vn_words USING (wordid) " + "LEFT JOIN pb_words USING (wordid)"
                groupBy = "synsetid"
            }

            XNetControl.WORDS_PBWORDS_VNWORDS -> {
                table = "words AS " + XNetContract.AS_WORDS + ' ' +
                        "LEFT JOIN senses AS " + XNetContract.AS_SENSES + " USING (wordid) " +
                        "LEFT JOIN synsets AS " + XNetContract.AS_SYNSETS + " USING (synsetid) " +
                        "LEFT JOIN poses AS " + XNetContract.AS_POSES + " USING (posid) " +
                        "LEFT JOIN casedwords USING (wordid,casedwordid) " +
                        "LEFT JOIN domains USING (domainid) " +
                        "LEFT JOIN vn_words USING (wordid) " + "LEFT JOIN pb_words USING (wordid)"
                groupBy = "synsetid"
            }

            XNetControl.WORDS_VNWORDS_VNCLASSES -> {
                table = "vn_words " +
                        "INNER JOIN vn_members_senses USING (vnwordid,wordid) " +
                        "INNER JOIN vn_classes AS " + XNetContract.AS_CLASSES + " USING (classid) " + "LEFT JOIN synsets USING (synsetid)"
                groupBy = "wordid,synsetid,classid"
            }

            XNetControl.WORDS_PBWORDS_PBROLESETS -> {
                table = "pb_words " +
                        "INNER JOIN pb_rolesets AS " + XNetContract.AS_CLASSES + " USING (pbwordid)"
                groupBy = "wordid,synsetid,rolesetid"
            }

            XNetControl.WORDS_VNWORDS_VNCLASSES_U -> {
                val table1 = "pm_vn " +
                        "INNER JOIN vn_classes USING (classid) " +
                        "LEFT JOIN synsets USING (synsetid)"
                val table2 = "vn_words " +
                        "INNER JOIN vn_members_senses USING (vnwordid,wordid) " +
                        "INNER JOIN vn_classes USING (classid)"
                val unionProjection = arrayOf("wordid", "synsetid", "classid", "class", "classtag", "definition")
                val table2Projection = arrayOf("wordid", "synsetid", "classid", "class", "classtag")
                val groupByArray = arrayOf("wordid", "synsetid", "classid")
                projection = prependProjection(projection, "GROUP_CONCAT(DISTINCT source) AS sources")
                return makeUnionQuery(table1, table2, unionProjection, table2Projection, unionProjection, projection, selection0, selectionArgs0, groupByArray, sortOrder, "vn")
            }

            XNetControl.WORDS_PBWORDS_PBROLESETS_U -> {
                val table1 = "pm_pb " +
                        "INNER JOIN pb_rolesets USING (rolesetid) " +
                        "LEFT JOIN synsets USING (synsetid)"
                val table2 = "pb_words " +
                        "INNER JOIN pb_rolesets USING (pbwordid)"
                val unionProjection = arrayOf("wordid", "synsetid", "rolesetid", "rolesetname", "rolesethead", "rolesetdescr", "definition")
                val table2Projection = arrayOf("wordid", "rolesetid", "rolesetname", "rolesethead", "rolesetdescr")
                val groupByArray = arrayOf("wordid", "synsetid", "rolesetid")
                projection = prependProjection(projection, "GROUP_CONCAT(DISTINCT source) AS sources")
                return makeUnionQuery(table1, table2, unionProjection, table2Projection, unionProjection, projection, selection0, selectionArgs0, groupByArray, sortOrder, "pb")
            }

            XNetControl.WORDS_FNWORDS_FNFRAMES_U -> {
                val table1 = "pm_fn " +
                        "INNER JOIN fn_frames USING (frameid) " +
                        "LEFT JOIN fn_lexunits USING (luid,frameid) " +
                        "LEFT JOIN synsets USING (synsetid)"
                val table2 = "fn_words " +
                        "INNER JOIN fn_lexemes USING (fnwordid) " +
                        "INNER JOIN fn_lexunits USING (luid,posid) " +
                        "INNER JOIN fn_frames USING (frameid)"
                val unionProjection = arrayOf("wordid", "synsetid", "frameid", "frame", "framedefinition", "luid", "lexunit", "ludefinition", "definition")
                val table2Projection = arrayOf("wordid", "frameid", "frame", "framedefinition", "luid", "lexunit", "ludefinition")
                val groupByArray = arrayOf("wordid", "synsetid", "frameid")
                projection = prependProjection(projection, "GROUP_CONCAT(DISTINCT source) AS sources")
                return makeUnionQuery(table1, table2, unionProjection, table2Projection, unionProjection, projection, selection0, selectionArgs0, groupByArray, sortOrder, "fn")
            }

            XNetControl.WORDS_VNWORDS_VNCLASSES_1 -> {
                table = "pm_vn " +
                        "INNER JOIN vn_classes USING (classid) " + "LEFT JOIN synsets USING (synsetid)"
                projection = arrayOf("wordid", "synsetid", "classid", "class", "classtag", "definition")
            }

            XNetControl.WORDS_VNWORDS_VNCLASSES_2 -> {
                table = "vn_words " +
                        "INNER JOIN vn_members_senses USING (vnwordid,wordid) " + "INNER JOIN vn_classes USING (classid)"
                projection = arrayOf("wordid", "synsetid", "classid", "class", "classtag")
            }

            XNetControl.WORDS_VNWORDS_VNCLASSES_1U2 ->
                table = "( " +
                        "SELECT wordid, synsetid, classid, class, classtag, definition, 'pmvn' AS source " +
                        "FROM pm_vn " +
                        "INNER JOIN vn_classes USING (classid) " +
                        "LEFT JOIN synsets USING (synsetid) " +
                        "WHERE (#{selection}) " +
                        "UNION " +
                        "SELECT wordid, synsetid, classid, class, classtag, NULL AS definition, 'vn' AS source " +
                        "FROM vn_words " +
                        "INNER JOIN vn_members_senses USING (vnwordid,wordid) " +
                        "INNER JOIN vn_classes USING (classid) " +
                        "WHERE (#{selection}) " + ")"

            XNetControl.WORDS_PBWORDS_PBROLESETS_1 -> {
                table = "pm_pb " +
                        "INNER JOIN pb_rolesets USING (rolesetid) " + "LEFT JOIN synsets USING (synsetid)"
                projection = arrayOf("wordid", "synsetid", "rolesetid", "rolesetname", "rolesethead", "rolesetdescr", "definition")
            }

            XNetControl.WORDS_PBWORDS_PBROLESETS_2 -> {
                table = "pb_words " +
                        "INNER JOIN pb_rolesets USING (pbwordid)"
                projection = arrayOf("wordid", "rolesetid", "rolesetname", "rolesethead", "rolesetdescr")
            }

            XNetControl.WORDS_PBWORDS_PBROLESETS_1U2 ->
                table = "( " + "SELECT wordid, synsetid, rolesetid, rolesetname, rolesethead, rolesetdescr, definition, 'pmpb' AS source " +
                        "FROM pm_pb " +
                        "INNER JOIN pb_rolesets USING (rolesetid) " +
                        "LEFT JOIN synsets USING (synsetid) " +
                        "WHERE (#{selection}) " +
                        "UNION " +
                        "SELECT wordid, NULL AS synsetid, rolesetid, rolesetname, rolesethead, rolesetdescr, NULL AS definition, 'pb' AS source " +
                        "FROM pb_words " +
                        "INNER JOIN pb_rolesets USING (pbwordid) " +
                        "WHERE (#{selection}) " + ")"

            XNetControl.WORDS_FNWORDS_FNFRAMES_1 -> {
                table = "pm_fn " +
                        "INNER JOIN fn_frames USING (frameid) " +
                        "LEFT JOIN fn_lexunits USING (luid,frameid) " + "LEFT JOIN synsets USING (synsetid)"
                projection = arrayOf("wordid", "synsetid", "frameid", "frame", "framedefinition", "luid", "lexunit", "ludefinition", "definition")
            }

            XNetControl.WORDS_FNWORDS_FNFRAMES_2 -> {
                table = "fn_words " +
                        "INNER JOIN fn_lexemes USING (fnwordid) " +
                        "INNER JOIN fn_lexunits USING (luid,posid) " + "INNER JOIN fn_frames USING (frameid)"
                projection = arrayOf("wordid", "frameid", "frame", "framedefinition", "luid", "lexunit", "ludefinition")
            }

            XNetControl.WORDS_FNWORDS_FNFRAMES_1U2 ->
                table = "( " +
                        "SELECT wordid, synsetid, frameid, frame, framedefinition, luid, lexunit, ludefinition, definition, 'pmfn' AS source " +
                        "FROM pm_fn " +
                        "INNER JOIN fn_frames USING (frameid) " +
                        "LEFT JOIN fn_lexunits USING (luid,frameid) " +
                        "LEFT JOIN synsets USING (synsetid) " +
                        "WHERE (#{selection}) " +
                        "UNION " +
                        "SELECT wordid, NULL AS synsetid, frameid, frame, framedefinition, luid, lexunit, ludefinition, NULL AS definition, 'fn' AS source " +
                        "FROM fn_words " +
                        "INNER JOIN fn_lexemes USING (fnwordid) " +
                        "INNER JOIN fn_lexunits USING (luid,posid) " +
                        "INNER JOIN fn_frames USING (frameid) " +
                        "WHERE (#{selection}) " + ")"

            else -> return null
        }
        return XNetControl.Result(table, projection, selection0, selectionArgs0, groupBy, sortOrder)
    }
}
