/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.wn

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import org.sqlunet.browser.AbstractBrowse2Activity
import org.sqlunet.browser.MenuHandler.menuDispatch
import org.sqlunet.browser.getParcelable
import org.sqlunet.browser.wn.lib.R
import org.sqlunet.provider.ProviderArgs

/**
 * Detail activity
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class Browse2Activity : AbstractBrowse2Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // content
        setContentView(R.layout.activity_browse2)

        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // set up the action bar
        val actionBar = supportActionBar!!
        actionBar.displayOptions = ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE
    }

    override fun onPostResume() {
        super.onPostResume()
        val args = intent.extras!!
        //var type = args.getInt(ProviderArgs.ARG_QUERYTYPE)
        val pointer = getParcelable(args, ProviderArgs.ARG_QUERYPOINTER)
        val word = args.getString(ProviderArgs.ARG_HINTWORD)
        val cased = args.getString(ProviderArgs.ARG_HINTCASED)
        val pronunciation = args.getString(ProviderArgs.ARG_HINTPRONUNCIATION)
        val pos = args.getString(ProviderArgs.ARG_HINTPOS)
        val fragment = (supportFragmentManager.findFragmentById(R.id.fragment_detail) as Browse2Fragment?)!!
        fragment.search(pointer, word, cased, pronunciation, pos)
    }

    // M E N U
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        // MenuCompat.setGroupDividerEnabled(menu, true)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return menuDispatch(this, item)
    }
}
