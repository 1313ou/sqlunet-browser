/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.vn;

import android.content.Context;
import android.os.Bundle;

import org.sqlunet.browser.BaseBrowse2Fragment;
import org.sqlunet.browser.vn.web.WebFragment;
import org.sqlunet.browser.vn.xselector.XSelectorPointer;
import org.sqlunet.browser.vn.xselector.XSelectorsFragment;
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

		// args
		final Bundle args = new Bundle();
		args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, this.pointer);

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
					final int groupId = xpointer.getXGroup();
					switch (groupId)
					{
						case XSelectorsFragment.GROUPID_VERBNET:
							mask = Settings.ENABLE_PROPBANK;
							break;
						case XSelectorsFragment.GROUPID_PROPBANK:
							mask = Settings.ENABLE_VERBNET | Settings.ENABLE_WORDNET;
							break;
					}
					enable &= ~mask;
				}

				// transaction
				final FragmentTransaction transaction = manager.beginTransaction().setReorderingAllowed(true);

				// verbnet
				if ((enable & Settings.ENABLE_VERBNET) != 0)
				{
					// final View labelView = findViewById(R.id.label_verbnet);
					// labelView.setVisibility(View.VISIBLE);
					final Fragment verbnetFragment = new VerbNetFragment();
					verbnetFragment.setArguments(args);
					transaction.replace(R.id.container_verbnet, verbnetFragment, VerbNetFragment.FRAGMENT_TAG);
				}
				else
				{
					final Fragment verbnetFragment = manager.findFragmentByTag(VerbNetFragment.FRAGMENT_TAG);
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
					transaction.replace(R.id.container_propbank, propbankFragment, PropBankFragment.FRAGMENT_TAG);
				}
				else
				{
					final Fragment propbankFragment = manager.findFragmentByTag(PropBankFragment.FRAGMENT_TAG);
					if (propbankFragment != null)
					{
						transaction.remove(propbankFragment);
					}
				}

				// wordnet
				if ((enable & Settings.ENABLE_WORDNET) != 0)
				{
					// final View labelView = findViewById(R.id.label_wordnet);
					// labelView.setVisibility(View.VISIBLE);
					final SenseFragment senseFragment = new SenseFragment();
					senseFragment.setArguments(args);
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
}
