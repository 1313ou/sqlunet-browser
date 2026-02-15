/*
 * Copyright (c) 2026. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser

import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.sqlunet.browser.common.R
import org.sqlunet.provider.XNetContract

class SourcesAdapter(private var cursor: Cursor?) : RecyclerView.Adapter<SourcesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_source, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (cursor?.moveToPosition(position) == true) {
            holder.bind(cursor!!)
        }
    }

    override fun getItemCount(): Int {
        return cursor?.count ?: 0
    }

    fun swapCursor(newCursor: Cursor?) {
        if (cursor != newCursor) {
            val oldCursor = cursor
            cursor = newCursor
            notifyDataSetChanged()
            oldCursor?.close()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameView: TextView = itemView.findViewById(R.id.name)
        private val versionView: TextView = itemView.findViewById(R.id.version)
        private val urlView: TextView = itemView.findViewById(R.id.url)
        private val providerView: TextView = itemView.findViewById(R.id.provider)
        private val referenceView: TextView = itemView.findViewById(R.id.reference)

        fun bind(cursor: Cursor) {
            val nameIdx = cursor.getColumnIndex(XNetContract.Sources.NAME)
            val versionIdx = cursor.getColumnIndex(XNetContract.Sources.VERSION)
            val urlIdx = cursor.getColumnIndex(XNetContract.Sources.URL)
            val providerIdx = cursor.getColumnIndex(XNetContract.Sources.PROVIDER)
            val referenceIdx = cursor.getColumnIndex(XNetContract.Sources.REFERENCE)

            if (nameIdx != -1) {
                nameView.text = cursor.getString(nameIdx)
            }
            if (versionIdx != -1) {
                versionView.text = cursor.getString(versionIdx)
            }
            if (urlIdx != -1) {
                urlView.text = cursor.getString(urlIdx)
            }
            if (providerIdx != -1) {
                providerView.text = cursor.getString(providerIdx)
            }
            if (referenceIdx != -1) {
                referenceView.text = cursor.getString(referenceIdx)
            }
        }
    }
}
