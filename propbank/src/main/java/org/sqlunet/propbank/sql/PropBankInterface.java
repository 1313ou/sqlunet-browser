package org.sqlunet.propbank.sql;

import android.database.sqlite.SQLiteDatabase;

import org.w3c.dom.Document;

/**
 * Business methods fro PropBank interface
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("unused")
interface PropBankInterface
{
	// S E L E C T I O N

	/**
	 * Business method the returns PropBank selector data as DOM document
	 *
	 * @param word is the target word
	 * @return PropBank selector data as DOM document
	 */
	Document querySelectorDoc(final SQLiteDatabase connection, final String word);

	/**
	 * Business method that returns PropBank selector data as XML
	 *
	 * @param word is the target word
	 * @return PropBank selector data as XML
	 */
	String querySelectorXML(final SQLiteDatabase connection, final String word);

	// D E T A I L

	/**
	 * Business method the returns PropBank data as DOM document from word
	 *
	 * @param word is the target word
	 * @return PropBank data as DOM document
	 */
	Document queryDoc(final SQLiteDatabase connection, final String word);

	/**
	 * Business method that returns PropBank selector data as XML from word
	 *
	 * @param word is the target word
	 * @return PropBank selector data as XML
	 */
	String queryXML(final SQLiteDatabase connection, final String word);

	/**
	 * Business method that returns PropBank data as DOM document from word id
	 *
	 * @param wordId is the word id to build query from
	 * @param pos    the pos to build query from
	 * @return PropBank data as DOM document
	 */
	Document queryDoc(final SQLiteDatabase connection, final long wordId, final Character pos);

	/**
	 * Business method that returns PropBank data as XML from word id
	 *
	 * @param wordId is the target word id
	 * @param pos    the pos to build query from
	 * @return PropBank data as XML
	 */
	String queryXML(final SQLiteDatabase connection, final long wordId, final Character pos);

	// I T E M S

	/**
	 * Business method the returns role set data as DOM document from roleSet id
	 *
	 * @param connection database connection
	 * @param roleSetId  the role set to build query from
	 * @param pos        the pos to build query from
	 * @return Propbank role set data as DOM document
	 */
	Document queryRoleSetDoc(final SQLiteDatabase connection, final long roleSetId, final Character pos);

	/**
	 * Business method that returns role set data as XML from roleSet id
	 *
	 * @param connection database connection
	 * @param roleSetId  the roleSet id to build query from
	 * @param pos        the pos to build query from
	 * @return Propbank role set data as XML
	 */
	String queryRoleSetXML(final SQLiteDatabase connection, final long roleSetId, final Character pos);
}
