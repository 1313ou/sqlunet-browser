package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

public class FnAnnoSet
{
	public final long annoSetId;

	public final FnSentence sentence;

	private FnAnnoSet(final long thisAnnoSetId, final FnSentence thisSentence)
	{
		this.annoSetId = thisAnnoSetId;
		this.sentence = thisSentence;
	}

	public static FnAnnoSet make(final SQLiteDatabase thisConnection, final long thisAnnoSetId)
	{
		FnAnnoSet thisResult = null;
		FnAnnoSetQueryCommand thisQuery = null;
		try
		{
			thisQuery = new FnAnnoSetQueryCommand(thisConnection, thisAnnoSetId);
			thisQuery.execute();

			if (thisQuery.next())
			{
				final long thisSentenceId = thisQuery.getSentenceId();
				final String thisText = thisQuery.getSentenceText();
				final FnSentence thisSentence = new FnSentence(thisSentenceId, thisText);
				thisResult = new FnAnnoSet(thisAnnoSetId, thisSentence);
			}
		}
		finally
		{
			if (thisQuery != null)
			{
				thisQuery.release();
			}
		}
		return thisResult;
	}
}
