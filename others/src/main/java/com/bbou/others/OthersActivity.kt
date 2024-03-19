package com.bbou.others

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

/**
 * Other applications
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class OthersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // content view
        setContentView(R.layout.activity_other)

        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // set up the action bar
        val actionBar = supportActionBar!!
        actionBar.displayOptions = ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE

        // menu listeners
        findViewById<ImageButton>(R.id.on_market_grammarscope)?.setOnClickListener { onMarketGrammarScope() }
        findViewById<ImageButton>(R.id.on_market_grammarscope_premium)?.setOnClickListener { onMarketGrammarScopePremium() }
        findViewById<ImageButton>(R.id.on_market_grammarscope_udpipe)?.setOnClickListener { onMarketGrammarScopeUDPipe() }
        findViewById<ImageButton>(R.id.on_market_grammarscope_udpipe_premium)?.setOnClickListener { onMarketGrammarScopeUDPipePremium() }
        findViewById<ImageButton>(R.id.on_market_treebolic_wordnet)?.setOnClickListener { onMarketTreebolicWordNet() }
        findViewById<ImageButton>(R.id.on_market_semantikos)?.setOnClickListener { onMarketSemantikos() }
        findViewById<ImageButton>(R.id.on_market_semantikos_wn)?.setOnClickListener { onMarketSemantikosWn() }
        findViewById<ImageButton>(R.id.on_market_semantikos_ewn)?.setOnClickListener { onMarketSemantikosEWn() }
        findViewById<ImageButton>(R.id.on_market_semantikos_vn)?.setOnClickListener { onMarketSemantikosVn() }
        findViewById<ImageButton>(R.id.on_market_semantikos_fn)?.setOnClickListener { onMarketSemantikosFn() }
        findViewById<ImageButton>(R.id.on_market_semantikos_sn)?.setOnClickListener { onMarketSemantikosSn() }
    }

    private fun onMarketGrammarScope() {
        install(R.string.grammarscope_uri)
    }

    private fun onMarketGrammarScopePremium() {
        install(R.string.grammarscope_premium_uri)
    }

    private fun onMarketGrammarScopeUDPipe() {
        install(R.string.grammarscope_udpipe_uri)
    }

    private fun onMarketGrammarScopeUDPipePremium() {
        install(R.string.grammarscope_udpipe_premium_uri)
    }

    private fun onMarketTreebolicWordNet() {
        install(R.string.treebolic_wordnet_uri)
    }

    private fun onMarketSemantikos() {
        install(R.string.semantikos_uri)
    }

    private fun onMarketSemantikosWn() {
        install(R.string.semantikos_wn_uri)
    }

    private fun onMarketSemantikosEWn() {
        install(R.string.semantikos_ewn_uri)
    }

    private fun onMarketSemantikosVn() {
        install(R.string.semantikos_vn_uri)
    }

    private fun onMarketSemantikosFn() {
        install(R.string.semantikos_fn_uri)
    }

    private fun onMarketSemantikosSn() {
        install(R.string.semantikos_sn_uri)
    }

    private fun install(@StringRes uri: Int) {
        install(getString(uri))
    }

    private fun install(uri: String) {
        install(uri, this)
    }

    companion object {

        @JvmStatic
        fun install(uri: String?, activity: Activity) {
            val goToMarket = Intent(Intent.ACTION_VIEW).setData(Uri.parse(uri))
            try {
                activity.startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                var message: String? = activity.getString(R.string.market_fail)
                message += ' '
                message += uri
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
            }
        }

        @Suppress("unused")
        private fun isAppInstalled(uri: String, context: Context): Boolean {
            val packageManager = context.packageManager
            val isInstalled: Boolean = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    packageManager.getPackageInfo(uri, PackageManager.PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong()))
                } else {
                    packageManager.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
                }
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
            return isInstalled
        }
    }
}
