/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.fn

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import org.sqlunet.browser.AppContext
import org.sqlunet.browser.BaseBrowse1Fragment
import org.sqlunet.browser.BaseSearchFragment
import org.sqlunet.browser.BrowseSplashFragment
import org.sqlunet.browser.SplashFragment
import org.sqlunet.browser.fn.selector.Browse1Activity
import org.sqlunet.browser.fn.selector.Browse1Fragment
import org.sqlunet.browser.fn.web.WebActivity
import org.sqlunet.browser.fn.web.WebFragment
import org.sqlunet.browser.history.History.recordQuery
import org.sqlunet.framenet.FnAnnoSetPointer
import org.sqlunet.framenet.FnFramePointer
import org.sqlunet.framenet.FnLexUnitPointer
import org.sqlunet.framenet.FnPatternPointer
import org.sqlunet.framenet.FnSentencePointer
import org.sqlunet.framenet.FnValenceUnitPointer
import org.sqlunet.framenet.browser.FnAnnoSetActivity
import org.sqlunet.framenet.browser.FnFrameActivity
import org.sqlunet.framenet.browser.FnLexUnitActivity
import org.sqlunet.framenet.browser.FnSentenceActivity
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.settings.Settings
import org.sqlunet.settings.Settings.DetailViewMode
import org.sqlunet.settings.Settings.SelectorViewMode
import org.sqlunet.browser.common.R as CommonR
import android.R as AndroidR

/**
 * Browse fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class BrowseFragment : BaseSearchFragment() {

    init {
        layoutId = R.layout.fragment_browse
        menuId = CommonR.menu.browse
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

    // override fun onStop() {
    //     super.onStop()
    //     // remove data fragments and replace with splash before onSaveInstanceState takes place (between -3 and -4)
    //     beforeSaving(BrowseSplashFragment(), SplashFragment.FRAGMENT_TAG, R.id.container_browse, BaseBrowse1Fragment.FRAGMENT_TAG)
    // }

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
        recordQuery(AppContext.context, trimmedQuery)
        // menuDispatch as per query prefix
        var fragment: Fragment?
        var targetIntent: Intent? = null
        val args = Bundle()
        if (trimmedQuery.matches("#\\p{Lower}\\p{Lower}\\d+".toRegex())) {
            val id = trimmedQuery.substring(3).toLong()
            // framenet
            targetIntent = if (trimmedQuery.startsWith("#ff")) {
                val framePointer: Parcelable = FnFramePointer(id)
                args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNFRAME)
                args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, framePointer)
                makeDetailIntent(FnFrameActivity::class.java)
            } else if (trimmedQuery.startsWith("#fl")) {
                val lexunitPointer: Parcelable = FnLexUnitPointer(id)
                args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNLEXUNIT)
                args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, lexunitPointer)
                makeDetailIntent(FnLexUnitActivity::class.java)
            } else if (trimmedQuery.startsWith("#fs")) {
                val sentencePointer: Parcelable = FnSentencePointer(id)
                args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNSENTENCE)
                args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, sentencePointer)
                makeDetailIntent(FnSentenceActivity::class.java)
            } else if (trimmedQuery.startsWith("#fa")) {
                val annoSetPointer: Parcelable = FnAnnoSetPointer(id)
                args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNANNOSET)
                args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, annoSetPointer)
                makeDetailIntent(FnAnnoSetActivity::class.java)
            } else if (trimmedQuery.startsWith("#fp")) {
                val patternPointer: Parcelable = FnPatternPointer(id)
                args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNPATTERN)
                args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, patternPointer)
                makeDetailIntent(FnAnnoSetActivity::class.java)
            } else if (trimmedQuery.startsWith("#fv")) {
                val valenceunitPointer: Parcelable = FnValenceUnitPointer(id)
                args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNVALENCEUNIT)
                args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, valenceunitPointer)
                makeDetailIntent(FnAnnoSetActivity::class.java)
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
            fragment.setArguments(args)
            // fragment
            fragment.setArguments(args)
            // transaction
            if (!isAdded) {
                return
            }
            getChildFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.container_browse, fragment, BaseBrowse1Fragment.FRAGMENT_TAG)
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

        // type
        val selectorType = Settings.getSelectorPref(AppContext.context)
        // mode
        val selectorMode = Settings.getSelectorViewModePref(AppContext.context)
        when (selectorMode) {
            SelectorViewMode.VIEW -> if (selectorType == Settings.Selector.SELECTOR) {
                return Browse1Fragment()
            }

            SelectorViewMode.WEB -> return WebFragment()
        }
        return null
    }

    /**
     * Make selector intent as per settings
     *
     * @return intent
     */
    private fun makeSelectorIntent(): Intent {

        // intent
        var intent: Intent?
        // type
        val selectorType = Settings.getSelectorPref(AppContext.context)
        // mode
        val selectorMode = Settings.getSelectorViewModePref(AppContext.context)
        when (selectorMode) {
            SelectorViewMode.VIEW -> {
                var intentClass: Class<*>? = null
                if (selectorType == Settings.Selector.SELECTOR) {
                    intentClass = Browse1Activity::class.java
                }
                intent = Intent(AppContext.context, intentClass)
            }

            SelectorViewMode.WEB -> intent = Intent(AppContext.context, WebActivity::class.java)
        }
        intent.action = ProviderArgs.ACTION_QUERY
        return intent
    }

    /**
     * Make detail intent as per settings
     *
     * @param intentClass intent class if WebActivity is not to be used
     * @return intent
     */
    private fun makeDetailIntent(intentClass: Class<*>): Intent {

        // mode
        val detailMode = Settings.getDetailViewModePref(AppContext.context)
        // intent
        val intent: Intent = when (detailMode) {
            DetailViewMode.VIEW -> Intent(AppContext.context, intentClass)
            DetailViewMode.WEB -> Intent(AppContext.context, WebActivity::class.java)
        }
        intent.action = ProviderArgs.ACTION_QUERY
        return intent
    }

    companion object {

        private const val TAG = "BrowseF"
    }
}
