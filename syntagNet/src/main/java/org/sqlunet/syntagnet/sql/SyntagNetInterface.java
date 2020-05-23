/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.syntagnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.w3c.dom.Document;

import androidx.annotation.NonNull;

/**
 * Business methods fro SyntagNet interface
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("unused")
interface SyntagNetInterface
{
	// S E L E C T O R

	/**
	 * Business method that returns SyntagNet selector data as DOM document
	 *
	 * @param word target word
	 * @return SyntagNet selector data as DOM document
	 */
	Document querySelectorDoc(final SQLiteDatabase connection, final String word);

	/**
	 * Business method that returns SyntagNet selector data as XML
	 *
	 * @param word target word
	 * @return SyntagNet selector data as XML
	 */
	@NonNull
	String querySelectorXML(final SQLiteDatabase connection, final String word);

	// D E T A I L

	/**
	 * Business method that returns SyntagNet data as DOM document from word
	 *
	 * @param word target word
	 * @return SyntagNet data as DOM document
	 */
	Document queryDoc(final SQLiteDatabase connection, final String word);

	/**
	 * Business method that returns SyntagNet selector data as XML from word
	 *
	 * @param word target word
	 * @return SyntagNet selector data as XML
	 */
	@NonNull
	String queryXML(final SQLiteDatabase connection, final String word);

	/**
	 * Business method that returns SyntagNet data as DOM document from word id
	 *
	 * @param wordId is the word id to build query from
	 * @param pos    the pos to build query from
	 * @return SyntagNet data as DOM document
	 */
	Document queryDoc(final SQLiteDatabase connection, final long wordId, final Character pos);

	/**
	 * Business method that returns SyntagNet data as XML from word id
	 *
	 * @param wordId target word id
	 * @param pos    the pos to build query from
	 * @return SyntagNet data as XML
	 */
	@NonNull
	String queryXML(final SQLiteDatabase connection, final long wordId, final Character pos);

	// I T E M S

	/**
	 * Business method that returns collocation data as DOM document from role set id
	 *
	 * @param connection connection
	 * @param roleSetId  the role set to build query from
	 * @param pos        the pos to build query from
	 * @return SyntagNet role set data as DOM document
	 */
	Document queryCollocationDoc(final SQLiteDatabase connection, final long roleSetId, final Character pos);

	/**
	 * Business method that returns collocation data as XML from role set id
	 *
	 * @param connection connection
	 * @param roleSetId  the role set id to build query from
	 * @param pos        the pos to build query from
	 * @return SyntagNet role set data as XML
	 */
	@NonNull
	String queryCollocationXML(final SQLiteDatabase connection, final long roleSetId, final Character pos);
}
