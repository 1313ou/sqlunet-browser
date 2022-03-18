package org.sqlunet.wordnet.provider;

import android.app.SearchManager;

import org.junit.Test;
import org.sqlunet.provider.BaseProvider;
import org.sqlunet.wordnet.loaders.BaseModule;
import org.sqlunet.wordnet.provider.WordNetDispatcher.Factory;
import org.sqlunet.wordnet.provider.WordNetDispatcher.Result;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Queries factory, which will execute on the development machine (host).
 */
public class QueriesUnitTest
{
	private final int[] codes = {WordNetDispatcher.WORDS, WordNetDispatcher.WORD, WordNetDispatcher.SENSES, WordNetDispatcher.SENSE, WordNetDispatcher.SYNSETS, WordNetDispatcher.SYNSET, WordNetDispatcher.SEMRELATIONS, WordNetDispatcher.LEXRELATIONS, WordNetDispatcher.RELATIONS, WordNetDispatcher.POSES, WordNetDispatcher.DOMAINS, WordNetDispatcher.ADJPOSITIONS, WordNetDispatcher.SAMPLES, WordNetDispatcher.DICT, WordNetDispatcher.WORDS_SENSES_SYNSETS, WordNetDispatcher.WORDS_SENSES_CASEDWORDS_SYNSETS, WordNetDispatcher.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS, WordNetDispatcher.SENSES_WORDS, WordNetDispatcher.SENSES_WORDS_BY_SYNSET, WordNetDispatcher.SENSES_SYNSETS_POSES_DOMAINS, WordNetDispatcher.SYNSETS_POSES_DOMAINS, WordNetDispatcher.ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET, WordNetDispatcher.SEMRELATIONS_SYNSETS, WordNetDispatcher.SEMRELATIONS_SYNSETS_X, WordNetDispatcher.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET, WordNetDispatcher.LEXRELATIONS_SENSES, WordNetDispatcher.LEXRELATIONS_SENSES_X, WordNetDispatcher.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET, WordNetDispatcher.SENSES_VFRAMES, WordNetDispatcher.SENSES_VTEMPLATES, WordNetDispatcher.SENSES_ADJPOSITIONS, WordNetDispatcher.LEXES_MORPHS, WordNetDispatcher.WORDS_LEXES_MORPHS, WordNetDispatcher.WORDS_LEXES_MORPHS_BY_WORD, WordNetDispatcher.LOOKUP_FTS_WORDS, WordNetDispatcher.LOOKUP_FTS_DEFINITIONS, WordNetDispatcher.LOOKUP_FTS_SAMPLES, WordNetDispatcher.SUGGEST_WORDS, WordNetDispatcher.SUGGEST_FTS_WORDS, WordNetDispatcher.SUGGEST_FTS_DEFINITIONS, WordNetDispatcher.SUGGEST_FTS_SAMPLES,};
	private final String uriLast = "LAST";
	private final String[] projection = {"PROJ1", "PROJ2", "PROJ3"};
	private final String selection = "SEL";
	private final String[] selectionArgs = {"ARG1", "ARG2", "ARG3"};
	private final String sortOrder = "SORT";
	private final Factory factory = s -> "SUBQUERY";

	@Test
	public void queriesLegacyAgainstNew()
	{
		for (int i = 0; i < codes.length; i++)
		{
			int code = codes[i];
			queryLegacyAgainstNew(code, uriLast, projection, selection, selectionArgs, sortOrder);
		}
	}

	@Test
	public void queriesLegacyAgainstProvider()
	{
		for (int i = 0; i < codes.length; i++)
		{
			int code = codes[i];
			queryLegacyAgainstProvider(code, uriLast, projection, selection, selectionArgs, sortOrder);
		}
	}

	@Test
	public void queriesNewAgainstProvider()
	{
		for (int i = 0; i < codes.length; i++)
		{
			int code = codes[i];
			queryNewAgainstProvider(code, uriLast, projection, selection, selectionArgs, sortOrder);
		}
	}

	public void queryLegacyAgainstNew(int code, @NonNull final String uriLast, final String[] projection, @Nullable final String selection, final String[] selectionArgs, final String sortOrder)
	{
		Result r1 = queryLegacy(code, uriLast, projection, selection, selectionArgs, sortOrder, factory);
		Result r2 = queryNew(code, uriLast, projection, selection, selectionArgs, sortOrder, factory);
		check(code, r1, r2);
	}

	public void queryLegacyAgainstProvider(int code, @NonNull final String uriLast, final String[] projection, @Nullable final String selection, final String[] selectionArgs, final String sortOrder)
	{
		Result r1 = queryLegacy(code, uriLast, projection, selection, selectionArgs, sortOrder, factory);
		Result r2 = queryProvider(code, uriLast, projection, selection, selectionArgs, sortOrder, factory);
		check(code, r1, r2);
	}

	public void queryNewAgainstProvider(int code, @NonNull final String uriLast, final String[] projection, @Nullable final String selection, final String[] selectionArgs, final String sortOrder)
	{
		Result r1 = queryNew(code, uriLast, projection, selection, selectionArgs, sortOrder, factory);
		Result r2 = queryProvider(code, uriLast, projection, selection, selectionArgs, sortOrder, factory);
		check(code, r1, r2);
	}

	public Result queryLegacy(int code, @NonNull final String uriLast, final String[] projection0, @Nullable final String selection0, final String[] selectionArgs0, final String sortOrder0, final Factory subqueryFactory)
	{
		String table;
		String[] projection = projection0;
		String selection = selection0;
		String[] selectionArgs = selectionArgs0;
		String groupBy = null;
		switch (code)
		{
			// T A B L E
			// table uri : last element is table

			case WordNetDispatcher.WORDS:
				table = WordNetContract.Words.TABLE;
				break;

			case WordNetDispatcher.SENSES:
				table = WordNetContract.Senses.TABLE;
				break;

			case WordNetDispatcher.SYNSETS:
				table = WordNetContract.Synsets.TABLE;
				break;

			case WordNetDispatcher.SEMRELATIONS:
				table = WordNetContract.SemRelations.TABLE;
				break;

			case WordNetDispatcher.LEXRELATIONS:
				table = WordNetContract.LexRelations.TABLE;
				break;

			case WordNetDispatcher.RELATIONS:
				table = WordNetContract.Relations.TABLE;
				break;

			case WordNetDispatcher.POSES:
				table = WordNetContract.Poses.TABLE;
				break;

			case WordNetDispatcher.DOMAINS:
				table = WordNetContract.Domains.TABLE;
				break;

			case WordNetDispatcher.ADJPOSITIONS:
				table = WordNetContract.AdjPositions.TABLE;
				break;

			case WordNetDispatcher.SAMPLES:
				table = WordNetContract.Samples.TABLE;
				break;

			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case WordNetDispatcher.WORD:
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

			case WordNetDispatcher.SENSE:
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

			case WordNetDispatcher.SYNSET:
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

			case WordNetDispatcher.DICT:
				table = WordNetContract.Dict.TABLE;
				break;

			// J O I N S

			case WordNetDispatcher.WORDS_SENSES_SYNSETS:
				table = "words AS " + WordNetContract.WORD + " " + //
						"LEFT JOIN senses AS " + WordNetContract.SENSE + " USING (wordid) " + //
						"LEFT JOIN synsets AS " + WordNetContract.SYNSET + " USING (synsetid)";
				break;

			case WordNetDispatcher.WORDS_SENSES_CASEDWORDS_SYNSETS:
				table = "words AS " + WordNetContract.WORD + " " + //
						"LEFT JOIN senses AS " + WordNetContract.SENSE + " USING (wordid) " + //
						"LEFT JOIN casedwords AS " + WordNetContract.CASED + " USING (wordid,casedwordid) " + //
						"LEFT JOIN synsets AS " + WordNetContract.SYNSET + " USING (synsetid)";
				break;

			case WordNetDispatcher.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS:
				table = "words AS " + WordNetContract.WORD + " " + //
						"LEFT JOIN senses AS " + WordNetContract.SENSE + " USING (wordid) " + //
						"LEFT JOIN casedwords AS " + WordNetContract.CASED + " USING (wordid,casedwordid) " + //
						"LEFT JOIN synsets AS " + WordNetContract.SYNSET + " USING (synsetid) " + //
						"LEFT JOIN poses AS " + WordNetContract.POS + " USING (posid) " + //
						"LEFT JOIN domains AS " + WordNetContract.DOMAIN + " USING (domainid)";
				break;

			case WordNetDispatcher.SENSES_WORDS:
				table = "senses AS " + WordNetContract.SENSE + " " + //
						"LEFT JOIN words AS " + WordNetContract.WORD + " USING (wordid)";
				break;

			case WordNetDispatcher.SENSES_WORDS_BY_SYNSET:
				table = "senses AS " + WordNetContract.SENSE + " " + //
						"LEFT JOIN words AS " + WordNetContract.WORD + " USING (wordid)";
				projection = BaseProvider.appendProjection(projection, "GROUP_CONCAT(words.word, ', ' ) AS " + WordNetContract.Senses_Words.MEMBERS);
				groupBy = "synsetid";
				break;

			case WordNetDispatcher.SENSES_SYNSETS_POSES_DOMAINS:
				table = "senses AS " + WordNetContract.SENSE + " " + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET + " USING (synsetid) " + //
						"LEFT JOIN poses AS " + WordNetContract.POS + " USING (posid) " + //
						"LEFT JOIN domains AS " + WordNetContract.DOMAIN + " USING (domainid)";
				break;

			case WordNetDispatcher.SYNSETS_POSES_DOMAINS:
				table = "synsets AS " + WordNetContract.SYNSET + " " + //
						"LEFT JOIN poses AS " + WordNetContract.POS + " USING (posid) " + //
						"LEFT JOIN domains AS " + WordNetContract.DOMAIN + " USING (domainid)";
				break;

			case WordNetDispatcher.ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET:
				final String subQuery = subqueryFactory.make(selection0);
				table = "( " + subQuery + " ) AS " + WordNetContract.RELATION + ' ' + //
						"INNER JOIN relations USING (relationid) " + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET2 + " ON " + WordNetContract.RELATION + ".synset2id = " + WordNetContract.SYNSET2 + ".synsetid " + //
						"LEFT JOIN senses ON " + WordNetContract.SYNSET2 + ".synsetid = senses.synsetid " + //
						"LEFT JOIN words AS " + WordNetContract.WORD + " USING (wordid) " + //
						"LEFT JOIN words AS " + WordNetContract.WORD2 + " ON " + WordNetContract.RELATION + ".word2id = " + WordNetContract.WORD2 + ".wordid";
				selection = null;
				groupBy = BaseModule.TARGET_SYNSETID + "," + WordNetContract.TYPE + ",relation,relationid," + BaseModule.TARGET_WORDID + ',' + BaseModule.TARGET_WORD;
				break;

			case WordNetDispatcher.SEMRELATIONS_SYNSETS:
				table = "semrelations AS " + WordNetContract.RELATION + ' ' + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET2 + " ON " + WordNetContract.RELATION + ".synset2id = " + WordNetContract.SYNSET2 + ".synsetid";
				break;

			case WordNetDispatcher.SEMRELATIONS_SYNSETS_X:
				table = "semrelations AS " + WordNetContract.RELATION + ' ' + //
						"INNER JOIN relations USING (relationid) " + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET2 + " ON " + WordNetContract.RELATION + ".synset2id = " + WordNetContract.SYNSET2 + ".synsetid ";
				break;

			case WordNetDispatcher.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET:
				table = "semrelations AS " + WordNetContract.RELATION + ' ' + //
						"INNER JOIN relations USING (relationid) " + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET2 + " ON " + WordNetContract.RELATION + ".synset2id = " + WordNetContract.SYNSET2 + ".synsetid " + //
						"LEFT JOIN senses ON " + WordNetContract.SYNSET2 + ".synsetid = senses.synsetid " + //
						"LEFT JOIN words USING (wordid)";
				projection = BaseProvider.appendProjection(projection, "GROUP_CONCAT(words.word, ', ' ) AS " + WordNetContract.SemRelations_Synsets_Words_X.MEMBERS2);
				groupBy = WordNetContract.SYNSET2 + ".synsetid";
				break;

			case WordNetDispatcher.LEXRELATIONS_SENSES:
				table = "lexrelations AS " + WordNetContract.RELATION + ' ' + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET2 + " ON " + WordNetContract.RELATION + ".synset2id = " + WordNetContract.SYNSET2 + ".synsetid " + //
						"INNER JOIN words AS " + WordNetContract.WORD + " ON " + WordNetContract.RELATION + ".word2id = " + WordNetContract.WORD + ".wordid";
				break;

			case WordNetDispatcher.LEXRELATIONS_SENSES_X:
				table = "lexrelations AS " + WordNetContract.RELATION + ' ' + //
						"INNER JOIN relations USING (relationid) " + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET2 + " ON " + WordNetContract.RELATION + ".synset2id = " + WordNetContract.SYNSET2 + ".synsetid " + //
						"INNER JOIN words AS " + WordNetContract.WORD + " ON " + WordNetContract.RELATION + ".word2id = " + WordNetContract.WORD + ".wordid ";
				break;

			case WordNetDispatcher.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET:
				table = "lexrelations AS " + WordNetContract.RELATION + ' ' + //
						"INNER JOIN relations USING (relationid) " + //
						"INNER JOIN synsets AS " + WordNetContract.SYNSET2 + " ON " + WordNetContract.RELATION + ".synset2id = " + WordNetContract.SYNSET2 + ".synsetid " + //
						"INNER JOIN words AS " + WordNetContract.WORD + " ON " + WordNetContract.RELATION + ".word2id = " + WordNetContract.WORD + ".wordid " + //
						"LEFT JOIN senses AS " + WordNetContract.SENSE + " ON " + WordNetContract.SYNSET2 + ".synsetid = " + WordNetContract.SENSE + ".synsetid " + //
						"LEFT JOIN words AS " + WordNetContract.WORD2 + " USING (wordid)";
				projection = BaseProvider.appendProjection(projection, "GROUP_CONCAT(DISTINCT " + WordNetContract.WORD2 + ".word) AS " + WordNetContract.LexRelations_Senses_Words_X.MEMBERS2);
				groupBy = WordNetContract.SYNSET2 + ".synsetid";
				break;

			case WordNetDispatcher.SENSES_VFRAMES:
				table = "senses_vframes " + //
						"LEFT JOIN vframes USING (frameid)";
				break;

			case WordNetDispatcher.SENSES_VTEMPLATES:
				table = "senses_vtemplates " + //
						"LEFT JOIN vtemplates USING (templateid)";
				break;

			case WordNetDispatcher.SENSES_ADJPOSITIONS:
				table = "senses_adjpositions " + //
						"LEFT JOIN adjpositions USING (positionid)";
				break;

			case WordNetDispatcher.LEXES_MORPHS:
				table = "lexes_morphs " + //
						"LEFT JOIN morphs USING (morphid)";
				break;

			case WordNetDispatcher.WORDS_LEXES_MORPHS:
				table = "words " + //
						"LEFT JOIN lexes_morphs USING (wordid) " + //
						"LEFT JOIN morphs USING (morphid)";
				break;

			case WordNetDispatcher.WORDS_LEXES_MORPHS_BY_WORD:
				table = "words " + //
						"LEFT JOIN lexes_morphs USING (wordid) " + //
						"LEFT JOIN morphs USING (morphid)";
				groupBy = "wordid";
				break;

			// T E X T S E A R C H

			case WordNetDispatcher.LOOKUP_FTS_WORDS:
				table = "words_word_fts4";
				break;

			case WordNetDispatcher.LOOKUP_FTS_DEFINITIONS:
				table = "synsets_definition_fts4";
				break;

			case WordNetDispatcher.LOOKUP_FTS_SAMPLES:
				table = "samples_sample_fts4";
				break;

			// S U G G E S T

			case WordNetDispatcher.SUGGEST_WORDS:
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

			case WordNetDispatcher.SUGGEST_FTS_WORDS:
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

			case WordNetDispatcher.SUGGEST_FTS_DEFINITIONS:
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

			case WordNetDispatcher.SUGGEST_FTS_SAMPLES:
			{
				//				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				//				{
				//					return false;
				//				}
				projection = new String[]{"sampleid AS _id", //
						"sample AS " + SearchManager.SUGGEST_COLUMN_TEXT_1, //
						"sample AS " + SearchManager.SUGGEST_COLUMN_QUERY};
				selection = "sample MATCH ?";
				selectionArgs = new String[]{uriLast + '*'};
				table = "samples_sample_fts4";
				break;
			}

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs, groupBy);
	}

	public Result queryNew(int code, @NonNull final String uriLast, final String[] projection0, @Nullable final String selection0, final String[] selectionArgs0, final String sortOrder0, final Factory subqueryFactory)
	{
		String table;
		String[] projection = projection0;
		String selection = selection0;
		String[] selectionArgs = selectionArgs0;
		String groupBy = null;
		switch (code)
		{
			// T A B L E
			// table uri : last element is table

			case WordNetDispatcher.WORDS:
				table = Q.WORDS.TABLE;
				break;

			case WordNetDispatcher.SENSES:
				table = Q.SENSES.TABLE;
				break;

			case WordNetDispatcher.SYNSETS:
				table = Q.SYNSETS.TABLE;
				break;

			case WordNetDispatcher.SEMRELATIONS:
				table = Q.SEMRELATIONS.TABLE;
				break;

			case WordNetDispatcher.LEXRELATIONS:
				table = Q.LEXRELATIONS.TABLE;
				break;

			case WordNetDispatcher.RELATIONS:
				table = Q.RELATIONS.TABLE;
				break;

			case WordNetDispatcher.POSES:
				table = Q.POSES.TABLE;
				break;

			case WordNetDispatcher.DOMAINS:
				table = Q.DOMAINS.TABLE;
				break;

			case WordNetDispatcher.ADJPOSITIONS:
				table = Q.ADJPOSITIONS.TABLE;
				break;

			case WordNetDispatcher.SAMPLES:
				table = Q.SAMPLES.TABLE;
				break;

			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case WordNetDispatcher.WORD:
				table = Q.WORD1.TABLE;
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

			case WordNetDispatcher.SENSE:
				table = Q.SENSE1.TABLE;
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

			case WordNetDispatcher.SYNSET:
				table = Q.SYNSET1.TABLE;
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

			case WordNetDispatcher.DICT:
				table = Q.DICT.TABLE;
				break;

			// J O I N S

			case WordNetDispatcher.WORDS_SENSES_SYNSETS:
				table = Q.WORDS_SENSES_SYNSETS.TABLE;
				break;

			case WordNetDispatcher.WORDS_SENSES_CASEDWORDS_SYNSETS:
				table = Q.WORDS_SENSES_CASEDWORDS_SYNSETS.TABLE;
				break;

			case WordNetDispatcher.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS:
				table = Q.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS.TABLE;
				break;

			case WordNetDispatcher.SENSES_WORDS:
				table = Q.SENSES_WORDS.TABLE;
				break;

			case WordNetDispatcher.SENSES_WORDS_BY_SYNSET:
				table = Q.SENSES_WORDS_BY_SYNSET.TABLE;
				projection = BaseProvider.appendProjection(projection, Q.SENSES_WORDS_BY_SYNSET.PROJECTION[0].replaceAll("#\\{members\\}", WordNetContract.MEMBERS));
				groupBy = Q.SENSES_WORDS_BY_SYNSET.GROUPBY;
				break;

			case WordNetDispatcher.SENSES_SYNSETS_POSES_DOMAINS:
				table = Q.SENSES_SYNSETS_POSES_DOMAINS.TABLE;
				break;

			case WordNetDispatcher.SYNSETS_POSES_DOMAINS:
				table = Q.SYNSETS_POSES_DOMAINS.TABLE;
				break;

			case WordNetDispatcher.ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET:
				final String subQuery = subqueryFactory.make(selection0);
				table = Q.ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET.TABLE.replaceFirst("#\\{query\\}", subQuery);
				selection = null;
				groupBy = Q.ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET.GROUPBY.replaceAll("#\\{query_target_synsetid\\}", BaseModule.TARGET_SYNSETID) //
						.replaceAll("#\\{query_target_wordid\\}", BaseModule.TARGET_WORDID) //
						.replaceAll("#\\{query_target_word\\}", BaseModule.TARGET_WORD);
				break;

			case WordNetDispatcher.SEMRELATIONS_SYNSETS:
				table = Q.SEMRELATIONS_SYNSETS.TABLE;
				break;

			case WordNetDispatcher.SEMRELATIONS_SYNSETS_X:
				table = Q.SEMRELATIONS_SYNSETS_X.TABLE;
				break;

			case WordNetDispatcher.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET:
				table = Q.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.TABLE;
				projection = BaseProvider.appendProjection(projection, Q.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.PROJECTION[0].replaceAll("#\\{members2\\}", WordNetContract.MEMBERS2));
				groupBy = Q.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.GROUPBY;
				break;

			case WordNetDispatcher.LEXRELATIONS_SENSES:
				table = Q.LEXRELATIONS_SENSES.TABLE;
				break;

			case WordNetDispatcher.LEXRELATIONS_SENSES_X:
				table = Q.LEXRELATIONS_SENSES_X.TABLE;
				break;

			case WordNetDispatcher.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET:
				table = Q.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.TABLE;
				projection = BaseProvider.appendProjection(projection, Q.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.PROJECTION[0].replaceAll("#\\{members2\\}", WordNetContract.MEMBERS2));
				groupBy = Q.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.GROUPBY;
				break;

			case WordNetDispatcher.SENSES_VFRAMES:
				table = Q.SENSES_VFRAMES.TABLE;
				break;

			case WordNetDispatcher.SENSES_VTEMPLATES:
				table = Q.SENSES_VTEMPLATES.TABLE;
				break;

			case WordNetDispatcher.SENSES_ADJPOSITIONS:
				table = Q.SENSES_ADJPOSITIONS.TABLE;
				break;

			case WordNetDispatcher.LEXES_MORPHS:
				table = Q.LEXES_MORPHS.TABLE;
				break;

			case WordNetDispatcher.WORDS_LEXES_MORPHS:
				table = Q.WORDS_LEXES_MORPHS.TABLE;
				break;

			case WordNetDispatcher.WORDS_LEXES_MORPHS_BY_WORD:
				table = Q.WORDS_LEXES_MORPHS.TABLE;
				groupBy = Q.WORDS_LEXES_MORPHS_BY_WORD.GROUPBY;
				break;

			// T E X T S E A R C H

			case WordNetDispatcher.LOOKUP_FTS_WORDS:
				table = Q.LOOKUP_FTS_WORDS.TABLE;
				break;

			case WordNetDispatcher.LOOKUP_FTS_DEFINITIONS:
				table = Q.LOOKUP_FTS_DEFINITIONS.TABLE;
				break;

			case WordNetDispatcher.LOOKUP_FTS_SAMPLES:
				table = Q.LOOKUP_FTS_SAMPLES.TABLE;
				break;

			// S U G G E S T

			case WordNetDispatcher.SUGGEST_WORDS:
			{
				//				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				//				{
				//					return false;
				//				}
				projection = Q.SUGGEST_WORDS.PROJECTION;
				selection = Q.SUGGEST_WORDS.SELECTION;
				selectionArgs = new String[]{Q.SUGGEST_WORDS.ARGS[0].replaceAll("#\\{uri_last\\}", uriLast)};
				table = Q.SUGGEST_WORDS.TABLE;
				break;
			}

			case WordNetDispatcher.SUGGEST_FTS_WORDS:
			{
				//				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				//				{
				//					return false;
				//				}
				projection = Q.SUGGEST_FTS_WORDS.PROJECTION;
				selection = Q.SUGGEST_FTS_WORDS.SELECTION;
				selectionArgs = new String[]{Q.SUGGEST_FTS_WORDS.ARGS[0].replaceAll("#\\{uri_last\\}", uriLast)};
				table = Q.SUGGEST_FTS_WORDS.TABLE;
				break;
			}

			case WordNetDispatcher.SUGGEST_FTS_DEFINITIONS:
			{
				projection = Q.SUGGEST_FTS_DEFINITIONS.PROJECTION;
				selection = Q.SUGGEST_FTS_DEFINITIONS.SELECTION;
				selectionArgs = new String[]{Q.SUGGEST_FTS_DEFINITIONS.ARGS[0].replaceAll("#\\{uri_last\\}", uriLast)};
				table = Q.SUGGEST_FTS_DEFINITIONS.TABLE;
				break;
			}

			case WordNetDispatcher.SUGGEST_FTS_SAMPLES:
			{
				//				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				//				{
				//					return false;
				//				}
				projection = Q.SUGGEST_FTS_SAMPLES.PROJECTION;
				selection = Q.SUGGEST_FTS_SAMPLES.SELECTION;
				selectionArgs = new String[]{Q.SUGGEST_FTS_SAMPLES.ARGS[0].replaceAll("#\\{uri_last\\}", uriLast)};
				table = Q.SUGGEST_FTS_SAMPLES.TABLE;
				break;
			}

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs, groupBy);
	}

	public Result queryProvider(int code, @NonNull final String uriLast, final String[] projection0, @Nullable final String selection0, final String[] selectionArgs0, final String sortOrder0, final Factory subqueryFactory)
	{
		Result r = WordNetDispatcher.queryMain(code, uriLast, projection0, selection0, selectionArgs0);
		if (r == null)
		{
			r = WordNetDispatcher.queryAllRelations(code, projection0, selection0, selectionArgs0, subqueryFactory);
			if (r == null)
			{
				r = WordNetDispatcher.querySearch(code, projection0, selection0, selectionArgs0);
				if (r == null)
				{
					r = WordNetDispatcher.querySuggest(code, uriLast);
				}
			}
		}
		return r;
	}

	private void check(final int code, final Result r1, final Result r2)
	{
		assert equals(r1.table, r2.table) : "Code=" + code + " " + r1.table + " != " + r2.table;
		assert Arrays.equals(r1.projection, r2.projection) : "Code=" + code + " " + Arrays.toString(r1.projection) + " != " + Arrays.toString(r2.projection);
		assert equals(r1.selection, r2.selection) : "Code=" + code + " " + r1.selection + " != " + r2.selection;
		assert Arrays.equals(r1.selectionArgs, r2.selectionArgs) : "Code=" + code + " " + Arrays.toString(r1.selectionArgs) + " != " + Arrays.toString(r2.selectionArgs);
		assert equals(r1.groupBy, r2.groupBy) : "Code=" + code + " " + r1.groupBy + " != " + r2.groupBy;
	}

	private static boolean equals(Object a, Object b)
	{
		return (a == b) || (a != null && a.equals(b));
	}
}