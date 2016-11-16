package org.sqlunet.browser.xselector;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.sqlunet.bnc.browser.BNCFragment;
import org.sqlunet.browser.DetailActivity;
import org.sqlunet.browser.DetailFragment;
import org.sqlunet.browser.R;
import org.sqlunet.browser.web.WebFragment;
import org.sqlunet.framenet.browser.FrameNetFragment;
import org.sqlunet.propbank.browser.PropBankFragment;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.settings.Settings;
import org.sqlunet.verbnet.browser.VerbNetFragment;
import org.sqlunet.wordnet.browser.SenseFragment;
import org.sqlunet.wordnet.browser.SynsetFragment;

/**
 * X selector activity
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class XSelectorFragment extends Fragment implements XSelectorResultFragment.Listener
{
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
	 */
	private boolean isTwoPane = false;

	/**
	 * VerbNet fragment
	 */
	private Fragment verbnetFragment;

	/**
	 * PropBank fragment
	 */
	private Fragment propbankFragment;

	/**
	 * FrameNet fragment
	 */
	private Fragment framenetFragment;

	// C R E A T I O N

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		// view
		final View view = inflater.inflate(R.layout.fragment_xselector, container);

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
			final XSelectorResultFragment xFragment = (XSelectorResultFragment) getFragmentManager().findFragmentById(R.id.xselector);
			xFragment.setActivateOnItemClick(true);
		}

		return view;
	}

	// I T E M S E L E C T I O N H A N D L I N G

	/**
	 * Callback method from {@link XSelectorResultFragment.Listener} indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(final XSelectorPointer pointer)
	{
		// activity
		final Activity activity = getActivity();

		if (this.isTwoPane)
		{
			// in two-pane mode, show the detail view in this activity by adding or replacing the detail fragment using a fragment transaction.
			final Bundle args = new Bundle();
			args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer);

			// transaction
			final FragmentTransaction transaction = getFragmentManager().beginTransaction();

			// detail fragment
			final Settings.DetailViewMode mode = Settings.getDetailViewModePref(activity);
			switch (mode)
			{
				case VIEW:
					if (Settings.getWordNetPref(activity))
					{
						// final View labelView = findViewById(R.id.label_wordnet);
						// labelView.setVisibility(View.VISIBLE);
						final SynsetFragment senseFragment = new SenseFragment();
						senseFragment.setExpand(pointer.wordNetOnly());
						senseFragment.setArguments(args);
						transaction.replace(R.id.container_wordnet, senseFragment);
					}

					if (Settings.getVerbNetPref(activity) && pointer.getXSources().contains("vn")) //
					{
						// final View labelView = findViewById(R.id.label_verbnet);
						// labelView.setVisibility(View.VISIBLE);
						this.verbnetFragment = new VerbNetFragment();
						this.verbnetFragment.setArguments(args);
						transaction.replace(R.id.container_verbnet, this.verbnetFragment);
					}
					else if (this.verbnetFragment != null)
					{
						transaction.remove(this.verbnetFragment);
						this.verbnetFragment = null;
					}

					if (Settings.getPropBankPref(activity) && pointer.getXSources().contains("pb")) //
					{
						// final View labelView = findViewById(R.id.label_propbank);
						// labelView.setVisibility(View.VISIBLE);
						this.propbankFragment = new PropBankFragment();
						this.propbankFragment.setArguments(args);
						transaction.replace(R.id.container_propbank, this.propbankFragment);
					}
					else if (this.propbankFragment != null)
					{
						transaction.remove(this.propbankFragment);
						this.propbankFragment = null;
					}

					if (Settings.getFrameNetPref(activity) && pointer.getXSources().contains("fn")) //
					{
						// final View labelView = findViewById(R.id.label_framenet);
						// labelView.setVisibility(View.VISIBLE);
						this.framenetFragment = new FrameNetFragment();
						this.framenetFragment.setArguments(args);
						transaction.replace(R.id.container_framenet, this.framenetFragment);
					}
					else if (this.framenetFragment != null)
					{
						transaction.remove(this.framenetFragment);
						this.framenetFragment = null;
					}

					if (Settings.getBncPref(activity))
					{
						// final View labelView = findViewById(R.id.label_bnc);
						// labelView.setVisibility(View.VISIBLE);
						final Fragment bncFragment = new BNCFragment();
						bncFragment.setArguments(args);
						transaction.replace(R.id.container_bnc, bncFragment);
					}
					break;

				case WEB:
					final Fragment fragment = new WebFragment();

					// arguments
					fragment.setArguments(args);

					// detail fragment replace
					transaction.replace(R.id.container_main, fragment);
					break;
			}
			transaction.commit();
		}
		else
		{
			// in single-pane mode, simply start the detail activity for the selected item ID.
			final Intent intent = new Intent(activity, DetailActivity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
			startActivity(intent);
		}
	}
}
