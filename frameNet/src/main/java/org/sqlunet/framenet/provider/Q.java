package org.sqlunet.framenet.provider;

public class Q
{
	// VARIABLES
	public static final String _ID = "_id";
	public static final String ANNOSETID = "annosetid";
	public static final String ANNOTATIONS = "annotations";
	public static final String APOS = "apos";
	public static final String AS_ANNOSETS = "an";
	public static final String AS_ARGS = "ar";
	public static final String AS_CASEDS = "c";
	public static final String AS_DOMAINS = "d";
	public static final String AS_EXAMPLES = "e";
	public static final String AS_FES = "fe";
	public static final String AS_FETYPES = "ft";
	public static final String AS_FRAMES = "fr";
	public static final String AS_FUNCS = "fu";
	public static final String AS_LEXUNITS = "lu";
	public static final String AS_MEMBERS = "m";
	public static final String AS_POSES = "p";
	public static final String AS_RELATED_FRAMES = "rf";
	public static final String AS_RELATIONS = "r";
	public static final String AS_SENSES = "s";
	public static final String AS_SENTENCES = "st";
	public static final String AS_SYNSETS = "y";
	public static final String AS_SYNSETS2 = "y2";
	public static final String AS_TYPES = "t";
	public static final String AS_WORDS = "w";
	public static final String AS_WORDS2 = "w2";
	public static final String BGCOLOR = "bgcolor";
	public static final String BREAKBEFORE = "breakbefore";
	public static final String CBY = "cby";
	public static final String CDATE = "cdate";
	public static final String CORESET = "coreset";
	public static final String CORETYPE = "coretype";
	public static final String CORETYPEID = "coretypeid";
	public static final String CORPUS = "corpus";
	public static final String CORPUSDESC = "corpusdesc";
	public static final String CORPUSID = "corpusid";
	public static final String CXN = "cxn";
	public static final String CXNID = "cxnid";
	public static final String DEST_FRAME = "df";
	public static final String DOCUMENT = "document";
	public static final String DOCUMENTDESC = "documentdesc";
	public static final String DOCUMENTID = "documentid";
	public static final String END = "end";
	public static final String FE2ID = "fe2id";
	public static final String FEABBREV = "feabbrev";
	public static final String FEDEFINITION = "fedefinition";
	public static final String FEGRID = "fegrid";
	public static final String FEID = "feid";
	public static final String FERID = "ferid";
	public static final String FETYPE = "fetype";
	public static final String FETYPEID = "fetypeid";
	public static final String FGCOLOR = "fgcolor";
	public static final String FNID = "fnid";
	public static final String FNWORDID = "fnwordid";
	public static final String FRAME = "frame";
	public static final String FRAME2 = "frame2";
	public static final String FRAME2ID = "frame2id";
	public static final String FRAMEDEFINITION = "framedefinition";
	public static final String FRAMEID = "frameid";
	public static final String GF = "gf";
	public static final String GFID = "gfid";
	public static final String GOVERNORID = "governorid";
	public static final String GOVERNORTYPE = "governortype";
	public static final String HEADWORD = "headword";
	public static final String INCORPORATEDFEID = "incorporatedfeid";
	public static final String INCORPORATEDFETYPEID = "incorporatedfetypeid";
	public static final String ISFRAME = "isframe";
	public static final String LABELID = "labelid";
	public static final String LABELITYPE = "labelitype";
	public static final String LABELITYPEDESCR = "labelitypedescr";
	public static final String LABELITYPEID = "labelitypeid";
	public static final String LABELTYPE = "labeltype";
	public static final String LABELTYPEID = "labeltypeid";
	public static final String LAYERID = "layerid";
	public static final String LAYERTYPE = "layertype";
	public static final String LAYERTYPEID = "layertypeid";
	public static final String LEXEMEID = "lexemeid";
	public static final String LEXEMEIDX = "lexemeidx";
	public static final String LEXUNIT = "lexunit";
	public static final String LUDEFINITION = "ludefinition";
	public static final String LUDICT = "ludict";
	public static final String LUID = "luid";
	public static final String MEMBERS = "members";
	public static final String MEMBERS2 = "members2";
	public static final String NAME = "name";
	public static final String NOCCURS = "noccurs";
	public static final String PARAGNO = "paragno";
	public static final String PATTERNID = "patternid";
	public static final String POS = "pos";
	public static final String POSID = "posid";
	public static final String PT = "pt";
	public static final String PTID = "ptid";
	public static final String RANK = "rank";
	public static final String RELATION = "relation";
	public static final String RELATIONID = "relationid";
	public static final String RESOLVE = "fes.resolve";
	public static final String SAMPLESET = "sampleset";
	public static final String SEMTYPE = "semtype";
	public static final String SEMTYPEABBREV = "semtypeabbrev";
	public static final String SEMTYPEDEFINITION = "semtypedefinition";
	public static final String SEMTYPEID = "semtypeid";
	public static final String SENTENCEID = "sentenceid";
	public static final String SENTNO = "sentno";
	public static final String SRC_FRAME = "sf";
	public static final String START = "start";
	public static final String STATUS = "status";
	public static final String STATUSID = "statusid";
	public static final String SUBCORPUS = "subcorpus";
	public static final String SUBCORPUSID = "subcorpusid";
	public static final String SUPERSEMTYPEID = "supersemtypeid";
	public static final String TEXT = "text";
	public static final String TOTAL = "total";
	public static final String TOTALANNOTATED = "totalannotated";
	public static final String VUID = "vuid";
	public static final String WORD = "word";
	public static final String WORD2 = "word2";
	public static final String WORDID = "wordid";

	public static final String TYPE = "type";
	public static final String GLOSS = "gloss";
	public static final String A_FRAMES = "frames";
	public static final String A_LEXUNITS = "lexunits";
	public static final String SEMTYPES = "semtypes";
	public static final String A_ANNOSETS = "annosets";
	public static final String FERS = "fers";
	public static final String GROUPREALIZATION = "grouprealization";
	public static final String A_GROUPREALIZATIONS = "grouprealizations";
	public static final String TOTALS = "totals";

	static public class WORDS
	{
		static public final String TABLE = "fn_words";
	}

	static public class LEXUNITS
	{
		static public final String TABLE = "fn_lexunits";
	}

	static public class FRAMES
	{
		static public final String TABLE = "fn_frames";
	}

	static public class ANNOSETS
	{
		static public final String TABLE = "fn_annosets";
	}

	static public class SENTENCES
	{
		static public final String TABLE = "fn_sentences";
	}

	static public class LEXUNIT
	{
		static public final String TABLE = "fn_lexunits";
	}

	static public class FRAME
	{
		static public final String TABLE = "fn_frames";
	}

	static public class SENTENCE
	{
		static public final String TABLE = "fn_sentences";
	}

	static public class ANNOSET
	{
		static public final String TABLE = "fn_annosets";
	}

	static public class LEXUNITS_OR_FRAMES
	{
		static public final String TABLE = "(SELECT fnwordid + 10000 AS _id, luid AS fnid, fnwordid AS fnwordid, wordid AS wordid, word AS word, lexunit AS name, frame AS frame, frameid AS frameid, 0 AS isframe FROM fn_words INNER JOIN fn_lexemes USING (fnwordid) INNER JOIN fn_lexunits AS lu USING (luid) INNER JOIN fn_frames AS fr USING (frameid) UNION SELECT frameid AS _id, frameid AS fnid, 0 AS fnwordid, 0 AS wordid, frame AS word, frame AS name, frame AS frame, frameid AS frameid, 1 AS isframe FROM fn_frames )";
	}

	static public class FRAMES_X_BY_FRAME
	{
		static public final String TABLE = "fn_frames LEFT JOIN fn_frames_semtypes USING (frameid) LEFT JOIN fn_semtypes USING (semtypeid)";
		static public final String GROUPBY = "frameid";
	}

	static public class FRAMES_RELATED
	{
		static public final String TABLE = "fn_frames_related AS rf LEFT JOIN fn_frames AS sf USING (frameid) LEFT JOIN fn_frames AS df ON (frame2id = df.frameid) LEFT JOIN fn_framerelations USING (relationid)";
	}

	static public class LEXUNITS_X_BY_LEXUNIT
	{
		static public final String TABLE = "fn_lexunits AS lu " +
				"LEFT JOIN fn_frames AS fr USING (frameid) " +
				"LEFT JOIN fn_poses AS p ON (lu.posid = p.posid) " +
				"LEFT JOIN fn_fetypes AS ft ON (incorporatedfetypeid = ft.fetypeid) " +
				//"LEFT JOIN fn_fes AS fe ON (incorporatedfeid = fe.feid)";
				"LEFT JOIN fn_fes AS fe ON (fr.frameid = fe.frameid AND incorporatedfetypeid = fe.fetypeid)";
		static public final String GROUPBY = "luid";
	}

	static public class SENTENCES_LAYERS_X
	{
		static public final String TABLE = "(SELECT annosetid,sentenceid,layerid,layertype,rank,GROUP_CONCAT(start||':'||end||':'||labeltype||':'||CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END||':'||CASE WHEN bgcolor IS NULL THEN '' ELSE bgcolor END||':'||CASE WHEN fgcolor IS NULL THEN '' ELSE fgcolor END,'|') AS annotations FROM fn_sentences LEFT JOIN fn_annosets USING (sentenceid) LEFT JOIN fn_layers USING (annosetid) LEFT JOIN fn_layertypes USING (layertypeid) LEFT JOIN fn_labels USING (layerid) LEFT JOIN fn_labeltypes USING (labeltypeid) LEFT JOIN fn_labelitypes USING (labelitypeid) WHERE sentenceid = ? AND labeltypeid IS NOT NULL GROUP BY layerid ORDER BY rank,layerid,start,end)";
	}

	static public class ANNOSETS_LAYERS_X
	{
		static public final String TABLE = "(SELECT sentenceid,text,layerid,layertype,rank,GROUP_CONCAT(start||':'||end||':'||labeltype||':'||CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END||':'||CASE WHEN bgcolor IS NULL THEN '' ELSE bgcolor END||':'||CASE WHEN fgcolor IS NULL THEN '' ELSE fgcolor END,'|') AS annotations FROM fn_annosets LEFT JOIN fn_sentences USING (sentenceid) LEFT JOIN fn_layers USING (annosetid) LEFT JOIN fn_layertypes USING (layertypeid) LEFT JOIN fn_labels USING (layerid) LEFT JOIN fn_labeltypes USING (labeltypeid) LEFT JOIN fn_labelitypes USING (labelitypeid) WHERE annosetid = ? AND labeltypeid IS NOT NULL GROUP BY layerid ORDER BY rank,layerid,start,end)";
	}

	static public class PATTERNS_LAYERS_X
	{
		static public final String TABLE = "(SELECT annosetid,sentenceid,text,layerid,layertype,rank,GROUP_CONCAT(start||':'||end||':'||labeltype||':'||CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END||':'||CASE WHEN bgcolor IS NULL THEN '' ELSE bgcolor END||':'||CASE WHEN fgcolor IS NULL THEN '' ELSE fgcolor END,'|') AS annotations FROM fn_grouppatterns_annosets LEFT JOIN fn_annosets USING (annosetid) LEFT JOIN fn_sentences USING (sentenceid) LEFT JOIN fn_layers USING (annosetid) LEFT JOIN fn_layertypes USING (layertypeid) LEFT JOIN fn_labels USING (layerid) LEFT JOIN fn_labeltypes USING (labeltypeid) LEFT JOIN fn_labelitypes USING (labelitypeid) WHERE patternid = ? AND labeltypeid IS NOT NULL GROUP BY layerid ORDER BY rank,layerid,start,end)";
	}

	static public class VALENCEUNITS_LAYERS_X
	{
		static public final String TABLE = "(SELECT annosetid,sentenceid,text,layerid,layertype,rank,GROUP_CONCAT(start||':'||end||':'||labeltype||':'||CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END||':'||CASE WHEN bgcolor IS NULL THEN '' ELSE bgcolor END||':'||CASE WHEN fgcolor IS NULL THEN '' ELSE fgcolor END,'|') AS annotations FROM fn_valenceunits_annosets LEFT JOIN fn_annosets USING (annosetid) LEFT JOIN fn_sentences USING (sentenceid) LEFT JOIN fn_layers USING (annosetid) LEFT JOIN fn_layertypes USING (layertypeid) LEFT JOIN fn_labels USING (layerid) LEFT JOIN fn_labeltypes USING (labeltypeid) LEFT JOIN fn_labelitypes USING (labelitypeid) WHERE vuid = ? AND labeltypeid IS NOT NULL GROUP BY layerid ORDER BY rank,layerid,start,end)";
	}

	static public class WORDS_LEXUNITS_FRAMES
	{
		static public final String TABLE = "words INNER JOIN fn_words USING (wordid) INNER JOIN fn_lexemes USING (fnwordid) INNER JOIN fn_lexunits AS lu USING (luid) LEFT JOIN fn_frames USING (frameid) LEFT JOIN fn_poses AS p ON (lu.posid = p.posid) LEFT JOIN fn_fes AS fe ON (incorporatedfeid = feid) LEFT JOIN fn_fetypes AS ft ON (incorporatedfetypeid = fe.fetypeid)";
	}

	static public class FRAMES_FES_BY_FE
	{
		static public final String TABLE = "fn_frames INNER JOIN fn_fes USING (frameid) LEFT JOIN fn_fetypes USING (fetypeid) LEFT JOIN fn_coretypes USING (coretypeid) LEFT JOIN fn_fes_semtypes USING (feid) LEFT JOIN fn_semtypes USING (semtypeid)";
		static public final String GROUPBY = "feid";
	}

	static public class FRAMES_FES
	{
		static public final String TABLE = "fn_frames INNER JOIN fn_fes USING (frameid) LEFT JOIN fn_fetypes USING (fetypeid) LEFT JOIN fn_coretypes USING (coretypeid) LEFT JOIN fn_fes_semtypes USING (feid) LEFT JOIN fn_semtypes USING (semtypeid)";
	}

	static public class LEXUNITS_SENTENCES_BY_SENTENCE
	{
		static public final String TABLE = "fn_lexunits AS lu LEFT JOIN fn_subcorpuses USING (luid) LEFT JOIN fn_subcorpuses_sentences USING (subcorpusid) INNER JOIN fn_sentences AS st USING (sentenceid)";
		static public final String GROUPBY = "st.sentenceid";
	}

	static public class LEXUNITS_SENTENCES
	{
		static public final String TABLE = "fn_lexunits AS lu LEFT JOIN fn_subcorpuses USING (luid) LEFT JOIN fn_subcorpuses_sentences USING (subcorpusid) INNER JOIN fn_sentences AS st USING (sentenceid)";
	}

	static public class LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE
	{
		static public final String TABLE = "fn_lexunits AS lu LEFT JOIN fn_subcorpuses USING (luid) LEFT JOIN fn_subcorpuses_sentences USING (subcorpusid) INNER JOIN fn_sentences AS st USING (sentenceid) LEFT JOIN fn_annosets USING (sentenceid) LEFT JOIN fn_layers USING (annosetid) LEFT JOIN fn_layertypes USING (layertypeid) LEFT JOIN fn_labels USING (layerid) LEFT JOIN fn_labeltypes USING (labeltypeid) LEFT JOIN fn_labelitypes USING (labelitypeid)";
		static public final String GROUPBY = "st.sentenceid";
	}

	static public class LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS
	{
		static public final String TABLE = "fn_lexunits AS lu LEFT JOIN fn_subcorpuses USING (luid) LEFT JOIN fn_subcorpuses_sentences USING (subcorpusid) INNER JOIN fn_sentences AS st USING (sentenceid) LEFT JOIN fn_annosets USING (sentenceid) LEFT JOIN fn_layers USING (annosetid) LEFT JOIN fn_layertypes USING (layertypeid) LEFT JOIN fn_labels USING (layerid) LEFT JOIN fn_labeltypes USING (labeltypeid) LEFT JOIN fn_labelitypes USING (labelitypeid)";
	}

	static public class LEXUNITS_GOVERNORS
	{
		static public final String TABLE = "fn_lexunits INNER JOIN fn_lexunits_governors USING (luid) INNER JOIN fn_governors USING (governorid) LEFT JOIN fn_words USING (fnwordid)";
	}

	static public class GOVERNORS_ANNOSETS
	{
		static public final String TABLE = "fn_governors_annosets LEFT JOIN fn_annosets USING (annosetid) LEFT JOIN fn_sentences USING (sentenceid)";
	}

	static public class LEXUNITS_REALIZATIONS_BY_REALIZATION
	{
		static public final String TABLE = "fn_lexunits " +
				"INNER JOIN fn_ferealizations USING (luid) " +
				"LEFT JOIN fn_ferealizations_valenceunits USING (ferid) " +
				"LEFT JOIN fn_valenceunits USING (vuid) " +
				"LEFT JOIN fn_fetypes USING (fetypeid) " +
				"LEFT JOIN fn_gftypes USING (gfid) " +
				"LEFT JOIN fn_pttypes USING (ptid)";
		static public final String GROUPBY = "ferid";
	}

	static public class LEXUNITS_REALIZATIONS
	{
		static public final String TABLE = "fn_lexunits INNER JOIN fn_ferealizations USING (luid) LEFT JOIN fn_valenceunits USING (ferid) LEFT JOIN fn_fetypes USING (fetypeid) LEFT JOIN fn_gftypes USING (gfid) LEFT JOIN fn_pttypes USING (ptid)";
	}

	static public class LEXUNITS_GROUPREALIZATIONS_BY_PATTERN
	{
		static public final String TABLE = "fn_lexunits INNER JOIN fn_fegrouprealizations USING (luid) LEFT JOIN fn_grouppatterns USING (fegrid) LEFT JOIN fn_grouppatterns_patterns USING (patternid) LEFT JOIN fn_valenceunits USING (vuid) LEFT JOIN fn_fetypes USING (fetypeid) LEFT JOIN fn_gftypes USING (gfid) LEFT JOIN fn_pttypes USING (ptid)";
		static public final String GROUPBY = "patternid";
	}

	static public class LEXUNITS_GROUPREALIZATIONS
	{
		static public final String TABLE = "fn_lexunits INNER JOIN fn_fegrouprealizations USING (luid) LEFT JOIN fn_grouppatterns USING (fegrid) LEFT JOIN fn_grouppatterns_patterns USING (patternid) LEFT JOIN fn_valenceunits USING (vuid) LEFT JOIN fn_fetypes USING (fetypeid) LEFT JOIN fn_gftypes USING (gfid) LEFT JOIN fn_pttypes USING (ptid)";
	}

	static public class PATTERNS_SENTENCES
	{
		static public final String TABLE = "fn_grouppatterns_annosets LEFT JOIN fn_annosets AS an USING (annosetid) LEFT JOIN fn_sentences AS st USING (sentenceid)";
	}

	static public class VALENCEUNITS_SENTENCES
	{
		static public final String TABLE = "fn_valenceunits_annosets LEFT JOIN fn_annosets AS an USING (annosetid) LEFT JOIN fn_sentences AS st USING (sentenceid)";
	}

	static public class LOOKUP_FTS_WORDS
	{
		static public final String TABLE = "fn_words_word_fts4";
	}

	static public class LOOKUP_FTS_SENTENCES
	{
		static public final String TABLE = "fn_sentences_text_fts4";
	}

	static public class LOOKUP_FTS_SENTENCES_X_BY_SENTENCE
	{
		static public final String TABLE = "fn_sentences_text_fts4 LEFT JOIN fn_frames USING (frameid) LEFT JOIN fn_lexunits USING (frameid,luid)";
		static public final String GROUPBY = "sentenceid";
	}

	static public class LOOKUP_FTS_SENTENCES_X
	{
		static public final String TABLE = "fn_sentences_text_fts4 LEFT JOIN fn_frames USING (frameid) LEFT JOIN fn_lexunits USING (frameid,luid)";
	}

	static public class SUGGEST_WORDS
	{
		static public final String TABLE = "fn_words";
		static public final String[] PROJECTION = {"fnwordid AS _id", "word AS #{suggest_text_1}", "word AS #{suggest_query}"};
		static public final String SELECTION = "word LIKE ? || '%'";
		static public final String[] ARGS = {"#{uri_last}"};
	}

	static public class SUGGEST_FTS_WORDS
	{
		static public final String TABLE = "fn_words_word_fts4";
		static public final String[] PROJECTION = {"fnwordid AS _id", "word AS #{suggest_text_1}", "word AS #{suggest_query}"};
		static public final String SELECTION = "word MATCH ?";
		static public final String[] ARGS = {"#{uri_last}*"};
	}
}
