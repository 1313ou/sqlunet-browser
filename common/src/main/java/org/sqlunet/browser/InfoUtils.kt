/*
 * Copyright (c) 2024. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.browser

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import com.google.android.material.behavior.SwipeDismissBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import org.sqlunet.browser.ColorUtils.fetchColors
import android.R as AndroidR
import com.google.android.material.R as MaterialR
import org.sqlunet.core.R as CoreR
import org.sqlunet.browser.common.R as CommonR

private const val SNACKBAR_DEFAULT_MINLINES = 3

private fun Snackbar.setTextMinLines(min: Int): Snackbar {
    if (min != 2) {
        val snackText = view.findViewById<TextView>(MaterialR.id.snackbar_text)
        snackText.minLines = min
    }
    return this
}

@RequiresApi(Build.VERSION_CODES.N)
fun makeSnackbar(
    context: Context,
    view: View,
    @StringRes textId: Int,
    minLines: Int = SNACKBAR_DEFAULT_MINLINES,
    @AttrRes backColorAttr: Int = MaterialR.attr.colorTertiary,
    @AttrRes foreColorAttr: Int = MaterialR.attr.colorOnTertiary,
    duration: Int = Snackbar.LENGTH_LONG,
): Snackbar {
    val formattedText = Html.fromHtml(context.getString(textId), Html.FROM_HTML_MODE_LEGACY)
    return makeSnackbar(context, view, formattedText, minLines = minLines, backColorAttr = backColorAttr, foreColorAttr = foreColorAttr, duration = duration)
}

fun makeSnackbar(
    context: Context,
    view: View,
    text: CharSequence,
    minLines: Int = SNACKBAR_DEFAULT_MINLINES,
    @AttrRes backColorAttr: Int = MaterialR.attr.colorTertiary,
    @AttrRes foreColorAttr: Int = MaterialR.attr.colorOnTertiary,
    duration: Int = Snackbar.LENGTH_LONG,
): Snackbar {
    val colors: IntArray = fetchColors(context, backColorAttr, foreColorAttr)
    return Snackbar
        .make(view, text, duration)
        .setTextMaxLines(8)
        .setTextMinLines(minLines)
        .setBackgroundTint(colors[0])
        .setTextColor(colors[1])
}

fun makeActionSnackbar(
    context: Context,
    anchorView: View,
    text: CharSequence,
    minLines: Int = SNACKBAR_DEFAULT_MINLINES,
    @AttrRes backColorAttr: Int = MaterialR.attr.colorTertiary,
    @AttrRes foreColorAttr: Int = MaterialR.attr.colorOnTertiary,
    duration: Int = Snackbar.LENGTH_LONG,
    action: (View) -> Unit,
): Snackbar {
    return makeSnackbar(context, anchorView, text, minLines = minLines, backColorAttr = backColorAttr, foreColorAttr = foreColorAttr, duration = duration)
        .setAction(AndroidR.string.ok, action)
}

@RequiresApi(Build.VERSION_CODES.N)
fun makeAnchoredSnackbar(
    context: Context,
    anchorView: View,
    @StringRes textId: Int,
    minLines: Int = SNACKBAR_DEFAULT_MINLINES,
    @AttrRes backColorAttr: Int = MaterialR.attr.colorTertiary,
    @AttrRes foreColorAttr: Int = MaterialR.attr.colorOnTertiary,
    duration: Int = Snackbar.LENGTH_LONG,
): Snackbar {
    val formattedText = Html.fromHtml(context.getString(textId), Html.FROM_HTML_MODE_LEGACY)
    return makeAnchoredSnackbar(context, anchorView, formattedText, minLines = minLines, backColorAttr = backColorAttr, foreColorAttr = foreColorAttr, duration = duration)
}

fun makeAnchoredSnackbar(
    context: Context,
    anchorView: View,
    text: CharSequence,
    minLines: Int = SNACKBAR_DEFAULT_MINLINES,
    @AttrRes backColorAttr: Int = MaterialR.attr.colorTertiary,
    @AttrRes foreColorAttr: Int = MaterialR.attr.colorOnTertiary,
    duration: Int = Snackbar.LENGTH_LONG,
): Snackbar {
    return makeActionSnackbar(context, anchorView, text, minLines = minLines, backColorAttr = backColorAttr, foreColorAttr = foreColorAttr, duration = duration) {}
        .setAnchorView(anchorView)
}

fun Snackbar.makeSwipable(
    @StringRes actionId: Int,
    listener: View.OnClickListener
): Snackbar {
    val action = context.getText(actionId)
    return makeSwipable(action, listener)
}

fun Snackbar.makeSwipable(
    action: CharSequence,
    listener: View.OnClickListener
): Snackbar {
    val behavior = BaseTransientBottomBar.Behavior()
    behavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_ANY)
    return this
        .setBehavior(behavior)
        .setAction(action, listener)
        .setActionTextColor(ContextCompat.getColor(context, AndroidR.color.white))
}

fun makeDialog(context: Context): MaterialAlertDialogBuilder {
    return MaterialAlertDialogBuilder(context, CoreR.style.MyM3AlertDialogOverlay)
        .setTitle(CommonR.string.app_name)
        .setIcon(CommonR.drawable.ic_logo)
        .setNegativeButton(CommonR.string.title_dismiss) { d, _ -> d.cancel() }
}

fun makeDialog(message: CharSequence, context: Context): MaterialAlertDialogBuilder {
    return makeDialog(context)
        .setMessage(message)
}

@RequiresApi(Build.VERSION_CODES.N)
fun showTooltip(context: Context, view: View, @StringRes textId: Int) {
    val formattedText = Html.fromHtml(context.getString(textId), Html.FROM_HTML_MODE_LEGACY)
    showTooltip(view, formattedText)
}

fun showTooltip(view: View, text: CharSequence) {
    // Using TooltipCompat ensures the most "Material 3" compliant behavior
    // for the View system across all API levels.
    TooltipCompat.setTooltipText(view, text)

    // To force the tooltip to show immediately (since it's a 'Primer'),
    // we simulate a long click which triggers the system tooltip.
    // If you prefer the Snackbar look (which is often used for M3 'Plain Tooltips'
    // that contain more than one word), the Snackbar code is actually more M3-standard.

    // However, if you want the strictly visual "PlainTooltip" anchor:
    view.performLongClick()

    // Since performLongClick might trigger your LongPress listener logic,
    // a safer Material 3 approach for a persistent primer is actually
    // the Snackbar anchored to the FAB (which you already had),
    // but styled specifically for M3.
}


// I N F O

fun snackbar(message: String, duration: Int, anchorView: View) {
    val snackbar = Snackbar.make(anchorView, message, duration)
    snackbar.show()
}

fun snackbar(@StringRes message: Int, duration: Int, anchorView: View) {
    val snackbar = Snackbar.make(anchorView, message, duration)
    snackbar.show()
}

// D I A L O G

fun dialog(message: CharSequence, context: Context) {
    makeDialog(message, context).show()
}

// W A R N I N G

fun operationPending(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

fun operationPending(context: Context, textId: Int) {
    operationPending(context, context.getString(textId))
}

fun fatal(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

fun fatal(context: Context, @StringRes textId: Int) {
    fatal(context, context.getString(textId))
}
