/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.wn

import android.app.Activity
import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.bbou.others.OthersActivity.Companion.install
import com.google.android.material.behavior.SwipeDismissBehavior
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

object Oewn {

    private const val PREF_FILE_NAME = "android_launches_pref_file"
    private const val PREF_KEY_LAUNCH_TIMES = "android_launch_times"
    private const val PREF_KEY_OEWN_NO_NOTICE = "pref_oewn_no_notice"
    private const val HOW_OFTEN = 10

    fun hook(activity: Activity) {
        val prefs = activity.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        val launchTimes = prefs.getInt(PREF_KEY_LAUNCH_TIMES, 0)
        if (launchTimes % HOW_OFTEN == 1) {
            val prefs2 = PreferenceManager.getDefaultSharedPreferences(activity)
            val noNotice = prefs2.getBoolean(PREF_KEY_OEWN_NO_NOTICE, false)
            if (!noNotice) {
                suggestOewn(activity)
            }
        }
        prefs.edit().putInt(PREF_KEY_LAUNCH_TIMES, launchTimes + 1).apply()
    }

    private fun suggestOewn(activity: Activity) {
        val behavior = BaseTransientBottomBar.Behavior()
        behavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_ANY)
        val parentLayout = activity.findViewById<View>(R.id.activity_main_sub) // view to find a parent from
        if (parentLayout != null) {
            val message1 = activity.getString(R.string.obsolete_app)
            val message2 = activity.getString(R.string.new_app)
            val sb = SpannableStringBuilder()
            sb.append(message1).append('\n')
            sb.append('@').setSpan(ImageSpan(activity, R.drawable.logo_semantikos_ewn), sb.length - 1, sb.length, 0)
            sb.append('\n').append(message2)
            val snackbar = Snackbar.make(parentLayout, sb, Snackbar.LENGTH_INDEFINITE)
            snackbar.setTextMaxLines(10)
                .setBackgroundTint(ContextCompat.getColor(activity, R.color.snackbar_oewn))
                .setAction(R.string.obsolete_get_oewn) { install(activity.getString(R.string.semantikos_ewn_uri), activity) }
                .setActionTextColor(ContextCompat.getColor(activity, android.R.color.white))
                .setBehavior(behavior).show()
        }
    }
}
