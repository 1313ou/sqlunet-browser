package org.sqlunet.browser;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sqlunet.bnc.browser.BNCFragment;
import org.sqlunet.browser.web.WebFragment;
import org.sqlunet.browser.xselector.XSelectorPointer;
import org.sqlunet.framenet.browser.FrameNetFragment;
import org.sqlunet.propbank.browser.PropBankFragment;
import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.settings.Settings;
import org.sqlunet.verbnet.browser.VerbNetFragment;
import org.sqlunet.wordnet.browser.SenseFragment;
import org.sqlunet.wordnet.browser.SynsetFragment;

/**
 * A fragment representing a detail
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class DetailFragment extends Fragment
{
	// C R E A T I O N

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public DetailFragment()
	{
		//
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		final Settings.DetailViewMode mode = Settings.getDetailViewModePref(this.getActivity());

		// layout
		View rootView = null;
		switch (mode)
		{
			case VIEW:
				rootView = inflater.inflate(R.layout.fragment_details, container, false);
				break;
			case WEB:
				rootView = inflater.inflate(R.layout.fragment_detail, container, false);
				break;
		}
		return rootView;
	}

	/**
	 * Search
	 *
	 * @param pointer pointer
	 */
	public void search(final Parcelable pointer)
	{
		final Context context = this.getActivity();

		// args
		final Bundle args = new Bundle();
		args.putParcelable(SqlUNetContract.ARG_QUERYPOINTER, pointer);

		// detail fragment
		final Settings.DetailViewMode mode = Settings.getDetailViewModePref(context);
		switch (mode)
		{
			case VIEW:
				if (Settings.getWordNetPref(context))
				{
					// final View labelView = findViewById(R.id.label_wordnet);
					// labelView.setVisibility(View.VISIBLE);
					final SynsetFragment senseFragment = new SenseFragment();
					senseFragment.setExpand(wordNetOnly(pointer));
					senseFragment.setArguments(args);
					getFragmentManager() //
							.beginTransaction() //
							.replace(R.id.container_wordnet, senseFragment) //
							.commit();
				}

				if (Settings.getVerbNetPref(context))
				{
					// final View labelView = findViewById(R.id.label_verbnet);
					// labelView.setVisibility(View.VISIBLE);
					final Fragment verbnetFragment = new VerbNetFragment();
					verbnetFragment.setArguments(args);
					getFragmentManager() //
							.beginTransaction() //
							.replace(R.id.container_verbnet, verbnetFragment) //
							.commit();
				}

				if (Settings.getPropBankPref(context))
				{
					// final View labelView = findViewById(R.id.label_propbank);
					// labelView.setVisibility(View.VISIBLE);
					final Fragment propbankFragment = new PropBankFragment();
					propbankFragment.setArguments(args);
					getFragmentManager() //
							.beginTransaction() //
							.replace(R.id.container_propbank, propbankFragment) //
							.commit();
				}

				if (Settings.getFrameNetPref(context))
				{
					// final View labelView = findViewById(R.id.label_framenet);
					// labelView.setVisibility(View.VISIBLE);
					final Fragment framenetFragment = new FrameNetFragment();
					framenetFragment.setArguments(args);
					getFragmentManager() //
							.beginTransaction() //
							.replace(R.id.container_framenet, framenetFragment) //
							.commit();
				}

				if (Settings.getBncPref(context))
				{
					// final View labelView = findViewById(R.id.label_bnc);
					// labelView.setVisibility(View.VISIBLE);
					final Fragment bncFragment = new BNCFragment();
					bncFragment.setArguments(args);
					getFragmentManager() //
							.beginTransaction() //
							.replace(R.id.container_bnc, bncFragment) //
							.commit();
				}
				break;

			case WEB:
				// fragment
				final Fragment fragment = new WebFragment();

				// arguments
				fragment.setArguments(args);

				// detail fragment replace
				getFragmentManager().beginTransaction().replace(R.id.container_web, fragment).commit();
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
