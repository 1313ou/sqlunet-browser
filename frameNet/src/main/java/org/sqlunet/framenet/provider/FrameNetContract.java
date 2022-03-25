/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

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

	static public final String SRC = "sf";
	static public final String DEST = "df";
	static public final String POS = "p";
	static public final String FRAME = "fr";
	static public final String RELATED = "rf";
	static public final String LU = "lu";
	static public final String FE = "fe";
	static public final String FETYPE = "ft";
	static public final String SENTENCE = "st";
	static public final String ANNOSET = "an";

	static public final class FnWords
	{
		static public final String TABLE = Q.WORDS.TABLE;
		static public final String CONTENT_URI_TABLE = FnWords.TABLE;
		static public final String FNWORDID = Q.FNWORDID;
		static public final String WORDID = Q.WORDID;
		static public final String WORD = Q.WORD;
	}

	static public final class LexUnits
	{
		static public final String TABLE = Q.LEXUNITS.TABLE;
		static public final String CONTENT_URI_TABLE = LexUnits.TABLE;
		static public final String LUID = Q.LUID;
		static public final String CONTENTS = Q.LUID;
		static public final String LEXUNIT = Q.LEXUNIT;
		static public final String LUDEFINITION = Q.LUDEFINITION;
		static public final String LUDICT = Q.LUDICT;
		static public final String FRAMEID = Q.FRAMEID;
	}

	static public final class LexUnits_X
	{
		static public final String TABLE_BY_LEXUNIT = "fnlexunits_x_by_lexunit";
		static public final String CONTENT_URI_TABLE = LexUnits_X.TABLE_BY_LEXUNIT;
		static public final String LUID = Q.LUID;
		static public final String CONTENTS = Q.LUID;
		static public final String LEXUNIT = Q.LEXUNIT;
		static public final String LUDEFINITION = Q.LUDEFINITION;
		static public final String LUDICT = Q.LUDICT;
		static public final String POSID = Q.POSID;
		static public final String FRAMEID = Q.FRAMEID;
		static public final String FRAME = Q.FRAME;
		static public final String FRAMEDEFINITION = Q.FRAMEDEFINITION;
		static public final String INCORPORATEDFETYPE = Q.FETYPE;
		static public final String INCORPORATEDFEDEFINITION = Q.FEDEFINITION;
		static public final String INCORPORATEDFECORESET = Q.CORESET;
	}

	static public final class LexUnits_or_Frames
	{
		static public final String TABLE = "fnlexunits_or_fnframe";
		static public final String CONTENT_URI_TABLE = LexUnits_or_Frames.TABLE;
		static public final String ID = Q._ID;
		static public final String FNID = Q.FNID;
		static public final String FNWORDID = Q.FNWORDID;
		static public final String WORDID = Q.WORDID;
		static public final String WORD = Q.WORD;
		static public final String NAME = Q.NAME;
		static public final String FRAMENAME = Q.FRAME;
		static public final String FRAMEID = Q.FRAMEID;
		static public final String ISFRAME = Q.ISFRAME;
	}

	static public final class Frames
	{
		static public final String TABLE = Q.FRAMES.TABLE;
		static public final String CONTENT_URI_TABLE = Frames.TABLE;
		static public final String FRAMEID = Q.FRAMEID;
		static public final String CONTENTS = Q.FRAMEID;
	}

	static public final class Frames_X
	{
		static public final String TABLE_BY_FRAME = "fnframes_x_by_frame";
		static public final String CONTENT_URI_TABLE = Frames_X.TABLE_BY_FRAME;
		static public final String FRAMEID = Q.FRAMEID;
		static public final String CONTENTS = Q.FRAMEID;
		static public final String FRAME = Q.FRAME;
		static public final String FRAMEDEFINITION = Q.FRAMEDEFINITION;
		static public final String SEMTYPE = Q.SEMTYPE;
		static public final String SEMTYPEABBREV = Q.SEMTYPEABBREV;
		static public final String SEMTYPEDEFINITION = Q.SEMTYPEDEFINITION;
	}

	static public final class Frames_Related
	{
		static public final String TABLE = Q.FRAMES_RELATED.TABLE;
		static public final String CONTENT_URI_TABLE = Frames_Related.TABLE;
		static public final String FRAMEID = Q.FRAMEID;
		static public final String FRAME = Q.FRAME;
		static public final String FRAME2ID = Q.FRAME2ID;
		static public final String RELATIONID = Q.RELATIONID;
		static public final String RELATION = Q.RELATION;
		static public final String RELATIONTYPE = Q.TYPE;
		static public final String RELATIONGLOSS = Q.GLOSS;
	}

	static public final class Sentences
	{
		static public final String TABLE = Q.SENTENCES.TABLE;
		static public final String CONTENT_URI_TABLE = Sentences.TABLE;
		static public final String SENTENCEID = Q.SENTENCEID;
		static public final String TEXT = Q.TEXT;
	}

	static public final class AnnoSets
	{
		static public final String TABLE = Q.ANNOSETS.TABLE;
		static public final String CONTENT_URI_TABLE = AnnoSets.TABLE;
		static public final String ANNOSETID = Q.ANNOSETID;
		static public final String CONTENTS = Q.ANNOSETID;
	}

	static public final class Sentences_Layers_X
	{
		static public final String TABLE = "fnsentences_fnlayers_x";
		static public final String CONTENT_URI_TABLE = Sentences_Layers_X.TABLE;
		static public final String SENTENCEID = Q.SENTENCEID;
		static public final String ANNOSETID = Q.ANNOSETID;
		static public final String LAYERID = Q.LAYERID;
		static public final String LAYERTYPE = Q.LAYERTYPE;
		static public final String LAYERANNOTATIONS = Q.ANNOTATIONS;
		static public final String RANK = Q.RANK;
		static public final String START = Q.START;
		static public final String END = Q.END;
		static public final String LABELTYPE = Q.LABELTYPE;
		static public final String LABELITYPE = Q.LABELITYPE;
		static public final String BGCOLOR = Q.BGCOLOR;
		static public final String FGCOLOR = Q.FGCOLOR;
	}

	static public final class AnnoSets_Layers_X
	{
		static public final String TABLE = "fnannosets_fnlayers_x";
		static public final String CONTENT_URI_TABLE = AnnoSets_Layers_X.TABLE;
		static public final String ANNOSETID = Q.ANNOSETID;
		static public final String SENTENCEID = Q.SENTENCEID;
		static public final String SENTENCETEXT = Q.TEXT;
		static public final String LAYERID = Q.LAYERID;
		static public final String LAYERTYPE = Q.LAYERTYPE;
		static public final String LAYERANNOTATIONS = Q.ANNOTATIONS;
		static public final String RANK = Q.RANK;
		static public final String START = Q.START;
		static public final String END = Q.END;
		static public final String LABELTYPE = Q.LABELTYPE;
		static public final String LABELITYPE = Q.LABELITYPE;
		static public final String BGCOLOR = Q.BGCOLOR;
		static public final String FGCOLOR = Q.FGCOLOR;
	}

	static public final class Patterns_Layers_X
	{
		static public final String TABLE = "fnpatterns_fnlayers_x";
		static public final String CONTENT_URI_TABLE = Patterns_Layers_X.TABLE;
		static public final String ANNOSETID = Q.ANNOSETID;
		static public final String SENTENCEID = Q.SENTENCEID;
		static public final String SENTENCETEXT = Q.TEXT;
		static public final String LAYERID = Q.LAYERID;
		static public final String LAYERTYPE = Q.LAYERTYPE;
		static public final String LAYERANNOTATIONS = Q.ANNOTATIONS;
		static public final String RANK = Q.RANK;
		static public final String START = Q.START;
		static public final String END = Q.END;
		static public final String LABELTYPE = Q.LABELTYPE;
		static public final String LABELITYPE = Q.LABELITYPE;
		static public final String BGCOLOR = Q.BGCOLOR;
		static public final String FGCOLOR = Q.FGCOLOR;
	}

	static public final class ValenceUnits_Layers_X
	{
		static public final String TABLE = "fnvalenceunits_fnlayers_x";
		static public final String CONTENT_URI_TABLE = ValenceUnits_Layers_X.TABLE;
		static public final String ANNOSETID = Q.ANNOSETID;
		static public final String SENTENCEID = Q.SENTENCEID;
		static public final String SENTENCETEXT = Q.TEXT;
		static public final String LAYERID = Q.LAYERID;
		static public final String LAYERTYPE = Q.LAYERTYPE;
		static public final String LAYERANNOTATIONS = Q.ANNOTATIONS;
		static public final String RANK = Q.RANK;
		static public final String START = Q.START;
		static public final String END = Q.END;
		static public final String LABELTYPE = Q.LABELTYPE;
		static public final String LABELITYPE = Q.LABELITYPE;
		static public final String BGCOLOR = Q.BGCOLOR;
		static public final String FGCOLOR = Q.FGCOLOR;
	}

	static public final class Words_LexUnits_Frames
	{
		static public final String TABLE = "words_fnlexunits";
		static public final String CONTENT_URI_TABLE = Words_LexUnits_Frames.TABLE;
		static public final String WORDID = Q.WORDID;
		static public final String LUID = Q.LUID;
		static public final String LEXUNIT = Q.LEXUNIT;
		static public final String LUDEFINITION = Q.LUDEFINITION;
		static public final String LUDICT = Q.LUDICT;
		static public final String POS = Q.POS;
		static public final String POSID = Q.POSID;
		static public final String FRAMEID = Q.FRAMEID;
		static public final String FRAME = Q.FRAME;
		static public final String FRAMEDEFINITION = Q.FRAMEDEFINITION;
		static public final String SEMTYPE = Q.SEMTYPE;
		static public final String SEMTYPEABBREV = Q.SEMTYPEABBREV;
		static public final String SEMTYPEDEFINITION = Q.SEMTYPEDEFINITION;
		static public final String INCORPORATEDFETYPE = Q.FETYPE;
		static public final String INCORPORATEDFEDEFINITION = Q.FEDEFINITION;
	}

	static public final class Frames_FEs
	{
		static public final String TABLE = "fnframes_fnfes";
		static public final String TABLE_BY_FE = TABLE + "/fe";
		static public final String CONTENT_URI_TABLE = Frames_FEs.TABLE;
		static public final String CONTENT_URI_TABLE_BY_FE = Frames_FEs.TABLE_BY_FE;
		static public final String FRAMEID = Q.FRAMEID;
		static public final String FETYPEID = Q.FETYPEID;
		static public final String FETYPE = Q.FETYPE;
		static public final String FEABBREV = Q.FEABBREV;
		static public final String FEDEFINITION = Q.FEDEFINITION;
		static public final String SEMTYPE = Q.SEMTYPE;
		static public final String SEMTYPES = Q.SEMTYPES;
		static public final String CORESET = Q.CORESET;
		static public final String CORETYPE = Q.CORETYPE;
		static public final String CORETYPEID = Q.CORETYPEID;
	}

	static public final class LexUnits_Sentences
	{
		static public final String TABLE = "fnframes_fnsentences";
		static public final String TABLE_BY_SENTENCE = TABLE + "/sentence";
		static public final String CONTENT_URI_TABLE = LexUnits_Sentences.TABLE;
		static public final String CONTENT_URI_TABLE_BY_SENTENCE = LexUnits_Sentences.TABLE_BY_SENTENCE;
		static public final String LUID = Q.LUID;
		static public final String FRAMEID = Q.FRAMEID;
		static public final String SENTENCEID = Q.SENTENCEID;
		static public final String TEXT = Q.TEXT;
		static public final String CORPUSID = Q.CORPUSID;
		static public final String DOCUMENTID = Q.DOCUMENTID;
		static public final String PARAGNO = Q.PARAGNO;
		static public final String SENTNO = Q.SENTNO;
	}

	static public final class LexUnits_Sentences_AnnoSets_Layers_Labels
	{
		static public final String TABLE = "fnlexunits_fnsentences_fnannosets_fnlayers_fnlabels";
		static public final String TABLE_BY_SENTENCE = TABLE + "/sentence";
		static public final String CONTENT_URI_TABLE = LexUnits_Sentences_AnnoSets_Layers_Labels.TABLE;
		static public final String CONTENT_URI_TABLE_BY_SENTENCE = LexUnits_Sentences_AnnoSets_Layers_Labels.TABLE_BY_SENTENCE;
		static public final String LUID = Q.LUID;
		static public final String FRAMEID = Q.FRAMEID;
		static public final String SENTENCEID = Q.SENTENCEID;
		static public final String TEXT = Q.TEXT;
		static public final String ANNOSETID = Q.ANNOSETID;
		static public final String LAYERID = Q.LAYERID;
		static public final String LAYERTYPE = Q.LAYERTYPE;
		static public final String LAYERANNOTATION = Q.ANNOTATIONS;
		static public final String RANK = Q.RANK;
		static public final String CORPUSID = Q.CORPUSID;
		static public final String DOCUMENTID = Q.DOCUMENTID;
		static public final String PARAGNO = Q.PARAGNO;
		static public final String SENTNO = Q.SENTNO;
		static public final String START = Q.START;
		static public final String END = Q.END;
		static public final String LABELTYPE = Q.LABELTYPE;
		static public final String LABELITYPE = Q.LABELITYPE;
		static public final String BGCOLOR = Q.BGCOLOR;
		static public final String FGCOLOR = Q.FGCOLOR;
	}

	static public final class LexUnits_Governors
	{
		static public final String TABLE = "fnlexunits_fngovernors";
		static public final String CONTENT_URI_TABLE = LexUnits_Governors.TABLE;
		static public final String LUID = Q.LUID;
		static public final String GOVERNORID = Q.GOVERNORID;
		static public final String GOVERNORTYPE = Q.GOVERNORTYPE;
		static public final String FNWORDID = Q.FNWORDID;
		static public final String FNWORD = Q.WORD;
	}

	static public final class LexUnits_FERealizations_ValenceUnits
	{
		static public final String TABLE = "fnlexunits_fnferealizations_fnvalenceunits";
		static public final String TABLE_BY_REALIZATION = TABLE + "/realization";
		static public final String CONTENT_URI_TABLE = LexUnits_FERealizations_ValenceUnits.TABLE;
		static public final String CONTENT_URI_TABLE_BY_REALIZATION = LexUnits_FERealizations_ValenceUnits.TABLE_BY_REALIZATION;
		static public final String LUID = Q.LUID;
		static public final String FERID = Q.FERID;
		static public final String FETYPE = Q.FETYPE;
		static public final String PT = Q.PT;
		static public final String GF = Q.GF;
		static public final String TOTAL = Q.TOTAL;
		static public final String VUID = Q.VUID;
		static public final String FERS = Q.FERS;
	}

	static public final class LexUnits_FEGroupRealizations_Patterns_ValenceUnits
	{
		static public final String TABLE = "fnlexunits_fnferealizations_fnpatterns_fnvalenceunits";
		static public final String TABLE_BY_PATTERN = TABLE + "/pattern";
		static public final String CONTENT_URI_TABLE = LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE;
		static public final String CONTENT_URI_TABLE_BY_PATTERN = LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE_BY_PATTERN;
		static public final String LUID = Q.LUID;
		static public final String FEGRID = Q.FEGRID;
		static public final String FETYPE = Q.FETYPE;
		static public final String GROUPREALIZATION = Q.GROUPREALIZATION;
		static public final String GROUPREALIZATIONS = Q.A_GROUPREALIZATIONS;
		static public final String GF = Q.GF;
		static public final String PT = Q.PT;
		static public final String TOTAL = Q.TOTAL;
		static public final String TOTALS = Q.TOTALS;
		static public final String PATTERNID = Q.PATTERNID;
		static public final String VUID = Q.PATTERNID;
	}

	static public final class Patterns_Sentences
	{
		static public final String TABLE = "fnpatterns_annosets";
		static public final String CONTENT_URI_TABLE = Patterns_Sentences.TABLE;
		static public final String PATTERNID = Q.PATTERNID;
		static public final String ANNOSETID = Q.ANNOSETID;
		static public final String SENTENCEID = Q.SENTENCEID;
		static public final String TEXT = Q.TEXT;
	}

	static public final class ValenceUnits_Sentences
	{
		static public final String TABLE = "fnvalenceunits_annosets";
		static public final String CONTENT_URI_TABLE = ValenceUnits_Sentences.TABLE;
		static public final String VUID = Q.VUID;
		static public final String ANNOSETID = Q.ANNOSETID;
		static public final String SENTENCEID = Q.SENTENCEID;
		static public final String TEXT = Q.TEXT;
	}

	static public final class Governors_AnnoSets_Sentences
	{
		static public final String TABLE = "fngovernors_annosets_sentences";
		static public final String CONTENT_URI_TABLE = Governors_AnnoSets_Sentences.TABLE;
		static public final String GOVERNORID = Q.GOVERNORID;
		static public final String ANNOSETID = Q.ANNOSETID;
		static public final String SENTENCEID = Q.SENTENCEID;
		static public final String TEXT = Q.TEXT;
	}

	static public final class Lookup_FnWords
	{
		static public final String TABLE = "fts_fnwords";
		static public final String CONTENT_URI_TABLE = Lookup_FnWords.TABLE;
		static public final String FNWORDID = Q.FNWORDID;
		static public final String WORDID = Q.WORDID;
		static public final String WORD = Q.WORD;
	}

	static public final class Lookup_FnSentences
	{
		static public final String TABLE = "fts_fnsentences";
		static public final String CONTENT_URI_TABLE = Lookup_FnSentences.TABLE;
		static public final String SENTENCEID = Q.SENTENCEID;
		static public final String TEXT = Q.TEXT;
		static public final String FRAMEID = Q.FRAMEID;
		static public final String LUID = Q.LUID;
		static public final String ANNOSETID = Q.ANNOSETID;
	}

	static public final class Lookup_FnSentences_X
	{
		static public final String TABLE = "fts_fnsentences_x";
		static public final String TABLE_BY_SENTENCE = "fts_fnsentences_x_by_sentence";
		static public final String CONTENT_URI_TABLE = Lookup_FnSentences_X.TABLE_BY_SENTENCE;
		static public final String SENTENCEID = Q.SENTENCEID;
		static public final String TEXT = Q.TEXT;
		static public final String FRAMEID = Q.FRAMEID;
		static public final String FRAME = Q.FRAME;
		static public final String FRAMES = Q.A_FRAMES;
		static public final String LUID = Q.LUID;
		static public final String LEXUNIT = Q.LEXUNIT;
		static public final String LEXUNITS = Q.A_LEXUNITS;
		static public final String ANNOSETID = Q.ANNOSETID;
		static public final String ANNOSETS = Q.A_ANNOSETS;
	}

	static public final class Suggest_FnWords
	{
		static final String SEARCH_WORD_PATH = "suggest_fnword";
		static public final String TABLE = Suggest_FnWords.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
		static public final String FNWORDID = Q.FNWORDID;
		static public final String WORDID = Q.WORDID;
		static public final String WORD = Q.WORD;
	}

	static public final class Suggest_FTS_FnWords
	{
		static final String SEARCH_WORD_PATH = "suggest_fts_fnword";
		static public final String TABLE = Suggest_FTS_FnWords.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
		static public final String FNWORDID = Q.FNWORDID;
		static public final String WORDID = Q.WORDID;
		static public final String WORD = Q.WORD;
	}
}
