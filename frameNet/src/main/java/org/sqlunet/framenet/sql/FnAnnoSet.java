package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

/**
 * AnnoSet
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class FnAnnoSet
{
	public final long annoSetId;

	public final FnSentence sentence;

	private FnAnnoSet(final long annoSetId, final FnSentence sentence)
	{
		this.annoSetId = annoSetId;
		this.sentence = sentence;
	}

	public static FnAnnoSet make(final SQLiteDatabase connection, final long annoSetId)
	{
		FnAnnoSet result = null;
		FnAnnoSetQueryCommand query = null;
		try
		{
			query = new FnAnnoSetQueryCommand(connection, annoSetId);
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
