/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.loaders

import org.sqlunet.browser.Module.ContentProviderSql
import org.sqlunet.provider.XNetContract
import org.sqlunet.wordnet.provider.V
import org.sqlunet.wordnet.provider.WordNetContract

object Queries {

    const val MORPHS = "morphs"
    const val PRONUNCIATIONS = "pronunciations"

    // B R O W S E R

    fun prepareWordSelect(word: String): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.URI
        providerSql.projection = arrayOf(
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID + " AS _id",
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.WORDID,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEID,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSENUM,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEKEY,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.LEXID,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.TAGCOUNT,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.DEFINITION,
            WordNetContract.AS_SYNSETS + '.' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.POSID,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.POS,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.DOMAIN,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.CASEDWORD
        )
        providerSql.selection = WordNetContract.AS_WORDS + '.' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.WORD + " = ?" //
        providerSql.selectionArgs = arrayOf(word)
        providerSql.sortBy = WordNetContract.AS_LEXES + '.' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.POSID +
                ',' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.TAGCOUNT + " DESC" +
                ',' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.CASEDWORD +
                ',' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSENUM
        return providerSql
    }

    fun prepareWordXSelect(word: String): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.URI
        providerSql.projection = arrayOf(
            WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SYNSETID + " AS _id",
            WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.WORDID,
            WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SENSEID,
            WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SENSENUM,
            WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SENSEKEY,
            WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.LEXID,
            WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.TAGCOUNT,
            WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SYNSETID,
            WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.DEFINITION,
            WordNetContract.AS_SYNSETS + '.' + WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.POSID,
            WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.POS,
            WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.DOMAIN,
            WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.CASEDWORD,
            "GROUP_CONCAT(CASE WHEN " + WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.VARIETY + " IS NULL THEN '/'||" + WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.PRONUNCIATION + "||'/' ELSE '['||" + WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.VARIETY + "||'] '||'/'||" + WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.PRONUNCIATION + "||'/' END) AS " + PRONUNCIATIONS
        )
        providerSql.selection = WordNetContract.AS_WORDS + '.' + WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.WORD + " = ?" //
        providerSql.selectionArgs = arrayOf(word)
        providerSql.sortBy = WordNetContract.AS_LEXES + '.' + WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.POSID +
                ',' + WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.TAGCOUNT + " DESC" +
                ',' + WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.CASEDWORD +
                ',' + WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SENSENUM
        return providerSql
    }

    fun prepareWnXSelect(wordId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.URI
        providerSql.projection = arrayOf(
            "'wn' AS " + XNetContract.Words_XNet_U.SOURCES,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.WORDID,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID + " AS " + XNetContract.Words_XNet_U.XID,
            "NULL AS " + XNetContract.Words_XNet_U.XCLASSID,
            "NULL AS " + XNetContract.Words_XNet_U.XMEMBERID,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.WORD + "|| '.' ||" + WordNetContract.AS_POSES + '.' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.POSID + " AS " + XNetContract.Words_XNet_U.XNAME,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.DOMAIN + " AS " + XNetContract.Words_XNet_U.XHEADER,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEKEY + " AS " + XNetContract.Words_XNet_U.XINFO,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.DEFINITION + " AS " + XNetContract.Words_XNet_U.XDEFINITION,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID + " AS _id"
        )
        providerSql.selection = WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.WORDID + " = ?"
        providerSql.selectionArgs = arrayOf(wordId.toString())
        providerSql.sortBy = WordNetContract.AS_LEXES + '.' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.POSID +
                ',' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.TAGCOUNT + " DESC" +
                ',' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.CASEDWORD +
                ',' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSENUM
        return providerSql
    }

    fun prepareWnPronunciationXSelect(wordId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.URI
        providerSql.projection = arrayOf(
            "'wn' AS " + XNetContract.Words_XNet_U.SOURCES,
            WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.WORDID,
            WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SYNSETID,
            WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SYNSETID + " AS " + XNetContract.Words_XNet_U.XID,
            "NULL AS " + XNetContract.Words_XNet_U.XCLASSID,
            "NULL AS " + XNetContract.Words_XNet_U.XMEMBERID,
            WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.WORD + "|| '.' ||" + WordNetContract.AS_POSES + '.' + WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.POSID + " AS " + XNetContract.Words_XNet_U.XNAME,
            WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.DOMAIN + " AS " + XNetContract.Words_XNet_U.XHEADER,
            WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SENSEKEY + " AS " + XNetContract.Words_XNet_U.XINFO,
            WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.DEFINITION + " AS " + XNetContract.Words_XNet_U.XDEFINITION,
            "GROUP_CONCAT(CASE WHEN " + WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.VARIETY + " IS NULL THEN '/'||" + WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.PRONUNCIATION + "||'/' ELSE '['||" + WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.VARIETY + "||'] '||'/'||" + WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.PRONUNCIATION + "||'/' END) AS " + XNetContract.Words_XNet_U.XPRONUNCIATION,
            WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SYNSETID + " AS _id"
        )
        providerSql.selection = WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.WORDID + " = ?"
        providerSql.selectionArgs = arrayOf(wordId.toString())
        providerSql.sortBy = WordNetContract.AS_LEXES + '.' + WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.POSID +
                ',' + WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.TAGCOUNT + " DESC" +
                ',' + WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.CASEDWORD +
                ',' + WordNetContract.Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains.SENSENUM
        return providerSql
    }

    fun prepareWord(wordId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.Words_Lexes_Morphs.URI_BY_WORD
        providerSql.projection = arrayOf(
            WordNetContract.Words_Lexes_Morphs.WORD,
            WordNetContract.Words_Lexes_Morphs.WORDID,
            "GROUP_CONCAT(" + WordNetContract.Words_Lexes_Morphs.MORPH + "||'-'||" + WordNetContract.Words_Lexes_Morphs.POSID + ") AS " + MORPHS
        )
        providerSql.selection = WordNetContract.Words.WORDID + " = ?"
        providerSql.selectionArgs = arrayOf(wordId.toString())
        return providerSql
    }

    fun prepareSenses(word: String): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.URI
        providerSql.projection = arrayOf(
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID + " AS _id",
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.WORDID,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEID,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSENUM,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEKEY,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.LEXID,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.TAGCOUNT,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.DEFINITION,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.POS,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.DOMAIN,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.CASEDWORD
        )
        providerSql.selection = WordNetContract.AS_WORDS + '.' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.WORD + " = ?"
        providerSql.selectionArgs = arrayOf(word)
        providerSql.sortBy = WordNetContract.AS_LEXES + '.' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.POSID +
                ',' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.TAGCOUNT + " DESC" +
                ',' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.CASEDWORD +
                ',' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSENUM
        return providerSql
    }

    fun prepareSenses(wordId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.URI
        providerSql.projection = arrayOf(
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID + " AS _id",
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.WORDID,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEID,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSENUM,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEKEY,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.LEXID,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.TAGCOUNT,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.DEFINITION,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.POS,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.DOMAIN,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.CASEDWORD
        )
        providerSql.selection = WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.WORDID + " = ?"
        providerSql.selectionArgs = arrayOf(wordId.toString())
        providerSql.sortBy =
            WordNetContract.AS_LEXES + '.' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.POSID +
                    ',' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.TAGCOUNT + " DESC" +
                    ',' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.CASEDWORD +
                    ',' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSENUM
        return providerSql
    }

    fun prepareSense(senseId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.Senses.URI
        providerSql.projection = arrayOf(
            WordNetContract.Senses.WORDID,
            WordNetContract.Senses.SYNSETID
        )
        providerSql.selection = WordNetContract.Senses.SENSEID + " = ?"
        providerSql.selectionArgs = arrayOf(senseId.toString())
        return providerSql
    }

    fun prepareSense(senseKey: String): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.Senses.URI
        providerSql.projection = arrayOf(
            WordNetContract.Senses.WORDID,
            WordNetContract.Senses.SYNSETID
        )
        providerSql.selection = WordNetContract.Senses.SENSEKEY + " = ?"
        providerSql.selectionArgs = arrayOf(senseKey)
        return providerSql
    }

    fun prepareSense(synsetId: Long, @Suppress("UNUSED_PARAMETER") wordId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.Synsets_Poses_Domains.URI
        providerSql.projection = arrayOf(
            WordNetContract.Synsets.DEFINITION,
            WordNetContract.Poses.POS,
            WordNetContract.Domains.DOMAIN
        )
        providerSql.selection = WordNetContract.Synsets_Poses_Domains.SYNSETID + " = ?"
        providerSql.selectionArgs = arrayOf(synsetId.toString())
        return providerSql
    }

    fun prepareSynset(synsetId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.Synsets_Poses_Domains.URI
        providerSql.projection = arrayOf(
            WordNetContract.Synsets.DEFINITION,
            WordNetContract.Poses.POS,
            WordNetContract.Domains.DOMAIN
        )
        providerSql.selection = WordNetContract.Synsets_Poses_Domains.SYNSETID + " = ?"
        providerSql.selectionArgs = arrayOf(synsetId.toString())
        return providerSql
    }

    fun prepareMembers(synsetId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.Senses_Words.URI
        providerSql.projection = arrayOf(WordNetContract.Senses_Words.WORDID, WordNetContract.Senses_Words.WORD)
        providerSql.selection = WordNetContract.Senses_Words.SYNSETID + " = ?"
        providerSql.selectionArgs = arrayOf(synsetId.toString())
        providerSql.sortBy = WordNetContract.Senses_Words.WORD
        return providerSql
    }

    fun prepareMembers2(synsetId: Long, membersGrouped: Boolean): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = if (membersGrouped) WordNetContract.Senses_Words.URI_BY_SYNSET else WordNetContract.Senses_Words.URI
        providerSql.projection = if (membersGrouped) arrayOf(WordNetContract.Senses_Words.SYNSETID) else arrayOf(WordNetContract.Words.WORD)
        providerSql.selection = WordNetContract.Senses_Words.SYNSETID + " = ?"
        providerSql.selectionArgs = arrayOf(synsetId.toString())
        providerSql.sortBy = WordNetContract.Words.WORD
        return providerSql
    }

    fun prepareSamples(synsetId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.Samples.URI
        providerSql.projection = arrayOf(
            WordNetContract.Samples.SAMPLEID,
            WordNetContract.Samples.SAMPLE
        )
        providerSql.selection = WordNetContract.Samples.SYNSETID + " = ?"
        providerSql.selectionArgs = arrayOf(synsetId.toString())
        providerSql.sortBy = WordNetContract.Samples.SAMPLEID
        return providerSql
    }

    fun prepareRelations(synsetId: Long, wordId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.AnyRelations_Senses_Words_X.URI_BY_SYNSET
        providerSql.projection = arrayOf(
            WordNetContract.RELATIONTYPE, WordNetContract.Relations.RELATIONID,
            WordNetContract.Relations.RELATION,
            WordNetContract.AS_SYNSETS2 + '.' + WordNetContract.Synsets.SYNSETID + " AS " + V.SYNSET2ID,
            WordNetContract.AS_SYNSETS2 + '.' + WordNetContract.Synsets.DEFINITION + " AS " + V.DEFINITION2,
            "GROUP_CONCAT(" + WordNetContract.AS_WORDS + '.' + WordNetContract.Words.WORD + ") AS " + WordNetContract.AnyRelations_Senses_Words_X.MEMBERS2,
            WordNetContract.AnyRelations_Senses_Words_X.RECURSES,
            WordNetContract.AS_WORDS2 + '.' + WordNetContract.Words.WORDID + " AS " + V.WORD2ID,
            WordNetContract.AS_WORDS2 + '.' + WordNetContract.Words.WORD + " AS " + V.WORD2
        )
        providerSql.selection = "synset1id = ? /**/|/**/ synset1id = ? AND word1id = ?"
        providerSql.selectionArgs = arrayOf(synsetId.toString(), synsetId.toString(), wordId.toString())
        providerSql.sortBy = WordNetContract.Relations.RELATIONID
        return providerSql
    }

    fun prepareSemRelations(synsetId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.SemRelations_Synsets_Words_X.URI_BY_SYNSET
        providerSql.projection = arrayOf(
            WordNetContract.Relations.RELATIONID,
            WordNetContract.Relations.RELATION,
            WordNetContract.AS_SYNSETS2 + '.' + WordNetContract.Synsets.SYNSETID + " AS " + V.SYNSET2ID,
            WordNetContract.AS_SYNSETS2 + '.' + WordNetContract.Synsets.DEFINITION + " AS " + V.DEFINITION2,
            WordNetContract.Relations.RECURSES
        )
        providerSql.selection = WordNetContract.AS_RELATIONS + '.' + WordNetContract.SemRelations_Synsets_Words_X.SYNSET1ID + " = ?" //
        providerSql.selectionArgs = arrayOf(synsetId.toString())
        providerSql.sortBy = WordNetContract.Relations.RELATIONID
        return providerSql
    }

    fun prepareSemRelations(synsetId: Long, relationId: Int): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.SemRelations_Synsets_Words_X.URI_BY_SYNSET
        providerSql.projection = arrayOf(
            WordNetContract.Relations.RELATIONID,
            WordNetContract.Relations.RELATION,
            WordNetContract.AS_SYNSETS2 + '.' + WordNetContract.Synsets.SYNSETID + " AS " + V.SYNSET2ID,
            WordNetContract.AS_SYNSETS2 + '.' + WordNetContract.Synsets.DEFINITION + " AS " + V.DEFINITION2,
            WordNetContract.Relations.RECURSES
        )
        providerSql.selection = WordNetContract.AS_RELATIONS + '.' + WordNetContract.SemRelations_Synsets_Words_X.SYNSET1ID + " = ? AND " + WordNetContract.Relations.RELATIONID + " = ?"
        providerSql.selectionArgs = arrayOf(synsetId.toString(), relationId.toString())
        return providerSql
    }

    fun prepareLexRelations(synsetId: Long, wordId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.LexRelations_Senses_Words_X.URI_BY_SYNSET
        providerSql.projection = arrayOf(
            WordNetContract.Relations.RELATIONID,
            WordNetContract.Relations.RELATION,
            WordNetContract.AS_SYNSETS2 + '.' + WordNetContract.Synsets.SYNSETID + " AS " + V.SYNSET2ID,
            WordNetContract.AS_SYNSETS2 + '.' + WordNetContract.Synsets.DEFINITION + " AS " + V.DEFINITION2,
            WordNetContract.AS_WORDS + '.' + WordNetContract.Words.WORDID + " AS " + V.WORD2ID,
            WordNetContract.AS_WORDS + '.' + WordNetContract.Words.WORD + " AS " + V.WORD2
        )
        providerSql.selection = WordNetContract.AS_RELATIONS + ".synset1id = ? AND " + WordNetContract.AS_RELATIONS + ".word1id = ?"
        providerSql.selectionArgs = arrayOf(synsetId.toString(), wordId.toString())
        providerSql.sortBy = WordNetContract.Relations.RELATIONID
        return providerSql
    }

    fun prepareLexRelations(synsetId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.LexRelations_Senses_Words_X.URI_BY_SYNSET
        providerSql.projection = arrayOf(
            WordNetContract.Relations.RELATIONID,
            WordNetContract.Relations.RELATION,
            WordNetContract.AS_SYNSETS2 + '.' + WordNetContract.Synsets.SYNSETID + " AS " + V.SYNSET2ID,
            WordNetContract.AS_SYNSETS2 + '.' + WordNetContract.Synsets.DEFINITION + " AS " + V.DEFINITION2,
            WordNetContract.AS_WORDS + '.' + WordNetContract.Words.WORDID + " AS " + V.WORD2ID,
            WordNetContract.AS_WORDS + '.' + WordNetContract.Words.WORD + " AS " + V.WORD2
        )
        providerSql.selection = WordNetContract.AS_RELATIONS + '.' + WordNetContract.LexRelations_Senses_Words_X.SYNSET1ID + " = ?" //
        providerSql.selectionArgs = arrayOf(synsetId.toString())
        return providerSql
    }

    fun prepareVFrames(synsetId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.Senses_VerbFrames.URI
        providerSql.projection = arrayOf(WordNetContract.Senses_VerbFrames.FRAME)
        providerSql.selection = WordNetContract.Senses_VerbFrames.SYNSETID + " = ?"
        providerSql.selectionArgs = arrayOf(synsetId.toString())
        return providerSql
    }

    fun prepareVFrames(synsetId: Long, wordId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.Senses_VerbFrames.URI
        providerSql.projection = arrayOf(WordNetContract.Senses_VerbFrames.FRAME)
        providerSql.selection = WordNetContract.Senses_VerbFrames.SYNSETID + " = ? AND " + WordNetContract.Senses_VerbFrames.WORDID + " = ?"
        providerSql.selectionArgs = arrayOf(synsetId.toString(), wordId.toString())
        return providerSql
    }

    fun prepareVTemplates(synsetId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.Senses_VerbTemplates.URI
        providerSql.projection = arrayOf(WordNetContract.Senses_VerbTemplates.TEMPLATE)
        providerSql.selection = WordNetContract.Senses_VerbTemplates.SYNSETID + " = ?"
        providerSql.selectionArgs = arrayOf(synsetId.toString())
        return providerSql
    }

    fun prepareVTemplates(synsetId: Long, wordId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.Senses_VerbTemplates.URI
        providerSql.projection = arrayOf(WordNetContract.Senses_VerbTemplates.TEMPLATE)
        providerSql.selection = WordNetContract.Senses_VerbTemplates.SYNSETID + " = ? AND " + WordNetContract.Senses_VerbTemplates.WORDID + " = ?"
        providerSql.selectionArgs = arrayOf(synsetId.toString(), wordId.toString())
        return providerSql
    }

    fun prepareAdjPosition(synsetId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.Senses_AdjPositions.URI
        providerSql.projection = arrayOf(WordNetContract.Senses_AdjPositions.POSITION)
        providerSql.selection = WordNetContract.Senses_AdjPositions.SYNSETID + " = ?"
        providerSql.selectionArgs = arrayOf(synsetId.toString())
        return providerSql
    }

    fun prepareAdjPosition(synsetId: Long, wordId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.Senses_AdjPositions.URI
        providerSql.projection = arrayOf(WordNetContract.Senses_AdjPositions.POSITION)
        providerSql.selection = WordNetContract.Senses_AdjPositions.SYNSETID + " = ? AND " + WordNetContract.Senses_AdjPositions.WORDID + " = ?"
        providerSql.selectionArgs = arrayOf(synsetId.toString(), wordId.toString())
        return providerSql
    }

    fun prepareMorphs(wordId: Long): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.Lexes_Morphs.URI
        providerSql.projection = arrayOf(WordNetContract.Lexes_Morphs.POSID, WordNetContract.Lexes_Morphs.MORPH)
        providerSql.selection = WordNetContract.Lexes_Morphs.WORDID + " = ?"
        providerSql.selectionArgs = arrayOf(wordId.toString())
        return providerSql
    }

    // S N   B R O W S E R

    fun prepareSelectSn(word: String): ContentProviderSql {
        val providerSql = ContentProviderSql()
        providerSql.providerUri = WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.URI
        providerSql.projection = arrayOf(
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID + " AS _id",
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.WORDID,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEID,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSENUM,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSEKEY,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.LEXID,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.TAGCOUNT,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SYNSETID,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.DEFINITION,
            WordNetContract.AS_SYNSETS + '.' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.POSID,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.POS,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.DOMAIN,
            WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.CASEDWORD
        )
        providerSql.selection = WordNetContract.AS_WORDS + '.' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.WORD + " = ?" //
        providerSql.selectionArgs = arrayOf(word)
        providerSql.sortBy =
            WordNetContract.AS_LEXES + '.' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.POSID + ',' + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.TAGCOUNT + " DESC ," + WordNetContract.Words_Senses_CasedWords_Synsets_Poses_Domains.SENSENUM
        return providerSql
    }
}
