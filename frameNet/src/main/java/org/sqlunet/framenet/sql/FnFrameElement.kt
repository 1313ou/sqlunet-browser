/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

import android.database.sqlite.SQLiteDatabase

/**
 * Frame element
 *
 * @param feId         FE id
 * @param feTypeId     FE type id
 * @param feType       FE type
 * @param feDefinition FE definition
 * @param feAbbrev     FE abbrev
 * @param coreType     FE core type
 * @param semTypes     FE sem types
 * @param isCore       whether FE is core
 * @param coreSet      core set number
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class FnFrameElement private constructor(
    @JvmField val feId: Long,
    private val feTypeId: Long,
    @JvmField val feType: String,
    @JvmField val feDefinition: String,
    private val feAbbrev: String,
    @JvmField val coreType: String,
    semTypes: String?,
    private val isCore: Boolean,
    @JvmField val coreSet: Int,
) {

    /**
     * FE sem types
     */
    @JvmField
    val semTypes: Array<String>?

    init {
        this.semTypes = semTypes?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
    }

    companion object {

        /**
         * Make sets of FEs from frame id
         *
         * @param connection connection
         * @param frameId    target frame id
         * @return list of FEs
         */
        @JvmStatic
        fun make(connection: SQLiteDatabase, frameId: Long): List<FnFrameElement?>? {
            var result: MutableList<FnFrameElement?>? = null
            FnFrameElementQueryFromFrameId(connection, frameId).use {
                it.execute()
                while (it.next()) {
                    val feTypeId = it.fETypeId
                    val feType = it.fEType
                    val feId = it.fEId
                    val feDefinition = it.fEDefinition
                    val feAbbrev = it.fEAbbrev
                    val feCoreType = it.fECoreType
                    val semTypes = it.semTypes
                    val isCore = it.isCore
                    val coreSet = it.coreSet
                    if (result == null) {
                        result = ArrayList()
                    }
                    result!!.add(FnFrameElement(feId, feTypeId, feType, feDefinition, feAbbrev, feCoreType, semTypes, isCore, coreSet))
                }
            }
            return result
        }
    }
}
