package org.sqlunet.framenet.provider;

public class FrameNetContract
{
	static public final String AUTHORITY = "org.sqlunet.framenet.provider"; //$NON-NLS-1$

	@SuppressWarnings("unused")
	static public final class LexUnits
	{
		static public final String TABLE = "fnlexunits"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + LexUnits.TABLE; //$NON-NLS-1$
		public static final String LUID = "luId"; //$NON-NLS-1$
		public static final String CONTENTS = "luId"; //$NON-NLS-1$
		public static final String LEXUNIT = "lexunit"; //$NON-NLS-1$
		public static final String LUDEFINITION = "ludefinition"; //$NON-NLS-1$
		public static final String LUDICT = "ludict"; //$NON-NLS-1$
		public static final String FRAMEID = "frameId"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class LexUnits_X
	{
		static public final String TABLE_BY_LEXUNIT = "fnlexunits_x_by_lexunit"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + LexUnits_X.TABLE_BY_LEXUNIT; //$NON-NLS-1$
		public static final String LUID = "luId"; //$NON-NLS-1$
		public static final String CONTENTS = "luId"; //$NON-NLS-1$
		public static final String LEXUNIT = "lexunit"; //$NON-NLS-1$
		public static final String LUDEFINITION = "ludefinition"; //$NON-NLS-1$
		public static final String LUDICT = "ludict"; //$NON-NLS-1$
		public static final String POSID = "posid"; //$NON-NLS-1$
		public static final String FRAMEID = "frameId"; //$NON-NLS-1$
		public static final String FRAME = "frame"; //$NON-NLS-1$
		public static final String FRAMEDEFINITION = "framedefinition"; //$NON-NLS-1$
		public static final String INCORPORATEDFETYPE = "fetype"; //$NON-NLS-1$
		public static final String INCORPORATEDFEDEFINITION = "fedefinition"; //$NON-NLS-1$
		public static final String INCORPORATEDFECORESET = "coreset"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class Frames
	{
		static public final String TABLE = "fnframes"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + Frames.TABLE; //$NON-NLS-1$
		public static final String FRAMEID = "frameId"; //$NON-NLS-1$
		public static final String CONTENTS = "frameId"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class Frames_X
	{
		static public final String TABLE_BY_FRAME = "fnframes_x_by_frame"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + Frames_X.TABLE_BY_FRAME; //$NON-NLS-1$
		public static final String FRAMEID = "frameId"; //$NON-NLS-1$
		public static final String CONTENTS = "frameId"; //$NON-NLS-1$
		public static final String FRAME = "frame"; //$NON-NLS-1$
		public static final String FRAMEDEFINITION = "framedefinition"; //$NON-NLS-1$
		public static final String SEMTYPE = "semtype"; //$NON-NLS-1$
		public static final String SEMTYPEABBREV = "semtypeabbrev"; //$NON-NLS-1$
		public static final String SEMTYPEDEFINITION = "semtypedefinition"; //$NON-NLS-1$
	}

	static public final class Frames_Related
	{
		static public final String TABLE = "fnframes_related"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + Frames_Related.TABLE; //$NON-NLS-1$
		public static final String FRAMEID = "s.frameId"; //$NON-NLS-1$
		public static final String FRAME = "s.frame"; //$NON-NLS-1$
		public static final String FRAME2ID = "d.frameId"; //$NON-NLS-1$
		public static final String FRAME2 = "d.frame"; //$NON-NLS-1$
		public static final String RELATIONID = "relationid"; //$NON-NLS-1$
		public static final String RELATION = "relation"; //$NON-NLS-1$
	}

	static public final class Sentences
	{
		static public final String TABLE = "fnsentences"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + Sentences.TABLE; //$NON-NLS-1$
		public static final String SENTENCEID = "sentenceid"; //$NON-NLS-1$
		public static final String TEXT = "text"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class AnnoSets
	{
		static public final String TABLE = "fnannosets"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + AnnoSets.TABLE; //$NON-NLS-1$
		public static final String ANNOSETID = "annoSetId"; //$NON-NLS-1$
		public static final String CONTENTS = "annoSetId"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class Sentences_Layers_X
	{
		static public final String TABLE = "fnsentences_fnlayers_x"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + Sentences_Layers_X.TABLE; //$NON-NLS-1$
		public static final String SENTENCEID = "sentenceid"; //$NON-NLS-1$
		public static final String ANNOSETID = "annoSetId"; //$NON-NLS-1$
		public static final String LAYERID = "layerid"; //$NON-NLS-1$
		public static final String LAYERTYPE = "layertype"; //$NON-NLS-1$
		public static final String LAYERANNOTATIONS = "annotations"; //$NON-NLS-1$
		public static final String RANK = "rank"; //$NON-NLS-1$
		public static final String START = "start"; //$NON-NLS-1$
		public static final String END = "end"; //$NON-NLS-1$
		public static final String LABELTYPE = "labeltype"; //$NON-NLS-1$
		public static final String LABELITYPE = "labelitype"; //$NON-NLS-1$
		public static final String BGCOLOR = "bgcolor"; //$NON-NLS-1$
		public static final String FGCOLOR = "fgcolor"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class AnnoSets_Layers_X
	{
		static public final String TABLE = "fnannosets_fnlayers_x"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + AnnoSets_Layers_X.TABLE; //$NON-NLS-1$
		public static final String ANNOSETID = "annoSetId"; //$NON-NLS-1$
		public static final String SENTENCEID = "sentenceid"; //$NON-NLS-1$
		public static final String SENTENCETEXT = "text"; //$NON-NLS-1$
		public static final String LAYERID = "layerid"; //$NON-NLS-1$
		public static final String LAYERTYPE = "layertype"; //$NON-NLS-1$
		public static final String LAYERANNOTATIONS = "annotations"; //$NON-NLS-1$
		public static final String RANK = "rank"; //$NON-NLS-1$
		public static final String START = "start"; //$NON-NLS-1$
		public static final String END = "end"; //$NON-NLS-1$
		public static final String LABELTYPE = "labeltype"; //$NON-NLS-1$
		public static final String LABELITYPE = "labelitype"; //$NON-NLS-1$
		public static final String BGCOLOR = "bgcolor"; //$NON-NLS-1$
		public static final String FGCOLOR = "fgcolor"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class Patterns_Layers_X
	{
		static public final String TABLE = "fnpatterns_fnlayers_x"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + Patterns_Layers_X.TABLE; //$NON-NLS-1$
		public static final String ANNOSETID = "annoSetId"; //$NON-NLS-1$
		public static final String SENTENCEID = "sentenceid"; //$NON-NLS-1$
		public static final String SENTENCETEXT = "text"; //$NON-NLS-1$
		public static final String LAYERID = "layerid"; //$NON-NLS-1$
		public static final String LAYERTYPE = "layertype"; //$NON-NLS-1$
		public static final String LAYERANNOTATIONS = "annotations"; //$NON-NLS-1$
		public static final String RANK = "rank"; //$NON-NLS-1$
		public static final String START = "start"; //$NON-NLS-1$
		public static final String END = "end"; //$NON-NLS-1$
		public static final String LABELTYPE = "labeltype"; //$NON-NLS-1$
		public static final String LABELITYPE = "labelitype"; //$NON-NLS-1$
		public static final String BGCOLOR = "bgcolor"; //$NON-NLS-1$
		public static final String FGCOLOR = "fgcolor"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class ValenceUnits_Layers_X
	{
		static public final String TABLE = "fnvalenceunits_fnlayers_x"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + ValenceUnits_Layers_X.TABLE; //$NON-NLS-1$
		public static final String ANNOSETID = "annoSetId"; //$NON-NLS-1$
		public static final String SENTENCEID = "sentenceid"; //$NON-NLS-1$
		public static final String SENTENCETEXT = "text"; //$NON-NLS-1$
		public static final String LAYERID = "layerid"; //$NON-NLS-1$
		public static final String LAYERTYPE = "layertype"; //$NON-NLS-1$
		public static final String LAYERANNOTATIONS = "annotations"; //$NON-NLS-1$
		public static final String RANK = "rank"; //$NON-NLS-1$
		public static final String START = "start"; //$NON-NLS-1$
		public static final String END = "end"; //$NON-NLS-1$
		public static final String LABELTYPE = "labeltype"; //$NON-NLS-1$
		public static final String LABELITYPE = "labelitype"; //$NON-NLS-1$
		public static final String BGCOLOR = "bgcolor"; //$NON-NLS-1$
		public static final String FGCOLOR = "fgcolor"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class Words_LexUnits_Frames
	{
		static public final String TABLE = "words_fnlexunits"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + Words_LexUnits_Frames.TABLE; //$NON-NLS-1$
		public static final String WORDID = "wordid"; //$NON-NLS-1$
		public static final String LUID = "luId"; //$NON-NLS-1$
		public static final String LEXUNIT = "lexunit"; //$NON-NLS-1$
		public static final String LUDEFINITION = "ludefinition"; //$NON-NLS-1$
		public static final String LUDICT = "ludict"; //$NON-NLS-1$
		public static final String POSID = "posid"; //$NON-NLS-1$
		public static final String FRAMEID = "frameId"; //$NON-NLS-1$
		public static final String FRAME = "frame"; //$NON-NLS-1$
		public static final String FRAMEDEFINITION = "framedefinition"; //$NON-NLS-1$
		public static final String SEMTYPE = "semtype"; //$NON-NLS-1$
		public static final String SEMTYPEABBREV = "semtypeabbrev"; //$NON-NLS-1$
		public static final String SEMTYPEDEFINITION = "semtypedefinition"; //$NON-NLS-1$
		public static final String INCORPORATEDFETYPE = "fetype"; //$NON-NLS-1$
		public static final String INCORPORATEDFEDEFINITION = "fedefinition"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class Frames_FEs
	{
		static public final String TABLE = "fnframes_fnfes"; //$NON-NLS-1$
		static public final String TABLE_BY_FE = TABLE + "/fe"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + Frames_FEs.TABLE; //$NON-NLS-1$
		static public final String CONTENT_URI_BY_FE = "content://" + FrameNetContract.AUTHORITY + '/' + Frames_FEs.TABLE_BY_FE; //$NON-NLS-1$
		public static final String FRAMEID = "frameId"; //$NON-NLS-1$
		public static final String FETYPEID = "fetypeid"; //$NON-NLS-1$
		public static final String FETYPE = "fetype"; //$NON-NLS-1$
		public static final String FEABBREV = "feabbrev"; //$NON-NLS-1$
		public static final String FEDEFINITION = "fedefinition"; //$NON-NLS-1$
		public static final String SEMTYPE = "semtype"; //$NON-NLS-1$
		public static final String SEMTYPES = "semtypes"; //$NON-NLS-1$
		public static final String CORESET = "coreset"; //$NON-NLS-1$
		public static final String CORETYPE = "coretype"; //$NON-NLS-1$
		public static final String CORETYPEID = "coretypeid"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class LexUnits_Sentences
	{
		static public final String TABLE = "fnframes_fnsentences"; //$NON-NLS-1$
		static public final String TABLE_BY_SENTENCE = TABLE + "/sentence"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + LexUnits_Sentences.TABLE; //$NON-NLS-1$
		static public final String CONTENT_URI_BY_SENTENCE = "content://" + FrameNetContract.AUTHORITY + '/' + LexUnits_Sentences.TABLE_BY_SENTENCE; //$NON-NLS-1$
		public static final String LUID = "luId"; //$NON-NLS-1$
		public static final String FRAMEID = "frameId"; //$NON-NLS-1$
		public static final String SENTENCEID = "sentenceid"; //$NON-NLS-1$
		public static final String TEXT = "text"; //$NON-NLS-1$
		public static final String CORPUSID = "corpusid"; //$NON-NLS-1$
		public static final String DOCUMENTID = "documentid"; //$NON-NLS-1$
		public static final String PARAGNO = "paragno"; //$NON-NLS-1$
		public static final String SENTNO = "sentno"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class LexUnits_Sentences_Annosets_Layers_Labels
	{
		static public final String TABLE = "fnlexunits_fnsentences_fnannosoets_fnlayers_fnlabels"; //$NON-NLS-1$
		static public final String TABLE_BY_SENTENCE = TABLE + "/sentence"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + LexUnits_Sentences_Annosets_Layers_Labels.TABLE; //$NON-NLS-1$
		static public final String CONTENT_URI_BY_SENTENCE = "content://" + FrameNetContract.AUTHORITY + '/' + LexUnits_Sentences_Annosets_Layers_Labels.TABLE_BY_SENTENCE; //$NON-NLS-1$
		public static final String LUID = "luId"; //$NON-NLS-1$
		public static final String FRAMEID = "frameId"; //$NON-NLS-1$
		public static final String SENTENCEID = "sentenceid"; //$NON-NLS-1$
		public static final String TEXT = "text"; //$NON-NLS-1$
		public static final String ANNOSETID = "annoSetId"; //$NON-NLS-1$
		public static final String LAYERID = "layerid"; //$NON-NLS-1$
		public static final String LAYERTYPE = "layertype"; //$NON-NLS-1$
		public static final String LAYERANNOTATION = "annotations"; //$NON-NLS-1$
		public static final String RANK = "rank"; //$NON-NLS-1$
		public static final String CORPUSID = "corpusid"; //$NON-NLS-1$
		public static final String DOCUMENTID = "documentid"; //$NON-NLS-1$
		public static final String PARAGNO = "paragno"; //$NON-NLS-1$
		public static final String SENTNO = "sentno"; //$NON-NLS-1$
		public static final String START = "start"; //$NON-NLS-1$
		public static final String END = "end"; //$NON-NLS-1$
		public static final String LABELTYPE = "labeltype"; //$NON-NLS-1$
		public static final String LABELITYPE = "labelitype"; //$NON-NLS-1$
		public static final String BGCOLOR = "bgcolor"; //$NON-NLS-1$
		public static final String FGCOLOR = "fgcolor"; //$NON-NLS-1$
	}

	static public final class LexUnits_Governors
	{
		static public final String TABLE = "fnlexunits_fngovernors"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + LexUnits_Governors.TABLE; //$NON-NLS-1$
		public static final String LUID = "luId"; //$NON-NLS-1$
		public static final String GOVERNORID = "governorid"; //$NON-NLS-1$
		public static final String GOVERNORTYPE = "governortype"; //$NON-NLS-1$
		public static final String FNWORDID = "fnwordid"; //$NON-NLS-1$
		public static final String FNWORD = "word"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class LexUnits_FERealizations_ValenceUnits
	{
		static public final String TABLE = "fnlexunits_fnferealizations_fnvalenceunits"; //$NON-NLS-1$
		static public final String TABLE_BY_REALIZATION = TABLE + "/realization"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + LexUnits_FERealizations_ValenceUnits.TABLE; //$NON-NLS-1$
		static public final String CONTENT_URI_BY_REALIZATION = "content://" + FrameNetContract.AUTHORITY + '/' + LexUnits_FERealizations_ValenceUnits.TABLE_BY_REALIZATION; //$NON-NLS-1$
		public static final String LUID = "luId"; //$NON-NLS-1$
		public static final String FERID = "ferid"; //$NON-NLS-1$
		public static final String FETYPE = "fetype"; //$NON-NLS-1$
		public static final String PT = "pt"; //$NON-NLS-1$
		public static final String GF = "gf"; //$NON-NLS-1$
		public static final String TOTAL = "total"; //$NON-NLS-1$
		public static final String VUID = "vuid"; //$NON-NLS-1$
		public static final String FERS = "fers"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class LexUnits_FEGroupRealizations_Patterns_ValenceUnits
	{
		static public final String TABLE = "fnlexunits_fnferealizations_fnpatterns_fnvalenceunits"; //$NON-NLS-1$
		static public final String TABLE_BY_PATTERN = TABLE + "/pattern"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE; //$NON-NLS-1$
		static public final String CONTENT_URI_BY_PATTERN = "content://" + FrameNetContract.AUTHORITY + '/' + LexUnits_FEGroupRealizations_Patterns_ValenceUnits.TABLE_BY_PATTERN; //$NON-NLS-1$
		public static final String LUID = "luId"; //$NON-NLS-1$
		public static final String FEGRID = "fegrid"; //$NON-NLS-1$
		public static final String FETYPE = "fetype"; //$NON-NLS-1$
		public static final String GROUPREALIZATION = "grouprealization"; //$NON-NLS-1$
		public static final String GROUPREALIZATIONS = "grouprealizations"; //$NON-NLS-1$
		public static final String GF = "gf"; //$NON-NLS-1$
		public static final String PT = "pt"; //$NON-NLS-1$
		public static final String TOTAL = "total"; //$NON-NLS-1$
		public static final String TOTALS = "totals"; //$NON-NLS-1$
		public static final String PATTERNID = "patternid"; //$NON-NLS-1$
		public static final String VUID = "patternid"; //$NON-NLS-1$
	}

	static public final class Patterns_Sentences
	{
		static public final String TABLE = "fnpatterns_annosets"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + Patterns_Sentences.TABLE; //$NON-NLS-1$
		public static final String PATTERNID = "patternid"; //$NON-NLS-1$
		public static final String ANNOSETID = "annoSetId"; //$NON-NLS-1$
		public static final String SENTENCEID = "sentenceid"; //$NON-NLS-1$
		public static final String TEXT = "text"; //$NON-NLS-1$
	}

	static public final class ValenceUnits_Sentences
	{
		static public final String TABLE = "fnvalenceunits_annosets"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + ValenceUnits_Sentences.TABLE; //$NON-NLS-1$
		public static final String VUID = "vuid"; //$NON-NLS-1$
		public static final String ANNOSETID = "annoSetId"; //$NON-NLS-1$
		public static final String SENTENCEID = "sentenceid"; //$NON-NLS-1$
		public static final String TEXT = "text"; //$NON-NLS-1$
	}

	static public final class Governors_AnnoSets_Sentences
	{
		static public final String TABLE = "fngovernors_annosets_sentences"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + Governors_AnnoSets_Sentences.TABLE; //$NON-NLS-1$
		public static final String GOVERNORID = "governorid"; //$NON-NLS-1$
		public static final String ANNOSETID = "annoSetId"; //$NON-NLS-1$
		public static final String SENTENCEID = "sentenceid"; //$NON-NLS-1$
		public static final String TEXT = "text"; //$NON-NLS-1$
	}

	@SuppressWarnings("unused")
	static public final class Lookup_FnSentences
	{
		static public final String TABLE = "fts_fnsentences"; //$NON-NLS-1$
		static public final String CONTENT_URI = "content://" + FrameNetContract.AUTHORITY + '/' + Lookup_FnSentences.TABLE; //$NON-NLS-1$
		static public final String SENTENCEID = "sentenceid"; //$NON-NLS-1$
		static public final String CORPUSID = "sentenceid"; //$NON-NLS-1$
		static public final String DOCUMENTID = "documentid"; //$NON-NLS-1$
		static public final String TEXT = "text"; //$NON-NLS-1$
	}
}
