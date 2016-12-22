package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

/**
 * AnnoSet
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnAnnoSet
{
	/**
	 * AnnoSet id
	 */
	public final long annoSetId;

	/**
	 * Sentence
	 */
	public final FnSentence sentence;

	/**
	 * Constructor
	 *
	 * @param annoSetId annoSet id
	 * @param sentence  sentence
	 */
	private FnAnnoSet(final long annoSetId, final FnSentence sentence)
	{
		this.annoSetId = annoSetId;
		this.sentence = sentence;
	}

	/**
	 * Make annoSet
	 *
	 * @param connection connection
	 * @param annoSetId  annoSet id
	 * @return annoSet
	 */
	public static FnAnnoSet make(final SQLiteDatabase connection, final long annoSetId)
	{
		FnAnnoSet result = null;
		FnAnnoSetQuery query = null;
		try
		{
			query = new FnAnnoSetQuery(connection, annoSetId);
			query.execute();

			if (query.next())
			{
				final long sentenceId = query.getSentenceId();
				final String text = query.getSentenceText();
				final FnSentence sentence = new FnSentence(sentenceId, text);
				result = new FnAnnoSet(annoSetId, sentence);
			}
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return result;
	}
}
