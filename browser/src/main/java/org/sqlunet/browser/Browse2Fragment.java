/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.browser;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.util.Log;

import org.sqlunet.bnc.browser.BNCFragment;
import org.sqlunet.browser.web.WebFragment;
import org.sqlunet.browser.xn.Settings;
import org.sqlunet.browser.xselector.XSelectorPointer;
import org.sqlunet.browser.xselector.XSelectorsFragment;
import org.sqlunet.framenet.browser.FrameNetFragment;
import org.sqlunet.propbank.browser.PropBankFragment;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.speak.Pronunciation;
import org.sqlunet.speak.SpeakButton;
import org.sqlunet.speak.TTS;
import org.sqlunet.style.Factories;
import org.sqlunet.style.Spanner;
import org.sqlunet.verbnet.browser.VerbNetFragment;
import org.sqlunet.wordnet.browser.SenseFragment;

import java.util.List;

import androidx.annotation.Nullable;
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

	@Override
	public void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		final Bundle args = getArguments();
		final boolean alt = args != null && args.getBoolean(ARG_ALT);
		this.layoutId = alt ? R.layout.fragment_browse2_multi_alt : R.layout.fragment_browse2_multi;
	}

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

		// args
		final int recurse = Settings.getRecursePref(context);
		final Bundle args = new Bundle();
		args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, this.pointer);
		args.putString(ProviderArgs.ARG_HINTPOS, this.pos);
		args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse);

		// detail fragment
		final Settings.DetailViewMode mode = Settings.getDetailViewModePref(context);
		switch (mode)
		{
			case VIEW:
				int enable = Settings.getAllPref(context);
				if (this.pointer instanceof XSelectorPointer)
				{
					// sections to disable
					int mask = 0;
					final XSelectorPointer xpointer = (XSelectorPointer) this.pointer;
					final int group = xpointer.getXGroup();
					switch (group)
					{
						case XSelectorsFragment.GROUPID_WORDNET:
							mask = Settings.ENABLE_VERBNET | Settings.ENABLE_PROPBANK | Settings.ENABLE_FRAMENET;
							break;
						case XSelectorsFragment.GROUPID_VERBNET:
							mask = Settings.ENABLE_PROPBANK | Settings.ENABLE_FRAMENET;
							break;
						case XSelectorsFragment.GROUPID_PROPBANK:
							mask = Settings.ENABLE_VERBNET | Settings.ENABLE_FRAMENET;
							break;
						case XSelectorsFragment.GROUPID_FRAMENET:
							mask = Settings.ENABLE_VERBNET | Settings.ENABLE_PROPBANK;
							break;
					}
					enable &= ~mask;
				}

				// transaction
				final FragmentTransaction transaction = manager.beginTransaction().setReorderingAllowed(true);

				// wordnet
				if ((enable & Settings.ENABLE_WORDNET) != 0)
				{
					// final View labelView = findViewById(R.id.label_wordnet);
					// labelView.setVisibility(View.VISIBLE);
					final SenseFragment senseFragment = new SenseFragment();
					senseFragment.setArguments(args);
					senseFragment.setExpand(wordNetOnly(this.pointer));
					transaction.replace(R.id.container_wordnet, senseFragment, "wordnet");
				}
				else
				{
					final Fragment senseFragment = manager.findFragmentByTag("wordnet");
					if (senseFragment != null)
					{
						transaction.remove(senseFragment);
					}
				}

				// verbnet
				if ((enable & Settings.ENABLE_VERBNET) != 0)
				{
					// final View labelView = findViewById(R.id.label_verbnet);
					// labelView.setVisibility(View.VISIBLE);
					final Fragment verbnetFragment = new VerbNetFragment();
					verbnetFragment.setArguments(args);
					transaction.replace(R.id.container_verbnet, verbnetFragment, "verbnet");
				}
				else
				{
					final Fragment verbnetFragment = manager.findFragmentByTag("verbnet");
					if (verbnetFragment != null)
					{
						transaction.remove(verbnetFragment);
					}
				}

				// propbank
				if ((enable & Settings.ENABLE_PROPBANK) != 0)
				{
					// final View labelView = findViewById(R.id.label_propbank);
					// labelView.setVisibility(View.VISIBLE);
					final Fragment propbankFragment = new PropBankFragment();
					propbankFragment.setArguments(args);
					transaction.replace(R.id.container_propbank, propbankFragment, "propbank");
				}
				else
				{
					final Fragment propbankFragment = manager.findFragmentByTag("propbank");
					if (propbankFragment != null)
					{
						transaction.remove(propbankFragment);
					}
				}

				// framenet
				if ((enable & Settings.ENABLE_FRAMENET) != 0)
				{
					// final View labelView = findViewById(R.id.label_framenet);
					// labelView.setVisibility(View.VISIBLE);
					final Fragment framenetFragment = new FrameNetFragment();
					framenetFragment.setArguments(args);
					transaction.replace(R.id.container_framenet, framenetFragment, "framenet");
				}
				else
				{
					final Fragment framenetFragment = manager.findFragmentByTag("framenet");
					if (framenetFragment != null)
					{
						transaction.remove(framenetFragment);
					}
				}

				// bnc
				if ((enable & Settings.ENABLE_BNC) != 0)
				{
					// final View labelView = findViewById(R.id.label_bnc);
					// labelView.setVisibility(View.VISIBLE);
					final Fragment bncFragment = new BNCFragment();
					bncFragment.setArguments(args);
					transaction.replace(R.id.container_bnc, bncFragment, "bnc");
				}
				else
				{
					final Fragment bncFragment = manager.findFragmentByTag("bnc");
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
						.replace(R.id.container_web, webFragment, "web") //
						.commit();
				break;
		}
	}

	/**
	 * Determine whether to expand
	 *
	 * @param pointer pointer
	 * @return whether to expand
	 */
	private boolean wordNetOnly(final Parcelable pointer)
	{
		if (pointer instanceof XSelectorPointer)
		{
			final XSelectorPointer xSelectorPointer = (XSelectorPointer) pointer;
			return xSelectorPointer.wordNetOnly();
		}
		return false;
	}

	/**
	 * Search target info
	 *
	 * @return styled string
	 */
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
