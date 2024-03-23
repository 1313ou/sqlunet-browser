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
    @JvmField
    var vnPredicateBackColor = Color.TRANSPARENT
    @JvmField
    var vnPredicateForeColor = Color.TRANSPARENT
    @JvmField
    var groupingBackColor = Color.TRANSPARENT
    @JvmField
    var groupingForeColor = Color.TRANSPARENT
    @JvmField
    var vnFrameBackColor = Color.TRANSPARENT
    @JvmField
    var vnFrameForeColor = Color.TRANSPARENT
    @JvmField
    var vnFrameSubnameBackColor = Color.TRANSPARENT
    @JvmField
    var vnFrameSubnameForeColor = Color.TRANSPARENT
    @JvmField
    var themroleBackColor = Color.TRANSPARENT
    @JvmField
    var themroleForeColor = Color.TRANSPARENT
    @JvmField
    var catBackColor = Color.TRANSPARENT
    @JvmField
    var catForeColor = Color.TRANSPARENT
    @JvmField
    var catValueBackColor = Color.TRANSPARENT
    @JvmField
    var catValueForeColor = Color.TRANSPARENT
    @JvmField
    var restrBackColor = Color.TRANSPARENT
    @JvmField
    var restrForeColor = Color.TRANSPARENT
    @JvmField
    var constantBackColor = Color.TRANSPARENT
    @JvmField
    var constantForeColor = Color.TRANSPARENT
    @JvmField
    var eventBackColor = Color.TRANSPARENT
    @JvmField
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
        eventForeColor = palette[i++]
    }
}
