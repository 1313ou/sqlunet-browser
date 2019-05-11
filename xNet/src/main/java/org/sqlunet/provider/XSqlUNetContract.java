package org.sqlunet.provider;

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

	@SuppressWarnings("unused")
	static public final class Words_FnWords_PbWords_VnWords
	{
		static public final String TABLE = "words_fnwords_pbwords_vnwords";
		static public final String CONTENT_URI_TABLE = Words_FnWords_PbWords_VnWords.TABLE;
		static public final String WORDID = "wordid";
		static public final String FNWORDID = "fnwordid";
		static public final String PBWORDID = "pbwordid";
		static public final String VNWORDID = "vnwordid";
		static public final String SYNSETID = "synsetid";
		static public final String LEXID = "lexid";
		static public final String SENSEID = "senseid";
		static public final String LEMMA = "lemma";
		static public final String SENSENUM = "sensenum";
		static public final String SENSEKEY = "sensekey";
		static public final String POS = "pos";
		static public final String POSNAME = "posname";
		static public final String LEXDOMAIN = "lexdomain";
		static public final String DEFINITION = "definition";
		static public final String CASED = "cased";
		static public final String TAGCOUNT = "tagcount";
		static public final String SOURCES = "sources";
	}

	static public final class Words_PbWords_VnWords
	{
		static public final String TABLE = "words_pbwords_vnwords";
		static public final String CONTENT_URI_TABLE = Words_PbWords_VnWords.TABLE;
		static public final String WORDID = "wordid";
		static public final String PBWORDID = "pbwordid";
		static public final String VNWORDID = "vnwordid";
		static public final String SYNSETID = "synsetid";
		static public final String LEXID = "lexid";
		static public final String SENSEID = "senseid";
		static public final String LEMMA = "lemma";
		static public final String SENSENUM = "sensenum";
		static public final String SENSEKEY = "sensekey";
		static public final String POS = "pos";
		static public final String POSNAME = "posname";
		static public final String LEXDOMAIN = "lexdomain";
		static public final String DEFINITION = "definition";
		static public final String CASED = "cased";
		static public final String TAGCOUNT = "tagcount";
		static public final String SOURCES = "sources";
	}

	@SuppressWarnings("unused")
	static public final class Words_VnWords_VnClasses
	{
		static public final String TABLE = "words_vnwords_vnclasses";
		static public final String CONTENT_URI_TABLE = Words_VnWords_VnClasses.TABLE;
		static public final String WORDID = "wordid";
		static public final String SYNSETID = "synsetid";
		static public final String VNWORDID = "vnwordid";
		static public final String CLASSID = "classid";
		static public final String CLASS = "class";
		static public final String CLASSTAG = "classtag";
		static public final String DEFINITION = "definition";
		static public final String SOURCE = "source";
		static public final String SOURCES = "sources";
	}

	@SuppressWarnings("unused")
	static public final class Words_PbWords_PbRoleSets
	{
		static public final String TABLE = "words_pbwords_pbrolesets";
		static public final String CONTENT_URI_TABLE = Words_PbWords_PbRoleSets.TABLE;
		static public final String WORDID = "wordid";
		static public final String SYNSETID = "synsetid";
		static public final String ROLESETID = "rolesetid";
		static public final String ROLESETNAME = "rolesetname";
		static public final String ROLESETHEAD = "rolesethead";
		static public final String ROLESETDESCR = "rolesetdescr";
		static public final String DEFINITION = "definition";
		static public final String SOURCE = "source";
		static public final String SOURCES = "sources";
	}

	@SuppressWarnings("unused")
	static public final class Words_XNet
	{
		static public final String WORDID = "wordid";
		static public final String SYNSETID = "synsetid";
		static public final String XID = "xid";
		static public final String XCLASSID = "xclassid";
		static public final String XMEMBERID = "xmemberid";
		static public final String XNAME = "xname";
		static public final String XHEADER = "xheader";
		static public final String XINFO = "xinfo";
		static public final String XDEFINITION = "xdefinition";
		static public final String SOURCE = "source";
		static public final String SOURCES = "sources";
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

	@SuppressWarnings("unused")
	static public final class PredicateMatrix
	{
		static public final String TABLE = "pm";
		static public final String CONTENT_URI_TABLE = PredicateMatrix.TABLE;
		static public final String WORDID = "wordid";
		static public final String SYNSETID = "synsetid";
		static public final String VNWORDID = "vnwordid";
		static public final String VNCLASSID = "vnclassid";
		static public final String PBWORDID = "pbwordid";
		static public final String PBROLESETID = "pbrolesetid";
		static public final String FNWORDID = "fnwordid";
		static public final String LUID = "fnluid";
		static public final String FRAMEID = "fnframeid";
	}

	@SuppressWarnings("unused")
	static public final class PredicateMatrix_VerbNet
	{
		static public final String TABLE = "predicatematrix_verbnet";
		static public final String CONTENT_URI_TABLE = PredicateMatrix_VerbNet.TABLE;
		static public final String WORDID = "wordid";
		static public final String SYNSETID = "synsetid";
		static public final String VNWORDID = "vnwordid";
		static public final String CLASSID = "vnclassid";
	}

	@SuppressWarnings("unused")
	static public final class PredicateMatrix_PropBank
	{
		static public final String TABLE = "predicatematrix_propbank";
		static public final String CONTENT_URI_TABLE = PredicateMatrix_PropBank.TABLE;
		static public final String WORDID = "wordid";
		static public final String SYNSETID = "synsetid";
		static public final String PBWORDID = "pbwordid";
		static public final String ROLESETID = "pbrolesetid";
	}

	@SuppressWarnings("unused")
	static public final class PredicateMatrix_FrameNet
	{
		static public final String TABLE = "predicatematrix_framenet";
		static public final String CONTENT_URI_TABLE = PredicateMatrix_FrameNet.TABLE;
		static public final String WORDID = "wordid";
		static public final String SYNSETID = "synsetid";
		static public final String FNWORDID = "fnwordid";
		static public final String LUID = "fnluid";
		static public final String FRAMEID = "fnframeid";
	}

	// PredicateMatrix unions

	@SuppressWarnings("unused")
	static public final class Words_XNet_U
	{
		static public final String WORDID = "wordid";
		static public final String SYNSETID = "synsetid";
		static public final String XID = "xid";
		static public final String XCLASSID = "xclassid";
		static public final String XMEMBERID = "xmemberid";
		static public final String XNAME = "xname";
		static public final String XHEADER = "xheader";
		static public final String XINFO = "xinfo";
		static public final String XDEFINITION = "xdefinition";
		static public final String SOURCE = "source";
		static public final String SOURCES = "sources";
	}

	@SuppressWarnings("unused")
	static public final class Words_VnWords_VnClasses_U
	{
		static public final String TABLE = "U_words_vnwords_vnclasses";
		static public final String CONTENT_URI_TABLE = Words_VnWords_VnClasses_U.TABLE;
		static public final String WORDID = "wordid";
		static public final String SYNSETID = "synsetid";
		static public final String VNWORDID = "vnwordid";
		static public final String CLASSID = "classid";
		static public final String CLASS = "class";
		static public final String CLASSTAG = "classtag";
		static public final String DEFINITION = "definition";
		static public final String SOURCE = "source";
		static public final String SOURCES = "sources";
	}

	@SuppressWarnings("unused")
	static public final class Words_PbWords_PbRoleSets_U
	{
		static public final String TABLE = "U_words_pbwords_pbrolesets";
		static public final String CONTENT_URI_TABLE = Words_PbWords_PbRoleSets_U.TABLE;
		static public final String WORDID = "wordid";
		static public final String SYNSETID = "synsetid";
		static public final String ROLESETID = "rolesetid";
		static public final String ROLESETNAME = "rolesetname";
		static public final String ROLESETHEAD = "rolesethead";
		static public final String ROLESETDESCR = "rolesetdescr";
		static public final String DEFINITION = "definition";
		static public final String SOURCE = "source";
		static public final String SOURCES = "sources";
	}

	@SuppressWarnings("unused")
	static public final class Words_FnWords_FnFrames_U
	{
		static public final String TABLE = "U_words_fnwords_fnframes";
		static public final String CONTENT_URI_TABLE = Words_FnWords_FnFrames_U.TABLE;
		static public final String WORDID = "wordid";
		static public final String SYNSETID = "synsetid";
		static public final String FNWORDID = "fnwordid";
		static public final String FRAMEID = "frameid";
		static public final String FRAME = "frame";
		static public final String FRAMEDEFINITION = "framedefinition";
		static public final String LUID = "luid";
		static public final String LEXUNIT = "lexunit";
		static public final String LUDEFINITION = "ludefinition";
		static public final String DEFINITION = "definition";
		static public final String SOURCE = "source";
		static public final String SOURCES = "sources";
	}
}
