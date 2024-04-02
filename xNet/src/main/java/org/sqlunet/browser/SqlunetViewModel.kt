/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.app.Application
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bbou.coroutines.BaseTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.sqlunet.browser.Module.ContentProviderSql

class SqlunetViewModel(application: Application) : AndroidViewModel(application) {

    fun interface PostProcessor {

        fun postProcess(cursor: Cursor)
    }

    private val mutableData = MutableLiveData<Cursor?>()

    fun getData(): LiveData<Cursor?> {
        return mutableData
    }

    fun loadData(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?, postProcessor: PostProcessor?) {
        Log.d(TAG, "Loading data for $uri")
        val task = object : BaseTask<Void?, Cursor?>() {

            override suspend fun doJob(params: Void?): Cursor? {
                val cursor = getApplication<Application>().contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
                Log.d(TAG, "Loaded data for $uri yielded cursor $cursor")
                if (postProcessor != null && cursor != null) {
                    postProcessor.postProcess(cursor)
                }
                return cursor
            }
        }
        viewModelScope.launch {
            val result = task.run(Dispatchers.Default, null)
            mutableData.value = result
        }
    }

    fun loadData(uri: Uri, sql: ContentProviderSql, postProcessor: PostProcessor?) {
        loadData(uri, sql.projection, sql.selection, sql.selectionArgs, sql.sortBy, postProcessor)
    }

    companion object {

        private const val TAG = "SqlunetViewModel"
    }
}
