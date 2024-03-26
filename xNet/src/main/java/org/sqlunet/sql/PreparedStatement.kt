/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.sql

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.text.TextUtils
import android.util.Log
import android.util.SparseArray
import org.sqlunet.sql.SqlFormatter.format

/**
 * Prepared statement
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class PreparedStatement(
    /**
     * Database
     */
    private val db: SQLiteDatabase,

    /**
     * Sql
     */
    private val sql: String,

) : AutoCloseable {
    /**
     * Selection arguments
     */
    private val selectionArgs = SparseArray<String?>()

    /**
     * Set string argument
     *
     * @param i      ith argument
     * @param string string
     */
    fun setString(i: Int, string: String?) {
        selectionArgs.put(i, string)
    }

    /**
     * Set long argument
     *
     * @param i ith argument
     * @param l long
     */
    fun setLong(i: Int, l: Long) {
        selectionArgs.put(i, l.toString())
    }

    /**
     * Set int argument
     *
     * @param i ith argument
     * @param n int
     */
    fun setInt(i: Int, n: Int) {
        selectionArgs.put(i, n.toString())
    }

    /**
     * Set null parameter
     *
     * @param i    ith parameter
     * @param type type
     */
    fun setNull(i: Int,  @Suppress("unused") type: Int) {
        selectionArgs.put(i, null)
    }

    /**
     * Execute statement
     *
     * @return cursor
     */
    fun executeQuery(): Cursor? {
        val args = toSelectionArgs()
        if (logSql) {
            Log.d(TAG + "SQL", format(sql).toString())
            Log.d(TAG + "ARGS", TextUtils.join(",", args))
        }
        return try {
            db.rawQuery(sql, args)
        } catch (e: Exception) {
            Log.e(TAG, sql + ' ' + Utils.argsToString(*args), e)
            null
        }
    }

    /**
     * Close
     */
    override fun close() {
        // do not close database
        // db.close()
    }

    /**
     * Arguments to string array
     *
     * @return selection arguments as string array
     */
    private fun toSelectionArgs(): Array<String?> {
        val n = selectionArgs.size()
        val args = arrayOfNulls<String>(n)
        for (i in 0 until n) {
            args[i] = selectionArgs[i]
        }
        return args
    }

    companion object {
        private const val TAG = "PreparedStatement"

        /**
         * Whether to output SQL statements
         */
        @JvmField
        var logSql = false
    }
}
