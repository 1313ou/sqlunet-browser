/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.wn

import android.content.Context
import android.text.Editable
import android.text.SpannableStringBuilder
import android.util.Log
import org.sqlunet.browser.config.Status
import org.sqlunet.browser.wn.lib.R

/**
 * Database _status
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal object WnStatus : Status() {

    private const val TAG = "Status"

    // status flags
    const val EXISTS_TS_WN = 0x100

    /**
     * Get status
     *
     * @param context context
     * @return _status
     */
    @JvmStatic
    fun status(context: Context): Int {
        if (existsDatabase(context)) {
            var status = EXISTS
            val existingTablesAndIndexes: List<String>? = try {
                tablesAndIndexes(context)
            } catch (e: Exception) {
                Log.e(TAG, "While getting _status", e)
                return status
            }
            val res = context.resources
            val requiredTables = res.getStringArray(R.array.required_tables)
            val requiredIndexes = res.getStringArray(R.array.required_indexes)
            val requiredTextsWn = res.getStringArray(R.array.required_texts_wn)
            val existsTables = contains(existingTablesAndIndexes, *requiredTables)
            val existsIdx = contains(existingTablesAndIndexes, *requiredIndexes)
            val existsTsWn = contains(existingTablesAndIndexes, *requiredTextsWn)
            if (existsTables) {
                status = status or EXISTS_TABLES
            }
            if (existsIdx) {
                status = status or EXISTS_INDEXES
            }
            if (existsTsWn) {
                status = status or EXISTS_TS_WN
            }
            return status
        }
        return 0
    }

    @JvmStatic
    fun toString(status: Int): CharSequence {
        val sb: Editable = SpannableStringBuilder()
        sb.append(Integer.toHexString(status))
        if (status and EXISTS != 0) {
            sb.append(" file")
        }
        if (status and EXISTS_TABLES != 0) {
            sb.append(" tables")
        }
        if (status and EXISTS_INDEXES != 0) {
            sb.append(" indexes")
        }
        if (status and EXISTS_TS_WN != 0) {
            sb.append(" tswn")
        }
        return sb
    }
}
