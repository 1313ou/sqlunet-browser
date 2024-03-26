/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.vn

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import org.sqlunet.browser.BaseBrowse1Fragment
import org.sqlunet.browser.BaseSearchFragment
import org.sqlunet.browser.BrowseSplashFragment
import org.sqlunet.browser.SplashFragment
import org.sqlunet.browser.history.History.recordQuery
import org.sqlunet.browser.vn.Settings.getXSelectorPref
import org.sqlunet.browser.vn.web.WebActivity
import org.sqlunet.browser.vn.web.WebFragment
import org.sqlunet.browser.vn.xselector.XBrowse1Activity
import org.sqlunet.browser.vn.xselector.XBrowse1Fragment
import org.sqlunet.propbank.PbRoleSetPointer
import org.sqlunet.propbank.browser.PbRoleSetActivity
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.settings.Settings
import org.sqlunet.verbnet.VnClassPointer
import org.sqlunet.verbnet.browser.VnClassActivity
import org.sqlunet.wordnet.SynsetPointer
import org.sqlunet.wordnet.WordPointer
import org.sqlunet.wordnet.browser.SynsetActivity
import org.sqlunet.wordnet.browser.WordActivity

/**
 * Browse fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class BrowseFragment : BaseSearchFragment() {

    init {
        layoutId = R.layout.fragment_browse
        menuId = R.menu.browse
        colorAttrId = R.attr.colorPrimary
        spinnerLabels = R.array.selectors_names
        spinnerIcons = R.array.selectors_icons
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            // splash fragment
            val fragment: Fragment = BrowseSplashFragment()
            getChildFragmentManager() 
                .beginTransaction() 
                .setReorderingAllowed(true) 
                .replace(R.id.container_browse, fragment, SplashFragment.FRAGMENT_TAG) 
                //.addToBackStack(SplashFragment.FRAGMENT_TAG) 
                .commit()
        }
    }

    override fun onStop() {
        super.onStop()

        // remove data fragments and replace with splash before onSaveInstanceState takes place (between -3 and -4)
        beforeSaving(BrowseSplashFragment(), SplashFragment.FRAGMENT_TAG, R.id.container_browse, BaseBrowse1Fragment.FRAGMENT_TAG)
    }

    // M E N U

    @Deprecated("Deprecated in Java", ReplaceWith("Add a MenuHost"))
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return false
    }

    // S E A R C H

    /**
     * Handle search
     *
     * @param query query
     */
    override fun search(query: String) {
        val trimmedQuery = query.trim { it <= ' ' }
        if (trimmedQuery.isEmpty()) {
            return
        }

        // super
        super.search(trimmedQuery)

        // log
        Log.d(TAG, "Browse '$trimmedQuery'")

        // history
        recordQuery(requireContext(), trimmedQuery)

        // menuDispatch as per query prefix
        var fragment: Fragment?
        var targetIntent: Intent? = null
        val args = Bundle()
        if (trimmedQuery.matches("#\\p{Lower}\\p{Lower}\\d+".toRegex())) {
            val id = trimmedQuery.substring(3).toLong()

            // parameters
            val parameters = org.sqlunet.wordnet.settings.Settings.getRenderParametersPref(requireContext())

            // wordnet
            targetIntent = if (trimmedQuery.startsWith("#ws")) {
                val synsetPointer: Parcelable = SynsetPointer(id)
                args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SYNSET)
                args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, synsetPointer)
                args.putInt(ProviderArgs.ARG_QUERYRECURSE, 0)
                args.putBundle(ProviderArgs.ARG_RENDERPARAMETERS, parameters)
                makeDetailIntent(SynsetActivity::class.java)
            } else if (trimmedQuery.startsWith("#ww")) {
                val wordPointer: Parcelable = WordPointer(id)
                args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_WORD)
                args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, wordPointer)
                args.putInt(ProviderArgs.ARG_QUERYRECURSE, 0)
                args.putBundle(ProviderArgs.ARG_RENDERPARAMETERS, parameters)
                makeDetailIntent(WordActivity::class.java)
            } else if (trimmedQuery.startsWith("#vc")) {
                val framePointer: Parcelable = VnClassPointer(id)
                args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_VNCLASS)
                args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, framePointer)
                makeDetailIntent(VnClassActivity::class.java)
            } else if (trimmedQuery.startsWith("#pr")) {
                val roleSetPointer: Parcelable = PbRoleSetPointer(id)
                args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_PBROLESET)
                args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, roleSetPointer)
                makeDetailIntent(PbRoleSetActivity::class.java)
            } else {
                return
            }
        }
        run {

            // search for string
            args.putString(ProviderArgs.ARG_QUERYSTRING, trimmedQuery)

            //targetIntent = makeSelectorIntent()
            fragment = makeOverviewFragment()
        }

        // menuDispatch
        Log.d(TAG, "Search $args")
        if (targetIntent != null) {
            targetIntent.putExtras(args)
            startActivity(targetIntent)
            return
        }
        if (fragment != null) {
            fragment!!.setArguments(args)

            // fragment
            fragment!!.setArguments(args)

            // transaction
            if (!isAdded) {
                return
            }
            getChildFragmentManager() 
                .beginTransaction() 
                .setReorderingAllowed(true) 
                .replace(R.id.container_browse, fragment!!, BaseBrowse1Fragment.FRAGMENT_TAG) 
                .addToBackStack(BaseBrowse1Fragment.FRAGMENT_TAG).commit()
        }
    }

    override fun triggerFocusSearch(): Boolean {
        if (!isAdded) {
            return false
        }
        val active = getChildFragmentManager().findFragmentById(R.id.container_browse)
        return active != null && SplashFragment.FRAGMENT_TAG == active.tag
    }

    // I N T E N T / F R A G M E N T   F A C T O R Y

    /**
     * Browse1/Web fragment factory
     *
     * @return fragment
     */
    private fun makeOverviewFragment(): Fragment? {
        // context
        val context = requireContext()

        // type
        val selectorType = getXSelectorPref(context)

        // mode
        val selectorMode: Settings.SelectorViewMode = Settings.getSelectorViewModePref(context)
        when (selectorMode) {
            Settings.SelectorViewMode.VIEW -> if (selectorType == org.sqlunet.browser.vn.Settings.Selector.XSELECTOR) {
                return XBrowse1Fragment()
            }

            Settings.SelectorViewMode.WEB -> return WebFragment()
        }
        return null
    }

    /**
     * Make selector intent as per settings
     *
     * @return intent
     */
    private fun makeSelectorIntent(): Intent {
        // context
        val context = requireContext()

        // intent
        var intent: Intent? = null

        // type
        val selectorType = getXSelectorPref(context)

        // mode
        val selectorMode: Settings.SelectorViewMode = Settings.getSelectorViewModePref(context)
        when (selectorMode) {
            Settings.SelectorViewMode.VIEW -> {
                var intentClass: Class<*>? = null
                if (selectorType == org.sqlunet.browser.vn.Settings.Selector.XSELECTOR) {
                    intentClass = XBrowse1Activity::class.java
                }
                intent = Intent(requireContext(), intentClass)
            }

            Settings.SelectorViewMode.WEB -> intent = Intent(requireContext(), WebActivity::class.java)
        }
        intent.setAction(ProviderArgs.ACTION_QUERY)
        return intent
    }

    /**
     * Make detail intent as per settings
     *
     * @param intentClass intent class if WebActivity is not to be used
     * @return intent
     */
    private fun makeDetailIntent(intentClass: Class<*>): Intent {
        // context
        val context = requireContext()

        // mode
        val detailMode: Settings.DetailViewMode = Settings.getDetailViewModePref(context)

        // intent
        val intent: Intent = when (detailMode) {
            Settings.DetailViewMode.VIEW -> Intent(context, intentClass)
            Settings.DetailViewMode.WEB -> Intent(context, WebActivity::class.java)
        }
        intent.setAction(ProviderArgs.ACTION_QUERY)
        return intent
    }

    companion object {
        private const val TAG = "BrowseF"
    }
}
