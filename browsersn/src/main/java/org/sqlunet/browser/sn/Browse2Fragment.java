/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.sn;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;

import org.sqlunet.bnc.browser.BNCFragment;
import org.sqlunet.browser.BaseBrowse2Fragment;
import org.sqlunet.browser.sn.selector.CollocationSelectorPointer;
import org.sqlunet.browser.sn.web.WebFragment;
import org.sqlunet.browser.sn.xselector.XSelectorPointer;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.syntagnet.browser.SyntagNetFragment;
import org.sqlunet.wordnet.browser.SenseFragment;

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

		// args
		final int recurse = org.sqlunet.wordnet.settings.Settings.getRecursePref(context);
		final Bundle parameters = org.sqlunet.wordnet.settings.Settings.getRenderParametersPref(requireContext());

		final Bundle args = new Bundle();
		args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, this.pointer);
		args.putString(ProviderArgs.ARG_HINTPOS, this.pos);
		args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse);
		args.putBundle(ProviderArgs.ARG_RENDERPARAMETERS, parameters);

		//
		boolean hasWordNet = true;
		if (this.pointer instanceof CollocationSelectorPointer)
		{
			CollocationSelectorPointer selectorPointer = (CollocationSelectorPointer) this.pointer;
			hasWordNet = selectorPointer.getSynsetId() != -1 || selectorPointer.getSynset2Id() != -1;
		}

		// detail fragment
		final Settings.DetailViewMode mode = Settings.getDetailViewModePref(context);
		switch (mode)
		{
			case VIEW:
				int enable = Settings.getAllPref(context);

				// transaction
				final FragmentTransaction transaction = manager.beginTransaction().setReorderingAllowed(true);

				// wordnet
				if ((enable & Settings.ENABLE_WORDNET) != 0 && hasWordNet)
				{
					// final View labelView = findViewById(R.id.label_wordnet);
					// labelView.setVisibility(View.VISIBLE);
					final SenseFragment senseFragment = new SenseFragment();
					senseFragment.setArguments(args);
					senseFragment.setExpand(wordNetOnly(this.pointer));
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

				// syntagnet
				if ((enable & Settings.ENABLE_SYNTAGNET) != 0)
				{
					// final View labelView = findViewById(R.id.label_syntagnet);
					// labelView.setVisibility(View.VISIBLE);
					final Fragment syntagNetFragment = new SyntagNetFragment();
					syntagNetFragment.setArguments(args);
					transaction.replace(R.id.container_syntagnet, syntagNetFragment, SyntagNetFragment.FRAGMENT_TAG);
				}
				else
				{
					final Fragment collocationFragment = manager.findFragmentByTag(SyntagNetFragment.FRAGMENT_TAG);
					if (collocationFragment != null)
					{
						transaction.remove(collocationFragment);
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
}
