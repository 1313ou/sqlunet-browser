/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.verbnet.provider;

import android.app.SearchManager;

/**
 * VerbNet provider contract
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VerbNetContract
{
	static public final class VnWords
	{
		static public final String TABLE = Q.WORDS.TABLE;
		static public final String CONTENT_URI_TABLE = VnWords.TABLE;
		static public final String VNWORDID = Q.VNWORDID;
		static public final String WORDID = Q.WORDID;
		static public final String WORD = Q.WORD;
	}

	static public final class VnClasses
	{
		static public final String TABLE = Q.VNCLASSES.TABLE;
		static public final String CONTENT_URI_TABLE = VnClasses.TABLE;
		static public final String WORDID = Q.WORDID;
		static public final String POS = Q.POSID;
		static public final String CLASSID = Q.CLASSID;
		static public final String CLASS = Q.CLASS;
		static public final String CLASSTAG = Q.CLASSTAG;
	}

	static public final class Words_VnClasses
	{
		static public final String TABLE = Q.WORDS_VNCLASSES.TABLE;
		static public final String CONTENT_URI_TABLE = Words_VnClasses.TABLE;
		static public final String WORDID = Q.WORDID;
		static public final String SYNSETID = Q.SYNSETID;
		static public final String CLASSID = Q.CLASSID;
		static public final String CLASS = Q.CLASS;
		static public final String CLASSTAG = Q.CLASSTAG;
		static public final String SENSENUM = Q.SENSENUM;
		static public final String SENSEKEY = Q.SENSEKEY;
		static public final String QUALITY = Q.QUALITY;
		static public final String NULLSYNSET = Q.NULLSYNSET;
	}

	static public final class VnClasses_VnMembers_X
	{
		static public final String TABLE_BY_WORD = Q.VNCLASSES_VNMEMBERS_X_BY_WORD.TABLE;
		static public final String CONTENT_URI_TABLE = VnClasses_VnMembers_X.TABLE_BY_WORD;
		static public final String CLASSID = Q.CLASSID;
		static public final String VNWORDID = Q.VNWORDID;
		static public final String WORDID = Q.WORDID;
		static public final String WORD = Q.WORD;
		static public final String DEFINITIONS = Q.DEFINITIONS;
		static public final String GROUPINGS = Q.GROUPINGS;
		static public final String DEFINITION = Q.DEFINITION;
		static public final String GROUPING = Q.GROUPING;
	}

	static public final class VnClasses_VnRoles_X
	{
		static public final String TABLE_BY_ROLE = Q.VNCLASSES_VNROLES_X_BY_VNROLE.TABLE;
		static public final String CONTENT_URI_TABLE = VnClasses_VnRoles_X.TABLE_BY_ROLE;
		static public final String CLASSID = Q.CLASSID;
		static public final String ROLEID = Q.ROLEID;
		static public final String ROLETYPE = Q.ROLETYPE;
		static public final String RESTRS = Q.RESTRS;
	}

	static public final class VnClasses_VnFrames_X
	{
		static public final String TABLE_BY_FRAME = Q.VNCLASSES_VNFRAMES_X_BY_VNFRAME.TABLE;
		static public final String CONTENT_URI_TABLE = VnClasses_VnFrames_X.TABLE_BY_FRAME;
		static public final String CLASSID = Q.CLASSID;
		static public final String FRAMEID = Q.FRAMEID;
		static public final String FRAMENAME = Q.FRAMENAME;
		static public final String FRAMESUBNAME = Q.FRAMESUBNAME;
		static public final String SYNTAX = Q.SYNTAX;
		static public final String SEMANTICS = Q.SEMANTICS;
		static public final String NUMBER = Q.NUMBER;
		static public final String XTAG = Q.XTAG;
		static public final String EXAMPLE = Q.EXAMPLE;
		static public final String EXAMPLES = Q.EXAMPLES;
	}

	static public final class Lookup_VnExamples
	{
		static public final String TABLE = Q.LOOKUP_FTS_EXAMPLES.TABLE;
		static public final String CONTENT_URI_TABLE = Lookup_VnExamples.TABLE;
		static public final String EXAMPLEID = Q.EXAMPLEID;
		static public final String EXAMPLE = Q.EXAMPLE;
		static public final String CLASSID = Q.CLASSID;
		static public final String FRAMEID = Q.FRAMEID;
	}

	static public final class Lookup_VnExamples_X
	{
		static public final String TABLE =  Q.LOOKUP_FTS_EXAMPLES_X.TABLE;
		static public final String TABLE_BY_EXAMPLE = Q.LOOKUP_FTS_EXAMPLES_X.TABLE;
		static public final String CONTENT_URI_TABLE = Lookup_VnExamples_X.TABLE_BY_EXAMPLE;
		static public final String EXAMPLEID = Q. EXAMPLEID;
		static public final String EXAMPLE =  Q.EXAMPLE;
		static public final String CLASSID =  Q.CLASSID;
		static public final String CLASS =  Q.CLASS;
		static public final String FRAMEID =  Q.FRAMEID;
		static public final String CLASSES =  Q.CLASSES;
		static public final String FRAMES =  Q.FRAMES;
	}

	static public final class Suggest_VnWords
	{
		static final String SEARCH_WORD_PATH = Q.SUGGEST_WORDS.TABLE;
		static public final String TABLE = Suggest_VnWords.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
		static public final String VNWORDID =  Q.VNWORDID;
		static public final String WORDID =  Q.WORDID;
		static public final String WORD =  Q.WORD;
	}

	static public final class Suggest_FTS_VnWords
	{
		static final String SEARCH_WORD_PATH = Q.SUGGEST_FTS_WORDS.TABLE;
		static public final String TABLE = Suggest_FTS_VnWords.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
		static public final String VNWORDID =  Q.VNWORDID;
		static public final String WORDID =  Q.WORDID;
		static public final String WORD = Q.WORD;
	}
}
