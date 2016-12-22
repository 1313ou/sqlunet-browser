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
	 * Synsets attached to entry
	 */
	public final List<VnSynset> synsets;

	/**
	 * Constructor
	 *
	 * @param word    word string
	 * @param synsets list of synsets attached to this entry
	 */
	private VnEntry(final BasicWord word, final List<VnSynset> synsets)
	{
		this.word = word;
		this.synsets = synsets;
	}

	/**
	 * Make entry
	 *
	 * @param connection connection
	 * @param word       target word
	 * @return new entry or null
	 */
	static public VnEntry make(final SQLiteDatabase connection, final String word)
	{
		VnEntry entry = null;
		VnClassQueryFromWordAndPos query = null;
		try
		{
			query = new VnClassQueryFromWordAndPos(connection, word);
			query.execute();

			long wordId = 0;
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
			if (wordId != 0 && synsets != null)
			{
				entry = new VnEntry(new BasicWord(word, wordId), synsets);
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
