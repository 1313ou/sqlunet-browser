/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.sql

import android.database.sqlite.SQLiteDatabase

/**
 * PropBank role set
 *
 * @param roleSetName  name
 * @param roleSetHead  head
 * @param roleSetDescr description
 * @param roleSetId    role set id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class PbRoleSet private constructor(
    @JvmField val roleSetName: String,
    @JvmField val roleSetHead: String,
    @JvmField val roleSetDescr: String,
    @JvmField val roleSetId: Long,
    @JvmField val wordId: Long,
) {
    companion object {

        /**
         * Make sets of PropBank roleSets from query built from word id
         *
         * @param connection connection
         * @param word       is the word to build query from
         * @return list of PropBank roleSets
         */
        @JvmStatic
        fun makeFromWord(connection: SQLiteDatabase, word: String?): List<PbRoleSet?>? {
            var result: MutableList<PbRoleSet?>? = null
            PbRoleSetQueryFromWord(connection, word).use { query ->
                query.execute()
                while (query.next()) {
                    val roleSetName = query.roleSetName
                    val roleSetHead = query.roleSetHead
                    val roleSetDescr = query.roleSetDescr
                    val roleSetId = query.roleSetId
                    val wordId = query.wordId
                    if (result == null) {
                        result = ArrayList()
                    }
                    result!!.add(PbRoleSet(roleSetName, roleSetHead, roleSetDescr, roleSetId, wordId))
                }
                return result
            }
        }

        /**
         * Make sets of PropBank roleSets from query built from word id
         *
         * @param connection connection
         * @param wordId     is the word id to build query from
         * @return list of PropBank roleSets
         */
        @JvmStatic
        fun makeFromWordId(connection: SQLiteDatabase, wordId: Long): List<PbRoleSet?>? {
            var result: MutableList<PbRoleSet?>? = null
            PbRoleSetQueryFromWordId(connection, wordId).use { query ->
                query.execute()
                while (query.next()) {
                    val roleSetName = query.roleSetName
                    val roleSetHead = query.roleSetHead
                    val roleSetDescr = query.roleSetDescr
                    val roleSetId = query.roleSetId
                    if (result == null) {
                        result = ArrayList()
                    }
                    result!!.add(PbRoleSet(roleSetName, roleSetHead, roleSetDescr, roleSetId, wordId))
                }
            }
            return result
        }

        /**
         * Make sets of PropBank roleSets from query built from roleSet id
         *
         * @param connection connection
         * @param roleSetId  is the role set id to build query from
         * @return list of PropBank role sets
         */
        @JvmStatic
        fun make(connection: SQLiteDatabase, roleSetId: Long): List<PbRoleSet?>? {
            var result: MutableList<PbRoleSet?>? = null
            PbRoleSetQuery(connection, roleSetId).use { query ->
                query.execute()
                while (query.next()) {
                    val roleSetName = query.roleSetName
                    val roleSetHead = query.roleSetHead
                    val roleSetDescr = query.roleSetDescr
                    if (result == null) {
                        result = ArrayList()
                    }
                    result!!.add(PbRoleSet(roleSetName, roleSetHead, roleSetDescr, roleSetId, 0))
                }
            }
            return result
        }
    }
}
