/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.provider

/**
 * SyntagNet provider contract
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object SyntagNetContract {

    // A L I A S E S

    const val AS_WORDS1 = V.AS_WORDS1
    const val AS_WORDS2 = V.AS_WORDS2
    const val AS_SYNSETS1 = V.AS_SYNSETS1
    const val AS_SYNSETS2 = V.AS_SYNSETS2
    const val WORD1 = V.WORD1
    const val WORD2 = V.WORD2
    const val POS1 = V.POS1
    const val POS2 = V.POS2
    const val DEFINITION1 = V.DEFINITION1
    const val DEFINITION2 = V.DEFINITION2

    // T A B L E S

    object SnCollocations {
        const val TABLE = Q.COLLOCATIONS.TABLE
        const val URI = TABLE
        const val COLLOCATIONID = V.SYNTAGMID
        const val WORD1ID = V.WORD1ID
        const val WORD2ID = V.WORD2ID
        const val SYNSET1ID = V.SYNSET1ID
        const val SYNSET2ID = V.SYNSET2ID
        const val WORD = V.WORD
    }

    object SnCollocations_X {
        const val URI = "sn_syntagms_x"
        const val COLLOCATIONID = V.SYNTAGMID
        const val WORD1ID = V.WORD1ID
        const val WORD2ID = V.WORD2ID
        const val SYNSET1ID = V.SYNSET1ID
        const val SYNSET2ID = V.SYNSET2ID
        const val WORD = V.WORD
        const val POS = V.POSID
        const val DEFINITION = V.DEFINITION
    }
}
