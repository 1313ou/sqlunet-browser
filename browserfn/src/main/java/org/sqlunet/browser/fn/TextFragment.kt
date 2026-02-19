/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.fn

import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Typeface
import android.os.Parcelable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import android.util.Pair
import androidx.appcompat.app.AlertDialog
import org.sqlunet.browser.AppContext
import org.sqlunet.browser.common.BaseTextFragment
import org.sqlunet.browser.common.TextAdapter
import org.sqlunet.framenet.FnFramePointer
import org.sqlunet.framenet.FnLexUnitPointer
import org.sqlunet.framenet.FnSentencePointer
import org.sqlunet.framenet.browser.FnFrameActivity
import org.sqlunet.framenet.browser.FnLexUnitActivity
import org.sqlunet.framenet.browser.FnSentenceActivity
import org.sqlunet.framenet.provider.FrameNetContract
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.style.Colors
import org.sqlunet.style.Factories.spans
import java.util.regex.Pattern
import org.sqlunet.browser.common.R as CommonR
import org.sqlunet.xnet.R as XNetR

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
            // wordnet
            if ("fn" == database) {
                val idFrames = cursor.getColumnIndex(FrameNetContract.Lookup_FTS_FnSentences_X.FRAMES)
                val idLexUnits = cursor.getColumnIndex(FrameNetContract.Lookup_FTS_FnSentences_X.LEXUNITS)
                val idSentenceId = cursor.getColumnIndex(FrameNetContract.Lookup_FTS_FnSentences_X.SENTENCEID)
                val frames = cursor.getString(idFrames)
                val lexUnits = cursor.getString(idLexUnits)
                val sentence = "sentence@" + cursor.getString(idSentenceId)
                Log.d(TAG, "Click: fn frames=$frames")
                Log.d(TAG, "Click: fn lexunits=$lexUnits")
                Log.d(TAG, "Click: fn sentence=$sentence")
                val result = makeData(frames, lexUnits, sentence)
                if (result.first.size > 1) {
                    val listener = DialogInterface.OnClickListener { _: DialogInterface?, which: Int ->
                        // which argument contains the index position of the selected item
                        val typedPointer = result.first[which]
                        startFn(typedPointer)
                    }
                    val dialog = makeDialog(listener, *result.second)
                    dialog.show()
                } else if (result.first.size == 1) {
                    val typedPointer = result.first[0]
                    startFn(typedPointer)
                }
            }
        }
    }

    /**
     * Start FrameNet
     *
     * @param typedPointer typed pointer
     */
    private fun startFn(typedPointer: TypedPointer) {
        val targetId = typedPointer.id
        var targetIntent: Intent? = null
        var pointer: Parcelable? = null
        when (typedPointer.type) {
            0 -> {
                pointer = FnFramePointer(targetId)
                targetIntent = Intent(AppContext.context, FnFrameActivity::class.java)
                targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNFRAME)
            }

            1 -> {
                pointer = FnLexUnitPointer(targetId)
                targetIntent = Intent(AppContext.context, FnLexUnitActivity::class.java)
                targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNLEXUNIT)
            }

            2 -> {
                pointer = FnSentencePointer(targetId)
                targetIntent = Intent(AppContext.context, FnSentenceActivity::class.java)
                targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_FNSENTENCE)
            }
        }
        targetIntent!!.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer)
        targetIntent.action = ProviderArgs.ACTION_QUERY

        // start
        startActivity(targetIntent)
    }

    /**
     * Typed pointer
     */
    private class TypedPointer(val type: Int, val id: Long)

    /**
     * Make choice data
     *
     * @param concatChoices concatenated choices
     * @return array of typed pointers and array of labels
     */
    private fun makeData(vararg concatChoices: String): Pair<Array<TypedPointer>, Array<CharSequence>> {
        val typedPointers: MutableList<TypedPointer> = ArrayList()
        val labels: MutableList<CharSequence> = ArrayList()
        val pattern = Pattern.compile("((?i)$query)")
        for ((type, choices) in concatChoices.withIndex()) {
            val choiceList = mutableListOf(*choices.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            choiceList.sort()
            for (choice in choiceList) {
                val fields = choice.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val label = fields[0]
                var resId = -1
                when (type) {
                    0 -> resId = XNetR.drawable.roles
                    1 -> resId = XNetR.drawable.role
                    2 -> resId = XNetR.drawable.sentence
                }
                val sb = SpannableStringBuilder()
                TextAdapter.appendImage(requireContext(), sb, resId)
                sb.append(' ')
                if (pattern.matcher(label).find()) {
                    TextAdapter.append(sb, label, spans(Colors.textMatchBackColor, Colors.textMatchForeColor, StyleSpan(Typeface.BOLD)))
                } else {
                    TextAdapter.append(sb, label, spans(Colors.textBackColor, Colors.textForeColor))
                }
                labels.add(sb)
                typedPointers.add(TypedPointer(type, fields[1].toLong()))
            }
        }
        val typedPointersArray = typedPointers.toTypedArray<TypedPointer>()
        val labelsArray = labels.toTypedArray<CharSequence>()
        return Pair(typedPointersArray, labelsArray)
    }

    /**
     * Make selection dialog
     *
     * @param listener click listener
     * @param choices  choices
     * @return dialog
     */
    private fun makeDialog(listener: DialogInterface.OnClickListener, vararg choices: CharSequence): AlertDialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(CommonR.string.title_activity_searchtext)
            .setItems(choices, listener)
            .create()
    }

    companion object {

        private const val TAG = "TextF"

        const val FRAGMENT_TAG = "text"
    }
}
