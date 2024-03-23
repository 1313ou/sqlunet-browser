/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.provider

import android.app.SearchManager

/**
 * VerbNet provider contract
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class VerbNetContract {

    object VnWords {
        const val TABLE = Q.WORDS.TABLE
        const val URI = TABLE
        const val VNWORDID = V.VNWORDID
        const val WORDID = V.WORDID
        const val WORD = V.WORD
    }

    object VnClasses {
        const val TABLE = Q.VNCLASSES.TABLE
        const val URI = TABLE
        const val URI1 = "vn_class1"
        const val WORDID = V.WORDID
        const val POS = V.POSID
        const val CLASSID = V.CLASSID
        const val CLASS = V.CLASS
        const val CLASSTAG = V.CLASSTAG
    }

    object VnClasses_X {
        const val URI_BY_VNCLASS = "vnclasses_x_by_vnclass"
        const val WORDID = V.WORDID
        const val POS = V.POSID
        const val CLASSID = V.CLASSID
        const val CLASS = V.CLASS
        const val CLASSTAG = V.CLASSTAG
    }

    object Words_VnClasses {
        const val TABLE = "words_vnclasses"
        const val URI = TABLE
        const val WORDID = V.WORDID
        const val SYNSETID = V.SYNSETID
        const val CLASSID = V.CLASSID
        const val CLASS = V.CLASS
        const val CLASSTAG = V.CLASSTAG
        const val SENSENUM = V.SENSENUM
        const val SENSEKEY = V.SENSEKEY
        const val QUALITY = V.QUALITY
        const val NULLSYNSET = V.NULLSYNSET
    }

    object VnClasses_VnMembers_X {
        const val URI_BY_WORD = "vnclasses_vnmembers_x_by_word"
        const val CLASSID = V.CLASSID
        const val VNWORDID = V.VNWORDID
        const val WORDID = V.WORDID
        const val WORD = V.WORD
        const val DEFINITIONS = V.DEFINITIONS
        const val GROUPINGS = V.GROUPINGS
        const val DEFINITION = V.DEFINITION
        const val GROUPING = V.GROUPING
    }

    object VnClasses_VnRoles_X {
        const val URI_BY_ROLE = "vnclasses_vnroles_x_by_vnrole"
        const val CLASSID = V.CLASSID
        const val ROLEID = V.ROLEID
        const val ROLETYPE = V.ROLETYPE
        const val RESTRS = V.RESTRS
    }

    object VnClasses_VnFrames_X {
        const val URI_BY_FRAME = "vnclasses_vnframes_x_by_vnframe"
        const val CLASSID = V.CLASSID
        const val FRAMEID = V.FRAMEID
        const val FRAMENAME = V.FRAMENAME
        const val FRAMESUBNAME = V.FRAMESUBNAME
        const val SYNTAX = V.SYNTAX
        const val SEMANTICS = V.SEMANTICS
        const val NUMBER = V.NUMBER
        const val XTAG = V.XTAG
        const val EXAMPLE = V.EXAMPLE
        const val EXAMPLES = V.EXAMPLES
    }

    object Lookup_VnExamples {
        const val TABLE = Q.LOOKUP_FTS_EXAMPLES.TABLE
        const val URI = TABLE
        const val EXAMPLEID = V.EXAMPLEID
        const val EXAMPLE = V.EXAMPLE
        const val CLASSID = V.CLASSID
        const val FRAMEID = V.FRAMEID
    }

    object Lookup_VnExamples_X {
        const val URI = "fts_vnexamples_x"
        const val URI_BY_EXAMPLE = "fts_vnexamples_x_by_example"
        const val EXAMPLEID = V.EXAMPLEID
        const val EXAMPLE = V.EXAMPLE
        const val CLASSID = V.CLASSID
        const val CLASS = V.CLASS
        const val FRAMEID = V.FRAMEID
        const val CLASSES = V.CLASSES
        const val FRAMES = V.FRAMES
    }

    object Suggest_VnWords {
        const val SEARCH_WORD_PATH = "suggest_vnword"
        const val URI = SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY
        const val VNWORDID = V.VNWORDID
        const val WORDID = V.WORDID
        const val WORD = V.WORD
    }

    object Suggest_FTS_VnWords {
        const val SEARCH_WORD_PATH = "suggest_fts_vnword"
        const val URI = SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY
        const val VNWORDID = V.VNWORDID
        const val WORDID = V.WORDID
        const val WORD = V.WORD
    }
}