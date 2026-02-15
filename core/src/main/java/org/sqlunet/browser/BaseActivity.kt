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
import androidx.core.view.WindowInsetsControllerCompat
import org.sqlunet.core.R
import org.sqlunet.browser.NightMode.isNightMode
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

    // open val rootView: View? by lazy { findViewById<ViewGroup>(AndroidR.id.content).getChildAt(0) }
    // override open val rootView: View by lazy { findViewById<ViewGroup>(CommonR.id.activity_main_sub) }
    // open val toolbar: View? by lazy { findViewById(R.id.toolbar) }
    // open val fab: View? by lazy { findViewById(R.id.fab) }

    // override fun onPostCreate(savedInstanceState: Bundle?) {
    //    super.onPostCreate(savedInstanceState)

        // handleInsets(rootView)
        // handleInsetsTop(toolbar)
        // handleInsetsBottom(rootView)
        // handleInsetsBottom(fab)
        // handleInsets2(rootView!!)
    // }
}