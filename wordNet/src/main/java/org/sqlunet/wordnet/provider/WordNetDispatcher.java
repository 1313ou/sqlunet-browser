/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.provider;

import android.app.SearchManager;

import org.sqlunet.provider.BaseProvider;
import org.sqlunet.wordnet.loaders.BaseModule;
import org.sqlunet.wordnet.provider.WordNetContract.Senses;
import org.sqlunet.wordnet.provider.WordNetContract.Synsets;
import org.sqlunet.wordnet.provider.WordNetContract.Words;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * WordNet provider
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class WordNetDispatcher
{
	// table codes
	static final int WORDS = 10;
	static final int WORD = 11;
	static final int SENSES = 20;
	static final int SENSE = 21;
	static final int SYNSETS = 30;
	static final int SYNSET = 31;
	static final int SEMRELATIONS = 40;
	static final int LEXRELATIONS = 50;
	static final int RELATIONS = 60;
	static final int POSES = 70;
	static final int DOMAINS = 80;
	static final int ADJPOSITIONS = 90;
	static final int SAMPLES = 100;

	// view codes
	static final int DICT = 200;

	// join codes
	static final int WORDS_SENSES_SYNSETS = 310;
	static final int WORDS_SENSES_CASEDWORDS_SYNSETS = 311;
	static final int WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS = 312;
	static final int SENSES_WORDS = 320;
	static final int SENSES_WORDS_BY_SYNSET = 321;
	static final int SENSES_SYNSETS_POSES_DOMAINS = 330;
	static final int SYNSETS_POSES_DOMAINS = 340;

	static final int ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET = 400;
	static final int SEMRELATIONS_SYNSETS = 410;
	static final int SEMRELATIONS_SYNSETS_X = 411;
	static final int SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET = 412;
	static final int LEXRELATIONS_SENSES = 420;
	static final int LEXRELATIONS_SENSES_X = 421;
	static final int LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET = 422;

	static final int SENSES_VFRAMES = 510;
	static final int SENSES_VTEMPLATES = 515;
	static final int SENSES_ADJPOSITIONS = 520;
	static final int LEXES_MORPHS = 530;
	static final int WORDS_LEXES_MORPHS = 541;
	static final int WORDS_LEXES_MORPHS_BY_WORD = 542;

	// search text codes
	static final int LOOKUP_FTS_WORDS = 810;
	static final int LOOKUP_FTS_DEFINITIONS = 820;
	static final int LOOKUP_FTS_SAMPLES = 830;

	// suggest codes
	static final int SUGGEST_WORDS = 900;
	static final int SUGGEST_FTS_WORDS = 910;
	static final int SUGGEST_FTS_DEFINITIONS = 920;
	static final int SUGGEST_FTS_SAMPLES = 930;

	static public class Result
	{
		final String table;
		final String[] projection;
		final String selection;
		final String[] selectionArgs;
		final String groupBy;

		public Result(final String table, final String[] projection, final String selection, final String[] selectionArgs, final String groupBy)
		{
			this.table = table;
			this.projection = projection;
			this.selection = selection;
			this.selectionArgs = selectionArgs;
			this.groupBy = groupBy;
		}
	}

	@FunctionalInterface
	public interface Factory
	{
		String make(String selection);
	}

	public static Result queryMain(int code, @NonNull final String uriLast, final String[] projection0, @Nullable final String selection0, final String[] selectionArgs0)
	{
		String table;
		String[] projection = projection0;
		String selection = selection0;
		String groupBy = null;
		switch (code)
		{
			// T A B L E
			// table uri : last element is table

			case WordNetDispatcher.WORDS:
				table = Queries.WORDS.TABLE;
				break;

			case WordNetDispatcher.SENSES:
				table = Queries.SENSES.TABLE;
				break;

			case WordNetDispatcher.SYNSETS:
				table = Queries.SYNSETS.TABLE;
				break;

			case WordNetDispatcher.SEMRELATIONS:
				table = Queries.SEMRELATIONS.TABLE;
				break;

			case WordNetDispatcher.LEXRELATIONS:
				table = Queries.LEXRELATIONS.TABLE;
				break;

			case WordNetDispatcher.RELATIONS:
				table = Queries.RELATIONS.TABLE;
				break;

			case WordNetDispatcher.POSES:
				table = Queries.POSES.TABLE;
				break;

			case WordNetDispatcher.DOMAINS:
				table = Queries.DOMAINS.TABLE;
				break;

			case WordNetDispatcher.ADJPOSITIONS:
				table = Queries.ADJPOSITIONS.TABLE;
				break;

			case WordNetDispatcher.SAMPLES:
				table = Queries.SAMPLES.TABLE;
				break;

			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case WordNetDispatcher.WORD:
				table = Queries.WORD.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += Words.WORDID + " = " + uriLast;
				break;

			case WordNetDispatcher.SENSE:
				table = Queries.SENSE.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += Senses.SENSEID + " = " + uriLast;
				break;

			case WordNetDispatcher.SYNSET:
				table = Queries.SYNSET.TABLE;
				if (selection != null)
				{
					selection += " AND ";
				}
				else
				{
					selection = "";
				}
				selection += Synsets.SYNSETID + " = " + uriLast;
				break;

			// V I E W S

			case WordNetDispatcher.DICT:
				table = Queries.DICT.TABLE;
				break;

			// J O I N S

			case WordNetDispatcher.WORDS_SENSES_SYNSETS:
				table = Queries.WORDS_SENSES_SYNSETS.TABLE;
				break;

			case WordNetDispatcher.WORDS_SENSES_CASEDWORDS_SYNSETS:
				table = Queries.WORDS_SENSES_CASEDWORDS_SYNSETS.TABLE;
				break;

			case WordNetDispatcher.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS:
				table = Queries.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS.TABLE;
				break;

			case WordNetDispatcher.SENSES_WORDS:
				table = Queries.SENSES_WORDS.TABLE;
				break;

			case WordNetDispatcher.SENSES_WORDS_BY_SYNSET:
				table = Queries.SENSES_WORDS_BY_SYNSET.TABLE;
				projection = BaseProvider.appendProjection(projection, Queries.SENSES_WORDS_BY_SYNSET.PROJECTION[0].replaceAll("#\\{members\\}", WordNetContract.MEMBERS));
				groupBy = Queries.SENSES_WORDS_BY_SYNSET.GROUPBY;
				break;

			case WordNetDispatcher.SENSES_SYNSETS_POSES_DOMAINS:
				table = Queries.SENSES_SYNSETS_POSES_DOMAINS.TABLE;
				break;

			case WordNetDispatcher.SYNSETS_POSES_DOMAINS:
				table = Queries.SYNSETS_POSES_DOMAINS.TABLE;
				break;

			case WordNetDispatcher.SEMRELATIONS_SYNSETS:
				table = Queries.SEMRELATIONS_SYNSETS.TABLE;
				break;

			case WordNetDispatcher.SEMRELATIONS_SYNSETS_X:
				table = Queries.SEMRELATIONS_SYNSETS_X.TABLE;
				break;

			case WordNetDispatcher.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET:
				table = Queries.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.TABLE;
				projection = BaseProvider.appendProjection(projection, Queries.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.PROJECTION[0].replaceAll("#\\{members2\\}", WordNetContract.MEMBERS2));
				groupBy = Queries.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.GROUPBY;
				break;

			case WordNetDispatcher.LEXRELATIONS_SENSES:
				table = Queries.LEXRELATIONS_SENSES.TABLE;
				break;

			case WordNetDispatcher.LEXRELATIONS_SENSES_X:
				table = Queries.LEXRELATIONS_SENSES_X.TABLE;
				break;

			case WordNetDispatcher.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET:
				table = Queries.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.TABLE;
				projection = BaseProvider.appendProjection(projection, Queries.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.PROJECTION[0].replaceAll("#\\{members2\\}", WordNetContract.MEMBERS2));
				groupBy = Queries.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.GROUPBY;
				break;

			case WordNetDispatcher.SENSES_VFRAMES:
				table = Queries.SENSES_VFRAMES.TABLE;
				break;

			case WordNetDispatcher.SENSES_VTEMPLATES:
				table = Queries.SENSES_VTEMPLATES.TABLE;
				break;

			case WordNetDispatcher.SENSES_ADJPOSITIONS:
				table = Queries.SENSES_ADJPOSITIONS.TABLE;
				break;

			case WordNetDispatcher.LEXES_MORPHS:
				table = Queries.LEXES_MORPHS.TABLE;
				break;

			case WordNetDispatcher.WORDS_LEXES_MORPHS:
				table = Queries.WORDS_LEXES_MORPHS.TABLE;
				break;

			case WordNetDispatcher.WORDS_LEXES_MORPHS_BY_WORD:
				table = Queries.WORDS_LEXES_MORPHS.TABLE;
				groupBy = Queries.WORDS_LEXES_MORPHS_BY_WORD.GROUPBY;
				break;

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs0, groupBy);
	}

	public static Result queryAllRelations(int code, final String[] projection0, @Nullable final String selection0, final String[] selectionArgs0, final Factory subqueryFactory)
	{
		if (code == WordNetDispatcher.ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET)
		{
			final String subQuery = subqueryFactory.make(selection0);
			String table = Queries.ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET.TABLE.replaceFirst("#\\{query\\}", subQuery);
			String groupBy = Queries.ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET.GROUPBY.replaceAll("#\\{query_target_synsetid\\}", BaseModule.TARGET_SYNSETID) //
					.replaceAll("#\\{query_target_wordid\\}", BaseModule.TARGET_WORDID) //
					.replaceAll("#\\{query_target_word\\}", BaseModule.TARGET_WORD);
			return new Result(table, projection0, null, selectionArgs0, groupBy);
		}
		return null;
	}

	public static Result querySearch(int code, final String[] projection0, @Nullable final String selection0, final String[] selectionArgs0)
	{
		String table;
		switch (code)
		{
			// T E X T S E A R C H

			case WordNetDispatcher.LOOKUP_FTS_WORDS:
				table = Queries.LOOKUP_FTS_WORDS.TABLE;
				break;

			case WordNetDispatcher.LOOKUP_FTS_DEFINITIONS:
				table = Queries.LOOKUP_FTS_DEFINITIONS.TABLE;
				break;

			case WordNetDispatcher.LOOKUP_FTS_SAMPLES:
				table = Queries.LOOKUP_FTS_SAMPLES.TABLE;
				break;

			default:
				return null;
		}
		return new Result(table, projection0, selection0, selectionArgs0, null);
	}

	public static Result querySuggest(int code, @NonNull final String uriLast)
	{
		String table;
		String[] projection;
		String selection;
		String[] selectionArgs;
		switch (code)
		{
			case WordNetDispatcher.SUGGEST_WORDS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				projection = Queries.SUGGEST_WORDS.PROJECTION;
				selection = Queries.SUGGEST_WORDS.SELECTION;
				selectionArgs = new String[]{Queries.SUGGEST_WORDS.ARGS[0].replaceAll("#\\{uri_last\\}", uriLast)};
				table = Queries.SUGGEST_WORDS.TABLE;
				break;
			}

			case WordNetDispatcher.SUGGEST_FTS_WORDS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				projection = Queries.SUGGEST_FTS_WORDS.PROJECTION;
				selection = Queries.SUGGEST_FTS_WORDS.SELECTION;
				selectionArgs = new String[]{Queries.SUGGEST_FTS_WORDS.ARGS[0].replaceAll("#\\{uri_last\\}", uriLast)};
				table = Queries.SUGGEST_FTS_WORDS.TABLE;
				break;
			}

			case WordNetDispatcher.SUGGEST_FTS_DEFINITIONS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				projection = Queries.SUGGEST_FTS_DEFINITIONS.PROJECTION;
				selection = Queries.SUGGEST_FTS_DEFINITIONS.SELECTION;
				selectionArgs = new String[]{Queries.SUGGEST_FTS_DEFINITIONS.ARGS[0].replaceAll("#\\{uri_last\\}", uriLast)};
				table = Queries.SUGGEST_FTS_DEFINITIONS.TABLE;
				break;
			}

			case WordNetDispatcher.SUGGEST_FTS_SAMPLES:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				table = Queries.SUGGEST_FTS_SAMPLES.TABLE;
				projection = Queries.SUGGEST_FTS_SAMPLES.PROJECTION;
				selection = Queries.SUGGEST_FTS_SAMPLES.SELECTION;
				selectionArgs = new String[]{Queries.SUGGEST_FTS_SAMPLES.ARGS[0].replaceAll("#\\{uri_last\\}", uriLast)};
				break;
			}

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs, null);
	}
}
