/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.sql

import android.database.sqlite.SQLiteDatabase

/**
 * VerbNet class with sense
 *
 * @param className  class name
 * @param classId    class id
 * @param wordId     word id
 * @param synsetId   synset id
 * @param definition sense num
 * @param senseNum   sense num
 * @param senseKey   senseKey
 * @param quality    quality
 * @param groupings  groupings
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class VnClassWithSense private constructor(
    val className: String,
    val classId: Long,
    val wordId: Long,
    val synsetId: Long?,
    val definition: String,
    val senseNum: Int,
    val senseKey: String,
    val quality: Float,
    val groupings: String?,
) {

    companion object {

        /**
         * Make sets of VerbNet classes with senses from query built from word id and synset id
         *
         * @param connection connection
         * @param wordId     word id to build query from
         * @param synsetId   synset id to build the query from (null if any)
         * @return list of VerbNet classes
         */
        fun make(connection: SQLiteDatabase, wordId: Long, synsetId: Long?): List<VnClassWithSense>? {
            var result: MutableList<VnClassWithSense>? = null
            VnClassQueryFromSense(connection, wordId, synsetId).use {
                it.execute()
                while (it.next()) {
                    val className = it.className
                    val classId = it.classId
                    val synsetSpecificFlag = it.synsetSpecific
                    val definition = it.definition
                    val sensekey = it.senseKey
                    val sensenum = it.senseNum
                    val quality = it.quality
                    val groupings = it.groupings
                    if (result == null) {
                        result = ArrayList()
                    }
                    result!!.add(VnClassWithSense(className, classId, wordId, if (synsetSpecificFlag) synsetId else null, definition, sensenum, sensekey, quality, groupings))
                }
            }
            return result
        }
    }
}
