/*
 * Copyright (c) 2022. Bernard Bou
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

	static public final String AS_EXAMPLES = V.AS_EXAMPLES;
	static public final String AS_RELATIONS = V.AS_RELATIONS;
	static public final String AS_FUNCS = V.AS_FUNCS;
	static public final String AS_ARGS = V.AS_ARGS;
	static public final String AS_WORDs = V.AS_WORDS;
	static public final String AS_PBWORDS = V.AS_PBWORDS;
	static public final String AS_MEMBERS = V.AS_MEMBERS;

	public interface PbWords
	{
		String TABLE = Q.PBWORDS.TABLE;
		String URI = TABLE;
		String PBWORDID = V.PBWORDID;
		String WORDID = V.WORDID;
		String WORD = V.WORD;
	}

	public interface PbRoleSets
	{
		String TABLE = Q.PBROLESET1.TABLE;
		String URI = TABLE;
		String URI1 = "roleset";
		String ROLESETID = V.ROLESETID;
		String ROLESETNAME = V.ROLESETNAME;
		String ROLESETDESC = V.ROLESETDESCR;
		String ROLESETHEAD = V.ROLESETHEAD;
	}

	public interface PbRoleSets_X
	{
		String URI = "rolesets_x";
		String URI_BY_ROLESET = URI + "_by_roleset";
		String ROLESETID = V.ROLESETID;
		String ROLESETNAME = V.ROLESETNAME;
		String ROLESETDESC = V.ROLESETDESCR;
		String ROLESETHEAD = V.ROLESETHEAD;
		String WORD = V.WORD;
		String ALIASES = V.ALIASES;
	}

	public interface Words_PbRoleSets
	{
		String TABLE = "words_rolesets";
		String URI = Words_PbRoleSets.TABLE;
		String WORDID = V.WORDID;
		String POS = V.POS;
		String ROLESETID = V.ROLESETID;
		String ROLESETNAME = V.ROLESETNAME;
		String ROLESETDESC = V.ROLESETDESCR;
		String ROLESETHEAD = V.ROLESETHEAD;
	}

	public interface PbRoleSets_PbRoles
	{
		String TABLE = "rolesets_roles";
		String URI = PbRoleSets_PbRoles.TABLE;
		String ROLESETID = V.ROLESETID;
		String ROLEID = V.ROLEID;
		String ROLEDESCR = V.ROLEDESCR;
		String ARGTYPE = V.ARGTYPE;
		String FUNC = V.FUNC;
		String THETA = V.THETA;
	}

	public interface PbRoleSets_PbExamples
	{
		String URI = "rolesets_examples";
		String URI_BY_EXAMPLE = URI + "_by_example";
		String ROLESETID = V.ROLESETID;
		String TEXT = V.TEXT;
		String REL = V.REL;
		String ARGTYPE = V.ARGTYPE;
		String FUNCNAME = V.FUNC;
		String ROLEDESCR = V.ROLEDESCR;
		String THETA = V.THETA;
		String ARG = V.ARG;
		String ARGS = V.ARGS;
		String EXAMPLEID = V.EXAMPLEID;
		String ASPECT = V.ASPECT;
		String FORM = V.FORM;
		String TENSE = V.TENSE;
		String VOICE = V.VOICE;
		String PERSON = V.PERSON;
	}

	public interface Lookup_PbExamples
	{
		String TABLE = "pb_examples_text_fts4";
		String URI = TABLE;
		String EXAMPLEID = V.EXAMPLEID;
		String TEXT = V.TEXT;
		String ROLESETID = V.ROLESETID;
	}

	public interface Lookup_PbExamples_X
	{
		String URI = "pb_examples_text_x_fts4";
		String URI_BY_EXAMPLE = URI + "_by_examples";
		String EXAMPLEID = V.EXAMPLEID;
		String TEXT = V.TEXT;
		String ROLESETID = V.ROLESETID;
		String ROLESET = V.ROLESETNAME;
		String ROLESETS = V.ROLESETS;
	}

	public interface Suggest_PbWords
	{
		String SEARCH_WORD_PATH = "suggest_pbword";
		String URI = Suggest_PbWords.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
		String PBWORDID = V.PBWORDID;
		String WORDID = V.WORDID;
		String WORD = V.WORD;
	}

	public interface Suggest_FTS_PbWords
	{
		String SEARCH_WORD_PATH = "suggest_fts_pbword";
		String URI = Suggest_FTS_PbWords.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
		String PBWORDID = V.PBWORDID;
		String WORDID = V.WORDID;
		String WORD = V.WORD;
	}
}
