/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.provider

import org.sqlunet.loaders.Queries
import org.sqlunet.xnet.provider.Q
import org.sqlunet.xnet.provider.V

/**
 * Extended cross WordNet-FrameNet-PropBank-VerbNet provider contract
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object XNetContract {

    // A L I A S E S

    const val AS_WORDS = V.AS_WORDS
    const val AS_SENSES = V.AS_SENSES
    const val AS_SYNSETS = V.AS_SYNSETS
    const val AS_POSES = V.AS_POSES
    const val AS_CLASSES = V.AS_CLASSES

    object Words_FnWords_PbWords_VnWords {
        const val URI = "words_fnwords_pbwords_vnwords"
        const val WORD = V.WORD
        const val WORDID = V.WORDID
        const val FNWORDID = V.FNWORDID
        const val PBWORDID = V.PBWORDID
        const val VNWORDID = V.VNWORDID
        const val SYNSETID = V.SYNSETID
        const val LUID = V.LUID
        const val SENSEID = V.SENSEID
        const val SENSENUM = V.SENSENUM
        const val SENSEKEY = V.SENSEKEY
        const val POSID = V.POSID
        const val POS = V.POS
        const val DOMAIN = V.DOMAIN
        const val DEFINITION = V.DEFINITION
        const val CASED = V.CASEDWORD
        const val TAGCOUNT = V.TAGCOUNT
        const val SOURCES = V.SOURCES
    }

    object Words_Pronunciations_FnWords_PbWords_VnWords {
        const val URI = "words_pronunciations_fnwords_pbwords_vnwords"
        const val PRONUNCIATION = V.PRONUNCIATION
        const val VARIETY = V.VARIETY
        const val PRONUNCIATIONS = Queries.PRONUNCIATIONS

        const val WORD = Words_FnWords_PbWords_VnWords.WORD
        const val WORDID = Words_FnWords_PbWords_VnWords.WORDID
        const val FNWORDID = Words_FnWords_PbWords_VnWords.FNWORDID
        const val PBWORDID = Words_FnWords_PbWords_VnWords.PBWORDID
        const val VNWORDID = Words_FnWords_PbWords_VnWords.VNWORDID
        const val SYNSETID = Words_FnWords_PbWords_VnWords.SYNSETID
        const val LUID = Words_FnWords_PbWords_VnWords.LUID
        const val SENSEID = Words_FnWords_PbWords_VnWords.SENSEID
        const val SENSENUM = Words_FnWords_PbWords_VnWords.SENSENUM
        const val SENSEKEY = Words_FnWords_PbWords_VnWords.SENSEKEY
        const val POSID = Words_FnWords_PbWords_VnWords.POSID
        const val POS = Words_FnWords_PbWords_VnWords.POS
        const val DOMAIN = Words_FnWords_PbWords_VnWords.DOMAIN
        const val DEFINITION = Words_FnWords_PbWords_VnWords.DEFINITION
        const val CASED = Words_FnWords_PbWords_VnWords.CASED
        const val TAGCOUNT = Words_FnWords_PbWords_VnWords.TAGCOUNT
        const val SOURCES = Words_FnWords_PbWords_VnWords.SOURCES
    }

    object Words_PbWords_VnWords {
        const val URI = "words_pbwords_vnwords"
        const val WORD = V.WORD
        const val WORDID = V.WORDID
        const val PBWORDID = V.PBWORDID
        const val VNWORDID = V.VNWORDID
        const val SYNSETID = V.SYNSETID
        const val LUID = V.LUID
        const val SENSEID = V.SENSEID
        const val SENSENUM = V.SENSENUM
        const val SENSEKEY = V.SENSEKEY
        const val POSID = V.POSID
        const val POS = V.POS
        const val DOMAIN = V.DOMAIN
        const val DEFINITION = V.DEFINITION
        const val CASEDWORD = V.CASEDWORD
        const val TAGCOUNT = V.TAGCOUNT
        const val SOURCES = V.SOURCES
    }

    object Words_VnWords_VnClasses {
        const val URI = "words_vnwords_vnclasses"
        const val WORDID = V.WORDID
        const val SYNSETID = V.SYNSETID
        const val VNWORDID = V.VNWORDID
        const val CLASSID = V.CLASSID
        const val CLASS = V.CLASS
        const val CLASSTAG = V.CLASSTAG
        const val DEFINITION = V.DEFINITION
        const val SOURCE = V.SOURCE
        const val SOURCES = V.SOURCES
    }

    object Words_PbWords_PbRoleSets {
        const val URI = "words_pbwords_pbrolesets"
        const val WORDID = V.WORDID
        const val SYNSETID = V.SYNSETID
        const val ROLESETID = V.ROLESETID
        const val ROLESETNAME = V.ROLESETNAME
        const val ROLESETHEAD = V.ROLESETHEAD
        const val ROLESETDESCR = V.ROLESETDESCR
        const val DEFINITION = V.DEFINITION
        const val SOURCE = V.SOURCE
        const val SOURCES = V.SOURCES
    }

    object Words_XNet {
        const val WORDID = V.WORDID
        const val SYNSETID = V.SYNSETID
        const val XID = V.XID
        const val XCLASSID = V.XCLASSID
        const val XMEMBERID = V.XMEMBERID
        const val XNAME = V.XNAME
        const val XHEADER = V.XHEADER
        const val XINFO = V.XINFO
        const val XDEFINITION = V.XDEFINITION
        const val SOURCE = V.SOURCE
        const val SOURCES = V.SOURCES
    }

    // Sources

    object Sources {
        const val TABLE = Q.SOURCES.TABLE
        const val URI = TABLE
        const val ID = "idsource"
        const val NAME = "name"
        const val VERSION = "version"
        const val URL = "url"
        const val PROVIDER = "provider"
        const val REFERENCE = "reference"
    }

    // Meta

    object Meta {
        const val TABLE = Q.META.TABLE
        const val URI = TABLE
        const val CREATED = "created"
        const val DBSIZE = "dbsize"
        const val BUILD = "build"
    }

    // PredicateMatrix

    object PredicateMatrix {
        const val URI = "predicatematrix"
        const val WORDID = V.WORDID
        const val SYNSETID = V.SYNSETID
        const val VNWORDID = V.VNWORDID
        const val VNCLASSID = V.CLASSID
        const val PBWORDID = V.PBWORDID
        const val PBROLESETID = V.ROLESETID
        const val FNWORDID = V.FNWORDID
        const val LUID = V.LUID
        const val FRAMEID = V.FRAMEID
    }

    object PredicateMatrix_VerbNet {
        const val URI = "predicatematrix_verbnet"
        const val WORDID = V.WORDID
        const val SYNSETID = V.SYNSETID
        const val VNWORDID = V.VNWORDID
        const val CLASSID = V.CLASSID
    }

    object PredicateMatrix_PropBank {
        const val URI = "predicatematrix_propbank"
        const val WORDID = V.WORDID
        const val SYNSETID = V.SYNSETID
        const val PBWORDID = V.PBWORDID
        const val ROLESETID = V.ROLESETID
    }

    object PredicateMatrix_FrameNet {
        const val URI = "predicatematrix_framenet"
        const val WORDID = V.WORDID
        const val SYNSETID = V.SYNSETID
        const val FNWORDID = V.FNWORDID
        const val LUID = V.LUID
        const val FRAMEID = V.FRAMEID
    }

// PredicateMatrix unions

    object Words_XNet_U {
        const val WORDID = V.WORDID
        const val SYNSETID = V.SYNSETID
        const val XID = V.XID
        const val XCLASSID = V.XCLASSID
        const val XMEMBERID = V.XMEMBERID
        const val XNAME = V.XNAME
        const val XHEADER = V.XHEADER
        const val XINFO = V.XINFO
        const val XPRONUNCIATION = V.XPRONUNCIATION
        const val XDEFINITION = V.XDEFINITION
        const val SOURCE = V.SOURCE
        const val SOURCES = V.SOURCES
    }

    object Words_VnWords_VnClasses_U {
        const val URI = "words_vnwords_vnclasses_u"
        const val WORDID = V.WORDID
        const val SYNSETID = V.SYNSETID
        const val VNWORDID = V.VNWORDID
        const val CLASSID = V.CLASSID
        const val CLASS = V.CLASS
        const val CLASSTAG = V.CLASSTAG
        const val DEFINITION = V.DEFINITION
        const val SOURCE = V.SOURCE
        const val SOURCES = V.SOURCES
    }

    object Words_VnWords_VnClasses_1U2 {
        const val URI = "words_vnwords_vnclasses_1u2"
    }

    object Words_VnWords_VnClasses_1 {
        const val URI = "words_vnwords_vnclasses_1"
    }

    object Words_VnWords_VnClasses_2 {
        const val URI = "words_vnwords_vnclasses_2"
    }

    object Words_PbWords_PbRoleSets_U {
        const val URI = "words_pbwords_pbrolesets_u"
        const val WORDID = V.WORDID
        const val SYNSETID = V.SYNSETID
        const val ROLESETID = V.ROLESETID
        const val ROLESETNAME = V.ROLESETNAME
        const val ROLESETHEAD = V.ROLESETHEAD
        const val ROLESETDESCR = V.ROLESETDESCR
        const val DEFINITION = V.DEFINITION
        const val SOURCE = V.SOURCE
        const val SOURCES = V.SOURCES
    }

    object Words_PbWords_PbRoleSets_1 {
        const val URI = "words_pbwords_pbrolesets_1"
    }

    object Words_PbWords_PbRoleSets_2 {
        const val URI = "words_pbwords_pbrolesets_2"
    }

    object Words_PbWords_PbRoleSets_1U2 {
        const val URI = "words_pbwords_pbrolesets_1u2"
    }

    object Words_FnWords_FnFrames_U {
        const val URI = "words_fnwords_fnframes_u"
        const val WORDID = V.WORDID
        const val SYNSETID = V.SYNSETID
        const val FNWORDID = V.FNWORDID
        const val FRAMEID = V.FRAMEID
        const val FRAME = V.FRAME
        const val FRAMEDEFINITION = V.FRAMEDEFINITION
        const val LUID = V.LUID
        const val LEXUNIT = V.LEXUNIT
        const val LUDEFINITION = V.LUDEFINITION
        const val DEFINITION = V.DEFINITION
        const val SOURCE = V.SOURCE
        const val SOURCES = V.SOURCES
    }

    object Words_FnWords_FnFrames_1U2 {
        const val URI = "words_fnwords_fnframes_1u2"
    }

    object Words_FnWords_FnFrames_1 {
        const val URI = "words_fnwords_fnframes_1"
    }

    object Words_FnWords_FnFrames_2 {
        const val URI = "words_fnwords_fnframes_2"
    }
}

