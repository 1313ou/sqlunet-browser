/*
 * Copyright (c) 2016. Shintaro Katafuchi hotchemi
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.rate

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import com.bbou.rate.StoreType.IntentBuilder

enum class StoreType {
    GOOGLE,
    AMAZON;

    internal fun interface IntentBuilder {

        fun build(context: Context): Intent
    }

    fun getIntent(context: Context): Intent {
        val builder = when (this) {
            GOOGLE -> googleIntentBuilder
            AMAZON -> amazonIntentBuilder
        }
        return builder.build(context)
    }

    companion object {

        private val googleIntentBuilder = IntentBuilder { context: Context ->
            // if GP not present on device, open web browser
            var intent = getGooglePlayIntent(context)
            if (intent != null) {
                return@IntentBuilder intent
            }
            val packageName = context.packageName
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
            intent
        }

        private val amazonIntentBuilder = IntentBuilder { context: Context ->
            val packageName = context.packageName
            Intent(Intent.ACTION_VIEW, getUri("amzn://apps/android?p=", packageName))
        }

        private fun getUri(@Suppress("SameParameterValue") uriPrefix: String, packageName: String?): Uri? {
            return if (packageName == null) null else Uri.parse(uriPrefix + packageName)
        }

        private fun getGooglePlayIntent(context: Context): Intent? {
            val packageName = context.packageName
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))

            // find all applications able to handle our rateIntent
            val handlingApps: List<ResolveInfo> =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) context.packageManager.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))
                else context.packageManager.queryIntentActivities(intent, 0)
            for (app in handlingApps) {
                // look for Google Play application
                if (app.activityInfo.applicationInfo.packageName == "com.android.vending") {
                    val activity = app.activityInfo
                    val componentName = ComponentName(activity.applicationInfo.packageName, activity.name)
                    intent.setComponent(componentName)
                    return intent
                }
            }
            return null
        }
    }
}