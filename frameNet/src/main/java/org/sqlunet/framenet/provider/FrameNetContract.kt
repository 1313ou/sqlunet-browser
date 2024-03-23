/*
 * Copyright (c) 2023. Bernard Bou
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

	static public final String AS_FRAMES = V.AS_FRAMES;
	static public final String AS_RELATED_FRAMES = V.AS_RELATED_FRAMES;
	static public final String AS_SRC_FRAMES = V.SRC_FRAME;
	static public final String AS_DEST_FRAMES = V.DEST_FRAME;
	static public final String AS_LEXUNITS = V.AS_LEXUNITS;
	static public final String AS_FES = V.AS_FES;
	static public final String AS_FETYPES = V.AS_FETYPES;
	static public final String AS_SENTENCES = V.AS_SENTENCES;
	static public final String AS_ANNOSETS = V.AS_ANNOSETS;
	static public final String AS_POSES = V.AS_POSES;

	public interface Words
	{
		String TABLE = Q.WORDS.TABLE;
		String URI = TABLE;
		String FNWORDID = V.FNWORDID;
		String WORDID = V.WORDID;
		//String WORD = V.WORD;
	}

	public interface LexUnits
	{
		String TABLE = Q.LEXUNITS.TABLE;
		String URI = LexUnits.TABLE;
		String URI1 = "lexunit";
		String LUID = V.LUID;
		String CONTENTS = V.LUID;
		String LEXUNIT = V.LEXUNIT;
		String LUDEFINITION = V.LUDEFINITION;
		String LUDICT = V.LUDICT;
		String FRAMEID = V.FRAMEID;
	}

	public interface LexUnits_X
	{
		String URI = "lexunits_x";
		String URI_BY_LEXUNIT = URI + "_by_lexunit";
		String LUID = V.LUID;
		String CONTENTS = V.LUID;
		String LEXUNIT = V.LEXUNIT;
		String LUDEFINITION = V.LUDEFINITION;
		String LUDICT = V.LUDICT;
		String POSID = V.POSID;
		String FRAMEID = V.FRAMEID;
		String FRAME = V.FRAME;
		String FRAMEDEFINITION = V.FRAMEDEFINITION;
		String INCORPORATEDFETYPE = V.FETYPE;
		String INCORPORATEDFEDEFINITION = V.FEDEFINITION;
		String INCORPORATEDFECORESET = V.CORESET;
	}

	public interface LexUnits_or_Frames
	{
		String URI = "lexunits_or_frame";
		String URI_FN = URI + "_fn";
		String ID = V._ID;
		String FNID = V.FNID;
		String FNWORDID = V.FNWORDID;
		String WORDID = V.WORDID;
		String WORD = V.WORD;
		String NAME = V.NAME;
		String FRAMENAME = V.FRAME;
		String FRAMEID = V.FRAMEID;
		String ISFRAME = V.ISFRAME;
	}

	public interface Frames
	{
		String TABLE = Q.FRAMES.TABLE;
		String URI = Frames.TABLE;
		String URI1 = "frame";
		String FRAMEID = V.FRAMEID;
		String CONTENTS = V.FRAMEID;
	}

	public interface Frames_X
	{
		String URI = "frames_x";
		String URI_BY_FRAME = URI + "_by_frame";
		String FRAMEID = V.FRAMEID;
		String CONTENTS = V.FRAMEID;
		String FRAME = V.FRAME;
		String FRAMEDEFINITION = V.FRAMEDEFINITION;
		String SEMTYPE = V.SEMTYPE;
		String SEMTYPEABBREV = V.SEMTYPEABBREV;
		String SEMTYPEDEFINITION = V.SEMTYPEDEFINITION;
	}

	public interface Frames_Related
	{
		String URI = "frames_related";
		String FRAMEID = V.FRAMEID;
		String FRAME = V.FRAME;
		String FRAME2ID = V.FRAME2ID;
		String RELATIONID = V.RELATIONID;
		String RELATION = V.RELATION;
	}

	public interface Sentences
	{
		String TABLE = Q.SENTENCES.TABLE;
		String URI = TABLE;
		String URI1 = "sentence";
		String SENTENCEID = V.SENTENCEID;
		String TEXT = V.TEXT;
	}

	public interface AnnoSets
	{
		String TABLE = Q.ANNOSETS.TABLE;
		String URI = TABLE;
		String URI1 = "annoset";
		String ANNOSETID = V.ANNOSETID;
		String CONTENTS = V.ANNOSETID;
	}

	public interface Sentences_Layers_X
	{
		String URI = "sentences_layers_x";
		String SENTENCEID = V.SENTENCEID;
		String ANNOSETID = V.ANNOSETID;
		String LAYERID = V.LAYERID;
		String LAYERTYPE = V.LAYERTYPE;
		String LAYERANNOTATIONS = V.ANNOTATIONS;
		String RANK = V.RANK;
		String START = V.START;
		String END = V.END;
		String LABELTYPE = V.LABELTYPE;
		String LABELITYPE = V.LABELITYPE;
		String BGCOLOR = V.BGCOLOR;
		String FGCOLOR = V.FGCOLOR;
	}

	public interface AnnoSets_Layers_X
	{
		String URI = "annosets_layers_x";
		String ANNOSETID = V.ANNOSETID;
		String SENTENCEID = V.SENTENCEID;
		String SENTENCETEXT = V.TEXT;
		String LAYERID = V.LAYERID;
		String LAYERTYPE = V.LAYERTYPE;
		String LAYERANNOTATIONS = V.ANNOTATIONS;
		String RANK = V.RANK;
		String START = V.START;
		String END = V.END;
		String LABELTYPE = V.LABELTYPE;
		String LABELITYPE = V.LABELITYPE;
		String BGCOLOR = V.BGCOLOR;
		String FGCOLOR = V.FGCOLOR;
	}

	public interface Patterns_Layers_X
	{
		String URI = "patterns_layers_x";
		String ANNOSETID = V.ANNOSETID;
		String SENTENCEID = V.SENTENCEID;
		String SENTENCETEXT = V.TEXT;
		String LAYERID = V.LAYERID;
		String LAYERTYPE = V.LAYERTYPE;
		String LAYERANNOTATIONS = V.ANNOTATIONS;
		String RANK = V.RANK;
		String START = V.START;
		String END = V.END;
		String LABELTYPE = V.LABELTYPE;
		String LABELITYPE = V.LABELITYPE;
		String BGCOLOR = V.BGCOLOR;
		String FGCOLOR = V.FGCOLOR;
	}

	public interface ValenceUnits_Layers_X
	{
		String URI = "valenceunits_layers_x";
		String ANNOSETID = V.ANNOSETID;
		String SENTENCEID = V.SENTENCEID;
		String SENTENCETEXT = V.TEXT;
		String LAYERID = V.LAYERID;
		String LAYERTYPE = V.LAYERTYPE;
		String LAYERANNOTATIONS = V.ANNOTATIONS;
		String RANK = V.RANK;
		String START = V.START;
		String END = V.END;
		String LABELTYPE = V.LABELTYPE;
		String LABELITYPE = V.LABELITYPE;
		String BGCOLOR = V.BGCOLOR;
		String FGCOLOR = V.FGCOLOR;
	}

	public interface Words_LexUnits_Frames
	{
		String URI = "words_lexunits";
		String URI_FN = URI + "_fn";
		String WORDID = V.WORDID;
		String LUID = V.LUID;
		String LEXUNIT = V.LEXUNIT;
		String LUDEFINITION = V.LUDEFINITION;
		String LUDICT = V.LUDICT;
		String POS = V.POS;
		String POSID = V.POSID;
		String FRAMEID = V.FRAMEID;
		String FRAME = V.FRAME;
		String FRAMEDEFINITION = V.FRAMEDEFINITION;
		String SEMTYPE = V.SEMTYPE;
		String SEMTYPEABBREV = V.SEMTYPEABBREV;
		String SEMTYPEDEFINITION = V.SEMTYPEDEFINITION;
		String INCORPORATEDFETYPE = V.FETYPE;
		String INCORPORATEDFEDEFINITION = V.FEDEFINITION;
	}

	public interface Frames_FEs
	{
		String URI = "frames_fes";
		String URI_BY_FE = URI + "_by_fe";
		String FRAMEID = V.FRAMEID;
		String FETYPEID = V.FETYPEID;
		String FETYPE = V.FETYPE;
		String FEABBREV = V.FEABBREV;
		String FEDEFINITION = V.FEDEFINITION;
		String SEMTYPE = V.SEMTYPE;
		String SEMTYPES = V.SEMTYPES;
		String CORESET = V.CORESET;
		String CORETYPE = V.CORETYPE;
		String CORETYPEID = V.CORETYPEID;
	}

	public interface LexUnits_Sentences
	{
		String URI = "frames_sentences";
		String URI_BY_SENTENCE = URI + "_by_sentence";
		String LUID = V.LUID;
		String FRAMEID = V.FRAMEID;
		String SENTENCEID = V.SENTENCEID;
		String TEXT = V.TEXT;
		String CORPUSID = V.CORPUSID;
		String DOCUMENTID = V.DOCUMENTID;
		String PARAGNO = V.PARAGNO;
		String SENTNO = V.SENTNO;
	}

	public interface LexUnits_Sentences_AnnoSets_Layers_Labels
	{
		String URI = "lexunits_sentences_annosets_layers_labels";
		String URI_BY_SENTENCE = URI + "_by_sentence";
		String LUID = V.LUID;
		String FRAMEID = V.FRAMEID;
		String SENTENCEID = V.SENTENCEID;
		String TEXT = V.TEXT;
		String ANNOSETID = V.ANNOSETID;
		String LAYERID = V.LAYERID;
		String LAYERTYPE = V.LAYERTYPE;
		String LAYERANNOTATION = V.ANNOTATIONS;
		String RANK = V.RANK;
		String CORPUSID = V.CORPUSID;
		String DOCUMENTID = V.DOCUMENTID;
		String PARAGNO = V.PARAGNO;
		String SENTNO = V.SENTNO;
		String START = V.START;
		String END = V.END;
		String LABELTYPE = V.LABELTYPE;
		String LABELITYPE = V.LABELITYPE;
		String BGCOLOR = V.BGCOLOR;
		String FGCOLOR = V.FGCOLOR;
	}

	public interface LexUnits_Governors
	{
		String URI = "lexunits_governors";
		String URI_FN = URI + "_fn";
		String LUID = V.LUID;
		String GOVERNORID = V.GOVERNORID;
		String GOVERNORTYPE = V.GOVERNORTYPE;
		String FNWORDID = V.FNWORDID;
		String WORD = V.WORD;
	}

	public interface LexUnits_FERealizations_ValenceUnits
	{
		String URI = "lexunits_ferealizations_valenceunits";
		String URI_BY_REALIZATION = URI + "_by_realization";
		String LUID = V.LUID;
		String FERID = V.FERID;
		String FETYPE = V.FETYPE;
		String PT = V.PT;
		String GF = V.GF;
		String TOTAL = V.TOTAL;
		String VUID = V.VUID;
		String FERS = V.FERS;
	}

	public interface LexUnits_FEGroupRealizations_Patterns_ValenceUnits
	{
		String URI = "lexunits_fegrouprealizations_patterns_valenceunits";
		String URI_BY_PATTERN = URI + "_by_pattern";
		String LUID = V.LUID;
		String FEGRID = V.FEGRID;
		String FETYPE = V.FETYPE;
		String GROUPREALIZATION = V.GROUPREALIZATION;
		String GROUPREALIZATIONS = V.A_GROUPREALIZATIONS;
		String GF = V.GF;
		String PT = V.PT;
		String TOTAL = V.TOTAL;
		String TOTALS = V.TOTALS;
		String PATTERNID = V.PATTERNID;
		String VUID = V.PATTERNID;
	}

	public interface Patterns_Sentences
	{
		String URI = "patterns_sentences";
		String PATTERNID = V.PATTERNID;
		String ANNOSETID = V.ANNOSETID;
		String SENTENCEID = V.SENTENCEID;
		String TEXT = V.TEXT;
	}

	public interface ValenceUnits_Sentences
	{
		String URI = "valenceunits_sentences";
		String VUID = V.VUID;
		String ANNOSETID = V.ANNOSETID;
		String SENTENCEID = V.SENTENCEID;
		String TEXT = V.TEXT;
	}

	public interface Governors_AnnoSets_Sentences
	{
		String URI = "governors_annosets_sentences";
		String GOVERNORID = V.GOVERNORID;
		String ANNOSETID = V.ANNOSETID;
		String SENTENCEID = V.SENTENCEID;
		String TEXT = V.TEXT;
	}

	public interface Lookup_FTS_FnWords
	{
		String TABLE = "fn_words_word_fts4";
		String URI = TABLE;
		String FNWORDID = V.FNWORDID;
		String WORDID = V.WORDID;
		String WORD = V.WORD;
	}

	public interface Lookup_FTS_FnSentences
	{
		String TABLE = "fn_sentences_text_fts4";
		String URI = TABLE;
		String SENTENCEID = V.SENTENCEID;
		String TEXT = V.TEXT;
		String FRAMEID = V.FRAMEID;
		String LUID = V.LUID;
		String ANNOSETID = V.ANNOSETID;
	}

	public interface Lookup_FTS_FnSentences_X
	{
		String URI = "fn_sentences_text_x_fts4";
		String URI_BY_SENTENCE = "fn_sentences_text_x_by_sentence_fts4";
		String SENTENCEID = V.SENTENCEID;
		String TEXT = V.TEXT;
		String FRAMEID = V.FRAMEID;
		String FRAME = V.FRAME;
		String FRAMES = V.A_FRAMES;
		String LUID = V.LUID;
		String LEXUNIT = V.LEXUNIT;
		String LEXUNITS = V.A_LEXUNITS;
		String ANNOSETID = V.ANNOSETID;
		String ANNOSETS = V.A_ANNOSETS;
	}

	public interface Suggest_FnWords
	{
		String SEARCH_WORD_PATH = "suggest_fnword";
		String URI = Suggest_FnWords.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
		String FNWORDID = V.FNWORDID;
		String WORDID = V.WORDID;
		String WORD = V.WORD;
	}

	public interface Suggest_FTS_FnWords
	{
		String SEARCH_WORD_PATH = "suggest_fts_fnword";
		String URI = Suggest_FTS_FnWords.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
		String FNWORDID = V.FNWORDID;
		String WORDID = V.WORDID;
		String WORD = V.WORD;
	}
}
