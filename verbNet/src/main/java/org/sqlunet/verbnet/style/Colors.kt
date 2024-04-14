/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.style

import android.content.Context
import android.graphics.Color
import org.sqlunet.verbnet.R

/**
 * Color values
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object Colors {

    var vnPredicateBackColor = Color.TRANSPARENT

    var vnPredicateForeColor = Color.TRANSPARENT

    var groupingBackColor = Color.TRANSPARENT

    var groupingForeColor = Color.TRANSPARENT

    var vnFrameBackColor = Color.TRANSPARENT

    var vnFrameForeColor = Color.TRANSPARENT

    var vnFrameSubnameBackColor = Color.TRANSPARENT

    var vnFrameSubnameForeColor = Color.TRANSPARENT

    var themroleBackColor = Color.TRANSPARENT

    var themroleForeColor = Color.TRANSPARENT

    var catBackColor = Color.TRANSPARENT

    var catForeColor = Color.TRANSPARENT

    var catValueBackColor = Color.TRANSPARENT

    var catValueForeColor = Color.TRANSPARENT

    var restrBackColor = Color.TRANSPARENT

    var restrForeColor = Color.TRANSPARENT

    var constantBackColor = Color.TRANSPARENT

    var constantForeColor = Color.TRANSPARENT

    var eventBackColor = Color.TRANSPARENT

    var eventForeColor = Color.TRANSPARENT

    fun setColorsFromResources(context: Context) {
        // do not reorder : dependent on resource array order
        val palette = context.resources.getIntArray(R.array.palette_vn)
        var i = 0
        vnPredicateBackColor = palette[i++]
        vnPredicateForeColor = palette[i++]
        groupingBackColor = palette[i++]
        groupingForeColor = palette[i++]
        vnFrameBackColor = palette[i++]
        vnFrameForeColor = palette[i++]
        vnFrameSubnameBackColor = palette[i++]
        vnFrameSubnameForeColor = palette[i++]
        themroleBackColor = palette[i++]
        themroleForeColor = palette[i++]
        catBackColor = palette[i++]
        catForeColor = palette[i++]
        catValueBackColor = palette[i++]
        catValueForeColor = palette[i++]
        restrBackColor = palette[i++]
        restrForeColor = palette[i++]
        constantBackColor = palette[i++]
        constantForeColor = palette[i++]
        eventBackColor = palette[i++]
        eventForeColor = palette[i]
        // increment i if colors added
    }
}
