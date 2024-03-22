/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser

import android.util.Log
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

object FragmentUtils {
    private const val TAG = "FragmentUtils"

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
    fun removeAllChildFragment(manager: FragmentManager, fragment: Fragment, tag: String?, @IdRes where: Int, vararg childFragmentTags: String?) {
        Log.d(TAG, "Removing fragments " + childFragmentTags.contentToString())
        if (childFragmentTags.isNotEmpty()) {
            val childFragments = manager.fragments
            if (childFragments.isNotEmpty()) {
                val transaction = manager.beginTransaction()
                for (childFragment in childFragments) {
                    if (childFragment != null) {
                        for (childFragmentTag in childFragmentTags) {
                            if (childFragmentTag == childFragment.tag) {
                                transaction.remove(childFragment)
                            }
                        }
                    }
                }
                transaction.replace(where, fragment, tag)
                transaction.commitAllowingStateLoss()
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
    fun dumpBackStack(manager: FragmentManager, type: String): String {
        val sb = StringBuilder()
        val count = manager.backStackEntryCount
        for (i in 0 until count) {
            val entry = manager.getBackStackEntryAt(i)
            sb.append("BackStack: ") //
                .append(type) //
                .append('\n') //
                .append("[") //
                .append(i) //
                .append("]: ") //
                .append(entry.name) //
                .append(' ') //
                .append(entry.id) //
                .append(' ') //
                .append(entry.javaClass.getName())
        }
        return sb.toString()
    }
}
