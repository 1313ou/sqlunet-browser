package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.w3c.dom.Document;

/**
 * Business methods for VerbNet interface
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("unused")
interface VerbNetInterface
{
	// S E L E C T I O N

	/**
	 * Business method the returns VerbNet selector data as DOM document
	 *
	 * @param word target word
	 * @return VerbNet selector data as DOM document
	 */
	Document querySelectorDoc(final SQLiteDatabase connection, final String word);

	/**
	 * Business method that returns VerbNet selector data as XML
	 *
	 * @param word target word
	 * @return VerbNet selector data as XML
	 */
	String querySelectorXML(final SQLiteDatabase connection, final String word);

	// D E T A I L

	/**
	 * Business method the returns VerbNet data as DOM document from word
	 *
	 * @param word target word
	 * @return VerbNet data as DOM document
	 */
	Document queryDoc(final SQLiteDatabase connection, final String word);

	/**
	 * Business method that returns VerbNet data as XML from word
	 *
	 * @param word target word
	 * @return VerbNet data as XML
	 */
	String queryXML(final SQLiteDatabase connection, final String word);

	/**
	 * Business method that returns VerbNet data as DOM document from sense
	 *
	 * @param wordId   is the word id to build query from
	 * @param synsetId is the synset id to build query from (-1 if any)
	 * @param pos      the pos to build query from
	 * @return VerbNet data as DOM document
	 */
	Document queryDoc(final SQLiteDatabase connection, final long wordId, final Long synsetId, final Character pos);

	/**
	 * Business method that returns VerbNet data as XML from sense
	 *
	 * @param wordId   target word id
	 * @param synsetId target synset id (-1 if any)
	 * @param pos      the pos to build query from
	 * @return VerbNet data as XML
	 */
	String queryXML(final SQLiteDatabase connection, final long wordId, final Long synsetId, final Character pos);

	// I T E M S

	/**
	 * Business method the returns class data as DOM document from class id
	 *
	 * @param connection database connection
	 * @param classId    the class to build query from
	 * @param pos        the pos to build query from
	 * @return VerbNet class data as DOM document
	 */
	Document queryClassDoc(final SQLiteDatabase connection, final long classId, final Character pos);

	/**
	 * Business method that returns class data as XML
	 *
	 * @param connection database connection
	 * @param classId    the class to build query from
	 * @param pos        the pos to build query from
	 * @return VerbNet class data as XML
	 */
	String queryClassXML(final SQLiteDatabase connection, final long classId, final Character pos);
}
