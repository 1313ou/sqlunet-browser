/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.app.Application
import android.database.Cursor
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bbou.coroutines.BaseTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.sqlunet.browser.Module.ContentProviderSql
import org.sqlunet.view.TreeOp

class SqlunetViewTreeModel(application: Application) : AndroidViewModel(application) {

    fun interface ToTreeOps {

        fun cursorToTreeOps(cursor: Cursor): Array<TreeOp>
    }

    val data = MutableLiveData<Array<TreeOp>?>()

    fun loadData(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?, treeConverter: ToTreeOps) {
        val task = object : BaseTask<Void?, Array<TreeOp>?>() {

            override suspend fun doJob(params: Void?): Array<TreeOp>? {
                val cursor = getApplication<Application>().contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
                return if (cursor == null) null else treeConverter.cursorToTreeOps(cursor)
            }
        }
        viewModelScope.launch {
            val result = task.run(Dispatchers.Default, null)
            data.value = result
        }
    }

    fun loadData(uri: Uri, sql: ContentProviderSql, treeConverter: ToTreeOps) {
        loadData(uri, sql.projection, sql.selection, sql.selectionArgs, sql.sortBy, treeConverter)
    }
}
