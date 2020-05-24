/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;

import org.sqlunet.bnc.browser.BNCFragment;
import org.sqlunet.browser.web.WebFragment;
import org.sqlunet.browser.xn.Settings;
import org.sqlunet.browser.xselector.XSelectorPointer;
import org.sqlunet.browser.xselector.XSelectorsFragment;
import org.sqlunet.browser.xselector.XSnSelectorsFragment;
import org.sqlunet.browser.xselector.XWnSelectorsFragment;
import org.sqlunet.provider.ProviderArgs;
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
		final FragmentManager manager = getChildFragmentManager();

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
//					final XSelectorPointer xpointer = (XSelectorPointer) this.pointer;
//					final int group = xpointer.getXGroup();
//					switch (group)
//					{
//						//TODO
//						case XWnSelectorsFragment.GROUPID_WORDNET:
//							mask = Settings.ENABLE_SYNTAGNET;
//							break;
//						//TODO
//						case XSnSelectorsFragment.GROUPID_SYNTAGNET:
//							mask = 0;
//							break;
//					}
					enable &= ~mask;
				}

				// transaction
				final FragmentTransaction transaction = manager.beginTransaction();

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
