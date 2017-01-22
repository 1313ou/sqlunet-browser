package org.sqlunet.browser.xselector;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.sqlunet.bnc.browser.BNCFragment;
import org.sqlunet.browser.Browse2Activity;
import org.sqlunet.browser.Browse2Fragment;
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
		final View view = inflater.inflate(R.layout.fragment_xbrowse1, container, false);

		// query
		final Bundle args = getArguments();
		final String query = args.getString(ProviderArgs.ARG_QUERYSTRING);

		// copy to query view
		final TextView queryView = (TextView) view.findViewById(R.id.queryView);
		queryView.setText(query);

		// selector fragment
		this.xSelectorsFragment = new XSelectorsFragment();
		this.xSelectorsFragment.setArguments(args);
		this.xSelectorsFragment.setListener(this);
		getChildFragmentManager() //
				.beginTransaction() //
				.replace(R.id.container_xselectors, this.xSelectorsFragment) //
				.commit();

		// two-pane specific set up
		if (view.findViewById(R.id.container_browse2) != null)
		{
			// the detail container view will be present only in the large-screen layouts (res/values-large and res/values-sw600dp).
			// if this view is present, then the activity should be in two-pane mode.
			this.isTwoPane = true;

			// detail fragment
			final Fragment browse2Fragment = new Browse2Fragment();
			getChildFragmentManager() //
					.beginTransaction() //
					.replace(R.id.container_browse2, browse2Fragment) //
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
		// activity
		final Activity activity = getActivity();

		if (this.isTwoPane)
		{
			// in two-pane mode, show the detail view in this activity by adding or replacing the detail fragment using a fragment transaction.
			final Bundle args = new Bundle();
			args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer);
			args.putString(ProviderArgs.ARG_HINTWORD, word);
			args.putString(ProviderArgs.ARG_HINTCASED, cased);
			args.putString(ProviderArgs.ARG_HINTPOS, pos);

			// transaction
			final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

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
						senseFragment.setArguments(args);
						senseFragment.setExpand(pointer.wordNetOnly());
						transaction.replace(R.id.container_wordnet, senseFragment);
					}

					// verbnet
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

					// propbank
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

					// framenet
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

					// bnc
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
					fragment.setArguments(args);

					// detail fragment replace
					transaction.replace(R.id.container_browse2, fragment);
					break;
			}
			transaction.commit();
		}
		else
		{
			// in single-pane mode, simply start the detail activity for the selected item ID.
			final Intent intent = new Intent(activity, Browse2Activity.class);
			intent.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer);
			startActivity(intent);
		}
	}
}
