/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.syntagnet.loaders

import org.sqlunet.browser.Module.ContentProviderSql
import org.sqlunet.syntagnet.provider.SyntagNetContract

object Queries {

    @JvmStatic
    fun prepareSnSelect(wordId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = SyntagNetContract.SnCollocations_X.URI
        providerSql.projection = arrayOf( 
            SyntagNetContract.SnCollocations_X.COLLOCATIONID + " AS _id",  
            SyntagNetContract.SnCollocations_X.WORD1ID,  
            SyntagNetContract.SnCollocations_X.WORD2ID,  
            SyntagNetContract.SnCollocations_X.SYNSET1ID,  
            SyntagNetContract.SnCollocations_X.SYNSET2ID,  
            SyntagNetContract.AS_WORDS1 + '.' + SyntagNetContract.SnCollocations_X.WORD + " AS " + SyntagNetContract.WORD1,  
            SyntagNetContract.AS_WORDS2 + '.' + SyntagNetContract.SnCollocations_X.WORD + " AS " + SyntagNetContract.WORD2,  
            SyntagNetContract.AS_SYNSETS1 + '.' + SyntagNetContract.SnCollocations_X.POS + " AS " + SyntagNetContract.POS1,  
            SyntagNetContract.AS_SYNSETS2 + '.' + SyntagNetContract.SnCollocations_X.POS + " AS " + SyntagNetContract.POS2
        )
        providerSql.selection = SyntagNetContract.SnCollocations_X.WORD1ID + " = ? OR " + SyntagNetContract.SnCollocations_X.WORD2ID + " = ?" 
        providerSql.selectionArgs = arrayOf(wordId.toString(), wordId.toString(), wordId.toString())
        providerSql.sortBy = SyntagNetContract.SnCollocations_X.WORD2ID + " = ?" + ',' + SyntagNetContract.AS_WORDS1 + '.' + SyntagNetContract.SnCollocations_X.WORD + ',' + SyntagNetContract.AS_WORDS2 + '.' + SyntagNetContract.SnCollocations_X.WORD
        return providerSql
    }

    @JvmStatic
    fun prepareCollocation(collocationId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = SyntagNetContract.SnCollocations_X.URI
        providerSql.projection = arrayOf( 
            SyntagNetContract.SnCollocations_X.COLLOCATIONID,  
            SyntagNetContract.SnCollocations_X.WORD1ID,  
            SyntagNetContract.SnCollocations_X.WORD2ID,  
            SyntagNetContract.SnCollocations_X.SYNSET1ID,  
            SyntagNetContract.SnCollocations_X.SYNSET2ID,  
            SyntagNetContract.AS_WORDS1 + '.' + SyntagNetContract.SnCollocations_X.WORD + " AS " + SyntagNetContract.WORD1,  
            SyntagNetContract.AS_WORDS2 + '.' + SyntagNetContract.SnCollocations_X.WORD + " AS " + SyntagNetContract.WORD2,  
            SyntagNetContract.AS_SYNSETS1 + '.' + SyntagNetContract.SnCollocations_X.DEFINITION + " AS " + SyntagNetContract.DEFINITION1,  
            SyntagNetContract.AS_SYNSETS2 + '.' + SyntagNetContract.SnCollocations_X.DEFINITION + " AS " + SyntagNetContract.DEFINITION2,  
            SyntagNetContract.AS_SYNSETS1 + '.' + SyntagNetContract.SnCollocations_X.POS + " AS " + SyntagNetContract.POS1,  
            SyntagNetContract.AS_SYNSETS2 + '.' + SyntagNetContract.SnCollocations_X.POS + " AS " + SyntagNetContract.POS2
        )
        providerSql.selection = SyntagNetContract.SnCollocations_X.COLLOCATIONID + " = ?"
        providerSql.selectionArgs = arrayOf(collocationId.toString())
        return providerSql
    }

    @JvmStatic
    fun prepareCollocations(word1Id: Long?, word2Id: Long?, synset1Id: Long?, synset2Id: Long?): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = SyntagNetContract.SnCollocations_X.URI
        providerSql.projection = arrayOf( 
            SyntagNetContract.SnCollocations_X.COLLOCATIONID,  
            SyntagNetContract.SnCollocations_X.WORD1ID,  
            SyntagNetContract.SnCollocations_X.WORD1ID,  
            SyntagNetContract.SnCollocations_X.WORD2ID,  
            SyntagNetContract.SnCollocations_X.SYNSET1ID,  
            SyntagNetContract.SnCollocations_X.SYNSET2ID,  
            SyntagNetContract.AS_WORDS1 + '.' + SyntagNetContract.SnCollocations_X.WORD + " AS " + SyntagNetContract.WORD1,  
            SyntagNetContract.AS_WORDS2 + '.' + SyntagNetContract.SnCollocations_X.WORD + " AS " + SyntagNetContract.WORD2,  
            SyntagNetContract.AS_SYNSETS1 + '.' + SyntagNetContract.SnCollocations_X.DEFINITION + " AS " + SyntagNetContract.DEFINITION1,  
            SyntagNetContract.AS_SYNSETS2 + '.' + SyntagNetContract.SnCollocations_X.DEFINITION + " AS " + SyntagNetContract.DEFINITION2,  
            SyntagNetContract.AS_SYNSETS1 + '.' + SyntagNetContract.SnCollocations_X.POS + " AS " + SyntagNetContract.POS1,  
            SyntagNetContract.AS_SYNSETS2 + '.' + SyntagNetContract.SnCollocations_X.POS + " AS " + SyntagNetContract.POS2
        )
        providerSql.selection = selection(word1Id, word2Id, synset1Id, synset2Id)
        providerSql.selectionArgs = selectionArgs(word1Id, word2Id, synset1Id, synset2Id, word2Id)
        providerSql.sortBy = SyntagNetContract.AS_WORDS1 + '.' + SyntagNetContract.SnCollocations_X.WORD + ',' + SyntagNetContract.AS_WORDS2 + '.' + SyntagNetContract.SnCollocations_X.WORD
        if (word2Id != null) {
            providerSql.sortBy = SyntagNetContract.SnCollocations_X.WORD2ID + " = ?" + ',' + providerSql.sortBy
        }
        return providerSql
    }

    @JvmStatic
    fun prepareCollocations(wordId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = SyntagNetContract.SnCollocations_X.URI
        providerSql.projection = arrayOf( 
            SyntagNetContract.SnCollocations_X.WORD1ID,  
            SyntagNetContract.SnCollocations_X.WORD2ID,  
            SyntagNetContract.SnCollocations_X.SYNSET1ID,  
            SyntagNetContract.SnCollocations_X.SYNSET2ID,  
            SyntagNetContract.AS_WORDS1 + '.' + SyntagNetContract.SnCollocations_X.WORD + " AS " + SyntagNetContract.WORD1,  
            SyntagNetContract.AS_WORDS2 + '.' + SyntagNetContract.SnCollocations_X.WORD + " AS " + SyntagNetContract.WORD2
        )
        providerSql.selection = SyntagNetContract.SnCollocations_X.WORD1ID + " = ? OR " + SyntagNetContract.SnCollocations_X.WORD2ID + " = ?"
        providerSql.selectionArgs = arrayOf(wordId.toString(), wordId.toString(), wordId.toString())
        providerSql.sortBy = SyntagNetContract.SnCollocations_X.WORD2ID + " = ?" + ',' + SyntagNetContract.AS_WORDS1 + '.' + SyntagNetContract.SnCollocations_X.WORD + ',' + SyntagNetContract.AS_WORDS2 + '.' + SyntagNetContract.SnCollocations_X.WORD
        return providerSql
    }

    @JvmStatic
    fun prepareCollocations(word: String): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = SyntagNetContract.SnCollocations_X.URI
        providerSql.projection = arrayOf( 
            SyntagNetContract.SnCollocations_X.WORD1ID,  
            SyntagNetContract.SnCollocations_X.WORD2ID,  
            SyntagNetContract.SnCollocations_X.SYNSET1ID,  
            SyntagNetContract.SnCollocations_X.SYNSET2ID,  
            SyntagNetContract.AS_WORDS1 + '.' + SyntagNetContract.SnCollocations_X.WORD + " AS " + SyntagNetContract.WORD1,  
            SyntagNetContract.AS_WORDS2 + '.' + SyntagNetContract.SnCollocations_X.WORD + " AS " + SyntagNetContract.WORD2
        )
        providerSql.selection = SyntagNetContract.SnCollocations_X.WORD1ID + " = ? OR " + SyntagNetContract.SnCollocations_X.WORD2ID + " = ?"
        providerSql.selectionArgs = arrayOf(word)
        providerSql.sortBy = SyntagNetContract.SnCollocations_X.WORD2ID + " = ?" + ',' + SyntagNetContract.AS_WORDS1 + '.' + SyntagNetContract.SnCollocations_X.WORD + ',' + SyntagNetContract.AS_WORDS2 + '.' + SyntagNetContract.SnCollocations_X.WORD
        return providerSql
    }

    /**
     * Make selection. When both arg1 and arg2 are equal, take it to mean the position (after, before) is indifferent (so OR)
     *
     * @param word1Id   word 1 id
     * @param word2Id   word 2 id
     * @param synset1Id synset 1 id
     * @param synset2Id synset 2 id
     * @return selection string
     */
    private fun selection(word1Id: Long?, word2Id: Long?, synset1Id: Long?, synset2Id: Long?): String {
        val wordSelection1 = if (word1Id == null) "" else SyntagNetContract.SnCollocations_X.WORD1ID + " = ?"
        val wordSelection2 = if (word2Id == null) "" else SyntagNetContract.SnCollocations_X.WORD2ID + " = ?"
        val wordSelection = if (wordSelection1.isEmpty() || wordSelection2.isEmpty()) wordSelection1 + wordSelection2 else wordSelection1 + (if (word1Id == word2Id) " OR " else " AND ") + wordSelection2
        val synsetSelection1 = if (synset1Id == null) "" else SyntagNetContract.SnCollocations_X.SYNSET1ID + " = ?"
        val synsetSelection2 = if (synset2Id == null) "" else SyntagNetContract.SnCollocations_X.SYNSET2ID + " = ?"
        val synsetSelection = if (synsetSelection1.isEmpty() || synsetSelection2.isEmpty()) synsetSelection1 + synsetSelection2 else synsetSelection1 + (if (synset1Id == synset2Id) " OR " else " AND ") + synsetSelection2
        return if (wordSelection.isEmpty() || synsetSelection.isEmpty()) wordSelection + synsetSelection else "($wordSelection) AND ($synsetSelection)"
    }

    /**
     * Make selection arguments
     *
     * @param word1Id   word 1 id
     * @param word2Id   word 2 id
     * @param synset1Id synset 1 id
     * @param synset2Id synset 2 id
     * @param orderId   id param used in order clause
     * @return selection arguments
     */
    private fun selectionArgs(word1Id: Long?, word2Id: Long?, synset1Id: Long?, synset2Id: Long?, orderId: Long?): Array<String> {
        val args: MutableList<String> = ArrayList()
        if (word1Id != null) {
            args.add(word1Id.toString())
        }
        if (word2Id != null) {
            args.add(word2Id.toString())
        }
        if (synset1Id != null) {
            args.add(synset1Id.toString())
        }
        if (synset2Id != null) {
            args.add(synset2Id.toString())
        }
        if (orderId != null) {
            args.add(orderId.toString())
        }
        return args.toTypedArray<String>()
    }
}
