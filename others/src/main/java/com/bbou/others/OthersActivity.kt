package com.bbou.others

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import org.sqlunet.browser.BaseActivity
import org.sqlunet.core.R as CoreR

/**
 * Other applications
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class OthersActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // content view
        setContentView(R.layout.activity_other)

        // toolbar
        val toolbar = findViewById<Toolbar>(CoreR.id.toolbar)
        setSupportActionBar(toolbar)

        // set up the action bar
        val actionBar = supportActionBar!!
        actionBar.displayOptions = ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE

        // menu listeners
        findViewById<Button>(R.id.market_grammarscope_syntaxnet)?.setOnClickListener { install(R.string.grammarscope_syntaxnet_uri) }
        findViewById<Button>(R.id.market_grammarscope_syntaxnet_premium)?.setOnClickListener { install(R.string.grammarscope_syntaxnet_premium_uri) }
        findViewById<Button>(R.id.market_grammarscope_udpipe)?.setOnClickListener { install(R.string.grammarscope_udpipe_uri) }
        findViewById<Button>(R.id.market_grammarscope_udpipe_premium)?.setOnClickListener { install(R.string.grammarscope_udpipe_premium_uri) }
        findViewById<Button>(R.id.market_grammarscope_corenlp)?.setOnClickListener { install(R.string.grammarscope_corenlp_uri) }
        findViewById<Button>(R.id.market_grammarscope_corenlp_premium)?.setOnClickListener { install(R.string.grammarscope_corenlp_premium_uri) }
        findViewById<Button>(R.id.market_treebolic_wordnet)?.setOnClickListener { install(R.string.semantikos_uri) }
        findViewById<Button>(R.id.market_semantikos)?.setOnClickListener { install(R.string.treebolic_wordnet_uri) }
        findViewById<Button>(R.id.market_semantikos_wn)?.setOnClickListener { install(R.string.semantikos_wn_uri) }
        findViewById<Button>(R.id.market_semantikos_ewn)?.setOnClickListener { install(R.string.semantikos_ewn_uri) }
        findViewById<Button>(R.id.market_semantikos_vn)?.setOnClickListener { install(R.string.semantikos_vn_uri) }
        findViewById<Button>(R.id.market_semantikos_fn)?.setOnClickListener { install(R.string.semantikos_fn_uri) }
        findViewById<Button>(R.id.market_semantikos_sn)?.setOnClickListener { install(R.string.semantikos_sn_uri) }
    }

    private fun install(@StringRes uri: Int) {
        install(getString(uri))
    }

    private fun install(uri: String) {
        install(uri, this)
    }

    companion object {

        fun install(uri: String?, activity: Activity) {
            val goToMarket = Intent(Intent.ACTION_VIEW).setData(uri!!.toUri())
            try {
                activity.startActivity(goToMarket)
            } catch (_: ActivityNotFoundException) {
                var message: String? = activity.getString(R.string.market_fail)
                message += ' '
                message += uri
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
            }
        }

        private fun isAppInstalled(uri: String, context: Context): Boolean {
            val packageManager = context.packageManager
            val isInstalled: Boolean = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    packageManager.getPackageInfo(uri, PackageManager.PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong()))
                } else {
                    packageManager.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
                }
                true
            } catch (_: PackageManager.NameNotFoundException) {
                false
            }
            return isInstalled
        }
    }
}
