package org.sqlunet.propbank.provider;

public class PropbankContract
{
	static public final String AUTHORITY = "org.sqlunet.propbank.provider"; //$NON-NLS-1$

	static public final class PbRolesets
	{
		static public final String TABLE = "pbrolesets"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + PropbankContract.AUTHORITY + '/' + PbRolesets.TABLE; //$NON-NLS-1$
		public static final String ROLESETID = "rolesetid"; //$NON-NLS-1$
		public static final String ROLESETNAME = "rolesetname"; //$NON-NLS-1$
		public static final String ROLESETDESC = "rolesetdescr"; //$NON-NLS-1$
		public static final String ROLESETHEAD = "rolesethead"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class Words_PbRolesets
	{
		static public final String TABLE = "words_pbrolesets"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + PropbankContract.AUTHORITY + '/' + Words_PbRolesets.TABLE; //$NON-NLS-1$
		public static final String WORDID = "wordid"; //$NON-NLS-1$
		public static final String POS = "pos"; //$NON-NLS-1$
		public static final String ROLESETID = "rolesetid"; //$NON-NLS-1$
		public static final String ROLESETNAME = "rolesetname"; //$NON-NLS-1$
		public static final String ROLESETDESC = "rolesetdescr"; //$NON-NLS-1$
		public static final String ROLESETHEAD = "rolesethead"; //$NON-NLS-1$
	}

	static public final class PbRolesets_PbRoles
	{
		static public final String TABLE = "pbrolesets_pbroles"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + PropbankContract.AUTHORITY + '/' + PbRolesets_PbRoles.TABLE; //$NON-NLS-1$
		public static final String ROLESETID = "rolesetid"; //$NON-NLS-1$
		public static final String ROLEID = "roleid"; //$NON-NLS-1$
		public static final String ROLEDESCR = "roledescr"; //$NON-NLS-1$
		public static final String NARG = "narg"; //$NON-NLS-1$
		public static final String FUNCNAME = "funcname"; //$NON-NLS-1$
		public static final String THETANAME = "thetaname"; //$NON-NLS-1$
	}

	static public final class PbRolesets_PbExamples
	{
		static public final String TABLE = "pbrolesets_pbexamples"; //$NON-NLS-1$
		static public final String TABLE_BY_EXAMPLE = "pbrolesets_pbexamples_by_example"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + PropbankContract.AUTHORITY + '/' + PbRolesets_PbExamples.TABLE_BY_EXAMPLE; //$NON-NLS-1$
		public static final String ROLESETID = "rolesetid"; //$NON-NLS-1$
		public static final String TEXT = "text"; //$NON-NLS-1$
		public static final String REL = "rel"; //$NON-NLS-1$
		public static final String NARG = "narg"; //$NON-NLS-1$
		public static final String FUNCNAME = "funcname"; //$NON-NLS-1$
		public static final String ROLEDESCR = "roledescr"; //$NON-NLS-1$
		public static final String THETANAME = "thetaname"; //$NON-NLS-1$
		public static final String ARG = "arg"; //$NON-NLS-1$
		public static final String ARGS = "args"; //$NON-NLS-1$
		public static final String EXAMPLEID = "e.exampleid"; //$NON-NLS-1$
		public static final String ASPECTNAME = "aspectname"; //$NON-NLS-1$
		public static final String FORMNAME = "formname"; //$NON-NLS-1$
		public static final String TENSENAME = "tensename"; //$NON-NLS-1$
		public static final String VOICENAME = "voicename"; //$NON-NLS-1$
		public static final String PERSONNAME = "personname"; //$NON-NLS-1$
	}
}
