package org.sqlunet.verbnet.provider;

/**
 * VerbNet provider contract
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VerbNetContract
{
	@SuppressWarnings("unused")
	static public final class VnClasses
	{
		static public final String TABLE = "vnclasses";
		static public final String CONTENT_URI_TABLE = VnClasses.TABLE;
		static public final String WORDID = "wordid";
		static public final String POS = "pos";
		static public final String CLASSID = "classid";
		static public final String CLASS = "class";
		static public final String CLASSTAG = "classtag";
	}

	@SuppressWarnings("unused")
	static public final class Words_VnClasses
	{
		static public final String TABLE = "words_vnclasses";
		static public final String CONTENT_URI_TABLE = Words_VnClasses.TABLE;
		static public final String WORDID = "wordid";
		static public final String SYNSETID = "synsetid";
		static public final String CLASSID = "classid";
		static public final String CLASS = "class";
		static public final String CLASSTAG = "classtag";
		static public final String SENSENUM = "sensenum";
		static public final String SENSEKEY = "sensekey";
		static public final String QUALITY = "quality";
		static public final String NULLSYNSET = "nullsynset";
	}

	static public final class VnClasses_VnMembers_X
	{
		static public final String TABLE_BY_WORD = "vnclasses_vnmembers_x_by_word";
		static public final String CONTENT_URI_TABLE = VnClasses_VnMembers_X.TABLE_BY_WORD;
		static public final String CLASSID = "classid";
		static public final String VNWORDID = "vnwordid";
		static public final String WORDID = "wordid";
		static public final String LEMMA = "lemma";
		static public final String DEFINITIONS = "definitions";
		static public final String GROUPINGS = "groupings";
		static public final String DEFINITION = "definition";
		static public final String GROUPING = "grouping";
	}

	static public final class VnClasses_VnRoles_X
	{
		static public final String TABLE_BY_ROLE = "vnclasses_vnroles_x_by_vnrole";
		static public final String CONTENT_URI_TABLE = VnClasses_VnRoles_X.TABLE_BY_ROLE;
		static public final String CLASSID = "classid";
		static public final String ROLEID = "roleid";
		static public final String ROLETYPE = "roletype";
		static public final String RESTRS = "restrs";
	}

	static public final class VnClasses_VnFrames_X
	{
		static public final String TABLE_BY_FRAME = "vnclasses_vnframes_x_by_vnframe";
		static public final String CONTENT_URI_TABLE = VnClasses_VnFrames_X.TABLE_BY_FRAME;
		static public final String CLASSID = "classid";
		static public final String FRAMEID = "frameid";
		static public final String FRAMENAME = "framename";
		static public final String FRAMESUBNAME = "framesubname";
		static public final String SYNTAX = "syntax";
		static public final String SEMANTICS = "semantics";
		static public final String NUMBER = "number";
		static public final String XTAG = "xtag";
		static public final String EXAMPLE = "example";
		static public final String EXAMPLES = "examples";
	}

	@SuppressWarnings("unused")
	static public final class Lookup_VnExamples
	{
		static public final String TABLE = "fts_vnexamples";
		static public final String CONTENT_URI_TABLE = Lookup_VnExamples.TABLE;
		static public final String EXAMPLEID = "exampleid";
		static public final String EXAMPLE = "example";
		static public final String CLASSID = "classid";
		static public final String FRAMEID = "frameid";
	}

	@SuppressWarnings("unused")
	static public final class Lookup_VnExamples_X
	{
		static public final String TABLE = "fts_vnexamples_x";
		static public final String TABLE_BY_EXAMPLE = "fts_vnexamples_x_by_example";
		static public final String CONTENT_URI_TABLE = Lookup_VnExamples_X.TABLE_BY_EXAMPLE;
		static public final String EXAMPLEID = "exampleid";
		static public final String EXAMPLE = "example";
		static public final String CLASSID = "classid";
		static public final String CLASS = "class";
		static public final String FRAMEID = "frameid";
		static public final String CLASSES = "classes";
		static public final String FRAMES = "frames";
	}
}
