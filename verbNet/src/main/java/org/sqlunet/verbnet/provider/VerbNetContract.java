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
	static public final class VnClasses_X
	{
		static public final String TABLE_BY_VNCLASS = "vnclasses_x_by_class"; //
		static public final String CONTENT_URI = "content://" + VerbNetContract.AUTHORITY + '/' + VnClasses_X.TABLE_BY_VNCLASS; //
		public static final String WORDID = "wordid"; //
		public static final String POS = "pos"; //
		public static final String CLASSID = "classid"; //
		public static final String CLASS = "class"; //
		public static final String CLASSTAG = "classtag"; //
		public static final String GROUPING = "grouping"; //
		public static final String GROUPINGS = "groupings"; //
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

	static public final class Words_VnClasses_VnGroupings
	{
		static public final String TABLE_BY_CLASS = "words_vnclasses_vngroupings_by_vnclass"; //
		static public final String CONTENT_URI = "content://" + VerbNetContract.AUTHORITY + '/' + Words_VnClasses_VnGroupings.TABLE_BY_CLASS; //
		public static final String WORDID = "wordid"; //
		public static final String SYNSETID = "synsetid"; //
		public static final String CLASSID = "classid"; //
		public static final String CLASS = "class"; //
		public static final String CLASSTAG = "classtag"; //
		public static final String SENSENUM = "sensenum"; //
		public static final String SENSEKEY = "sensekey"; //
		public static final String QUALITY = "quality"; //
		public static final String GROUPING = "grouping"; //
		public static final String GROUPINGS = "groupings"; //
		public static final String NULLSYNSET = "nullsynset"; //
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
}
