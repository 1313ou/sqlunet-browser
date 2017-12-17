package org.sqlunet.verbnet.sql;

import android.support.annotation.NonNull;

import org.sqlunet.wordnet.sql.BasicSynset;

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
		super(query.getSynsetId(), query.getDefinition(), query.getLexDomainId(), null);
		this.flag = query.getSynsetSpecific();
	}
}
