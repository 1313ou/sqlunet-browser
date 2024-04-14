/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

/**
 * Semantic type
 *
 * @param semTypeId         semtype id
 * @param semTypeName       semtype name
 * @param semTypeDefinition semtype definition
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FnSemType private constructor(
    val semTypeId: Long,
    val semTypeName: String,
    val semTypeDefinition: String,
) {

    companion object {

        /**
         * Make semtypes from string
         *
         * @param semTypesString (id:def|id:def...)
         * @return list of semtypes
         */
        fun make(semTypesString: String): List<FnSemType>? {
            var result: MutableList<FnSemType>? = null
            val semTypes = semTypesString.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (semType in semTypes) {
                if (result == null) {
                    result = ArrayList()
                }
                val fields = semType.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val semTypeId = fields[0].toLong()
                val semTypeName = fields[1]
                val semTypeDefinition = fields[2]
                result.add(FnSemType(semTypeId, semTypeName, semTypeDefinition))
            }
            return result
        }
    }
}
