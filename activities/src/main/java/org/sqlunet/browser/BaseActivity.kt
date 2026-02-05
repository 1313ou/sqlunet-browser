/*
 * Copyright (c) 2026. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import org.sqlunet.browser.EdgeToEdge.handleInsets
import org.sqlunet.browser.EdgeToEdge.handleInsetsBottom
import org.sqlunet.browser.EdgeToEdge.handleInsetsTop
import android.R as AndroidR
import org.sqlunet.activities.R as ActivitiesR

/**
 * Common activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Set transparent bars
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        /*
        enableEdgeToEdge(
            /*
            statusBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
             */
            statusBarStyle = SystemBarStyle.auto(
                Color.GREEN,
                Color.GREEN,
            ),
            navigationBarStyle = SystemBarStyle.auto(
                Color.RED,
                Color.RED
            )
       )
         */

        // from res
        // window.setBackgroundDrawableResource(android.R.color.holo_red_light)
        // window.setBackgroundDrawableResource(ActivitiesR.color.primaryColor)

        // from attr
        /*
        val attr = AndroidR.attr.colorPrimary // AndroidR.attr.windowBackground,AndroidR.attr.colorBackground,
        val typedValue = TypedValue()
        theme.resolveAttribute(attr, typedValue, true)
        window.setBackgroundDrawable(typedValue.data.toDrawable())
         */
        val lightBackground = true
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = lightBackground  // Dark icons on light background
            isAppearanceLightNavigationBars = lightBackground  // Dark nav buttons on light background
        }
    }

    open val rootView: View by lazy { findViewById<ViewGroup>(AndroidR.id.content).getChildAt(0) }
    open val toolbar: View by lazy { findViewById(ActivitiesR.id.toolbar) }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // handleInsets(rootView)
        handleInsetsTop(toolbar)
        // handleInsetsBottom(rootView)
    }
}