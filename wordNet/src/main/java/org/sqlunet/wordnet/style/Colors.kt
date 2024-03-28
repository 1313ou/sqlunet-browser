/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.style

import android.content.Context
import android.graphics.Color
import org.sqlunet.wordnet.R

/**
 * Color values
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object Colors {
    @JvmField
    var membersBackColor = Color.TRANSPARENT
    @JvmField
    var membersForeColor = Color.TRANSPARENT
    @JvmField
    var wordBackColor = Color.TRANSPARENT
    @JvmField
    var wordForeColor = Color.TRANSPARENT
    @JvmStatic
    fun setColorsFromResources(context: Context) {
        // do not reorder : dependent on resource array order
        val palette = context.resources.getIntArray(R.array.palette_wn)
        var i = 0
        membersBackColor = palette[i++]
        membersForeColor = palette[i++]
        wordBackColor = palette[i++]
        wordForeColor = palette[i]
        // increment i if colors added
    }
}
