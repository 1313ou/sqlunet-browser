package org.sqlunet.propbank.provider;

public class Q0
{

	static public class PBWORDS
	{
		static public final String TABLE = "pb_words";
	}

	static public class PBROLESETS
	{
		static public final String TABLE = "pb_rolesets";
	}

	static public class PBROLESETS_X
	{
		static public final String TABLE = "pb_rolesets LEFT JOIN pb_members AS m USING (rolesetid) LEFT JOIN pb_words AS w ON m.pbwordid = w.pbwordid";
	}

	static public class PBROLESETS_X_BY_ROLESET
	{
		static public final String TABLE = "pb_rolesets LEFT JOIN pb_members AS m USING (rolesetid) LEFT JOIN pb_words AS w ON m.pbwordid = w.pbwordid";
		static public final String GROUPBY = "rolesetid";
	}

	static public class PBROLESET1
	{
		static public final String TABLE = "pb_rolesets";
		static public final String SELECTION = "rolesetid = ?";
	}

	static public class PBROLESETS_PBROLES
	{
		static public final String TABLE = "pb_rolesets INNER JOIN pb_roles USING (rolesetid) LEFT JOIN pb_funcs USING (func) LEFT JOIN pb_thetas USING (theta)";
		static public final String ORDERBY = "nargid";
	}

	static public class PBROLESETS_PBEXAMPLES
	{
		static public final String TABLE = "pb_rolesets " +
				"INNER JOIN pb_examples AS e USING (rolesetid) " +
				"LEFT JOIN pb_rels AS r USING (exampleid) " +
				"LEFT JOIN pb_args AS ar USING (exampleid) " +
				"LEFT JOIN pb_funcs AS fu ON (ar.func = fu.func) " +
				"LEFT JOIN pb_aspects USING (aspect) " +
				"LEFT JOIN pb_forms USING (form) " +
				"LEFT JOIN pb_tenses USING (tense) " +
				"LEFT JOIN pb_voices USING (voice) " +
				"LEFT JOIN pb_persons USING (person) " +
				"LEFT JOIN pb_roles USING (rolesetid,arg) " +
				"LEFT JOIN pb_thetas USING (theta)";
		static public final String ORDERBY = "e.exampleid,arg";
	}

	static public class PBROLESETS_PBEXAMPLES_BY_EXAMPLE
	{
		static public final String TABLE = "pb_rolesets " +
				"INNER JOIN pb_examples AS e USING (rolesetid) " +
				"LEFT JOIN pb_rels AS r USING (exampleid) " +
				"LEFT JOIN pb_args AS ar USING (exampleid) " +
				"LEFT JOIN pb_funcs AS fu ON (ar.func = fu.func) " +
				"LEFT JOIN pb_aspects USING (aspect) " +
				"LEFT JOIN pb_forms USING (form) " +
				"LEFT JOIN pb_tenses USING (tense) " +
				"LEFT JOIN pb_voices USING (voice) " +
				"LEFT JOIN pb_persons USING (person) " +
				"LEFT JOIN pb_roles USING (rolesetid,arg) " +
				"LEFT JOIN pb_thetas USING (theta)";
		static public final String GROUPBY = "e.exampleid";
		static public final String ORDERBY = "e.exampleid,arg";
	}

	static public class WORDS_PBROLESETS
	{
		static public final String TABLE = "words INNER JOIN pb_words USING (wordid) INNER JOIN pb_rolesets USING (pbwordid)";
	}

	static public class LOOKUP_FTS_EXAMPLES
	{
		static public final String TABLE = "pb_examples_text_fts4";
	}

	static public class LOOKUP_FTS_EXAMPLES_X
	{
		static public final String TABLE = "pb_examples_text_fts4 LEFT JOIN pb_rolesets USING (rolesetid)";
	}

	static public class LOOKUP_FTS_EXAMPLES_X_BY_EXAMPLE
	{
		static public final String TABLE = "pb_examples_text_fts4 LEFT JOIN pb_rolesets USING (rolesetid)";
		static public final String GROUPBY = "exampleid";
	}

	static public class SUGGEST_WORDS
	{
		static public final String TABLE = "pb_words";
		static public final String[] PROJECTION = {"pbwordid AS _id", "word AS #{suggest_text_1}", "word AS #{suggest_query}"};
		static public final String SELECTION = "word LIKE ? || '%'";
		static public final String[] ARGS = {"#{uri_last}"};
	}

	static public class SUGGEST_FTS_WORDS
	{
		static public final String TABLE = "pb_words_word_fts4";
		static public final String[] PROJECTION = {"pbwordid AS _id", "word AS #{suggest_text_1}", "word AS #{suggest_query}"};
		static public final String SELECTION = "word MATCH ?";
		static public final String[] ARGS = {"#{uri_last}*"};
	}

}
