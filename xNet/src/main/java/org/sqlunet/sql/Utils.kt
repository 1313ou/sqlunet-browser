/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.sql

/**
 * Prepared statement
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object Utils {

    /**
     * Join ids into string
     *
     * @param ids ids
     * @return joined ids
     */
    fun join(vararg ids: Int): String {
        val sb = StringBuilder()
        var first = true
        for (id in ids) {
            if (first) {
                first = false
            } else {
                sb.append(',')
            }
            sb.append(id)
        }
        return sb.toString()
    }

    /**
     * Join ids into string
     *
     * @param ids ids
     * @return joined ids
     */
    fun join(vararg ids: Long): String {
        val sb = StringBuilder()
        var first = true
        for (id in ids) {
            if (first) {
                first = false
            } else {
                sb.append(',')
            }
            sb.append(id)
        }
        return sb.toString()
    }

    /**
     * Join strings
     *
     * @param strings strings to join
     * @return joined strings
     */
    fun join(vararg strings: String): String {
        val sb = StringBuilder()
        var first = true
        for (string in strings) {
            if (first) {
                first = false
            } else {
                sb.append(',')
            }
            sb.append(string)
        }
        return sb.toString()
    }

    /**
     * Arguments to single string
     *
     * @param args arguments
     * @return arguments as string
     */
    fun argsToString(vararg args: String?): String {
        val sb = StringBuilder()
        if (args.isNotEmpty()) {
            for (arg in args) {
                if (sb.isNotEmpty()) {
                    sb.append(", ")
                }
                sb.append(arg)
            }
        }
        return sb.toString()
    }

    /**
     * Replace argument placeholders with argument values
     *
     * @param sql  sql
     * @param args arguments
     * @return expanded sql
     */
    fun replaceArgs(sql: String, vararg args: String?): String {
        var processedSql = sql
        if (args.isNotEmpty()) {
            for (a in args) {
                processedSql = processedSql.replaceFirst("\\?".toRegex(), a!!)
            }
        }
        return processedSql
    }

    fun toArgs(vararg args: String): Array<String> {
        val result = Array(args.size) {
            // A single quote within the string can be encoded by putting two single quotes in a row
            val arg = args[it].replace("'".toRegex(), "''")
            if (arg.matches("-?\\d+(\\.\\d+)?".toRegex()))
                arg
            else
                "'$arg'"
        }
        return result
    }

    /**
     * Convert to ids
     *
     * @param string ,-separated string of ids
     * @return ids as long array
     */
    fun toIds(string: String): LongArray {
        val strings = string.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val ids = LongArray(strings.size)
        for (i in strings.indices) {
            ids[i] = strings[i].toLong()
        }
        return ids
    }
}
