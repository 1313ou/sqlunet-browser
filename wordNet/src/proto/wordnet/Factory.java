/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * WordNet provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class Factory implements Function<String, String[]>, Supplier<String[]>
{
	//# instantiated at runtime
	static public final String URI_LAST = "#{uri_last}";
	static public final String SUBQUERY = "#{query}";

	//# column aliases at runtime
	static public final String MEMBERS = "${members}";
	static public final String MEMBERS2 = "${members2}";
	// static public final String SYNSET2ID = "${synset2id}";
	// static public final String WORD2ID = "${word2id}";
	// static public final String WORD2 = "${word2}";

	public Factory()
	{
		System.out.println("WN Factory");
	}

	@Override
	public String[] apply(String keyName)
	{
		Key key = Key.valueOf(keyName);
		return apply(key).toStrings();
	}

	public Result apply(final Key key)
	{
		Result r = new Result();
		switch (key)
		{
			// T A B L E

			// table uri : last element is table

			case LEXES:
				r.table = "${lexes.table}";
				break;
			case WORDS:
				r.table = "${words.table}";
				break;
			case CASEDWORDS:
				r.table = "${casedwords.table}";
				break;
			case PRONUNCIATIONS:
				r.table = "${pronunciations.table}";
				break;
			case MORPHS:
				r.table = "${morphs.table}";
				break;
			case SENSES:
				r.table = "${senses.table}";
				break;
			case SYNSETS:
				r.table = "${synsets.table}";
				break;
			case SEMRELATIONS:
				r.table = "${semrelations.table}";
				break;
			case LEXRELATIONS:
				r.table = "${lexrelations.table}";
				break;
			case RELATIONS:
				r.table = "${relations.table}";
				break;
			case POSES:
				r.table = "${poses.table}";
				break;
			case DOMAINS:
				r.table = "${domains.table}";
				break;
			case ADJPOSITIONS:
				r.table = "${adjpositions.table}";
				break;
			case VFRAMES:
				r.table = "${vframes.table}";
				break;
			case VTEMPLATES:
				r.table = "${vtemplates.table}";
				break;
			case SAMPLES:
				r.table = "${samples.table}";
				break;
			case USAGES:
				r.table = "${usages.table}";
				break;
			case ILIS:
				r.table = "${ilis.table}";
				break;
			case WIKIDATAS:
				r.table = "${wikidatas.table}";
				break;

			// I T E M

			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case WORD1:
				r.table = "${words.table}";
				r.selection = "${words.wordid} = #{uri_last}";
				break;

			case SENSE1:
				r.table = "${senses.table}";
				r.selection = "${senses.senseid} = #{uri_last}";
				break;

			case SYNSET1:
				r.table = "${synsets.table}";
				r.selection = "${synsets.synsetid} = #{uri_last}";
				break;

			case SAMPLE1:
				r.table = "${samples.table}";
				r.selection = "${samples.sampleid} = #{uri_last}";
				break;

			case USAGE1:
				r.table = "${usages.table}";
				r.selection = "${usages.synsetid} = #{uri_last}";
				break;

			case ILI1:
				r.table = "${ilis.table}";
				r.selection = "${ilis.synsetid} = #{uri_last}";
				break;

			case WIKIDATA1:
				r.table = "${wikidatas.table}";
				r.selection = "${wikidatas.synsetid} = #{uri_last}";
				break;

			// V I E W S

			case DICT:
				r.table = "${dict.table}";
				break;

			// J O I N S

			case WORDS_SENSES_SYNSETS:
				r.table = "${words.table} AS ${as_words} " + //
						"LEFT JOIN ${senses.table} AS ${as_senses} USING (${senses.wordid}) " + //
						"LEFT JOIN ${synsets.table} AS ${as_synsets} USING (${synsets.synsetid})";
				break;

			case WORDS_SENSES_CASEDWORDS_SYNSETS:
				r.table = "${words.table} AS ${as_words} " + //
						"LEFT JOIN ${senses.table} AS ${as_senses} USING (${senses.wordid}) " + //
						"LEFT JOIN ${casedwords.table} AS ${as_caseds} USING (${casedwords.wordid},${casedwords.casedwordid}) " + //
						"LEFT JOIN ${synsets.table} AS ${as_synsets} USING (${synsets.synsetid})";
				break;

			case WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS:
				r.table = "${lexes.table} AS ${as_lexes} " + // 1
						"INNER JOIN ${words.table} AS ${as_words} USING (${words.wordid}) " + // 2
						"LEFT JOIN ${casedwords.table} AS ${as_caseds} USING (${casedwords.wordid},${casedwords.casedwordid}) " + // 3
						"LEFT JOIN ${senses.table} AS ${as_senses} USING (${lexes.luid},${words.wordid}) " + // 4
						"LEFT JOIN ${synsets.table} AS ${as_synsets} USING (${synsets.synsetid}) " + // 5
						"LEFT JOIN ${poses.table} AS ${as_poses} USING (${poses.posid}) " + // 6
						"LEFT JOIN ${domains.table} AS ${as_domains} USING (${domains.domainid})"; // 7
				break;

			case WORDS_SENSES_CASEDWORDS_PRONUNCIATIONS_SYNSETS_POSES_DOMAINS:
				r.table = "${lexes.table} AS ${as_lexes} " + // 1
						"INNER JOIN ${words.table} AS ${as_words} USING (${words.wordid}) " + // 2
						"LEFT JOIN ${casedwords.table} AS ${as_caseds} USING (${casedwords.wordid},${casedwords.casedwordid}) " + // 3
						"LEFT JOIN ${lexes_pronunciations.table} USING (${lexes.luid},${words.wordid},${poses.posid}) " + // 4
						"LEFT JOIN ${pronunciations.table} AS ${as_pronunciations} USING (${pronunciations.pronunciationid}) " + // 5
						"LEFT JOIN ${senses.table} AS ${as_senses} USING (${lexes.luid},${words.wordid}) " + // 6
						"LEFT JOIN ${synsets.table} AS ${as_synsets} USING (${synsets.synsetid}) " + // 7
						"LEFT JOIN ${poses.table} AS ${as_poses} USING (${poses.posid}) " + // 8
						"LEFT JOIN ${domains.table} AS ${as_domains} USING (${domains.domainid})"; // 9
				r.groupBy = "${senses.senseid}";
				break;

			case SENSES_WORDS:
				r.table = "${senses.table} AS ${as_senses} " + //
						"LEFT JOIN ${words.table} AS ${as_words} USING (${senses.wordid})"; //
				break;

			case SENSES_WORDS_BY_SYNSET:
				r.table = "${senses.table} AS ${as_senses} " + //
						"LEFT JOIN ${words.table} AS ${as_words} USING (${words.wordid})"; //
				r.projection = new String[]{"GROUP_CONCAT(DISTINCT ${as_words}.${words.word}) AS ${members}"};
				r.groupBy = "${synsets.synsetid}";
				break;

			case SENSES_SYNSETS_POSES_DOMAINS:
				r.table = "${senses.table} AS ${as_senses} " + //
						"INNER JOIN ${synsets.table} AS ${as_synsets} USING (${synsets.synsetid}) " + //
						"LEFT JOIN ${poses.table} AS ${as_poses} USING (${poses.posid}) " + //
						"LEFT JOIN ${domains.table} AS ${as_domains} USING (${domains.domainid})"; //
				break;

			case SYNSETS_POSES_DOMAINS:
				r.table = "${synsets.table} AS ${as_synsets} " + //
						"LEFT JOIN ${poses.table} AS ${as_poses} USING (${poses.posid}) " + //
						"LEFT JOIN ${domains.table} AS ${as_domains} USING (${domains.domainid})"; //
				break;

			// RELATIONS

			case SEMRELATIONS_QUERY:
				r.table = "semrelations";
				r.projection = new String[]{"${anyrelations.relationid}", "NULL AS ${anyrelations.word1id}", "${anyrelations.synset1id}", "NULL AS ${anyrelations.word2id}", "${anyrelations.synset2id}", "'sem' AS ${relationtype}"};
				r.selection = "${anyrelations.synset1id} = ?";
				break;

			case LEXRELATIONS_QUERY:
				r.table = "lexrelations";
				r.projection = new String[]{"${anyrelations.relationid}", "${anyrelations.word1id}", "${anyrelations.synset1id}", "${anyrelations.word2id}", "${anyrelations.synset2id}", "'lex' AS ${relationtype}"};
				r.selection = "${anyrelations.synset1id} = ? AND ${anyrelations.word1id} = ?";
				break;

			case ANYRELATIONS_QUERY:
				Result q1 = apply(Key.SEMRELATIONS_QUERY);
				Result q2 = apply(Key.LEXRELATIONS_QUERY);
				r.table = " ( SELECT " + String.join(",", q1.projection) + " FROM " + q1.table + " WHERE (" + q1.selection + ") UNION SELECT " + String.join(",", q2.projection) + " FROM " + q2.table + " WHERE (" + q2.selection + ") ) ";
				r.table = String.format(" ( SELECT %s FROM %s WHERE (%s) UNION SELECT %s FROM %s WHERE (%s) ) ", //
						String.join(",", q1.projection), q1.table, q1.selection, //
						String.join(",", q2.projection), q2.table, q2.selection);
				break;

			case ANYRELATIONS_SENSES_WORDS_X_BY_SYNSET:
				Result q3 = apply(Key.ANYRELATIONS_QUERY);
				r.table = "( " + SUBQUERY + " ) AS ${as_relations} " + // 1
						"INNER JOIN ${relations.table} USING (${relations.relationid}) " + // 2
						"INNER JOIN ${synsets.table} AS ${as_synsets2} ON ${as_relations}.${anyrelations.synset2id} = ${as_synsets2}.${synsets.synsetid} " + // 3
						"LEFT JOIN ${senses.table} AS ${as_senses2} ON ${as_synsets2}.${synsets.synsetid} = ${as_senses2}.${senses.synsetid} " + // 4
						"LEFT JOIN ${words.table} AS ${as_words} USING (${words.wordid}) " + // 5
						"LEFT JOIN ${words.table} AS ${as_words2} ON ${as_relations}.${anyrelations.word2id} = ${as_words2}.${words.wordid}"; // 6
				r.table = r.table.replace(SUBQUERY, q3.table);
				r.groupBy = "${synset2id},${relationtype},${relations.relation},${relations.relationid},${word2id},${word2}";
				break;

			case SEMRELATIONS_SYNSETS:
				r.table = "${semrelations.table} AS ${as_relations} " + //
						"INNER JOIN ${synsets.table} AS ${as_synsets2} ON ${as_relations}.${semrelations.synset2id} = ${as_synsets2}.${synsets.synsetid}"; //
				break;

			case SEMRELATIONS_SYNSETS_X:
				r.table = "${semrelations.table} AS ${as_relations} " + //
						"INNER JOIN ${relations.table} USING (${relations.relationid}) " + //
						"INNER JOIN ${synsets.table} AS ${as_synsets2} ON ${as_relations}.${semrelations.synset2id} = ${as_synsets2}.${synsets.synsetid}"; //
				break;

			case SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET:
				r.table = "${semrelations.table} AS ${as_relations} " + // 1
						"INNER JOIN ${relations.table} USING (${relations.relationid}) " + // 2
						"INNER JOIN ${synsets.table} AS ${as_synsets2} ON ${as_relations}.${semrelations.synset2id} = ${as_synsets2}.${synsets.synsetid} " + // 3
						"LEFT JOIN ${senses.table} AS ${as_senses2} ON ${as_synsets2}.${synsets.synsetid} = ${as_senses2}.${senses.synsetid} " + // 4
						"LEFT JOIN ${words.table} AS ${as_words2} USING (${words.wordid})"; // 5
				r.projection = new String[]{"GROUP_CONCAT(DISTINCT ${as_words2}.${words.word} || '|' || CASE WHEN ${as_senses2}.${senses.tagcount} IS NULL THEN '' ELSE ${as_senses2}.${senses.tagcount} END) AS ${members2}"};
				r.groupBy = "${as_synsets2}.${synsets.synsetid}";
				break;

			case LEXRELATIONS_SENSES:
				r.table = "${lexrelations.table} AS ${as_relations} " + //
						"INNER JOIN ${synsets.table} AS ${as_synsets2} ON ${as_relations}.${lexrelations.synset2id} = ${as_synsets2}.${synsets.synsetid} " + //
						"INNER JOIN ${words.table} AS ${as_words} ON ${as_relations}.${lexrelations.word2id} = ${as_words}.${words.wordid}"; //
				break;

			case LEXRELATIONS_SENSES_X:
				r.table = "${lexrelations.table} AS ${as_relations} " + //
						"INNER JOIN ${relations.table} USING (${relations.relationid}) " + //
						"INNER JOIN ${synsets.table} AS ${as_synsets2} ON ${as_relations}.${lexrelations.synset2id} = ${as_synsets2}.${synsets.synsetid} " + //
						"INNER JOIN ${words.table} AS ${as_words} ON ${as_relations}.${lexrelations.word2id} = ${as_words}.${words.wordid}"; //
				break;

			case LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET:
				r.table = "${lexrelations.table} AS ${as_relations} " + // 1
						"INNER JOIN ${relations.table} USING (${relations.relationid}) " + // 2
						"INNER JOIN ${synsets.table} AS ${as_synsets2} ON ${as_relations}.${lexrelations.synset2id} = ${as_synsets2}.${synsets.synsetid} " + // 3
						"INNER JOIN ${words.table} AS ${as_words} ON ${as_relations}.${lexrelations.word2id} = ${as_words}.${words.wordid} " + // 4
						"LEFT JOIN ${senses.table} AS ${as_senses2} ON ${as_synsets2}.${senses.synsetid} = ${as_senses2}.${senses.synsetid} " + // 5
						"LEFT JOIN ${words.table} AS ${as_words2} USING (${words.wordid})"; // 6
				r.projection = new String[]{"GROUP_CONCAT(DISTINCT ${as_words2}.${words.word} || '|' || CASE WHEN ${as_senses2}.${senses.tagcount} IS NULL THEN '' ELSE ${as_senses2}.${senses.tagcount} END) AS ${members2}"};
				r.groupBy = "${as_synsets2}.${synsets.synsetid}";
				break;

			// JOINS

			case SENSES_VFRAMES:
				r.table = "${senses_vframes.table} " + //
						"LEFT JOIN ${vframes.table} USING (${vframes.frameid})"; //
				break;

			case SENSES_VTEMPLATES:
				r.table = "${senses_vtemplates.table} " + //
						"LEFT JOIN ${vtemplates.table} USING (${vtemplates.templateid})"; //
				break;

			case SENSES_ADJPOSITIONS:
				r.table = "${senses_adjpositions.table} " + //
						"LEFT JOIN ${adjpositions.table} USING (${adjpositions.positionid})"; //
				break;

			case LEXES_MORPHS:
				r.table = "${lexes_morphs.table} " + //
						"LEFT JOIN ${morphs.table} USING (${morphs.morphid})"; //
				break;

			case WORDS_LEXES_MORPHS_BY_WORD:
				r.groupBy = "${words.wordid}";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case WORDS_LEXES_MORPHS:
				r.table = "${words.table} " + //
						"LEFT JOIN ${lexes_morphs.table} USING (${words.wordid}) " + //
						"LEFT JOIN ${morphs.table} USING (${morphs.morphid})"; //
				break;

			// T E X T S E A R C H

			case LOOKUP_FTS_WORDS:
				r.table = "${words.table}_${words.word}_fts4";
				break;

			case LOOKUP_FTS_DEFINITIONS:
				r.table = "${synsets.table}_${synsets.definition}_fts4";
				break;

			case LOOKUP_FTS_SAMPLES:
				r.table = "${samples.table}_${samples.sample}_fts4";
				break;

			// S U G G E S T

			case SUGGEST_WORDS:
			{
				r.table = "${words.table}";
				r.projection = new String[]{ //
						"${words.wordid} AS _id", //
						"${words.word} AS #{suggest_text_1}", //
						"${words.word} AS #{suggest_query}"};
				r.selection = "${words.word} LIKE ? || '%'";
				r.selectionArgs = new String[]{URI_LAST};
				break;
			}

			case SUGGEST_FTS_WORDS:
			{
				r.table = "@{words.table}_@{words.word}_fts4";
				r.projection = new String[]{ //
						"${words.wordid} AS _id", //
						"${words.word} AS #{suggest_text_1}", //
						"${words.word} AS #{suggest_query}"}; //
				r.selection = "${words.word} MATCH ?";
				r.selectionArgs = new String[]{URI_LAST + '*'};
				break;
			}

			case SUGGEST_FTS_DEFINITIONS:
			{
				r.table = "@{synsets.table}_@{synsets.definition}_fts4";
				r.projection = new String[]{ //
						"${synsets.synsetid} AS _id", //
						"${synsets.definition} AS #{suggest_text_1}", //
						"${synsets.definition} AS #{suggest_query}"};
				r.selection = "${synsets.definition} MATCH ?";
				r.selectionArgs = new String[]{URI_LAST + '*'};
				break;
			}

			case SUGGEST_FTS_SAMPLES:
			{
				r.table = "@{samples.table}_@{samples.sample}_fts4";
				r.projection = new String[]{ //
						"${synsets.synsetid} AS _id", //
						"${samples.sample} AS #{suggest_text_1}", //
						"${samples.sample} AS #{suggest_query}"};
				r.selection = "${samples.sample} MATCH ?";
				r.selectionArgs = new String[]{URI_LAST + '*'};
				break;
			}

			default:
				return null;
		}
		return r;
	}

	@Override
	public String[] get()
	{
		return Arrays.stream(Key.values()).map(Enum::name).toArray(String[]::new);
	}

	private enum Key
	{
		LEXES, WORDS, CASEDWORDS, PRONUNCIATIONS, SENSES, SYNSETS, POSES, DOMAINS, //
		RELATIONS, SEMRELATIONS, LEXRELATIONS, //
		ADJPOSITIONS, MORPHS, SAMPLES, VFRAMES, VTEMPLATES, //
		LEXES_MORPHS, SENSES_VFRAMES, SENSES_VTEMPLATES, SENSES_ADJPOSITIONS, //
		USAGES, ILIS, WIKIDATAS, //
		DICT, //
		WORD1, SENSE1, SYNSET1, //
		SAMPLE1, USAGE1, ILI1, WIKIDATA1, //
		WORDS_LEXES_MORPHS, WORDS_LEXES_MORPHS_BY_WORD, WORDS_SENSES_SYNSETS, WORDS_SENSES_CASEDWORDS_SYNSETS, WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS, WORDS_SENSES_CASEDWORDS_PRONUNCIATIONS_SYNSETS_POSES_DOMAINS, //
		SENSES_WORDS, SENSES_WORDS_BY_SYNSET, SENSES_SYNSETS_POSES_DOMAINS, //
		SYNSETS_POSES_DOMAINS, //
		ANYRELATIONS_SENSES_WORDS_X_BY_SYNSET, //
		SEMRELATIONS_QUERY, LEXRELATIONS_QUERY, ANYRELATIONS_QUERY, //
		SEMRELATIONS_SYNSETS, SEMRELATIONS_SYNSETS_X, SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET, //
		LEXRELATIONS_SENSES, LEXRELATIONS_SENSES_X, LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET, //
		LOOKUP_FTS_DEFINITIONS, LOOKUP_FTS_SAMPLES, LOOKUP_FTS_WORDS, //
		SUGGEST_FTS_DEFINITIONS, SUGGEST_FTS_SAMPLES, SUGGEST_FTS_WORDS, SUGGEST_WORDS,
	}

	static class Result
	{
		String table = null;
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String sortOrder = null;

		private static String quote(String str)
		{
			return str == null ? null : '"' + str + '"';
		}

		String[] toStrings()
		{
			return new String[]{ //
					quote(table), //
					projection == null ? null : "{" + Arrays.stream(projection).map(Result::quote).collect(Collectors.joining(",")) + "}", //
					quote(selection), //
					selectionArgs == null ? null : "{" + Arrays.stream(selectionArgs).map(Result::quote).collect(Collectors.joining(",")) + "}", //
					quote(groupBy), //
					quote(sortOrder)};
		}
	}
}
