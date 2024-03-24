/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

import android.database.sqlite.SQLiteDatabase

/**
 * Layer
 *
 * @param layerId   layer id
 * @param layerType layer type
 * @param annoSetId annoSetId
 * @param rank      layer rank
 * @param labels    labels
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class FnLayer private constructor(
    @JvmField val layerId: Long,
    @JvmField val layerType: String,
    @JvmField val annoSetId: Long,
    @JvmField val rank: Int,
    @JvmField val labels: List<FnLabel>?,
) {
    companion object {

        /**
         * From-sentence factory
         *
         * @param connection connection
         * @param sentenceId sentence id
         * @return layers
         */
        @JvmStatic
        fun makeFromSentence(connection: SQLiteDatabase, sentenceId: Long): List<FnLayer>? {
            var result: MutableList<FnLayer>? = null
            FnLayerQueryFromSentenceId(connection, sentenceId).use { query ->
                query.execute()
                while (query.next()) {
                    val layerId = query.layerId
                    val layerType = query.layerType
                    val rank = query.rank
                    val annoSetId = query.annoSetId
                    val labels = query.labels
                    if (result == null) {
                        result = ArrayList()
                    }
                    result!!.add(FnLayer(layerId, layerType, annoSetId, rank, labels))
                }
            }
            return result
        }

        /**
         * From-annoSet factory
         *
         * @param connection connection
         * @param annoSetId  annoset id
         * @return layers
         */
        @JvmStatic
        fun makeFromAnnoSet(connection: SQLiteDatabase, annoSetId: Long): List<FnLayer?>? {
            var result: MutableList<FnLayer?>? = null
            FnLayerQueryFromAnnoSetId(connection, annoSetId).use { query ->
                query.execute()
                while (query.next()) {
                    val layerId = query.layerId
                    val layerType = query.layerType
                    val rank = query.rank
                    val labels = query.labels
                    if (result == null) {
                        result = ArrayList()
                    }
                    result!!.add(FnLayer(layerId, layerType, annoSetId, rank, labels))
                }
            }
            return result
        }
    }
}
