package org.sqlunet.browser.xselector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.sqlunet.browser.Browse2Activity;
import org.sqlunet.browser.Browse2Fragment;
import org.sqlunet.browser.R;
import org.sqlunet.provider.ProviderArgs;

/**
 * X selector activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class XBrowse1Fragment extends Fragment implements XSelectorsFragment.Listener
{
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
	 */
	private boolean isTwoPane = false;

	/**
	 * Selectors fragment
	 */
	private XSelectorsFragment xSelectorsFragment;

	// C R E A T I O N

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = inflater.inflate(R.layout.fragment_xbrowse1, container, false);

		// query
		final Bundle args = getArguments();
		final String query = args == null ? null : args.getString(ProviderArgs.ARG_QUERYSTRING);

		// copy to target view
		final TextView targetView = (TextView) view.findViewById(R.id.targetView);
		targetView.setText(query);

		// x selector fragment
		this.xSelectorsFragment = new XSelectorsFragment();
		this.xSelectorsFragment.setArguments(args);
		this.xSelectorsFragment.setListener(this);

		// manager
		final FragmentManager manager = getChildFragmentManager();

		// transaction on selectors pane
		manager.beginTransaction() //
				.replace(R.id.container_xselectors, this.xSelectorsFragment) //
				.commit();

		// two-pane specific set up
		if (view.findViewById(R.id.container_browse2) != null)
		{
			// the detail container view will be present only in the large-screen layouts (res/values-large and res/values-sw600dp).
			// if this view is present, then the activity should be in two-pane mode.
			this.isTwoPane = true;

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
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		if (this.isTwoPane)
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
		if (this.isTwoPane)
		{
			// in two-pane mode, show the detail view in this activity by adding or replacing the detail fragment using a fragment transaction.
			final Browse2Fragment fragment = (Browse2Fragment) getChildFragmentManager().findFragmentById(R.id.container_browse2);
			fragment.search(pointer);
		}
		else
		{
			// in single-pane mode, simply start the detail activity for the selected item ID.
			final Bundle args = new Bundle();
			args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer);
			args.putString(ProviderArgs.ARG_HINTWORD, word);
			args.putString(ProviderArgs.ARG_HINTCASED, cased);
			args.putString(ProviderArgs.ARG_HINTPOS, pos);

			final Intent intent = new Intent(getActivity(), Browse2Activity.class);
			intent.putExtras(args);
			startActivity(intent);
		}
	}
}
