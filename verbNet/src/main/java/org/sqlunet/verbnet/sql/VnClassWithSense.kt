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
    @JvmField val className: String,
    @JvmField val classId: Long,
    @JvmField val wordId: Long,
    @JvmField val synsetId: Long?,
    @JvmField val definition: String,
    @JvmField val senseNum: Int,
    @JvmField val senseKey: String,
    @JvmField val quality: Float,
    @JvmField val groupings: String,
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
        @JvmStatic
        fun make(connection: SQLiteDatabase?, wordId: Long, synsetId: Long?): List<VnClassWithSense?>? {
            var result: MutableList<VnClassWithSense?>? = null
            VnClassQueryFromSense(connection, wordId, synsetId).use { query ->
                query.execute()
                while (query.next()) {
                    val className = query.getClassName()
                    val classId = query.getClassId()
                    val synsetSpecificFlag = query.getSynsetSpecific()
                    val definition = query.getDefinition()
                    val sensekey = query.getSenseKey()
                    val sensenum = query.getSenseNum()
                    val quality = query.getQuality()
                    val groupings = query.getGroupings()
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
