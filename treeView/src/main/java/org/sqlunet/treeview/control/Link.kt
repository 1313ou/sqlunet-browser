/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.treeview.control

/**
 * Link Data
 *
 * @param id id
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
abstract class Link protected constructor(
    /**
     * Id used in link
     */
    @JvmField val id: Long,
) {

    /**
     * Process
     */
    abstract fun process()
}
