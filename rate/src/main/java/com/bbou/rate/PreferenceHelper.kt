/*
 * Copyright (c) 2016. Shintaro Katafuchi hotchemi
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.rate

import android.content.Context
import android.content.SharedPreferences
import java.util.Date

internal object PreferenceHelper {

    private const val PREF_FILE_NAME = "android_rate_pref_file"
    private const val PREF_KEY_INSTALL_DATE = "android_rate_install_date"
    private const val PREF_KEY_LAUNCH_TIMES = "android_rate_launch_times"
    private const val PREF_KEY_AGREE_SHOW_DIALOG = "android_rate_agree_show_dialog"
    private const val PREF_KEY_REMIND_INTERVAL = "android_rate_remind_interval"
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
    }

    private fun getPreferencesEditor(context: Context): SharedPreferences.Editor {
        return getPreferences(context).edit()
    }

    /**
     * Clear data in shared preferences.<br></br>
     *
     * @param context context
     */
    fun clearSharedPreferences(context: Context) {
        val editor = getPreferencesEditor(context)
        editor.remove(PREF_KEY_INSTALL_DATE)
        editor.remove(PREF_KEY_LAUNCH_TIMES)
        editor.apply()
    }

    /**
     * Set agree flag about show dialog.<br></br>
     * If it is false, rate dialog will never shown unless data is cleared.
     *
     * @param context context
     * @param agree   agree forActivity showing rate dialog
     */
    fun setAgreeShowDialog(context: Context, agree: Boolean) {
        val editor = getPreferencesEditor(context)
        editor.putBoolean(PREF_KEY_AGREE_SHOW_DIALOG, agree)
        editor.apply()
    }

    fun getIsAgreeShowDialog(context: Context): Boolean {
        return getPreferences(context).getBoolean(PREF_KEY_AGREE_SHOW_DIALOG, true)
    }

    fun setRemindInterval(context: Context) {
        val editor = getPreferencesEditor(context)
        editor.remove(PREF_KEY_REMIND_INTERVAL)
        editor.putLong(PREF_KEY_REMIND_INTERVAL, Date().time)
        editor.apply()
    }

    fun getRemindInterval(context: Context): Long {
        return getPreferences(context).getLong(PREF_KEY_REMIND_INTERVAL, 0)
    }

    fun setInstallDate(context: Context) {
        val editor = getPreferencesEditor(context)
        editor.putLong(PREF_KEY_INSTALL_DATE, Date().time)
        editor.apply()
    }

    fun getInstallDate(context: Context): Long {
        return getPreferences(context).getLong(PREF_KEY_INSTALL_DATE, 0)
    }

    fun setLaunchTimes(context: Context, launchTimes: Int) {
        val editor = getPreferencesEditor(context)
        editor.putInt(PREF_KEY_LAUNCH_TIMES, launchTimes)
        editor.apply()
    }

    fun getLaunchTimes(context: Context): Int {
        return getPreferences(context).getInt(PREF_KEY_LAUNCH_TIMES, 0)
    }

    fun isFirstLaunch(context: Context): Boolean {
        return getPreferences(context).getLong(PREF_KEY_INSTALL_DATE, 0) == 0L
    }
}