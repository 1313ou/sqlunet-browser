/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.TypedArray
import android.text.TextUtils
import android.util.AttributeSet
import androidx.core.content.res.use
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference

import org.sqlunet.browser.common.R

/**
 * Aggregate preference (aggregate bits to long)
 *
 * @author Bernard Bou
 */
class BitAggregatePreference : CheckBoxPreference {

    /**
     * Aggregate key
     */
    private var aggregateKey: String? = null

    /**
     * Shift value in long
     */
    private var shift = 0

    /**
     * Whether aggregate value is persisted
     */
    private var aggregatePersistent = false

    /**
     * Constructor
     *
     * @param context  context
     * @param attrs    attributes
     * @param defStyle def style
     */
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        // attributes
        init(context, attrs)

        isPersistent = false
    }

    /**
     * Constructor
     *
     * @param context context
     * @param attrs   attributes
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        // attributes
        init(context, attrs)

        isPersistent = false
    }

    /**
     * Initialize
     *
     * @param context context
     * @param attrs   attributes
     */
    private fun init(context: Context, attrs: AttributeSet) {
        // preference attributes
        context.obtainStyledAttributes(attrs, R.styleable.BitAggregatePreference).use {
            aggregateKey = it.getString(R.styleable.BitAggregatePreference_aggregateKey)
            shift = it.getInt(R.styleable.BitAggregatePreference_index, -1)
            aggregatePersistent = it.getBoolean(R.styleable.BitAggregatePreference_persist, true)
        }
    }

    /**
     * Inherit value from parent key in layout (typically a group key)
     *
     * @param parentAggregateKey parent key
     * @return true if value was inherited
     */
    fun inheritAggregateKey(parentAggregateKey: String?): Boolean {
        if (parentAggregateKey == null) {
            return false
        }
        aggregateKey = parentAggregateKey
        return true
    }

    fun getShift(): Long {
        return shift.toLong()
    }

    override fun onGetDefaultValue(array: TypedArray, index: Int): Any {
        return array.getBoolean(index, false)
    }

    public override fun onSetInitialValue(defaultValue: Any?) {
        val value = defaultValue as Boolean
        isChecked = value
    }

    //  set value
    override fun setChecked(checked: Boolean) {
        // old value
        val oldValue = isChecked

        // super
        super.setChecked(checked)

        // persist if changed
        val changed = oldValue != checked
        if (changed) {
            persistBit(checked)
            notifyChanged()
        }
    }

    // P E R S I S T

    /**
     * Should persist aggregate value
     *
     * @return true if aggregate value should be persisted
     */
    private fun shouldPersistAggregate(): Boolean {
        return preferenceManager != null && aggregatePersistent && !TextUtils.isEmpty(aggregateKey)
    }

    /**
     * Get persisted bit
     *
     * @param defaultReturnValue default return value
     * @return persisted bit or default if none
     */
    private fun getPersistedBit(defaultReturnValue: Boolean): Boolean {
        if (!shouldPersistAggregate()) {
            return defaultReturnValue
        }

        val sharedPrefs = checkNotNull(preferenceManager.sharedPreferences)
        val aggregateValue = sharedPrefs.getLong(aggregateKey, NULL_AGGREGATE_VALUE)
        if (aggregateValue == NULL_AGGREGATE_VALUE) {
            return defaultReturnValue
        }
        return (aggregateValue and (1L shl shift)) != 0L
    }

    /**
     * Persist bit
     *
     * @param value value
     * @return true if already persisted
     */
    private fun persistBit(value: Boolean): Boolean {
        if (shouldPersistAggregate()) {
            if (value == getPersistedBit(!value)) // it's already there, so the same as persisting
            {
                return true
            }

            val sharedPreferences = checkNotNull(preferenceManager.sharedPreferences)
            val editor = sharedPreferences.edit()

            var aggregateValue = sharedPreferences.getLong(aggregateKey, 0)
            aggregateValue = if (value) {
                aggregateValue or (1L shl shift)
            } else {
                aggregateValue and (1L shl shift).inv()
            }

            editor.putLong(aggregateKey, aggregateValue)
            tryCommit(editor)
            return true
        }
        return false
    }

    companion object {

        /**
         * Long value to mean that aggregate value is null
         */
        private const val NULL_AGGREGATE_VALUE = -1L

        /**
         * Try to commit
         *
         * @param editor editor editor
         */
        @SuppressLint("CommitPrefEdits", "ApplySharedPref")
        private fun tryCommit(editor: SharedPreferences.Editor) {
            try {
                editor.apply()
            } catch (ignored: AbstractMethodError) {
                // The app injected its own pre-Gingerbread SharedPreferences.Editor implementation without an apply method.
                editor.commit()
            }
        }

        /**
         * Apply aggregate value to sub preferences
         *
         * @param filter      aggregate value
         * @param preferences sub preferences (BitAggregatePreference)
         */
        @JvmStatic
        fun apply(filter: Long, preferences: Array<Preference>) {
            // inherit from category preference
            for (preference in preferences) {
                val bitAggregatePreference = preference as BitAggregatePreference
                val value = (filter and (1L shl bitAggregatePreference.getShift().toInt())) != 0L
                bitAggregatePreference.onSetInitialValue(value)
            }
        }
    }
}
