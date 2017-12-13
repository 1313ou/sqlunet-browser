package org.sqlunet.browser;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import org.sqlunet.browser.vn.R;
import org.sqlunet.browser.vn.Settings;
import org.sqlunet.browser.web.WebFragment;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.verbnet.browser.VnClassFragment;

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
		final Context context = this.getActivity();
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

				// verbnet
				boolean enable = Settings.getVerbNetPref(context);
				if (enable)
				{
					// final View labelView = findViewById(R.id.label_verbnet);
					// labelView.setVisibility(View.VISIBLE);
					final Fragment verbnetFragment = new VnClassFragment();
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
