/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.bnc.style

import android.content.Context
import android.graphics.Color
import org.sqlunet.bnc.R

/**
 * Color values
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object Colors {

    var bncHeaderBackColor = Color.TRANSPARENT
    var bncHeaderForeColor = Color.TRANSPARENT

    @JvmStatic
    fun setColorsFromResources(context: Context) {
        // do not reorder : dependent on resource array order
        val palette = context.resources.getIntArray(R.array.palette_bnc)
        var i = 0
        bncHeaderBackColor = palette[i++]
        bncHeaderForeColor = palette[i]
        // increment i if colors added
    }
}
