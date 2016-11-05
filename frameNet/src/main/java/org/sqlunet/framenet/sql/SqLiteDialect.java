package org.sqlunet.framenet.sql;

/**
 * FrameNet SQL dialect
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("unused")
class SqLiteDialect
{
	public static final String FrameNetLexUnitQueryFromWord = //
			"SELECT wordid,luid,lexunit,pos,ludefinition,ludict,fetype AS incorporatedfe,frameid,frame,framedefinition " + //
					"FROM words AS w " + //
					"INNER JOIN fnwords USING (wordid) " + //
					"INNER JOIN fnlexemes USING (fnwordid) " + //
					"INNER JOIN fnlexunits AS lu USING (luid) " + //
					"LEFT JOIN fnframes USING (frameid) " + //
					"LEFT JOIN fnposes AS p ON (lu.posid = p.posid) " + //
					"LEFT JOIN fnfetypes ON (incorporatedfetypeid = fetypeid) " + //
					"WHERE w.lemma = ?";
	static public final String FrameNetWordLexUnitQueryFromWordId = //
			"SELECT luid,lexunit,pos,ludefinition,ludict,fetype AS incorporatedfe,frameid,frame,framedefinition " + //
					"FROM words " + //
					"INNER JOIN fnwords USING (wordid) " + //
					"INNER JOIN fnlexemes USING (fnwordid) " + //
					"INNER JOIN fnlexunits AS lu USING (luid) " + //
					"LEFT JOIN fnframes USING (frameid) " + //
					"LEFT JOIN fnposes AS p ON (lu.posid = p.posid) " + //
					"LEFT JOIN fnfetypes ON (incorporatedfetypeid = fetypeid) " + //
					"WHERE wordid = ? " + //
					"ORDER BY frame;";
	static public final String FrameNetLexUnitQuery = //
			"SELECT luid,lexunit,pos,ludefinition,ludict,fetype AS incorporatedfe,frameid,frame,framedefinition " + //
					"FROM fnlexunits AS lu " + //
					"LEFT JOIN fnframes USING (frameid) " + //
					"LEFT JOIN fnposes AS p ON (lu.posid = p.posid) " + //
					"LEFT JOIN fnfetypes ON (incorporatedfetypeid = fetypeid) " + //
					"WHERE luid = ? " + //
					"ORDER BY frame;";
	static public final String FrameNetWordLexUnitWithPosQuery = //
			"SELECT luid,lexunit,pos,ludefinition,ludict,fetype AS incorporatedfe,frameid,frame,framedefinition " + //
					"FROM words " + //
					"INNER JOIN fnwords USING (wordid) " + //
					"INNER JOIN fnlexemes USING (fnwordid) " + //
					"INNER JOIN fnlexunits AS lu USING (luid) " + //
					"LEFT JOIN fnframes USING (frameid) " + //
					"LEFT JOIN fnposes AS p ON (lu.posid = p.posid) " + //
					"LEFT JOIN fnfetypes ON (incorporatedfetypeid = fetypeid) " + //
					"WHERE wordid = ? AND lu.posid = ? " + //
					"ORDER BY frame;";
	static public final String FrameNetFrameLexUnitQuery = //
			"SELECT luid,lexunit,pos,ludefinition,ludict,fetype AS incorporatedfe,frameid,frame " + //
					"FROM fnframes " + //
					"INNER JOIN fnlexunits AS lu USING (frameid) " + //
					"LEFT JOIN fnposes AS p ON (lu.posid = p.posid) " + //
					"LEFT JOIN fnfetypes ON (incorporatedfetypeid = fetypeid) " + //
					"WHERE frameid = ? " + //
					"ORDER BY frame;";
	// query for framenet frame
	static public final String FrameNetFrameQuery = //
			"SELECT f.frameid,f.frame,f.framedefinition, " + //
					"(SELECT GROUP_CONCAT(semtypeid||':'||semtype||':'||semtypedefinition,'|') " + //
					"	FROM fnframes_semtypes AS t " + //
					"	LEFT JOIN fnsemtypes USING (semtypeid) " + //
					"	WHERE t.frameid = f.frameid), " + //
					"(SELECT GROUP_CONCAT(frame2id||':'||f2.frame||':'||relation,'|') " + //
					"	FROM fnframes_related AS r " + //
					"	LEFT JOIN fnframes AS f2 ON (frame2id = f2.frameid) " + //
					"	LEFT JOIN fnframerelations USING (relationid) " + //
					"	WHERE r.frameid = f.frameid) " + //
					"FROM fnframes AS f " + //
					"WHERE f.frameid = ? ;";
	// query for fe from frame
	static public final String FrameNetFrameFEQuery = //
			"SELECT fetypeid,fetype,feid,fedefinition,feabbrev,coretype,GROUP_CONCAT(semtype,'|') AS semtypes,coretypeid = 1 AS iscorefe,coreset " + //
					"FROM fnframes " + //
					"INNER JOIN fnfes USING (frameid) " + //
					"LEFT JOIN fnfetypes USING (fetypeid) " + //
					"LEFT JOIN fncoretypes USING (coretypeid) " + //
					"LEFT JOIN fnfes_semtypes USING (feid) " + //
					"LEFT JOIN fnsemtypes USING (semtypeid) " + //
					"WHERE frameid = ? " + //
					"GROUP BY feid " + //
					"ORDER BY iscorefe DESC,coreset,fetype;";
	// query for framenet annotations
	//TODO
	static public final String FrameNetAnnotationsQuery = //
			"";
	// query for framenet sentence
	static public final String FrameNetSentenceQuery = //
			"SELECT sentenceid,`text` " + //
					"FROM fnsentences " + //
					"WHERE sentenceid = ?;";
	// query for framenet sentences from luid
	static public final String FrameNetSentencesFromLexicalUnitQuery = //
			"SELECT sentenceid,`text` " + //
					"FROM fnlexunits AS u " + //
					"LEFT JOIN fnsubcorpuses USING (luid) " + //
					"LEFT JOIN fnsubcorpuses_sentences USING (subcorpusid) " + //
					"LEFT JOIN fnsentences AS s USING (sentenceid) " + //
					"WHERE u.luid = ? AND s.sentenceid IS NOT NULL " + //
					"ORDER BY corpusid,documentid,paragno,sentno;";
	// query for governors from frame id
	static public final String FrameNetGovernorQuery = //
			"SELECT governorid, wordid, word AS governor " + //
					"FROM fngovernors " + //
					"LEFT JOIN fnlexunits_governors USING (governorid) " + //
					"LEFT JOIN fnlexunits USING (luid) " + //
					"LEFT JOIN fnwords USING (fnwordid) " + //
					"LEFT JOIN words USING (wordid) " + //
					"WHERE luid = ? " + //
					"ORDER BY governor;";
	// query for annoSet from annoSet id
	public static final String FrameNetAnnoSetQuery = //
			"SELECT s.sentenceid,`text`,GROUP_CONCAT(o.annosetid) " + //
					"FROM fnannosets AS a " + //
					"LEFT JOIN fnsentences AS s USING (sentenceid) " + //
					"LEFT JOIN fnannosets AS o ON (s.sentenceid = o.sentenceid) " + //
					"WHERE a.annosetid = ? " + //
					"GROUP BY a.annosetid;";
	// query for layers from annoSet id
	public static final String FrameNetLayerQueryFromAnnoSet = //
			"SELECT layerid,layertype,rank,GROUP_CONCAT(start||':'||end||':'||labeltype||':'||CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END,'|') " + //
					"FROM " + //
					"(SELECT layerid,layertype,rank,start,end,labeltype,labelitype " + //
					"FROM fnlayers " + //
					"LEFT JOIN fnlayertypes USING (layertypeid) " + //
					"LEFT JOIN fnlabels USING (layerid) " + //
					"LEFT JOIN fnlabeltypes USING (labeltypeid) " + //
					"LEFT JOIN fnlabelitypes USING (labelitypeid) " + //
					"WHERE annosetid = ? AND labeltypeid IS NOT NULL " + //
					"ORDER BY rank,layerid,start,end) " + //
					"GROUP BY layerid;";
	// query for layers from sentence id
	public static final String FrameNetLayerQueryFromSentence = //
			"SELECT layerid,layertype,rank,GROUP_CONCAT(start||':'||end||':'||labeltype||':'||CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END,'|') " + //
					"FROM " + //
					"(SELECT layerid,layertype,rank,start,end,labeltype,labelitype " + //
					"FROM fnsentences " + //
					"LEFT JOIN fnannosets USING (sentenceid) " + //
					"LEFT JOIN fnlayers USING (annosetid) " + //
					"LEFT JOIN fnlayertypes USING (layertypeid) " + //
					"LEFT JOIN fnlabels USING (layerid) " + //
					"LEFT JOIN fnlabeltypes USING (labeltypeid) " + //
					"LEFT JOIN fnlabelitypes USING (labelitypeid) " + //
					"WHERE sentenceid = ? AND labeltypeid IS NOT NULL " + //
					"ORDER BY rank,layerid,start,end) " + //
					"GROUP BY layerid;";
}
