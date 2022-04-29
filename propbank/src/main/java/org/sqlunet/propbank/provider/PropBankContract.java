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
	static public final String FUNC = "fu";
	static public final String ARG = "ar";
	static public final String WORD = "w";
	static public final String PBWORD = "p";
	static public final String MEMBER = "m";

	static public final class PbWords
	{
		static public final String TABLE = Q.PBWORDS.TABLE;
		static public final String CONTENT_URI_TABLE = PbWords.TABLE;
		static public final String PBWORDID = V.PBWORDID;
		static public final String WORDID = V.WORDID;
		static public final String WORD = V.WORD;
	}

	static public final class PbRoleSets
	{
		static public final String TABLE = Q.PBROLESET1.TABLE;
		static public final String CONTENT_URI_TABLE = PbRoleSets.TABLE;
		static public final String ROLESETID = V.ROLESETID;
		static public final String ROLESETNAME = V.ROLESETNAME;
		static public final String ROLESETDESC = V.ROLESETDESCR;
		static public final String ROLESETHEAD = V.ROLESETHEAD;
	}

	static public final class PbRoleSets_X
	{
		static public final String TABLE = "pbrolesets_x";
		static public final String TABLE_BY_ROLESET = "pbrolesets_x_by_roleset";
		static public final String CONTENT_URI_TABLE = PbRoleSets_X.TABLE_BY_ROLESET;
		static public final String ROLESETID = V.ROLESETID;
		static public final String ROLESETNAME = V.ROLESETNAME;
		static public final String ROLESETDESC = V.ROLESETDESCR;
		static public final String ROLESETHEAD = V.ROLESETHEAD;
		static public final String WORD = V.WORD;
		static public final String ALIASES = V.ALIASES;
	}

	static public final class Words_PbRoleSets
	{
		static public final String TABLE = "words_pbrolesets";
		static public final String CONTENT_URI_TABLE = Words_PbRoleSets.TABLE;
		static public final String WORDID = V.WORDID;
		static public final String POS = V.POS;
		static public final String ROLESETID = V.ROLESETID;
		static public final String ROLESETNAME = V.ROLESETNAME;
		static public final String ROLESETDESC = V.ROLESETDESCR;
		static public final String ROLESETHEAD = V.ROLESETHEAD;
	}

	static public final class PbRoleSets_PbRoles
	{
		static public final String TABLE = "pbrolesets_pbroles";
		static public final String CONTENT_URI_TABLE = PbRoleSets_PbRoles.TABLE;
		static public final String ROLESETID = V.ROLESETID;
		static public final String ROLEID = V.ROLEID;
		static public final String ROLEDESCR = V.ROLEDESCR;
		static public final String ARGTYPE = V.ARGTYPE;
		static public final String FUNC = V.FUNC;
		static public final String THETA = V.THETA;
	}

	static public final class PbRoleSets_PbExamples
	{
		static public final String TABLE = "pbrolesets_pbexamples";
		static public final String TABLE_BY_EXAMPLE = "pbrolesets_pbexamples_by_example";
		static public final String CONTENT_URI_TABLE = PbRoleSets_PbExamples.TABLE_BY_EXAMPLE;
		static public final String ROLESETID = V.ROLESETID;
		static public final String TEXT = V.TEXT;
		static public final String REL = V.REL;
		static public final String ARGTYPE = V.ARGTYPE;
		static public final String FUNCNAME = V.FUNC;
		static public final String ROLEDESCR = V.ROLEDESCR;
		static public final String THETANAME = V.THETA;
		static public final String ARG = V.ARG;
		static public final String ARGS = V.ARGS;
		static public final String EXAMPLEID = V.EXAMPLEID;
		static public final String ASPECTNAME = V.ASPECT;
		static public final String FORMNAME = V.FORM;
		static public final String TENSENAME = V.TENSE;
		static public final String VOICENAME = V.VOICE;
		static public final String PERSONNAME = V.PERSON;
	}

	static public final class Lookup_PbExamples
	{
		static public final String TABLE = "fts_pbexamples";
		static public final String CONTENT_URI_TABLE = Lookup_PbExamples.TABLE;
		static public final String EXAMPLEID = V.EXAMPLEID;
		static public final String TEXT = V.TEXT;
		static public final String ROLESETID = V.ROLESETID;
	}

	static public final class Lookup_PbExamples_X
	{
		static public final String TABLE = "fts_pbexamples_x";
		static public final String TABLE_BY_EXAMPLE = "fts_pbexamples_x_by_examples";
		static public final String CONTENT_URI_TABLE = Lookup_PbExamples_X.TABLE_BY_EXAMPLE;
		static public final String EXAMPLEID = V.EXAMPLEID;
		static public final String TEXT = V.TEXT;
		static public final String ROLESETID = V.ROLESETID;
		static public final String ROLESET = V.ROLESETNAME;
		static public final String ROLESETS = V.ROLESETS;
	}

	static public final class Suggest_PbWords
	{
		static final String SEARCH_WORD_PATH = "suggest_pbword";
		static public final String TABLE = Suggest_PbWords.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
		static public final String PBWORDID = V.PBWORDID;
		static public final String WORDID = V.WORDID;
		static public final String WORD = V.WORD;
	}

	static public final class Suggest_FTS_PbWords
	{
		static final String SEARCH_WORD_PATH = "suggest_fts_pbword";
		static public final String TABLE = Suggest_FTS_PbWords.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
		static public final String PBWORDID = V.PBWORDID;
		static public final String WORDID = V.WORDID;
		static public final String WORD = V.WORD;
	}
}
