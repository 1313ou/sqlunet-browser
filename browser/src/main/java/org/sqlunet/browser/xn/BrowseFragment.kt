/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.xn

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
import org.sqlunet.browser.R
import org.sqlunet.browser.SplashFragment
import org.sqlunet.browser.config.TableActivity
import org.sqlunet.browser.history.History.recordQuery
import org.sqlunet.browser.xn.selector.Browse1Activity
import org.sqlunet.browser.xn.selector.Browse1Fragment
import org.sqlunet.browser.xn.web.WebActivity
import org.sqlunet.browser.xn.web.WebFragment
import org.sqlunet.browser.xn.xselector.XBrowse1Activity
import org.sqlunet.browser.xn.xselector.XBrowse1Fragment
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
import org.sqlunet.predicatematrix.PmRolePointer
import org.sqlunet.propbank.PbRoleSetPointer
import org.sqlunet.propbank.browser.PbRoleSetActivity
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.settings.Settings
import org.sqlunet.verbnet.VnClassPointer
import org.sqlunet.verbnet.browser.VnClassActivity
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

    // override fun onStop() {
    //     super.onStop()
    //     // remove data fragments and replace with splash before onSaveInstanceState takes place (between -3 and -4)
    //     beforeSaving(BrowseSplashFragment(), SplashFragment.FRAGMENT_TAG, R.id.container_browse, BaseBrowse1Fragment.FRAGMENT_TAG)
    // }

    // S P I N N E R

    override fun acquireSpinner(spinner: Spinner) {
        // to set position
        super.acquireSpinner(spinner)

        // visible
        spinner.visibility = View.VISIBLE

        // apply spinner adapter
        spinner.setAdapter(spinnerAdapter)

        // spinner listener
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long) {
                val selectorMode: Settings.Selector = Settings.Selector.entries.toTypedArray()[position]
                selectorMode.setPref(requireContext())
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }

        // saved selector mode
        val selectorMode = Settings.Selector.getPref(requireContext())
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
            intent.putExtra(ProviderArgs.ARG_QUERYID, AdjPositions.POSITIONID)
            intent.putExtra(ProviderArgs.ARG_QUERYITEMS, arrayOf(AdjPositions.POSITIONID, AdjPositions.POSITION))
            intent.putExtra(ProviderArgs.ARG_QUERYLAYOUT, R.layout.item_table2)
        } else if (R.id.action_table_relations == itemId) {
            intent = Intent(context, TableActivity::class.java)
            intent.putExtra(ProviderArgs.ARG_QUERYURI, makeUri(Relations.URI))
            intent.putExtra(ProviderArgs.ARG_QUERYID, Relations.RELATIONID)
            intent.putExtra(ProviderArgs.ARG_QUERYITEMS, arrayOf(Relations.RELATIONID, Relations.RELATION, Relations.RECURSES_SELECT))
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
            targetIntent = if (trimmedQuery.startsWith("#ws")) {
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
                args.putInt(ProviderArgs.ARG_QUERYRECURSE, recurse)
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
            } else if (trimmedQuery.startsWith("#ff")) {
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
            } else if (trimmedQuery.startsWith("#mr")) {
                val rolePointer: Parcelable = PmRolePointer(id)
                args.putInt(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_PMROLE)
                args.putParcelable(ProviderArgs.ARG_QUERYPOINTER, rolePointer)
                makeDetailIntent(BrowsePredicateMatrixActivity::class.java)
            } else {
                return
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
            fragment = makeBrowse1Fragment()
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
     * Fragment factory
     *
     * @return fragment
     */
    private fun makeBrowse1Fragment(): Fragment {

        // context
        val context = requireContext()

        // type
        val selectorType = XnSettings.getXSelectorPref(context)

        // mode
        val selectorMode = Settings.getSelectorViewModePref(context)
        return when (selectorMode) {
            Settings.SelectorViewMode.VIEW ->
                when (selectorType) {
                    XnSettings.Selector.SELECTOR -> Browse1Fragment()
                    XnSettings.Selector.XSELECTOR -> XBrowse1Fragment()
                }

            Settings.SelectorViewMode.WEB -> WebFragment()
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
        val selectorType = XnSettings.getXSelectorPref(context)

        // mode
        val selectorMode: Settings.SelectorViewMode = Settings.getSelectorViewModePref(context)
        val intent: Intent = when (selectorMode) {
            Settings.SelectorViewMode.VIEW -> {
                val intentClass: Class<*> = when (selectorType) {
                    XnSettings.Selector.SELECTOR -> Browse1Activity::class.java
                    XnSettings.Selector.XSELECTOR -> XBrowse1Activity::class.java
                }
                Intent(requireContext(), intentClass)
            }

            Settings.SelectorViewMode.WEB -> Intent(requireContext(), WebActivity::class.java)
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
