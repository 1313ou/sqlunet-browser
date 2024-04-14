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

    var thetaBackColor = Color.TRANSPARENT

    var thetaForeColor = Color.TRANSPARENT

    var relationBackColor = Color.TRANSPARENT

    var relationForeColor = Color.TRANSPARENT

    fun setColorsFromResources(context: Context) {
        // do not reorder : dependent on resource array order
        val palette = context.resources.getIntArray(R.array.palette_pb)
        var i = 0
        thetaBackColor = palette[i++]
        thetaForeColor = palette[i++]
        relationBackColor = palette[i++]
        relationForeColor = palette[i]
        // increment i if colors added
    }
}
