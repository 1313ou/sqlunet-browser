/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.annotation.SuppressLint
import android.app.Application
import android.database.Cursor
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bbou.concurrency.Task
import org.sqlunet.browser.Module.ContentProviderSql
import org.sqlunet.view.TreeOp

class SqlunetViewTreeModel(application: Application) : AndroidViewModel(application) {

    fun interface ToTreeOps {
        fun cursorToTreeOps(cursor: Cursor): Array<TreeOp>
    }

    val data = MutableLiveData<Array<TreeOp>?>()

    @SuppressLint("StaticFieldLeak")
    fun loadData(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?, treeConverter: ToTreeOps) {

        object : Task<Void?, Void?, Array<TreeOp>?>() {

            override fun doJob(params: Void?): Array<TreeOp>? {
                val cursor = getApplication<Application>().contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
                return if (cursor == null) null else treeConverter.cursorToTreeOps(cursor)
            }

            override fun onDone(result: Array<TreeOp>?) {
                if (result != null) {
                    data.value = result
                }
            }
        }.execute(null)
    }

    fun loadData(uri: Uri, sql: ContentProviderSql, treeConverter: ToTreeOps) {
        loadData(uri, sql.projection, sql.selection, sql.selectionArgs, sql.sortBy, treeConverter)
    }
}
