/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import org.sqlunet.browser.common.R

class SqlStatementsAdapter(var dataSet: Array<out CharSequence?>) : RecyclerView.Adapter<SqlStatementsAdapter.SqlStatementsViewHolder>() {

    class SqlStatementsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SqlStatementsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sql, parent, false)
        return SqlStatementsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SqlStatementsViewHolder, position: Int) {
        val statement = dataSet[position]
        holder.textView.text = statement
        holder.itemView.setOnLongClickListener {
            val context = holder.itemView.context
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", statement)
            clipboard.setPrimaryClip(clipData)
            Toast.makeText(context, R.string.status_clipboard_copied, Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun getItemCount() = dataSet.size
}
