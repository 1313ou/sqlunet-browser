/*
 * Copyright (c) 2022. Bernard Bou
 */

package org.sqlunet.wordnet.sql;

import android.database.sqlite.SQLiteDatabase;

import org.sqlunet.dom.DomFactory;
import org.sqlunet.dom.DomTransformer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Encapsulates WordNet query implementation
 *
 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
 */
public class WordNetImplementation implements WordNetInterface
{
	// S E T T I N  G S

	static private final String WN_NS = "http://org.sqlunet/wn";

	/**
	 * The maximum recursion level defined for down-tree queries
	 */
	static private final int MAX_RECURSE_LEVEL = 2;

	// S E L E C T O R

	/**
	 * Business method that returns WordNet selector data as DOM document
	 *
	 * @param connection connection
	 * @param word       target word
	 * @return WordNet selector data as DOM document
	 */
	@Override
	public Document querySelectorDoc(final SQLiteDatabase connection, final String word)
	{
		final Document doc = DomFactory.makeDocument();
		final Element rootNode = NodeFactory.makeNode(doc, doc, "wordnet", null, WordNetImplementation.WN_NS);
		NodeFactory.addAttributes(rootNode, "word", word);
		WordNetImplementation.walkSelector(connection, doc, rootNode, word);
		return doc;
	}

	/**
	 * Business method that returns WordNet selector data as XML
	 *
	 * @param connection connection
	 * @param word       target word
	 * @return WordNet selector data as XML
	 */
	@NonNull
	@Override
	public String querySelectorXML(final SQLiteDatabase connection, final String word)
	{
		final Document doc = querySelectorDoc(connection, word);
		return DomTransformer.docToString(doc);
	}

	// D E T A I L

	/**
	 * Business method that returns WordNet data as a Document
	 *
	 * @param connection    connection
	 * @param word          target word
	 * @param withRelations determines if queries are to include relations
	 * @param recurse       determines if queries are to follow relations recursively
	 * @return WordNet data as a DOM Document
	 */
	@Override
	public Document queryDoc(final SQLiteDatabase connection, final String word, final boolean withRelations, final boolean recurse)
	{
		final Document doc = DomFactory.makeDocument();
		final Element rootNode = NodeFactory.makeNode(doc, doc, "wordnet", null, WordNetImplementation.WN_NS);
		NodeFactory.addAttributes(rootNode, //
				"word", word, //
				"withRelations", Boolean.toString(withRelations), //
				"recurse", Boolean.toString(recurse));
		WordNetImplementation.walk(connection, word, doc, rootNode, withRelations, recurse, Mapping.ANYTYPE, Mapping.ANYTYPE, Mapping.ANYTYPE);
		return doc;
	}

	/**
	 * Business method that returns WordNet data as a Document
	 *
	 * @param connection    connection
	 * @param wordId        target word id
	 * @param synsetId      target synset id
	 * @param withRelations determines if queries are to include relations
	 * @param recurse       determines if queries are to follow relations recursively
	 * @return WordNet data as a DOM Document
	 */
	@Override
	public Document queryDoc(final SQLiteDatabase connection, final long wordId, @Nullable final Long synsetId, final boolean withRelations, final boolean recurse)
	{
		final Document doc = DomFactory.makeDocument();
		final Element rootNode = NodeFactory.makeNode(doc, doc, "wordnet", null, WordNetImplementation.WN_NS);
		NodeFactory.addAttributes(rootNode, //
				"wordid", Long.toString(wordId), //
				"synsetid", synsetId == null ? null : Long.toString(synsetId), //
				"withRelations", Boolean.toString(withRelations), //
				"recurse", Boolean.toString(recurse));
		WordNetImplementation.walkSense(connection, wordId, synsetId, doc, rootNode, withRelations, recurse, Mapping.ANYTYPE);
		return doc;
	}

	/**
	 * Business method that returns complete data as XML
	 *
	 * @param connection    connection
	 * @param word          target word
	 * @param withRelations determines if queries are to include relations
	 * @param recurse       determines if queries are to follow relations recursively
	 * @return WordNet data as XML
	 */
	@NonNull
	@Override
	public String queryXML(final SQLiteDatabase connection, final String word, final boolean withRelations, final boolean recurse)
	{
		final Document doc = queryDoc(connection, word, withRelations, recurse);
		return DomTransformer.docToString(doc);
	}

	/**
	 * Business method that returns WordNet data as DOM document
	 *
	 * @param connection    connection
	 * @param word          target word
	 * @param posName       target part-of-speech
	 * @param domainName    target domain
	 * @param relationName  target relation type name
	 * @param withRelations determines if queries are to include relations
	 * @param recurse       determines if queries are to follow relations recursively
	 * @return WordNet data as DOM document
	 */
	@Override
	public Document queryDoc(final SQLiteDatabase connection, final String word, final String posName, final String domainName, final String relationName, final boolean withRelations, final boolean recurse)
	{
		final Document doc = DomFactory.makeDocument();

		// parameters
		final int posId = Mapping.getPosId(posName);
		final int domainId = Mapping.getDomainId(posName, domainName);
		final int relationId = Mapping.getRelationId(relationName);

		// fill document
		final Element rootNode = NodeFactory.makeNode(doc, doc, "wordnet", null, WordNetImplementation.WN_NS);
		NodeFactory.addAttributes(rootNode, //
				"word", word,  //
				"pos", posName,  //
				"domain", domainName, //
				"relation", relationName, //
				"withrelations", Boolean.toString(withRelations), //
				"recurse", Boolean.toString(recurse));
		WordNetImplementation.walk(connection, word, doc, rootNode, withRelations, recurse, posId, domainId, relationId);

		return doc;
	}

	/**
	 * Business method that returns WordNet data as XML
	 *
	 * @param connection    connection
	 * @param word          target word
	 * @param posName       target part-of-speech
	 * @param domainName    target domain
	 * @param relationName  target relation type name
	 * @param withRelations determines if queries are to include relations
	 * @param recurse       determines if queries are to follow relations recursively
	 * @return WordNet data as XML data
	 */
	@NonNull
	@Override
	public String queryXML(final SQLiteDatabase connection, final String word, final String posName, final String domainName, final String relationName, final boolean withRelations, final boolean recurse)
	{
		final Document doc = queryDoc(connection, word, posName, domainName, relationName, withRelations, recurse);
		return DomTransformer.docToString(doc);
	}

	/**
	 * Business method that returns WordNet word data as DOM document
	 *
	 * @param connection connection
	 * @param wordId     target word id
	 * @return WordNet word data as DOM document
	 */
	@Override
	public Document queryWordDoc(final SQLiteDatabase connection, final long wordId)
	{
		final Document doc = DomFactory.makeDocument();
		final Element rootNode = NodeFactory.makeNode(doc, doc, "wordnet", null, WordNetImplementation.WN_NS);
		NodeFactory.addAttributes(rootNode, //
				"wordid", Long.toString(wordId));

		// word
		final Word word = Word.make(connection, wordId);
		assert word != null;
		final Node wordNode = NodeFactory.makeWordNode(doc, rootNode, word.word, word.id);

		final SynsetsQueryFromWordId query = new SynsetsQueryFromWordId(connection, wordId);
		query.execute();
		while (query.next())
		{
			// synset
			final Synset synset = new Synset(query);

			// sense node
			final Element senseElement = NodeFactory.makeSenseNode(doc, wordNode, wordId, synset.synsetId, 0);
			NodeFactory.addAttributes(senseElement, //
					"pos", synset.getPosName(), //
					"domain", synset.getDomainName());

			// synset node
			WordNetImplementation.walkSynsetHeader(doc, senseElement, synset);
		}
		return doc;
	}

	/**
	 * Business method that returns WordNet word data as XML
	 *
	 * @param connection connection
	 * @param wordId     target word id
	 * @return WordNet word data as XML
	 */
	@NonNull
	@Override
	public String queryWordXML(final SQLiteDatabase connection, final long wordId)
	{
		final Document doc = queryWordDoc(connection, wordId);
		return DomTransformer.docToString(doc);
	}

	/**
	 * Business method that returns WordNet sense data as DOM document
	 *
	 * @param connection connection
	 * @param wordId     target word id
	 * @param synsetId   target synset id
	 * @return WordNet synset data as DOM document
	 */
	@Override
	public Document querySenseDoc(final SQLiteDatabase connection, final long wordId, final long synsetId)
	{
		final Document doc = DomFactory.makeDocument();
		final Element rootNode = NodeFactory.makeNode(doc, doc, "wordnet", null, WordNetImplementation.WN_NS);
		NodeFactory.addAttributes(rootNode, //
				"wordid", Long.toString(wordId), //
				"synsetid", Long.toString(synsetId));
		final Node senseNode = NodeFactory.makeSenseNode(doc, rootNode, wordId, synsetId, 0);

		final SynsetQuery query = new SynsetQuery(connection, synsetId);
		query.execute();
		if (query.next())
		{
			// synset
			final Synset synset = new Synset(query);
			final Node synsetNode = WordNetImplementation.walkSynset(connection, doc, senseNode, synset);

			// relations
			WordNetImplementation.walkSynsetRelations(connection, doc, synsetNode, synset, wordId, true /* withRelations */, true /* recurse */, Mapping.ANYTYPE);
		}
		return doc;
	}

	/**
	 * Business method that returns WordNet sense data as XML
	 *
	 * @param connection connection
	 * @param wordId     target word id
	 * @param synsetId   target synset id
	 * @return WordNet synset data as XML
	 */
	@NonNull
	@Override
	public String querySenseXML(final SQLiteDatabase connection, final long wordId, final long synsetId)
	{
		final Document doc = querySenseDoc(connection, wordId, synsetId);
		return DomTransformer.docToString(doc);
	}

	/**
	 * Business method that returns WordNet synset data as DOM document
	 *
	 * @param connection connection
	 * @param synsetId   target synset id
	 * @return WordNet synset data as DOM document
	 */
	@Override
	public Document querySynsetDoc(final SQLiteDatabase connection, final long synsetId)
	{
		final Document doc = DomFactory.makeDocument();
		final Element rootNode = NodeFactory.makeNode(doc, doc, "wordnet", null, WordNetImplementation.WN_NS);
		NodeFactory.addAttributes(rootNode, "synsetid", Long.toString(synsetId));
		final SynsetQuery query = new SynsetQuery(connection, synsetId);
		query.execute();
		if (query.next())
		{
			// synset
			final Synset synset = new Synset(query);
			final Node synsetNode = WordNetImplementation.walkSynset(connection, doc, rootNode, synset);

			// relations
			WordNetImplementation.walkSynsetRelations(connection, doc, synsetNode, synset, 0, true /* withRelations */, true /* recurse */, Mapping.ANYTYPE);
		}
		return doc;
	}

	/**
	 * Business method that returns WordNet synset data as XML
	 *
	 * @param connection connection
	 * @param synsetId   target synset id
	 * @return WordNet synset data as XML
	 */
	@NonNull
	@Override
	public String querySynsetXML(final SQLiteDatabase connection, final long synsetId)
	{
		final Document doc = querySynsetDoc(connection, synsetId);
		return DomTransformer.docToString(doc);
	}

	// W A L K

	/**
	 * Perform queries for WordNet selector
	 *
	 * @param connection connection
	 * @param doc        org.w3c.dom.Document being built
	 * @param parent     org.w3c.dom.Node walk will attach results to
	 * @param targetWord target word
	 */
	static private void walkSelector(final SQLiteDatabase connection, @NonNull final Document doc, final Node parent, final String targetWord)
	{
		// word
		final Word word = Word.make(connection, targetWord);
		if (word == null)
		{
			return;
		}

		// iterate synsets
		final List<Synset> synsets = word.getSynsets(connection);
		if (synsets == null)
		{
			return;
		}
		String domain = null;
		String pos = null;
		Node domainNode = null;
		Node posNode = null;
		for (int i = 0; i < synsets.size(); i++)
		{
			final Synset synset = synsets.get(i);

			// pos node
			final String posName = synset.getPosName();
			if (!posName.equals(pos))
			{
				posNode = NodeFactory.makePosNode(doc, parent, posName);
				pos = posName;
				domain = null;
			}

			// domain node
			final String domainName = synset.getDomainName();
			if (!domainName.equals(domain))
			{
				domainNode = NodeFactory.makeDomainNode(doc, posNode, domainName);
				domain = domainName;
			}

			// sense node
			final Node senseNode = NodeFactory.makeSenseNode(doc, domainNode, word.id, synset.synsetId, i + 1);

			// word node
			NodeFactory.makeWordNode(doc, senseNode, word.word, word.id);

			// synset node
			WordNetImplementation.walkSynset(connection, doc, senseNode, synset);
		}
	}

	/**
	 * Perform queries for WordNet data from word
	 *
	 * @param connection       connection
	 * @param targetWord       target word
	 * @param doc              org.w3c.dom.Document being built
	 * @param parent           org.w3c.dom.Node walk will attach results to
	 * @param withRelations    determines if queries are to include relations
	 * @param recurse          determines if queries are to follow relations recursively
	 * @param targetPosId      target part-of-speech id (ANYTYPE for all types)
	 * @param targetDomainId   target domain id (ANYTYPE for all types)
	 * @param targetRelationId target relation type id (ANYTYPE for all types)
	 */
	static private void walk(final SQLiteDatabase connection, final String targetWord, @NonNull final Document doc, final Node parent, final boolean withRelations, final boolean recurse, final int targetPosId, final int targetDomainId, final int targetRelationId)
	{
		// word
		final Word word = Word.make(connection, targetWord);
		if (word == null)
		{
			return;
		}
		NodeFactory.makeWordNode(doc, parent, word.word, word.id);

		// iterate synsets
		final List<Synset> synsets = targetPosId == Mapping.ANYTYPE && targetDomainId == Mapping.ANYTYPE ?
				word.getSynsets(connection) :
				word.getTypedSynsets(connection, targetDomainId == Mapping.ANYTYPE ? targetPosId : targetDomainId, targetDomainId != Mapping.ANYTYPE);
		if (synsets == null)
		{
			return;
		}
		String domain = null;
		String pos = null;
		Node domainNode = null;
		Node posNode = null;
		for (int i = 0; i < synsets.size(); i++)
		{
			final Synset synset = synsets.get(i);

			// pos node
			final String posName = synset.getPosName();
			if (!posName.equals(pos))
			{
				posNode = NodeFactory.makePosNode(doc, parent, posName);
				pos = posName;
				domain = null;
			}

			// domain node
			final String domainName = synset.getDomainName();
			if (!domainName.equals(domain))
			{
				domainNode = NodeFactory.makeDomainNode(doc, posNode, domainName);
				domain = domainName;
			}

			// sense node
			final Node senseNode = NodeFactory.makeSenseNode(doc, domainNode, word.id, synset.synsetId, i + 1);

			// synset nodes
			final Node synsetNode = WordNetImplementation.walkSynset(connection, doc, senseNode, synset);

			// relations
			WordNetImplementation.walkSynsetRelations(connection, doc, synsetNode, synset, word.id, withRelations, recurse, targetRelationId);
		}
	}

	/**
	 * Perform queries for WordNet data from word id and synset id
	 *
	 * @param connection       connection
	 * @param wordId           target word id
	 * @param synsetId         target synset id
	 * @param doc              org.w3c.dom.Document being built
	 * @param parent           org.w3c.dom.Node walk will attach results to
	 * @param withRelations    determines if queries are to include relations
	 * @param recurse          determines if queries are to follow relations recursively
	 * @param targetRelationId target relation type id
	 */
	static private void walkSense(final SQLiteDatabase connection, //
			final long wordId,  //
			@Nullable final Long synsetId,  //
			@NonNull final Document doc, //
			final Node parent, //
			final boolean withRelations,//
			final boolean recurse, //
			@SuppressWarnings("SameParameterValue") final int targetRelationId)
	{
		if (synsetId == null)
		{
			final SynsetsQueryFromWordId query = new SynsetsQueryFromWordId(connection, wordId);
			query.execute();

			String posName = null;
			String domainName = null;
			Node posNode = null;
			Node domainNode = null;
			int i = 0;
			while (query.next())
			{
				final Synset synset = new Synset(query);

				// pos node
				final String synsetPosName = synset.getPosName();
				if (!synsetPosName.equals(posName))
				{
					posNode = NodeFactory.makePosNode(doc, parent, synsetPosName);
					posName = synsetPosName;
				}

				// domain node
				final String synsetDomainName = synset.getDomainName();
				if (!synsetDomainName.equals(domainName))
				{
					domainNode = NodeFactory.makeDomainNode(doc, posNode, synsetDomainName);
					domainName = synsetDomainName;
				}

				// sense node
				final Node senseNode = NodeFactory.makeSenseNode(doc, domainNode, wordId, 0, ++i);

				// synset
				final Node synsetNode = WordNetImplementation.walkSynset(connection, doc, senseNode, synset);

				// relations
				WordNetImplementation.walkSynsetRelations(connection, doc, synsetNode, synset, wordId, withRelations, recurse, targetRelationId);
			}
			return;
		}

		final SynsetQuery query = new SynsetQuery(connection, synsetId);
		query.execute();
		if (query.next())
		{
			final Synset synset = new Synset(query);

			// pos node
			final String posName = synset.getPosName();
			final Node posNode = NodeFactory.makePosNode(doc, parent, posName);

			// domain node
			final String domainName = synset.getDomainName();
			final Node domainNode = NodeFactory.makeDomainNode(doc, posNode, domainName);

			// sense node
			final Node senseNode = NodeFactory.makeSenseNode(doc, domainNode, wordId, synsetId, 1);

			// synset
			final Node synsetNode = WordNetImplementation.walkSynset(connection, doc, senseNode, synset);

			// relations
			WordNetImplementation.walkSynsetRelations(connection, doc, synsetNode, synset, wordId, withRelations, recurse, targetRelationId);
		}
	}

	/**
	 * Process synset data (summary)
	 *
	 * @param doc    org.w3c.dom.Document being built
	 * @param parent org.w3c.dom.Node walk will attach results to
	 * @param synset synset whose data are to be processed
	 */
	@SuppressWarnings("UnusedReturnValue")
	static private Node walkSynsetHeader(@NonNull final Document doc, final Node parent, @NonNull final Synset synset)
	{
		// anchor node
		final Node synsetNode = NodeFactory.makeSynsetNode(doc, parent, synset.synsetId, 0);

		// gloss
		NodeFactory.makeNode(doc, synsetNode, "definition", synset.definition);

		return synsetNode;
	}

	/**
	 * Process synset data
	 *
	 * @param connection connection
	 * @param doc        org.w3c.dom.Document being built
	 * @param parent     org.w3c.dom.Node walk will attach results to
	 * @param synset     synset whose data are to be processed
	 */
	static private Node walkSynset(final SQLiteDatabase connection, @NonNull final Document doc, final Node parent, @NonNull final Synset synset)
	{
		// synset words
		final List<Word> words = synset.getSynsetWords(connection);

		// anchor node
		final Node synsetNode = NodeFactory.makeSynsetNode(doc, parent, synset.synsetId, words != null ? words.size() : 0);

		// words
		if (words != null)
		{
			for (final Word word : words)
			{
				final String word2 = word.word.replace('_', ' ');
				NodeFactory.makeWordNode(doc, synsetNode, word2, word.id);
			}
		}

		// gloss
		NodeFactory.makeNode(doc, synsetNode, "definition", synset.definition);

		// sample
		if (synset.sample != null)
		{
			NodeFactory.makeNode(doc, synsetNode, "sample", synset.sample);
		}

		return synsetNode;
	}

	/**
	 * Perform synset relation queries for WordNet data
	 *
	 * @param connection       connection
	 * @param doc              org.w3c.dom.Document being built
	 * @param parent           org.w3c.dom.Node walk will attach results to
	 * @param synset           target synset
	 * @param wordId           target word id
	 * @param withRelations    determines if queries are to include relations
	 * @param recurse          determines if queries are to follow relations recursively
	 * @param targetRelationId target relation type id
	 */
	static private void walkSynsetRelations(final SQLiteDatabase connection, @NonNull final Document doc, final Node parent, @NonNull final Synset synset, final long wordId, final boolean withRelations, final boolean recurse, final int targetRelationId)
	{
		if (withRelations)
		{
			// relations node
			final Node relationsNode = NodeFactory.makeNode(doc, parent, "relations", null);

			// get related
			final List<Related> relateds = targetRelationId == Mapping.ANYTYPE ? synset.getRelateds(connection, wordId) : synset.getTypedRelateds(connection, wordId, targetRelationId);
			if (relateds == null)
			{
				return;
			}

			// iterate relations
			Node relationNode = null;
			String currentRelationName = null;
			for (final Related related : relateds)
			{
				// anchor node
				final String relationName = related.getRelationName();
				if (!relationName.equals(currentRelationName))
				{
					relationNode = NodeFactory.makeRelationNode(doc, relationsNode, relationName, 0);
					currentRelationName = relationName;
				}

				// recurse check
				final int recurse2 = recurse ?
						synset.domainId == Mapping.topsId && (targetRelationId == Mapping.hyponymId || targetRelationId == Mapping.instanceHyponymId || targetRelationId == Mapping.ANYTYPE) ? Mapping.NONRECURSIVE : 0 :
						Mapping.NONRECURSIVE;

				// process relation
				WordNetImplementation.walkRelation(connection, doc, relationNode, related, wordId, recurse2, targetRelationId);
			}
		}
	}

	/**
	 * Process synset relation (recurses)
	 *
	 * @param connection       connection
	 * @param doc              org.w3c.dom.Document being built
	 * @param parent0          org.w3c.dom.Node walk will attach results to
	 * @param related          synset to start walk from
	 * @param wordId           word id to start walk from
	 * @param recurseLevel     recursion level
	 * @param targetRelationId target relation type id (cannot be ANYTYPE for all types)
	 */
	static private void walkRelation(final SQLiteDatabase connection, @NonNull final Document doc, final Node parent0, @NonNull final Related related, final long wordId, final int recurseLevel, final int targetRelationId)
	{
		Node parent = parent0;

		// word in lex relations
		if (related.wordId != 0)
		{
			parent = NodeFactory.makeSenseNode(doc, parent0, related.wordId, related.synsetId, 0);
			NodeFactory.makeWordNode(doc, parent, related.word, related.wordId);
		}

		// synset
		final Node synsetNode = WordNetImplementation.walkSynset(connection, doc, parent, related);

		// recurse
		if (recurseLevel != Mapping.NONRECURSIVE && related.canRecurse())
		{
			// relation node
			final Node relationsNode = NodeFactory.makeNode(doc, synsetNode, "relations", null);

			// stop recursion in case maximum level is reached and
			// hyponym/all relations and source synset domain is tops
			if ((targetRelationId == Mapping.hyponymId || targetRelationId == Mapping.instanceHyponymId) && recurseLevel >= WordNetImplementation.MAX_RECURSE_LEVEL)
			{
				NodeFactory.makeMoreRelationNode(doc, relationsNode, related.getRelationName(), recurseLevel);
			}
			else
			{
				// get related
				final List<Related> subRelated = targetRelationId == Mapping.ANYTYPE ? related.getRelateds(connection, wordId) : related.getTypedRelateds(connection, wordId, targetRelationId);
				if (subRelated == null)
				{
					return;
				}

				// iterate subrelations
				Node subRelatedNode = null;
				String currentSubRelationName = null;

				for (final Related subRelation : subRelated)
				{
					// anchor node
					final String relationName = subRelation.getRelationName();
					if (!relationName.equals(currentSubRelationName))
					{
						subRelatedNode = NodeFactory.makeRelationNode(doc, relationsNode, relationName, recurseLevel);
						currentSubRelationName = relationName;
					}
					WordNetImplementation.walkRelation(connection, doc, subRelatedNode, subRelation, wordId, recurseLevel + 1, targetRelationId);
				}
			}
		}
	}

	/**
	 * Initial house-keeping - directory queries - connections - database static data
	 */
	static public void init(final SQLiteDatabase connection)
	{
		// do queries for static maps
		Mapping.initDomains(connection);
		Mapping.initRelations(connection);
	}

	// I T E M S

	/**
	 * Business method that returns WordNet parts-of-speech as array of strings
	 *
	 * @return array of Strings
	 */
	@NonNull
	@Override
	public String[] getPosNames()
	{
		return Mapping.getPosNames();
	}

	/**
	 * Business method that returns WordNet domains as array of strings
	 *
	 * @return array of Strings
	 */
	@NonNull
	@Override
	public String[] getDomainNames()
	{
		return Mapping.getDomainNames();
	}

	// I N I T I A L I Z E

	/**
	 * Business method that returns WordNet relation names as array of strings
	 *
	 * @return array of Strings
	 */
	@NonNull
	@Override
	public String[] getRelationNames()
	{
		return Mapping.getRelationNames();
	}
}
