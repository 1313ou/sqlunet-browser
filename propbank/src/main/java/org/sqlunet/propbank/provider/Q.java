package org.sqlunet.propbank.provider;

public class Q
{
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

	public static final String ARG = "arg";
	public static final String ARGID = "argid";
	public static final String AS_ARGS = "a";
	public static final String ASPECT = "aspect";
	public static final String ASPECTID = "aspectid";
	public static final String CLASSID = "classid";
	public static final String EXAMPLEID = "exampleid";
	public static final String EXAMPLENAME = "examplename";
	public static final String FNFRAME = "fnframe";
	public static final String FNFRAMEID = "fnframeid";
	public static final String FORM = "form";
	public static final String FORMID = "formid";
	public static final String FUNC = "func";
	public static final String FUNCDESCR = "funcdescr";
	public static final String FUNCID = "funcid";
	public static final String MEMBERS = "members";
	public static final String MEMBERS2 = "members2";
	public static final String NARGDESCR = "nargdescr";
	public static final String NARGID = "nargid";
	public static final String PBWORDID = "pbwordid";
	public static final String PERSON = "person";
	public static final String PERSONID = "personid";
	public static final String POS = "pos";
	public static final String REL = "rel";
	public static final String RELID = "relid";
	public static final String ROLEDESCR = "roledescr";
	public static final String ROLEID = "roleid";
	public static final String ROLESETDESCR = "rolesetdescr";
	public static final String ROLESETHEAD = "rolesethead";
	public static final String ROLESETID = "rolesetid";
	public static final String ROLESETNAME = "rolesetname";
	public static final String SAMPLESET = "sampleset";
	public static final String TENSE = "tense";
	public static final String TENSEID = "tenseid";
	public static final String TEXT = "text";
	public static final String THETA = "theta";
	public static final String THETAID = "thetaid";
	public static final String VNCLASS = "vnclass";
	public static final String VNCLASSID = "vnclassid";
	public static final String VNROLEID = "vnroleid";
	public static final String VNROLETYPEID = "vnroletypeid";
	public static final String VNTHETA = "vntheta";
	public static final String VOICE = "voice";
	public static final String VOICEID = "voiceid";
	public static final String WORD = "word";
	public static final String WORD2 = "word2";
	public static final String WORDID = "wordid";

	static public class PBROLESET
	{
		static public final String TABLE = "pb_rolesets";
		static public final String SELECTION = "rolesetid = ?";
	}

	static public class PBROLESETS
	{
		static public final String TABLE = "pb_rolesets";
	}

	static public class PBROLESETS_PBEXAMPLES
	{
		static public final String TABLE = "pb_rolesets INNER JOIN pb_examples AS e USING (rolesetid) LEFT JOIN pb_rels AS r USING (exampleid) LEFT JOIN pb_args AS a USING (exampleid) LEFT JOIN pb_funcs AS f ON (a.func = f.func) LEFT JOIN pb_aspects USING (aspect) LEFT JOIN pb_forms USING (form) LEFT JOIN pb_tenses USING (tense) LEFT JOIN pb_voices USING (voice) LEFT JOIN pb_persons USING (person) LEFT JOIN pb_roles USING (rolesetid,arg) LEFT JOIN pb_thetas USING (theta)";
	}

	static public class PBROLESETS_PBEXAMPLES_BY_EXAMPLE
	{
		static public final String TABLE = "pb_rolesets INNER JOIN pb_examples AS e USING (rolesetid) LEFT JOIN pb_rels AS r USING (exampleid) LEFT JOIN pb_args AS a USING (exampleid) LEFT JOIN pb_funcs AS f ON (a.func = f.func) LEFT JOIN pb_aspects USING (aspect) LEFT JOIN pb_forms USING (form) LEFT JOIN pb_tenses USING (tense) LEFT JOIN pb_voices USING (voice) LEFT JOIN pb_persons USING (person) LEFT JOIN pb_roles USING (rolesetid,arg) LEFT JOIN pb_thetas USING (theta)";
		static public final String GROUPBY = "e.exampleid";
	}

	static public class PBROLESETS_PBROLES
	{
		static public final String TABLE = "pb_rolesets INNER JOIN pb_roles USING (rolesetid) LEFT JOIN pb_funcs USING (func) LEFT JOIN pb_thetas USING (theta)";
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

	static public class WORDS_PBROLESETS
	{
		static public final String TABLE = "words INNER JOIN pb_words USING (wordid) INNER JOIN pb_rolesets USING (pbwordid)";
	}

	static public class SUGGEST_FTS_WORDS
	{
		static public final String TABLE = "pb_words_word_fts4";
		static public final String[] PROJECTION = {"pbwordid AS _id", "word AS SearchManager.SUGGEST_COLUMN_COLUMN_TEXT_1", "word AS SearchManager.SUGGEST_COLUMN_COLUMN_QUERY"};
		static public final String SELECTION = "word MATCH ?";
		static public final String[] ARGS = {"#{uri_last}*"};
	}

	static public class SUGGEST_WORDS
	{
		static public final String TABLE = "pb_words";
		static public final String[] PROJECTION = {"pbwordid AS _id", "word AS SearchManager.SUGGEST_COLUMN_TEXT_1", "word AS SearchManager.SUGGEST_COLUMN_COLUMN_QUERY"};
		static public final String SELECTION = "word LIKE ? || '%'";
		static public final String[] ARGS = {"#{uri_last}"};
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

}
