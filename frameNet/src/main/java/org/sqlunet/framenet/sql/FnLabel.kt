/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.sql

/**
 * Label
 *
 * @param from    from-indexOf
 * @param to      to-indexOf
 * @param label   label
 * @param iType   i-type
 * @param bgColor background color
 * @param fgColor foreground color
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class FnLabel(
    @JvmField val from: String,
    @JvmField val to: String,
    @JvmField val label: String,
    @JvmField val iType: String,
    bgColor: String?, fgColor: String?,
) {

    /**
     * Background color
     */
    val bgColor: String?

    /**
     * Foreground color
     */
    val fgColor: String?

    /**
     * Constructor
     */
    init {
        this.bgColor = if (bgColor != null && bgColor.isEmpty()) null else bgColor
        this.fgColor = if (fgColor != null && fgColor.isEmpty()) null else fgColor
    }

    override fun toString(): String {
        return "$label[$from,$to] type=$iType"
    }
}
