package org.sqlunet.verbnet.provider;

public class Q0
{

	static public class WORDS
	{
		static public final String TABLE = "vn_words";
	}

	static public class SENSES
	{
		static public final String TABLE = "synsets";
	}

	static public class SYNSETS
	{
		static public final String TABLE = "synsets";
	}

	static public class VNCLASSES
	{
		static public final String TABLE = "vn_classes";
	}

	static public class VNCLASS1
	{
		static public final String TABLE = "vn_classes";
		static public final String SELECTION = "classid = #{uri_last}";
	}

	static public class WORDS_VNCLASSES
	{
		static public final String TABLE = "words INNER JOIN vn_words USING (wordid) INNER JOIN vn_members_senses USING (vnwordid) LEFT JOIN vn_classes USING (classid)";
	}

	static public class VNCLASSES_X_BY_VNCLASS
	{
		static public final String TABLE = "vn_classes LEFT JOIN vn_members_groupings USING (classid) LEFT JOIN vn_groupings USING (groupingid)";
		static public final String GROUPBY = "classid";
	}

	static public class VNCLASSES_VNMEMBERS_X_BY_WORD
	{
		static public final String TABLE = "words INNER JOIN vn_members_senses USING (wordid) LEFT JOIN vn_members_groupings USING (classid, vnwordid) LEFT JOIN vn_groupings USING (groupingid) LEFT JOIN synsets USING (synsetid)";
		static public final String GROUPBY = "vnwordid";
	}

	static public class VNCLASSES_VNROLES_X_BY_VNROLE
	{
		static public final String TABLE = "vn_classes INNER JOIN vn_roles USING (classid) INNER JOIN vn_roletypes USING (roletypeid) LEFT JOIN vn_restrs USING (restrsid)";
		static public final String GROUPBY = "roleid";
	}

	static public class VNCLASSES_VNFRAMES_X_BY_VNFRAME
	{
		static public final String TABLE = "vn_classes_frames INNER JOIN vn_frames USING (frameid) LEFT JOIN vn_framenames USING (framenameid) LEFT JOIN vn_framesubnames USING (framesubnameid) LEFT JOIN vn_syntaxes USING (syntaxid) LEFT JOIN vn_semantics USING (semanticsid) LEFT JOIN vn_frames_examples USING (frameid) LEFT JOIN vn_examples USING (exampleid)";
		static public final String GROUPBY = "frameid";
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
		static public final String TABLE = "vn_words INNER JOIN words USING (wordid)";
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
