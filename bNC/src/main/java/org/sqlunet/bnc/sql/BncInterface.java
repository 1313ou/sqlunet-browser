/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.bnc.sql;

import android.database.sqlite.SQLiteDatabase;

import org.w3c.dom.Document;

import androidx.annotation.NonNull;

interface BncInterface
{
	/**
	 * Business method that returns BNC data as DOM document
	 *
	 * @param connection connection
	 * @param word       the target word
	 * @return BNC data as DOM document
	 */
	Document queryDoc(final SQLiteDatabase connection, final String word);

	/**
	 * Business method that returns BNC data as XML
	 *
	 * @param connection connection
	 * @param word       the target word
	 * @return BNC data as XML
	 */
	@NonNull
	String queryXML(final SQLiteDatabase connection, final String word);

	/**
	 * Business method that returns BNC data as DOM document
	 *
	 * @param connection connection
	 * @param wordId     the word id to build query from
	 * @param pos        the pos to build query from (null if any)
	 * @return BNC data as DOM document
	 */
	Document queryDoc(final SQLiteDatabase connection, final long wordId, final Character pos);

	/**
	 * Business method that returns BNC data as XML
	 *
	 * @param connection connection
	 * @param wordId     the target word id
	 * @param pos        the target pos (null if any)
	 * @return BNC data as XML
	 */
	@NonNull
	String queryXML(final SQLiteDatabase connection, final long wordId, final Character pos);
}