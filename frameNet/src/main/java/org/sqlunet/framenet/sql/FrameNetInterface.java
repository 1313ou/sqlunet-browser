/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.framenet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.w3c.dom.Document;

import androidx.annotation.NonNull;

/**
 * Business methods for FrameNet interface
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
interface FrameNetInterface
{
	// S E L E C T O R

	/**
	 * Business method that returns FrameNet selector data as DOM document
	 *
	 * @param word target word
	 * @param pos  target pos to build query from
	 * @return FrameNet selector data as DOM document
	 */
	Document querySelectorDoc(final SQLiteDatabase connection, final String word, final Character pos);

	/**
	 * Business method that returns FrameNet selector data as XML
	 *
	 * @param word target word
	 * @param pos  target pos to build query from
	 * @return FrameNet selector data as XML
	 */
	@NonNull
	String querySelectorXML(final SQLiteDatabase connection, final String word, final Character pos);

	// D E T A I L

	/**
	 * Business method that returns FrameNet data as DOM document
	 *
	 * @param wordId target word id to build query from
	 * @param pos    target pos to build query from
	 * @return FrameNet data as DOM document
	 */
	Document queryDoc(final SQLiteDatabase connection, final long wordId, final Character pos);

	/**
	 * Business method that returns FrameNet data as XML
	 *
	 * @param wordId target word id
	 * @param pos    target pos to build query from
	 * @return FrameNet data as XML
	 */
	@NonNull
	String queryXML(final SQLiteDatabase connection, final long wordId, final Character pos);

	/**
	 * Business method that returns FrameNet data as DOM document
	 *
	 * @param word target word
	 * @param pos  target pos to build query from
	 * @return FrameNet data as DOM document
	 */
	Document queryDoc(final SQLiteDatabase connection, final String word, final Character pos);

	/**
	 * Business method that returns FrameNet data as XML
	 *
	 * @param word target word
	 * @param pos  target pos to build query from
	 * @return FrameNet data as XML
	 */
	@NonNull
	String queryXML(final SQLiteDatabase connection, final String word, final Character pos);

	// I T E M S

	/**
	 * Business method that returns FrameNet frame data as DOM document
	 *
	 * @param connection connection
	 * @param frameId    target frame to build query from
	 * @param pos        target pos to build query from
	 * @return FrameNet frame id data as DOM document
	 */
	Document queryFrameDoc(final SQLiteDatabase connection, final long frameId, final Character pos);

	/**
	 * Business method that returns FrameNet frame data as XML
	 *
	 * @param connection connection
	 * @param frameId    target frame id to build query from
	 * @param pos        target pos to build query from
	 * @return FrameNet frame data as XML
	 */
	@NonNull
	String queryFrameXML(final SQLiteDatabase connection, final long frameId, final Character pos);

	/**
	 * Business method that returns FrameNet lex unit data as DOM document
	 *
	 * @param connection connection
	 * @param luId       target lex unit id to build query from
	 * @return FrameNet lex unit data as DOM document
	 */
	Document queryLexUnitDoc(final SQLiteDatabase connection, final long luId);

	/**
	 * Business method that returns FrameNet lex unit data as XML
	 *
	 * @param connection connection
	 * @param luId       target lex unit id to build query from
	 * @return FrameNet lex unit data as XML
	 */
	@NonNull
	String queryLexUnitXML(final SQLiteDatabase connection, final long luId);
}
