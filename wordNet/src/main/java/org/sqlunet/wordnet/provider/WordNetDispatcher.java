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
 * WordNet query dispatcher
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

			case WORDS:
				table = Q.WORDS.TABLE;
				break;

			case SENSES:
				table = Q.SENSES.TABLE;
				break;

			case SYNSETS:
				table = Q.SYNSETS.TABLE;
				break;

			case SEMRELATIONS:
				table = Q.SEMRELATIONS.TABLE;
				break;

			case LEXRELATIONS:
				table = Q.LEXRELATIONS.TABLE;
				break;

			case RELATIONS:
				table = Q.RELATIONS.TABLE;
				break;

			case POSES:
				table = Q.POSES.TABLE;
				break;

			case DOMAINS:
				table = Q.DOMAINS.TABLE;
				break;

			case ADJPOSITIONS:
				table = Q.ADJPOSITIONS.TABLE;
				break;

			case SAMPLES:
				table = Q.SAMPLES.TABLE;
				break;

			// I T E M
			// the incoming URI was for a single item because this URI was for a single row, the _ID value part is present.
			// get the last path segment from the URI: this is the _ID value. then, append the value to the WHERE clause for the query

			case WORD:
				table = Q.WORDS.TABLE;
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

			case SENSE:
				table = Q.SENSE1.TABLE;
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

			case SYNSET:
				table = Q.SYNSET1.TABLE;
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

			case DICT:
				table = Q.DICT.TABLE;
				break;

			// J O I N S

			case WORDS_SENSES_SYNSETS:
				table = Q.WORDS_SENSES_SYNSETS.TABLE;
				break;

			case WORDS_SENSES_CASEDWORDS_SYNSETS:
				table = Q.WORDS_SENSES_CASEDWORDS_SYNSETS.TABLE;
				break;

			case WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS:
				table = Q.WORDS_SENSES_CASEDWORDS_SYNSETS_POSES_DOMAINS.TABLE;
				break;

			case SENSES_WORDS:
				table = Q.SENSES_WORDS.TABLE;
				break;

			case SENSES_WORDS_BY_SYNSET:
				table = Q.SENSES_WORDS_BY_SYNSET.TABLE;
				projection = BaseProvider.appendProjection(projection, Q.SENSES_WORDS_BY_SYNSET.PROJECTION[0].replaceAll("#\\{members\\}", WordNetContract.MEMBERS));
				groupBy = Q.SENSES_WORDS_BY_SYNSET.GROUPBY;
				break;

			case SENSES_SYNSETS_POSES_DOMAINS:
				table = Q.SENSES_SYNSETS_POSES_DOMAINS.TABLE;
				break;

			case SYNSETS_POSES_DOMAINS:
				table = Q.SYNSETS_POSES_DOMAINS.TABLE;
				break;

			case SEMRELATIONS_SYNSETS:
				table = Q.SEMRELATIONS_SYNSETS.TABLE;
				break;

			case SEMRELATIONS_SYNSETS_X:
				table = Q.SEMRELATIONS_SYNSETS_X.TABLE;
				break;

			case SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET:
				table = Q.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.TABLE;
				projection = BaseProvider.appendProjection(projection, Q.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.PROJECTION[0].replaceAll("#\\{members2\\}", WordNetContract.MEMBERS2));
				groupBy = Q.SEMRELATIONS_SYNSETS_WORDS_X_BY_SYNSET.GROUPBY;
				break;

			case LEXRELATIONS_SENSES:
				table = Q.LEXRELATIONS_SENSES.TABLE;
				break;

			case LEXRELATIONS_SENSES_X:
				table = Q.LEXRELATIONS_SENSES_X.TABLE;
				break;

			case LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET:
				table = Q.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.TABLE;
				projection = BaseProvider.appendProjection(projection, Q.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.PROJECTION[0].replaceAll("#\\{members2\\}", WordNetContract.MEMBERS2));
				groupBy = Q.LEXRELATIONS_SENSES_WORDS_X_BY_SYNSET.GROUPBY;
				break;

			case SENSES_VFRAMES:
				table = Q.SENSES_VFRAMES.TABLE;
				break;

			case SENSES_VTEMPLATES:
				table = Q.SENSES_VTEMPLATES.TABLE;
				break;

			case SENSES_ADJPOSITIONS:
				table = Q.SENSES_ADJPOSITIONS.TABLE;
				break;

			case LEXES_MORPHS:
				table = Q.LEXES_MORPHS.TABLE;
				break;

			case WORDS_LEXES_MORPHS:
				table = Q.WORDS_LEXES_MORPHS.TABLE;
				break;

			case WORDS_LEXES_MORPHS_BY_WORD:
				table = Q.WORDS_LEXES_MORPHS.TABLE;
				groupBy = Q.WORDS_LEXES_MORPHS_BY_WORD.GROUPBY;
				break;

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs0, groupBy);
	}

	public static Result queryAllRelations(int code, final String[] projection0, @Nullable final String selection0, final String[] selectionArgs0, final Factory subqueryFactory)
	{
		if (code == ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET)
		{
			final String subQuery = subqueryFactory.make(selection0);
			String table = Q.ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET.TABLE.replaceFirst("#\\{query\\}", subQuery);
			String groupBy = Q.ALLRELATIONS_SENSES_WORDS_X_BY_SYNSET.GROUPBY.replaceAll("#\\{query_target_synsetid\\}", BaseModule.TARGET_SYNSETID) //
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
			case LOOKUP_FTS_WORDS:
				table = Q.LOOKUP_FTS_WORDS.TABLE;
				break;

			case LOOKUP_FTS_DEFINITIONS:
				table = Q.LOOKUP_FTS_DEFINITIONS.TABLE;
				break;

			case LOOKUP_FTS_SAMPLES:
				table = Q.LOOKUP_FTS_SAMPLES.TABLE;
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
			case SUGGEST_WORDS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				projection = Q.SUGGEST_WORDS.PROJECTION;
				selection = Q.SUGGEST_WORDS.SELECTION;
				selectionArgs = new String[]{Q.SUGGEST_WORDS.ARGS[0].replaceAll("#\\{uri_last\\}", uriLast)};
				table = Q.SUGGEST_WORDS.TABLE;
				break;
			}

			case SUGGEST_FTS_WORDS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				projection = Q.SUGGEST_FTS_WORDS.PROJECTION;
				selection = Q.SUGGEST_FTS_WORDS.SELECTION;
				selectionArgs = new String[]{Q.SUGGEST_FTS_WORDS.ARGS[0].replaceAll("#\\{uri_last\\}", uriLast)};
				table = Q.SUGGEST_FTS_WORDS.TABLE;
				break;
			}

			case SUGGEST_FTS_DEFINITIONS:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				projection = Q.SUGGEST_FTS_DEFINITIONS.PROJECTION;
				selection = Q.SUGGEST_FTS_DEFINITIONS.SELECTION;
				selectionArgs = new String[]{Q.SUGGEST_FTS_DEFINITIONS.ARGS[0].replaceAll("#\\{uri_last\\}", uriLast)};
				table = Q.SUGGEST_FTS_DEFINITIONS.TABLE;
				break;
			}

			case SUGGEST_FTS_SAMPLES:
			{
				if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(uriLast))
				{
					return null;
				}
				table = Q.SUGGEST_FTS_SAMPLES.TABLE;
				projection = Q.SUGGEST_FTS_SAMPLES.PROJECTION;
				selection = Q.SUGGEST_FTS_SAMPLES.SELECTION;
				selectionArgs = new String[]{Q.SUGGEST_FTS_SAMPLES.ARGS[0].replaceAll("#\\{uri_last\\}", uriLast)};
				break;
			}

			default:
				return null;
		}
		return new Result(table, projection, selection, selectionArgs, null);
	}
}
