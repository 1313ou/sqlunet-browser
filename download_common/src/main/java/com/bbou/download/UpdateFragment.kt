/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bbou.download.common.R

/**
 * Update fragment
 */
class UpdateFragment : Fragment() {

    /**
     * onCreateView
     *
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState saved instance state
     * @return view
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_update, container, false)
    }

    /**
     * onViewCreated
     *
     * @param view view
     * @param savedInstanceState saved instance state
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // arguments
        val activity: Activity = requireActivity()
        val intent = activity.intent
        val upSrcArg = intent.getStringExtra(UP_SOURCE_ARG)
        val upDateArg = intent.getStringExtra(UP_DATE_ARG)
        val upSizeArg = intent.getStringExtra(UP_SIZE_ARG)
        val upEtagArg = intent.getStringExtra(UP_ETAG_ARG)
        val upVersionArg = intent.getStringExtra(UP_VERSION_ARG)
        val upStaticVersionArg = intent.getStringExtra(UP_STATIC_VERSION_ARG)
        val downNameArg = intent.getStringExtra(DOWN_NAME_ARG)
        val downDateArg = intent.getStringExtra(DOWN_DATE_ARG)
        val downSizeArg = intent.getStringExtra(DOWN_SIZE_ARG)
        val downSourceArg = intent.getStringExtra(DOWN_SOURCE_ARG)
        val downSourceDateArg = intent.getStringExtra(DOWN_SOURCE_DATE_ARG)
        val downSourceSizeArg = intent.getStringExtra(DOWN_SOURCE_SIZE_ARG)
        val downSourceEtagArg = intent.getStringExtra(DOWN_SOURCE_ETAG_ARG)
        val downSourceVersionArg = intent.getStringExtra(DOWN_SOURCE_VERSION_ARG)
        val downSourceStaticVersionArg = intent.getStringExtra(DOWN_SOURCE_STATIC_VERSION_ARG)
        val upSrc = view.findViewById<TextView>(R.id.up_src)
        val upDate = view.findViewById<TextView>(R.id.up_date)
        val upSize = view.findViewById<TextView>(R.id.up_size)
        val upEtag = view.findViewById<TextView>(R.id.up_etag)
        val upVersion = view.findViewById<TextView>(R.id.up_version)
        val upStaticVersion = view.findViewById<TextView>(R.id.up_static_version)
        upSrc.text = upSrcArg
        upDate.text = upDateArg
        upSize.text = upSizeArg
        upEtag.text = upEtagArg
        upVersion.text = upVersionArg
        upStaticVersion.text = upStaticVersionArg
        val downDatapack = view.findViewById<TextView>(R.id.down_datapack)
        val downSource = view.findViewById<TextView>(R.id.down_source)
        val downDate = view.findViewById<TextView>(R.id.down_datapack_date)
        val downSize = view.findViewById<TextView>(R.id.down_datapack_size)
        val downSourceDate = view.findViewById<TextView>(R.id.down_source_date)
        val downSourceSize = view.findViewById<TextView>(R.id.down_source_size)
        val downSourceEtag = view.findViewById<TextView>(R.id.down_source_etag)
        val downSourceVersion = view.findViewById<TextView>(R.id.down_source_version)
        val downSourceStaticVersion = view.findViewById<TextView>(R.id.down_source_static_version)
        downDatapack.text = downNameArg
        downSource.text = downSourceArg
        downDate.text = downDateArg
        downSize.text = downSizeArg
        downSourceDate.text = downSourceDateArg
        downSourceSize.text = downSourceSizeArg
        downSourceEtag.text = downSourceEtagArg
        downSourceVersion.text = downSourceVersionArg
        downSourceStaticVersion.text = downSourceStaticVersionArg
        val newerArg = intent.getBooleanExtra(NEWER_ARG, false)
        val newer = view.findViewById<TextView>(R.id.newer)
        if (newerArg) {
            newer.setTextColor(Color.BLUE)
            newer.setText(R.string.download_newer)
        } else {
            newer.setTextColor(Color.GREEN)
            newer.setText(R.string.download_uptodate)
        }

        // proceed with update button
        // if (newerArg) // do not depend on newer flag
        run {
            val button = view.findViewById<ImageButton>(R.id.update)
            button.visibility = View.VISIBLE
            button.setOnClickListener {
                val activityContext = requireContext()
                confirm(activityContext, R.string.title_activity_update, R.string.askUpdate) {
                    val downloadIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) intent.getParcelableExtra(DOWNLOAD_INTENT_ARG, Intent::class.java) else @Suppress("DEPRECATION") intent.getParcelableExtra(DOWNLOAD_INTENT_ARG)
                    downloadIntent?.let {
                        activityContext.startActivity(downloadIntent)
                        val activity2: Activity? = getActivity()
                        activity2?.finish()
                    }
                }
            }
        }
    }

    companion object {

        /**
         * Up source argument (available upstream)
         */
        const val UP_SOURCE_ARG = "up_source"

        /**
         * Up date argument (available upstream)
         */
        const val UP_DATE_ARG = "up_date"

        /**
         * Up size argument (available upstream)
         */
        const val UP_SIZE_ARG = "up_size"

        /**
         * Up etag argument (available upstream)
         */
        const val UP_ETAG_ARG = "up_etag"

        /**
         * Up version argument (available upstream)
         */
        const val UP_VERSION_ARG = "up_version"

        /**
         * Up static version argument (available upstream)
         */
        const val UP_STATIC_VERSION_ARG = "up_static_version"

        /**
         * Down name argument (already downloaded)
         */
        const val DOWN_NAME_ARG = "down_name"

        /**
         * Down date argument (already downloaded)
         */
        const val DOWN_DATE_ARG = "down_date"

        /**
         * Down size argument (already downloaded)
         */
        const val DOWN_SIZE_ARG = "down_size"

        /**
         * Down source argument (already downloaded)
         */
        const val DOWN_SOURCE_ARG = "down_source"

        /**
         * Down source date argument (already downloaded)
         */
        const val DOWN_SOURCE_DATE_ARG = "down_source_date"

        /**
         * Down source size argument (already downloaded)
         */
        const val DOWN_SOURCE_SIZE_ARG = "down_source_size"

        /**
         * Down source etag argument (already downloaded)
         */
        const val DOWN_SOURCE_ETAG_ARG = "down_source_etag"

        /**
         * Down source version argument (already downloaded)
         */
        const val DOWN_SOURCE_VERSION_ARG = "down_source_version"

        /**
         * Down source static version argument (already downloaded)
         */
        const val DOWN_SOURCE_STATIC_VERSION_ARG = "down_source_static_version"

        /**
         * Down intent argument (already downloaded)
         */
        const val DOWNLOAD_INTENT_ARG = "download_intent"

        /**
         * Newer argument
         */
        const val NEWER_ARG = "newer"

        /**
         * Confirm
         *
         * @param context  context
         * @param titleId  title resource id
         * @param askId    ask resource id
         * @param runnable run if confirmed
         */
        private fun confirm(context: Context, titleId: Int, askId: Int, runnable: Runnable) {
            AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(titleId)
                .setMessage(askId)
                .setPositiveButton(R.string.yes) { _: DialogInterface?, _: Int -> runnable.run() }.setNegativeButton(R.string.no, null).show()
        }
    }
}
