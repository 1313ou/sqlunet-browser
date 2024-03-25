/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.sqlunet.browser.common.R

/**
 * About fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class AboutFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // fragment
        val fragment: Fragment = SourceFragment()
        getChildFragmentManager() //
            .beginTransaction() //
            .setReorderingAllowed(true) //
            .replace(R.id.container_source, fragment) //
            .commit()
    }
}
