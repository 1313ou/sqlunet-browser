/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.framenet.sql;

/**
 * FrameNet SQL dialect
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class SqLiteDialect0
{
	// LEXUNITS
	// lex unit from lex unit id
	static public final String FrameNetLexUnitQuery = //
			"SELECT luid,lexunit,pos,ludefinition,ludict,fetype AS incorporatedfe,frameid,frame,framedefinition " + //
					"FROM fn_lexunits AS lu " + //
					"LEFT JOIN fn_frames USING (frameid) " + //
					"LEFT JOIN fn_poses AS p ON (lu.posid = p.posid) " + //
					"LEFT JOIN fn_fetypes ON (incorporatedfetypeid = fetypeid) " + //
					"WHERE luid = ? " + //
					"ORDER BY frame;";
	// lex units from word
	static public final String FrameNetLexUnitQueryFromWord = //
			"SELECT wordid,luid,lexunit,pos,ludefinition,ludict,fetype AS incorporatedfe,frameid,frame,framedefinition " + //
					"FROM words AS w " + //
					"INNER JOIN fn_words USING (wordid) " + //
					"INNER JOIN fn_lexemes USING (fnwordid) " + //
					"INNER JOIN fn_lexunits AS lu USING (luid) " + //
					"LEFT JOIN fn_frames USING (frameid) " + //
					"LEFT JOIN fn_poses AS p ON (lu.posid = p.posid) " + //
					"LEFT JOIN fn_fetypes ON (incorporatedfetypeid = fetypeid) " + //
					"WHERE w.word = ?";
	// lex units from fn word
	static public final String FrameNetLexUnitQueryFromFnWord = //
			"SELECT fnwordid,luid,lexunit,posid,ludefinition,ludict,fetype AS incorporatedfe,frameid,frame,framedefinition " + //
					"FROM fnwords AS w " + //
					"INNER JOIN fn_lexemes USING (fnwordid) " + //
					"INNER JOIN fn_lexunits AS lu USING (luid) " + //
					"LEFT JOIN fn_frames USING (frameid) " + //
					"LEFT JOIN fn_poses AS p ON (lu.posid = p.posid) " + //
					"LEFT JOIN fn_fetypes ON (incorporatedfetypeid = fetypeid) " + //
					"WHERE w.word = ?";
	// lex units from word id
	static public final String FrameNetLexUnitQueryFromWordId = //
			"SELECT luid,lexunit,posid,ludefinition,ludict,fetype AS incorporatedfe,frameid,frame,framedefinition " + //
					"FROM words " + //
					"INNER JOIN fn_words USING (wordid) " + //
					"INNER JOIN fn_lexemes USING (fnwordid) " + //
					"INNER JOIN fn_lexunits AS lu USING (luid) " + //
					"LEFT JOIN fn_frames USING (frameid) " + //
					"LEFT JOIN fn_poses AS p ON (lu.posid = p.posid) " + //
					"LEFT JOIN fn_fetypes ON (incorporatedfetypeid = fetypeid) " + //
					"WHERE wordid = ? " + //
					"ORDER BY frame;";
	// lex units from fn word id
	static public final String FrameNetLexUnitQueryFromFnWordId = //
			"SELECT luid,lexunit,posid,ludefinition,ludict,fetype AS incorporatedfe,frameid,frame,framedefinition " + //
					"FROM fn_words " + //
					"INNER JOIN fn_lexemes USING (fnwordid) " + //
					"INNER JOIN fn_lexunits AS lu USING (luid) " + //
					"LEFT JOIN fn_frames USING (frameid) " + //
					"LEFT JOIN fn_poses AS p ON (lu.posid = p.posid) " + //
					"LEFT JOIN fn_fetypes ON (incorporatedfetypeid = fetypeid) " + //
					"WHERE fnwordid = ? " + //
					"ORDER BY frame;";
	// lex units from word id and pos
	static public final String FrameNetLexUnitQueryFromWordIdAndPos = //
			"SELECT luid,lexunit,posid,ludefinition,ludict,fetype AS incorporatedfe,frameid,frame,framedefinition " + //
					"FROM words " + //
					"INNER JOIN fn_words USING (wordid) " + //
					"INNER JOIN fn_lexemes USING (fnwordid) " + //
					"INNER JOIN fn_lexunits AS lu USING (luid) " + //
					"LEFT JOIN fn_frames USING (frameid) " + //
					"LEFT JOIN fn_poses AS p ON (lu.posid = p.posid) " + //
					"LEFT JOIN fn_fetypes ON (incorporatedfetypeid = fetypeid) " + //
					"WHERE wordid = ? AND p.posid = ? " + //
					"ORDER BY frame;";
	// lex units from word id and pos
	static public final String FrameNetLexUnitQueryFromFnWordIdAndPos = //
			"SELECT luid,lexunit,posid,ludefinition,ludict,fetype AS incorporatedfe,frameid,frame,framedefinition " + //
					"FROM fn_words " + //
					"INNER JOIN fn_lexemes USING (fnwordid) " + //
					"INNER JOIN fn_lexunits AS lu USING (luid) " + //
					"LEFT JOIN fn_frames USING (frameid) " + //
					"LEFT JOIN fn_poses AS p ON (lu.posid = p.posid) " + //
					"LEFT JOIN fn_fetypes ON (incorporatedfetypeid = fetypeid) " + //
					"WHERE fnwordid = ? AND p.posid = ? " + //
					"ORDER BY frame;";
	// lex units from frame id
	static public final String FrameNetLexUnitQueryFromFrameId = //
			"SELECT luid,lexunit,posid,ludefinition,ludict,fetype AS incorporatedfe,frameid,frame " + //
					"FROM fnframes " + //
					"INNER JOIN fn_lexunits AS lu USING (frameid) " + //
					"LEFT JOIN fn_poses AS p ON (lu.posid = p.posid) " + //
					"LEFT JOIN fn_fetypes ON (incorporatedfetypeid = fetypeid) " + //
					"WHERE frameid = ? " + //
					"ORDER BY frame;";

	// FRAMES
	// frame from frame id
	static public final String FrameNetFrameQuery = //
			"SELECT fr.frameid,fr.frame,fr.framedefinition, " + //
					"(SELECT GROUP_CONCAT(semtypeid||':'||semtype||':'||semtypedefinition,'|') " + //
					"	FROM fn_frames_semtypes AS t " + //
					"	LEFT JOIN fn_semtypes USING (semtypeid) " + //
					"	WHERE t.frameid = fr.frameid), " + //
					"(SELECT GROUP_CONCAT(frame2id||':'||f2.frame||':'||relation,'|') " + //
					"	FROM fn_frames_related AS r " + //
					"	LEFT JOIN fn_frames AS f2 ON (frame2id = f2.frameid) " + //
					"	LEFT JOIN fn_framerelations USING (relationid) " + //
					"	WHERE r.frameid = fr.frameid) " + //
					"FROM fn_frames AS fr " + //
					"WHERE fr.frameid = ? ;";

	// FRAME ELEMENTS
	// fes from frame id
	static public final String FrameNetFEQueryFromFrameId = //
			"SELECT fetypeid,fetype,feid,fedefinition,feabbrev,coretype,GROUP_CONCAT(semtype,'|') AS semtypes,coretypeid = 1 AS iscorefe,coreset " + //
					"FROM fn_frames " + //
					"INNER JOIN fn_fes USING (frameid) " + //
					"LEFT JOIN fn_fetypes USING (fetypeid) " + //
					"LEFT JOIN fn_coretypes USING (coretypeid) " + //
					"LEFT JOIN fn_fes_semtypes USING (feid) " + //
					"LEFT JOIN fn_semtypes USING (semtypeid) " + //
					"WHERE frameid = ? " + //
					"GROUP BY feid " + //
					"ORDER BY iscorefe DESC,coreset,fetype;";

	// SENTENCES
	// sentence from sentence id
	static public final String FrameNetSentenceQuery = //
			"SELECT sentenceid,`text` " + //
					"FROM fn_sentences " + //
					"WHERE sentenceid = ?;";
	// sentences from lexunit id
	static public final String FrameNetSentencesQueryFromLexUnitId = //
			"SELECT sentenceid,`text` " + //
					"FROM fn_lexunits AS lu " + //
					"LEFT JOIN fn_subcorpuses USING (luid) " + //
					"LEFT JOIN fn_subcorpuses_sentences USING (subcorpusid) " + //
					"LEFT JOIN fn_sentences AS st USING (sentenceid) " + //
					"WHERE lu.luid = ? AND st.sentenceid IS NOT NULL " + //
					"ORDER BY corpusid,documentid,paragno,sentno;";

	// GOVERNORS
	// governors from frame id
	static public final String FrameNetGovernorQueryFromLexUnitId = //
			"SELECT governorid, wordid, word AS governor " + //
					"FROM fn_governors " + //
					"LEFT JOIN fn_lexunits_governors USING (governorid) " + //
					"LEFT JOIN fn_lexunits USING (luid) " + //
					"LEFT JOIN fn_words USING (fnwordid) " + //
					"LEFT JOIN words USING (wordid) " + //
					"WHERE luid = ? " + //
					"ORDER BY governor;";

	// ANNOSETS
	// annoSet from annoSet id
	static public final String FrameNetAnnoSetQuery = //
			"SELECT s.sentenceid,`text`,GROUP_CONCAT(an2.annosetid) " + //
					"FROM fn_annosets AS an " + //
					"LEFT JOIN fn_sentences AS st USING (sentenceid) " + //
					"LEFT JOIN fn_annosets AS an2 ON (st.sentenceid = an2.sentenceid) " + //
					"WHERE an.annosetid = ? " + //
					"GROUP BY an.annosetid;";

	// LAYERS
	// layers from annoSet id
	static public final String FrameNetLayerQueryFromAnnoSetId = //
			"SELECT layerid,layertype,annosetid,rank,GROUP_CONCAT(start||':'||end||':'||labeltype||':'||CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END,'|') " + //
					"FROM " + //
					"(SELECT layerid,layertype,annosetid,rank,start,end,labeltype,labelitype " + //
					"FROM fn_layers " + //
					"LEFT JOIN fn_layertypes USING (layertypeid) " + //
					"LEFT JOIN fn_labels USING (layerid) " + //
					"LEFT JOIN fn_labeltypes USING (labeltypeid) " + //
					"LEFT JOIN fn_labelitypes USING (labelitypeid) " + //
					"WHERE annosetid = ? AND labeltypeid IS NOT NULL " + //
					"ORDER BY rank,layerid,start,end) " + //
					"GROUP BY layerid;";
	// layers from sentence id
	static public final String FrameNetLayerQueryFromSentenceId = //
			"SELECT layerid,layertype,annosetid,rank,GROUP_CONCAT(start||':'||end||':'||labeltype||':'||CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END,'|') " + //
					"FROM " + //
					"(SELECT layerid,layertype,annosetid,rank,start,end,labeltype,labelitype " + //
					"FROM fn_sentences " + //
					"LEFT JOIN fn_annosets USING (sentenceid) " + //
					"LEFT JOIN fn_layers USING (annosetid) " + //
					"LEFT JOIN fn_layertypes USING (layertypeid) " + //
					"LEFT JOIN fn_labels USING (layerid) " + //
					"LEFT JOIN fn_labeltypes USING (labeltypeid) " + //
					"LEFT JOIN fn_labelitypes USING (labelitypeid) " + //
					"WHERE sentenceid = ? AND labeltypeid IS NOT NULL " + //
					"ORDER BY annosetid,rank,layerid,start,end) " + //
					"GROUP BY layerid;";
}
