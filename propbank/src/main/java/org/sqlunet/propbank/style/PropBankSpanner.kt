/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.style

import android.content.Context
import android.text.style.ImageSpan
import org.sqlunet.propbank.R
import org.sqlunet.style.RegExprSpanner
import org.sqlunet.style.Spanner.SpanFactory

/**
 * Spanner for PropBank
 *
 * @param context context
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class PropBankSpanner(context: Context) : RegExprSpanner(
    patterns,
    arrayOf(
        arrayOf(
            SpanFactory { _: Long -> ImageSpan(context, R.drawable.trace) },
            SpanFactory { _: Long -> HiddenSpan() })
    )
) {

    companion object {

        /**
         * Patterns
         */
        private val patterns = arrayOf("((?:\\[\\*\\]|\\*trace\\*)(\\-?\\d*))")
    }
}
