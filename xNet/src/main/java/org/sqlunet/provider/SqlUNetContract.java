package org.sqlunet.provider;

/**
 * Contract for SqlUNet provider
 */
@SuppressWarnings("unused")
public final class SqlUNetContract
{
	static public final String VENDOR = "sqlunet"; //$NON-NLS-1$
	static public final String SCHEME = "content://"; //$NON-NLS-1$

	// global
	public static final String ARG_QUERYPOINTER = "QUERYPOINTER"; //$NON-NLS-1$
	public static final String ARG_QUERYSTRING = "QUERYSTRING"; //$NON-NLS-1$
	public static final String ARG_QUERYRECURSE = "QUERYRECURSE"; //$NON-NLS-1$

	// action
	public static final String ARG_QUERYACTION = "QUERYACTION"; //$NON-NLS-1$
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
	public static final String ARG_QUERYURI = "QUERYURI"; //$NON-NLS-1$
	public static final String ARG_QUERYID = "QUERYID"; //$NON-NLS-1$
	public static final String ARG_QUERYITEMS = "QUERYITEMS"; //$NON-NLS-1$
	public static final String ARG_QUERYXITEMS = "QUERYXITEMS"; //$NON-NLS-1$
	public static final String ARG_QUERYARG = "QUERYARG"; //$NON-NLS-1$
	public static final String ARG_QUERYLAYOUT = "QUERYLAYOUT"; //$NON-NLS-1$
	public static final String ARG_QUERYSORT = "QUERYSORT"; //$NON-NLS-1$
	public static final String ARG_QUERYFILTER = "QUERYFILTER"; //$NON-NLS-1$
	public static final String ARG_QUERYINTENT = "QUERYINTENT"; //$NON-NLS-1$
}
