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
    val layerId: Long,
    val layerType: String,
    val annoSetId: Long,
    val rank: Int,
    val labels: List<FnLabel>?,
) {

    companion object {

        /**
         * From-sentence factory
         *
         * @param connection connection
         * @param sentenceId sentence id
         * @return layers
         */
        fun makeFromSentence(connection: SQLiteDatabase, sentenceId: Long): List<FnLayer>? {
            var result: MutableList<FnLayer>? = null
            FnLayerQueryFromSentenceId(connection, sentenceId).use {
                it.execute()
                while (it.next()) {
                    val layerId = it.layerId
                    val layerType = it.layerType
                    val rank = it.rank
                    val annoSetId = it.annoSetId
                    val labels = it.labels
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
        fun makeFromAnnoSet(connection: SQLiteDatabase, annoSetId: Long): List<FnLayer?>? {
            var result: MutableList<FnLayer?>? = null
            FnLayerQueryFromAnnoSetId(connection, annoSetId).use {
                it.execute()
                while (it.next()) {
                    val layerId = it.layerId
                    val layerType = it.layerType
                    val rank = it.rank
                    val labels = it.labels
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
