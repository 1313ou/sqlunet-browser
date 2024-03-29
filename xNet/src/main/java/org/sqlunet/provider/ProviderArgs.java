/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.provider;

/**
 * SqlUNet provider contract
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public final class ProviderArgs
{
	// intent type
	static public final String ACTION_QUERY = "org.sqlunet.browser.QUERY";

	// parameters
	static public final String ARG_QUERYPOINTER = "QUERYPOINTER";
	static public final String ARG_QUERYSTRING = "QUERYSTRING";
	static public final String ARG_QUERYRECURSE = "QUERYRECURSE";
	// type
	static public final String ARG_QUERYTYPE = "QUERYTYPE";
	static public final int ARG_QUERYTYPE_ALL = 0;
	static public final int ARG_QUERYTYPE_WORD = 1;
	static public final int ARG_QUERYTYPE_SYNSET = 2;
	static public final int ARG_QUERYTYPE_SENSE = 3;
	static public final int ARG_QUERYTYPE_TREE = 4;
	static public final int ARG_QUERYTYPE_VNCLASS = 10;
	static public final int ARG_QUERYTYPE_PBROLESET = 20;
	static public final int ARG_QUERYTYPE_FNFRAME = 31;
	static public final int ARG_QUERYTYPE_FNLEXUNIT = 32;
	static public final int ARG_QUERYTYPE_FNSENTENCE = 33;
	static public final int ARG_QUERYTYPE_FNANNOSET = 34;
	static public final int ARG_QUERYTYPE_FNPATTERN = 35;
	static public final int ARG_QUERYTYPE_FNVALENCEUNIT = 36;
	static public final int ARG_QUERYTYPE_FNPREDICATE = 37;
	static public final int ARG_QUERYTYPE_PM = 40;
	static public final int ARG_QUERYTYPE_PMROLE = 41;
	static public final int ARG_QUERYTYPE_COLLOCATIONS = 50;
	static public final int ARG_QUERYTYPE_COLLOCATION = 51;

	// tables
	static public final String ARG_QUERYURI = "QUERYURI";
	static public final String ARG_QUERYDATABASE = "QUERYDATABASE";
	static public final String ARG_QUERYID = "QUERYID";
	static public final String ARG_QUERYIDTYPE = "QUERYIDTYPE";
	static public final String ARG_QUERYITEMS = "QUERYITEMS";
	static public final String ARG_QUERYHIDDENITEMS = "QUERYXITEMS";
	static public final String ARG_QUERYARG = "QUERYARG";
	static public final String ARG_QUERYLAYOUT = "QUERYLAYOUT";
	static public final String ARG_QUERYSORT = "QUERYSORT";
	static public final String ARG_QUERYFILTER = "QUERYFILTER";

	// render
	static public final String ARG_RENDERPARAMETERS = "RENDERPARAMETERS";
	static public final String ARG_RENDER_DISPLAY_SEM_RELATION_NAME_KEY = "display_sem_relation_name_key";
	static public final String ARG_RENDER_DISPLAY_LEX_RELATION_NAME_KEY ="display_lex_relation_name_key";

	// hints
	static public final String ARG_HINTWORDID = "HINTWORDID";
	static public final String ARG_HINTWORD = "HINTWORD";
	static public final String ARG_HINTCASED = "HINTCASED";
	static public final String ARG_HINTPRONUNCIATION = "HINTPRONUNCIATION";
	static public final String ARG_HINTPOS = "HINTPOS";
}
