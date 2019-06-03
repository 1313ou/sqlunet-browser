/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.w3c.dom.Document;

import androidx.annotation.NonNull;

/**
 * Business methods for WordNet interface
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("unused")
interface WordNetInterface
{
	// S E L E C T O R

	/**
	 * Business method that returns WordNet selector data as DOM document
	 *
	 * @param connection connection
	 * @param word       target word
	 * @return WordNet selector data as DOM document
	 */
	Document querySelectorDoc(final SQLiteDatabase connection, final String word);

	/**
	 * Business method that returns WordNet selector data as XML
	 *
	 * @param connection connection
	 * @param word       target word
	 * @return WordNet selector data as XML
	 */
	@NonNull
	String querySelectorXML(final SQLiteDatabase connection, final String word);

	// D E T A I L

	/**
	 * Business method that returns WordNet data as a Document
	 *
	 * @param connection connection
	 * @param word       target word
	 * @param withLinks  determines if queries are to include links
	 * @param recurse    determines if queries are to follow links recursively
	 * @return WordNet data as a DOM Document
	 */
	Document queryDoc(final SQLiteDatabase connection, final String word, final boolean withLinks, final boolean recurse);

	/**
	 * Business method that returns WordNet data as XML
	 *
	 * @param connection connection
	 * @param word       target word
	 * @param withLinks  determines if queries are to include links
	 * @param recurse    determines if queries are to follow links recursively
	 * @return WordNet data as XML
	 */
	@NonNull
	String queryXML(final SQLiteDatabase connection, final String word, final boolean withLinks, final boolean recurse);

	/**
	 * Business method that returns WordNet data as DOM document
	 *
	 * @param connection    connection
	 * @param word          target word
	 * @param posName       target part-of-speech
	 * @param lexDomainName target lexdomain
	 * @param linkName      target link type
	 * @param withLinks     determines if queries are to include links
	 * @param recurse       determines if queries are to follow links recursively
	 * @return WordNet data as DOM document
	 */
	Document queryDoc(final SQLiteDatabase connection, final String word, final String posName, final String lexDomainName, final String linkName, final boolean withLinks, final boolean recurse);

	/**
	 * Business method that returns WordNet data as XML
	 *
	 * @param connection    connection
	 * @param word          target word
	 * @param posName       target part-of-speech
	 * @param lexDomainName target lexdomain
	 * @param linkName      target link type
	 * @param withLinks     determines if queries are to include links
	 * @param recurse       determines if queries are to follow links recursively
	 * @return WordNet data as XML data
	 */
	@NonNull
	String queryXML(final SQLiteDatabase connection, final String word, final String posName, final String lexDomainName, final String linkName, final boolean withLinks, final boolean recurse);

	/**
	 * Business method that returns WordNet data as a Document
	 *
	 * @param connection connection
	 * @param wordId     target word id
	 * @param synsetId   target synset id
	 * @param withLinks  determines if queries are to include links
	 * @param recurse    determines if queries are to follow links recursively
	 * @return WordNet data as a DOM Document
	 */
	Document queryDoc(final SQLiteDatabase connection, final long wordId, final Long synsetId, final boolean withLinks, final boolean recurse);

	// I T E M S

	/**
	 * Business method that returns WordNet word data as DOM document
	 *
	 * @param connection connection
	 * @param wordId     target word id
	 * @return WordNet word data as DOM document
	 */
	Document queryWordDoc(final SQLiteDatabase connection, final long wordId);

	/**
	 * Business method that returns WordNet word data as XML
	 *
	 * @param connection connection
	 * @param wordId     target word id
	 * @return WordNet word data as XML
	 */
	@NonNull
	String queryWordXML(final SQLiteDatabase connection, final long wordId);

	/**
	 * Business method that returns WordNet sense data as DOM document
	 *
	 * @param connection connection
	 * @param wordId     target word id
	 * @param synsetId   target synset id
	 * @return WordNet synset data as DOM document
	 */
	Document querySenseDoc(final SQLiteDatabase connection, final long wordId, final long synsetId);

	/**
	 * Business method that returns WordNet sense data as XML
	 *
	 * @param connection connection
	 * @param wordId     target word id
	 * @param synsetId   target synset id
	 * @return WordNet synset data as XML
	 */
	@NonNull
	String querySenseXML(final SQLiteDatabase connection, final long wordId, final long synsetId);

	/**
	 * Business method that returns WordNet synset data as DOM document
	 *
	 * @param connection connection
	 * @param synsetId   target synset id
	 * @return WordNet synset data as DOM document
	 */
	Document querySynsetDoc(final SQLiteDatabase connection, final long synsetId);

	/**
	 * Business method that returns WordNet synset data as XML
	 *
	 * @param connection connection
	 * @param synsetId   target synset id
	 * @return WordNet synset data as XML
	 */
	@NonNull
	String querySynsetXML(final SQLiteDatabase connection, final long synsetId);

	/**
	 * Business method that returns WordNet parts-of-speech as array of strings
	 *
	 * @return array of Strings
	 */
	@NonNull
	String[] getPosNames();

	/**
	 * Business method that returns WordNet lexdomains as array of strings
	 *
	 * @return array of Strings
	 */
	@NonNull
	String[] getLexDomainNames();

	/**
	 * Business method that returns WordNet link names as array of strings
	 *
	 * @return array of Strings
	 */
	@NonNull
	String[] getLinkNames();
}