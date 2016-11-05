package org.sqlunet.predicatematrix.provider;

/**
 * PredicateMatrix provider contract
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PredicateMatrixContract
{
	static public final String AUTHORITY = "org.sqlunet.predicatematrix.provider";
	// A L I A S E S

	static public final String PMROLE = "mr";
	static public final String PMPREDICATE = "mp";
	static public final String VNCLASS = "vc";
	static public final String VNROLE = "vr";
	static public final String VNROLETYPE = "vt";
	static public final String PBROLESET = "pc";
	static public final String PBROLE = "pr";
	static public final String PBARG = "pa";
	static public final String FNFRAME = "ff";
	static public final String FNFE = "fe";
	static public final String FNFETYPE = "ft";
	static public final String FNLU = "fu";
	@SuppressWarnings("unused")
	static public class PredicateMatrix
	{
		public static final String PMID = "pmid";
		public static final String PMROLEID = "pmroleid";
		public static final String PMPREDID = "pmpredid";
		public static final String PMPREDICATE = "predicate";
		public static final String PMROLE = "role";
		public static final String PMPOS = "pos";
		public static final String WORD = "lemma";
		public static final String WORDID = "wordid";
		public static final String SYNSETID = "synsetid";
		public static final String PMWSOURCE = "wsource";
		public static final String VNWORDID = "vnwordid";
		public static final String VNCLASSID = "vnclassid";
		public static final String VNROLEID = "vnroleid";
		public static final String PBWORDID = "pbwordid";
		public static final String PBROLESETID = "pbrolesetid";
		public static final String PBROLEID = "pbroleid";
		public static final String FNWORDID = "fnwordid";
		public static final String FRAMEID = "fnframeid";
		public static final String LUID = "fnluid";
		public static final String FEID = "fnfeid";
	}

	@SuppressWarnings("unused")
	static public final class Pm extends PredicateMatrix
	{
		static public final String TABLE = "pm";
		static public final String CONTENT_URI = "content://" + PredicateMatrixContract.AUTHORITY + '/' + Pm.TABLE;
	}

	@SuppressWarnings("unused")
	static public final class Pm_X extends PredicateMatrix
	{
		static public final String TABLE = "pm_x";
		static public final String CONTENT_URI = "content://" + PredicateMatrixContract.AUTHORITY + '/' + Pm_X.TABLE;
		public static final String DEFINITION = "definition";
		public static final String VNCLASS = "class";
		public static final String VNROLERESTRID = "restrsid";
		public static final String VNROLETYPEID = "roletypeid";
		public static final String VNROLETYPE = "roletype";
		public static final String PBROLESETNAME = "rolesetname";
		public static final String PBROLESETDESCR = "rolesetdescr";
		public static final String PBROLESETHEAD = "rolesethead";
		public static final String PBROLEDESCR = "roledescr";
		public static final String PBROLENARG = "narg";
		public static final String PBROLENARGDESCR = "nargdescr";
		public static final String FRAME = "frame";
		public static final String FRAMEDEFINITION = "framedefinition";
		public static final String LEXUNIT = "lexunit";
		public static final String LUDEFINITION = "ludefinition";
		public static final String LUDICT = "ludict";
		public static final String FETYPEID = "fetypeid";
		public static final String FETYPE = "fetype";
		public static final String FEABBREV = "feabbrev";
		public static final String FEDEFINITION = "fedefinition";
	}
}
