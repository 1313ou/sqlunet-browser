/*
 * Copyright (c) 2026. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser

import android.view.View
import android.view.ViewGroup
import org.sqlunet.browser.common.R as CommonR

open class AppCompatCommonWithDrawerActivity : AppCompatCommonActivity() {

    override val rootView: View by lazy { findViewById<ViewGroup>(CommonR.id.activity_main_sub) }
}