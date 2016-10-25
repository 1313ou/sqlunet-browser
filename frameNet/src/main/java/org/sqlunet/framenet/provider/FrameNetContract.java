package org.sqlunet.framenet.provider;

/**
 * FrameNet provider contract
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FrameNetContract
{
	static public final String AUTHORITY = "org.sqlunet.framenet.provider"; //

	// A L I A S E S

	static public final String SRC = "s";
	static public final String DEST = "d";
	static public final String POS = "p";
	static public final String FRAME = "f";
	static public final String LU = "u";
	static public final String FE = "e";
	static public final String FETYPE = "t";
	static public final String SENTENCE = "s";
	static public final String ANNOSET = "a";

	@SuppressWarnings("unused")
	static public final class LexUnits
	{
		static public final String TABLE = "fnlexunits"; //
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + LexUnits.TABLE; //
		public static final String LUID = "luid"; //
		public static final String CONTENTS = "luid"; //
		public static final String LEXUNIT = "lexunit"; //
		public static final String LUDEFINITION = "ludefinition"; //
		public static final String LUDICT = "ludict"; //
		public static final String FRAMEID = "frameid"; //
	}

	@SuppressWarnings("unused")
	static public final class LexUnits_X
	{
		static public final String TABLE_BY_LEXUNIT = "fnlexunits_x_by_lexunit"; //
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + LexUnits_X.TABLE_BY_LEXUNIT; //
		public static final String LUID = "luid"; //
		public static final String CONTENTS = "luid"; //
		public static final String LEXUNIT = "lexunit"; //
		public static final String LUDEFINITION = "ludefinition"; //
		public static final String LUDICT = "ludict"; //
		public static final String POSID = "posid"; //
		public static final String FRAMEID = "frameid"; //
		public static final String FRAME = "frame"; //
		public static final String FRAMEDEFINITION = "framedefinition"; //
		public static final String INCORPORATEDFETYPE = "fetype"; //
		public static final String INCORPORATEDFEDEFINITION = "fedefinition"; //
		public static final String INCORPORATEDFECORESET = "coreset"; //
	}

	@SuppressWarnings("unused")
	static public final class Frames
	{
		static public final String TABLE = "fnframes"; //
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + Frames.TABLE; //
		public static final String FRAMEID = "frameid"; //
		public static final String CONTENTS = "frameid"; //
	}

	@SuppressWarnings("unused")
	static public final class Frames_X
	{
		static public final String TABLE_BY_FRAME = "fnframes_x_by_frame"; //
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + Frames_X.TABLE_BY_FRAME; //
		public static final String FRAMEID = "frameid"; //
		public static final String CONTENTS = "frameid"; //
		public static final String FRAME = "frame"; //
		public static final String FRAMEDEFINITION = "framedefinition"; //
		public static final String SEMTYPE = "semtype"; //
		public static final String SEMTYPEABBREV = "semtypeabbrev"; //
		public static final String SEMTYPEDEFINITION = "semtypedefinition"; //
	}

	static public final class Frames_Related
	{
		static public final String TABLE = "fnframes_related"; //
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + Frames_Related.TABLE; //
		public static final String FRAMEID = "frameid"; //
		public static final String FRAME = "frame"; //
		public static final String FRAME2ID = "frameid"; //
		public static final String FRAME2 = "frame"; //
		public static final String RELATIONID = "relationid"; //
		public static final String RELATION = "relation"; //
	}

	static public final class Sentences
	{
		static public final String TABLE = "fnsentences"; //
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + Sentences.TABLE; //
		public static final String SENTENCEID = "sentenceid"; //
		public static final String TEXT = "text"; //
	}

	@SuppressWarnings("unused")
	static public final class AnnoSets
	{
		static public final String TABLE = "fnannosets"; //
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + AnnoSets.TABLE; //
		public static final String ANNOSETID = "annosetid"; //
		public static final String CONTENTS = "annosetid"; //
	}

	@SuppressWarnings("unused")
	static public final class Sentences_Layers_X
	{
		static public final String TABLE = "fnsentences_fnlayers_x"; //
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + Sentences_Layers_X.TABLE; //
		public static final String SENTENCEID = "sentenceid"; //
		public static final String ANNOSETID = "annosetid"; //
		public static final String LAYERID = "layerid"; //
		public static final String LAYERTYPE = "layertype"; //
		public static final String LAYERANNOTATIONS = "annotations"; //
		public static final String RANK = "rank"; //
		public static final String START = "start"; //
		public static final String END = "end"; //
		public static final String LABELTYPE = "labeltype"; //
		public static final String LABELITYPE = "labelitype"; //
		public static final String BGCOLOR = "bgcolor"; //
		public static final String FGCOLOR = "fgcolor"; //
	}

	@SuppressWarnings("unused")
	static public final class AnnoSets_Layers_X
	{
		static public final String TABLE = "fnannosets_fnlayers_x"; //
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + AnnoSets_Layers_X.TABLE; //
		public static final String ANNOSETID = "annosetid"; //
		public static final String SENTENCEID = "sentenceid"; //
		public static final String SENTENCETEXT = "text"; //
		public static final String LAYERID = "layerid"; //
		public static final String LAYERTYPE = "layertype"; //
		public static final String LAYERANNOTATIONS = "annotations"; //
		public static final String RANK = "rank"; //
		public static final String START = "start"; //
		public static final String END = "end"; //
		public static final String LABELTYPE = "labeltype"; //
		public static final String LABELITYPE = "labelitype"; //
		public static final String BGCOLOR = "bgcolor"; //
		public static final String FGCOLOR = "fgcolor"; //
	}

	@SuppressWarnings("unused")
	static public final class Patterns_Layers_X
	{
		static public final String TABLE = "fnpatterns_fnlayers_x"; //
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + Patterns_Layers_X.TABLE; //
		public static final String ANNOSETID = "annosetid"; //
		public static final String SENTENCEID = "sentenceid"; //
		public static final String SENTENCETEXT = "text"; //
		public static final String LAYERID = "layerid"; //
		public static final String LAYERTYPE = "layertype"; //
		public static final String LAYERANNOTATIONS = "annotations"; //
		public static final String RANK = "rank"; //
		public static final String START = "start"; //
		public static final String END = "end"; //
		public static final String LABELTYPE = "labeltype"; //
		public static final String LABELITYPE = "labelitype"; //
		public static final String BGCOLOR = "bgcolor"; //
		public static final String FGCOLOR = "fgcolor"; //
	}

	@SuppressWarnings("unused")
	static public final class ValenceUnits_Layers_X
	{
		static public final String TABLE = "fnvalenceunits_fnlayers_x"; //
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + ValenceUnits_Layers_X.TABLE; //
		public static final String ANNOSETID = "annosetid"; //
		public static final String SENTENCEID = "sentenceid"; //
		public static final String SENTENCETEXT = "text"; //
		public static final String LAYERID = "layerid"; //
		public static final String LAYERTYPE = "layertype"; //
		public static final String LAYERANNOTATIONS = "annotations"; //
		public static final String RANK = "rank"; //
		public static final String START = "start"; //
		public static final String END = "end"; //
		public static final String LABELTYPE = "labeltype"; //
		public static final String LABELITYPE = "labelitype"; //
		public static final String BGCOLOR = "bgcolor"; //
		public static final String FGCOLOR = "fgcolor"; //
	}

	@SuppressWarnings("unused")
	static public final class Words_LexUnits_Frames
	{
		static public final String TABLE = "words_fnlexunits"; //
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + Words_LexUnits_Frames.TABLE; //
		public static final String WORDID = "wordid"; //
		public static final String LUID = "luid"; //
		public static final String LEXUNIT = "lexunit"; //
		public static final String LUDEFINITION = "ludefinition"; //
		public static final String LUDICT = "ludict"; //
		public static final String POSID = "posid"; //
		public static final String FRAMEID = "frameid"; //
		public static final String FRAME = "frame"; //
		public static final String FRAMEDEFINITION = "framedefinition"; //
		public static final String SEMTYPE = "semtype"; //
		public static final String SEMTYPEABBREV = "semtypeabbrev"; //
		public static final String SEMTYPEDEFINITION = "semtypedefinition"; //
		public static final String INCORPORATEDFETYPE = "fetype"; //
		public static final String INCORPORATEDFEDEFINITION = "fedefinition"; //
	}

	@SuppressWarnings("unused")
	static public final class Frames_FEs
	{
		static public final String TABLE = "fnframes_fnfes"; //
		static public final String TABLE_BY_FE = TABLE + "/fe"; //
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + Frames_FEs.TABLE; //
		static public final String CONTENT_URI_BY_FE = "content://" + FrameNetContract.AUTHORITY + '/' + Frames_FEs.TABLE_BY_FE; //
		public static final String FRAMEID = "frameid"; //
		public static final String FETYPEID = "fetypeid"; //
		public static final String FETYPE = "fetype"; //
		public static final String FEABBREV = "feabbrev"; //
		public static final String FEDEFINITION = "fedefinition"; //
		public static final String SEMTYPE = "semtype"; //
		public static final String SEMTYPES = "semtypes"; //
		public static final String CORESET = "coreset"; //
		public static final String CORETYPE = "coretype"; //
		public static final String CORETYPEID = "coretypeid"; //
	}

	@SuppressWarnings("unused")
	static public final class LexUnits_Sentences
	{
		static public final String TABLE = "fnframes_fnsentences"; //
		static public final String TABLE_BY_SENTENCE = TABLE + "/sentence"; //
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + LexUnits_Sentences.TABLE; //
		static public final String CONTENT_URI_BY_SENTENCE = "content://" + FrameNetContract.AUTHORITY + '/' + LexUnits_Sentences.TABLE_BY_SENTENCE; //
		public static final String LUID = "luid"; //
		public static final String FRAMEID = "frameid"; //
		public static final String SENTENCEID = "sentenceid"; //
		public static final String TEXT = "text"; //
		public static final String CORPUSID = "corpusid"; //
		public static final String DOCUMENTID = "documentid"; //
		public static final String PARAGNO = "paragno"; //
		public static final String SENTNO = "sentno"; //
	}

	@SuppressWarnings("unused")
	static public final class LexUnits_Sentences_AnnoSets_Layers_Labels
	{
		static public final String TABLE = "fnlexunits_fnsentences_fnannosets_fnlayers_fnlabels"; //
		static public final String TABLE_BY_SENTENCE = TABLE + "/sentence"; //
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + LexUnits_Sentences_AnnoSets_Layers_Labels.TABLE; //
		static public final String CONTENT_URI_BY_SENTENCE = "content://" + FrameNetContract.AUTHORITY + '/' + LexUnits_Sentences_AnnoSets_Layers_Labels.TABLE_BY_SENTENCE; //
		public static final String LUID = "luid"; //
		public static final String FRAMEID = "frameid"; //
		public static final String SENTENCEID = "sentenceid"; //
		public static final String TEXT = "text"; //
		public static final String ANNOSETID = "annosetid"; //
		public static final String LAYERID = "layerid"; //
		public static final String LAYERTYPE = "layertype"; //
		public static final String LAYERANNOTATION = "annotations"; //
		public static final String RANK = "rank"; //
		public static final String CORPUSID = "corpusid"; //
		public static final String DOCUMENTID = "documentid"; //
		public static final String PARAGNO = "paragno"; //
		public static final String SENTNO = "sentno"; //
		public static final String START = "start"; //
		public static final String END = "end"; //
		public static final String LABELTYPE = "labeltype"; //
		public static final String LABELITYPE = "labelitype"; //
		public static final String BGCOLOR = "bgcolor"; //
		public static final String FGCOLOR = "fgcolor"; //
	}

	static public final class LexUnits_Governors
	{
		static public final String TABLE = "fnlexunits_fngovernors"; //
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + LexUnits_Governors.TABLE; //
		public static final String LUID = "luid"; //
		public static final String GOVERNORID = "governorid"; //
		public static final String GOVERNORTYPE = "governortype"; //
		public static final String FNWORDID = "fnwordid"; //
		public static final String FNWORD = "word"; //
	}

	@SuppressWarnings("unused")
	static public final class LexUnits_FERealizations_ValenceUnits
	{
		static public final String TABLE = "fnlexunits_fnferealizations_fnvalenceunits"; //
		static public final String TABLE_BY_REALIZATION = TABLE + "/realization"; //
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + LexUnits_FERealizations_ValenceUnits.TABLE; //
		static public final String CONTENT_URI_BY_REALIZATION = "content://" + FrameNetContract.AUTHORITY + '/' + LexUnits_FERealizations_ValenceUnits.TABLE_BY_REALIZATION; //
		public static final String LUID = "luid"; //
		public static final String FERID = "ferid"; //
		public static final String FETYPE = "fetype"; //
		public static final String PT = "pt"; //
		public static final String GF = "gf"; //
		public static final String TOTAL = "total"; //
		public static final String VUID = "vuid"; //
		public static final String FERS = "fers"; //
	}

	@SuppressWarnings("unused")
	static public final class LexUnits_FEGroupRealizations_Patterns_ValenceUnits
	{
		static public final String TABLE = "fnlexunits_fnferealizations_fnpatterns_fnvalenceunits"; //
		static public final String TABLE_BY_PATTERN = TABLE + "/pattern"; //
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE; //
		static public final String CONTENT_URI_BY_PATTERN = "content://" + FrameNetContract.AUTHORITY + '/' + LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE_BY_PATTERN; //
		public static final String LUID = "luid"; //
		public static final String FEGRID = "fegrid"; //
		public static final String FETYPE = "fetype"; //
		public static final String GROUPREALIZATION = "grouprealization"; //
		public static final String GROUPREALIZATIONS = "grouprealizations"; //
		public static final String GF = "gf"; //
		public static final String PT = "pt"; //
		public static final String TOTAL = "total"; //
		public static final String TOTALS = "totals"; //
		public static final String PATTERNID = "patternid"; //
		public static final String VUID = "patternid"; //
	}

	static public final class Patterns_Sentences
	{
		static public final String TABLE = "fnpatterns_annosets"; //
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + Patterns_Sentences.TABLE; //
		public static final String PATTERNID = "patternid"; //
		public static final String ANNOSETID = "annosetid"; //
		public static final String SENTENCEID = "sentenceid"; //
		public static final String TEXT = "text"; //
	}

	static public final class ValenceUnits_Sentences
	{
		static public final String TABLE = "fnvalenceunits_annosets"; //
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + ValenceUnits_Sentences.TABLE; //
		public static final String VUID = "vuid"; //
		public static final String ANNOSETID = "annosetid"; //
		public static final String SENTENCEID = "sentenceid"; //
		public static final String TEXT = "text"; //
	}

	static public final class Governors_AnnoSets_Sentences
	{
		static public final String TABLE = "fngovernors_annosets_sentences"; //
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + Governors_AnnoSets_Sentences.TABLE; //
		public static final String GOVERNORID = "governorid"; //
		public static final String ANNOSETID = "annosetid"; //
		public static final String SENTENCEID = "sentenceid"; //
		public static final String TEXT = "text"; //
	}

	@SuppressWarnings("unused")
	static public final class Lookup_FnSentences
	{
		static public final String TABLE = "fts_fnsentences"; //
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + Lookup_FnSentences.TABLE; //
		static public final String SENTENCEID = "sentenceid"; //
		static public final String ANNOSETID = "annosetid"; //
		static public final String FRAMEID = "frameid"; //
		static public final String LUID = "luid"; //
		static public final String TEXT = "text"; //
	}
}
