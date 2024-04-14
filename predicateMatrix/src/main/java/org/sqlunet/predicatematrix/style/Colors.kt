/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.predicatematrix.style

import android.content.Context
import android.graphics.Color
import org.sqlunet.predicatematrix.R

/**
 * Color values
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object Colors {

    var predicateNameBackColor = Color.TRANSPARENT

    var predicateNameForeColor = Color.TRANSPARENT

    var groupBackColor = Color.TRANSPARENT

    var groupForeColor = Color.TRANSPARENT

    var roleAliasBackColor = Color.TRANSPARENT

    var roleAliasForeColor = Color.TRANSPARENT

    fun setColorsFromResources(context: Context) {
        // do not reorder : dependent on resource array order
        val palette = context.resources.getIntArray(R.array.palette_pm)
        var i = 0
        predicateNameBackColor = palette[i++]
        predicateNameForeColor = palette[i++]
        groupBackColor = palette[i++]
        groupForeColor = palette[i++]
        roleAliasBackColor = palette[i++]
        roleAliasForeColor = palette[i]
        // increment i if colors added
    }
}
