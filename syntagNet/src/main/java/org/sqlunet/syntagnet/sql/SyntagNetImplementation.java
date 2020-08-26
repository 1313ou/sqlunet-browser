/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.syntagnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.dom.DomFactory;
import org.sqlunet.dom.DomTransformer;
import org.sqlunet.wordnet.sql.NodeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Encapsulates SyntagNet query implementation
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SyntagNetImplementation implements SyntagNetInterface
{
	static final String SN_NS = "http://org.sqlunet/sn";

	// S E L E C T O R

	/**
	 * Perform queries for SyntagNet selector data from word
	 *
	 * @param connection connection
	 * @param doc        org.w3c.dom.Document being built
	 * @param parent     org.w3c.dom.Node the walk will attach results to
	 * @param targetWord target word
	 */
	static private void walkSelector(final SQLiteDatabase connection, @NonNull final Document doc, final Node parent, final String targetWord)
	{
		final List<Collocation> collocations = Collocation.makeSelectorFromWord(connection, targetWord);

		// word
		NodeFactory.makeNode(doc, parent, "word", targetWord);

		// syntagnet nodes
		SyntagNetImplementation.makeSelector(doc, parent, collocations);
	}

	/**
	 * Perform queries for SyntagNet data from word
	 *
	 * @param connection connection
	 * @param doc        org.w3c.dom.Document being built
	 * @param parent     org.w3c.dom.Node the walk will attach results to
	 * @param targetWord target word
	 */
	static private void walk(final SQLiteDatabase connection, @NonNull final Document doc, final Node parent, final String targetWord)
	{
		final List<Collocation.WithDefinitionAndPos> collocations = Collocation.WithDefinitionAndPos.makeFromWord(connection, targetWord);

		// word
		NodeFactory.makeNode(doc, parent, "word", targetWord);

		// collocations
		int i = 1;
		for (final Collocation.WithDefinitionAndPos collocation : collocations)
		{
			SnNodeFactory.makeCollocationNode(doc, parent, collocation, i++);
		}
	}

	// D E T A I L

	/**
	 * Perform queries for SyntagNet data from word id
	 *
	 * @param connection   data source
	 * @param doc          org.w3c.dom.Document being built
	 * @param parent       org.w3c.dom.Node the walk will attach results to
	 * @param targetWordId target word id
	 */
	static private void walk(final SQLiteDatabase connection, @NonNull final Document doc, final Node parent, final long targetWordId)
	{
		// collocations
		final List<Collocation.WithDefinitionAndPos> collocations = Collocation.WithDefinitionAndPos.makeFromWordId(connection, targetWordId);
		walk(connection, doc, parent, collocations);
	}

	/**
	 * Perform queries for SyntagNet data from word id
	 *
	 * @param connection     data source
	 * @param doc            org.w3c.dom.Document being built
	 * @param parent         org.w3c.dom.Node the walk will attach results to
	 * @param targetWordId   target word id
	 * @param targetSynsetId target synset id
	 */
	private static void walk(final SQLiteDatabase connection, @NonNull final Document doc, final Node parent, final long targetWordId, final long targetSynsetId)
	{
		// collocations
		final List<Collocation.WithDefinitionAndPos> collocations = Collocation.WithDefinitionAndPos.makeFromWordIdAndSynsetId(connection, targetWordId, targetSynsetId);
		walk(connection, doc, parent, collocations);
	}

	/**
	 * Perform queries for SyntagNet data from word id
	 *
	 * @param connection   data source
	 * @param doc          org.w3c.dom.Document being built
	 * @param parent       org.w3c.dom.Node the walk will attach results to
	 * @param targetWordId target word id
	 */
	static private void walk2(final SQLiteDatabase connection, @NonNull final Document doc, final Node parent, final long targetWordId, final long targetWord2Id)
	{
		// collocations
		final List<Collocation.WithDefinitionAndPos> collocations = Collocation.WithDefinitionAndPos.makeFromWordIds(connection, targetWordId, targetWord2Id);
		walk(connection, doc, parent, collocations);
	}

	/**
	 * Perform queries for SyntagNet data from word id
	 *
	 * @param connection     data source
	 * @param doc            org.w3c.dom.Document being built
	 * @param parent         org.w3c.dom.Node the walk will attach results to
	 * @param targetWordId   target word id
	 * @param targetSynsetId target synset id
	 */
	private static void walk2(final SQLiteDatabase connection, @NonNull final Document doc, final Node parent, final long targetWordId, final long targetSynsetId, final long targetWord2Id, final long targetSynset2Id)
	{
		// collocations
		final List<Collocation.WithDefinitionAndPos> collocations = Collocation.WithDefinitionAndPos.makeFromWordIdAndSynsetIds(connection, targetWordId, targetSynsetId, targetWord2Id, targetSynset2Id);
		walk(connection, doc, parent, collocations);
	}

	/**
	 * Perform queries for SyntagNet data from collocation id
	 *
	 * @param connection    data source
	 * @param doc           org.w3c.dom.Document being built
	 * @param parent        org.w3c.dom.Node the walk will attach results to
	 * @param collocationId collocation id
	 */
	static private void walkCollocation(final SQLiteDatabase connection, @NonNull final Document doc, final Node parent, final long collocationId)
	{
		// collocations
		final List<Collocation.WithDefinitionAndPos> collocations = Collocation.WithDefinitionAndPos.make(connection, collocationId);
		walk(connection, doc, parent, collocations);
	}

	/**
	 * Query SyntagNet data from collocations
	 *
	 * @param connection   data source
	 * @param doc          org.w3c.dom.Document being built
	 * @param parent       org.w3c.dom.Node the walk will attach results to
	 * @param collocations collocations
	 */
	static private void walk(final SQLiteDatabase connection, @NonNull final Document doc, final Node parent, @NonNull final Iterable<Collocation.WithDefinitionAndPos> collocations)
	{
		int i = 1;
		for (final Collocation.WithDefinitionAndPos collocation : collocations)
		{
			// collocation
			SnNodeFactory.makeCollocationNode(doc, parent, collocation, i++);
		}
	}

	// I T E M S

	/**
	 * Business method that returns SyntagNet selector data as DOM document
	 *
	 * @param connection connection
	 * @param word       target word
	 * @return SyntagNet selector data as DOM document
	 */
	@Override
	public Document querySelectorDoc(final SQLiteDatabase connection, final String word)
	{
		final Document doc = DomFactory.makeDocument();
		final Node rootNode = SnNodeFactory.makeSnRootNode(doc, word);
		SyntagNetImplementation.walkSelector(connection, doc, rootNode, word);
		return doc;
	}

	/**
	 * Business method that returns SyntagNet selector data as XML
	 *
	 * @param connection connection
	 * @param word       target word
	 * @return SyntagNet selector data as XML
	 */
	@NonNull
	@Override
	public String querySelectorXML(final SQLiteDatabase connection, final String word)
	{
		final Document doc = querySelectorDoc(connection, word);
		return DomTransformer.docToString(doc);
	}

	// W A L K

	/**
	 * Business method that returns SyntagNet data as DOM document from word
	 *
	 * @param connection connection
	 * @param word       target word
	 * @return SyntagNet data as DOM document
	 */
	@Override
	public Document queryDoc(final SQLiteDatabase connection, @NonNull final String word)
	{
		final Document doc = DomFactory.makeDocument();
		final Node rootNode = SnNodeFactory.makeSnRootNode(doc, word);
		SyntagNetImplementation.walk(connection, doc, rootNode, word);
		return doc;
	}

	/**
	 * Business method that returns SyntagNet data as XML from word
	 *
	 * @param connection connection
	 * @param word       target word
	 * @return SyntagNet data as XML
	 */
	@NonNull
	@Override
	public String queryXML(final SQLiteDatabase connection, @NonNull final String word)
	{
		final Document doc = queryDoc(connection, word);
		return DomTransformer.docToString(doc);
	}

	/**
	 * Business method that returns SyntagNet data as DOM document from word id
	 *
	 * @param connection connection
	 * @param wordId     word id to build query from
	 * @param synsetId   is the synset id to build query from (nullable)
	 * @param pos        pos to build query from (nullable)
	 * @return SyntagNet data as DOM document
	 */
	@Override
	public Document queryDoc(final SQLiteDatabase connection, final long wordId, @Nullable Long synsetId, @Nullable final Character pos)
	{
		final Document doc = DomFactory.makeDocument();
		final Node wordNode = SnNodeFactory.makeSnRootNode(doc, wordId);
		if (synsetId == null)
		{
			SyntagNetImplementation.walk(connection, doc, wordNode, wordId);
		}
		else
		{
			SyntagNetImplementation.walk(connection, doc, wordNode, wordId, synsetId);
		}
		return doc;
	}

	/**
	 * Business method that returns SyntagNet data as XML from word id
	 *
	 * @param connection connection
	 * @param wordId     target word id
	 * @param synsetId   is the synset id to build query from (nullable)
	 * @param pos        pos to build query from (nullable)
	 * @return SyntagNet data as XML
	 */
	@NonNull
	@Override
	public String queryXML(final SQLiteDatabase connection, final long wordId, @Nullable Long synsetId, @Nullable final Character pos)
	{
		final Document doc = queryDoc(connection, wordId, synsetId, pos);
		return DomTransformer.docToString(doc);
	}

	@Override
	public Document queryDoc(final SQLiteDatabase connection, final long wordId, @Nullable final Long synsetId, final long word2Id, @Nullable final Long synset2Id)
	{
		final Document doc = DomFactory.makeDocument();
		final Node wordNode = SnNodeFactory.makeSnRootNode(doc, wordId);
		if (synsetId == null || synset2Id == null)
		{
			SyntagNetImplementation.walk2(connection, doc, wordNode, wordId, word2Id);
		}
		else
		{
			SyntagNetImplementation.walk2(connection, doc, wordNode, wordId, synsetId, word2Id, synset2Id);
		}
		return doc;
	}

	@NonNull
	@Override
	public String queryXML(final SQLiteDatabase connection, final long wordId, @Nullable final Long synsetId, final long word2Id, @Nullable final Long synset2Id)
	{
		final Document doc = queryDoc(connection, wordId, synsetId, word2Id, synset2Id);
		return DomTransformer.docToString(doc);
	}

	/**
	 * Business method that returns collocation data as DOM document from collocation id
	 *
	 * @param connection    connection
	 * @param collocationId collocation to build query from
	 * @return SyntagNet collocation data as DOM document
	 */
	@Override
	public Document queryCollocationDoc(final SQLiteDatabase connection, final long collocationId)
	{
		final Document doc = DomFactory.makeDocument();
		final Node rootNode = SnNodeFactory.makeSnRootNode(doc, collocationId);
		SyntagNetImplementation.walkCollocation(connection, doc, rootNode, collocationId);
		return doc;
	}

	// H E L P E R S

	/**
	 * Business method that returns collocation data as XML from collocation id
	 *
	 * @param connection    connection
	 * @param collocationId collocation id to build query from
	 * @return SyntagNet collocation data as XML
	 */
	@NonNull
	@Override
	public String queryCollocationXML(final SQLiteDatabase connection, final long collocationId)
	{
		final Document doc = queryCollocationDoc(connection, collocationId);
		return DomTransformer.docToString(doc);
	}

	/**
	 * Display query results for SyntagNet data from query result
	 *
	 * @param doc          org.w3c.dom.Document being built
	 * @param parent       org.w3c.dom.Node the walk will attach results to
	 * @param collocations collocations
	 */
	static private void makeSelector(@NonNull final Document doc, final Node parent, @NonNull final Iterable<Collocation> collocations)
	{
		int i = 1;
		for (final Collocation collocation : collocations)
		{
			// collocation
			SnNodeFactory.makeSelectorCollocationNode(doc, parent, collocation, i++);
		}
	}
}
