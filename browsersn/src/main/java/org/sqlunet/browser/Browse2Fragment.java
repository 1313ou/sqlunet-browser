/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;

import org.sqlunet.bnc.browser.BNCFragment;
import org.sqlunet.browser.selector.CollocationSelectorPointer;
import org.sqlunet.browser.sn.R;
import org.sqlunet.browser.sn.Settings;
import org.sqlunet.browser.web.WebFragment;
import org.sqlunet.browser.xselector.XSelectorPointer;
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
		final int recurse = Settings.getRecursePref(context);
		final Bundle args = new Bundle();
		args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, this.pointer);
		args.putString(ProviderArgs.ARG_HINTPOS, this.pos);
		args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse);

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
				final FragmentTransaction transaction = manager.beginTransaction();

				// wordnet
				if ((enable & Settings.ENABLE_WORDNET) != 0 && hasWordNet)
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

				// syntagnet
				if ((enable & Settings.ENABLE_SYNTAGNET) != 0)
				{
					// final View labelView = findViewById(R.id.label_syntagnet);
					// labelView.setVisibility(View.VISIBLE);
					final Fragment syntagNetFragment = new SyntagNetFragment();
					syntagNetFragment.setArguments(args);
					transaction.replace(R.id.container_syntagnet, syntagNetFragment, "syntagnet");
				}
				else
				{
					final Fragment collocationFragment = manager.findFragmentByTag("syntagnet");
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
}
