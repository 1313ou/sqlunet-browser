/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.fn

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import org.sqlunet.browser.AbstractBrowse2Activity
import org.sqlunet.browser.MenuHandler.menuDispatch
import org.sqlunet.browser.getParcelable
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

        //final int type = args.getInt(ProviderArgs.ARG_QUERYTYPE);
        val pointer = getParcelable(args, ProviderArgs.ARG_QUERYPOINTER)
        val fragment = (supportFragmentManager.findFragmentById(R.id.fragment_detail) as Browse2Fragment?)!!
        fragment.search(pointer, null, null, null, null)
    }

    // M E N U

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // inflate the menu; this adds items to the type bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        // MenuCompat.setGroupDividerEnabled(menu, true);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return menuDispatch(this, item)
    }
}
