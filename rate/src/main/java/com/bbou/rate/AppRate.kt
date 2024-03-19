/*
 * Copyright (c) 2016. Shintaro Katafuchi hotchemi
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.rate

import android.app.Activity
import android.content.Context
import android.view.View
import java.util.Date
import kotlin.concurrent.Volatile

class AppRate {

    private val options = DialogOptions()
    private var pastInstallDateDays = 10
    private var launchTimes = 10
    private var remindInterval = 1
    var isDebug = false
        private set

    fun setLaunchTimes(launchTimes: Int): AppRate {
        this.launchTimes = launchTimes
        return this
    }

    fun setPastInstallDays(pastInstallDateDays: Int): AppRate {
        this.pastInstallDateDays = pastInstallDateDays
        return this
    }

    fun setRemindInterval(remindInterval: Int): AppRate {
        this.remindInterval = remindInterval
        return this
    }

    fun setShowLaterButton(isShowNeutralButton: Boolean): AppRate {
        options.setShowNeutralButton(isShowNeutralButton)
        return this
    }

    fun setShowNeverButton(isShowNeverButton: Boolean): AppRate {
        options.setShowNegativeButton(isShowNeverButton)
        return this
    }

    fun setShowTitle(isShowTitle: Boolean): AppRate {
        options.setShowTitle(isShowTitle)
        return this
    }

    fun clearAgreeShowDialog(context: Context): AppRate {
        PreferenceHelper.setAgreeShowDialog(context, true)
        return this
    }

    fun clearSettingsParam(context: Context): AppRate {
        PreferenceHelper.setAgreeShowDialog(context, true)
        PreferenceHelper.clearSharedPreferences(context)
        return this
    }

    fun setAgreeShowDialog(context: Context, clear: Boolean): AppRate {
        PreferenceHelper.setAgreeShowDialog(context, clear)
        return this
    }

    fun setView(view: View?): AppRate {
        options.view = view
        return this
    }

    fun setTitle(resourceId: Int): AppRate {
        options.titleResId = resourceId
        return this
    }

    fun setTitle(title: String?): AppRate {
        options.setTitleText(title)
        return this
    }

    fun setMessage(resourceId: Int): AppRate {
        options.messageResId = resourceId
        return this
    }

    fun setMessage(message: String?): AppRate {
        options.setMessageText(message)
        return this
    }

    fun setTextRateNow(resourceId: Int): AppRate {
        options.textPositiveResId = resourceId
        return this
    }

    fun setTextRateNow(positiveText: String?): AppRate {
        options.setPositiveText(positiveText)
        return this
    }

    fun setTextLater(resourceId: Int): AppRate {
        options.textNeutralResId = resourceId
        return this
    }

    fun setTextLater(neutralText: String?): AppRate {
        options.setNeutralText(neutralText)
        return this
    }

    fun setTextNever(resourceId: Int): AppRate {
        options.textNegativeResId = resourceId
        return this
    }

    fun setTextNever(negativeText: String?): AppRate {
        options.setNegativeText(negativeText)
        return this
    }

    fun setCancelable(cancelable: Boolean): AppRate {
        options.cancelable = cancelable
        return this
    }

    private fun setStoreType(@Suppress("SameParameterValue") appstore: StoreType): AppRate {
        options.storeType = appstore
        return this
    }

    fun monitor(context: Context) {
        if (PreferenceHelper.isFirstLaunch(context)) {
            PreferenceHelper.setInstallDate(context)
        }
        PreferenceHelper.setLaunchTimes(context, PreferenceHelper.getLaunchTimes(context) + 1)
    }

    private fun showRateDialog(activity: Activity) {
        if (!activity.isFinishing) {
            DialogBuilder.build(activity, options).show()
        }
    }

    private fun shouldShowRateDialog(context: Context): Boolean {
        return PreferenceHelper.getIsAgreeShowDialog(context) && isOverLaunchTimes(context) && isOverInstallDate(context) && isOverRemindDate(context)
    }

    private fun isOverLaunchTimes(context: Context): Boolean = PreferenceHelper.getLaunchTimes(context) >= launchTimes

    private fun isOverInstallDate(context: Context): Boolean = isOverDate(PreferenceHelper.getInstallDate(context), pastInstallDateDays)

    private fun isOverRemindDate(context: Context): Boolean = isOverDate(PreferenceHelper.getRemindInterval(context), remindInterval)

    fun setDebug(isDebug: Boolean): AppRate {
        this.isDebug = isDebug
        return this
    }

    companion object {
        @Volatile
        private var singleton: AppRate? = null

        private fun forActivity(): AppRate? {
            if (singleton == null) {
                synchronized(AppRate::class.java) {
                    if (singleton == null) {
                        singleton = AppRate()
                    }
                }
            }
            return singleton
        }

        private fun showRateDialogIfMeetsConditions(activity: Activity): Boolean {
            val isMeetsConditions = singleton!!.isDebug || singleton!!.shouldShowRateDialog(activity)
            if (isMeetsConditions) {
                singleton!!.showRateDialog(activity)
            }
            return isMeetsConditions
        }

        private fun isOverDate(targetDate: Long, threshold: Int): Boolean {
            return Date().time - targetDate >= threshold.toLong() * 24 * 60 * 60 * 1000
        }

        // Called from activity's onCreate()
        @JvmStatic
        operator fun invoke(activity: Activity) {
            forActivity()!!.setStoreType(StoreType.GOOGLE) //default is Google, other option is Amazon
                .setDebug(false) // default false.
                .monitor(activity)
            showRateDialogIfMeetsConditions(activity)
        }

        // Called from menu
        @JvmStatic
        fun rate(activity: Activity) {
            forActivity()!!.showRateDialog(activity)
        }
    }
}
