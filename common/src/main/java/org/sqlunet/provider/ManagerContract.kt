/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.provider

import android.content.Context
import android.content.Intent
import org.sqlunet.browser.config.TableActivity

/**
 * Manager contract
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object ManagerContract {

    /**
     * Query tables and indexes intent factory
     *
     * @param context context
     * @return intent
     */
    fun makeTablesAndIndexesIntent(context: Context?): Intent {
        val intent = Intent(context, TableActivity::class.java)
        intent.putExtra(ProviderArgs.ARG_QUERYURI, ManagerProvider.makeUri(TablesAndIndices.URI))
        intent.putExtra(ProviderArgs.ARG_QUERYID, "rowid")
        intent.putExtra(ProviderArgs.ARG_QUERYITEMS, arrayOf("rowid", TablesAndIndices.TYPE, TablesAndIndices.NAME))
        val order = ("CASE "
                + "WHEN " + TablesAndIndices.TYPE + " = 'table' THEN '1' "
                + "WHEN " + TablesAndIndices.TYPE + " = 'view' THEN '2' "
                + "WHEN " + TablesAndIndices.TYPE + " = 'index' THEN '3' "
                + "ELSE " + TablesAndIndices.TYPE + " END ASC,"
                + TablesAndIndices.NAME + " ASC")
        intent.putExtra(ProviderArgs.ARG_QUERYSORT, order)
        intent.putExtra(ProviderArgs.ARG_QUERYFILTER, "name NOT LIKE 'sqlite_%' AND name NOT LIKE 'android_%'")
        return intent
    }

    /**
     * Table and indices contract
     */
    object TablesAndIndices {

        const val TABLE = "sqlite_master"
        const val URI = TABLE
        const val NAME = "name"
        const val TYPE = "type"
    }
}
