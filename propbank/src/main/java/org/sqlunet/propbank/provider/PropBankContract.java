package org.sqlunet.propbank.provider;


import org.sqlunet.provider.BaseProvider;

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

	@SuppressWarnings("unused")
	static public final class PbRoleSets
	{
		static public final String TABLE = "pbrolesets";
		static public final String CONTENT_URI = BaseProvider.SCHEME + PropBankContract.AUTHORITY + '/' + PbRoleSets.TABLE;
		static public final String ROLESETID = "rolesetid";
		static public final String ROLESETNAME = "rolesetname";
		static public final String ROLESETDESC = "rolesetdescr";
		static public final String ROLESETHEAD = "rolesethead";
	}

	static public final class PbRoleSets_X
	{
		static public final String TABLE = "pbrolesets_x";
		static public final String TABLE_BY_ROLESET = "pbrolesets_x_by_roleset";
		static public final String CONTENT_URI = BaseProvider.SCHEME + PropBankContract.AUTHORITY + '/' + PbRoleSets_X.TABLE_BY_ROLESET;
		static public final String ROLESETID = "rolesetid";
		static public final String ROLESETNAME = "rolesetname";
		static public final String ROLESETDESC = "rolesetdescr";
		static public final String ROLESETHEAD = "rolesethead";
		static public final String LEMMA = "lemma";
		static public final String ALIASES = "aliases";
	}

	@SuppressWarnings("unused")
	static public final class Words_PbRoleSets
	{
		static public final String TABLE = "words_pbrolesets";
		static public final String CONTENT_URI = BaseProvider.SCHEME + PropBankContract.AUTHORITY + '/' + Words_PbRoleSets.TABLE;
		static public final String WORDID = "wordid";
		static public final String POS = "pos";
		static public final String ROLESETID = "rolesetid";
		static public final String ROLESETNAME = "rolesetname";
		static public final String ROLESETDESC = "rolesetdescr";
		static public final String ROLESETHEAD = "rolesethead";
	}

	static public final class PbRoleSets_PbRoles
	{
		static public final String TABLE = "pbrolesets_pbroles";
		static public final String CONTENT_URI = BaseProvider.SCHEME + PropBankContract.AUTHORITY + '/' + PbRoleSets_PbRoles.TABLE;
		static public final String ROLESETID = "rolesetid";
		static public final String ROLEID = "roleid";
		static public final String ROLEDESCR = "roledescr";
		static public final String NARG = "narg";
		static public final String FUNCNAME = "funcname";
		static public final String THETANAME = "thetaname";
	}

	static public final class PbRoleSets_PbExamples
	{
		static public final String TABLE = "pbrolesets_pbexamples";
		static public final String TABLE_BY_EXAMPLE = "pbrolesets_pbexamples_by_example";
		static public final String CONTENT_URI = BaseProvider.SCHEME + PropBankContract.AUTHORITY + '/' + PbRoleSets_PbExamples.TABLE_BY_EXAMPLE;
		static public final String ROLESETID = "rolesetid";
		static public final String TEXT = "text";
		static public final String REL = "rel";
		static public final String NARG = "narg";
		static public final String FUNCNAME = "funcname";
		static public final String ROLEDESCR = "roledescr";
		static public final String THETANAME = "thetaname";
		static public final String ARG = "arg";
		static public final String ARGS = "args";
		static public final String EXAMPLEID = "exampleid";
		static public final String ASPECTNAME = "aspectname";
		static public final String FORMNAME = "formname";
		static public final String TENSENAME = "tensename";
		static public final String VOICENAME = "voicename";
		static public final String PERSONNAME = "personname";
	}

	@SuppressWarnings("unused")
	static public final class Lookup_PbExamples
	{
		static public final String TABLE = "fts_pbexamples";
		static public final String CONTENT_URI = BaseProvider.SCHEME + PropBankContract.AUTHORITY + '/' + Lookup_PbExamples.TABLE;
		static public final String EXAMPLEID = "exampleid";
		static public final String TEXT = "text";
		static public final String ROLESETID = "rolesetid";
	}

	@SuppressWarnings("unused")
	static public final class Lookup_PbExamples_X
	{
		static public final String TABLE = "fts_pbexamples_x";
		static public final String TABLE_BY_EXAMPLE = "fts_pbexamples_x_by_examples";
		static public final String CONTENT_URI = BaseProvider.SCHEME + PropBankContract.AUTHORITY + '/' + Lookup_PbExamples_X.TABLE_BY_EXAMPLE;
		static public final String EXAMPLEID = "exampleid";
		static public final String TEXT = "text";
		static public final String ROLESETID = "rolesetid";
		static public final String ROLESET = "rolesetname";
		static public final String ROLESETS = "rolesets";
	}
}
