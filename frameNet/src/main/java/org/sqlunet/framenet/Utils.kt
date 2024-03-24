/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet

import org.sqlunet.framenet.sql.FnLabel

object Utils {

    /**
     * Convert WordNet pos character to pos id
     *
     * @param c pos character
     * @return pos id
     */
    fun posToPosId(c: Char?): Int {
        if (c == null) {
            return -1
        }
        when (c) {
            'n' -> return 9
            'v' -> return 14
            's', 'a' -> return 1 // adj
            'r' -> return 2 // adv
        }
        return -1
    }

    /**
     * Parse labels from the result set
     *
     * @param labelsString label string
     * @return the labels from the result set
     */
    @JvmStatic
    fun parseLabels(labelsString: String?): List<FnLabel>? {
        if (labelsString == null) {
            return null
        }
        var result: MutableList<FnLabel>? = null
        val labels = labelsString.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (label in labels) {
            val fields = label.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val from = if (fields.isEmpty()) null else fields[0]
            val to = if (fields.size < 2) null else fields[1]
            val value = if (fields.size < 3) null else fields[2]
            val iType = if (fields.size < 4) null else fields[3]
            val bgColor = if (fields.size < 5) null else fields[4]
            val fgColor = if (fields.size < 6) null else fields[5]
            if (result == null) {
                result = ArrayList()
            }
            result.add(FnLabel(from!!, to!!, value!!, iType!!, bgColor, fgColor))
        }
        return result
    }
}
