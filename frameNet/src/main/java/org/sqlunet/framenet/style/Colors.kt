/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.style

import android.content.Context
import android.graphics.Color
import org.sqlunet.framenet.BuildConfig
import org.sqlunet.framenet.R

/**
 * Color values
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object Colors {
    @JvmField
    var feBackColor = Color.TRANSPARENT
    @JvmField
    var feForeColor = Color.TRANSPARENT
    @JvmField
    var feAbbrevBackColor = Color.TRANSPARENT
    @JvmField
    var feAbbrevForeColor = Color.TRANSPARENT
    @JvmField
    var fe2BackColor = Color.TRANSPARENT
    @JvmField
    var fe2ForeColor = Color.TRANSPARENT
    @JvmField
    var fenBackColor = Color.TRANSPARENT
    @JvmField
    var fenForeColor = Color.TRANSPARENT
    @JvmField
    var fenWithinDefBackColor = Color.TRANSPARENT
    @JvmField
    var fenWithinDefForeColor = Color.TRANSPARENT
    @JvmField
    var xfenBackColor = Color.TRANSPARENT
    @JvmField
    var xfenForeColor = Color.TRANSPARENT
    @JvmField
    var fexBackColor = Color.TRANSPARENT
    @JvmField
    var fexForeColor = Color.TRANSPARENT
    @JvmField
    var fexWithinDefBackColor = Color.TRANSPARENT
    @JvmField
    var fexWithinDefForeColor = Color.TRANSPARENT
    @JvmField
    var metaFrameDefinitionBackColor = Color.TRANSPARENT
    @JvmField
    var metaFrameDefinitionForeColor = Color.TRANSPARENT
    @JvmField
    var metaFeDefinitionBackColor = Color.TRANSPARENT
    @JvmField
    var metaFeDefinitionForeColor = Color.TRANSPARENT
    @JvmField
    var tag1BackColor = Color.TRANSPARENT
    @JvmField
    var tag1ForeColor = Color.TRANSPARENT
    @JvmField
    var tag2BackColor = Color.TRANSPARENT
    @JvmField
    var tag2ForeColor = Color.TRANSPARENT
    @JvmField
    var exBackColor = Color.TRANSPARENT
    @JvmField
    var exForeColor = Color.TRANSPARENT
    @JvmField
    var xBackColor = Color.TRANSPARENT
    @JvmField
    var xForeColor = Color.TRANSPARENT
    @JvmField
    var tBackColor = Color.TRANSPARENT
    @JvmField
    var tForeColor = Color.TRANSPARENT
    @JvmField
    var governorTypeBackColor = Color.TRANSPARENT
    @JvmField
    var governorTypeForeColor = Color.TRANSPARENT
    @JvmField
    var governorBackColor = Color.TRANSPARENT
    @JvmField
    var governorForeColor = Color.TRANSPARENT
    @JvmField
    var annoSetBackColor = Color.TRANSPARENT
    @JvmField
    var annoSetForeColor = Color.TRANSPARENT
    @JvmField
    var layerTypeBackColor = Color.TRANSPARENT
    @JvmField
    var layerTypeForeColor = Color.TRANSPARENT
    @JvmField
    var labelBackColor = Color.TRANSPARENT
    @JvmField
    var labelForeColor = Color.TRANSPARENT
    @JvmField
    var subtextBackColor = Color.TRANSPARENT
    @JvmField
    var subtextForeColor = Color.TRANSPARENT
    private var groupBackColor = Color.TRANSPARENT
    private var groupForeColor = Color.TRANSPARENT
    @JvmField
    var targetBackColor = Color.TRANSPARENT
    @JvmField
    var targetForeColor = Color.TRANSPARENT
    @JvmField
    var targetHighlightTextBackColor = Color.TRANSPARENT
    @JvmField
    var targetHighlightTextForeColor = Color.TRANSPARENT
    @JvmField
    var ptBackColor = Color.TRANSPARENT
    @JvmField
    var ptForeColor = Color.TRANSPARENT
    @JvmField
    var gfBackColor = Color.TRANSPARENT
    @JvmField
    var gfForeColor = Color.TRANSPARENT

    fun setColorsFromResources(context: Context) {
        // do not reorder : dependent on resource array order
        val palette = context.resources.getIntArray(R.array.palette_fn)
        var i = 0
        feBackColor = palette[i++]
        feForeColor = palette[i++]
        feAbbrevBackColor = palette[i++]
        feAbbrevForeColor = palette[i++]
        fe2BackColor = palette[i++]
        fe2ForeColor = palette[i++]
        fenBackColor = palette[i++]
        fenForeColor = palette[i++]
        fenWithinDefBackColor = palette[i++]
        fenWithinDefForeColor = palette[i++]
        xfenBackColor = palette[i++]
        xfenForeColor = palette[i++]
        fexBackColor = palette[i++]
        fexForeColor = palette[i++]
        fexWithinDefBackColor = palette[i++]
        fexWithinDefForeColor = palette[i++]
        metaFrameDefinitionBackColor = palette[i++]
        metaFrameDefinitionForeColor = palette[i++]
        metaFeDefinitionBackColor = palette[i++]
        metaFeDefinitionForeColor = palette[i++]
        tag1BackColor = palette[i++]
        tag1ForeColor = palette[i++]
        tag2BackColor = palette[i++]
        tag2ForeColor = palette[i++]
        exBackColor = palette[i++]
        exForeColor = palette[i++]
        xBackColor = palette[i++]
        xForeColor = palette[i++]
        tBackColor = palette[i++]
        tForeColor = palette[i++]
        governorTypeBackColor = palette[i++]
        governorTypeForeColor = palette[i++]
        governorBackColor = palette[i++]
        governorForeColor = palette[i++]
        annoSetBackColor = palette[i++]
        annoSetForeColor = palette[i++]
        layerTypeBackColor = palette[i++]
        layerTypeForeColor = palette[i++]
        labelBackColor = palette[i++]
        labelForeColor = palette[i++]
        subtextBackColor = palette[i++]
        subtextForeColor = palette[i++]
        groupBackColor = palette[i++]
        groupForeColor = palette[i++]
        targetBackColor = palette[i++]
        targetForeColor = palette[i++]
        targetHighlightTextBackColor = palette[i++]
        targetHighlightTextForeColor = palette[i++]
        ptBackColor = palette[i++]
        ptForeColor = palette[i++]
        gfBackColor = palette[i++]
        gfForeColor = palette[i++]
        if (BuildConfig.DEBUG && i != palette.size) {
            throw AssertionError("Assertion failed")
        }
    }
}
