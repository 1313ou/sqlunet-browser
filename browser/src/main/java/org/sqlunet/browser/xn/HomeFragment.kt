/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.xn

import android.os.Bundle
import android.view.View
import org.sqlunet.browser.HomeFragment
import org.sqlunet.browser.R

/**
 * Home fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class HomeFragment : HomeFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val homeImage = view.findViewById<HomeImageView>(R.id.splash)
        homeImage.init()
    }
}
