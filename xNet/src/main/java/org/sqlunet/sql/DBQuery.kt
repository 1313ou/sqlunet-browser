/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.sql

import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase

/**
 * Database query
 *
 * @param connection connection
 * @param statement  is the SQL statement
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class DBQuery protected constructor(connection: SQLiteDatabase, statement: String) : AutoCloseable {

    /**
     * `SQL statement
     */
    protected val statement: PreparedStatement = PreparedStatement(connection, statement)

    /**
     * Result set/cursor
     */
    protected var cursor: Cursor? = null

    /**
     * Execute query
     */
    fun execute() {
        cursor = statement.executeQuery()
    }

    /**
     * Iterate over cursor/result set
     *
     * @return true if more data is available
     */
    operator fun next(): Boolean {
        return cursor != null && cursor!!.moveToNext()
    }

    /**
     * Release resources
     */
    override fun close() {
        try {
            if (cursor != null) {
                cursor!!.close()
            }
            statement.close()
        } catch (e: SQLException) {
            // nothing
        }
    }

    /**
     * Set parameters in prepared SQL statement
     *
     * @param params the parameters
     */
    protected fun setParams(vararg params: Any) {
        for ((i, param) in params.withIndex()) {
            when (param) {
                is String -> statement.setString(i, param)
                is Long -> statement.setLong(i, param)
                is Int -> statement.setInt(i, param)
                is Char -> statement.setString(i, param.toString())
            }
        }
    }
}