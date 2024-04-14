/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.style

import android.content.Context
import android.graphics.Color
import org.sqlunet.syntagnet.R

/**
 * Color values
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object Colors {

    var collocationBackColor = Color.TRANSPARENT

    var collocationForeColor = Color.TRANSPARENT

    var word1BackColor = Color.TRANSPARENT

    var word1ForeColor = Color.TRANSPARENT

    var word2BackColor = Color.TRANSPARENT

    var word2ForeColor = Color.TRANSPARENT

    var definition1BackColor = Color.TRANSPARENT

    var definition1ForeColor = Color.TRANSPARENT

    var definition2BackColor = Color.TRANSPARENT

    var definition2ForeColor = Color.TRANSPARENT

    var idsBackColor = Color.TRANSPARENT

    var idsForeColor = Color.TRANSPARENT
    fun setColorsFromResources(context: Context) {
        // do not reorder : dependent on resource array order
        val palette = context.resources.getIntArray(R.array.palette_sn)
        var i = 0
        collocationBackColor = palette[i++]
        collocationForeColor = palette[i++]
        word1BackColor = palette[i++]
        word1ForeColor = palette[i++]
        word2BackColor = palette[i++]
        word2ForeColor = palette[i++]
        definition1BackColor = palette[i++]
        definition1ForeColor = palette[i++]
        definition2BackColor = palette[i++]
        definition2ForeColor = palette[i++]
        idsBackColor = palette[i++]
        idsForeColor = palette[i]
        // increment i if colors added
    }
}
