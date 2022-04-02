/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.fn;

/**
 * FrameNet SQL dialect
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class SqLiteDialect
{
	// LEXUNITS
	// lex unit from lex unit id
	static public final String FrameNetLexUnitQuery = //
			"SELECT ${lexunits.luid},${lexunits.lexunit},${poses.pos},${lexunits.ludefinition},${lexunits.ludict},${fetypes.fetype} AS ${incorporatedfe},${frames.frameid},${frames.frame},${frames.framedefinition} " + //
					"FROM ${lexunits.table} AS ${as_lexunits} " + //
					"LEFT JOIN ${frames.table} USING (${frames.frameid}) " + //
					"LEFT JOIN ${poses.table} AS ${as_poses} ON (${as_lexunits}.${poses.posid} = ${as_poses}.${poses.posid}) " + //
					"LEFT JOIN ${fetypes.table} ON (${lexunits.incorporatedfetypeid} = ${fetypes.fetypeid}) " + //
					"WHERE ${lexunits.luid} = ? " + //
					"ORDER BY ${frames.frame};";
	// lex units from word
	static public final String FrameNetLexUnitQueryFromWord = //
			"SELECT ${wnwords.wordid},${lexunits.luid},${lexunits.lexunit},${poses.pos},${lexunits.ludefinition},${lexunits.ludict},${fetypes.fetype} AS ${incorporatedfe},${frames.frameid},${frames.frame},${frames.framedefinition} " + //
					"FROM ${wnwords.table} AS ${as_words} " + //
					"INNER JOIN ${words.table} USING (${wnwords.wordid}) " + //
					"INNER JOIN ${lexemes.table} USING (${words.fnwordid}) " + //
					"INNER JOIN ${lexunits.table} AS ${as_lexunits} USING (${lexunits.luid}) " + //
					"LEFT JOIN ${frames.table} USING (${frames.frameid}) " + //
					"LEFT JOIN ${poses.table} AS ${as_poses} ON (${as_lexunits}.${poses.posid} = ${as_poses}.${poses.posid}) " + //
					"LEFT JOIN ${fetypes.table} ON (${lexunits.incorporatedfetypeid} = ${fetypes.fetypeid}) " + //
					"WHERE ${as_words}.${wnwords.word} = ?";
	// lex units from fn word
	static public final String FrameNetLexUnitQueryFromFnWord = //
			"SELECT ${wnwords.wordid},${lexunits.luid},${lexunits.lexunit},${poses.posid},${lexunits.ludefinition},${lexunits.ludict},${fetypes.fetype} AS ${incorporatedfe},${frames.frameid},${frames.frame},${frames.framedefinition} " + //
					"FROM ${wnwords.table} AS ${as_words} " + //
					"INNER JOIN ${lexemes.table} USING (${wnwords.wordid}) " + //
					"INNER JOIN ${lexunits.table} AS ${as_lexunits} USING (${lexunits.luid}) " + //
					"LEFT JOIN ${frames.table} USING (${frames.frameid}) " + //
					"LEFT JOIN ${poses.table} AS ${as_poses} ON (${as_lexunits}.${poses.posid} = ${as_poses}.${poses.posid}) " + //
					"LEFT JOIN ${fetypes.table} ON (${lexunits.incorporatedfetypeid} = ${fetypes.fetypeid}) " + //
					"WHERE ${as_words}.${wnwords.word} = ?";
	// lex units from word id
	static public final String FrameNetLexUnitQueryFromWordId = //
			"SELECT ${lexunits.luid},${lexunits.lexunit},${poses.posid},${lexunits.ludefinition},${lexunits.ludict},${fetypes.fetype} AS ${incorporatedfe},${frames.frameid},${frames.frame},${frames.framedefinition} " + //
					"FROM ${wnwords.table} " + //
					"INNER JOIN ${words.table} USING (${wnwords.wordid}) " + //
					"INNER JOIN ${lexemes.table} USING (${wnwords.wordid}) " + //
					"INNER JOIN ${lexunits.table} AS ${as_lexunits} USING (${lexunits.luid}) " + //
					"LEFT JOIN ${frames.table} USING (${frames.frameid}) " + //
					"LEFT JOIN ${poses.table} AS ${as_poses} ON (${as_lexunits}.${poses.posid} = ${as_poses}.${poses.posid}) " + //
					"LEFT JOIN ${fetypes.table} ON (${lexunits.incorporatedfetypeid} = ${fetypes.fetypeid}) " + //
					"WHERE ${wnwords.wordid} = ? " + //
					"ORDER BY ${frames.frame};";
	// lex units from fn word id
	static public final String FrameNetLexUnitQueryFromFnWordId = //
			"SELECT ${lexunits.luid},${lexunits.lexunit},${poses.posid},${lexunits.ludefinition},${lexunits.ludict},${fetypes.fetype} AS ${incorporatedfe},${frames.frameid},${frames.frame},${frames.framedefinition} " + //
					"FROM ${wnwords.table} " + //
					"INNER JOIN ${lexemes.table} USING (${words.fnwordid}) " + //
					"INNER JOIN ${lexunits.table} AS ${as_lexunits} USING (${lexunits.luid}) " + //
					"LEFT JOIN ${frames.table} USING (${frames.frameid}) " + //
					"LEFT JOIN ${poses.table} AS ${as_poses} ON (${as_lexunits}.${poses.posid} = ${as_poses}.${poses.posid}) " + //
					"LEFT JOIN ${fetypes.table} ON (${lexunits.incorporatedfetypeid} = ${fetypes.fetypeid}) " + //
					"WHERE ${wnwords.wordid} = ? " + //
					"ORDER BY ${frames.frame};";
	// lex units from word id and pos
	static public final String FrameNetLexUnitQueryFromWordIdAndPos = //
			"SELECT ${lexunits.luid},${lexunits.lexunit},${poses.posid},${lexunits.ludefinition},${lexunits.ludict},${fetypes.fetype} AS ${incorporatedfe},${frames.frameid},${frames.frame},${frames.framedefinition} " + //
					"FROM ${wnwords.table} " + //
					"INNER JOIN ${words.table} USING (${wnwords.wordid}) " + //
					"INNER JOIN ${lexemes.table} USING (${words.fnwordid}) " + //
					"INNER JOIN ${lexunits.table} AS ${as_lexunits} USING (${lexunits.luid}) " + //
					"LEFT JOIN ${frames.table} USING (${frames.frameid}) " + //
					"LEFT JOIN ${poses.table} AS ${as_poses} ON (${as_lexunits}.${poses.posid} = ${as_poses}.${poses.posid}) " + //
					"LEFT JOIN ${fetypes.table} ON (${lexunits.incorporatedfetypeid} = ${fetypes.fetypeid}) " + //
					"WHERE ${{wnwords.wordid} = ? AND ${as_poses}.${poses.posid} = ? " + //
					"ORDER BY ${frames.frame};";
	// lex units from word id and pos
	static public final String FrameNetLexUnitQueryFromFnWordIdAndPos = //
			"SELECT ${lexunits.luid},${lexunits.lexunit},${poses.posid},${lexunits.ludefinition},${lexunits.ludict},${fetypes.fetype} AS ${incorporatedfe},${frames.frameid},${frames.frame},${frames.framedefinition} " + //
					"FROM ${wnwords.table} " + //
					"INNER JOIN ${lexemes.table} USING (${words.fnwordid}) " + //
					"INNER JOIN ${lexunits.table} AS ${as_lexunits} USING (${lexunits.luid}) " + //
					"LEFT JOIN ${frames.table} USING (${frames.frameid}) " + //
					"LEFT JOIN ${poses.table} AS ${as_poses} ON (${as_lexunits}.${poses.posid} = ${as_poses}.${poses.posid}) " + //
					"LEFT JOIN ${fetypes.table} ON (${lexunits.incorporatedfetypeid} = ${fetypes.fetypeid}) " + //
					"WHERE ${wnwords.wordid} = ? AND ${as_poses}.${poses.posid} = ? " + //
					"ORDER BY ${frames.frame};";
	// lex units from frame id
	static public final String FrameNetLexUnitQueryFromFrameId = //
			"SELECT ${lexunits.luid},${lexunits.lexunit},${poses.posid},${lexunits.ludefinition},${lexunits.ludict},${fetypes.fetype} AS ${incorporatedfe},${frames.frameid},${frames.frame} " + //
					"FROM ${frames.table} " + //
					"INNER JOIN ${lexunits.table} AS ${as_lexunits} USING (${frames.frameid}) " + //
					"LEFT JOIN ${poses.table} AS ${as_poses} ON (${as_lexunits}.${poses.posid} = ${as_poses}.${poses.posid}) " + //
					"LEFT JOIN ${fetypes.table} ON (${lexunits.incorporatedfetypeid} = ${fetypes.fetypeid}) " + //
					"WHERE ${frames.frameid} = ? " + //
					"ORDER BY ${frames.frame};";

	// FRAMES
	// frame from frame id
	static public final String FrameNetFrameQuery = //
			"SELECT ${as_frames}.${frames.frameid},${as_frames}.${frames.frame},${as_frames}.${frames.framedefinition}, " + //
					"(SELECT GROUP_CONCAT(${semtypes.semtypeid}||':'||${semtypes.semtype}||':'||${semtypes.semtypedefinition},'|') " + //
					"	FROM ${frames_semtypes.table} AS ${as_types} " + //
					"	LEFT JOIN ${semtypes.table} USING (${semtypes.semtypeid}) " + //
					"	WHERE ${as_types}.${frames.frameid} = ${as_frames}.${frames.frameid}), " + //
					"(SELECT GROUP_CONCAT(${frames_related.frame2id}||':'||${as_related_frames}.${frames.frame}||':'||${framerelations.relation},'|') " + //
					"	FROM ${frames_related.table} AS ${as_relations} " + //
					"	LEFT JOIN ${frames.table} AS ${as_related_frames} ON (${frames_related.frame2id} = ${as_related_frames}.${frames.frameid}) " + //
					"	LEFT JOIN ${framerelations.table} USING (${framerelations.relationid}) " + //
					"	WHERE ${as_related_frames}.${frames.frameid} = ${as_frames}.${frames.frameid}) " + //
					"FROM ${frames.table} AS ${as_frames} " + //
					"WHERE ${as_frames}.${frames.frameid} = ? ;";

	// FRAME ELEMENTS
	// fes from frame id
	static public final String FrameNetFEQueryFromFrameId = //
			"SELECT ${fetypes.fetypeid},${fetypes.fetype},${fes.feid},${fes.fedefinition},${fes.feabbrev},${coretypes.coretype},GROUP_CONCAT(${semtypes.semtype},'|') AS ${{semtypes.semtypes},${coretypes.coretypeid} = 1 AS ${iscorefe},${fes.coreset} " + //
					"FROM ${frames.table} " + //
					"INNER JOIN ${fes.table} USING (${frames.frameid}) " + //
					"LEFT JOIN ${fetypes.table} USING (${fetypes.fetypeid}) " + //
					"LEFT JOIN ${coretypes.table} USING (${coretypes.coretypeid}) " + //
					"LEFT JOIN ${fes_semtypes.table} USING (${fes.feid}) " + //
					"LEFT JOIN ${semtypes.table} USING (${semtypes.semtypeid}) " + //
					"WHERE ${frames.frameid} = ? " + //
					"GROUP BY ${fes.feid} " + //
					"ORDER BY ${iscorefe} DESC,${fes.coreset},${fetypes.fetype};";

	// SENTENCES
	// sentence from sentence id
	static public final String FrameNetSentenceQuery = //
			"SELECT ${sentences.sentenceid},${sentences.text} " + //
					"FROM ${sentences.table} " + //
					"WHERE ${sentences.sentenceid} = ?;";
	// sentences from lexunit id
	static public final String FrameNetSentencesQueryFromLexUnitId = //
			"SELECT ${sentences.sentenceid},${sentences.text} " + //
					"FROM ${lexunits.table} AS ${as_lexunits} " + //
					"LEFT JOIN ${subcorpuses.table} USING (${lexunits.luid}) " + //
					"LEFT JOIN ${subcorpuses_sentences.table} USING (${subcorpuses.subcorpusid}) " + //
					"LEFT JOIN ${sentences.table} AS ${as_sentences} USING (${sentences.sentenceid}) " + //
					"WHERE ${as_lexunits}.${lexunits.luid} = ? AND ${as_sentences}.${sentences.sentenceid} IS NOT NULL " + //
					"ORDER BY ${corpuses.corpusid},${sentences.documentid},${sentences.paragno},${sentences.sentno};";

	// GOVERNORS
	// governors from frame id
	static public final String FrameNetGovernorQueryFromLexUnitId = //
			"SELECT ${governors.governorid}, ${words.fnwordid}, ${words.word} AS ${governor} " + //
					"FROM ${governors.table} " + //
					"LEFT JOIN ${lexunits_governors.table} USING (${governors.governorid}) " + //
					"LEFT JOIN ${lexunits.table} USING (${lexunits.luid}) " + //
					"LEFT JOIN ${words.table} USING (${words.fnwordid}) " + //
					"LEFT JOIN ${words.table} USING (${words.fnwordid}) " + //
					"WHERE ${lexunits.luid} = ? " + //
					"ORDER BY ${governor};";

	// ANNOSETS
	// annoSet from annoSet id
	static public final String FrameNetAnnoSetQuery = //
			"SELECT s.${sentences.sentenceid},${sentences.text},GROUP_CONCAT(${as_annosets2}.${annosets.annosetid}) " + //
					"FROM ${annosets.table} AS ${as_annosets} " + //
					"LEFT JOIN ${sentences.table} AS ${as_sentences} USING (${sentences.sentenceid}) " + //
					"LEFT JOIN ${annosets.table} AS ${as_annosets2} ON (${as_sentences}.${sentences.sentenceid} = ${as_annosets2}.${sentences.sentenceid}) " + //
					"WHERE ${as_annosets}.${annosets.annosetid} = ? " + //
					"GROUP BY ${as_annosets}.${annosets.annosetid};";

	// LAYERS
	// layers from annoSet id
	static public final String FrameNetLayerQueryFromAnnoSetId = //
			"SELECT ${layers.layerid},${layertypes.layertype},${annosets.annosetid},${layers.rank},GROUP_CONCAT(${labels.start}||':'||${labels.end}||':'||${labeltypes.labeltype}||':'||CASE WHEN ${labelitypes.labelitype} IS NULL THEN '' ELSE ${labelitypes.labelitype} END,'|') " + //
					"FROM " + //
					"(SELECT ${layers.layerid},${layertypes.layertype},${annosets.annosetid},${layers.rank},${labels.start},${labels.end},${labeltypes.labeltype},${labelitypes.labelitype} " + //
					"FROM ${layers.table} " + //
					"LEFT JOIN ${layertypes.table} USING (${layertypes.layertypeid}) " + //
					"LEFT JOIN ${labels.table} USING (${layers.layerid}) " + //
					"LEFT JOIN ${labeltypes.table} USING (${labeltypes.labeltypeid}) " + //
					"LEFT JOIN ${labelitypes.table} USING (${labelitypes.labelitypeid}) " + //
					"WHERE ${annosets.annosetid} = ? AND ${labeltypes.labeltypeid} IS NOT NULL " + //
					"ORDER BY ${layers.rank},${layers.layerid},${labels.start},${labels.end}) " + //
					"GROUP BY ${layers.layerid};";
	// layers from sentence id
	static public final String FrameNetLayerQueryFromSentenceId = //
			"SELECT ${layers.layerid},${layertypes.layertype},${annosets.annosetid},${layers.rank},GROUP_CONCAT(${labels.start}||':'||${labels.end}||':'||${labeltypes.labeltype}||':'||CASE WHEN ${labelitypes.labelitype} IS NULL THEN '' ELSE ${labelitypes.labelitype} END,'|') " + //
					"FROM " + //
					"(SELECT ${layers.layerid},${layertypes.layertype},${annosets.annosetid},${layers.rank},${labels.start},${labels.end},${labeltypes.labeltype},${labelitypes.labelitype} " + //
					"FROM ${sentences.table} " + //
					"LEFT JOIN ${annosets.table} USING (${sentences.sentenceid}) " + //
					"LEFT JOIN ${layers.table} USING (${annosets.annosetid}) " + //
					"LEFT JOIN ${layertypes.table} USING (${layertypes.layertypeid}) " + //
					"LEFT JOIN ${labels.table} USING (${layers.layerid}) " + //
					"LEFT JOIN ${labeltypes.table} USING (${labeltypes.labeltypeid}) " + //
					"LEFT JOIN ${labelitypes.table} USING (${labelitypes.labelitypeid}) " + //
					"WHERE ${sentences.sentenceid} = ? AND ${labeltypes.labeltypeid} IS NOT NULL " + //
					"ORDER BY ${annosets.annosetid},${layers.rank},${layers.layerid},${labels.start},${labels.end}) " + //
					"GROUP BY ${layers.layerid};";
}
