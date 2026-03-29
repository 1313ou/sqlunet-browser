/*
 * Copyright (c) 2026. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import org.sqlunet.browser.EdgeToEdge.updateHorizontalPadding
import org.sqlunet.browser.NightMode.isNightMode
import org.sqlunet.core.R
import android.R as AndroidR

/**
 * Common activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT) //(DefaultLightScrim, DefaultDarkScrim)
        )

        val lightBackground = !isNightMode(this)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = lightBackground  // Dark icons on light background
            isAppearanceLightNavigationBars = lightBackground  // Dark nav buttons on light background
        }
    }

    // val toolbar: View? by lazy { findViewById(R.id.toolbar) }
    // val fab: View? by lazy { findViewById(R.id.fab) }

    private val rootView: View? by lazy { findViewById<ViewGroup>(AndroidR.id.content).getChildAt(0) }

    private val appBarLayout: View? by lazy { findViewById(R.id.appbar_layout) }

    private val mainView: View? by lazy { findViewById<ViewGroup>(R.id.main) }

    private val contentView: View? by lazy { findViewById<ViewGroup>(R.id.content) }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (rootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView!!) { _, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                //mainView?.updateHorizontalPadding(systemBars)
                contentView?.updateHorizontalPadding(systemBars)
                appBarLayout?.updateHorizontalPadding(systemBars)
                insets
            }
        }
    }
}