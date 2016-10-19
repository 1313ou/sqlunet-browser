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
			"SELECT wordid,luid,lexunit,pos,ludefinition,ludict,fetype AS incorporatedfe,frameid,frame,framedefinition " + // //$NON-NLS-1$
					"FROM words AS w " + // //$NON-NLS-1$
					"INNER JOIN fnwords USING (wordid) " + // //$NON-NLS-1$
					"INNER JOIN fnlexemes USING (fnwordid) " + // //$NON-NLS-1$
					"INNER JOIN fnlexunits AS lu USING (luid) " + // //$NON-NLS-1$
					"LEFT JOIN fnframes USING (frameid) " + // //$NON-NLS-1$
					"LEFT JOIN fnposes AS p ON (lu.posid = p.posid) " + // //$NON-NLS-1$
					"LEFT JOIN fnfetypes ON (incorporatedfetypeid = fetypeid) " + // //$NON-NLS-1$
					"WHERE w.lemma = ?"; // //$NON-NLS-1$

	static public final String FrameNetWordLexUnitQueryFromWordId = //
			"SELECT luid,lexunit,pos,ludefinition,ludict,fetype AS incorporatedfe,frameid,frame,framedefinition " + // //$NON-NLS-1$
					"FROM words " + // //$NON-NLS-1$
					"INNER JOIN fnwords USING (wordid) " + // //$NON-NLS-1$
					"INNER JOIN fnlexemes USING (fnwordid) " + // //$NON-NLS-1$
					"INNER JOIN fnlexunits AS lu USING (luid) " + // //$NON-NLS-1$
					"LEFT JOIN fnframes USING (frameid) " + // //$NON-NLS-1$
					"LEFT JOIN fnposes AS p ON (lu.posid = p.posid) " + // //$NON-NLS-1$
					"LEFT JOIN fnfetypes ON (incorporatedfetypeid = fetypeid) " + // //$NON-NLS-1$
					"WHERE wordid = ? " + // //$NON-NLS-1$
					"ORDER BY frame;"; //$NON-NLS-1$

	static public final String FrameNetLexUnitQuery = //
			"SELECT luid,lexunit,pos,ludefinition,ludict,fetype AS incorporatedfe,frameid,frame,framedefinition " + // //$NON-NLS-1$
					"FROM fnlexunits AS lu " + // //$NON-NLS-1$
					"LEFT JOIN fnframes USING (frameid) " + // //$NON-NLS-1$
					"LEFT JOIN fnposes AS p ON (lu.posid = p.posid) " + // //$NON-NLS-1$
					"LEFT JOIN fnfetypes ON (incorporatedfetypeid = fetypeid) " + // //$NON-NLS-1$
					"WHERE luid = ? " + // //$NON-NLS-1$
					"ORDER BY frame;"; //$NON-NLS-1$

	static public final String FrameNetWordLexUnitWithPosQuery = //
			"SELECT luid,lexunit,pos,ludefinition,ludict,fetype AS incorporatedfe,frameid,frame,framedefinition " + // //$NON-NLS-1$
					"FROM words " + // //$NON-NLS-1$
					"INNER JOIN fnwords USING (wordid) " + // //$NON-NLS-1$
					"INNER JOIN fnlexemes USING (fnwordid) " + // //$NON-NLS-1$
					"INNER JOIN fnlexunits AS lu USING (luid) " + // //$NON-NLS-1$
					"LEFT JOIN fnframes USING (frameid) " + // //$NON-NLS-1$
					"LEFT JOIN fnposes AS p ON (lu.posid = p.posid) " + // //$NON-NLS-1$
					"LEFT JOIN fnfetypes ON (incorporatedfetypeid = fetypeid) " + // //$NON-NLS-1$
					"WHERE wordid = ? AND lu.posid = ? " + // //$NON-NLS-1$
					"ORDER BY frame;"; //$NON-NLS-1$

	static public final String FrameNetFrameLexUnitQuery = //
			"SELECT luid,lexunit,pos,ludefinition,ludict,fetype AS incorporatedfe,frameid,frame " + // //$NON-NLS-1$
					"FROM fnframes " + // //$NON-NLS-1$
					"INNER JOIN fnlexunits AS lu USING (frameid) " + // //$NON-NLS-1$
					"LEFT JOIN fnposes AS p ON (lu.posid = p.posid) " + // //$NON-NLS-1$
					"LEFT JOIN fnfetypes ON (incorporatedfetypeid = fetypeid) " + // //$NON-NLS-1$
					"WHERE frameid = ? " + // //$NON-NLS-1$
					"ORDER BY frame;"; //$NON-NLS-1$

	// query for framenet frame
	static public final String FrameNetFrameQuery = //
			"SELECT f.frameid,f.frame,f.framedefinition, " + // //$NON-NLS-1$
					"(SELECT GROUP_CONCAT(semtypeid||':'||semtype||':'||semtypedefinition,'|') " + // //$NON-NLS-1$
					"	FROM fnframes_semtypes AS t " + // //$NON-NLS-1$
					"	LEFT JOIN fnsemtypes USING (semtypeid) " + // //$NON-NLS-1$
					"	WHERE t.frameid = f.frameid), " + // //$NON-NLS-1$
					"(SELECT GROUP_CONCAT(frame2id||':'||f2.frame||':'||relation,'|') " + // //$NON-NLS-1$
					"	FROM fnframes_related AS r " + // //$NON-NLS-1$
					"	LEFT JOIN fnframes AS f2 ON (frame2id = f2.frameid) " + // //$NON-NLS-1$
					"	LEFT JOIN fnframerelations USING (relationid) " + // //$NON-NLS-1$
					"	WHERE r.frameid = f.frameid) " + // //$NON-NLS-1$
					"FROM fnframes AS f " + // //$NON-NLS-1$
					"WHERE f.frameid = ? ;"; //$NON-NLS-1$

	// query for fe from frame
	static public final String FrameNetFrameFEQuery = //
			"SELECT fetypeid,fetype,feid,fedefinition,feabbrev,coretype,GROUP_CONCAT(semtype,'|') AS semtypes,coretypeid = 1 AS iscorefe,coreset " + // //$NON-NLS-1$
					"FROM fnframes " + // //$NON-NLS-1$
					"INNER JOIN fnfes USING (frameid) " + // //$NON-NLS-1$
					"LEFT JOIN fnfetypes USING (fetypeid) " + // //$NON-NLS-1$
					"LEFT JOIN fncoretypes USING (coretypeid) " + // //$NON-NLS-1$
					"LEFT JOIN fnfes_semtypes USING (feid) " + // //$NON-NLS-1$
					"LEFT JOIN fnsemtypes USING (semtypeid) " + // //$NON-NLS-1$
					"WHERE frameid = ? " + // //$NON-NLS-1$
					"GROUP BY feid " + // //$NON-NLS-1$
					"ORDER BY iscorefe DESC,coreset,fetype;"; //$NON-NLS-1$

	// query for framenet annotations
	//TODO
	static public final String FrameNetAnnotationsQuery = //
			""; //$NON-NLS-1$

	// query for framenet sentence
	static public final String FrameNetSentenceQuery = //
			"SELECT sentenceid,`text` " + // //$NON-NLS-1$
					"FROM fnsentences " + // //$NON-NLS-1$
					"WHERE sentenceid = ?;"; //$NON-NLS-1$

	// query for framenet sentences from luid
	static public final String FrameNetSentencesFromLexicalUnitQuery = //
			"SELECT sentenceid,`text` " + // //$NON-NLS-1$
					"FROM fnlexunits AS u " + // //$NON-NLS-1$
					"LEFT JOIN fnsubcorpuses USING (luid) " + // //$NON-NLS-1$
					"LEFT JOIN fnsubcorpuses_sentences USING (subcorpusid) " + // //$NON-NLS-1$
					"LEFT JOIN fnsentences AS s USING (sentenceid) " + // //$NON-NLS-1$
					"WHERE u.luid = ? AND s.sentenceid IS NOT NULL " + // //$NON-NLS-1$
					"ORDER BY corpusid,documentid,paragno,sentno;"; //$NON-NLS-1$

	// query for governors from frameid
	static public final String FrameNetGovernorQuery = //
			"SELECT governorid, wordid, word AS governor " + // //$NON-NLS-1$
					"FROM fngovernors " + // //$NON-NLS-1$
					"LEFT JOIN fnlexunits_governors USING (governorid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlexunits USING (luid) " + // //$NON-NLS-1$
					"LEFT JOIN fnwords USING (fnwordid) " + // //$NON-NLS-1$
					"LEFT JOIN words USING (wordid) " + // //$NON-NLS-1$
					"WHERE luid = ? " + // //$NON-NLS-1$
					"ORDER BY governor;"; //$NON-NLS-1$

	// query for annoset from annosetid
	public static final String FrameNetAnnoSetQuery = //
			"SELECT s.sentenceid,`text`,GROUP_CONCAT(o.annosetid) " + // //$NON-NLS-1$
					"FROM fnannosets AS a " + // //$NON-NLS-1$
					"LEFT JOIN fnsentences AS s USING (sentenceid) " + // //$NON-NLS-1$
					"LEFT JOIN fnannosets AS o ON (s.sentenceid = o.sentenceid) " + // //$NON-NLS-1$
					"WHERE a.annosetid = ? " + // //$NON-NLS-1$
					"GROUP BY a.annosetid;"; //$NON-NLS-1$

	// query for layers from annosetid
	public static final String FrameNetLayerQueryFromAnnoSet = //
			"SELECT layerid,layertype,rank,GROUP_CONCAT(start||':'||end||':'||labeltype||':'||CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END,'|') " + // //$NON-NLS-1$
					"FROM " + // //$NON-NLS-1$
					"(SELECT layerid,layertype,rank,start,end,labeltype,labelitype " + // //$NON-NLS-1$
					"FROM fnlayers " + // //$NON-NLS-1$
					"LEFT JOIN fnlayertypes USING (layertypeid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlabels USING (layerid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlabeltypes USING (labeltypeid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlabelitypes USING (labelitypeid) " + // //$NON-NLS-1$
					"WHERE annosetid = ? AND labeltypeid IS NOT NULL " + // //$NON-NLS-1$
					"ORDER BY rank,layerid,start,end) " + // //$NON-NLS-1$
					"GROUP BY layerid;"; //$NON-NLS-1$


	// query for layers from sentenceid
	public static final String FrameNetLayerQueryFromSentence = //
			"SELECT layerid,layertype,rank,GROUP_CONCAT(start||':'||end||':'||labeltype||':'||CASE WHEN labelitype IS NULL THEN '' ELSE labelitype END,'|') " + // //$NON-NLS-1$
					"FROM " + // //$NON-NLS-1$
					"(SELECT layerid,layertype,rank,start,end,labeltype,labelitype " + // //$NON-NLS-1$
					"FROM fnsentences " + // //$NON-NLS-1$
					"LEFT JOIN fnannosets USING (sentenceid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlayers USING (annosetid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlayertypes USING (layertypeid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlabels USING (layerid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlabeltypes USING (labeltypeid) " + // //$NON-NLS-1$
					"LEFT JOIN fnlabelitypes USING (labelitypeid) " + // //$NON-NLS-1$
					"WHERE sentenceid = ? AND labeltypeid IS NOT NULL " + // //$NON-NLS-1$
					"ORDER BY rank,layerid,start,end) " + // //$NON-NLS-1$
					"GROUP BY layerid;"; //$NON-NLS-1$
}
