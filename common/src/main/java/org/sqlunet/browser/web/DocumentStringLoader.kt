/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.browser.web

/**
 * Document string loader
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
fun interface DocumentStringLoader {

    /**
     * Get document
     *
     * @return document
     */
    fun getDoc(): String?
}
