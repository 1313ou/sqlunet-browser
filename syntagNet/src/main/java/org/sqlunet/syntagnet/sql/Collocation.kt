/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.sql

import android.database.sqlite.SQLiteDatabase

/**
 * SyntagNet collocation
 *
 * @param word1Id   word 1 id
 * @param word2Id   word 2 id
 * @param synset1Id synset 1 id
 * @param synset2Id synset 2 id
 * @param word1     word 1
 * @param word2     word 2
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal open class Collocation private constructor(
    @JvmField val collocationId: Long,
    @JvmField val word1Id: Long,
    @JvmField val word2Id: Long,
    @JvmField val synset1Id: Long,
    @JvmField val synset2Id: Long,
    @JvmField val word1: String,
    @JvmField val word2: String,
) {
    internal class WithDefinitionAndPos(
        collocationId: Long, word1Id: Long, word2Id: Long, synset1Id: Long, synset2Id: Long, word1: String, word2: String,
        @JvmField val pos1: Char,
        @JvmField val pos2: Char,
        @JvmField val definition1: String,
        @JvmField val definition2: String,
    ) : Collocation(collocationId, word1Id, word2Id, synset1Id, synset2Id, word1, word2) {

        companion object {

            /**
             * Make sets of SyntagNet collocations from query built from word
             *
             * @param connection connection
             * @param targetWord is the word to build query from
             * @return list of SyntagNet collocations
             */
            @JvmStatic
            fun makeFromWord(connection: SQLiteDatabase, targetWord: String): List<WithDefinitionAndPos> {
                val result: MutableList<WithDefinitionAndPos> = ArrayList()
                CollocationQueryFromWord(connection, targetWord).use { query ->
                    query.execute()
                    while (query.next()) {
                        val collocation = makeCollocationWithDefinitionAndPos(query)
                        result.add(collocation)
                    }
                    return result
                }
            }

            /**
             * Make sets of SyntagNet collocations from query built from word id
             *
             * @param connection   connection
             * @param targetWordId is the word id to build query from
             * @return list of SyntagNet collocations
             */
            @JvmStatic
            fun makeFromWordId(connection: SQLiteDatabase, targetWordId: Long): List<WithDefinitionAndPos> {
                val result: MutableList<WithDefinitionAndPos> = ArrayList()
                CollocationQueryFromWordId(connection, targetWordId).use { query ->
                    query.execute()
                    while (query.next()) {
                        val collocation = makeCollocationWithDefinitionAndPos(query)
                        result.add(collocation)
                    }
                }
                return result
            }

            /**
             * Make sets of SyntagNet collocations from query built from word ids
             *
             * @param connection    connection
             * @param targetWordId  is the word id to build query from
             * @param targetWord2Id is the word 2 id to build query from
             * @return list of SyntagNet collocations
             */
            @JvmStatic
            fun makeFromWordIds(connection: SQLiteDatabase, targetWordId: Long, targetWord2Id: Long): List<WithDefinitionAndPos> {
                val result: MutableList<WithDefinitionAndPos> = ArrayList()
                CollocationQueryFromWordIds(connection, targetWordId, targetWord2Id).use { query ->
                    query.execute()
                    while (query.next()) {
                        val collocation = makeCollocationWithDefinitionAndPos(query)
                        result.add(collocation)
                    }
                }
                return result
            }

            /**
             * Make sets of SyntagNet collocations from query built from word id ad synset id
             *
             * @param connection   connection
             * @param targetWordId is the word id to build query from
             * @return list of SyntagNet collocations
             */
            @JvmStatic
            fun makeFromWordIdAndSynsetId(connection: SQLiteDatabase, targetWordId: Long, targetSynsetId: Long): List<WithDefinitionAndPos> {
                val result: MutableList<WithDefinitionAndPos> = ArrayList()
                CollocationQueryFromWordIdAndSynsetId(connection, targetWordId, targetSynsetId).use { query ->
                    query.execute()
                    while (query.next()) {
                        val collocation = makeCollocationWithDefinitionAndPos(query)
                        result.add(collocation)
                    }
                }
                return result
            }

            /**
             * Make sets of SyntagNet collocations from query built from word ids ad synset ids
             *
             * @param connection      connection
             * @param targetWordId    is the word id to build query from
             * @param targetWord2Id   is the word 2 id to build query from
             * @param targetSynsetId  is the synset id to build query from
             * @param targetSynset2Id is the synset 2 id to build query from
             * @return list of SyntagNet collocations
             */
            @JvmStatic
            fun makeFromWordIdAndSynsetIds(connection: SQLiteDatabase, targetWordId: Long, targetSynsetId: Long, targetWord2Id: Long, targetSynset2Id: Long): List<WithDefinitionAndPos> {
                val result: MutableList<WithDefinitionAndPos> = ArrayList()
                CollocationQueryFromWordIdsAndSynsetIds(connection, targetWordId, targetSynsetId, targetWord2Id, targetSynset2Id).use { query ->
                    query.execute()
                    while (query.next()) {
                        val collocation = makeCollocationWithDefinitionAndPos(query)
                        result.add(collocation)
                    }
                }
                return result
            }

            /**
             * Make sets of SyntagNet collocations from query built from collocation id
             *
             * @param connection    connection
             * @param collocationId is the collocation id to build query from
             * @return list of SyntagNet collocations
             */
            @JvmStatic
            fun make(connection: SQLiteDatabase, collocationId: Long): List<WithDefinitionAndPos> {
                val result: MutableList<WithDefinitionAndPos> = ArrayList()
                CollocationQuery(connection, collocationId).use { query ->
                    query.execute()
                    while (query.next()) {
                        val collocation = makeCollocationWithDefinitionAndPos(query)
                        result.add(collocation)
                    }
                }
                return result
            }

            private fun makeCollocationWithDefinitionAndPos(query: BaseCollocationQuery): WithDefinitionAndPos {
                val collocationId = query.id
                val word1Id = query.word1Id
                val word2Id = query.word2Id
                val synset1Id = query.synset1Id
                val synset2Id = query.synset2Id
                val word1 = query.word1
                val word2 = query.word2
                val pos1 = query.pos1!!
                val pos2 = query.pos2!!
                val definition1 = query.definition1
                val definition2 = query.definition2
                return WithDefinitionAndPos(collocationId, word1Id, word2Id, synset1Id, synset2Id, word1, word2, pos1, pos2, definition1, definition2)
            }
        }
    }

    companion object {

        @JvmStatic
        fun makeSelectorFromWord(connection: SQLiteDatabase, targetWord: String): List<Collocation> {
            val result: MutableList<Collocation> = ArrayList()
            CollocationQueryFromWord(connection, targetWord).use { query ->
                query.execute()
                while (query.next()) {
                    val collocation = makeCollocation(query)
                    result.add(collocation)
                }
                return result
            }
        }

        private fun makeCollocation(query: BaseCollocationQuery): Collocation {
            val collocationId = query.id
            val word1Id = query.word1Id
            val word2Id = query.word2Id
            val synset1Id = query.synset1Id
            val synset2Id = query.synset2Id
            val word1 = query.word1
            val word2 = query.word2
            return Collocation(collocationId, word1Id, word2Id, synset1Id, synset2Id, word1, word2)
        }
    }
}
