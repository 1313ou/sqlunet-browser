/*
 * Copyright (c) 2026. Bernard Bou <1313ou@gmail.com>
 */

package com.bbou.capture

import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

object Captured {
    private val viewIds = listOf(
        R.id.container_browse2,
        R.id.container_browse2m,
        R.id.container_browse12,
        R.id.container_browse_extra,
        R.id.container_browse,
        R.id.container_senses,
        R.id.container_data,
        R.id.container_selectors,

        R.id.container_searchtext
    )

    fun capturedView(activity: AppCompatActivity): View? =
        viewIds.firstNotNullOfOrNull { id ->
            activity.findViewById<View>(id)?.takeIf { it.width > 0 && it.height > 0 }
        } ?: run {
            Toast.makeText(activity, R.string.status_capture_no_view, Toast.LENGTH_SHORT).show()
            null
        }
}