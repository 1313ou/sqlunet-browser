/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.sn

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import org.sqlunet.browser.BaseBrowse1Fragment
import org.sqlunet.browser.BaseSearchFragment
import org.sqlunet.browser.BrowseSplashFragment
import org.sqlunet.browser.SplashFragment
import org.sqlunet.browser.config.TableActivity
import org.sqlunet.browser.history.History.recordQuery
import org.sqlunet.browser.sn.Settings.Selector.Companion.getPref
import org.sqlunet.browser.sn.Settings.getXSelectorPref
import org.sqlunet.browser.sn.selector.Browse1Activity
import org.sqlunet.browser.sn.selector.Browse1Fragment
import org.sqlunet.browser.sn.selector.SnBrowse1Fragment
import org.sqlunet.browser.sn.web.WebActivity
import org.sqlunet.browser.sn.web.WebFragment
import org.sqlunet.browser.sn.xselector.XBrowse1Activity
import org.sqlunet.browser.sn.xselector.XBrowse1Fragment
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.settings.Settings
import org.sqlunet.settings.Settings.DetailViewMode
import org.sqlunet.settings.Settings.SelectorViewMode
import org.sqlunet.syntagnet.SnCollocationPointer
import org.sqlunet.syntagnet.browser.CollocationActivity
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

    // S P I N N E R

    override fun acquireSpinner(spinner: Spinner) {
        // to set position
        super.acquireSpinner(spinner)
        spinner.visibility = View.VISIBLE

        // apply spinner adapter
        spinner.setAdapter(spinnerAdapter)

        // spinner listener
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long) {
                val selectorMode = org.sqlunet.browser.sn.Settings.Selector.entries.toTypedArray()[position]
                selectorMode.setPref(requireContext())
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                
            }
        }

        // saved selector mode
        val selectorMode = getPref(requireContext())
        spinner.setSelection(selectorMode.ordinal)
    }

    // M E N U

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // activity
        val context = requireContext()

        // intent
        val intent: Intent

        // handle item selection
        val itemId = item.itemId
        if (R.id.action_table_domains == itemId) {
            intent = Intent(context, TableActivity::class.java)
            intent.putExtra(ProviderArgs.ARG_QUERYURI, makeUri(Domains.URI))
            intent.putExtra(ProviderArgs.ARG_QUERYID, Domains.DOMAINID)
            intent.putExtra(ProviderArgs.ARG_QUERYITEMS, arrayOf(Domains.DOMAINID, Domains.DOMAIN, Domains.POSID))
            intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_table3)
        } else if (R.id.action_table_poses == itemId) {
            intent = Intent(context, TableActivity::class.java)
            intent.putExtra(ProviderArgs.ARG_QUERYURI, makeUri(Poses.URI))
            intent.putExtra(ProviderArgs.ARG_QUERYID, Poses.POSID)
            intent.putExtra(ProviderArgs.ARG_QUERYITEMS, arrayOf(Poses.POSID, Poses.POS))
            intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_table2)
        } else if (R.id.action_table_adjpositions == itemId) {
            intent = Intent(context, TableActivity::class.java)
            intent.putExtra(ProviderArgs.ARG_QUERYURI, makeUri(AdjPositions.URI))
            intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_table2)
        } else if (R.id.action_table_relations == itemId) {
            intent = Intent(context, TableActivity::class.java)
            intent.putExtra(ProviderArgs.ARG_QUERYURI, makeUri(Relations.URI))
            intent.putExtra(ProviderArgs.ARG_QUERYID, Relations.RELATIONID)
            intent.putExtra(ProviderArgs.ARG_QUERYITEMS, arrayOf(Relations.RELATIONID, Relations.RELATION, Relations.RECURSESSELECT))
            intent.putExtra(ProviderArgs.ARG_QUERYSORT, Relations.RELATIONID + " ASC")
            intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_table3)
        } else {
            return false
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
        recordQuery(requireContext(), trimmedQuery)

        // parameters
        val recurse = org.sqlunet.wordnet.settings.Settings.getRecursePref(requireContext())
        val parameters = org.sqlunet.wordnet.settings.Settings.getRenderParametersPref(requireContext())

        // menuDispatch as per query prefix
        var fragment: Fragment? = null
        var targetIntent: Intent? = null
        val args = Bundle()
        if (trimmedQuery.matches("#\\p{Lower}\\p{Lower}\\d+".toRegex())) {
            val id = trimmedQuery.substring(3).toLong()

            // wordnet
            if (trimmedQuery.startsWith("#ws")) {
                val synsetPointer: Parcelable = SynsetPointer(id)
                args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SYNSET)
                args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, synsetPointer)
                args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse)
                args.putBundle(ProviderArgs.ARG_RENDERPARAMETERS, parameters)
                targetIntent = makeDetailIntent(SynsetActivity::class.java)
            } else if (trimmedQuery.startsWith("#ww")) {
                val wordPointer: Parcelable = WordPointer(id)
                args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_WORD)
                args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, wordPointer)
                args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse)
                args.putBundle(ProviderArgs.ARG_RENDERPARAMETERS, parameters)
                targetIntent = makeDetailIntent(WordActivity::class.java)
            } else if (trimmedQuery.startsWith("#sc")) {
                val collocationPointer: Parcelable = SnCollocationPointer(id)
                args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_COLLOCATION)
                args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, collocationPointer)
                args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse)
                args.putBundle(ProviderArgs.ARG_RENDERPARAMETERS, parameters)
                targetIntent = makeDetailIntent(CollocationActivity::class.java)
            }
        }
        if (trimmedQuery.matches("#\\p{Lower}\\p{Lower}[\\w:%]+".toRegex())) {
            val id = trimmedQuery.substring(3)
            if (trimmedQuery.startsWith("#wk")) {
                val senseKeyPointer: Parcelable = SenseKeyPointer(id)
                args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SENSE)
                args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, senseKeyPointer)
                args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse)
                args.putBundle(ProviderArgs.ARG_RENDERPARAMETERS, parameters)
                targetIntent = makeDetailIntent(SenseKeyActivity::class.java)
            }
        } else {
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
    private fun makeOverviewFragment(): Fragment {
        // context
        val context = requireContext()

        // type
        val selectorType = getXSelectorPref(context)

        // mode
        val selectorMode: SelectorViewMode = Settings.getSelectorViewModePref(context)
        return when (selectorMode) {
            SelectorViewMode.VIEW -> when (selectorType) {
                org.sqlunet.browser.sn.Settings.Selector.SELECTOR -> Browse1Fragment()
                org.sqlunet.browser.sn.Settings.Selector.XSELECTOR -> XBrowse1Fragment()
                org.sqlunet.browser.sn.Settings.Selector.SELECTOR_ALT -> SnBrowse1Fragment()
            }

            SelectorViewMode.WEB -> WebFragment()
        }
    }

    /**
     * Make selector intent as per settings
     *
     * @return intent
     */
    private fun makeSelectorIntent(): Intent {

        // context
        val context = requireContext()

        // type
        val selectorType = getXSelectorPref(context)

        // mode
        val selectorMode: SelectorViewMode = Settings.getSelectorViewModePref(context)

        // intent
        val intent: Intent = when (selectorMode) {
            SelectorViewMode.VIEW -> {
                val intentClass: Class<*> =
                    when (selectorType) {
                        org.sqlunet.browser.sn.Settings.Selector.SELECTOR -> Browse1Activity::class.java
                        org.sqlunet.browser.sn.Settings.Selector.SELECTOR_ALT -> SnBrowse1Fragment::class.java
                        org.sqlunet.browser.sn.Settings.Selector.XSELECTOR -> XBrowse1Activity::class.java
                    }
                Intent(requireContext(), intentClass)
            }

            SelectorViewMode.WEB -> Intent(requireContext(), WebActivity::class.java)
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
        val detailMode: DetailViewMode = Settings.getDetailViewModePref(context)

        // intent
        val intent: Intent = when (detailMode) {
            DetailViewMode.VIEW -> Intent(context, intentClass)
            DetailViewMode.WEB -> Intent(context, WebActivity::class.java)
        }
        intent.setAction(ProviderArgs.ACTION_QUERY)
        return intent
    }

    companion object {
        private const val TAG = "BrowseF"
    }
}
