package com.bbou.concurrency.observe

import java.text.NumberFormat
import java.util.Locale

/**
 * Formatter of progress information
 */
object Formatter {

    private val UNITS = arrayOf("B", "KB", "MB", "GB")

    /**
     * Format byte count as string with information units
     *
     * @param count byte count, standard units starting from byte
     * @return string
     */
    private fun formatAsInformationString(count: Long): String {
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

    /**
     * Format byte count as string
     *
     * @param count byte count
     * @param unit unit, if null then information units are used
     * @return string
     */
    @JvmStatic
    fun formatAsString(count: Long, unit: String?): String {
        val strValue = NumberFormat.getNumberInstance(Locale.US).format(count)
        return if (unit == null) strValue else "$strValue $unit"
    }

    /**
     * Format byte count as fraction string
     *
     * @param progress byte count
     * @param total total byte count
     * @param unit unit, if null then information units are used
     * @return string
     */
    @JvmStatic
    fun formatAsString(progress: Long, total: Long, unit: String?): String {
        var str = if (unit != null) formatAsString(progress, unit) else formatAsInformationString(progress)
        if (total != -1L) {
            val strLength = if (unit != null) formatAsString(total, unit) else formatAsInformationString(total)
            str += " / $strLength"
        }
        return str
    }
}
