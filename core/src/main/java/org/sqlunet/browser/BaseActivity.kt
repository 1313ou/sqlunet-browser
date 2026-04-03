/*
 * Copyright (c) 2026. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import org.sqlunet.browser.EdgeToEdge.updateHorizontalMargin
import org.sqlunet.browser.NightMode.createOverrideConfigurationForDayNight
import org.sqlunet.browser.NightMode.isNightMode
import org.sqlunet.core.R
import android.R as AndroidR

/**
 * Common activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class BaseActivity : AppCompatActivity() {

    /**
     * Whether orientation is landscape
     */
    protected var isLandscape: Boolean = false

    protected var wasNightMode: Int = Configuration.UI_MODE_NIGHT_UNDEFINED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Read
        isLandscape = resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
        wasNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        // Resolve custom theme color
        val navBarColor = if (isLandscape) {
            val typedValue = TypedValue()
            theme.resolveAttribute(R.attr.colorCustom, typedValue, true)
            typedValue.data
        } else {
            Color.TRANSPARENT
        }

        // Set the navigation bar style to your themed color instead of TRANSPARENT
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(navBarColor, navBarColor)
        )

        // Ensure contrast
        val lightBackground = !isNightMode(this)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = lightBackground  // Dark icons on light background
            isAppearanceLightNavigationBars = lightBackground  // Dark nav buttons on light background
        }
    }

    // private val toolbar: View? by lazy { findViewById(R.id.toolbar) }

    // private val fab: View? by lazy { findViewById(R.id.fab) }

    // private val appBar: View? by lazy { findViewById(R.id.appbar_layout) }

    // private val contentView: View? by lazy { findViewById(R.id.content) }ase

    // private val searchViewDataView: View? by lazy { findViewById(R.id.search_view_data_container) }

    // private val navView: View? by lazy { findViewById(R.id.nav_view) }

    private val rootView: View? by lazy { findViewById<ViewGroup>(AndroidR.id.content).getChildAt(0) }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (rootView != null && isLandscape) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView!!) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                Log.d(TAG, "Inset listener systemBars=$systemBars")
                view.updateHorizontalMargin(systemBars)
                //navView?.updateHorizontalPadding(systemBars)
                //contentView?.updateHorizontalPadding(systemBars)
                //appBar?.updateHorizontalPadding(systemBars)
                //searchViewDataView?.updateHorizontalPadding(systemBars)
                insets
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "uiMode changed, not recreating. nightMode=${newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK}")

        isLandscape = newConfig.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

        val newNightMode: Int = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (newNightMode != wasNightMode) {
            wasNightMode = newNightMode
            restartActivityClean()
        }
    }

    override fun onNightModeChanged(mode: Int) {
        super.onNightModeChanged(mode)
        val overrideConfig = createOverrideConfigurationForDayNight(this, mode)
        application.onConfigurationChanged(overrideConfig)
    }

    private fun restartActivityClean() {
        val intent = intent.addFlags(
            Intent.FLAG_ACTIVITY_NO_ANIMATION
        )
        finish()
        startActivity(intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }

    companion object {

        private const val TAG = "BaseActivity"
    }
}