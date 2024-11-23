/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.style

import android.content.Context
import android.graphics.Color
import org.sqlunet.propbank.R

/**
 * Color values
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object Colors {

    var funcBackColor = Color.TRANSPARENT

    var funcForeColor = Color.TRANSPARENT

    var vnroleBackColor = Color.TRANSPARENT

    var vnroleForeColor = Color.TRANSPARENT

    var fnfeBackColor = Color.TRANSPARENT

    var fnfeForeColor = Color.TRANSPARENT

    var relationBackColor = Color.TRANSPARENT

    var relationForeColor = Color.TRANSPARENT

    fun setColorsFromResources(context: Context) {
        // do not reorder : dependent on resource array order
        val palette = context.resources.getIntArray(R.array.palette_pb)
        var i = 0
        funcBackColor = palette[i++]
        funcForeColor = palette[i++]
        vnroleBackColor = palette[i++]
        vnroleForeColor = palette[i++]
        fnfeBackColor = palette[i++]
        fnfeForeColor = palette[i++]
        relationBackColor = palette[i++]
        relationForeColor = palette[i]
        // increment i if colors added
    }
}
