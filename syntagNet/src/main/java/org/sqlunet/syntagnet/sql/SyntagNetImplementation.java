/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.syntagnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.dom.DomFactory;
import org.sqlunet.dom.DomTransformer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * Encapsulates SyntagNet query implementation
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class SyntagNetImplementation implements SyntagNetInterface
{
	static public final String SN_NS = "http://org.sqlunet/sn";

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
		final List<Collocation> roleSets = Collocation.makeFromWord(connection, targetWord);
		//if (roleSets == null)
		//{
		//	return;
		//}

		// word
		// NodeFactory.makeWordNode(doc, parent, targetWord, wordId);

		// syntagnet nodes
		SyntagNetImplementation.makeSelector(doc, parent, roleSets);
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
		final List<Collocation> collocations = Collocation.makeFromWord(connection, targetWord);
		//if (collocations == null)
		//{
		//	return;
		//}

		// word
		// NodeFactory.makeWordNode(doc, parent, targetWord, wordId);

		// collocations
		int i = 1;
		for (final Collocation collocation : collocations)
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
		final List<Collocation> collocations = Collocation.makeFromWordId(connection, targetWordId);
		walk(connection, doc, parent, collocations);
	}

	/**
	 * Perform queries for SyntagNet data from role set id
	 *
	 * @param connection data source
	 * @param doc        org.w3c.dom.Document being built
	 * @param parent     org.w3c.dom.Node the walk will attach results to
	 * @param roleSetId  role set id
	 */
	static private void walkCollocations(final SQLiteDatabase connection, @NonNull final Document doc, final Node parent, final long roleSetId)
	{
		// collocations
		final List<Collocation> collocations = Collocation.make(connection, roleSetId);
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
	static private void walk(final SQLiteDatabase connection, @NonNull final Document doc, final Node parent, @NonNull final Iterable<Collocation> collocations)
	{
		int i = 1;
		for (final Collocation collocation : collocations)
		{
			// role set
			final Node collocationNode = SnNodeFactory.makeCollocationNode(doc, parent, collocation, i++);
		}
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
			// role set
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
	public Document queryDoc(final SQLiteDatabase connection, final String word)
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
	public String queryXML(final SQLiteDatabase connection, final String word)
	{
		final Document doc = queryDoc(connection, word);
		return DomTransformer.docToString(doc);
	}

	/**
	 * Business method that returns SyntagNet data as DOM document from word id
	 *
	 * @param connection connection
	 * @param wordId     word id to build query from
	 * @param pos        pos to build query from
	 * @return SyntagNet data as DOM document
	 */
	@Override
	public Document queryDoc(final SQLiteDatabase connection, final long wordId, final Character pos)
	{
		final Document doc = DomFactory.makeDocument();
		final Node wordNode = SnNodeFactory.makeSnRootNode(doc, wordId);
		SyntagNetImplementation.walk(connection, doc, wordNode, wordId);
		return doc;
	}

	/**
	 * Business method that returns SyntagNet data as XML from word id
	 *
	 * @param connection connection
	 * @param wordId     target word id
	 * @param pos        pos to build query from
	 * @return SyntagNet data as XML
	 */
	@NonNull
	@Override
	public String queryXML(final SQLiteDatabase connection, final long wordId, final Character pos)
	{
		final Document doc = queryDoc(connection, wordId, pos);
		return DomTransformer.docToString(doc);
	}

	/**
	 * Business method that returns collocation data as DOM document from collocation id
	 *
	 * @param connection connection
	 * @param collocationId  role set to build query from
	 * @return SyntagNet role set data as DOM document
	 */
	@Override
	public Document queryCollocationDoc(final SQLiteDatabase connection, final long collocationId)
	{
		final Document doc = DomFactory.makeDocument();
		final Node rootNode = SnNodeFactory.makeSnRootNode(doc, collocationId);
		SyntagNetImplementation.walkCollocations(connection, doc, rootNode, collocationId);
		return doc;
	}

	// H E L P E R S

	/**
	 * Business method that returns role set data as XML from role set id
	 *
	 * @param connection connection
	 * @param roleSetId  role set id to build query from
	 * @param pos        pos to build query from
	 * @return SyntagNet role set data as XML
	 */
	@NonNull
	@Override
	public String queryCollocationXML(final SQLiteDatabase connection, final long roleSetId, final Character pos)
	{
		final Document doc = queryCollocationDoc(connection, roleSetId, pos);
		return DomTransformer.docToString(doc);
	}
}
