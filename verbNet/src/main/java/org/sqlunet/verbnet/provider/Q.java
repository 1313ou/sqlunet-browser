package org.sqlunet.verbnet.provider;

public class Q
{
	public static final String AS_ARGS = "a";
	public static final String AS_CASEDS = "c";
	public static final String AS_DOMAINS = "d";
	public static final String AS_EXAMPLES = "e";
	public static final String AS_FUNCS = "f";
	public static final String AS_MEMBERS = "m";
	public static final String AS_POSES = "p";
	public static final String AS_RELATIONS = "r";
	public static final String AS_SENSES = "s";
	public static final String AS_SYNSETS = "y";
	public static final String AS_SYNSETS2 = "y2";
	public static final String AS_TYPES = "t";
	public static final String AS_WORDS = "w";
	public static final String AS_WORDS2 = "w2";

	public static final String NULLSYNSET = "null";
	public static final String CLASSES = "classes";
	public static final String DEFINITIONS = "definitions";
	public static final String FRAMES = "frames";
	public static final String EXAMPLES = "examples";
	public static final String GROUPINGS = "groupings";
	public static final String MEMBERS = "members";
	public static final String MEMBERS2 = "members2";

	public static final String CLASS = "class";
	public static final String CLASSID = "classid";
	public static final String CLASSTAG = "classtag";
	public static final String DEFINITION = "definition";
	public static final String EXAMPLE = "example";
	public static final String EXAMPLEID = "exampleid";
	public static final String FRAMEID = "frameid";
	public static final String FRAMENAME = "framename";
	public static final String FRAMENAMEID = "framenameid";
	public static final String FRAMESUBNAME = "framesubname";
	public static final String FRAMESUBNAMEID = "framesubnameid";
	public static final String GROUPING = "grouping";
	public static final String GROUPINGID = "groupingid";
	public static final String ISSYN = "issyn";
	public static final String NUMBER = "number";
	public static final String POSID = "posid";
	public static final String PREDICATE = "predicate";
	public static final String PREDICATEID = "predicateid";
	public static final String QUALITY = "quality";
	public static final String RESTR = "restr";
	public static final String RESTRID = "restrid";
	public static final String RESTRS = "restrs";
	public static final String RESTRSID = "restrsid";
	public static final String RESTRVAL = "restrval";
	public static final String ROLEID = "roleid";
	public static final String ROLETYPE = "roletype";
	public static final String ROLETYPEID = "roletypeid";
	public static final String SAMPLESET = "sampleset";
	public static final String SEMANTICS = "semantics";
	public static final String SEMANTICSID = "semanticsid";
	public static final String SENSEKEY = "sensekey";
	public static final String SENSENUM = "sensenum";
	public static final String SYNSETID = "synsetid";
	public static final String SYNTAX = "syntax";
	public static final String SYNTAXID = "syntaxid";
	public static final String VNWORDID = "vnwordid";
	public static final String WORD = "word";
	public static final String WORDID = "wordid";
	public static final String XTAG = "xtag";

	static public class SENSES
	{
		static public final String TABLE = "synsets";
	}

	static public class SYNSETS
	{
		static public final String TABLE = "synsets";
	}

	static public class VNCLASS
	{
		static public final String TABLE = "vn_classes";
		static public final String SELECTION = "classid = #{uri_last}";
	}

	static public class VNCLASSES
	{
		static public final String TABLE = "vn_classes";
	}

	static public class VNCLASSES_VNFRAMES_X_BY_VNFRAME
	{
		static public final String TABLE = "vn_classes_frames INNER JOIN vn_frames USING (frameid) LEFT JOIN vn_framenames USING (framenameid) LEFT JOIN vn_framesubnames USING (framesubnameid) LEFT JOIN vn_syntaxes USING (syntaxid) LEFT JOIN vn_semantics USING (semanticsid) LEFT JOIN vn_frames_examples USING (frameid) LEFT JOIN vn_examples USING (exampleid)";
		static public final String GROUPBY = "frameid";
	}

	static public class VNCLASSES_VNMEMBERS_X_BY_WORD
	{
		static public final String TABLE = "vn_members_senses LEFT JOIN vn_words USING (vnwordid) LEFT JOIN vn_members_groupings USING (classid, vnwordid) LEFT JOIN vn_groupings USING (groupingid) LEFT JOIN synsets USING (synsetid)";
		static public final String GROUPBY = "vnwordid";
	}

	static public class VNCLASSES_VNROLES_X_BY_VNROLE
	{
		static public final String TABLE = "vn_classes INNER JOIN vn_roles USING (classid) INNER JOIN vn_roletypes USING (roletypeid) LEFT JOIN vn_restrs USING (restrsid)";
		static public final String GROUPBY = "roleid";
	}

	static public class VNCLASSES_X_BY_VNCLASS
	{
		static public final String TABLE = "vn_classes LEFT JOIN vn_members_groupings USING (classid) LEFT JOIN vn_groupings USING (groupingid)";
		static public final String GROUPBY = "classid";
	}

	static public class WORDS
	{
		static public final String TABLE = "vn_words";
	}

	static public class WORDS_VNCLASSES
	{
		static public final String TABLE = "words INNER JOIN vn_words USING (wordid) INNER JOIN vn_members_senses USING (vnwordid) LEFT JOIN vn_classes USING (classid)";
	}

	static public class LOOKUP_FTS_EXAMPLES
	{
		static public final String TABLE = "vn_examples_example_fts4";
	}

	static public class LOOKUP_FTS_EXAMPLES_X
	{
		static public final String TABLE = "vn_examples_example_fts4 LEFT JOIN vn_classes USING (classid)";
	}

	static public class LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE
	{
		static public final String TABLE = "vn_examples_example_fts4 LEFT JOIN vn_classes USING (classid)";
		static public final String GROUPBY = "exampleid";
	}

	static public class SUGGEST_WORDS
	{
		static public final String TABLE = "vn_words";
		static public final String[] PROJECTION = {"vnwordid AS _id", "word AS #{suggest_text_1}", "word AS #{suggest_query}"};
		static public final String SELECTION = "word LIKE ? || '%'";
		static public final String[] ARGS = {"#{uri_last}"};
	}

	static public class SUGGEST_FTS_WORDS
	{
		static public final String TABLE = "vn_words_word_fts4";
		static public final String[] PROJECTION = {"vnwordid AS _id", "word AS #{suggest_text_1}", "word AS #{suggest_query}"};
		static public final String SELECTION = "word MATCH ?";
		static public final String[] ARGS = {"#{uri_last}*"};
	}
}
