/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.sqlunet.browser.common.R
import org.sqlunet.provider.BaseProvider
import org.sqlunet.sql.SqlFormatter.styledFormat

/**
 * Sql fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SqlStatementsFragment : Fragment() {

    private lateinit var adapter: SqlStatementsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sql_statements, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SqlStatementsAdapter(getSqlStatements())
        val recyclerView = view.findViewById<RecyclerView>(R.id.statements_list).apply {
            setHasFixedSize(true)
            val itemDecorator = DividerItemDecoration(requireContext(), (layoutManager as LinearLayoutManager).orientation)
            itemDecorator.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider_sql)!!)
            addItemDecoration(itemDecorator)
        }
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        update()
    }

    private fun getSqlStatements(): Array<out CharSequence?> {
        val sqls = BaseProvider.sqlBuffer.reverseItems()
        for (i in sqls.indices) {
            sqls[i] = styledFormat(sqls[i]!!)
        }
        return if (sqls.isNotEmpty()) sqls else arrayOf<CharSequence>("empty")
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update() {
        if (!this::adapter.isInitialized) {
            return
        }
        adapter.dataSet = getSqlStatements()
        adapter.notifyDataSetChanged()
    }

    inner class SqlStatementsAdapter(var dataSet: Array<out CharSequence?>) : RecyclerView.Adapter<SqlStatementsAdapter.SqlStatementsViewHolder>() {

        /**
         * Tracks activated item position
         */
        private var activatedPosition = RecyclerView.NO_POSITION

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SqlStatementsViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sql, parent, false)
            return SqlStatementsViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: SqlStatementsViewHolder, position: Int) {
            val statement = dataSet[position]!!
            viewHolder.textView.text = statement
            viewHolder.itemView.setOnLongClickListener {

                val position2 = viewHolder.bindingAdapterPosition
                val statement2 = dataSet[position2]!!
                Log.d(TAG, "Activate position $position2")
                val previouslyActivatedPosition = activatedPosition
                activatedPosition = position2
                if (previouslyActivatedPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(previouslyActivatedPosition)
                }
                notifyItemChanged(activatedPosition)

                activate(viewHolder.itemView.context, statement2)
                true
            }
            viewHolder.itemView.isActivated = position == activatedPosition
        }

        override fun getItemCount() = dataSet.size

        private fun activate(context: Context, statement: CharSequence) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", statement)
            clipboard.setPrimaryClip(clipData)
            Toast.makeText(context, R.string.status_clipboard_copied, Toast.LENGTH_SHORT).show()
        }

        inner class SqlStatementsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val textView: TextView = itemView.findViewById(android.R.id.text1)
        }
    }

    companion object {

        private const val TAG = "SqlStatementsF"
    }
}
