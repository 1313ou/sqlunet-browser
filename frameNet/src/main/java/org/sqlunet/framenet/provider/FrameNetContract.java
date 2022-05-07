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

	static public final class Words
	{
		static public final String TABLE = Q.WORDS.TABLE;
		static public final String CONTENT_URI_TABLE = Words.TABLE;
		static public final String FNWORDID = V.FNWORDID;
		static public final String WORDID = V.WORDID;
		//static public final String WORD = V.WORD;
	}

	static public final class LexUnits
	{
		static public final String TABLE = Q.LEXUNITS.TABLE;
		static public final String CONTENT_URI_TABLE = LexUnits.TABLE;
		static public final String LUID = V.LUID;
		static public final String CONTENTS = V.LUID;
		static public final String LEXUNIT = V.LEXUNIT;
		static public final String LUDEFINITION = V.LUDEFINITION;
		static public final String LUDICT = V.LUDICT;
		static public final String FRAMEID = V.FRAMEID;
	}

	static public final class LexUnits_X
	{
		static public final String TABLE_BY_LEXUNIT = "lexunits_x_by_lexunit";
		static public final String CONTENT_URI_TABLE = LexUnits_X.TABLE_BY_LEXUNIT;
		static public final String LUID = V.LUID;
		static public final String CONTENTS = V.LUID;
		static public final String LEXUNIT = V.LEXUNIT;
		static public final String LUDEFINITION = V.LUDEFINITION;
		static public final String LUDICT = V.LUDICT;
		static public final String POSID = V.POSID;
		static public final String FRAMEID = V.FRAMEID;
		static public final String FRAME = V.FRAME;
		static public final String FRAMEDEFINITION = V.FRAMEDEFINITION;
		static public final String INCORPORATEDFETYPE = V.FETYPE;
		static public final String INCORPORATEDFEDEFINITION = V.FEDEFINITION;
		static public final String INCORPORATEDFECORESET = V.CORESET;
	}

	static public final class LexUnits_or_Frames
	{
		static public final String TABLE = "lexunits_or_frame";
		static public final String CONTENT_URI_TABLE = LexUnits_or_Frames.TABLE;
		static public final String TABLE_FN = "lexunits_or_frame_fn";
		static public final String CONTENT_URI_TABLE_FN = LexUnits_or_Frames.TABLE_FN;
		static public final String ID = V._ID;
		static public final String FNID = V.FNID;
		static public final String FNWORDID = V.FNWORDID;
		static public final String WORDID = V.WORDID;
		static public final String WORD = V.WORD;
		static public final String NAME = V.NAME;
		static public final String FRAMENAME = V.FRAME;
		static public final String FRAMEID = V.FRAMEID;
		static public final String ISFRAME = V.ISFRAME;
	}

	static public final class Frames
	{
		static public final String TABLE = Q.FRAMES.TABLE;
		static public final String CONTENT_URI_TABLE = Frames.TABLE;
		static public final String FRAMEID = V.FRAMEID;
		static public final String CONTENTS = V.FRAMEID;
	}

	static public final class Frames_X
	{
		static public final String TABLE = "frames_x";
		static public final String TABLE_BY_FRAME = "frames_x_by_frame";
		static public final String CONTENT_URI_TABLE = Frames_X.TABLE;
		static public final String CONTENT_URI_TABLE_BY_FRAME = Frames_X.TABLE_BY_FRAME;
		static public final String FRAMEID = V.FRAMEID;
		static public final String CONTENTS = V.FRAMEID;
		static public final String FRAME = V.FRAME;
		static public final String FRAMEDEFINITION = V.FRAMEDEFINITION;
		static public final String SEMTYPE = V.SEMTYPE;
		static public final String SEMTYPEABBREV = V.SEMTYPEABBREV;
		static public final String SEMTYPEDEFINITION = V.SEMTYPEDEFINITION;
	}

	static public final class Frames_Related
	{
		static public final String TABLE = "frames_related";
		static public final String CONTENT_URI_TABLE = Frames_Related.TABLE;
		static public final String FRAMEID = V.FRAMEID;
		static public final String FRAME = V.FRAME;
		static public final String FRAME2ID = V.FRAME2ID;
		static public final String RELATIONID = V.RELATIONID;
		static public final String RELATION = V.RELATION;
	}

	static public final class Sentences
	{
		static public final String TABLE = Q.SENTENCES.TABLE;
		static public final String CONTENT_URI_TABLE = Sentences.TABLE;
		static public final String SENTENCEID = V.SENTENCEID;
		static public final String TEXT = V.TEXT;
	}

	static public final class AnnoSets
	{
		static public final String TABLE = Q.ANNOSETS.TABLE;
		static public final String CONTENT_URI_TABLE = AnnoSets.TABLE;
		static public final String ANNOSETID = V.ANNOSETID;
		static public final String CONTENTS = V.ANNOSETID;
	}

	static public final class Sentences_Layers_X
	{
		static public final String TABLE = "sentences_layers_x";
		static public final String CONTENT_URI_TABLE = Sentences_Layers_X.TABLE;
		static public final String SENTENCEID = V.SENTENCEID;
		static public final String ANNOSETID = V.ANNOSETID;
		static public final String LAYERID = V.LAYERID;
		static public final String LAYERTYPE = V.LAYERTYPE;
		static public final String LAYERANNOTATIONS = V.ANNOTATIONS;
		static public final String RANK = V.RANK;
		static public final String START = V.START;
		static public final String END = V.END;
		static public final String LABELTYPE = V.LABELTYPE;
		static public final String LABELITYPE = V.LABELITYPE;
		static public final String BGCOLOR = V.BGCOLOR;
		static public final String FGCOLOR = V.FGCOLOR;
	}

	static public final class AnnoSets_Layers_X
	{
		static public final String TABLE = "annosets_layers_x";
		static public final String CONTENT_URI_TABLE = AnnoSets_Layers_X.TABLE;
		static public final String ANNOSETID = V.ANNOSETID;
		static public final String SENTENCEID = V.SENTENCEID;
		static public final String SENTENCETEXT = V.TEXT;
		static public final String LAYERID = V.LAYERID;
		static public final String LAYERTYPE = V.LAYERTYPE;
		static public final String LAYERANNOTATIONS = V.ANNOTATIONS;
		static public final String RANK = V.RANK;
		static public final String START = V.START;
		static public final String END = V.END;
		static public final String LABELTYPE = V.LABELTYPE;
		static public final String LABELITYPE = V.LABELITYPE;
		static public final String BGCOLOR = V.BGCOLOR;
		static public final String FGCOLOR = V.FGCOLOR;
	}

	static public final class Patterns_Layers_X
	{
		static public final String TABLE = "patterns_layers_x";
		static public final String CONTENT_URI_TABLE = Patterns_Layers_X.TABLE;
		static public final String ANNOSETID = V.ANNOSETID;
		static public final String SENTENCEID = V.SENTENCEID;
		static public final String SENTENCETEXT = V.TEXT;
		static public final String LAYERID = V.LAYERID;
		static public final String LAYERTYPE = V.LAYERTYPE;
		static public final String LAYERANNOTATIONS = V.ANNOTATIONS;
		static public final String RANK = V.RANK;
		static public final String START = V.START;
		static public final String END = V.END;
		static public final String LABELTYPE = V.LABELTYPE;
		static public final String LABELITYPE = V.LABELITYPE;
		static public final String BGCOLOR = V.BGCOLOR;
		static public final String FGCOLOR = V.FGCOLOR;
	}

	static public final class ValenceUnits_Layers_X
	{
		static public final String TABLE = "valenceunits_layers_x";
		static public final String CONTENT_URI_TABLE = ValenceUnits_Layers_X.TABLE;
		static public final String ANNOSETID = V.ANNOSETID;
		static public final String SENTENCEID = V.SENTENCEID;
		static public final String SENTENCETEXT = V.TEXT;
		static public final String LAYERID = V.LAYERID;
		static public final String LAYERTYPE = V.LAYERTYPE;
		static public final String LAYERANNOTATIONS = V.ANNOTATIONS;
		static public final String RANK = V.RANK;
		static public final String START = V.START;
		static public final String END = V.END;
		static public final String LABELTYPE = V.LABELTYPE;
		static public final String LABELITYPE = V.LABELITYPE;
		static public final String BGCOLOR = V.BGCOLOR;
		static public final String FGCOLOR = V.FGCOLOR;
	}

	static public final class Words_LexUnits_Frames
	{
		static public final String TABLE = "words_lexunits";
		static public final String CONTENT_URI_TABLE = Words_LexUnits_Frames.TABLE;
		static public final String TABLE_FN = "words_lexunits_fn";
		static public final String CONTENT_URI_TABLE_FN = Words_LexUnits_Frames.TABLE_FN;
		static public final String WORDID = V.WORDID;
		static public final String LUID = V.LUID;
		static public final String LEXUNIT = V.LEXUNIT;
		static public final String LUDEFINITION = V.LUDEFINITION;
		static public final String LUDICT = V.LUDICT;
		static public final String POS = V.POS;
		static public final String POSID = V.POSID;
		static public final String FRAMEID = V.FRAMEID;
		static public final String FRAME = V.FRAME;
		static public final String FRAMEDEFINITION = V.FRAMEDEFINITION;
		static public final String SEMTYPE = V.SEMTYPE;
		static public final String SEMTYPEABBREV = V.SEMTYPEABBREV;
		static public final String SEMTYPEDEFINITION = V.SEMTYPEDEFINITION;
		static public final String INCORPORATEDFETYPE = V.FETYPE;
		static public final String INCORPORATEDFEDEFINITION = V.FEDEFINITION;
	}

	static public final class Frames_FEs
	{
		static public final String TABLE = "frames_fes";
		static public final String TABLE_BY_FE = TABLE + "by_fe";
		static public final String CONTENT_URI_TABLE = Frames_FEs.TABLE;
		static public final String CONTENT_URI_TABLE_BY_FE = Frames_FEs.TABLE_BY_FE;
		static public final String FRAMEID = V.FRAMEID;
		static public final String FETYPEID = V.FETYPEID;
		static public final String FETYPE = V.FETYPE;
		static public final String FEABBREV = V.FEABBREV;
		static public final String FEDEFINITION = V.FEDEFINITION;
		static public final String SEMTYPE = V.SEMTYPE;
		static public final String SEMTYPES = V.SEMTYPES;
		static public final String CORESET = V.CORESET;
		static public final String CORETYPE = V.CORETYPE;
		static public final String CORETYPEID = V.CORETYPEID;
	}

	static public final class LexUnits_Sentences
	{
		static public final String TABLE = "fnframes_fnsentences";
		static public final String TABLE_BY_SENTENCE = TABLE + "/sentence";
		static public final String CONTENT_URI_TABLE = LexUnits_Sentences.TABLE;
		static public final String CONTENT_URI_TABLE_BY_SENTENCE = LexUnits_Sentences.TABLE_BY_SENTENCE;
		static public final String LUID = V.LUID;
		static public final String FRAMEID = V.FRAMEID;
		static public final String SENTENCEID = V.SENTENCEID;
		static public final String TEXT = V.TEXT;
		static public final String CORPUSID = V.CORPUSID;
		static public final String DOCUMENTID = V.DOCUMENTID;
		static public final String PARAGNO = V.PARAGNO;
		static public final String SENTNO = V.SENTNO;
	}

	static public final class LexUnits_Sentences_AnnoSets_Layers_Labels
	{
		static public final String TABLE = "fnlexunits_fnsentences_fnannosets_fnlayers_fnlabels";
		static public final String TABLE_BY_SENTENCE = TABLE + "/sentence";
		static public final String CONTENT_URI_TABLE = LexUnits_Sentences_AnnoSets_Layers_Labels.TABLE;
		static public final String CONTENT_URI_TABLE_BY_SENTENCE = LexUnits_Sentences_AnnoSets_Layers_Labels.TABLE_BY_SENTENCE;
		static public final String LUID = V.LUID;
		static public final String FRAMEID = V.FRAMEID;
		static public final String SENTENCEID = V.SENTENCEID;
		static public final String TEXT = V.TEXT;
		static public final String ANNOSETID = V.ANNOSETID;
		static public final String LAYERID = V.LAYERID;
		static public final String LAYERTYPE = V.LAYERTYPE;
		static public final String LAYERANNOTATION = V.ANNOTATIONS;
		static public final String RANK = V.RANK;
		static public final String CORPUSID = V.CORPUSID;
		static public final String DOCUMENTID = V.DOCUMENTID;
		static public final String PARAGNO = V.PARAGNO;
		static public final String SENTNO = V.SENTNO;
		static public final String START = V.START;
		static public final String END = V.END;
		static public final String LABELTYPE = V.LABELTYPE;
		static public final String LABELITYPE = V.LABELITYPE;
		static public final String BGCOLOR = V.BGCOLOR;
		static public final String FGCOLOR = V.FGCOLOR;
	}

	static public final class LexUnits_Governors
	{
		static public final String TABLE = "fnlexunits_fngovernors";
		static public final String CONTENT_URI_TABLE = LexUnits_Governors.TABLE;
		static public final String TABLE_FN = "fnlexunits_fngovernors_fn";
		static public final String CONTENT_URI_TABLE_FN = LexUnits_Governors.TABLE_FN;
		static public final String LUID = V.LUID;
		static public final String GOVERNORID = V.GOVERNORID;
		static public final String GOVERNORTYPE = V.GOVERNORTYPE;
		static public final String FNWORDID = V.FNWORDID;
		static public final String WORD = V.WORD;
	}

	static public final class LexUnits_FERealizations_ValenceUnits
	{
		static public final String TABLE = "lexunits_ferealizations_valenceunits";
		static public final String TABLE_BY_REALIZATION = TABLE + "by_realization";
		static public final String CONTENT_URI_TABLE = LexUnits_FERealizations_ValenceUnits.TABLE;
		static public final String CONTENT_URI_TABLE_BY_REALIZATION = LexUnits_FERealizations_ValenceUnits.TABLE_BY_REALIZATION;
		static public final String LUID = V.LUID;
		static public final String FERID = V.FERID;
		static public final String FETYPE = V.FETYPE;
		static public final String PT = V.PT;
		static public final String GF = V.GF;
		static public final String TOTAL = V.TOTAL;
		static public final String VUID = V.VUID;
		static public final String FERS = V.FERS;
	}

	static public final class LexUnits_FEGroupRealizations_Patterns_ValenceUnits
	{
		static public final String TABLE = "lexunits_fegrouprealizations_patterns_valenceunits";
		static public final String TABLE_BY_PATTERN = TABLE + "_by_pattern";
		static public final String CONTENT_URI_TABLE = LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE;
		static public final String CONTENT_URI_TABLE_BY_PATTERN = LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE_BY_PATTERN;
		static public final String LUID = V.LUID;
		static public final String FEGRID = V.FEGRID;
		static public final String FETYPE = V.FETYPE;
		static public final String GROUPREALIZATION = V.GROUPREALIZATION;
		static public final String GROUPREALIZATIONS = V.A_GROUPREALIZATIONS;
		static public final String GF = V.GF;
		static public final String PT = V.PT;
		static public final String TOTAL = V.TOTAL;
		static public final String TOTALS = V.TOTALS;
		static public final String PATTERNID = V.PATTERNID;
		static public final String VUID = V.PATTERNID;
	}

	static public final class Patterns_Sentences
	{
		static public final String TABLE = "patterns_sentences";
		static public final String CONTENT_URI_TABLE = Patterns_Sentences.TABLE;
		static public final String PATTERNID = V.PATTERNID;
		static public final String ANNOSETID = V.ANNOSETID;
		static public final String SENTENCEID = V.SENTENCEID;
		static public final String TEXT = V.TEXT;
	}

	static public final class ValenceUnits_Sentences
	{
		static public final String TABLE = "valenceunits_sentences";
		static public final String CONTENT_URI_TABLE = ValenceUnits_Sentences.TABLE;
		static public final String VUID = V.VUID;
		static public final String ANNOSETID = V.ANNOSETID;
		static public final String SENTENCEID = V.SENTENCEID;
		static public final String TEXT = V.TEXT;
	}

	static public final class Governors_AnnoSets_Sentences
	{
		static public final String TABLE = "governors_annosets_sentences";
		static public final String CONTENT_URI_TABLE = Governors_AnnoSets_Sentences.TABLE;
		static public final String GOVERNORID = V.GOVERNORID;
		static public final String ANNOSETID = V.ANNOSETID;
		static public final String SENTENCEID = V.SENTENCEID;
		static public final String TEXT = V.TEXT;
	}

	static public final class Lookup_FTS_FnWords
	{
		static public final String TABLE = "fts_fnwords";
		static public final String CONTENT_URI_TABLE = Lookup_FTS_FnWords.TABLE;
		static public final String FNWORDID = V.FNWORDID;
		static public final String WORDID = V.WORDID;
		static public final String WORD = V.WORD;
	}

	static public final class Lookup_FTS_FnSentences
	{
		static public final String TABLE = "fts_sentences";
		static public final String CONTENT_URI_TABLE = Lookup_FTS_FnSentences.TABLE;
		static public final String SENTENCEID = V.SENTENCEID;
		static public final String TEXT = V.TEXT;
		static public final String FRAMEID = V.FRAMEID;
		static public final String LUID = V.LUID;
		static public final String ANNOSETID = V.ANNOSETID;
	}

	static public final class Lookup_FTS_FnSentences_X
	{
		static public final String TABLE = "fts_sentences_x";
		static public final String TABLE_BY_SENTENCE = "fts_fnsentences_x_by_sentence";
		static public final String CONTENT_URI_TABLE = Lookup_FTS_FnSentences_X.TABLE;
		static public final String CONTENT_URI_TABLE_BY_SENTENCE = Lookup_FTS_FnSentences_X.TABLE_BY_SENTENCE;
		static public final String SENTENCEID = V.SENTENCEID;
		static public final String TEXT = V.TEXT;
		static public final String FRAMEID = V.FRAMEID;
		static public final String FRAME = V.FRAME;
		static public final String FRAMES = V.A_FRAMES;
		static public final String LUID = V.LUID;
		static public final String LEXUNIT = V.LEXUNIT;
		static public final String LEXUNITS = V.A_LEXUNITS;
		static public final String ANNOSETID = V.ANNOSETID;
		static public final String ANNOSETS = V.A_ANNOSETS;
	}

	static public final class Suggest_FnWords
	{
		static final String SEARCH_WORD_PATH = "suggest_fnword";
		static public final String TABLE = Suggest_FnWords.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
		static public final String FNWORDID = V.FNWORDID;
		static public final String WORDID = V.WORDID;
		static public final String WORD = V.WORD;
	}

	static public final class Suggest_FTS_FnWords
	{
		static final String SEARCH_WORD_PATH = "suggest_fts_fnword";
		static public final String TABLE = Suggest_FTS_FnWords.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
		static public final String FNWORDID = V.FNWORDID;
		static public final String WORDID = V.WORDID;
		static public final String WORD = V.WORD;
	}
}
