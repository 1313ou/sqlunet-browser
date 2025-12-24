/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

import android.os.Parcelable
import org.sqlunet.treeview.model.TreeNode

/**
 * Abstract module to perform queries
 * @param fragment fragment
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class Module protected constructor(protected val fragment: TreeFragment) {

    class ContentProviderSql {

        lateinit var providerUri: String

        var projection: Array<String>? = null

        var selection: String? = null

        var selectionArgs: Array<String>? = null

        var sortBy: String? = null

        constructor()

        constructor(providerUri: String, projection: Array<String>, selection: String?, selectionArgs: Array<String>, sortBy: String?) {
            this.providerUri = providerUri
            this.projection = projection
            this.selection = selection
            this.selectionArgs = selectionArgs
            this.sortBy = sortBy
        }

        override fun toString(): String {
            return "ContentProviderSql{" + "providerUri='" + providerUri + '\'' + ", projection=" + projection.contentToString() + ", selection='" + selection + '\'' + ", selectionArgs=" + selectionArgs.contentToString() + ", sortBy='" + sortBy + '\'' + '}'
        }
    }

    /**
     * Type of query (expected result)
     */
    protected var type = 0

    /**
     * Init
     *
     * @param type    type
     * @param pointer parceled pointer
     */
    fun init(type: Int, pointer: Parcelable) {
        this.type = type
        unmarshal(pointer)
    }

    /**
     * Unmarshal
     *
     * @param pointer pointer
     */
    protected abstract fun unmarshal(pointer: Parcelable)

    /**
     * Load and process data
     *
     * @param node tree node to attach results to
     */
    abstract fun process(node: TreeNode)

    /**
     * Instantiate template string
     *
     * @param args args
     * @return instantiated string
     */
    fun String.instantiate(vararg args: Any?): String {
        return String.format(this, *args)
    }
}