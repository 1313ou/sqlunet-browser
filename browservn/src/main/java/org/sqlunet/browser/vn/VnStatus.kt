/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.vn

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
internal object VnStatus : Status() {

    private const val TAG = "Status"

    // status flags
    const val EXISTS_TS_VN = 0x1000
    const val EXISTS_TS_PB = 0x2000

    /**
     * Get _status
     *
     * @param context context
     * @return _status
     */
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
            val requiredTextsVn = res.getStringArray(R.array.required_texts_vn)
            val requiredTextsPb = res.getStringArray(R.array.required_texts_pb)
            val existsTables = contains(existingTablesAndIndexes, *requiredTables)
            val existsIdx = contains(existingTablesAndIndexes, *requiredIndexes)
            val existsTsVn = contains(existingTablesAndIndexes, *requiredTextsVn)
            val existsTsPb = contains(existingTablesAndIndexes, *requiredTextsPb)
            if (existsTables) {
                status = status or EXISTS_TABLES
            }
            if (existsIdx) {
                status = status or EXISTS_INDEXES
            }
            if (existsTsVn) {
                status = status or EXISTS_TS_VN
            }
            if (existsTsPb) {
                status = status or EXISTS_TS_PB
            }
            return status
        }
        return 0
    }

    @Suppress("unused")
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
        if (status and EXISTS_TS_VN != 0) {
            sb.append(" tsvn")
        }
        if (status and EXISTS_TS_PB != 0) {
            sb.append(" tspb")
        }
        return sb
    }
}
