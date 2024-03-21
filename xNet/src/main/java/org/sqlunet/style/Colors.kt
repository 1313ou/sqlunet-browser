/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.style

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.annotation.ColorRes
import androidx.annotation.StyleRes
import androidx.annotation.StyleableRes
import androidx.core.content.res.ResourcesCompat
import org.sqlunet.xnet.R

/**
 * Color values
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object Colors {

    // COMMON
    var classBackColor = Color.TRANSPARENT
    var classForeColor = Color.TRANSPARENT
    var roleBackColor = Color.TRANSPARENT
    var roleForeColor = Color.TRANSPARENT
    var memberBackColor = Color.TRANSPARENT
    var memberForeColor = Color.TRANSPARENT
    var definitionBackColor = Color.TRANSPARENT
    var definitionForeColor = Color.TRANSPARENT
    var exampleBackColor = Color.TRANSPARENT
    var exampleForeColor = Color.TRANSPARENT
    var relationBackColor = Color.TRANSPARENT
    var relationForeColor = Color.TRANSPARENT
    var dataBackColor = Color.TRANSPARENT
    var dataForeColor = Color.TRANSPARENT
    var wordBackColor = Color.TRANSPARENT
    var wordForeColor = Color.TRANSPARENT
    var casedBackColor = Color.TRANSPARENT
    var casedForeColor = Color.TRANSPARENT
    var pronunciationBackColor = Color.TRANSPARENT
    var pronunciationForeColor = Color.TRANSPARENT
    var posBackColor = Color.TRANSPARENT
    var posForeColor = Color.TRANSPARENT

    // TEXT
    @JvmField
    var textBackColor = Color.TRANSPARENT
    @JvmField
    var textForeColor = Color.TRANSPARENT
    @JvmField
    var textNormalBackColor = Color.TRANSPARENT
    @JvmField
    var textNormalForeColor = Color.TRANSPARENT
    @JvmField
    var textHighlightBackColor = Color.TRANSPARENT
    @JvmField
    var textHighlightForeColor = Color.TRANSPARENT
    @JvmField
    var textDimmedBackColor = Color.TRANSPARENT
    @JvmField
    var textDimmedForeColor = Color.TRANSPARENT
    @JvmField
    var textMatchBackColor = Color.TRANSPARENT
    @JvmField
    var textMatchForeColor = Color.TRANSPARENT

    // SQL
    var sqlKeywordBackColor = Color.TRANSPARENT
    var sqlKeywordForeColor = Color.TRANSPARENT
    var sqlSlashBackColor = Color.TRANSPARENT
    var sqlSlashForeColor = Color.TRANSPARENT
    var sqlQuestionMarkBackColor = Color.TRANSPARENT
    var sqlQuestionMarkForeColor = Color.TRANSPARENT

    // REPORT
    @JvmField
    var storageTypeBackColor = Color.TRANSPARENT
    @JvmField
    var storageTypeForeColor = Color.TRANSPARENT
    @JvmField
    var storageValueBackColor = Color.TRANSPARENT
    @JvmField
    var storageValueForeColor = Color.TRANSPARENT
    @JvmField
    var dirTypeBackColor = Color.TRANSPARENT
    @JvmField
    var dirTypeForeColor = Color.TRANSPARENT
    @JvmField
    var dirValueBackColor = Color.TRANSPARENT
    @JvmField
    var dirValueForeColor = Color.TRANSPARENT
    @JvmField
    var dirOkBackColor = Color.TRANSPARENT
    @JvmField
    var dirOkForeColor = Color.TRANSPARENT
    @JvmField
    var dirFailBackColor = Color.TRANSPARENT
    @JvmField
    var dirFailForeColor = Color.TRANSPARENT

    @JvmStatic
    fun setColorsFromResources(context: Context) {
        // do not reorder : dependent on resource array order
        val palette = context.resources.getIntArray(R.array.palette)
        var i = 0

        // COMMON
        classBackColor = palette[i++]
        classForeColor = palette[i++]
        roleBackColor = palette[i++]
        roleForeColor = palette[i++]
        memberBackColor = palette[i++]
        memberForeColor = palette[i++]
        definitionBackColor = palette[i++]
        definitionForeColor = palette[i++]
        exampleBackColor = palette[i++]
        exampleForeColor = palette[i++]
        relationBackColor = palette[i++]
        relationForeColor = palette[i++]
        dataBackColor = palette[i++]
        dataForeColor = palette[i++]
        wordBackColor = palette[i++]
        wordForeColor = palette[i++]
        casedBackColor = palette[i++]
        casedForeColor = palette[i++]
        pronunciationBackColor = palette[i++]
        pronunciationForeColor = palette[i++]
        posBackColor = palette[i++]
        posForeColor = palette[i++]

        // TEXT
        textBackColor = palette[i++]
        textForeColor = palette[i++]
        textNormalBackColor = palette[i++]
        textNormalForeColor = palette[i++]
        textHighlightBackColor = palette[i++]
        textHighlightForeColor = palette[i++]
        textDimmedBackColor = palette[i++]
        textDimmedForeColor = palette[i++]
        textMatchBackColor = palette[i++]
        textMatchForeColor = palette[i++]

        // SQL
        sqlKeywordBackColor = palette[i++]
        sqlKeywordForeColor = palette[i++]
        sqlSlashBackColor = palette[i++]
        sqlSlashForeColor = palette[i++]
        sqlQuestionMarkBackColor = palette[i++]
        sqlQuestionMarkForeColor = palette[i++]

        // REPORT
        storageTypeBackColor = palette[i++]
        storageTypeForeColor = palette[i++]
        storageValueBackColor = palette[i++]
        storageValueForeColor = palette[i++]
        dirTypeBackColor = palette[i++]
        dirTypeForeColor = palette[i++]
        dirValueBackColor = palette[i++]
        dirValueForeColor = palette[i++]
        dirOkBackColor = palette[i++]
        dirOkForeColor = palette[i++]
        dirFailBackColor = palette[i++]
        dirFailForeColor = palette[i++]
    }

    fun getColors(context: Context, @ColorRes vararg colorIds: Int): IntArray {
        val result = IntArray(colorIds.size)
        for (i in colorIds.indices) {
            result[i] = ResourcesCompat.getColor(context.resources, colorIds[i], null)
        }
        return result
    }

    private const val NOT_DEFINED = -0x55555556

    @JvmStatic
    fun getColorAttrs(context: Context, @StyleRes themeId: Int, @StyleableRes vararg resIds: Int): IntArray {
        val a = context.theme.obtainStyledAttributes(themeId, resIds)
        val result = IntArray(resIds.size)
        for (i in resIds.indices) {
            result[i] = a.getColor(i, NOT_DEFINED)
        }
        a.recycle()
        return result
    }

    private const val DUMP = "Contrast"

    @JvmStatic
    fun dumpColorAttrs(context: Context, @StyleRes themeId: Int, @StyleableRes vararg resIds: Int) {
        val a = context.theme.obtainStyledAttributes(themeId, resIds)
        for (i in 0 until a.length()) {
            val name = context.resources.getResourceName(resIds[i])
            val value = a.getColor(i, NOT_DEFINED)
            Log.i(DUMP, String.format("Attr %s = %s", name, colorToString(value)))
        }
        a.recycle()
    }

    @JvmStatic
    fun colorToString(color: Int): String {
        return when (color) {
            0 -> "transparent"
            -0x1000000 -> "black"
            -0x1 -> "white"
            -0x7f7f7F80 -> "gray"
            else -> '#'.toString() + Integer.toHexString(color)
        }
    }
}
