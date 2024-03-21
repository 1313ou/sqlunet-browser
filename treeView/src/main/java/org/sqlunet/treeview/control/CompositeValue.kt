/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.treeview.control

/**
 * Value for tree item with extra icon
 *
 * @author Bogdan Melnychuk on 2/12/15.
 */
class CompositeValue {

    /**
     * Label text
     */
    @JvmField
    val text: CharSequence

    /**
     * Extra icon
     */
    val icon: Int

    /**
     * Payload
     */
    val payload: Array<out Any?>?

    /**
     * Constructor
     *
     * @param text    label text
     * @param icon    extra icon
     * @param payload payload
     */
    constructor(text: CharSequence, icon: Int, vararg payload: Any?) {
        this.text = text
        this.icon = icon
        this.payload = payload
    }

    /**
     * Constructor
     *
     * @param text label text
     * @param icon extra icon
     */
    constructor(text: CharSequence, icon: Int) {
        this.text = text
        this.icon = icon
        payload = null
    }

    // S T R I N G I F Y

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append('"')
        sb.append(text)
        sb.append('"')
        if (payload != null) {
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
        }
        return sb.toString()
    }
}