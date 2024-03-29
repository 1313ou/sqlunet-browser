/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet

/**
 * Has extended-id interface
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
interface HasXId {

    /**
     * Get extended class id (class=vn:class id,pb:rolesetid,fn:frame id)
     *
     * @return extended class id (class=vn:class,pb:rolesetid,fn:frame)
     */
    fun getXId(): Long?

    /**
     * Get extended class id (class=vn:class id,pb:roleset id,fn:frame id)
     *
     * @return extended class id (class=vn:class id,pb:roleset id,fn:frame id)
     */
    fun getXClassId(): Long?

    /**
     * Get extended member id (fn:lexunit)
     *
     * @return extended member id (fn:lexunit)
     */
    fun getXMemberId(): Long?

    /**
     * Get extended sources
     *
     * @return extended sources
     */
    fun getXSources(): String?
}
