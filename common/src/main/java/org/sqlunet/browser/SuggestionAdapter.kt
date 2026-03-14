/*
 * Copyright (c) 2026. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.sqlunet.browser.common.R

class SuggestionAdapter(private val onClick: (String) -> Unit) :
    ListAdapter<Pair<String, Int>, SuggestionAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val textView: TextView = view.findViewById(R.id.suggestion_text)
        val imageView: ImageView = view.findViewById(R.id.suggestion_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_suggestion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (suggestion, type) = getItem(position)
        holder.textView.text = suggestion
        holder.imageView.setImageResource(type)
        holder.itemView.setOnClickListener { onClick(suggestion) }
    }

    object DiffCallback : DiffUtil.ItemCallback<Pair<String, Int>>() {

        override fun areItemsTheSame(oldItem: Pair<String, Int>, newItem: Pair<String, Int>) = oldItem.first == newItem.first
        override fun areContentsTheSame(oldItem: Pair<String, Int>, newItem: Pair<String, Int>) = oldItem.first == newItem.first
    }
}
