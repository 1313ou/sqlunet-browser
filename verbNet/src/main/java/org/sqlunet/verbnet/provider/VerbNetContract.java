package org.sqlunet.verbnet.provider;

/**
 * VerbNet provider contract
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VerbNetContract
{
	static public final String AUTHORITY = "org.sqlunet.verbnet.provider"; //

	@SuppressWarnings("unused")
	static public final class VnClasses
	{
		static public final String TABLE = "vnclasses"; //
		static public final String CONTENT_URI = "content://" + VerbNetContract.AUTHORITY + '/' + VnClasses.TABLE; //
		public static final String WORDID = "wordid"; //
		public static final String POS = "pos"; //
		public static final String CLASSID = "classid"; //
		public static final String CLASS = "class"; //
		public static final String CLASSTAG = "classtag"; //
	}

	@SuppressWarnings("unused")
	static public final class Words_VnClasses
	{
		static public final String TABLE = "words_vnclasses"; //
		static public final String CONTENT_URI = "content://" + VerbNetContract.AUTHORITY + '/' + Words_VnClasses.TABLE; //
		public static final String WORDID = "wordid"; //
		public static final String SYNSETID = "synsetid"; //
		public static final String CLASSID = "classid"; //
		public static final String CLASS = "class"; //
		public static final String CLASSTAG = "classtag"; //
		public static final String SENSENUM = "sensenum"; //
		public static final String SENSEKEY = "sensekey"; //
		public static final String QUALITY = "quality"; //
		public static final String NULLSYNSET = "nullsynset"; //
	}

	static public final class VnClasses_VnMembers_X
	{
		static public final String TABLE_BY_WORD = "vnclasses_vnmembers_x_by_word"; //
		static public final String CONTENT_URI = "content://" + VerbNetContract.AUTHORITY + '/' + VnClasses_VnMembers_X.TABLE_BY_WORD; //
		public static final String CLASSID = "classid"; //
		public static final String VNWORDID = "vnwordid"; //
		public static final String WORDID = "wordid"; //
		public static final String LEMMA = "lemma"; //
		public static final String DEFINITIONS = "definitions"; //
		public static final String GROUPINGS = "groupings"; //
		public static final String DEFINITION = "definition"; //
		public static final String GROUPING = "grouping"; //
	}

	static public final class VnClasses_VnRoles_X
	{
		static public final String TABLE_BY_ROLE = "vnclasses_vnroles_x_by_vnrole"; //
		static public final String CONTENT_URI = "content://" + VerbNetContract.AUTHORITY + '/' + VnClasses_VnRoles_X.TABLE_BY_ROLE; //
		public static final String CLASSID = "classid"; //
		public static final String ROLEID = "roleid"; //
		public static final String ROLETYPE = "roletype"; //
		public static final String RESTRS = "restrs"; //
	}

	static public final class VnClasses_VnFrames_X
	{
		static public final String TABLE_BY_FRAME = "vnclasses_vnframes_x_by_vnframe"; //
		static public final String CONTENT_URI = "content://" + VerbNetContract.AUTHORITY + '/' + VnClasses_VnFrames_X.TABLE_BY_FRAME; //
		public static final String CLASSID = "classid"; //
		public static final String FRAMEID = "frameid"; //
		public static final String FRAMENAME = "framename"; //
		public static final String FRAMESUBNAME = "framesubname"; //
		public static final String SYNTAX = "syntax"; //
		public static final String SEMANTICS = "semantics"; //
		public static final String NUMBER = "number"; //
		public static final String XTAG = "xtag"; //
		public static final String EXAMPLE = "example"; //
		public static final String EXAMPLES = "examples"; //
	}

	@SuppressWarnings("unused")
	static public final class Lookup_VnExamples
	{
		static public final String TABLE = "fts_vnexamples"; //
		static public final String CONTENT_URI = "content://" + VerbNetContract.AUTHORITY + '/' + Lookup_VnExamples.TABLE; //
		static public final String EXAMPLEID = "exampleid"; //
		static public final String FRAMEID = "frameid"; //
		static public final String CLASSID = "classid"; //
		static public final String EXAMPLE = "example"; //
	}
}
