/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

import android.database.sqlite.SQLiteDatabase
import android.util.Pair

/**
 * Lex unit
 *
 * @param luId            lex unit id
 * @param lexUnit         lex unit string
 * @param pos             pos
 * @param definition      definition
 * @param dictionary      definition dictionary
 * @param incorporatedFe  incorporated frame element
 * @param frameId         frame id
 * @param frame           frame
 * @param frameDefinition frame definition
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FnLexUnit private constructor(
    @JvmField val luId: Long,
    @JvmField val lexUnit: String,
    val pos: String,
    @JvmField val definition: String,
    private val dictionary: String,
    private val incorporatedFe: String,
    frameId: Long, frame: String?,
    frameDefinition: String?,
) {

    /**
     * Buddy frame
     */
    @JvmField
    val frame: FnFrame?

    init {
        this.frame = if (frameId == 0L) null else FnFrame(frameId, frame!!, frameDefinition!!, null, null)
    }

    companion object {

        /**
         * Lex unit from lex unit id
         *
         * @param connection connection
         * @param luId       lex unit id
         * @return lex units
         */
        @JvmStatic
        fun makeFromId(connection: SQLiteDatabase, luId: Long): FnLexUnit? {
            FnLexUnitQuery(connection, luId).use {
                it.execute()
                if (it.next()) {
                    // var luId = query.getLuId()
                    val lexUnit = it.lexUnit
                    val pos = it.pos
                    val definition = it.definition
                    val dictionary = it.dictionary
                    val incorporatedFe = it.incorporatedFe
                    val frameId = it.frameId
                    val frame = it.frame
                    val frameDescription = it.frameDescription
                    return FnLexUnit(luId, lexUnit, pos, definition, dictionary, incorporatedFe, frameId, frame, frameDescription)
                }
            }
            return null
        }

        /**
         * Make pairs of (word id-lex units) from query built from word
         *
         * @param connection connection
         * @param word       target word
         * @return paris of (word id-list of lex units)
         */
        @JvmStatic
        fun makeFromWord(connection: SQLiteDatabase, word: String): Pair<Long, List<FnLexUnit>?> {
            var result: MutableList<FnLexUnit>? = null
            FnLexUnitQueryFromWord(connection, false, word).use {
                it.execute()
                var wordId: Long = 0
                while (it.next()) {
                    wordId = it.wordId
                    val luId = it.luId
                    val lexUnit = it.lexUnit
                    val pos = it.pos
                    val definition = it.lexUnitDefinition
                    val dictionary = it.lexUnitDictionary
                    val incorporatedFe = it.incorporatedFe
                    val frameId = it.frameId
                    val frame = it.frame
                    val frameDefinition = it.frameDefinition
                    if (result == null) {
                        result = ArrayList()
                    }
                    result!!.add(FnLexUnit(luId, lexUnit, pos, definition, dictionary, incorporatedFe, frameId, frame, frameDefinition))
                }
                return Pair(wordId, result)
            }
        }

        /**
         * Make pairs of (word id-lex units) from query built from fn word
         *
         * @param connection connection
         * @param fnWord     target fn word
         * @return pairs of (fn word id-list of lex units)
         */
        @JvmStatic
        fun makeFromFnWord(connection: SQLiteDatabase, fnWord: String?): Pair<Long, List<FnLexUnit?>?> {
            var result: MutableList<FnLexUnit?>? = null
            FnLexUnitQueryFromFnWord(connection, true, fnWord).use {
                it.execute()
                var fnWordId: Long = 0
                while (it.next()) {
                    fnWordId = it.fnWordId
                    val luId = it.luId
                    val lexUnit = it.lexUnit
                    val pos = it.pos
                    val definition = it.lexUnitDefinition
                    val dictionary = it.lexUnitDictionary
                    val incorporatedFe = it.incorporatedFe
                    val frameId = it.frameId
                    val frame = it.frame
                    val frameDefinition = it.frameDefinition
                    if (result == null) {
                        result = ArrayList()
                    }
                    result!!.add(FnLexUnit(luId, lexUnit, pos, definition, dictionary, incorporatedFe, frameId, frame, frameDefinition))
                }
                return Pair(fnWordId, result)
            }
        }

        /**
         * Make list of lex units from query built from word id
         *
         * @param connection connection
         * @param wordId     word id to build query from
         * @param pos        pos to build query from, null if any
         * @return list of lex units
         */
        @JvmStatic
        fun makeFromWordId(connection: SQLiteDatabase, wordId: Long, pos: Char?): List<FnLexUnit?>? {
            var result: MutableList<FnLexUnit?>? = null
            FnLexUnitQueryFromWordId(connection, false, wordId, pos).use {
                it.execute()
                while (it.next()) {
                    val luId = it.luId
                    val lexUnit = it.lexUnit
                    val luPos = it.pos
                    val definition = it.definition
                    val dictionary = it.dictionary
                    val incorporatedFe = it.incorporatedFe
                    val frameId = it.frameId
                    val frame = it.frame
                    val frameDescription = it.frameDescription
                    if (result == null) {
                        result = ArrayList()
                    }
                    result!!.add(FnLexUnit(luId, lexUnit, luPos, definition, dictionary, incorporatedFe, frameId, frame, frameDescription))
                }
            }
            return result
        }

        /**
         * Make list of lex units from query built from word id
         *
         * @param connection connection
         * @param fnWordId   fn word id to build query from
         * @param pos        pos to build query from, null if any
         * @return list of lex units
         */
        @JvmStatic
        fun makeFromFnWordId(connection: SQLiteDatabase, fnWordId: Long, pos: Char?): List<FnLexUnit?>? {
            var result: MutableList<FnLexUnit?>? = null
            FnLexUnitQueryFromFnWordId(connection, true, fnWordId, pos).use {
                it.execute()
                while (it.next()) {
                    val luId = it.luId
                    val lexUnit = it.lexUnit
                    val luPos = it.pos
                    val definition = it.definition
                    val dictionary = it.dictionary
                    val incorporatedFe = it.incorporatedFe
                    val frameId = it.frameId
                    val frame = it.frame
                    val frameDescription = it.frameDescription
                    if (result == null) {
                        result = ArrayList()
                    }
                    result!!.add(FnLexUnit(luId, lexUnit, luPos, definition, dictionary, incorporatedFe, frameId, frame, frameDescription))
                }
            }
            return result
        }

        /**
         * Make list of lex units from query built from frame id
         *
         * @param connection connection
         * @param frameId    frame id
         * @return list of lex units
         */
        @JvmStatic
        fun makeFromFrame(connection: SQLiteDatabase, frameId: Long): List<FnLexUnit?>? {
            var result: MutableList<FnLexUnit?>? = null
            FnLexUnitQueryFromFrameId(connection, frameId).use {
                it.execute()
                while (it.next()) {
                    val luId = it.luId
                    val lexUnit = it.lexUnit
                    val pos = it.pos
                    val definition = it.definition
                    val dictionary = it.dictionary
                    val incorporatedFe = it.incorporatedFe
                    if (result == null) {
                        result = ArrayList()
                    }
                    result!!.add(FnLexUnit(luId, lexUnit, pos, definition, dictionary, incorporatedFe, 0, null, null))
                }
            }
            return result
        }
    }
}
