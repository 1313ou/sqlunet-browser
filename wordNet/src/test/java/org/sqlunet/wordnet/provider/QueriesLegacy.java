/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.wordnet.provider;

import android.app.SearchManager;

import org.sqlunet.provider.BaseProvider;
import org.sqlunet.wordnet.provider.WordNetControl.Factory;
import org.sqlunet.wordnet.provider.WordNetControl.Result;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Queries factory, which will execute on the development machine (host).
 */
public class QueriesLegacy
{
	public static Result queryLegacy(int code, @NonNull final String uriLast, final String[] projection0, @Nullable final String selection0, final String[] selectionArgs0, @SuppressWarnings("unused") final String sortOrder0, final Factory subqueryFactory)
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

			case WordNetControl.WORDS:
				table = WordNetContract.Words.TABLE;
				break;

			case WordNetControl.SENSES:
				table = WordNetContract.Senses.TABLE;
				break;

			case WordNetControl.SYNSETS:
				table = WordNetContract.Synsets.TABLE;
				break;

			case WordNetControl.SEMRELATIONS:
				table = WordNetContract.SemRelations.TABLE;
				break;

			case WordNetControl.LEXRELATIONS:
				table = WordNetContract.LexRelations.TABLE;
				break;

			case WordNetControl.RELATIONS:
				table = WordNetContract.Relations.TABLE;
				break;

			case WordNetControl.POSES:
				table = WordNetContract.Poses.TABLE;
				break;

			case WordNetControl.DOMAINS:
				table = WordNetContract.Domains.TABLE;
				break;

			case WordNetControl.ADJPOSITIONS:
				table = WordNetContract.AdjPositions.TABLE;
				break;

			case WordNetControl.SAMPLES:
				table = WordNetContract.Samples.TABLE;
				break;

			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case WordNetControl.WORD:
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

			case WordNetControl.SENSE:
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

			case WordNetControl.SYNSET:
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

			case WordNetControl.DICT:
				table = WordNetContract.Dict.TABLE;
				break;

			// J O I N S

			case WordNetControl.WORDS_SENSES_SYNSETS:
				table = "words AS " + WordNetContract.AS_WORDS + " " + //
						"LEFT JOIN senses AS " + WordNetContract.AS_SENSES + " USING (wordid) " + //
						"LEFT JOIN synsets AS " + WordNetContract.AS_SYNSETS + " USING (synsetid)";
				break;

			case WordNetControl.WORDS_SENSES_CASEDWORDS_SYNSETS:
				table = "words AS " + WordNetContract.AS_WORDS + " " + //
						"LEFT JOIN senses AS " + WordNetContract.AS_SENSES + " USING (wordid) " + //
						"LEFT JOIN casedwords AS " + WordNetContract.AS_CASEDS + " USING (wordid,casedwordid) " + //
						"LEFT JOIN synsets AS " + WordNetContract.AS_SYNSETS + " USING (synsetid)";
				break;

			case WordNetControl.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS:
				table = "lexes AS " + WordNetContract.AS_LEXES + " " + //
						"INNER JOIN words AS " + WordNetContract.AS_WORDS + " USING (wordid) " + //
						"LEFT JOIN senses AS " + WordNetContract.AS_SENSES + " USING (wordid) " + //
						"LEFT JOIN casedwords AS " + WordNetContract.AS_CASEDS + " USING (wordid,casedwordid) " + //
						"LEFT JOIN synsets AS " + WordNetContract.AS_SYNSETS + " USING (synsetid) " + //
						"LEFT JOIN poses AS " + WordNetContract.AS_POSES + " USING (posid) " + //
						"LEFT JOIN domains AS " + WordNetContract.AS_DOMAINS + " USING (domainid)";
				break;

			case WordNetControl.SENSES_WORDS:
				table = "senses AS " + WordNetContract.AS_SENSES + " " + //
						"LEFT JOIN words AS " + WordNetContract.AS_WORDS + " USING (wordid)";
				break;

			case WordNetControl.SENSES_WORDS_BY_SYNSET:
				table = "senses AS " + WordNetContract.AS_SENSES + " " + //
						"LEFT JOIN words AS " + WordNetContract.AS_WORDS + " USING (wordid)";
				projection = BaseProvider.appendProjection(projection, "GROUP_CONCAT(DISTINCT " + WordNetContract.AS_WORDS + ".word) AS " + WordNetContract.Senses_Words.MEMBERS);
				groupBy = "synsetid";
				break;

			case WordNetControl.SENSES_SYNSETS_POSES_DOMAINS:
				table = "senses AS " + WordNetContract.AS_SENSES + " " + //
						"INNER JOIN synsets AS " + WordNetContract.AS_SYNSETS + " USING (synsetid) " + //
						"LEFT JOIN poses AS " + WordNetContract.AS_POSES + " USING (posid) " + //
						"LEFT JOIN domains AS " + WordNetContract.AS_DOMAINS + " USING (domainid)";
				break;

			case WordNetControl.SYNSETS_POSES_DOMAINS:
				table = "synsets AS " + WordNetContract.AS_SYNSETS + " " + //
						"LEFT JOIN poses AS " + WordNetContract.AS_POSES + " USING (posid) " + //
						"LEFT JOIN domains AS " + WordNetContract.AS_DOMAINS + " USING (domainid)";
				break;

			case WordNetControl.ANYRELATIONS_SENSES_WORDS_X_BY_SYNSET:
				final String subQuery = subqueryFactory.make(selection0);
				table = "( " + subQuery + " ) AS " + WordNetContract.AS_RELATIONS + ' ' + //
						"INNER JOIN relations USING (relationid) " + //
						"INNER JOIN synsets AS " + WordNetContract.AS_SYNSETS2 + " ON " + WordNetContract.AS_RELATIONS + ".synset2id = " + WordNetContract.AS_SYNSETS2 + ".synsetid " + //
						"LEFT JOIN senses ON " + WordNetContract.AS_SYNSETS2 + ".synsetid = senses.synsetid " + //
						"LEFT JOIN words AS " + WordNetContract.AS_WORDS + " USING (wordid) " + //
						"LEFT JOIN words AS " + WordNetContract.AS_WORDS2 + " ON " + WordNetContract.AS_RELATIONS + ".word2id = " + WordNetContract.AS_WORDS2 + ".wordid";
				selection = null;
				groupBy = V.SYNSET2ID + "," + WordNetContract.RELATIONTYPE + ",relation,relationid," + V.WORD2ID + ',' + V.WORD2;
				break;

			case WordNetControl.SEMRELATIONS_SYNSETS:
				table = "semrelations AS " + WordNetContract.AS_RELATIONS + ' ' + //
						"INNER JOIN synsets AS " + WordNetContract.AS_SYNSETS2 + " ON " + WordNetContract.AS_RELATIONS + ".synset2id = " + WordNetContract.AS_SYNSETS2 + ".synsetid";
				break;

			case WordNetControl.SEMRELATIONS_SYNSETS_X:
				table = "semrelations AS " + WordNetContract.AS_RELATIONS + ' ' + //
						"INNER JOIN relations USING (relationid) " + //
						"INNER JOIN synsets AS " + WordNetContract.AS_SYNSETS2 + " ON " + WordNetContract.AS_RELATIONS + ".synset2id = " + WordNetContract.AS_SYNSETS2 + ".synsetid ";
				break;

			case WordNetControl.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET:
				table = "semrelations AS " + WordNetContract.AS_RELATIONS + ' ' + //
						"INNER JOIN relations USING (relationid) " + //
						"INNER JOIN synsets AS " + WordNetContract.AS_SYNSETS2 + " ON " + WordNetContract.AS_RELATIONS + ".synset2id = " + WordNetContract.AS_SYNSETS2 + ".synsetid " + //
						"LEFT JOIN senses ON " + WordNetContract.AS_SYNSETS2 + ".synsetid = senses.synsetid " + //
						"LEFT JOIN words AS " + WordNetContract.AS_WORDS2 + " USING (wordid)";
				projection = BaseProvider.appendProjection(projection, "GROUP_CONCAT(DISTINCT " + WordNetContract.AS_WORDS2 + ".word) AS " + WordNetContract.SemRelations_Synsets_Words_X.MEMBERS2);
				groupBy = WordNetContract.AS_SYNSETS2 + ".synsetid";
				break;

			case WordNetControl.LEXRELATIONS_SENSES:
				table = "lexrelations AS " + WordNetContract.AS_RELATIONS + ' ' + //
						"INNER JOIN synsets AS " + WordNetContract.AS_SYNSETS2 + " ON " + WordNetContract.AS_RELATIONS + ".synset2id = " + WordNetContract.AS_SYNSETS2 + ".synsetid " + //
						"INNER JOIN words AS " + WordNetContract.AS_WORDS + " ON " + WordNetContract.AS_RELATIONS + ".word2id = " + WordNetContract.AS_WORDS + ".wordid";
				break;

			case WordNetControl.LEXRELATIONS_SENSES_X:
				table = "lexrelations AS " + WordNetContract.AS_RELATIONS + ' ' + //
						"INNER JOIN relations USING (relationid) " + //
						"INNER JOIN synsets AS " + WordNetContract.AS_SYNSETS2 + " ON " + WordNetContract.AS_RELATIONS + ".synset2id = " + WordNetContract.AS_SYNSETS2 + ".synsetid " + //
						"INNER JOIN words AS " + WordNetContract.AS_WORDS + " ON " + WordNetContract.AS_RELATIONS + ".word2id = " + WordNetContract.AS_WORDS + ".wordid ";
				break;

			case WordNetControl.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET:
				table = "lexrelations AS " + WordNetContract.AS_RELATIONS + ' ' + //
						"INNER JOIN relations USING (relationid) " + //
						"INNER JOIN synsets AS " + WordNetContract.AS_SYNSETS2 + " ON " + WordNetContract.AS_RELATIONS + ".synset2id = " + WordNetContract.AS_SYNSETS2 + ".synsetid " + //
						"INNER JOIN words AS " + WordNetContract.AS_WORDS + " ON " + WordNetContract.AS_RELATIONS + ".word2id = " + WordNetContract.AS_WORDS + ".wordid " + //
						"LEFT JOIN senses AS " + WordNetContract.AS_SENSES + " ON " + WordNetContract.AS_SYNSETS2 + ".synsetid = " + WordNetContract.AS_SENSES + ".synsetid " + //
						"LEFT JOIN words AS " + WordNetContract.AS_WORDS2 + " USING (wordid)";
				projection = BaseProvider.appendProjection(projection, "GROUP_CONCAT(DISTINCT " + WordNetContract.AS_WORDS2 + ".word) AS " + WordNetContract.LexRelations_Senses_Words_X.MEMBERS2);
				groupBy = WordNetContract.AS_SYNSETS2 + ".synsetid";
				break;

			case WordNetControl.SENSES_VFRAMES:
				table = "senses_vframes " + //
						"LEFT JOIN vframes USING (frameid)";
				break;

			case WordNetControl.SENSES_VTEMPLATES:
				table = "senses_vtemplates " + //
						"LEFT JOIN vtemplates USING (templateid)";
				break;

			case WordNetControl.SENSES_ADJPOSITIONS:
				table = "senses_adjpositions " + //
						"LEFT JOIN adjpositions USING (positionid)";
				break;

			case WordNetControl.LEXES_MORPHS:
				table = "lexes_morphs " + //
						"LEFT JOIN morphs USING (morphid)";
				break;

			case WordNetControl.WORDS_LEXES_MORPHS:
				table = "words " + //
						"LEFT JOIN lexes_morphs USING (wordid) " + //
						"LEFT JOIN morphs USING (morphid)";
				break;

			case WordNetControl.WORDS_LEXES_MORPHS_BY_WORD:
				table = "words " + //
						"LEFT JOIN lexes_morphs USING (wordid) " + //
						"LEFT JOIN morphs USING (morphid)";
				groupBy = "wordid";
				break;

			// T E X T S E A R C H

			case WordNetControl.LOOKUP_FTS_WORDS:
				table = "words_word_fts4";
				break;

			case WordNetControl.LOOKUP_FTS_DEFINITIONS:
				table = "synsets_definition_fts4";
				break;

			case WordNetControl.LOOKUP_FTS_SAMPLES:
				table = "samples_sample_fts4";
				break;

			// S U G G E S T

			case WordNetControl.SUGGEST_WORDS:
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

			case WordNetControl.SUGGEST_FTS_WORDS:
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

			case WordNetControl.SUGGEST_FTS_DEFINITIONS:
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

			case WordNetControl.SUGGEST_FTS_SAMPLES:
			{
				//				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(last))
				//				{
				//					return false;
				//				}
				projection = new String[]{"synsetid AS _id", //
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
}