/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.xn

import android.content.Context
import android.text.Editable
import android.text.SpannableStringBuilder
import android.util.Log
import org.sqlunet.browser.R
import org.sqlunet.browser.config.Status

/**
 * Database _status
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal object Status : Status() {
    private const val TAG = "Status"

    // _status flags
    const val EXISTS_PREDICATEMATRIX = 0x20
    const val EXISTS_TS_WN = 0x100
    const val EXISTS_TS_VN = 0x1000
    const val EXISTS_TS_PB = 0x2000
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
            val res = context.resources
            val requiredTables = res.getStringArray(R.array.required_tables)
            val requiredIndexes = res.getStringArray(R.array.required_indexes)
            val requiredTextsPm = res.getStringArray(R.array.required_pm)
            val requiredTextsWn = res.getStringArray(R.array.required_texts_wn)
            val requiredTextsVn = res.getStringArray(R.array.required_texts_vn)
            val requiredTextsPb = res.getStringArray(R.array.required_texts_pb)
            val requiredTextsFn = res.getStringArray(R.array.required_texts_fn)
            val existingTablesAndIndexes: List<String>? = try {
                tablesAndIndexes(context)
            } catch (e: Exception) {
                Log.e(TAG, "While getting _status", e)
                return status
            }
            val existsTables = contains(existingTablesAndIndexes, *requiredTables)
            val existsIdx = contains(existingTablesAndIndexes, *requiredIndexes)
            val existsPm = contains(existingTablesAndIndexes, *requiredTextsPm)
            val existsTsWn = contains(existingTablesAndIndexes, *requiredTextsWn)
            val existsTsVn = contains(existingTablesAndIndexes, *requiredTextsVn)
            val existsTsPb = contains(existingTablesAndIndexes, *requiredTextsPb)
            val existsTsFn = contains(existingTablesAndIndexes, *requiredTextsFn)
            if (existsTables) {
                status = status or EXISTS_TABLES
            }
            if (existsIdx) {
                status = status or EXISTS_INDEXES
            }
            if (existsPm) {
                status = status or EXISTS_PREDICATEMATRIX
            }
            if (existsTsWn) {
                status = status or EXISTS_TS_WN
            }
            if (existsTsVn) {
                status = status or EXISTS_TS_VN
            }
            if (existsTsPb) {
                status = status or EXISTS_TS_PB
            }
            if (existsTsFn) {
                status = status or EXISTS_TS_FN
            }
            return status
        }
        return 0
    }

    /**
     * Can run _status
     *
     * @param context context
     * @return true if app is ready to run
     */
    fun canRun(context: Context): Boolean {
        val status = status(context)
        return status and (EXISTS or EXISTS_TABLES or EXISTS_INDEXES or EXISTS_PREDICATEMATRIX) == EXISTS or EXISTS_TABLES or EXISTS_INDEXES or EXISTS_PREDICATEMATRIX
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
        if (status and EXISTS_PREDICATEMATRIX != 0) {
            sb.append(" pm")
        }
        if (status and EXISTS_TS_WN != 0) {
            sb.append(" tswn")
        }
        if (status and EXISTS_TS_VN != 0) {
            sb.append(" tsvn")
        }
        if (status and EXISTS_TS_PB != 0) {
            sb.append(" tspb")
        }
        if (status and EXISTS_TS_FN != 0) {
            sb.append(" tsfn")
        }
        return sb
    }
}