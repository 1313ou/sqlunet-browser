/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.wn

import android.content.Intent
import android.database.Cursor
import android.os.Parcelable
import android.util.Log
import org.sqlunet.browser.AppContext
import org.sqlunet.browser.common.BaseTextFragment
import org.sqlunet.browser.common.TextAdapter
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.wordnet.SynsetPointer
import org.sqlunet.wordnet.WordPointer
import org.sqlunet.wordnet.browser.SynsetActivity
import org.sqlunet.wordnet.browser.WordActivity
import org.sqlunet.wordnet.settings.Settings

/**
 * Text result fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class TextFragment : BaseTextFragment() {

    override fun makeAdapter(layoutId: Int?): TextAdapter {
        return TextAdapter(requireContext(), query, null, this::onItemClick, layoutId)
    }

    private fun onItemClick(cursor: Cursor, position: Int, id: Long) {
        Log.d(TAG, "Click: id=$id pos=$position")

        // args
        val args = requireArguments()

        // search target
        val database = args.getString(ProviderArgs.ARG_QUERYDATABASE)
        if (database != null) {
            if ("wn" == database) {
                val subtarget = args.getString(ProviderArgs.ARG_QUERYIDTYPE)
                if ("synset" == subtarget) {

                    // parameters
                    val recurse = Settings.getRecursePref(AppContext.context)
                    val parameters = Settings.makeParametersPref(AppContext.context)

                    // target
                    val colIdx = cursor.getColumnIndex("synsetid")
                    val targetId = cursor.getLong(colIdx)
                    Log.d(TAG, "Click: wn synset=$targetId")

                    // build pointer
                    val synsetPointer: Parcelable = SynsetPointer(targetId)

                    // intent
                    val targetIntent = Intent(AppContext.context, SynsetActivity::class.java)
                    targetIntent.action = ProviderArgs.ACTION_QUERY
                    targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_SYNSET)
                    targetIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, synsetPointer)
                    targetIntent.putExtra(ProviderArgs.ARG_QUERYRECURSE, recurse)
                    targetIntent.putExtra(ProviderArgs.ARG_RENDERPARAMETERS, parameters)

                    // start
                    startActivity(targetIntent)
                } else if ("word" == subtarget) {

                    // target
                    val colIdx = cursor.getColumnIndex("wordid")
                    val targetId = cursor.getLong(colIdx)
                    Log.d(TAG, "Click: wn word=$targetId")

                    // build pointer
                    val wordPointer: Parcelable = WordPointer(targetId)

                    // intent
                    val targetIntent = Intent(AppContext.context, WordActivity::class.java)
                    targetIntent.action = ProviderArgs.ACTION_QUERY
                    targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_WORD)
                    targetIntent.putExtra(ProviderArgs.ARG_QUERYPOINTER, wordPointer)

                    // start
                    startActivity(targetIntent)
                }
            }
        }
    }

    companion object {

        private const val TAG = "TextF"
        const val FRAGMENT_TAG = "text"
    }
}
