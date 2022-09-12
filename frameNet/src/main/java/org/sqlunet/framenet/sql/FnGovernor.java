/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Governor
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class FnGovernor
{
	/**
	 * Governor id
	 */
	public final long governorId;

	/**
	 * Word id
	 */
	public final long wordId;

	/**
	 * Governor
	 */
	public final String governor;

	/**
	 * Constructor
	 *
	 * @param governorId governor id
	 * @param wordId     word id
	 * @param governor   governor
	 */
	private FnGovernor(final long governorId, final long wordId, final String governor)
	{
		this.governorId = governorId;
		this.wordId = wordId;
		this.governor = governor;
	}

	/**
	 * Make set of governors from query built from lex unit
	 *
	 * @param connection connection
	 * @param luId       target lex unit id
	 * @return list of governors
	 */
	@Nullable
	static public List<FnGovernor> make(final SQLiteDatabase connection, final long luId)
	{
		List<FnGovernor> result = null;
		try (FnGovernorQueryFromLexUnitId query = new FnGovernorQueryFromLexUnitId(connection, luId))
		{
			query.execute();

			while (query.next())
			{
				final long governorId = query.getGovernorId();
				final long wordId = query.getWordId();
				final String governor = query.getGovernor();
				if (result == null)
				{
					result = new ArrayList<>();
				}
				result.add(new FnGovernor(governorId, wordId, governor));
			}
		}
		return result;
	}
}
