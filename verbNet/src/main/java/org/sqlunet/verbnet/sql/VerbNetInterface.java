/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.verbnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.w3c.dom.Document;

import androidx.annotation.NonNull;

/**
 * Business methods for VerbNet interface
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
@SuppressWarnings("unused")
interface VerbNetInterface
{
	// S E L E C T O R

	/**
	 * Business method that returns VerbNet selector data as DOM document
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
	@NonNull
	String querySelectorXML(final SQLiteDatabase connection, final String word);

	// D E T A I L

	/**
	 * Business method that returns VerbNet data as DOM document from word
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
	@NonNull
	String queryXML(final SQLiteDatabase connection, final String word);

	/**
	 * Business method that returns VerbNet data as DOM document from sense
	 *
	 * @param wordId   word id to build query from
	 * @param synsetId synset id to build query from (null if any)
	 * @param pos      pos to build query from
	 * @return VerbNet data as DOM document
	 */
	Document queryDoc(final SQLiteDatabase connection, final long wordId, final Long synsetId, final Character pos);

	/**
	 * Business method that returns VerbNet data as XML from sense
	 *
	 * @param wordId   target word id
	 * @param synsetId target synset id (null if any)
	 * @param pos      pos to build query from
	 * @return VerbNet data as XML
	 */
	@NonNull
	String queryXML(final SQLiteDatabase connection, final long wordId, final Long synsetId, final Character pos);

	// I T E M S

	/**
	 * Business method that returns class data as DOM document from class id
	 *
	 * @param connection connection
	 * @param classId    class to build query from
	 * @param pos        pos to build query from
	 * @return VerbNet class data as DOM document
	 */
	Document queryClassDoc(final SQLiteDatabase connection, final long classId, final Character pos);

	/**
	 * Business method that returns class data as XML
	 *
	 * @param connection connection
	 * @param classId    class to build query from
	 * @param pos        pos to build query from
	 * @return VerbNet class data as XML
	 */
	@NonNull
	String queryClassXML(final SQLiteDatabase connection, final long classId, final Character pos);
}
