/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.xn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import org.sqlunet.browser.AppContext
import org.sqlunet.browser.BaseSearchFragment
import org.sqlunet.browser.R
import org.sqlunet.browser.SearchTextSplashFragment
import org.sqlunet.browser.SplashFragment
import org.sqlunet.browser.StatusActivity
import org.sqlunet.browser.xn.XnStatus.validTable
import org.sqlunet.framenet.provider.FrameNetContract
import org.sqlunet.framenet.provider.FrameNetProvider
import org.sqlunet.propbank.provider.PropBankContract
import org.sqlunet.propbank.provider.PropBankProvider
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.settings.Settings
import org.sqlunet.verbnet.provider.VerbNetContract
import org.sqlunet.verbnet.provider.VerbNetProvider
import org.sqlunet.wordnet.provider.WordNetContract
import org.sqlunet.wordnet.provider.WordNetProvider
import org.sqlunet.browser.common.R as CommonR

/**
 * Search text fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SearchTextFragment : BaseSearchFragment() {

    init {
        layoutId = CommonR.layout.fragment_searchtext
        menuIds = listOf(CommonR.menu.searchtext)
        spinnerLabels = R.array.searchtext_modes
        spinnerIcons = R.array.searchtext_icons
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            // splash fragment
            val fragment: Fragment = SearchTextSplashFragment()
            getChildFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(CommonR.id.container_searchtext, fragment, SplashFragment.FRAGMENT_TAG)
                //.addToBackStack(SplashFragment.FRAGMENT_TAG)
                .commit()
        }
    }

    // S P I N N E R

    override fun onSelection(position: Int) {
        searchModePosition = position
        Settings.setSearchTextModePref(AppContext.context, position)
    }

    override val initialSelection: Int = Settings.getSearchTextModePref(AppContext.context)

    // S E A R C H

    /**
     * Handle query
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
        Log.d(TAG, "Search text $trimmedQuery")

        // mode
        val modePosition = searchModePosition

        // status
        // var[] textSearches = getResources().getTextArray(R.array.searchtext_modes)

        // as per selected mode
        val table: String
        val searchUri: String
        val id: String
        val idType: String
        val target: String
        val columns: Array<String>
        val hiddenColumns: Array<String>
        val database: String
        when (modePosition) {
            0 -> {
                table = WordNetContract.Lookup_Definitions.URI
                searchUri = WordNetProvider.makeUri(table)
                id = WordNetContract.Lookup_Definitions.SYNSETID
                idType = "synset"
                target = WordNetContract.Lookup_Definitions.DEFINITION
                columns = arrayOf(WordNetContract.Lookup_Definitions.DEFINITION)
                hiddenColumns = arrayOf(WordNetContract.Lookup_Definitions.SYNSETID)
                database = "wn"
            }

            1 -> {
                table = WordNetContract.Lookup_Samples.URI
                searchUri = WordNetProvider.makeUri(table)
                id = WordNetContract.Lookup_Samples.SYNSETID
                idType = "synset"
                target = WordNetContract.Lookup_Samples.SAMPLE
                columns = arrayOf(WordNetContract.Lookup_Samples.SAMPLE)
                hiddenColumns = arrayOf(WordNetContract.Lookup_Samples.SYNSETID)
                database = "wn"
            }

            2 -> {
                table = WordNetContract.Lookup_Words.URI
                searchUri = WordNetProvider.makeUri(table)
                id = WordNetContract.Lookup_Words.WORDID
                idType = "word"
                target = WordNetContract.Lookup_Words.WORD
                columns = arrayOf(WordNetContract.Lookup_Words.WORD)
                hiddenColumns = arrayOf(WordNetContract.Lookup_Words.WORDID)
                database = "wn"
            }

            3 -> {
                table = VerbNetContract.Lookup_VnExamples.URI
                searchUri = VerbNetProvider.makeUri(VerbNetContract.Lookup_VnExamples_X.URI_BY_EXAMPLE)
                id = VerbNetContract.Lookup_VnExamples_X.EXAMPLEID
                idType = "vnexample"
                target = VerbNetContract.Lookup_VnExamples_X.EXAMPLE
                columns = arrayOf(VerbNetContract.Lookup_VnExamples_X.EXAMPLE)
                hiddenColumns = arrayOf(
                    "GROUP_CONCAT(DISTINCT class || '@' || classid) AS " + VerbNetContract.Lookup_VnExamples_X.CLASSES
                )
                database = "vn"
            }

            4 -> {
                table = PropBankContract.Lookup_PbExamples.URI
                searchUri = PropBankProvider.makeUri(PropBankContract.Lookup_PbExamples_X.URI_BY_EXAMPLE)
                id = PropBankContract.Lookup_PbExamples_X.EXAMPLEID
                idType = "pbexample"
                target = PropBankContract.Lookup_PbExamples_X.TEXT
                columns = arrayOf(PropBankContract.Lookup_PbExamples_X.TEXT)
                hiddenColumns = arrayOf(
                    "GROUP_CONCAT(DISTINCT rolesetname ||'@'||rolesetid) AS " + PropBankContract.Lookup_PbExamples_X.ROLESETS
                )
                database = "pb"
            }

            5 -> {
                table = FrameNetContract.Lookup_FTS_FnSentences.URI
                searchUri = FrameNetProvider.makeUri(FrameNetContract.Lookup_FTS_FnSentences_X.URI_BY_SENTENCE)
                id = FrameNetContract.Lookup_FTS_FnSentences_X.SENTENCEID
                idType = "fnsentence"
                target = FrameNetContract.Lookup_FTS_FnSentences_X.TEXT
                columns = arrayOf(FrameNetContract.Lookup_FTS_FnSentences_X.TEXT)
                hiddenColumns = arrayOf(
                    FrameNetContract.Lookup_FTS_FnSentences_X.SENTENCEID,
                    "GROUP_CONCAT(DISTINCT  frame || '@' || frameid) AS " + FrameNetContract.Lookup_FTS_FnSentences_X.FRAMES,
                    "GROUP_CONCAT(DISTINCT  lexunit || '@' || luid) AS " + FrameNetContract.Lookup_FTS_FnSentences_X.LEXUNITS
                )
                database = "fn"
            }

            else -> return
        }
        Log.d(TAG, "Search text $table $searchUri")
        if (!validTable(requireContext(), table)) {
            if (view != null) {
                Snackbar.make(requireView(), getString(CommonR.string.error_invalid_table, table), Snackbar.LENGTH_LONG)
                    .setTextMaxLines(10)
                    .setAction(CommonR.string.fix_it) {
                        val intent = Intent(AppContext.context, StatusActivity::class.java)
                        startActivity(intent)
                    }
                    .show()
            }
            return
        }

        // parameters
        val args = Bundle()
        args.putString(ProviderArgs.ARG_QUERYURI, searchUri)
        args.putString(ProviderArgs.ARG_QUERYID, id)
        args.putString(ProviderArgs.ARG_QUERYIDTYPE, idType)
        args.putStringArray(ProviderArgs.ARG_QUERYITEMS, columns)
        args.putStringArray(ProviderArgs.ARG_QUERYHIDDENITEMS, hiddenColumns)
        args.putString(ProviderArgs.ARG_QUERYFILTER, "$target MATCH ?")
        args.putString(ProviderArgs.ARG_QUERYARG, trimmedQuery)
        args.putInt(ProviderArgs.ARG_QUERYLAYOUT, CommonR.layout.item_searchtext)
        args.putString(ProviderArgs.ARG_QUERYDATABASE, database)

        // fragment
        val fragment: Fragment = TextFragment()
        fragment.setArguments(args)
        if (!isAdded) {
            return
        }
        getChildFragmentManager()
            .beginTransaction()
            .setReorderingAllowed(true)
            .replace(CommonR.id.container_searchtext, fragment, TextFragment.FRAGMENT_TAG)
            .addToBackStack(TextFragment.FRAGMENT_TAG)
            .commit()
    }

    override fun triggerFocusSearch(): Boolean {
        if (!isAdded) {
            return false
        }
        val active = getChildFragmentManager().findFragmentById(CommonR.id.container_searchtext)
        return active != null && SplashFragment.FRAGMENT_TAG == active.tag
    }

    companion object {

        private const val TAG = "SearchTextF"
    }
}
