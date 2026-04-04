/*
 * Copyright (c) 2026. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser.xn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.sqlunet.browser.BaseBrowse2Fragment
import org.sqlunet.wordnet.browser.SensesFragment
import org.sqlunet.browser.common.R as CommonR

/**
 * Senses fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class BrowseSensesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(CommonR.layout.fragment_browse_senses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val manager = getChildFragmentManager()

        // selector fragment
        val sensesFragment = SensesFragment()
        sensesFragment.arguments = arguments

        manager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(CommonR.id.container_senses, sensesFragment, BaseBrowse2Fragment.FRAGMENT_TAG)
            // .addToBackStack(BaseBrowse2Fragment.FRAGMENT_TAG)
            .commit()
    }
}
