/*
 * Copyright (c) 2024. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.wordnet.settings

private const val PREFIX = "pref_"

/**
 * Relation reference
 *
 * @param pref preference key
 */
enum class RelationReference(
    @JvmField val key: String,
    /**
     * NID in relation table
     */
    @JvmField val id: Int
) {

    HYPERNYM("hypernym", 1), // 0
    HYPONYM("hyponym", 2), // 1
    HYPERNYM_INSTANCE("hypernym_instance", 3), // 2
    HYPONYM_INSTANCE("hyponym_instance", 4), // 3
    HOLONYM_MEMBER("holonym_member", 13), // 4
    HOLONYM_SUBSTANCE("holonym_substance", 15), // 5
    HOLONYM_PART("holonym_part", 11), // 6
    MERONYM_MEMBER("meronym_member", 14), // 7
    MERONYM_SUBSTANCE("meronym_substance", 16), // 8
    MERONYM_PART("meronym_part", 12), // 9

    ENTAILS("entails", 21), // 10
    CAUSES("causes", 23), // 11
    ENTAILED("entailed", 22), // 12
    CAUSED("caused", 24), // 13

    ANTONYM("antonym", 30), // 14
    SIMILAR("similar", 40), // 15
    ALSO("also", 50), // 16
    ATTRIBUTE("attribute", 60), // 17
    PERTAINYM("pertainym",80), // 18
    DERIVATION("derivation",81), // 19
    VERBGROUP("verbgroup", 70), // 20
    PARTICIPLE("participle",71), // 21

    USAGE("usage",96), // 22 is exemplified by
    TOPIC("topic",91), // 23
    REGION("region",93), // 24
    USAGE_MEMBER("hasdomain_usage",95), // 25 exemplifies
    TOPIC_MEMBER("hasdomain_topic", 92), // 26
    REGION_MEMBER("hasdomain_region",94), // 27

    COLLOCATION("collocation", 200), // 28
    ROLES("roles", -100), // 29 state(100) -> bodypart 170
    ;

    /**
     * Preference key
     */
    val pref: String
        get() = PREFIX + key

    /**
     * Mask
     *
     * @return mask
     */
    fun mask(): Long {
        return 1L shl ordinal
    }

    /**
     * Test mask against long value
     *
     * @param aggregate bitmap long value
     * @return true if bit is set
     */
    fun test(aggregate: Long): Boolean {
        return (aggregate and (1L shl ordinal)) != 0L
    }

    companion object {

        /**
         * Default filter
         */
        @JvmField
        val FILTER_DEFAULT: Long = HYPERNYM.mask() or HYPONYM.mask() or
                HOLONYM_MEMBER.mask() or HOLONYM_SUBSTANCE.mask() or HOLONYM_PART.mask() or
                MERONYM_MEMBER.mask() or MERONYM_SUBSTANCE.mask() or MERONYM_PART.mask() or
                CAUSES.mask() or CAUSED.mask() or ENTAILS.mask() or ENTAILED.mask() or
                SIMILAR.mask() or
                ANTONYM.mask()
    }
}
