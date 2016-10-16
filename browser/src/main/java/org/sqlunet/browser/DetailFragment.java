package org.sqlunet.browser;

import org.sqlunet.bnc.browser.BNCFragment;
import org.sqlunet.browser.web.WebFragment;
import org.sqlunet.framenet.browser.FrameNetFragment;
import org.sqlunet.propbank.browser.PropbankFragment;
import org.sqlunet.provider.SqlUNetContract;
import org.sqlunet.settings.Settings;
import org.sqlunet.verbnet.browser.VerbNetFragment;
import org.sqlunet.wordnet.browser.SenseFragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A fragment representing a synset.
 *
 * @author Bernard Bou
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		final Settings.DetailMode mode = Settings.getDetailModePref(this.getActivity());

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

	public void search(Parcelable pointer)
	{
		final Context context = this.getActivity();

		final Bundle arguments = new Bundle();
		arguments.putParcelable(SqlUNetContract.ARG_QUERYPOINTER, pointer);

		// detail fragment
		final Settings.DetailMode mode = Settings.getDetailModePref(context);
		switch (mode)
		{
		case VIEW:
			if (Settings.getWordNetPref(context))
			{
				// final View labelView = findViewById(R.id.label_wordnet);
				// labelView.setVisibility(View.VISIBLE);
				final Fragment senseFragment = new SenseFragment();
				senseFragment.setArguments(arguments);
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
				verbnetFragment.setArguments(arguments);
				getFragmentManager() //
						.beginTransaction() //
						.replace(R.id.container_verbnet, verbnetFragment) //
						.commit();
			}

			if (Settings.getPropBankPref(context))
			{
				// final View labelView = findViewById(R.id.label_propbank);
				// labelView.setVisibility(View.VISIBLE);
				final Fragment propbankFragment = new PropbankFragment();
				propbankFragment.setArguments(arguments);
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
				framenetFragment.setArguments(arguments);
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
				bncFragment.setArguments(arguments);
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
			fragment.setArguments(arguments);

			// detail fragment replace
			getFragmentManager().beginTransaction().replace(R.id.container_web, fragment).commit();
			break;
		default:
		}
	}
}
