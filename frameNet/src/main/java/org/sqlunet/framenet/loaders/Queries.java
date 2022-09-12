/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.framenet.loaders;

import org.sqlunet.browser.Module;
import org.sqlunet.framenet.provider.FrameNetContract;

import androidx.annotation.Nullable;

public class Queries
{
	/**
	 * Focused layer name
	 */
	public static final String FOCUSLAYER = "FE"; // "Target";

	/**
	 * Is like artifact column
	 */
	public static final String ISLIKE = "islike";

	public static Module.ContentProviderSql prepareSelect(final String word)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = FrameNetContract.LexUnits_or_Frames.URI_FN;
		providerSql.projection = new String[]{ //
				FrameNetContract.LexUnits_or_Frames.ID, //
				FrameNetContract.LexUnits_or_Frames.FNID, //
				FrameNetContract.LexUnits_or_Frames.FNWORDID, //
				FrameNetContract.LexUnits_or_Frames.WORDID, //
				FrameNetContract.LexUnits_or_Frames.WORD, //
				FrameNetContract.LexUnits_or_Frames.NAME, //
				FrameNetContract.LexUnits_or_Frames.FRAMENAME, //
				FrameNetContract.LexUnits_or_Frames.FRAMEID, //
				FrameNetContract.LexUnits_or_Frames.ISFRAME, //
				FrameNetContract.LexUnits_or_Frames.WORD + "<>'" + word + "' AS " + ISLIKE, //
		};
		providerSql.selection = FrameNetContract.LexUnits_or_Frames.WORD + " LIKE ? || '%'";
		providerSql.selectionArgs = new String[]{word};
		providerSql.sortBy = FrameNetContract.LexUnits_or_Frames.ISFRAME + ',' + FrameNetContract.LexUnits_or_Frames.WORD + ',' + FrameNetContract.LexUnits_or_Frames.ID;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareFrame(final long frameId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = FrameNetContract.Frames_X.URI_BY_FRAME;
		providerSql.projection = new String[]{ //
				FrameNetContract.Frames_X.FRAMEID, //
				FrameNetContract.Frames_X.FRAME, //
				FrameNetContract.Frames_X.FRAMEDEFINITION, //
				FrameNetContract.Frames_X.SEMTYPE, //
				FrameNetContract.Frames_X.SEMTYPEABBREV, //
				FrameNetContract.Frames_X.SEMTYPEDEFINITION, //
		};
		providerSql.selection = FrameNetContract.Frames_X.FRAMEID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(frameId)};
		providerSql.sortBy = FrameNetContract.Frames_X.FRAME;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareRelatedFrames(final long frameId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = FrameNetContract.Frames_Related.URI;
		providerSql.projection = new String[]{ //
				FrameNetContract.AS_SRC_FRAMES + '.' + FrameNetContract.Frames_Related.FRAMEID + " AS " + "i1", //
				FrameNetContract.AS_SRC_FRAMES + '.' + FrameNetContract.Frames_Related.FRAME + " AS " + "f1", //
				FrameNetContract.AS_DEST_FRAMES + '.' + FrameNetContract.Frames_Related.FRAMEID + " AS " + "i2", //
				FrameNetContract.AS_DEST_FRAMES + '.' + FrameNetContract.Frames_Related.FRAME + " AS " + "f2", //
				FrameNetContract.Frames_Related.RELATIONID, //
				FrameNetContract.Frames_Related.RELATION, //
		};
		providerSql.selection = FrameNetContract.AS_RELATED_FRAMES + '.' + FrameNetContract.Frames_Related.FRAMEID + " = ?" + " OR " + FrameNetContract.AS_RELATED_FRAMES + '.' + FrameNetContract.Frames_Related.FRAME2ID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(frameId), Long.toString(frameId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareFesForFrame(final int frameId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = FrameNetContract.Frames_FEs.URI_BY_FE;
		providerSql.projection = new String[]{ //
				FrameNetContract.Frames_FEs.FETYPEID, //
				FrameNetContract.Frames_FEs.FETYPE, //
				FrameNetContract.Frames_FEs.FEABBREV, //
				FrameNetContract.Frames_FEs.FEDEFINITION, //
				"GROUP_CONCAT(" + FrameNetContract.Frames_FEs.SEMTYPE + ",'|') AS " + FrameNetContract.Frames_FEs.SEMTYPES, //
				FrameNetContract.Frames_FEs.CORETYPEID, //
				FrameNetContract.Frames_FEs.CORETYPE, //
				FrameNetContract.Frames_FEs.CORESET, //
		};
		providerSql.selection = FrameNetContract.Frames_FEs.FRAMEID + " = ? ";
		providerSql.selectionArgs = new String[]{Integer.toString(frameId)};
		providerSql.sortBy = FrameNetContract.Frames_FEs.CORETYPEID + ',' + FrameNetContract.Frames_FEs.FETYPE;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareLexUnit(final long luId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = FrameNetContract.LexUnits_X.URI_BY_LEXUNIT;
		providerSql.projection = new String[]{ //
				FrameNetContract.LexUnits_X.LUID, //
				FrameNetContract.LexUnits_X.LEXUNIT, //
				FrameNetContract.LexUnits_X.LUDEFINITION, //
				FrameNetContract.LexUnits_X.LUDICT, //
				FrameNetContract.LexUnits_X.INCORPORATEDFETYPE, //
				FrameNetContract.LexUnits_X.INCORPORATEDFEDEFINITION, //
				FrameNetContract.AS_LEXUNITS + '.' + FrameNetContract.LexUnits_X.POSID, //
				FrameNetContract.AS_LEXUNITS + '.' + FrameNetContract.LexUnits_X.FRAMEID, //
				FrameNetContract.LexUnits_X.FRAME, //
		};
		providerSql.selection = FrameNetContract.LexUnits_X.LUID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(luId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareLexUnitsForFrame(final long frameId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = FrameNetContract.LexUnits_X.URI_BY_LEXUNIT;
		providerSql.projection = new String[]{ //
				FrameNetContract.LexUnits_X.LUID, //
				FrameNetContract.LexUnits_X.LEXUNIT, //
				FrameNetContract.AS_LEXUNITS + '.' + FrameNetContract.LexUnits_X.FRAMEID, //
				FrameNetContract.AS_LEXUNITS + '.' + FrameNetContract.LexUnits_X.POSID, //
				FrameNetContract.LexUnits_X.LUDEFINITION, //
				FrameNetContract.LexUnits_X.LUDICT, //
				FrameNetContract.LexUnits_X.INCORPORATEDFETYPE, //
				FrameNetContract.LexUnits_X.INCORPORATEDFEDEFINITION, //
		};
		providerSql.selection = FrameNetContract.AS_FRAMES + '.' + FrameNetContract.LexUnits_X.FRAMEID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(frameId)};
		providerSql.sortBy = FrameNetContract.LexUnits_X.LEXUNIT;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareLexUnitsForWordAndPos(final long wordId, @Nullable final Character pos)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = FrameNetContract.Words_LexUnits_Frames.URI_FN;
		providerSql.projection = new String[]{ //
				FrameNetContract.Words_LexUnits_Frames.LUID, //
				FrameNetContract.Words_LexUnits_Frames.LEXUNIT, //
				FrameNetContract.AS_LEXUNITS + '.' + FrameNetContract.Words_LexUnits_Frames.FRAMEID, //
				FrameNetContract.AS_LEXUNITS + '.' + FrameNetContract.Words_LexUnits_Frames.POSID, //
				FrameNetContract.Words_LexUnits_Frames.LUDEFINITION, //
				FrameNetContract.Words_LexUnits_Frames.LUDICT, //
				FrameNetContract.Words_LexUnits_Frames.INCORPORATEDFETYPE, //
				FrameNetContract.Words_LexUnits_Frames.INCORPORATEDFEDEFINITION, //
		};
		providerSql.selection = pos == null ?  //
				FrameNetContract.Words_LexUnits_Frames.WORDID + " = ?" :  //
				FrameNetContract.Words_LexUnits_Frames.WORDID + " = ? AND " + FrameNetContract.AS_POSES + '.' + FrameNetContract.Words_LexUnits_Frames.POS + " = ?";
		providerSql.selectionArgs = pos == null ? new String[]{Long.toString(wordId)} : new String[]{Long.toString(wordId), pos.toString().toUpperCase()};
		providerSql.sortBy = FrameNetContract.Words_LexUnits_Frames.FRAME + ',' + FrameNetContract.Words_LexUnits_Frames.LUID;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareGovernorsForLexUnit(final long luId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = FrameNetContract.LexUnits_Governors.URI_FN;
		providerSql.projection = new String[]{ //
				FrameNetContract.LexUnits_Governors.LUID, //
				FrameNetContract.LexUnits_Governors.GOVERNORID, //
				FrameNetContract.LexUnits_Governors.GOVERNORTYPE, //
				FrameNetContract.LexUnits_Governors.FNWORDID, //
				FrameNetContract.LexUnits_Governors.WORD, //
		};
		providerSql.selection = FrameNetContract.LexUnits_Governors.LUID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(luId)};
		providerSql.sortBy = FrameNetContract.LexUnits_Governors.GOVERNORID;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareRealizationsForLexicalUnit(final long luId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = FrameNetContract.LexUnits_FERealizations_ValenceUnits.URI_BY_REALIZATION;
		providerSql.projection = new String[]{ //
				FrameNetContract.LexUnits_FERealizations_ValenceUnits.LUID, //
				FrameNetContract.LexUnits_FERealizations_ValenceUnits.FERID, //
				FrameNetContract.LexUnits_FERealizations_ValenceUnits.FETYPE, //
				"GROUP_CONCAT(IFNULL(" +  //
						FrameNetContract.LexUnits_FERealizations_ValenceUnits.PT + ",'') || ':' || IFNULL(" + //
						FrameNetContract.LexUnits_FERealizations_ValenceUnits.GF + ",'') || ':' || " +  //
						FrameNetContract.LexUnits_FERealizations_ValenceUnits.VUID + ") AS " +  //
						FrameNetContract.LexUnits_FERealizations_ValenceUnits.FERS, //
				FrameNetContract.LexUnits_FERealizations_ValenceUnits.TOTAL, //
		};
		providerSql.selection = FrameNetContract.LexUnits_FERealizations_ValenceUnits.LUID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(luId)};
		providerSql.sortBy = FrameNetContract.LexUnits_FERealizations_ValenceUnits.FERID;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareGroupRealizationsForLexUnit(final long luId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.URI_BY_PATTERN;
		providerSql.projection = new String[]{ //
				FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.LUID, //
				FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.FEGRID, //
				FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.PATTERNID, //
				"GROUP_CONCAT(" + //
						FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.FETYPE + " || '.' || " + //
						FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.PT + " || '.'|| IFNULL(" + //
						FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.GF + ", '--')) AS " + //
						FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.GROUPREALIZATIONS,};
		providerSql.selection = FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.LUID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(luId)};
		providerSql.sortBy = FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.FEGRID;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareSentence(final long sentenceId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = FrameNetContract.Sentences.URI;
		providerSql.projection = new String[]{ //
				FrameNetContract.Sentences.SENTENCEID, //
				FrameNetContract.Sentences.TEXT, //
		};
		providerSql.selection = FrameNetContract.Sentences.SENTENCEID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(sentenceId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareSentencesForLexUnit(final long luId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.URI_BY_SENTENCE;
		providerSql.projection = new String[]{ //
				FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.SENTENCEID, //
				FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.TEXT, //
				FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.LAYERTYPE, //
				FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.RANK, //
				"GROUP_CONCAT(" + //
						FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.START + "||':'||" + //
						FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.END + "||':'||" + //
						FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.LABELTYPE + "||':'||" + //
						"CASE WHEN " + FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.LABELITYPE + " IS NULL THEN '' ELSE " + FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.LABELITYPE + " END||':'||" + //
						//"CASE WHEN " + LexUnits_Sentences_AnnoSets_Layers_Labels.BGCOLOR + " IS NULL THEN '' ELSE " + LexUnits_Sentences_AnnoSets_Layers_Labels.BGCOLOR + " END||':'||" + //
						"''||':'||" + //
						//"CASE WHEN " + LexUnits_Sentences_AnnoSets_Layers_Labels.FGCOLOR + " IS NULL THEN '' ELSE " + LexUnits_Sentences_AnnoSets_Layers_Labels.FGCOLOR + " END" + //
						"''" + //
						",'|')" + //
						" AS " + FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.LAYERANNOTATION, //
		};
		providerSql.selection = FrameNetContract.AS_LEXUNITS + '.' + FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.LUID + " = ? AND " + FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.LAYERTYPE + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(luId), FOCUSLAYER};
		providerSql.sortBy = FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.CORPUSID + ',' + //
				FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.DOCUMENTID + ',' + //
				FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.PARAGNO + ',' + //
				FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.SENTNO;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareSentencesForPattern(final long patternId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = FrameNetContract.Patterns_Sentences.URI;
		providerSql.projection = new String[]{ //
				FrameNetContract.Patterns_Sentences.ANNOSETID, //
				FrameNetContract.Patterns_Sentences.SENTENCEID, //
				FrameNetContract.Patterns_Sentences.TEXT, //
		};
		providerSql.selection = FrameNetContract.Patterns_Sentences.PATTERNID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(patternId)};
		providerSql.sortBy = FrameNetContract.Patterns_Sentences.SENTENCEID;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareSentencesForValenceUnit(final long vuId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = FrameNetContract.ValenceUnits_Sentences.URI;
		providerSql.projection = new String[]{ //
				FrameNetContract.Patterns_Sentences.ANNOSETID, //
				FrameNetContract.Patterns_Sentences.SENTENCEID, //
				FrameNetContract.Patterns_Sentences.TEXT, //
		};
		providerSql.selection = FrameNetContract.ValenceUnits_Sentences.VUID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(vuId)};
		providerSql.sortBy = FrameNetContract.ValenceUnits_Sentences.SENTENCEID;
		return providerSql;
	}

	public static Module.ContentProviderSql prepareAnnoSet(final long annoSetId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = FrameNetContract.AnnoSets_Layers_X.URI;
		providerSql.projection = new String[]{ //
				FrameNetContract.AnnoSets_Layers_X.SENTENCEID, //
				FrameNetContract.AnnoSets_Layers_X.SENTENCETEXT, //
				FrameNetContract.AnnoSets_Layers_X.LAYERID, //
				FrameNetContract.AnnoSets_Layers_X.LAYERTYPE, //
				FrameNetContract.AnnoSets_Layers_X.RANK, //
				FrameNetContract.AnnoSets_Layers_X.LAYERANNOTATIONS, //
		};
		// providerSql.selection = null; // embedded selection
		providerSql.selectionArgs = new String[]{Long.toString(annoSetId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareAnnoSetsForGovernor(final long governorId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = FrameNetContract.Governors_AnnoSets_Sentences.URI;
		providerSql.projection = new String[]{ //
				FrameNetContract.Governors_AnnoSets_Sentences.GOVERNORID, //
				FrameNetContract.Governors_AnnoSets_Sentences.ANNOSETID, //
				FrameNetContract.Governors_AnnoSets_Sentences.SENTENCEID, //
				FrameNetContract.Governors_AnnoSets_Sentences.TEXT, //
		};
		providerSql.selection = FrameNetContract.Governors_AnnoSets_Sentences.GOVERNORID + " = ?";
		providerSql.selectionArgs = new String[]{Long.toString(governorId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareAnnoSetsForPattern(final long patternId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = FrameNetContract.Patterns_Layers_X.URI;
		providerSql.projection = new String[]{ //
				FrameNetContract.Patterns_Layers_X.ANNOSETID, //
				FrameNetContract.Patterns_Layers_X.SENTENCEID, //
				FrameNetContract.Patterns_Layers_X.SENTENCETEXT, //
				FrameNetContract.Patterns_Layers_X.LAYERID, //
				FrameNetContract.Patterns_Layers_X.LAYERTYPE, //
				FrameNetContract.Patterns_Layers_X.RANK, //
				FrameNetContract.Patterns_Layers_X.LAYERANNOTATIONS, //
		};
		// providerSql.selection = null; // embedded selection
		providerSql.selectionArgs = new String[]{Long.toString(patternId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareAnnoSetsForValenceUnit(final long vuId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = FrameNetContract.ValenceUnits_Layers_X.URI;
		providerSql.projection = new String[]{ //
				FrameNetContract.ValenceUnits_Layers_X.ANNOSETID, //
				FrameNetContract.ValenceUnits_Layers_X.SENTENCEID, //
				FrameNetContract.ValenceUnits_Layers_X.SENTENCETEXT, //
				FrameNetContract.ValenceUnits_Layers_X.LAYERID, //
				FrameNetContract.ValenceUnits_Layers_X.LAYERTYPE, //
				FrameNetContract.ValenceUnits_Layers_X.RANK, //
				FrameNetContract.ValenceUnits_Layers_X.LAYERANNOTATIONS, //
		};
		// providerSql.selection = null; // embedded selection
		providerSql.selectionArgs = new String[]{Long.toString(vuId)};
		return providerSql;
	}

	public static Module.ContentProviderSql prepareLayersForSentence(final long sentenceId)
	{
		final Module.ContentProviderSql providerSql = new Module.ContentProviderSql();
		providerSql.providerUri = FrameNetContract.Sentences_Layers_X.URI;
		providerSql.projection = new String[]{ //
				FrameNetContract.Sentences_Layers_X.ANNOSETID, //
				FrameNetContract.Sentences_Layers_X.LAYERID, //
				FrameNetContract.Sentences_Layers_X.LAYERTYPE, //
				FrameNetContract.Sentences_Layers_X.RANK, //
				FrameNetContract.Sentences_Layers_X.LAYERANNOTATIONS, //
		};
		// providerSql.selection = null; // embedded selection
		providerSql.selectionArgs = new String[]{Long.toString(sentenceId)};
		return providerSql;
	}
}
