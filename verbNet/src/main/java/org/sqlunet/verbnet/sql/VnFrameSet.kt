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
            VnFrameQueryFromClassIdAndSense(connection, classId, wordId, synsetId).use {
                it.execute()
                var frameSet: VnFrameSet? = null
                while (it.next()) {
                    // data from result set
                    // var frameId = query.getFrameId()
                    val number = it.number
                    val xTag = it.xTag
                    val description1 = it.description1
                    val description2 = it.description2
                    val syntax = it.syntax
                    val semantics = it.semantics
                    val concatExamples = it.example
                    val examples = concatExamples.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    // var quality = query.quality
                    // var synsetSpecific = query.synsetSpecific

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
            VnFrameQueryFromClassId(connection, classId).use {
                it.execute()
                var frameSet: VnFrameSet? = null
                while (it.next()) {
                    // data from result set
                    // var frameId = query.frameId
                    val number = it.number
                    val xTag = it.xTag
                    val description1 = it.description1
                    val description2 = it.description2
                    val syntax = it.syntax
                    val semantics = it.semantics
                    val exampleConcat = it.examples
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
