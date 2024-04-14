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

    var feBackColor = Color.TRANSPARENT

    var feForeColor = Color.TRANSPARENT

    var feAbbrevBackColor = Color.TRANSPARENT

    var feAbbrevForeColor = Color.TRANSPARENT

    var fe2BackColor = Color.TRANSPARENT

    var fe2ForeColor = Color.TRANSPARENT

    var fenBackColor = Color.TRANSPARENT

    var fenForeColor = Color.TRANSPARENT

    var fenWithinDefBackColor = Color.TRANSPARENT

    var fenWithinDefForeColor = Color.TRANSPARENT

    var xfenBackColor = Color.TRANSPARENT

    var xfenForeColor = Color.TRANSPARENT

    var fexBackColor = Color.TRANSPARENT

    var fexForeColor = Color.TRANSPARENT

    var fexWithinDefBackColor = Color.TRANSPARENT

    var fexWithinDefForeColor = Color.TRANSPARENT

    var metaFrameDefinitionBackColor = Color.TRANSPARENT

    var metaFrameDefinitionForeColor = Color.TRANSPARENT

    var metaFeDefinitionBackColor = Color.TRANSPARENT

    var metaFeDefinitionForeColor = Color.TRANSPARENT

    var tag1BackColor = Color.TRANSPARENT

    var tag1ForeColor = Color.TRANSPARENT

    var tag2BackColor = Color.TRANSPARENT

    var tag2ForeColor = Color.TRANSPARENT

    var exBackColor = Color.TRANSPARENT

    var exForeColor = Color.TRANSPARENT

    var xBackColor = Color.TRANSPARENT

    var xForeColor = Color.TRANSPARENT

    var tBackColor = Color.TRANSPARENT

    var tForeColor = Color.TRANSPARENT

    var governorTypeBackColor = Color.TRANSPARENT

    var governorTypeForeColor = Color.TRANSPARENT

    var governorBackColor = Color.TRANSPARENT

    var governorForeColor = Color.TRANSPARENT

    var annoSetBackColor = Color.TRANSPARENT

    var annoSetForeColor = Color.TRANSPARENT

    var layerTypeBackColor = Color.TRANSPARENT

    var layerTypeForeColor = Color.TRANSPARENT

    var labelBackColor = Color.TRANSPARENT

    var labelForeColor = Color.TRANSPARENT

    var subtextBackColor = Color.TRANSPARENT

    var subtextForeColor = Color.TRANSPARENT
    private var groupBackColor = Color.TRANSPARENT
    private var groupForeColor = Color.TRANSPARENT

    var targetBackColor = Color.TRANSPARENT

    var targetForeColor = Color.TRANSPARENT

    var targetHighlightTextBackColor = Color.TRANSPARENT

    var targetHighlightTextForeColor = Color.TRANSPARENT

    var ptBackColor = Color.TRANSPARENT

    var ptForeColor = Color.TRANSPARENT

    var gfBackColor = Color.TRANSPARENT

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
