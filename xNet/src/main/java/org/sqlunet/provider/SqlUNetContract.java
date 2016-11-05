package org.sqlunet.provider;

/**
 * SqlUNet provider contract
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("unused")
public final class SqlUNetContract
{
	static public final String VENDOR = "sqlunet";
	static public final String SCHEME = "content://";
	// global
	public static final String ARG_QUERYPOINTER = "QUERYPOINTER";
	public static final String ARG_QUERYSTRING = "QUERYSTRING";
	public static final String ARG_QUERYRECURSE = "QUERYRECURSE";
	// action
	public static final String ARG_QUERYACTION = "QUERYACTION";
	public static final int ARG_QUERYACTION_ALL = 0;
	public static final int ARG_QUERYACTION_SYNSET = 1;
	public static final int ARG_QUERYACTION_VNCLASS = 2;
	public static final int ARG_QUERYACTION_PBROLESET = 3;
	public static final int ARG_QUERYACTION_FNFRAME = 4;
	public static final int ARG_QUERYACTION_FNLEXUNIT = 5;
	public static final int ARG_QUERYACTION_FNSENTENCE = 6;
	public static final int ARG_QUERYACTION_FNANNOSET = 7;
	public static final int ARG_QUERYACTION_FNPATTERN = 8;
	public static final int ARG_QUERYACTION_FNVALENCEUNIT = 9;
	public static final int ARG_QUERYACTION_FNPREDICATE = 10;
	public static final int ARG_QUERYACTION_PM = 11;
	public static final int ARG_QUERYACTION_PMROLE = 12;

	// tables
	public static final String ARG_QUERYURI = "QUERYURI";
	public static final String ARG_QUERYID = "QUERYID";
	public static final String ARG_QUERYITEMS = "QUERYITEMS";
	public static final String ARG_QUERYHIDDENITEMS = "QUERYXITEMS";
	public static final String ARG_QUERYARG = "QUERYARG";
	public static final String ARG_QUERYLAYOUT = "QUERYLAYOUT";
	public static final String ARG_QUERYSORT = "QUERYSORT";
	public static final String ARG_QUERYFILTER = "QUERYFILTER";
	public static final String ARG_QUERYINTENT = "QUERYINTENT";
}
