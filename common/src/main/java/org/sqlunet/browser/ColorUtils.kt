/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources

/**
 * Color utils
 *
 * @author Bernard Bou
 */
object ColorUtils {

    fun tint(color: Int, vararg drawables: Drawable) {
        for (drawable in drawables) {
            tint(drawable, color)
        }
    }

    fun tint(drawable: Drawable, color: Int) {
        drawable.setTint(color)
        //DrawableCompat.setTint(drawable, iconTint)
    }

    fun getColors(context: Context, vararg colorRes: Int): IntArray {
        val result = IntArray(colorRes.size)
        for (i in colorRes.indices) {
            result[i] = getColor(context, colorRes[i])
        }
        return result
    }

    @ColorInt
    fun getColor(context: Context, @ColorRes colorRes: Int): Int {
        val res = context.resources
        val theme = context.theme
        return getColor(res, theme, colorRes)
    }

    @ColorInt
    fun getColor(res: Resources, theme: Theme?, @ColorRes colorRes: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            res.getColor(colorRes, theme)
        } else {
            @Suppress("DEPRECATION")
            res.getColor(colorRes)
        }
    }

    fun getDrawable(context: Context, @DrawableRes resId: Int): Drawable? {
        return AppCompatResources.getDrawable(context, resId)
    }

    @ColorInt
    fun fetchColor(context: Context, @AttrRes attr: Int): Int {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    @ColorRes
    fun fetchColorRes(context: Context, @AttrRes attr: Int): Int {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.resourceId
    }
}
