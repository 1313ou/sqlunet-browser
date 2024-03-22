/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.sqlunet.browser.common.R

/**
 * Home fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
open class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // fab
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { v: View? ->
            val activity = requireActivity()
            val navHostFragment = (activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?)!!
            navHostFragment.navController.navigate(R.id.nav_search_browse)
        }
    }

    override fun onResume() {
        super.onResume()
        val activity = requireActivity() as AppCompatActivity
        val actionBar = activity.supportActionBar!!
        actionBar.customView = null
        actionBar.setBackgroundDrawable(null)
    }
}
