package org.sqlunet.browser.selector;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.sqlunet.browser.DetailActivity;
import org.sqlunet.browser.DetailFragment;
import org.sqlunet.browser.R;
import org.sqlunet.provider.ProviderArgs;

/**
 * Selector activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SelectorFragment extends Fragment implements SelectorResultFragment.Listener
{
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
	 */
	private boolean isTwoPane = false;

	// C R E A T I O N

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = inflater.inflate(R.layout.fragment_selector, container);

		// query
		final Bundle args = getArguments();
		final String query = args.getString(ProviderArgs.ARG_QUERYSTRING);

		// copy to query view
		final TextView queryView = (TextView) view.findViewById(R.id.queryView);
		queryView.setText(query);

		// two-pane specific set up
		if (view.findViewById(R.id.container_main) != null)
		{
			// the detail container view will be present only in the large-screen layouts (res/values-large and res/values-sw600dp).
			// if this view is present, then the activity should be in two-pane mode.
			this.isTwoPane = true;

			// detail fragment
			final Fragment detailFragment = new DetailFragment();
			getFragmentManager() //
					.beginTransaction() //
					.replace(R.id.container_main, detailFragment) //
					.commit();

			// in two-pane mode, list items should be given the 'activated' state when touched.
			final SelectorResultFragment fragment = (SelectorResultFragment) getFragmentManager().findFragmentById(R.id.selector);
			fragment.setActivateOnItemClick(true);
		}

		return view;
	}

	// I T E M S E L E C T I O N H A N D L I N G

	/**
	 * Callback method from {@link SelectorResultFragment.Listener} indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(final SelectorPointer pointer)
	{
		if (this.isTwoPane)
		{
			// in two-pane mode, show the detail view in this activity by adding or replacing the detail fragment using a fragment transaction.
			final DetailFragment fragment = (DetailFragment) getFragmentManager().findFragmentById(R.id.container_main);
			fragment.search(pointer);
		}
		else
		{
			// in single-pane mode, simply start the detail activity for the selected item ID.
			final Bundle args = new Bundle();
			args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer);

			final Intent intent = new Intent(getActivity(), DetailActivity.class);
			intent.putExtras(args);
			startActivity(intent);
		}
	}
}
