package org.sqlunet.browser;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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

/**
 * A fragment representing a detail
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Browse2Fragment extends Fragment
{
	static private final String TAG = "Browse2Fragment";

	static private final String POINTER_STATE = "pointer";

	private Parcelable pointer = null;

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
		//TODO
		this.setRetainInstance(true);

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

		if (savedInstanceState != null)
		{
			Log.d(TAG, "restore instance state " + this);
			this.pointer = savedInstanceState.getParcelable(POINTER_STATE);
		}

		return view;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if (this.pointer != null)
		{
			search();
		}
	}

	//TODO
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		Log.d(TAG, "save instance state " + this);
		super.onSaveInstanceState(outState);
		outState.putParcelable(POINTER_STATE, this.pointer);
	}

	/**
	 * Search
	 *
	 * @param pointer pointer
	 */
	public void search(final Parcelable pointer)
	{
		this.pointer = pointer;
		search();
	}

	/**
	 * Search
	 */
	private void search()
	{
		final Context context = this.getActivity();

		// args
		final Bundle args = new Bundle();
		args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, this.pointer);

		// detail fragment
		final Settings.DetailViewMode mode = Settings.getDetailViewModePref(context);
		switch (mode)
		{
			case VIEW:
				final FragmentManager manager = getChildFragmentManager();
				final FragmentTransaction transaction = manager.beginTransaction();
				if (Settings.getWordNetPref(context))
				{
					// final View labelView = findViewById(R.id.label_wordnet);
					// labelView.setVisibility(View.VISIBLE);
					Fragment senseFragment = manager.findFragmentByTag("wordnet");
					if (senseFragment == null)
					{
						final SenseFragment senseFragment2 = new SenseFragment();
						senseFragment2.setArguments(args);
						senseFragment2.setExpand(wordNetOnly(this.pointer));
						senseFragment = senseFragment2;
					}
					transaction.replace(R.id.container_wordnet, senseFragment, "wordnet");
				}

				if (Settings.getVerbNetPref(context))
				{
					// final View labelView = findViewById(R.id.label_verbnet);
					// labelView.setVisibility(View.VISIBLE);
					Fragment verbnetFragment = manager.findFragmentByTag("verbnet");
					if (verbnetFragment == null)
					{
						verbnetFragment = new VerbNetFragment();
						verbnetFragment.setArguments(args);
					}
					transaction.replace(R.id.container_verbnet, verbnetFragment, "verbnet");
				}

				if (Settings.getPropBankPref(context))
				{
					// final View labelView = findViewById(R.id.label_propbank);
					// labelView.setVisibility(View.VISIBLE);
					Fragment propbankFragment = manager.findFragmentByTag("propbank");
					if (propbankFragment == null)
					{
						propbankFragment = new PropBankFragment();
						propbankFragment.setArguments(args);
					}
					transaction.replace(R.id.container_propbank, propbankFragment, "propbank");
				}

				if (Settings.getFrameNetPref(context))
				{
					// final View labelView = findViewById(R.id.label_framenet);
					// labelView.setVisibility(View.VISIBLE);
					Fragment framenetFragment = manager.findFragmentByTag("framenet");
					if (framenetFragment == null)
					{
						framenetFragment = new FrameNetFragment();
						framenetFragment.setArguments(args);
					}
					transaction.replace(R.id.container_framenet, framenetFragment, "framenet");
				}

				if (Settings.getBncPref(context))
				{
					// final View labelView = findViewById(R.id.label_bnc);
					// labelView.setVisibility(View.VISIBLE);
					Fragment bncFragment = manager.findFragmentByTag("bnc");
					if (bncFragment == null)
					{
						bncFragment = new BNCFragment();
						bncFragment.setArguments(args);
					}
					transaction.replace(R.id.container_bnc, bncFragment, "bnc");
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
