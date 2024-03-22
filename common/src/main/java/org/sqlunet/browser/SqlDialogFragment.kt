/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import org.sqlunet.browser.common.R

/**
 * Sql dialog fragment
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class SqlDialogFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sql_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // title
        val dialog = dialog!!
        dialog.setTitle(R.string.title_dialog_sql)

        // sub fragment
        val fragment: Fragment = SqlStatementsFragment()
        assert(isAdded)
        getChildFragmentManager() //
            .beginTransaction() //
            .setReorderingAllowed(true) //
            .replace(R.id.container_sql_statements, fragment) //
            .commit()
    }

    companion object {
        fun show(manager: FragmentManager) {
            val dialogFragment = SqlDialogFragment()
            dialogFragment.show(manager, "sql")
        }
    }
}
