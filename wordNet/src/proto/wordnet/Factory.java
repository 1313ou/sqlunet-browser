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
public class Factory implements Function<String,String[]>, Supplier<String[]>
{
	//# instantiated at runtime
	static public final String URI_LAST = "#{uri_last}";
	static public final String SUBQUERY = "#{query}";

	//# column aliases at runtime
	static public final String MEMBERS = "${members}";
	static public final String MEMBERS2 = "${members2}";
	static public final String SYNSET2ID = "${synset2id}";
	static public final String WORD2ID = "${word2id}";
	static public final String WORD2 = "${word2}";

	public Factory()
	{
		System.out.println("WN Factory");
	}

	@Override
	public String[] apply(String keyName)
	{
		final String last = URI_LAST;

		String table;
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String sortOrder = null;

		Key key = Key.valueOf(keyName);
		switch (key)
		{
			// T A B L E
			// table uri : last element is table

			case LEXES:
				table = "${lexes.table}";
				break;
			case WORDS:
				table = "${words.table}";
				break;
			case CASEDWORDS:
				table = "${casedwords.table}";
				break;
			case PRONUNCIATIONS:
				table = "${pronunciations.table}";
				break;
			case MORPHS:
				table = "${morphs.table}";
				break;
			case SENSES:
				table = "${senses.table}";
				break;
			case SYNSETS:
				table = "${synsets.table}";
				break;
			case SEMRELATIONS:
				table = "${semrelations.table}";
				break;
			case LEXRELATIONS:
				table = "${lexrelations.table}";
				break;
			case RELATIONS:
				table = "${relations.table}";
				break;
			case POSES:
				table = "${poses.table}";
				break;
			case DOMAINS:
				table = "${domains.table}";
				break;
			case ADJPOSITIONS:
				table = "${adjpositions.table}";
				break;
			case VFRAMES:
				table = "${vframes.table}";
				break;
			case VTEMPLATES:
				table = "${vtemplates.table}";
				break;
			case SAMPLES:
				table = "${samples.table}";
				break;

			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case WORD1:
				table = "${words.table}";
				selection = "${words.wordid} = #{uri_last}";
				break;

			case SENSE1:
				table = "${senses.table}";
				selection = "${senses.senseid} = #{uri_last}";
				break;

			case SYNSET1:
				table = "${synsets.table}";
				selection = "${synsets.synsetid} = #{uri_last}";
				break;

			// V I E W S

			case DICT:
				table = "${dict.table}";
				break;

			// J O I N S

			case WORDS_SENSES_SYNSETS:
				table = String.format("%s AS %s " + //
								"LEFT JOIN %s AS %s USING (%s) " + //
								"LEFT JOIN %s AS %s USING (%s)", //
						"${words.table}", "${as_words}", //
						"${senses.table}", "${as_senses}", "${senses.wordid}", //
						"${synsets.table}", "${as_synsets}", "${synsets.synsetid}");
				break;

			case WORDS_SENSES_CASEDWORDS_SYNSETS:
				table = String.format("%s AS %s " + //
								"LEFT JOIN %s AS %s USING (%s) " + //
								"LEFT JOIN %s AS %s USING (%s,%s) " + //
								"LEFT JOIN %s AS %s USING (%s)", //
						"${words.table}", "${as_words}", //
						"${senses.table}", "${as_senses}", "${senses.wordid}", //
						"${casedwords.table}", "${as_caseds}", "${casedwords.wordid}", "${casedwords.casedwordid}", //
						"${synsets.table}", "${as_synsets}", "${synsets.synsetid}");
				break;

			case WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS:
				table = String.format("%s AS %s " + //
								"LEFT JOIN %s AS %s USING (%s) " + //
								"LEFT JOIN %s AS %s USING (%s,%s) " + //
								"LEFT JOIN %s AS %s USING (%s) " + //
								"LEFT JOIN %s AS %s USING (%s) " + //
								"LEFT JOIN %s AS %s USING (%s)", //
						"${words.table}", "${as_words}", //
						"${senses.table}", "${as_senses}", "${senses.wordid}", //
						"${casedwords.table}", "${as_caseds}", "${casedwords.wordid}", "${casedwords.casedwordid}", //
						"${synsets.table}", "${as_synsets}", "${synsets.synsetid}", //
						"${poses.table}", "${as_poses}", "${poses.posid}", //
						"${domains.table}", "${as_domains}", "${domains.domainid}");
				break;

			case SENSES_WORDS:
				table = String.format("%s AS %s " + //
								"LEFT JOIN %s AS %s USING (%s)", //
						"${senses.table}", "${as_senses}", //
						"${words.table}", "${as_words}", "${senses.wordid}");
				break;

			case SENSES_WORDS_BY_SYNSET:
				table = String.format("%s AS %s " + //
								"LEFT JOIN %s AS %s USING (%s)", //
						"${senses.table}", "${as_senses}", //
						"${words.table}", "${as_words}", "${words.wordid}");
				projection = new String[]{String.format("GROUP_CONCAT(%s.%s, ', ' ) AS %s", "${words.table}", "${words.word}", MEMBERS)};
				groupBy = "${synsets.synsetid}";
				break;

			case SENSES_SYNSETS_POSES_DOMAINS:
				table = String.format("%s AS %s " + //
								"INNER JOIN %s AS %s USING (%s) " + //
								"LEFT JOIN %s AS %s USING (%s) " + //
								"LEFT JOIN %s AS %s USING (%s)", //
						"${senses.table}", "${as_senses}", //
						"${synsets.table}", "${as_synsets}", "${synsets.synsetid}", //
						"${poses.table}", "${as_poses}", "${poses.posid}", //
						"${domains.table}", "${as_domains}", "${domains.domainid}");
				break;

			case SYNSETS_POSES_DOMAINS:
				table = String.format("%s AS %s " + //
								"LEFT JOIN %s AS %s USING (%s) " + //
								"LEFT JOIN %s AS %s USING (%s)", //
						"${synsets.table}", "${as_synsets}", //
						"${poses.table}", "${as_poses}", "${poses.posid}",//
						"${domains.table}", "${as_domains}", "${domains.domainid}");
				break;

			case ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET:
			{
				table = String.format("( %s ) AS %s " + // 1
								"INNER JOIN %s USING (%s) " + // 2
								"INNER JOIN %s AS %s ON %s.%s = %s.%s " + // 3
								"LEFT JOIN %s ON %s.%s = %s.%s " + // 4
								"LEFT JOIN %s AS %s USING (%s) " + // 5
								"LEFT JOIN %s AS %s ON %s.%s = %s.%s", // 6
						SUBQUERY, "${as_relations}", // 1
						"${relations.table}", "${relations.relationid}", // 2
						"${synsets.table}", "${as_synsets2}", "${as_relations}", "${allrelations.synset2id}", "${as_synsets2}", "${synsets.synsetid}", // 3
						"${senses.table}", "${as_synsets2}", "${synsets.synsetid}", "${senses.table}", "${senses.synsetid}", // 4
						"${words.table}", "${as_words}", "${words.wordid}", //
						"${words.table}", "${as_words2}", "${as_relations}", "${allrelations.word2id}", "${as_words2}", "${words.wordid}");
				groupBy = String.format("%s,%s,%s,%s,%s,%s", "${synset2id}", "${relationtype}", "${relations.relation}", "${relations.relationid}", "${word2id}", "${word2}");
			}
			break;

			case SEMRELATIONS_SYNSETS:
				table = String.format("%s AS %s " + //
								"INNER JOIN %s AS %s ON %s.%s = %s.%s", //
						"${semrelations.table}", "${as_relations}", //
						"${synsets.table}", "${as_synsets2}", "${as_relations}", "${semrelations.synset2id}", "${as_synsets2}", "${synsets.synsetid}");
				break;

			case SEMRELATIONS_SYNSETS_X:
				table = String.format("%s AS %s " + //
								"INNER JOIN %s USING (%s) " + //
								"INNER JOIN %s AS %s ON %s.%s = %s.%s ", //
						"${semrelations.table}", "${as_relations}", //
						"${relations.table}", "${relations.relationid}", //
						"${synsets.table}", "${as_synsets2}", "${as_relations}", "${semrelations.synset2id}", "${as_synsets2}", "${synsets.synsetid}");
				break;

			case SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET:
				table = String.format("%s AS %s " + // 1
								"INNER JOIN %s USING (%s) " + // 2
								"INNER JOIN %s AS %s ON %s.%s = %s.%s " + // 3
								"LEFT JOIN %s ON %s.%s = %s.%s " + // 4
								"LEFT JOIN %s USING (%s)", // 5
						"${semrelations.table}", "${as_relations}", // 1
						"${relations.table}", "${relations.relationid}", // 2
						"${synsets.table}", "${as_synsets2}", "${as_relations}", "${semrelations.synset2id}", "${as_synsets2}", "${synsets.synsetid}", // 3
						"${senses.table}", "${as_synsets2}", "${synsets.synsetid}", "${senses.table}", "${senses.synsetid}", // 4
						"${words.table}", "${words.wordid}"); //5
				projection =  new String[]{ String.format("GROUP_CONCAT(%s.%s, ', ' ) AS %s", "${words.table}", "${words.word}", MEMBERS2)};
				groupBy = String.format("%s.%s", "${as_synsets2}", "${synsets.synsetid}");
				break;

			case LEXRELATIONS_SENSES:
				table = String.format("%s AS %s " + //
								"INNER JOIN %s AS %s ON %s.%s = %s.%s " + //
								"INNER JOIN %s AS %s ON %s.%s = %s.%s", //
						"${lexrelations.table}", "${as_relations}", //
						"${synsets.table}", "${as_synsets2}", "${as_relations}", "${lexrelations.synset2id}", "${as_synsets2}", "${synsets.synsetid}", //
						"${words.table}", "${as_words}", "${as_relations}", "${lexrelations.word2id}", "${as_words}", "${words.wordid}");
				break;

			case LEXRELATIONS_SENSES_X:
				table = String.format("%s AS %s " + //
								"INNER JOIN %s USING (%s) " + //
								"INNER JOIN %s AS %s ON %s.%s = %s.%s " + //
								"INNER JOIN %s AS %s ON %s.%s = %s.%s ", //
						"${lexrelations.table}", "${as_relations}", //
						"${relations.table}", "${relations.relationid}", //
						"${synsets.table}", "${as_synsets2}", "${as_relations}", "${lexrelations.synset2id}", "${as_synsets2}", "${synsets.synsetid}", //
						"${words.table}", "${as_words}", "${as_relations}", "${lexrelations.word2id}", "${as_words}", "${words.wordid}");
				break;

			case LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET:
				table = String.format("%s AS %s " + // 1
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
				projection =  new String[]{ String.format("GROUP_CONCAT(DISTINCT %s.%s) AS %s", "${as_words2}", "${words.word}", MEMBERS2)};
				groupBy = String.format("%s.%s", "${as_synsets2}", "${synsets.synsetid}");
				break;

			case SENSES_VFRAMES:
				table = String.format("%s " + //
								"LEFT JOIN %s USING (%s)", //
						"${senses_vframes.table}", //
						"${vframes.table}", "${vframes.frameid}");
				break;

			case SENSES_VTEMPLATES:
				table = String.format("%s " + //
								"LEFT JOIN %s USING (%s)", //
						"${senses_vtemplates.table}", //
						"${vtemplates.table}", "${vtemplates.templateid}");
				break;

			case SENSES_ADJPOSITIONS:
				table = String.format("%s " + //
								"LEFT JOIN %s USING (%s)", //
						"${senses_adjpositions.table}", //
						"${adjpositions.table}", "${adjpositions.positionid}");
				break;

			case LEXES_MORPHS:
				table = String.format("%s " + //
								"LEFT JOIN %s USING (%s)", //
						"${lexes_morphs.table}", //
						"${morphs.table}", "${morphs.morphid}");
				break;

			case WORDS_LEXES_MORPHS_BY_WORD:
				groupBy = "${words.wordid}";
				//$FALL-THROUGH$
				//noinspection fallthrough
			case WORDS_LEXES_MORPHS:
				table = String.format("%s " + //
								"LEFT JOIN %s USING (%s) " + //
								"LEFT JOIN %s USING (%s)", //
						"${words.table}", //
						"${lexes_morphs.table}", "${words.wordid}", //
						"${morphs.table}", "${morphs.morphid}");
				break;

			// T E X T S E A R C H

			case LOOKUP_FTS_WORDS:
				table = String.format("%s_%s_fts4", "${words.table}", "${words.word}");
				break;

			case LOOKUP_FTS_DEFINITIONS:
				table = String.format("%s_%s_fts4", "${synsets.table}", "${synsets.definition}");
				break;

			case LOOKUP_FTS_SAMPLES:
				table = String.format("%s_%s_fts4", "${samples.table}", "${samples.sample}");
				break;

			// S U G G E S T

			case SUGGEST_WORDS:
			{
				table = "${words.table}";
				projection = new String[]{ //
						String.format("%s AS _id", "${words.wordid}"), //
						String.format("%s AS %s", "${words.word}", "#{suggest_text_1}"), //
						String.format("%s AS %s", "${words.word}", "#{suggest_query}")};
				selection = String.format("%s LIKE ? || '%%'", "${words.word}");
				selectionArgs = new String[]{last};
				break;
			}

			case SUGGEST_FTS_WORDS:
			{
				table = String.format("%s_%s_fts4", "@{words.table}", "@{words.word}");
				projection = new String[]{ //
						String.format("%s AS _id", "${words.wordid}"), //
						String.format("%s AS %s", "${words.word}", "#{suggest_text_1}"), //
						String.format("%s AS %s", "${words.word}", "#{suggest_query}")}; //
				selection = String.format("%s MATCH ?", "${words.word}");
				selectionArgs = new String[]{last + '*'};
				break;
			}

			case SUGGEST_FTS_DEFINITIONS:
			{
				table = String.format("%s_%s_fts4", "@{synsets.table}", "@{synsets.definition}");
				projection = new String[]{ //
						String.format("%s AS _id", "${synsets.synsetid}"), //
						String.format("%s AS %s", "${synsets.definition}", "#{suggest_text_1}"), //
						String.format("%s AS %s", "${synsets.definition}", "#{suggest_query}")};
				selection = String.format("%s MATCH ?", "${synsets.definition}");
				selectionArgs = new String[]{last + '*'};
				break;
			}

			case SUGGEST_FTS_SAMPLES:
			{
				table = String.format("%s_%s_fts4", "@{samples.table}", "@{samples.sample}");
				projection = new String[]{ //
						String.format("%s AS _id", "${samples.sampleid}"), //
						String.format("%s AS %s", "${samples.sample}", "#{suggest_text_1}"), //
						String.format("%s AS %s", "${samples.sample}", "#{suggest_query}")};
				selection = String.format("%s MATCH ?", "${samples.sample}");
				selectionArgs = new String[]{last + '*'};
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
		LEXES, WORDS, CASEDWORDS, PRONUNCIATIONS, SENSES, SYNSETS, POSES, DOMAINS, //
		RELATIONS, SEMRELATIONS, LEXRELATIONS, //
		ADJPOSITIONS, MORPHS, SAMPLES, VFRAMES, VTEMPLATES, //
		LEXES_MORPHS, SENSES_VFRAMES, SENSES_VTEMPLATES, SENSES_ADJPOSITIONS, //
		DICT, //
		WORD1, SENSE1, SYNSET1, //
		WORDS_LEXES_MORPHS, WORDS_LEXES_MORPHS_BY_WORD, WORDS_SENSES_SYNSETS, WORDS_SENSES_CASEDWORDS_SYNSETS, WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS, //
		SENSES_WORDS, SENSES_WORDS_BY_SYNSET, SENSES_SYNSETS_POSES_DOMAINS, //
		SYNSETS_POSES_DOMAINS, //
		ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET, //
		SEMRELATIONS_SYNSETS, SEMRELATIONS_SYNSETS_X, SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET, //
		LEXRELATIONS_SENSES, LEXRELATIONS_SENSES_X, LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET, //
		LOOKUP_FTS_DEFINITIONS, LOOKUP_FTS_SAMPLES, LOOKUP_FTS_WORDS, //
		SUGGEST_FTS_DEFINITIONS, SUGGEST_FTS_SAMPLES, SUGGEST_FTS_WORDS, SUGGEST_WORDS
	}

	private static String quote(String str)
	{
		return str == null ? null : String.format("\"%s\"", str);
	}
}
