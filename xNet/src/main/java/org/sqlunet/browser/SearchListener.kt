/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser

/**
 * Search interface for fragments
 */
internal fun interface SearchListener {
    fun search(query: String?)
}
