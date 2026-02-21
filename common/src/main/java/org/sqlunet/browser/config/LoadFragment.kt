/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.config

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import org.sqlunet.browser.common.R
import org.sqlunet.browser.config.DownloadIntentFactory.makeIntent

/**
 * Load fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class LoadFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_load, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // buttons
        val assetLoadButton = view.findViewById<Button>(R.id.assetload)
        assetLoadButton.setOnClickListener {
            val activity: Activity = requireActivity()
            val intent = Intent(activity, AssetLoadActivity::class.java)
            intent.addFlags(0)
            activity.startActivity(intent)
        }
        val downloadButton = view.findViewById<Button>(R.id.download)
        downloadButton.setOnClickListener {
            val activity: Activity = requireActivity()
            val intent = makeIntent(activity)
            intent.addFlags(0)
            activity.startActivity(intent)
        }
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener { requireActivity().finish() }
    }
}
