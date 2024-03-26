/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.ewn

import androidx.appcompat.app.AppCompatDelegate
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ColorsDayTest : AbstractColors() {

    override val mode: Int
        get() = AppCompatDelegate.MODE_NIGHT_NO
}
