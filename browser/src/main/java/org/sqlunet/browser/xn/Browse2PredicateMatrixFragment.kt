/*
 * Copyright (c) 2026. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.xn

import android.os.Bundle
import androidx.fragment.app.Fragment
import org.sqlunet.browser.BaseBrowse2Fragment
import org.sqlunet.browser.R
import org.sqlunet.predicatematrix.browser.PredicateMatrixFragment

class Browse2PredicateMatrixFragment : BaseBrowse2Fragment() {

    private var args: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args = arguments
        layoutId = R.layout.fragment_browse2_predicatematrix
    }

    override fun onStart() {
        super.onStart()
        search()
    }

    override fun search() {

        if (!isAdded) {
            return
        }

         // detail fragment
        val fragment: Fragment = PredicateMatrixFragment()
        fragment.arguments = args

        // transaction
        childFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.container_browse_extra, fragment, PredicateMatrixFragment.FRAGMENT_TAG)
            .commit()
    }

    companion object {

        const val FRAGMENT_TAG = "browse2_predicatematrix"
    }
}
