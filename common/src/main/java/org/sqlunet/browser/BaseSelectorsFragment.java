package org.sqlunet.browser;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class BaseSelectorsFragment extends Fragment
{
	static private final String TAG = "SelectorsF";

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
	 */
	public BaseSelectorsFragment()
	{
		Log.d(TAG, "lifecycle: Constructor (0) " + this);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d(TAG, "lifecycle: onCreate (2) " + this);
		//noinspection deprecation
		this.setRetainInstance(false); // default
		this.setHasOptionsMenu(true);
		requireActivity().invalidateOptionsMenu();
	}

	// M E N U

	@Override
	public void onPrepareOptionsMenu(@NonNull final Menu menu)
	{
		super.onPrepareOptionsMenu(menu);
		MenuHandler.disableDataChange(menu);
	}
}
