/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.use

/**
 * Color utils
 *
 * @author Bernard Bou
 */
object ColorUtils {

    // colors

    /**
     * Get color
     *
     * @param context context
     * @param resId resource id
     * @return color int
     */
    @JvmStatic
    @ColorInt
    fun getColor(context: Context, @ColorRes resId: Int): Int {
        return ContextCompat.getColor(context, resId)
    }

    /**
     * Get color
     *
     * @param context context
     * @param resIds resource ids
     * @return color int array
     */
    @JvmStatic
    fun getColors(context: Context, @ColorRes vararg resIds: Int): IntArray {
        return IntArray(resIds.size) {
            getColor(context, resIds[it])
        }
    }

    /**
     * Add transparency
     * @receiver color int
     * @param alpha 0.0 to 1.0
     */
    fun Int.withAlpha(alpha: Float): Int {
        val alphaInt = (alpha.coerceIn(0f, 1f) * 255).toInt()
        return (this and 0x00FFFFFF) or (alphaInt shl 24)
    }

    // theme color attr

    /**
     * Fetch colors resources
     *
     * @param context context
     * @param colorAttr color attribute
     * @return color int
     */
    @JvmStatic
    @ColorInt
    fun fetchColor(context: Context, @AttrRes colorAttr: Int): Int {
        return fetchColorNullable(context, colorAttr) ?: Color.TRANSPARENT
    }

    /**
     * Fetch colors resources
     *
     * @param context context
     * @param colorAttr color attribute
     * @return color int
     */
    @JvmStatic
    @ColorInt
    fun fetchColorNullable(context: Context, @AttrRes colorAttr: Int): Int? {
        val typedValue = TypedValue()
        val wasResolved = context.theme.resolveAttribute(colorAttr, typedValue, true)
        return if (wasResolved) {
            if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT &&
                typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT
            ) {
                // It's a raw hex value like #FF0000 defined directly in the theme
                typedValue.data
            } else {
                // It's a reference to a color resource (@color/...)
                // Must use ContextCompat or Resources to get the actual hex
                getColor(context, typedValue.resourceId)
            }
        } else null
    }

    /**
     * Fetch colors resources from theme
     *
     * @param context context
     * @param colorAttrs attributes
     * Expects an array of Attribute IDs (@AttrRes attrs)
     * Even though passing a dynamic array of @AttrRes (the IDs), the compiler treats that array as a "temporary styleable."
     * @return array of int resources, with 0 value if not found
     */
    @JvmStatic
    @ColorInt
    fun fetchColors(
        context: Context,
        vararg colorAttrs: /* @AttrRes */ Int
    ): IntArray {
        context.obtainStyledAttributes(colorAttrs).use { typedArray ->
            return IntArray(colorAttrs.size) { i ->
                typedArray.getColor(i, 0)
            }
        }
    }

    /**
     * Fetch colors resources from theme
     *
     * @param context context
     * @param colorAttrs attributes
     * Expects an array of Attribute IDs (@AttrRes attrs)
     * Even though passing a dynamic array of @AttrRes (the IDs), the compiler treats that array as a "temporary styleable."
     * @return array of Integer resources, with null value if not found
     */
    @JvmStatic
    @ColorInt
    fun fetchColorsNullable(
        context: Context,
        vararg colorAttrs: /* @AttrRes */ Int
    ): Array<Int?> {
        context.obtainStyledAttributes(colorAttrs).use {
            return Array(colorAttrs.size) { i ->
                if (it.hasValue(i)) it.getColor(i, 0) else null
            }
        }
    }

    /**
     * Fetch colors resources from style
     *
     * @param context context
     * @param styleId style id (e.g.: R.style.actionBarTheme)
     * @param colorAttrs color attributes
     * Expects an array of Attribute IDs (@AttrRes attrs)
     * Even though passing a dynamic array of @AttrRes (the IDs), the compiler treats that array as a "temporary styleable."
     * @return array of int resources, with 0 value if not found
     */
    @JvmStatic
    @ColorInt
    fun fetchColorsFromStyle(
        context: Context,
        @StyleRes styleId: Int,
        vararg colorAttrs: /* @AttrRes */ Int
    ): IntArray {
        context.obtainStyledAttributes(styleId, colorAttrs).use { typedArray ->
            return IntArray(colorAttrs.size) { i ->
                typedArray.getColor(i, 0)
            }
        }
    }

    /**
     * Fetch colors resources from style
     *
     * @param context context
     * @param styleId style id (e.g.: R.style.actionBarTheme)
     * @param colorAttrs color attributes
     * Expects an array of Attribute IDs (@AttrRes attrs)
     * Even though passing a dynamic array of @AttrRes (the IDs), the compiler treats that array as a "temporary styleable."
     * @return array of Integer resources, with null value if not found
     */
    @JvmStatic
    @ColorInt
    fun fetchColorsNullableFromStyle(
        context: Context,
        @StyleRes styleId: Int,
        vararg colorAttrs: /* @AttrRes */ Int
    ): Array<Int?> {
        context.obtainStyledAttributes(styleId, colorAttrs).use {
            return Array(colorAttrs.size) { i ->
                if (it.hasValue(i)) it.getColor(i, 0) else null
            }
        }
    }

    /**
     * Get style from theme
     * Resolve the style attribute to the style it points to in the current Theme.
     * @param context context
     * @param styleAttr style attribute id (e.g.: R.attr.actionBarTheme)
     * @return a style reference
     */
    @StyleRes
    fun getStyleFromTheme(
        context: Context,
        @AttrRes styleAttr: Int // We search for an Attribute key
    ): Int {
        val typedValue = TypedValue()
        val found = context.theme.resolveAttribute(styleAttr, typedValue, true)
        return if (found) typedValue.resourceId else 0
    }

    /**
     * Fetch colors resources from style
     *
     * @param context context
     * @param styleAttr style attribute id (e.g.: R.attr.actionBarTheme)
     * @param colorAttrs color attributes
     * Expects an array of Attribute IDs (@AttrRes attrs)
     * Even though passing a dynamic array of @AttrRes (the IDs), the compiler treats that array as a "temporary styleable."
     * @return array of Integer resources, with null value if not found
     */
    @JvmStatic
    @ColorInt
    fun fetchColorsFromStyleInThemeNullable(
        context: Context,
        @AttrRes styleAttr: Int,
        vararg colorAttrs: /* @AttrRes */ Int
    ): Array<Int?> {

        // res id of style pointed to from style attr (e.g. actionBarStyle)
        val styleId = getStyleFromTheme(context, styleAttr)

        // get style values (e.g. textColor, or background)
        return fetchColorsNullableFromStyle(context, styleId, *colorAttrs)
    }

    /**
     * Fetch colors resources from style
     *
     * @param context context
     * @param styleAttr style attribute id (e.g.: R.attr.actionBarTheme)
     * @param colorAttr color attribute
     * @return array of Integer resources, with null value if not found
     */
    @JvmStatic
    @ColorInt
    fun fetchColorFromStyleInTheme(
        context: Context,
        @AttrRes styleAttr: Int,
        @AttrRes colorAttr: Int
    ): Int? = fetchColorsFromStyleInThemeNullable(context, styleAttr, colorAttr)[0]

    // drawable

    fun getDrawable(context: Context, @DrawableRes resId: Int): Drawable {
        return AppCompatResources.getDrawable(context, resId)!!
    }

    // tint

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
