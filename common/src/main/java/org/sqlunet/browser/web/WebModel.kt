/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.web

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bbou.concurrency.Task

class WebModel : ViewModel() {

    private val data = MutableLiveData<String?>()

    fun getData(): LiveData<String?> {
        return data
    }

    fun loadData(loader: DocumentStringLoader) {

        object : Task<Void?, Void, String?>() {
            override fun doJob(params: Void?): String? {
                return loader.getDoc()
            }

            override fun onDone(result: String?) {
                data.value = result
            }
        }.execute(null)
    }
}
