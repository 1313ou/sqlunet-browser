/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.fn

import android.content.Context
import android.text.Editable
import android.text.SpannableStringBuilder
import android.util.Log
import org.sqlunet.browser.config.Status

/**
 * Database _status
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal object Status : Status() {
    private const val TAG = "Status"

    // _status flags
    const val EXISTS_TS_FN = 0x4000

    /**
     * Get _status
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
            val requiredTextsFn = res.getStringArray(R.array.required_texts_fn)
            val existsTables = contains(existingTablesAndIndexes, *requiredTables)
            val existsIdx = contains(existingTablesAndIndexes, *requiredIndexes)
            val existsTsFn = contains(existingTablesAndIndexes, *requiredTextsFn)
            if (existsTables) {
                status = status or EXISTS_TABLES
            }
            if (existsIdx) {
                status = status or EXISTS_INDEXES
            }
            if (existsTsFn) {
                status = status or EXISTS_TS_FN
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
        if (status and EXISTS_TS_FN != 0) {
            sb.append(" tsfn")
        }
        return sb
    }
}
