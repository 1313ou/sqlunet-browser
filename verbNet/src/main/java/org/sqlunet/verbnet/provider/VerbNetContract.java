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
		static public final String URI = VnWords.TABLE;
		static public final String VNWORDID = V.VNWORDID;
		static public final String WORDID = V.WORDID;
		static public final String WORD = V.WORD;
	}

	static public final class VnClasses
	{
		static public final String TABLE = Q.VNCLASSES.TABLE;
		static public final String URI = VnClasses.TABLE;
		static public final String URI1 = "vn_class1";
		static public final String WORDID = V.WORDID;
		static public final String POS = V.POSID;
		static public final String CLASSID = V.CLASSID;
		static public final String CLASS = V.CLASS;
		static public final String CLASSTAG = V.CLASSTAG;
	}

	static public final class VnClasses_X
	{
		static public final String TABLE ="vnclasses_x_by_vnclass";
		static public final String URI_BY_VN_CLASS = VnClasses_X.TABLE;
		static public final String WORDID = V.WORDID;
		static public final String POS = V.POSID;
		static public final String CLASSID = V.CLASSID;
		static public final String CLASS = V.CLASS;
		static public final String CLASSTAG = V.CLASSTAG;
	}

	static public final class Words_VnClasses
	{
		static public final String TABLE = "words_vnclasses";
		static public final String URI = Words_VnClasses.TABLE;
		static public final String WORDID = V.WORDID;
		static public final String SYNSETID = V.SYNSETID;
		static public final String CLASSID = V.CLASSID;
		static public final String CLASS = V.CLASS;
		static public final String CLASSTAG = V.CLASSTAG;
		static public final String SENSENUM = V.SENSENUM;
		static public final String SENSEKEY = V.SENSEKEY;
		static public final String QUALITY = V.QUALITY;
		static public final String NULLSYNSET = V.NULLSYNSET;
	}

	static public final class VnClasses_VnMembers_X
	{
		static public final String TABLE_BY_WORD = "vnclasses_vnmembers_x_by_word";
		static public final String URI = VnClasses_VnMembers_X.TABLE_BY_WORD;
		static public final String CLASSID = V.CLASSID;
		static public final String VNWORDID = V.VNWORDID;
		static public final String WORDID = V.WORDID;
		static public final String WORD = V.WORD;
		static public final String DEFINITIONS = V.DEFINITIONS;
		static public final String GROUPINGS = V.GROUPINGS;
		static public final String DEFINITION = V.DEFINITION;
		static public final String GROUPING = V.GROUPING;
	}

	static public final class VnClasses_VnRoles_X
	{
		static public final String TABLE_BY_ROLE = "vnclasses_vnroles_x_by_vnrole";
		static public final String URI = VnClasses_VnRoles_X.TABLE_BY_ROLE;
		static public final String CLASSID = V.CLASSID;
		static public final String ROLEID = V.ROLEID;
		static public final String ROLETYPE = V.ROLETYPE;
		static public final String RESTRS = V.RESTRS;
	}

	static public final class VnClasses_VnFrames_X
	{
		static public final String TABLE_BY_FRAME = "vnclasses_vnframes_x_by_vnframe";
		static public final String URI = VnClasses_VnFrames_X.TABLE_BY_FRAME;
		static public final String CLASSID = V.CLASSID;
		static public final String FRAMEID = V.FRAMEID;
		static public final String FRAMENAME = V.FRAMENAME;
		static public final String FRAMESUBNAME = V.FRAMESUBNAME;
		static public final String SYNTAX = V.SYNTAX;
		static public final String SEMANTICS = V.SEMANTICS;
		static public final String NUMBER = V.NUMBER;
		static public final String XTAG = V.XTAG;
		static public final String EXAMPLE = V.EXAMPLE;
		static public final String EXAMPLES = V.EXAMPLES;
	}

	static public final class Lookup_VnExamples
	{
		static public final String TABLE = "fts_vnexamples";
		static public final String URI = Lookup_VnExamples.TABLE;
		static public final String EXAMPLEID = V.EXAMPLEID;
		static public final String EXAMPLE = V.EXAMPLE;
		static public final String CLASSID = V.CLASSID;
		static public final String FRAMEID = V.FRAMEID;
	}

	static public final class Lookup_VnExamples_X
	{
		static public final String TABLE = "fts_vnexamples_x";
		static public final String TABLE_BY_EXAMPLE = "fts_vnexamples_x_by_example";
		static public final String URI = Lookup_VnExamples_X.TABLE;
		static public final String URI_BY_EXAMPLE = Lookup_VnExamples_X.TABLE_BY_EXAMPLE;
		static public final String EXAMPLEID = V. EXAMPLEID;
		static public final String EXAMPLE =  V.EXAMPLE;
		static public final String CLASSID =  V.CLASSID;
		static public final String CLASS =  V.CLASS;
		static public final String FRAMEID =  V.FRAMEID;
		static public final String CLASSES =  V.CLASSES;
		static public final String FRAMES =  V.FRAMES;
	}

	static public final class Suggest_VnWords
	{
		static final String SEARCH_WORD_PATH = "suggest_vnword";
		static public final String TABLE = Suggest_VnWords.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
		static public final String VNWORDID =  V.VNWORDID;
		static public final String WORDID =  V.WORDID;
		static public final String WORD =  V.WORD;
	}

	static public final class Suggest_FTS_VnWords
	{
		static final String SEARCH_WORD_PATH = "suggest_fts_vnword";
		static public final String TABLE = Suggest_FTS_VnWords.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
		static public final String VNWORDID =  V.VNWORDID;
		static public final String WORDID =  V.WORDID;
		static public final String WORD = V.WORD;
	}
}
