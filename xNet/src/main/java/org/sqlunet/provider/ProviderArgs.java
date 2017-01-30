package org.sqlunet.provider;

/**
 * SqlUNet provider contract
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("unused")
public final class ProviderArgs
{
	// intent type
	static public final String ACTION_QUERY = "org.sqlunet.browser.QUERY";

	// global
	static public final String ARG_QUERYPOINTER = "QUERYPOINTER";
	static public final String ARG_QUERYSTRING = "QUERYSTRING";
	static public final String ARG_QUERYRECURSE = "QUERYRECURSE";
	static public final String ARG_HINTWORD = "HINTWORD";
	static public final String ARG_HINTCASED = "HINTCASED";
	static public final String ARG_HINTNAME = "HINTNAME";
	static public final String ARG_HINTPOS = "HINTPOS";

	// type
	static public final String ARG_QUERYTYPE = "QUERYTYPE";
	static public final int ARG_QUERYTYPE_ALL = 0;
	static public final int ARG_QUERYTYPE_WORD = 1;
	static public final int ARG_QUERYTYPE_SYNSET = 2;
	static public final int ARG_QUERYTYPE_SENSE = 3;
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

	// tables
	static public final String ARG_QUERYURI = "QUERYURI";
	static public final String ARG_QUERYDATABASE = "QUERYDATABASE";
	static public final String ARG_QUERYID = "QUERYID";
	static public final String ARG_QUERYITEMS = "QUERYITEMS";
	static public final String ARG_QUERYHIDDENITEMS = "QUERYXITEMS";
	static public final String ARG_QUERYARG = "QUERYARG";
	static public final String ARG_QUERYLAYOUT = "QUERYLAYOUT";
	static public final String ARG_QUERYSORT = "QUERYSORT";
	static public final String ARG_QUERYFILTER = "QUERYFILTER";
}
