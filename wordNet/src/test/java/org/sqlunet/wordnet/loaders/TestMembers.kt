/*
 * Copyright (c) 2025. Bernard Bou <1313ou@gmail.com>
 */

package org.sqlunet.wordnet.loaders

import org.sqlunet.wordnet.loaders.BaseModule.Companion.extractMembers
import org.junit.Test

class TestMembers {

    @Test
    fun testMemberSorting() {
        val m = extractMembers("e,d|,a|,b|3,c|2")
        print(m.joinToString(separator = "\n"))
    }

}