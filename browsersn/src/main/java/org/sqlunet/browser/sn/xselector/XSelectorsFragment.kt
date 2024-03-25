/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.sn.xselector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.sqlunet.browser.BaseSelectorsFragment
import org.sqlunet.browser.sn.R
import org.sqlunet.browser.sn.selector.CollocationSelectorPointer
import org.sqlunet.browser.sn.selector.SelectorPointer
import org.sqlunet.browser.sn.selector.SelectorsFragment
import org.sqlunet.browser.sn.selector.SnSelectorsFragment

/**
 * X selectors fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class XSelectorsFragment : BaseSelectorsFragment(), SelectorsFragment.Listener, SnSelectorsFragment.Listener {

    /**
     * Wn listener
     */
    private var wnListener: SelectorsFragment.Listener? = null

    /**
     * Sn collocation listener
     */
    private var snListener: SnSelectorsFragment.Listener? = null

    // V I E W

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_xselectors, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check that the activity is using the layout version with the fragment_container FrameLayout
        if (view.findViewById<View?>(R.id.wnselectors) != null) {
            // However, if we're being restored from a previous state, then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return
            }

            // Create a new Fragment to be placed in the activity layout
            val fragment = SelectorsFragment()

            // Pass the arguments
            fragment.setArguments(arguments)

            // Listeners
            fragment.setListeners(wnListener!!, this)
            getChildFragmentManager() 
                .beginTransaction() 
                .setReorderingAllowed(true) 
                .add(R.id.wnselectors, fragment, "wn$FRAGMENT_TAG") 
                .commit()
        }
        if (view.findViewById<View?>(R.id.snselectors) != null) {
            // However, if we're being restored from a previous state, then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return
            }

            // Create a new Fragment to be placed in the activity layout
            val fragment = SnSelectorsFragment()

            // Pass the arguments
            fragment.setArguments(arguments)

            // Listener
            fragment.setListeners(snListener!!, this)
            getChildFragmentManager() 
                .beginTransaction() 
                .setReorderingAllowed(true) 
                .add(R.id.snselectors, fragment, "sn$FRAGMENT_TAG") 
                .commit()
        }
    }

    // L I S T E N E R S

    /**
     * Set listener
     *
     * @param listener1 wordnet listener
     * @param listener2 syntagnet listener
     */
    fun setListener(listener1: SelectorsFragment.Listener?, listener2: SnSelectorsFragment.Listener?) {
        wnListener = listener1
        snListener = listener2
    }

    override fun onItemSelected(pointer: SelectorPointer?, word: String?, cased: String?, pronunciation: String?, pos: String?) {
        if (!isAdded) {
            return
        }
        val f = getChildFragmentManager().findFragmentByTag("sn$FRAGMENT_TAG") as SnSelectorsFragment?
        f?.deactivate()
    }

    override fun onItemSelected(pointer: CollocationSelectorPointer?) {
        if (!isAdded) {
            return
        }
        val f = getChildFragmentManager().findFragmentByTag("wn$FRAGMENT_TAG") as SelectorsFragment?
        f?.deactivate()
    }
}
