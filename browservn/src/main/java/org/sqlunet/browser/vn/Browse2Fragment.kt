/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.vn

import android.os.Bundle
import androidx.fragment.app.Fragment
import org.sqlunet.browser.AppContext
import org.sqlunet.browser.BaseBrowse2Fragment
import org.sqlunet.browser.vn.web.WebFragment
import org.sqlunet.browser.vn.xselector.XSelectorPointer
import org.sqlunet.browser.vn.xselector.XSelectorsFragment
import org.sqlunet.propbank.browser.PropBankFragment
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.settings.Settings
import org.sqlunet.verbnet.browser.VerbNetFragment
import org.sqlunet.wordnet.browser.SenseFragment

/**
 * A fragment representing a detail
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class Browse2Fragment : BaseBrowse2Fragment() {

    /**
     * Search
     */
    override fun search() {

        if (!isAdded) {
            return
        }
        val manager = getChildFragmentManager()

        // args
        val args = Bundle()
        args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer)

        // detail fragment
        val mode: Settings.DetailViewMode = Settings.getDetailViewModePref(AppContext.context)
        when (mode) {
            Settings.DetailViewMode.VIEW -> {
                var enable = VnSettings.getAllPref(AppContext.context)
                if (pointer is XSelectorPointer) {
                    // sections to disable
                    var mask = 0
                    val xpointer = pointer as XSelectorPointer?
                    val groupId = xpointer!!.xGroup
                    when (groupId) {
                        XSelectorsFragment.GROUPID_VERBNET -> mask = VnSettings.ENABLE_PROPBANK
                        XSelectorsFragment.GROUPID_PROPBANK -> mask = VnSettings.ENABLE_VERBNET or VnSettings.ENABLE_WORDNET
                    }
                    enable = enable and mask.inv()
                }

                // transaction
                val transaction = manager.beginTransaction().setReorderingAllowed(true)

                // verbnet
                if (enable and VnSettings.ENABLE_VERBNET != 0) {
                    // var labelView = findViewById(R.id.label_verbnet)
                    // labelView.setVisibility(View.VISIBLE)
                    val verbnetFragment: Fragment = VerbNetFragment()
                    verbnetFragment.setArguments(args)
                    transaction.replace(R.id.container_verbnet, verbnetFragment, VerbNetFragment.FRAGMENT_TAG)
                } else {
                    val verbnetFragment = manager.findFragmentByTag(VerbNetFragment.FRAGMENT_TAG)
                    if (verbnetFragment != null) {
                        transaction.remove(verbnetFragment)
                    }
                }

                // propbank
                if (enable and VnSettings.ENABLE_PROPBANK != 0) {
                    // var labelView = findViewById(R.id.label_propbank)
                    // labelView.setVisibility(View.VISIBLE)
                    val propbankFragment: Fragment = PropBankFragment()
                    propbankFragment.setArguments(args)
                    transaction.replace(R.id.container_propbank, propbankFragment, PropBankFragment.FRAGMENT_TAG)
                } else {
                    val propbankFragment = manager.findFragmentByTag(PropBankFragment.FRAGMENT_TAG)
                    if (propbankFragment != null) {
                        transaction.remove(propbankFragment)
                    }
                }

                // wordnet
                if (enable and VnSettings.ENABLE_WORDNET != 0) {
                    // var labelView = findViewById(R.id.label_wordnet)
                    // labelView.setVisibility(View.VISIBLE)
                    val senseFragment = SenseFragment()
                    senseFragment.setArguments(args)
                    transaction.replace(R.id.container_wordnet, senseFragment, SenseFragment.FRAGMENT_TAG)
                } else {
                    val senseFragment = manager.findFragmentByTag(SenseFragment.FRAGMENT_TAG)
                    if (senseFragment != null) {
                        transaction.remove(senseFragment)
                    }
                }
                transaction.commit()
            }

            Settings.DetailViewMode.WEB -> {
                // web fragment
                val webFragment: Fragment = WebFragment()
                webFragment.setArguments(args)

                // detail fragment replace
                manager.beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.container_data, webFragment, WebFragment.FRAGMENT_TAG)
                    .commit()
            }
        }
    }

    companion object {

        const val ARG_ALT = "alt_arg"
    }
}
