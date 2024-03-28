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
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.preference.DialogPreference
import androidx.preference.Preference
import androidx.preference.Preference.SummaryProvider
import androidx.preference.PreferenceDialogFragmentCompat
import androidx.preference.PreferenceFragmentCompat
import java.util.Arrays

/**
 * OpenEditTextPreference
 *
 * @author Bernard Bou
 */
class OpenEditTextPreference : DialogPreference {

    // V A L U E

    /**
     * Possible values
     */
    lateinit var values: Array<CharSequence>

    /**
     * Possible entry enable
     */
    private lateinit var enable: BooleanArray

    /**
     * Value
     */
    private var value: String? = null

    // S E T T I N G S

    /**
     * Possible labels
     */
    lateinit var labels: Array<CharSequence>

    /**
     * Extra values
     */
    private var xValues: List<String>? = null

    /**
     * Labels for extra values
     */
    private var xLabels: List<String>? = null

    // C O N S T R U C T O R

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

        // attributes
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
        context.obtainStyledAttributes(attrs, R.styleable.OpenEditTextPreference).use {
            values = it.getTextArray(R.styleable.OpenEditTextPreference_values)
            labels = it.getTextArray(R.styleable.OpenEditTextPreference_labels)
        }

        // enable all
        enable = BooleanArray(values.size)
        Arrays.fill(enable, true)
    }

    /**
     * Set up
     */
    private fun setup() {
        dialogLayoutResource = R.layout.dialog_openedittext_pref
        setPositiveButtonText(android.R.string.ok)
        setNegativeButtonText(android.R.string.cancel)
        dialogIcon = null
    }

    /**
     * Enable Setter
     *
     * @param enables enable flags
     */
    fun setEnables(enables: BooleanArray) {
        enable = enables
    }

    /**
     * Add options
     *
     * @param xValues extra values
     * @param xLabels extra labels
     */
    fun addOptions(xValues: List<String>?, xLabels: List<String>?) {
        this.xValues = xValues
        this.xLabels = xLabels
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
    class OpenEditTextPreferenceDialogFragmentCompat : PreferenceDialogFragmentCompat() {

        private var editView: EditText? = null
        private var optionsView: RadioGroup? = null

        override fun onBindDialogView(view: View) {
            super.onBindDialogView(view)

            // pref
            val pref = getPreference() as OpenEditTextPreference

            // edit text
            editView = view.findViewById(R.id.openedit_text)
            editView!!.requestFocus()

            // populate with value
            if (pref.value != null) {
                editView!!.setText(pref.value)
                // place cursor at the end
                editView!!.setSelection(pref.value!!.length)
            }

            // options
            optionsView = view.findViewById(R.id.openedit_options)

            // populate
            optionsView!!.removeAllViews()
            var i = 0
            while (i < pref.values.size && i < pref.labels.size && i < pref.enable.size) {
                val value = pref.values[i]
                val label = pref.labels[i]
                val enable = pref.enable[i]
                val radioButton = RadioButton(requireContext())
                radioButton.text = label
                radioButton.tag = value
                radioButton.setEnabled(enable)
                optionsView!!.addView(radioButton)
                i++
            }
            if (pref.xValues != null && pref.xLabels != null) {
                var j = 0
                while (j < pref.xValues!!.size && j < pref.xLabels!!.size) {
                    val value: CharSequence = pref.xValues!![j]
                    val label: CharSequence = pref.xLabels!![j]
                    val enable = true
                    val radioButton = RadioButton(requireContext())
                    radioButton.text = label
                    radioButton.tag = value
                    radioButton.setEnabled(enable)
                    optionsView!!.addView(radioButton)
                    j++
                }
            }

            // check listener
            optionsView!!.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
                if (checkedId == -1) {
                    editView!!.setText("")
                } else {
                    val radioButton = optionsView!!.findViewById<RadioButton>(checkedId)
                    val tag = radioButton.tag.toString()
                    editView!!.setText(tag)
                    editView!!.setSelection(tag.length)
                }
            }
        }

        override fun onDialogClosed(positiveResult: Boolean) {
            // when the user selects "OK", persist the new value
            if (positiveResult) {
                val editable = editView!!.getText()
                val pref = getPreference() as OpenEditTextPreference
                val newValue = editable?.toString()
                if (pref.callChangeListener(newValue)) {
                    pref.setValue(newValue)
                }
            }
        }

        companion object {
            fun newInstance(pref: OpenEditTextPreference): OpenEditTextPreferenceDialogFragmentCompat {
                val fragment = OpenEditTextPreferenceDialogFragmentCompat()
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
        private const val DIALOG_FRAGMENT_TAG = "OpenEditTextPreference"

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
            if (preference is OpenEditTextPreference) {
                val dialogFragment: DialogFragment = OpenEditTextPreferenceDialogFragmentCompat.newInstance(preference)
                dialogFragment.setTargetFragment(prefFragment, 0)
                dialogFragment.show(manager, DIALOG_FRAGMENT_TAG)
                return true
            }
            return false
        }

        /**
         * Summary provider
         */
        val SUMMARY_PROVIDER = SummaryProvider { preference: OpenEditTextPreference ->
            val context = preference.context
            val value = preference.getPersistedString(null)
            value ?: context.getString(R.string.pref_value_default)
        }
    }
}
