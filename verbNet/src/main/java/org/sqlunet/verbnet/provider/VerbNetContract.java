package org.sqlunet.verbnet.provider;

public class VerbNetContract
{
	static public final String AUTHORITY = "org.sqlunet.verbnet.provider"; //$NON-NLS-1$

	@SuppressWarnings("unused")
	static public final class VnClasses
	{
		static public final String TABLE = "vnclasses"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + VerbNetContract.AUTHORITY + '/' + VnClasses.TABLE; //$NON-NLS-1$
		public static final String WORDID = "wordid"; //$NON-NLS-1$
		public static final String POS = "pos"; //$NON-NLS-1$
		public static final String CLASSID = "classid"; //$NON-NLS-1$
		public static final String CLASS = "class"; //$NON-NLS-1$
		public static final String CLASSTAG = "classtag"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class VnClasses_X
	{
		static public final String TABLE_BY_VNCLASS = "vnclasses_x_by_class"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + VerbNetContract.AUTHORITY + '/' + VnClasses_X.TABLE_BY_VNCLASS; //$NON-NLS-1$
		public static final String WORDID = "wordid"; //$NON-NLS-1$
		public static final String POS = "pos"; //$NON-NLS-1$
		public static final String CLASSID = "classid"; //$NON-NLS-1$
		public static final String CLASS = "class"; //$NON-NLS-1$
		public static final String CLASSTAG = "classtag"; //$NON-NLS-1$
		public static final String GROUPING = "grouping"; //$NON-NLS-1$
		public static final String GROUPINGS = "groupings"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class Words_VnClasses
	{
		static public final String TABLE = "words_vnclasses"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + VerbNetContract.AUTHORITY + '/' + Words_VnClasses.TABLE; //$NON-NLS-1$
		public static final String WORDID = "wordid"; //$NON-NLS-1$
		public static final String SYNSETID = "synsetid"; //$NON-NLS-1$
		public static final String CLASSID = "classid"; //$NON-NLS-1$
		public static final String CLASS = "class"; //$NON-NLS-1$
		public static final String CLASSTAG = "classtag"; //$NON-NLS-1$
		public static final String SENSENUM = "sensenum"; //$NON-NLS-1$
		public static final String SENSEKEY = "sensekey"; //$NON-NLS-1$
		public static final String QUALITY = "quality"; //$NON-NLS-1$
		public static final String NULLSYNSET = "nullsynset"; //$NON-NLS-1$
	}

	static public final class Words_VnClasses_VnGroupings
	{
		static public final String TABLE_BY_CLASS = "words_vnclasses_vngroupings_by_vnclass"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + VerbNetContract.AUTHORITY + '/' + Words_VnClasses_VnGroupings.TABLE_BY_CLASS; //$NON-NLS-1$
		public static final String WORDID = "wordid"; //$NON-NLS-1$
		public static final String SYNSETID = "synsetid"; //$NON-NLS-1$
		public static final String CLASSID = "classid"; //$NON-NLS-1$
		public static final String CLASS = "class"; //$NON-NLS-1$
		public static final String CLASSTAG = "classtag"; //$NON-NLS-1$
		public static final String SENSENUM = "sensenum"; //$NON-NLS-1$
		public static final String SENSEKEY = "sensekey"; //$NON-NLS-1$
		public static final String QUALITY = "quality"; //$NON-NLS-1$
		public static final String GROUPING = "grouping"; //$NON-NLS-1$
		public static final String GROUPINGS = "groupings"; //$NON-NLS-1$
		public static final String NULLSYNSET = "nullsynset"; //$NON-NLS-1$
	}

	static public final class VnClasses_VnRoles_X
	{
		static public final String TABLE_BY_ROLE = "vnclasses_vnroles_x_by_vnrole"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + VerbNetContract.AUTHORITY + '/' + VnClasses_VnRoles_X.TABLE_BY_ROLE; //$NON-NLS-1$
		public static final String CLASSID = "classid"; //$NON-NLS-1$
		public static final String ROLEID = "roleid"; //$NON-NLS-1$
		public static final String ROLETYPE = "roletype"; //$NON-NLS-1$
		public static final String RESTRS = "restrs"; //$NON-NLS-1$
	}

	static public final class VnClasses_VnFrames_X
	{
		static public final String TABLE_BY_FRAME = "vnclasses_vnframes_x_by_vnframe"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + VerbNetContract.AUTHORITY + '/' + VnClasses_VnFrames_X.TABLE_BY_FRAME; //$NON-NLS-1$
		public static final String CLASSID = "classid"; //$NON-NLS-1$
		public static final String FRAMEID = "frameid"; //$NON-NLS-1$
		public static final String FRAMENAME = "framename"; //$NON-NLS-1$
		public static final String FRAMESUBNAME = "framesubname"; //$NON-NLS-1$
		public static final String SYNTAX = "syntax"; //$NON-NLS-1$
		public static final String SEMANTICS = "semantics"; //$NON-NLS-1$
		public static final String NUMBER = "number"; //$NON-NLS-1$
		public static final String XTAG = "xtag"; //$NON-NLS-1$
		public static final String EXAMPLE = "example"; //$NON-NLS-1$
		public static final String EXAMPLES = "examples"; //$NON-NLS-1$
	}
}
