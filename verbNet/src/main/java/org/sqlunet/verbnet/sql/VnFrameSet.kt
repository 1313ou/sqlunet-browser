/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.sql

import android.database.sqlite.SQLiteDatabase

/**
 * Frames attached to a VerbNet class
 *
 * @param frames list of frames
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class VnFrameSet private constructor(
    @JvmField val frames: MutableList<VnFrame>,
) {
    companion object {
        /**
         * Make a set of frames from query built from class id, word id and synset id
         *
         * @param connection connection
         * @param classId    class id to build query from
         * @param wordId     word id to build query from
         * @param synsetId   synset id to build query from (null for any)
         * @return set of frames
         */
        @JvmStatic
        fun make(connection: SQLiteDatabase, classId: Long, wordId: Long, synsetId: Long?): VnFrameSet? {
            VnFrameQueryFromClassIdAndSense(connection, classId, wordId, synsetId).use { query ->
                query.execute()
                var frameSet: VnFrameSet? = null
                while (query.next()) {
                    // data from result set
                    // final long frameId = query.getFrameId()
                    val number = query.number
                    val xTag = query.xTag
                    val description1 = query.description1
                    val description2 = query.description2
                    val syntax = query.syntax
                    val semantics = query.semantics
                    val concatExamples = query.example
                    val examples = concatExamples.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    // final float quality = query.quality
                    // final boolean synsetSpecific = query.synsetSpecific

                    // frame
                    val frame = VnFrame(number, xTag, description1, description2, syntax, semantics, *examples)

                    // allocate
                    if (frameSet == null) {
                        frameSet = VnFrameSet(ArrayList())
                    }

                    // if same class, addItem role to frame set
                    frameSet.frames.add(frame)
                }
                return frameSet
            }
        }

        /**
         * Make frame set for class from query built from class id
         *
         * @param connection connection
         * @param classId    class id
         * @return set of frames
         */
        @JvmStatic
        fun make(connection: SQLiteDatabase, classId: Long): VnFrameSet? {
            VnFrameQueryFromClassId(connection, classId).use { query ->
                query.execute()
                var frameSet: VnFrameSet? = null
                while (query.next()) {
                    // data from result set
                    // final long frameId = query.frameId
                    val number = query.number
                    val xTag = query.xTag
                    val description1 = query.description1
                    val description2 = query.description2
                    val syntax = query.syntax
                    val semantics = query.semantics
                    val exampleConcat = query.examples
                    val examples = exampleConcat.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    // frame
                    val frame = VnFrame(number, xTag, description1, description2, syntax, semantics, *examples)

                    // allocate
                    if (frameSet == null) {
                        frameSet = VnFrameSet(ArrayList())
                    }

                    // if same class, addItem role to frame set
                    frameSet.frames.add(frame)
                }
                return frameSet
            }
        }
    }
}