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
import com.bbou.concurrency.Task
import org.sqlunet.browser.Module.ContentProviderSql

class SqlunetViewModel(application: Application) : AndroidViewModel(application) {

    fun interface PostProcessor {

        fun postProcess(cursor: Cursor)
    }

    val mutableData = MutableLiveData<Cursor?>()

    fun getData(): LiveData<Cursor?> {
        return mutableData
    }

    fun loadData(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?, postProcessor: PostProcessor?) {
        Log.d(TAG, "Loading data for $uri")
        object : Task<Void?, Void, Cursor?>() {

            override fun doJob(params: Void?): Cursor? {
                val cursor = getApplication<Application>().contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
                Log.d(TAG, "Loaded data for $uri yielded cursor $cursor")
                if (postProcessor != null && cursor != null) {
                    postProcessor.postProcess(cursor)
                }
                return cursor
            }

            override fun onDone(result: Cursor?) {
                mutableData.value = result
            }
        }.execute(null)
    }

    fun loadData(uri: Uri, sql: ContentProviderSql, postProcessor: PostProcessor?) {
        loadData(uri, sql.projection, sql.selection, sql.selectionArgs, sql.sortBy, postProcessor)
    }

    companion object {

        private const val TAG = "SqlunetViewModel"
    }
}
