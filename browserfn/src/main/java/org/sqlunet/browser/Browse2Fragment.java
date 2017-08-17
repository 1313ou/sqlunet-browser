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

import org.sqlunet.browser.fn.R;
import org.sqlunet.browser.web.WebFragment;
import org.sqlunet.framenet.FnFramePointer;
import org.sqlunet.framenet.browser.FnFrameFragment;
import org.sqlunet.framenet.browser.FnLexUnitFragment;
import org.sqlunet.provider.ProviderArgs;
import org.sqlunet.settings.Settings;

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
		// retain instance
		setRetainInstance(true);

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
		final FragmentManager manager = getChildFragmentManager();

		// args
		final Bundle args = new Bundle();
		args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, this.pointer);

		// detail fragment
		final Settings.DetailViewMode mode = Settings.getDetailViewModePref(context);
		switch (mode)
		{
			case VIEW:
				boolean enable = true;

				// transaction
				final FragmentTransaction transaction = manager.beginTransaction();

				// framenet
				if (enable)
				{
					// final View labelView = findViewById(R.id.label_framenet);
					// labelView.setVisibility(View.VISIBLE);
					final Fragment framenetFragment = (this.pointer instanceof FnFramePointer) ? new FnFrameFragment() : new FnLexUnitFragment();
					framenetFragment.setArguments(args);
					transaction.replace(R.id.container_framenet, framenetFragment, "framenet");
				}
				else
				{
					final Fragment framenetFragment = manager.findFragmentByTag("framenet");
					if (framenetFragment != null)
					{
						transaction.remove(framenetFragment);
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
