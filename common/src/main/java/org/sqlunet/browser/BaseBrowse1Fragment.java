/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.browser;

import android.os.Bundle;

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
	}
}
