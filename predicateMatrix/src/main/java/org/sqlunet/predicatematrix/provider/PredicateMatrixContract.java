package org.sqlunet.predicatematrix.provider;

public class PredicateMatrixContract
{
	static public final String AUTHORITY = "org.sqlunet.predicatematrix.provider"; //$NON-NLS-1$

	@SuppressWarnings("unused")
	static public class PredicateMatrix
	{
		public static final String PMID = "pmid"; //$NON-NLS-1$
		public static final String PMROLEID = "pmroleid"; //$NON-NLS-1$
		public static final String PMPREDID = "pmpredid"; //$NON-NLS-1$
		public static final String PMPREDICATE = "predicate"; //$NON-NLS-1$
		public static final String PMROLE = "role"; //$NON-NLS-1$
		public static final String PMPOS = "pos"; //$NON-NLS-1$

		public static final String WORD = "lemma"; //$NON-NLS-1$
		public static final String WORDID = "wordid"; //$NON-NLS-1$
		public static final String SYNSETID = "synsetid"; //$NON-NLS-1$
		public static final String PMWSOURCE = "wsource"; //$NON-NLS-1$

		public static final String VNWORDID = "vnwordid"; //$NON-NLS-1$
		public static final String VNCLASSID = "vnclassid"; //$NON-NLS-1$
		public static final String VNROLEID = "vnroleid"; //$NON-NLS-1$

		public static final String PBWORDID = "pbwordid"; //$NON-NLS-1$
		public static final String PBROLESETID = "pbrolesetid"; //$NON-NLS-1$
		public static final String PBROLEID = "pbroleid"; //$NON-NLS-1$

		public static final String FNWORDID = "fnwordid"; //$NON-NLS-1$
		public static final String FRAMEID = "fnframeid"; //$NON-NLS-1$
		public static final String LUID = "fnluid"; //$NON-NLS-1$
		public static final String FEID = "fnfeid"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class Pm extends PredicateMatrix
	{
		static public final String TABLE = "pm"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + PredicateMatrixContract.AUTHORITY + '/' + Pm.TABLE; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class Pm_X extends PredicateMatrix
	{
		static public final String TABLE = "pm_x"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + PredicateMatrixContract.AUTHORITY + '/' + Pm_X.TABLE; //$NON-NLS-1$

		public static final String DEFINITION = "definition"; //$NON-NLS-1$

		public static final String VNCLASS = "class"; //$NON-NLS-1$
		public static final String VNROLERESTRID = "restrsid"; //$NON-NLS-1$
		public static final String VNROLETYPEID = "roletypeid"; //$NON-NLS-1$
		public static final String VNROLETYPE = "roletype"; //$NON-NLS-1$

		public static final String PBROLESETNAME = "rolesetname"; //$NON-NLS-1$
		public static final String PBROLESETDESCR = "rolesetdescr"; //$NON-NLS-1$
		public static final String PBROLESETHEAD = "rolesethead"; //$NON-NLS-1$
		public static final String PBROLEDESCR = "roledescr"; //$NON-NLS-1$
		public static final String PBROLENARG = "narg"; //$NON-NLS-1$
		public static final String PBROLENARGDESCR = "nargdescr"; //$NON-NLS-1$

		public static final String FRAME = "frame"; //$NON-NLS-1$
		public static final String FRAMEDEFINITION = "framedefinition"; //$NON-NLS-1$
		public static final String LEXUNIT = "lexunit"; //$NON-NLS-1$
		public static final String LUDEFINITION = "ludefinition"; //$NON-NLS-1$
		public static final String LUDICT = "ludict"; //$NON-NLS-1$
		public static final String FETYPEID = "fetypeid"; //$NON-NLS-1$
		public static final String FETYPE = "fetype"; //$NON-NLS-1$
		public static final String FEABBREV = "feabbrev"; //$NON-NLS-1$
		public static final String FEDEFINITION = "fedefinition"; //$NON-NLS-1$
	}
}
