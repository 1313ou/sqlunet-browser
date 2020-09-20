/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.propbank.provider;


import android.app.SearchManager;

/**
 * PropBank provider contract
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class PropBankContract
{
	// A L I A S E S

	static public final String EXAMPLE = "e";
	static public final String REL = "r";
	static public final String FUNC = "f";
	static public final String ARG = "a";
	static public final String WORD = "w";
	static public final String MEMBER = "m";

	static public final class PbWords
	{
		static public final String TABLE = "pbwords";
		static public final String CONTENT_URI_TABLE = PbWords.TABLE;
		static public final String PBWORDID = "pbwordid";
		static public final String WORDID = "wordid";
		static public final String WORD = "lemma";
	}

	static public final class PbRoleSets
	{
		static public final String TABLE = "pbrolesets";
		static public final String CONTENT_URI_TABLE = PbRoleSets.TABLE;
		static public final String ROLESETID = "rolesetid";
		static public final String ROLESETNAME = "rolesetname";
		static public final String ROLESETDESC = "rolesetdescr";
		static public final String ROLESETHEAD = "rolesethead";
	}

	static public final class PbRoleSets_X
	{
		static public final String TABLE = "pbrolesets_x";
		static public final String TABLE_BY_ROLESET = "pbrolesets_x_by_roleset";
		static public final String CONTENT_URI_TABLE = PbRoleSets_X.TABLE_BY_ROLESET;
		static public final String ROLESETID = "rolesetid";
		static public final String ROLESETNAME = "rolesetname";
		static public final String ROLESETDESC = "rolesetdescr";
		static public final String ROLESETHEAD = "rolesethead";
		static public final String LEMMA = "lemma";
		static public final String ALIASES = "aliases";
	}

	static public final class Words_PbRoleSets
	{
		static public final String TABLE = "words_pbrolesets";
		static public final String CONTENT_URI_TABLE = Words_PbRoleSets.TABLE;
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
		static public final String CONTENT_URI_TABLE = PbRoleSets_PbRoles.TABLE;
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
		static public final String CONTENT_URI_TABLE = PbRoleSets_PbExamples.TABLE_BY_EXAMPLE;
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

	static public final class Lookup_PbExamples
	{
		static public final String TABLE = "fts_pbexamples";
		static public final String CONTENT_URI_TABLE = Lookup_PbExamples.TABLE;
		static public final String EXAMPLEID = "exampleid";
		static public final String TEXT = "text";
		static public final String ROLESETID = "rolesetid";
	}

	static public final class Lookup_PbExamples_X
	{
		static public final String TABLE = "fts_pbexamples_x";
		static public final String TABLE_BY_EXAMPLE = "fts_pbexamples_x_by_examples";
		static public final String CONTENT_URI_TABLE = Lookup_PbExamples_X.TABLE_BY_EXAMPLE;
		static public final String EXAMPLEID = "exampleid";
		static public final String TEXT = "text";
		static public final String ROLESETID = "rolesetid";
		static public final String ROLESET = "rolesetname";
		static public final String ROLESETS = "rolesets";
	}

	static public final class Suggest_PbWords
	{
		static final String SEARCH_WORD_PATH = "suggest_pbword";
		static public final String TABLE = Suggest_PbWords.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
		static public final String PBWORDID = "pbwordid";
		static public final String WORDID = "wordid";
		static public final String WORD = "lemma";
	}

	static public final class Suggest_FTS_PbWords
	{
		static final String SEARCH_WORD_PATH = "suggest_fts_pbword";
		static public final String TABLE = Suggest_FTS_PbWords.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
		static public final String PBWORDID = "pbwordid";
		static public final String WORDID = "wordid";
		static public final String WORD = "lemma";
	}
}
