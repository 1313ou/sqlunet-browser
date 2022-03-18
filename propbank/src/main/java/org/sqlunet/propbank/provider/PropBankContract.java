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
		static public final String TABLE = Q.PBWORDS.TABLE;
		static public final String CONTENT_URI_TABLE = PbWords.TABLE;
		static public final String PBWORDID = Q.PBWORDID;
		static public final String WORDID = Q.WORDID;
		static public final String WORD = Q.WORD;
	}

	static public final class PbRoleSets
	{
		static public final String TABLE = Q.PBROLESET.TABLE;
		static public final String CONTENT_URI_TABLE = PbRoleSets.TABLE;
		static public final String ROLESETID = Q.ROLESETID;
		static public final String ROLESETNAME = Q.ROLESETNAME;
		static public final String ROLESETDESC = Q.ROLESETDESCR;
		static public final String ROLESETHEAD = Q.ROLESETHEAD;
	}

	static public final class PbRoleSets_X
	{
		static public final String TABLE = Q.PBROLESETS_X.TABLE;
		static public final String TABLE_BY_ROLESET = Q.PBROLESETS_X_BY_ROLESET.TABLE;
		static public final String CONTENT_URI_TABLE = PbRoleSets_X.TABLE_BY_ROLESET;
		static public final String ROLESETID = Q.ROLESETID;
		static public final String ROLESETNAME = Q.ROLESETNAME;
		static public final String ROLESETDESC = Q.ROLESETDESCR;
		static public final String ROLESETHEAD = Q.ROLESETHEAD;
		static public final String WORD = Q.WORD;
		static public final String ALIASES = Q.ALIASES;
	}

	static public final class Words_PbRoleSets
	{
		static public final String TABLE = Q.WORDS_PBROLESETS.TABLE;
		static public final String CONTENT_URI_TABLE = Words_PbRoleSets.TABLE;
		static public final String WORDID = Q.WORDID;
		static public final String POS = Q.POS;
		static public final String ROLESETID = Q.ROLESETID;
		static public final String ROLESETNAME = Q.ROLESETNAME;
		static public final String ROLESETDESC = Q.ROLESETDESCR;
		static public final String ROLESETHEAD = Q.ROLESETHEAD;
	}

	static public final class PbRoleSets_PbRoles
	{
		static public final String TABLE = Q.PBROLESETS_PBROLES.TABLE;
		static public final String CONTENT_URI_TABLE = PbRoleSets_PbRoles.TABLE;
		static public final String ROLESETID = Q.ROLESETID;
		static public final String ROLEID = Q.ROLEID;
		static public final String ROLEDESCR = Q.ROLEDESCR;
		static public final String NARG = Q.ARG;
		static public final String FUNCNAME = Q.FUNC;
		static public final String THETANAME = Q.THETA;
	}

	static public final class PbRoleSets_PbExamples
	{
		static public final String TABLE = Q.PBROLESETS_PBEXAMPLES.TABLE;
		static public final String TABLE_BY_EXAMPLE = Q.PBROLESETS_PBEXAMPLES_BY_EXAMPLE.TABLE;
		static public final String CONTENT_URI_TABLE = PbRoleSets_PbExamples.TABLE_BY_EXAMPLE;
		static public final String ROLESETID = Q.ROLESETID;
		static public final String TEXT = Q.TEXT;
		static public final String REL = Q.REL;
		static public final String NARG = Q.ARG;
		static public final String FUNCNAME = Q.FUNC;
		static public final String ROLEDESCR = Q.ROLEDESCR;
		static public final String THETANAME = Q.THETA;
		static public final String ARG = Q.ARG;
		static public final String ARGS = Q.ARGS;
		static public final String EXAMPLEID = Q.EXAMPLEID;
		static public final String ASPECTNAME = Q.ASPECT;
		static public final String FORMNAME = Q.FORM;
		static public final String TENSENAME = Q.TENSE;
		static public final String VOICENAME = Q.VOICE;
		static public final String PERSONNAME = Q.PERSON;
	}

	static public final class Lookup_PbExamples
	{
		static public final String TABLE = Q.LOOKUP_FTS_EXAMPLES.TABLE;
		static public final String CONTENT_URI_TABLE = Lookup_PbExamples.TABLE;
		static public final String EXAMPLEID = Q.EXAMPLEID;
		static public final String TEXT = Q.TEXT;
		static public final String ROLESETID = Q.ROLESETID;
	}

	static public final class Lookup_PbExamples_X
	{
		static public final String TABLE = Q.LOOKUP_FTS_EXAMPLES_X.TABLE;
		static public final String TABLE_BY_EXAMPLE = Q.LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE.TABLE;
		static public final String CONTENT_URI_TABLE = Lookup_PbExamples_X.TABLE_BY_EXAMPLE;
		static public final String EXAMPLEID = Q.EXAMPLEID;
		static public final String TEXT = Q.TEXT;
		static public final String ROLESETID = Q.ROLESETID;
		static public final String ROLESET = Q.ROLESETNAME;
		static public final String ROLESETS = Q.ROLESETS;
	}

	static public final class Suggest_PbWords
	{
		static final String SEARCH_WORD_PATH = Q.SUGGEST_WORDS.TABLE;
		static public final String TABLE = Suggest_PbWords.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
		static public final String PBWORDID = Q.PBWORDID;
		static public final String WORDID = Q.WORDID;
		static public final String WORD = Q.WORD;
	}

	static public final class Suggest_FTS_PbWords
	{
		static final String SEARCH_WORD_PATH = Q.SUGGEST_FTS_WORDS.TABLE;
		static public final String TABLE = Suggest_FTS_PbWords.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
		static public final String PBWORDID = Q.PBWORDID;
		static public final String WORDID = Q.WORDID;
		static public final String WORD = Q.WORD;
	}
}
