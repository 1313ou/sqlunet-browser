/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

import android.database.sqlite.SQLiteDatabase

/**
 * Frame
 *
 * @param frameId         frame id
 * @param frameName       frame
 * @param frameDefinition frame definition
 * @param semTypes        semtypes
 * @param relatedFrames   related frames
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FnFrame  internal constructor(
	@JvmField val frameId: Long,
	@JvmField val frameName: String,
	@JvmField val frameDefinition: String,
	@JvmField val semTypes: List<FnSemType>?,
	@JvmField val relatedFrames: List<FnRelatedFrame>?
) {
    companion object {
        /**
         * Frame factory
         *
         * @param connection connection
         * @param frameId    target frame id
         * @return frame
         */
        @JvmStatic
        fun make(connection: SQLiteDatabase, frameId: Long): FnFrame? {
            FnFrameQuery(connection, frameId).use { query ->
                query.execute()
                if (query.next()) {
                    val frameName = query.frame
                    val frameDescription = query.frameDescription
                    // val frameId = query.getFrameId()
                    val semTypes = FnSemType.make(query.semTypes)
                    val relatedFrames = FnRelatedFrame.make(query.relatedFrames)
                    return FnFrame(frameId, frameName, frameDescription, semTypes, relatedFrames)
                }
            }
            return null
        }
    }
}
