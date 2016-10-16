package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.w3c.dom.Document;

/**
 * Business methods for VerbNet interface
 *
 * @author Bernard Bou <bbou@ac-toulouse.fr>
 */
@SuppressWarnings("unused")
interface VerbNetInterface
{
	// S E L E C T I O N

	/**
	 * Business method the returns VerbNet selector data as DOM document
	 *
	 * @param thisWord is the target word
	 * @return VerbNet selector data as DOM document
	 */
	Document querySelectorDoc(final SQLiteDatabase thisConnection, final String thisWord);

	/**
	 * Business method that returns VerbNet selector data as XML
	 *
	 * @param thisWord is the target word
	 * @return VerbNet selector data as XML
	 */
	String querySelectorXML(final SQLiteDatabase thisConnection, final String thisWord);

	// D E T A I L

	/**
	 * Business method the returns VerbNet data as DOM document from word
	 *
	 * @param thisWord is the target word
	 * @return VerbNet data as DOM document
	 */
	Document queryDoc(final SQLiteDatabase thisConnection, final String thisWord);

	/**
	 * Business method that returns VerbNet data as XML from word
	 *
	 * @param thisWord is the target word
	 * @return VerbNet data as XML
	 */
	String queryXML(final SQLiteDatabase thisConnection, final String thisWord);

	/**
	 * Business method that returns VerbNet data as DOM document from sense
	 *
	 * @param thisWordId   is the word id to build query from
	 * @param thisSynsetId is the synset id to build query from (-1 if any)
	 * @param thisPos      the pos to build query from
	 * @return VerbNet data as DOM document
	 */
	Document queryDoc(final SQLiteDatabase thisConnection, final long thisWordId, final Long thisSynsetId, final Character thisPos);

	/**
	 * Business method that returns VerbNet data as XML from sense
	 *
	 * @param thisWordId   is the target word id
	 * @param thisSynsetId is the target synset id (-1 if any)
	 * @param thisPos      the pos to build query from
	 * @return VerbNet data as XML
	 */
	String queryXML(final SQLiteDatabase thisConnection, final long thisWordId, final Long thisSynsetId, final Character thisPos);

	// I T E M S

	/**
	 * Business method the returns class data as DOM document from class id
	 *
	 * @param thisConnection database connection
	 * @param thisClassId    the class to build query from
	 * @param thisPos        the pos to build query from
	 * @return VerbNet class data as DOM document
	 */
	Document queryClassDoc(final SQLiteDatabase thisConnection, final long thisClassId, final Character thisPos);

	/**
	 * Business method that returns class data as XML
	 *
	 * @param thisConnection database connection
	 * @param thisClassId    the class to build query from
	 * @param thisPos        the pos to build query from
	 * @return VerbNet class data as XML
	 */
	String queryClassXML(final SQLiteDatabase thisConnection, final long thisClassId, final Character thisPos);
}
