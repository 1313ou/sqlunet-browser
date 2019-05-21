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
import org.sqlunet.framenet.browser.FrameNetFragment;
import org.sqlunet.propbank.browser.PropBankFragment;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.verbnet.browser.VerbNetFragment;
import org.sqlunet.wordnet.browser.SenseFragment;

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
