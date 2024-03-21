/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.speak

import android.content.Context
import android.util.AttributeSet
import androidx.preference.MultiSelectListPreference

open class MultiSelectListPreference : MultiSelectListPreference {
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context) : super(context)

    fun notifyEntriesChanged() {
        notifyChanged()
    }
}
