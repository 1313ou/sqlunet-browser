/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.google.android.material.color.MaterialColors

/**
 * Color utils
 *
 * @author Bernard Bou
 */
object ColorUtils {

    // colors

    @ColorInt
    fun getColor(context: Context, @ColorRes colorRes: Int): Int {
        return getColor(context.resources, context.theme, colorRes)
    }

    @ColorInt
    private fun getColor(res: Resources, theme: Theme?, @ColorRes colorRes: Int): Int {
        return res.getColor(colorRes, theme)
    }

    @ColorInt
    fun getColors(context: Context, @ColorRes vararg colorRes: Int): IntArray {
        val result = IntArray(colorRes.size)
        for (i in colorRes.indices) {
            result[i] = getColor(context, colorRes[i])
        }
        return result
    }

   @ColorInt
    fun fetchColor(context: Context, @AttrRes attr: Int): Int {
        val resId = fetchColorResId(context, attr)
        return ContextCompat.getColor(context, resId)
        // return typedValue.data from typedValue
    }

    @ColorRes
    private fun fetchColorResId(context: Context, @AttrRes attr: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attr, typedValue, true)
        return typedValue.resourceId
    }

    /**
     * Fetch text color
     *
     * @param view view
     * @param attr color attribute like android.R.attr.background
     * @return color
     */
    @ColorInt
    fun fetchColorFromView(view: View, @AttrRes attr: Int): Int {
        return MaterialColors.getColor(view, attr)
    }

    /**
     * Fetch colors from style
     *
     * @param context activity or view's context
     * @param style style resource id
     * @param attrs color attribute like android.R.attr.background, android.R.attr.textColor
     * @return colors
     */
    @ColorInt
    fun fetchColorsFromStyle(context: Context, @StyleRes style: Int, @AttrRes attrs: IntArray): IntArray {
        val colors = IntArray(attrs.size)
        context.withStyledAttributes(style, attrs) {
            for (i in attrs.indices) {
                colors[i] = getColor(i, 0)
            }
        }
        return colors
    }

    // drawable

    fun getDrawable(context: Context, @DrawableRes resId: Int): Drawable {
        return AppCompatResources.getDrawable(context, resId)!!
    }

    fun tint(@ColorInt color: Int, vararg drawables: Drawable) {
        for (drawable in drawables) {
            tint(drawable, color)
        }
    }

    fun tint(drawable: Drawable, @ColorInt color: Int) {
        drawable.setTint(color)
        //DrawableCompat.setTint(drawable, color)
    }
}
