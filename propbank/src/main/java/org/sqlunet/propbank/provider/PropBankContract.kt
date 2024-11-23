/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.propbank.provider

import android.app.SearchManager

/**
 * PropBank provider contract
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object PropBankContract {

    // A L I A S E S

    const val AS_EXAMPLES = V.AS_EXAMPLES
    const val AS_RELATIONS = V.AS_RELATIONS
    const val AS_FUNCS = V.AS_FUNCS
    const val AS_ARGS = V.AS_ARGS
    const val AS_WORDS = V.AS_WORDS
    const val AS_PBWORDS = V.AS_PBWORDS
    const val AS_MEMBERS = V.AS_MEMBERS

    object PbWords {

        const val TABLE = Q.PBWORDS.TABLE
        const val URI = TABLE
        const val PBWORDID = V.PBWORDID
        const val WORDID = V.WORDID
        const val WORD = V.WORD
    }

    object PbRoleSets {

        const val TABLE = Q.PBROLESET1.TABLE
        const val URI = TABLE
        const val URI1 = "roleset"
        const val ROLESETID = V.ROLESETID
        const val ROLESETNAME = V.ROLESETNAME
        const val ROLESETDESC = V.ROLESETDESCR
        const val ROLESETHEAD = V.ROLESETHEAD
    }

    object PbRoleSets_X {

        const val URI = "rolesets_x"
        const val URI_BY_ROLESET = URI + "_by_roleset"
        const val ROLESETID = V.ROLESETID
        const val ROLESETNAME = V.ROLESETNAME
        const val ROLESETDESC = V.ROLESETDESCR
        const val ROLESETHEAD = V.ROLESETHEAD
        const val WORD = V.WORD
        const val ALIASES = V.ALIASES
    }

    object Words_PbRoleSets {

        const val TABLE = "words_rolesets"
        const val URI = TABLE
        const val WORDID = V.WORDID
        const val POS = V.POS
        const val ROLESETID = V.ROLESETID
        const val ROLESETNAME = V.ROLESETNAME
        const val ROLESETDESC = V.ROLESETDESCR
        const val ROLESETHEAD = V.ROLESETHEAD
    }

    object PbRoleSets_PbRoles {

        const val TABLE = "rolesets_roles"
        const val URI = TABLE
        const val ROLESETID = V.ROLESETID
        const val ROLEID = V.ROLEID
        const val ROLEDESCR = V.ROLEDESCR
        const val ARGTYPE = V.ARGTYPE
        const val FUNC = V.FUNC
        const val FUNCDESCR = V.FUNCDESCR
        const val VNROLE = V.VNROLE
        const val FNFE = V.FNFE
    }

    object PbRoleSets_PbExamples {

        const val URI = "rolesets_examples"
        const val URI_BY_EXAMPLE = URI + "_by_example"
        const val ROLESETID = V.ROLESETID
        const val TEXT = V.TEXT
        const val REL = V.REL
        const val ARGTYPE = V.ARGTYPE
        const val FUNCNAME = V.FUNC
        const val ROLEDESCR = V.ROLEDESCR
        const val VNROLE = V.VNROLE
        const val FNFE = V.FNFE
        const val ARG = V.ARG
        const val ARGS = V.ARGS
        const val EXAMPLEID = V.EXAMPLEID
    }

    object Lookup_PbExamples {

        const val TABLE = "pb_examples_text_fts4"
        const val URI = TABLE
        const val EXAMPLEID = V.EXAMPLEID
        const val TEXT = V.TEXT
        const val ROLESETID = V.ROLESETID
    }

    object Lookup_PbExamples_X {

        const val URI = "pb_examples_text_x_fts4"
        const val URI_BY_EXAMPLE = URI + "_by_examples"
        const val EXAMPLEID = V.EXAMPLEID
        const val TEXT = V.TEXT
        const val ROLESETID = V.ROLESETID
        const val ROLESET = V.ROLESETNAME
        const val ROLESETS = V.ROLESETS
    }

    object Suggest_PbWords {

        const val SEARCH_WORD_PATH = "suggest_pbword"
        const val URI = SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY
        const val PBWORDID = V.PBWORDID
        const val WORDID = V.WORDID
        const val WORD = V.WORD
    }

    object Suggest_FTS_PbWords {

        const val SEARCH_WORD_PATH = "suggest_fts_pbword"
        const val URI = SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY
        const val PBWORDID = V.PBWORDID
        const val WORDID = V.WORDID
        const val WORD = V.WORD
    }
}
