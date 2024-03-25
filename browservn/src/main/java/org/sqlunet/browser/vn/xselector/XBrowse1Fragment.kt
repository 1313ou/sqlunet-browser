/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.vn.xselector

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.sqlunet.browser.BaseBrowse1Fragment
import org.sqlunet.browser.BaseBrowse2Fragment
import org.sqlunet.browser.BaseSelectorsFragment
import org.sqlunet.browser.Selectors
import org.sqlunet.browser.vn.Browse2Activity
import org.sqlunet.browser.vn.Browse2Fragment
import org.sqlunet.browser.vn.R
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.settings.Settings.Companion.getPaneLayout
import org.sqlunet.wordnet.settings.Settings.getRecursePref
import org.sqlunet.wordnet.settings.Settings.getRenderParametersPref

/**
 * X selector fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class XBrowse1Fragment : BaseBrowse1Fragment(), XSelectorsFragment.Listener {

    // C R E A T I O N

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getPaneLayout(R.layout.fragment_xbrowse_first, R.layout.fragment_xbrowse1, R.layout.fragment_xbrowse1_browse2), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isTwoPane = isTwoPane(view)
        val manager = getChildFragmentManager()

        // x selector fragment
        // transaction on selectors pane
        var xSelectorsFragment = manager.findFragmentByTag(BaseSelectorsFragment.FRAGMENT_TAG) as XSelectorsFragment?
        if (xSelectorsFragment == null) {
            xSelectorsFragment = XSelectorsFragment()
            xSelectorsFragment.setArguments(arguments)
        }
        var args1 = xSelectorsFragment.arguments
        if (args1 == null) {
            args1 = Bundle()
        }
        args1.putBoolean(Selectors.IS_TWO_PANE, isTwoPane)
        xSelectorsFragment.setListener(this)
        manager.beginTransaction() //
            .replace(R.id.container_xselectors, xSelectorsFragment, BaseSelectorsFragment.FRAGMENT_TAG) //
            // .addToBackStack(BaseSelectorsFragment.FRAGMENT_TAG) //
            .commit()

        // two-pane specific set up
        if (isTwoPane) {
            // in two-pane mode, list items should be given the 'activated' state when touched.
            xSelectorsFragment.setActivateOnItemClick(true)

            // detail fragment (rigid layout)
            var browse2Fragment: Fragment
            // browse2Fragment = manager.findFragmentByTag(BaseBrowse2Fragment.FRAGMENT_TAG);
            // if (browse2Fragment == null)
            run {
                browse2Fragment = Browse2Fragment()
                val args2 = Bundle()
                args2.putBoolean(Browse2Fragment.ARG_ALT, false)
                browse2Fragment.setArguments(args2)
            }
            manager.beginTransaction() //
                .setReorderingAllowed(true) //
                .replace(R.id.container_browse2, browse2Fragment, BaseBrowse2Fragment.FRAGMENT_TAG) //
                // .addToBackStack(BaseBrowse2Fragment.FRAGMENT_TAG) //
                .commit()
        }
    }

    // I T E M S E L E C T I O N H A N D L I N G

    /**
     * Callback method indicating that the item with the given ID was selected.
     */
    override fun onItemSelected(pointer: XSelectorPointer?, word: String?, cased: String?, pronunciation: String?, pos: String?) {
        val view = requireView()
        if (isTwoPane(view)) {
            // in two-pane mode, show the detail view in this activity by adding or replacing the detail fragment using a fragment transaction.
            if (!isAdded) {
                return
            }
            val fragment = (getChildFragmentManager().findFragmentById(R.id.container_browse2) as Browse2Fragment?)!!
            fragment.search(pointer, word, cased, pronunciation, pos)
        } else {
            // in single-pane mode, simply start the detail activity for the selected item ID.
            val recurse = getRecursePref(requireContext())
            val parameters = getRenderParametersPref(requireContext())
            val args = Bundle()
            args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer)
            args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse)
            args.putBundle(ProviderArgs.ARG_RENDERPARAMETERS, parameters)
            args.putString(ProviderArgs.ARG_HINTWORD, word)
            args.putString(ProviderArgs.ARG_HINTCASED, cased)
            args.putString(ProviderArgs.ARG_HINTPRONUNCIATION, pronunciation)
            args.putString(ProviderArgs.ARG_HINTPOS, pos)
            val intent = Intent(requireContext(), Browse2Activity::class.java)
            intent.putExtras(args)
            startActivity(intent)
        }
    }

    // V I E W   D E T E C T I O N

    /**
     * Whether view is two-pane
     *
     * @param view view
     * @return true if view is two-pane
     */
    private fun isTwoPane(view: View): Boolean {
        // the detail view will be present only in the large-screen layouts
        // if this view is present, then the activity should be in two-pane mode.
        return view.findViewById<View?>(R.id.detail) != null
    }
}
