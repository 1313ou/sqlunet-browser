package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.wordnet.sql.BasicWord;

import java.util.ArrayList;
import java.util.List;

/**
 * VerbNet entry
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class VnEntry
{
	/**
	 * Word for this entry
	 */
	public final BasicWord word;

	/**
	 * Synsets attached to VerbNet entry
	 */
	public final List<VnSynset> synsets;

	/**
	 * FnEntry
	 *
	 * @param word    is the word string
	 * @param synsets is the list of synsets attached to this entry
	 */
	private VnEntry(final BasicWord word, final List<VnSynset> synsets)
	{
		this.word = word;
		this.synsets = synsets;
	}

	/**
	 * Make word
	 *
	 * @param connection database connection
	 * @param lemma      target string
	 * @return Word or null
	 */
	static public VnEntry make(final SQLiteDatabase connection, final String lemma)
	{
		VnEntry entry = null;
		VnQueryCommand query = null;
		try
		{
			query = new VnQueryCommand(connection, lemma);
			query.execute();

			long wordId = -1;

			// synsets
			List<VnSynset> synsets = null;
			while (query.next())
			{
				wordId = query.getWordId();
				if (synsets == null)
				{
					synsets = new ArrayList<>();
				}
				synsets.add(new VnSynset(query));
			}
			//noinspection ConstantConditions
			if (wordId != -1 && synsets != null)
			{
				entry = new VnEntry(new BasicWord(lemma, wordId), synsets);
			}
		}
		finally
		{
			if (query != null)
			{
				query.release();
			}
		}
		return entry;
	}
}
