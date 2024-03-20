/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.bnc.provider

class BNCContract {
    interface BNCs {
        companion object {
            const val TABLE = Q.BNCS.TABLE
            const val URI = TABLE
            const val WORDID = V.WORDID
            const val POSID = V.POSID
            const val FREQ = V.FREQ
        }
    }

    interface Words_BNCs {
        companion object {
            const val URI = "words_bncs"
            const val WORD = V.WORD
            const val WORDID = V.WORDID
            const val POSID = V.POSID
            const val FREQ = V.FREQ
            const val RANGE = V.RANGE
            const val DISP = V.DISP
            const val FREQ1 = V.FREQ1
            const val RANGE1 = V.RANGE1
            const val DISP1 = V.DISP1
            const val FREQ2 = V.FREQ2
            const val RANGE2 = V.RANGE2
            const val DISP2 = V.DISP2
            const val BNCCONVTASKS = V.BNCCONVTASKS
            const val BNCIMAGINFS = V.BNCIMAGINFS
            const val BNCSPWRS = V.BNCSPWRS
        }
    }
}
