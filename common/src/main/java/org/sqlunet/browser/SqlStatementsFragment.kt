/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.Toast
import androidx.fragment.app.ListFragment
import org.sqlunet.browser.common.R
import org.sqlunet.provider.BaseProvider
import org.sqlunet.sql.SqlFormatter.styledFormat

/**
 * Sql fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SqlStatementsFragment : ListFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listView = getListView()
        listView.setOnItemLongClickListener { av: AdapterView<*>, _: View?, pos: Int, _: Long ->
            val statement = av.adapter.getItem(pos) as CharSequence
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", statement)
            clipboard.setPrimaryClip(clipData)
            Toast.makeText(requireContext(), R.string.status_clipboard_copied, Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun onResume() {
        super.onResume()
        update()
    }

    fun update() {
        val context = context
        if (context != null) {
            val sqls = BaseProvider.sqlBuffer.reverseItems()
            for (i in sqls.indices) {
                sqls[i] = styledFormat(sqls[i]!!)
            }
            val adapter: ListAdapter = ArrayAdapter(context, R.layout.item_sql, android.R.id.text1, if (sqls.size > 0) sqls else arrayOf<CharSequence>("empty"))
            setListAdapter(adapter)
        }
    }
}
