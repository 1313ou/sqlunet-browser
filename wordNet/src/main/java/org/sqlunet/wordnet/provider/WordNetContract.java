/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.provider;

import android.app.SearchManager;

/**
 * WordNet provider contract
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class WordNetContract
{
	// A L I A S E S

	// refers to actual table
	public static final String AS_WORDS = V.AS_WORDS;
	public static final String AS_SENSES = V.AS_SENSES;
	public static final String AS_SYNSETS = V.AS_SYNSETS;
	public static final String AS_RELATIONS = V.AS_RELATIONS;
	public static final String AS_WORDS2 = V.AS_WORDS2;
	public static final String AS_SYNSETS2 = V.AS_SYNSETS2;
	public static final String AS_POSES = V.AS_POSES;
	public static final String AS_CASEDS = V.AS_CASEDS;
	public static final String AS_DOMAINS = V.AS_DOMAINS;

	// refers to artifact column
	public static final String MEMBERS = V.MEMBERS;
	public static final String MEMBERS2 = V.MEMBERS2;
	public static final String RELATIONTYPE = V.RELATIONTYPE; // type discriminator column for relations whose values are in ["sem","lex"]

	// T A B L E S

	public interface Lexes
	{
		String TABLE = Q.LEXES.TABLE;
		String CONTENT_URI_TABLE = Words.TABLE;
		String LUID = V.LUID;
		String POSID = V.POSID;
		String WORDID = V.WORDID;
		String CASEDWORDID = V.CASEDWORDID;
		/*
		CREATE TABLE ${lexes.table} (
		${lexes.luid}        INT                       NOT NULL,
		${lexes.posid}       ENUM('n','v','a','r','s') NOT NULL,
		${lexes.wordid}      INT                       NOT NULL,
		${lexes.casedwordid} INT                       NULL)
	    */
	}

	public interface Words
	{
		String TABLE = Q.WORDS.TABLE;
		String CONTENT_URI_TABLE = Words.TABLE;
		String CONTENT_URI_TABLE1 = "word";
		String WORDID = V.WORDID;
		String WORD = V.WORD;
		/*
		CREATE TABLE ${words.table} (
		${words.wordid} INT         NOT NULL,
		${words.word}   VARCHAR(80) NOT NULL)
		 */
	}

	public interface CasedWords
	{
		String TABLE = Q.CASEDWORDS.TABLE;
		String CONTENT_URI_TABLE = CasedWords.TABLE;
		String CASEDWORDID = V.CASEDWORDID;
		String WORDID = V.WORDID;
		String CASEDWORD = V.CASEDWORD;
		/*
		CREATE TABLE ${casedwords.table} (
		${casedwords.casedwordid} INT                                             NOT NULL,
		${casedwords.wordid}      INT                                             NOT NULL ,
		${casedwords.casedword}   VARCHAR(80) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL)
		 */
	}

	public interface Morphs
	{
		String TABLE = Q.MORPHS.TABLE;
		String CONTENT_URI_TABLE = Morphs.TABLE;
		String MORPHID = V.MORPHID;
		String MORPH = V.MORPH;
		/*
		CREATE TABLE ${morphs.table} (
		${morphs.morphid} INT         NOT NULL,
		${morphs.morph}   VARCHAR(70) NOT NULL)
		 */
	}

	public interface Pronunciations
	{
		String TABLE = Q.PRONUNCIATIONS.TABLE;
		String CONTENT_URI_TABLE = Pronunciations.TABLE;
		String PRONUNCIATIONID = V.PRONUNCIATIONID;
		String PRONUNCIATION = V.PRONUNCIATION;
		/*
		CREATE TABLE ${pronunciations.table} (
		${pronunciations.pronunciationid} INT         NOT NULL,
		${pronunciations.pronunciation}   VARCHAR(50) NOT NULL)
	 */
	}

	public interface Senses
	{
		String TABLE = Q.SENSES.TABLE;
		String CONTENT_URI_TABLE = Senses.TABLE;
		String CONTENT_URI_TABLE1 = "sense";
		String SENSEID = V.SENSEID;
		String SENSEKEY = V.SENSEKEY;
		String SYNSETID = V.SYNSETID;
		String WORDID = V.WORDID;
		String LUID = V.LUID;
		String CASEDWORDID = V.CASEDWORDID;
		String LEXID = V.LEXID;
		String SENSENUM = V.SENSENUM;
		String TAGCOUNT = V.TAGCOUNT;
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

	public interface Synsets
	{
		String TABLE = Q.SYNSETS.TABLE;
		String CONTENT_URI_TABLE = Synsets.TABLE;
		String CONTENT_URI_TABLE1 = "synset";
		String SYNSETID = V.SYNSETID;
		String POSID = V.POSID;
		String DOMAINID = V.DOMAINID;
		String DEFINITION = V.DEFINITION;
		/*
		CREATE TABLE ${synsets.table} (
		${synsets.synsetid}   INT                       NOT NULL,
		${synsets.posid}      ENUM('n','v','a','r','s') NOT NULL,
		${synsets.domainid}   INT                       NOT NULL,
		${synsets.definition} MEDIUMTEXT                NOT NULL)
		 */
	}

	public interface SemRelations
	{
		String TABLE = Q.SEMRELATIONS.TABLE;
		String CONTENT_URI_TABLE = SemRelations.TABLE;
		String SYNSET1ID = V.SYNSET1ID;
		String SYNSET2ID = V.SYNSET2ID;
		String RELATIONID = V.RELATIONID;
		/*
		CREATE TABLE ${semrelations.table} (
		${semrelations.synset1id}  INT NOT NULL,
		${semrelations.synset2id}  INT NOT NULL,
		${semrelations.relationid} INT NOT NULL)
		 */
	}

	public interface LexRelations
	{
		String TABLE = Q.LEXRELATIONS.TABLE;
		String CONTENT_URI_TABLE = LexRelations.TABLE;
		String WORD1ID = V.WORD1ID;
		String LU1D = V.LU1ID;
		String SYNSET1ID = V.SYNSET1ID;
		String WORD2ID = V.WORD2ID;
		String LU2D = V.LU2ID;
		String SYNSET2ID = V.SYNSET2ID;
		String RELATIONID = V.RELATIONID;
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

	public interface Relations
	{
		String TABLE = Q.RELATIONS.TABLE;
		String CONTENT_URI_TABLE = Relations.TABLE;
		String RELATIONID = V.RELATIONID;
		String RELATION = V.RELATION;
		String RECURSES = V.RECURSES;

		String RECURSESSTR = RECURSES;
		String RECURSESSELECT = "CASE WHEN " + RECURSES + " <> 0 THEN 'recurses' ELSE '' END) AS " + RECURSESSTR;
		/*
		CREATE TABLE ${relations.table} (
		${relations.relationid} INT         NOT NULL,
		${relations.relation}   VARCHAR(50) NOT NULL,
		${relations.recurses}   TINYINT(1)  NOT NULL)
		 */
	}

	public interface Poses
	{
		String TABLE = Q.POSES.TABLE;
		String CONTENT_URI_TABLE = Poses.TABLE;
		String POSID = V.POSID;
		String POS = V.POS;
		/*
		CREATE TABLE ${poses.table} (
		${poses.posid} ENUM('n','v','a','r','s') NOT NULL,
		${poses.pos}   VARCHAR(20)               NOT NULL)
		 */
	}

	public interface AdjPositions
	{
		String TABLE = Q.ADJPOSITIONS.TABLE;
		String CONTENT_URI_TABLE = AdjPositions.TABLE;
		String POSITIONID = V.POSITIONID;
		String POSITION = V.POSITION;
		/*
		CREATE TABLE ${adjpositions.table} (
		${adjpositions.positionid} ENUM('a','p','ip') NOT NULL,
		${adjpositions.position}   VARCHAR(24)        NOT NULL)
		 */
	}

	public interface Domains
	{
		String TABLE = Q.DOMAINS.TABLE;
		String CONTENT_URI_TABLE = Domains.TABLE;
		String DOMAINID = V.DOMAINID;
		String DOMAIN = V.DOMAIN;
		String DOMAINNAME = V.DOMAINNAME;
		String POSID = V.POSID;
		/*
		CREATE TABLE ${domains.table} (
		${domains.domainid}   INT                       NOT NULL,
		${domains.domain}     VARCHAR(32)               NOT NULL,
		${domains.domainname} VARCHAR(32)               NOT NULL,
		${domains.posid}      ENUM('n','v','a','r','s') NOT NULL)
		 */
	}

	public interface Samples
	{
		String TABLE = Q.SAMPLES.TABLE;
		String CONTENT_URI_TABLE = Samples.TABLE;
		String SAMPLEID = V.SAMPLEID;
		String SAMPLE = V.SAMPLE;
		String SYNSETID = V.SYNSETID;
		/*
		CREATE TABLE ${samples.table} (
		${samples.sampleid} INT        NOT NULL,
		${samples.sample}   MEDIUMTEXT NOT NULL,
		${samples.synsetid} INT        NOT NULL)
		 */
	}

	public interface VerbFrames
	{
		String TABLE = Q.VFRAMES.TABLE;
		String CONTENT_URI_TABLE = VerbFrames.TABLE;
		String FRAMEID = V.FRAMEID;
		String FRAME = V.FRAME;
		/*
		CREATE TABLE ${vframes.table} (
		${vframes.frameid} INT         NOT NULL,
		${vframes.frame}   VARCHAR(50) NOT NULL)
)		 */
	}

	public interface VerbTemplates
	{
		String TABLE = Q.VTEMPLATES.TABLE;
		String CONTENT_URI_TABLE = VerbTemplates.TABLE;
		String TEMPLATEID = V.TEMPLATEID;
		String TEMPLATE = V.TEMPLATE;
		/*
		CREATE TABLE ${vtemplates.table} (
		${vtemplates.templateid} INT        NOT NULL,
		${vtemplates.template}   MEDIUMTEXT NOT NULL)
		*/
	}

	public interface Senses_VerbFrames extends Senses, VerbFrames
	{
		String TABLE = "senses_vframes";
		String CONTENT_URI_TABLE = Senses_VerbFrames.TABLE;
		/*
		CREATE TABLE ${senses_vframes.table} (
		${senses_vframes.synsetid} INT NOT NULL,
		${senses_vframes.luid}     INT NOT NULL,
		${senses_vframes.wordid}   INT NOT NULL,
		${senses_vframes.frameid}  INT NOT NULL)
		 */
	}

	public interface Senses_VerbTemplates extends Senses, VerbTemplates
	{
		String TABLE = "senses_vtemplates";
		String CONTENT_URI_TABLE = Senses_VerbTemplates.TABLE;
		/*
		CREATE TABLE ${senses_vtemplates.table} (
		${senses_vtemplates.synsetid}   INT NOT NULL,
		${senses_vtemplates.luid}       INT NOT NULL,
		${senses_vtemplates.wordid}     INT NOT NULL,
		${senses_vtemplates.templateid} INT NOT NULL)
		 */
	}

	public interface Senses_AdjPositions extends Senses, AdjPositions
	{
		String TABLE = "senses_adjpositions";
		String CONTENT_URI_TABLE = Senses_AdjPositions.TABLE;
		/*
		CREATE TABLE ${senses_adjpositions.table} (
		${senses_adjpositions.synsetid}   INT                NOT NULL,
		${senses_adjpositions.luid}       INT                NOT NULL,
		${senses_adjpositions.wordid}     INT                NOT NULL,
		${senses_adjpositions.positionid} ENUM('a','p','ip') NOT NULL)
		 */
	}

	public interface Lexes_Morphs extends Lexes, Morphs
	{
		String TABLE = "lexes_morphs";
		String CONTENT_URI_TABLE = Lexes_Morphs.TABLE;
		/*
		CREATE TABLE ${lexes_morphs.table} (
		${lexes_morphs.luid}    INT                       NOT NULL,
		${lexes_morphs.wordid}  INT                       NOT NULL,
		${lexes_morphs.posid}   ENUM('n','v','a','r','s') NOT NULL,
		${lexes_morphs.morphid} INT                       NOT NULL)
		 */
	}

	public interface Lexes_Pronunciations extends Lexes, Pronunciations
	{
		String TABLE = "lexes_pronunciations";
		String CONTENT_URI_TABLE = Lexes_Pronunciations.TABLE;
		String VARIETY = V.VARIETY;
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

	public interface AnyRelations extends LexRelations
	{
		String TABLE = "anyrelations";
		String CONTENT_URI_TABLE = AnyRelations.TABLE;
	}

	// J O I N S

	public interface Words_Senses_Synsets extends Words, Senses, Synsets
	{
		String TABLE = "words_senses_synsets";
		String CONTENT_URI_TABLE = Words_Senses_Synsets.TABLE;
	}

	public interface Words_Senses_CasedWords_Synsets extends Words, CasedWords, Senses, Synsets
	{
		String TABLE = "words_senses_casedwords_synsets";
		String CONTENT_URI_TABLE = Words_Senses_CasedWords_Synsets.TABLE;
	}

	public interface Words_Senses_CasedWords_Synsets_Poses_Domains extends Words, CasedWords, Senses, Synsets, Poses, Domains
	{
		String TABLE = "words_senses_casedwords_synsets_poses_domains";
		String CONTENT_URI_TABLE = Words_Senses_CasedWords_Synsets_Poses_Domains.TABLE;
		String SYNSETID = Synsets.SYNSETID;
		String WORDID = Words.WORDID;
		String CASEDWORD = CasedWords.CASEDWORD;
		String POSID = Poses.POSID;
	}

	public interface Senses_Words extends Words, Senses
	{
		String TABLE = "senses_words";
		String TABLE_BY_SYNSET = "senses_words_by_synset";
		String CONTENT_URI_TABLE = Senses_Words.TABLE;
		String CONTENT_URI_TABLE_BY_SYNSET = Senses_Words.TABLE_BY_SYNSET;
		String WORDID = V.WORDID;
		String MEMBERS = V.MEMBERS;
	}

	public interface Senses_Synsets_Poses_Domains extends Senses, Synsets, Poses, Domains
	{
		String TABLE = "senses_synsets_poses_domains";
		String CONTENT_URI_TABLE = Senses_Synsets_Poses_Domains.TABLE;
	}

	public interface Synsets_Poses_Domains extends Synsets, Poses, Domains
	{
		String TABLE = "synsets_poses_domains";
		String CONTENT_URI_TABLE = Synsets_Poses_Domains.TABLE;
	}

	public interface AnyRelations_Senses_Words_X extends AnyRelations, Senses, Words
	{
		String TABLE_BY_SYNSET = "anyrelations_senses_words_x_by_synset";
		String CONTENT_URI_TABLE_BY_SYNSET = AnyRelations_Senses_Words_X.TABLE_BY_SYNSET;
		String MEMBERS2 = WordNetContract.MEMBERS2;
		String RECURSES = V.RECURSES;
	}

	public interface SemRelations_Synsets extends SemRelations, Synsets
	{
		String TABLE = "semrelations_synsets";
		String CONTENT_URI_TABLE = SemRelations_Synsets.TABLE;
	}

	public interface SemRelations_Synsets_X extends SemRelations_Synsets, Relations
	{
		String TABLE = "semrelations_synsets_relations";
		String CONTENT_URI_TABLE = SemRelations_Synsets_X.TABLE;
	}

	public interface SemRelations_Synsets_Words_X extends SemRelations_Synsets, Words, Relations
	{
		String TABLE_BY_SYNSET = "semrelations_synsets_words_x_by_synset";
		String CONTENT_URI_TABLE_BY_SYNSET = SemRelations_Synsets_Words_X.TABLE_BY_SYNSET;
		String MEMBERS2 = WordNetContract.MEMBERS2;
	}

	public interface LexRelations_Senses extends LexRelations, Senses
	{
		String TABLE = "lexrelations_synsets_words";
		String CONTENT_URI_TABLE = LexRelations_Senses.TABLE;
	}

	public interface LexRelations_Senses_X extends LexRelations_Senses
	{
		String TABLE = "lexrelations_synsets_words_relations";
		String CONTENT_URI_TABLE = LexRelations_Senses_X.TABLE;
	}

	public interface LexRelations_Senses_Words_X extends LexRelations_Senses, Words
	{
		String TABLE_BY_SYNSET = "lexrelations_senses_words_x_by_synset";
		String CONTENT_URI_TABLE_BY_SYNSET = LexRelations_Senses_Words_X.TABLE_BY_SYNSET;
		String MEMBERS2 = WordNetContract.MEMBERS2;
	}

	public interface Words_Lexes_Morphs extends Words, Lexes, Morphs
	{
		String TABLE = "words_lexes_morphs";
		String TABLE_BY_WORD = "words_lexes_morphs_by_word";
		String CONTENT_URI_TABLE = Words_Lexes_Morphs.TABLE;
		String CONTENT_URI_TABLE_BY_WORD = Words_Lexes_Morphs.TABLE_BY_WORD;
		String WORDID = Words.WORDID;
	}

	// V I E W S

	public interface Dict
	{
		String TABLE = "dict";
		String CONTENT_URI_TABLE = Dict.TABLE;
	}

	// T E X T S E A R C H

	public interface Lookup_Words extends Words
	{
		String TABLE = "fts_words";
		String CONTENT_URI_TABLE = Lookup_Words.TABLE;
	}

	public interface Lookup_Definitions extends Synsets
	{
		String TABLE = "fts_definitions";
		String CONTENT_URI_TABLE = Lookup_Definitions.TABLE;
	}

	public interface Lookup_Samples extends Samples
	{
		String TABLE = "fts_samples";
		String CONTENT_URI_TABLE = Lookup_Samples.TABLE;
	}

	// S U G G E S T

	public interface Suggest_Words extends Words
	{
		String SEARCH_WORD_PATH = "suggest_word";
		String TABLE = Suggest_Words.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
	}

	public interface Suggest_FTS_Words extends Words
	{
		String SEARCH_WORD_PATH = "suggest_fts_word";
		String TABLE = Suggest_FTS_Words.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
	}

	public interface Suggest_FTS_Definitions extends Synsets
	{
		String SEARCH_DEFINITION_PATH = "suggest_fts_definition";
		String TABLE = Suggest_FTS_Definitions.SEARCH_DEFINITION_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
	}

	public interface Suggest_FTS_Samples extends Samples
	{
		String SEARCH_SAMPLE_PATH = "suggest_fts_sample";
		String TABLE = Suggest_FTS_Samples.SEARCH_SAMPLE_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
	}
}
