package org.sqlunet.provider;

/**
 * Extended cross WordNet-FrameNet-PropBank-VerbNet provider contract
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public final class XSqlUNetContract
{
	// Contract for SqlUNet cross provider

	static public final String AUTHORITY = "org.sqlunet.cross.provider";
	// A L I A S E S

	static public final String WORD = "w";
	static public final String SENSE = "s";
	static public final String SYNSET = "y";
	static public final String POS = "p";

	// Word cross reference

	@SuppressWarnings("unused")
	static public final class Words_FnWords_PbWords_VnWords
	{
		static public final String TABLE = "words_fnwords_pbwords_vnwords";
		static public final String CONTENT_URI = "content://" + XSqlUNetContract.AUTHORITY + '/' + Words_FnWords_PbWords_VnWords.TABLE;
		public static final String WORDID = "wordid";
		public static final String FNWORDID = "fnwordid";
		public static final String PBWORDID = "pbwordid";
		public static final String VNWORDID = "vnwordid";
		public static final String SYNSETID = "synsetid";
		static public final String LEXID = "lexid";
		static public final String SENSEID = "senseid";
		public static final String LEMMA = "lemma";
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

	// PredicateMatrix

	@SuppressWarnings("unused")
	static public final class PredicateMatrix
	{
		static public final String TABLE = "pm";
		static public final String CONTENT_URI = "content://" + XSqlUNetContract.AUTHORITY + '/' + PredicateMatrix.TABLE;
		public static final String WORDID = "wordid";
		public static final String SYNSETID = "synsetid";
		public static final String VNWORDID = "vnwordid";
		public static final String VNCLASSID = "vnclassid";
		public static final String PBWORDID = "pbwordid";
		public static final String PBROLESETID = "pbrolesetid";
		public static final String FNWORDID = "fnwordid";
		public static final String LUID = "fnluid";
		public static final String FRAMEID = "fnframeid";
	}

	@SuppressWarnings("unused")
	static public final class PredicateMatrix_VerbNet
	{
		static public final String TABLE = "predicatematrix_verbnet";
		static public final String CONTENT_URI = "content://" + XSqlUNetContract.AUTHORITY + '/' + PredicateMatrix_VerbNet.TABLE;
		public static final String WORDID = "wordid";
		public static final String SYNSETID = "synsetid";
		public static final String VNWORDID = "vnwordid";
		public static final String CLASSID = "vnclassid";
	}

	@SuppressWarnings("unused")
	static public final class PredicateMatrix_PropBank
	{
		static public final String TABLE = "predicatematrix_propbank";
		static public final String CONTENT_URI = "content://" + XSqlUNetContract.AUTHORITY + '/' + PredicateMatrix_PropBank.TABLE;
		public static final String WORDID = "wordid";
		public static final String SYNSETID = "synsetid";
		public static final String PBWORDID = "pbwordid";
		public static final String ROLESETID = "pbrolesetid";
	}

	@SuppressWarnings("unused")
	static public final class PredicateMatrix_FrameNet
	{
		static public final String TABLE = "predicatematrix_framenet";
		static public final String CONTENT_URI = "content://" + XSqlUNetContract.AUTHORITY + '/' + PredicateMatrix_FrameNet.TABLE;
		public static final String WORDID = "wordid";
		public static final String SYNSETID = "synsetid";
		public static final String FNWORDID = "fnwordid";
		public static final String LUID = "fnluid";
		public static final String FRAMEID = "fnframeid";
	}

	// PredicateMatrix unions

	@SuppressWarnings("unused")
	static public final class Words_XNet_U
	{
		public static final String WORDID = "wordid";
		public static final String SYNSETID = "synsetid";
		public static final String XID = "xid";
		public static final String XCLASSID = "xclassid";
		public static final String XMEMBERID = "xmemberid";
		public static final String XNAME = "xname";
		public static final String XHEADER = "xheader";
		public static final String XINFO = "xinfo";
		public static final String XDEFINITION = "xdefinition";
		public static final String SOURCE = "source";
		public static final String SOURCES = "sources";
	}

	@SuppressWarnings("unused")
	static public final class Words_VnWords_VnClasses_U
	{
		static public final String TABLE = "U_words_vnwords_vnclasses";
		static public final String CONTENT_URI = "content://" + XSqlUNetContract.AUTHORITY + '/' + Words_VnWords_VnClasses_U.TABLE;
		public static final String WORDID = "wordid";
		public static final String SYNSETID = "synsetid";
		public static final String VNWORDID = "vnwordid";
		public static final String CLASSID = "classid";
		public static final String CLASS = "class";
		public static final String CLASSTAG = "classtag";
		public static final String DEFINITION = "definition";
		public static final String SOURCE = "source";
		public static final String SOURCES = "sources";
	}

	@SuppressWarnings("unused")
	static public final class Words_PbWords_PbRolesets_U
	{
		static public final String TABLE = "U_words_pbwords_pbrolesets";
		static public final String CONTENT_URI = "content://" + XSqlUNetContract.AUTHORITY + '/' + Words_PbWords_PbRolesets_U.TABLE;
		public static final String WORDID = "wordid";
		public static final String SYNSETID = "synsetid";
		public static final String ROLESETID = "rolesetid";
		public static final String ROLESETNAME = "rolesetname";
		public static final String ROLESETHEAD = "rolesethead";
		public static final String ROLESETDESCR = "rolesetdescr";
		public static final String DEFINITION = "definition";
		public static final String SOURCE = "source";
		public static final String SOURCES = "sources";
	}

	@SuppressWarnings("unused")
	static public final class Words_FnWords_FnFrames_U
	{
		static public final String TABLE = "U_words_fnwords_fnframes";
		static public final String CONTENT_URI = "content://" + XSqlUNetContract.AUTHORITY + '/' + Words_FnWords_FnFrames_U.TABLE;
		public static final String WORDID = "wordid";
		public static final String SYNSETID = "synsetid";
		public static final String FNWORDID = "fnwordid";
		public static final String FRAMEID = "frameid";
		public static final String FRAME = "frame";
		public static final String FRAMEDEFINITION = "framedefinition";
		public static final String LUID = "luid";
		public static final String LEXUNIT = "lexunit";
		public static final String LUDEFINITION = "ludefinition";
		public static final String DEFINITION = "definition";
		public static final String SOURCE = "source";
		public static final String SOURCES = "sources";
	}
}
