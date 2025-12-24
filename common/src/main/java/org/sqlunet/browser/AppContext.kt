/*
 * Copyright (c) 2025. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.browser

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

@SuppressLint("StaticFieldLeak")
object AppContext {

    lateinit var context: Context

    fun init(application: Application) {
        context = application.applicationContext
    }
}