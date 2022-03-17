package org.sqlunet.wordnet.provider;

import android.app.SearchManager;

import org.junit.Test;
import org.sqlunet.provider.BaseProvider;
import org.sqlunet.wordnet.loaders.BaseModule;

import java.util.Arrays;
import java.util.function.Function;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Queries factory, which will execute on the development machine (host).
 */
public class QueriesUnitTest
{
	int[] codes = {WordNetProvider.WORDS, WordNetProvider.WORD, WordNetProvider.SENSES, WordNetProvider.SENSE, WordNetProvider.SYNSETS, WordNetProvider.SYNSET, WordNetProvider.SEMRELATIONS, WordNetProvider.LEXRELATIONS, WordNetProvider.RELATIONS, WordNetProvider.POSES, WordNetProvider.DOMAINS, WordNetProvider.ADJPOSITIONS, WordNetProvider.SAMPLES, WordNetProvider.DICT, WordNetProvider.WORDS_SENSES_SYNSETS, WordNetProvider.WORDS_SENSES_CASEDWORDS_SYNSETS, WordNetProvider.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS, WordNetProvider.SENSES_WORDS, WordNetProvider.SENSES_WORDS_BY_SYNSET, WordNetProvider.SENSES_SYNSETS_POSES_DOMAINS, WordNetProvider.SYNSETS_POSES_DOMAINS, WordNetProvider.ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET, WordNetProvider.SEMRELATIONS_SYNSETS, WordNetProvider.SEMRELATIONS_SYNSETS_X, WordNetProvider.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET, WordNetProvider.LEXRELATIONS_SENSES, WordNetProvider.LEXRELATIONS_SENSES_X, WordNetProvider.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET, WordNetProvider.SENSES_VFRAMES, WordNetProvider.SENSES_VTEMPLATES, WordNetProvider.SENSES_ADJPOSITIONS, WordNetProvider.LEXES_MORPHS, WordNetProvider.WORDS_LEXES_MORPHS, WordNetProvider.WORDS_LEXES_MORPHS_BY_WORD, WordNetProvider.LOOKUP_FTS_WORDS, WordNetProvider.LOOKUP_FTS_DEFINITIONS, WordNetProvider.LOOKUP_FTS_SAMPLES, WordNetProvider.SUGGEST_WORDS, WordNetProvider.SUGGEST_FTS_WORDS, WordNetProvider.SUGGEST_FTS_DEFINITIONS, WordNetProvider.SUGGEST_FTS_SAMPLES,};

	@SuppressWarnings("EmptyMethod")
	@Test
	public void queries()
	{
		String uriLast = "LAST";
		String[] projection = {"PROJ1", "PROJ2", "PROJ3"};
		String selection = "SEL";
		String[] selectionArgs = {"ARG1", "ARG2", "ARG3"};
		String sortOrder = "SORT";
		for (int i = 0; i < codes.length; i++)
		{
			int code = codes[i];
			query(code, uriLast, projection, selection, selectionArgs, sortOrder);
		}
	}

	static class Result
	{
		final String table;
		final String[] projection;
		final String selection;
		final String[] selectionArgs;
		final String groupBy;

		Result(final String table, final String[] projection, final String selection, final String[] selectionArgs, final String groupBy)
		{
			this.table = table;
			this.projection = projection;
			this.selection = selection;
			this.selectionArgs = selectionArgs;
			this.groupBy = groupBy;
		}
	}

	public void query(int code, @NonNull final String uriLast, final String[] projection, @Nullable final String selection, final String[] selectionArgs, final String sortOrder)
	{
		Result r1 = query1(code, uriLast, projection, selection, selectionArgs, sortOrder, s -> "SUBQUERY");
		Result r2 = query12(code, uriLast, projection, selection, selectionArgs, sortOrder, s -> "SUBQUERY");
		if (r1 == null || r2 == null)
		{
			r1 = query2(code, uriLast, projection, selection, selectionArgs, sortOrder);
			r2 = query22(code, uriLast, projection, selection, selectionArgs, sortOrder);
		}
		assert equals(r1.table, r2.table) : "Code=" + code + " " + r1.table + " != " + r2.table;
		assert Arrays.equals(r1.projection, r2.projection) : "Code=" + code + " " + Arrays.toString(r1.projection) + " != " + Arrays.toString(r2.projection);
		assert equals(r1.selection, r2.selection) : "Code=" + code + " " + r1.selection + " != " + r2.selection;
		assert Arrays.equals(r1.selectionArgs, r2.selectionArgs) : "Code=" + code + " " + Arrays.toString(r1.selectionArgs) + " != " + Arrays.toString(r2.selectionArgs);
		assert equals(r1.groupBy, r2.groupBy) : "Code=" + code + " " + r1.groupBy + " != " + r2.groupBy;
	}

	public Result query1(int code, @NonNull final String uriLast, final String[] projection0, @Nullable final String selection0, final String[] selectionArgs0, final String sortOrder0, final Function<String, String> subqueryFactory)
	{
		String table;
		String[] projection = projection0;
		String selection = selection0;
		String groupBy = null;
		switch (code)
		{
			// T A B L E
			// table uri : last element is table

			case WordNetProvider.WORDS:
				table = WordNetContract.Words.TABLE;
				break;

			case WordNetProvider.SENSES:
				table = WordNetContract.Senses.TABLE;
				break;

			case WordNetProvider.SYNSETS:
				table = WordNetContract.Synsets.TABLE;
				break;

			case WordNetProvider.SEMRELATIONS:
				table = WordNetContract.SemRelations.TABLE;
				break;

			case WordNetProvider.LEXRELATIONS:
				table = WordNetContract.LexRelations.TABLE;
				break;

			case WordNetProvider.RELATIONS:
				table = WordNetContract.Relations.TABLE;
				break;

			case WordNetProvider.POSES:
				table = WordNetContract.Poses.TABLE;
				break;

			case WordNetProvider.DOMAINS:
				table = WordNetContract.Domains.TABLE;
				break;

			case WordNetProvider.ADJPOSITIONS:
				table = WordNetContract.AdjPositions.TABLE;
				break;

			case WordNetProvider.SAMPLES:
				table = WordNetContract.Samples.TABLE;
				break;

			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case WordNetProvider.WORD:
				table = WordNetContract.Words.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += WordNetContract.Words.WORDID + " = " + uriLast;
				break;

			case WordNetProvider.SENSE:
				table = WordNetContract.Senses.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += WordNetContract.Senses.SENSEID + " = " + uriLast;
				break;

			case WordNetProvider.SYNSET:
				table = WordNetContract.Synsets.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += WordNetContract.Synsets.SYNSETID + " = " + uriLast;
				break;

			// V I E W S

			case WordNetProvider.DICT:
				table = WordNetContract.Dict.TABLE;
				break;

			// J O I N S

			case WordNetProvider.WORDS_SENSES_SYNSETS:
				table = "words AS " + WordNetContract.WORD + " " + //
						"LEFT JOIN senses AS " + WordNetContract.SENSE + " USING (wordid) " + //
						"LEFT JOIN synsets AS " + WordNetContract.SYNSET + " USING (synsetid)";
				break;

			case WordNetProvider.WORDS_SENSES_CASEDWORDS_SYNSETS:
				table = "words AS " + WordNetContract.WORD + " " + //
						"LEFT JOIN senses AS " + WordNetContract.SENSE + " USING (wordid) " + //
						"LEFT JOIN casedwords AS " + WordNetContract.CASED + " USING (wordid,casedwordid) " + //
						"LEFT JOIN synsets AS " + WordNetContract.SYNSET + " USING (synsetid)";
				break;

			case WordNetProvider.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS:
				table = "words AS " + WordNetContract.WORD + " " + //
						"LEFT JOIN senses AS " + WordNetContract.SENSE + " USING (wordid) " + //
						"LEFT JOIN casedwords AS " + WordNetContract.CASED + " USING (wordid,casedwordid) " + //
						"LEFT JOIN synsets AS " + WordNetContract.SYNSET + " USING (synsetid) " + //
						"LEFT JOIN poses AS " + WordNetContract.POS + " USING (posid) " + //
						"LEFT JOIN domains AS " + WordNetContract.DOMAIN + " USING (domainid)";
				break;

			case WordNetProvider.SENSES_WORDS:
				table = "senses AS " + WordNetContract.SENSE + " " + //
						"LEFT JOIN words AS " + WordNetContract.WORD + " USING (wordid)";
				break;

			case WordNetProvider.SENSES_WORDS_BY_SYNSET:
				table = "senses AS " + WordNetContract.SENSE + " " + //
						"LEFT JOIN words AS " + WordNetContract.WORD + " USING (wordid)";
				projection = BaseProvider.appendProjection(projection, "GROUP_CONCAT(words.word, ', ' ) AS " + WordNetContract.Senses_Words.MEMBERS);
				groupBy = "synsetid";
				break;

			case WordNetProvider.SENSES_SYNSETS_POSES_DOMAINS:
				table = "senses AS " + WordNetContract.SENSE + " " + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET + " USING (synsetid) " + //
						"LEFT JOIN poses AS " + WordNetContract.POS + " USING (posid) " + //
						"LEFT JOIN domains AS " + WordNetContract.DOMAIN + " USING (domainid)";
				break;

			case WordNetProvider.SYNSETS_POSES_DOMAINS:
				table = "synsets AS " + WordNetContract.SYNSET + " " + //
						"LEFT JOIN poses AS " + WordNetContract.POS + " USING (posid) " + //
						"LEFT JOIN domains AS " + WordNetContract.DOMAIN + " USING (domainid)";
				break;

			case WordNetProvider.ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET:
				final String subQuery = subqueryFactory.apply(selection0);
				table = "( " + subQuery + " ) AS " + WordNetContract.RELATION + ' ' + //
						"INNER JOIN relations USING (relationid) " + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET2 + " ON " + WordNetContract.RELATION + ".synset2id = " + WordNetContract.SYNSET2 + ".synsetid " + //
						"LEFT JOIN senses ON " + WordNetContract.SYNSET2 + ".synsetid = senses.synsetid " + //
						"LEFT JOIN words AS " + WordNetContract.WORD + " USING (wordid) " + //
						"LEFT JOIN words AS " + WordNetContract.WORD2 + " ON " + WordNetContract.RELATION + ".word2id = " + WordNetContract.WORD2 + ".wordid";
				selection = null;
				groupBy = BaseModule.TARGET_SYNSETID + "," + WordNetContract.TYPE + ",relation,relationid," + BaseModule.TARGET_WORDID + ',' + BaseModule.TARGET_WORD;
				break;

			case WordNetProvider.SEMRELATIONS_SYNSETS:
				table = "semrelations AS " + WordNetContract.RELATION + ' ' + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET2 + " ON " + WordNetContract.RELATION + ".synset2id = " + WordNetContract.SYNSET2 + ".synsetid";
				break;

			case WordNetProvider.SEMRELATIONS_SYNSETS_X:
				table = "semrelations AS " + WordNetContract.RELATION + ' ' + //
						"INNER JOIN relations USING (relationid) " + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET2 + " ON " + WordNetContract.RELATION + ".synset2id = " + WordNetContract.SYNSET2 + ".synsetid ";
				break;

			case WordNetProvider.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET:
				table = "semrelations AS " + WordNetContract.RELATION + ' ' + //
						"INNER JOIN relations USING (relationid) " + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET2 + " ON " + WordNetContract.RELATION + ".synset2id = " + WordNetContract.SYNSET2 + ".synsetid " + //
						"LEFT JOIN senses ON " + WordNetContract.SYNSET2 + ".synsetid = senses.synsetid " + //
						"LEFT JOIN words USING (wordid)";
				projection = BaseProvider.appendProjection(projection, "GROUP_CONCAT(words.word, ', ' ) AS " + WordNetContract.SemRelations_Synsets_Words_X.MEMBERS2);
				groupBy = WordNetContract.SYNSET2 + ".synsetid";
				break;

			case WordNetProvider.LEXRELATIONS_SENSES:
				table = "lexrelations AS " + WordNetContract.RELATION + ' ' + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET2 + " ON " + WordNetContract.RELATION + ".synset2id = " + WordNetContract.SYNSET2 + ".synsetid " + //
						"INNER JOIN words AS " + WordNetContract.WORD + " ON " + WordNetContract.RELATION + ".word2id = " + WordNetContract.WORD + ".wordid";
				break;

			case WordNetProvider.LEXRELATIONS_SENSES_X:
				table = "lexrelations AS " + WordNetContract.RELATION + ' ' + //
						"INNER JOIN relations USING (relationid) " + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET2 + " ON " + WordNetContract.RELATION + ".synset2id = " + WordNetContract.SYNSET2 + ".synsetid " + //
						"INNER JOIN words AS " + WordNetContract.WORD + " ON " + WordNetContract.RELATION + ".word2id = " + WordNetContract.WORD + ".wordid ";
				break;

			case WordNetProvider.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET:
				table = "lexrelations AS " + WordNetContract.RELATION + ' ' + //
						"INNER JOIN relations USING (relationid) " + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET2 + " ON " + WordNetContract.RELATION + ".synset2id = " + WordNetContract.SYNSET2 + ".synsetid " + //
						"INNER JOIN words AS " + WordNetContract.WORD + " ON " + WordNetContract.RELATION + ".word2id = " + WordNetContract.WORD + ".wordid " + //
						"LEFT JOIN senses AS " + WordNetContract.SENSE + " ON " + WordNetContract.SYNSET2 + ".synsetid = " + WordNetContract.SENSE + ".synsetid " + //
						"LEFT JOIN words AS " + WordNetContract.WORD2 + " USING (wordid)";
				projection = BaseProvider.appendProjection(projection, "GROUP_CONCAT(DISTINCT " + WordNetContract.WORD2 + ".word) AS " + WordNetContract.LexRelations_Senses_Words_X.MEMBERS2);
				groupBy = WordNetContract.SYNSET2 + ".synsetid";
				break;

			case WordNetProvider.SENSES_VFRAMES:
				table = "senses_vframes " + //
						"LEFT JOIN vframes USING (frameid)";
				break;

			case WordNetProvider.SENSES_VTEMPLATES:
				table = "senses_vtemplates " + //
						"LEFT JOIN vtemplates USING (templateid)";
				break;

			case WordNetProvider.SENSES_ADJPOSITIONS:
				table = "senses_adjpositions " + //
						"LEFT JOIN adjpositions USING (positionid)";
				break;

			case WordNetProvider.LEXES_MORPHS:
				table = "lexes_morphs " + //
						"LEFT JOIN morphs USING (morphid)";
				break;

			case WordNetProvider.WORDS_LEXES_MORPHS:
				table = "words " + //
						"LEFT JOIN lexes_morphs USING (wordid) " + //
						"LEFT JOIN morphs USING (morphid)";
				break;

			case WordNetProvider.WORDS_LEXES_MORPHS_BY_WORD:
				table = "words " + //
						"LEFT JOIN lexes_morphs USING (wordid) " + //
						"LEFT JOIN morphs USING (morphid)";
				groupBy = "wordid";
				break;

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs0, groupBy);
	}

	public Result query12(int code, @NonNull final String uriLast, final String[] projection0, @Nullable final String selection0, final String[] selectionArgs0, final String sortOrder0, final Function<String, String> subqueryFactory)
	{
		String table;
		String[] projection = projection0;
		String selection = selection0;
		String groupBy = null;
		switch (code)
		{
			// T A B L E
			// table uri : last element is table

			case WordNetProvider.WORDS:
				table = Queries.WORDS.TABLE;
				break;

			case WordNetProvider.SENSES:
				table = Queries.SENSES.TABLE;
				break;

			case WordNetProvider.SYNSETS:
				table = Queries.SYNSETS.TABLE;
				break;

			case WordNetProvider.SEMRELATIONS:
				table = Queries.SEMRELATIONS.TABLE;
				break;

			case WordNetProvider.LEXRELATIONS:
				table = Queries.LEXRELATIONS.TABLE;
				break;

			case WordNetProvider.RELATIONS:
				table = Queries.RELATIONS.TABLE;
				break;

			case WordNetProvider.POSES:
				table = Queries.POSES.TABLE;
				break;

			case WordNetProvider.DOMAINS:
				table = Queries.DOMAINS.TABLE;
				break;

			case WordNetProvider.ADJPOSITIONS:
				table = Queries.ADJPOSITIONS.TABLE;
				break;

			case WordNetProvider.SAMPLES:
				table = Queries.SAMPLES.TABLE;
				break;

			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case WordNetProvider.WORD:
				table = Queries.WORD.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += WordNetContract.Words.WORDID + " = " + uriLast;
				break;

			case WordNetProvider.SENSE:
				table = Queries.SENSE.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += WordNetContract.Senses.SENSEID + " = " + uriLast;
				break;

			case WordNetProvider.SYNSET:
				table = Queries.SYNSET.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += WordNetContract.Synsets.SYNSETID + " = " + uriLast;
				break;

			// V I E W S

			case WordNetProvider.DICT:
				table = Queries.DICT.TABLE;
				break;

			// J O I N S

			case WordNetProvider.WORDS_SENSES_SYNSETS:
				table = Queries.WORDS_SENSES_SYNSETS.TABLE;
				break;

			case WordNetProvider.WORDS_SENSES_CASEDWORDS_SYNSETS:
				table = Queries.WORDS_SENSES_CASEDWORDS_SYNSETS.TABLE;
				break;

			case WordNetProvider.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS:
				table = Queries.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS.TABLE;
				break;

			case WordNetProvider.SENSES_WORDS:
				table = Queries.SENSES_WORDS.TABLE;
				break;

			case WordNetProvider.SENSES_WORDS_BY_SYNSET:
				table = Queries.SENSES_WORDS_BY_SYNSET.TABLE;
				projection = BaseProvider.appendProjection(projection, Queries.SENSES_WORDS_BY_SYNSET.PROJECTION[0].replaceAll("#\\{members\\}", WordNetContract.MEMBERS));
				groupBy = Queries.SENSES_WORDS_BY_SYNSET.GROUPBY;
				break;

			case WordNetProvider.SENSES_SYNSETS_POSES_DOMAINS:
				table = Queries.SENSES_SYNSETS_POSES_DOMAINS.TABLE;
				break;

			case WordNetProvider.SYNSETS_POSES_DOMAINS:
				table = Queries.SYNSETS_POSES_DOMAINS.TABLE;
				break;

			case WordNetProvider.ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET:
				final String subQuery = subqueryFactory.apply(selection0);
				table = Queries.ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET.TABLE.replaceFirst("#\\{query\\}", subQuery);
				selection = null;
				groupBy = Queries.ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET.GROUPBY.replaceAll("#\\{query_target_synsetid\\}", BaseModule.TARGET_SYNSETID) //
						.replaceAll("#\\{query_target_wordid\\}", BaseModule.TARGET_WORDID) //
						.replaceAll("#\\{query_target_word\\}", BaseModule.TARGET_WORD);
				break;

			case WordNetProvider.SEMRELATIONS_SYNSETS:
				table = Queries.SEMRELATIONS_SYNSETS.TABLE;
				break;

			case WordNetProvider.SEMRELATIONS_SYNSETS_X:
				table = Queries.SEMRELATIONS_SYNSETS_X.TABLE;
				break;

			case WordNetProvider.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET:
				table = Queries.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.TABLE;
				projection = BaseProvider.appendProjection(projection, Queries.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.PROJECTION[0].replaceAll("#\\{members2\\}", WordNetContract.MEMBERS2));
				groupBy = Queries.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.GROUPBY;
				break;

			case WordNetProvider.LEXRELATIONS_SENSES:
				table = Queries.LEXRELATIONS_SENSES.TABLE;
				break;

			case WordNetProvider.LEXRELATIONS_SENSES_X:
				table = Queries.LEXRELATIONS_SENSES_X.TABLE;
				break;

			case WordNetProvider.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET:
				table = Queries.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.TABLE;
				projection = BaseProvider.appendProjection(projection, Queries.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.PROJECTION[0].replaceAll("#\\{members2\\}", WordNetContract.MEMBERS2));
				groupBy = Queries.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.GROUPBY;
				break;

			case WordNetProvider.SENSES_VFRAMES:
				table = Queries.SENSES_VFRAMES.TABLE;
				break;

			case WordNetProvider.SENSES_VTEMPLATES:
				table = Queries.SENSES_VTEMPLATES.TABLE;
				break;

			case WordNetProvider.SENSES_ADJPOSITIONS:
				table = Queries.SENSES_ADJPOSITIONS.TABLE;
				break;

			case WordNetProvider.LEXES_MORPHS:
				table = Queries.LEXES_MORPHS.TABLE;
				break;

			case WordNetProvider.WORDS_LEXES_MORPHS:
				table = Queries.WORDS_LEXES_MORPHS.TABLE;
				break;

			case WordNetProvider.WORDS_LEXES_MORPHS_BY_WORD:
				table = Queries.WORDS_LEXES_MORPHS.TABLE;
				groupBy = Queries.WORDS_LEXES_MORPHS_BY_WORD.GROUPBY;
				break;

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs0, groupBy);
	}

	public Result query2(int code, @NonNull final String uriLast, final String[] projection0, @Nullable final String selection0, final String[] selectionArgs0, final String sortOrder0)
	{
		String table;
		String[] projection = projection0;
		String selection = selection0;
		String[] selectionArgs = selectionArgs0;
		String groupBy = null;

		switch (code)
		{
			// T E X T S E A R C H

			case WordNetProvider.LOOKUP_FTS_WORDS:
				table = "words_word_fts4";
				break;

			case WordNetProvider.LOOKUP_FTS_DEFINITIONS:
				table = "synsets_definition_fts4";
				break;

			case WordNetProvider.LOOKUP_FTS_SAMPLES:
				table = "samples_sample_fts4";
				break;

			// S U G G E S T

			case WordNetProvider.SUGGEST_WORDS:
			{
				//				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				//				{
				//					return false;
				//				}
				projection = new String[]{"wordid AS _id", //
						"word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
						"word AS " + SearchManager.SUGGEST_COLUMN_QUERY};
				selection = "word LIKE ? || '%'";
				selectionArgs = new String[]{uriLast};
				table = "words";
				break;
			}

			case WordNetProvider.SUGGEST_FTS_WORDS:
			{
				//				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				//				{
				//					return false;
				//				}
				projection = new String[]{"wordid AS _id", //
						"word AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
						"word AS " + SearchManager.SUGGEST_COLUMN_QUERY};
				selection = "word MATCH ?";
				selectionArgs = new String[]{uriLast + '*'};
				table = "words_word_fts4";
				break;
			}

			case WordNetProvider.SUGGEST_FTS_DEFINITIONS:
			{
				//				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				//				{
				//					return false;
				//				}
				projection = new String[]{"synsetid AS _id", //
						"definition AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
						"definition AS " + SearchManager.SUGGEST_COLUMN_QUERY};
				selection = "definition MATCH ?";
				selectionArgs = new String[]{uriLast + '*'};
				table = "synsets_definition_fts4";
				break;
			}

			case WordNetProvider.SUGGEST_FTS_SAMPLES:
			{
				final String last = uriLast;
				//				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				//				{
				//					return false;
				//				}
				projection = new String[]{"sampleid AS _id", //
						"sample AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
						"sample AS " + SearchManager.SUGGEST_COLUMN_QUERY};
				selection = "sample MATCH ?";
				selectionArgs = new String[]{last + '*'};
				table = "samples_sample_fts4";
				break;
			}

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs, groupBy);
	}

	public Result query22(int code, @NonNull final String uriLast, final String[] projection0, @Nullable final String selection0, final String[] selectionArgs0, final String sortOrder0)
	{
		String table;
		String[] projection = projection0;
		String selection = selection0;
		String[] selectionArgs = selectionArgs0;
		switch (code)
		{
			// T E X T S E A R C H

			case WordNetProvider.LOOKUP_FTS_WORDS:
				table = Queries.LOOKUP_FTS_WORDS.TABLE;
				break;

			case WordNetProvider.LOOKUP_FTS_DEFINITIONS:
				table = Queries.LOOKUP_FTS_DEFINITIONS.TABLE;
				break;

			case WordNetProvider.LOOKUP_FTS_SAMPLES:
				table = Queries.LOOKUP_FTS_SAMPLES.TABLE;
				break;

			// S U G G E S T

			case WordNetProvider.SUGGEST_WORDS:
			{
				//				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				//				{
				//					return false;
				//				}
				projection = Queries.SUGGEST_WORDS.PROJECTION;
				selection = Queries.SUGGEST_WORDS.SELECTION;
				selectionArgs = new String[]{Queries.SUGGEST_WORDS.ARGS[0].replaceAll("#\\{uri_last\\}", uriLast)};
				table = Queries.SUGGEST_WORDS.TABLE;
				break;
			}

			case WordNetProvider.SUGGEST_FTS_WORDS:
			{
				//				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				//				{
				//					return false;
				//				}
				projection = Queries.SUGGEST_FTS_WORDS.PROJECTION;
				selection = Queries.SUGGEST_FTS_WORDS.SELECTION;
				selectionArgs = new String[]{Queries.SUGGEST_FTS_WORDS.ARGS[0].replaceAll("#\\{uri_last\\}", uriLast)};
				table = Queries.SUGGEST_FTS_WORDS.TABLE;
				break;
			}

			case WordNetProvider.SUGGEST_FTS_DEFINITIONS:
			{
				projection = Queries.SUGGEST_FTS_DEFINITIONS.PROJECTION;
				selection = Queries.SUGGEST_FTS_DEFINITIONS.SELECTION;
				selectionArgs = new String[]{Queries.SUGGEST_FTS_DEFINITIONS.ARGS[0].replaceAll("#\\{uri_last\\}", uriLast)};
				table = Queries.SUGGEST_FTS_DEFINITIONS.TABLE;
				break;
			}

			case WordNetProvider.SUGGEST_FTS_SAMPLES:
			{
				//				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				//				{
				//					return false;
				//				}
				projection = Queries.SUGGEST_FTS_SAMPLES.PROJECTION;
				selection = Queries.SUGGEST_FTS_SAMPLES.SELECTION;
				selectionArgs = new String[]{Queries.SUGGEST_FTS_SAMPLES.ARGS[0].replaceAll("#\\{uri_last\\}", uriLast)};
				table = Queries.SUGGEST_FTS_SAMPLES.TABLE;
				break;
			}

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs, null);
	}

	private static boolean equals(Object a, Object b)
	{
		return (a == b) || (a != null && a.equals(b));
	}
}