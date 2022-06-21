package org.sqlunet.browser;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BaseBrowse1Fragment extends Fragment
{
	@SuppressWarnings("unused")
	static private final String TAG = "Browse1F";

	@Override
	public void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
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
