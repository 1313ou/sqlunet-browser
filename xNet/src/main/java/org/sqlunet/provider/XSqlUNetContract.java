/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.provider;

import org.sqlunet.xnet.provider.V;

/**
 * Extended cross WordNet-FrameNet-PropBank-VerbNet provider contract
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public final class XSqlUNetContract
{
	// A L I A S E S

	static public final String WORD = "w";
	static public final String SENSE = "s";
	static public final String SYNSET = "y";
	static public final String POS = "p";
	static public final String CLASS = "c";

	// Word cross reference

	static public final class Words_FnWords_PbWords_VnWords
	{
		static public final String TABLE = "words_fnwords_pbwords_vnwords";
		static public final String CONTENT_URI_TABLE = Words_FnWords_PbWords_VnWords.TABLE;
		static public final String WORD = V.WORD;
		static public final String WORDID = V.WORDID;
		static public final String FNWORDID = V.FNWORDID;
		static public final String PBWORDID = V.PBWORDID;
		static public final String VNWORDID = V.VNWORDID;
		static public final String SYNSETID = V.SYNSETID;
		static public final String LUID = V.LUID;
		static public final String SENSEID = V.SENSEID;
		static public final String SENSENUM = V.SENSENUM;
		static public final String SENSEKEY = V.SENSEKEY;
		static public final String POSID = V.POSID;
		static public final String POS = V.POS;
		static public final String DOMAIN = V.DOMAIN;
		static public final String DEFINITION = V.DEFINITION;
		static public final String CASED = V.CASEDWORD;
		static public final String TAGCOUNT = V.TAGCOUNT;
		static public final String SOURCES = V.SOURCES;
	}

	static public final class Words_PbWords_VnWords
	{
		static public final String TABLE = "words_pbwords_vnwords";
		static public final String CONTENT_URI_TABLE = Words_PbWords_VnWords.TABLE;
		static public final String WORD = V.WORD;
		static public final String WORDID = V.WORDID;
		static public final String PBWORDID = V.PBWORDID;
		static public final String VNWORDID = V.VNWORDID;
		static public final String SYNSETID = V.SYNSETID;
		static public final String LUID = V.LUID;
		static public final String SENSEID = V.SENSEID;
		static public final String SENSENUM = V.SENSENUM;
		static public final String SENSEKEY = V.SENSEKEY;
		static public final String POSID = V.POSID;
		static public final String POS = V.POS;
		static public final String DOMAIN = V.DOMAIN;
		static public final String DEFINITION = V.DEFINITION;
		static public final String CASEDWORD = V.CASEDWORD;
		static public final String TAGCOUNT = V.TAGCOUNT;
		static public final String SOURCES = V.SOURCES;
	}

	static public final class Words_VnWords_VnClasses
	{
		static public final String TABLE = "words_vnwords_vnclasses";
		static public final String CONTENT_URI_TABLE = Words_VnWords_VnClasses.TABLE;
		static public final String WORDID = V.WORDID;
		static public final String SYNSETID = V.SYNSETID;
		static public final String VNWORDID = V.VNWORDID;
		static public final String CLASSID = V.CLASSID;
		static public final String CLASS = V.CLASS;
		static public final String CLASSTAG = V.CLASSTAG;
		static public final String DEFINITION = V.DEFINITION;
		static public final String SOURCE = V.SOURCE;
		static public final String SOURCES = V.SOURCES;
	}

	static public final class Words_PbWords_PbRoleSets
	{
		static public final String TABLE = "words_pbwords_pbrolesets";
		static public final String CONTENT_URI_TABLE = Words_PbWords_PbRoleSets.TABLE;
		static public final String WORDID = V.WORDID;
		static public final String SYNSETID = V.SYNSETID;
		static public final String ROLESETID = V.ROLESETID;
		static public final String ROLESETNAME = V.ROLESETNAME;
		static public final String ROLESETHEAD = V.ROLESETHEAD;
		static public final String ROLESETDESCR = V.ROLESETDESCR;
		static public final String DEFINITION = V.DEFINITION;
		static public final String SOURCE = V.SOURCE;
		static public final String SOURCES = V.SOURCES;
	}

	static public final class Words_XNet
	{
		static public final String WORDID = V.WORDID;
		static public final String SYNSETID = V.SYNSETID;
		static public final String XID = V.XID;
		static public final String XCLASSID = V.XCLASSID;
		static public final String XMEMBERID = V.XMEMBERID;
		static public final String XNAME = V.XNAME;
		static public final String XHEADER = V.XHEADER;
		static public final String XINFO = V.XINFO;
		static public final String XDEFINITION = V.XDEFINITION;
		static public final String SOURCE = V.SOURCE;
		static public final String SOURCES = V.SOURCES;
	}

	// Sources

	static public final class Sources
	{
		static public final String TABLE = "sources";
		static public final String CONTENT_URI_TABLE = Sources.TABLE;
		static public final String ID = "idsource";
		static public final String NAME = "name";
		static public final String VERSION = "version";
		static public final String URL = "url";
		static public final String PROVIDER = "provider";
		static public final String REFERENCE = "reference";
	}

	// PredicateMatrix

	static public final class PredicateMatrix
	{
		static public final String TABLE = "pm";
		static public final String CONTENT_URI_TABLE = PredicateMatrix.TABLE;
		static public final String WORDID = V.WORDID;
		static public final String SYNSETID = V.SYNSETID;
		static public final String VNWORDID = V.VNWORDID;
		static public final String VNCLASSID = V.CLASSID;
		static public final String PBWORDID = V.PBWORDID;
		static public final String PBROLESETID = V.ROLESETID;
		static public final String FNWORDID = V.FNWORDID;
		static public final String LUID = V.LUID;
		static public final String FRAMEID = V.FRAMEID;
	}

	static public final class PredicateMatrix_VerbNet
	{
		static public final String TABLE = "predicatematrix_verbnet";
		static public final String CONTENT_URI_TABLE = PredicateMatrix_VerbNet.TABLE;
		static public final String WORDID = V.WORDID;
		static public final String SYNSETID = V.SYNSETID;
		static public final String VNWORDID = V.VNWORDID;
		static public final String CLASSID = V.CLASSID;
	}

	static public final class PredicateMatrix_PropBank
	{
		static public final String TABLE = "predicatematrix_propbank";
		static public final String CONTENT_URI_TABLE = PredicateMatrix_PropBank.TABLE;
		static public final String WORDID = V.WORDID;
		static public final String SYNSETID = V.SYNSETID;
		static public final String PBWORDID = V.PBWORDID;
		static public final String ROLESETID = V.ROLESETID;
	}

	static public final class PredicateMatrix_FrameNet
	{
		static public final String TABLE = "predicatematrix_framenet";
		static public final String CONTENT_URI_TABLE = PredicateMatrix_FrameNet.TABLE;
		static public final String WORDID = V.WORDID;
		static public final String SYNSETID = V.SYNSETID;
		static public final String FNWORDID = V.FNWORDID;
		static public final String LUID = V.LUID;
		static public final String FRAMEID = V.FRAMEID;
	}

	// PredicateMatrix unions

	static public final class Words_XNet_U
	{
		static public final String WORDID = V.WORDID;
		static public final String SYNSETID = V.SYNSETID;
		static public final String XID = V.XID;
		static public final String XCLASSID = V.XCLASSID;
		static public final String XMEMBERID = V.XMEMBERID;
		static public final String XNAME = V.XNAME;
		static public final String XHEADER = V.XHEADER;
		static public final String XINFO = V.XINFO;
		static public final String XDEFINITION = V.XDEFINITION;
		static public final String SOURCE = V.SOURCE;
		static public final String SOURCES = V.SOURCES;
	}

	static public final class Words_VnWords_VnClasses_U
	{
		static public final String TABLE = "U_words_vnwords_vnclasses";
		static public final String CONTENT_URI_TABLE = Words_VnWords_VnClasses_U.TABLE;
		static public final String WORDID = V.WORDID;
		static public final String SYNSETID = V.SYNSETID;
		static public final String VNWORDID = V.VNWORDID;
		static public final String CLASSID = V.CLASSID;
		static public final String CLASS = V.CLASS;
		static public final String CLASSTAG = V.CLASSTAG;
		static public final String DEFINITION = V.DEFINITION;
		static public final String SOURCE = V.SOURCE;
		static public final String SOURCES = V.SOURCES;
	}

	static public final class Words_PbWords_PbRoleSets_U
	{
		static public final String TABLE = "U_words_pbwords_pbrolesets";
		static public final String CONTENT_URI_TABLE = Words_PbWords_PbRoleSets_U.TABLE;
		static public final String WORDID = V.WORDID;
		static public final String SYNSETID = V.SYNSETID;
		static public final String ROLESETID = V.ROLESETID;
		static public final String ROLESETNAME = V.ROLESETNAME;
		static public final String ROLESETHEAD = V.ROLESETHEAD;
		static public final String ROLESETDESCR = V.ROLESETDESCR;
		static public final String DEFINITION = V.DEFINITION;
		static public final String SOURCE = V.SOURCE;
		static public final String SOURCES = V.SOURCES;
	}

	static public final class Words_FnWords_FnFrames_U
	{
		static public final String TABLE = "U_words_fnwords_fnframes";
		static public final String CONTENT_URI_TABLE = Words_FnWords_FnFrames_U.TABLE;
		static public final String WORDID = V.WORDID;
		static public final String SYNSETID = V.SYNSETID;
		static public final String FNWORDID = V.FNWORDID;
		static public final String FRAMEID = V.FRAMEID;
		static public final String FRAME = V.FRAME;
		static public final String FRAMEDEFINITION = V.FRAMEDEFINITION;
		static public final String LUID = V.LUID;
		static public final String LEXUNIT = V.LEXUNIT;
		static public final String LUDEFINITION = V.LUDEFINITION;
		static public final String DEFINITION = V.DEFINITION;
		static public final String SOURCE = V.SOURCE;
		static public final String SOURCES = V.SOURCES;
	}
}
