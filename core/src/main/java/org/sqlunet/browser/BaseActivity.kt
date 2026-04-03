/*
 * Copyright (c) 2026. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser

import android.graphics.Color
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
import org.sqlunet.browser.NightMode.isNightMode
import org.sqlunet.core.R as CoreR
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Read
        isLandscape = resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

        // Resolve custom theme color
        val navBarColor = if (isLandscape) {
            val typedValue = TypedValue()
            theme.resolveAttribute(CoreR.attr.colorCustom, typedValue, true)
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

    private val rootView: View? by lazy { findViewById<ViewGroup>(AndroidR.id.content).getChildAt(0) }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (rootView != null && isLandscape) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView!!) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                Log.d(TAG, "Inset listener systemBars=$systemBars")
                view.updateHorizontalMargin(systemBars)
                insets
            }
        }
    }

    companion object {

        private const val TAG = "BaseActivity"
    }
}