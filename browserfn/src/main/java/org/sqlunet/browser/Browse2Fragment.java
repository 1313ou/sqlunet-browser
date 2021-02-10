/*
 * Copyright (c) 2021. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser;

import android.content.Context;
import android.os.Bundle;

import org.sqlunet.browser.fn.R;
import org.sqlunet.browser.fn.Settings;
import org.sqlunet.browser.web.WebFragment;
import org.sqlunet.framenet.FnFramePointer;
import org.sqlunet.framenet.browser.FnFrameFragment;
import org.sqlunet.framenet.browser.FnLexUnitFragment;
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
				final FragmentTransaction transaction = manager.beginTransaction();

				// framenet
				boolean enable = Settings.getFrameNetPref(context);
				if (enable)
				{
					// final View labelView = findViewById(R.id.label_framenet);
					// labelView.setVisibility(View.VISIBLE);
					final Fragment framenetFragment = (this.pointer instanceof FnFramePointer) ? new FnFrameFragment() : new FnLexUnitFragment();
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
}
