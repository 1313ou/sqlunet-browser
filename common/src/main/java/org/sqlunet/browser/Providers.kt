/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.app.SearchManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.text.SpannableStringBuilder
import androidx.appcompat.app.AppCompatActivity
import org.sqlunet.browser.common.R
import org.sqlunet.style.Factories
import org.sqlunet.style.Spanner.append
import java.io.IOException

/**
 * Providers
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal object Providers {

    /**
     * Providers
     *
     * @param activity activity
     */
    fun listProviders(activity: AppCompatActivity) {
        val sb = SpannableStringBuilder()
        sb.append(activity.getString(R.string.providers), 0, Factories.boldFactory)
        sb.append('\n').append('\n')
        val manager = activity.applicationContext.packageManager
        val packageName = activity.applicationContext.packageName
        val pack: PackageInfo
        try {
            pack = manager.getPackageInfo(packageName, PackageManager.GET_PROVIDERS)
            if (pack.providers != null) {
                for (provider in pack.providers) {
                    if (provider.name.contains("android")) {
                        continue
                    }
                    try {
                        build(sb, provider.name, null, null, activity.getString(R.string.provider_authority), provider.authority, null, null)
                    } catch (_: IOException) {
                    }
                }
            }
        } catch (_: PackageManager.NameNotFoundException) {
        }

        // suggestion (this activity may not be searchable)
        val searchManager = (activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager)
        val info = searchManager.getSearchableInfo(activity.componentName)
        if (info != null) {
            val suggestAuthority = info.suggestAuthority
            val suggestPath = info.suggestPath
            val suggestPkg = info.suggestPackage

            // message
            try {
                sb.append(activity.getString(R.string.suggestions), 0, Factories.boldFactory)
                    .append('\n')
                    .append('\n')
                build(sb, null, activity.getString(R.string.suggestion_provider_pack), suggestPkg, activity.getString(R.string.suggestion_provider_authority), suggestAuthority, activity.getString(R.string.suggestion_provider_path), suggestPath)
            } catch (_: IOException) {
            }
        }

        // dialog
        makeDialog(sb,activity)
            .setTitle(R.string.action_provider_info)
            .show()
    }

    /** @noinspection UnusedReturnValue
     */
    @Throws(IOException::class)
    private fun build(sb: SpannableStringBuilder, name: String?, pkgLabel: String?, pkg: String?, authorityLabel: String, authority: String, pathLabel: String?, path: String?): SpannableStringBuilder {
        if (name != null) {
            // name
            sb
                .append(name)
                .append('\n')
        }

        // package
        if (pkg != null) {
            sb.append(pkgLabel, 0, Factories.boldFactory)
                .append(' ')
                .append(pkg)
                .append('\n')
        }

        // authority
        sb.append(authorityLabel, 0, Factories.boldFactory)
            .append(':')
            .append(' ')
            .append(authority)
            .append('\n')

        // package
        if (path != null) {
            sb.append(pathLabel, 0, Factories.boldFactory)
                .append(' ')
                .append(path)
                .append('\n')
        }
        sb.append('\n')
        return sb
    }
}
