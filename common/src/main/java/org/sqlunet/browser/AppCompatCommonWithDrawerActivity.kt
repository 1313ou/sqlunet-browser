/*
 * Copyright (c) 2026. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import org.sqlunet.browser.EdgeToEdge.handleInsets
import org.sqlunet.browser.common.R

open class AppCompatCommonWithDrawerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        handleInsets(this, R.id.activity_main_sub)
    }
}