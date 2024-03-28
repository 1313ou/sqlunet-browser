/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.speak

import java.util.regex.Pattern

open class Pronunciation(@JvmField val ipa: String, variety: String?) : Comparable<Pronunciation> {

    @JvmField
    val variety: String?

    fun hasVariety(): Boolean {
        return variety != null
    }

    override fun compareTo(other: Pronunciation): Int {
        return COMPARATOR.compare(this, other)
    }

    override fun toString(): String {
        return if (variety == null) String.format("/%s/", ipa) else String.format("[%s] /%s/", variety, ipa)
    }

    init {
        this.variety = variety
    }

    companion object {
        val COMPARATOR = java.util.Comparator { p1: Pronunciation, p2: Pronunciation ->
            if (p1.variety == null && p2.variety == null) {
                return@Comparator 0
            }
            if (p1.variety == null) {
                return@Comparator -1
            }
            if (p2.variety == null) {
                return@Comparator 1
            }

            // priority
            val priority1 = priority(p1.variety)
            val priority2 = priority(p2.variety)
            var c = priority1.compareTo(priority2)
            if (c != 0) {
                return@Comparator c
            }

            // name
            c = p1.variety.compareTo(p2.variety, ignoreCase = true)
            if (c == 0) {
                return@Comparator 0
            }
            p1.ipa.compareTo(p2.ipa)
        }

        private fun priority(s: String): Int {
            return when (s) {
                "GB" -> -5
                "US" -> -4
                else -> 0
            }
        }

        private fun toStrings(pronunciations: List<Pronunciation>): Array<String?> {
            val n = pronunciations.size
            val result = arrayOfNulls<String>(n)
            for (i in 0 until n) {
                result[i] = pronunciations[i].toString()
            }
            return result
        }

        private val PATTERN = Pattern.compile("\\[(..)] /(.*)/")

        private val PATTERN2 = Pattern.compile("/(.*)/")

        @JvmStatic
        fun pronunciations(pronunciationBundle: String?): List<Pronunciation>? {
            if (pronunciationBundle == null) {
                return null
            }
            val pronunciations = pronunciationBundle.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val result: MutableList<Pronunciation> = ArrayList()
            for (pronunciation in pronunciations) {
                val pronunciation2 = pronunciation.trim { it <= ' ' }
                val m = PATTERN.matcher(pronunciation2)
                if (m.find()) {
                    val ipa = m.group(2)
                    val country = m.group(1)
                    result.add(Pronunciation(ipa!!, country))
                } else {
                    val m2 = PATTERN2.matcher(pronunciation2)
                    if (m2.find()) {
                        val ipa = m2.group(1)
                        result.add(Pronunciation(ipa!!, null))
                    }
                }
            }
            result.sort()
            return result
        }

        @JvmStatic
        fun sortedPronunciations(pronunciationBundle: String?): String? {
            if (pronunciationBundle == null) {
                return null
            }
            val pronunciations = pronunciations(pronunciationBundle)
            return java.lang.String.join(",", *toStrings(pronunciations!!))
        }
    }
}
