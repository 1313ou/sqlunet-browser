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
	public static final String ACTION_QUERY = "org.sqlunet.browser.QUERY";

	// global
	public static final String ARG_QUERYPOINTER = "QUERYPOINTER";
	public static final String ARG_QUERYSTRING = "QUERYSTRING";
	public static final String ARG_QUERYRECURSE = "QUERYRECURSE";

	// type
	public static final String ARG_QUERYTYPE = "QUERYTYPE";
	public static final int ARG_QUERYTYPE_ALL = 0;
	public static final int ARG_QUERYTYPE_SYNSET = 1;
	public static final int ARG_QUERYTYPE_VNCLASS = 2;
	public static final int ARG_QUERYTYPE_PBROLESET = 3;
	public static final int ARG_QUERYTYPE_FNFRAME = 4;
	public static final int ARG_QUERYTYPE_FNLEXUNIT = 5;
	public static final int ARG_QUERYTYPE_FNSENTENCE = 6;
	public static final int ARG_QUERYTYPE_FNANNOSET = 7;
	public static final int ARG_QUERYTYPE_FNPATTERN = 8;
	public static final int ARG_QUERYTYPE_FNVALENCEUNIT = 9;
	public static final int ARG_QUERYTYPE_FNPREDICATE = 10;
	public static final int ARG_QUERYTYPE_PM = 11;
	public static final int ARG_QUERYTYPE_PMROLE = 12;

	// tables
	public static final String ARG_QUERYURI = "QUERYURI";
	public static final String ARG_QUERYDATABASE = "QUERYDATABASE";
	public static final String ARG_QUERYID = "QUERYID";
	public static final String ARG_QUERYITEMS = "QUERYITEMS";
	public static final String ARG_QUERYHIDDENITEMS = "QUERYXITEMS";
	public static final String ARG_QUERYARG = "QUERYARG";
	public static final String ARG_QUERYLAYOUT = "QUERYLAYOUT";
	public static final String ARG_QUERYSORT = "QUERYSORT";
	public static final String ARG_QUERYFILTER = "QUERYFILTER";
}
