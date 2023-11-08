/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser;

import android.util.Log;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentUtils
{
	private static final String TAG = "FragmentUtils";

	/**
	 * Remove children fragments with tags and insert given fragment with at given location
	 *
	 * @param manager           fragment manager
	 * @param fragment          new fragment to insert
	 * @param tag               new fragment's tag
	 * @param where             new fragment's location
	 * @param childFragmentTags removed children's tags
	 * @noinspection SameParameterValue
	 */
	public static void removeAllChildFragment(@NonNull final FragmentManager manager, @NonNull final Fragment fragment, @Nullable final String tag, @IdRes final int where, @Nullable final String... childFragmentTags)
	{
		Log.d(TAG, "Removing fragments " + Arrays.toString(childFragmentTags));
		if (childFragmentTags != null && childFragmentTags.length > 0)
		{
			List<Fragment> childFragments = manager.getFragments();
			if (!childFragments.isEmpty())
			{
				FragmentTransaction transaction = manager.beginTransaction();
				for (Fragment childFragment : childFragments)
				{
					if (childFragment != null)
					{
						for (final String childFragmentTag : childFragmentTags)
						{
							if (childFragmentTag.equals(childFragment.getTag()))
							{
								transaction.remove(childFragment);
							}
						}
					}
				}
				transaction.replace(where, fragment, tag);
				transaction.commitAllowingStateLoss();
			}
		}
	}

	// B A C K S T A C K   H E L P E R

	/**
	 * Dump back stack for a fragment's manager
	 *
	 * @param manager fragment manager
	 * @param type    fragment manager type (child or parent)
	 * @return dump string
	 */
	@NonNull
	public static String dumpBackStack(@NonNull final FragmentManager manager, @NonNull final String type)
	{
		StringBuilder sb = new StringBuilder();
		int count = manager.getBackStackEntryCount();
		for (int i = 0; i < count; i++)
		{
			FragmentManager.BackStackEntry entry = manager.getBackStackEntryAt(i);
			sb.append("BackStack: ") //
					.append(type) //
					.append('\n') //
					.append("[") //
					.append(i) //
					.append("]: ") //
					.append(entry.getName()) //
					.append(' ') //
					.append(entry.getId()) //
					.append(' ') //
					.append(entry.getClass().getName());
		}
		return sb.toString();
	}
}
