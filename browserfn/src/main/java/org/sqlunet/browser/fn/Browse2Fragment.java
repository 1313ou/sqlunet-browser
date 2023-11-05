/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.fn;

import android.content.Context;
import android.os.Bundle;

import org.sqlunet.browser.BaseBrowse2Fragment;
import org.sqlunet.browser.fn.R;
import org.sqlunet.browser.fn.Settings;
import org.sqlunet.browser.web.WebFragment;
import org.sqlunet.framenet.FnFramePointer;
import org.sqlunet.framenet.browser.FnFrameFragment;
import org.sqlunet.framenet.browser.FnLexUnitFragment;
import org.sqlunet.framenet.browser.FrameNetFragment;
import org.sqlunet.provider.ProviderArgs;

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

				// transaction
				final FragmentTransaction transaction = manager.beginTransaction().setReorderingAllowed(true);

				// framenet
				boolean enable = Settings.getFrameNetPref(context);
				if (enable)
				{
					// final View labelView = findViewById(R.id.label_framenet);
					// labelView.setVisibility(View.VISIBLE);
					final Fragment framenetFragment = (this.pointer instanceof FnFramePointer) ? new FnFrameFragment() : new FnLexUnitFragment();
					framenetFragment.setArguments(args);
					transaction.replace(R.id.container_framenet, framenetFragment, FrameNetFragment.FRAGMENT_TAG);
				}
				else
				{
					final Fragment framenetFragment = manager.findFragmentByTag(FrameNetFragment.FRAGMENT_TAG);
					if (framenetFragment != null)
					{
						transaction.remove(framenetFragment);
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
