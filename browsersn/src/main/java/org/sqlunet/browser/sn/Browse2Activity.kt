/*
 * Copyright (c) 2023. Bernard Bou <1313ou@gmail.com>
 */
package org.sqlunet.browser.sn

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import org.sqlunet.browser.AbstractBrowse2Activity
import org.sqlunet.browser.BaseBrowse2Fragment
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

        // fragment
        val browse2Fragment = Browse2Fragment()
        val alt = intent.getBooleanExtra(Browse2Fragment.ARG_ALT, false)
        val args = Bundle()
        args.putBoolean(Browse2Fragment.ARG_ALT, alt)
        browse2Fragment.setArguments(args)
        supportFragmentManager 
            .beginTransaction() 
            .setReorderingAllowed(true) 
            .replace(R.id.container_browse2, browse2Fragment, BaseBrowse2Fragment.FRAGMENT_TAG) 
            // .addToBackStack(BaseBrowse2Fragment.FRAGMENT_TAG) 
            .commit()
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
        val fragment = (supportFragmentManager.findFragmentByTag(BaseBrowse2Fragment.FRAGMENT_TAG) as Browse2Fragment?)!!
        fragment.search(pointer, word, cased, pronunciation, pos)
    }

    // M E N U
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // inflate the menu; this adds items to the type bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        // MenuCompat.setGroupDividerEnabled(menu, true)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return menuDispatch(this, item)
    }
}
