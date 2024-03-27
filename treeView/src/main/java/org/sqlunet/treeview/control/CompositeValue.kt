/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.treeview.control

/**
 * Value for tree item with extra icon
 *
 * @param text    label text
 * @param icon    extra icon
 * @param payload payload
 *
 * @author Bogdan Melnychuk on 2/12/15.
 */
class CompositeValue(
    /**
     * Label text
     */
    @JvmField val text: CharSequence,
    /**
     * Extra icon
     */
    val icon: Int,
    /**
     * Payload
     */
    vararg val payload: Any?,
) {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append('"')
        sb.append(text)
        sb.append('"')
        sb.append(' ')
        sb.append("payload=")
        var first = true
        sb.append('{')
        for (obj in payload) {
            if (first) {
                first = false
            } else {
                sb.append(',')
            }
            sb.append(obj)
        }
        sb.append('}')
        return sb.toString()
    }
}