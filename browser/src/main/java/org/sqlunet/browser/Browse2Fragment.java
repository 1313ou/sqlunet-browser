package org.sqlunet.browser;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.settings.Settings;
import org.sqlunet.verbnet.browser.VerbNetFragment;
import org.sqlunet.wordnet.browser.SenseFragment;
import org.sqlunet.wordnet.browser.SynsetFragment;

/**
 * A fragment representing a detail
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Browse2Fragment extends Fragment
{
	// C R E A T I O N

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public Browse2Fragment()
	{
		//
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
	{
		final Settings.DetailViewMode mode = Settings.getDetailViewModePref(this.getActivity());

		// view
		View view = null;
		switch (mode)
		{
			case VIEW:
				view = inflater.inflate(R.layout.fragment_browse2_multi, container, false);
				break;
			case WEB:
				view = inflater.inflate(R.layout.fragment_browse2, container, false);
				break;
		}
		return view;
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
		args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer);

		// detail fragment
		final Settings.DetailViewMode mode = Settings.getDetailViewModePref(context);
		switch (mode)
		{
			case VIEW:
				final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
				if (Settings.getWordNetPref(context))
				{
					// final View labelView = findViewById(R.id.label_wordnet);
					// labelView.setVisibility(View.VISIBLE);
					final SynsetFragment senseFragment = new SenseFragment();
					senseFragment.setArguments(args);
					senseFragment.setExpand(wordNetOnly(pointer));
					transaction.replace(R.id.container_wordnet, senseFragment);
				}

				if (Settings.getVerbNetPref(context))
				{
					// final View labelView = findViewById(R.id.label_verbnet);
					// labelView.setVisibility(View.VISIBLE);
					final Fragment verbnetFragment = new VerbNetFragment();
					verbnetFragment.setArguments(args);
					transaction.replace(R.id.container_verbnet, verbnetFragment);
				}

				if (Settings.getPropBankPref(context))
				{
					// final View labelView = findViewById(R.id.label_propbank);
					// labelView.setVisibility(View.VISIBLE);
					final Fragment propbankFragment = new PropBankFragment();
					propbankFragment.setArguments(args);
					transaction.replace(R.id.container_propbank, propbankFragment);
				}

				if (Settings.getFrameNetPref(context))
				{
					// final View labelView = findViewById(R.id.label_framenet);
					// labelView.setVisibility(View.VISIBLE);
					final Fragment framenetFragment = new FrameNetFragment();
					framenetFragment.setArguments(args);
					transaction.replace(R.id.container_framenet, framenetFragment);
				}

				if (Settings.getBncPref(context))
				{
					// final View labelView = findViewById(R.id.label_bnc);
					// labelView.setVisibility(View.VISIBLE);
					final Fragment bncFragment = new BNCFragment();
					bncFragment.setArguments(args);
					transaction.replace(R.id.container_bnc, bncFragment);
				}
				transaction.commit();
				break;

			case WEB:
				// fragment
				final Fragment fragment = new WebFragment();
				fragment.setArguments(args);

				// detail fragment replace
				getChildFragmentManager() //
						.beginTransaction() //
						.replace(R.id.container_web, fragment) //
						.commit();
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
