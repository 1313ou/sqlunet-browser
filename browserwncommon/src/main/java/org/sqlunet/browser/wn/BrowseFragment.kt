/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.wn

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
import org.sqlunet.browser.config.TableActivity
import org.sqlunet.browser.history.History.recordQuery
import org.sqlunet.browser.wn.lib.R
import org.sqlunet.browser.wn.selector.Browse1Activity
import org.sqlunet.browser.wn.selector.Browse1Fragment
import org.sqlunet.browser.wn.web.WebActivity
import org.sqlunet.browser.wn.web.WebFragment
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.settings.Settings
import org.sqlunet.settings.Settings.DetailViewMode
import org.sqlunet.settings.Settings.SelectorViewMode
import org.sqlunet.wordnet.SenseKeyPointer
import org.sqlunet.wordnet.SynsetPointer
import org.sqlunet.wordnet.WordPointer
import org.sqlunet.wordnet.browser.SenseKeyActivity
import org.sqlunet.wordnet.browser.SynsetActivity
import org.sqlunet.wordnet.browser.WordActivity
import org.sqlunet.wordnet.provider.WordNetContract.AdjPositions
import org.sqlunet.wordnet.provider.WordNetContract.Domains
import org.sqlunet.wordnet.provider.WordNetContract.Poses
import org.sqlunet.wordnet.provider.WordNetContract.Relations
import org.sqlunet.wordnet.provider.WordNetProvider.Companion.makeUri
import android.R as AndroidR
import org.sqlunet.browser.common.R as CommonR

/**
 * Browse fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class BrowseFragment : BaseSearchFragment() {

    // C R E A T I O N

    init {
        layoutId = R.layout.fragment_browse
        menuId = R.menu.browse
        colorAttrId = AndroidR.attr.colorPrimary
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

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // intent
        val intent: Intent

        // handle item selection
        when (item.itemId) {
            R.id.action_table_domains -> {
                intent = Intent(AppContext.context, TableActivity::class.java)
                intent.putExtra(ProviderArgs.ARG_QUERYURI, makeUri(Domains.URI))
                intent.putExtra(ProviderArgs.ARG_QUERYID, Domains.DOMAINID)
                intent.putExtra(ProviderArgs.ARG_QUERYITEMS, arrayOf(Domains.DOMAINID, Domains.DOMAIN, Domains.POSID))
                intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, CommonR.layout.item_table3)
            }

            R.id.action_table_poses -> {
                intent = Intent(AppContext.context, TableActivity::class.java)
                intent.putExtra(ProviderArgs.ARG_QUERYURI, makeUri(Poses.URI))
                intent.putExtra(ProviderArgs.ARG_QUERYID, Poses.POSID)
                intent.putExtra(ProviderArgs.ARG_QUERYITEMS, arrayOf(Poses.POSID, Poses.POS))
                intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, CommonR.layout.item_table2)
            }

            R.id.action_table_adjpositions -> {
                intent = Intent(AppContext.context, TableActivity::class.java)
                intent.putExtra(ProviderArgs.ARG_QUERYURI, makeUri(AdjPositions.URI))
                intent.putExtra(ProviderArgs.ARG_QUERYID, AdjPositions.POSITIONID)
                intent.putExtra(ProviderArgs.ARG_QUERYITEMS, arrayOf(AdjPositions.POSITIONID, AdjPositions.POSITION))
                intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, CommonR.layout.item_table2)
            }

            R.id.action_table_relations -> {
                intent = Intent(AppContext.context, TableActivity::class.java)
                intent.putExtra(ProviderArgs.ARG_QUERYURI, makeUri(Relations.URI))
                intent.putExtra(ProviderArgs.ARG_QUERYID, Relations.RELATIONID)
                intent.putExtra(ProviderArgs.ARG_QUERYITEMS, arrayOf(Relations.RELATIONID, Relations.RELATION, Relations.RECURSES_SELECT))
                intent.putExtra(ProviderArgs.ARG_QUERYSORT, Relations.RELATIONID + " ASC")
                intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, CommonR.layout.item_table3)
            }

            else -> {
                return false
            }
        }

        // start activity
        startActivity(intent)
        return true
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
        var fragment: Fragment? = null
        var targetIntent: Intent? = null
        val args = Bundle()
        if (trimmedQuery.matches("#\\p{Lower}\\p{Lower}\\d+".toRegex())) {
            val id = trimmedQuery.substring(3).toLong()

            // wordnet
            targetIntent = if (trimmedQuery.startsWith("#ws")) {
                // parameters
                val recurse = org.sqlunet.wordnet.settings.Settings.getRecursePref(AppContext.context)
                val parameters = org.sqlunet.wordnet.settings.Settings.makeParametersPref(AppContext.context)
                val synsetPointer: Parcelable = SynsetPointer(id)
                args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SYNSET)
                args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, synsetPointer)
                args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse)
                args.putBundle(ProviderArgs.ARG_RENDERPARAMETERS, parameters)
                makeDetailIntent(SynsetActivity::class.java)
            } else if (trimmedQuery.startsWith("#ww")) {
                val wordPointer: Parcelable = WordPointer(id)
                args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_WORD)
                args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, wordPointer)
                makeDetailIntent(WordActivity::class.java)
            } else {
                return
            }
        }
        if (trimmedQuery.matches("#\\p{Lower}\\p{Lower}[\\w:%]+".toRegex())) {
            val id = trimmedQuery.substring(3)
            if (trimmedQuery.startsWith("#wk")) {
                // parameters
                val recurse = org.sqlunet.wordnet.settings.Settings.getRecursePref(AppContext.context)
                val parameters = org.sqlunet.wordnet.settings.Settings.makeParametersPref(AppContext.context)
                val senseKeyPointer: Parcelable = SenseKeyPointer(id)
                args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SENSE)
                args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, senseKeyPointer)
                args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse)
                args.putBundle(ProviderArgs.ARG_RENDERPARAMETERS, parameters)
                targetIntent = makeDetailIntent(SenseKeyActivity::class.java)
            }
        } else {
            // parameters
            val recurse = org.sqlunet.wordnet.settings.Settings.getRecursePref(AppContext.context)
            val parameters = org.sqlunet.wordnet.settings.Settings.makeParametersPref(AppContext.context)

            // search for string
            args.putString(ProviderArgs.ARG_QUERYSTRING, trimmedQuery)
            args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse)
            args.putBundle(ProviderArgs.ARG_RENDERPARAMETERS, parameters)

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
                .addToBackStack(BaseBrowse1Fragment.FRAGMENT_TAG)
                .commit()
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
        val selectorType: Settings.Selector = Settings.getSelectorPref(AppContext.context)

        // mode
        val selectorMode: SelectorViewMode = Settings.getSelectorViewModePref(AppContext.context)
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
        val selectorType: Settings.Selector = Settings.getSelectorPref(AppContext.context)

        // mode
        val selectorMode: SelectorViewMode = Settings.getSelectorViewModePref(AppContext.context)
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
        val detailMode: DetailViewMode = Settings.getDetailViewModePref(AppContext.context)
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
