/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>.
 */
package com.bbou.download

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.bbou.download.common.R
import org.sqlunet.browser.BaseActivity
import org.sqlunet.core.R as CoreR

/**
 * Update activity
 */
class UpdateActivity : BaseActivity() {

    /**
     * onCreate
     *
     * @param savedInstanceState saved instance state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // content view
        setContentView(R.layout.activity_update)

        // toolbar
        val toolbar = findViewById<Toolbar>(CoreR.id.toolbar)
        setSupportActionBar(toolbar)

        // set up the action bar
        val actionBar = supportActionBar!!
        actionBar.displayOptions = ActionBar.DISPLAY_SHOW_TITLE
    }
}
