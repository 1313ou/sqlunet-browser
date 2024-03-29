/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.preference

import android.content.Context
import android.content.res.TypedArray
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.preference.DialogPreference
import androidx.preference.Preference
import androidx.preference.Preference.SummaryProvider
import androidx.preference.PreferenceDialogFragmentCompat
import androidx.preference.PreferenceFragmentCompat

/**
 * AutoEditTextPreference
 *
 * @author Bernard Bou
 */
class AutoEditTextPreference : DialogPreference {

    // V A L U E

    /**
     * Value
     */
    private var value: String? = null

    /**
     * Possible values
     */
    lateinit var values: Array<CharSequence>

    // C O N S T R U C T O R S

    /**
     * Constructor
     *
     * @param context  context
     * @param attrs    attributes
     * @param defStyle def style
     */
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {

        // init attributes
        init(context, attrs)

        // set up
        setup()
    }

    /**
     * Constructor
     *
     * @param context context
     * @param attrs   attributes
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        // init attributes
        init(context, attrs)

        // set up
        setup()
    }

    /**
     * Initialize
     *
     * @param context context
     * @param attrs   attributes
     */
    private fun init(context: Context, attrs: AttributeSet) {
        // obtain values through styled attributes
        context.obtainStyledAttributes(attrs, R.styleable.AutoEditTextPreference).use {
            values = it.getTextArray(R.styleable.AutoEditTextPreference_values)
        }
    }

    /**
     * Set up
     */
    private fun setup() {
        dialogLayoutResource = R.layout.dialog_autoedittext_pref
        setPositiveButtonText(android.R.string.ok)
        setNegativeButtonText(android.R.string.cancel)
        dialogIcon = null
    }

    // V A L U E

    override fun onGetDefaultValue(array: TypedArray, index: Int): Any? {
        return array.getString(index)
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        setValue(getPersistedString(defaultValue as String?))
    }

    private fun setValue(newValue: String?) {
        value = newValue
        persistString(newValue)
        notifyChanged()
    }

    // D I A L O G  F R A G M E N T

    class AutoEditTextPreferenceDialogFragmentCompat : PreferenceDialogFragmentCompat() {

        private var editView: AutoCompleteTextView? = null
        override fun onBindDialogView(view: View) {
            super.onBindDialogView(view)

            // pref
            val pref = getPreference() as AutoEditTextPreference

            // edit text
            editView = view.findViewById(R.id.autoedit_text)
            editView!!.requestFocus()

            // populate with value
            if (pref.value != null) {
                editView!!.setText(pref.value)
                // place cursor at the end
                editView!!.setSelection(pref.value!!.length)
            }

            // populate with possible values
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, pref.values)
            editView!!.setAdapter(adapter)
        }

        override fun onDialogClosed(positiveResult: Boolean) {
            // when the user selects "OK", persist the new value
            if (positiveResult) {
                val editable = editView!!.getText()
                val pref = getPreference() as AutoEditTextPreference
                val newValue = editable?.toString()
                if (pref.callChangeListener(newValue)) {
                    pref.setValue(newValue)
                }
            }
        }

        companion object {

            fun newInstance(pref: AutoEditTextPreference): AutoEditTextPreferenceDialogFragmentCompat {
                val fragment = AutoEditTextPreferenceDialogFragmentCompat()
                val args = Bundle()
                args.putString(ARG_KEY, pref.key)
                fragment.setArguments(args)
                return fragment
            }
        }
    }

    // S T A T E

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()

        // check whether this Preference is persistent (continually saved)
        if (isPersistent) // no need to save instance state since it's persistent, use superclass state
        {
            return superState
        }

        // create instance of custom BaseSavedState
        val state = StringSavedState(superState)

        // set the state's value with the class member that holds current setting value
        state.value = value
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        // check whether we saved the state in onSaveInstanceState
        if (state == null || state.javaClass != StringSavedState::class.java) {
            // didn't save the state, so call superclass
            super.onRestoreInstanceState(state)
            return
        }

        // cast state to custom BaseSavedState and pass to superclass
        val savedState = state as StringSavedState
        super.onRestoreInstanceState(savedState.superState)

        // set this preference's widget to reflect the restored state
        setValue(savedState.value)
    }

    companion object {

        private const val DIALOG_FRAGMENT_TAG = "AutoEditTextPreference"

        /**
         * onDisplayPreferenceDialog helper
         *
         * @param prefFragment preference fragment
         * @param preference   preference
         * @return false if not handled: call super.onDisplayPreferenceDialog(preference)
         */
        fun onDisplayPreferenceDialog(prefFragment: PreferenceFragmentCompat, preference: Preference?): Boolean {
            val manager: FragmentManager = try {
                prefFragment.getParentFragmentManager()
            } catch (e: IllegalStateException) {
                return false
            }
            if (manager.findFragmentByTag(DIALOG_FRAGMENT_TAG) != null) {
                return true
            }
            if (preference is AutoEditTextPreference) {
                val dialogFragment: DialogFragment = AutoEditTextPreferenceDialogFragmentCompat.newInstance(preference)
                dialogFragment.setTargetFragment(prefFragment, 0)
                dialogFragment.show(manager, DIALOG_FRAGMENT_TAG)
                return true
            }
            return false
        }

        /**
         * Summary provider
         */
        val SUMMARY_PROVIDER = SummaryProvider { preference: AutoEditTextPreference ->
            val context = preference.context
            val value = preference.getPersistedString(null)
            value ?: context.getString(R.string.pref_value_default)
        }
    }
}
