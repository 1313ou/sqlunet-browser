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
				table = "(" + // 1
						"SELECT ${words.fnwordid} + 10000 AS ${_id}, ${lexunits.luid} AS ${fnid}, ${words.fnwordid} AS ${words.fnwordid}, ${words.wordid} AS ${words.wordid}, ${words.word} AS ${words.word}, ${lexunits.lexunit} AS ${name}, ${frames.frame} AS ${frames.frame}, ${frames.frameid} AS ${frames.frameid}, 0 AS ${isframe} " + // 2
						"FROM ${words.table} " + // 3
						"INNER JOIN ${lexemes.table} USING (${words.fnwordid}) " + // 4
						"INNER JOIN ${lexunits.table} AS ${as_lexunits} USING (${lexunits.luid}) " + // 5
						"INNER JOIN ${frames.table} AS ${as_frames} USING (${frames.frameid}) " + // 6
						"UNION " + // 7
						"SELECT ${frames.frameid} AS ${_id}, ${frames.frameid} AS ${fnid}, 0 AS ${words.fnwordid}, 0 AS ${words.wordid}, ${frames.frame} AS ${words.word}, ${frames.frame} AS ${name}, ${frames.frame} AS ${frames.frame}, ${frames.frameid} AS ${frames.frameid}, 1 AS ${isframe} " + // 8
						"FROM ${frames.table} " + // 9
						")"; //
				break;

			case LEXUNITS_OR_FRAMES:
				table = "(" + // 1
						"SELECT ${words.fnwordid} + 10000 AS ${_id}, ${lexunits.luid} AS ${fnid}, ${words.fnwordid} AS ${words.fnwordid}, ${words.wordid} AS ${words.wordid}, ${words.word} AS ${words.word}, ${lexunits.lexunit} AS ${name}, ${frames.frame} AS ${frames.frame}, ${frames.frameid} AS ${frames.frameid}, 0 AS ${isframe} " + // 2
						"FROM ${words.table} " + // 3
						"INNER JOIN ${wnwords.table} USING (${wnwords.wordid}) " + // 3b
						"INNER JOIN ${lexemes.table} USING (${words.fnwordid}) " + // 4
						"INNER JOIN ${lexunits.table} AS ${as_lexunits} USING (${lexunits.luid}) " + // 5
						"INNER JOIN ${frames.table} AS ${as_frames} USING (${frames.frameid}) " + // 6
						"UNION " + // 7
						"SELECT ${frames.frameid} AS ${_id}, ${frames.frameid} AS ${fnid}, 0 AS ${words.fnwordid}, 0 AS ${words.wordid}, ${frames.frame} AS ${words.word}, ${frames.frame} AS ${name}, ${frames.frame} AS ${frames.frame}, ${frames.frameid} AS ${frames.frameid}, 1 AS ${isframe} " + // 8
						"FROM ${frames.table} " + // 9
						")"; //
				break;

			case FRAMES_X_BY_FRAME:
				table = "${frames.table} " + //
						"LEFT JOIN ${frames_semtypes.table} USING (${frames.frameid}) " + //
						"LEFT JOIN ${semtypes.table} USING (${semtypes.semtypeid})"; //
				groupBy = "${frames.frameid}";
				break;

			case FRAMES_RELATED:
				table = "${frames_related.table} AS ${as_related_frames} " + //
						"LEFT JOIN ${frames.table} AS ${src_frame} USING (${frames.frameid}) " + //
						"LEFT JOIN ${frames.table} AS ${dest_frame} ON ${frames_related.frame2id} = ${dest_frame}.${frames.frameid} " + //
						"LEFT JOIN ${framerelations.table} USING (${frames_related.relationid})"; //
				break;

			case LEXUNITS_X_BY_LEXUNIT:
				table = "${lexunits.table} AS ${as_lexunits} " + //
						"LEFT JOIN ${frames.table} AS ${as_frames} USING (${frames.frameid}) " + //
						"LEFT JOIN ${poses.table} AS ${as_poses} ON ${as_lexunits}.${poses.posid} = ${as_poses}.${poses.posid} " + //
						"LEFT JOIN ${fetypes.table} AS ${as_fetypes} ON (${lexunits.incorporatedfetypeid} = ${as_fetypes}.${fetypes.fetypeid}) " + //
						"LEFT JOIN ${fes.table} AS ${as_fes} ON (${as_frames}.${frames.frameid} = ${as_fes}.${frames.frameid} AND ${lexunits.incorporatedfetypeid} = ${as_fes}.${fes.fetypeid})"; //
				groupBy = "${lexunits.luid}";
				break;

			case SENTENCES_LAYERS_X:
				table = "(SELECT ${annosets.annosetid},${sentences.sentenceid},${layers.layerid},${layertypes.layertype},${layers.rank}," + // 1
						"GROUP_CONCAT(${labels.start}||':'||" + // 2
						"${labels.end}||':'||" + // 3
						"${labeltypes.labeltype}||':'||" + // 4
						"CASE WHEN ${labelitypes.labelitype} IS NULL THEN '' ELSE ${labelitypes.labelitype} END" + // 5
						// "||':'||CASE WHEN ${labels.bgcolor} IS NULL THEN '' ELSE ${labels.bgcolor} END" + // 6
						// "||':'||CASE WHEN ${labels.fgcolor} IS NULL THEN '' ELSE ${labels.fgcolor} END + // 7
						// ", '|'" + // GROUP_CONCAT DELIMITER
						") AS ${annotations} " + // 2bis
						"FROM ${sentences.table} " + // 8
						"LEFT JOIN ${annosets.table} USING (${sentences.sentenceid}) " + // 9
						"LEFT JOIN ${layers.table} USING (${annosets.annosetid}) " + // 10
						"LEFT JOIN ${layertypes.table} USING (${layertypes.layertypeid}) " + // 11
						"LEFT JOIN ${labels.table} USING (${layers.layerid}) " + // 12
						"LEFT JOIN ${labeltypes.table} USING (${labeltypes.labeltypeid}) " + // 13
						"LEFT JOIN ${labelitypes.table} USING (${labelitypes.labelitypeid}) " + // 14
						"WHERE ${sentences.sentenceid} = ? AND ${labeltypes.labeltypeid} IS NOT NULL " + // 15
						"GROUP BY ${layers.layerid} " + // 16
						"ORDER BY ${layers.rank},${layertypes.layertypeid})"; // 17 "ORDER BY ${layers.rank},${layers.layerid},${labels.start},${labels.end})"
				break;

			case ANNOSETS_LAYERS_X:
				table = "(SELECT ${sentences.sentenceid},${sentences.text},${layers.layerid},${layertypes.layertype},${layers.rank}," + // 1
						"GROUP_CONCAT(${labels.start}||':'||" + // 2
						"${labels.end}||':'||" + // 3
						"${labeltypes.labeltype}||':'||" + // 4
						"CASE WHEN ${labelitypes.labelitype} IS NULL THEN '' ELSE ${labelitypes.labelitype} END" + // 5
						// "||':'||CASE WHEN ${labels.bgcolor} IS NULL THEN '' ELSE ${labels.bgcolor} END" + // 6
						// "||':'||CASE WHEN ${labels.fgcolor} IS NULL THEN '' ELSE ${labels.fgcolor} END
						// ", '|'" + // GROUP_CONCAT DELIMITER
						") AS ${annotations} " + // 2bis
						"FROM ${annosets.table} " + // 8
						"LEFT JOIN ${sentences.table} USING (${sentences.sentenceid}) " + // 9
						"LEFT JOIN ${layers.table} USING (${annosets.annosetid}) " + // 10
						"LEFT JOIN ${layertypes.table} USING (${layertypes.layertypeid}) " + // 11
						"LEFT JOIN ${labels.table} USING (${layers.layerid}) " + // 12
						"LEFT JOIN ${labeltypes.table} USING (${labeltypes.labeltypeid}) " + // 13
						"LEFT JOIN ${labelitypes.table} USING (${labelitypes.labelitypeid}) " + // 14
						"WHERE ${annosets.annosetid} = ? AND ${labeltypes.labeltypeid} IS NOT NULL " + // 15
						"GROUP BY ${layers.layerid} " + // 16
						"ORDER BY ${layers.rank},${layers.layerid},${labels.start},${labels.end})"; // 17
				break;

			case PATTERNS_LAYERS_X:
				table = "(SELECT ${annosets.annosetid},${sentences.sentenceid},${sentences.text},${layers.layerid},${layertypes.layertype},${layers.rank}," + // 1
						"GROUP_CONCAT(${labels.start}||':'||" + // 2
						"${labels.end}||':'||" + // 3
						"${labeltypes.labeltype}||':'||" + // 4
						"CASE WHEN ${labelitypes.labelitype} IS NULL THEN '' ELSE ${labelitypes.labelitype} END" + // 5
						// "||':'||CASE WHEN ${labels.bgcolor} IS NULL THEN '' ELSE ${labels.bgcolor} END" + // 6
						// "||':'||CASE WHEN ${labels.fgcolor} IS NULL THEN '' ELSE ${labels.fgcolor} END" + // 7
						// ", '|'" + // GROUP_CONCAT DELIMITER
						") AS ${annotations} " + // 2bis
						"FROM ${grouppatterns_annosets.table} " + // 8
						"LEFT JOIN ${annosets.table} USING (${annosets.annosetid}) " + // 9
						"LEFT JOIN ${sentences.table} USING (${sentences.sentenceid}) " + // 10
						"LEFT JOIN ${layers.table} USING (${annosets.annosetid}) " + // 11
						"LEFT JOIN ${layertypes.table} USING (${layertypes.layertypeid}) " + // 12
						"LEFT JOIN ${labels.table} USING (${layers.layerid}) " + // 13
						"LEFT JOIN ${labeltypes.table} USING (${labeltypes.labeltypeid}) " + // 14
						"LEFT JOIN ${labelitypes.table} USING (${labelitypes.labelitypeid}) " + // 15
						"WHERE ${grouppatterns.patternid} = ? AND ${labeltypes.labeltypeid} IS NOT NULL " + // 16
						"GROUP BY ${layers.layerid} " + // 17
						"ORDER BY ${layers.rank},${layers.layerid},${labels.start},${labels.end})"; // 18
				break;

			case VALENCEUNITS_LAYERS_X:
				table = "(SELECT ${annosets.annosetid},${sentences.sentenceid},${sentences.text},${layers.layerid},${layertypes.layertype},${layers.rank}," + // 1
						"GROUP_CONCAT(${labels.start}||':'||" + // 2
						"${labels.end}||':'||" + // 3
						"${labeltypes.labeltype}||':'||" + // 4
						"CASE WHEN ${labelitypes.labelitype} IS NULL THEN '' ELSE ${labelitypes.labelitype} END" + // 5
						// "||':'||CASE WHEN ${labels.bgcolor} IS NULL THEN '' ELSE ${labels.bgcolor} END" + // 6
						// "||':'||CASE WHEN ${labels.fgcolor} IS NULL THEN '' ELSE ${labels.fgcolor} END" + // 7
						// ", '|'" + // GROUP_CONCAT DELIMITER
						") AS ${annotations} " + // 2bis
						"FROM ${valenceunits_annosets.table} " + // 8
						"LEFT JOIN ${annosets.table} USING (${annosets.annosetid}) " + // 9
						"LEFT JOIN ${sentences.table} USING (${sentences.sentenceid}) " + // 10
						"LEFT JOIN ${layers.table} USING (${annosets.annosetid}) " + // 11
						"LEFT JOIN ${layertypes.table} USING (${layertypes.layertypeid}) " + // 12
						"LEFT JOIN ${labels.table} USING (${layers.layerid}) " + // 13
						"LEFT JOIN ${labeltypes.table} USING (${labeltypes.labeltypeid}) " + // 14
						"LEFT JOIN ${labelitypes.table} USING (${labelitypes.labelitypeid}) " + // 15
						"WHERE ${valenceunits.vuid} = ? AND ${labeltypes.labeltypeid} IS NOT NULL " + // 16
						"GROUP BY ${layers.layerid} " + // 17
						"ORDER BY ${layers.rank},${layers.layerid},${labels.start},${labels.end})"; // 18
				break;

			case WORDS_LEXUNITS_FRAMES_FN:
				table = "${words.table} " + // 1
						"INNER JOIN ${lexemes.table} USING (${words.fnwordid}) " + // 3
						"INNER JOIN ${lexunits.table} AS ${as_lexunits} USING (${lexunits.luid}) " + // 4
						"LEFT JOIN ${frames.table} AS ${as_frames} USING (${frames.frameid}) " + // 5
						"LEFT JOIN ${poses.table} AS ${as_poses} ON (${as_lexunits}.${poses.posid} = ${as_poses}.${poses.posid}) " + // 6
						"LEFT JOIN ${fetypes.table} AS ${as_fetypes} ON (${lexunits.incorporatedfetypeid} = ${as_fetypes}.${fetypes.fetypeid}) " + // 7
						"LEFT JOIN ${fes.table} AS ${as_fes} ON (${as_frames}.${frames.frameid} = ${as_fes}.${frames.frameid} AND ${lexunits.incorporatedfetypeid} = ${as_fes}.${fes.fetypeid})"; // 8
				groupBy = "${lexunits.luid}";
				break;

			case WORDS_LEXUNITS_FRAMES:
				table = "${words.table} " + // 1
						"INNER JOIN ${wnwords.table} USING (${wnwords.wordid}) " + // 2
						"INNER JOIN ${lexemes.table} USING (${words.fnwordid}) " + // 3
						"INNER JOIN ${lexunits.table} AS ${as_lexunits} USING (${lexunits.luid}) " + // 4
						"LEFT JOIN ${frames.table} AS ${as_frames} USING (${frames.frameid}) " + // 5
						"LEFT JOIN ${poses.table} AS ${as_poses} ON (${as_lexunits}.${poses.posid} = ${as_poses}.${poses.posid}) " + // 6
						"LEFT JOIN ${fetypes.table} AS ${as_fetypes} ON (${lexunits.incorporatedfetypeid} = ${as_fetypes}.${fetypes.fetypeid}) " + // 7
						"LEFT JOIN ${fes.table} AS ${as_fes} ON (${as_frames}.${frames.frameid} = ${as_fes}.${frames.frameid} AND ${lexunits.incorporatedfetypeid} = ${as_fes}.${fes.fetypeid})"; // 8
				groupBy = "${lexunits.luid}";
				break;

			case FRAMES_FES_BY_FE:
				groupBy = "${fes.feid}";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case FRAMES_FES:
				table = "${frames.table} " + //
						"INNER JOIN ${fes.table} USING (${frames.frameid}) " + //
						"LEFT JOIN ${fetypes.table} USING (${fetypes.fetypeid}) " + //
						"LEFT JOIN ${coretypes.table} USING (${coretypes.coretypeid}) " + //
						"LEFT JOIN ${fes_semtypes.table} USING (${fes.feid}) " + //
						"LEFT JOIN ${semtypes.table} USING (${semtypes.semtypeid})"; //
				break;

			case LEXUNITS_SENTENCES_BY_SENTENCE:
				groupBy = "${as_sentences}.${sentences.sentenceid}";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case LEXUNITS_SENTENCES:
				table = "${lexunits.table} AS ${as_lexunits} " + //
						"LEFT JOIN ${subcorpuses.table} USING (${lexunits.luid}) " + //
						"LEFT JOIN ${subcorpuses_sentences.table} USING (${subcorpuses.subcorpusid}) " + //
						"INNER JOIN ${sentences.table} AS ${as_sentences} USING (${sentences.sentenceid})"; //
				break;

			case LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS_BY_SENTENCE:
				groupBy = "${as_sentences}.${sentences.sentenceid}";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case LEXUNITS_SENTENCES_ANNOSETS_LAYERS_LABELS:
				table = "${lexunits.table} AS ${as_lexunits} " + //
						"LEFT JOIN ${subcorpuses.table} USING (${lexunits.luid}) " + //
						"LEFT JOIN ${subcorpuses_sentences.table} USING (${subcorpuses.subcorpusid}) " + //
						"INNER JOIN ${sentences.table} AS ${as_sentences} USING (${sentences.sentenceid}) " + //
						"LEFT JOIN ${annosets.table} USING (${sentences.sentenceid}) " + //
						"LEFT JOIN ${layers.table} USING (${annosets.annosetid}) " + //
						"LEFT JOIN ${layertypes.table} USING (${layertypes.layertypeid}) " + //
						"LEFT JOIN ${labels.table} USING (${layers.layerid}) " + //
						"LEFT JOIN ${labeltypes.table} USING (${labeltypes.labeltypeid}) " + //
						"LEFT JOIN ${labelitypes.table} USING (${labelitypes.labelitypeid})"; //
				break;

			case LEXUNITS_GOVERNORS_FN:
				table = "${lexunits.table} " + //
						"INNER JOIN ${lexunits_governors.table} USING (${lexunits.luid}) " + //
						"INNER JOIN ${governors.table} USING (${governors.governorid}) " + //
						"LEFT JOIN ${words.table} USING (${words.fnwordid})"; //
				break;

			case LEXUNITS_GOVERNORS:
				table = "${lexunits.table} " + //
						"INNER JOIN ${lexunits_governors.table} USING (${lexunits.luid}) " + //
						"INNER JOIN ${governors.table} USING (${governors.governorid}) " + //
						"INNER JOIN ${words.table} USING (${words.fnwordid}) " + //
						"LEFT JOIN ${wnwords.table} USING (${wnwords.wordid})"; //
				break;

			case GOVERNORS_ANNOSETS:
				table = "${governors_annosets.table} " + //
						"LEFT JOIN ${annosets.table} USING (${annosets.annosetid}) " + //
						"LEFT JOIN ${sentences.table} USING (${sentences.sentenceid})"; //
				break;

			case LEXUNITS_REALIZATIONS_BY_REALIZATION:
				groupBy = "${ferealizations.ferid}";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case LEXUNITS_REALIZATIONS:
				table = "${lexunits.table} " + // 1
						"INNER JOIN ${ferealizations.table} USING (${lexunits.luid}) " + // 2
						"LEFT JOIN ${ferealizations_valenceunits.table} USING (${ferealizations.ferid}) " + // 3
						"LEFT JOIN ${valenceunits.table} USING (${valenceunits.vuid}) " + // 4
						"LEFT JOIN ${fetypes.table} USING (${fetypes.fetypeid}) " + // 5
						"LEFT JOIN ${gftypes.table} USING (${gftypes.gfid}) " + // 6
						"LEFT JOIN ${pttypes.table} USING (${pttypes.ptid})"; // 7
				break;

			case LEXUNITS_GROUPREALIZATIONS_BY_PATTERN:
				groupBy = "${grouppatterns.patternid}";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case LEXUNITS_GROUPREALIZATIONS:
				table = "${lexunits.table} " + // 1
						"INNER JOIN ${fegrouprealizations.table} USING (${lexunits.luid}) " + // 2
						"LEFT JOIN ${grouppatterns.table} USING (${fegrouprealizations.fegrid}) " + // 3
						"LEFT JOIN ${grouppatterns_patterns.table} USING (${grouppatterns.patternid}) " + // 4
						//"LEFT JOIN ${ferealizations_valenceunits.table} USING (${ferealizations.ferid},${valenceunits.vuid}) " + // 5
						"LEFT JOIN ${valenceunits.table} USING (${valenceunits.vuid}) " + // 6
						"LEFT JOIN ${fetypes.table} USING (${fetypes.fetypeid}) " + // 7
						"LEFT JOIN ${gftypes.table} USING (${gftypes.gfid}) " + // 8
						"LEFT JOIN ${pttypes.table} USING (${pttypes.ptid})"; // 9
				break;

			case PATTERNS_SENTENCES:
				table = "${grouppatterns_annosets.table} " + //
						"LEFT JOIN ${annosets.table} AS ${as_annosets} USING (${annosets.annosetid}) " + //
						"LEFT JOIN ${sentences.table} AS ${as_sentences} USING (${sentences.sentenceid})"; //
				break;

			case VALENCEUNITS_SENTENCES:
				table = "${valenceunits_annosets.table} " + //
						"LEFT JOIN ${annosets.table} AS ${as_annosets} USING (${annosets.annosetid}) " + //
						"LEFT JOIN ${sentences.table} AS ${as_sentences} USING (${sentences.sentenceid})"; //
				break;

			// L O O K U P

			case LOOKUP_FTS_WORDS:
				table = "${words.table}_${words.word}_fts4";
				break;

			case LOOKUP_FTS_SENTENCES:
				table = "${sentences.table}_${sentences.text}_fts4";
				break;

			case LOOKUP_FTS_SENTENCES_X_BY_SENTENCE:
				groupBy = "${sentences.sentenceid}";
				//addProjection(projection, "GROUP_CONCAT(DISTINCT  frame || '@' || frameid)", "GROUP_CONCAT(DISTINCT  lexunit || '@' || luid)");
				//$FALL-THROUGH$
				//noinspection fallthrough

			case LOOKUP_FTS_SENTENCES_X:
				table = "${sentences.table}_${sentences.text}_fts4 " + //
						"LEFT JOIN ${frames.table} USING (${frames.frameid}) " + //
						"LEFT JOIN ${lexunits.table} USING (${frames.frameid},${lexunits.luid})"; //
				break;

			// S U G G E S T

			case SUGGEST_WORDS:
			{
				table = "${words.table}";
				projection = new String[]{ //
						"${words.fnwordid} AS _id", //
						"${words.word} AS #{suggest_text_1}", //
						"${words.word} AS #{suggest_query}" //
				};
				selection = "${words.word} LIKE ? || '%'";
				selectionArgs = new String[]{String.format("%s", last)};
				break;
			}

			case SUGGEST_FTS_WORDS:
			{
				table = "${words.table}_${words.word}_fts4";
				projection = new String[]{ //
						"${words.fnwordid} AS _id", //
						"${words.word} AS #{suggest_text_1}", //
						"${words.word} AS #{suggest_query}" //
				};
				selection = "${words.word} MATCH ?";
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
