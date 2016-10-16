package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.w3c.dom.Document;

/**
 * Business methods for FrameNet interface
 *
 * @author Bernard Bou <bbou@ac-toulouse.fr>
 */
@SuppressWarnings("unused")
interface FrameNetInterface
{
	// S E L E C T I O N

	/**
	 * Business method the returns FrameNet selector data as DOM document
	 *
	 * @param word is the target word
	 * @param pos  the pos to build query from
	 * @return FrameNet selector data as DOM document
	 */
	Document querySelectorDoc(final SQLiteDatabase connection, final String word, final Character pos);

	/**
	 * Business method that returns FrameNet selector data as XML
	 *
	 * @param word is the target word
	 * @param pos  the pos to build query from
	 * @return FrameNet selector data as XML
	 */
	String querySelectorXML(final SQLiteDatabase connection, final String word, final Character pos);

	// D E T A I L

	/**
	 * Business method that returns FrameNet data as DOM document
	 *
	 * @param wordId is the word id to build query from
	 * @param pos    the pos to build query from
	 * @return FrameNet data as DOM document
	 */
	Document queryDoc(final SQLiteDatabase connection, final long wordId, final Character pos);

	/**
	 * Business method that returns FrameNet data as XML
	 *
	 * @param wordId is the target word id
	 * @param pos    the pos to build query from
	 * @return FrameNet data as XML
	 */
	String queryXML(final SQLiteDatabase connection, final long wordId, final Character pos);

	/**
	 * Business method the returns FrameNet data as DOM document
	 *
	 * @param word is the target word
	 * @param pos  the pos to build query from
	 * @return FrameNet data as DOM document
	 */
	Document queryDoc(final SQLiteDatabase connection, final String word, final Character pos);

	/**
	 * Business method that returns FrameNet data as XML
	 *
	 * @param word is the target word
	 * @param pos  the pos to build query from
	 * @return FrameNet data as XML
	 */
	String queryXML(final SQLiteDatabase connection, final String word, final Character pos);

	// I T E M S

	/**
	 * Business method the returns FrameNet frame data as DOM document
	 *
	 * @param connection database connection
	 * @param frameId    the frame to build query from
	 * @param pos        the pos to build query from
	 * @return FrameNet frame id data as DOM document
	 */
	Document queryFrameDoc(final SQLiteDatabase connection, final long frameId, @SuppressWarnings("UnusedParameters") final Character pos);

	/**
	 * Business method that returns FrameNet frame data as XML
	 *
	 * @param connection database connection
	 * @param frameId    the frame id to build query from
	 * @param pos        the pos to build query from
	 * @return FrameNet frame data as XML
	 */
	String queryFrameXML(final SQLiteDatabase connection, final long frameId, final Character pos);

	/**
	 * Business method the returns FrameNet lex unit data as DOM document
	 *
	 * @param connection database connection
	 * @param luId       the lex unit id to build query from
	 * @return FrameNet lex unit data as DOM document
	 */
	Document queryLexUnitDoc(final SQLiteDatabase connection, final long luId);

	/**
	 * Business method that returns FrameNet lex unit data as XML
	 *
	 * @param connection database connection
	 * @param luId       the lex unit id to build query from
	 * @return FrameNet lex unit data as XML
	 */
	String queryLexUnitXML(final SQLiteDatabase connection, final long luId);
}
