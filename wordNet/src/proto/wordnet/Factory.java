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
				r.table = String.format("%s AS %s " + //
								"LEFT JOIN %s AS %s USING (%s) " + //
								"LEFT JOIN %s AS %s USING (%s)", //
						"${words.table}", "${as_words}", //
						"${senses.table}", "${as_senses}", "${senses.wordid}", //
						"${synsets.table}", "${as_synsets}", "${synsets.synsetid}");
				break;

			case WORDS_SENSES_CASEDWORDS_SYNSETS:
				r.table = String.format("%s AS %s " + //
								"LEFT JOIN %s AS %s USING (%s) " + //
								"LEFT JOIN %s AS %s USING (%s,%s) " + //
								"LEFT JOIN %s AS %s USING (%s)", //
						"${words.table}", "${as_words}", //
						"${senses.table}", "${as_senses}", "${senses.wordid}", //
						"${casedwords.table}", "${as_caseds}", "${casedwords.wordid}", "${casedwords.casedwordid}", //
						"${synsets.table}", "${as_synsets}", "${synsets.synsetid}");
				break;

			case WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS:
				r.table = String.format("%s AS %s " + // 1
								"INNER JOIN %s AS %s USING (%s) " + // 2
								"LEFT JOIN %s AS %s USING (%s,%s) " + // 3
								"LEFT JOIN %s AS %s USING (%s,%s) " + // 4
								"LEFT JOIN %s AS %s USING (%s) " + // 5
								"LEFT JOIN %s AS %s USING (%s) " + // 6
								"LEFT JOIN %s AS %s USING (%s)", // 7
						"${lexes.table}", "${as_lexes}", // 1
						"${words.table}", "${as_words}", "${words.wordid}", // 2
						"${casedwords.table}", "${as_caseds}", "${casedwords.wordid}", "${casedwords.casedwordid}", // 3
						"${senses.table}", "${as_senses}", "${lexes.luid}", "${words.wordid}", // 4
						"${synsets.table}", "${as_synsets}", "${synsets.synsetid}", // 5
						"${poses.table}", "${as_poses}", "${poses.posid}", // 6
						"${domains.table}", "${as_domains}", "${domains.domainid}"); // 7
				break;

			case WORDS_SENSES_CASEDWORDS_PRONUNCIATIONS_SYNSETS_POSES_DOMAINS:
				r.table = String.format("%s AS %s " + // 1
								"INNER JOIN %s AS %s USING (%s) " + // 2
								"LEFT JOIN %s AS %s USING (%s,%s) " + // 3
								"LEFT JOIN %s USING (%s,%s,%s) " + // 4
								"LEFT JOIN %s AS %s USING (%s) " + // 5
								"LEFT JOIN %s AS %s USING (%s,%s) " + // 6
								"LEFT JOIN %s AS %s USING (%s) " + // 7
								"LEFT JOIN %s AS %s USING (%s) " + // 8
								"LEFT JOIN %s AS %s USING (%s)", // 9
						"${lexes.table}", "${as_lexes}", // 1
						"${words.table}", "${as_words}", "${words.wordid}", // 2
						"${casedwords.table}", "${as_caseds}", "${casedwords.wordid}", "${casedwords.casedwordid}", // 3
						"${lexes_pronunciations.table}", "${lexes.luid}", "${words.wordid}", "${poses.posid}", // 4
						"${pronunciations.table}", "${as_pronunciations}", "${pronunciations.pronunciationid}", // 5
						"${senses.table}", "${as_senses}", "${lexes.luid}", "${words.wordid}", // 6
						"${synsets.table}", "${as_synsets}", "${synsets.synsetid}", // 7
						"${poses.table}", "${as_poses}", "${poses.posid}", // 8
						"${domains.table}", "${as_domains}", "${domains.domainid}"); // 9
				r.groupBy = "${senses.senseid}";
				break;

			case SENSES_WORDS:
				r.table = String.format("%s AS %s " + //
								"LEFT JOIN %s AS %s USING (%s)", //
						"${senses.table}", "${as_senses}", //
						"${words.table}", "${as_words}", "${senses.wordid}");
				break;

			case SENSES_WORDS_BY_SYNSET:
				r.table = String.format("%s AS %s " + //
								"LEFT JOIN %s AS %s USING (%s)", //
						"${senses.table}", "${as_senses}", //
						"${words.table}", "${as_words}", "${words.wordid}");
				r.projection = new String[]{String.format("GROUP_CONCAT(DISTINCT %s.%s) AS %s", "${as_words}", "${words.word}", MEMBERS)};
				r.groupBy = "${synsets.synsetid}";
				break;

			case SENSES_SYNSETS_POSES_DOMAINS:
				r.table = String.format("%s AS %s " + //
								"INNER JOIN %s AS %s USING (%s) " + //
								"LEFT JOIN %s AS %s USING (%s) " + //
								"LEFT JOIN %s AS %s USING (%s)", //
						"${senses.table}", "${as_senses}", //
						"${synsets.table}", "${as_synsets}", "${synsets.synsetid}", //
						"${poses.table}", "${as_poses}", "${poses.posid}", //
						"${domains.table}", "${as_domains}", "${domains.domainid}");
				break;

			case SYNSETS_POSES_DOMAINS:
				r.table = String.format("%s AS %s " + //
								"LEFT JOIN %s AS %s USING (%s) " + //
								"LEFT JOIN %s AS %s USING (%s)", //
						"${synsets.table}", "${as_synsets}", //
						"${poses.table}", "${as_poses}", "${poses.posid}",//
						"${domains.table}", "${as_domains}", "${domains.domainid}");
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
				r.table = String.format("( %s ) AS %s " + // 1
										"INNER JOIN %s USING (%s) " + // 2
										"INNER JOIN %s AS %s ON %s.%s = %s.%s " + // 3
										"LEFT JOIN %s ON %s.%s = %s.%s " + // 4
										"LEFT JOIN %s AS %s USING (%s) " + // 5
										"LEFT JOIN %s AS %s ON %s.%s = %s.%s", // 6
								SUBQUERY, "${as_relations}", // 1
								"${relations.table}", "${relations.relationid}", // 2
								"${synsets.table}", "${as_synsets2}", "${as_relations}", "${anyrelations.synset2id}", "${as_synsets2}", "${synsets.synsetid}", // 3
								"${senses.table}", "${as_synsets2}", "${synsets.synsetid}", "${senses.table}", "${senses.synsetid}", // 4
								"${words.table}", "${as_words}", "${words.wordid}", //
								"${words.table}", "${as_words2}", "${as_relations}", "${anyrelations.word2id}", "${as_words2}", "${words.wordid}") //
						.replace(SUBQUERY, q3.table);
				r.groupBy = String.format("%s,%s,%s,%s,%s,%s", "${synset2id}", "${relationtype}", "${relations.relation}", "${relations.relationid}", "${word2id}", "${word2}");
				break;

			case SEMRELATIONS_SYNSETS:
				r.table = String.format("%s AS %s " + //
								"INNER JOIN %s AS %s ON %s.%s = %s.%s", //
						"${semrelations.table}", "${as_relations}", //
						"${synsets.table}", "${as_synsets2}", "${as_relations}", "${semrelations.synset2id}", "${as_synsets2}", "${synsets.synsetid}");
				break;

			case SEMRELATIONS_SYNSETS_X:
				r.table = String.format("%s AS %s " + //
								"INNER JOIN %s USING (%s) " + //
								"INNER JOIN %s AS %s ON %s.%s = %s.%s ", //
						"${semrelations.table}", "${as_relations}", //
						"${relations.table}", "${relations.relationid}", //
						"${synsets.table}", "${as_synsets2}", "${as_relations}", "${semrelations.synset2id}", "${as_synsets2}", "${synsets.synsetid}");
				break;

			case SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET:
				r.table = String.format("%s AS %s " + // 1
								"INNER JOIN %s USING (%s) " + // 2
								"INNER JOIN %s AS %s ON %s.%s = %s.%s " + // 3
								"LEFT JOIN %s ON %s.%s = %s.%s " + // 4
								"LEFT JOIN %s AS %s USING (%s)", // 5
						"${semrelations.table}", "${as_relations}", // 1
						"${relations.table}", "${relations.relationid}", // 2
						"${synsets.table}", "${as_synsets2}", "${as_relations}", "${semrelations.synset2id}", "${as_synsets2}", "${synsets.synsetid}", // 3
						"${senses.table}", "${as_synsets2}", "${synsets.synsetid}", "${senses.table}", "${senses.synsetid}", // 4
						"${words.table}", "${as_words2}", "${words.wordid}"); //5
				r.projection = new String[]{String.format("GROUP_CONCAT(DISTINCT %s.%s) AS %s", "${as_words2}", "${words.word}", MEMBERS2)};
				r.groupBy = String.format("%s.%s", "${as_synsets2}", "${synsets.synsetid}");
				break;

			case LEXRELATIONS_SENSES:
				r.table = String.format("%s AS %s " + //
								"INNER JOIN %s AS %s ON %s.%s = %s.%s " + //
								"INNER JOIN %s AS %s ON %s.%s = %s.%s", //
						"${lexrelations.table}", "${as_relations}", //
						"${synsets.table}", "${as_synsets2}", "${as_relations}", "${lexrelations.synset2id}", "${as_synsets2}", "${synsets.synsetid}", //
						"${words.table}", "${as_words}", "${as_relations}", "${lexrelations.word2id}", "${as_words}", "${words.wordid}");
				break;

			case LEXRELATIONS_SENSES_X:
				r.table = String.format("%s AS %s " + //
								"INNER JOIN %s USING (%s) " + //
								"INNER JOIN %s AS %s ON %s.%s = %s.%s " + //
								"INNER JOIN %s AS %s ON %s.%s = %s.%s ", //
						"${lexrelations.table}", "${as_relations}", //
						"${relations.table}", "${relations.relationid}", //
						"${synsets.table}", "${as_synsets2}", "${as_relations}", "${lexrelations.synset2id}", "${as_synsets2}", "${synsets.synsetid}", //
						"${words.table}", "${as_words}", "${as_relations}", "${lexrelations.word2id}", "${as_words}", "${words.wordid}");
				break;

			case LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET:
				r.table = String.format("%s AS %s " + // 1
								"INNER JOIN %s USING (%s) " + // 2
								"INNER JOIN %s AS %s ON %s.%s = %s.%s " + // 3
								"INNER JOIN %s AS %s ON %s.%s = %s.%s " + // 4
								"LEFT JOIN %s AS %s ON %s.%s = %s.%s " + // 5
								"LEFT JOIN %s AS %s USING (%s)", // 6
						"${lexrelations.table}", "${as_relations}", // 1
						"${relations.table}", "${relations.relationid}", // 2
						"${synsets.table}", "${as_synsets2}", "${as_relations}", "${lexrelations.synset2id}", "${as_synsets2}", "${synsets.synsetid}", // 3
						"${words.table}", "${as_words}", "${as_relations}", "${lexrelations.word2id}", "${as_words}", "${words.wordid}", // 4
						"${senses.table}", "${as_senses}", "${as_synsets2}", "${senses.synsetid}", "${as_senses}", "${senses.synsetid}", // 5
						"${words.table}", "${as_words2}", "${words.wordid}"); //6
				r.projection = new String[]{String.format("GROUP_CONCAT(DISTINCT %s.%s) AS %s", "${as_words2}", "${words.word}", MEMBERS2)};
				r.groupBy = String.format("%s.%s", "${as_synsets2}", "${synsets.synsetid}");
				break;

			// JOINS

			case SENSES_VFRAMES:
				r.table = String.format("%s " + //
								"LEFT JOIN %s USING (%s)", //
						"${senses_vframes.table}", //
						"${vframes.table}", "${vframes.frameid}");
				break;

			case SENSES_VTEMPLATES:
				r.table = String.format("%s " + //
								"LEFT JOIN %s USING (%s)", //
						"${senses_vtemplates.table}", //
						"${vtemplates.table}", "${vtemplates.templateid}");
				break;

			case SENSES_ADJPOSITIONS:
				r.table = String.format("%s " + //
								"LEFT JOIN %s USING (%s)", //
						"${senses_adjpositions.table}", //
						"${adjpositions.table}", "${adjpositions.positionid}");
				break;

			case LEXES_MORPHS:
				r.table = String.format("%s " + //
								"LEFT JOIN %s USING (%s)", //
						"${lexes_morphs.table}", //
						"${morphs.table}", "${morphs.morphid}");
				break;

			case WORDS_LEXES_MORPHS_BY_WORD:
				r.groupBy = "${words.wordid}";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case WORDS_LEXES_MORPHS:
				r.table = String.format("%s " + //
								"LEFT JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s)", //
						"${words.table}", //
						"${lexes_morphs.table}", "${words.wordid}", //
						"${morphs.table}", "${morphs.morphid}");
				break;

			// T E X T S E A R C H

			case LOOKUP_FTS_WORDS:
				r.table = String.format("%s_%s_fts4", "${words.table}", "${words.word}");
				break;

			case LOOKUP_FTS_DEFINITIONS:
				r.table = String.format("%s_%s_fts4", "${synsets.table}", "${synsets.definition}");
				break;

			case LOOKUP_FTS_SAMPLES:
				r.table = String.format("%s_%s_fts4", "${samples.table}", "${samples.sample}");
				break;

			// S U G G E S T

			case SUGGEST_WORDS:
			{
				r.table = "${words.table}";
				r.projection = new String[]{ //
						String.format("%s AS _id", "${words.wordid}"), //
						String.format("%s AS %s", "${words.word}", "#{suggest_text_1}"), //
						String.format("%s AS %s", "${words.word}", "#{suggest_query}")};
				r.selection = String.format("%s LIKE ? || '%%'", "${words.word}");
				r.selectionArgs = new String[]{URI_LAST};
				break;
			}

			case SUGGEST_FTS_WORDS:
			{
				r.table = String.format("%s_%s_fts4", "@{words.table}", "@{words.word}");
				r.projection = new String[]{ //
						String.format("%s AS _id", "${words.wordid}"), //
						String.format("%s AS %s", "${words.word}", "#{suggest_text_1}"), //
						String.format("%s AS %s", "${words.word}", "#{suggest_query}")}; //
				r.selection = String.format("%s MATCH ?", "${words.word}");
				r.selectionArgs = new String[]{URI_LAST + '*'};
				break;
			}

			case SUGGEST_FTS_DEFINITIONS:
			{
				r.table = String.format("%s_%s_fts4", "@{synsets.table}", "@{synsets.definition}");
				r.projection = new String[]{ //
						String.format("%s AS _id", "${synsets.synsetid}"), //
						String.format("%s AS %s", "${synsets.definition}", "#{suggest_text_1}"), //
						String.format("%s AS %s", "${synsets.definition}", "#{suggest_query}")};
				r.selection = String.format("%s MATCH ?", "${synsets.definition}");
				r.selectionArgs = new String[]{URI_LAST + '*'};
				break;
			}

			case SUGGEST_FTS_SAMPLES:
			{
				r.table = String.format("%s_%s_fts4", "@{samples.table}", "@{samples.sample}");
				r.projection = new String[]{ //
						String.format("%s AS _id", "${synsets.synsetid}"), //
						String.format("%s AS %s", "${samples.sample}", "#{suggest_text_1}"), //
						String.format("%s AS %s", "${samples.sample}", "#{suggest_query}")};
				r.selection = String.format("%s MATCH ?", "${samples.sample}");
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
			return str == null ? null : String.format("\"%s\"", str);
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
