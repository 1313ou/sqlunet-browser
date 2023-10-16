/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.speak;

import android.os.Build;
import android.os.Bundle;
import android.speech.tts.Voice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.preference.PreferenceFragmentCompat;

public class SpeakSettingsFragment extends PreferenceFragmentCompat
{
	public static final String VOICE_PREF = "voice";
	public static final String COUNTRY_PREF = "country";

	@RequiresApi(api = Build.VERSION_CODES.N)
	public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey)
	{
		this.setPreferencesFromResource(R.xml.speak_preferences, rootKey);

		final String noneString = requireContext().getString(R.string.none);

		final ResettableMultiSelectListPreference voicePref = this.findPreference(VOICE_PREF);
		if (voicePref != null)
		{
			voicePref.setEntries(new String[]{noneString});
			voicePref.setEntryValues(new String[]{""});
			voicePref.setSummaryProvider(pref2 -> prepareSummary((MultiSelectListPreference) pref2));
			voicePref.setClickListener((v) -> voicePref.setValues(new HashSet<>()));

			new Discover().discoverVoices(getContext(), voices -> {

				List<String> voicesValues = prepareVoiceValues(voices);
				voicePref.setEntryValues(voicesValues.toArray(new String[0]));

				List<String> voicesLabels = prepareVoiceLabels(voices);
				voicePref.setEntries(voicesLabels.toArray(new String[0]));

				voicePref.notifyEntriesChanged();
			});
		}

		final ListPreference countryPref = this.findPreference(COUNTRY_PREF);
		if (countryPref != null)
		{
			countryPref.setEntries(new String[]{noneString});
			countryPref.setEntryValues(new String[]{""});

			new Discover().discoverLanguages(getContext(), locales -> {

				List<String> localeValues = prepareLocaleValues(locales);
				localeValues.add("");
				countryPref.setEntryValues(localeValues.toArray(new String[0]));

				List<String> localeLabels = prepareLocaleLabels(locales);
				localeLabels.add(noneString);
				countryPref.setEntries(localeLabels.toArray(new String[0]));

				countryPref.notifyEntriesChanged();
			});
		}
	}

	@NonNull
	@RequiresApi(api = Build.VERSION_CODES.N)
	private List<String> prepareVoiceValues(@NonNull List<Voice> voices)
	{
		return voices.stream().map(Voice::getName).collect(Collectors.toList());
	}

	@NonNull
	@RequiresApi(api = Build.VERSION_CODES.N)
	private List<String> prepareVoiceLabels(@NonNull List<Voice> voices)
	{
		return voices.stream().map(voice -> voice.getName() + " " + (voice.isNetworkConnectionRequired() ? "N" : "L")).collect(Collectors.toList());
	}

	@NonNull
	@RequiresApi(api = Build.VERSION_CODES.N)
	private List<String> prepareLocaleValues(@NonNull List<Locale> locales)
	{
		return locales.stream().map(Locale::getCountry).collect(Collectors.toList());
	}

	@NonNull
	@RequiresApi(api = Build.VERSION_CODES.N)
	private List<String> prepareLocaleLabels(@NonNull List<Locale> locales)
	{
		return locales.stream().map(locale -> locale.getCountry() + " " + locale.getLanguage()).collect(Collectors.toList());
	}

	private CharSequence prepareSummary(@NonNull MultiSelectListPreference pref)
	{
		final String noneString = requireContext().getString(R.string.none);
		List<String> titles = new ArrayList<>();
		CharSequence[] entryValues = pref.getEntryValues();
		Set<String> persisted = pref.getPersistedStringSet(null);
		if (persisted != null)
		{
			if (persisted.size() == 0)
			{
				return noneString;
			}
			else
			{
				if (entryValues != null)
				{
					int i = 0;
					for (CharSequence value : entryValues)
					{
						if (persisted.contains(value.toString()))
						{
							titles.add(entryValues[i].toString().substring(3));
						}
						i++;
					}
				}
			}
			return String.join("\n", titles);
		}
		return noneString;
	}
}
