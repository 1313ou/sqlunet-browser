/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.sql

/**
 * Domain, utility class to encapsulate domain data
 *
 * @param id     domain id
 * @param posId  part-of-speech id
 * @param name   domain name
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
internal class Domain(
    @JvmField val id: Int,
    @JvmField val posId: Int,
    name: String,
) {

    @JvmField
    val domainName: String

    init {
        domainName = getDomainName(name)
    }

    /**
     * Get domain name
     *
     * @param string full domain name
     * @return the domain name
     */
    private fun getDomainName(string: String): String {
        val index = string.indexOf('.')
        return if (index == -1) string else string.substring(index + 1)
    }
}