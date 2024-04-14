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
    val roleSetName: String,
    val roleSetHead: String,
    val roleSetDescr: String,
    val roleSetId: Long,
    val wordId: Long,
) {

    companion object {

        /**
         * Make sets of PropBank roleSets from query built from word id
         *
         * @param connection connection
         * @param word       is the word to build query from
         * @return list of PropBank roleSets
         */
        fun makeFromWord(connection: SQLiteDatabase, word: String?): List<PbRoleSet?>? {
            var result: MutableList<PbRoleSet?>? = null
            PbRoleSetQueryFromWord(connection, word).use {
                it.execute()
                while (it.next()) {
                    val roleSetName = it.roleSetName
                    val roleSetHead = it.roleSetHead
                    val roleSetDescr = it.roleSetDescr
                    val roleSetId = it.roleSetId
                    val wordId = it.wordId
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
        fun makeFromWordId(connection: SQLiteDatabase, wordId: Long): List<PbRoleSet?>? {
            var result: MutableList<PbRoleSet?>? = null
            PbRoleSetQueryFromWordId(connection, wordId).use {
                it.execute()
                while (it.next()) {
                    val roleSetName = it.roleSetName
                    val roleSetHead = it.roleSetHead
                    val roleSetDescr = it.roleSetDescr
                    val roleSetId = it.roleSetId
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
        fun make(connection: SQLiteDatabase, roleSetId: Long): List<PbRoleSet?>? {
            var result: MutableList<PbRoleSet?>? = null
            PbRoleSetQuery(connection, roleSetId).use {
                it.execute()
                while (it.next()) {
                    val roleSetName = it.roleSetName
                    val roleSetHead = it.roleSetHead
                    val roleSetDescr = it.roleSetDescr
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
