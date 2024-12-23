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
    @JvmField val pref: String
) {
    HYPERNYM(PREFIX + "hypernym"), // 0
    HYPONYM(PREFIX + "hyponym"), // 1
    HYPERNYM_INSTANCE(PREFIX + "hypernym_instance"), // 2
    HYPONYM_INSTANCE(PREFIX + "hyponym_instance"), // 3
    HOLONYM_MEMBER(PREFIX + "holonym_member"), // 4
    HOLONYM_SUBSTANCE(PREFIX + "holonym_substance"), // 5
    HOLONYM_PART(PREFIX + "holonym_part"), // 6
    MERONYM_MEMBER(PREFIX + "meronym_member"), // 7
    MERONYM_SUBSTANCE(PREFIX + "meronym_substance"), // 8
    MERONYM_PART(PREFIX + "meronym_part"), // 9

    ENTAILS(PREFIX + "entails"), // 10
    CAUSES(PREFIX + "causes"), // 11
    ENTAILED(PREFIX + "entailed"), // 12
    CAUSED(PREFIX + "caused"), // 13

    ANTONYM(PREFIX + "antonym"), // 14
    SIMILAR(PREFIX + "similar"), // 15
    ALSO(PREFIX + "also"), // 16
    ATTRIBUTE(PREFIX + "attribute"), // 17
    PERTAINYM(PREFIX + "pertainym"), // 18
    DERIVATION(PREFIX + "derivation"), // 19
    DERIVATION_ADJ(PREFIX + "derivation_adj"), // 20
    VERBGROUP(PREFIX + "verbgroup"), // 21
    PARTICIPLE(PREFIX + "participle"), // 22

    USAGE(PREFIX + "usage"), // 23
    TOPIC(PREFIX + "topic"), // 24
    REGION(PREFIX + "region"), // 25
    USAGE_MEMBER(PREFIX + "hasdomain_usage"), // 26
    TOPIC_MEMBER(PREFIX + "hasdomain_topic"), // 27
    REGION_MEMBER(PREFIX + "hasdomain_region"), // 28

    COLLOCATION(PREFIX + "collocation"), // 29
    ROLES(PREFIX + "roles"), // 30
    ;

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
        val FILTERDEFAULT: Long = HYPERNYM.mask() or HYPONYM.mask() or
                HOLONYM_MEMBER.mask() or HOLONYM_SUBSTANCE.mask() or HOLONYM_PART.mask() or
                MERONYM_MEMBER.mask() or MERONYM_SUBSTANCE.mask() or MERONYM_PART.mask() or
                CAUSES.mask() or CAUSED.mask() or ENTAILS.mask() or ENTAILED.mask() or
                SIMILAR.mask() or
                ANTONYM.mask()
    }
}
