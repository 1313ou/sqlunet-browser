package org.sqlunet.framenet.provider;

import android.app.SearchManager;

/**
 * FrameNet provider contract
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FrameNetContract
{
	// A L I A S E S

	static public final String SRC = "s";
	static public final String DEST = "d";
	static public final String POS = "p";
	static public final String FRAME = "f";
	static public final String RELATED = "r";
	static public final String LU = "u";
	static public final String FE = "e";
	static public final String FETYPE = "t";
	static public final String SENTENCE = "s";
	static public final String ANNOSET = "a";

	@SuppressWarnings("unused")
	static public final class FnWords
	{
		static public final String TABLE = "fnwords";
		static public final String CONTENT_URI_TABLE = FnWords.TABLE;
		static public final String FNWORDID = "fnwordid";
		static public final String WORDID = "wordid";
		static public final String WORD = "word";
	}

	@SuppressWarnings("unused")
	static public final class LexUnits
	{
		static public final String TABLE = "fnlexunits";
		static public final String CONTENT_URI_TABLE = LexUnits.TABLE;
		static public final String LUID = "luid";
		static public final String CONTENTS = "luid";
		static public final String LEXUNIT = "lexunit";
		static public final String LUDEFINITION = "ludefinition";
		static public final String LUDICT = "ludict";
		static public final String FRAMEID = "frameid";
	}

	@SuppressWarnings("unused")
	static public final class LexUnits_X
	{
		static public final String TABLE_BY_LEXUNIT = "fnlexunits_x_by_lexunit";
		static public final String CONTENT_URI_TABLE = LexUnits_X.TABLE_BY_LEXUNIT;
		static public final String LUID = "luid";
		static public final String CONTENTS = "luid";
		static public final String LEXUNIT = "lexunit";
		static public final String LUDEFINITION = "ludefinition";
		static public final String LUDICT = "ludict";
		static public final String POSID = "posid";
		static public final String FRAMEID = "frameid";
		static public final String FRAME = "frame";
		static public final String FRAMEDEFINITION = "framedefinition";
		static public final String INCORPORATEDFETYPE = "fetype";
		static public final String INCORPORATEDFEDEFINITION = "fedefinition";
		static public final String INCORPORATEDFECORESET = "coreset";
	}

	static public final class LexUnits_or_Frames
	{
		static public final String TABLE = "fnlexunits_or_fnframe";
		static public final String CONTENT_URI_TABLE = LexUnits_or_Frames.TABLE;
		static public final String ID = "_id";
		static public final String FNID = "fnid";
		static public final String FNWORDID = "fnwordid";
		static public final String WORDID = "wordid";
		static public final String WORD = "word";
		static public final String NAME = "name";
		static public final String FRAMENAME = "framename";
		static public final String FRAMEID = "frameid";
		static public final String ISFRAME = "isframe";
	}

	@SuppressWarnings("unused")
	static public final class Frames
	{
		static public final String TABLE = "fnframes";
		static public final String CONTENT_URI_TABLE = Frames.TABLE;
		static public final String FRAMEID = "frameid";
		static public final String CONTENTS = "frameid";
	}

	@SuppressWarnings("unused")
	static public final class Frames_X
	{
		static public final String TABLE_BY_FRAME = "fnframes_x_by_frame";
		static public final String CONTENT_URI_TABLE = Frames_X.TABLE_BY_FRAME;
		static public final String FRAMEID = "frameid";
		static public final String CONTENTS = "frameid";
		static public final String FRAME = "frame";
		static public final String FRAMEDEFINITION = "framedefinition";
		static public final String SEMTYPE = "semtype";
		static public final String SEMTYPEABBREV = "semtypeabbrev";
		static public final String SEMTYPEDEFINITION = "semtypedefinition";
	}

	static public final class Frames_Related
	{
		static public final String TABLE = "fnframes_related";
		static public final String CONTENT_URI_TABLE = Frames_Related.TABLE;
		static public final String FRAMEID = "frameid";
		static public final String FRAME = "frame";
		static public final String FRAME2ID = "frame2id";
		static public final String RELATIONID = "relationid";
		static public final String RELATION = "relation";
		static public final String RELATIONTYPE = "type";
		static public final String RELATIONGLOSS = "gloss";
	}

	static public final class Sentences
	{
		static public final String TABLE = "fnsentences";
		static public final String CONTENT_URI_TABLE = Sentences.TABLE;
		static public final String SENTENCEID = "sentenceid";
		static public final String TEXT = "text";
	}

	@SuppressWarnings("unused")
	static public final class AnnoSets
	{
		static public final String TABLE = "fnannosets";
		static public final String CONTENT_URI_TABLE = AnnoSets.TABLE;
		static public final String ANNOSETID = "annosetid";
		static public final String CONTENTS = "annosetid";
	}

	@SuppressWarnings("unused")
	static public final class Sentences_Layers_X
	{
		static public final String TABLE = "fnsentences_fnlayers_x";
		static public final String CONTENT_URI_TABLE = Sentences_Layers_X.TABLE;
		static public final String SENTENCEID = "sentenceid";
		static public final String ANNOSETID = "annosetid";
		static public final String LAYERID = "layerid";
		static public final String LAYERTYPE = "layertype";
		static public final String LAYERANNOTATIONS = "annotations";
		static public final String RANK = "rank";
		static public final String START = "start";
		static public final String END = "end";
		static public final String LABELTYPE = "labeltype";
		static public final String LABELITYPE = "labelitype";
		static public final String BGCOLOR = "bgcolor";
		static public final String FGCOLOR = "fgcolor";
	}

	@SuppressWarnings("unused")
	static public final class AnnoSets_Layers_X
	{
		static public final String TABLE = "fnannosets_fnlayers_x";
		static public final String CONTENT_URI_TABLE = AnnoSets_Layers_X.TABLE;
		static public final String ANNOSETID = "annosetid";
		static public final String SENTENCEID = "sentenceid";
		static public final String SENTENCETEXT = "text";
		static public final String LAYERID = "layerid";
		static public final String LAYERTYPE = "layertype";
		static public final String LAYERANNOTATIONS = "annotations";
		static public final String RANK = "rank";
		static public final String START = "start";
		static public final String END = "end";
		static public final String LABELTYPE = "labeltype";
		static public final String LABELITYPE = "labelitype";
		static public final String BGCOLOR = "bgcolor";
		static public final String FGCOLOR = "fgcolor";
	}

	@SuppressWarnings("unused")
	static public final class Patterns_Layers_X
	{
		static public final String TABLE = "fnpatterns_fnlayers_x";
		static public final String CONTENT_URI_TABLE = Patterns_Layers_X.TABLE;
		static public final String ANNOSETID = "annosetid";
		static public final String SENTENCEID = "sentenceid";
		static public final String SENTENCETEXT = "text";
		static public final String LAYERID = "layerid";
		static public final String LAYERTYPE = "layertype";
		static public final String LAYERANNOTATIONS = "annotations";
		static public final String RANK = "rank";
		static public final String START = "start";
		static public final String END = "end";
		static public final String LABELTYPE = "labeltype";
		static public final String LABELITYPE = "labelitype";
		static public final String BGCOLOR = "bgcolor";
		static public final String FGCOLOR = "fgcolor";
	}

	@SuppressWarnings("unused")
	static public final class ValenceUnits_Layers_X
	{
		static public final String TABLE = "fnvalenceunits_fnlayers_x";
		static public final String CONTENT_URI_TABLE = ValenceUnits_Layers_X.TABLE;
		static public final String ANNOSETID = "annosetid";
		static public final String SENTENCEID = "sentenceid";
		static public final String SENTENCETEXT = "text";
		static public final String LAYERID = "layerid";
		static public final String LAYERTYPE = "layertype";
		static public final String LAYERANNOTATIONS = "annotations";
		static public final String RANK = "rank";
		static public final String START = "start";
		static public final String END = "end";
		static public final String LABELTYPE = "labeltype";
		static public final String LABELITYPE = "labelitype";
		static public final String BGCOLOR = "bgcolor";
		static public final String FGCOLOR = "fgcolor";
	}

	@SuppressWarnings("unused")
	static public final class Words_LexUnits_Frames
	{
		static public final String TABLE = "words_fnlexunits";
		static public final String CONTENT_URI_TABLE = Words_LexUnits_Frames.TABLE;
		static public final String WORDID = "wordid";
		static public final String LUID = "luid";
		static public final String LEXUNIT = "lexunit";
		static public final String LUDEFINITION = "ludefinition";
		static public final String LUDICT = "ludict";
		static public final String POSID = "posid";
		static public final String FRAMEID = "frameid";
		static public final String FRAME = "frame";
		static public final String FRAMEDEFINITION = "framedefinition";
		static public final String SEMTYPE = "semtype";
		static public final String SEMTYPEABBREV = "semtypeabbrev";
		static public final String SEMTYPEDEFINITION = "semtypedefinition";
		static public final String INCORPORATEDFETYPE = "fetype";
		static public final String INCORPORATEDFEDEFINITION = "fedefinition";
	}

	@SuppressWarnings("unused")
	static public final class Frames_FEs
	{
		static public final String TABLE = "fnframes_fnfes";
		static public final String TABLE_BY_FE = TABLE + "/fe";
		static public final String CONTENT_URI_TABLE = Frames_FEs.TABLE;
		static public final String CONTENT_URI_TABLE_BY_FE = Frames_FEs.TABLE_BY_FE;
		static public final String FRAMEID = "frameid";
		static public final String FETYPEID = "fetypeid";
		static public final String FETYPE = "fetype";
		static public final String FEABBREV = "feabbrev";
		static public final String FEDEFINITION = "fedefinition";
		static public final String SEMTYPE = "semtype";
		static public final String SEMTYPES = "semtypes";
		static public final String CORESET = "coreset";
		static public final String CORETYPE = "coretype";
		static public final String CORETYPEID = "coretypeid";
	}

	@SuppressWarnings("unused")
	static public final class LexUnits_Sentences
	{
		static public final String TABLE = "fnframes_fnsentences";
		static public final String TABLE_BY_SENTENCE = TABLE + "/sentence";
		static public final String CONTENT_URI_TABLE = LexUnits_Sentences.TABLE;
		static public final String CONTENT_URI_TABLE_BY_SENTENCE = LexUnits_Sentences.TABLE_BY_SENTENCE;
		static public final String LUID = "luid";
		static public final String FRAMEID = "frameid";
		static public final String SENTENCEID = "sentenceid";
		static public final String TEXT = "text";
		static public final String CORPUSID = "corpusid";
		static public final String DOCUMENTID = "documentid";
		static public final String PARAGNO = "paragno";
		static public final String SENTNO = "sentno";
	}

	@SuppressWarnings("unused")
	static public final class LexUnits_Sentences_AnnoSets_Layers_Labels
	{
		static public final String TABLE = "fnlexunits_fnsentences_fnannosets_fnlayers_fnlabels";
		static public final String TABLE_BY_SENTENCE = TABLE + "/sentence";
		static public final String CONTENT_URI_TABLE = LexUnits_Sentences_AnnoSets_Layers_Labels.TABLE;
		static public final String CONTENT_URI_TABLE_BY_SENTENCE = LexUnits_Sentences_AnnoSets_Layers_Labels.TABLE_BY_SENTENCE;
		static public final String LUID = "luid";
		static public final String FRAMEID = "frameid";
		static public final String SENTENCEID = "sentenceid";
		static public final String TEXT = "text";
		static public final String ANNOSETID = "annosetid";
		static public final String LAYERID = "layerid";
		static public final String LAYERTYPE = "layertype";
		static public final String LAYERANNOTATION = "annotations";
		static public final String RANK = "rank";
		static public final String CORPUSID = "corpusid";
		static public final String DOCUMENTID = "documentid";
		static public final String PARAGNO = "paragno";
		static public final String SENTNO = "sentno";
		static public final String START = "start";
		static public final String END = "end";
		static public final String LABELTYPE = "labeltype";
		static public final String LABELITYPE = "labelitype";
		static public final String BGCOLOR = "bgcolor";
		static public final String FGCOLOR = "fgcolor";
	}

	static public final class LexUnits_Governors
	{
		static public final String TABLE = "fnlexunits_fngovernors";
		static public final String CONTENT_URI_TABLE = LexUnits_Governors.TABLE;
		static public final String LUID = "luid";
		static public final String GOVERNORID = "governorid";
		static public final String GOVERNORTYPE = "governortype";
		static public final String FNWORDID = "fnwordid";
		static public final String FNWORD = "word";
	}

	@SuppressWarnings("unused")
	static public final class LexUnits_FERealizations_ValenceUnits
	{
		static public final String TABLE = "fnlexunits_fnferealizations_fnvalenceunits";
		static public final String TABLE_BY_REALIZATION = TABLE + "/realization";
		static public final String CONTENT_URI_TABLE = LexUnits_FERealizations_ValenceUnits.TABLE;
		static public final String CONTENT_URI_TABLE_BY_REALIZATION = LexUnits_FERealizations_ValenceUnits.TABLE_BY_REALIZATION;
		static public final String LUID = "luid";
		static public final String FERID = "ferid";
		static public final String FETYPE = "fetype";
		static public final String PT = "pt";
		static public final String GF = "gf";
		static public final String TOTAL = "total";
		static public final String VUID = "vuid";
		static public final String FERS = "fers";
	}

	@SuppressWarnings("unused")
	static public final class LexUnits_FEGroupRealizations_Patterns_ValenceUnits
	{
		static public final String TABLE = "fnlexunits_fnferealizations_fnpatterns_fnvalenceunits";
		static public final String TABLE_BY_PATTERN = TABLE + "/pattern";
		static public final String CONTENT_URI_TABLE = LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE;
		static public final String CONTENT_URI_TABLE_BY_PATTERN = LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE_BY_PATTERN;
		static public final String LUID = "luid";
		static public final String FEGRID = "fegrid";
		static public final String FETYPE = "fetype";
		static public final String GROUPREALIZATION = "grouprealization";
		static public final String GROUPREALIZATIONS = "grouprealizations";
		static public final String GF = "gf";
		static public final String PT = "pt";
		static public final String TOTAL = "total";
		static public final String TOTALS = "totals";
		static public final String PATTERNID = "patternid";
		static public final String VUID = "patternid";
	}

	static public final class Patterns_Sentences
	{
		static public final String TABLE = "fnpatterns_annosets";
		static public final String CONTENT_URI_TABLE = Patterns_Sentences.TABLE;
		static public final String PATTERNID = "patternid";
		static public final String ANNOSETID = "annosetid";
		static public final String SENTENCEID = "sentenceid";
		static public final String TEXT = "text";
	}

	static public final class ValenceUnits_Sentences
	{
		static public final String TABLE = "fnvalenceunits_annosets";
		static public final String CONTENT_URI_TABLE = ValenceUnits_Sentences.TABLE;
		static public final String VUID = "vuid";
		static public final String ANNOSETID = "annosetid";
		static public final String SENTENCEID = "sentenceid";
		static public final String TEXT = "text";
	}

	static public final class Governors_AnnoSets_Sentences
	{
		static public final String TABLE = "fngovernors_annosets_sentences";
		static public final String CONTENT_URI_TABLE = Governors_AnnoSets_Sentences.TABLE;
		static public final String GOVERNORID = "governorid";
		static public final String ANNOSETID = "annosetid";
		static public final String SENTENCEID = "sentenceid";
		static public final String TEXT = "text";
	}

	static public final class Suggest_FnWords
	{
		static final String SEARCH_WORD_PATH = "suggest_fnword";
		static public final String TABLE = Suggest_FnWords.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
		static public final String FNWORDID = "fnwordid";
		static public final String WORDID = "wordid";
		static public final String WORD = "word";
	}

	static public final class Suggest_FTS_FnWords
	{
		static final String SEARCH_WORD_PATH = "suggest_fts_fnword";
		static public final String TABLE = Suggest_FTS_FnWords.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
		static public final String FNWORDID = "fnwordid";
		static public final String WORDID = "wordid";
		static public final String WORD = "word";
	}

	static public final class Lookup_FnWords
	{
		static public final String TABLE = "fts_fnwords";
		static public final String CONTENT_URI_TABLE = Lookup_FnWords.TABLE;
		static public final String FNWORDID = "fnwordid";
		static public final String WORDID = "wordid";
		static public final String WORD = "word";
	}

	@SuppressWarnings("unused")
	static public final class Lookup_FnSentences
	{
		static public final String TABLE = "fts_fnsentences";
		static public final String CONTENT_URI_TABLE = Lookup_FnSentences.TABLE;
		static public final String SENTENCEID = "sentenceid";
		static public final String TEXT = "text";
		static public final String FRAMEID = "frameid";
		static public final String LUID = "luid";
		static public final String ANNOSETID = "annosetid";
	}

	@SuppressWarnings("unused")
	static public final class Lookup_FnSentences_X
	{
		static public final String TABLE = "fts_fnsentences_x";
		static public final String TABLE_BY_SENTENCE = "fts_fnsentences_x_by_sentence";
		static public final String CONTENT_URI_TABLE = Lookup_FnSentences_X.TABLE_BY_SENTENCE;
		static public final String SENTENCEID = "sentenceid";
		static public final String TEXT = "text";
		static public final String FRAMEID = "frameid";
		static public final String FRAME = "frame";
		static public final String FRAMES = "frames";
		static public final String LUID = "luid";
		static public final String LEXUNIT = "lexunit";
		static public final String LEXUNITS = "lexunits";
		static public final String ANNOSETID = "annosetid";
		static public final String ANNOSETS = "annosets";
	}
}
