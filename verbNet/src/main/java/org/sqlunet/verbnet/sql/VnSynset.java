/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.sqlunet.verbnet.sql;

import org.sqlunet.wordnet.sql.BasicSynset;

import androidx.annotation.NonNull;

/**
 * Synset extended to hold VerbNet specific data
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
class VnSynset extends BasicSynset
{
	/**
	 * <code>flag</code> is a selection flag used by some queries
	 */
	public final boolean flag;

	/**
	 * Constructor from query for synsets
	 *
	 * @param query query for synsets
	 */
	VnSynset(@NonNull final VnClassQueryFromWordAndPos query)
	{
		super(query.getSynsetId(), query.getDefinition(), query.getDomainId(), null);
		this.flag = query.getSynsetSpecific();
	}
}
