package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.w3c.dom.Document;

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
	 * @param connection connection
	 * @param word       the target word
	 * @return WordNet selector data as DOM document <!-- end-user-doc -->
	 */
	Document querySelectorDoc(final SQLiteDatabase connection, final String word);

	/**
	 * Business method that returns WordNet selector data as XML
	 *
	 * @param connection connection
	 * @param word       the target word
	 * @return WordNet selector data as XML
	 */
	String querySelectorXML(final SQLiteDatabase connection, final String word);

	// D E T A I L

	/**
	 * Business method that returns WordNet data as a Document
	 *
	 * @param connection connection
	 * @param word       the target word
	 * @param withLinks  determines if queries are to include links
	 * @param recurse    determines if queries are to follow links recursively
	 * @return WordNet data as a DOM Document <!-- end-user-doc -->
	 */
	Document queryDoc(final SQLiteDatabase connection, final String word, final boolean withLinks, final boolean recurse);

	/**
	 * Business method that returns WordNet data as XML
	 *
	 * @param connection connection
	 * @param word       the target word
	 * @param withLinks  determines if queries are to include links
	 * @param recurse    determines if queries are to follow links recursively
	 * @return WordNet data as XML <!-- end-user-doc -->
	 */
	String queryXML(final SQLiteDatabase connection, final String word, final boolean withLinks, final boolean recurse);

	/**
	 * Business method that returns WordNet data as DOM document
	 *
	 * @param connection    connection
	 * @param word          the target word
	 * @param posName       the target part-of-speech
	 * @param lexDomainName the target lexdomain
	 * @param linkName      the target link type
	 * @param withLinks     determines if queries are to include links
	 * @param recurse       determines if queries are to follow links recursively
	 * @return WordNet data as DOM document <!-- end-user-doc -->
	 */
	Document queryDoc(final SQLiteDatabase connection, final String word, final String posName, final String lexDomainName, final String linkName, final boolean withLinks, final boolean recurse);

	/**
	 * Business method that returns WordNet data as XML
	 *
	 * @param connection    connection
	 * @param word          the target word
	 * @param posName       the target part-of-speech
	 * @param lexDomainName the target lexdomain
	 * @param linkName      the target link type
	 * @param withLinks     determines if queries are to include links
	 * @param recurse       determines if queries are to follow links recursively
	 * @return WordNet data as XML data <!-- end-user-doc -->
	 */
	String queryXML(final SQLiteDatabase connection, final String word, final String posName, final String lexDomainName, final String linkName, final boolean withLinks, final boolean recurse);

	/**
	 * Business method that returns WordNet data as a Document
	 *
	 * @param connection connection
	 * @param wordId     the target word id
	 * @param synsetId   the target synset id
	 * @param withLinks  determines if queries are to include links
	 * @param recurse    determines if queries are to follow links recursively
	 * @return WordNet data as a DOM Document <!-- end-user-doc -->
	 */
	Document queryDoc(final SQLiteDatabase connection, final long wordId, final Long synsetId, final boolean withLinks, final boolean recurse);

	// I T E M S

	/**
	 * Business method that returns WordNet sense data as DOM document
	 *
	 * @param connection connection
	 * @param wordId     the target wordid
	 * @param synsetId   the target synsetid
	 * @return WordNet synset data as DOM document <!-- end-user-doc -->
	 */
	Document querySenseDoc(final SQLiteDatabase connection, final long wordId, final long synsetId);

	/**
	 * Business method that returns WordNet sense data as XML
	 *
	 * @param connection connection
	 * @param wordId     the target wordid
	 * @param synsetId   the target synsetid
	 * @return WordNet synset data as XML
	 */
	String querySenseXML(final SQLiteDatabase connection, final long wordId, final long synsetId);

	/**
	 * Business method that returns WordNet synset data as DOM document
	 *
	 * @param connection connection
	 * @param synsetId   the target synsetid
	 * @return WordNet synset data as DOM document <!-- end-user-doc -->
	 */
	Document querySynsetDoc(final SQLiteDatabase connection, final long synsetId);

	/**
	 * Business method that returns WordNet synset data as XML
	 *
	 * @param connection connection
	 * @param synsetId   the target synsetid
	 * @return WordNet synset data as XML
	 */
	String querySynsetXML(final SQLiteDatabase connection, final long synsetId);

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