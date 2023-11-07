/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.wn;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.util.Log;

import org.sqlunet.bnc.browser.BNCFragment;
import org.sqlunet.browser.BaseBrowse2Fragment;
import org.sqlunet.browser.web.WebFragment;
import org.sqlunet.browser.wn.Settings;
import org.sqlunet.browser.wn.lib.R;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.speak.Pronunciation;
import org.sqlunet.speak.SpeakButton;
import org.sqlunet.speak.TTS;
import org.sqlunet.style.Factories;
import org.sqlunet.style.Spanner;
import org.sqlunet.wordnet.browser.SenseFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * A fragment representing a detail
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Browse2Fragment extends BaseBrowse2Fragment
{
	public static final String ARG_ALT = "alt_arg";

	/**
	 * Search
	 */
	@Override
	protected void search()
	{
		final Context context = requireContext();
		if (!isAdded())
		{
			return;
		}
		final FragmentManager manager = getChildFragmentManager();

		// target
		assert targetView != null;
		targetView.setMovementMethod(new LinkMovementMethod());
		targetView.setText(toTarget());

		// parameters
		final int recurse = org.sqlunet.wordnet.settings.Settings.getRecursePref(context);
		final Bundle parameters = org.sqlunet.wordnet.settings.Settings.getRenderParametersPref(requireContext());

		final Bundle args = new Bundle();
		args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, this.pointer);
		args.putString(ProviderArgs.ARG_HINTWORD, this.word);
		args.putString(ProviderArgs.ARG_HINTCASED, this.cased);
		args.putString(ProviderArgs.ARG_HINTPRONUNCIATION, this.pronunciation);
		args.putString(ProviderArgs.ARG_HINTPOS, this.pos);
		args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse);
		args.putBundle(ProviderArgs.ARG_RENDERPARAMETERS, parameters);

		// detail fragment
		final Settings.DetailViewMode mode = Settings.getDetailViewModePref(context);
		switch (mode)
		{
			case VIEW:

				// transaction
				final FragmentTransaction transaction = manager.beginTransaction().setReorderingAllowed(true);

				int enable = Settings.getAllPref(context);

				// wordnet
				if ((enable & Settings.ENABLE_WORDNET) != 0)
				{
					// final View labelView = findViewById(R.id.label_wordnet);
					// labelView.setVisibility(View.VISIBLE);
					final SenseFragment senseFragment = new SenseFragment();
					senseFragment.setArguments(args);
					senseFragment.setExpand(false);
					transaction.replace(R.id.container_wordnet, senseFragment, SenseFragment.FRAGMENT_TAG);
				}
				else
				{
					final Fragment senseFragment = manager.findFragmentByTag(SenseFragment.FRAGMENT_TAG);
					if (senseFragment != null)
					{
						transaction.remove(senseFragment);
					}
				}

				// bnc
				if ((enable & Settings.ENABLE_BNC) != 0)
				{
					// final View labelView = findViewById(R.id.label_bnc);
					// labelView.setVisibility(View.VISIBLE);
					final Fragment bncFragment = new BNCFragment();
					bncFragment.setArguments(args);
					transaction.replace(R.id.container_bnc, bncFragment, BNCFragment.FRAGMENT_TAG);
				}
				else
				{
					final Fragment bncFragment = manager.findFragmentByTag(BNCFragment.FRAGMENT_TAG);
					if (bncFragment != null)
					{
						transaction.remove(bncFragment);
					}
				}

				transaction.commit();
				break;

			case WEB:
				// web fragment
				final Fragment webFragment = new WebFragment();
				webFragment.setArguments(args);

				// detail fragment replace
				manager.beginTransaction() //
						.setReorderingAllowed(true) //
						.replace(R.id.container_web, webFragment, WebFragment.FRAGMENT_TAG) //
						.commit();
				break;
		}
	}

	/**
	 * Search target info
	 *
	 * @return styled string
	 */
	@NonNull
	private CharSequence toTarget()
	{
		final SpannableStringBuilder sb = new SpannableStringBuilder();
		if (cased != null)
		{
			sb.append(' ');
			Spanner.append(sb, cased, 0, Factories.casedFactory);
		}
		else
		{
			Spanner.append(sb, word, 0, Factories.wordFactory);
		}
		if (pos != null)
		{
			sb.append(' ');
			Spanner.append(sb, pos, 0, Factories.posFactory);
		}
		if (pronunciation != null)
		{
			List<Pronunciation> pronunciations = Pronunciation.pronunciations(pronunciation);
			for (Pronunciation p : pronunciations)
			{
				final String label = p.toString();
				final String ipa = p.ipa;
				final String country = p.variety == null ? org.sqlunet.speak.Settings.findCountry(requireContext()) : p.variety;
				sb.append('\n');
				SpeakButton.appendClickableImage(sb, org.sqlunet.speak.R.drawable.ic_speak_button, label, () -> {
					Log.d("Speak", "");
					TTS.pronounce(requireContext(), word, ipa, country, org.sqlunet.speak.Settings.findVoiceFor(country, requireContext()));
				}, requireContext());
			}
		}
		return sb;
	}
}
