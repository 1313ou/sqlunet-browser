/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.provider

import android.app.SearchManager

/**
 * FrameNet provider contract
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object FrameNetContract {

    // A L I A S E S

    const val AS_FRAMES = V.AS_FRAMES
    const val AS_RELATED_FRAMES = V.AS_RELATED_FRAMES
    const val AS_SRC_FRAMES = V.SRC_FRAME
    const val AS_DEST_FRAMES = V.DEST_FRAME
    const val AS_LEXUNITS = V.AS_LEXUNITS
    const val AS_FES = V.AS_FES
    const val AS_FETYPES = V.AS_FETYPES
    const val AS_SENTENCES = V.AS_SENTENCES
    const val AS_ANNOSETS = V.AS_ANNOSETS
    const val AS_POSES = V.AS_POSES

    object Words {
        const val TABLE = Q.WORDS.TABLE
        const val URI = TABLE
        const val FNWORDID = V.FNWORDID
        const val WORDID = V.WORDID
        // const val WORD = V.WORD
    }

    object LexUnits {
        const val TABLE = Q.LEXUNITS.TABLE
        const val URI = TABLE
        const val URI1 = "lexunit"
        const val LUID = V.LUID
        const val CONTENTS = V.LUID
        const val LEXUNIT = V.LEXUNIT
        const val LUDEFINITION = V.LUDEFINITION
        const val LUDICT = V.LUDICT
        const val FRAMEID = V.FRAMEID
    }

    object LexUnits_X {
        const val URI = "lexunits_x"
        const val URI_BY_LEXUNIT = URI + "_by_lexunit"
        const val LUID = V.LUID
        const val CONTENTS = V.LUID
        const val LEXUNIT = V.LEXUNIT
        const val LUDEFINITION = V.LUDEFINITION
        const val LUDICT = V.LUDICT
        const val POSID = V.POSID
        const val FRAMEID = V.FRAMEID
        const val FRAME = V.FRAME
        const val FRAMEDEFINITION = V.FRAMEDEFINITION
        const val INCORPORATEDFETYPE = V.FETYPE
        const val INCORPORATEDFEDEFINITION = V.FEDEFINITION
        const val INCORPORATEDFECORESET = V.CORESET
    }

    object LexUnits_or_Frames {
        const val URI = "lexunits_or_frame"
        const val URI_FN = URI + "_fn"
        const val ID = V._ID
        const val FNID = V.FNID
        const val FNWORDID = V.FNWORDID
        const val WORDID = V.WORDID
        const val WORD = V.WORD
        const val NAME = V.NAME
        const val FRAMENAME = V.FRAME
        const val FRAMEID = V.FRAMEID
        const val ISFRAME = V.ISFRAME
    }

    object Frames {
        const val TABLE = Q.FRAMES.TABLE
        const val URI = TABLE
        const val URI1 = "frame"
        const val FRAMEID = V.FRAMEID
        const val CONTENTS = V.FRAMEID
    }

    object Frames_X {
        const val URI = "frames_x"
        const val URI_BY_FRAME = URI + "_by_frame"
        const val FRAMEID = V.FRAMEID
        const val CONTENTS = V.FRAMEID
        const val FRAME = V.FRAME
        const val FRAMEDEFINITION = V.FRAMEDEFINITION
        const val SEMTYPE = V.SEMTYPE
        const val SEMTYPEABBREV = V.SEMTYPEABBREV
        const val SEMTYPEDEFINITION = V.SEMTYPEDEFINITION
    }

    object Frames_Related {
        const val URI = "frames_related"
        const val FRAMEID = V.FRAMEID
        const val FRAME = V.FRAME
        const val FRAME2ID = V.FRAME2ID
        const val RELATIONID = V.RELATIONID
        const val RELATION = V.RELATION
    }

    object Sentences {
        const val TABLE = Q.SENTENCES.TABLE
        const val URI = TABLE
        const val URI1 = "sentence"
        const val SENTENCEID = V.SENTENCEID
        const val TEXT = V.TEXT
    }

    object AnnoSets {
        const val TABLE = Q.ANNOSETS.TABLE
        const val URI = TABLE
        const val URI1 = "annoset"
        const val ANNOSETID = V.ANNOSETID
        const val CONTENTS = V.ANNOSETID
    }

    object Sentences_Layers_X {
        const val URI = "sentences_layers_x"
        const val SENTENCEID = V.SENTENCEID
        const val ANNOSETID = V.ANNOSETID
        const val LAYERID = V.LAYERID
        const val LAYERTYPE = V.LAYERTYPE
        const val LAYERANNOTATIONS = V.ANNOTATIONS
        const val RANK = V.RANK
        const val START = V.START
        const val END = V.END
        const val LABELTYPE = V.LABELTYPE
        const val LABELITYPE = V.LABELITYPE
        const val BGCOLOR = V.BGCOLOR
        const val FGCOLOR = V.FGCOLOR
    }

    object AnnoSets_Layers_X {
        const val URI = "annosets_layers_x"
        const val ANNOSETID = V.ANNOSETID
        const val SENTENCEID = V.SENTENCEID
        const val SENTENCETEXT = V.TEXT
        const val LAYERID = V.LAYERID
        const val LAYERTYPE = V.LAYERTYPE
        const val LAYERANNOTATIONS = V.ANNOTATIONS
        const val RANK = V.RANK
        const val START = V.START
        const val END = V.END
        const val LABELTYPE = V.LABELTYPE
        const val LABELITYPE = V.LABELITYPE
        const val BGCOLOR = V.BGCOLOR
        const val FGCOLOR = V.FGCOLOR
    }

    object Patterns_Layers_X {
        const val URI = "patterns_layers_x"
        const val ANNOSETID = V.ANNOSETID
        const val SENTENCEID = V.SENTENCEID
        const val SENTENCETEXT = V.TEXT
        const val LAYERID = V.LAYERID
        const val LAYERTYPE = V.LAYERTYPE
        const val LAYERANNOTATIONS = V.ANNOTATIONS
        const val RANK = V.RANK
        const val START = V.START
        const val END = V.END
        const val LABELTYPE = V.LABELTYPE
        const val LABELITYPE = V.LABELITYPE
        const val BGCOLOR = V.BGCOLOR
        const val FGCOLOR = V.FGCOLOR
    }

    object ValenceUnits_Layers_X {
        const val URI = "valenceunits_layers_x"
        const val ANNOSETID = V.ANNOSETID
        const val SENTENCEID = V.SENTENCEID
        const val SENTENCETEXT = V.TEXT
        const val LAYERID = V.LAYERID
        const val LAYERTYPE = V.LAYERTYPE
        const val LAYERANNOTATIONS = V.ANNOTATIONS
        const val RANK = V.RANK
        const val START = V.START
        const val END = V.END
        const val LABELTYPE = V.LABELTYPE
        const val LABELITYPE = V.LABELITYPE
        const val BGCOLOR = V.BGCOLOR
        const val FGCOLOR = V.FGCOLOR
    }

    object Words_LexUnits_Frames {
        const val URI = "words_lexunits"
        const val URI_FN = URI + "_fn"
        const val WORDID = V.WORDID
        const val LUID = V.LUID
        const val LEXUNIT = V.LEXUNIT
        const val LUDEFINITION = V.LUDEFINITION
        const val LUDICT = V.LUDICT
        const val POS = V.POS
        const val POSID = V.POSID
        const val FRAMEID = V.FRAMEID
        const val FRAME = V.FRAME
        const val FRAMEDEFINITION = V.FRAMEDEFINITION
        const val SEMTYPE = V.SEMTYPE
        const val SEMTYPEABBREV = V.SEMTYPEABBREV
        const val SEMTYPEDEFINITION = V.SEMTYPEDEFINITION
        const val INCORPORATEDFETYPE = V.FETYPE
        const val INCORPORATEDFEDEFINITION = V.FEDEFINITION
    }

    object Frames_FEs {
        const val URI = "frames_fes"
        const val URI_BY_FE = URI + "_by_fe"
        const val FRAMEID = V.FRAMEID
        const val FETYPEID = V.FETYPEID
        const val FETYPE = V.FETYPE
        const val FEABBREV = V.FEABBREV
        const val FEDEFINITION = V.FEDEFINITION
        const val SEMTYPE = V.SEMTYPE
        const val SEMTYPES = V.SEMTYPES
        const val CORESET = V.CORESET
        const val CORETYPE = V.CORETYPE
        const val CORETYPEID = V.CORETYPEID
    }

    object LexUnits_Sentences {
        const val URI = "frames_sentences"
        const val URI_BY_SENTENCE = URI + "_by_sentence"
        const val LUID = V.LUID
        const val FRAMEID = V.FRAMEID
        const val SENTENCEID = V.SENTENCEID
        const val TEXT = V.TEXT
        const val CORPUSID = V.CORPUSID
        const val DOCUMENTID = V.DOCUMENTID
        const val PARAGNO = V.PARAGNO
        const val SENTNO = V.SENTNO
    }

    object LexUnits_Sentences_AnnoSets_Layers_Labels {
        const val URI = "lexunits_sentences_annosets_layers_labels"
        const val URI_BY_SENTENCE = URI + "_by_sentence"
        const val LUID = V.LUID
        const val FRAMEID = V.FRAMEID
        const val SENTENCEID = V.SENTENCEID
        const val TEXT = V.TEXT
        const val ANNOSETID = V.ANNOSETID
        const val LAYERID = V.LAYERID
        const val LAYERTYPE = V.LAYERTYPE
        const val LAYERANNOTATION = V.ANNOTATIONS
        const val RANK = V.RANK
        const val CORPUSID = V.CORPUSID
        const val DOCUMENTID = V.DOCUMENTID
        const val PARAGNO = V.PARAGNO
        const val SENTNO = V.SENTNO
        const val START = V.START
        const val END = V.END
        const val LABELTYPE = V.LABELTYPE
        const val LABELITYPE = V.LABELITYPE
        const val BGCOLOR = V.BGCOLOR
        const val FGCOLOR = V.FGCOLOR
    }

    object LexUnits_Governors {
        const val URI = "lexunits_governors"
        const val URI_FN = URI + "_fn"
        const val LUID = V.LUID
        const val GOVERNORID = V.GOVERNORID
        const val GOVERNORTYPE = V.GOVERNORTYPE
        const val FNWORDID = V.FNWORDID
        const val WORD = V.WORD
    }

    object LexUnits_FERealizations_ValenceUnits {
        const val URI = "lexunits_ferealizations_valenceunits"
        const val URI_BY_REALIZATION = URI + "_by_realization"
        const val LUID = V.LUID
        const val FERID = V.FERID
        const val FETYPE = V.FETYPE
        const val PT = V.PT
        const val GF = V.GF
        const val TOTAL = V.TOTAL
        const val VUID = V.VUID
        const val FERS = V.FERS
    }

    object LexUnits_FEGroupRealizations_Patterns_ValenceUnits {
        const val URI = "lexunits_fegrouprealizations_patterns_valenceunits"
        const val URI_BY_PATTERN = URI + "_by_pattern"
        const val LUID = V.LUID
        const val FEGRID = V.FEGRID
        const val FETYPE = V.FETYPE
        const val GROUPREALIZATION = V.GROUPREALIZATION
        const val GROUPREALIZATIONS = V.A_GROUPREALIZATIONS
        const val GF = V.GF
        const val PT = V.PT
        const val TOTAL = V.TOTAL
        const val TOTALS = V.TOTALS
        const val PATTERNID = V.PATTERNID
        const val VUID = V.PATTERNID
    }

    object Patterns_Sentences {
        const val URI = "patterns_sentences"
        const val PATTERNID = V.PATTERNID
        const val ANNOSETID = V.ANNOSETID
        const val SENTENCEID = V.SENTENCEID
        const val TEXT = V.TEXT
    }

    object ValenceUnits_Sentences {
        const val URI = "valenceunits_sentences"
        const val VUID = V.VUID
        const val ANNOSETID = V.ANNOSETID
        const val SENTENCEID = V.SENTENCEID
        const val TEXT = V.TEXT
    }

    object Governors_AnnoSets_Sentences {
        const val URI = "governors_annosets_sentences"
        const val GOVERNORID = V.GOVERNORID
        const val ANNOSETID = V.ANNOSETID
        const val SENTENCEID = V.SENTENCEID
        const val TEXT = V.TEXT
    }

    object Lookup_FTS_FnWords {
        const val TABLE = "fn_words_word_fts4"
        const val URI = TABLE
        const val FNWORDID = V.FNWORDID
        const val WORDID = V.WORDID
        const val WORD = V.WORD
    }

    object Lookup_FTS_FnSentences {
        const val TABLE = "fn_sentences_text_fts4"
        const val URI = TABLE
        const val SENTENCEID = V.SENTENCEID
        const val TEXT = V.TEXT
        const val FRAMEID = V.FRAMEID
        const val LUID = V.LUID
        const val ANNOSETID = V.ANNOSETID
    }

    object Lookup_FTS_FnSentences_X {
        const val URI = "fn_sentences_text_x_fts4"
        const val URI_BY_SENTENCE = "fn_sentences_text_x_by_sentence_fts4"
        const val SENTENCEID = V.SENTENCEID
        const val TEXT = V.TEXT
        const val FRAMEID = V.FRAMEID
        const val FRAME = V.FRAME
        const val FRAMES = V.A_FRAMES
        const val LUID = V.LUID
        const val LEXUNIT = V.LEXUNIT
        const val LEXUNITS = V.A_LEXUNITS
        const val ANNOSETID = V.ANNOSETID
        const val ANNOSETS = V.A_ANNOSETS
    }

    object Suggest_FnWords {
        const val SEARCH_WORD_PATH = "suggest_fnword"
        const val URI = SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY
        const val FNWORDID = V.FNWORDID
        const val WORDID = V.WORDID
        const val WORD = V.WORD
    }

    object Suggest_FTS_FnWords {
        const val SEARCH_WORD_PATH = "suggest_fts_fnword"
        const val URI = SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY
        const val FNWORDID = V.FNWORDID
        const val WORDID = V.WORDID
        const val WORD = V.WORD
    }
}
