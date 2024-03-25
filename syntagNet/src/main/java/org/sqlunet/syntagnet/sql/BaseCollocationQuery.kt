/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.sql

import android.database.sqlite.SQLiteDatabase
import org.sqlunet.sql.DBQuery

/**
 * SyntagNet collocation query
 *
 * @param connection connection
 * @param query      query
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal abstract class BaseCollocationQuery(connection: SQLiteDatabase, query: String?) : DBQuery(connection, query!!) {

    // 1         2                 3          4               5
    // "word1id, w1.word AS word1, synset1id, s1.pos AS pos1, s1.definition AS definition1,"
    // 6         7                 8          9               10
    // "word2id, w2.word AS word2, synset2id, s2.pos AS pos2, s2.definition AS definition2,"

    /**
     * Id from the result set
     */
    val id: Long
        get() = cursor!!.getLong(0)

    /**
     * Word 1 id from the result set
     */
    val word1Id: Long
        get() = cursor!!.getLong(1)

    /**
     * Word 2 id from the result set
     */
    val word2Id: Long
        get() = cursor!!.getLong(6)

    /**
     * Synset 1  id from the result set
     */
    val synset1Id: Long
        get() = cursor!!.getLong(3)

    /**
     * Synset 2 id from the result set
     */
    val synset2Id: Long
        get() = cursor!!.getLong(8)

    /**
     * Word 1 from the result set
     */
    val word1: String
        get() = cursor!!.getString(2)

    /**
     * Word 2 from the result set
     */
    val word2: String
        get() = cursor!!.getString(7)

    /**
     * Pos 1 from the result set
     */
    val pos1: Char?
        get() = cursor!!.getString(4)?.get(0)

    /**
     * Pos 2 from the result set
     */
    val pos2: Char?
        get() = cursor!!.getString(9)?.get(0)

    /**
     * Definition 1 from the result set
     */
    val definition1: String
        get() = cursor!!.getString(5)

    /**
     * Definition 2 from the result set
     */
    val definition2: String
        get() = cursor!!.getString(10)
}
