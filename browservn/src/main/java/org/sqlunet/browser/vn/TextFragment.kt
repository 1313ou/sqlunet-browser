/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.vn

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.text.style.StyleSpan
import android.util.Log
import android.util.Pair
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import org.sqlunet.browser.BaseListFragment
import org.sqlunet.propbank.PbRoleSetPointer
import org.sqlunet.propbank.browser.PbRoleSetActivity
import org.sqlunet.propbank.provider.PropBankContract
import org.sqlunet.provider.ProviderArgs
import org.sqlunet.style.Colors
import org.sqlunet.style.Factories
import org.sqlunet.style.Factories.spans
import org.sqlunet.style.RegExprSpanner
import org.sqlunet.verbnet.VnClassPointer
import org.sqlunet.verbnet.browser.VnClassActivity
import org.sqlunet.verbnet.provider.VerbNetContract
import java.util.regex.Pattern

/**
 * Text result fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class TextFragment : BaseListFragment() {

    /**
     * Query argument
     */
    private var query: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // args
        val args = requireArguments()

        // search target
        // var database = args.getString(ProviderArgs.ARG_QUERYDATABASE)
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
        val patterns = toPatterns(query!!)

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
                    throw IllegalStateException(view.javaClass.name + " is not a view that can be bound by this SimpleCursorAdapter")
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
            // wordnet
            if ("vn" == database) {
                val idClasses = cursor.getColumnIndex(VerbNetContract.Lookup_VnExamples_X.CLASSES)
                val classes = cursor.getString(idClasses)
                Log.d(TAG, "Click: vn classes=$classes")
                val result = makeData(classes)
                if (result.first.size > 1) {
                    val listener = DialogInterface.OnClickListener { _: DialogInterface?, which: Int ->
                        // which argument contains the index position of the selected item
                        val typedPointer = result.first[which]
                        startVn(typedPointer)
                    }
                    val dialog = makeDialog(listener, *result.second)
                    dialog.show()
                } else if (result.first.size == 1) {
                    val typedPointer = result.first[0]
                    startVn(typedPointer)
                }
            } else if ("pb" == database) {
                val idRoleSets = cursor.getColumnIndex(PropBankContract.Lookup_PbExamples_X.ROLESETS)
                val roleSets = cursor.getString(idRoleSets)
                Log.d(TAG, "Click: pb rolesets=$roleSets")
                val result = makeData(roleSets)
                if (result.first.size > 1) {
                    val listener = DialogInterface.OnClickListener { _: DialogInterface?, which: Int ->
                        // which argument contains the index position of the selected item
                        val typedPointer = result.first[which]
                        startPb(typedPointer)
                    }
                    val dialog = makeDialog(listener, *result.second)
                    dialog.show()
                } else if (result.first.size == 1) {
                    val typedPointer = result.first[0]
                    startPb(typedPointer)
                }
            }
        }
    }

    /**
     * Start VerbNet
     *
     * @param typedPointer typed pointer
     */
    private fun startVn(typedPointer: TypedPointer) {
        val targetId = typedPointer.id
        var targetIntent: Intent? = null
        var pointer: Parcelable? = null

        // intent, type, pointer
        if (typedPointer.type == 0) {
            pointer = VnClassPointer(targetId)
            targetIntent = Intent(requireContext(), VnClassActivity::class.java)
            targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_VNCLASS)
        }
        targetIntent!!.putExtra(ProviderArgs.ARG_QUERYPOINTER, pointer)
        targetIntent.action = ProviderArgs.ACTION_QUERY

        // start
        startActivity(targetIntent)
    }

    /**
     * Start PropBank
     *
     * @param typedPointer typed pointer
     */
    private fun startPb(typedPointer: TypedPointer) {
        val targetId = typedPointer.id
        var targetIntent: Intent? = null
        var pointer: Parcelable? = null

        // intent, type, pointer
        if (typedPointer.type == 0) {
            pointer = PbRoleSetPointer(targetId)
            targetIntent = Intent(requireContext(), PbRoleSetActivity::class.java)
            targetIntent.putExtra(ProviderArgs.ARG_QUERYTYPE, ProviderArgs.ARG_QUERYTYPE_PBROLESET)
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
                    0 -> resId = R.drawable.roles
                    1 -> resId = R.drawable.role
                    2 -> resId = R.drawable.sentence
                }
                val sb = SpannableStringBuilder()
                appendImage(requireContext(), sb, resId)
                sb.append(' ')
                if (pattern.matcher(label).find()) {
                    append(sb, label, spans(Colors.textMatchBackColor, Colors.textMatchForeColor, StyleSpan(Typeface.BOLD)))
                } else {
                    append(sb, label, spans(Colors.textBackColor, Colors.textForeColor))
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
            .setTitle(R.string.title_activity_searchtext)
            .setItems(choices, listener)
            .create()
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

        /**
         * Append text
         *
         * @param sb    spannable string builder
         * @param text  text
         * @param spans spans to apply
         */
        private fun append(sb: SpannableStringBuilder, text: CharSequence?, vararg spans: Any) {
            if (text.isNullOrEmpty()) {
                return
            }
            val from = sb.length
            sb.append(text)
            val to = sb.length
            for (span in spans) {
                sb.setSpan(span, from, to, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        /**
         * Append Image
         *
         * @param context context
         * @param sb      spannable string builder
         * @param resId   resource id
         */
        private fun appendImage(context: Context, sb: SpannableStringBuilder, resId: Int) {
            append(sb, "\u0000", makeImageSpan(context, resId))
        }

        /**
         * Make image span
         *
         * @param context context
         * @param resId   res id
         * @return image span
         */
        private fun makeImageSpan(context: Context, @DrawableRes resId: Int): Any {
            val drawable = AppCompatResources.getDrawable(context, resId)!!
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            return ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BOTTOM)
        }
    }
}
