/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * PropBank role sets query from word
 *
 * @param connection connection
 * @param word       target word
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class PbRoleSetQueryFromWord(connection: SQLiteDatabase, word: String?) : DBQuery(connection, QUERY) {

    /**
     * Constructor
     */
    init {
        setParams(word!!)
    }

    /**
     * Word id from the result set
     */
    val wordId: Long
        get() = cursor!!.getLong(0)

    /**
     * Role set id from the result set
     *
     * @return the role set id from the result set
     */
    val roleSetId: Long
        get() = cursor!!.getLong(1)

    /**
     * Role set name from the result set
     *
     * @return the role set name from the result set
     */
    val roleSetName: String
        get() = cursor!!.getString(2)

    /**
     * Role set head
     */
    val roleSetHead: String
        get() = cursor!!.getString(3)

    /**
     * Role set description
     */
    val roleSetDescr: String
        get() = cursor!!.getString(4)

    companion object {

        /**
         * `QUERY` is the SQL statement
         */
        private const val QUERY = SqLiteDialect.PropBankRoleSetQueryFromWord
    }
}
