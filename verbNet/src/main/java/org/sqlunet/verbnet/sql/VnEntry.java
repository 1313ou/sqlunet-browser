package org.sqlunet.verbnet.sql;

import java.util.ArrayList;
import java.util.List;

import org.sqlunet.wordnet.sql.BasicWord;

import android.database.sqlite.SQLiteDatabase;

/**
 * VerbNet entry
 *
 * @author Bernard Bou
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
	 * @param thisWord
	 *            is the word string
	 * @param theseSynsets
	 *            is the list of synsets attached to this entry
	 */
	private VnEntry(final BasicWord thisWord, final List<VnSynset> theseSynsets)
	{
		this.word = thisWord;
		this.synsets = theseSynsets;
	}

	/**
	 * Make word
	 *
	 * @param connection
	 *            is the database connection
	 * @param lemma
	 *            is the target string
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
