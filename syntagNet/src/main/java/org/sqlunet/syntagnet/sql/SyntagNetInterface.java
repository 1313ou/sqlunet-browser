/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.syntagnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.w3c.dom.Document;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
	Document queryDoc(final SQLiteDatabase connection, @NonNull final String word);

	/**
	 * Business method that returns SyntagNet selector data as XML from word
	 *
	 * @param word target word
	 * @return SyntagNet selector data as XML
	 */
	@NonNull
	String queryXML(final SQLiteDatabase connection, @NonNull final String word);

	/**
	 * Business method that returns SyntagNet data as DOM document from word id
	 *
	 * @param wordId   is the word id to build query from
	 * @param synsetId is the synset id to build query from (nullable)
	 * @param pos      the pos to build query from (nullable)
	 * @return SyntagNet data as DOM document
	 */
	Document queryDoc(final SQLiteDatabase connection, final long wordId, @Nullable final Long synsetId, @Nullable final Character pos);

	/**
	 * Business method that returns SyntagNet data as XML from word id
	 *
	 * @param wordId   target word id
	 * @param synsetId is the synset id to build query from (nullable)
	 * @param pos      the pos to build query from (nullable)
	 * @return SyntagNet data as XML
	 */
	@NonNull
	String queryXML(final SQLiteDatabase connection, final long wordId, @Nullable final Long synsetId, @Nullable final Character pos);

	/**
	 * Business method that returns SyntagNet data as DOM document from word id
	 *
	 * @param wordId   is the word id to build query from
	 * @param synsetId is the synset id to build query from (nullable)
	 * @param word2Id   is the word 2 id to build query from
	 * @param synset2Id is the synset 2 id to build query from (nullable)
	 * @return SyntagNet data as DOM document
	 */
	Document queryDoc(final SQLiteDatabase connection, final long wordId, @Nullable final Long synsetId, final long word2Id, @Nullable final Long synset2Id);

	/**
	 * Business method that returns SyntagNet data as XML from word id
	 *
	 * @param wordId   target word id
	 * @param synsetId is the synset id to build query from (nullable)
	 * @param word2Id   is the word 2 id to build query from
	 * @param synset2Id is the synset 2 id to build query from (nullable)
	 * @return SyntagNet data as XML
	 */
	@NonNull
	String queryXML(final SQLiteDatabase connection, final long wordId, @Nullable final Long synsetId, final long word2Id, @Nullable final Long synset2Id);

	// I T E M S

	/**
	 * Business method that returns collocation data as DOM document from collocation id
	 *
	 * @param connection    connection
	 * @param collocationId the collocation to build query from
	 * @return SyntagNet collocation data as DOM document
	 */
	Document queryCollocationDoc(final SQLiteDatabase connection, final long collocationId);

	/**
	 * Business method that returns collocation data as XML from collocation id
	 *
	 * @param connection    connection
	 * @param collocationId the collocation id to build query from
	 * @return SyntagNet collocation data as XML
	 */
	@NonNull
	String queryCollocationXML(final SQLiteDatabase connection, final long collocationId);
}
