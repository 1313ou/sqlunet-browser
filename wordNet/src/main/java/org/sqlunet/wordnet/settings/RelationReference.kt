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
    HYPERNYM(PREFIX + "hypernym"),
    HYPERNYM_INSTANCE(PREFIX + "hypernym_instance"),
    HYPONYM(PREFIX + "hyponym"),
    HYPONYM_INSTANCE(PREFIX + "hyponym_instance"),
    HOLONYM_MEMBER(PREFIX + "holonym_member"),
    HOLONYM_SUBSTANCE(PREFIX + "holonym_substance"),
    HOLONYM_PART(PREFIX + "holonym_part"),
    MERONYM_MEMBER(PREFIX + "meronym_member"),
    MERONYM_SUBSTANCE(PREFIX + "meronym_substance"),
    MERONYM_PART(PREFIX + "meronym_part"),
    ANTONYM(PREFIX + "antonym"),
    ENTAILS(PREFIX + "entails"),
    CAUSES(PREFIX + "causes"),
    SIMILAR(PREFIX + "similar"),
    ALSO(PREFIX + "also"),
    ATTRIBUTE(PREFIX + "attribute"),
    PERTAINYM(PREFIX + "pertainym"),
    DERIVATION(PREFIX + "derivation"),
    DERIVATION_ADJ(PREFIX + "derivation_adj"),
    VERBGROUP(PREFIX + "verbgroup"),
    PARTICIPLE(PREFIX + "participle"),
    DOMAIN(PREFIX + "domain"),
    TOPIC(PREFIX + "topic"),
    USAGE(PREFIX + "usage"),
    REGION(PREFIX + "region"),
    MEMBER(PREFIX + "hasdomain"),
    TOPIC_MEMBER(PREFIX + "hasdomain_topic"),
    USAGE_MEMBER(PREFIX + "hasdomain_usage"),
    REGION_MEMBER(PREFIX + "hasdomain_region"),
    ENTAILED(PREFIX + "entailed"),
    CAUSED(PREFIX + "caused");

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
