/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.wn

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import org.sqlunet.browser.BaseListFragment
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.style.Factories
import org.sqlunet.style.RegExprSpanner
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
class TextFragment : BaseListFragment() {

    /**
     * Query argument
     */
    private lateinit var query: String

    override fun onCreate(savedInstanceState: Bundle?) {

        // args
        val args = requireArguments()

        // search target
        val queryArg = args.getString(ProviderArgs.ARG_QUERYARG)
        query = queryArg?.trim { it <= ' ' } ?: ""
        super.onCreate(savedInstanceState)
    }

    /**
     * Make view binder
     *
     * @return ViewBinder
     */
    override fun makeViewBinder(): SimpleCursorAdapter.ViewBinder {
        // patterns (case-insensitive)
        val patterns = toPatterns(query)

        // spanner
        val spanner = RegExprSpanner(patterns, factories)

        // view binder
        return SimpleCursorAdapter.ViewBinder { view: View, cursor: Cursor, columnIndex: Int ->
            var value = cursor.getString(columnIndex)
            if (value == null) {
                value = ""
            }
            when (view) {
                is TextView -> {
                    val sb = SpannableStringBuilder(value)
                    spanner.setSpan(sb, 0, 0)
                    view.text = sb
                    return@ViewBinder true
                }

                is ImageView -> {
                    try {
                        view.setImageResource(value.toInt())
                        return@ViewBinder true
                    } catch (nfe: NumberFormatException) {
                        view.setImageURI(Uri.parse(value))
                        return@ViewBinder true
                    }
                }

                else -> {
                    throw IllegalStateException(view.javaClass.getName() + " is not a view that can be bound by this SimpleCursorAdapter")
                }
            }
        }
    }

    override fun onListItemClick(listView: ListView, view: View, position: Int, id: Long) {
        super.onListItemClick(listView, view, position, id)
        Log.d(TAG, "Click: id=$id pos=$position")

        // cursor
        val adapter = listAdapter!!
        val item = adapter.getItem(position)
        val cursor = item as Cursor

        // args
        val args = requireArguments()

        // search target
        val database = args.getString(ProviderArgs.ARG_QUERYDATABASE)
        if (database != null) {
            if ("wn" == database) {
                val subtarget = args.getString(ProviderArgs.ARG_QUERYIDTYPE)
                if ("synset" == subtarget) {

                    // parameters
                    val recurse = Settings.getRecursePref(requireContext())
                    val parameters = Settings.getRenderParametersPref(requireContext())

                    // target
                    val colIdx = cursor.getColumnIndex("synsetid")
                    val targetId = cursor.getLong(colIdx)
                    Log.d(TAG, "Click: wn synset=$targetId")

                    // build pointer
                    val synsetPointer: Parcelable = SynsetPointer(targetId)

                    // intent
                    val targetIntent = Intent(requireContext(), SynsetActivity::class.java)
                    targetIntent.setAction(ProviderArgs.ACTION_QUERY)
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
                    val targetIntent = Intent(requireContext(), WordActivity::class.java)
                    targetIntent.setAction(ProviderArgs.ACTION_QUERY)
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

        /**
         * Factories
         */
        private val factories = arrayOf(Factories.boldFactory)

        private fun toPatterns(query: String): Array<String> {
            val tokens = query.split("[\\s()]+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val patterns: MutableList<String> = ArrayList()
            for (token in tokens) {
                var token2 = token.trim { it <= ' ' }
                token2 = token2.replace("\\*$".toRegex(), "")
                if (token2.isEmpty() || "AND" == token2 || "OR" == token2 || "NOT" == token2 || token2.startsWith("NEAR")) {
                    continue
                }
                // Log.d(TAG, '<' + token + '>')
                patterns.add("((?i)$token2)")
            }
            return patterns.toTypedArray<String>()
        }
    }
}
