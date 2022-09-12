/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.propbank.sql;

import android.database.sqlite.SQLiteDatabase;

import org.w3c.dom.Document;

import androidx.annotation.NonNull;

/**
 * Business methods fro PropBank interface
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
interface PropBankInterface
{
	// S E L E C T O R

	/**
	 * Business method that returns PropBank selector data as DOM document
	 *
	 * @param word target word
	 * @return PropBank selector data as DOM document
	 */
	Document querySelectorDoc(final SQLiteDatabase connection, final String word);

	/**
	 * Business method that returns PropBank selector data as XML
	 *
	 * @param word target word
	 * @return PropBank selector data as XML
	 */
	@NonNull
	String querySelectorXML(final SQLiteDatabase connection, final String word);

	// D E T A I L

	/**
	 * Business method that returns PropBank data as DOM document from word
	 *
	 * @param word target word
	 * @return PropBank data as DOM document
	 */
	Document queryDoc(final SQLiteDatabase connection, final String word);

	/**
	 * Business method that returns PropBank selector data as XML from word
	 *
	 * @param word target word
	 * @return PropBank selector data as XML
	 */
	@NonNull
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
	 * @param wordId target word id
	 * @param pos    the pos to build query from
	 * @return PropBank data as XML
	 */
	@NonNull
	String queryXML(final SQLiteDatabase connection, final long wordId, final Character pos);

	// I T E M S

	/**
	 * Business method that returns role set data as DOM document from role set id
	 *
	 * @param connection connection
	 * @param roleSetId  the role set to build query from
	 * @param pos        the pos to build query from
	 * @return PropBank role set data as DOM document
	 */
	Document queryRoleSetDoc(final SQLiteDatabase connection, final long roleSetId, final Character pos);

	/**
	 * Business method that returns role set data as XML from role set id
	 *
	 * @param connection connection
	 * @param roleSetId  the role set id to build query from
	 * @param pos        the pos to build query from
	 * @return PropBank role set data as XML
	 */
	@NonNull
	String queryRoleSetXML(final SQLiteDatabase connection, final long roleSetId, final Character pos);
}
