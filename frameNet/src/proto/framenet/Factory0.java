/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * FrameNet provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Factory implements Function<String, String[]>, Supplier<String[]>
{
	//# instantiated at runtime
	static public final String URI_LAST = "#{uri_last}";

	public Factory()
	{
		System.out.println("FN Factory");
	}

	@Override
	public String[] apply(String keyname)
	{
		final String last = URI_LAST;

		String table;
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String sortOrder = null;

		Key key = Key.valueOf(keyname);
		switch (key)
		{
			// T A B L E

			// table uri : last element is table

			case LEXUNITS:
				table = "${lexunits.table}";
				break;

			case FRAMES:
				table = "${frames.table}";
				break;

			case ANNOSETS:
				table = "${annosets.table}";
				break;

			case SENTENCES:
				table = "${sentences.table}";
				break;

			case WORDS:
				table = "${words.table}";
				break;

			// I T E M

			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case LEXUNIT1:
				table = "${lexunits.table}";
				selection = "${lexunits.luid} = #{uri_last}";
				break;

			case FRAME1:
				table = "${frames.table}";
				selection = "${frames.frameid} = #{uri_last}";
				break;

			case SENTENCE1:
				table = "${sentences.table}";
				selection = "${sentences.sentenceid} = #{uri_last}";
				break;

			case ANNOSET1:
				table = "${annosets.table}";
				selection = "${annosets.annosetid} = #{uri_last}";
				break;

			// J O I N S

			case LEXUNITS_OR_FRAMES_FN:
				table = String.format("(" + // 1
								"SELECT %s + %s AS %s, %s AS %s, %s AS %s, %s AS %s, %s AS %s, %s AS %s, %s AS %s, %s AS %s, 0 AS %s " + // 2
								"FROM %s " + // 3
								"INNER JOIN %s USING (%s) " + // 4
								"INNER JOIN %s AS %s USING (%s) " + // 5
								"INNER JOIN %s AS %s USING (%s) " + // 6
								"UNION " + // 7
								"SELECT %s AS %s, %s AS %s, 0 AS %s, 0 AS %s, %s AS %s, %s AS %s, %s AS %s, %s AS %s, 1 AS %s " + // 8
								"FROM %s " + // 9
								")", //
						"${words.fnwordid}", "10000", "${_id}", "${lexunits.luid}", "${fnid}", "${words.fnwordid}", "${words.fnwordid}", "${words.wordid}", "${words.wordid}", "${words.word}", "${words.word}", "${lexunits.lexunit}", "${name}", "${frames.frame}", "${frames.frame}", "${frames.frameid}", "${frames.frameid}", "${isframe}", // 2
						"${words.table}", // 3
						"${lexemes.table}", "${words.fnwordid}", // 4
						"${lexunits.table}", "${as_lexunits}", "${lexunits.luid}", // 5
						"${frames.table}", "${as_frames}", "${frames.frameid}", // 6
						// 7
						"${frames.frameid}", "${_id}", "${frames.frameid}", "${fnid}", "${words.fnwordid}", "${words.wordid}", "${frames.frame}", "${words.word}", "${frames.frame}", "${name}", "${frames.frame}", "${frames.frame}", "${frames.frameid}", "${frames.frameid}", "${isframe}", // 8
						"${frames.table}"); // 9
				break;

			case LEXUNITS_OR_FRAMES:
				table = String.format("(" + // 1
								"SELECT %s + %s AS %s, %s AS %s, %s AS %s, %s AS %s, %s AS %s, %s AS %s, %s AS %s, %s AS %s, 0 AS %s " + // 2
								"FROM %s " + // 3
								"INNER JOIN %s USING (%s) " + // 3b
								"INNER JOIN %s USING (%s) " + // 4
								"INNER JOIN %s AS %s USING (%s) " + // 5
								"INNER JOIN %s AS %s USING (%s) " + // 6
								"UNION " + // 7
								"SELECT %s AS %s, %s AS %s, 0 AS %s, 0 AS %s, %s AS %s, %s AS %s, %s AS %s, %s AS %s, 1 AS %s " + // 8
								"FROM %s " + // 9
								")", //
						"${words.fnwordid}", "10000", "${_id}", "${lexunits.luid}", "${fnid}", "${words.fnwordid}", "${words.fnwordid}", "${words.wordid}", "${words.wordid}", "${words.word}", "${words.word}", "${lexunits.lexunit}", "${name}", "${frames.frame}", "${frames.frame}", "${frames.frameid}", "${frames.frameid}", "${isframe}", // 2
						"${words.table}", // 3
						"${wnwords.table}", "${wnwords.wordid}", // 3b
						"${lexemes.table}", "${words.fnwordid}", // 4
						"${lexunits.table}", "${as_lexunits}", "${lexunits.luid}", // 5
						"${frames.table}", "${as_frames}", "${frames.frameid}", // 6
						// 7
						"${frames.frameid}", "${_id}", "${frames.frameid}", "${fnid}", "${words.fnwordid}", "${words.wordid}", "${frames.frame}", "${words.word}", "${frames.frame}", "${name}", "${frames.frame}", "${frames.frame}", "${frames.frameid}", "${frames.frameid}", "${isframe}", // 8
						"${frames.table}"); // 9
				break;

			case FRAMES_X_BY_FRAME:
				table = String.format("%s " + //
								"LEFT JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s)", //
						"${frames.table}", //
						"${frames_semtypes.table}", "${frames.frameid}", //
						"${semtypes.table}", "${semtypes.semtypeid}");
				groupBy = "${frames.frameid}";
				break;

			case FRAMES_RELATED:
				table = String.format("%s AS %s " + //
								"LEFT JOIN %s AS %s USING (%s) " + //
								"LEFT JOIN %s AS %s ON (%s = %s.%s) " + //
								"LEFT JOIN %s USING (%s)", //
						"${frames_related.table}", "${as_related_frames}", //
						"${frames.table}", "${src_frame}", "${frames.frameid}", //
						"${frames.table}", "${dest_frame}", "${frames_related.frame2id}", "${dest_frame}", "${frames.frameid}", //
						"${framerelations.table}", "${frames_related.relationid}");
				break;

			case LEXUNITS_X_BY_LEXUNIT:
				table = String.format("%s AS %s " + //
								"LEFT JOIN %s AS %s USING (%s) " + //
								"LEFT JOIN %s AS %s ON (%s.%s = %s.%s) " + //
								"LEFT JOIN %s AS %s ON (%s = %s.%s) " + //
								"LEFT JOIN %s AS %s ON (%s.%s = %s.%s AND %s = %s.%s)", //
						"${lexunits.table}", "${as_lexunits}", //
						"${frames.table}", "${as_frames}", "${frames.frameid}", //
						"${poses.table}", "${as_poses}", "${as_lexunits}", "${poses.posid}", "${as_poses}", "${poses.posid}", //
						"${fetypes.table}", "${as_fetypes}", "${lexunits.incorporatedfetypeid}", "${as_fetypes}", "${fetypes.fetypeid}", //
						"${fes.table}", "${as_fes}", "${as_frames}", "${frames.frameid}", "${as_fes}", "${frames.frameid}", "${lexunits.incorporatedfetypeid}", "${as_fes}", "${fes.fetypeid}");
				groupBy = "${lexunits.luid}";
				break;

			case SENTENCES_LAYERS_X:
				table = String.format("(SELECT %s,%s,%s,%s,%s," + // 1
								"GROUP_CONCAT(%s||':'||" + // 2
								"%s||':'||" + // 3
								"%s||':'||" + // 4
								"CASE WHEN %s IS NULL THEN '' ELSE %s END" + // 5
								// "||':'||CASE WHEN %s IS NULL THEN '' ELSE %s END" + // 6
								// "||':'||CASE WHEN %s IS NULL THEN '' ELSE %s END + // 7
								// ", '|'" + // GROUP_CONCAT DELIMITER
								") AS %s " + // 2bis
								"FROM %s " + // 8
								"LEFT JOIN %s USING (%s) " + // 9
								"LEFT JOIN %s USING (%s) " + // 10
								"LEFT JOIN %s USING (%s) " + // 11
								"LEFT JOIN %s USING (%s) " + // 12
								"LEFT JOIN %s USING (%s) " + // 13
								"LEFT JOIN %s USING (%s) " + // 14
								"WHERE %s = ? AND %s IS NOT NULL " + // 15
								"GROUP BY %s " + // 16
								"ORDER BY %s,%s)", // 17 "ORDER BY %s,%s,%s,%s)"
						"${annosets.annosetid}", "${sentences.sentenceid}", "${layers.layerid}", "${layertypes.layertype}", "${layers.rank}", // 1
						"${labels.start}", // 2
						"${labels.end}", // 3
						"${labeltypes.labeltype}", // 4
						"${labelitypes.labelitype}", "${labelitypes.labelitype}", // 5
						// "${labels.bgcolor}", "${labels.bgcolor}", // 6
						// "${labels.fgcolor}", "${labels.fgcolor}", // 7
						"${annotations}", // 2bis
						"${sentences.table}", // 8
						"${annosets.table}", "${sentences.sentenceid}", // 9
						"${layers.table}", "${annosets.annosetid}", // 10
						"${layertypes.table}", "${layertypes.layertypeid}", // 11
						"${labels.table}", "${layers.layerid}", // 12
						"${labeltypes.table}", "${labeltypes.labeltypeid}", // 13
						"${labelitypes.table}", "${labelitypes.labelitypeid}", // 14
						"${sentences.sentenceid}", "${labeltypes.labeltypeid}", // 15
						"${layers.layerid}", // 16
						"${layers.rank}", "${layertypes.layertypeid}"); // 17 "${layers.rank}", "${layers.layerid}", "${labels.start}", "${labels.end}");
				break;

			case ANNOSETS_LAYERS_X:
				table = String.format("(SELECT %s,%s,%s,%s,%s," + // 1
								"GROUP_CONCAT(%s||':'||" + // 2
								"%s||':'||" + // 3
								"%s||':'||" + // 4
								"CASE WHEN %s IS NULL THEN '' ELSE %s END" + // 5
								// "||':'||CASE WHEN %s IS NULL THEN '' ELSE %s END" + // 6
								// "||':'||CASE WHEN %s IS NULL THEN '' ELSE %s END
								// ", '|'" + // GROUP_CONCAT DELIMITER
								") AS %s " + // 2bis
								"FROM %s " + // 8
								"LEFT JOIN %s USING (%s) " + // 9
								"LEFT JOIN %s USING (%s) " + // 10
								"LEFT JOIN %s USING (%s) " + // 11
								"LEFT JOIN %s USING (%s) " + // 12
								"LEFT JOIN %s USING (%s) " + // 13
								"LEFT JOIN %s USING (%s) " + // 14
								"WHERE %s = ? AND %s IS NOT NULL " + // 15
								"GROUP BY %s " + // 16
								"ORDER BY %s,%s,%s,%s)", // 17
						"${sentences.sentenceid}", "${sentences.text}", "${layers.layerid}", "${layertypes.layertype}", "${layers.rank}", // 1
						"${labels.start}", // 2
						"${labels.end}", // 3
						"${labeltypes.labeltype}", // 4
						"${labelitypes.labelitype}", "${labelitypes.labelitype}", // 5
						// "${labels.bgcolor}", "${labels.bgcolor}", // 6
						// "${labels.fgcolor}", "${labels.fgcolor}", // 7
						"${annotations}", // 2bis
						"${annosets.table}", // 8
						"${sentences.table}", "${sentences.sentenceid}", // 9
						"${layers.table}", "${annosets.annosetid}", // 10
						"${layertypes.table}", "${layertypes.layertypeid}", // 11
						"${labels.table}", "${layers.layerid}", // 12
						"${labeltypes.table}", "${labeltypes.labeltypeid}", // 13
						"${labelitypes.table}", "${labelitypes.labelitypeid}", // 14
						"${annosets.annosetid}", "${labeltypes.labeltypeid}", // 15
						"${layers.layerid}", // 16
						"${layers.rank}", "${layers.layerid}", "${labels.start}", "${labels.end}"); // 17
				break;

			case PATTERNS_LAYERS_X:
				table = String.format("(SELECT %s,%s,%s,%s,%s,%s," + // 1
								"GROUP_CONCAT(%s||':'||" + // 2
								"%s||':'||" + // 3
								"%s||':'||" + // 4
								"CASE WHEN %s IS NULL THEN '' ELSE %s END" + // 5
								// "||':'||CASE WHEN %s IS NULL THEN '' ELSE %s END" + // 6
								// "||':'||CASE WHEN %s IS NULL THEN '' ELSE %s END" + // 7
								// ", '|'" + // GROUP_CONCAT DELIMITER
								") AS %s " + // 2bis
								"FROM %s " + // 8
								"LEFT JOIN %s USING (%s) " + // 9
								"LEFT JOIN %s USING (%s) " + // 10
								"LEFT JOIN %s USING (%s) " + // 11
								"LEFT JOIN %s USING (%s) " + // 12
								"LEFT JOIN %s USING (%s) " + // 13
								"LEFT JOIN %s USING (%s) " + // 14
								"LEFT JOIN %s USING (%s) " + // 15
								"WHERE %s = ? AND %s IS NOT NULL " + // 16
								"GROUP BY %s " + // 17
								"ORDER BY %s,%s,%s,%s)", // 18
						"${annosets.annosetid}", "${sentences.sentenceid}", "${sentences.text}", "${layers.layerid}", "${layertypes.layertype}", "${layers.rank}", // 1
						"${labels.start}", // 2
						"${labels.end}", // 3
						"${labeltypes.labeltype}", // 4
						"${labelitypes.labelitype}", "${labelitypes.labelitype}", // 5
						// "${labels.bgcolor}", "${labels.bgcolor}", // 6
						// "${labels.fgcolor}", "${labels.fgcolor}", // 7
						"${annotations}", // 2bis
						"${grouppatterns_annosets.table}", // 8
						"${annosets.table}", "${annosets.annosetid}", // 9
						"${sentences.table}", "${sentences.sentenceid}", // 10
						"${layers.table}", "${annosets.annosetid}", // 11
						"${layertypes.table}", "${layertypes.layertypeid}", // 12
						"${labels.table}", "${layers.layerid}", // 13
						"${labeltypes.table}", "${labeltypes.labeltypeid}", // 14
						"${labelitypes.table}", "${labelitypes.labelitypeid}", // 15
						"${grouppatterns.patternid}", "${labeltypes.labeltypeid}", // 16
						"${layers.layerid}", // 17
						"${layers.rank}", "${layers.layerid}", "${labels.start}", "${labels.end}"); // 18
				break;

			case VALENCEUNITS_LAYERS_X:
				table = String.format("(SELECT %s,%s,%s,%s,%s,%s," + // 1
								"GROUP_CONCAT(%s||':'||" + // 2
								"%s||':'||" + // 3
								"%s||':'||" + // 4
								"CASE WHEN %s IS NULL THEN '' ELSE %s END" + // 5
								// "||':'||CASE WHEN %s IS NULL THEN '' ELSE %s END" + // 6
								// "||':'||CASE WHEN %s IS NULL THEN '' ELSE %s END" + // 7
								// ", '|'" + // GROUP_CONCAT DELIMITER
								") AS %s " + // 2bis
								"FROM %s " + // 8
								"LEFT JOIN %s USING (%s) " + // 9
								"LEFT JOIN %s USING (%s) " + // 10
								"LEFT JOIN %s USING (%s) " + // 11
								"LEFT JOIN %s USING (%s) " + // 12
								"LEFT JOIN %s USING (%s) " + // 13
								"LEFT JOIN %s USING (%s) " + // 14
								"LEFT JOIN %s USING (%s) " + // 15
								"WHERE %s = ? AND %s IS NOT NULL " + // 16
								"GROUP BY %s " + // 17
								"ORDER BY %s,%s,%s,%s)", // 18
						"${annosets.annosetid}", "${sentences.sentenceid}", "${sentences.text}", "${layers.layerid}", "${layertypes.layertype}", "${layers.rank}", // 1
						"${labels.start}", // 2
						"${labels.end}", // 3
						"${labeltypes.labeltype}", // 4
						"${labelitypes.labelitype}", "${labelitypes.labelitype}", // 5
						// "${labels.bgcolor}", "${labels.bgcolor}", // 6
						// "${labels.fgcolor}", "${labels.fgcolor}", // 7
						"${annotations}", // 2bis
						"${valenceunits_annosets.table}", // 8
						"${annosets.table}", "${annosets.annosetid}", // 9
						"${sentences.table}", "${sentences.sentenceid}", // 10
						"${layers.table}", "${annosets.annosetid}", // 11
						"${layertypes.table}", "${layertypes.layertypeid}", // 12
						"${labels.table}", "${layers.layerid}", // 13
						"${labeltypes.table}", "${labeltypes.labeltypeid}", // 14
						"${labelitypes.table}", "${labelitypes.labelitypeid}", // 15
						"${valenceunits.vuid}", "${labeltypes.labeltypeid}", // 16
						"${layers.layerid}", // 17
						"${layers.rank}", "${layers.layerid}", "${labels.start}", "${labels.end}"); // 18
				break;

			case WORDS_LEXUNITS_FRAMES_FN:
				table = String.format("%s " + // 1
								"INNER JOIN %s USING (%s) " + // 3
								"INNER JOIN %s AS %s USING (%s) " + // 4
								"LEFT JOIN %s AS %s USING (%s) " + // 5
								"LEFT JOIN %s AS %s ON (%s.%s = %s.%s) " + // 6
								"LEFT JOIN %s AS %s ON (%s = %s.%s) " + // 7
								"LEFT JOIN %s AS %s ON (%s.%s = %s.%s AND %s = %s.%s)", // 8
						"${words.table}", // 1
						"${lexemes.table}", "${words.fnwordid}", // 3
						"${lexunits.table}", "${as_lexunits}", "${lexunits.luid}", // 4
						"${frames.table}", "${as_frames}", "${frames.frameid}", // 5
						"${poses.table}", "${as_poses}", "${as_lexunits}", "${poses.posid}", "${as_poses}", "${poses.posid}", // 6
						"${fetypes.table}", "${as_fetypes}", "${lexunits.incorporatedfetypeid}", "${as_fetypes}", "${fetypes.fetypeid}", // 7
						"${fes.table}", "${as_fes}", "${as_frames}", "${frames.frameid}", "${as_fes}", "${frames.frameid}", "${lexunits.incorporatedfetypeid}", "${as_fes}", "${fes.fetypeid}"); // 8
				groupBy = "${lexunits.luid}";
				break;

			case WORDS_LEXUNITS_FRAMES:
				table = String.format("%s " + // 1
								"INNER JOIN %s USING (%s) " + // 2
								"INNER JOIN %s USING (%s) " + // 3
								"INNER JOIN %s AS %s USING (%s) " + // 4
								"LEFT JOIN %s AS %s USING (%s) " + // 5
								"LEFT JOIN %s AS %s ON (%s.%s = %s.%s) " + // 6
								"LEFT JOIN %s AS %s ON (%s = %s.%s) " + // 7
								"LEFT JOIN %s AS %s ON (%s.%s = %s.%s AND %s = %s.%s)", // 8
						"${words.table}", // 1
						"${wnwords.table}", "${wnwords.wordid}", // 2
						"${lexemes.table}", "${words.fnwordid}", // 3
						"${lexunits.table}", "${as_lexunits}", "${lexunits.luid}", // 4
						"${frames.table}", "${as_frames}", "${frames.frameid}", // 5
						"${poses.table}", "${as_poses}", "${as_lexunits}", "${poses.posid}", "${as_poses}", "${poses.posid}", // 6
						"${fetypes.table}", "${as_fetypes}", "${lexunits.incorporatedfetypeid}", "${as_fetypes}", "${fetypes.fetypeid}", // 7
						"${fes.table}", "${as_fes}", "${as_frames}", "${frames.frameid}", "${as_fes}", "${frames.frameid}", "${lexunits.incorporatedfetypeid}", "${as_fes}", "${fes.fetypeid}"); // 8
				groupBy = "${lexunits.luid}";
				break;

			case FRAMES_FES_BY_FE:
				groupBy = "${fes.feid}";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case FRAMES_FES:
				table = String.format("%s " + //
								"INNER JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s)", //
						"${frames.table}", //
						"${fes.table}", "${frames.frameid}", //
						"${fetypes.table}", "${fetypes.fetypeid}", //
						"${coretypes.table}", "${coretypes.coretypeid}", //
						"${fes_semtypes.table}", "${fes.feid}", //
						"${semtypes.table}", "${semtypes.semtypeid}");
				break;

			case LEXUNITS_SENTENCES_BY_SENTENCE:
				groupBy = "${as_sentences}" + ".sentenceid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case LEXUNITS_SENTENCES:
				table = String.format("%s AS %s " + //
								"LEFT JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s) " + //
								"INNER JOIN %s AS %s USING (%s)", //
						"${lexunits.table}", "${as_lexunits}", //
						"${subcorpuses.table}", "${lexunits.luid}", //
						"${subcorpuses_sentences.table}", "${subcorpuses.subcorpusid}", //
						"${sentences.table}", "${as_sentences}", "${sentences.sentenceid}");
				break;

			case LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE:
				groupBy = "${as_sentences}" + ".sentenceid";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS:
				table = String.format("%s AS %s " + //
								"LEFT JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s) " + //
								"INNER JOIN %s AS %s USING (%s) " + //
								"LEFT JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s)", //
						"${lexunits.table}", "${as_lexunits}", //
						"${subcorpuses.table}", "${lexunits.luid}", //
						"${subcorpuses_sentences.table}", "${subcorpuses.subcorpusid}", //
						"${sentences.table}", "${as_sentences}", "${sentences.sentenceid}", //
						"${annosets.table}", "${sentences.sentenceid}", //
						"${layers.table}", "${annosets.annosetid}", //
						"${layertypes.table}", "${layertypes.layertypeid}", //
						"${labels.table}", "${layers.layerid}", //
						"${labeltypes.table}", "${labeltypes.labeltypeid}", //
						"${labelitypes.table}", "${labelitypes.labelitypeid}");
				break;

			case LEXUNITS_GOVERNORS_FN:
				table = String.format("%s " + //
								"INNER JOIN %s USING (%s) " + //
								"INNER JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s)", //
						"${lexunits.table}", //
						"${lexunits_governors.table}", "${lexunits.luid}", //
						"${governors.table}", "${governors.governorid}", //
						"${words.table}", "${words.fnwordid}");
				break;

			case LEXUNITS_GOVERNORS:
				table = String.format("%s " + //
								"INNER JOIN %s USING (%s) " + //
								"INNER JOIN %s USING (%s) " + //
								"INNER JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s)", //
						"${lexunits.table}", //
						"${lexunits_governors.table}", "${lexunits.luid}", //
						"${governors.table}", "${governors.governorid}", //
						"${words.table}", "${words.fnwordid}", //
						"${wnwords.table}", "${wnwords.wordid}" //
				);
				break;

			case GOVERNORS_ANNOSETS:
				table = String.format("%s " + //
								"LEFT JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s)", //
						"${governors_annosets.table}", //
						"${annosets.table}", "${annosets.annosetid}", //
						"${sentences.table}", "${sentences.sentenceid}");
				break;

			case LEXUNITS_REALIZATIONS_BY_REALIZATION:
				groupBy = "${ferealizations.ferid}";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case LEXUNITS_REALIZATIONS:
				table = String.format("%s " + // 1
								"INNER JOIN %s USING (%s) " + // 2
								"LEFT JOIN %s USING (%s) " + // 3
								"LEFT JOIN %s USING (%s) " + // 4
								"LEFT JOIN %s USING (%s) " + // 5
								"LEFT JOIN %s USING (%s) " + // 6
								"LEFT JOIN %s USING (%s)", // 7
						"${lexunits.table}", // 1
						"${ferealizations.table}", "${lexunits.luid}", // 2
						"${ferealizations_valenceunits.table}", "${ferealizations.ferid}", // 3
						"${valenceunits.table}", "${valenceunits.vuid}", // 4
						"${fetypes.table}", "${fetypes.fetypeid}", // 5
						"${gftypes.table}", "${gftypes.gfid}", // 6
						"${pttypes.table}", "${pttypes.ptid}"); // 7
				break;

			case LEXUNITS_GROUPREALIZATIONS_BY_PATTERN:
				groupBy = "${grouppatterns.patternid}";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case LEXUNITS_GROUPREALIZATIONS:
				table = String.format("%s " + // 1
								"INNER JOIN %s USING (%s) " + // 2
								"LEFT JOIN %s USING (%s) " + // 3
								"LEFT JOIN %s USING (%s) " + // 4
								//"LEFT JOIN %s USING (%s,%s) " + // 5
								"LEFT JOIN %s USING (%s) " + // 6
								"LEFT JOIN %s USING (%s) " + // 7
								"LEFT JOIN %s USING (%s) " + // 8
								"LEFT JOIN %s USING (%s)", // 9
						"${lexunits.table}", // 1
						"${fegrouprealizations.table}", "${lexunits.luid}", // 2
						"${grouppatterns.table}", "${fegrouprealizations.fegrid}", // 3
						"${grouppatterns_patterns.table}", "${grouppatterns.patternid}", // 4
						//"${ferealizations_valenceunits.table}","${ferealizations.ferid}","${valenceunits.vuid}", // 5
						"${valenceunits.table}", "${valenceunits.vuid}", // 6
						"${fetypes.table}", "${fetypes.fetypeid}", // 7
						"${gftypes.table}", "${gftypes.gfid}", // 8
						"${pttypes.table}", "${pttypes.ptid}"); // 9
				break;

			case PATTERNS_SENTENCES:
				table = String.format("%s " + //
								"LEFT JOIN %s AS %s USING (%s) " + //
								"LEFT JOIN %s AS %s USING (%s)", //
						"${grouppatterns_annosets.table}", //
						"${annosets.table}", "${as_annosets}", "${annosets.annosetid}", //
						"${sentences.table}", "${as_sentences}", "${sentences.sentenceid}");
				break;

			case VALENCEUNITS_SENTENCES:
				table = String.format("%s " + //
								"LEFT JOIN %s AS %s USING (%s) " + //
								"LEFT JOIN %s AS %s USING (%s)", //
						"${valenceunits_annosets.table}", //
						"${annosets.table}", "${as_annosets}", "${annosets.annosetid}", //
						"${sentences.table}", "${as_sentences}", "${sentences.sentenceid}");
				break;

			// L O O K U P

			case LOOKUP_FTS_WORDS:
				table = String.format("%s_%s_fts4", "${words.table}", "${words.word}");
				break;

			case LOOKUP_FTS_SENTENCES:
				table = String.format("%s_%s_fts4", "${sentences.table}", "${sentences.text}");
				break;

			case LOOKUP_FTS_SENTENCES_X_BY_SENTENCE:
				groupBy = "${sentences.sentenceid}";
				//addProjection(projection, "GROUP_CONCAT(DISTINCT  frame || '@' || frameid)", "GROUP_CONCAT(DISTINCT  lexunit || '@' || luid)");
				//$FALL-THROUGH$
				//noinspection fallthrough

			case LOOKUP_FTS_SENTENCES_X:
				table = String.format("%s_%s_fts4 " + //
								"LEFT JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s,%s)", //
						"${sentences.table}", "${sentences.text}", //
						"${frames.table}", "${frames.frameid}", //
						"${lexunits.table}", "${frames.frameid}", "${lexunits.luid}");
				break;

			// S U G G E S T

			case SUGGEST_WORDS:
			{
				table = "${words.table}";
				projection = new String[]{String.format("%s AS _id", "${words.fnwordid}"), //
						String.format("%s AS %s", "${words.word}", "#{suggest_text_1}"), //
						String.format("%s AS %s", "${words.word}", "#{suggest_query}")};
				selection = String.format("%s LIKE ? || '%%'", "${words.word}");
				selectionArgs = new String[]{String.format("%s", last)};
				break;
			}

			case SUGGEST_FTS_WORDS:
			{
				table = String.format("%s_%s_fts4", "${words.table}", "${words.word}");
				projection = new String[]{String.format("%s AS _id", "${words.fnwordid}"), //
						String.format("%s AS %s", "${words.word}", "#{suggest_text_1}"), //
						String.format("%s AS %s", "${words.word}", "#{suggest_query}")};
				selection = String.format("%s MATCH ?", "${words.word}");
				selectionArgs = new String[]{String.format("%s*", last)};
				break;
			}

			default:
				return null;
		}
		return new String[]{ //
				quote(table), //
				projection == null ? null : "{" + Arrays.stream(projection).map(Factory::quote).collect(Collectors.joining(",")) + "}", //
				quote(selection), //
				selectionArgs == null ? null : "{" + Arrays.stream(selectionArgs).map(Factory::quote).collect(Collectors.joining(",")) + "}", //
				quote(groupBy), //
				quote(sortOrder)};
	}

	@Override
	public String[] get()
	{
		return Arrays.stream(Key.values()).map(Enum::name).toArray(String[]::new);
	}

	private enum Key
	{
		LEXUNITS, FRAMES, ANNOSETS, SENTENCES, WORDS,//
		LEXUNIT1, FRAME1, SENTENCE1, ANNOSET1, //
		LEXUNITS_OR_FRAMES, LEXUNITS_OR_FRAMES_FN, //
		FRAMES_X_BY_FRAME, FRAMES_RELATED, //
		FRAMES_FES, FRAMES_FES_BY_FE, //
		WORDS_LEXUNITS_FRAMES, WORDS_LEXUNITS_FRAMES_FN, LEXUNITS_X_BY_LEXUNIT, //
		LEXUNITS_SENTENCES, LEXUNITS_SENTENCES_BY_SENTENCE, LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE, LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS, //
		LEXUNITS_GOVERNORS, LEXUNITS_GOVERNORS_FN, GOVERNORS_ANNOSETS, //
		LEXUNITS_REALIZATIONS, LEXUNITS_REALIZATIONS_BY_REALIZATION, //
		LEXUNITS_GROUPREALIZATIONS, LEXUNITS_GROUPREALIZATIONS_BY_PATTERN, SENTENCES_LAYERS_X, ANNOSETS_LAYERS_X, PATTERNS_LAYERS_X, VALENCEUNITS_LAYERS_X, //
		PATTERNS_SENTENCES, VALENCEUNITS_SENTENCES, //
		LOOKUP_FTS_WORDS, LOOKUP_FTS_SENTENCES, LOOKUP_FTS_SENTENCES_X_BY_SENTENCE, LOOKUP_FTS_SENTENCES_X, //
		SUGGEST_WORDS, SUGGEST_FTS_WORDS
	}

	private static String quote(String str)
	{
		return str == null ? null : String.format("\"%s\"", str);
	}
}
