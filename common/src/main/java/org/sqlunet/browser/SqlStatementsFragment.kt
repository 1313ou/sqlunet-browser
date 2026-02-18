/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
}
