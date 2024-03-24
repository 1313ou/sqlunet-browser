/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.verbnet.sql

import org.sqlunet.wordnet.sql.BasicSynset

/**
 * Synset extended to hold VerbNet specific data
 *
 * @param query query for synsets
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
class VnSynset(query: VnClassQueryFromWordAndPos) : BasicSynset(query.getSynsetId(), query.getDefinition(), query.getDomainId(), null) {
    /**
     * `flag` is a selection flag used by some queries
     */
    @JvmField
    val flag: Boolean

    /**
     * Constructor from query for synsets
     */
    init {
        flag = query.getSynsetSpecific()
    }
}
