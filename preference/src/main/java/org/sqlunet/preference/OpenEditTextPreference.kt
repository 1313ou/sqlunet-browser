/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceDialogFragmentCompat;
import androidx.preference.PreferenceFragmentCompat;

/**
 * OpenEditTextPreference
 *
 * @author Bernard Bou
 */
public class OpenEditTextPreference extends DialogPreference
{
	// V A L U E

	/**
	 * Possible values
	 */
	private CharSequence[] values;

	/**
	 * Possible entry enable
	 */
	private boolean[] enable;

	/**
	 * Value
	 */
	@Nullable
	private String value;

	// S E T T I N G S

	/**
	 * Possible labels
	 */
	private CharSequence[] labels;

	/**
	 * Extra values
	 */
	private List<String> xValues;

	/**
	 * Labels for extra values
	 */
	private List<String> xLabels;

	// C O N S T R U C T O R

	/**
	 * Constructor
	 *
	 * @param context  context
	 * @param attrs    attributes
	 * @param defStyle def style
	 */
	public OpenEditTextPreference(@NonNull final Context context, @NonNull final AttributeSet attrs, final int defStyle)
	{
		super(context, attrs, defStyle);

		// attributes
		init(context, attrs);

		// set up
		setup();
	}

	/**
	 * Constructor
	 *
	 * @param context context
	 * @param attrs   attributes
	 */
	public OpenEditTextPreference(@NonNull final Context context, @NonNull final AttributeSet attrs)
	{
		super(context, attrs);

		// attributes
		init(context, attrs);

		// set up
		setup();
	}

	/**
	 * Initialize
	 *
	 * @param context context
	 * @param attrs   attributes
	 */
	private void init(@NonNull final Context context, @NonNull final AttributeSet attrs)
	{
		// obtain values through styled attributes
		final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.OpenEditTextPreference);
		this.values = array.getTextArray(R.styleable.OpenEditTextPreference_values);
		this.labels = array.getTextArray(R.styleable.OpenEditTextPreference_labels);
		array.recycle();

		// ensure not null
		if (this.values == null)
		{
			this.values = new CharSequence[0];
		}
		if (this.labels == null)
		{
			this.labels = new CharSequence[0];
		}

		// enable all
		this.enable = new boolean[this.values.length];
		Arrays.fill(this.enable, true);
	}

	/**
	 * Set up
	 */
	private void setup()
	{
		setDialogLayoutResource(R.layout.dialog_openedittext_pref);
		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(android.R.string.cancel);
		setDialogIcon(null);
	}

	/**
	 * Values Getter
	 *
	 * @return values
	 */
	public CharSequence[] getValues()
	{
		return this.values;
	}

	/**
	 * Values Setter
	 *
	 * @param values values
	 */
	public void setValues(CharSequence[] values)
	{
		this.values = values;
	}

	/**
	 * Labels Getter
	 *
	 * @return labels
	 */
	public CharSequence[] getLabels()
	{
		return this.labels;
	}

	/**
	 * Labels Setter
	 *
	 * @param labels labels
	 */
	public void setLabels(CharSequence[] labels)
	{
		this.values = labels;
	}

	/**
	 * Enable Setter
	 *
	 * @param enables enable flags
	 */
	public void setEnables(boolean[] enables)
	{
		this.enable = enables;
	}

	/**
	 * Add options
	 *
	 * @param xValues extra values
	 * @param xLabels extra labels
	 */
	public void addOptions(final List<String> xValues, final List<String> xLabels)
	{
		this.xValues = xValues;
		this.xLabels = xLabels;
	}

	// V A L U E

	@Nullable
	@Override
	protected Object onGetDefaultValue(@NonNull final TypedArray array, final int index)
	{
		return array.getString(index);
	}

	@Override
	protected void onSetInitialValue(final Object defaultValue)
	{
		setValue(getPersistedString((String) defaultValue));
	}

	private void setValue(@Nullable final String newValue)
	{
		this.value = newValue;
		persistString(newValue);
		notifyChanged();
	}

	// D I A L O G  F R A G M E N T

	static public class OpenEditTextPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat
	{
		@NonNull
		@SuppressWarnings("WeakerAccess")
		static public OpenEditTextPreferenceDialogFragmentCompat newInstance(@NonNull final OpenEditTextPreference pref)
		{
			final OpenEditTextPreferenceDialogFragmentCompat fragment = new OpenEditTextPreferenceDialogFragmentCompat();
			final Bundle args = new Bundle();
			args.putString(ARG_KEY, pref.getKey());
			fragment.setArguments(args);
			return fragment;
		}

		private EditText editView;

		private RadioGroup optionsView;

		@Override
		protected void onBindDialogView(@NonNull View view)
		{
			super.onBindDialogView(view);

			// pref
			final OpenEditTextPreference pref = (OpenEditTextPreference) getPreference();

			// edit text
			editView = view.findViewById(R.id.openedit_text);
			assert editView != null;
			editView.requestFocus();

			// populate with value
			if (pref.value != null)
			{
				editView.setText(pref.value);
				// place cursor at the end
				editView.setSelection(pref.value.length());
			}

			// options
			optionsView = view.findViewById(R.id.openedit_options);
			assert optionsView != null;

			// populate
			optionsView.removeAllViews();
			for (int i = 0; i < pref.values.length && i < pref.labels.length && i < pref.enable.length; i++)
			{
				final CharSequence value = pref.values[i];
				final CharSequence label = pref.labels[i];
				final boolean enable = pref.enable[i];

				final RadioButton radioButton = new RadioButton(requireContext());
				radioButton.setText(label);
				radioButton.setTag(value);
				radioButton.setEnabled(enable);
				optionsView.addView(radioButton);
			}
			if (pref.xValues != null && pref.xLabels != null)
			{
				for (int i = 0; i < pref.xValues.size() && i < pref.xLabels.size(); i++)
				{
					final CharSequence value = pref.xValues.get(i);
					final CharSequence label = pref.xLabels.get(i);
					final boolean enable = true;

					final RadioButton radioButton = new RadioButton(requireContext());
					radioButton.setText(label);
					radioButton.setTag(value);
					radioButton.setEnabled(enable);
					optionsView.addView(radioButton);
				}
			}

			// check listener
			optionsView.setOnCheckedChangeListener((group, checkedId) -> {
				if (checkedId == -1)
				{
					editView.setText("");
				}
				else
				{
					final RadioButton radioButton = optionsView.findViewById(checkedId);
					final String tag = radioButton.getTag().toString();
					editView.setText(tag);
					editView.setSelection(tag.length());
				}
			});
		}

		@Override
		public void onDialogClosed(final boolean positiveResult)
		{
			// when the user selects "OK", persist the new value
			if (positiveResult)
			{
				final Editable editable = editView.getText();
				final OpenEditTextPreference pref = (OpenEditTextPreference) getPreference();
				final String newValue = editable == null ? null : editable.toString();
				if (pref.callChangeListener(newValue))
				{
					pref.setValue(newValue);
				}
			}
		}
	}

	private static final String DIALOG_FRAGMENT_TAG = "OpenEditTextPreference";

	/**
	 * onDisplayPreferenceDialog helper
	 *
	 * @param prefFragment preference fragment
	 * @param preference   preference
	 * @return false if not handled: call super.onDisplayPreferenceDialog(preference)
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	static public boolean onDisplayPreferenceDialog(@NonNull final PreferenceFragmentCompat prefFragment, final Preference preference)
	{
		final FragmentManager manager;
		try
		{
			manager = prefFragment.getParentFragmentManager();
		}
		catch (IllegalStateException e)
		{
			return false;
		}

		if (manager.findFragmentByTag(DIALOG_FRAGMENT_TAG) != null)
		{
			return true;
		}

		if (preference instanceof OpenEditTextPreference)
		{
			final DialogFragment dialogFragment = OpenEditTextPreferenceDialogFragmentCompat.newInstance((OpenEditTextPreference) preference);
			//noinspection deprecation
			dialogFragment.setTargetFragment(prefFragment, 0);
			dialogFragment.show(manager, DIALOG_FRAGMENT_TAG);
			return true;
		}
		return false;
	}

	// S T A T E

	@Override
	protected Parcelable onSaveInstanceState()
	{
		final Parcelable superState = super.onSaveInstanceState();

		// check whether this Preference is persistent (continually saved)
		if (isPersistent())
		// no need to save instance state since it's persistent, use superclass state
		{
			return superState;
		}

		// create instance of custom BaseSavedState
		final StringSavedState state = new StringSavedState(superState);

		// set the state's value with the class member that holds current setting value
		state.value = this.value;
		return state;
	}

	@Override
	protected void onRestoreInstanceState(@Nullable final Parcelable state)
	{
		// check whether we saved the state in onSaveInstanceState
		if (state == null || !state.getClass().equals(StringSavedState.class))
		{
			// didn't save the state, so call superclass
			super.onRestoreInstanceState(state);
			return;
		}

		// cast state to custom BaseSavedState and pass to superclass
		final StringSavedState savedState = (StringSavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());

		// set this preference's widget to reflect the restored state
		setValue(savedState.value);
	}

	// S U M M A R Y

	/**
	 * Summary provider
	 */
	static public final Preference.SummaryProvider<OpenEditTextPreference> SUMMARY_PROVIDER = (preference) -> {

		final Context context = preference.getContext();
		final String value = preference.getPersistedString(null);
		return value == null ? context.getString(R.string.pref_value_default) : value;
	};
}
