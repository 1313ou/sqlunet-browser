/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.provider

/**
 * SqlUNet provider argument constants
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object ProviderArgs {

    // intent type
    const val ACTION_QUERY = "org.sqlunet.browser.QUERY"

    // parameters
    const val ARG_QUERYPOINTER = "QUERYPOINTER"
    const val ARG_QUERYSTRING = "QUERYSTRING"
    const val ARG_QUERYRECURSE = "QUERYRECURSE"

    // type
    const val ARG_QUERYTYPE = "QUERYTYPE"
    const val ARG_QUERYTYPE_ALL = 0
    const val ARG_QUERYTYPE_WORD = 1
    const val ARG_QUERYTYPE_SYNSET = 2
    const val ARG_QUERYTYPE_SENSE = 3
    const val ARG_QUERYTYPE_TREE = 4
    const val ARG_QUERYTYPE_VNCLASS = 10
    const val ARG_QUERYTYPE_PBROLESET = 20
    const val ARG_QUERYTYPE_FNFRAME = 31
    const val ARG_QUERYTYPE_FNLEXUNIT = 32
    const val ARG_QUERYTYPE_FNSENTENCE = 33
    const val ARG_QUERYTYPE_FNANNOSET = 34
    const val ARG_QUERYTYPE_FNPATTERN = 35
    const val ARG_QUERYTYPE_FNVALENCEUNIT = 36
    const val ARG_QUERYTYPE_FNPREDICATE = 37
    const val ARG_QUERYTYPE_PM = 40
    const val ARG_QUERYTYPE_PMROLE = 41
    const val ARG_QUERYTYPE_COLLOCATIONS = 50
    const val ARG_QUERYTYPE_COLLOCATION = 51

    // tables
    const val ARG_QUERYURI = "QUERYURI"
    const val ARG_QUERYDATABASE = "QUERYDATABASE"
    const val ARG_QUERYID = "QUERYID"
    const val ARG_QUERYIDTYPE = "QUERYIDTYPE"
    const val ARG_QUERYITEMS = "QUERYITEMS"
    const val ARG_QUERYHIDDENITEMS = "QUERYXITEMS"
    const val ARG_QUERYARG = "QUERYARG"
    const val ARG_QUERYLAYOUT = "QUERYLAYOUT"
    const val ARG_QUERYSORT = "QUERYSORT"
    const val ARG_QUERYFILTER = "QUERYFILTER"

    // render
    const val ARG_RENDERPARAMETERS = "RENDERPARAMETERS"
    const val ARG_RENDER_DISPLAY_SEM_RELATION_NAME_KEY = "display_sem_relation_name_key"
    const val ARG_RENDER_DISPLAY_LEX_RELATION_NAME_KEY = "display_lex_relation_name_key"

    // filter
    const val ARG_RELATION_FILTER_KEY = "relation_filter_key"

    // hints
    const val ARG_HINTWORDID = "HINTWORDID"
    const val ARG_HINTWORD = "HINTWORD"
    const val ARG_HINTCASED = "HINTCASED"
    const val ARG_HINTPRONUNCIATION = "HINTPRONUNCIATION"
    const val ARG_HINTPOS = "HINTPOS"
}
