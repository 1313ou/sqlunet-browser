package org.sqlunet.wordnet.sql;

import org.w3c.dom.Document;

import android.database.sqlite.SQLiteDatabase;

/**
 * Business methods for WordNet interface
 *
 * @author Bernard Bou <bbou@ac-toulouse.fr>
 */
@SuppressWarnings("unused")
interface WordNetInterface
{
	// S E L E C T I O N
	
	/**
	 * Business method that returns WordNet selector data as DOM document
	 *
	 * @param thisConnection
	 *            connection
	 * @param thisWord
	 *            the target word
	 * @return WordNet selector data as DOM document <!-- end-user-doc -->
	 */
	Document querySelectorDoc(final SQLiteDatabase thisConnection, final String thisWord);

	/**
	 * Business method that returns WordNet selector data as XML
	 *
	 * @param thisConnection
	 *            connection
	 * @param thisWord
	 *            the target word
	 * @return WordNet selector data as XML
	 */
	String querySelectorXML(final SQLiteDatabase thisConnection, final String thisWord);

	// D E T A I L
	
	/**
	 * Business method that returns WordNet data as a Document
	 *
	 * @param thisConnection
	 *            connection
	 * @param thisWord
	 *            the target word
	 * @param withLinks
	 *            determines if queries are to include links
	 * @param recurse
	 *            determines if queries are to follow links recursively
	 * @return WordNet data as a DOM Document <!-- end-user-doc -->
	 */
	Document queryDoc(final SQLiteDatabase thisConnection, final String thisWord, final boolean withLinks, final boolean recurse);

	/**
	 * Business method that returns WordNet data as XML
	 *
	 * @param thisConnection
	 *            connection
	 * @param thisWord
	 *            the target word
	 * @param withLinks
	 *            determines if queries are to include links
	 * @param recurse
	 *            determines if queries are to follow links recursively
	 * @return WordNet data as XML <!-- end-user-doc -->
	 */
	String queryXML(final SQLiteDatabase thisConnection, final String thisWord, final boolean withLinks, final boolean recurse);

	/**
	 * Business method that returns WordNet data as DOM document
	 *
	 * @param thisConnection
	 *            connection
	 * @param thisWord
	 *            the target word
	 * @param thisPosName
	 *            the target part-of-speech
	 * @param thisLexDomainName
	 *            the target lexdomain
	 * @param thisLinkName
	 *            the target link type
	 * @param withLinks
	 *            determines if queries are to include links
	 * @param recurse
	 *            determines if queries are to follow links recursively
	 * @return WordNet data as DOM document <!-- end-user-doc -->
	 */
	Document queryDoc(final SQLiteDatabase thisConnection, final String thisWord, final String thisPosName, final String thisLexDomainName, final String thisLinkName, final boolean withLinks, final boolean recurse);

	/**
	 * Business method that returns WordNet data as XML
	 *
	 * @param thisConnection
	 *            connection
	 * @param thisWord
	 *            the target word
	 * @param thisPosName
	 *            the target part-of-speech
	 * @param thisLexDomainName
	 *            the target lexdomain
	 * @param thisLinkName
	 *            the target link type
	 * @param withLinks
	 *            determines if queries are to include links
	 * @param recurse
	 *            determines if queries are to follow links recursively
	 * @return WordNet data as XML data <!-- end-user-doc -->
	 */
	String queryXML(final SQLiteDatabase thisConnection, final String thisWord, final String thisPosName, final String thisLexDomainName, final String thisLinkName, final boolean withLinks, final boolean recurse);

	/**
	 * Business method that returns WordNet data as a Document
	 *
	 * @param thisConnection
	 *            connection
	 * @param thisWordId
	 *            the target word id
	 * @param thisSynsetId
	 *            the target synset id
	 * @param withLinks
	 *            determines if queries are to include links
	 * @param recurse
	 *            determines if queries are to follow links recursively
	 * @return WordNet data as a DOM Document <!-- end-user-doc -->
	 */
	Document queryDoc(final SQLiteDatabase thisConnection, final long thisWordId, final Long thisSynsetId, @SuppressWarnings("SameParameterValue") final boolean withLinks, @SuppressWarnings("SameParameterValue") final boolean recurse);

	// I T E M S
	
	/**
	 * Business method that returns WordNet sense data as DOM document
	 *
	 * @param thisConnection
	 *            connection
	 * @param thisWordId
	 *            the target wordid
	 * @param thisSynsetId
	 *            the target synsetid
	 * @return WordNet synset data as DOM document <!-- end-user-doc -->
	 */
	Document querySenseDoc(final SQLiteDatabase thisConnection, final long thisWordId, final long thisSynsetId);

	/**
	 * Business method that returns WordNet sense data as XML
	 *
	 * @param thisConnection
	 *            connection
	 * @param thisWordId
	 *            the target wordid
	 * @param thisSynsetId
	 *            the target synsetid
	 * @return WordNet synset data as XML
	 */
	String querySenseXML(final SQLiteDatabase thisConnection, final long thisWordId, final long thisSynsetId);

	/**
	 * Business method that returns WordNet synset data as DOM document
	 *
	 * @param thisConnection
	 *            connection
	 * @param thisSynsetId
	 *            the target synsetid
	 * @return WordNet synset data as DOM document <!-- end-user-doc -->
	 */
	Document querySynsetDoc(final SQLiteDatabase thisConnection, final long thisSynsetId);

	/**
	 * Business method that returns WordNet synset data as XML
	 *
	 * @param thisConnection
	 *            connection
	 * @param thisSynsetId
	 *            the target synsetid
	 * @return WordNet synset data as XML
	 */
	String querySynsetXML(final SQLiteDatabase thisConnection, final long thisSynsetId);

	/**
	 * Business method that returns WordNet parts-of-speech as array of strings
	 *
	 * @return array of Strings
	 */
	String[] getPosNames();

	/**
	 * Business method that returns WordNet lexdomains as array of strings
	 *
	 * @return array of Strings
	 */
	String[] getLexDomainNames();

	/**
	 * Business method that returns WordNet link names as array of strings
	 *
	 * @return array of Strings
	 */
	String[] getLinkNames();
}