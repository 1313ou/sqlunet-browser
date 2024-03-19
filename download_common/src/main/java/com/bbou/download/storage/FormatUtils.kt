/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download.storage

import java.util.Locale

/**
 * Storage display Format
 */
object FormatUtils {

    /**
     * Storage types
     */
    enum class StorageType {
        /**
         * Primary emulated
         */
        PRIMARY_EMULATED,

        /**
         * Primary physical
         */
        PRIMARY_PHYSICAL,

        /**
         * Secondary
         */
        SECONDARY
    }

    /**
     * Storage units
     */
    private val UNITS = arrayOf("B", "KB", "MB", "GB")

    /**
     * Format byte count as string with information/storage units
     *
     * @param count byte count, standard units starting from byte
     * @return string
     */
    @JvmStatic
    fun formatAsInformationString(count: Long): String {
        if (count > 0) {
            var unit = 1024f * 1024f * 1024f
            for (i in 3 downTo 0) {
                if (count >= unit) {
                    return String.format(Locale.ENGLISH, "%.1f %s", count / unit, UNITS[i])
                }
                unit /= 1024f
            }
        } else if (count == 0L) {
            return "0 Byte"
        }
        return "[n/a]"
    }
}
