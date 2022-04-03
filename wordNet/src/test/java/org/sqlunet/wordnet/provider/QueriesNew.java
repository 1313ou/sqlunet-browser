package org.sqlunet.wordnet.provider;

import android.app.SearchManager;

import org.sqlunet.provider.BaseProvider;
import org.sqlunet.wordnet.provider.WordNetDispatcher.Factory;
import org.sqlunet.wordnet.provider.WordNetDispatcher.Result;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Queries factory, which will execute on the development machine (host).
 */
public class QueriesNew
{
	public static Result queryNew(int code, @NonNull final String uriLast, final String[] projection0, @Nullable final String selection0, final String[] selectionArgs0, final String sortOrder0, final Factory subqueryFactory)
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
				groupBy = Q.ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET.GROUPBY;
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
				projection[1] = projection[1].replaceAll("#\\{suggest_text_1\\}", SearchManager.SUGGEST_COLUMN_TEXT_1);
				projection[2] = projection[2].replaceAll("#\\{suggest_query\\}", SearchManager.SUGGEST_COLUMN_QUERY);
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
				projection[1] = projection[1].replaceAll("#\\{suggest_text_1\\}", SearchManager.SUGGEST_COLUMN_TEXT_1);
				projection[2] = projection[2].replaceAll("#\\{suggest_query\\}", SearchManager.SUGGEST_COLUMN_QUERY);
				selection = Q.SUGGEST_FTS_WORDS.SELECTION;
				selectionArgs = new String[]{Q.SUGGEST_FTS_WORDS.ARGS[0].replaceAll("#\\{uri_last\\}", uriLast)};
				table = Q.SUGGEST_FTS_WORDS.TABLE;
				break;
			}

			case WordNetDispatcher.SUGGEST_FTS_DEFINITIONS:
			{
				projection = Q.SUGGEST_FTS_DEFINITIONS.PROJECTION;
				projection[1] = projection[1].replaceAll("#\\{suggest_text_1\\}", SearchManager.SUGGEST_COLUMN_TEXT_1);
				projection[2] = projection[2].replaceAll("#\\{suggest_query\\}", SearchManager.SUGGEST_COLUMN_QUERY);
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
				projection[1] = projection[1].replaceAll("#\\{suggest_text_1\\}", SearchManager.SUGGEST_COLUMN_TEXT_1);
				projection[2] = projection[2].replaceAll("#\\{suggest_query\\}", SearchManager.SUGGEST_COLUMN_QUERY);
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
}