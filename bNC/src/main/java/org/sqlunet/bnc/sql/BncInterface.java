package org.sqlunet.bnc.sql;

import org.w3c.dom.Document;

import android.database.sqlite.SQLiteDatabase;

@SuppressWarnings("unused")
interface BncInterface
{
	/**
	 * Business method the returns BNC data as DOM document
	 *
	 * @param thisConnection
	 *            database connection
	 * @param thisWord
	 *            the target word
	 * @return BNC data as DOM document
	 */
	Document queryDoc(final SQLiteDatabase thisConnection, final String thisWord);

	/**
	 * Business method that returns BNC data as XML
	 *
	 * @param thisConnection
	 *            database connection
	 * @param thisWord
	 *            the target word
	 * @return BNC data as XML
	 */
	String queryXML(final SQLiteDatabase thisConnection, final String thisWord);

	/**
	 * Business method that returns BNC data as DOM document
	 *
	 * @param thisConnection
	 *            database connection
	 * @param thisWordId
	 *            the word id to build query from
	 * @param thisPos
	 *            the pos to build query from (null if any)
	 * @return BNC data as DOM document
	 */
	Document queryDoc(final SQLiteDatabase thisConnection, final long thisWordId, final Character thisPos);

	/**
	 * Business method that returns BNC data as XML
	 *
	 * @param thisConnection
	 *            database connection
	 * @param thisWordId
	 *            the target word id
	 * @param thisPos
	 *            the target pos (null if any)
	 * @return BNC data as XML
	 */
	String queryXML(final SQLiteDatabase thisConnection, final long thisWordId, final Character thisPos);
}