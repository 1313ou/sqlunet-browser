package org.sqlunet.propbank.provider;


/**
 * PropBank provider contract
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PropBankContract
{
	static public final String AUTHORITY = "org.sqlunet.propbank.provider";
	// A L I A S E S

	static public final String EXAMPLE = "e";
	static public final String REL = "r";
	static public final String FUNC = "f";
	static public final String ARG = "a";
	static public final String WORD = "w";
	static public final String MEMBER = "m";

	static public final class PbRoleSets
	{
		static public final String TABLE = "pbrolesets";
		static public final String CONTENT_URI = "content://" + PropBankContract.AUTHORITY + '/' + PbRoleSets.TABLE;
		public static final String ROLESETID = "rolesetid";
		public static final String ROLESETNAME = "rolesetname";
		public static final String ROLESETDESC = "rolesetdescr";
		public static final String ROLESETHEAD = "rolesethead";
	}

	static public final class PbRoleSets_X
	{
		static public final String TABLE = "pbrolesets_x";
		static public final String TABLE_BY_ROLESET = "pbrolesets_x_by_roleset";
		static public final String CONTENT_URI = "content://" + PropBankContract.AUTHORITY + '/' + PbRoleSets_X.TABLE_BY_ROLESET;
		public static final String ROLESETID = "rolesetid";
		public static final String ROLESETNAME = "rolesetname";
		public static final String ROLESETDESC = "rolesetdescr";
		public static final String ROLESETHEAD = "rolesethead";
		public static final String LEMMA = "lemma";
		public static final String ALIASES = "aliases";
	}

	@SuppressWarnings("unused")
	static public final class Words_PbRoleSets
	{
		static public final String TABLE = "words_pbrolesets";
		static public final String CONTENT_URI = "content://" + PropBankContract.AUTHORITY + '/' + Words_PbRoleSets.TABLE;
		public static final String WORDID = "wordid";
		public static final String POS = "pos";
		public static final String ROLESETID = "rolesetid";
		public static final String ROLESETNAME = "rolesetname";
		public static final String ROLESETDESC = "rolesetdescr";
		public static final String ROLESETHEAD = "rolesethead";
	}

	static public final class PbRoleSets_PbRoles
	{
		static public final String TABLE = "pbrolesets_pbroles";
		static public final String CONTENT_URI = "content://" + PropBankContract.AUTHORITY + '/' + PbRoleSets_PbRoles.TABLE;
		public static final String ROLESETID = "rolesetid";
		public static final String ROLEID = "roleid";
		public static final String ROLEDESCR = "roledescr";
		public static final String NARG = "narg";
		public static final String FUNCNAME = "funcname";
		public static final String THETANAME = "thetaname";
	}

	static public final class PbRoleSets_PbExamples
	{
		static public final String TABLE = "pbrolesets_pbexamples";
		static public final String TABLE_BY_EXAMPLE = "pbrolesets_pbexamples_by_example";
		static public final String CONTENT_URI = "content://" + PropBankContract.AUTHORITY + '/' + PbRoleSets_PbExamples.TABLE_BY_EXAMPLE;
		public static final String ROLESETID = "rolesetid";
		public static final String TEXT = "text";
		public static final String REL = "rel";
		public static final String NARG = "narg";
		public static final String FUNCNAME = "funcname";
		public static final String ROLEDESCR = "roledescr";
		public static final String THETANAME = "thetaname";
		public static final String ARG = "arg";
		public static final String ARGS = "args";
		public static final String EXAMPLEID = "exampleid";
		public static final String ASPECTNAME = "aspectname";
		public static final String FORMNAME = "formname";
		public static final String TENSENAME = "tensename";
		public static final String VOICENAME = "voicename";
		public static final String PERSONNAME = "personname";
	}

	@SuppressWarnings("unused")
	static public final class Lookup_PbExamples
	{
		static public final String TABLE = "fts_fnsentences";
		static public final String CONTENT_URI = "content://" + PropBankContract.AUTHORITY + '/' + Lookup_PbExamples.TABLE;
		static public final String EXAMPLEID = "exampleid";
		static public final String ROLESETID = "rolesetid";
		static public final String TEXT = "text";
	}
}
