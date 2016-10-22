package org.sqlunet.provider;

/**
 * Extended cross WordNet-FrameNet-PropBank-VerbNet provider contract
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public final class XSqlUNetContract
{
	// Contract for SqlUNet cross provider

	static public final String AUTHORITY = "org.sqlunet.cross.provider"; //$NON-NLS-1$

	// Word cross reference

	@SuppressWarnings("unused")
	static public final class Words_FnWords_PbWords_VnWords
	{
		static public final String TABLE = "words_fnwords_pbwords_vnwords"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + XSqlUNetContract.AUTHORITY + '/' + Words_FnWords_PbWords_VnWords.TABLE; //$NON-NLS-1$
		public static final String WORDID = "wordid"; //$NON-NLS-1$
		public static final String FNWORDID = "fnwordid"; //$NON-NLS-1$
		public static final String PBWORDID = "pbwordid"; //$NON-NLS-1$
		public static final String VNWORDID = "vnwordid"; //$NON-NLS-1$
		public static final String SYNSETID = "synsetid"; //$NON-NLS-1$
		static public final String LEXID = "lexid"; //$NON-NLS-1$
		static public final String SENSEID = "senseid"; //$NON-NLS-1$
		public static final String LEMMA = "lemma"; //$NON-NLS-1$
		static public final String SENSENUM = "sensenum"; //$NON-NLS-1$
		static public final String SENSEKEY = "sensekey"; //$NON-NLS-1$
		static public final String POS = "pos"; //$NON-NLS-1$
		static public final String POSNAME = "posname"; //$NON-NLS-1$
		static public final String LEXDOMAIN = "lexdomain"; //$NON-NLS-1$
		static public final String DEFINITION = "definition"; //$NON-NLS-1$
		static public final String CASED = "cased"; //$NON-NLS-1$
		static public final String TAGCOUNT = "tagcount"; //$NON-NLS-1$
		static public final String SOURCES = "sources"; //$NON-NLS-1$
	}

	// PredicateMatrix

	@SuppressWarnings("unused")
	static public final class PredicateMatrix
	{
		static public final String TABLE = "pm"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + XSqlUNetContract.AUTHORITY + '/' + PredicateMatrix.TABLE; //$NON-NLS-1$
		public static final String WORDID = "wordid"; //$NON-NLS-1$
		public static final String SYNSETID = "synsetid"; //$NON-NLS-1$
		public static final String VNWORDID = "vnwordid"; //$NON-NLS-1$
		public static final String VNCLASSID = "vnclassid"; //$NON-NLS-1$
		public static final String PBWORDID = "pbwordid"; //$NON-NLS-1$
		public static final String PBROLESETID = "pbrolesetid"; //$NON-NLS-1$
		public static final String FNWORDID = "fnwordid"; //$NON-NLS-1$
		public static final String LUID = "fnluid"; //$NON-NLS-1$
		public static final String FRAMEID = "fnframeid"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class PredicateMatrix_VerbNet
	{
		static public final String TABLE = "predicatematrix_verbnet"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + XSqlUNetContract.AUTHORITY + '/' + PredicateMatrix_VerbNet.TABLE; //$NON-NLS-1$
		public static final String WORDID = "wordid"; //$NON-NLS-1$
		public static final String SYNSETID = "synsetid"; //$NON-NLS-1$
		public static final String VNWORDID = "vnwordid"; //$NON-NLS-1$
		public static final String CLASSID = "vnclassid"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class PredicateMatrix_PropBank
	{
		static public final String TABLE = "predicatematrix_propbank"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + XSqlUNetContract.AUTHORITY + '/' + PredicateMatrix_PropBank.TABLE; //$NON-NLS-1$
		public static final String WORDID = "wordid"; //$NON-NLS-1$
		public static final String SYNSETID = "synsetid"; //$NON-NLS-1$
		public static final String PBWORDID = "pbwordid"; //$NON-NLS-1$
		public static final String ROLESETID = "pbrolesetid"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class PredicateMatrix_FrameNet
	{
		static public final String TABLE = "predicatematrix_framenet"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + XSqlUNetContract.AUTHORITY + '/' + PredicateMatrix_FrameNet.TABLE; //$NON-NLS-1$
		public static final String WORDID = "wordid"; //$NON-NLS-1$
		public static final String SYNSETID = "synsetid"; //$NON-NLS-1$
		public static final String FNWORDID = "fnwordid"; //$NON-NLS-1$
		public static final String LUID = "fnluid"; //$NON-NLS-1$
		public static final String FRAMEID = "fnframeid"; //$NON-NLS-1$
	}

	// PredicateMatrix unions

	@SuppressWarnings("unused")
	static public final class Words_XNet_U
	{
		public static final String WORDID = "wordid"; //$NON-NLS-1$
		public static final String SYNSETID = "synsetid"; //$NON-NLS-1$
		public static final String XID = "xid"; //$NON-NLS-1$
		public static final String XCLASSID = "xclassid"; //$NON-NLS-1$
		public static final String XINSTANCEID = "xinstanceid"; //$NON-NLS-1$
		public static final String XNAME = "xname"; //$NON-NLS-1$
		public static final String XHEADER = "xheader"; //$NON-NLS-1$
		public static final String XINFO = "xinfo"; //$NON-NLS-1$
		public static final String XDEFINITION = "xdefinition"; //$NON-NLS-1$
		public static final String SOURCE = "source"; //$NON-NLS-1$
		public static final String SOURCES = "sources"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class Words_VnWords_VnClasses_U
	{
		static public final String TABLE = "U_words_vnwords_vnclasses"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + XSqlUNetContract.AUTHORITY + '/' + Words_VnWords_VnClasses_U.TABLE; //$NON-NLS-1$
		public static final String WORDID = "wordid"; //$NON-NLS-1$
		public static final String SYNSETID = "synsetid"; //$NON-NLS-1$
		public static final String VNWORDID = "vnwordid"; //$NON-NLS-1$
		public static final String CLASSID = "classid"; //$NON-NLS-1$
		public static final String CLASS = "class"; //$NON-NLS-1$
		public static final String CLASSTAG = "classtag"; //$NON-NLS-1$
		public static final String DEFINITION = "definition"; //$NON-NLS-1$
		public static final String SOURCE = "source"; //$NON-NLS-1$
		public static final String SOURCES = "sources"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class Words_PbWords_PbRolesets_U
	{
		static public final String TABLE = "U_words_pbwords_pbrolesets"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + XSqlUNetContract.AUTHORITY + '/' + Words_PbWords_PbRolesets_U.TABLE; //$NON-NLS-1$
		public static final String WORDID = "wordid"; //$NON-NLS-1$
		public static final String SYNSETID = "synsetid"; //$NON-NLS-1$
		public static final String ROLESETID = "rolesetid"; //$NON-NLS-1$
		public static final String ROLESETNAME = "rolesetname"; //$NON-NLS-1$
		public static final String ROLESETHEAD = "rolesethead"; //$NON-NLS-1$
		public static final String ROLESETDESCR = "rolesetdescr"; //$NON-NLS-1$
		public static final String DEFINITION = "definition"; //$NON-NLS-1$
		public static final String SOURCE = "source"; //$NON-NLS-1$
		public static final String SOURCES = "sources"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class Words_FnWords_FnFrames_U
	{
		static public final String TABLE = "U_words_fnwords_fnframes"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + XSqlUNetContract.AUTHORITY + '/' + Words_FnWords_FnFrames_U.TABLE; //$NON-NLS-1$
		public static final String WORDID = "wordid"; //$NON-NLS-1$
		public static final String SYNSETID = "synsetid"; //$NON-NLS-1$
		public static final String FNWORDID = "fnwordid"; //$NON-NLS-1$
		public static final String FRAMEID = "frameid"; //$NON-NLS-1$
		public static final String FRAME = "frame"; //$NON-NLS-1$
		public static final String FRAMEDEFINITION = "framedefinition"; //$NON-NLS-1$
		public static final String LUID = "luid"; //$NON-NLS-1$
		public static final String LEXUNIT = "lexunit"; //$NON-NLS-1$
		public static final String LUDEFINITION = "ludefinition"; //$NON-NLS-1$
		public static final String DEFINITION = "definition"; //$NON-NLS-1$
		public static final String SOURCE = "source"; //$NON-NLS-1$
		public static final String SOURCES = "sources"; //$NON-NLS-1$
	}
}
