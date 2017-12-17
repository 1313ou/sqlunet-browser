package org.sqlunet.browser.xselector;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.browser.Browse2Activity;
import org.sqlunet.browser.Browse2Fragment;
import org.sqlunet.browser.R;
import org.sqlunet.browser.xn.Settings;
import org.sqlunet.provider.ProviderArgs;

/**
 * X selector fragment
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class XBrowse1Fragment extends Fragment implements XSelectorsFragment.Listener
{
	/**
	 * Selectors fragment
	 */
	private XSelectorsFragment xSelectorsFragment;

	// C R E A T I O N

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = inflater.inflate(R.layout.fragment_xbrowse1, container, false);

		// retain instance
		setRetainInstance(true);

		// manager
		final FragmentManager manager = getChildFragmentManager();

		// x selector fragment
		this.xSelectorsFragment = (XSelectorsFragment) manager.findFragmentByTag("browse1");
		if (this.xSelectorsFragment == null)
		{
			this.xSelectorsFragment = new XSelectorsFragment();
			this.xSelectorsFragment.setArguments(getArguments());
			this.xSelectorsFragment.setListener(this);
		}

		// transaction on selectors pane
		manager.beginTransaction() //
				.replace(R.id.container_xselectors, this.xSelectorsFragment, "browse1") //
				.commit();

		// two-pane specific set up
		if (isTwoPane(view))
		{
			// detail fragment
			Fragment browse2Fragment = manager.findFragmentByTag("browse2");
			if (browse2Fragment == null)
			{
				browse2Fragment = new Browse2Fragment();
			}
			manager.beginTransaction() //
					.replace(R.id.container_browse2, browse2Fragment, "browse2") //
					.commit();
		}

		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		if (isTwoPane(view))
		{
			// in two-pane mode, list items should be given the 'activated' state when touched.
			this.xSelectorsFragment.setActivateOnItemClick(true);
		}
	}

	// I T E M S E L E C T I O N H A N D L I N G

	/**
	 * Callback method from {@link XSelectorsFragment.Listener} indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(final XSelectorPointer pointer, final String word, final String cased, final String pos)
	{
		if (isTwoPane(getView()))
		{
			// in two-pane mode, show the detail view in this activity by adding or replacing the detail fragment using a fragment transaction.
			final Browse2Fragment fragment = (Browse2Fragment) getChildFragmentManager().findFragmentById(R.id.container_browse2);
			fragment.search(pointer, pos);
		}
		else
		{
			// in single-pane mode, simply start the detail activity for the selected item ID.
			final int recurse = Settings.getRecursePref(getContext());
			final Bundle args = new Bundle();
			args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer);
			args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse);
			args.putString(ProviderArgs.ARG_HINTWORD, word);
			args.putString(ProviderArgs.ARG_HINTCASED, cased);
			args.putString(ProviderArgs.ARG_HINTPOS, pos);

			final Intent intent = new Intent(getActivity(), Browse2Activity.class);
			intent.putExtras(args);
			startActivity(intent);
		}
	}

	// V I E W   D E T E C T I O N

	/**
	 * Whether view is two-pane
	 *
	 * @param view view
	 * @return true if view is two-pane
	 */
	private boolean isTwoPane(@NonNull final View view)
	{
		// the detail view will be present only in the large-screen layouts
		// if this view is present, then the activity should be in two-pane mode.
		return view.findViewById(R.id.detail) != null;
	}
}
