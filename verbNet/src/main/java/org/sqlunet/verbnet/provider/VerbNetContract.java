/*
 * Copyright (c) 2022. Bernard Bou
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
	public interface VnWords
	{
		String TABLE = Q.WORDS.TABLE;
		String URI = VnWords.TABLE;
		String VNWORDID = V.VNWORDID;
		String WORDID = V.WORDID;
		String WORD = V.WORD;
	}

	public interface VnClasses
	{
		String TABLE = Q.VNCLASSES.TABLE;
		String URI = VnClasses.TABLE;
		String URI1 = "vn_class1";
		String WORDID = V.WORDID;
		String POS = V.POSID;
		String CLASSID = V.CLASSID;
		String CLASS = V.CLASS;
		String CLASSTAG = V.CLASSTAG;
	}

	public interface VnClasses_X
	{
		String URI_BY_VNCLASS ="vnclasses_x_by_vnclass";
		String WORDID = V.WORDID;
		String POS = V.POSID;
		String CLASSID = V.CLASSID;
		String CLASS = V.CLASS;
		String CLASSTAG = V.CLASSTAG;
	}

	public interface Words_VnClasses
	{
		String TABLE = "words_vnclasses";
		String URI = Words_VnClasses.TABLE;
		String WORDID = V.WORDID;
		String SYNSETID = V.SYNSETID;
		String CLASSID = V.CLASSID;
		String CLASS = V.CLASS;
		String CLASSTAG = V.CLASSTAG;
		String SENSENUM = V.SENSENUM;
		String SENSEKEY = V.SENSEKEY;
		String QUALITY = V.QUALITY;
		String NULLSYNSET = V.NULLSYNSET;
	}

	public interface VnClasses_VnMembers_X
	{
		String URI_BY_WORD = "vnclasses_vnmembers_x_by_word";
		String CLASSID = V.CLASSID;
		String VNWORDID = V.VNWORDID;
		String WORDID = V.WORDID;
		String WORD = V.WORD;
		String DEFINITIONS = V.DEFINITIONS;
		String GROUPINGS = V.GROUPINGS;
		String DEFINITION = V.DEFINITION;
		String GROUPING = V.GROUPING;
	}

	public interface VnClasses_VnRoles_X
	{
		String URI_BY_ROLE = "vnclasses_vnroles_x_by_vnrole";
		String CLASSID = V.CLASSID;
		String ROLEID = V.ROLEID;
		String ROLETYPE = V.ROLETYPE;
		String RESTRS = V.RESTRS;
	}

	public interface VnClasses_VnFrames_X
	{
		String URI_BY_FRAME = "vnclasses_vnframes_x_by_vnframe";
		String CLASSID = V.CLASSID;
		String FRAMEID = V.FRAMEID;
		String FRAMENAME = V.FRAMENAME;
		String FRAMESUBNAME = V.FRAMESUBNAME;
		String SYNTAX = V.SYNTAX;
		String SEMANTICS = V.SEMANTICS;
		String NUMBER = V.NUMBER;
		String XTAG = V.XTAG;
		String EXAMPLE = V.EXAMPLE;
		String EXAMPLES = V.EXAMPLES;
	}

	public interface Lookup_VnExamples
	{
		String TABLE = Q.LOOKUP_FTS_EXAMPLES.TABLE;
		String URI = TABLE;
		String EXAMPLEID = V.EXAMPLEID;
		String EXAMPLE = V.EXAMPLE;
		String CLASSID = V.CLASSID;
		String FRAMEID = V.FRAMEID;
	}

	public interface Lookup_VnExamples_X
	{
		String URI = "fts_vnexamples_x";
		String URI_BY_EXAMPLE = "fts_vnexamples_x_by_example";
		String EXAMPLEID = V. EXAMPLEID;
		String EXAMPLE =  V.EXAMPLE;
		String CLASSID =  V.CLASSID;
		String CLASS =  V.CLASS;
		String FRAMEID =  V.FRAMEID;
		String CLASSES =  V.CLASSES;
		String FRAMES =  V.FRAMES;
	}

	public interface Suggest_VnWords
	{
		String SEARCH_WORD_PATH = "suggest_vnword";
		String URI = SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
		String VNWORDID =  V.VNWORDID;
		String WORDID =  V.WORDID;
		String WORD =  V.WORD;
	}

	public interface Suggest_FTS_VnWords
	{
		String SEARCH_WORD_PATH = "suggest_fts_vnword";
		String URI = SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
		String VNWORDID =  V.VNWORDID;
		String WORDID =  V.WORDID;
		String WORD = V.WORD;
	}
}
