/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.framenet.loaders

import org.sqlunet.browser.Module.ContentProviderSql
import org.sqlunet.framenet.provider.FrameNetContract
import java.util.Locale

object Queries {

    /**
     * Focused layer name
     */
    private const val FOCUSLAYER = "FE" // "Target";

    /**
     * Is like artifact column
     */
    const val ISLIKE = "islike"

    @JvmStatic
    fun prepareSelect(word: String): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = FrameNetContract.LexUnits_or_Frames.URI_FN
        providerSql.projection = arrayOf(
            FrameNetContract.LexUnits_or_Frames.ID,
            FrameNetContract.LexUnits_or_Frames.FNID,
            FrameNetContract.LexUnits_or_Frames.FNWORDID,
            FrameNetContract.LexUnits_or_Frames.WORDID,
            FrameNetContract.LexUnits_or_Frames.WORD,
            FrameNetContract.LexUnits_or_Frames.NAME,
            FrameNetContract.LexUnits_or_Frames.FRAMENAME,
            FrameNetContract.LexUnits_or_Frames.FRAMEID,
            FrameNetContract.LexUnits_or_Frames.ISFRAME,
            FrameNetContract.LexUnits_or_Frames.WORD + "<>'" + word + "' AS " + ISLIKE
        )
        providerSql.selection = FrameNetContract.LexUnits_or_Frames.WORD + " LIKE ? || '%'"
        providerSql.selectionArgs = arrayOf(word)
        providerSql.sortBy = FrameNetContract.LexUnits_or_Frames.ISFRAME + ',' + FrameNetContract.LexUnits_or_Frames.WORD + ',' + FrameNetContract.LexUnits_or_Frames.ID
        return providerSql
    }

    @JvmStatic
    fun prepareFrame(frameId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = FrameNetContract.Frames_X.URI_BY_FRAME
        providerSql.projection = arrayOf(
            FrameNetContract.Frames_X.FRAMEID,
            FrameNetContract.Frames_X.FRAME,
            FrameNetContract.Frames_X.FRAMEDEFINITION,
            FrameNetContract.Frames_X.SEMTYPE,
            FrameNetContract.Frames_X.SEMTYPEABBREV,
            FrameNetContract.Frames_X.SEMTYPEDEFINITION
        )
        providerSql.selection = FrameNetContract.Frames_X.FRAMEID + " = ?"
        providerSql.selectionArgs = arrayOf(frameId.toString())
        providerSql.sortBy = FrameNetContract.Frames_X.FRAME
        return providerSql
    }

    @JvmStatic
    fun prepareRelatedFrames(frameId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = FrameNetContract.Frames_Related.URI
        providerSql.projection = arrayOf(
            FrameNetContract.AS_SRC_FRAMES + '.' + FrameNetContract.Frames_Related.FRAMEID + " AS " + "i1",
            FrameNetContract.AS_SRC_FRAMES + '.' + FrameNetContract.Frames_Related.FRAME + " AS " + "f1",
            FrameNetContract.AS_DEST_FRAMES + '.' + FrameNetContract.Frames_Related.FRAMEID + " AS " + "i2",
            FrameNetContract.AS_DEST_FRAMES + '.' + FrameNetContract.Frames_Related.FRAME + " AS " + "f2",
            FrameNetContract.Frames_Related.RELATIONID,
            FrameNetContract.Frames_Related.RELATION
        )
        providerSql.selection = FrameNetContract.AS_RELATED_FRAMES + '.' + FrameNetContract.Frames_Related.FRAMEID + " = ?" + " OR " + FrameNetContract.AS_RELATED_FRAMES + '.' + FrameNetContract.Frames_Related.FRAME2ID + " = ?"
        providerSql.selectionArgs = arrayOf(frameId.toString(), frameId.toString())
        return providerSql
    }

    @JvmStatic
    fun prepareFesForFrame(frameId: Int): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = FrameNetContract.Frames_FEs.URI_BY_FE
        providerSql.projection = arrayOf(
            FrameNetContract.Frames_FEs.FETYPEID,
            FrameNetContract.Frames_FEs.FETYPE,
            FrameNetContract.Frames_FEs.FEABBREV,
            FrameNetContract.Frames_FEs.FEDEFINITION,
            "GROUP_CONCAT(" + FrameNetContract.Frames_FEs.SEMTYPE + ",'|') AS " + FrameNetContract.Frames_FEs.SEMTYPES,
            FrameNetContract.Frames_FEs.CORETYPEID,
            FrameNetContract.Frames_FEs.CORETYPE,
            FrameNetContract.Frames_FEs.CORESET
        )
        providerSql.selection = FrameNetContract.Frames_FEs.FRAMEID + " = ? "
        providerSql.selectionArgs = arrayOf(frameId.toString())
        providerSql.sortBy = FrameNetContract.Frames_FEs.CORETYPEID + ',' + FrameNetContract.Frames_FEs.FETYPE
        return providerSql
    }

    @JvmStatic
    fun prepareLexUnit(luId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = FrameNetContract.LexUnits_X.URI_BY_LEXUNIT
        providerSql.projection = arrayOf(
            FrameNetContract.LexUnits_X.LUID,
            FrameNetContract.LexUnits_X.LEXUNIT,
            FrameNetContract.LexUnits_X.LUDEFINITION,
            FrameNetContract.LexUnits_X.LUDICT,
            FrameNetContract.LexUnits_X.INCORPORATEDFETYPE,
            FrameNetContract.LexUnits_X.INCORPORATEDFEDEFINITION,
            FrameNetContract.AS_LEXUNITS + '.' + FrameNetContract.LexUnits_X.POSID,
            FrameNetContract.AS_LEXUNITS + '.' + FrameNetContract.LexUnits_X.FRAMEID,
            FrameNetContract.LexUnits_X.FRAME
        )
        providerSql.selection = FrameNetContract.LexUnits_X.LUID + " = ?"
        providerSql.selectionArgs = arrayOf(luId.toString())
        return providerSql
    }

    @JvmStatic
    fun prepareLexUnitsForFrame(frameId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = FrameNetContract.LexUnits_X.URI_BY_LEXUNIT
        providerSql.projection = arrayOf(
            FrameNetContract.LexUnits_X.LUID,
            FrameNetContract.LexUnits_X.LEXUNIT,
            FrameNetContract.AS_LEXUNITS + '.' + FrameNetContract.LexUnits_X.FRAMEID,
            FrameNetContract.AS_LEXUNITS + '.' + FrameNetContract.LexUnits_X.POSID,
            FrameNetContract.LexUnits_X.LUDEFINITION,
            FrameNetContract.LexUnits_X.LUDICT,
            FrameNetContract.LexUnits_X.INCORPORATEDFETYPE,
            FrameNetContract.LexUnits_X.INCORPORATEDFEDEFINITION
        )
        providerSql.selection = FrameNetContract.AS_FRAMES + '.' + FrameNetContract.LexUnits_X.FRAMEID + " = ?"
        providerSql.selectionArgs = arrayOf(frameId.toString())
        providerSql.sortBy = FrameNetContract.LexUnits_X.LEXUNIT
        return providerSql
    }

    @JvmStatic
    fun prepareLexUnitsForWordAndPos(wordId: Long, pos: Char?): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = FrameNetContract.Words_LexUnits_Frames.URI_FN
        providerSql.projection = arrayOf(
            FrameNetContract.Words_LexUnits_Frames.LUID,
            FrameNetContract.Words_LexUnits_Frames.LEXUNIT,
            FrameNetContract.AS_LEXUNITS + '.' + FrameNetContract.Words_LexUnits_Frames.FRAMEID,
            FrameNetContract.AS_LEXUNITS + '.' + FrameNetContract.Words_LexUnits_Frames.POSID,
            FrameNetContract.Words_LexUnits_Frames.LUDEFINITION,
            FrameNetContract.Words_LexUnits_Frames.LUDICT,
            FrameNetContract.Words_LexUnits_Frames.INCORPORATEDFETYPE,
            FrameNetContract.Words_LexUnits_Frames.INCORPORATEDFEDEFINITION
        )
        providerSql.selection = if (pos == null)
            FrameNetContract.Words_LexUnits_Frames.WORDID + " = ?" else
            FrameNetContract.Words_LexUnits_Frames.WORDID + " = ? AND " + FrameNetContract.AS_POSES + '.' + FrameNetContract.Words_LexUnits_Frames.POS + " = ?"
        providerSql.selectionArgs = if (pos == null) arrayOf(wordId.toString()) else arrayOf(wordId.toString(), pos.toString().uppercase(Locale.getDefault()))
        providerSql.sortBy = FrameNetContract.Words_LexUnits_Frames.FRAME + ',' + FrameNetContract.Words_LexUnits_Frames.LUID
        return providerSql
    }

    @JvmStatic
    fun prepareGovernorsForLexUnit(luId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = FrameNetContract.LexUnits_Governors.URI_FN
        providerSql.projection = arrayOf(
            FrameNetContract.LexUnits_Governors.LUID,
            FrameNetContract.LexUnits_Governors.GOVERNORID,
            FrameNetContract.LexUnits_Governors.GOVERNORTYPE,
            FrameNetContract.LexUnits_Governors.FNWORDID,
            FrameNetContract.LexUnits_Governors.WORD
        )
        providerSql.selection = FrameNetContract.LexUnits_Governors.LUID + " = ?"
        providerSql.selectionArgs = arrayOf(luId.toString())
        providerSql.sortBy = FrameNetContract.LexUnits_Governors.GOVERNORID
        return providerSql
    }

    @JvmStatic
    fun prepareRealizationsForLexicalUnit(luId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = FrameNetContract.LexUnits_FERealizations_ValenceUnits.URI_BY_REALIZATION
        providerSql.projection = arrayOf(
            FrameNetContract.LexUnits_FERealizations_ValenceUnits.LUID,
            FrameNetContract.LexUnits_FERealizations_ValenceUnits.FERID,
            FrameNetContract.LexUnits_FERealizations_ValenceUnits.FETYPE,
            "GROUP_CONCAT(IFNULL(" +
                    FrameNetContract.LexUnits_FERealizations_ValenceUnits.PT + ",'') || ':' || IFNULL(" +
                    FrameNetContract.LexUnits_FERealizations_ValenceUnits.GF + ",'') || ':' || " +
                    FrameNetContract.LexUnits_FERealizations_ValenceUnits.VUID + ") AS " +
                    FrameNetContract.LexUnits_FERealizations_ValenceUnits.FERS,
            FrameNetContract.LexUnits_FERealizations_ValenceUnits.TOTAL
        )
        providerSql.selection = FrameNetContract.LexUnits_FERealizations_ValenceUnits.LUID + " = ?"
        providerSql.selectionArgs = arrayOf(luId.toString())
        providerSql.sortBy = FrameNetContract.LexUnits_FERealizations_ValenceUnits.FERID
        return providerSql
    }

    @JvmStatic
    fun prepareGroupRealizationsForLexUnit(luId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.URI_BY_PATTERN
        providerSql.projection = arrayOf(
            FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.LUID,
            FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.FEGRID,
            FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.PATTERNID,
            "GROUP_CONCAT(" +
                    FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.FETYPE + " || '.' || " +
                    FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.PT + " || '.'|| IFNULL(" +
                    FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.GF + ", '--')) AS " +
                    FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.GROUPREALIZATIONS
        )
        providerSql.selection = FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.LUID + " = ?"
        providerSql.selectionArgs = arrayOf(luId.toString())
        providerSql.sortBy = FrameNetContract.LexUnits_FEGroupRealizations_Patterns_ValenceUnits.FEGRID
        return providerSql
    }

    @JvmStatic
    fun prepareSentence(sentenceId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = FrameNetContract.Sentences.URI
        providerSql.projection = arrayOf(
            FrameNetContract.Sentences.SENTENCEID,
            FrameNetContract.Sentences.TEXT
        )
        providerSql.selection = FrameNetContract.Sentences.SENTENCEID + " = ?"
        providerSql.selectionArgs = arrayOf(sentenceId.toString())
        return providerSql
    }

    @JvmStatic
    fun prepareSentencesForLexUnit(luId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.URI_BY_SENTENCE
        providerSql.projection = arrayOf(
            FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.SENTENCEID,
            FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.TEXT,
            FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.LAYERTYPE,
            FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.RANK,
            "GROUP_CONCAT(" +
                    FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.START + "||':'||" +
                    FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.END + "||':'||" +
                    FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.LABELTYPE + "||':'||" +
                    "CASE WHEN " + FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.LABELITYPE + " IS NULL THEN '' ELSE " + FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.LABELITYPE + " END||':'||" +
                    //"CASE WHEN " + LexUnits_Sentences_AnnoSets_Layers_Labels.BGCOLOR + " IS NULL THEN '' ELSE " + LexUnits_Sentences_AnnoSets_Layers_Labels.BGCOLOR + " END||':'||" + 
                    "''||':'||" +
                    //"CASE WHEN " + LexUnits_Sentences_AnnoSets_Layers_Labels.FGCOLOR + " IS NULL THEN '' ELSE " + LexUnits_Sentences_AnnoSets_Layers_Labels.FGCOLOR + " END" + 
                    "''" +
                    ",'|')" +
                    " AS " + FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.LAYERANNOTATION
        )
        providerSql.selection = FrameNetContract.AS_LEXUNITS + '.' + FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.LUID + " = ? AND " + FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.LAYERTYPE + " = ?"
        providerSql.selectionArgs = arrayOf(luId.toString(), FOCUSLAYER)
        providerSql.sortBy = FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.CORPUSID + ',' +
                FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.DOCUMENTID + ',' +
                FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.PARAGNO + ',' + FrameNetContract.LexUnits_Sentences_AnnoSets_Layers_Labels.SENTNO
        return providerSql
    }

    @JvmStatic
    fun prepareSentencesForPattern(patternId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = FrameNetContract.Patterns_Sentences.URI
        providerSql.projection = arrayOf(
            FrameNetContract.Patterns_Sentences.ANNOSETID,
            FrameNetContract.Patterns_Sentences.SENTENCEID,
            FrameNetContract.Patterns_Sentences.TEXT
        )
        providerSql.selection = FrameNetContract.Patterns_Sentences.PATTERNID + " = ?"
        providerSql.selectionArgs = arrayOf(patternId.toString())
        providerSql.sortBy = FrameNetContract.Patterns_Sentences.SENTENCEID
        return providerSql
    }

    @JvmStatic
    fun prepareSentencesForValenceUnit(vuId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = FrameNetContract.ValenceUnits_Sentences.URI
        providerSql.projection = arrayOf(
            FrameNetContract.Patterns_Sentences.ANNOSETID,
            FrameNetContract.Patterns_Sentences.SENTENCEID,
            FrameNetContract.Patterns_Sentences.TEXT
        )
        providerSql.selection = FrameNetContract.ValenceUnits_Sentences.VUID + " = ?"
        providerSql.selectionArgs = arrayOf(vuId.toString())
        providerSql.sortBy = FrameNetContract.ValenceUnits_Sentences.SENTENCEID
        return providerSql
    }

    @JvmStatic
    fun prepareAnnoSet(annoSetId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = FrameNetContract.AnnoSets_Layers_X.URI
        providerSql.projection = arrayOf(
            FrameNetContract.AnnoSets_Layers_X.SENTENCEID,
            FrameNetContract.AnnoSets_Layers_X.SENTENCETEXT,
            FrameNetContract.AnnoSets_Layers_X.LAYERID,
            FrameNetContract.AnnoSets_Layers_X.LAYERTYPE,
            FrameNetContract.AnnoSets_Layers_X.RANK,
            FrameNetContract.AnnoSets_Layers_X.LAYERANNOTATIONS
        )
        // providerSql.selection = null; // embedded selection
        providerSql.selectionArgs = arrayOf(annoSetId.toString())
        return providerSql
    }

    @JvmStatic
    fun prepareAnnoSetsForGovernor(governorId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = FrameNetContract.Governors_AnnoSets_Sentences.URI
        providerSql.projection = arrayOf(
            FrameNetContract.Governors_AnnoSets_Sentences.GOVERNORID,
            FrameNetContract.Governors_AnnoSets_Sentences.ANNOSETID,
            FrameNetContract.Governors_AnnoSets_Sentences.SENTENCEID,
            FrameNetContract.Governors_AnnoSets_Sentences.TEXT
        )
        providerSql.selection = FrameNetContract.Governors_AnnoSets_Sentences.GOVERNORID + " = ?"
        providerSql.selectionArgs = arrayOf(governorId.toString())
        return providerSql
    }

    @JvmStatic
    fun prepareAnnoSetsForPattern(patternId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = FrameNetContract.Patterns_Layers_X.URI
        providerSql.projection = arrayOf(
            FrameNetContract.Patterns_Layers_X.ANNOSETID,
            FrameNetContract.Patterns_Layers_X.SENTENCEID,
            FrameNetContract.Patterns_Layers_X.SENTENCETEXT,
            FrameNetContract.Patterns_Layers_X.LAYERID,
            FrameNetContract.Patterns_Layers_X.LAYERTYPE,
            FrameNetContract.Patterns_Layers_X.RANK,
            FrameNetContract.Patterns_Layers_X.LAYERANNOTATIONS
        )
        // providerSql.selection = null; // embedded selection
        providerSql.selectionArgs = arrayOf(patternId.toString())
        return providerSql
    }

    @JvmStatic
    fun prepareAnnoSetsForValenceUnit(vuId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = FrameNetContract.ValenceUnits_Layers_X.URI
        providerSql.projection = arrayOf(
            FrameNetContract.ValenceUnits_Layers_X.ANNOSETID,
            FrameNetContract.ValenceUnits_Layers_X.SENTENCEID,
            FrameNetContract.ValenceUnits_Layers_X.SENTENCETEXT,
            FrameNetContract.ValenceUnits_Layers_X.LAYERID,
            FrameNetContract.ValenceUnits_Layers_X.LAYERTYPE,
            FrameNetContract.ValenceUnits_Layers_X.RANK,
            FrameNetContract.ValenceUnits_Layers_X.LAYERANNOTATIONS
        )
        // providerSql.selection = null; // embedded selection
        providerSql.selectionArgs = arrayOf(vuId.toString())
        return providerSql
    }

    @JvmStatic
    fun prepareLayersForSentence(sentenceId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = FrameNetContract.Sentences_Layers_X.URI
        providerSql.projection = arrayOf(
            FrameNetContract.Sentences_Layers_X.ANNOSETID,
            FrameNetContract.Sentences_Layers_X.LAYERID,
            FrameNetContract.Sentences_Layers_X.LAYERTYPE,
            FrameNetContract.Sentences_Layers_X.RANK,
            FrameNetContract.Sentences_Layers_X.LAYERANNOTATIONS
        )
        // providerSql.selection = null; // embedded selection
        providerSql.selectionArgs = arrayOf(sentenceId.toString())
        return providerSql
    }
}
