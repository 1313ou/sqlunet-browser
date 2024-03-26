/*
 * Copyright (c) 2023. Bernard Bou
 */
package org.sqlunet.wordnet.provider

import android.app.SearchManager
import org.sqlunet.wordnet.loaders.Queries

/**
 * WordNet provider contract
 *
 * @author [Bernard Bou](mailto:1313ou@gmail.com)
 */
object WordNetContract {

    // A L I A S E S

    // refers to actual table
    const val AS_LEXES = V.AS_LEXES
    const val AS_WORDS = V.AS_WORDS
    const val AS_SENSES = V.AS_SENSES
    const val AS_SYNSETS = V.AS_SYNSETS
    const val AS_RELATIONS = V.AS_RELATIONS
    const val AS_WORDS2 = V.AS_WORDS2
    const val AS_SYNSETS2 = V.AS_SYNSETS2
    const val AS_POSES = V.AS_POSES
    const val AS_CASEDS = V.AS_CASEDS
    const val AS_DOMAINS = V.AS_DOMAINS

    // refers to artifact column
    const val MEMBERS = V.MEMBERS
    const val MEMBERS2 = V.MEMBERS2
    const val RELATIONTYPE = V.RELATIONTYPE // type discriminator column for relations whose values are in ["sem","lex"]

    // T A B L E S

    object Lexes {
        const val TABLE = Q.LEXES.TABLE
        const val URI = TABLE
        const val LUID = V.LUID
        const val POSID = V.POSID
        const val WORDID = V.WORDID
        const val CASEDWORDID = V.CASEDWORDID
        /*
		CREATE TABLE ${lexes.table} (
		${lexes.luid}        INT                       NOT NULL,
		${lexes.posid}       ENUM('n','v','a','r','s') NOT NULL,
		${lexes.wordid}      INT                       NOT NULL,
		${lexes.casedwordid} INT                       NULL)
	    */
    }

    object Words {
        const val TABLE = Q.WORDS.TABLE
        const val URI = TABLE
        const val URI1 = "word"
        const val WORDID = V.WORDID
        const val WORD = V.WORD
        /*
        CREATE TABLE ${words.table} (
        ${words.wordid} INT         NOT NULL,
        ${words.word}   VARCHAR(80) NOT NULL)
        */
    }

    object CasedWords {
        const val TABLE = Q.CASEDWORDS.TABLE
        const val URI = TABLE
        const val CASEDWORDID = V.CASEDWORDID
        const val WORDID = V.WORDID
        const val CASEDWORD = V.CASEDWORD
        /*
		CREATE TABLE ${casedwords.table} (
		${casedwords.casedwordid} INT                                             NOT NULL,
		${casedwords.wordid}      INT                                             NOT NULL ,
		${casedwords.casedword}   VARCHAR(80) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL)
		*/
    }

    object Morphs {
        const val TABLE = Q.MORPHS.TABLE
        const val URI = TABLE
        const val MORPHID = V.MORPHID
        const val MORPH = V.MORPH
        /*
        CREATE TABLE ${morphs.table} (
        ${morphs.morphid} INT         NOT NULL,
        ${morphs.morph}   VARCHAR(70) NOT NULL)
        */
    }

    object Pronunciations {
        const val TABLE = Q.PRONUNCIATIONS.TABLE
        const val URI = TABLE
        const val PRONUNCIATIONID = V.PRONUNCIATIONID
        const val PRONUNCIATION = V.PRONUNCIATION
        /*
		CREATE TABLE ${pronunciations.table} (
		${pronunciations.pronunciationid} INT         NOT NULL,
		${pronunciations.pronunciation}   VARCHAR(50) NOT NULL)
	    */
    }

    object Senses {
        const val TABLE = Q.SENSES.TABLE
        const val URI = TABLE
        const val URI1 = "sense"
        const val SENSEID = V.SENSEID
        const val SENSEKEY = V.SENSEKEY
        const val SYNSETID = V.SYNSETID
        const val WORDID = V.WORDID
        const val LUID = V.LUID
        const val CASEDWORDID = V.CASEDWORDID
        const val LEXID = V.LEXID
        const val SENSENUM = V.SENSENUM
        const val TAGCOUNT = V.TAGCOUNT
        /*
        CREATE TABLE ${senses.table} (
        ${senses.senseid}     INT                                               NOT NULL,
        ${senses.sensekey}    VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_bin  DEFAULT NULL,
        ${senses.synsetid}    INT                                               NOT NULL,
        ${senses.luid}        INT                                               NOT NULL,
        ${senses.wordid}      INT                                               NOT NULL,
        ${senses.casedwordid} INT                                               DEFAULT NULL,
        ${senses.lexid}       INT                                               NOT NULL,
        ${senses.sensenum}    INT                                               DEFAULT NULL,
        ${senses.tagcount}    INT                                               DEFAULT NULL)
        */
    }

    object Synsets {
        const val TABLE = Q.SYNSETS.TABLE
        const val URI = TABLE
        const val URI1 = "synset"
        const val SYNSETID = V.SYNSETID
        const val POSID = V.POSID
        const val DOMAINID = V.DOMAINID
        const val DEFINITION = V.DEFINITION
        /*
        CREATE TABLE ${synsets.table} (
        ${synsets.synsetid}   INT                       NOT NULL,
        ${synsets.posid}      ENUM('n','v','a','r','s') NOT NULL,
        ${synsets.domainid}   INT                       NOT NULL,
        ${synsets.definition} MEDIUMTEXT                NOT NULL)
        */
    }

    object SemRelations {
        const val TABLE = Q.SEMRELATIONS.TABLE
        const val URI = TABLE
        const val SYNSET1ID = V.SYNSET1ID
        const val SYNSET2ID = V.SYNSET2ID
        const val RELATIONID = V.RELATIONID
        /*
        CREATE TABLE ${semrelations.table} (
        ${semrelations.synset1id}  INT NOT NULL,
        ${semrelations.synset2id}  INT NOT NULL,
        ${semrelations.relationid} INT NOT NULL)
        */
    }

    object LexRelations {
        const val TABLE = Q.LEXRELATIONS.TABLE
        const val URI = TABLE
        const val WORD1ID = V.WORD1ID
        const val LU1D = V.LU1ID
        const val SYNSET1ID = V.SYNSET1ID
        const val WORD2ID = V.WORD2ID
        const val LU2D = V.LU2ID
        const val SYNSET2ID = V.SYNSET2ID
        const val RELATIONID = V.RELATIONID
        /*
        CREATE TABLE ${lexrelations.table} (
        ${lexrelations.synset1id}  INT NOT NULL,
        ${lexrelations.lu1id}      INT NOT NULL,
        ${lexrelations.word1id}    INT NOT NULL,
        ${lexrelations.synset2id}  INT NOT NULL,
        ${lexrelations.lu2id}      INT NOT NULL,
        ${lexrelations.word2id}    INT NOT NULL,
        ${lexrelations.relationid} INT NOT NULL)
        */
    }

    object Relations {
        const val TABLE = Q.RELATIONS.TABLE
        const val URI = TABLE
        const val RELATIONID = V.RELATIONID
        const val RELATION = V.RELATION
        const val RECURSES = V.RECURSES
        private const val RECURSES_STR = RECURSES
        const val RECURSES_SELECT = "(CASE WHEN $RECURSES <> 0 THEN 'recurses' ELSE '' END) AS $RECURSES_STR"
        /*
        CREATE TABLE ${relations.table} (
        ${relations.relationid} INT         NOT NULL,
        ${relations.relation}   VARCHAR(50) NOT NULL,
        ${relations.recurses}   TINYINT(1)  NOT NULL)
        */
    }

    object Poses {
        const val TABLE = Q.POSES.TABLE
        const val URI = TABLE
        const val POSID = V.POSID
        const val POS = V.POS
        /*
        CREATE TABLE ${poses.table} (
        ${poses.posid} ENUM('n','v','a','r','s') NOT NULL,
        ${poses.pos}   VARCHAR(20)               NOT NULL)
        */
    }

    object AdjPositions {
        const val TABLE = Q.ADJPOSITIONS.TABLE
        const val URI = TABLE
        const val POSITIONID = V.POSITIONID
        const val POSITION = V.POSITION /*
		CREATE TABLE ${adjpositions.table} (
		${adjpositions.positionid} ENUM('a','p','ip') NOT NULL,
		${adjpositions.position}   VARCHAR(24)        NOT NULL)
		*/
    }

    object Domains {
        const val TABLE = Q.DOMAINS.TABLE
        const val URI = TABLE
        const val DOMAINID = V.DOMAINID
        const val DOMAIN = V.DOMAIN
        const val DOMAINNAME = V.DOMAINNAME
        const val POSID = V.POSID
        /*
        CREATE TABLE ${domains.table} (
        ${domains.domainid}   INT                       NOT NULL,
        ${domains.domain}     VARCHAR(32)               NOT NULL,
        ${domains.domainname} VARCHAR(32)               NOT NULL,
        ${domains.posid}      ENUM('n','v','a','r','s') NOT NULL)
        */
    }

    object Samples {
        const val TABLE = Q.SAMPLES.TABLE
        const val URI = TABLE
        const val SAMPLEID = V.SAMPLEID
        const val SAMPLE = V.SAMPLE
        const val SYNSETID = V.SYNSETID
        /*
        CREATE TABLE ${samples.table} (
        ${samples.sampleid} INT        NOT NULL,
        ${samples.sample}   MEDIUMTEXT NOT NULL,
        ${samples.synsetid} INT        NOT NULL)
        */
    }

    object VerbFrames {
        const val TABLE = Q.VFRAMES.TABLE
        const val URI = TABLE
        const val FRAMEID = V.FRAMEID
        const val FRAME = V.FRAME /*
		CREATE TABLE ${vframes.table} (
		${vframes.frameid} INT         NOT NULL,
		${vframes.frame}   VARCHAR(50) NOT NULL)
        */
    }

    object VerbTemplates {
        const val TABLE = Q.VTEMPLATES.TABLE
        const val URI = TABLE
        const val TEMPLATEID = V.TEMPLATEID
        const val TEMPLATE = V.TEMPLATE
        /*
        CREATE TABLE ${vtemplates.table} (
        ${vtemplates.templateid} INT        NOT NULL,
        ${vtemplates.template}   MEDIUMTEXT NOT NULL)
        */
    }

    object Senses_VerbFrames { // : Senses, VerbFrames
        const val URI = "senses_vframes"
        const val WORDID = Senses.WORDID
        const val SYNSETID = Senses.SYNSETID
        const val FRAME = VerbFrames.FRAME
        /*
         CREATE TABLE ${senses_vframes.table} (
         ${senses_vframes.synsetid} INT NOT NULL,
         ${senses_vframes.luid}     INT NOT NULL,
         ${senses_vframes.wordid}   INT NOT NULL,
         ${senses_vframes.frameid}  INT NOT NULL)
         */
    }

    object Senses_VerbTemplates { // : Senses, VerbTemplates
        const val URI = "senses_vtemplates"
        const val WORDID = Senses.WORDID
        const val SYNSETID = Senses.SYNSETID
        const val TEMPLATE = VerbTemplates.TEMPLATE
        /*
        CREATE TABLE ${senses_vtemplates.table} (
        ${senses_vtemplates.synsetid}   INT NOT NULL,
        ${senses_vtemplates.luid}       INT NOT NULL,
        ${senses_vtemplates.wordid}     INT NOT NULL,
        ${senses_vtemplates.templateid} INT NOT NULL)
        */
    }

    object Senses_AdjPositions { // : Senses, AdjPositions
        const val URI = "senses_adjpositions"
        const val WORDID = Senses.WORDID
        const val SYNSETID = Senses.SYNSETID
        const val POSITION = AdjPositions.POSITION
        /*
         CREATE TABLE ${senses_adjpositions.table} (
         ${senses_adjpositions.synsetid}   INT                NOT NULL,
         ${senses_adjpositions.luid}       INT                NOT NULL,
         ${senses_adjpositions.wordid}     INT                NOT NULL,
         ${senses_adjpositions.positionid} ENUM('a','p','ip') NOT NULL)
         */
    }

    object Lexes_Morphs { // : Lexes, Morphs
        const val POSID = Poses.POSID
        const val MORPH = Morphs.MORPH
        const val URI = "lexes_morphs"
        const val WORDID = Lexes.WORDID
        /*
        CREATE TABLE ${lexes_morphs.table} (
        ${lexes_morphs.luid}    INT                       NOT NULL,
        ${lexes_morphs.wordid}  INT                       NOT NULL,
        ${lexes_morphs.posid}   ENUM('n','v','a','r','s') NOT NULL,
        ${lexes_morphs.morphid} INT                       NOT NULL)
        */
    }

    object Lexes_Pronunciations { // : Lexes, Pronunciations
        const val URI = "lexes_pronunciations"
        const val VARIETY = V.VARIETY
        /*
        CREATE TABLE ${lexes_pronunciations.table} (
        ${lexes_pronunciations.luid}            INT                       NOT NULL,
        ${lexes_pronunciations.wordid}          INT                       NOT NULL,
        ${lexes_pronunciations.posid}           ENUM('n','v','a','r','s') NOT NULL,
        ${lexes_pronunciations.pronunciationid} INT                       NOT NULL,
        ${lexes_pronunciations.variety}         VARCHAR(2)                DEFAULT NULL)
        */
    }

    // A R T I F A C T   T A B L E S

    object AnyRelations { //  : LexRelations
        const val URI = "anyrelations"
        const val RELATIONID = LexRelations.RELATIONID
        const val WORD1ID = LexRelations.WORD1ID
        const val SYNSET1ID = LexRelations.SYNSET1ID
        const val WORD2ID = LexRelations.WORD2ID
        const val SYNSET2ID = LexRelations.SYNSET2ID
    }

    // J O I N S

    object Words_Senses_Synsets { // : Words, Senses, Synsets
        const val URI = "words_senses_synsets"
    }

    object Words_Senses_CasedWords_Synsets { //  : Words, CasedWords, Senses, Synsets
        const val URI = "words_senses_casedwords_synsets"
    }

    object Words_Senses_CasedWords_Synsets_Poses_Domains { // : Words, CasedWords, Senses, Synsets, Poses, Domains
        const val URI = "words_senses_casedwords_synsets_poses_domains"
        const val SYNSETID = Synsets.SYNSETID
        const val DEFINITION = Synsets.DEFINITION
        const val SENSEID = Senses.SENSEID
        const val SENSENUM = Senses.SENSENUM
        const val SENSEKEY = Senses.SENSEKEY
        const val LEXID = Senses.LEXID
        const val TAGCOUNT = Senses.TAGCOUNT
        const val WORD = Words.WORD
        const val WORDID = Words.WORDID
        const val CASEDWORD = CasedWords.CASEDWORD
        const val POSID = Poses.POSID
        const val POS = Poses.POS
        const val DOMAIN = Domains.DOMAIN
    }

    object Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains { // : Words_Senses_CasedWords_Synsets_Poses_Domains
        const val URI = "words_senses_casedwords_pronunciations_synsets_poses_domains"
        const val PRONUNCIATION = V.PRONUNCIATION
        const val VARIETY = V.VARIETY
        const val PRONUNCIATIONS = Queries.PRONUNCIATIONS
        const val SYNSETID = Synsets.SYNSETID
        const val DEFINITION = Synsets.DEFINITION
        const val SENSEID = Senses.SENSEID
        const val SENSENUM = Senses.SENSENUM
        const val SENSEKEY = Senses.SENSEKEY
        const val LEXID = Senses.LEXID
        const val TAGCOUNT = Senses.TAGCOUNT
        const val WORD = Words.WORD
        const val WORDID = Words.WORDID
        const val CASEDWORD = CasedWords.CASEDWORD
        const val POSID = Poses.POSID
        const val POS = Poses.POS
        const val DOMAIN = Domains.DOMAIN
    }

    object Senses_Words { // : Words, Senses
        const val URI = "senses_words"
        const val URI_BY_SYNSET = "senses_words_by_synset"
        const val MEMBERS = V.MEMBERS
        const val WORD = Words.WORD
        const val WORDID = Words.WORDID
        const val SYNSETID = Senses.SYNSETID
    }

    object Senses_Synsets_Poses_Domains { // : Senses, Synsets, Poses, Domains
        const val URI = "senses_synsets_poses_domains"
    }

    object Synsets_Poses_Domains { // : Synsets, Poses, Domains
        const val URI = "synsets_poses_domains"
        const val SYNSETID = Synsets.SYNSETID
    }

    object AnyRelations_Senses_Words_X { // : AnyRelations, Senses, Words
        const val URI_BY_SYNSET = "anyrelations_senses_words_x_by_synset"
        const val MEMBERS2 = WordNetContract.MEMBERS2
        const val RECURSES = V.RECURSES
    }

    object SemRelations_Synsets { // : SemRelations, Synsets
        const val URI = "semrelations_synsets"
    }

    object SemRelations_Synsets_X { // : SemRelations_Synsets, Relations
        const val URI = "semrelations_synsets_relations"
    }

    object SemRelations_Synsets_Words_X { // : SemRelations_Synsets, Words, Relations
        const val URI_BY_SYNSET = "semrelations_synsets_words_x_by_synset"
        const val SYNSET1ID = SemRelations.SYNSET1ID
        const val MEMBERS2 = WordNetContract.MEMBERS2
        const val SYNSETID = Synsets.SYNSETID
        const val RECURSES = Relations.RECURSES
    }

    object LexRelations_Senses { //  : LexRelations, Senses
        const val URI = "lexrelations_synsets_words"
    }

    object LexRelations_Senses_X { // : LexRelations_Senses
        const val URI = "lexrelations_synsets_words_relations"
    }

    object LexRelations_Senses_Words_X { // : LexRelations_Senses, Words
        const val URI_BY_SYNSET = "lexrelations_senses_words_x_by_synset"
        const val MEMBERS2 = WordNetContract.MEMBERS2
        const val SYNSETID = Senses.SYNSETID
        const val SYNSET1ID = LexRelations.SYNSET1ID
    }

    object Words_Lexes_Morphs { // : Words, Lexes, Morphs
        const val URI = "words_lexes_morphs"
        const val URI_BY_WORD = "words_lexes_morphs_by_word"
        const val MORPHS = Queries.MORPHS
        const val WORDID = Words.WORDID
        const val WORD = Words.WORD
        const val MORPH = Morphs.MORPH
        const val POSID = Lexes.POSID
    }

    // V I E W S

    object Dict {
        const val TABLE = Q.DICT.TABLE
        const val URI = TABLE
    }

    // T E X T S E A R C H

    object Lookup_Words { // : Words
        const val TABLE = Q.LOOKUP_FTS_WORDS.TABLE
        const val URI = TABLE
        const val WORDID = Words.WORDID
        const val WORD = Words.WORD
    }

    object Lookup_Definitions { // : Synsets
        const val TABLE = Q.LOOKUP_FTS_DEFINITIONS.TABLE
        const val URI = TABLE
        const val SYNSETID = Synsets.SYNSETID
        const val DEFINITION = Synsets.DEFINITION
    }

    object Lookup_Samples { // : Samples
        const val TABLE = Q.LOOKUP_FTS_SAMPLES.TABLE
        const val URI = TABLE
        const val SYNSETID = Samples.SYNSETID
        const val SAMPLE = Samples.SAMPLE
    }

    // S U G G E S T

    object Suggest_Words { // : Words
        const val SEARCH_WORD_PATH = "suggest_word"
        const val URI = SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY
    }

    object Suggest_FTS_Words { // : Words
        const val SEARCH_WORD_PATH = "suggest_fts_word"
        const val URI = SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY
    }

    object Suggest_FTS_Definitions { // : Synsets
        const val SEARCH_DEFINITION_PATH = "suggest_fts_definition"
        const val URI = SEARCH_DEFINITION_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY
    }

    object Suggest_FTS_Samples { // : Samples
        const val SEARCH_SAMPLE_PATH = "suggest_fts_sample"
        const val URI = SEARCH_SAMPLE_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY
    }
}
