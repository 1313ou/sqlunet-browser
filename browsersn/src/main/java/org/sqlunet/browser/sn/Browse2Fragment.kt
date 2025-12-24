/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.sn

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import org.sqlunet.bnc.browser.BNCFragment
import org.sqlunet.browser.AppContext
import org.sqlunet.browser.BaseBrowse2Fragment
import org.sqlunet.browser.sn.selector.CollocationSelectorPointer
import org.sqlunet.browser.sn.web.WebFragment
import org.sqlunet.browser.sn.xselector.XSelectorPointer
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.settings.Settings
import org.sqlunet.syntagnet.browser.SyntagNetFragment
import org.sqlunet.wordnet.browser.SenseFragment

/**
 * A fragment representing a detail
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class Browse2Fragment : BaseBrowse2Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        val alt = args != null && args.getBoolean(ARG_ALT)
        layoutId = if (alt) R.layout.fragment_browse2_multi_alt else R.layout.fragment_browse2_multi
    }

    /**
     * Search
     */
    override fun search() {

        if (!isAdded) {
            return
        }
        val manager = getChildFragmentManager()

        // args
        val recurse = org.sqlunet.wordnet.settings.Settings.getRecursePref(AppContext.context)
        val parameters = org.sqlunet.wordnet.settings.Settings.makeParametersPref(AppContext.context)
        val args = Bundle()
        args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer)
        args.putString(ProviderArgs.ARG_HINTPOS, pos)
        args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse)
        args.putBundle(ProviderArgs.ARG_RENDERPARAMETERS, parameters)

        var hasWordNet = true
        if (pointer is CollocationSelectorPointer) {
            val selectorPointer = pointer as CollocationSelectorPointer?
            hasWordNet = selectorPointer!!.getSynsetId() != -1L || selectorPointer.getSynset2Id() != -1L
        }

        // detail fragment
        val mode: Settings.DetailViewMode = Settings.getDetailViewModePref(AppContext.context)
        when (mode) {
            Settings.DetailViewMode.VIEW -> {
                val enable = SnSettings.getAllPref(AppContext.context)

                // transaction
                val transaction = manager.beginTransaction().setReorderingAllowed(true)

                // wordnet
                if (enable and SnSettings.ENABLE_WORDNET != 0 && hasWordNet) {
                    // var labelView = findViewById(R.id.label_wordnet)
                    // labelView.setVisibility(View.VISIBLE)
                    val senseFragment = SenseFragment()
                    senseFragment.setArguments(args)
                    senseFragment.setExpand(wordNetOnly(pointer))
                    transaction.replace(R.id.container_wordnet, senseFragment, SenseFragment.FRAGMENT_TAG)
                } else {
                    val senseFragment = manager.findFragmentByTag(SenseFragment.FRAGMENT_TAG)
                    if (senseFragment != null) {
                        transaction.remove(senseFragment)
                    }
                }

                // syntagnet
                if (enable and SnSettings.ENABLE_SYNTAGNET != 0) {
                    // var labelView = findViewById(R.id.label_syntagnet)
                    // labelView.setVisibility(View.VISIBLE)
                    val syntagNetFragment: Fragment = SyntagNetFragment()
                    syntagNetFragment.setArguments(args)
                    transaction.replace(R.id.container_syntagnet, syntagNetFragment, SyntagNetFragment.FRAGMENT_TAG)
                } else {
                    val collocationFragment = manager.findFragmentByTag(SyntagNetFragment.FRAGMENT_TAG)
                    if (collocationFragment != null) {
                        transaction.remove(collocationFragment)
                    }
                }

                // bnc
                if (enable and SnSettings.ENABLE_BNC != 0) {
                    // var labelView = findViewById(R.id.label_bnc)
                    // labelView.setVisibility(View.VISIBLE)
                    val bncFragment: Fragment = BNCFragment()
                    bncFragment.setArguments(args)
                    transaction.replace(R.id.container_bnc, bncFragment, BNCFragment.FRAGMENT_TAG)
                } else {
                    val bncFragment = manager.findFragmentByTag(BNCFragment.FRAGMENT_TAG)
                    if (bncFragment != null) {
                        transaction.remove(bncFragment)
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
                    .replace(R.id.container_web, webFragment, WebFragment.FRAGMENT_TAG)
                    .commit()
            }
        }
    }

    /**
     * Determine whether to expand
     *
     * @param pointer pointer
     * @return whether to expand
     */
    private fun wordNetOnly(pointer: Parcelable?): Boolean {
        if (pointer is XSelectorPointer) {
            return pointer.wordNetOnly()
        }
        return false
    }

    companion object {

        const val ARG_ALT = "alt_arg"
    }
}
