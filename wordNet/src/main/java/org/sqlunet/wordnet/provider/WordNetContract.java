/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.provider;

import android.app.SearchManager;

import org.sqlunet.wordnet.loaders.Queries;

/**
 * WordNet provider contract
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class WordNetContract
{
	// A L I A S E S

	// refers to actual table
	public static final String AS_LEXES = V.AS_LEXES;
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
		String URI = TABLE;
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
		String URI = TABLE;
		String URI1 = "word";
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
		String URI = TABLE;
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
		String URI = TABLE;
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
		String URI = TABLE;
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
		String URI = TABLE;
		String URI1 = "sense";
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
		String URI = TABLE;
		String URI1 = "synset";
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
		String URI = TABLE;
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
		String URI = TABLE;
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
		String URI = TABLE;
		String RELATIONID = V.RELATIONID;
		String RELATION = V.RELATION;
		String RECURSES = V.RECURSES;

		String RECURSESSTR = RECURSES;
		String RECURSESSELECT = "(CASE WHEN " + RECURSES + " <> 0 THEN 'recurses' ELSE '' END) AS " + RECURSESSTR;
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
		String URI = TABLE;
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
		String URI = TABLE;
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
		String URI = TABLE;
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
		String URI = TABLE;
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
		String URI = TABLE;
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
		String URI = TABLE;
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
		String URI = "senses_vframes";
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
		String URI = "senses_vtemplates";
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
		String URI = "senses_adjpositions";
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
		String URI = "lexes_morphs";
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
		String URI = "lexes_pronunciations";
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
		String URI = "anyrelations";
	}

	// J O I N S

	public interface Words_Senses_Synsets extends Words, Senses, Synsets
	{
		String URI = "words_senses_synsets";
	}

	public interface Words_Senses_CasedWords_Synsets extends Words, CasedWords, Senses, Synsets
	{
		String URI = "words_senses_casedwords_synsets";
	}

	public interface Words_Senses_CasedWords_Synsets_Poses_Domains extends Words, CasedWords, Senses, Synsets, Poses, Domains
	{
		String URI = "words_senses_casedwords_synsets_poses_domains";
		String SYNSETID = Synsets.SYNSETID;
		String WORDID = Words.WORDID;
		String CASEDWORD = CasedWords.CASEDWORD;
		String POSID = Poses.POSID;
	}

	public interface Words_Senses_CasedWords_Pronunciations_Synsets_Poses_Domains extends Words_Senses_CasedWords_Synsets_Poses_Domains
	{
		String URI = "words_senses_casedwords_pronunciations_synsets_poses_domains";
		String PRONUNCIATION = V.PRONUNCIATION;
		String VARIETY = V.VARIETY;
		String PRONUNCIATIONS = Queries.PRONUNCIATIONS;
	}

	public interface Senses_Words extends Words, Senses
	{
		String URI = "senses_words";
		String URI_BY_SYNSET = "senses_words_by_synset";
		String WORDID = V.WORDID;
		String MEMBERS = V.MEMBERS;
	}

	public interface Senses_Synsets_Poses_Domains extends Senses, Synsets, Poses, Domains
	{
		String URI = "senses_synsets_poses_domains";
	}

	public interface Synsets_Poses_Domains extends Synsets, Poses, Domains
	{
		String URI = "synsets_poses_domains";
	}

	public interface AnyRelations_Senses_Words_X extends AnyRelations, Senses, Words
	{
		String URI_BY_SYNSET = "anyrelations_senses_words_x_by_synset";
		String MEMBERS2 = WordNetContract.MEMBERS2;
		String RECURSES = V.RECURSES;
	}

	public interface SemRelations_Synsets extends SemRelations, Synsets
	{
		String URI = "semrelations_synsets";
	}

	public interface SemRelations_Synsets_X extends SemRelations_Synsets, Relations
	{
		String URI = "semrelations_synsets_relations";
	}

	public interface SemRelations_Synsets_Words_X extends SemRelations_Synsets, Words, Relations
	{
		String URI_BY_SYNSET = "semrelations_synsets_words_x_by_synset";
		String MEMBERS2 = WordNetContract.MEMBERS2;
	}

	public interface LexRelations_Senses extends LexRelations, Senses
	{
		String URI = "lexrelations_synsets_words";
	}

	public interface LexRelations_Senses_X extends LexRelations_Senses
	{
		String URI = "lexrelations_synsets_words_relations";
	}

	public interface LexRelations_Senses_Words_X extends LexRelations_Senses, Words
	{
		String URI_BY_SYNSET = "lexrelations_senses_words_x_by_synset";
		String MEMBERS2 = WordNetContract.MEMBERS2;
	}

	public interface Words_Lexes_Morphs extends Words, Lexes, Morphs
	{
		String URI = "words_lexes_morphs";
		String URI_BY_WORD = "words_lexes_morphs_by_word";
		String WORDID = Words.WORDID;
		String MORPHS = Queries.MORPHS;
	}

	// V I E W S

	public interface Dict
	{
		String TABLE = Q.DICT.TABLE;
		String URI = TABLE;
	}

	// T E X T S E A R C H

	public interface Lookup_Words extends Words
	{
		String TABLE = Q.LOOKUP_FTS_WORDS.TABLE;
		String URI = TABLE;
	}

	public interface Lookup_Definitions extends Synsets
	{
		String TABLE = Q.LOOKUP_FTS_DEFINITIONS.TABLE;
		String URI = TABLE;
	}

	public interface Lookup_Samples extends Samples
	{
		String TABLE = Q.LOOKUP_FTS_SAMPLES.TABLE;
		String URI = TABLE;
	}

	// S U G G E S T

	public interface Suggest_Words extends Words
	{
		String SEARCH_WORD_PATH = "suggest_word";
		String URI = Suggest_Words.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
	}

	public interface Suggest_FTS_Words extends Words
	{
		String SEARCH_WORD_PATH = "suggest_fts_word";
		String URI = Suggest_FTS_Words.SEARCH_WORD_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
	}

	public interface Suggest_FTS_Definitions extends Synsets
	{
		String SEARCH_DEFINITION_PATH = "suggest_fts_definition";
		String URI = Suggest_FTS_Definitions.SEARCH_DEFINITION_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
	}

	public interface Suggest_FTS_Samples extends Samples
	{
		String SEARCH_SAMPLE_PATH = "suggest_fts_sample";
		String URI = Suggest_FTS_Samples.SEARCH_SAMPLE_PATH + "/" + SearchManager.SUGGEST_URI_PATH_QUERY;
	}
}
