/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

/**
 * Related frame
 *
 * @param frameId   related frame id
 * @param frameName related frame name
 * @param relation  relation
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FnRelatedFrame private constructor(
    val frameId: Long,
    val frameName: String,
    val relation: String,
) {

    companion object {

        /**
         * Make related frames from string
         *
         * @param relatedFramesString (id:rel|id:rel...)
         * @return list of related frames
         */
        fun make(relatedFramesString: String): List<FnRelatedFrame>? {
            var result: MutableList<FnRelatedFrame>? = null
            val relatedFrames = relatedFramesString.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (relatedFrame in relatedFrames) {
                if (result == null) {
                    result = ArrayList()
                }
                val fields = relatedFrame.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val frameId = fields[0].toLong()
                val frameName = fields[1]
                val relation = fields[2]
                result.add(FnRelatedFrame(frameId, frameName, relation))
            }
            return result
        }
    }
}
