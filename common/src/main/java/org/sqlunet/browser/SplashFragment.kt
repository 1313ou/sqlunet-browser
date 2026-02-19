/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * Splash fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class SplashFragment : Fragment() {

    /**
     * Layout id set in super class
     */
    protected var layoutId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId, container, false)
    }

    companion object {

        const val FRAGMENT_TAG = "splash"
    }
}
