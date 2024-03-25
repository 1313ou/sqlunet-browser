/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.fn

import android.os.Bundle
import androidx.fragment.app.Fragment
import org.sqlunet.browser.BaseBrowse2Fragment
import org.sqlunet.browser.fn.web.WebFragment
import org.sqlunet.framenet.FnFramePointer
import org.sqlunet.framenet.browser.FnFrameFragment
import org.sqlunet.framenet.browser.FnLexUnitFragment
import org.sqlunet.framenet.browser.FrameNetFragment
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.settings.Settings

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

        val context = requireContext()
        if (!isAdded) {
            return
        }
        val manager = getChildFragmentManager()

        // args
        val args = Bundle()
        args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, pointer)

        // detail fragment
        val mode: Settings.DetailViewMode = Settings.getDetailViewModePref(context)
        when (mode) {
            Settings.DetailViewMode.VIEW -> {

                // transaction
                val transaction = manager.beginTransaction().setReorderingAllowed(true)

                // framenet
                val enable = org.sqlunet.browser.fn.Settings.getFrameNetPref(context)
                if (enable) {
                    // var labelView = findViewById(R.id.label_framenet)
                    // labelView.setVisibility(View.VISIBLE)
                    val framenetFragment: Fragment = if (pointer is FnFramePointer) FnFrameFragment() else FnLexUnitFragment()
                    framenetFragment.setArguments(args)
                    transaction.replace(R.id.container_framenet, framenetFragment, FrameNetFragment.FRAGMENT_TAG)
                } else {
                    val framenetFragment = manager.findFragmentByTag(FrameNetFragment.FRAGMENT_TAG)
                    if (framenetFragment != null) {
                        transaction.remove(framenetFragment)
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

    companion object {
        const val ARG_ALT = "alt_arg"
    }
}
